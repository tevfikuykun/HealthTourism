package com.healthtourism.doctorservice.repository;
import com.healthtourism.doctorservice.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findByHospitalIdAndIsAvailableTrue(Long hospitalId);
    List<Doctor> findBySpecializationAndIsAvailableTrue(String specialization);
    @Query("SELECT d FROM Doctor d WHERE d.hospitalId = :hospitalId AND d.isAvailable = true ORDER BY d.rating DESC")
    List<Doctor> findByHospitalIdOrderByRatingDesc(@Param("hospitalId") Long hospitalId);
    @Query("SELECT d FROM Doctor d WHERE d.isAvailable = true AND d.specialization = :specialization ORDER BY d.rating DESC")
    List<Doctor> findBySpecializationOrderByRatingDesc(@Param("specialization") String specialization);
    Optional<Doctor> findByIdAndIsAvailableTrue(Long id);
}

