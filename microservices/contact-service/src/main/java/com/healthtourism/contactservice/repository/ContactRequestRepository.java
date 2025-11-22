package com.healthtourism.contactservice.repository;
import com.healthtourism.contactservice.entity.ContactRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContactRequestRepository extends JpaRepository<ContactRequest, Long> {
    List<ContactRequest> findByStatus(String status);
    List<ContactRequest> findByEmail(String email);
    @Query("SELECT c FROM ContactRequest c ORDER BY c.createdAt DESC")
    List<ContactRequest> findAllOrderByCreatedAtDesc();
}

