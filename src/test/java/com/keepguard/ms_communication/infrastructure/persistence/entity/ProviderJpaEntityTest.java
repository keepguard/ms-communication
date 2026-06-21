package com.keepguard.ms_communication.infrastructure.persistence.entity;

import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.domain.enums.ProviderTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para ProviderJpaEntity
 */
class ProviderJpaEntityTest {

    private ProviderJpaEntity providerJpaEntity;
    private UUID testId;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testDateTime = LocalDateTime.now();
        
        providerJpaEntity = new ProviderJpaEntity();
    }

    @Test
    @DisplayName("Deve criar ProviderJpaEntity com construtor padrão")
    void shouldCreateProviderJpaEntityWithDefaultConstructor() {
        // When
        ProviderJpaEntity entity = new ProviderJpaEntity();

        // Then
        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getName());
        assertNull(entity.getProviderType());
        assertNull(entity.getCommunicationType());
        assertTrue(entity.getIsActive());
        assertFalse(entity.getIsDefault());
        assertEquals(1, entity.getPriority());
        assertNull(entity.getUrl());
        assertNull(entity.getConfiguration());
        assertEquals(3, entity.getMaxRetries());
        assertEquals(30, entity.getTimeoutSeconds());
        assertNull(entity.getRateLimitPerMinute());
        assertNull(entity.getDailyLimit());
        assertNull(entity.getMonthlyLimit());
        assertNull(entity.getCreatedAt());
        assertNull(entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve criar ProviderJpaEntity com construtor completo")
    void shouldCreateProviderJpaEntityWithFullConstructor() {
        // Given
        String configuration = "{\"apiKey\":\"test-key\",\"baseUrl\":\"https://api.example.com\"}";

        // When
        ProviderJpaEntity entity = new ProviderJpaEntity(
            testId,
            "Test Provider",
            ProviderTypeEnum.N8N,
            CommunicationTypeEnum.EMAIL,
            true,
            false,
            2,
            "https://n8n.example.com",
            configuration,
            5,
            60,
            100,
            1000,
            10000,
            null, // variables
            testDateTime,
            testDateTime
        );

        // Then
        assertNotNull(entity);
        assertEquals(testId, entity.getId());
        assertEquals("Test Provider", entity.getName());
        assertEquals(ProviderTypeEnum.N8N, entity.getProviderType());
        assertEquals(CommunicationTypeEnum.EMAIL, entity.getCommunicationType());
        assertTrue(entity.getIsActive());
        assertFalse(entity.getIsDefault());
        assertEquals(2, entity.getPriority());
        assertEquals("https://n8n.example.com", entity.getUrl());
        assertEquals(configuration, entity.getConfiguration());
        assertEquals(5, entity.getMaxRetries());
        assertEquals(60, entity.getTimeoutSeconds());
        assertEquals(100, entity.getRateLimitPerMinute());
        assertEquals(1000, entity.getDailyLimit());
        assertEquals(10000, entity.getMonthlyLimit());
        assertEquals(testDateTime, entity.getCreatedAt());
        assertEquals(testDateTime, entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve criar ProviderJpaEntity com builder")
    void shouldCreateProviderJpaEntityWithBuilder() {
        // Given
        String configuration = "{\"apiKey\":\"sendgrid-key\"}";

        // When
        ProviderJpaEntity entity = ProviderJpaEntity.builder()
            .id(testId)
            .name("SendGrid Provider")
            .providerType(ProviderTypeEnum.SENDGRID)
            .communicationType(CommunicationTypeEnum.EMAIL)
            .isActive(true)
            .isDefault(true)
            .priority(1)
            .url("https://api.sendgrid.com")
            .configuration(configuration)
            .maxRetries(3)
            .timeoutSeconds(30)
            .rateLimitPerMinute(200)
            .dailyLimit(5000)
            .monthlyLimit(100000)
            .createdAt(testDateTime)
            .updatedAt(testDateTime)
            .build();

        // Then
        assertNotNull(entity);
        assertEquals(testId, entity.getId());
        assertEquals("SendGrid Provider", entity.getName());
        assertEquals(ProviderTypeEnum.SENDGRID, entity.getProviderType());
        assertEquals(CommunicationTypeEnum.EMAIL, entity.getCommunicationType());
        assertTrue(entity.getIsActive());
        assertTrue(entity.getIsDefault());
        assertEquals(1, entity.getPriority());
        assertEquals("https://api.sendgrid.com", entity.getUrl());
        assertEquals(configuration, entity.getConfiguration());
        assertEquals(3, entity.getMaxRetries());
        assertEquals(30, entity.getTimeoutSeconds());
        assertEquals(200, entity.getRateLimitPerMinute());
        assertEquals(5000, entity.getDailyLimit());
        assertEquals(100000, entity.getMonthlyLimit());
        assertEquals(testDateTime, entity.getCreatedAt());
        assertEquals(testDateTime, entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve usar valores padrão quando não especificados")
    void shouldUseDefaultValuesWhenNotSpecified() {
        // When
        ProviderJpaEntity entity = ProviderJpaEntity.builder()
            .name("Test Provider")
            .providerType(ProviderTypeEnum.N8N)
            .communicationType(CommunicationTypeEnum.EMAIL)
            .build();

        // Then
        assertTrue(entity.getIsActive());
        assertFalse(entity.getIsDefault());
        assertEquals(1, entity.getPriority());
        assertEquals(3, entity.getMaxRetries());
        assertEquals(30, entity.getTimeoutSeconds());
    }

    @Test
    @DisplayName("Deve definir timestamps automaticamente no PrePersist")
    void shouldSetTimestampsAutomaticallyOnPrePersist() {
        // Given
        ProviderJpaEntity entity = ProviderJpaEntity.builder()
            .name("Test Provider")
            .providerType(ProviderTypeEnum.N8N)
            .communicationType(CommunicationTypeEnum.EMAIL)
            .build();

        // When
        entity.onCreate();

        // Then
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
        assertTrue(entity.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(entity.getUpdatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        // Allow small time difference between createdAt and updatedAt
        assertTrue(Math.abs(entity.getCreatedAt().getNano() - entity.getUpdatedAt().getNano()) < 1000000);
    }

    @Test
    @DisplayName("Deve atualizar updatedAt no PreUpdate")
    void shouldUpdateUpdatedAtOnPreUpdate() throws InterruptedException {
        // Given
        ProviderJpaEntity entity = ProviderJpaEntity.builder()
            .name("Test Provider")
            .providerType(ProviderTypeEnum.N8N)
            .communicationType(CommunicationTypeEnum.EMAIL)
            .build();
        
        entity.onCreate();
        LocalDateTime originalUpdatedAt = entity.getUpdatedAt();
        
        // Aguarda um milissegundo para garantir que o timestamp seja diferente
        Thread.sleep(1);

        // When
        entity.onUpdate();

        // Then
        assertNotNull(entity.getUpdatedAt());
        assertTrue(entity.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    @DisplayName("Deve permitir definir todos os campos")
    void shouldAllowSettingAllFields() {
        // Given
        String configuration = "{\"apiKey\":\"test-key\"}";

        // When
        providerJpaEntity.setId(testId);
        providerJpaEntity.setName("Custom Provider");
        providerJpaEntity.setProviderType(ProviderTypeEnum.SENDGRID);
        providerJpaEntity.setCommunicationType(CommunicationTypeEnum.SMS);
        providerJpaEntity.setIsActive(false);
        providerJpaEntity.setIsDefault(true);
        providerJpaEntity.setPriority(5);
        providerJpaEntity.setUrl("https://custom.example.com");
        providerJpaEntity.setConfiguration(configuration);
        providerJpaEntity.setMaxRetries(10);
        providerJpaEntity.setTimeoutSeconds(120);
        providerJpaEntity.setRateLimitPerMinute(500);
        providerJpaEntity.setDailyLimit(2000);
        providerJpaEntity.setMonthlyLimit(50000);
        providerJpaEntity.setCreatedAt(testDateTime);
        providerJpaEntity.setUpdatedAt(testDateTime);

        // Then
        assertEquals(testId, providerJpaEntity.getId());
        assertEquals("Custom Provider", providerJpaEntity.getName());
        assertEquals(ProviderTypeEnum.SENDGRID, providerJpaEntity.getProviderType());
        assertEquals(CommunicationTypeEnum.SMS, providerJpaEntity.getCommunicationType());
        assertFalse(providerJpaEntity.getIsActive());
        assertTrue(providerJpaEntity.getIsDefault());
        assertEquals(5, providerJpaEntity.getPriority());
        assertEquals("https://custom.example.com", providerJpaEntity.getUrl());
        assertEquals(configuration, providerJpaEntity.getConfiguration());
        assertEquals(10, providerJpaEntity.getMaxRetries());
        assertEquals(120, providerJpaEntity.getTimeoutSeconds());
        assertEquals(500, providerJpaEntity.getRateLimitPerMinute());
        assertEquals(2000, providerJpaEntity.getDailyLimit());
        assertEquals(50000, providerJpaEntity.getMonthlyLimit());
        assertEquals(testDateTime, providerJpaEntity.getCreatedAt());
        assertEquals(testDateTime, providerJpaEntity.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve permitir campos nulos")
    void shouldAllowNullFields() {
        // When
        providerJpaEntity.setId(null);
        providerJpaEntity.setName(null);
        providerJpaEntity.setProviderType(null);
        providerJpaEntity.setCommunicationType(null);
        providerJpaEntity.setUrl(null);
        providerJpaEntity.setConfiguration(null);
        providerJpaEntity.setRateLimitPerMinute(null);
        providerJpaEntity.setDailyLimit(null);
        providerJpaEntity.setMonthlyLimit(null);
        providerJpaEntity.setCreatedAt(null);
        providerJpaEntity.setUpdatedAt(null);

        // Then
        assertNull(providerJpaEntity.getId());
        assertNull(providerJpaEntity.getName());
        assertNull(providerJpaEntity.getProviderType());
        assertNull(providerJpaEntity.getCommunicationType());
        assertNull(providerJpaEntity.getUrl());
        assertNull(providerJpaEntity.getConfiguration());
        assertNull(providerJpaEntity.getRateLimitPerMinute());
        assertNull(providerJpaEntity.getDailyLimit());
        assertNull(providerJpaEntity.getMonthlyLimit());
        assertNull(providerJpaEntity.getCreatedAt());
        assertNull(providerJpaEntity.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve permitir configuração JSON complexa")
    void shouldAllowComplexJsonConfiguration() {
        // Given
        String complexConfiguration = """
            {
                "apiKey": "test-key-123",
                "baseUrl": "https://api.example.com",
                "timeout": 30000,
                "retryAttempts": 3,
                "headers": {
                    "Content-Type": "application/json",
                    "Authorization": "Bearer token"
                },
                "endpoints": {
                    "send": "/v1/messages/send",
                    "status": "/v1/messages/status"
                }
            }
            """;

        // When
        providerJpaEntity.setConfiguration(complexConfiguration);

        // Then
        assertEquals(complexConfiguration, providerJpaEntity.getConfiguration());
        assertTrue(providerJpaEntity.getConfiguration().contains("apiKey"));
        assertTrue(providerJpaEntity.getConfiguration().contains("baseUrl"));
        assertTrue(providerJpaEntity.getConfiguration().contains("endpoints"));
    }

    @Test
    @DisplayName("Deve testar toString")
    void shouldTestToString() {
        // Given
        providerJpaEntity.setId(testId);
        providerJpaEntity.setName("Test Provider");
        providerJpaEntity.setProviderType(ProviderTypeEnum.N8N);
        providerJpaEntity.setCommunicationType(CommunicationTypeEnum.EMAIL);
        providerJpaEntity.setIsActive(true);

        // When
        String result = providerJpaEntity.toString();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("ProviderJpaEntity"));
        assertTrue(result.contains(testId.toString()));
        assertTrue(result.contains("Test Provider"));
        assertTrue(result.contains("N8N"));
        assertTrue(result.contains("EMAIL"));
    }

    @Test
    @DisplayName("Deve testar equals e hashCode")
    void shouldTestEqualsAndHashCode() {
        // Given
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        
        ProviderJpaEntity entity1 = ProviderJpaEntity.builder()
            .id(id1)
            .name("Provider 1")
            .providerType(ProviderTypeEnum.N8N)
            .communicationType(CommunicationTypeEnum.EMAIL)
            .build();

        ProviderJpaEntity entity2 = ProviderJpaEntity.builder()
            .id(id1)
            .name("Provider 1")
            .providerType(ProviderTypeEnum.N8N)
            .communicationType(CommunicationTypeEnum.EMAIL)
            .build();

        ProviderJpaEntity entity3 = ProviderJpaEntity.builder()
            .id(id2)
            .name("Provider 2")
            .providerType(ProviderTypeEnum.SENDGRID)
            .communicationType(CommunicationTypeEnum.SMS)
            .build();

        // When & Then
        assertEquals(entity1, entity2);
        assertNotEquals(entity1, entity3);
        assertEquals(entity1.hashCode(), entity2.hashCode());
        assertNotEquals(entity1.hashCode(), entity3.hashCode());
    }

    @Test
    @DisplayName("Deve criar entidade com diferentes tipos de provider")
    void shouldCreateEntityWithDifferentProviderTypes() {
        // Given
        ProviderTypeEnum[] providerTypes = {
            ProviderTypeEnum.N8N,
            ProviderTypeEnum.SENDGRID
        };

        // When & Then
        for (ProviderTypeEnum type : providerTypes) {
            ProviderJpaEntity entity = ProviderJpaEntity.builder()
                .name("Provider " + type.name())
                .providerType(type)
                .communicationType(CommunicationTypeEnum.EMAIL)
                .build();

            assertNotNull(entity);
            assertEquals(type, entity.getProviderType());
        }
    }

    @Test
    @DisplayName("Deve criar entidade com diferentes tipos de comunicação")
    void shouldCreateEntityWithDifferentCommunicationTypes() {
        // Given
        CommunicationTypeEnum[] communicationTypes = {
            CommunicationTypeEnum.EMAIL,
            CommunicationTypeEnum.SMS,
            CommunicationTypeEnum.WHATSAPP,
            CommunicationTypeEnum.PUSH,
            CommunicationTypeEnum.SENDGRID
        };

        // When & Then
        for (CommunicationTypeEnum type : communicationTypes) {
            ProviderJpaEntity entity = ProviderJpaEntity.builder()
                .name("Provider for " + type.name())
                .providerType(ProviderTypeEnum.N8N)
                .communicationType(type)
                .build();

            assertNotNull(entity);
            assertEquals(type, entity.getCommunicationType());
        }
    }

    @Test
    @DisplayName("Deve permitir limites nulos")
    void shouldAllowNullLimits() {
        // When
        providerJpaEntity.setRateLimitPerMinute(null);
        providerJpaEntity.setDailyLimit(null);
        providerJpaEntity.setMonthlyLimit(null);

        // Then
        assertNull(providerJpaEntity.getRateLimitPerMinute());
        assertNull(providerJpaEntity.getDailyLimit());
        assertNull(providerJpaEntity.getMonthlyLimit());
    }

    @Test
    @DisplayName("Deve permitir valores zero para limites")
    void shouldAllowZeroValuesForLimits() {
        // When
        providerJpaEntity.setRateLimitPerMinute(0);
        providerJpaEntity.setDailyLimit(0);
        providerJpaEntity.setMonthlyLimit(0);
        providerJpaEntity.setMaxRetries(0);
        providerJpaEntity.setTimeoutSeconds(0);
        providerJpaEntity.setPriority(0);

        // Then
        assertEquals(0, providerJpaEntity.getRateLimitPerMinute());
        assertEquals(0, providerJpaEntity.getDailyLimit());
        assertEquals(0, providerJpaEntity.getMonthlyLimit());
        assertEquals(0, providerJpaEntity.getMaxRetries());
        assertEquals(0, providerJpaEntity.getTimeoutSeconds());
        assertEquals(0, providerJpaEntity.getPriority());
    }

    @Test
    @DisplayName("Deve permitir valores negativos para limites")
    void shouldAllowNegativeValuesForLimits() {
        // When
        providerJpaEntity.setRateLimitPerMinute(-1);
        providerJpaEntity.setDailyLimit(-100);
        providerJpaEntity.setMonthlyLimit(-1000);
        providerJpaEntity.setMaxRetries(-1);
        providerJpaEntity.setTimeoutSeconds(-10);
        providerJpaEntity.setPriority(-5);

        // Then
        assertEquals(-1, providerJpaEntity.getRateLimitPerMinute());
        assertEquals(-100, providerJpaEntity.getDailyLimit());
        assertEquals(-1000, providerJpaEntity.getMonthlyLimit());
        assertEquals(-1, providerJpaEntity.getMaxRetries());
        assertEquals(-10, providerJpaEntity.getTimeoutSeconds());
        assertEquals(-5, providerJpaEntity.getPriority());
    }
}
