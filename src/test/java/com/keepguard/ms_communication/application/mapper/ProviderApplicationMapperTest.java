package com.keepguard.ms_communication.application.mapper;

import com.keepguard.ms_communication.adapters.in.rest.provider.dto.request.ProviderCreateRequestDTO;
import com.keepguard.ms_communication.adapters.in.rest.provider.dto.request.ProviderUpdateRequestDTO;
import com.keepguard.ms_communication.domain.dto.provider.ProviderCreateCommandDTO;
import com.keepguard.ms_communication.domain.dto.provider.ProviderUpdateCommandDTO;
import com.keepguard.ms_communication.application.dto.provider.ProviderView;
import com.keepguard.ms_communication.domain.dto.provider.ProviderCreateCommandDTO;
import com.keepguard.ms_communication.domain.dto.provider.ProviderUpdateCommandDTO;
import com.keepguard.ms_communication.domain.entity.Provider;
import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.domain.enums.ProviderTypeEnum;
import com.keepguard.ms_communication.test.builder.ProviderTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProviderApplicationMapper Tests")
class ProviderApplicationMapperTest {

    @InjectMocks
    private ProviderApplicationMapper providerMapper;

    private ProviderCreateRequestDTO providerCreateRequestDTO;
    private ProviderUpdateRequestDTO providerUpdateRequestDTO;
    private ProviderView providerView;
    private Provider domainProvider;
    private UUID providerId;
    private UUID xApplicationUuid;
    private ProviderCreateCommandDTO providerCreateCommandDTO;
    private ProviderUpdateCommandDTO providerUpdateCommandDTO;
    private Provider provider;

