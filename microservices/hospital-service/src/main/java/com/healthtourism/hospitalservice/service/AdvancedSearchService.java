package com.healthtourism.hospitalservice.service;

import com.healthtourism.hospitalservice.entity.Hospital;
import com.healthtourism.hospitalservice.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Advanced Search Service using JPA Specification
 * Supports complex multi-criteria searches
 */
@Service
public class AdvancedSearchService {
    
    @Autowired
    private HospitalRepository hospitalRepository;
    
    public List<Hospital> advancedSearch(
            String city,
            Integer minRating,
            Double maxDistanceFromAirport,
            String specialization,
            String treatmentType,
            Boolean hasVisaSupport,
            Boolean hasTranslationService) {
        
        Specification<Hospital> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (city != null && !city.isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("city")), city.toLowerCase()));
            }
            
            if (minRating != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), minRating));
            }
            
            if (maxDistanceFromAirport != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("distanceFromAirport"), maxDistanceFromAirport));
            }
            
            if (specialization != null && !specialization.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("specializations")), "%" + specialization.toLowerCase() + "%"));
            }
            
            if (treatmentType != null && !treatmentType.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("treatmentTypes")), "%" + treatmentType.toLowerCase() + "%"));
            }
            
            if (hasVisaSupport != null) {
                predicates.add(cb.equal(root.get("hasVisaSupport"), hasVisaSupport));
            }
            
            if (hasTranslationService != null) {
                predicates.add(cb.equal(root.get("hasTranslationService"), hasTranslationService));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return hospitalRepository.findAll(spec);
    }
    
    public List<Hospital> searchByMultipleCriteria(Map<String, Object> criteria) {
        Specification<Hospital> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            criteria.forEach((key, value) -> {
                if (value != null) {
                    switch (key) {
                        case "city":
                            predicates.add(cb.equal(cb.lower(root.get("city")), value.toString().toLowerCase()));
                            break;
                        case "minRating":
                            predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), (Integer) value));
                            break;
                        case "maxDistanceFromAirport":
                            predicates.add(cb.lessThanOrEqualTo(root.get("distanceFromAirport"), (Double) value));
                            break;
                        case "specialization":
                            predicates.add(cb.like(cb.lower(root.get("specializations")), "%" + value.toString().toLowerCase() + "%"));
                            break;
                        case "hasVisaSupport":
                            predicates.add(cb.equal(root.get("hasVisaSupport"), value));
                            break;
                        case "hasTranslationService":
                            predicates.add(cb.equal(root.get("hasTranslationService"), value));
                            break;
                    }
                }
            });
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return hospitalRepository.findAll(spec);
    }
}
