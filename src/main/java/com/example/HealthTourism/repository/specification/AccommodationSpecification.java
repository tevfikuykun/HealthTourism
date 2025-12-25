package com.example.HealthTourism.repository.specification;

import com.example.HealthTourism.entity.Accommodation;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Specification utility class for dynamic Accommodation queries.
 * This allows building complex search criteria at runtime without creating
 * dozens of repository methods.
 * 
 * Usage example:
 * <pre>
 * Specification<Accommodation> spec = AccommodationSpecification.builder()
 *     .withCity("Istanbul")
 *     .withMaxPrice(new BigDecimal("200"))
 *     .withMinRating(4.0)
 *     .withHasWifi(true)
 *     .withMaxDistanceToHospital(5.0)
 *     .withHospitalId(1L)
 *     .build();
 * 
 * Page<Accommodation> results = accommodationRepository.findAll(spec, pageable);
 * </pre>
 */
public class AccommodationSpecification {

    /**
     * Builder pattern for constructing specifications.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a specification that filters only active accommodations.
     */
    public static Specification<Accommodation> isActive() {
        return (root, query, cb) -> cb.equal(root.get("isActive"), true);
    }

    /**
     * Creates a specification that filters by city.
     */
    public static Specification<Accommodation> hasCity(String city) {
        return (root, query, cb) -> 
            city == null || city.isEmpty() 
                ? cb.conjunction() 
                : cb.equal(cb.lower(root.get("city")), city.toLowerCase());
    }

    /**
     * Creates a specification that filters by hospital ID.
     */
    public static Specification<Accommodation> hasHospitalId(Long hospitalId) {
        return (root, query, cb) -> 
            hospitalId == null 
                ? cb.conjunction() 
                : cb.equal(root.get("hospital").get("id"), hospitalId);
    }

    /**
     * Creates a specification that filters by maximum price per night.
     */
    public static Specification<Accommodation> hasMaxPrice(BigDecimal maxPrice) {
        return (root, query, cb) -> 
            maxPrice == null 
                ? cb.conjunction() 
                : cb.lessThanOrEqualTo(root.get("pricePerNight"), maxPrice);
    }

    /**
     * Creates a specification that filters by price range.
     */
    public static Specification<Accommodation> hasPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("pricePerNight"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("pricePerNight"), maxPrice));
            }
            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Creates a specification that filters by minimum rating.
     */
    public static Specification<Accommodation> hasMinRating(Double minRating) {
        return (root, query, cb) -> 
            minRating == null 
                ? cb.conjunction() 
                : cb.greaterThanOrEqualTo(root.get("rating"), minRating);
    }

    /**
     * Creates a specification that filters by minimum star rating.
     */
    public static Specification<Accommodation> hasMinStarRating(Integer minStarRating) {
        return (root, query, cb) -> 
            minStarRating == null 
                ? cb.conjunction() 
                : cb.greaterThanOrEqualTo(root.get("starRating"), minStarRating);
    }

    /**
     * Creates a specification that filters by maximum distance to hospital (km).
     */
    public static Specification<Accommodation> hasMaxDistanceToHospital(Double maxKm) {
        return (root, query, cb) -> 
            maxKm == null 
                ? cb.conjunction() 
                : cb.lessThanOrEqualTo(root.get("distanceToHospital"), maxKm);
    }

    /**
     * Creates a specification that filters by WiFi availability.
     */
    public static Specification<Accommodation> hasWifi(Boolean hasWifi) {
        return (root, query, cb) -> 
            hasWifi == null 
                ? cb.conjunction() 
                : cb.equal(root.get("hasWifi"), hasWifi);
    }

    /**
     * Creates a specification that filters by parking availability.
     */
    public static Specification<Accommodation> hasParking(Boolean hasParking) {
        return (root, query, cb) -> 
            hasParking == null 
                ? cb.conjunction() 
                : cb.equal(root.get("hasParking"), hasParking);
    }

    /**
     * Creates a specification that filters by breakfast availability.
     */
    public static Specification<Accommodation> hasBreakfast(Boolean hasBreakfast) {
        return (root, query, cb) -> 
            hasBreakfast == null 
                ? cb.conjunction() 
                : cb.equal(root.get("hasBreakfast"), hasBreakfast);
    }

    /**
     * Creates a specification that filters by accommodation type.
     */
    public static Specification<Accommodation> hasType(String type) {
        return (root, query, cb) -> 
            type == null || type.isEmpty() 
                ? cb.conjunction() 
                : cb.equal(cb.lower(root.get("type")), type.toLowerCase());
    }

    /**
     * Creates a specification that searches in name or description.
     */
    public static Specification<Accommodation> searchInNameOrDescription(String searchTerm) {
        return (root, query, cb) -> {
            if (searchTerm == null || searchTerm.isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + searchTerm.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("name")), pattern),
                cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }

    /**
     * Fluent builder for combining multiple specifications.
     */
    public static class Builder {
        private List<Specification<Accommodation>> specifications = new ArrayList<>();

        public Builder() {
            // Always include active filter by default
            specifications.add(isActive());
        }

        public Builder withCity(String city) {
            specifications.add(hasCity(city));
            return this;
        }

        public Builder withHospitalId(Long hospitalId) {
            specifications.add(hasHospitalId(hospitalId));
            return this;
        }

        public Builder withMaxPrice(BigDecimal maxPrice) {
            specifications.add(hasMaxPrice(maxPrice));
            return this;
        }

        public Builder withPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
            specifications.add(hasPriceRange(minPrice, maxPrice));
            return this;
        }

        public Builder withMinRating(Double minRating) {
            specifications.add(hasMinRating(minRating));
            return this;
        }

        public Builder withMinStarRating(Integer minStarRating) {
            specifications.add(hasMinStarRating(minStarRating));
            return this;
        }

        public Builder withMaxDistanceToHospital(Double maxKm) {
            specifications.add(hasMaxDistanceToHospital(maxKm));
            return this;
        }

        public Builder withHasWifi(Boolean hasWifi) {
            specifications.add(hasWifi(hasWifi));
            return this;
        }

        public Builder withHasParking(Boolean hasParking) {
            specifications.add(hasParking(hasParking));
            return this;
        }

        public Builder withHasBreakfast(Boolean hasBreakfast) {
            specifications.add(hasBreakfast(hasBreakfast));
            return this;
        }

        public Builder withType(String type) {
            specifications.add(hasType(type));
            return this;
        }

        public Builder withSearchTerm(String searchTerm) {
            specifications.add(searchInNameOrDescription(searchTerm));
            return this;
        }

        /**
         * Optionally include active filter (by default it's always included).
         */
        public Builder includeInactive() {
            // Remove the default isActive filter
            specifications.removeIf(spec -> spec == isActive());
            return this;
        }

        /**
         * Builds the combined specification.
         */
        public Specification<Accommodation> build() {
            return specifications.stream()
                .reduce(Specification.where(null), Specification::and);
        }
    }
}

