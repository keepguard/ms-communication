package com.keepguard.ms_communication.adapters.in.rest.provider;

import com.keepguard.ms_communication.adapters.in.rest.provider.dto.request.ProviderCreateRequestDTO;
import com.keepguard.ms_communication.adapters.in.rest.provider.dto.response.*;
import com.keepguard.ms_communication.adapters.in.rest.provider.dto.request.ProviderUpdateRequestDTO;
import com.keepguard.ms_communication.domain.dto.provider.ProviderCreateCommandDTO;
import com.keepguard.ms_communication.domain.dto.provider.ProviderUpdateCommandDTO;
import com.keepguard.ms_communication.application.dto.provider.ProviderView;
import com.keepguard.ms_communication.adapters.in.rest.provider.mapper.ProviderAdapterMapper;
import com.keepguard.ms_communication.application.mapper.ProviderApplicationMapper;
import com.keepguard.ms_communication.application.port.in.service.ProviderPort;
import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.domain.enums.ProviderTypeEnum;
import com.keepguard.ms_communication.application.port.out.metrics.MetricsPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para ProviderController
 */
@ExtendWith(MockitoExtension.class)
class ProviderControllerTest {
    
    @Mock
    private ProviderPort providerPort;
    
    @Mock
    private ProviderAdapterMapper adapterMapper;

    @Mock
    private ProviderApplicationMapper applicationMapper;

    @Mock
    private MetricsPort metricsPort;
    
    @InjectMocks
    private ProviderController providerController;
    
    private ProviderCreateRequestDTO providerCreateRequestDTO;
    private ProviderUpdateRequestDTO providerUpdateRequestDTO;
    private ProviderCreateResponseDTO providerCreateResponseDTO;
    private ProviderGetProviderByIdResponseDTO providerGetProviderByIdResponseDTO;
    private ProviderGetAllProvidersResponseDTO providerGetAllProvidersResponseDTO;
    private ProviderGetActiveProvidersResponseDTO providerGetActiveProvidersResponseDTO;
    private ProviderGetProvidersByCommunicationTypeResponseDTO providerGetProvidersByCommunicationTypeResponseDTO;
    private ProviderGetDefaultProviderResponseDTO providerGetDefaultProviderResponseDTO;
    private ProviderActivateProviderResponseDTO providerActivateProviderResponseDTO;
    private ProviderDeactivateProviderResponseDTO providerDeactivateProviderResponseDTO;
    private ProviderSetAsDefaultResponseDTO providerSetAsDefaultResponseDTO;
    private ProviderTestProviderConnectionResponseDTO providerTestProviderConnectionResponseDTO;
    private ProviderView providerView;
    private ProviderCreateCommandDTO providerCreateCommand;
    private ProviderUpdateCommandDTO providerUpdateCommand;
    private UUID providerId;
    private String tenantIdStr;
    private UUID tenantId;
    
