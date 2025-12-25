package com.healthtourism.reservationservice.controller;

import com.healthtourism.jpa.dto.ApiResponse;
import com.healthtourism.jpa.util.IdempotencyHelper;
import com.healthtourism.jpa.util.SecurityContextHelper;
import com.healthtourism.reservationservice.dto.ReservationDTO;
import com.healthtourism.reservationservice.dto.ReservationRequestDTO;
import com.healthtourism.reservationservice.dto.StatusUpdateRequest;
import com.healthtourism.reservationservice.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Reservation Controller - Professional Enterprise Implementation
 * 
 * Best Practices Applied:
 * - API versioning (/api/v1/reservations)
 * - IDOR prevention (user ID from SecurityContext, not URL)
 * - Global exception handling (no try-catch in controller)
 * - Standardized ApiResponse wrapper
 * - Swagger/OpenAPI documentation
 * - Role-based access control (@PreAuthorize)
 * - Input validation (@Valid)
 * - Pagination and Sorting (Pageable)
 * - Idempotency support (X-Idempotency-Key header)
 * - HATEOAS (self-discovery links)
 * - Comprehensive logging
 * 
 * Security:
 * - User ID is NEVER taken from URL parameters
 * - User ID is ALWAYS extracted from SecurityContext (JWT token)
 * - Prevents IDOR (Insecure Direct Object Reference) attacks
 */
@RestController
@RequestMapping("/api/v1/reservations") // API versioning
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Reservations", description = "Reservation management API")
public class ReservationController {
    
    private final ReservationService reservationService;
    private final IdempotencyHelper idempotencyHelper;
    private final PagedResourcesAssembler<ReservationDTO> pagedResourcesAssembler;
    
