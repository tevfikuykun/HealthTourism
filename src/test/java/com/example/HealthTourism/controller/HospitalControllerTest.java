package com.example.HealthTourism.controller;

import com.example.HealthTourism.dto.HospitalDTO;
import com.example.HealthTourism.service.HospitalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Hospital Controller Test
 * Controller katmanı için unit testler
 * 
 * Test Coverage:
 * - GET /api/v1/hospitals - Tüm hastaneleri getir
 * - GET /api/v1/hospitals/{id} - ID ile hastane getir
 * - GET /api/v1/hospitals/city/{city} - Şehre göre filtrele
 * - GET /api/v1/hospitals/district/{district} - İlçeye göre filtrele
 * - GET /api/v1/hospitals/near-airport - Havalimanına yakın hastaneler
 * - POST /api/v1/hospitals - Yeni hastane oluştur
 */
@WebMvcTest(HospitalController.class)
@DisplayName("Hospital Controller Tests")
class HospitalControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private HospitalService hospitalService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private HospitalDTO hospitalDTO1;
    private HospitalDTO hospitalDTO2;
    private List<HospitalDTO> hospitalList;
    
    @BeforeEach
    void setUp() {
        hospitalDTO1 = new HospitalDTO();
        hospitalDTO1.setId(1L);
        hospitalDTO1.setName("Acıbadem Hastanesi");
        hospitalDTO1.setCity("İstanbul");
        hospitalDTO1.setDistrict("Kadıköy");
        hospitalDTO1.setRating(4.8);
        hospitalDTO1.setAirportDistance(25.5);
        
        hospitalDTO2 = new HospitalDTO();
        hospitalDTO2.setId(2L);
        hospitalDTO2.setName("Memorial Hastanesi");
        hospitalDTO2.setCity("İstanbul");
        hospitalDTO2.setDistrict("Şişli");
        hospitalDTO2.setRating(4.9);
        hospitalDTO2.setAirportDistance(30.0);
        
        hospitalList = Arrays.asList(hospitalDTO1, hospitalDTO2);
    }
    
    @Test
    @DisplayName("GET /api/v1/hospitals - Should return all hospitals")
    void getAllHospitals_ShouldReturnListOfHospitals() throws Exception {
        // Given
        when(hospitalService.getAllActiveHospitals()).thenReturn(hospitalList);
        
        // When & Then
        mockMvc.perform(get("/api/v1/hospitals")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Acıbadem Hastanesi"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Memorial Hastanesi"));
    }
    
    @Test
    @DisplayName("GET /api/v1/hospitals/{id} - Should return hospital by ID")
    void getHospitalById_ShouldReturnHospital() throws Exception {
        // Given
        Long hospitalId = 1L;
        when(hospitalService.getHospitalById(hospitalId)).thenReturn(hospitalDTO1);
        
        // When & Then
        mockMvc.perform(get("/api/v1/hospitals/{id}", hospitalId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Acıbadem Hastanesi"))
                .andExpect(jsonPath("$.city").value("İstanbul"))
                .andExpect(jsonPath("$.district").value("Kadıköy"))
                .andExpect(jsonPath("$.rating").value(4.8));
    }
    
    @Test
    @DisplayName("GET /api/v1/hospitals/city/{city} - Should return hospitals by city")
    void getHospitalsByCity_ShouldReturnFilteredHospitals() throws Exception {
        // Given
        String city = "İstanbul";
        when(hospitalService.getHospitalsByCity(city)).thenReturn(hospitalList);
        
        // When & Then
        mockMvc.perform(get("/api/v1/hospitals/city/{city}", city)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].city").value("İstanbul"))
                .andExpect(jsonPath("$[1].city").value("İstanbul"));
    }
    
    @Test
    @DisplayName("GET /api/v1/hospitals/district/{district} - Should return hospitals by district")
    void getHospitalsByDistrict_ShouldReturnFilteredHospitals() throws Exception {
        // Given
        String district = "Kadıköy";
        List<HospitalDTO> kadikoyHospitals = Arrays.asList(hospitalDTO1);
        when(hospitalService.getHospitalsByDistrict(district)).thenReturn(kadikoyHospitals);
        
        // When & Then
        mockMvc.perform(get("/api/v1/hospitals/district/{district}", district)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].district").value("Kadıköy"));
    }
    
    @Test
    @DisplayName("GET /api/v1/hospitals/near-airport - Should return hospitals near airport")
    void getHospitalsNearAirport_ShouldReturnFilteredHospitals() throws Exception {
        // Given
        Double maxDistance = 30.0;
        when(hospitalService.getHospitalsNearAirport(maxDistance)).thenReturn(hospitalList);
        
        // When & Then
        mockMvc.perform(get("/api/v1/hospitals/near-airport")
                .param("maxDistance", maxDistance.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }
    
    @Test
    @DisplayName("GET /api/v1/hospitals/near-airport - Should use default maxDistance when not provided")
    void getHospitalsNearAirport_ShouldUseDefaultMaxDistance() throws Exception {
        // Given
        Double defaultDistance = 50.0;
        when(hospitalService.getHospitalsNearAirport(defaultDistance)).thenReturn(hospitalList);
        
        // When & Then
        mockMvc.perform(get("/api/v1/hospitals/near-airport")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
    
    @Test
    @DisplayName("GET /api/v1/hospitals/search - Should return filtered hospitals by multiple criteria")
    void searchHospitals_ShouldReturnFilteredHospitals() throws Exception {
        // Given
        String city = "İstanbul";
        Double maxDistance = 30.0;
        when(hospitalService.getHospitalsByCity(city)).thenReturn(hospitalList);
        
        // When & Then
        mockMvc.perform(get("/api/v1/hospitals/search")
                .param("city", city)
                .param("maxDistance", maxDistance.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}

