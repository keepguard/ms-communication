package com.keepguard.ms_communication.infrastructure.persistence;

import com.keepguard.ms_communication.application.dto.common.PageResultView;
import com.keepguard.ms_communication.application.dto.provider.ProviderSearchCriteriaView;
import com.keepguard.ms_communication.domain.entity.Provider;
import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.domain.enums.ProviderTypeEnum;
import com.keepguard.ms_communication.infrastructure.persistence.entity.ProviderJpaEntity;
import com.keepguard.ms_communication.infrastructure.persistence.mapper.ProviderJpaMapper;
import com.keepguard.ms_communication.infrastructure.persistence.spring.ProviderSpringRepository;
import com.keepguard.ms_communication.test.builder.ProviderTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ProviderRepositoryAdapter
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Provider Repository Adapter Tests")
class ProviderRepositoryAdapterTest {

    @InjectMocks
    private ProviderRepositoryAdapter providerRepositoryAdapter;
    
    @Mock
    private ProviderSpringRepository springRepository;
    
    @Mock
    private ProviderJpaMapper mapper;
    
    private Provider provider;
    private ProviderJpaEntity providerJpaEntity;
    private UUID providerId;
    
    @BeforeEach
    void setUp() {
        providerId = UUID.randomUUID();
        
        provider = ProviderTestBuilder.aProvider()
            .withId(providerId)
            .withName("Test Provider")
            .withProviderType(ProviderTypeEnum.N8N)
            .withCommunicationType(CommunicationTypeEnum.EMAIL)
            .withIsActive(true)
            .buildDomain();
        
        providerJpaEntity = ProviderJpaEntity.builder()
            .id(providerId)
            .name("Test Provider")
            .providerType(ProviderTypeEnum.N8N)
            .communicationType(CommunicationTypeEnum.EMAIL)
            .isActive(true)
            .build();
    }
    
    @Test
    @DisplayName("Deve salvar provider com sucesso")
    void shouldSaveProviderSuccessfully() {
        // Given
        when(mapper.toEntity(provider)).thenReturn(providerJpaEntity);
        when(springRepository.save(providerJpaEntity)).thenReturn(providerJpaEntity);
        when(mapper.toDomain(providerJpaEntity)).thenReturn(provider);
        
        // When
        Provider result = providerRepositoryAdapter.save(provider);
        
        // Then
        assertNotNull(result);
        assertEquals(provider, result);
        
        verify(mapper).toEntity(provider);
        verify(springRepository).save(providerJpaEntity);
        verify(mapper).toDomain(providerJpaEntity);
    }
    
    @Test
    @DisplayName("Deve encontrar provider por ID com sucesso")
    void shouldFindProviderByIdSuccessfully() {
        // Given
        when(springRepository.findById(providerId)).thenReturn(Optional.of(providerJpaEntity));
        when(mapper.toDomain(providerJpaEntity)).thenReturn(provider);
        
        // When
        Optional<Provider> result = providerRepositoryAdapter.findById(providerId);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(provider, result.get());
        
        verify(springRepository).findById(providerId);
        verify(mapper).toDomain(providerJpaEntity);
    }
    
    @Test
    @DisplayName("Deve retornar Optional vazio quando provider não encontrado por ID")
    void shouldReturnEmptyOptionalWhenProviderNotFoundById() {
        // Given
        when(springRepository.findById(providerId)).thenReturn(Optional.empty());
        
        // When
        Optional<Provider> result = providerRepositoryAdapter.findById(providerId);
        
        // Then
        assertFalse(result.isPresent());
        
        verify(springRepository).findById(providerId);
        verify(mapper, never()).toDomain(any());
    }
    
    @Test
    @DisplayName("Deve buscar providers com critérios de busca com sucesso")
    void shouldSearchProvidersWithCriteriaSuccessfully() {
        // Given
        ProviderSearchCriteriaView criteria = new ProviderSearchCriteriaView(
            0, 10, "name", "ASC", "Test", ProviderTypeEnum.N8N, 
            CommunicationTypeEnum.EMAIL, true, false
        );
        
        Page<ProviderJpaEntity> page = new PageImpl<>(List.of(providerJpaEntity));
        when(springRepository.findWithFilters(
            eq("Test"), eq("N8N"), eq("EMAIL"), eq(true), eq(false), any(Pageable.class)
        )).thenReturn(page);
        when(mapper.toDomain(providerJpaEntity)).thenReturn(provider);
        
        // When
        PageResultView<Provider> result = providerRepositoryAdapter.search(criteria);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(provider, result.content().get(0));
        assertEquals(0, result.page());
        assertEquals(1, result.size());
        assertEquals(1, result.totalElements());
        
        verify(springRepository).findWithFilters(
            eq("Test"), eq("N8N"), eq("EMAIL"), eq(true), eq(false), any(Pageable.class)
        );
        verify(mapper).toDomain(providerJpaEntity);
    }
    
