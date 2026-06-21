package com.keepguard.ms_communication.application.service;

import com.keepguard.ms_communication.application.dto.common.PageResultView;
import com.keepguard.ms_communication.application.dto.provider.ProviderSearchCriteriaView;
import com.keepguard.ms_communication.application.dto.provider.ProviderView;
import com.keepguard.ms_communication.application.mapper.ProviderApplicationMapper;
import com.keepguard.ms_communication.application.port.out.persistence.ProviderRepositoryPort;
import com.keepguard.ms_communication.application.service.exception.NotFoundException;
import com.keepguard.ms_communication.application.service.provider.ProviderQueryService;
import com.keepguard.ms_communication.domain.entity.Provider;
import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.domain.enums.ProviderTypeEnum;
import com.keepguard.ms_communication.test.builder.ProviderTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ProviderQueryService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Provider Query Service Tests")
class ProviderQueryServiceTest {

    @InjectMocks
    private ProviderQueryService providerQueryService;
    
    @Mock
    private ProviderRepositoryPort repositoryPort;
    
    @Mock
    private ProviderApplicationMapper mapper;
    
    private Provider provider;
    private ProviderView providerView;
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
        
        providerView = new ProviderView(
            providerId,
            "Test Provider",
            ProviderTypeEnum.N8N,
            CommunicationTypeEnum.EMAIL,
            true,
            false,
            1,
            "https://api.example.com",
            "{}",
            3,
            30,
            100,
            1000,
            30000,
            java.time.LocalDateTime.now(),
            java.time.LocalDateTime.now()
        );
    }
    
    @Test
    @DisplayName("Deve buscar provider por ID com sucesso")
    void shouldGetProviderByIdSuccessfully() {
        // Given
        when(repositoryPort.findById(providerId)).thenReturn(Optional.of(provider));
        when(mapper.toView(provider)).thenReturn(providerView);
        
        // When
        ProviderView result = providerQueryService.getById(providerId);
        
        // Then
        assertNotNull(result);
        assertEquals(providerView, result);
        assertEquals(providerId, result.id());
        assertEquals("Test Provider", result.name());
        assertEquals(ProviderTypeEnum.N8N, result.providerType());
        assertEquals(CommunicationTypeEnum.EMAIL, result.communicationType());
        assertTrue(result.isActive());
        
        verify(repositoryPort).findById(providerId);
        verify(mapper).toView(provider);
    }
    
    @Test
    @DisplayName("Deve lançar NotFoundException quando provider não encontrado por ID")
    void shouldThrowNotFoundExceptionWhenProviderNotFoundById() {
        // Given
        when(repositoryPort.findById(providerId)).thenReturn(Optional.empty());
        
        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            providerQueryService.getById(providerId);
        });
        
        assertEquals("Provedor não encontrado: " + providerId, exception.getMessage());
        
        verify(repositoryPort).findById(providerId);
        verify(mapper, never()).toView(any());
    }
    
    @Test
    @DisplayName("Deve buscar providers com critérios de busca com sucesso")
    void shouldSearchProvidersWithCriteriaSuccessfully() {
        // Given
        ProviderSearchCriteriaView criteria = new ProviderSearchCriteriaView(
            0, 10, "name", "ASC", "Test", ProviderTypeEnum.N8N, 
            CommunicationTypeEnum.EMAIL, true, false
        );
        
        PageResultView<Provider> domainResult = PageResultView.of(
            List.of(provider), 0, 10, 1L
        );
        
        when(repositoryPort.search(criteria)).thenReturn(domainResult);
        when(mapper.toView(provider)).thenReturn(providerView);
        
        // When
        PageResultView<ProviderView> result = providerQueryService.search(criteria);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(providerView, result.content().get(0));
        
        verify(repositoryPort).search(criteria);
        verify(mapper).toView(provider);
    }
    
    @Test
    @DisplayName("Deve retornar lista vazia quando busca não retorna resultados")
    void shouldReturnEmptyListWhenSearchReturnsNoResults() {
        // Given
        ProviderSearchCriteriaView criteria = new ProviderSearchCriteriaView(
            0, 10, "name", "ASC", "NonExistent", null, null, null, null
        );
        
        PageResultView<Provider> domainResult = PageResultView.of(
            List.of(), 0, 10, 0L
        );
        
        when(repositoryPort.search(criteria)).thenReturn(domainResult);
        
        // When
        PageResultView<ProviderView> result = providerQueryService.search(criteria);
        
        // Then
        assertNotNull(result);
        assertTrue(result.content().isEmpty());
        
        verify(repositoryPort).search(criteria);
        verify(mapper, never()).toView(any());
    }
    
    @Test
    @DisplayName("Deve listar todos os providers ativos")
    void shouldGetAllActiveProviders() {
        // Given
        List<Provider> providers = List.of(provider);
        when(repositoryPort.findAllActive()).thenReturn(providers);
        when(mapper.toView(provider)).thenReturn(providerView);
        
        // When
        List<ProviderView> result = providerQueryService.getAllActive();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(providerView, result.get(0));
        
        verify(repositoryPort).findAllActive();
        verify(mapper).toView(provider);
    }
    
    @Test
    @DisplayName("Deve listar providers por tipo de comunicação")
    void shouldGetProvidersByCommunicationType() {
        // Given
        CommunicationTypeEnum communicationType = CommunicationTypeEnum.EMAIL;
        List<Provider> providers = List.of(provider);
        
        when(repositoryPort.findByCommunicationType(communicationType)).thenReturn(providers);
        when(mapper.toView(provider)).thenReturn(providerView);
        
        // When
        List<ProviderView> result = providerQueryService.getByCommunicationType(communicationType);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(providerView, result.get(0));
        
        verify(repositoryPort).findByCommunicationType(communicationType);
        verify(mapper).toView(provider);
    }
    
    @Test
    @DisplayName("Deve buscar provider padrão por tipo de comunicação com sucesso")
    void shouldGetDefaultProviderByCommunicationTypeSuccessfully() {
        // Given
        CommunicationTypeEnum communicationType = CommunicationTypeEnum.EMAIL;
        when(repositoryPort.findDefaultByCommunicationType(communicationType))
            .thenReturn(Optional.of(provider));
        when(mapper.toView(provider)).thenReturn(providerView);
        
        // When
        Optional<ProviderView> result = providerQueryService.getDefaultByCommunicationType(communicationType);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(providerView, result.get());
        
        verify(repositoryPort).findDefaultByCommunicationType(communicationType);
        verify(mapper).toView(provider);
    }
    
    @Test
    @DisplayName("Deve retornar Optional vazio quando provider padrão não encontrado")
    void shouldReturnEmptyOptionalWhenDefaultProviderNotFound() {
        // Given
        CommunicationTypeEnum communicationType = CommunicationTypeEnum.SMS;
        when(repositoryPort.findDefaultByCommunicationType(communicationType))
            .thenReturn(Optional.empty());
        
        // When
        Optional<ProviderView> result = providerQueryService.getDefaultByCommunicationType(communicationType);
        
        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(repositoryPort).findDefaultByCommunicationType(communicationType);
        verify(mapper, never()).toView(any());
    }
    
    @Test
    @DisplayName("Deve verificar se provider existe por ID quando existe")
    void shouldReturnTrueWhenProviderExistsById() {
        // Given
        when(repositoryPort.findById(providerId)).thenReturn(Optional.of(provider));
        
        // When
        boolean result = providerQueryService.existsById(providerId);
        
        // Then
        assertTrue(result);
        
        verify(repositoryPort).findById(providerId);
    }
    
    @Test
    @DisplayName("Deve retornar false quando provider não existe por ID")
    void shouldReturnFalseWhenProviderNotExistsById() {
        // Given
        when(repositoryPort.findById(providerId)).thenReturn(Optional.empty());
        
        // When
        boolean result = providerQueryService.existsById(providerId);
        
        // Then
        assertFalse(result);
        
        verify(repositoryPort).findById(providerId);
    }
    
    @Test
    @DisplayName("Deve verificar se provider existe por nome quando existe")
    void shouldReturnTrueWhenProviderExistsByName() {
        // Given
        String name = "Test Provider";
        when(repositoryPort.existsByName(name)).thenReturn(true);
        
        // When
        boolean result = providerQueryService.existsByName(name);
        
        // Then
        assertTrue(result);
        
        verify(repositoryPort).existsByName(name);
    }
    
    @Test
    @DisplayName("Deve retornar false quando provider não existe por nome")
    void shouldReturnFalseWhenProviderNotExistsByName() {
        // Given
        String name = "Non Existent Provider";
        when(repositoryPort.existsByName(name)).thenReturn(false);
        
        // When
        boolean result = providerQueryService.existsByName(name);
        
        // Then
        assertFalse(result);
        
        verify(repositoryPort).existsByName(name);
    }
    
    @Test
    @DisplayName("Deve buscar múltiplos providers com critérios de busca")
    void shouldSearchMultipleProvidersWithCriteria() {
        // Given
        Provider provider2 = ProviderTestBuilder.aProvider()
            .withId(UUID.randomUUID())
            .withName("Test Provider 2")
            .buildDomain();
            
        ProviderView providerView2 = new ProviderView(
            provider2.getId(),
            "Test Provider 2",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
        
        ProviderSearchCriteriaView criteria = new ProviderSearchCriteriaView(
            0, 10, "name", "ASC", "Test", null, null, null, null
        );
        
        PageResultView<Provider> domainResult = PageResultView.of(
            List.of(provider, provider2), 0, 10, 2L
        );
        
        when(repositoryPort.search(criteria)).thenReturn(domainResult);
        when(mapper.toView(provider)).thenReturn(providerView);
        when(mapper.toView(provider2)).thenReturn(providerView2);
        
        // When
        PageResultView<ProviderView> result = providerQueryService.search(criteria);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.content().size());
        assertEquals(providerView, result.content().get(0));
        assertEquals(providerView2, result.content().get(1));
        
        verify(repositoryPort).search(criteria);
        verify(mapper).toView(provider);
        verify(mapper).toView(provider2);
    }
    
    @Test
    @DisplayName("Deve buscar providers para diferentes tipos de comunicação")
    void shouldGetProvidersForDifferentCommunicationTypes() {
        // Given
        CommunicationTypeEnum[] communicationTypes = {
            CommunicationTypeEnum.EMAIL,
            CommunicationTypeEnum.SMS,
            CommunicationTypeEnum.WHATSAPP,
            CommunicationTypeEnum.PUSH,
            CommunicationTypeEnum.SENDGRID
        };
        
        for (CommunicationTypeEnum communicationType : communicationTypes) {
            Provider testProvider = ProviderTestBuilder.aProvider()
                .withCommunicationType(communicationType)
                .buildDomain();
                
            ProviderView testProviderView = new ProviderView(
                null, null, null, communicationType, null, null, null, null, null, null, null, null, null, null, null, null
            );
            
            when(repositoryPort.findByCommunicationType(communicationType))
                .thenReturn(List.of(testProvider));
            when(mapper.toView(testProvider)).thenReturn(testProviderView);
            
            // When
            List<ProviderView> result = providerQueryService.getByCommunicationType(communicationType);
            
            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(communicationType, result.get(0).communicationType());
        }
        
        verify(repositoryPort, times(communicationTypes.length)).findByCommunicationType(any());
    }
}
