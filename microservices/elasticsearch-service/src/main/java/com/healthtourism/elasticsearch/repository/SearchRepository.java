package com.healthtourism.elasticsearch.repository;

import com.healthtourism.elasticsearch.document.SearchableDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRepository extends ElasticsearchRepository<SearchableDocument, String> {
    List<SearchableDocument> findByTitleContainingOrContentContaining(String title, String content);
    List<SearchableDocument> findByType(String type);
}
