package com.healthtourism.posttreatmentservice.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "care_packages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarePackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private Long reservationId;
    
    @Column(nullable = false)
    private String treatmentType;
    
    @Column(columnDefinition = "TEXT")
    private String carePlan; // Tedavi sonrası bakım planı
    
    @ElementCollection
    @CollectionTable(name = "care_tasks", joinColumns = @JoinColumn(name = "package_id"))
    private List<CareTask> tasks;
    
    @Column(nullable = false)
    private LocalDateTime startDate;
    
    @Column
    private LocalDateTime endDate;
    
    @Column(nullable = false)
    private String status; // ACTIVE, COMPLETED, CANCELLED
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = "ACTIVE";
    }
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CareTask {
        private String taskName;
        private String description;
        private LocalDateTime dueDate;
        private Boolean isCompleted;
    }
}

