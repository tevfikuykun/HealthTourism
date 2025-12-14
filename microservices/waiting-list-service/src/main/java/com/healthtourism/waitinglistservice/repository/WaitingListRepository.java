package com.healthtourism.waitinglistservice.repository;
import com.healthtourism.waitinglistservice.entity.WaitingListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WaitingListRepository extends JpaRepository<WaitingListItem, Long> {
    List<WaitingListItem> findByUserId(Long userId);
}

