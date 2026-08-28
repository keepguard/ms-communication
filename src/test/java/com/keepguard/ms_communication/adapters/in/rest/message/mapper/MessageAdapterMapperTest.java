package com.keepguard.ms_communication.adapters.in.rest.message.mapper;

import com.keepguard.ms_communication.adapters.in.rest.message.dto.request.MessageSendRequestDTO;
import com.keepguard.ms_communication.domain.dto.message.MessageSendCommandDTO;
import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para MessageAdapterMapper
 */
@ExtendWith(MockitoExtension.class)
class MessageAdapterMapperTest {

    @InjectMocks
    private MessageAdapterMapper mapper;

    private MessageSendRequestDTO requestDTO;
    private UUID companyId;
    private Map<String, Object> variables;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        
        variables = new HashMap<>();
        variables.put("userName", "Test User");
        variables.put("activationLink", "https://example.com/activate");

        requestDTO = new MessageSendRequestDTO();
        requestDTO.setMessageType(MessageTypeEnum.EMAIL);
        requestDTO.setRecipient("test@example.com");
        requestDTO.setTemplateType(TemplateTypeEnum.CADASTRO_SUCESSO);
        requestDTO.setSubject("Welcome to our system");
        requestDTO.setContent("Hello {{userName}}!");
        requestDTO.setCommunicationType(CommunicationTypeEnum.EMAIL);
        requestDTO.setVariables(variables);
    }

    @Test
    @DisplayName("Deve mapear MessageSendRequestDTO para MessageSendCommandDTO com sucesso")
    void shouldMapMessageSendRequestDTOToMessageSendCommandDTO() {
        // When
        MessageSendCommandDTO command = mapper.toSendCommand(requestDTO, companyId);

        // Then
        assertNotNull(command);
        assertEquals(companyId, command.getCompanyId());
        assertEquals(CommunicationTypeEnum.EMAIL, command.getCommunicationType());
        assertEquals("test@example.com", command.getRecipient());
        assertEquals("Welcome to our system", command.getSubject());
        assertEquals("Hello {{userName}}!", command.getContent());
        assertEquals("EMAIL", command.getMessageType());
        assertEquals("CADASTRO_SUCESSO", command.getTemplateType());
        assertNotNull(command.getVariables());
        assertEquals("Test User", command.getVariables().get("userName"));
        assertEquals("https://example.com/activate", command.getVariables().get("activationLink"));
    }

    @Test
    @DisplayName("Deve mapear MessageSendRequestDTO sem variáveis")
    void shouldMapMessageSendRequestDTOWithoutVariables() {
        // Given
        requestDTO.setVariables(null);

        // When
        MessageSendCommandDTO command = mapper.toSendCommand(requestDTO, companyId);

        // Then
        assertNotNull(command);
        assertEquals(companyId, command.getCompanyId());
        assertEquals(CommunicationTypeEnum.EMAIL, command.getCommunicationType());
        assertEquals("test@example.com", command.getRecipient());
        assertNull(command.getVariables());
    }

    @Test
    @DisplayName("Deve mapear MessageSendRequestDTO com messageType null")
    void shouldMapMessageSendRequestDTOWithNullMessageType() {
        // Given
        requestDTO.setMessageType(null);

        // When
        MessageSendCommandDTO command = mapper.toSendCommand(requestDTO, companyId);

        // Then
        assertNotNull(command);
        assertNull(command.getMessageType());
    }

    @Test
    @DisplayName("Deve mapear MessageSendRequestDTO com templateType null")
    void shouldMapMessageSendRequestDTOWithNullTemplateType() {
        // Given
        requestDTO.setTemplateType(null);

        // When
        MessageSendCommandDTO command = mapper.toSendCommand(requestDTO, companyId);

        // Then
        assertNotNull(command);
        assertNull(command.getTemplateType());
    }

    @Test
    @DisplayName("Deve retornar null quando requestDTO for null")
    void shouldReturnNullWhenRequestDTOIsNull() {
        // When
        MessageSendCommandDTO command = mapper.toSendCommand(null, companyId);

        // Then
        assertNull(command);
    }

    @Test
    @DisplayName("Deve mapear MessageSendRequestDTO com todos os tipos de comunicação")
    void shouldMapMessageSendRequestDTOWithAllCommunicationTypes() {
        // Test EMAIL
        requestDTO.setCommunicationType(CommunicationTypeEnum.EMAIL);
        MessageSendCommandDTO emailCommand = mapper.toSendCommand(requestDTO, companyId);
        assertEquals(CommunicationTypeEnum.EMAIL, emailCommand.getCommunicationType());

        // Test SMS
        requestDTO.setCommunicationType(CommunicationTypeEnum.SMS);
        MessageSendCommandDTO smsCommand = mapper.toSendCommand(requestDTO, companyId);
        assertEquals(CommunicationTypeEnum.SMS, smsCommand.getCommunicationType());

        // Test WHATSAPP
        requestDTO.setCommunicationType(CommunicationTypeEnum.WHATSAPP);
        MessageSendCommandDTO whatsappCommand = mapper.toSendCommand(requestDTO, companyId);
        assertEquals(CommunicationTypeEnum.WHATSAPP, whatsappCommand.getCommunicationType());

        // Test PUSH_NOTIFICATION
        requestDTO.setCommunicationType(CommunicationTypeEnum.PUSH_NOTIFICATION);
        MessageSendCommandDTO pushCommand = mapper.toSendCommand(requestDTO, companyId);
        assertEquals(CommunicationTypeEnum.PUSH_NOTIFICATION, pushCommand.getCommunicationType());

        // Test TELEGRAM
        requestDTO.setCommunicationType(CommunicationTypeEnum.TELEGRAM);
        MessageSendCommandDTO telegramCommand = mapper.toSendCommand(requestDTO, companyId);
        assertEquals(CommunicationTypeEnum.TELEGRAM, telegramCommand.getCommunicationType());
    }

    @Test
    @DisplayName("Deve mapear MessageSendRequestDTO com subject e content vazios")
    void shouldMapMessageSendRequestDTOWithEmptySubjectAndContent() {
        // Given
        requestDTO.setSubject("");
        requestDTO.setContent("");

        // When
        MessageSendCommandDTO command = mapper.toSendCommand(requestDTO, companyId);

        // Then
        assertNotNull(command);
        assertEquals("", command.getSubject());
        assertEquals("", command.getContent());
    }

    @Test
    @DisplayName("Deve mapear MessageSendRequestDTO com variáveis complexas")
    void shouldMapMessageSendRequestDTOWithComplexVariables() {
        // Given
        Map<String, Object> complexVariables = new HashMap<>();
        complexVariables.put("userName", "Test User");
        complexVariables.put("accountNumber", 12345);
        complexVariables.put("isActive", true);
        complexVariables.put("balance", 1000.50);
        
        Map<String, Object> nestedMap = new HashMap<>();
        nestedMap.put("street", "Test Street");
        nestedMap.put("city", "Test City");
        complexVariables.put("address", nestedMap);
        
        requestDTO.setVariables(complexVariables);

        // When
        MessageSendCommandDTO command = mapper.toSendCommand(requestDTO, companyId);

        // Then
        assertNotNull(command);
        assertNotNull(command.getVariables());
        assertEquals("Test User", command.getVariables().get("userName"));
        assertEquals(12345, command.getVariables().get("accountNumber"));
        assertEquals(true, command.getVariables().get("isActive"));
        assertEquals(1000.50, command.getVariables().get("balance"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> address = (Map<String, Object>) command.getVariables().get("address");
        assertNotNull(address);
        assertEquals("Test Street", address.get("street"));
        assertEquals("Test City", address.get("city"));
    }
}