    @BeforeEach
    void setUp() {
        providerId = UUID.randomUUID();
        xApplicationUuid = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // Setup ProviderCreateDTO
        providerCreateRequestDTO = new ProviderCreateRequestDTO();
        providerCreateRequestDTO.setName("Test Provider");
        providerCreateRequestDTO.setProviderType(ProviderTypeEnum.N8N);
        providerCreateRequestDTO.setCommunicationType(CommunicationTypeEnum.EMAIL);
        providerCreateRequestDTO.setIsActive(true);
        providerCreateRequestDTO.setIsDefault(false);
        providerCreateRequestDTO.setPriority(1);
        providerCreateRequestDTO.setUrl("https://test.com");
        providerCreateRequestDTO.setConfiguration("{\"apiKey\":\"test\"}");
        providerCreateRequestDTO.setMaxRetries(3);
        providerCreateRequestDTO.setTimeoutSeconds(30);
        providerCreateRequestDTO.setRateLimitPerMinute(100);
        providerCreateRequestDTO.setDailyLimit(1000);
        providerCreateRequestDTO.setMonthlyLimit(30000);

        // Setup ProviderUpdateDTO
        providerUpdateRequestDTO = new ProviderUpdateRequestDTO();
        providerUpdateRequestDTO.setName("Updated Provider");
        providerUpdateRequestDTO.setProviderType(ProviderTypeEnum.SENDGRID);
        providerUpdateRequestDTO.setCommunicationType(CommunicationTypeEnum.SMS);
        providerUpdateRequestDTO.setIsActive(false);
        providerUpdateRequestDTO.setIsDefault(true);
        providerUpdateRequestDTO.setPriority(2);
        providerUpdateRequestDTO.setUrl("https://updated.com");
        providerUpdateRequestDTO.setConfiguration("{\"apiKey\":\"updated\"}");
        providerUpdateRequestDTO.setMaxRetries(5);
        providerUpdateRequestDTO.setTimeoutSeconds(60);
        providerUpdateRequestDTO.setRateLimitPerMinute(200);
        providerUpdateRequestDTO.setDailyLimit(2000);
        providerUpdateRequestDTO.setMonthlyLimit(60000);

        // Setup ProviderView
        providerView = new ProviderView(
                providerId,
                "Test Provider",
                ProviderTypeEnum.N8N,
                CommunicationTypeEnum.EMAIL,
                true,
                false,
                1,
                "https://test.com",
                "{\"apiKey\":\"test\"}",
                3,
                30,
                100,
                1000,
                30000,
                now,
                now
        );

        // Setup domain Provider
        domainProvider = Provider.create(
                "Test Provider",
                ProviderTypeEnum.N8N,
                CommunicationTypeEnum.EMAIL,
                "https://test.com",
                "{\"apiKey\":\"test\"}"
        );
        domainProvider.setId(providerId);
        domainProvider.setIsActive(true);
        domainProvider.setIsDefault(false);
        domainProvider.setPriority(1);
        domainProvider.setMaxRetries(3);
        domainProvider.setTimeoutSeconds(30);
        domainProvider.setRateLimitPerMinute(100);
        domainProvider.setDailyLimit(1000);
        domainProvider.setMonthlyLimit(30000);
        domainProvider.setCreatedAt(now);
        domainProvider.setUpdatedAt(now);
        
        // Setup ProviderCreateCommandDTO
        providerCreateCommandDTO = ProviderCreateCommandDTO.builder()
                .xApplicationUuid(xApplicationUuid)
                .name("Test Provider")
                .providerType(ProviderTypeEnum.N8N)
                .communicationType(CommunicationTypeEnum.EMAIL)
                .isActive(true)
                .isDefault(false)
                .priority(1)
                .url("https://test.com")
                .configuration("{\"apiKey\":\"test\"}")
                .maxRetries(3)
                .timeoutSeconds(30)
                .rateLimitPerMinute(100)
                .dailyLimit(1000)
                .monthlyLimit(30000)
                .build();
        
        // Setup ProviderUpdateCommandDTO
        providerUpdateCommandDTO = ProviderUpdateCommandDTO.builder()
                .id(providerId)
                .xApplicationUuid(xApplicationUuid)
                .name("Updated Provider")
                .providerType(ProviderTypeEnum.SENDGRID)
                .communicationType(CommunicationTypeEnum.EMAIL)
                .isActive(false)
                .isDefault(true)
                .priority(2)
                .url("https://updated.com")
                .configuration("{\"apiKey\":\"updated\"}")
                .maxRetries(5)
                .timeoutSeconds(60)
                .rateLimitPerMinute(120)
                .dailyLimit(2000)
                .monthlyLimit(60000)
                .build();
        
        // Setup Provider
        provider = Provider.create(
                "Test Provider",
                ProviderTypeEnum.N8N,
                CommunicationTypeEnum.EMAIL,
                "https://test.com",
                "{\"apiKey\":\"test\"}"
        );
        provider.setId(providerId);
        provider.setIsActive(true);
        provider.setIsDefault(false);
        provider.setPriority(1);
        provider.setMaxRetries(3);
        provider.setTimeoutSeconds(30);
        provider.setRateLimitPerMinute(100);
        provider.setDailyLimit(1000);
        provider.setMonthlyLimit(30000);
        provider.setCreatedAt(now);
        provider.setUpdatedAt(now);
    }

    @Test
    @DisplayName("Should convert ProviderCreateDTO to ProviderCreateCommandDTO successfully")
    void shouldConvertProviderCreateDTOToCommandSuccessfully() {
        // When
        ProviderCreateCommandDTO result = providerMapper.toCreateCommand(providerCreateCommandDTO);

        // Then
        assertNotNull(result);
        assertEquals(providerCreateCommandDTO.getXApplicationUuid(), result.getXApplicationUuid());
        assertEquals(providerCreateCommandDTO.getName(), result.getName());
        assertEquals(providerCreateCommandDTO.getProviderType(), result.getProviderType());
        assertEquals(providerCreateCommandDTO.getCommunicationType(), result.getCommunicationType());
        assertEquals(providerCreateCommandDTO.getIsActive(), result.getIsActive());
        assertEquals(providerCreateCommandDTO.getIsDefault(), result.getIsDefault());
        assertEquals(providerCreateCommandDTO.getPriority(), result.getPriority());
        assertEquals(providerCreateCommandDTO.getUrl(), result.getUrl());
        assertEquals(providerCreateCommandDTO.getConfiguration(), result.getConfiguration());
        assertEquals(providerCreateCommandDTO.getMaxRetries(), result.getMaxRetries());
        assertEquals(providerCreateCommandDTO.getTimeoutSeconds(), result.getTimeoutSeconds());
        assertEquals(providerCreateCommandDTO.getRateLimitPerMinute(), result.getRateLimitPerMinute());
        assertEquals(providerCreateCommandDTO.getDailyLimit(), result.getDailyLimit());
        assertEquals(providerCreateCommandDTO.getMonthlyLimit(), result.getMonthlyLimit());
    }

