package com.keepguard.ms_communication.infrastructure.persistence.mapper;

import com.keepguard.ms_communication.domain.entity.Provider;
import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.domain.enums.ProviderTypeEnum;
import com.keepguard.ms_communication.infrastructure.persistence.entity.ProviderJpaEntity;
import com.keepguard.ms_communication.test.builder.ProviderTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para ProviderJpaMapper
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Provider JPA Mapper Tests")
class ProviderJpaMapperTest {
    
    @InjectMocks
    private ProviderJpaMapper providerJpaMapper;
    
    private Provider provider;
    private ProviderJpaEntity providerJpaEntity;
    private UUID providerId;
    private LocalDateTime now;
    
    @BeforeEach
    void setUp() {
        providerId = UUID.randomUUID();
        now = LocalDateTime.now();
        
        provider = ProviderTestBuilder.aProvider()
            .withId(providerId)
            .withName("Test Provider")
            .withProviderType(ProviderTypeEnum.N8N)
            .withCommunicationType(CommunicationTypeEnum.EMAIL)
            .withIsActive(true)
            .withIsDefault(false)
            .withPriority(1)
            .withUrl("https://api.example.com")
            .withMaxRetries(3)
            .withTimeoutSeconds(30)
            .withRateLimitPerMinute(100)
            .withDailyLimit(1000)
            .withMonthlyLimit(30000)
            .withCreatedAt(now)
            .withUpdatedAt(now)
            .buildDomain();
        
        String configuration = "{\"apiKey\":\"test-api-key\",\"endpoint\":\"https://api.example.com\"}";
        provider.setConfiguration(configuration);
        
        providerJpaEntity = ProviderJpaEntity.builder()
            .id(providerId)
            .name("Test Provider")
            .providerType(ProviderTypeEnum.N8N)
            .communicationType(CommunicationTypeEnum.EMAIL)
            .isActive(true)
            .isDefault(false)
            .priority(1)
            .url("https://api.example.com")
            .configuration(configuration)
            .maxRetries(3)
            .timeoutSeconds(30)
            .rateLimitPerMinute(100)
            .dailyLimit(1000)
            .monthlyLimit(30000)
            .createdAt(now)
            .updatedAt(now)
            .build();
    }
    
