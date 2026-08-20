package com.keepguard.ms_communication.application.service;

import com.keepguard.ms_communication.application.dto.common.PageResultView;
import com.keepguard.ms_communication.domain.dto.template.TemplateCreateCommandDTO;
import com.keepguard.ms_communication.application.dto.template.TemplateSearchCriteriaView;
import com.keepguard.ms_communication.domain.dto.template.TemplateUpdateCommandDTO;
import com.keepguard.ms_communication.application.dto.template.TemplateView;
import com.keepguard.ms_communication.application.service.exception.NotFoundException;
import com.keepguard.ms_communication.application.service.template.TemplateCommandService;
import com.keepguard.ms_communication.application.service.template.TemplateQueryService;
import com.keepguard.ms_communication.application.service.template.TemplateUseCaseService;
import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import com.keepguard.ms_communication.test.builder.TemplateTestBuilder;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("TemplateUseCaseService Tests")
class TemplateUseCaseServiceTest {

    @Mock
    private TemplateCommandService commandService;

    @Mock
    private TemplateQueryService queryService;

    @InjectMocks
    private TemplateUseCaseService templateUseCaseService;

    private TemplateCreateCommandDTO templateCreateCommand;
    private TemplateUpdateCommandDTO templateUpdateCommand;
    private TemplateView templateView;
    private TemplateSearchCriteriaView searchCriteria;
    private UUID templateId;

    @BeforeEach
    void setUp() {
        templateId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        templateCreateCommand = TemplateTestBuilder.builder()
                .withTenantId(tenantId)
                .withName("Test Template")
                .withDescription("Test Description")
                .withMessageType(MessageTypeEnum.EMAIL)
                .withTemplateType(TemplateTypeEnum.CADASTRO_SUCESSO)
                .withContent("Test Content")
                .withSubject("Test Subject")
                .withIsActive(true)
                .buildCreateCommand();

        templateUpdateCommand = TemplateTestBuilder.builder()
                .withId(templateId)
                .withTenantId(tenantId)
                .withName("Updated Template")
                .withDescription("Updated Description")
                .withMessageType(MessageTypeEnum.SMS)
                .withTemplateType(TemplateTypeEnum.NOTIFICACAO_GERAL)
                .withContent("Updated Content")
                .withSubject("Updated Subject")
                .withIsActive(false)
                .buildUpdateCommand();

        templateView = new TemplateView(
                templateId,
                "Test Template",
                "Test Description",
                MessageTypeEnum.EMAIL,
                TemplateTypeEnum.CADASTRO_SUCESSO,
                "test-app",
                "Test Content",
                "Test Subject",
                true,
                "{\"name\":\"John\"}",
                now,
                now
        );

        searchCriteria = new TemplateSearchCriteriaView(
                0,
                10,
                null,
                "ASC",
                "Test Template",
                MessageTypeEnum.EMAIL,
                TemplateTypeEnum.CADASTRO_SUCESSO,
                true
        );
    }

    @Test
    @DisplayName("Should create template successfully")
    void shouldCreateTemplateSuccessfully() {
        // Given
        when(commandService.create(templateCreateCommand)).thenReturn(templateView);

        // When
        TemplateView result = templateUseCaseService.create(templateCreateCommand);

        // Then
        assertNotNull(result);
        assertEquals(templateId, result.id());
        assertEquals(templateCreateCommand.getName(), result.name());
        assertEquals(templateCreateCommand.getMessageType(), result.messageType());
        assertEquals(templateCreateCommand.getTemplateType(), result.templateType());

        verify(commandService).create(templateCreateCommand);
        verify(queryService, never()).getById(any());
    }

    @Test
    @DisplayName("Should update template successfully")
    void shouldUpdateTemplateSuccessfully() {
        // Given
        when(commandService.update(templateUpdateCommand.getId(), templateUpdateCommand)).thenReturn(templateView);

        // When
        TemplateView result = templateUseCaseService.update(templateUpdateCommand);

        // Then
        assertNotNull(result);
        assertEquals(templateId, result.id());

        verify(commandService).update(templateUpdateCommand.getId(), templateUpdateCommand);
        verify(queryService, never()).getById(any());
    }

