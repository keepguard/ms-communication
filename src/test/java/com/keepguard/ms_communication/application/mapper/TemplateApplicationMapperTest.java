package com.keepguard.ms_communication.application.mapper;

import com.keepguard.ms_communication.adapters.in.rest.template.dto.request.TemplateCreateRequestDTO;
import com.keepguard.ms_communication.adapters.in.rest.template.dto.request.TemplateUpdateRequestDTO;
import com.keepguard.ms_communication.domain.dto.template.TemplateCreateCommandDTO;
import com.keepguard.ms_communication.domain.dto.template.TemplateUpdateCommandDTO;
import com.keepguard.ms_communication.application.dto.template.TemplateView;
import com.keepguard.ms_communication.domain.dto.template.TemplateCreateCommandDTO;
import com.keepguard.ms_communication.domain.dto.template.TemplateUpdateCommandDTO;
import com.keepguard.ms_communication.domain.entity.Template;
import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import com.keepguard.ms_communication.test.builder.TemplateTestBuilder;
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
@DisplayName("TemplateApplicationMapper Tests")
class TemplateApplicationMapperTest {

    @InjectMocks
    private TemplateApplicationMapper templateMapper;

    private TemplateCreateRequestDTO templateCreateRequestDTO;
    private TemplateUpdateRequestDTO templateUpdateRequestDTO;
    private TemplateView templateView;
    private Template domainTemplate;
    private UUID templateId;
    private UUID xApplicationUuid;
    private TemplateCreateCommandDTO templateCreateCommandDTO;
    private TemplateUpdateCommandDTO templateUpdateCommandDTO;
    private Template template;
    private Template templateWithNullVariables;

    @BeforeEach
    void setUp() {
        templateId = UUID.randomUUID();
        xApplicationUuid = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        // Setup TemplateCreateDTO
        templateCreateRequestDTO = new TemplateCreateRequestDTO();
        templateCreateRequestDTO.setName("Test Template");
        templateCreateRequestDTO.setDescription("Test Description");
        templateCreateRequestDTO.setMessageType(MessageTypeEnum.EMAIL);
        templateCreateRequestDTO.setTemplateType(TemplateTypeEnum.CADASTRO_SUCESSO);
        templateCreateRequestDTO.setContent("Test Content");
        templateCreateRequestDTO.setSubject("Test Subject");
        templateCreateRequestDTO.setIsActive(true);
        templateCreateRequestDTO.setVariables("{\"name\":\"John\"}");
        templateCreateRequestDTO.setXApplication("test-app");

        // Setup TemplateUpdateDTO
        templateUpdateRequestDTO = new TemplateUpdateRequestDTO();
        templateUpdateRequestDTO.setName("Updated Template");
        templateUpdateRequestDTO.setDescription("Updated Description");
        templateUpdateRequestDTO.setMessageType(MessageTypeEnum.SMS);
        templateUpdateRequestDTO.setTemplateType(TemplateTypeEnum.RECUPERACAO_SENHA);
        templateUpdateRequestDTO.setContent("Updated Content");
        templateUpdateRequestDTO.setSubject("Updated Subject");
        templateUpdateRequestDTO.setIsActive(false);
        templateUpdateRequestDTO.setVariables("{\"name\":\"Jane\"}");

        // Setup TemplateView
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

        // Setup domain Template
        domainTemplate = Template.create(
                TemplateTypeEnum.CADASTRO_SUCESSO,
                MessageTypeEnum.EMAIL,
                "test-app",
                "Test Template",
                "Test Description",
                "Test Subject",
                "Test Content"
        );
        domainTemplate.setId(templateId);
        domainTemplate.setIsActive(true);
        domainTemplate.setVariables("{\"name\":\"John\"}");
        domainTemplate.setCreatedAt(now);
        domainTemplate.setUpdatedAt(now);
        
        // Setup TemplateCreateCommandDTO
        templateCreateCommandDTO = TemplateCreateCommandDTO.builder()
                .xApplicationUuid(xApplicationUuid)
                .name("Test Template")
                .description("Test Description")
                .application("test-app")
                .messageType(MessageTypeEnum.EMAIL)
                .templateType(TemplateTypeEnum.CADASTRO_SUCESSO)
                .content("Test Content")
                .subject("Test Subject")
                .isActive(true)
                .variables("{\"name\":\"John\"}")
                .build();
        
        // Setup TemplateUpdateCommandDTO
        templateUpdateCommandDTO = TemplateUpdateCommandDTO.builder()
                .id(templateId)
                .xApplicationUuid(xApplicationUuid)
                .name("Updated Template")
                .description("Updated Description")
                .messageType(MessageTypeEnum.SMS)
                .templateType(TemplateTypeEnum.RECUPERACAO_SENHA)
                .content("Updated Content")
                .subject("Updated Subject")
                .isActive(false)
                .variables("{\"name\":\"Jane\"}")
                .build();
        
        // Setup Template
        template = Template.create(
                TemplateTypeEnum.CADASTRO_SUCESSO,
                MessageTypeEnum.EMAIL,
                "test-app",
                "Test Template",
                "Test Description",
                "Test Subject",
                "Test Content"
        );
        template.setId(templateId);
        template.setIsActive(true);
        template.setVariables("{\"name\":\"John\"}");
        template.setCreatedAt(now);
        template.setUpdatedAt(now);
        
        // Setup templateWithNullVariables
        templateWithNullVariables = Template.create(
                TemplateTypeEnum.CADASTRO_SUCESSO,
                MessageTypeEnum.EMAIL,
                "test-app",
                "Test Template",
                "Test Description",
                "Test Subject",
                "Test Content"
        );
        templateWithNullVariables.setId(templateId);
        templateWithNullVariables.setIsActive(true);
        templateWithNullVariables.setVariables(null);
        templateWithNullVariables.setCreatedAt(now);
        templateWithNullVariables.setUpdatedAt(now);
    }

