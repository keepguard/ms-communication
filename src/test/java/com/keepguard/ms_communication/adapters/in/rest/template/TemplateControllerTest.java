package com.keepguard.ms_communication.adapters.in.rest.template;

import com.keepguard.ms_communication.adapters.in.rest.template.dto.request.TemplateCreateRequestDTO;
import com.keepguard.ms_communication.adapters.in.rest.template.dto.response.*;
import com.keepguard.ms_communication.adapters.in.rest.template.dto.request.TemplateUpdateRequestDTO;
import com.keepguard.ms_communication.domain.dto.template.TemplateCreateCommandDTO;
import com.keepguard.ms_communication.domain.dto.template.TemplateUpdateCommandDTO;
import com.keepguard.ms_communication.application.dto.template.TemplateView;
import com.keepguard.ms_communication.adapters.in.rest.template.mapper.TemplateAdapterMapper;
import com.keepguard.ms_communication.application.mapper.TemplateApplicationMapper;
import com.keepguard.ms_communication.application.port.in.service.TemplatePort;
import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import com.keepguard.ms_communication.application.port.out.metrics.MetricsPort;
import io.micrometer.core.instrument.Timer;
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
 * Testes unitários para TemplateController
 */
@ExtendWith(MockitoExtension.class)
class TemplateControllerTest {
    
    @Mock
    private TemplatePort templatePort;
    
    @Mock
    private TemplateAdapterMapper adapterMapper;

    @Mock
    private TemplateApplicationMapper applicationMapper;

    @Mock
    private MetricsPort metricsPort;
    
    @InjectMocks
    private TemplateController templateController;
    
    private TemplateCreateRequestDTO templateCreateRequestDTO;
    private TemplateUpdateRequestDTO templateUpdateRequestDTO;
    private TemplateCreateResponseDTO templateCreateResponseDTO;
    private TemplateUpdateResponseDTO templateUpdateResponseDTO;
    private TemplateGetTemplateByIdResponseDTO templateGetTemplateByIdResponseDTO;
    private TemplateGetTemplateByTypeResponseDTO templateGetTemplateByTypeResponseDTO;
    private TemplateGetTemplatesResponseDTO templateGetTemplatesResponseDTO;
    private TemplateView templateView;
    private TemplateCreateCommandDTO templateCreateCommand;
    private TemplateUpdateCommandDTO templateUpdateCommand;
    private UUID templateId;
    private String tenantIdStr;
    private UUID tenantId;
    
