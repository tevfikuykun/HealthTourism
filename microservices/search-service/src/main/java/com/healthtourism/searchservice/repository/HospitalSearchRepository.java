package com.healthtourism.searchservice.repository;

import com.healthtourism.searchservice.document.HospitalDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalSearchRepository extends ElasticsearchRepository<HospitalDocument, Long> {
    List<HospitalDocument> findByNameContaining(String name);
    List<HospitalDocument> findByCity(String city);
    List<HospitalDocument> findByRatingGreaterThanEqual(Double rating);
}


