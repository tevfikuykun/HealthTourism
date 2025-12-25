# Reservation Entity Profesyonel Revizyon Özeti

## 🎯 Yapılan İyileştirmeler

### 1. ReservationStatus Enum ✅

**Önce:**
```java
private String status; // "PNDING" typo riski
```

**Sonra:**
```java
@Enumerated(EnumType.STRING)
@Column(nullable = false, length = 30)
private ReservationStatus status = ReservationStatus.PENDING;
```

**Enum States:**
- `PENDING` - Beklemede
- `CONFIRMED` - Onaylandı
- `CANCELLED` - İptal Edildi
- `COMPLETED` - Tamamlandı
- `NO_SHOW` - Gelmedi
- `REFUNDING` - İade İşleminde
- `REFUNDED` - İade Edildi

**Business Logic Methods:**
```java
status.canBeCancelled() // PENDING or CONFIRMED
status.isFinalState() // COMPLETED, CANCELLED, REFUNDED, NO_SHOW
status.allowsRefund() // CANCELLED, CONFIRMED, NO_SHOW
```

### 2. ReservationNumberGenerator ✅

**Format:** `HT-YYYY-MMDD-XXXX`
**Example:** `HT-2024-0325-A3B7`

**Features:**
- ✅ Predictable format (HT prefix, year, date)
- ✅ Unpredictable suffix (random characters) for security
- ✅ Human-readable for customer support
- ✅ Unique identifier

### 3. Appointment Conflict Detection ✅

**Business Rule:** A doctor cannot have two appointments at the same time

**Implementation:**
```java
@Query("""
    SELECT r FROM Reservation r
    WHERE r.doctorId = :doctorId
    AND r.status IN :activeStatuses
    AND r.appointmentDate BETWEEN :startTime AND :endTime
    """)
List<Reservation> findConflictingAppointments(...)
```

**Conflict Check:**
- Checks 60 minutes before and after appointment time
- Only considers ACTIVE statuses (PENDING, CONFIRMED)
- Excludes current reservation (for updates)

### 4. Price Calculation (calculateTotal) ✅

**Business Logic:**
```java
public void calculateTotal(BigDecimal doctorFee, BigDecimal accommodationDailyPrice) {
    this.doctorFeeSnapshot = doctorFee;
    BigDecimal accommodationCost = accommodationDailyPrice * numberOfNights;
    this.totalPrice = doctorFee + accommodationCost;
}
```

**Features:**
- ✅ Automatic calculation
- ✅ Price snapshots (historical accuracy)
- ✅ Handles optional accommodation
- ✅ BigDecimal precision

### 5. Lazy Loading ✅

**All Relationships:**
- User: `@Column(name = "user_id")` - ID only (microservice)
- Doctor: `@Column(name = "doctor_id")` - ID only (microservice)
- Hospital: `@Column(name = "hospital_id")` - ID only (microservice)
- Accommodation: `@Column(name = "accommodation_id")` - ID only (microservice)

**Faydalar:**
- ✅ No N+1 problems
- ✅ Microservice architecture compatible
- ✅ Efficient queries

### 6. Audit Fields ✅

**JPA Auditing:**
- `createdAt` - @CreatedDate
- `updatedAt` - @LastModifiedDate
- `createdBy` - @CreatedBy
- `updatedBy` - @LastModifiedBy
- `version` - @Version (Optimistic Locking)

### 7. Soft Delete ✅

```java
@SQLDelete(sql = "UPDATE reservations SET deleted = true, updated_at = CURRENT_TIMESTAMP WHERE id = ?")
@Where(clause = "deleted = false")
```

### 8. Database Indexes ✅

```java
@Table(name = "reservations", indexes = {
    @Index(name = "idx_res_number", columnList = "reservation_number", unique = true),
    @Index(name = "idx_res_status", columnList = "status"),
    @Index(name = "idx_res_user_id", columnList = "user_id"),
    @Index(name = "idx_res_doctor_id", columnList = "doctor_id"),
    @Index(name = "idx_res_hospital_id", columnList = "hospital_id"),
    @Index(name = "idx_res_appointment_date", columnList = "appointment_date"),
    @Index(name = "idx_res_status_date", columnList = "status, appointment_date"),
    @Index(name = "idx_res_doctor_date", columnList = "doctor_id, appointment_date"),
    @Index(name = "idx_res_deleted", columnList = "deleted")
})
```

## 📊 Önce ve Sonra Karşılaştırması