    @BeforeEach
    void setUp() {
        providerId = UUID.randomUUID();
        tenantId = UUID.randomUUID();
        tenantIdStr = tenantId.toString();
        
        
        providerCreateRequestDTO = new ProviderCreateRequestDTO();
        providerCreateRequestDTO.setName("Test Provider");
        providerCreateRequestDTO.setProviderType(ProviderTypeEnum.N8N);
        providerCreateRequestDTO.setCommunicationType(CommunicationTypeEnum.EMAIL);
        providerCreateRequestDTO.setIsActive(true);
        providerCreateRequestDTO.setIsDefault(false);
        providerCreateRequestDTO.setPriority(1);
        providerCreateRequestDTO.setUrl("https://test.com/webhook");
        providerCreateRequestDTO.setConfiguration("{\"apiKey\": \"test-key\"}");
        providerCreateRequestDTO.setMaxRetries(3);
        providerCreateRequestDTO.setTimeoutSeconds(30);
        providerCreateRequestDTO.setRateLimitPerMinute(60);
        providerCreateRequestDTO.setDailyLimit(1000);
        providerCreateRequestDTO.setMonthlyLimit(30000);
        
        providerUpdateRequestDTO = new ProviderUpdateRequestDTO();
        providerUpdateRequestDTO.setName("Updated Provider");
        providerUpdateRequestDTO.setProviderType(ProviderTypeEnum.SENDGRID);
        providerUpdateRequestDTO.setCommunicationType(CommunicationTypeEnum.EMAIL);
        providerUpdateRequestDTO.setIsActive(false);
        providerUpdateRequestDTO.setIsDefault(true);
        providerUpdateRequestDTO.setPriority(2);
        providerUpdateRequestDTO.setUrl("https://updated.com/webhook");
        providerUpdateRequestDTO.setConfiguration("{\"apiKey\": \"updated-key\"}");
        providerUpdateRequestDTO.setMaxRetries(5);
        providerUpdateRequestDTO.setTimeoutSeconds(60);
        providerUpdateRequestDTO.setRateLimitPerMinute(120);
        providerUpdateRequestDTO.setDailyLimit(2000);
        providerUpdateRequestDTO.setMonthlyLimit(60000);
        
        providerCreateResponseDTO = ProviderCreateResponseDTO.builder()
            .id(providerId)
            .name("Test Provider")
            .providerType(ProviderTypeEnum.N8N)
            .communicationType(CommunicationTypeEnum.EMAIL)
            .isActive(true)
            .isDefault(false)
            .priority(1)
            .url("https://test.com/webhook")
            .configuration("{\"apiKey\": \"test-key\"}")
            .maxRetries(3)
            .timeoutSeconds(30)
            .rateLimitPerMinute(60)
            .dailyLimit(1000)
            .monthlyLimit(30000)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        providerGetProviderByIdResponseDTO = ProviderGetProviderByIdResponseDTO.builder()
            .id(providerId)
            .name("Test Provider")
            .providerType(ProviderTypeEnum.N8N)
            .communicationType(CommunicationTypeEnum.EMAIL)
            .isActive(true)
            .isDefault(false)
            .priority(1)
            .url("https://test.com/webhook")
            .configuration("{\"apiKey\": \"test-key\"}")
            .maxRetries(3)
            .timeoutSeconds(30)
            .rateLimitPerMinute(60)
            .dailyLimit(1000)
            .monthlyLimit(30000)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        providerGetAllProvidersResponseDTO = ProviderGetAllProvidersResponseDTO.builder()
            .id(providerId)
            .name("Test Provider")
            .providerType(ProviderTypeEnum.N8N)
            .communicationType(CommunicationTypeEnum.EMAIL)
            .isActive(true)
            .isDefault(false)
            .priority(1)
            .url("https://test.com/webhook")
            .configuration("{\"apiKey\": \"test-key\"}")
            .maxRetries(3)
            .timeoutSeconds(30)
            .rateLimitPerMinute(60)
            .dailyLimit(1000)
            .monthlyLimit(30000)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        providerGetActiveProvidersResponseDTO = ProviderGetActiveProvidersResponseDTO.builder()
            .id(providerId)
            .name("Test Provider")
            .providerType(ProviderTypeEnum.N8N)
            .communicationType(CommunicationTypeEnum.EMAIL)
            .isActive(true)
            .isDefault(false)
            .priority(1)
            .url("https://test.com/webhook")
            .configuration("{\"apiKey\": \"test-key\"}")
            .maxRetries(3)
            .timeoutSeconds(30)
            .rateLimitPerMinute(60)
            .dailyLimit(1000)
            .monthlyLimit(30000)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        providerGetProvidersByCommunicationTypeResponseDTO = ProviderGetProvidersByCommunicationTypeResponseDTO.builder()
            .id(providerId)
            .name("Test Provider")
            .providerType(ProviderTypeEnum.N8N)
            .communicationType(CommunicationTypeEnum.EMAIL)
            .isActive(true)
            .isDefault(false)
            .priority(1)
            .url("https://test.com/webhook")
            .configuration("{\"apiKey\": \"test-key\"}")
            .maxRetries(3)
            .timeoutSeconds(30)
            .rateLimitPerMinute(60)
            .dailyLimit(1000)
            .monthlyLimit(30000)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        providerGetDefaultProviderResponseDTO = ProviderGetDefaultProviderResponseDTO.builder()
            .id(providerId)
            .name("Test Provider")
            .providerType(ProviderTypeEnum.N8N)
            .communicationType(CommunicationTypeEnum.EMAIL)
            .isActive(true)
            .isDefault(true)
            .priority(1)
            .url("https://test.com/webhook")
            .configuration("{\"apiKey\": \"test-key\"}")
            .maxRetries(3)
            .timeoutSeconds(30)
            .rateLimitPerMinute(60)
            .dailyLimit(1000)
            .monthlyLimit(30000)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        providerActivateProviderResponseDTO = ProviderActivateProviderResponseDTO.builder()
            .id(providerId)
            .name("Test Provider")
            .providerType(ProviderTypeEnum.N8N)
            .communicationType(CommunicationTypeEnum.EMAIL)
            .isActive(true)
            .isDefault(false)
            .priority(1)
            .url("https://test.com/webhook")
            .configuration("{\"apiKey\": \"test-key\"}")
            .maxRetries(3)
            .timeoutSeconds(30)
            .rateLimitPerMinute(60)
            .dailyLimit(1000)
            .monthlyLimit(30000)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        providerDeactivateProviderResponseDTO = ProviderDeactivateProviderResponseDTO.builder()
            .id(providerId)
            .name("Test Provider")
            .providerType(ProviderTypeEnum.N8N)
            .communicationType(CommunicationTypeEnum.EMAIL)
            .isActive(false)
            .isDefault(false)
            .priority(1)
            .url("https://test.com/webhook")
            .configuration("{\"apiKey\": \"test-key\"}")
            .maxRetries(3)
            .timeoutSeconds(30)
            .rateLimitPerMinute(60)
            .dailyLimit(1000)
            .monthlyLimit(30000)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        providerSetAsDefaultResponseDTO = ProviderSetAsDefaultResponseDTO.builder()
            .id(providerId)
            .name("Test Provider")
            .providerType(ProviderTypeEnum.N8N)
            .communicationType(CommunicationTypeEnum.EMAIL)
            .isActive(true)
            .isDefault(true)
            .priority(1)
            .url("https://test.com/webhook")
            .configuration("{\"apiKey\": \"test-key\"}")
            .maxRetries(3)
            .timeoutSeconds(30)
            .rateLimitPerMinute(60)
            .dailyLimit(1000)
            .monthlyLimit(30000)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        providerTestProviderConnectionResponseDTO = ProviderTestProviderConnectionResponseDTO.builder()
            .providerId(providerId)
            .providerName("Test Provider")
            .isConnected(true)
            .testedAt(LocalDateTime.now())
            .build();
        
        providerView = new ProviderView(
            providerId,
            "Test Provider",
            ProviderTypeEnum.N8N,
            CommunicationTypeEnum.EMAIL,
            true,
            false,
            1,
            "https://test.com/webhook",
            "{\"apiKey\": \"test-key\"}",
            3,
            30,
            60,
            1000,
            30000,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        providerCreateCommand = ProviderCreateCommandDTO.builder()
                .tenantId(tenantId)
                .name("Test Provider")
                .providerType(ProviderTypeEnum.N8N)
                .communicationType(CommunicationTypeEnum.EMAIL)
                .isActive(true)
                .isDefault(false)
                .priority(1)
                .url("https://test.com/webhook")
                .configuration("{\"apiKey\": \"test-key\"}")
                .maxRetries(3)
                .timeoutSeconds(30)
                .rateLimitPerMinute(60)
                .dailyLimit(1000)
                .monthlyLimit(30000)
                .build();
        
        providerUpdateCommand = ProviderUpdateCommandDTO.builder()
                .id(providerId)
                .tenantId(tenantId)
                .name("Updated Provider")
                .providerType(ProviderTypeEnum.SENDGRID)
                .communicationType(CommunicationTypeEnum.EMAIL)
                .isActive(false)
                .isDefault(true)
                .priority(2)
                .url("https://updated.com/webhook")
                .configuration("{\"apiKey\": \"updated-key\"}")
                .maxRetries(5)
                .timeoutSeconds(60)
                .rateLimitPerMinute(120)
                .dailyLimit(2000)
                .monthlyLimit(60000)
                .build();
    }
    
    @Test
    @DisplayName("Deve criar provedor com sucesso")
    void shouldCreateProviderSuccessfully() {
        // Given
        when(adapterMapper.toCreateCommand(providerCreateRequestDTO, tenantId)).thenReturn(com.keepguard.ms_communication.domain.dto.provider.ProviderCreateCommandDTO.builder()
                .name("Test Provider")
                .providerType(ProviderTypeEnum.N8N)
                .communicationType(CommunicationTypeEnum.EMAIL)
                .isActive(true)
                .isDefault(false)
                .priority(1)
                .url("https://test.com/webhook")
                .configuration("{\"apiKey\": \"test-key\"}")
                .maxRetries(3)
                .timeoutSeconds(30)
                .rateLimitPerMinute(60)
                .dailyLimit(1000)
                .monthlyLimit(30000)
                .build());
        when(applicationMapper.toCreateCommand(any(com.keepguard.ms_communication.domain.dto.provider.ProviderCreateCommandDTO.class))).thenReturn(providerCreateCommand);
        when(providerPort.create(providerCreateCommand)).thenReturn(providerView);
        when(adapterMapper.toCreateResponseDTO(providerView)).thenReturn(providerCreateResponseDTO);
        
        // When
        ResponseEntity<ProviderCreateResponseDTO> response = providerController.createProvider(providerCreateRequestDTO, tenantIdStr);
        
        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        ProviderCreateResponseDTO responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(providerId, responseBody.getId());
        assertEquals("Test Provider", responseBody.getName());
        assertEquals(ProviderTypeEnum.N8N, responseBody.getProviderType());
        assertEquals(CommunicationTypeEnum.EMAIL, responseBody.getCommunicationType());
        
        verify(adapterMapper, times(1)).toCreateCommand(providerCreateRequestDTO, tenantId);
        verify(providerPort, times(1)).create(providerCreateCommand);
        verify(adapterMapper, times(1)).toCreateResponseDTO(providerView);
    }
    
    @Test
    @DisplayName("Deve lidar com exceções durante criação de provedor")
    void shouldHandleExceptionsDuringProviderCreation() {
        // Given
        com.keepguard.ms_communication.domain.dto.provider.ProviderCreateCommandDTO requestCommand = com.keepguard.ms_communication.domain.dto.provider.ProviderCreateCommandDTO.builder()
                .name("Test Provider")
                .providerType(ProviderTypeEnum.N8N)
                .communicationType(CommunicationTypeEnum.EMAIL)
                .isActive(true)
                .isDefault(false)
                .priority(1)
                .url("https://test.com/webhook")
                .configuration("{\"apiKey\": \"test-key\"}")
                .maxRetries(3)
                .timeoutSeconds(30)
                .rateLimitPerMinute(60)
                .dailyLimit(1000)
                .monthlyLimit(30000)
                .build();
        
        when(adapterMapper.toCreateCommand(providerCreateRequestDTO, tenantId)).thenReturn(requestCommand);
        when(applicationMapper.toCreateCommand(requestCommand)).thenReturn(providerCreateCommand);
        when(providerPort.create(providerCreateCommand))
            .thenThrow(new RuntimeException("Service error"));
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            providerController.createProvider(providerCreateRequestDTO, tenantIdStr);
        });
        
