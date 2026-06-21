package com.keepguard.ms_communication.application.dto;

import com.keepguard.ms_communication.application.dto.common.SearchCriteriaView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para SearchCriteria
 */
class SearchCriteriaTest {

    @Test
    @DisplayName("Deve criar SearchCriteriaDTO com construtor completo")
    void shouldCreateSearchCriteriaWithFullConstructor() {
        // Given
        int page = 0;
        int size = 10;
        String sortBy = "name";
        String sortDirection = "DESC";

        // When
        SearchCriteriaView criteria = SearchCriteriaView.of(page, size, sortBy, sortDirection);

        // Then
        assertNotNull(criteria);
        assertEquals(page, criteria.page());
        assertEquals(size, criteria.size());
        assertEquals(sortBy, criteria.sortBy());
        assertEquals(sortDirection, criteria.sortDirection());
    }

    @Test
    @DisplayName("Deve criar SearchCriteriaDTOcom valores nulos")
    void shouldCreateSearchCriteriaDTOithNullValues() {
        // Given
        int page = 1;
        int size = 20;

        // When
        SearchCriteriaView criteria = new SearchCriteriaView(page, size, null, null);

        // Then
        assertNotNull(criteria);
        assertEquals(page, criteria.page());
        assertEquals(size, criteria.size());
        assertNull(criteria.sortBy());
        assertNull(criteria.sortDirection());
    }

    @Test
    @DisplayName("Deve criar SearchCriteriaDTOusando factory method com page e size")
    void shouldCreateSearchCriteriaDTOithPageAndSize() {
        // Given
        int page = 2;
        int size = 15;

        // When
        SearchCriteriaView criteria = SearchCriteriaView.of(page, size);

        // Then
        assertNotNull(criteria);
        assertEquals(page, criteria.page());
        assertEquals(size, criteria.size());
        assertNull(criteria.sortBy());
        assertEquals("ASC", criteria.sortDirection());
    }

    @Test
    @DisplayName("Deve criar SearchCriteriaDTOusando factory method com page, size e sortBy")
    void shouldCreateSearchCriteriaDTOithPageSizeAndSortBy() {
        // Given
        int page = 1;
        int size = 25;
        String sortBy = "createdAt";

        // When
        SearchCriteriaView criteria = SearchCriteriaView.of(page, size, sortBy);

        // Then
        assertNotNull(criteria);
        assertEquals(page, criteria.page());
        assertEquals(size, criteria.size());
        assertEquals(sortBy, criteria.sortBy());
        assertEquals("ASC", criteria.sortDirection());
    }

    @Test
    @DisplayName("Deve criar SearchCriteriaDTOusando factory method completo")
    void shouldCreateSearchCriteriaDTOithAllParameters() {
        // Given
        int page = 3;
        int size = 5;
        String sortBy = "updatedAt";
        String sortDirection = "DESC";

        // When
        SearchCriteriaView criteria = SearchCriteriaView.of(page, size, sortBy, sortDirection);

        // Then
        assertNotNull(criteria);
        assertEquals(page, criteria.page());
        assertEquals(size, criteria.size());
        assertEquals(sortBy, criteria.sortBy());
        assertEquals(sortDirection, criteria.sortDirection());
    }

    @Test
    @DisplayName("Deve criar SearchCriteriaDTOcom valores zero")
    void shouldCreateSearchCriteriaDTOithZeroValues() {
        // Given
        int page = 0;
        int size = 0;

        // When
        SearchCriteriaView criteria = new SearchCriteriaView(page, size, null, null);

        // Then
        assertNotNull(criteria);
        assertEquals(0, criteria.page());
        assertEquals(0, criteria.size());
        assertNull(criteria.sortBy());
        assertNull(criteria.sortDirection());
    }

    @Test
    @DisplayName("Deve criar SearchCriteriaDTOcom valores negativos")
    void shouldCreateSearchCriteriaDTOithNegativeValues() {
        // Given
        int page = -1;
        int size = -5;

        // When
        SearchCriteriaView criteria = new SearchCriteriaView(page, size, null, null);

        // Then
        assertNotNull(criteria);
        assertEquals(-1, criteria.page());
        assertEquals(-5, criteria.size());
        assertNull(criteria.sortBy());
        assertNull(criteria.sortDirection());
    }

    @Test
    @DisplayName("Deve criar SearchCriteriaDTOcom strings vazias")
    void shouldCreateSearchCriteriaDTOithEmptyStrings() {
        // Given
        int page = 1;
        int size = 10;
        String sortBy = "";
        String sortDirection = "";

        // When
        SearchCriteriaView criteria = SearchCriteriaView.of(page, size, sortBy, sortDirection);

        // Then
        assertNotNull(criteria);
        assertEquals(page, criteria.page());
        assertEquals(size, criteria.size());
        assertEquals("", criteria.sortBy());
        assertEquals("", criteria.sortDirection());
    }

