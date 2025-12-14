package com.healthtourism.gamificationservice.repository;
import com.healthtourism.gamificationservice.entity.UserPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserPointsRepository extends JpaRepository<UserPoints, Long> {
    Optional<UserPoints> findByUserId(Long userId);
    
    @Query("SELECT up FROM UserPoints up ORDER BY up.totalPoints DESC")
    List<UserPoints> findTopByOrderByTotalPointsDesc(int limit);
}

