package com.example.HealthTourism.repository.specification;

import com.example.HealthTourism.entity.CarRental;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Specification utility class for dynamic CarRental queries.
 * This allows building complex search criteria at runtime without creating
 * dozens of repository methods.
 * 
 * Usage example:
 * <pre>
 * Specification<CarRental> spec = CarRentalSpecification.builder()
 *     .withCarType("SUV")
 *     .withMinPassengerCapacity(5)
 *     .withPriceRange(new BigDecimal("50"), new BigDecimal("200"))
 *     .withIncludesDriver(true)
 *     .withHasAirConditioning(true)
 *     .withPickupLocation("Istanbul Airport")
 *     .build();
 * 
 * Page<CarRental> results = carRentalRepository.findAll(spec, pageable);
 * </pre>
 */
public class CarRentalSpecification {

    /**
     * Builder pattern for constructing specifications.
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a specification that filters only available cars.
     */
    public static Specification<CarRental> isAvailable() {
        return (root, query, cb) -> cb.equal(root.get("isAvailable"), true);
    }

    /**
     * Creates a specification that filters by car type.
     */
    public static Specification<CarRental> hasCarType(String carType) {
        return (root, query, cb) -> 
            carType == null || carType.isEmpty() 
                ? cb.conjunction() 
                : cb.equal(cb.lower(root.get("carType")), carType.toLowerCase());
    }

    /**
     * Creates a specification that filters by minimum passenger capacity.
     * Critical for health tourism: patients often travel with family.
     */
    public static Specification<CarRental> hasMinPassengerCapacity(Integer minCapacity) {
        return (root, query, cb) -> 
            minCapacity == null 
                ? cb.conjunction() 
                : cb.greaterThanOrEqualTo(root.get("passengerCapacity"), minCapacity);
    }

    /**
     * Creates a specification that filters by minimum luggage capacity.
     */
    public static Specification<CarRental> hasMinLuggageCapacity(Integer minLuggage) {
        return (root, query, cb) -> 
            minLuggage == null 
                ? cb.conjunction() 
                : cb.greaterThanOrEqualTo(root.get("luggageCapacity"), minLuggage);
    }

