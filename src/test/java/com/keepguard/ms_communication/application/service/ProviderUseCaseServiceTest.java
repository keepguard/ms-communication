package com.keepguard.ms_communication.application.service;

import com.keepguard.ms_communication.application.dto.common.PageResultView;
import com.keepguard.ms_communication.domain.dto.provider.ProviderCreateCommandDTO;
import com.keepguard.ms_communication.application.dto.provider.ProviderSearchCriteriaView;
import com.keepguard.ms_communication.domain.dto.provider.ProviderUpdateCommandDTO;
import com.keepguard.ms_communication.application.dto.provider.ProviderView;
import com.keepguard.ms_communication.application.service.exception.NotFoundException;
import com.keepguard.ms_communication.application.service.provider.ProviderCommandService;
import com.keepguard.ms_communication.application.service.provider.ProviderQueryService;
import com.keepguard.ms_communication.application.service.provider.ProviderUseCaseService;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Disabled;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProviderUseCaseService Tests")
class ProviderUseCaseServiceTest {

    @Mock
    private ProviderCommandService commandService;

    @Mock
    private ProviderQueryService queryService;

    @InjectMocks
    private ProviderUseCaseService providerUseCaseService;

    private ProviderCreateCommandDTO providerCreateCommand;
    private ProviderUpdateCommandDTO providerUpdateCommand;
    private ProviderView providerView;
    private ProviderSearchCriteriaView searchCriteria;
    private UUID providerId;

    @BeforeEach
    void setUp() {
        providerId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        providerCreateCommand = ProviderTestBuilder.builder()
                .withTenantId(tenantId)
                .withName("Test Provider")
                .withProviderType(ProviderTypeEnum.N8N)
                .withCommunicationType(CommunicationTypeEnum.EMAIL)
                .withIsActive(true)
                .withIsDefault(false)
                .withPriority(1)
                .withUrl("https://test.com")
                .withMaxRetries(3)
                .withTimeoutSeconds(30)
                .withRateLimitPerMinute(100)
                .withDailyLimit(1000)
                .withMonthlyLimit(30000)
                .buildCreateCommand();

        providerUpdateCommand = ProviderTestBuilder.builder()
                .withId(providerId)
                .withTenantId(tenantId)
                .withName("Updated Provider")
                .withProviderType(ProviderTypeEnum.SENDGRID)
                .withCommunicationType(CommunicationTypeEnum.SMS)
                .withIsActive(false)
                .withIsDefault(true)
                .withPriority(2)
                .withUrl("https://updated.com")
                .withMaxRetries(5)
                .withTimeoutSeconds(60)
                .withRateLimitPerMinute(200)
                .withDailyLimit(2000)
                .withMonthlyLimit(60000)
                .buildUpdateCommand();

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

        searchCriteria = new ProviderSearchCriteriaView(
                0,
                10,
                null,
                "ASC",
                "Test Provider",
                ProviderTypeEnum.N8N,
                CommunicationTypeEnum.EMAIL,
                true,
                false
        );
    }

    @Test
    @DisplayName("Should create provider successfully")
    void shouldCreateProviderSuccessfully() {
        // Given
        when(commandService.create(providerCreateCommand)).thenReturn(providerView);

        // When
        ProviderView result = providerUseCaseService.create(providerCreateCommand);

        // Then
        assertNotNull(result);
        assertEquals(providerId, result.id());
        assertEquals(providerCreateCommand.getName(), result.name());
        assertEquals(providerCreateCommand.getProviderType(), result.providerType());
        assertEquals(providerCreateCommand.getCommunicationType(), result.communicationType());

        verify(commandService).create(providerCreateCommand);
        verify(queryService, never()).getById(any());
    }

    @Test
    @DisplayName("Should update provider successfully")
    void shouldUpdateProviderSuccessfully() {
        // Given
        when(commandService.update(providerUpdateCommand.getId(), providerUpdateCommand)).thenReturn(providerView);

        // When
        ProviderView result = providerUseCaseService.update(providerUpdateCommand);

        // Then
        assertNotNull(result);
        assertEquals(providerId, result.id());

        verify(commandService).update(providerUpdateCommand.getId(), providerUpdateCommand);
        verify(queryService, never()).getById(any());
    }

