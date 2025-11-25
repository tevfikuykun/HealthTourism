package com.healthtourism.hospitalservice.service;

import com.healthtourism.hospitalservice.dto.HospitalDTO;
import com.healthtourism.hospitalservice.entity.Hospital;
import com.healthtourism.hospitalservice.repository.HospitalRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Hospital Service Unit Tests")
class HospitalServiceTest {

    @Mock
    private HospitalRepository hospitalRepository;

    @InjectMocks
    private HospitalService hospitalService;

    private Hospital hospital;

    @BeforeEach
    void setUp() {
        hospital = new Hospital();
        hospital.setId(1L);
        hospital.setName("Test Hospital");
        hospital.setCity("Istanbul");
        hospital.setDistrict("Kadıköy");
        hospital.setAddress("Test Address");
        hospital.setRating(4.5);
        hospital.setIsActive(true);
        hospital.setAirportDistance(30.0);
    }

    @Test
    @DisplayName("Should get hospital by id")
    void testGetHospitalById() {
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));

        HospitalDTO result = hospitalService.getHospitalById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Hospital", result.getName());
        verify(hospitalRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should get all active hospitals")
    void testGetAllActiveHospitals() {
        when(hospitalRepository.findAllActiveOrderByRatingDesc())
            .thenReturn(Arrays.asList(hospital));

        List<HospitalDTO> result = hospitalService.getAllActiveHospitals();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(hospitalRepository, times(1)).findAllActiveOrderByRatingDesc();
    }

    @Test
    @DisplayName("Should get hospitals by city")
    void testGetHospitalsByCity() {
        when(hospitalRepository.findByCity("Istanbul"))
            .thenReturn(Arrays.asList(hospital));

        List<HospitalDTO> result = hospitalService.getHospitalsByCity("Istanbul");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Istanbul", result.get(0).getCity());
        verify(hospitalRepository, times(1)).findByCity("Istanbul");
    }

    @Test
    @DisplayName("Should create hospital successfully")
    void testCreateHospital() {
        when(hospitalRepository.save(any(Hospital.class))).thenAnswer(invocation -> {
            Hospital h = invocation.getArgument(0);
            h.setId(1L);
            return h;
        });

        HospitalDTO result = hospitalService.createHospital(hospital);

        assertNotNull(result);
        assertEquals("Test Hospital", result.getName());
        verify(hospitalRepository, times(1)).save(any(Hospital.class));
    }

    @Test
    @DisplayName("Should update hospital successfully")
    void testUpdateHospital() {
        when(hospitalRepository.findById(1L)).thenReturn(Optional.of(hospital));
        when(hospitalRepository.save(any(Hospital.class))).thenReturn(hospital);

        Hospital updated = new Hospital();
        updated.setName("Updated Hospital");
        updated.setCity("Ankara");

        HospitalDTO result = hospitalService.updateHospital(1L, updated);

        assertNotNull(result);
        verify(hospitalRepository, times(1)).findById(1L);
        verify(hospitalRepository, times(1)).save(any(Hospital.class));
    }
}