    @Test
    @DisplayName("Should return null when ProviderCreateDTO is null")
    void shouldReturnNullWhenProviderCreateDTOIsNull() {
        // When
        ProviderCreateCommandDTO result = providerMapper.toCreateCommand(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should convert ProviderUpdateDTO to ProviderUpdateCommandDTO successfully")
    void shouldConvertProviderUpdateDTOToCommandSuccessfully() {
        // When
        ProviderUpdateCommandDTO result = providerMapper.toUpdateCommand(providerUpdateCommandDTO);

        // Then
        assertNotNull(result);
        assertEquals(providerId, result.getId());
        assertEquals(xApplicationUuid, result.getXApplicationUuid());
        assertEquals(providerUpdateCommandDTO.getName(), result.getName());
        assertEquals(providerUpdateCommandDTO.getProviderType(), result.getProviderType());
        assertEquals(providerUpdateCommandDTO.getCommunicationType(), result.getCommunicationType());
        assertEquals(providerUpdateCommandDTO.getIsActive(), result.getIsActive());
        assertEquals(providerUpdateCommandDTO.getIsDefault(), result.getIsDefault());
        assertEquals(providerUpdateCommandDTO.getPriority(), result.getPriority());
        assertEquals(providerUpdateCommandDTO.getUrl(), result.getUrl());
        assertEquals(providerUpdateCommandDTO.getConfiguration(), result.getConfiguration());
        assertEquals(providerUpdateCommandDTO.getMaxRetries(), result.getMaxRetries());
        assertEquals(providerUpdateCommandDTO.getTimeoutSeconds(), result.getTimeoutSeconds());
        assertEquals(providerUpdateCommandDTO.getRateLimitPerMinute(), result.getRateLimitPerMinute());
        assertEquals(providerUpdateCommandDTO.getDailyLimit(), result.getDailyLimit());
        assertEquals(providerUpdateCommandDTO.getMonthlyLimit(), result.getMonthlyLimit());
    }

    @Test
    @DisplayName("Should return null when ProviderUpdateDTO is null")
    void shouldReturnNullWhenProviderUpdateDTOIsNull() {
        // When
        ProviderUpdateCommandDTO result = providerMapper.toUpdateCommand(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should convert ProviderViewDTO to ProviderResponseDTO successfully")
    void shouldConvertProviderViewToResponseDTOSuccessfully() {
        // When
        ProviderView result = providerMapper.toView(provider);

        // Then
        assertNotNull(result);
        assertEquals(providerView.id(), result.id());
        assertEquals(providerView.name(), result.name());
        assertEquals(providerView.providerType(), result.providerType());
        assertEquals(providerView.communicationType(), result.communicationType());
        assertEquals(providerView.isActive(), result.isActive());
        assertEquals(providerView.isDefault(), result.isDefault());
        assertEquals(providerView.priority(), result.priority());
        assertEquals(providerView.url(), result.url());
        assertEquals(providerView.configuration(), result.configuration());
        assertEquals(providerView.maxRetries(), result.maxRetries());
        assertEquals(providerView.timeoutSeconds(), result.timeoutSeconds());
        assertEquals(providerView.rateLimitPerMinute(), result.rateLimitPerMinute());
        assertEquals(providerView.dailyLimit(), result.dailyLimit());
        assertEquals(providerView.monthlyLimit(), result.monthlyLimit());
        assertEquals(providerView.createdAt(), result.createdAt());
        assertEquals(providerView.updatedAt(), result.updatedAt());
    }

    @Test
    @DisplayName("Should return null when ProviderViewDTO is null")
    void shouldReturnNullWhenProviderViewIsNull() {
        // When
        ProviderView result = providerMapper.toView(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should convert ProviderCreateCommandDTO to Provider domain successfully")
    void shouldConvertProviderCreateCommandToDomainSuccessfully() {
        // Given
        ProviderCreateCommandDTO command = ProviderCreateCommandDTO.builder()
                .xApplicationUuid(xApplicationUuid)
                .name("Test Provider")
                .providerType(ProviderTypeEnum.N8N)
                .communicationType(CommunicationTypeEnum.EMAIL)
                .isActive(true)
                .isDefault(false)
                .priority(1)
                .url("https://test.com")
                .configuration("{\"apiKey\":\"test\"}")
                .maxRetries(3)
                .timeoutSeconds(30)
                .rateLimitPerMinute(100)
                .dailyLimit(1000)
                .monthlyLimit(30000)
                .build();

        // When
        Provider result = providerMapper.toDomain(command);

        // Then
        assertNotNull(result);
        assertEquals(command.getName(), result.getName());
        assertEquals(command.getProviderType(), result.getProviderType());
        assertEquals(command.getCommunicationType(), result.getCommunicationType());
        assertEquals(command.getUrl(), result.getUrl());
        assertEquals(command.getConfiguration(), result.getConfiguration());
        assertEquals(command.getIsActive(), result.getIsActive());
        assertEquals(command.getIsDefault(), result.getIsDefault());
        assertEquals(command.getPriority(), result.getPriority());
        assertEquals(command.getMaxRetries(), result.getMaxRetries());
        assertEquals(command.getTimeoutSeconds(), result.getTimeoutSeconds());
        assertEquals(command.getRateLimitPerMinute(), result.getRateLimitPerMinute());
        assertEquals(command.getDailyLimit(), result.getDailyLimit());
        assertEquals(command.getMonthlyLimit(), result.getMonthlyLimit());
    }

    @Test
    @DisplayName("Should return null when ProviderCreateCommandDTO is null")
    void shouldReturnNullWhenProviderCreateCommandIsNull() {
        // When
        Provider result = providerMapper.toDomain((ProviderCreateCommandDTO) null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should convert ProviderCreateCommandDTO with null optional fields successfully")
    void shouldConvertProviderCreateCommandWithNullOptionalFieldsSuccessfully() {
        // Given
        ProviderCreateCommandDTO command = ProviderCreateCommandDTO.builder()
                .xApplicationUuid(xApplicationUuid)
                .name("Test Provider")
                .providerType(ProviderTypeEnum.N8N)
                .communicationType(CommunicationTypeEnum.EMAIL)
                .url("https://test.com")
                .configuration("{\"apiKey\":\"test\"}")
                .isActive(null)
                .isDefault(null)
                .priority(null)
                .maxRetries(null)
                .timeoutSeconds(null)
                .rateLimitPerMinute(null)
                .dailyLimit(null)
                .monthlyLimit(null)
                .build();

        // When
        Provider result = providerMapper.toDomain(command);

        // Then
        assertNotNull(result);
        assertEquals(command.getName(), result.getName());
        assertEquals(command.getProviderType(), result.getProviderType());
        assertEquals(command.getCommunicationType(), result.getCommunicationType());
        assertEquals(command.getUrl(), result.getUrl());
        assertEquals(command.getConfiguration(), result.getConfiguration());
        // Optional fields should have default values when not provided
        assertTrue(result.getIsActive()); // default true
        assertFalse(result.getIsDefault()); // default false
        assertEquals(1, result.getPriority()); // default 1
        assertEquals(3, result.getMaxRetries()); // default 3
        assertEquals(30, result.getTimeoutSeconds()); // default 30
        assertNull(result.getRateLimitPerMinute()); // no default
        assertNull(result.getDailyLimit()); // no default
        assertNull(result.getMonthlyLimit()); // no default
    }

    @Test
    @DisplayName("Should convert ProviderUpdateCommandDTO to Provider domain successfully")
    void shouldConvertProviderUpdateCommandToDomainSuccessfully() {
        // Given
        ProviderUpdateCommandDTO command = ProviderUpdateCommandDTO.builder()
                .id(providerId)
                .xApplicationUuid(xApplicationUuid)
                .name("Updated Provider")
                .providerType(ProviderTypeEnum.SENDGRID)
                .communicationType(CommunicationTypeEnum.SMS)
                .isActive(false)
                .isDefault(true)
                .priority(2)
                .url("https://updated.com")
                .configuration("{\"apiKey\":\"updated\"}")
                .maxRetries(5)
                .timeoutSeconds(60)
                .rateLimitPerMinute(200)
                .dailyLimit(2000)
                .monthlyLimit(60000)
                .build();

        // When
        Provider result = providerMapper.toDomain(command, domainProvider);

        // Then
        assertNotNull(result);
        assertEquals(command.getId(), result.getId());
        assertEquals(command.getName(), result.getName());
        assertEquals(command.getProviderType(), result.getProviderType());
        assertEquals(command.getCommunicationType(), result.getCommunicationType());
        assertEquals(command.getIsActive(), result.getIsActive());
        assertEquals(command.getIsDefault(), result.getIsDefault());
        assertEquals(command.getPriority(), result.getPriority());
        assertEquals(command.getUrl(), result.getUrl());
        assertEquals(command.getConfiguration(), result.getConfiguration());
        assertEquals(command.getMaxRetries(), result.getMaxRetries());
        assertEquals(command.getTimeoutSeconds(), result.getTimeoutSeconds());
        assertEquals(command.getRateLimitPerMinute(), result.getRateLimitPerMinute());
        assertEquals(command.getDailyLimit(), result.getDailyLimit());
        assertEquals(command.getMonthlyLimit(), result.getMonthlyLimit());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    @DisplayName("Should return null when ProviderUpdateCommandDTO is null")
    void shouldReturnNullWhenProviderUpdateCommandIsNull() {
        // When
        Provider result = providerMapper.toDomain(null, domainProvider);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should return null when existing Provider is null")
    void shouldReturnNullWhenExistingProviderIsNull() {
        // Given
        ProviderUpdateCommandDTO command = ProviderUpdateCommandDTO.builder()
                .id(providerId)
                .xApplicationUuid(xApplicationUuid)
                .name("Updated Provider")
                .providerType(ProviderTypeEnum.SENDGRID)
                .communicationType(CommunicationTypeEnum.SMS)
                .isActive(false)
                .isDefault(true)
                .priority(2)
                .url("https://updated.com")
                .configuration("{\"apiKey\":\"updated\"}")
                .maxRetries(5)
                .timeoutSeconds(60)
                .rateLimitPerMinute(200)
                .dailyLimit(2000)
                .monthlyLimit(60000)
                .build();

        // When
        Provider result = providerMapper.toDomain(command, null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should convert ProviderUpdateCommandDTO with null optional fields successfully")
    void shouldConvertProviderUpdateCommandWithNullOptionalFieldsSuccessfully() {
        // Given
        ProviderUpdateCommandDTO command = ProviderUpdateCommandDTO.builder()
                .id(providerId)
                .xApplicationUuid(xApplicationUuid)
                .name(null)
                .providerType(null)
                .communicationType(null)
                .isActive(null)
                .isDefault(null)
                .priority(null)
                .url(null)
                .configuration(null)
                .maxRetries(null)
                .timeoutSeconds(null)
                .rateLimitPerMinute(null)
                .dailyLimit(null)
                .monthlyLimit(null)
                .build();

        // When
        Provider result = providerMapper.toDomain(command, domainProvider);

        // Then
        assertNotNull(result);
        assertEquals(providerId, result.getId());
        // Original values should be preserved for null fields
        assertEquals(domainProvider.getName(), result.getName());
        assertEquals(domainProvider.getProviderType(), result.getProviderType());
        assertEquals(domainProvider.getCommunicationType(), result.getCommunicationType());
        assertEquals(domainProvider.getIsActive(), result.getIsActive());
        assertEquals(domainProvider.getIsDefault(), result.getIsDefault());
        assertEquals(domainProvider.getPriority(), result.getPriority());
        assertEquals(domainProvider.getUrl(), result.getUrl());
        assertEquals(domainProvider.getConfiguration(), result.getConfiguration());
        assertEquals(domainProvider.getMaxRetries(), result.getMaxRetries());
        assertEquals(domainProvider.getTimeoutSeconds(), result.getTimeoutSeconds());
        assertEquals(domainProvider.getRateLimitPerMinute(), result.getRateLimitPerMinute());
        assertEquals(domainProvider.getDailyLimit(), result.getDailyLimit());
        assertEquals(domainProvider.getMonthlyLimit(), result.getMonthlyLimit());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    @DisplayName("Should convert Provider domain to ProviderViewDTO successfully")
    void shouldConvertProviderDomainToViewSuccessfully() {
        // When
        ProviderView result = providerMapper.toView(domainProvider);

        // Then
        assertNotNull(result);
        assertEquals(domainProvider.getId(), result.id());
        assertEquals(domainProvider.getName(), result.name());
        assertEquals(domainProvider.getProviderType(), result.providerType());
        assertEquals(domainProvider.getCommunicationType(), result.communicationType());
        assertEquals(domainProvider.getIsActive(), result.isActive());
        assertEquals(domainProvider.getIsDefault(), result.isDefault());
        assertEquals(domainProvider.getPriority(), result.priority());
        assertEquals(domainProvider.getUrl(), result.url());
        assertEquals(domainProvider.getConfiguration(), result.configuration());
        assertEquals(domainProvider.getMaxRetries(), result.maxRetries());
        assertEquals(domainProvider.getTimeoutSeconds(), result.timeoutSeconds());
        assertEquals(domainProvider.getRateLimitPerMinute(), result.rateLimitPerMinute());
        assertEquals(domainProvider.getDailyLimit(), result.dailyLimit());
        assertEquals(domainProvider.getMonthlyLimit(), result.monthlyLimit());
        assertEquals(domainProvider.getCreatedAt(), result.createdAt());
        assertEquals(domainProvider.getUpdatedAt(), result.updatedAt());
    }

    @Test
    @DisplayName("Should return null when Provider domain is null")
    void shouldReturnNullWhenProviderDomainIsNull() {
        // When
        ProviderView result = providerMapper.toView(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle ProviderUpdateCommandDTO with partial updates")
    void shouldHandleProviderUpdateCommandWithPartialUpdates() {
        // Given
        ProviderUpdateCommandDTO command = ProviderUpdateCommandDTO.builder()
                .id(providerId)
                .xApplicationUuid(xApplicationUuid)
                .name("Updated Name Only")
                .providerType(null)
                .communicationType(null)
                .isActive(null)
                .isDefault(null)
                .priority(null)
                .url(null)
                .configuration(null)
                .maxRetries(null)
                .timeoutSeconds(null)
                .rateLimitPerMinute(null)
                .dailyLimit(null)
                .monthlyLimit(null)
                .build();

        // When
        Provider result = providerMapper.toDomain(command, domainProvider);

        // Then
        assertNotNull(result);
        assertEquals(command.getName(), result.getName());
        // Other fields should remain unchanged
        assertEquals(domainProvider.getProviderType(), result.getProviderType());
        assertEquals(domainProvider.getCommunicationType(), result.getCommunicationType());
        assertEquals(domainProvider.getIsActive(), result.getIsActive());
        assertEquals(domainProvider.getIsDefault(), result.getIsDefault());
        assertEquals(domainProvider.getPriority(), result.getPriority());
        assertEquals(domainProvider.getUrl(), result.getUrl());
        assertEquals(domainProvider.getConfiguration(), result.getConfiguration());
        assertEquals(domainProvider.getMaxRetries(), result.getMaxRetries());
        assertEquals(domainProvider.getTimeoutSeconds(), result.getTimeoutSeconds());
        assertEquals(domainProvider.getRateLimitPerMinute(), result.getRateLimitPerMinute());
        assertEquals(domainProvider.getDailyLimit(), result.getDailyLimit());
        assertEquals(domainProvider.getMonthlyLimit(), result.getMonthlyLimit());
        assertNotNull(result.getUpdatedAt());
    }
}
