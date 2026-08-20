package com.keepguard.ms_communication.adapters.in.rest.message;

import com.keepguard.lib_common.utils.ValidationUtils;
import com.keepguard.ms_communication.adapters.in.rest.message.dto.request.MessageSendRequestDTO;
import com.keepguard.ms_communication.adapters.in.rest.message.dto.response.MessageSendResponseDTO;
import com.keepguard.ms_communication.adapters.in.rest.message.mapper.MessageAdapterMapper;
import com.keepguard.ms_communication.application.port.in.service.MessagePort;
import com.keepguard.ms_communication.domain.dto.message.MessageSendCommandDTO;
import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para MessageController
 */
@ExtendWith(MockitoExtension.class)
class MessageControllerTest {

    @Mock
    private MessagePort messagePort;

    @Mock
    private MessageAdapterMapper adapterMapper;

    @InjectMocks
    private MessageController messageController;

    private MessageSendRequestDTO messageSendRequestDTO;
    private MessageSendCommandDTO messageSendCommandDTO;
    private String tenantIdStr;
    private UUID tenantId;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        tenantIdStr = tenantId.toString();
        

        // Setup request DTO
        messageSendRequestDTO = new MessageSendRequestDTO();
        messageSendRequestDTO.setMessageType(MessageTypeEnum.EMAIL);
        messageSendRequestDTO.setRecipient("test@example.com");
        messageSendRequestDTO.setTemplateType(TemplateTypeEnum.CADASTRO_SUCESSO);
        messageSendRequestDTO.setSubject("Test Subject");
        messageSendRequestDTO.setContent("Test Content");
        messageSendRequestDTO.setCommunicationType(CommunicationTypeEnum.EMAIL);
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("userName", "Test User");
        messageSendRequestDTO.setVariables(variables);

        // Setup command DTO
        messageSendCommandDTO = MessageSendCommandDTO.builder()
                .tenantId(tenantId)
                .communicationType(CommunicationTypeEnum.EMAIL)
                .recipient("test@example.com")
                .subject("Test Subject")
                .content("Test Content")
                .messageType("EMAIL")
                .templateType("CADASTRO_SUCESSO")
                .variables(variables)
                .build();
    }

    @Test
    @DisplayName("Deve enviar mensagem com sucesso")
    void shouldSendMessageSuccessfully() {
        // Given
        try (MockedStatic<ValidationUtils> validationUtilsMock = mockStatic(ValidationUtils.class)) {
            validationUtilsMock.when(() -> ValidationUtils.validateTenantId(tenantIdStr))
                    .thenReturn(tenantId);

            when(adapterMapper.toSendCommand(messageSendRequestDTO, tenantId))
                    .thenReturn(messageSendCommandDTO);
            when(messagePort.sendWithFallback(messageSendCommandDTO))
                    .thenReturn(true);

            // When
            ResponseEntity<MessageSendResponseDTO> response = messageController.send(messageSendRequestDTO, tenantIdStr);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            MessageSendResponseDTO responseBody = response.getBody();
            assertNotNull(responseBody);
            assertTrue(responseBody.isSuccess());
            assertEquals("Mensagem enviada com sucesso", responseBody.getMessage());

            verify(adapterMapper, times(1)).toSendCommand(messageSendRequestDTO, tenantId);
            verify(messagePort, times(1)).sendWithFallback(messageSendCommandDTO);
        }
    }

    @Test
    @DisplayName("Deve retornar falha ao enviar mensagem quando o envio falhar")
    void shouldReturnFailureWhenSendingMessageFails() {
        // Given
        try (MockedStatic<ValidationUtils> validationUtilsMock = mockStatic(ValidationUtils.class)) {
            validationUtilsMock.when(() -> ValidationUtils.validateTenantId(tenantIdStr))
                    .thenReturn(tenantId);

            when(adapterMapper.toSendCommand(messageSendRequestDTO, tenantId))
                    .thenReturn(messageSendCommandDTO);
            when(messagePort.sendWithFallback(messageSendCommandDTO))
                    .thenReturn(false);

            // When
            ResponseEntity<MessageSendResponseDTO> response = messageController.send(messageSendRequestDTO, tenantIdStr);

            // Then
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            MessageSendResponseDTO responseBody = response.getBody();
            assertNotNull(responseBody);
            assertFalse(responseBody.isSuccess());
            assertEquals("Falha ao enviar mensagem", responseBody.getMessage());

            verify(adapterMapper, times(1)).toSendCommand(messageSendRequestDTO, tenantId);
            verify(messagePort, times(1)).sendWithFallback(messageSendCommandDTO);
        }
    }

    @Test
    @DisplayName("Deve lançar exceção quando X-Tenant-Id for inválido")
    void shouldThrowExceptionWhenTenantIdIsInvalid() {
        // Given
        String invalidTenantId = "invalid-uuid";

        try (MockedStatic<ValidationUtils> validationUtilsMock = mockStatic(ValidationUtils.class)) {
            validationUtilsMock.when(() -> ValidationUtils.validateTenantId(tenantIdStr))
                    .thenThrow(new IllegalArgumentException("X-Tenant-Id inválido"));

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> {
                messageController.send(messageSendRequestDTO, invalidTenantId);
            });

            verify(adapterMapper, never()).toSendCommand(any(), any());
            verify(messagePort, never()).sendWithFallback(any());
        }
    }

    @Test
    @DisplayName("Deve testar MessageSendRequestDTO com dados válidos")
    void shouldTestMessageSendRequestDTOWithValidData() {
        // Given & When
        MessageSendRequestDTO dto = new MessageSendRequestDTO();
        dto.setMessageType(MessageTypeEnum.EMAIL);
        dto.setRecipient("test@example.com");
        dto.setTemplateType(TemplateTypeEnum.CADASTRO_SUCESSO);
        dto.setSubject("Test Subject");
        dto.setContent("Test Content");
        dto.setCommunicationType(CommunicationTypeEnum.EMAIL);
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("userName", "Test User");
        dto.setVariables(variables);

        // Then
        assertNotNull(dto);
        assertEquals(MessageTypeEnum.EMAIL, dto.getMessageType());
        assertEquals("test@example.com", dto.getRecipient());
        assertEquals(TemplateTypeEnum.CADASTRO_SUCESSO, dto.getTemplateType());
        assertEquals("Test Subject", dto.getSubject());
        assertEquals("Test Content", dto.getContent());
        assertEquals(CommunicationTypeEnum.EMAIL, dto.getCommunicationType());
        assertNotNull(dto.getVariables());
        assertEquals("Test User", dto.getVariables().get("userName"));
    }

    @Test
    @DisplayName("Deve testar MessageSendResponseDTO com sucesso")
    void shouldTestMessageSendResponseDTOSuccess() {
        // Given & When
        MessageSendResponseDTO dto = MessageSendResponseDTO.builder()
                .success(true)
                .message("Mensagem enviada com sucesso")
                .build();

        // Then
        assertNotNull(dto);
        assertTrue(dto.isSuccess());
        assertEquals("Mensagem enviada com sucesso", dto.getMessage());
    }

    @Test
    @DisplayName("Deve testar MessageSendResponseDTO com falha")
    void shouldTestMessageSendResponseDTOFailure() {
        // Given & When
        MessageSendResponseDTO dto = MessageSendResponseDTO.builder()
                .success(false)
                .message("Falha ao enviar mensagem")
                .build();

        // Then
        assertNotNull(dto);
        assertFalse(dto.isSuccess());
        assertEquals("Falha ao enviar mensagem", dto.getMessage());
    }
}