    @Test
    @DisplayName("Deve buscar providers com critérios nulos")
    void shouldSearchProvidersWithNullCriteria() {
        // Given
        ProviderSearchCriteriaView criteria = new ProviderSearchCriteriaView(
            0, 10, "name", "ASC", null, null, null, null, null
        );
        
        Page<ProviderJpaEntity> page = new PageImpl<>(List.of(providerJpaEntity));
        when(springRepository.findWithFilters(
            isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class)
        )).thenReturn(page);
        when(mapper.toDomain(providerJpaEntity)).thenReturn(provider);
        
        // When
        PageResultView<Provider> result = providerRepositoryAdapter.search(criteria);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.content().size());
        
        verify(springRepository).findWithFilters(
            isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class)
        );
    }
    
    @Test
    @DisplayName("Deve encontrar todos os providers ativos")
    void shouldFindAllActiveProviders() {
        // Given
        when(springRepository.findAllActiveProviders()).thenReturn(List.of(providerJpaEntity));
        when(mapper.toDomain(providerJpaEntity)).thenReturn(provider);
        
        // When
        List<Provider> result = providerRepositoryAdapter.findAllActive();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(provider, result.get(0));
        
        verify(springRepository).findAllActiveProviders();
        verify(mapper).toDomain(providerJpaEntity);
    }
    
    @Test
    @DisplayName("Deve encontrar providers por tipo de comunicação")
    void shouldFindProvidersByCommunicationType() {
        // Given
        CommunicationTypeEnum communicationType = CommunicationTypeEnum.EMAIL;
        when(springRepository.findActiveProvidersByCommunicationType(communicationType))
            .thenReturn(List.of(providerJpaEntity));
        when(mapper.toDomain(providerJpaEntity)).thenReturn(provider);
        
        // When
        List<Provider> result = providerRepositoryAdapter.findByCommunicationType(communicationType);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(provider, result.get(0));
        
        verify(springRepository).findActiveProvidersByCommunicationType(communicationType);
        verify(mapper).toDomain(providerJpaEntity);
    }
    
    @Test
    @DisplayName("Deve encontrar provider padrão por tipo de comunicação com sucesso")
    void shouldFindDefaultProviderByCommunicationTypeSuccessfully() {
        // Given
        CommunicationTypeEnum communicationType = CommunicationTypeEnum.EMAIL;
        when(springRepository.findDefaultProviderByCommunicationType(communicationType))
            .thenReturn(Optional.of(providerJpaEntity));
        when(mapper.toDomain(providerJpaEntity)).thenReturn(provider);
        
        // When
        Optional<Provider> result = providerRepositoryAdapter.findDefaultByCommunicationType(communicationType);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(provider, result.get());
        
        verify(springRepository).findDefaultProviderByCommunicationType(communicationType);
        verify(mapper).toDomain(providerJpaEntity);
    }
    
    @Test
    @DisplayName("Deve retornar Optional vazio quando provider padrão não encontrado")
    void shouldReturnEmptyOptionalWhenDefaultProviderNotFound() {
        // Given
        CommunicationTypeEnum communicationType = CommunicationTypeEnum.EMAIL;
        when(springRepository.findDefaultProviderByCommunicationType(communicationType))
            .thenReturn(Optional.empty());
        
        // When
        Optional<Provider> result = providerRepositoryAdapter.findDefaultByCommunicationType(communicationType);
        
        // Then
        assertFalse(result.isPresent());
        
        verify(springRepository).findDefaultProviderByCommunicationType(communicationType);
        verify(mapper, never()).toDomain(any());
    }
    
    @Test
    @DisplayName("Deve deletar provider por ID")
    void shouldDeleteProviderById() {
        // Given
        doNothing().when(springRepository).deleteById(providerId);
        
        // When
        providerRepositoryAdapter.deleteById(providerId);
        
        // Then
        verify(springRepository).deleteById(providerId);
    }
    
    @Test
    @DisplayName("Deve verificar se existe provider por nome")
    void shouldCheckIfProviderExistsByName() {
        // Given
        String name = "Test Provider";
        when(springRepository.existsByName(name)).thenReturn(true);
        
        // When
        boolean result = providerRepositoryAdapter.existsByName(name);
        
        // Then
        assertTrue(result);
        verify(springRepository).existsByName(name);
    }
    
    @Test
    @DisplayName("Deve verificar se existe provider por nome e ID diferente")
    void shouldCheckIfProviderExistsByNameAndIdNot() {
        // Given
        String name = "Test Provider";
        UUID differentId = UUID.randomUUID();
        when(springRepository.existsByNameAndIdNot(name, differentId)).thenReturn(true);
        
        // When
        boolean result = providerRepositoryAdapter.existsByNameAndIdNot(name, differentId);
        
        // Then
        assertTrue(result);
        verify(springRepository).existsByNameAndIdNot(name, differentId);
    }
    
    @Test
    @DisplayName("Deve retornar false quando provider não existe por nome")
    void shouldReturnFalseWhenProviderNotExistsByName() {
        // Given
        String name = "Non Existent Provider";
        when(springRepository.existsByName(name)).thenReturn(false);
        
        // When
        boolean result = providerRepositoryAdapter.existsByName(name);
        
        // Then
        assertFalse(result);
        verify(springRepository).existsByName(name);
    }
    
    @Test
    @DisplayName("Deve retornar false quando provider não existe por nome e ID diferente")
    void shouldReturnFalseWhenProviderNotExistsByNameAndIdNot() {
        // Given
        String name = "Non Existent Provider";
        UUID differentId = UUID.randomUUID();
        when(springRepository.existsByNameAndIdNot(name, differentId)).thenReturn(false);
        
        // When
        boolean result = providerRepositoryAdapter.existsByNameAndIdNot(name, differentId);
        
        // Then
        assertFalse(result);
        verify(springRepository).existsByNameAndIdNot(name, differentId);
    }
    
    @Test
    @DisplayName("Deve buscar providers com página vazia")
    void shouldSearchProvidersWithEmptyPage() {
        // Given
        ProviderSearchCriteriaView criteria = new ProviderSearchCriteriaView(
            0, 10, "name", "ASC", "NonExistent", null, null, null, null
        );
        
        Page<ProviderJpaEntity> emptyPage = new PageImpl<>(List.of());
        when(springRepository.findWithFilters(
            eq("NonExistent"), isNull(), isNull(), isNull(), isNull(), any(Pageable.class)
        )).thenReturn(emptyPage);
        
        // When
        PageResultView<Provider> result = providerRepositoryAdapter.search(criteria);
        
        // Then
        assertNotNull(result);
        assertTrue(result.content().isEmpty());
        assertEquals(0, result.totalElements());
        
        verify(springRepository).findWithFilters(
            eq("NonExistent"), isNull(), isNull(), isNull(), isNull(), any(Pageable.class)
        );
        verify(mapper, never()).toDomain(any());
    }
    
    @Test
    @DisplayName("Deve buscar providers com diferentes tipos de ordenação")
    void shouldSearchProvidersWithDifferentSortOrders() {
        // Given
        ProviderSearchCriteriaView criteriaAsc = new ProviderSearchCriteriaView(
            0, 10, "name", "ASC", null, null, null, null, null
        );
        ProviderSearchCriteriaView criteriaDesc = new ProviderSearchCriteriaView(
            0, 10, "name", "DESC", null, null, null, null, null
        );
        
        Page<ProviderJpaEntity> page = new PageImpl<>(List.of(providerJpaEntity));
        when(springRepository.findWithFilters(
            any(), any(), any(), any(), any(), any(Pageable.class)
        )).thenReturn(page);
        when(mapper.toDomain(providerJpaEntity)).thenReturn(provider);
        
        // When
        PageResultView<Provider> resultAsc = providerRepositoryAdapter.search(criteriaAsc);
        PageResultView<Provider> resultDesc = providerRepositoryAdapter.search(criteriaDesc);
        
        // Then
        assertNotNull(resultAsc);
        assertNotNull(resultDesc);
        assertEquals(1, resultAsc.content().size());
        assertEquals(1, resultDesc.content().size());
        
        verify(springRepository, times(2)).findWithFilters(
            any(), any(), any(), any(), any(), any(Pageable.class)
        );
    }
}
