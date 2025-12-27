package com.example.HealthTourism.service;

import com.example.HealthTourism.dto.HospitalDTO;
import com.example.HealthTourism.entity.Hospital;
import com.example.HealthTourism.exception.HospitalNotFoundException;
import com.example.HealthTourism.mapper.HospitalMapper;
import com.example.HealthTourism.repository.DoctorRepository;
import com.example.HealthTourism.repository.HospitalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Hospital Service Test
 * Service katmanı için unit testler
 * 
 * Test Coverage:
 * - getAllActiveHospitals() - Tüm aktif hastaneleri getir
 * - getHospitalById() - ID ile hastane getir
 * - getHospitalsByCity() - Şehre göre filtrele
 * - getHospitalsByDistrict() - İlçeye göre filtrele
 * - getHospitalsNearAirport() - Havalimanına yakın hastaneler
 * - createHospital() - Yeni hastane oluştur
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Hospital Service Tests")
class HospitalServiceTest {
    
    @Mock
    private HospitalRepository hospitalRepository;
    
    @Mock
    private DoctorRepository doctorRepository;
    
    @Mock
    private HospitalMapper hospitalMapper;
    
    @InjectMocks
    private HospitalService hospitalService;
    
    private Hospital hospital1;
    private Hospital hospital2;
    private HospitalDTO hospitalDTO1;
    private HospitalDTO hospitalDTO2;
    
    @BeforeEach
    void setUp() {
        hospital1 = new Hospital();
        hospital1.setId(1L);
        hospital1.setName("Acıbadem Hastanesi");
        hospital1.setCity("İstanbul");
        hospital1.setDistrict("Kadıköy");
        hospital1.setRating(4.8);
        hospital1.setAirportDistance(25.5);
        hospital1.setIsActive(true);
        
        hospital2 = new Hospital();
        hospital2.setId(2L);
        hospital2.setName("Memorial Hastanesi");
        hospital2.setCity("İstanbul");
        hospital2.setDistrict("Şişli");
        hospital2.setRating(4.9);
        hospital2.setAirportDistance(30.0);
        hospital2.setIsActive(true);
        
        hospitalDTO1 = new HospitalDTO();
        hospitalDTO1.setId(1L);
        hospitalDTO1.setName("Acıbadem Hastanesi");
        hospitalDTO1.setCity("İstanbul");
        hospitalDTO1.setDistrict("Kadıköy");
        hospitalDTO1.setRating(4.8);
        
        hospitalDTO2 = new HospitalDTO();
        hospitalDTO2.setId(2L);
        hospitalDTO2.setName("Memorial Hastanesi");
        hospitalDTO2.setCity("İstanbul");
        hospitalDTO2.setDistrict("Şişli");
        hospitalDTO2.setRating(4.9);
    }
    
