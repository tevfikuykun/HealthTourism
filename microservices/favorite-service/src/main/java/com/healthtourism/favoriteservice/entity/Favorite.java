package com.healthtourism.favoriteservice.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "favorites", uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "itemType", "itemId"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private Long userId;
    @Column(nullable = false) private String itemType; // HOSPITAL, DOCTOR, PACKAGE
    @Column(nullable = false) private Long itemId;
    @Column(nullable = false) private LocalDateTime createdAt;
}

