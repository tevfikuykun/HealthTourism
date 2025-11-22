package com.healthtourism.faqservice.repository;
import com.healthtourism.faqservice.entity.FAQ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FAQRepository extends JpaRepository<FAQ, Long> {
    List<FAQ> findByIsActiveTrue();
    List<FAQ> findByCategoryAndIsActiveTrue(String category);
    @Query("SELECT f FROM FAQ f WHERE f.isActive = true ORDER BY f.displayOrder ASC")
    List<FAQ> findAllActiveOrderByDisplayOrder();
}