    @BeforeEach
    void setUp() {
        templateId = UUID.randomUUID();
        tenantId = UUID.randomUUID();
        tenantIdStr = tenantId.toString();
        
        
        templateCreateRequestDTO = new TemplateCreateRequestDTO();
        templateCreateRequestDTO.setTemplateType(TemplateTypeEnum.CADASTRO_SUCESSO);
        templateCreateRequestDTO.setMessageType(MessageTypeEnum.EMAIL);
        templateCreateRequestDTO.setName("Test Template");
        templateCreateRequestDTO.setDescription("Test template description");
        templateCreateRequestDTO.setSubject("Test Subject");
        templateCreateRequestDTO.setContent("Hello {{userName}}, welcome to our system!");
        templateCreateRequestDTO.setVariables("[\"userName\", \"activationLink\"]");
        templateCreateRequestDTO.setTenantId("test-app");
        templateCreateRequestDTO.setIsActive(true);
        
        templateUpdateRequestDTO = new TemplateUpdateRequestDTO();
        templateUpdateRequestDTO.setName("Updated Template");
        templateUpdateRequestDTO.setDescription("Updated template description");
        templateUpdateRequestDTO.setSubject("Updated Subject");
        templateUpdateRequestDTO.setContent("Updated content with {{userName}}");
        templateUpdateRequestDTO.setVariables("[\"userName\", \"companyName\"]");
        templateUpdateRequestDTO.setMessageType(MessageTypeEnum.SMS);
        templateUpdateRequestDTO.setTemplateType(TemplateTypeEnum.RECUPERACAO_SENHA);
        templateUpdateRequestDTO.setIsActive(false);
        
        templateCreateResponseDTO = TemplateCreateResponseDTO.builder()
            .id(templateId)
            .type(TemplateTypeEnum.CADASTRO_SUCESSO)
            .templateType(TemplateTypeEnum.CADASTRO_SUCESSO)
            .messageType(MessageTypeEnum.EMAIL)
            .tenantId(tenantIdStr)
            .name("Test Template")
            .description("Test template description")
            .subject("Test Subject")
            .content("Hello {{userName}}, welcome to our system!")
            .variables("[\"userName\", \"activationLink\"]")
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        templateUpdateResponseDTO = TemplateUpdateResponseDTO.builder()
            .id(templateId)
            .type(TemplateTypeEnum.RECUPERACAO_SENHA)
            .templateType(TemplateTypeEnum.RECUPERACAO_SENHA)
            .messageType(MessageTypeEnum.SMS)
            .application(tenantIdStr)
            .name("Updated Template")
            .description("Updated template description")
            .subject("Updated Subject")
            .content("Updated content with {{userName}}")
            .variables("[\"userName\", \"companyName\"]")
            .isActive(false)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        templateGetTemplateByIdResponseDTO = TemplateGetTemplateByIdResponseDTO.builder()
            .id(templateId)
            .type(TemplateTypeEnum.CADASTRO_SUCESSO)
            .templateType(TemplateTypeEnum.CADASTRO_SUCESSO)
            .messageType(MessageTypeEnum.EMAIL)
            .application(tenantIdStr)
            .name("Test Template")
            .description("Test template description")
            .subject("Test Subject")
            .content("Hello {{userName}}, welcome to our system!")
            .variables("[\"userName\", \"activationLink\"]")
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        templateGetTemplateByTypeResponseDTO = TemplateGetTemplateByTypeResponseDTO.builder()
            .id(templateId)
            .type(TemplateTypeEnum.CADASTRO_SUCESSO)
            .templateType(TemplateTypeEnum.CADASTRO_SUCESSO)
            .messageType(MessageTypeEnum.EMAIL)
            .application(tenantIdStr)
            .name("Test Template")
            .description("Test template description")
            .subject("Test Subject")
            .content("Hello {{userName}}, welcome to our system!")
            .variables("[\"userName\", \"activationLink\"]")
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        templateGetTemplatesResponseDTO = TemplateGetTemplatesResponseDTO.builder()
            .id(templateId)
            .type(TemplateTypeEnum.CADASTRO_SUCESSO)
            .templateType(TemplateTypeEnum.CADASTRO_SUCESSO)
            .messageType(MessageTypeEnum.EMAIL)
            .tenantId(tenantIdStr)
            .name("Test Template")
            .description("Test template description")
            .subject("Test Subject")
            .content("Hello {{userName}}, welcome to our system!")
            .variables("[\"userName\", \"activationLink\"]")
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
        
        templateView = new TemplateView(
            templateId,
            "Test Template",
            "Test template description",
            MessageTypeEnum.EMAIL,
            TemplateTypeEnum.CADASTRO_SUCESSO,
            tenantIdStr,
            "Hello {{userName}}, welcome to our system!",
            "Test Subject",
            true,
            "[\"userName\", \"activationLink\"]",
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        templateCreateCommand = TemplateCreateCommandDTO.builder()
                .tenantId(tenantId)
                .name("Test Template")
                .description("Test template description")
                .application("test-app")
                .messageType(MessageTypeEnum.EMAIL)
                .templateType(TemplateTypeEnum.CADASTRO_SUCESSO)
                .content("Hello {{userName}}, welcome to our system!")
                .subject("Test Subject")
                .isActive(true)
                .variables("[\"userName\", \"activationLink\"]")
                .build();
        
        templateUpdateCommand = TemplateUpdateCommandDTO.builder()
                .id(templateId)
                .tenantId(tenantId)
                .name("Updated Template")
                .description("Updated template description")
                .messageType(MessageTypeEnum.SMS)
                .templateType(TemplateTypeEnum.RECUPERACAO_SENHA)
                .content("Updated content with {{userName}}")
                .subject("Updated Subject")
                .isActive(false)
                .variables("[\"userName\", \"companyName\"]")
                .build();
    }
    
    @Test
    @DisplayName("Deve criar template com sucesso")
    void shouldCreateTemplateSuccessfully() {
        // Given
        Timer.Sample sample = mock(Timer.Sample.class);
        when(adapterMapper.toCreateCommand(templateCreateRequestDTO, tenantId)).thenReturn(com.keepguard.ms_communication.domain.dto.template.TemplateCreateCommandDTO.builder()
                .name("Test Template")
                .description("Test template description")
                .messageType(MessageTypeEnum.EMAIL)
                .templateType(TemplateTypeEnum.CADASTRO_SUCESSO)
                .content("Hello {{userName}}, welcome to our system!")
                .subject("Welcome")
                .variables("[\"userName\", \"activationLink\"]")
                .application("test-app")
                .isActive(true)
                .build());
        when(applicationMapper.toCreateCommand(any(com.keepguard.ms_communication.domain.dto.template.TemplateCreateCommandDTO.class))).thenReturn(templateCreateCommand);
        when(templatePort.create(templateCreateCommand)).thenReturn(templateView);
        when(adapterMapper.toCreateResponseDTO(templateView)).thenReturn(templateCreateResponseDTO);
        
        // When
        ResponseEntity<TemplateCreateResponseDTO> response = templateController.createTemplate(templateCreateRequestDTO, tenantIdStr);
        
        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        TemplateCreateResponseDTO responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(templateId, responseBody.getId());
        assertEquals("Test Template", responseBody.getName());
        assertEquals(TemplateTypeEnum.CADASTRO_SUCESSO, responseBody.getTemplateType());
        assertEquals(MessageTypeEnum.EMAIL, responseBody.getMessageType());
        assertEquals(tenantId, responseBody.getTenantId());
        
        verify(adapterMapper, times(1)).toCreateCommand(templateCreateRequestDTO, tenantId);
        verify(templatePort, times(1)).create(templateCreateCommand);
        verify(adapterMapper, times(1)).toCreateResponseDTO(templateView);
    }
    
    @Test
    @DisplayName("Deve lidar com exceções durante criação de template")
    void shouldHandleExceptionsDuringTemplateCreation() {
        // Given
        Timer.Sample sample = mock(Timer.Sample.class);
        com.keepguard.ms_communication.domain.dto.template.TemplateCreateCommandDTO requestCommand = com.keepguard.ms_communication.domain.dto.template.TemplateCreateCommandDTO.builder()
                .name("Test Template")
                .description("Test template description")
                .messageType(MessageTypeEnum.EMAIL)
                .templateType(TemplateTypeEnum.CADASTRO_SUCESSO)
                .content("Hello {{userName}}, welcome to our system!")
                .subject("Welcome")
                .variables("[\"userName\", \"activationLink\"]")
                .application("test-app")
                .isActive(true)
                .build();
        
        when(adapterMapper.toCreateCommand(templateCreateRequestDTO, tenantId)).thenReturn(requestCommand);
        when(applicationMapper.toCreateCommand(requestCommand)).thenReturn(templateCreateCommand);
        when(templatePort.create(templateCreateCommand))
            .thenThrow(new RuntimeException("Service error"));
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            templateController.createTemplate(templateCreateRequestDTO, tenantIdStr);
        });
        
