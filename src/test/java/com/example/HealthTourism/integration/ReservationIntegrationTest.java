package com.example.HealthTourism.integration;

import com.example.HealthTourism.dto.ReservationDTO;
import com.example.HealthTourism.dto.ReservationRequestDTO;
import com.example.HealthTourism.entity.*;
import com.example.HealthTourism.repository.*;
import com.example.HealthTourism.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Reservation Integration Test
 * Database ile entegrasyon testleri
 * 
 * Important Notes:
 * - @Transactional: Her test sonrası rollback yapılır
 * - ID Generation: Sequence/Identity rollback'ten etkilenmez
 * - Best Practice: ID kontrollerinde dinamik değerler kullanılmalı
 * 
 * Test Coverage:
 * - Reservation creation
 * - Reservation retrieval
 * - Reservation filtering by user
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Reservation Integration Tests")
class ReservationIntegrationTest {
    
    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private HospitalRepository hospitalRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
        doctorRepository.deleteAll();
        hospitalRepository.deleteAll();
        userRepository.deleteAll();
    }
    
    @Test
    @DisplayName("Create reservation - Should persist with dynamic ID")
    void createReservation_ShouldPersistWithDynamicId() {
        // Given - Test data setup
        User user = createTestUser();
        Hospital hospital = createTestHospital();
        Doctor doctor = createTestDoctor(hospital);
        
        ReservationRequestDTO request = new ReservationRequestDTO();
        request.setUserId(user.getId());
        request.setHospitalId(hospital.getId());
        request.setDoctorId(doctor.getId());
        request.setAppointmentDate(LocalDateTime.now().plusDays(7));
        // Note: appointmentEndDate ReservationService tarafından otomatik hesaplanır
        
        // When
        ReservationDTO created = reservationService.createReservation(request);
        
        // Then - Dinamik ID kontrolü (statik değer kullanılmamalı)
        // Not: @Transactional rollback yapılsa bile ID sequence artmaya devam eder
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull(); // ID null olmamalı
        assertThat(created.getId()).isPositive(); // ID pozitif olmalı
        assertThat(created.getReservationNumber()).isNotNull(); // Reservation number oluşturulmalı
        assertThat(created.getUserId()).isEqualTo(user.getId());
        assertThat(created.getHospitalId()).isEqualTo(hospital.getId());
        assertThat(created.getDoctorId()).isEqualTo(doctor.getId());
        
        // Retrieve and verify - Dinamik ID ile
        ReservationDTO retrieved = reservationService.getReservationByNumber(created.getReservationNumber());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getId()).isEqualTo(created.getId()); // Dinamik ID ile karşılaştır
        assertThat(retrieved.getReservationNumber()).isEqualTo(created.getReservationNumber());
    }
    
    @Test
    @DisplayName("Get reservations by user - Should return user's reservations with dynamic IDs")
    void getReservationsByUser_ShouldReturnUserReservationsWithDynamicIds() {
        // Given
        User user1 = createTestUser("user1@test.com", "User", "One");
        User user2 = createTestUser("user2@test.com", "User", "Two");
        Hospital hospital = createTestHospital();
        Doctor doctor = createTestDoctor(hospital);
        
        // Create reservations for user1
        ReservationRequestDTO request1 = createReservationRequest(user1.getId(), hospital.getId(), doctor.getId());
        ReservationRequestDTO request2 = createReservationRequest(user1.getId(), hospital.getId(), doctor.getId());
        
        ReservationDTO created1 = reservationService.createReservation(request1);
        ReservationDTO created2 = reservationService.createReservation(request2);
        
        // Create reservation for user2
        ReservationRequestDTO request3 = createReservationRequest(user2.getId(), hospital.getId(), doctor.getId());
        reservationService.createReservation(request3);
        
        // When
        List<ReservationDTO> user1Reservations = reservationService.getReservationsByUser(user1.getId());
        
        // Then - Dinamik ID kontrolleri
        assertThat(user1Reservations).isNotNull();
        assertThat(user1Reservations).hasSize(2);
        assertThat(user1Reservations).allMatch(r -> r.getUserId().equals(user1.getId()));
        assertThat(user1Reservations).allMatch(r -> r.getId() != null && r.getId() > 0); // Dinamik ID kontrolü
        
        // Oluşturulan reservation'ların ID'lerini kontrol et (statik değer yerine dinamik)
        assertThat(user1Reservations)
                .extracting(ReservationDTO::getId)
                .containsExactlyInAnyOrder(created1.getId(), created2.getId());
    }
    
    // Helper methods
    private User createTestUser() {
        return createTestUser("test@user.com", "Test", "User");
    }
    
    private User createTestUser(String email, String firstName, String lastName) {
        User user = new User();
        user.setEmail(email);
        user.setPassword("$2a$10$encrypted"); // BCrypt hash
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone("5551234567");
        user.setCountry("Turkey");
        user.setRole("USER");
        user.setIsActive(true);
        user.setAccountNonLocked(true);
        return userRepository.save(user);
    }
    
    private Hospital createTestHospital() {
        Hospital hospital = new Hospital();
        hospital.setName("Test Hospital");
        hospital.setAddress("Test Address");
        hospital.setCity("İstanbul");
        hospital.setDistrict("Kadıköy");
        hospital.setPhone("02121234567");
        hospital.setEmail("test@hospital.com");
        hospital.setLatitude(41.0);
        hospital.setLongitude(29.0);
        hospital.setAirportDistance(25.0);
        hospital.setAirportDistanceMinutes(30);
        hospital.setRating(4.5);
        hospital.setTotalReviews(10);
        hospital.setIsActive(true);
        return hospitalRepository.save(hospital);
    }
    
    private Doctor createTestDoctor(Hospital hospital) {
        Doctor doctor = new Doctor();
        doctor.setFirstName("Test");
        doctor.setLastName("Doctor");
        doctor.setSpecialization("Cardiology");
        doctor.setTitle("Dr.");
        doctor.setExperienceYears(10);
        doctor.setLanguages("Turkish,English");
        doctor.setRating(4.8);
        doctor.setTotalReviews(50);
        doctor.setConsultationFee(500.0);
        doctor.setIsAvailable(true);
        doctor.setHospital(hospital);
        return doctorRepository.save(doctor);
    }
    
    private ReservationRequestDTO createReservationRequest(Long userId, Long hospitalId, Long doctorId) {
        ReservationRequestDTO request = new ReservationRequestDTO();
        request.setUserId(userId);
        request.setHospitalId(hospitalId);
        request.setDoctorId(doctorId);
        request.setAppointmentDate(LocalDateTime.now().plusDays(7));
        // Note: appointmentEndDate ReservationService tarafından otomatik hesaplanır
        return request;
    }
}

