package com.healthtourism.gamificationservice.controller;
import com.healthtourism.gamificationservice.entity.*;
import com.healthtourism.gamificationservice.service.GamificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gamification")
@CrossOrigin(origins = "*")
public class GamificationController {
    @Autowired
    private GamificationService service;
    
    @PostMapping("/points/add")
    public ResponseEntity<UserPoints> addPoints(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(service.addPoints(
            Long.parseLong(request.get("userId").toString()),
            Integer.parseInt(request.get("points").toString()),
            request.get("reason").toString()
        ));
    }
    
    @PostMapping("/badges/award")
    public ResponseEntity<UserBadge> awardBadge(@RequestBody Map<String, Long> request) {
        return ResponseEntity.ok(service.awardBadge(request.get("userId"), request.get("badgeId")));
    }
    
    @GetMapping("/badges")
    public ResponseEntity<List<Badge>> getAllBadges() {
        return ResponseEntity.ok(service.getAllBadges());
    }
    
    @GetMapping("/badges/user/{userId}")
    public ResponseEntity<List<UserBadge>> getUserBadges(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getUserBadges(userId));
    }
    
    @GetMapping("/leaderboard")
    public ResponseEntity<List<UserPoints>> getLeaderboard(@RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(service.getLeaderboard(limit));
    }
    
    @PostMapping("/challenges")
    public ResponseEntity<Challenge> createChallenge(@RequestBody Challenge challenge) {
        return ResponseEntity.ok(service.createChallenge(challenge));
    }
    
    @GetMapping("/challenges/active")
    public ResponseEntity<List<Challenge>> getActiveChallenges() {
        return ResponseEntity.ok(service.getActiveChallenges());
    }
}

