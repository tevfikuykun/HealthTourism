package com.healthtourism.gamificationservice.service;
import com.healthtourism.gamificationservice.entity.*;
import com.healthtourism.gamificationservice.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GamificationService {
    @Autowired
    private UserPointsRepository userPointsRepository;
    @Autowired
    private BadgeRepository badgeRepository;
    @Autowired
    private UserBadgeRepository userBadgeRepository;
    @Autowired
    private ChallengeRepository challengeRepository;
    
    public UserPoints addPoints(Long userId, Integer points, String reason) {
        UserPoints userPoints = userPointsRepository.findByUserId(userId)
            .orElseGet(() -> {
                UserPoints newPoints = new UserPoints();
                newPoints.setUserId(userId);
                return newPoints;
            });
        
        userPoints.setTotalPoints(userPoints.getTotalPoints() + points);
        // Seviye hesaplama: Her 1000 puan = 1 seviye
        userPoints.setLevel((userPoints.getTotalPoints() / 1000) + 1);
        userPoints.setLastUpdated(java.time.LocalDateTime.now());
        
        // Badge kontrolü
        checkAndAwardBadges(userId, userPoints.getTotalPoints());
        
        return userPointsRepository.save(userPoints);
    }
    
    public UserBadge awardBadge(Long userId, Long badgeId) {
        // Zaten bu badge'e sahip mi kontrol et
        if (userBadgeRepository.findByUserIdAndBadgeId(userId, badgeId).isPresent()) {
            throw new RuntimeException("Bu badge zaten kazanılmış");
        }
        
        UserBadge userBadge = new UserBadge();
        userBadge.setUserId(userId);
        userBadge.setBadgeId(badgeId);
        return userBadgeRepository.save(userBadge);
    }
    
    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }
    
    public List<UserBadge> getUserBadges(Long userId) {
        return userBadgeRepository.findByUserId(userId);
    }
    
    public List<UserPoints> getLeaderboard(Integer limit) {
        return userPointsRepository.findTopByOrderByTotalPointsDesc(limit != null ? limit : 10);
    }
    
    public Challenge createChallenge(Challenge challenge) {
        return challengeRepository.save(challenge);
    }
    
    public List<Challenge> getActiveChallenges() {
        return challengeRepository.findByStatus("ACTIVE");
    }
    
    private void checkAndAwardBadges(Long userId, Integer totalPoints) {
        List<Badge> badges = badgeRepository.findByPointsRequiredLessThanEqual(totalPoints);
        for (Badge badge : badges) {
            if (userBadgeRepository.findByUserIdAndBadgeId(userId, badge.getId()).isEmpty()) {
                awardBadge(userId, badge.getId());
            }
        }
    }
}

