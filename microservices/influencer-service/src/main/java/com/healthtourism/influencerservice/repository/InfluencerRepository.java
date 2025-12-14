package com.healthtourism.influencerservice.repository;
import com.healthtourism.influencerservice.entity.Influencer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface InfluencerRepository extends JpaRepository<Influencer, Long> {
    List<Influencer> findByStatus(String status);
    Influencer findByUserId(Long userId);
}