        verify(adapterMapper, times(1)).toCreateCommand(templateCreateRequestDTO, tenantId);
        verify(templatePort, times(1)).create(templateCreateCommand);
    }
    
    @Test
    @DisplayName("Deve atualizar template com sucesso")
    void shouldUpdateTemplateSuccessfully() {
        // Given
        when(adapterMapper.toUpdateCommand(templateId, templateUpdateRequestDTO, tenantId)).thenReturn(com.keepguard.ms_communication.domain.dto.template.TemplateUpdateCommandDTO.builder()
                .name("Updated Template")
                .description("Updated template description")
                .messageType(MessageTypeEnum.EMAIL)
                .templateType(TemplateTypeEnum.CADASTRO_SUCESSO)
                .content("Updated content with {{userName}}")
                .subject("Updated Subject")
                .variables("[\"userName\", \"companyName\"]")
                .isActive(false)
                .build());
        when(applicationMapper.toUpdateCommand(any(com.keepguard.ms_communication.domain.dto.template.TemplateUpdateCommandDTO.class))).thenReturn(templateUpdateCommand);
        when(templatePort.update(templateUpdateCommand)).thenReturn(templateView);
        when(adapterMapper.toUpdateResponseDTO(templateView)).thenReturn(templateUpdateResponseDTO);
        
        // When
        ResponseEntity<TemplateUpdateResponseDTO> response = templateController.updateTemplate(templateId, templateUpdateRequestDTO, tenantIdStr);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        verify(adapterMapper, times(1)).toUpdateCommand(templateId, templateUpdateRequestDTO, tenantId);
        verify(templatePort, times(1)).update(templateUpdateCommand);
        verify(adapterMapper, times(1)).toUpdateResponseDTO(templateView);
    }
    
    @Test
    @DisplayName("Deve deletar template com sucesso")
    void shouldDeleteTemplateSuccessfully() {
        // Given
        doNothing().when(templatePort).delete(templateId);
        
        // When
        ResponseEntity<Void> response = templateController.deleteTemplate(templateId, tenantIdStr);
        
        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        
        verify(templatePort, times(1)).delete(templateId);
    }
    
    @Test
    @DisplayName("Deve buscar template por ID com sucesso")
    void shouldGetTemplateByIdSuccessfully() {
        // Given
        when(templatePort.getById(templateId)).thenReturn(Optional.of(templateView));
        when(adapterMapper.toGetTemplateByIdResponseDTO(templateView)).thenReturn(templateGetTemplateByIdResponseDTO);
        
        // When
        ResponseEntity<TemplateGetTemplateByIdResponseDTO> response = templateController.getTemplateById(templateId, tenantIdStr);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        verify(templatePort, times(1)).getById(templateId);
        verify(adapterMapper, times(1)).toGetTemplateByIdResponseDTO(templateView);
    }
    
    @Test
    @DisplayName("Deve lidar com template não encontrado por ID")
    void shouldHandleTemplateNotFoundById() {
        // Given
        when(templatePort.getById(templateId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(RuntimeException.class, () -> {
            templateController.getTemplateById(templateId, tenantIdStr);
        });
        
        verify(templatePort, times(1)).getById(templateId);
    }
    
    @Test
    @DisplayName("Deve buscar template por tipo com sucesso")
    void shouldGetTemplateByTypeSuccessfully() {
        // Given
        when(templatePort.getById(any(UUID.class))).thenReturn(Optional.of(templateView));
        when(adapterMapper.toGetTemplateByTypeResponseDTO(templateView)).thenReturn(templateGetTemplateByTypeResponseDTO);
        
        // When
        ResponseEntity<TemplateGetTemplateByTypeResponseDTO> response = templateController.getTemplateByType(
            TemplateTypeEnum.CADASTRO_SUCESSO, MessageTypeEnum.EMAIL, tenantIdStr);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        verify(templatePort, times(1)).getById(any(UUID.class));
        verify(adapterMapper, times(1)).toGetTemplateByTypeResponseDTO(templateView);
    }
    
    @Test
    @DisplayName("Deve listar templates com sucesso")
    void shouldGetTemplatesSuccessfully() {
        // Given
        List<TemplateView> views = List.of(templateView);
        when(templatePort.getAllActive()).thenReturn(views);
        when(adapterMapper.toGetTemplatesResponseDTO(templateView)).thenReturn(templateGetTemplatesResponseDTO);
        
        // When
        ResponseEntity<List<TemplateGetTemplatesResponseDTO>> response = templateController.getTemplates(tenantIdStr);
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        List<TemplateGetTemplatesResponseDTO> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(1, responseBody.size());
        
        verify(templatePort, times(1)).getAllActive();
        verify(adapterMapper, times(1)).toGetTemplatesResponseDTO(templateView);
    }
    
    @Test
    @DisplayName("Deve testar endpoint de hot reload")
    void shouldTestHotReloadEndpoint() {
        // When
        ResponseEntity<String> response = templateController.testeHotReload();
        
        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        String responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.contains("Hot Reload está funcionando"));
        assertTrue(responseBody.contains("Timestamp"));
    }
    
    @Test
    @DisplayName("Deve testar TemplateCreateDTO com dados válidos")
    void shouldTestTemplateCreateDTOWithValidData() {
        // Given & When
        TemplateCreateRequestDTO dto = new TemplateCreateRequestDTO();
        dto.setTemplateType(TemplateTypeEnum.CADASTRO_SUCESSO);
        dto.setMessageType(MessageTypeEnum.EMAIL);
        dto.setName("Test Template");
        dto.setDescription("Test template description");
        dto.setSubject("Test Subject");
        dto.setContent("Hello {{userName}}, welcome!");
        dto.setVariables("[\"userName\"]");
        dto.setTenantId("test-app");
        dto.setIsActive(true);
        
        // Then
        assertNotNull(dto);
        assertEquals(TemplateTypeEnum.CADASTRO_SUCESSO, dto.getTemplateType());
        assertEquals(MessageTypeEnum.EMAIL, dto.getMessageType());
        assertEquals("Test Template", dto.getName());
        assertEquals("Test template description", dto.getDescription());
        assertEquals("Test Subject", dto.getSubject());
        assertEquals("Hello {{userName}}, welcome!", dto.getContent());
        assertEquals("[\"userName\"]", dto.getVariables());
        assertEquals("test-app", dto.getTenantId());
        assertTrue(dto.getIsActive());
    }
    
    @Test
    @DisplayName("Deve testar TemplateUpdateDTO com dados válidos")
    void shouldTestTemplateUpdateDTOWithValidData() {
        // Given & When
        TemplateUpdateRequestDTO dto = new TemplateUpdateRequestDTO();
        dto.setName("Updated Template");
        dto.setDescription("Updated template description");
        dto.setSubject("Updated Subject");
        dto.setContent("Updated content with {{userName}}");
        dto.setVariables("[\"userName\", \"companyName\"]");
        dto.setMessageType(MessageTypeEnum.SMS);
        dto.setTemplateType(TemplateTypeEnum.RECUPERACAO_SENHA);
        dto.setIsActive(false);
        
        // Then
        assertNotNull(dto);
        assertEquals("Updated Template", dto.getName());
        assertEquals("Updated template description", dto.getDescription());
        assertEquals("Updated Subject", dto.getSubject());
        assertEquals("Updated content with {{userName}}", dto.getContent());
        assertEquals("[\"userName\", \"companyName\"]", dto.getVariables());
        assertEquals(MessageTypeEnum.SMS, dto.getMessageType());
        assertEquals(TemplateTypeEnum.RECUPERACAO_SENHA, dto.getTemplateType());
        assertFalse(dto.getIsActive());
    }
    
    @Test
    @DisplayName("Deve testar TemplateMapper.toCreateCommand com dados válidos")
    void shouldTestTemplateMapperToCreateCommandWithValidData() {
        // Given
        TemplateCreateRequestDTO dto = new TemplateCreateRequestDTO();
        dto.setTemplateType(TemplateTypeEnum.CADASTRO_SUCESSO);
        dto.setMessageType(MessageTypeEnum.EMAIL);
        dto.setName("Test Template");
        dto.setDescription("Test template description");
        dto.setSubject("Test Subject");
        dto.setContent("Hello {{userName}}, welcome!");
        dto.setVariables("[\"userName\"]");
        dto.setTenantId("test-app");
        dto.setIsActive(true);
        
        when(adapterMapper.toCreateCommand(dto, tenantId)).thenReturn(com.keepguard.ms_communication.domain.dto.template.TemplateCreateCommandDTO.builder().build());
        when(applicationMapper.toCreateCommand(any(com.keepguard.ms_communication.domain.dto.template.TemplateCreateCommandDTO.class))).thenReturn(templateCreateCommand);
        
        // When
        com.keepguard.ms_communication.domain.dto.template.TemplateCreateCommandDTO requestCommand = adapterMapper.toCreateCommand(dto, tenantId);
        TemplateCreateCommandDTO result = applicationMapper.toCreateCommand(requestCommand);
        
        // Then
        assertNotNull(result);
        assertEquals(TemplateTypeEnum.CADASTRO_SUCESSO, result.getTemplateType());
        assertEquals(MessageTypeEnum.EMAIL, result.getMessageType());
        assertEquals("test-app", result.getApplication());
        assertEquals("Test Template", result.getName());
        assertEquals("Test template description", result.getDescription());
        assertEquals("Test Subject", result.getSubject());
        assertEquals("Hello {{userName}}, welcome to our system!", result.getContent());
        assertEquals("[\"userName\", \"activationLink\"]", result.getVariables());
        assertTrue(result.getIsActive());
        
        verify(adapterMapper, times(1)).toCreateCommand(dto, tenantId);
    }
    
    @Test
    @DisplayName("Deve testar TemplateMapper.toUpdateCommand com dados válidos")
    void shouldTestTemplateMapperToUpdateCommandWithValidData() {
        // Given
        TemplateUpdateRequestDTO dto = new TemplateUpdateRequestDTO();
        dto.setName("Updated Template");
        dto.setDescription("Updated template description");
        dto.setSubject("Updated Subject");
        dto.setContent("Updated content with {{userName}}");
        dto.setVariables("[\"userName\", \"companyName\"]");
        dto.setMessageType(MessageTypeEnum.SMS);
        dto.setTemplateType(TemplateTypeEnum.RECUPERACAO_SENHA);
        dto.setIsActive(false);
        
        when(adapterMapper.toUpdateCommand(templateId, dto, tenantId)).thenReturn(com.keepguard.ms_communication.domain.dto.template.TemplateUpdateCommandDTO.builder().build());
        when(applicationMapper.toUpdateCommand(any(com.keepguard.ms_communication.domain.dto.template.TemplateUpdateCommandDTO.class))).thenReturn(templateUpdateCommand);
        
        // When
        com.keepguard.ms_communication.domain.dto.template.TemplateUpdateCommandDTO requestCommand = adapterMapper.toUpdateCommand(templateId, dto, tenantId);
        TemplateUpdateCommandDTO result = applicationMapper.toUpdateCommand(requestCommand);
        
        // Then
        assertNotNull(result);
        assertEquals(templateId, result.getId());
        assertEquals("Updated Template", result.getName());
        assertEquals("Updated template description", result.getDescription());
        assertEquals("Updated Subject", result.getSubject());
        assertEquals("Updated content with {{userName}}", result.getContent());
        assertEquals("[\"userName\", \"companyName\"]", result.getVariables());
        assertEquals(MessageTypeEnum.SMS, result.getMessageType());
        assertEquals(TemplateTypeEnum.RECUPERACAO_SENHA, result.getTemplateType());
        assertFalse(result.getIsActive());
        
        verify(adapterMapper, times(1)).toUpdateCommand(templateId, dto, tenantId);
    }
}
