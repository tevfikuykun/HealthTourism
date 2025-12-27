package com.example.HealthTourism.integration;

import com.example.HealthTourism.dto.HospitalDTO;
import com.example.HealthTourism.entity.Hospital;
import com.example.HealthTourism.repository.HospitalRepository;
import com.example.HealthTourism.service.HospitalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Hospital Integration Test
 * Database ile entegrasyon testleri
 * 
 * Test Coverage:
 * - Hospital creation and retrieval
 * - Hospital filtering by city, district
 * - Hospital filtering by airport distance
 * 
 * Important Notes:
 * - Bu test gerçek database kullanır (H2 in-memory database)
 * - @Transactional: Her test sonrası rollback yapılır (veritabanı temiz kalır)
 * - ID Generation: @Transactional rollback yapılsa bile ID sequence/identity artmaya devam eder
 * - Best Practice: ID kontrollerinde statik değerler yerine dinamik değerler kullanılmalı
 *   (Örn: assertThat(result.getId()).isNotNull() ve created.getId() ile karşılaştırma)
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Hospital Integration Tests")
class HospitalIntegrationTest {
    
    @Autowired
    private HospitalService hospitalService;
    
    @Autowired
    private HospitalRepository hospitalRepository;
    
    @BeforeEach
    void setUp() {
        hospitalRepository.deleteAll();
    }
    
    @Test
    @DisplayName("Create and retrieve hospital - Should persist and retrieve correctly")
    void createAndRetrieveHospital_ShouldPersistAndRetrieveCorrectly() {
        // Given
        Hospital hospital = new Hospital();
        hospital.setName("Test Hastanesi");
        hospital.setAddress("Test Adresi");
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
        
        // When
        HospitalDTO created = hospitalService.createHospital(hospital);
        
        // Then - Dinamik ID kontrolü (statik değer kullanılmamalı)
        // Not: @Transactional rollback yapılsa bile ID sequence/identity artmaya devam eder
        // Bu yüzden statik ID kontrolü (örn: isEqualTo(1L)) yerine dinamik kontrol yapılmalı
        assertThat(created).isNotNull();
        assertThat(created.getId()).isNotNull(); // ID'nin null olmadığını kontrol et
        assertThat(created.getId()).isPositive(); // ID'nin pozitif olduğunu kontrol et
        assertThat(created.getName()).isEqualTo("Test Hastanesi");
        assertThat(created.getCity()).isEqualTo("İstanbul");
        
        // Retrieve and verify - Dinamik ID ile karşılaştırma
        Long createdId = created.getId(); // ID'yi sakla
        HospitalDTO retrieved = hospitalService.getHospitalById(createdId);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getId()).isEqualTo(createdId); // Dinamik ID ile karşılaştır
        assertThat(retrieved.getName()).isEqualTo("Test Hastanesi");
        assertThat(retrieved.getCity()).isEqualTo("İstanbul");
    }
    
    @Test
    @DisplayName("Get hospitals by city - Should return filtered hospitals")
    void getHospitalsByCity_ShouldReturnFilteredHospitals() {
        // Given
        Hospital hospital1 = createTestHospital("Hospital 1", "İstanbul", "Kadıköy", 25.0);
        Hospital hospital2 = createTestHospital("Hospital 2", "İstanbul", "Şişli", 30.0);
        Hospital hospital3 = createTestHospital("Hospital 3", "Ankara", "Çankaya", 20.0);
        
        HospitalDTO created1 = hospitalService.createHospital(hospital1);
        HospitalDTO created2 = hospitalService.createHospital(hospital2);
        HospitalDTO created3 = hospitalService.createHospital(hospital3);
        
        // When
        List<HospitalDTO> istanbulHospitals = hospitalService.getHospitalsByCity("İstanbul");
        
        // Then - Dinamik ID kontrolleri
        assertThat(istanbulHospitals).isNotNull();
        assertThat(istanbulHospitals).hasSize(2);
        assertThat(istanbulHospitals).allMatch(h -> h.getCity().equals("İstanbul"));
        assertThat(istanbulHospitals).allMatch(h -> h.getId() != null && h.getId() > 0); // Dinamik ID kontrolü
        // Oluşturulan hastanelerin ID'lerini kontrol et (statik değer yerine dinamik)
        assertThat(istanbulHospitals)
                .extracting(HospitalDTO::getId)
                .containsExactlyInAnyOrder(created1.getId(), created2.getId());
        
        // created3'ün ID'si kontrol edilmeli (Ankara'da olduğu için İstanbul listesinde olmamalı)
        assertThat(istanbulHospitals)
                .extracting(HospitalDTO::getId)
                .doesNotContain(created3.getId());
    }
    
    @Test
    @DisplayName("Get hospitals near airport - Should return hospitals within distance")
    void getHospitalsNearAirport_ShouldReturnHospitalsWithinDistance() {
        // Given
        Hospital hospital1 = createTestHospital("Hospital 1", "İstanbul", "Kadıköy", 25.0);
        Hospital hospital2 = createTestHospital("Hospital 2", "İstanbul", "Şişli", 30.0);
        Hospital hospital3 = createTestHospital("Hospital 3", "İstanbul", "Beşiktaş", 35.0);
        
        HospitalDTO created1 = hospitalService.createHospital(hospital1);
        HospitalDTO created2 = hospitalService.createHospital(hospital2);
        HospitalDTO created3 = hospitalService.createHospital(hospital3);
        
        // When
        List<HospitalDTO> nearHospitals = hospitalService.getHospitalsNearAirport(30.0);
        
        // Then - Dinamik ID kontrolleri
        assertThat(nearHospitals).isNotNull();
        assertThat(nearHospitals).hasSize(2);
        assertThat(nearHospitals).allMatch(h -> h.getAirportDistance() <= 30.0);
        assertThat(nearHospitals).allMatch(h -> h.getId() != null && h.getId() > 0); // Dinamik ID kontrolü
        // Oluşturulan hastanelerin ID'lerini kontrol et (statik değer yerine dinamik)
        assertThat(nearHospitals)
                .extracting(HospitalDTO::getId)
                .containsExactlyInAnyOrder(created1.getId(), created2.getId())
                .doesNotContain(created3.getId()); // 35.0 km > 30.0 km, bu yüzden dahil edilmemeli
    }
    
    private Hospital createTestHospital(String name, String city, String district, Double airportDistance) {
        Hospital hospital = new Hospital();
        hospital.setName(name);
        hospital.setAddress("Test Address");
        hospital.setCity(city);
        hospital.setDistrict(district);
        hospital.setPhone("02121234567");
        hospital.setEmail("test@hospital.com");
        hospital.setLatitude(41.0);
        hospital.setLongitude(29.0);
        hospital.setAirportDistance(airportDistance);
        hospital.setAirportDistanceMinutes(30);
        hospital.setRating(4.5);
        hospital.setTotalReviews(10);
        hospital.setIsActive(true);
        return hospital;
    }
}