    @Test
    @DisplayName("getAllActiveHospitals - Should return all active hospitals")
    void getAllActiveHospitals_ShouldReturnAllActiveHospitals() {
        // Given
        List<Hospital> hospitals = Arrays.asList(hospital1, hospital2);
        when(hospitalRepository.findAllActiveOrderByRatingDesc()).thenReturn(hospitals);
        when(hospitalMapper.toDto(hospital1)).thenReturn(hospitalDTO1);
        when(hospitalMapper.toDto(hospital2)).thenReturn(hospitalDTO2);
        when(hospitalMapper.extractSpecializations(any())).thenReturn(List.of());
        when(hospitalMapper.countActiveDoctors(any())).thenReturn(0);
        
        // When
        List<HospitalDTO> result = hospitalService.getAllActiveHospitals();
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(1).getId()).isEqualTo(2L);
        verify(hospitalRepository, times(1)).findAllActiveOrderByRatingDesc();
    }
    
    @Test
    @DisplayName("getHospitalById - Should return hospital when exists")
    void getHospitalById_ShouldReturnHospital_WhenExists() {
        // Given
        Long hospitalId = 1L;
        when(hospitalRepository.findByIdWithDoctors(hospitalId)).thenReturn(Optional.of(hospital1));
        when(hospitalMapper.toDto(hospital1)).thenReturn(hospitalDTO1);
        when(hospitalMapper.extractSpecializations(any())).thenReturn(List.of());
        when(hospitalMapper.countActiveDoctors(any())).thenReturn(0);
        
        // When
        HospitalDTO result = hospitalService.getHospitalById(hospitalId);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Acıbadem Hastanesi");
        verify(hospitalRepository, times(1)).findByIdWithDoctors(hospitalId);
    }
    
    @Test
    @DisplayName("getHospitalById - Should throw HospitalNotFoundException when not exists")
    void getHospitalById_ShouldThrowException_WhenNotExists() {
        // Given
        Long hospitalId = 999L;
        when(hospitalRepository.findByIdWithDoctors(hospitalId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> hospitalService.getHospitalById(hospitalId))
                .isInstanceOf(HospitalNotFoundException.class)
                .hasMessageContaining("Hospital not found");
        verify(hospitalRepository, times(1)).findByIdWithDoctors(hospitalId);
    }
    
    @Test
    @DisplayName("getHospitalsByCity - Should return hospitals in specified city")
    void getHospitalsByCity_ShouldReturnHospitalsInCity() {
        // Given
        String city = "İstanbul";
        List<Hospital> hospitals = Arrays.asList(hospital1, hospital2);
        when(hospitalRepository.findByCityAndIsActiveTrue(city)).thenReturn(hospitals);
        when(hospitalMapper.toDto(hospital1)).thenReturn(hospitalDTO1);
        when(hospitalMapper.toDto(hospital2)).thenReturn(hospitalDTO2);
        when(hospitalMapper.extractSpecializations(any())).thenReturn(List.of());
        when(hospitalMapper.countActiveDoctors(any())).thenReturn(0);
        
        // When
        List<HospitalDTO> result = hospitalService.getHospitalsByCity(city);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCity()).isEqualTo("İstanbul");
        assertThat(result.get(1).getCity()).isEqualTo("İstanbul");
        verify(hospitalRepository, times(1)).findByCityAndIsActiveTrue(city);
    }
    
    @Test
    @DisplayName("getHospitalsByCity - Should throw IllegalArgumentException when city is null")
    void getHospitalsByCity_ShouldThrowException_WhenCityIsNull() {
        // When & Then
        assertThatThrownBy(() -> hospitalService.getHospitalsByCity(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("City cannot be null or empty");
    }
    
    @Test
    @DisplayName("getHospitalsByCity - Should throw IllegalArgumentException when city is empty")
    void getHospitalsByCity_ShouldThrowException_WhenCityIsEmpty() {
        // When & Then
        assertThatThrownBy(() -> hospitalService.getHospitalsByCity("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("City cannot be null or empty");
    }
    
    @Test
    @DisplayName("getHospitalsByDistrict - Should return hospitals in specified district")
    void getHospitalsByDistrict_ShouldReturnHospitalsInDistrict() {
        // Given
        String district = "Kadıköy";
        List<Hospital> hospitals = Arrays.asList(hospital1);
        when(hospitalRepository.findByDistrictAndIsActiveTrue(district)).thenReturn(hospitals);
        when(hospitalMapper.toDto(hospital1)).thenReturn(hospitalDTO1);
        when(hospitalMapper.extractSpecializations(any())).thenReturn(List.of());
        when(hospitalMapper.countActiveDoctors(any())).thenReturn(0);
        
        // When
        List<HospitalDTO> result = hospitalService.getHospitalsByDistrict(district);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDistrict()).isEqualTo("Kadıköy");
        verify(hospitalRepository, times(1)).findByDistrictAndIsActiveTrue(district);
    }
    
    @Test
    @DisplayName("getHospitalsNearAirport - Should return hospitals within max distance")
    void getHospitalsNearAirport_ShouldReturnHospitalsWithinDistance() {
        // Given
        Double maxDistance = 30.0;
        List<Hospital> hospitals = Arrays.asList(hospital1, hospital2);
        when(hospitalRepository.findByAirportDistanceLessThanEqual(maxDistance)).thenReturn(hospitals);
        when(hospitalMapper.toDto(hospital1)).thenReturn(hospitalDTO1);
        when(hospitalMapper.toDto(hospital2)).thenReturn(hospitalDTO2);
        when(hospitalMapper.extractSpecializations(any())).thenReturn(List.of());
        when(hospitalMapper.countActiveDoctors(any())).thenReturn(0);
        
        // When
        List<HospitalDTO> result = hospitalService.getHospitalsNearAirport(maxDistance);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        verify(hospitalRepository, times(1)).findByAirportDistanceLessThanEqual(maxDistance);
    }
}

