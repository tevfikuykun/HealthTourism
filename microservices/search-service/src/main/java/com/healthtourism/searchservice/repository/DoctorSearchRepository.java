package com.healthtourism.searchservice.repository;

import com.healthtourism.searchservice.document.DoctorDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorSearchRepository extends ElasticsearchRepository<DoctorDocument, Long> {
    List<DoctorDocument> findByFirstNameContainingOrLastNameContaining(String firstName, String lastName);
    List<DoctorDocument> findBySpecialty(String specialty);
    List<DoctorDocument> findByRatingGreaterThanEqual(Double rating);
}


