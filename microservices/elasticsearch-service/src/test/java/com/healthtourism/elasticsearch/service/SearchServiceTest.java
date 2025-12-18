package com.healthtourism.elasticsearch.service;

import com.healthtourism.elasticsearch.document.SearchableDocument;
import com.healthtourism.elasticsearch.repository.SearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private SearchRepository searchRepository;

    @InjectMocks
    private SearchService searchService;

    private SearchableDocument document;

    @BeforeEach
    void setUp() {
        document = new SearchableDocument();
        document.setId("1");
        document.setTitle("Test Hospital");
        document.setContent("Test content");
        document.setType("HOSPITAL");
        document.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testSearch() {
        // Given
        when(searchRepository.findByTitleContainingOrContentContaining("test", "test"))
            .thenReturn(Arrays.asList(document));

        // When
        List<SearchableDocument> result = searchService.search("test");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(searchRepository, times(1)).findByTitleContainingOrContentContaining("test", "test");
    }

    @Test
    void testSearchByType() {
        // Given
        when(searchRepository.findByType("HOSPITAL"))
            .thenReturn(Arrays.asList(document));

        // When
        List<SearchableDocument> result = searchService.searchByType("HOSPITAL");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(searchRepository, times(1)).findByType("HOSPITAL");
    }

    @Test
    void testIndexDocument() {
        // Given
        when(searchRepository.save(any(SearchableDocument.class))).thenReturn(document);

        // When
        searchService.indexDocument(document);

        // Then
        verify(searchRepository, times(1)).save(document);
    }

    @Test
    void testDeleteDocument() {
        // Given
        doNothing().when(searchRepository).deleteById("1");

        // When
        searchService.deleteDocument("1");

        // Then
        verify(searchRepository, times(1)).deleteById("1");
    }
}