    /**
     * Create a new reservation
     * 
     * Security: User ID is extracted from JWT token (SecurityContext)
     * Idempotency: X-Idempotency-Key header prevents duplicate operations
     * 
     * @param idempotencyKey Idempotency key from header (optional but recommended)
     * @param request Reservation request data
     * @return Created reservation with HATEOAS links
     */
    @PostMapping
    @Operation(
        summary = "Create new reservation",
        description = "Creates a new reservation for the authenticated user. " +
                     "User ID is automatically extracted from JWT token. " +
                     "Include X-Idempotency-Key header to prevent duplicate operations."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Reservation created successfully",
            content = @Content(schema = @Schema(implementation = ReservationDTO.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid request data"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "Duplicate request (idempotency key already used)"
        )
    })
    public ResponseEntity<EntityModel<ReservationDTO>> createReservation(
            @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody ReservationRequestDTO request) {
        
        // SECURITY: Get user ID from SecurityContext (JWT token), NOT from request
        UUID userId = SecurityContextHelper.getCurrentUserId();
        log.info("Creating reservation for user: {} (idempotency key: {})", userId, idempotencyKey);
        
        // Validate idempotency key format if provided
        if (idempotencyKey != null && !idempotencyHelper.isValidFormat(idempotencyKey)) {
            throw new IllegalArgumentException("Geçersiz idempotency key formatı. UUID formatında olmalıdır.");
        }
        
        ReservationDTO reservation = reservationService.createReservation(request, userId, idempotencyKey);
        
        // HATEOAS: Add self-discovery links
        EntityModel<ReservationDTO> resource = EntityModel.of(reservation,
            linkTo(methodOn(ReservationController.class).getReservationById(reservation.getId())).withSelfRel(),
            linkTo(methodOn(ReservationController.class).cancelReservation(reservation.getId())).withRel("cancel"),
            linkTo(methodOn(ReservationController.class).getMyReservations(Pageable.unpaged())).withRel("my-reservations")
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }
    
    /**
     * Get current user's reservations with pagination and sorting
     * 
     * Security: User ID is extracted from JWT token (SecurityContext)
     * Pagination: ?page=0&size=20&sort=appointmentDate,desc
     * 
     * @param pageable Pagination and sorting parameters
     * @return Paginated list of user's reservations with HATEOAS links
     */
    @GetMapping("/my-reservations")
    @Operation(
        summary = "Get my reservations",
        description = "Returns paginated reservations for the authenticated user. " +
                     "Supports pagination (?page=0&size=20) and sorting (?sort=appointmentDate,desc). " +
                     "User ID is automatically extracted from JWT token."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Reservations retrieved successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized"
        )
    })
    public ResponseEntity<PagedModel<EntityModel<ReservationDTO>>> getMyReservations(
            @ParameterObject @PageableDefault(size = 20, sort = "appointmentDate") Pageable pageable) {
        
        // SECURITY: Get user ID from SecurityContext (JWT token), NOT from URL
        UUID userId = SecurityContextHelper.getCurrentUserId();
        log.debug("Fetching reservations for user: {} (page: {}, size: {})", 
            userId, pageable.getPageNumber(), pageable.getPageSize());
        
        Page<ReservationDTO> reservations = reservationService.getReservationsByUserId(userId, pageable);
        
        // HATEOAS: Convert Page to PagedModel with links
        PagedModel<EntityModel<ReservationDTO>> pagedModel = pagedResourcesAssembler.toModel(
            reservations,
            reservation -> EntityModel.of(reservation,
                linkTo(methodOn(ReservationController.class).getReservationById(reservation.getId())).withSelfRel(),
                linkTo(methodOn(ReservationController.class).cancelReservation(reservation.getId())).withRel("cancel")
            )
        );
        
        return ResponseEntity.ok(pagedModel);
    }
    
    /**
     * Get reservation by ID with HATEOAS links
     * 
     * Security: Only the owner of the reservation or admin can access
     * 
     * @param id Reservation ID
     * @return Reservation details with HATEOAS links
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Get reservation by ID",
        description = "Returns reservation details with available actions (HATEOAS). " +
                     "Only the owner or admin can access."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Reservation retrieved successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Access denied - not the owner"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Reservation not found"
        )
    })
    public ResponseEntity<EntityModel<ReservationDTO>> getReservationById(
            @Parameter(description = "Reservation ID", required = true)
            @PathVariable UUID id) {
        
        // SECURITY: Get user ID from SecurityContext to verify ownership
        UUID userId = SecurityContextHelper.getCurrentUserId();
        log.debug("Fetching reservation: {} for user: {}", id, userId);
        
        ReservationDTO reservation = reservationService.getReservationById(id, userId);
        
        // HATEOAS: Add self-discovery links based on reservation status
        EntityModel<ReservationDTO> resource = EntityModel.of(reservation,
            linkTo(methodOn(ReservationController.class).getReservationById(id)).withSelfRel()
        );
        
        // Add conditional links based on reservation status
        if (reservation.getStatus().canBeCancelled()) {
            resource.add(linkTo(methodOn(ReservationController.class).cancelReservation(id)).withRel("cancel"));
        }
        
        // Add invoice link (if payment service exists)
        // resource.add(linkTo(methodOn(PaymentController.class).getInvoice(id)).withRel("invoice"));
        
        return ResponseEntity.ok(resource);
    }
    
    /**
     * Update reservation status
     * 
     * Security: Only ADMIN can update reservation status
     * 
     * @param id Reservation ID
     * @param statusRequest Status update request
     * @return Updated reservation
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Update reservation status",
        description = "Updates reservation status. Only admins can perform this operation."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Status updated successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - admin role required"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Reservation not found"
        )
    })
    public ResponseEntity<EntityModel<ReservationDTO>> updateStatus(
            @Parameter(description = "Reservation ID", required = true)
            @PathVariable UUID id,
            @Valid @RequestBody StatusUpdateRequest statusRequest) {
        
        log.info("Updating reservation status: {} to {}", id, statusRequest.getStatus());
        
        ReservationDTO reservation = reservationService.updateReservationStatus(id, statusRequest.getStatus());
        
        // HATEOAS: Add links
        EntityModel<ReservationDTO> resource = EntityModel.of(reservation,
            linkTo(methodOn(ReservationController.class).getReservationById(id)).withSelfRel()
        );
        
        return ResponseEntity.ok(resource);
    }
    
    /**
     * Cancel reservation
     * 
     * Security: Only the owner of the reservation can cancel
     * 
     * @param id Reservation ID
     * @return Cancelled reservation
     */
    @PostMapping("/{id}/cancel")
    @Operation(
        summary = "Cancel reservation",
        description = "Cancels a reservation. Only the owner can cancel their reservation."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Reservation cancelled successfully"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Access denied - not the owner"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Reservation not found"
        )
    })
    public ResponseEntity<EntityModel<ReservationDTO>> cancelReservation(
            @Parameter(description = "Reservation ID", required = true)
            @PathVariable UUID id) {
        
        // SECURITY: Get user ID from SecurityContext to verify ownership
        UUID userId = SecurityContextHelper.getCurrentUserId();
        log.info("Cancelling reservation: {} by user: {}", id, userId);
        
        ReservationDTO reservation = reservationService.cancelReservation(id, userId);
        
        // HATEOAS: Add links
        EntityModel<ReservationDTO> resource = EntityModel.of(reservation,
            linkTo(methodOn(ReservationController.class).getReservationById(id)).withSelfRel(),
            linkTo(methodOn(ReservationController.class).getMyReservations(Pageable.unpaged())).withRel("my-reservations")
        );
        
        return ResponseEntity.ok(resource);
    }
    
    /**
     * Confirm reservation
     * 
     * Security: Only ADMIN can confirm reservations
     * 
     * @param id Reservation ID
     * @return Confirmed reservation
     */
    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Confirm reservation",
        description = "Confirms a reservation. Only admins can perform this operation."
    )
    public ResponseEntity<EntityModel<ReservationDTO>> confirmReservation(
            @Parameter(description = "Reservation ID", required = true)
            @PathVariable UUID id) {
        
        log.info("Confirming reservation: {}", id);
        
        ReservationDTO reservation = reservationService.confirmReservation(id);
        
        // HATEOAS: Add links
        EntityModel<ReservationDTO> resource = EntityModel.of(reservation,
            linkTo(methodOn(ReservationController.class).getReservationById(id)).withSelfRel()
        );
        
        return ResponseEntity.ok(resource);
    }
}