| Özellik | Önce | Sonra |
|---------|------|-------|
| Status | ❌ String | ✅ Enum (ReservationStatus) |
| Reservation Number | ❌ ? | ✅ HT-YYYY-MMDD-XXXX format |
| Conflict Detection | ❌ Yok | ✅ Repository query |
| Price Calculation | ❌ Manuel | ✅ calculateTotal() method |
| Relationships | ❌ EAGER | ✅ LAZY (IDs only) |
| Audit Fields | ⚠️ createdAt only | ✅ Full JPA Auditing |
| Soft Delete | ❌ Yok | ✅ @SQLDelete + @Where |
| Indexes | ❌ Basic | ✅ 9 indexes (optimized) |
| Business Logic | ❌ Yok | ✅ Lifecycle methods |

## 🔒 Business Rules Uygulandı

### 1. Appointment Conflict Check

```java
// A doctor cannot have two appointments at the same time
checkAppointmentConflict(doctorId, appointmentDate, excludeReservationId);
```

### 2. Status Lifecycle Management

```java
reservation.cancel(); // Only PENDING or CONFIRMED
reservation.confirm(); // Only PENDING
reservation.complete(); // Only CONFIRMED
```

### 3. Price Calculation

```java
reservation.calculateTotal(doctorFee, accommodationDailyPrice);
// Automatically calculates: doctorFee + (accommodationDailyPrice * numberOfNights)
```

### 4. Optional: User Daily Limit

```java
// Optional business rule: User cannot have multiple appointments on same day
checkUserDailyLimit(userId, appointmentDate);
```

## 📁 Oluşturulan Dosyalar

**Entities:**
- `Reservation.java` - Profesyonel entity
- `ReservationStatus.java` - Status enum

**Services:**
- `ReservationService.java` - Business logic
- `ReservationNumberGenerator.java` - Number generation

**Repositories:**
- `ReservationRepository.java` - Conflict detection queries

**Exceptions:**
- `ReservationNotFoundException.java`
- `ReservationConflictException.java`
- `BusinessRuleException.java`

## 🗄️ Database Schema

```sql
CREATE TABLE reservations (
    id BIGSERIAL PRIMARY KEY,
    reservation_number VARCHAR(20) NOT NULL UNIQUE,
    appointment_date TIMESTAMP NOT NULL,
    check_in_date TIMESTAMP NOT NULL,
    check_out_date TIMESTAMP NOT NULL,
    number_of_nights INTEGER NOT NULL,
    total_price DECIMAL(19,4) NOT NULL,
    status VARCHAR(30) NOT NULL,
    notes TEXT,
    contact_preference VARCHAR(50),
    user_id BIGINT NOT NULL,
    hospital_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    accommodation_id BIGINT,
    doctor_fee_snapshot DECIMAL(10,2),
    accommodation_daily_price_snapshot DECIMAL(10,2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP,
    updated_by VARCHAR(255),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    version BIGINT DEFAULT 0
);

-- Indexes
CREATE INDEX idx_res_number ON reservations(reservation_number);
CREATE INDEX idx_res_status ON reservations(status);
CREATE INDEX idx_res_user_id ON reservations(user_id);
CREATE INDEX idx_res_doctor_id ON reservations(doctor_id);
CREATE INDEX idx_res_hospital_id ON reservations(hospital_id);
CREATE INDEX idx_res_appointment_date ON reservations(appointment_date);
CREATE INDEX idx_res_status_date ON reservations(status, appointment_date);
CREATE INDEX idx_res_doctor_date ON reservations(doctor_id, appointment_date);
CREATE INDEX idx_res_deleted ON reservations(deleted);
```

## 🚀 Business Logic Implementation

### ReservationService Methods

1. **createReservation()**
   - ✅ Conflict check
   - ✅ Price calculation
   - ✅ Reservation number generation
   - ✅ Status = PENDING

2. **updateReservation()**
   - ✅ Status validation
   - ✅ Conflict check (if date changed)
   - ✅ Price recalculation

3. **cancelReservation()**
   - ✅ Status validation (canBeCancelled)
   - ✅ Trigger refund process (TODO)
   - ✅ Send cancellation notification (TODO)

4. **confirmReservation()**
   - ✅ Status validation (only PENDING)
   - ✅ Send confirmation (TODO)

## 📝 Best Practices Applied

✅ **Enum for Status** - Type safety
✅ **Unique Reservation Number** - Human-readable format
✅ **Conflict Detection** - Appointment time validation
✅ **Price Calculation** - Automatic with snapshots
✅ **Lazy Loading** - Microservice compatible
✅ **JPA Auditing** - Complete audit trail
✅ **Soft Delete** - Data preservation
✅ **Database Indexes** - Query performance
✅ **Business Logic Methods** - Lifecycle management
✅ **Custom Exceptions** - Proper error handling
✅ **Transaction Management** - Data consistency

## 🔄 Next Steps

1. **Email/SMS Integration**: Send notifications on status changes
2. **Refund Service**: Implement refund process
3. **Scheduler**: Auto-complete past appointments
4. **Reminders**: Send appointment reminders
5. **Reporting**: Reservation statistics and analytics