    @Test
    @DisplayName("Deve criar SearchCriteriaDTOcom sortDirection case insensitive")
    void shouldCreateSearchCriteriaDTOithCaseInsensitiveSortDirection() {
        // Given
        int page = 0;
        int size = 10;
        String sortBy = "name";
        String sortDirection = "desc";

        // When
        SearchCriteriaView criteria = SearchCriteriaView.of(page, size, sortBy, sortDirection);

        // Then
        assertNotNull(criteria);
        assertEquals(page, criteria.page());
        assertEquals(size, criteria.size());
        assertEquals(sortBy, criteria.sortBy());
        assertEquals("desc", criteria.sortDirection());
    }

    @Test
    @DisplayName("Deve criar múltiplas instâncias independentes")
    void shouldCreateMultipleIndependentInstances() {
        // Given
        SearchCriteriaView criteria1 = new SearchCriteriaView(0, 10, "name", "ASC");
        SearchCriteriaView criteria2 = new SearchCriteriaView(1, 20, "createdAt", "DESC");

        // When & Then
        assertNotNull(criteria1);
        assertNotNull(criteria2);
        assertNotSame(criteria1, criteria2);
        
        assertEquals(0, criteria1.page());
        assertEquals(10, criteria1.size());
        assertEquals("name", criteria1.sortBy());
        assertEquals("ASC", criteria1.sortDirection());
        
        assertEquals(1, criteria2.page());
        assertEquals(20, criteria2.size());
        assertEquals("createdAt", criteria2.sortBy());
        assertEquals("DESC", criteria2.sortDirection());
    }

    @Test
    @DisplayName("Deve criar SearchCriteriaDTOcom valores máximos")
    void shouldCreateSearchCriteriaDTOithMaxValues() {
        // Given
        int page = Integer.MAX_VALUE;
        int size = Integer.MAX_VALUE;
        String sortBy = "id";
        String sortDirection = "ASC";

        // When
        SearchCriteriaView criteria = SearchCriteriaView.of(page, size, sortBy, sortDirection);

        // Then
        assertNotNull(criteria);
        assertEquals(Integer.MAX_VALUE, criteria.page());
        assertEquals(Integer.MAX_VALUE, criteria.size());
        assertEquals(sortBy, criteria.sortBy());
        assertEquals(sortDirection, criteria.sortDirection());
    }

    @Test
    @DisplayName("Deve criar SearchCriteriaDTOcom valores mínimos")
    void shouldCreateSearchCriteriaDTOithMinValues() {
        // Given
        int page = Integer.MIN_VALUE;
        int size = Integer.MIN_VALUE;
        String sortBy = "name";
        String sortDirection = "DESC";

        // When
        SearchCriteriaView criteria = SearchCriteriaView.of(page, size, sortBy, sortDirection);

        // Then
        assertNotNull(criteria);
        assertEquals(Integer.MIN_VALUE, criteria.page());
        assertEquals(Integer.MIN_VALUE, criteria.size());
        assertEquals(sortBy, criteria.sortBy());
        assertEquals(sortDirection, criteria.sortDirection());
    }

    @Test
    @DisplayName("Deve testar equals e hashCode")
    void shouldTestEqualsAndHashCode() {
        // Given
        SearchCriteriaView criteria1 = SearchCriteriaView.of(0, 10, "name", "ASC");
        SearchCriteriaView criteria2 = SearchCriteriaView.of(0, 10, "name", "ASC");
        SearchCriteriaView criteria3 = SearchCriteriaView.of(1, 10, "name", "ASC");

        // When & Then
        assertEquals(criteria1, criteria2);
        assertNotEquals(criteria1, criteria3);
        assertEquals(criteria1.hashCode(), criteria2.hashCode());
        assertNotEquals(criteria1.hashCode(), criteria3.hashCode());
    }

    @Test
    @DisplayName("Deve testar toString")
    void shouldTestToString() {
        // Given
        SearchCriteriaView criteria = SearchCriteriaView.of(0, 10, "name", "ASC");

        // When
        String result = criteria.toString();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("SearchCriteriaView"));
        assertTrue(result.contains("page=0"));
        assertTrue(result.contains("size=10"));
        assertTrue(result.contains("sortBy=name"));
        assertTrue(result.contains("sortDirection=ASC"));
    }
}