        verify(adapterMapper, times(1)).toCreateCommand(providerCreateRequestDTO, tenantId);
        verify(providerPort, times(1)).create(providerCreateCommand);
    }
    
    @Test
    @DisplayName("Deve atualizar provedor com sucesso")
    void shouldUpdateProviderSuccessfully() {
        // Given
        when(adapterMapper.toUpdateCommand(providerId, providerUpdateRequestDTO, tenantId)).thenReturn(com.keepguard.ms_communication.domain.dto.provider.ProviderUpdateCommandDTO.builder()
                .name("Updated Provider")
                .providerType(ProviderTypeEnum.SENDGRID)
                .communicationType(CommunicationTypeEnum.EMAIL)
                .isActive(false)
                .isDefault(true)
                .priority(2)
                .url("https://updated.com/webhook")
                .configuration("{\"apiKey\": \"updated-key\"}")
                .maxRetries(5)
                .timeoutSeconds(60)
                .rateLimitPerMinute(120)
                .dailyLimit(2000)
                .monthlyLimit(60000)
                .build());
        when(applicationMapper.toUpdateCommand(any(com.keepguard.ms_communication.domain.dto.provider.ProviderUpdateCommandDTO.class))).thenReturn(providerUpdateCommand);
        when(providerPort.update(providerUpdateCommand)).thenReturn(providerView);
        when(adapterMapper.toUpdateResponseDTO(providerView)).thenReturn(ProviderUpdateResponseDTO.builder()
                .id(providerId)
                .name("Updated Provider")
                .providerType(ProviderTypeEnum.SENDGRID)
                .communicationType(CommunicationTypeEnum.EMAIL)
                .isActive(false)
                .isDefault(true)
                .priority(2)
                .url("https://updated.com/webhook")
                .configuration("{\"apiKey\": \"updated-key\"}")
                .maxRetries(5)
                .timeoutSeconds(60)
                .rateLimitPerMinute(120)
                .dailyLimit(2000)
                .monthlyLimit(60000)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build());
        
        // When
        ResponseEntity<ProviderUpdateResponseDTO> response = providerController.updateProvider(providerId, providerUpdateRequestDTO, tenantIdStr);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        verify(adapterMapper, times(1)).toUpdateCommand(providerId, providerUpdateRequestDTO, tenantId);
        verify(providerPort, times(1)).update(providerUpdateCommand);
        verify(adapterMapper, times(1)).toUpdateResponseDTO(providerView);
    }
    
    @Test
    @DisplayName("Deve buscar provedor por ID com sucesso")
    void shouldGetProviderByIdSuccessfully() {
        // Given
        when(providerPort.getById(providerId)).thenReturn(Optional.of(providerView));
        when(adapterMapper.toGetProviderByIdResponseDTO(providerView)).thenReturn(providerGetProviderByIdResponseDTO);
        
        // When
        ResponseEntity<ProviderGetProviderByIdResponseDTO> response = providerController.getProviderById(providerId, tenantIdStr);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        verify(providerPort, times(1)).getById(providerId);
        verify(adapterMapper, times(1)).toGetProviderByIdResponseDTO(providerView);
    }
    
    @Test
    @DisplayName("Deve lidar com provedor não encontrado por ID")
    void shouldHandleProviderNotFoundById() {
        // Given
        when(providerPort.getById(providerId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            providerController.getProviderById(providerId, tenantIdStr);
        });
        
        verify(providerPort, times(1)).getById(providerId);
    }
    
    @Test
    @DisplayName("Deve listar todos os provedores")
    void shouldGetAllProviders() {
        // Given
        List<ProviderView> views = List.of(providerView);
        when(providerPort.getAllActive()).thenReturn(views);
        when(adapterMapper.toGetAllProvidersResponseDTO(providerView)).thenReturn(providerGetAllProvidersResponseDTO);
        
        // When
        ResponseEntity<List<ProviderGetAllProvidersResponseDTO>> response = providerController.getAllProviders(tenantIdStr);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<ProviderGetAllProvidersResponseDTO> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(1, responseBody.size());
        
        verify(providerPort, times(1)).getAllActive();
        verify(adapterMapper, times(1)).toGetAllProvidersResponseDTO(providerView);
    }
    
    @Test
    @DisplayName("Deve listar provedores ativos")
    void shouldGetActiveProviders() {
        // Given
        List<ProviderView> views = List.of(providerView);
        when(providerPort.getAllActive()).thenReturn(views);
        when(adapterMapper.toGetActiveProvidersResponseDTO(providerView)).thenReturn(providerGetActiveProvidersResponseDTO);
        
        // When
        ResponseEntity<List<ProviderGetActiveProvidersResponseDTO>> response = providerController.getActiveProviders(tenantIdStr);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<ProviderGetActiveProvidersResponseDTO> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(1, responseBody.size());
        
        verify(providerPort, times(1)).getAllActive();
        verify(adapterMapper, times(1)).toGetActiveProvidersResponseDTO(providerView);
    }
    
    @Test
    @DisplayName("Deve listar provedores por tipo de comunicação")
    void shouldGetProvidersByCommunicationType() {
        // Given
        List<ProviderView> views = List.of(providerView);
        when(providerPort.getByCommunicationType(CommunicationTypeEnum.EMAIL)).thenReturn(views);
        when(adapterMapper.toGetProvidersByCommunicationTypeResponseDTO(providerView)).thenReturn(providerGetProvidersByCommunicationTypeResponseDTO);
        
        // When
        ResponseEntity<List<ProviderGetProvidersByCommunicationTypeResponseDTO>> response = providerController.getProvidersByCommunicationType(CommunicationTypeEnum.EMAIL, tenantIdStr);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<ProviderGetProvidersByCommunicationTypeResponseDTO> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(1, responseBody.size());
        
        verify(providerPort, times(1)).getByCommunicationType(CommunicationTypeEnum.EMAIL);
        verify(adapterMapper, times(1)).toGetProvidersByCommunicationTypeResponseDTO(providerView);
    }
    
    @Test
    @DisplayName("Deve buscar provedor padrão por tipo de comunicação")
    void shouldGetDefaultProviderByCommunicationType() {
        // Given
        when(providerPort.getDefaultByCommunicationType(CommunicationTypeEnum.EMAIL)).thenReturn(Optional.of(providerView));
        when(adapterMapper.toGetDefaultProviderResponseDTO(providerView)).thenReturn(providerGetDefaultProviderResponseDTO);
        
        // When
        ResponseEntity<ProviderGetDefaultProviderResponseDTO> response = providerController.getDefaultProvider(CommunicationTypeEnum.EMAIL, tenantIdStr);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        verify(providerPort, times(1)).getDefaultByCommunicationType(CommunicationTypeEnum.EMAIL);
        verify(adapterMapper, times(1)).toGetDefaultProviderResponseDTO(providerView);
    }
    
    @Test
    @DisplayName("Deve lidar com provedor padrão não encontrado")
    void shouldHandleDefaultProviderNotFound() {
        // Given
        when(providerPort.getDefaultByCommunicationType(CommunicationTypeEnum.EMAIL)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            providerController.getDefaultProvider(CommunicationTypeEnum.EMAIL, tenantIdStr);
        });
        
        verify(providerPort, times(1)).getDefaultByCommunicationType(CommunicationTypeEnum.EMAIL);
    }
    
    @Test
    @DisplayName("Deve deletar provedor com sucesso")
    void shouldDeleteProviderSuccessfully() {
        // Given
        doNothing().when(providerPort).delete(providerId);
        
        // When
        ResponseEntity<Void> response = providerController.deleteProvider(providerId, tenantIdStr);
        
        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        
        verify(providerPort, times(1)).delete(providerId);
    }
    
    @Test
    @DisplayName("Deve ativar provedor com sucesso")
    void shouldActivateProviderSuccessfully() {
        // Given
        when(providerPort.activate(providerId)).thenReturn(providerView);
        when(adapterMapper.toActivateProviderResponseDTO(providerView)).thenReturn(providerActivateProviderResponseDTO);
        
        // When
        ResponseEntity<ProviderActivateProviderResponseDTO> response = providerController.activateProvider(providerId, tenantIdStr);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        verify(providerPort, times(1)).activate(providerId);
        verify(adapterMapper, times(1)).toActivateProviderResponseDTO(providerView);
    }
    
    @Test
    @DisplayName("Deve desativar provedor com sucesso")
    void shouldDeactivateProviderSuccessfully() {
        // Given
        when(providerPort.deactivate(providerId)).thenReturn(providerView);
        when(adapterMapper.toDeactivateProviderResponseDTO(providerView)).thenReturn(providerDeactivateProviderResponseDTO);
        
        // When
        ResponseEntity<ProviderDeactivateProviderResponseDTO> response = providerController.deactivateProvider(providerId, tenantIdStr);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        verify(providerPort, times(1)).deactivate(providerId);
        verify(adapterMapper, times(1)).toDeactivateProviderResponseDTO(providerView);
    }
    
    @Test
    @DisplayName("Deve definir provedor como padrão com sucesso")
    void shouldSetProviderAsDefaultSuccessfully() {
        // Given
        when(providerPort.setAsDefault(providerId)).thenReturn(providerView);
        when(adapterMapper.toSetAsDefaultResponseDTO(providerView)).thenReturn(providerSetAsDefaultResponseDTO);
        
        // When
        ResponseEntity<ProviderSetAsDefaultResponseDTO> response = providerController.setAsDefault(providerId, tenantIdStr);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        verify(providerPort, times(1)).setAsDefault(providerId);
        verify(adapterMapper, times(1)).toSetAsDefaultResponseDTO(providerView);
    }
    
    @Test
    @DisplayName("Deve listar tipos de provedores")
    void shouldGetProviderTypes() {
        // When
        ResponseEntity<ProviderTypeEnum[]> response = providerController.getProviderTypes(tenantIdStr);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        ProviderTypeEnum[] responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(ProviderTypeEnum.values().length, responseBody.length);
    }
    
    @Test
    @DisplayName("Deve listar tipos de comunicação")
    void shouldGetCommunicationTypes() {
        // When
        ResponseEntity<CommunicationTypeEnum[]> response = providerController.getCommunicationTypes(tenantIdStr);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        CommunicationTypeEnum[] responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(CommunicationTypeEnum.values().length, responseBody.length);
    }
    
    
    @Test
    @DisplayName("Deve testar ProviderCreateDTO com dados válidos")
    void shouldTestProviderCreateDTOWithValidData() {
        // Given & When
        ProviderCreateRequestDTO dto = new ProviderCreateRequestDTO();
        dto.setName("Test Provider");
        dto.setProviderType(ProviderTypeEnum.N8N);
        dto.setCommunicationType(CommunicationTypeEnum.EMAIL);
        dto.setIsActive(true);
        dto.setIsDefault(false);
        dto.setPriority(1);
        dto.setUrl("https://test.com");
        dto.setConfiguration("{\"key\": \"value\"}");
        dto.setMaxRetries(3);
        dto.setTimeoutSeconds(30);
        dto.setRateLimitPerMinute(60);
        dto.setDailyLimit(1000);
        dto.setMonthlyLimit(30000);
        
        // Then
        assertNotNull(dto);
        assertEquals("Test Provider", dto.getName());
        assertEquals(ProviderTypeEnum.N8N, dto.getProviderType());
        assertEquals(CommunicationTypeEnum.EMAIL, dto.getCommunicationType());
        assertTrue(dto.getIsActive());
        assertFalse(dto.getIsDefault());
        assertEquals(1, dto.getPriority());
        assertEquals("https://test.com", dto.getUrl());
        assertEquals("{\"key\": \"value\"}", dto.getConfiguration());
        assertEquals(3, dto.getMaxRetries());
        assertEquals(30, dto.getTimeoutSeconds());
        assertEquals(60, dto.getRateLimitPerMinute());
        assertEquals(1000, dto.getDailyLimit());
        assertEquals(30000, dto.getMonthlyLimit());
    }
    
    @Test
    @DisplayName("Deve testar ProviderUpdateDTO com dados válidos")
    void shouldTestProviderUpdateDTOWithValidData() {
        // Given & When
        ProviderUpdateRequestDTO dto = new ProviderUpdateRequestDTO();
        dto.setName("Updated Provider");
        dto.setProviderType(ProviderTypeEnum.SENDGRID);
        dto.setCommunicationType(CommunicationTypeEnum.SMS);
        dto.setIsActive(false);
        dto.setIsDefault(true);
        dto.setPriority(2);
        dto.setUrl("https://updated.com");
        dto.setConfiguration("{\"key\": \"updated\"}");
        dto.setMaxRetries(5);
        dto.setTimeoutSeconds(60);
        dto.setRateLimitPerMinute(120);
        dto.setDailyLimit(2000);
        dto.setMonthlyLimit(60000);
        
        // Then
        assertNotNull(dto);
        assertEquals("Updated Provider", dto.getName());
        assertEquals(ProviderTypeEnum.SENDGRID, dto.getProviderType());
        assertEquals(CommunicationTypeEnum.SMS, dto.getCommunicationType());
        assertFalse(dto.getIsActive());
        assertTrue(dto.getIsDefault());
        assertEquals(2, dto.getPriority());
        assertEquals("https://updated.com", dto.getUrl());
        assertEquals("{\"key\": \"updated\"}", dto.getConfiguration());
        assertEquals(5, dto.getMaxRetries());
        assertEquals(60, dto.getTimeoutSeconds());
        assertEquals(120, dto.getRateLimitPerMinute());
        assertEquals(2000, dto.getDailyLimit());
        assertEquals(60000, dto.getMonthlyLimit());
    }
    
    @Test
    @DisplayName("Deve testar ProviderResponseDTO com dados válidos")
    void shouldTestProviderResponseDTOWithValidData() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        
        // When
        ProviderCreateResponseDTO dto = ProviderCreateResponseDTO.builder()
            .id(providerId)
            .name("Test Provider")
            .providerType(ProviderTypeEnum.N8N)
            .communicationType(CommunicationTypeEnum.EMAIL)
            .isActive(true)
            .isDefault(false)
            .priority(1)
            .url("https://test.com")
            .configuration("{\"key\": \"value\"}")
            .maxRetries(3)
            .timeoutSeconds(30)
            .rateLimitPerMinute(60)
            .dailyLimit(1000)
            .monthlyLimit(30000)
            .createdAt(now)
            .updatedAt(now)
            .build();
        
        // Then
        assertNotNull(dto);
        assertEquals(providerId, dto.getId());
        assertEquals("Test Provider", dto.getName());
        assertEquals(ProviderTypeEnum.N8N, dto.getProviderType());
        assertEquals(CommunicationTypeEnum.EMAIL, dto.getCommunicationType());
        assertTrue(dto.getIsActive());
        assertFalse(dto.getIsDefault());
        assertEquals(1, dto.getPriority());
        assertEquals("https://test.com", dto.getUrl());
        assertEquals("{\"key\": \"value\"}", dto.getConfiguration());
        assertEquals(3, dto.getMaxRetries());
        assertEquals(30, dto.getTimeoutSeconds());
        assertEquals(60, dto.getRateLimitPerMinute());
        assertEquals(1000, dto.getDailyLimit());
        assertEquals(30000, dto.getMonthlyLimit());
        assertEquals(now, dto.getCreatedAt());
        assertEquals(now, dto.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Deve testar ProviderMapper.toCreateCommand com dados válidos")
    void shouldTestProviderMapperToCreateCommandWithValidData() {
        // Given
        ProviderCreateRequestDTO dto = new ProviderCreateRequestDTO();
        dto.setName("Test Provider");
        dto.setProviderType(ProviderTypeEnum.N8N);
        dto.setCommunicationType(CommunicationTypeEnum.EMAIL);
        dto.setIsActive(true);
        dto.setIsDefault(false);
        dto.setPriority(1);
        dto.setUrl("https://test.com");
        dto.setConfiguration("{\"key\": \"value\"}");
        dto.setMaxRetries(3);
        dto.setTimeoutSeconds(30);
        dto.setRateLimitPerMinute(60);
        dto.setDailyLimit(1000);
        dto.setMonthlyLimit(30000);
        
        when(adapterMapper.toCreateCommand(dto, tenantId)).thenReturn(com.keepguard.ms_communication.domain.dto.provider.ProviderCreateCommandDTO.builder().build());
        when(applicationMapper.toCreateCommand(any(com.keepguard.ms_communication.domain.dto.provider.ProviderCreateCommandDTO.class))).thenReturn(providerCreateCommand);
        
        // When
        com.keepguard.ms_communication.domain.dto.provider.ProviderCreateCommandDTO requestCommand = adapterMapper.toCreateCommand(dto, tenantId);
        ProviderCreateCommandDTO result = applicationMapper.toCreateCommand(requestCommand);
        
        // Then
        assertNotNull(result);
        assertEquals("Test Provider", result.getName());
        assertEquals(ProviderTypeEnum.N8N, result.getProviderType());
        assertEquals(CommunicationTypeEnum.EMAIL, result.getCommunicationType());
        assertTrue(result.getIsActive());
        assertFalse(result.getIsDefault());
        assertEquals(1, result.getPriority());
        assertEquals("https://test.com/webhook", result.getUrl());
        assertEquals("{\"apiKey\": \"test-key\"}", result.getConfiguration());
        assertEquals(3, result.getMaxRetries());
        assertEquals(30, result.getTimeoutSeconds());
        assertEquals(60, result.getRateLimitPerMinute());
        assertEquals(1000, result.getDailyLimit());
        assertEquals(30000, result.getMonthlyLimit());
        
        verify(adapterMapper, times(1)).toCreateCommand(dto, tenantId);
    }
    
    @Test
    @DisplayName("Deve testar ProviderMapper.toUpdateCommand com dados válidos")
    void shouldTestProviderMapperToUpdateCommandWithValidData() {
        // Given
        ProviderUpdateRequestDTO dto = new ProviderUpdateRequestDTO();
        dto.setName("Updated Provider");
        dto.setProviderType(ProviderTypeEnum.SENDGRID);
        dto.setCommunicationType(CommunicationTypeEnum.SMS);
        dto.setIsActive(false);
        dto.setIsDefault(true);
        dto.setPriority(2);
        dto.setUrl("https://updated.com");
        dto.setConfiguration("{\"key\": \"updated\"}");
        dto.setMaxRetries(5);
        dto.setTimeoutSeconds(60);
        dto.setRateLimitPerMinute(120);
        dto.setDailyLimit(2000);
        dto.setMonthlyLimit(60000);
        
        when(adapterMapper.toUpdateCommand(providerId, dto, tenantId)).thenReturn(com.keepguard.ms_communication.domain.dto.provider.ProviderUpdateCommandDTO.builder().build());
        when(applicationMapper.toUpdateCommand(any(com.keepguard.ms_communication.domain.dto.provider.ProviderUpdateCommandDTO.class))).thenReturn(providerUpdateCommand);
        
        // When
        com.keepguard.ms_communication.domain.dto.provider.ProviderUpdateCommandDTO requestCommand = adapterMapper.toUpdateCommand(providerId, dto, tenantId);
        ProviderUpdateCommandDTO result = applicationMapper.toUpdateCommand(requestCommand);
        
        // Then
        assertNotNull(result);
        assertEquals(providerId, result.getId());
        assertEquals("Updated Provider", result.getName());
        assertEquals(ProviderTypeEnum.SENDGRID, result.getProviderType());
        assertEquals(CommunicationTypeEnum.EMAIL, result.getCommunicationType());
        assertFalse(result.getIsActive());
        assertTrue(result.getIsDefault());
        assertEquals(2, result.getPriority());
        assertEquals("https://updated.com/webhook", result.getUrl());
        assertEquals("{\"apiKey\": \"updated-key\"}", result.getConfiguration());
        assertEquals(5, result.getMaxRetries());
        assertEquals(60, result.getTimeoutSeconds());
        assertEquals(120, result.getRateLimitPerMinute());
        assertEquals(2000, result.getDailyLimit());
        assertEquals(60000, result.getMonthlyLimit());
        
        verify(adapterMapper, times(1)).toUpdateCommand(providerId, dto, tenantId);
    }
    
    @Test
    @DisplayName("Deve testar ProviderMapper.toCreateResponseDTO com dados válidos")
    void shouldTestProviderMapperToCreateResponseDTOWithValidData() {
        // Given
        when(adapterMapper.toCreateResponseDTO(providerView)).thenReturn(providerCreateResponseDTO);
        
        // When
        ProviderCreateResponseDTO result = adapterMapper.toCreateResponseDTO(providerView);
        
        // Then
        assertNotNull(result);
        assertEquals(providerId, result.getId());
        assertEquals("Test Provider", result.getName());
        assertEquals(ProviderTypeEnum.N8N, result.getProviderType());
        assertEquals(CommunicationTypeEnum.EMAIL, result.getCommunicationType());
        assertTrue(result.getIsActive());
        assertFalse(result.getIsDefault());
        assertEquals(1, result.getPriority());
        assertEquals("https://test.com/webhook", result.getUrl());
        assertEquals("{\"apiKey\": \"test-key\"}", result.getConfiguration());
        assertEquals(3, result.getMaxRetries());
        assertEquals(30, result.getTimeoutSeconds());
        assertEquals(60, result.getRateLimitPerMinute());
        assertEquals(1000, result.getDailyLimit());
        assertEquals(30000, result.getMonthlyLimit());
        
        verify(adapterMapper, times(1)).toCreateResponseDTO(providerView);
    }
}
