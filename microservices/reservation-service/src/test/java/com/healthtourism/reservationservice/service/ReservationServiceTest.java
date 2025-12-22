package com.healthtourism.reservationservice.service;

import com.healthtourism.reservationservice.dto.ReservationDTO;
import com.healthtourism.reservationservice.dto.ReservationRequestDTO;
import com.healthtourism.reservationservice.entity.Reservation;
import com.healthtourism.reservationservice.repository.ReservationRepository;
import com.healthtourism.reservationservice.util.ReservationNumberGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Reservation Service Unit Tests")
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private KafkaEventService kafkaEventService;

    @Mock
    private EventStoreService eventStoreService;

    @Mock
    private ReservationNumberGenerator reservationNumberGenerator;

    @Mock
    private PriceCalculationService priceCalculationService;

    @InjectMocks
    private ReservationService reservationService;

    private ReservationRequestDTO request;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        request = new ReservationRequestDTO();
        request.setUserId(1L);
        request.setHospitalId(10L);
        request.setDoctorId(20L);
        request.setAppointmentDate(LocalDateTime.now().plusDays(7));
        request.setCheckInDate(LocalDateTime.now().plusDays(7));
        request.setCheckOutDate(LocalDateTime.now().plusDays(10));

        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setReservationNumber("RES12345");
        reservation.setUserId(1L);
        reservation.setHospitalId(10L);
        reservation.setDoctorId(20L);
        reservation.setAppointmentDate(request.getAppointmentDate());
        reservation.setCheckInDate(request.getCheckInDate());
        reservation.setCheckOutDate(request.getCheckOutDate());
        reservation.setTotalPrice(new BigDecimal("1000.00"));
        reservation.setStatus("PENDING");
    }

    @Test
    @DisplayName("Should create reservation successfully")
    void testCreateReservationSuccess() {
        when(reservationRepository.findConflictingReservations(any(), any(), any()))
            .thenReturn(Collections.emptyList());
        when(reservationNumberGenerator.generateReservationNumber()).thenReturn("RES_TEST_0001");
        when(priceCalculationService.calculateTotalPrice(any(), any(), anyInt(), any()))
            .thenReturn(new BigDecimal("1000.00"));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation r = invocation.getArgument(0);
            r.setId(1L);
            return r;
        });

        ReservationDTO result = reservationService.createReservation(request);

        assertNotNull(result);
        assertEquals(request.getUserId(), result.getUserId());
        assertEquals("PENDING", result.getStatus());
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Should throw exception when appointment time is conflicting")
    void testCreateReservationConflict() {
        when(reservationRepository.findConflictingReservations(any(), any(), any()))
            .thenReturn(Arrays.asList(reservation));

        assertThrows(RuntimeException.class, () -> 
            reservationService.createReservation(request));
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    @DisplayName("Should get reservations by user")
    void testGetReservationsByUser() {
        when(reservationRepository.findByUserIdOrderByCreatedAtDesc(1L))
            .thenReturn(Arrays.asList(reservation));

        List<ReservationDTO> result = reservationService.getReservationsByUser(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reservationRepository, times(1)).findByUserIdOrderByCreatedAtDesc(1L);
    }

    @Test
    @DisplayName("Should update reservation status")
    void testUpdateReservationStatus() {
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);
        when(eventStoreService.getEventsByReservationId(1L)).thenReturn(Collections.emptyList());

        ReservationDTO result = reservationService.updateReservationStatus(1L, "CONFIRMED");

        assertNotNull(result);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(kafkaEventService, times(1)).publishReservationUpdated(anyLong(), anyString());
    }
}