    @Test
    @DisplayName("Deve converter Provider para ProviderJpaEntity com sucesso")
    void shouldConvertProviderToEntitySuccessfully() {
        // When
        ProviderJpaEntity result = providerJpaMapper.toEntity(provider);
        
        // Then
        assertNotNull(result);
        assertEquals(provider.getId(), result.getId());
        assertEquals(provider.getName(), result.getName());
        assertEquals(provider.getProviderType(), result.getProviderType());
        assertEquals(provider.getCommunicationType(), result.getCommunicationType());
        assertEquals(provider.getIsActive(), result.getIsActive());
        assertEquals(provider.getIsDefault(), result.getIsDefault());
        assertEquals(provider.getPriority(), result.getPriority());
        assertEquals(provider.getUrl(), result.getUrl());
        assertEquals(provider.getConfiguration(), result.getConfiguration());
        assertEquals(provider.getMaxRetries(), result.getMaxRetries());
        assertEquals(provider.getTimeoutSeconds(), result.getTimeoutSeconds());
        assertEquals(provider.getRateLimitPerMinute(), result.getRateLimitPerMinute());
        assertEquals(provider.getDailyLimit(), result.getDailyLimit());
        assertEquals(provider.getMonthlyLimit(), result.getMonthlyLimit());
        assertEquals(provider.getCreatedAt(), result.getCreatedAt());
        assertEquals(provider.getUpdatedAt(), result.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Deve converter ProviderJpaEntity para Provider com sucesso")
    void shouldConvertEntityToProviderSuccessfully() {
        // When
        Provider result = providerJpaMapper.toDomain(providerJpaEntity);
        
        // Then
        assertNotNull(result);
        assertEquals(providerJpaEntity.getId(), result.getId());
        assertEquals(providerJpaEntity.getName(), result.getName());
        assertEquals(providerJpaEntity.getProviderType(), result.getProviderType());
        assertEquals(providerJpaEntity.getCommunicationType(), result.getCommunicationType());
        assertEquals(providerJpaEntity.getIsActive(), result.getIsActive());
        assertEquals(providerJpaEntity.getIsDefault(), result.getIsDefault());
        assertEquals(providerJpaEntity.getPriority(), result.getPriority());
        assertEquals(providerJpaEntity.getUrl(), result.getUrl());
        assertEquals(providerJpaEntity.getConfiguration(), result.getConfiguration());
        assertEquals(providerJpaEntity.getMaxRetries(), result.getMaxRetries());
        assertEquals(providerJpaEntity.getTimeoutSeconds(), result.getTimeoutSeconds());
        assertEquals(providerJpaEntity.getRateLimitPerMinute(), result.getRateLimitPerMinute());
        assertEquals(providerJpaEntity.getDailyLimit(), result.getDailyLimit());
        assertEquals(providerJpaEntity.getMonthlyLimit(), result.getMonthlyLimit());
        assertEquals(providerJpaEntity.getCreatedAt(), result.getCreatedAt());
        assertEquals(providerJpaEntity.getUpdatedAt(), result.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Deve retornar null quando Provider é null")
    void shouldReturnNullWhenProviderIsNull() {
        // When
        ProviderJpaEntity result = providerJpaMapper.toEntity(null);
        
        // Then
        assertNull(result);
    }
    
    @Test
    @DisplayName("Deve retornar null quando ProviderJpaEntity é null")
    void shouldReturnNullWhenEntityIsNull() {
        // When
        Provider result = providerJpaMapper.toDomain(null);
        
        // Then
        assertNull(result);
    }
    
    @Test
    @DisplayName("Deve converter Provider com diferentes tipos de provider")
    void shouldConvertProviderWithDifferentProviderTypes() {
        // Given
        ProviderTypeEnum[] providerTypes = {
            ProviderTypeEnum.N8N,
            ProviderTypeEnum.SENDGRID
        };
        
        for (ProviderTypeEnum providerType : providerTypes) {
            Provider testProvider = ProviderTestBuilder.aProvider()
                .withProviderType(providerType)
                .buildDomain();
            
            // When
            ProviderJpaEntity result = providerJpaMapper.toEntity(testProvider);
            
            // Then
            assertNotNull(result);
            assertEquals(providerType, result.getProviderType());
        }
    }
    
    @Test
    @DisplayName("Deve converter Provider com diferentes tipos de comunicação")
    void shouldConvertProviderWithDifferentCommunicationTypes() {
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
            
            // When
            ProviderJpaEntity result = providerJpaMapper.toEntity(testProvider);
            
            // Then
            assertNotNull(result);
            assertEquals(communicationType, result.getCommunicationType());
        }
    }
    
    @Test
    @DisplayName("Deve converter Provider com diferentes estados ativo")
    void shouldConvertProviderWithDifferentActiveStates() {
        // Given
        boolean[] activeStates = {true, false};
        
        for (boolean isActive : activeStates) {
            Provider testProvider = ProviderTestBuilder.aProvider()
                .withIsActive(isActive)
                .buildDomain();
            
            // When
            ProviderJpaEntity result = providerJpaMapper.toEntity(testProvider);
            
            // Then
            assertNotNull(result);
            assertEquals(isActive, result.getIsActive());
        }
    }
    
    @Test
    @DisplayName("Deve converter Provider com diferentes estados padrão")
    void shouldConvertProviderWithDifferentDefaultStates() {
        // Given
        boolean[] defaultStates = {true, false};
        
        for (boolean isDefault : defaultStates) {
            Provider testProvider = ProviderTestBuilder.aProvider()
                .withIsDefault(isDefault)
                .buildDomain();
            
            // When
            ProviderJpaEntity result = providerJpaMapper.toEntity(testProvider);
            
            // Then
            assertNotNull(result);
            assertEquals(isDefault, result.getIsDefault());
        }
    }
    
    @Test
    @DisplayName("Deve converter Provider com diferentes prioridades")
    void shouldConvertProviderWithDifferentPriorities() {
        // Given
        int[] priorities = {1, 2, 3, 10, 100};
        
        for (int priority : priorities) {
            Provider testProvider = ProviderTestBuilder.aProvider()
                .withPriority(priority)
                .buildDomain();
            
            // When
            ProviderJpaEntity result = providerJpaMapper.toEntity(testProvider);
            
            // Then
            assertNotNull(result);
            assertEquals(priority, result.getPriority());
        }
    }
    
    @Test
    @DisplayName("Deve converter Provider com diferentes URLs")
    void shouldConvertProviderWithDifferentUrls() {
        // Given
        String[] urls = {
            "https://api.example.com",
            "https://api.sendgrid.com",
            "https://api.whatsapp.com",
            "https://api.sms.com",
            null
        };
        
        for (String url : urls) {
            Provider testProvider = ProviderTestBuilder.aProvider()
                .withUrl(url)
                .buildDomain();
            
            // When
            ProviderJpaEntity result = providerJpaMapper.toEntity(testProvider);
            
            // Then
            assertNotNull(result);
            assertEquals(url, result.getUrl());
        }
    }
    
    @Test
    @DisplayName("Deve converter Provider com diferentes configurações")
    void shouldConvertProviderWithDifferentConfigurations() {
        // Given
        String[] configurations = {
            "{\"apiKey\":\"test-key-1\",\"endpoint\":\"https://api1.example.com\"}",
            "{\"username\":\"test-user\",\"password\":\"test-pass\",\"port\":587}",
            "{}",
            null
        };
        
        for (String configuration : configurations) {
            Provider testProvider = ProviderTestBuilder.aProvider()
                .buildDomain();
            testProvider.setConfiguration(configuration);
            
            // When
            ProviderJpaEntity result = providerJpaMapper.toEntity(testProvider);
            
            // Then
            assertNotNull(result);
            assertEquals(configuration, result.getConfiguration());
        }
    }
    
    @Test
    @DisplayName("Deve converter Provider com diferentes limites de retry")
    void shouldConvertProviderWithDifferentMaxRetries() {
        // Given
        int[] maxRetries = {1, 3, 5, 10, 0};
        
        for (int maxRetry : maxRetries) {
            Provider testProvider = ProviderTestBuilder.aProvider()
                .withMaxRetries(maxRetry)
                .buildDomain();
            
            // When
            ProviderJpaEntity result = providerJpaMapper.toEntity(testProvider);
            
            // Then
            assertNotNull(result);
            assertEquals(maxRetry, result.getMaxRetries());
        }
    }
    
    @Test
    @DisplayName("Deve converter Provider com diferentes timeouts")
    void shouldConvertProviderWithDifferentTimeouts() {
        // Given
        int[] timeouts = {5, 10, 30, 60, 120};
        
        for (int timeout : timeouts) {
            Provider testProvider = ProviderTestBuilder.aProvider()
                .withTimeoutSeconds(timeout)
                .buildDomain();
            
            // When
            ProviderJpaEntity result = providerJpaMapper.toEntity(testProvider);
            
            // Then
            assertNotNull(result);
            assertEquals(timeout, result.getTimeoutSeconds());
        }
    }
    
    @Test
    @DisplayName("Deve converter Provider com diferentes rate limits")
    void shouldConvertProviderWithDifferentRateLimits() {
        // Given
        int[] rateLimits = {10, 50, 100, 500, 1000};
        
        for (int rateLimit : rateLimits) {
            Provider testProvider = ProviderTestBuilder.aProvider()
                .withRateLimitPerMinute(rateLimit)
                .buildDomain();
            
            // When
            ProviderJpaEntity result = providerJpaMapper.toEntity(testProvider);
            
            // Then
            assertNotNull(result);
            assertEquals(rateLimit, result.getRateLimitPerMinute());
        }
    }
    
    @Test
    @DisplayName("Deve converter Provider com diferentes limites diários")
    void shouldConvertProviderWithDifferentDailyLimits() {
        // Given
        int[] dailyLimits = {100, 500, 1000, 5000, 10000};
        
        for (int dailyLimit : dailyLimits) {
            Provider testProvider = ProviderTestBuilder.aProvider()
                .withDailyLimit(dailyLimit)
                .buildDomain();
            
            // When
            ProviderJpaEntity result = providerJpaMapper.toEntity(testProvider);
            
            // Then
            assertNotNull(result);
            assertEquals(dailyLimit, result.getDailyLimit());
        }
    }
    
    @Test
    @DisplayName("Deve converter Provider com diferentes limites mensais")
    void shouldConvertProviderWithDifferentMonthlyLimits() {
        // Given
        int[] monthlyLimits = {1000, 5000, 10000, 50000, 100000};
        
        for (int monthlyLimit : monthlyLimits) {
            Provider testProvider = ProviderTestBuilder.aProvider()
                .withMonthlyLimit(monthlyLimit)
                .buildDomain();
            
            // When
            ProviderJpaEntity result = providerJpaMapper.toEntity(testProvider);
            
            // Then
            assertNotNull(result);
            assertEquals(monthlyLimit, result.getMonthlyLimit());
        }
    }
    
    @Test
    @DisplayName("Deve converter Provider com diferentes timestamps")
    void shouldConvertProviderWithDifferentTimestamps() {
        // Given
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();
        
        Provider testProvider = ProviderTestBuilder.aProvider()
            .withCreatedAt(createdAt)
            .withUpdatedAt(updatedAt)
            .buildDomain();
        
        // When
        ProviderJpaEntity result = providerJpaMapper.toEntity(testProvider);
        
        // Then
        assertNotNull(result);
        assertEquals(createdAt, result.getCreatedAt());
        assertEquals(updatedAt, result.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Deve converter Provider com todos os campos nulos")
    void shouldConvertProviderWithAllNullFields() {
        // Given
        Provider testProvider = new Provider(
            providerId,
            null,
            null,
            null,
            false,
            false,
            0,
            null,
            null,
            0,
            0,
            0,
            0,
            0,
            null,
            null,
            null
        );
        
        // When
        ProviderJpaEntity result = providerJpaMapper.toEntity(testProvider);
        
        // Then
        assertNotNull(result);
        assertNull(result.getName());
        assertNull(result.getProviderType());
        assertNull(result.getCommunicationType());
        assertFalse(result.getIsActive());
        assertFalse(result.getIsDefault());
        assertEquals(0, result.getPriority());
        assertNull(result.getUrl());
        assertNull(result.getConfiguration());
        assertEquals(0, result.getMaxRetries());
        assertEquals(0, result.getTimeoutSeconds());
        assertEquals(0, result.getRateLimitPerMinute());
        assertEquals(0, result.getDailyLimit());
        assertEquals(0, result.getMonthlyLimit());
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Deve converter ProviderJpaEntity com todos os campos nulos")
    void shouldConvertEntityWithAllNullFields() {
        // Given
        ProviderJpaEntity testEntity = ProviderJpaEntity.builder()
            .id(providerId)
            .name(null)
            .providerType(null)
            .communicationType(null)
            .isActive(false)
            .isDefault(false)
            .priority(0)
            .url(null)
            .configuration(null)
            .maxRetries(0)
            .timeoutSeconds(0)
            .rateLimitPerMinute(0)
            .dailyLimit(0)
            .monthlyLimit(0)
            .createdAt(null)
            .updatedAt(null)
            .build();
        
        // When
        Provider result = providerJpaMapper.toDomain(testEntity);
        
        // Then
        assertNotNull(result);
        assertNull(result.getName());
        assertNull(result.getProviderType());
        assertNull(result.getCommunicationType());
        assertFalse(result.getIsActive());
        assertFalse(result.getIsDefault());
        assertEquals(0, result.getPriority());
        assertNull(result.getUrl());
        assertNull(result.getConfiguration());
        assertEquals(0, result.getMaxRetries());
        assertEquals(0, result.getTimeoutSeconds());
        assertEquals(0, result.getRateLimitPerMinute());
        assertEquals(0, result.getDailyLimit());
        assertEquals(0, result.getMonthlyLimit());
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
    }
}