    /**
     * Creates a specification that filters by price range.
     */
    public static Specification<CarRental> hasPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("pricePerDay"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("pricePerDay"), maxPrice));
            }
            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Creates a specification that filters by maximum price.
     */
    public static Specification<CarRental> hasMaxPrice(BigDecimal maxPrice) {
        return (root, query, cb) -> 
            maxPrice == null 
                ? cb.conjunction() 
                : cb.lessThanOrEqualTo(root.get("pricePerDay"), maxPrice);
    }

    /**
     * Creates a specification that filters by minimum rating.
     */
    public static Specification<CarRental> hasMinRating(Double minRating) {
        return (root, query, cb) -> 
            minRating == null 
                ? cb.conjunction() 
                : cb.greaterThanOrEqualTo(root.get("rating"), minRating);
    }

    /**
     * Creates a specification that filters by transmission type.
     */
    public static Specification<CarRental> hasTransmission(String transmission) {
        return (root, query, cb) -> 
            transmission == null || transmission.isEmpty() 
                ? cb.conjunction() 
                : cb.equal(cb.lower(root.get("transmission")), transmission.toLowerCase());
    }

    /**
     * Creates a specification that filters by air conditioning availability.
     */
    public static Specification<CarRental> hasAirConditioning(Boolean hasAirConditioning) {
        return (root, query, cb) -> 
            hasAirConditioning == null 
                ? cb.conjunction() 
                : cb.equal(root.get("hasAirConditioning"), hasAirConditioning);
    }

    /**
     * Creates a specification that filters by GPS availability.
     */
    public static Specification<CarRental> hasGPS(Boolean hasGPS) {
        return (root, query, cb) -> 
            hasGPS == null 
                ? cb.conjunction() 
                : cb.equal(root.get("hasGPS"), hasGPS);
    }

    /**
     * Creates a specification that filters by driver inclusion.
     */
    public static Specification<CarRental> includesDriver(Boolean includesDriver) {
        return (root, query, cb) -> 
            includesDriver == null 
                ? cb.conjunction() 
                : cb.equal(root.get("includesDriver"), includesDriver);
    }

    /**
     * Creates a specification that filters by insurance inclusion.
     */
    public static Specification<CarRental> includesInsurance(Boolean includesInsurance) {
        return (root, query, cb) -> 
            includesInsurance == null 
                ? cb.conjunction() 
                : cb.equal(root.get("includesInsurance"), includesInsurance);
    }

    /**
     * Creates a specification that filters by pickup location.
     */
    public static Specification<CarRental> hasPickupLocation(String pickupLocation) {
        return (root, query, cb) -> {
            if (pickupLocation == null || pickupLocation.isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("pickupLocation")), "%" + pickupLocation.toLowerCase() + "%");
        };
    }

    /**
     * Creates a specification that filters by dropoff location.
     */
    public static Specification<CarRental> hasDropoffLocation(String dropoffLocation) {
        return (root, query, cb) -> {
            if (dropoffLocation == null || dropoffLocation.isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("dropoffLocation")), "%" + dropoffLocation.toLowerCase() + "%");
        };
    }

    /**
     * Creates a specification that searches in car model or description.
     */
    public static Specification<CarRental> searchInModelOrDescription(String searchTerm) {
        return (root, query, cb) -> {
            if (searchTerm == null || searchTerm.isEmpty()) {
                return cb.conjunction();
            }
            String pattern = "%" + searchTerm.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("carModel")), pattern),
                cb.like(cb.lower(root.get("description")), pattern),
                cb.like(cb.lower(root.get("companyName")), pattern)
            );
        };
    }

    /**
     * Fluent builder for combining multiple specifications.
     */
    public static class Builder {
        private List<Specification<CarRental>> specifications = new ArrayList<>();

        public Builder() {
            // Always include available filter by default
            specifications.add(isAvailable());
        }

        public Builder withCarType(String carType) {
            specifications.add(hasCarType(carType));
            return this;
        }

        public Builder withMinPassengerCapacity(Integer minCapacity) {
            specifications.add(hasMinPassengerCapacity(minCapacity));
            return this;
        }

        public Builder withMinLuggageCapacity(Integer minLuggage) {
            specifications.add(hasMinLuggageCapacity(minLuggage));
            return this;
        }

        public Builder withPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
            specifications.add(hasPriceRange(minPrice, maxPrice));
            return this;
        }

        public Builder withMaxPrice(BigDecimal maxPrice) {
            specifications.add(hasMaxPrice(maxPrice));
            return this;
        }

        public Builder withMinRating(Double minRating) {
            specifications.add(hasMinRating(minRating));
            return this;
        }

        public Builder withTransmission(String transmission) {
            specifications.add(hasTransmission(transmission));
            return this;
        }

        public Builder withHasAirConditioning(Boolean hasAirConditioning) {
            specifications.add(hasAirConditioning(hasAirConditioning));
            return this;
        }

        public Builder withHasGPS(Boolean hasGPS) {
            specifications.add(hasGPS(hasGPS));
            return this;
        }

        public Builder withIncludesDriver(Boolean includesDriver) {
            specifications.add(includesDriver(includesDriver));
            return this;
        }

        public Builder withIncludesInsurance(Boolean includesInsurance) {
            specifications.add(includesInsurance(includesInsurance));
            return this;
        }

        public Builder withPickupLocation(String pickupLocation) {
            specifications.add(hasPickupLocation(pickupLocation));
            return this;
        }

        public Builder withDropoffLocation(String dropoffLocation) {
            specifications.add(hasDropoffLocation(dropoffLocation));
            return this;
        }

        public Builder withSearchTerm(String searchTerm) {
            specifications.add(searchInModelOrDescription(searchTerm));
            return this;
        }

        /**
         * Optionally include unavailable cars (by default only available cars are included).
         */
        public Builder includeUnavailable() {
            // Remove the default isAvailable filter
            specifications.removeIf(spec -> spec == isAvailable());
            return this;
        }

        /**
         * Builds the combined specification.
         */
        public Specification<CarRental> build() {
            return specifications.stream()
                .reduce(Specification.where(null), Specification::and);
        }
    }
}