    @Test
    @DisplayName("Should convert TemplateCreateDTO to TemplateCreateCommandDTO successfully")
    void shouldConvertTemplateCreateDTOToCommandSuccessfully() {
        // When
        TemplateCreateCommandDTO result = templateMapper.toCreateCommand(templateCreateCommandDTO);

        // Then
        assertNotNull(result);
        assertEquals(templateCreateCommandDTO.getXApplicationUuid(), result.getXApplicationUuid());
        assertEquals(templateCreateCommandDTO.getName(), result.getName());
        assertEquals(templateCreateCommandDTO.getDescription(), result.getDescription());
        assertEquals(templateCreateCommandDTO.getMessageType(), result.getMessageType());
        assertEquals(templateCreateCommandDTO.getTemplateType(), result.getTemplateType());
        assertEquals(templateCreateCommandDTO.getContent(), result.getContent());
        assertEquals(templateCreateCommandDTO.getSubject(), result.getSubject());
        assertEquals(templateCreateCommandDTO.getIsActive(), result.getIsActive());
        assertEquals(templateCreateCommandDTO.getVariables(), result.getVariables());
        assertEquals(templateCreateCommandDTO.getApplication(), result.getApplication());
    }

    @Test
    @DisplayName("Should return null when TemplateCreateDTO is null")
    void shouldReturnNullWhenTemplateCreateDTOIsNull() {
        // When
        TemplateCreateCommandDTO result = templateMapper.toCreateCommand(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should convert TemplateUpdateDTO to TemplateUpdateCommandDTO successfully")
    void shouldConvertTemplateUpdateDTOToCommandSuccessfully() {
        // When
        TemplateUpdateCommandDTO result = templateMapper.toUpdateCommand(templateUpdateCommandDTO);

        // Then
        assertNotNull(result);
        assertEquals(templateId, result.getId());
        assertEquals(xApplicationUuid, result.getXApplicationUuid());
        assertEquals(templateUpdateCommandDTO.getName(), result.getName());
        assertEquals(templateUpdateCommandDTO.getDescription(), result.getDescription());
        assertEquals(templateUpdateCommandDTO.getMessageType(), result.getMessageType());
        assertEquals(templateUpdateCommandDTO.getTemplateType(), result.getTemplateType());
        assertEquals(templateUpdateCommandDTO.getContent(), result.getContent());
        assertEquals(templateUpdateCommandDTO.getSubject(), result.getSubject());
        assertEquals(templateUpdateCommandDTO.getIsActive(), result.getIsActive());
        assertEquals(templateUpdateCommandDTO.getVariables(), result.getVariables());
    }

    @Test
    @DisplayName("Should return null when TemplateUpdateDTO is null")
    void shouldReturnNullWhenTemplateUpdateDTOIsNull() {
        // When
        TemplateUpdateCommandDTO result = templateMapper.toUpdateCommand(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should convert Template to TemplateViewDTO successfully")
    void shouldConvertTemplateToViewDTOSuccessfully() {
        // When
        TemplateView result = templateMapper.toView(template);

        // Then
        assertNotNull(result);
        assertEquals(templateView.id(), result.id());
        assertEquals(templateView.name(), result.name());
        assertEquals(templateView.description(), result.description());
        assertEquals(templateView.messageType(), result.messageType());
        assertEquals(templateView.templateType(), result.templateType());
        assertEquals(templateView.content(), result.content());
        assertEquals(templateView.subject(), result.subject());
        assertEquals(templateView.isActive(), result.isActive());
        assertEquals(templateView.variables(), result.variables());
        assertEquals(templateView.createdAt(), result.createdAt());
        assertEquals(templateView.updatedAt(), result.updatedAt());
    }

    @Test
    @DisplayName("Should return null when TemplateViewDTO is null")
    void shouldReturnNullWhenTemplateViewIsNull() {
        // When
        TemplateView result = templateMapper.toView(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should convert TemplateCreateCommandDTO to Template domain successfully")
    void shouldConvertTemplateCreateCommandToDomainSuccessfully() {
        // Given
        TemplateCreateCommandDTO command = TemplateCreateCommandDTO.builder()
                .xApplicationUuid(xApplicationUuid)
                .name("Test Template")
                .description("Test Description")
                .application("test-app")
                .messageType(MessageTypeEnum.EMAIL)
                .templateType(TemplateTypeEnum.CADASTRO_SUCESSO)
                .content("Test Content")
                .subject("Test Subject")
                .isActive(true)
                .variables("{\"name\":\"John\"}")
                .build();

        // When
        Template result = templateMapper.toDomain(command);

        // Then
        assertNotNull(result);
        assertEquals(command.getName(), result.getName());
        assertEquals(command.getDescription(), result.getDescription());
        assertEquals(command.getMessageType(), result.getMessageType());
        assertEquals(command.getTemplateType(), result.getTemplateType());
        assertEquals(command.getContent(), result.getContent());
        assertEquals(command.getSubject(), result.getSubject());
        assertEquals(command.getIsActive(), result.getIsActive());
        assertEquals(command.getVariables(), result.getVariables());
    }

    @Test
    @DisplayName("Should return null when TemplateCreateCommandDTO is null")
    void shouldReturnNullWhenTemplateCreateCommandIsNull() {
        // When
        Template result = templateMapper.toDomain((TemplateCreateCommandDTO) null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should convert TemplateCreateCommandDTO with null optional fields successfully")
    void shouldConvertTemplateCreateCommandWithNullOptionalFieldsSuccessfully() {
        // Given
        TemplateCreateCommandDTO command = TemplateTestBuilder.builder()
                .withName("Test Template")
                .withDescription("Test Description")
                .withMessageType(MessageTypeEnum.EMAIL)
                .withTemplateType(TemplateTypeEnum.CADASTRO_SUCESSO)
                .withContent("Test Content")
                .withSubject("Test Subject")
                .withIsActive(null)
                .withVariables(null)
                .buildCreateCommand();

        // When
        Template result = templateMapper.toDomain(command);

        // Then
        assertNotNull(result);
        assertEquals(command.getName(), result.getName());
        assertEquals(command.getDescription(), result.getDescription());
        assertEquals(command.getMessageType(), result.getMessageType());
        assertEquals(command.getTemplateType(), result.getTemplateType());
        assertEquals(command.getContent(), result.getContent());
        assertEquals(command.getSubject(), result.getSubject());
        // Optional fields should remain with default values
        assertNotNull(result.getIsActive());
        assertNotNull(result.getVariables());
    }

    @Test
    @DisplayName("Should convert TemplateUpdateCommandDTO to Template domain successfully")
    void shouldConvertTemplateUpdateCommandToDomainSuccessfully() {
        // Given
        TemplateUpdateCommandDTO command = TemplateUpdateCommandDTO.builder()
                .id(templateId)
                .xApplicationUuid(xApplicationUuid)
                .name("Updated Template")
                .description("Updated Description")
                .messageType(MessageTypeEnum.SMS)
                .templateType(TemplateTypeEnum.NOTIFICACAO_GERAL)
                .content("Updated Content")
                .subject("Updated Subject")
                .isActive(false)
                .variables("{\"name\":\"Jane\"}")
                .build();

        // When
        Template result = templateMapper.toDomain(command, domainTemplate);

        // Then
        assertNotNull(result);
        assertEquals(command.getId(), result.getId());
        assertEquals(command.getName(), result.getName());
        assertEquals(command.getDescription(), result.getDescription());
        assertEquals(command.getMessageType(), result.getMessageType());
        assertEquals(command.getTemplateType(), result.getTemplateType());
        assertEquals(command.getContent(), result.getContent());
        assertEquals(command.getSubject(), result.getSubject());
        assertEquals(command.getIsActive(), result.getIsActive());
        assertEquals(command.getVariables(), result.getVariables());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    @DisplayName("Should return null when TemplateUpdateCommandDTO is null")
    void shouldReturnNullWhenTemplateUpdateCommandIsNull() {
        // When
        Template result = templateMapper.toDomain(null, domainTemplate);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should return null when existing Template is null")
    void shouldReturnNullWhenExistingTemplateIsNull() {
        // Given
        TemplateUpdateCommandDTO command = TemplateUpdateCommandDTO.builder()
                .id(templateId)
                .xApplicationUuid(xApplicationUuid)
                .name("Updated Template")
                .description("Updated Description")
                .messageType(MessageTypeEnum.SMS)
                .templateType(TemplateTypeEnum.NOTIFICACAO_GERAL)
                .content("Updated Content")
                .subject("Updated Subject")
                .isActive(false)
                .variables("{\"name\":\"Jane\"}")
                .build();

        // When
        Template result = templateMapper.toDomain(command, null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should convert TemplateUpdateCommandDTO with null optional fields successfully")
    void shouldConvertTemplateUpdateCommandWithNullOptionalFieldsSuccessfully() {
        // Given
        TemplateUpdateCommandDTO command = TemplateUpdateCommandDTO.builder()
                .id(templateId)
                .xApplicationUuid(xApplicationUuid)
                .name(null)
                .description(null)
                .messageType(null)
                .templateType(null)
                .content(null)
                .subject(null)
                .isActive(null)
                .variables(null)
                .build();

        // When
        Template result = templateMapper.toDomain(command, domainTemplate);

        // Then
        assertNotNull(result);
        assertEquals(templateId, result.getId());
        // Original values should be preserved for null fields
        assertEquals(domainTemplate.getName(), result.getName());
        assertEquals(domainTemplate.getDescription(), result.getDescription());
        assertEquals(domainTemplate.getMessageType(), result.getMessageType());
        assertEquals(domainTemplate.getTemplateType(), result.getTemplateType());
        assertEquals(domainTemplate.getContent(), result.getContent());
        assertEquals(domainTemplate.getSubject(), result.getSubject());
        assertEquals(domainTemplate.getIsActive(), result.getIsActive());
        assertEquals(domainTemplate.getVariables(), result.getVariables());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    @DisplayName("Should convert Template domain to TemplateViewDTO successfully")
    void shouldConvertTemplateDomainToViewSuccessfully() {
        // When
        TemplateView result = templateMapper.toView(domainTemplate);

        // Then
        assertNotNull(result);
        assertEquals(domainTemplate.getId(), result.id());
        assertEquals(domainTemplate.getName(), result.name());
        assertEquals(domainTemplate.getDescription(), result.description());
        assertEquals(domainTemplate.getMessageType(), result.messageType());
        assertEquals(domainTemplate.getTemplateType(), result.templateType());
        assertEquals(domainTemplate.getContent(), result.content());
        assertEquals(domainTemplate.getSubject(), result.subject());
        assertEquals(domainTemplate.getIsActive(), result.isActive());
        assertEquals(domainTemplate.getVariables(), result.variables());
        assertEquals(domainTemplate.getCreatedAt(), result.createdAt());
        assertEquals(domainTemplate.getUpdatedAt(), result.updatedAt());
    }

    @Test
    @DisplayName("Should return null when Template domain is null")
    void shouldReturnNullWhenTemplateDomainIsNull() {
        // When
        TemplateView result = templateMapper.toView(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle TemplateUpdateCommandDTO with partial updates")
    void shouldHandleTemplateUpdateCommandWithPartialUpdates() {
        // Given
        TemplateUpdateCommandDTO command = TemplateUpdateCommandDTO.builder()
                .id(templateId)
                .xApplicationUuid(xApplicationUuid)
                .name("Updated Name Only")
                .description(null)
                .messageType(null)
                .templateType(null)
                .content(null)
                .subject(null)
                .isActive(null)
                .variables(null)
                .build();

        // When
        Template result = templateMapper.toDomain(command, domainTemplate);

        // Then
        assertNotNull(result);
        assertEquals(command.getName(), result.getName());
        // Other fields should remain unchanged
        assertEquals(domainTemplate.getDescription(), result.getDescription());
        assertEquals(domainTemplate.getMessageType(), result.getMessageType());
        assertEquals(domainTemplate.getTemplateType(), result.getTemplateType());
        assertEquals(domainTemplate.getContent(), result.getContent());
        assertEquals(domainTemplate.getSubject(), result.getSubject());
        assertEquals(domainTemplate.getIsActive(), result.getIsActive());
        assertEquals(domainTemplate.getVariables(), result.getVariables());
        assertNotNull(result.getUpdatedAt());
    }

    @Test
    @DisplayName("Should handle TemplateCreateDTO with null variables successfully")
    void shouldHandleTemplateCreateDTOWithNullVariablesSuccessfully() {
        // Given
        TemplateCreateCommandDTO command = TemplateCreateCommandDTO.builder()
                .xApplicationUuid(xApplicationUuid)
                .name("Test Template")
                .description("Test Description")
                .application("test-app")
                .messageType(MessageTypeEnum.EMAIL)
                .templateType(TemplateTypeEnum.CADASTRO_SUCESSO)
                .content("Test Content")
                .subject("Test Subject")
                .isActive(true)
                .variables(null)
                .build();

        // When
        TemplateCreateCommandDTO result = templateMapper.toCreateCommand(command);

        // Then
        assertNotNull(result);
        assertNull(result.getVariables());
    }

    @Test
    @DisplayName("Should handle TemplateCreateDTO with empty variables successfully")
    void shouldHandleTemplateCreateDTOWithEmptyVariablesSuccessfully() {
        // Given
        TemplateCreateCommandDTO command = TemplateCreateCommandDTO.builder()
                .xApplicationUuid(xApplicationUuid)
                .name("Test Template")
                .description("Test Description")
                .application("test-app")
                .messageType(MessageTypeEnum.EMAIL)
                .templateType(TemplateTypeEnum.CADASTRO_SUCESSO)
                .content("Test Content")
                .subject("Test Subject")
                .isActive(true)
                .variables("{}")
                .build();

        // When
        TemplateCreateCommandDTO result = templateMapper.toCreateCommand(command);

        // Then
        assertNotNull(result);
        assertNotNull(result.getVariables());
        assertEquals("{}", result.getVariables());
    }

    @Test
    @DisplayName("Should handle TemplateUpdateDTO with null variables successfully")
    void shouldHandleTemplateUpdateDTOWithNullVariablesSuccessfully() {
        // Given
        TemplateUpdateCommandDTO command = TemplateUpdateCommandDTO.builder()
                .id(templateId)
                .xApplicationUuid(xApplicationUuid)
                .name("Updated Template")
                .description("Updated Description")
                .messageType(MessageTypeEnum.SMS)
                .templateType(TemplateTypeEnum.RECUPERACAO_SENHA)
                .content("Updated Content")
                .subject("Updated Subject")
                .isActive(false)
                .variables(null)
                .build();

        // When
        TemplateUpdateCommandDTO result = templateMapper.toUpdateCommand(command);

        // Then
        assertNotNull(result);
        assertNull(result.getVariables());
    }

    @Test
    @DisplayName("Should handle TemplateViewDTO with null variables successfully")
    void shouldHandleTemplateViewWithNullVariablesSuccessfully() {
        // Given
        TemplateView templateViewWithNullVariables = new TemplateView(
                templateId,
                "Test Template",
                "Test Description",
                MessageTypeEnum.EMAIL,
                TemplateTypeEnum.CADASTRO_SUCESSO,
                "test-app",
                "Test Content",
                "Test Subject",
                true,
                null, // variables
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // When
        TemplateView result = templateMapper.toView(templateWithNullVariables);

        // Then
        assertNotNull(result);
        assertNull(result.variables());
    }

    @Test
    @DisplayName("Should handle TemplateCreateCommandDTO with empty variables successfully")
    void shouldHandleTemplateCreateCommandWithEmptyVariablesSuccessfully() {
        // Given
        TemplateCreateCommandDTO command = TemplateCreateCommandDTO.builder()
                .xApplicationUuid(xApplicationUuid)
                .name("Test Template")
                .description("Test Description")
                .application("test-app")
                .messageType(MessageTypeEnum.EMAIL)
                .templateType(TemplateTypeEnum.CADASTRO_SUCESSO)
                .content("Test Content")
                .subject("Test Subject")
                .isActive(true)
                .variables("{}")
                .build();

        // When
        Template result = templateMapper.toDomain(command);

        // Then
        assertNotNull(result);
        assertNotNull(result.getVariables());
        assertEquals("{}", result.getVariables());
    }

    @Test
    @DisplayName("Should handle TemplateUpdateCommandDTO with empty variables successfully")
    void shouldHandleTemplateUpdateCommandWithEmptyVariablesSuccessfully() {
        // Given
        TemplateUpdateCommandDTO command = TemplateUpdateCommandDTO.builder()
                .id(templateId)
                .xApplicationUuid(xApplicationUuid)
                .name("Updated Template")
                .description("Updated Description")
                .messageType(MessageTypeEnum.SMS)
                .templateType(TemplateTypeEnum.NOTIFICACAO_GERAL)
                .content("Updated Content")
                .subject("Updated Subject")
                .isActive(false)
                .variables("{}")
                .build();

        // When
        Template result = templateMapper.toDomain(command, domainTemplate);

        // Then
        assertNotNull(result);
        assertNotNull(result.getVariables());
        assertEquals("{}", result.getVariables());
    }
}
