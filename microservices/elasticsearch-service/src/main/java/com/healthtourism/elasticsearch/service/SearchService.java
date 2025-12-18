package com.healthtourism.elasticsearch.service;

import com.healthtourism.elasticsearch.document.SearchableDocument;
import com.healthtourism.elasticsearch.repository.SearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {
    
    @Autowired
    private SearchRepository searchRepository;
    
    @Autowired
    private ElasticsearchOperations elasticsearchOperations;
    
    public List<SearchableDocument> search(String query) {
        return searchRepository.findByTitleContainingOrContentContaining(query, query);
    }
    
    public List<SearchableDocument> searchByType(String type) {
        return searchRepository.findByType(type);
    }
    
    public void indexDocument(SearchableDocument document) {
        searchRepository.save(document);
    }
    
    public void deleteDocument(String id) {
        searchRepository.deleteById(id);
    }
}
