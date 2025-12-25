package com.example.HealthTourism.repository.specification;

import com.example.HealthTourism.entity.Doctor;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Specification utility class for dynamic Doctor queries.
 * This allows building complex search criteria at runtime without creating
 * dozens of repository methods.
 * 
 * Usage example:
 * <pre>
 * Specification<Doctor> spec = DoctorSpecification.builder()
 *     .withSpecialization("Cerrahi")
 *     .withLanguage("English")
 *     .withMinExperience(10)
 *     .withMinRating(4.5)
 *     .withHospitalId(1L)
 *     .withMaxConsultationFee(new BigDecimal("500"))
 *     .build();
 * 
 * Page<Doctor> results = doctorRepository.findAll(spec, pageable);
 * </pre>
 */
public class DoctorSpecification {

    /**
     * Builder pattern for constructing specifications.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a specification that filters only available doctors.
     */
    public static Specification<Doctor> isAvailable() {
        return (root, query, cb) -> cb.equal(root.get("isAvailable"), true);
    }

    /**
     * Creates a specification that filters by specialization (flexible search using LIKE).
     */
    public static Specification<Doctor> hasSpecialization(String specialization) {
        return (root, query, cb) -> {
            if (specialization == null || specialization.isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("specialization")), 
                          "%" + specialization.toLowerCase() + "%");
        };
    }

    /**
     * Creates a specification that filters by hospital ID.
     */
    public static Specification<Doctor> hasHospitalId(Long hospitalId) {
        return (root, query, cb) -> 
            hospitalId == null 
                ? cb.conjunction() 
                : cb.equal(root.get("hospital").get("id"), hospitalId);
    }

    /**
     * Creates a specification that filters by language (flexible search using LIKE).
     * Critical for health tourism: patients must find doctors who speak their language.
     */
    public static Specification<Doctor> speaksLanguage(String language) {
        return (root, query, cb) -> {
            if (language == null || language.isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("languages")), 
                          "%" + language.toLowerCase() + "%");
        };
    }

    /**
     * Creates a specification that filters by minimum experience years.
     */
    public static Specification<Doctor> hasMinExperience(Integer minExperience) {
        return (root, query, cb) -> 
            minExperience == null 
                ? cb.conjunction() 
                : cb.greaterThanOrEqualTo(root.get("experienceYears"), minExperience);
    }

    /**
     * Creates a specification that filters by maximum experience years.
     */
    public static Specification<Doctor> hasMaxExperience(Integer maxExperience) {
        return (root, query, cb) -> 
            maxExperience == null 
                ? cb.conjunction() 
                : cb.lessThanOrEqualTo(root.get("experienceYears"), maxExperience);
    }

    /**
     * Creates a specification that filters by experience range.
     */
    public static Specification<Doctor> hasExperienceRange(Integer minExp, Integer maxExp) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (minExp != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("experienceYears"), minExp));
            }
            if (maxExp != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("experienceYears"), maxExp));
            }
            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Creates a specification that filters by minimum rating.
     */
    public static Specification<Doctor> hasMinRating(Double minRating) {
        return (root, query, cb) -> 
            minRating == null 
                ? cb.conjunction() 
                : cb.greaterThanOrEqualTo(root.get("rating"), minRating);
    }

    /**
     * Creates a specification that filters by minimum total reviews.
     */
    public static Specification<Doctor> hasMinTotalReviews(Integer minReviews) {
        return (root, query, cb) -> 
            minReviews == null 
                ? cb.conjunction() 
                : cb.greaterThanOrEqualTo(root.get("totalReviews"), minReviews);
    }

    /**
     * Creates a specification that filters by maximum consultation fee.
     */
    public static Specification<Doctor> hasMaxConsultationFee(BigDecimal maxFee) {
        return (root, query, cb) -> 
            maxFee == null 
                ? cb.conjunction() 
                : cb.lessThanOrEqualTo(root.get("consultationFee"), maxFee);
    }

    /**
     * Creates a specification that filters by consultation fee range.
     */
    public static Specification<Doctor> hasConsultationFeeRange(BigDecimal minFee, BigDecimal maxFee) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (minFee != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("consultationFee"), minFee));
            }
            if (maxFee != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("consultationFee"), maxFee));
            }
            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Creates a specification that filters by title (e.g., "Prof. Dr.", "Doç. Dr.").
     */
    public static Specification<Doctor> hasTitle(String title) {
        return (root, query, cb) -> 
            title == null || title.isEmpty() 
                ? cb.conjunction() 
                : cb.equal(cb.lower(root.get("title")), title.toLowerCase());
    }

    /**
     * Creates a specification that searches in name, bio, or specialization.
     */
    public static Specification<Doctor> searchInNameOrBio(String searchTerm) {
        return (root, query, cb) -> {
            if (searchTerm == null || searchTerm.isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + searchTerm.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("firstName")), pattern),
                cb.like(cb.lower(root.get("lastName")), pattern),
                cb.like(cb.lower(root.get("bio")), pattern),
                cb.like(cb.lower(root.get("specialization")), pattern)
            );
        };
    }

    /**
     * Fluent builder for combining multiple specifications.
     */
    public static class Builder {
        private List<Specification<Doctor>> specifications = new ArrayList<>();

        public Builder() {
            // Always include available filter by default
            specifications.add(isAvailable());
        }

        public Builder withSpecialization(String specialization) {
            specifications.add(hasSpecialization(specialization));
            return this;
        }

        public Builder withHospitalId(Long hospitalId) {
            specifications.add(hasHospitalId(hospitalId));
            return this;
        }

        public Builder withLanguage(String language) {
            specifications.add(speaksLanguage(language));
            return this;
        }

        public Builder withMinExperience(Integer minExperience) {
            specifications.add(hasMinExperience(minExperience));
            return this;
        }

        public Builder withMaxExperience(Integer maxExperience) {
            specifications.add(hasMaxExperience(maxExperience));
            return this;
        }

        public Builder withExperienceRange(Integer minExp, Integer maxExp) {
            specifications.add(hasExperienceRange(minExp, maxExp));
            return this;
        }

        public Builder withMinRating(Double minRating) {
            specifications.add(hasMinRating(minRating));
            return this;
        }

        public Builder withMinTotalReviews(Integer minReviews) {
            specifications.add(hasMinTotalReviews(minReviews));
            return this;
        }

        public Builder withMaxConsultationFee(BigDecimal maxFee) {
            specifications.add(hasMaxConsultationFee(maxFee));
            return this;
        }

        public Builder withConsultationFeeRange(BigDecimal minFee, BigDecimal maxFee) {
            specifications.add(hasConsultationFeeRange(minFee, maxFee));
            return this;
        }

        public Builder withTitle(String title) {
            specifications.add(hasTitle(title));
            return this;
        }

        public Builder withSearchTerm(String searchTerm) {
            specifications.add(searchInNameOrBio(searchTerm));
            return this;
        }

        /**
         * Optionally include unavailable doctors (by default only available doctors are included).
         */
        public Builder includeUnavailable() {
            // Remove the default isAvailable filter
            specifications.removeIf(spec -> spec == isAvailable());
            return this;
        }

        /**
         * Builds the combined specification.
         */
        public Specification<Doctor> build() {
            return specifications.stream()
                .reduce(Specification.where(null), Specification::and);
        }
    }
}