    @Test
    @DisplayName("Should get template by id successfully")
    void shouldGetTemplateByIdSuccessfully() {
        // Given
        when(queryService.getById(templateId)).thenReturn(templateView);

        // When
        Optional<TemplateView> result = templateUseCaseService.getById(templateId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(templateId, result.get().id());
        assertEquals(templateView.name(), result.get().name());

        verify(queryService).getById(templateId);
        verify(commandService, never()).create(any());
        verify(commandService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should return empty when template not found by id")
    void shouldReturnEmptyWhenTemplateNotFoundById() {
        // Given
        when(queryService.getById(templateId))
                .thenThrow(new NotFoundException("Template not found"));

        // When
        Optional<TemplateView> result = templateUseCaseService.getById(templateId);

        // Then
        assertFalse(result.isPresent());

        verify(queryService).getById(templateId);
        verify(commandService, never()).create(any());
        verify(commandService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should return empty when query service throws generic exception")
    void shouldReturnEmptyWhenQueryServiceThrowsGenericException() {
        // Given
        when(queryService.getById(templateId))
                .thenThrow(new RuntimeException("Database error"));

        // When
        Optional<TemplateView> result = templateUseCaseService.getById(templateId);

        // Then
        assertFalse(result.isPresent());

        verify(queryService).getById(templateId);
        verify(commandService, never()).create(any());
        verify(commandService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should search templates successfully")
    void shouldSearchTemplatesSuccessfully() {
        // Given
        List<TemplateView> templates = Arrays.asList(templateView);
        PageResultView<TemplateView> pageResult = PageResultView.of(templates, 0, 10, 1L);
        when(queryService.search(searchCriteria)).thenReturn(pageResult);

        // When
        PageResultView<TemplateView> result = templateUseCaseService.search(searchCriteria);

        // Then
        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(templateId, result.content().get(0).id());
        assertEquals(searchCriteria.page(), result.page());
        assertEquals(searchCriteria.size(), result.size());
        assertEquals(1, result.totalElements());

        verify(queryService).search(searchCriteria);
        verify(commandService, never()).create(any());
        verify(commandService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should get all active templates successfully")
    void shouldGetAllActiveTemplatesSuccessfully() {
        // Given
        List<TemplateView> templates = Arrays.asList(templateView);
        when(queryService.getAllActive()).thenReturn(templates);

        // When
        List<TemplateView> result = templateUseCaseService.getAllActive();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(templateId, result.get(0).id());
        assertTrue(result.get(0).isActive());

        verify(queryService).getAllActive();
        verify(commandService, never()).create(any());
        verify(commandService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should get templates by type successfully")
    void shouldGetTemplatesByTypeSuccessfully() {
        // Given
        List<TemplateView> templates = Arrays.asList(templateView);
        when(queryService.getByType(TemplateTypeEnum.CADASTRO_SUCESSO)).thenReturn(templates);

        // When
        List<TemplateView> result = templateUseCaseService.getByType(TemplateTypeEnum.CADASTRO_SUCESSO);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(templateId, result.get(0).id());
        assertEquals(TemplateTypeEnum.CADASTRO_SUCESSO, result.get(0).templateType());

        verify(queryService).getByType(TemplateTypeEnum.CADASTRO_SUCESSO);
        verify(commandService, never()).create(any());
        verify(commandService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should get templates by message type successfully")
    void shouldGetTemplatesByMessageTypeSuccessfully() {
        // Given
        List<TemplateView> templates = Arrays.asList(templateView);
        when(queryService.getByMessageType(MessageTypeEnum.EMAIL)).thenReturn(templates);

        // When
        List<TemplateView> result = templateUseCaseService.getByMessageType(MessageTypeEnum.EMAIL);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(templateId, result.get(0).id());
        assertEquals(MessageTypeEnum.EMAIL, result.get(0).messageType());

        verify(queryService).getByMessageType(MessageTypeEnum.EMAIL);
        verify(commandService, never()).create(any());
        verify(commandService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should delete template successfully")
    void shouldDeleteTemplateSuccessfully() {
        // Given
        doNothing().when(commandService).delete(templateId);

        // When
        templateUseCaseService.delete(templateId);

        // Then
        verify(commandService).delete(templateId);
        verify(queryService, never()).getById(any());
        verify(commandService, never()).create(any());
        verify(commandService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should activate template successfully")
    void shouldActivateTemplateSuccessfully() {
        // Given
        doNothing().when(commandService).activate(templateId);
        when(queryService.getById(templateId)).thenReturn(templateView);

        // When
        TemplateView result = templateUseCaseService.activate(templateId);

        // Then
        assertNotNull(result);
        assertEquals(templateId, result.id());
        assertTrue(result.isActive());

        verify(commandService).activate(templateId);
        verify(queryService).getById(templateId);
        verify(commandService, never()).create(any());
        verify(commandService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should deactivate template successfully")
    void shouldDeactivateTemplateSuccessfully() {
        // Given
        doNothing().when(commandService).deactivate(templateId);
        when(queryService.getById(templateId)).thenReturn(templateView);

        // When
        TemplateView result = templateUseCaseService.deactivate(templateId);

        // Then
        assertNotNull(result);
        assertEquals(templateId, result.id());

        verify(commandService).deactivate(templateId);
        verify(queryService).getById(templateId);
        verify(commandService, never()).create(any());
        verify(commandService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should handle command service exception when creating template")
    void shouldHandleCommandServiceExceptionWhenCreatingTemplate() {
        // Given
        when(commandService.create(templateCreateCommand))
                .thenThrow(new RuntimeException("Command service error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> templateUseCaseService.create(templateCreateCommand));

        verify(commandService).create(templateCreateCommand);
        verify(queryService, never()).getById(any());
    }

    @Test
    @DisplayName("Should handle command service exception when updating template")
    void shouldHandleCommandServiceExceptionWhenUpdatingTemplate() {
        // Given
        when(commandService.update(templateUpdateCommand.getId(), templateUpdateCommand))
                .thenThrow(new RuntimeException("Command service error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> templateUseCaseService.update(templateUpdateCommand));

        verify(commandService).update(templateUpdateCommand.getId(), templateUpdateCommand);
        verify(queryService, never()).getById(any());
    }

    @Test
    @DisplayName("Should handle command service exception when deleting template")
    void shouldHandleCommandServiceExceptionWhenDeletingTemplate() {
        // Given
        doThrow(new RuntimeException("Command service error")).when(commandService).delete(templateId);

        // When & Then
        assertThrows(RuntimeException.class, () -> templateUseCaseService.delete(templateId));

        verify(commandService).delete(templateId);
        verify(queryService, never()).getById(any());
    }

    @Test
    @DisplayName("Should handle command service exception when activating template")
    void shouldHandleCommandServiceExceptionWhenActivatingTemplate() {
        // Given
        doThrow(new RuntimeException("Command service error")).when(commandService).activate(templateId);

        // When & Then
        assertThrows(RuntimeException.class, () -> templateUseCaseService.activate(templateId));

        verify(commandService).activate(templateId);
        verify(queryService, never()).getById(any());
    }

    @Test
    @DisplayName("Should handle command service exception when deactivating template")
    void shouldHandleCommandServiceExceptionWhenDeactivatingTemplate() {
        // Given
        doThrow(new RuntimeException("Command service error")).when(commandService).deactivate(templateId);

        // When & Then
        assertThrows(RuntimeException.class, () -> templateUseCaseService.deactivate(templateId));

        verify(commandService).deactivate(templateId);
        verify(queryService, never()).getById(any());
    }

    @Test
    @DisplayName("Should handle query service exception when activating template")
    void shouldHandleQueryServiceExceptionWhenActivatingTemplate() {
        // Given
        doNothing().when(commandService).activate(templateId);
        when(queryService.getById(templateId))
                .thenThrow(new RuntimeException("Query service error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> templateUseCaseService.activate(templateId));

        verify(commandService).activate(templateId);
        verify(queryService).getById(templateId);
    }

    @Test
    @DisplayName("Should handle query service exception when deactivating template")
    void shouldHandleQueryServiceExceptionWhenDeactivatingTemplate() {
        // Given
        doNothing().when(commandService).deactivate(templateId);
        when(queryService.getById(templateId))
                .thenThrow(new RuntimeException("Query service error"));

        // When & Then
        assertThrows(RuntimeException.class, () -> templateUseCaseService.deactivate(templateId));

        verify(commandService).deactivate(templateId);
        verify(queryService).getById(templateId);
    }

    @Test
    @DisplayName("Should handle null template create command")
    void shouldHandleNullTemplateCreateCommand() {
        // Given
        when(commandService.create(null))
                .thenThrow(new IllegalArgumentException("Command cannot be null"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> templateUseCaseService.create(null));

        verify(commandService).create(null);
        verify(queryService, never()).getById(any());
    }

    @Test
    @DisplayName("Should handle null template update command")
    void shouldHandleNullTemplateUpdateCommand() {
        // When & Then - NullPointerException is thrown before reaching commandService
        assertThrows(NullPointerException.class, () -> templateUseCaseService.update(null));

        verify(commandService, never()).update(any(), any());
        verify(queryService, never()).getById(any());
    }

    @Test
    @DisplayName("Should handle null template id when getting by id")
    void shouldHandleNullTemplateIdWhenGettingById() {
        // Given
        when(queryService.getById(null))
                .thenThrow(new IllegalArgumentException("ID cannot be null"));

        // When
        Optional<TemplateView> result = templateUseCaseService.getById(null);

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
        assertThrows(IllegalArgumentException.class, () -> templateUseCaseService.search(null));

        verify(queryService).search(null);
        verify(commandService, never()).create(any());
        verify(commandService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should handle null template type when getting by type")
    void shouldHandleNullTemplateTypeWhenGettingByType() {
        // Given
        when(queryService.getByType(null))
                .thenThrow(new IllegalArgumentException("Template type cannot be null"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> templateUseCaseService.getByType(null));

        verify(queryService).getByType(null);
        verify(commandService, never()).create(any());
        verify(commandService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should handle null message type when getting by message type")
    void shouldHandleNullMessageTypeWhenGettingByMessageType() {
        // Given
        when(queryService.getByMessageType(null))
                .thenThrow(new IllegalArgumentException("Message type cannot be null"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> templateUseCaseService.getByMessageType(null));

        verify(queryService).getByMessageType(null);
        verify(commandService, never()).create(any());
        verify(commandService, never()).update(any(), any());
    }

    @Test
    @DisplayName("Should handle null template id when activating")
    void shouldHandleNullTemplateIdWhenActivating() {
        // Given
        doThrow(new IllegalArgumentException("ID cannot be null")).when(commandService).activate(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> templateUseCaseService.activate(null));

        verify(commandService).activate(null);
        verify(queryService, never()).getById(any());
    }

    @Test
    @DisplayName("Should handle null template id when deactivating")
    void shouldHandleNullTemplateIdWhenDeactivating() {
        // Given
        doThrow(new IllegalArgumentException("ID cannot be null")).when(commandService).deactivate(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> templateUseCaseService.deactivate(null));

        verify(commandService).deactivate(null);
        verify(queryService, never()).getById(any());
    }

    @Test
    @DisplayName("Should handle null template id when deleting")
    void shouldHandleNullTemplateIdWhenDeleting() {
        // Given
        doThrow(new IllegalArgumentException("ID cannot be null")).when(commandService).delete(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> templateUseCaseService.delete(null));

        verify(commandService).delete(null);
        verify(queryService, never()).getById(any());
    }
}