    @Test
    @DisplayName("Should get provider by id successfully")
    void shouldGetProviderByIdSuccessfully() {
        // Given
        when(queryService.getById(providerId)).thenReturn(providerView);

        // When
        Optional<ProviderView> result = providerUseCaseService.getById(providerId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(providerId, result.get().id());
        assertEquals(providerView.name(), result.get().name());

        verify(queryService).getById(providerId);
        verify(commandService, never()).create(any());
        verify(commandService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should return empty when provider not found by id")
    void shouldReturnEmptyWhenProviderNotFoundById() {
        // Given
        when(queryService.getById(providerId))
                .thenThrow(new NotFoundException("Provider not found"));

        // When
        Optional<ProviderView> result = providerUseCaseService.getById(providerId);

        // Then
        assertFalse(result.isPresent());

        verify(queryService).getById(providerId);
        verify(commandService, never()).create(any());
        verify(commandService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should return empty when query service throws generic exception")
    void shouldReturnEmptyWhenQueryServiceThrowsGenericException() {
        // Given
        when(queryService.getById(providerId))
                .thenThrow(new RuntimeException("Database error"));

        // When
        Optional<ProviderView> result = providerUseCaseService.getById(providerId);

        // Then
        assertFalse(result.isPresent());

        verify(queryService).getById(providerId);
        verify(commandService, never()).create(any());
        verify(commandService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should search providers successfully")
    void shouldSearchProvidersSuccessfully() {
        // Given
        List<ProviderView> providers = Arrays.asList(providerView);
        PageResultView<ProviderView> pageResult = PageResultView.of(providers, 0, 10, 1L);
        when(queryService.search(searchCriteria)).thenReturn(pageResult);

        // When
        PageResultView<ProviderView> result = providerUseCaseService.search(searchCriteria);

        // Then
        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(providerId, result.content().get(0).id());
        assertEquals(searchCriteria.page(), result.page());
        assertEquals(searchCriteria.size(), result.size());
        assertEquals(1, result.totalElements());

        verify(queryService).search(searchCriteria);
        verify(commandService, never()).create(any());
        verify(commandService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should get all active providers successfully")
    void shouldGetAllActiveProvidersSuccessfully() {
        // Given
        List<ProviderView> providers = Arrays.asList(providerView);
        when(queryService.getAllActive()).thenReturn(providers);

        // When
        List<ProviderView> result = providerUseCaseService.getAllActive();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(providerId, result.get(0).id());
        assertTrue(result.get(0).isActive());

        verify(queryService).getAllActive();
        verify(commandService, never()).create(any());
        verify(commandService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should get providers by communication type successfully")
    void shouldGetProvidersByCommunicationTypeSuccessfully() {
        // Given
        List<ProviderView> providers = Arrays.asList(providerView);
        when(queryService.getByCommunicationType(CommunicationTypeEnum.EMAIL)).thenReturn(providers);

        // When
        List<ProviderView> result = providerUseCaseService.getByCommunicationType(CommunicationTypeEnum.EMAIL);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(providerId, result.get(0).id());
        assertEquals(CommunicationTypeEnum.EMAIL, result.get(0).communicationType());

        verify(queryService).getByCommunicationType(CommunicationTypeEnum.EMAIL);
        verify(commandService, never()).create(any());
        verify(commandService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should get default provider by communication type successfully")
    void shouldGetDefaultProviderByCommunicationTypeSuccessfully() {
        // Given
        when(queryService.getDefaultByCommunicationType(CommunicationTypeEnum.EMAIL)).thenReturn(Optional.of(providerView));

        // When
        Optional<ProviderView> result = providerUseCaseService.getDefaultByCommunicationType(CommunicationTypeEnum.EMAIL);

        // Then
        assertTrue(result.isPresent());
        assertEquals(providerId, result.get().id());
        assertEquals(CommunicationTypeEnum.EMAIL, result.get().communicationType());

        verify(queryService).getDefaultByCommunicationType(CommunicationTypeEnum.EMAIL);
        verify(commandService, never()).create(any());
        verify(commandService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should return empty when default provider not found by communication type")
    void shouldReturnEmptyWhenDefaultProviderNotFoundByCommunicationType() {
        // Given
        when(queryService.getDefaultByCommunicationType(CommunicationTypeEnum.EMAIL))
                .thenThrow(new NotFoundException("Default provider not found"));

        // When
        Optional<ProviderView> result = providerUseCaseService.getDefaultByCommunicationType(CommunicationTypeEnum.EMAIL);

        // Then
        assertFalse(result.isPresent());

        verify(queryService).getDefaultByCommunicationType(CommunicationTypeEnum.EMAIL);
        verify(commandService, never()).create(any());
        verify(commandService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should delete provider successfully")
    void shouldDeleteProviderSuccessfully() {
        // Given
        doNothing().when(commandService).delete(providerId);

        // When
        providerUseCaseService.delete(providerId);

        // Then
        verify(commandService).delete(providerId);
        verify(queryService, never()).getById(any());
        verify(commandService, never()).create(any());
        verify(commandService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should activate provider successfully")
    void shouldActivateProviderSuccessfully() {
        // Given
        when(commandService.activate(providerId)).thenReturn(providerView);

        // When
        ProviderView result = providerUseCaseService.activate(providerId);

        // Then
        assertNotNull(result);
        assertEquals(providerId, result.id());
        assertTrue(result.isActive());

        verify(commandService).activate(providerId);
        verify(queryService, never()).getById(any());
        verify(commandService, never()).create(any());
        verify(commandService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should deactivate provider successfully")
    void shouldDeactivateProviderSuccessfully() {
        // Given
        when(commandService.deactivate(providerId)).thenReturn(providerView);

        // When
        ProviderView result = providerUseCaseService.deactivate(providerId);

        // Then
        assertNotNull(result);
        assertEquals(providerId, result.id());

        verify(commandService).deactivate(providerId);
        verify(queryService, never()).getById(any());
        verify(commandService, never()).create(any());
        verify(commandService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should set provider as default successfully")
    void shouldSetProviderAsDefaultSuccessfully() {
        // Given
        when(commandService.setAsDefault(providerId)).thenReturn(providerView);

        // When
        ProviderView result = providerUseCaseService.setAsDefault(providerId);

        // Then
        assertNotNull(result);
        assertEquals(providerId, result.id());

        verify(commandService).setAsDefault(providerId);
        verify(queryService, never()).getById(any());
        verify(commandService, never()).create(any());
        verify(commandService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should handle command service exception when creating provider")
    void shouldHandleCommandServiceExceptionWhenCreatingProvider() {
        // Given
        when(commandService.create(providerCreateCommand))
                .thenThrow(new RuntimeException("Command service error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> providerUseCaseService.create(providerCreateCommand));

        verify(commandService).create(providerCreateCommand);
        verify(queryService, never()).getById(any());
    }

    @Test
    @DisplayName("Should handle command service exception when updating provider")
    void shouldHandleCommandServiceExceptionWhenUpdatingProvider() {
        // Given
        when(commandService.update(providerUpdateCommand.getId(), providerUpdateCommand))
                .thenThrow(new RuntimeException("Command service error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> providerUseCaseService.update(providerUpdateCommand));

        verify(commandService).update(providerUpdateCommand.getId(), providerUpdateCommand);
        verify(queryService, never()).getById(any());
    }

    @Test
    @DisplayName("Should handle command service exception when deleting provider")
    void shouldHandleCommandServiceExceptionWhenDeletingProvider() {
        // Given
        doThrow(new RuntimeException("Command service error")).when(commandService).delete(providerId);

        // When & Then
        assertThrows(RuntimeException.class, () -> providerUseCaseService.delete(providerId));

        verify(commandService).delete(providerId);
        verify(queryService, never()).getById(any());
    }

    @Test
    @DisplayName("Should handle null provider create command")
    void shouldHandleNullProviderCreateCommand() {
        // Given
        when(commandService.create(null))
                .thenThrow(new IllegalArgumentException("Command cannot be null"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> providerUseCaseService.create(null));

        verify(commandService).create(null);
        verify(queryService, never()).getById(any());
    }

    @Test
    @DisplayName("Should handle null provider update command")
    void shouldHandleNullProviderUpdateCommand() {
        // When & Then - NullPointerException is thrown before reaching commandService
        assertThrows(NullPointerException.class, () -> providerUseCaseService.update(null));

        verify(commandService, never()).update(any(), any());
        verify(queryService, never()).getById(any());
    }

    @Test
    @DisplayName("Should handle null provider id when getting by id")
    void shouldHandleNullProviderIdWhenGettingById() {
        // Given
        when(queryService.getById(null))
                .thenThrow(new IllegalArgumentException("ID cannot be null"));

        // When
        Optional<ProviderView> result = providerUseCaseService.getById(null);

        // Then
        assertFalse(result.isPresent());

        verify(queryService).getById(null);
        verify(commandService, never()).create(any());
        verify(commandService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should handle null search criteria")
    void shouldHandleNullSearchCriteria() {
        // Given
        when(queryService.search(null))
                .thenThrow(new IllegalArgumentException("Criteria cannot be null"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> providerUseCaseService.search(null));

        verify(queryService).search(null);
        verify(commandService, never()).create(any());
        verify(commandService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should handle null communication type when getting by type")
    void shouldHandleNullCommunicationTypeWhenGettingByType() {
        // Given
        when(queryService.getByCommunicationType(null))
                .thenThrow(new IllegalArgumentException("Communication type cannot be null"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> providerUseCaseService.getByCommunicationType(null));

        verify(queryService).getByCommunicationType(null);
        verify(commandService, never()).create(any());
        verify(commandService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should handle null communication type when getting default by type")
    void shouldHandleNullCommunicationTypeWhenGettingDefaultByType() {
        // Given
        when(queryService.getDefaultByCommunicationType(null))
                .thenThrow(new IllegalArgumentException("Communication type cannot be null"));

        // When
        Optional<ProviderView> result = providerUseCaseService.getDefaultByCommunicationType(null);

        // Then
        assertFalse(result.isPresent());

        verify(queryService).getDefaultByCommunicationType(null);
        verify(commandService, never()).create(any());
        verify(commandService, never()).update(any(), any());
    }
}
