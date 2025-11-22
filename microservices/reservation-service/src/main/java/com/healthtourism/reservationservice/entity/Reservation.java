package com.healthtourism.reservationservice.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true) private String reservationNumber;
    @Column(nullable = false) private LocalDateTime appointmentDate;
    @Column(nullable = false) private LocalDateTime checkInDate;
    @Column(nullable = false) private LocalDateTime checkOutDate;
    @Column(nullable = false) private Integer numberOfNights;
    @Column(nullable = false) private BigDecimal totalPrice;
    @Column(nullable = false) private String status;
    @Column(length = 1000) private String notes;
    @Column(nullable = false) private LocalDateTime createdAt;
    @Column(nullable = false) private Long userId;
    @Column(nullable = false) private Long hospitalId;
    @Column(nullable = false) private Long doctorId;
    @Column private Long accommodationId;
}

