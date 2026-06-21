package com.keepguard.ms_communication.infrastructure.provider.n8n;

import com.keepguard.ms_communication.domain.entity.Provider;
import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.domain.enums.ProviderTypeEnum;
import com.keepguard.ms_communication.infrastructure.provider.n8n.payload.EmailPayload;
import com.keepguard.ms_communication.infrastructure.provider.n8n.payload.PushPayload;
import com.keepguard.ms_communication.infrastructure.provider.n8n.payload.SMSPayload;
import com.keepguard.ms_communication.infrastructure.provider.n8n.payload.WhatsAppPayload;
import com.keepguard.ms_communication.test.builder.ProviderTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

/**
 * Testes unitários para PayloadFactory
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Payload Factory Tests")
class PayloadFactoryTest {

    @InjectMocks
    private PayloadFactory payloadFactory;
    
    @Mock
    private N8NConfigParser configParser;
    
    private Provider provider;
    
    @BeforeEach
    void setUp() {
        provider = ProviderTestBuilder.aProvider()
            .withProviderType(ProviderTypeEnum.N8N)
            .withCommunicationType(CommunicationTypeEnum.EMAIL)
            .withUrl("https://api.example.com")
            .buildDomain();
    }
    
    @Test
    @DisplayName("Deve criar EmailPayload com sucesso")
    void shouldCreateEmailPayloadSuccessfully() {
        // Given
        String recipient = "test@example.com";
        String subject = "Test Subject";
        String content = "<h1>Test Content</h1>";
        
        // When
        EmailPayload payload = payloadFactory.createEmailPayload(provider, recipient, subject, content);
        
        // Then
        assertNotNull(payload);
        assertEquals(recipient, payload.getTo());
        assertEquals(subject, payload.getSubject());
        assertEquals("", payload.getMessage());
        assertEquals(content, payload.getHtml());
        assertEquals("", payload.getCc());
        assertEquals("", payload.getReplyTo());
    }
    
    @Test
    @DisplayName("Deve criar EmailPayload com valores nulos")
    void shouldCreateEmailPayloadWithNullValues() {
        // Given
        String recipient = null;
        String subject = null;
        String content = null;
        
        // When
        EmailPayload payload = payloadFactory.createEmailPayload(provider, recipient, subject, content);
        
        // Then
        assertNotNull(payload);
        assertNull(payload.getTo());
        assertNull(payload.getSubject());
        assertEquals("", payload.getMessage());
        assertNull(payload.getHtml());
        assertEquals("", payload.getCc());
        assertEquals("", payload.getReplyTo());
    }
    
    @Test
    @DisplayName("Deve criar EmailPayload com strings vazias")
    void shouldCreateEmailPayloadWithEmptyStrings() {
        // Given
        String recipient = "";
        String subject = "";
        String content = "";
        
        // When
        EmailPayload payload = payloadFactory.createEmailPayload(provider, recipient, subject, content);
        
        // Then
        assertNotNull(payload);
        assertEquals("", payload.getTo());
        assertEquals("", payload.getSubject());
        assertEquals("", payload.getMessage());
        assertEquals("", payload.getHtml());
        assertEquals("", payload.getCc());
        assertEquals("", payload.getReplyTo());
    }
    
    @Test
    @DisplayName("Deve criar EmailPayload com diferentes tipos de conteúdo HTML")
    void shouldCreateEmailPayloadWithDifferentHtmlContent() {
        // Given
        String recipient = "test@example.com";
        String subject = "Test Subject";
        String[] htmlContents = {
            "<h1>Simple HTML</h1>",
            "<div><p>Complex HTML with <strong>formatting</strong></p></div>",
            "<html><body><table><tr><td>Table content</td></tr></table></body></html>",
            "Plain text without HTML tags"
        };
        
        for (String content : htmlContents) {
            // When
            EmailPayload payload = payloadFactory.createEmailPayload(provider, recipient, subject, content);
            
            // Then
            assertNotNull(payload);
            assertEquals(content, payload.getHtml());
            assertEquals("", payload.getMessage());
        }
    }
    
    @Test
    @DisplayName("Deve criar SMSPayload com sucesso")
    void shouldCreateSMSPayloadSuccessfully() {
        // Given
        String recipient = "+5511999999999";
        String content = "Test SMS content";
        
        // When
        SMSPayload payload = payloadFactory.createSMSPayload(provider, recipient, content);
        
        // Then
        assertNotNull(payload);
        assertEquals(recipient, payload.getTo());
        assertEquals(content, payload.getMessage());
        assertEquals("sms", payload.getWorkflowType());
    }
    
    @Test
    @DisplayName("Deve criar SMSPayload com valores nulos")
    void shouldCreateSMSPayloadWithNullValues() {
        // Given
        String recipient = null;
        String content = null;
        
        // When
        SMSPayload payload = payloadFactory.createSMSPayload(provider, recipient, content);
        
        // Then
        assertNotNull(payload);
        assertNull(payload.getTo());
        assertNull(payload.getMessage());
        assertEquals("sms", payload.getWorkflowType());
    }
    
    @Test
    @DisplayName("Deve criar SMSPayload com strings vazias")
    void shouldCreateSMSPayloadWithEmptyStrings() {
        // Given
        String recipient = "";
        String content = "";
        
        // When
        SMSPayload payload = payloadFactory.createSMSPayload(provider, recipient, content);
        
        // Then
        assertNotNull(payload);
        assertEquals("", payload.getTo());
        assertEquals("", payload.getMessage());
        assertEquals("sms", payload.getWorkflowType());
    }
    
    @Test
    @DisplayName("Deve criar SMSPayload com diferentes formatos de telefone")
    void shouldCreateSMSPayloadWithDifferentPhoneFormats() {
        // Given
        String[] phoneNumbers = {
            "+5511999999999",
            "+5511888888888",
            "11999999999",
            "11888888888",
            "+1234567890",
            "1234567890"
        };
        String content = "Test SMS";
        
        for (String phoneNumber : phoneNumbers) {
            // When
            SMSPayload payload = payloadFactory.createSMSPayload(provider, phoneNumber, content);
            
            // Then
            assertNotNull(payload);
            assertEquals(phoneNumber, payload.getTo());
            assertEquals(content, payload.getMessage());
            assertEquals("sms", payload.getWorkflowType());
        }
    }
    
    @Test
    @DisplayName("Deve criar WhatsAppPayload com sucesso")
    void shouldCreateWhatsAppPayloadSuccessfully() {
        // Given
        String recipient = "chat-id-123";
        String content = "Test WhatsApp message";
        String messageType = "text";
        String templateType = "welcome";
        
        // When
        WhatsAppPayload payload = payloadFactory.createWhatsAppPayload(provider, recipient, content, messageType, templateType);
        
        // Then
        assertNotNull(payload);
        assertEquals(recipient, payload.getChatId());
        assertEquals(content, payload.getText());
        assertEquals(messageType, payload.getMessageType());
        assertEquals(templateType, payload.getTemplateType());
    }
    
    @Test
    @DisplayName("Deve criar WhatsAppPayload com valores nulos")
    void shouldCreateWhatsAppPayloadWithNullValues() {
        // Given
        String recipient = null;
        String content = null;
        String messageType = null;
        String templateType = null;
        
        // When
        WhatsAppPayload payload = payloadFactory.createWhatsAppPayload(provider, recipient, content, messageType, templateType);
        
        // Then
        assertNotNull(payload);
        assertNull(payload.getChatId());
        assertNull(payload.getText());
        assertNull(payload.getMessageType());
        assertNull(payload.getTemplateType());
    }
    
    @Test
    @DisplayName("Deve criar WhatsAppPayload com strings vazias")
    void shouldCreateWhatsAppPayloadWithEmptyStrings() {
        // Given
        String recipient = "";
        String content = "";
        String messageType = "";
        String templateType = "";
        
        // When
        WhatsAppPayload payload = payloadFactory.createWhatsAppPayload(provider, recipient, content, messageType, templateType);
        
        // Then
        assertNotNull(payload);
        assertEquals("", payload.getChatId());
        assertEquals("", payload.getText());
        assertEquals("", payload.getMessageType());
        assertEquals("", payload.getTemplateType());
    }
    
    @Test
    @DisplayName("Deve criar WhatsAppPayload com diferentes tipos de mensagem")
    void shouldCreateWhatsAppPayloadWithDifferentMessageTypes() {
        // Given
        String recipient = "chat-id-123";
        String content = "Test message";
        String[] messageTypes = {"text", "template", "media", "location"};
        String templateType = "welcome";
        
        for (String messageType : messageTypes) {
            // When
            WhatsAppPayload payload = payloadFactory.createWhatsAppPayload(provider, recipient, content, messageType, templateType);
            
            // Then
            assertNotNull(payload);
            assertEquals(messageType, payload.getMessageType());
        }
    }
    
    @Test
    @DisplayName("Deve criar WhatsAppPayload com diferentes tipos de template")
    void shouldCreateWhatsAppPayloadWithDifferentTemplateTypes() {
        // Given
        String recipient = "chat-id-123";
        String content = "Test message";
        String messageType = "template";
        String[] templateTypes = {"welcome", "confirmation", "reminder", "notification"};
        
        for (String templateType : templateTypes) {
            // When
            WhatsAppPayload payload = payloadFactory.createWhatsAppPayload(provider, recipient, content, messageType, templateType);
            
            // Then
            assertNotNull(payload);
            assertEquals(templateType, payload.getTemplateType());
        }
    }
    
    @Test
    @DisplayName("Deve criar PushPayload com sucesso")
    void shouldCreatePushPayloadSuccessfully() {
        // Given
        String recipient = "device-token-123";
        String subject = "Test Push Title";
        String content = "Test push notification content";
        
        // When
        PushPayload payload = payloadFactory.createPushPayload(provider, recipient, subject, content);
        
        // Then
        assertNotNull(payload);
        assertEquals(recipient, payload.getTo());
        assertEquals(subject, payload.getTitle());
        assertEquals(content, payload.getMessage());
        assertEquals("push", payload.getWorkflowType());
    }
    
    @Test
    @DisplayName("Deve criar PushPayload com valores nulos")
    void shouldCreatePushPayloadWithNullValues() {
        // Given
        String recipient = null;
        String subject = null;
        String content = null;
        
        // When
        PushPayload payload = payloadFactory.createPushPayload(provider, recipient, subject, content);
        
        // Then
        assertNotNull(payload);
        assertNull(payload.getTo());
        assertNull(payload.getTitle());
        assertNull(payload.getMessage());
        assertEquals("push", payload.getWorkflowType());
    }
    
    @Test
    @DisplayName("Deve criar PushPayload com strings vazias")
    void shouldCreatePushPayloadWithEmptyStrings() {
        // Given
        String recipient = "";
        String subject = "";
        String content = "";
        
        // When
        PushPayload payload = payloadFactory.createPushPayload(provider, recipient, subject, content);
        
        // Then
        assertNotNull(payload);
        assertEquals("", payload.getTo());
        assertEquals("", payload.getTitle());
        assertEquals("", payload.getMessage());
        assertEquals("push", payload.getWorkflowType());
    }
    
    @Test
    @DisplayName("Deve criar PushPayload com diferentes tipos de device tokens")
    void shouldCreatePushPayloadWithDifferentDeviceTokens() {
        // Given
        String[] deviceTokens = {
            "device-token-123",
            "firebase-token-456",
            "apns-token-789",
            "fcm-token-abc",
            "android-device-123",
            "ios-device-456"
        };
        String subject = "Test Push";
        String content = "Test content";
        
        for (String deviceToken : deviceTokens) {
            // When
            PushPayload payload = payloadFactory.createPushPayload(provider, deviceToken, subject, content);
            
            // Then
            assertNotNull(payload);
            assertEquals(deviceToken, payload.getTo());
            assertEquals(subject, payload.getTitle());
            assertEquals(content, payload.getMessage());
            assertEquals("push", payload.getWorkflowType());
        }
    }
    
    @Test
    @DisplayName("Deve criar PushPayload com diferentes títulos")
    void shouldCreatePushPayloadWithDifferentTitles() {
        // Given
        String recipient = "device-token-123";
        String[] titles = {
            "Welcome!",
            "New Message",
            "Security Alert",
            "Reminder",
            "System Notification"
        };
        String content = "Test content";
        
        for (String title : titles) {
            // When
            PushPayload payload = payloadFactory.createPushPayload(provider, recipient, title, content);
            
            // Then
            assertNotNull(payload);
            assertEquals(title, payload.getTitle());
        }
    }
    
    @Test
    @DisplayName("Deve criar payloads com provider nulo")
    void shouldCreatePayloadsWithNullProvider() {
        // Given
        String recipient = "test@example.com";
        String subject = "Test Subject";
        String content = "Test content";
        
        // When & Then - EmailPayload
        EmailPayload emailPayload = payloadFactory.createEmailPayload(null, recipient, subject, content);
        assertNotNull(emailPayload);
        assertEquals(recipient, emailPayload.getTo());
        
        // When & Then - SMSPayload
        SMSPayload smsPayload = payloadFactory.createSMSPayload(null, recipient, content);
        assertNotNull(smsPayload);
        assertEquals(recipient, smsPayload.getTo());
        
        // When & Then - WhatsAppPayload
        WhatsAppPayload whatsAppPayload = payloadFactory.createWhatsAppPayload(null, recipient, content, "text", "template");
        assertNotNull(whatsAppPayload);
        assertEquals(recipient, whatsAppPayload.getChatId());
        
        // When & Then - PushPayload
        PushPayload pushPayload = payloadFactory.createPushPayload(null, recipient, subject, content);
        assertNotNull(pushPayload);
        assertEquals(recipient, pushPayload.getTo());
    }
    
    @Test
    @DisplayName("Deve criar payloads com provider de diferentes tipos")
    void shouldCreatePayloadsWithDifferentProviderTypes() {
        // Given
        String recipient = "test@example.com";
        String subject = "Test Subject";
        String content = "Test content";
        
        ProviderTypeEnum[] providerTypes = {ProviderTypeEnum.N8N, ProviderTypeEnum.SENDGRID};
        
        for (ProviderTypeEnum providerType : providerTypes) {
            Provider testProvider = ProviderTestBuilder.aProvider()
                .withProviderType(providerType)
                .buildDomain();
            
            // When & Then - EmailPayload
            EmailPayload emailPayload = payloadFactory.createEmailPayload(testProvider, recipient, subject, content);
            assertNotNull(emailPayload);
            assertEquals(recipient, emailPayload.getTo());
            
            // When & Then - SMSPayload
            SMSPayload smsPayload = payloadFactory.createSMSPayload(testProvider, recipient, content);
            assertNotNull(smsPayload);
            assertEquals(recipient, smsPayload.getTo());
        }
    }
}
