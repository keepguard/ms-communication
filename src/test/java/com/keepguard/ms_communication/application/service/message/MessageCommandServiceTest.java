package com.keepguard.ms_communication.application.service.message;

import com.keepguard.ms_communication.application.port.out.metrics.MetricsPort;
import com.keepguard.ms_communication.application.port.out.persistence.ProviderRepositoryPort;
import com.keepguard.ms_communication.application.service.exception.NotFoundException;
import com.keepguard.ms_communication.application.service.template.TemplateProcessorService;
import com.keepguard.ms_communication.domain.dto.message.MessageSendCommandDTO;
import com.keepguard.ms_communication.domain.entity.Provider;
import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.domain.enums.ProviderTypeEnum;
import com.keepguard.ms_communication.infrastructure.provider.CommunicationProvider;
import com.keepguard.ms_communication.infrastructure.provider.strategy.ProviderStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para MessageCommandService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Message Command Service Tests")
class MessageCommandServiceTest {

    @Mock
    private ProviderRepositoryPort providerRepositoryPort;

    @Mock
    private ProviderStrategyFactory strategyFactory;

    @Mock
    private MetricsPort metricsPort;

    @Mock
    private List<CommunicationProvider> communicationProviders;

    @Mock
    private TemplateProcessorService templateProcessorService;

    @InjectMocks
    private MessageCommandService messageCommandService;

    private MessageSendCommandDTO messageSendCommand;
    private Provider provider;
    private UUID providerId;
    private UUID xApplicationUuid;

    @BeforeEach
    void setUp() {
        providerId = UUID.randomUUID();
        xApplicationUuid = UUID.randomUUID();

        Map<String, Object> variables = new HashMap<>();
        variables.put("userName", "Test User");
        variables.put("activationLink", "https://example.com/activate");

        messageSendCommand = MessageSendCommandDTO.builder()
                .xApplicationUuid(xApplicationUuid)
                .communicationType(CommunicationTypeEnum.EMAIL)
                .recipient("test@example.com")
                .subject("Test Subject")
                .content("Test Content")
                .messageType("EMAIL")
                .templateType("CADASTRO_SUCESSO")
                .variables(variables)
                .build();

        provider = Provider.builder()
                .id(providerId)
                .name("Test Provider")
                .providerType(ProviderTypeEnum.N8N)
                .communicationType(CommunicationTypeEnum.EMAIL)
                .isActive(true)
                .isDefault(false)
                .priority(1)
                .url("https://test.com")
                .configuration("{}")
                .maxRetries(3)
                .timeoutSeconds(30)
                .rateLimitPerMinute(100)
                .dailyLimit(1000)
                .monthlyLimit(30000)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Deve lançar exceção quando provedor não for encontrado por ID")
    void shouldThrowExceptionWhenProviderNotFoundById() {
        // Given
        when(providerRepositoryPort.findById(providerId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> {
            messageCommandService.sendWithProvider(providerId, messageSendCommand);
        });

        verify(providerRepositoryPort, times(1)).findById(providerId);
        verify(strategyFactory, never()).getStrategy(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nenhum provedor ativo for encontrado para fallback")
    void shouldThrowExceptionWhenNoActiveProviderFoundForFallback() {
        // Given
        when(providerRepositoryPort.findByCommunicationType(CommunicationTypeEnum.EMAIL))
                .thenReturn(Collections.emptyList());

        // When & Then
        assertThrows(NotFoundException.class, () -> {
            messageCommandService.sendWithFallback(messageSendCommand);
        });

        verify(providerRepositoryPort, times(1)).findByCommunicationType(CommunicationTypeEnum.EMAIL);
        verify(strategyFactory, never()).getStrategy(any());
    }

    @Test
    @DisplayName("Deve criar command DTO com todos os campos corretamente")
    void shouldCreateCommandDTOWithAllFieldsCorrectly() {
        // Given & When
        MessageSendCommandDTO command = MessageSendCommandDTO.builder()
                .xApplicationUuid(xApplicationUuid)
                .communicationType(CommunicationTypeEnum.EMAIL)
                .recipient("test@example.com")
                .subject("Test Subject")
                .content("Test Content")
                .messageType("EMAIL")
                .templateType("CADASTRO_SUCESSO")
                .variables(new HashMap<>())
                .build();

        // Then
        assertNotNull(command);
        assertEquals(xApplicationUuid, command.getXApplicationUuid());
        assertEquals(CommunicationTypeEnum.EMAIL, command.getCommunicationType());
        assertEquals("test@example.com", command.getRecipient());
        assertEquals("Test Subject", command.getSubject());
        assertEquals("Test Content", command.getContent());
        assertEquals("EMAIL", command.getMessageType());
        assertEquals("CADASTRO_SUCESSO", command.getTemplateType());
        assertNotNull(command.getVariables());
    }

    @Test
    @DisplayName("Deve criar Provider com todos os campos corretamente")
    void shouldCreateProviderWithAllFieldsCorrectly() {
        // Then
        assertNotNull(provider);
        assertEquals(providerId, provider.getId());
        assertEquals("Test Provider", provider.getName());
        assertEquals(ProviderTypeEnum.N8N, provider.getProviderType());
        assertEquals(CommunicationTypeEnum.EMAIL, provider.getCommunicationType());
        assertTrue(provider.getIsActive());
        assertFalse(provider.getIsDefault());
        assertEquals(1, provider.getPriority());
        assertEquals("https://test.com", provider.getUrl());
        assertEquals("{}", provider.getConfiguration());
        assertEquals(3, provider.getMaxRetries());
        assertEquals(30, provider.getTimeoutSeconds());
        assertEquals(100, provider.getRateLimitPerMinute());
        assertEquals(1000, provider.getDailyLimit());
        assertEquals(30000, provider.getMonthlyLimit());
        assertNotNull(provider.getCreatedAt());
        assertNotNull(provider.getUpdatedAt());
    }
}
