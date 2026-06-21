package com.keepguard.ms_communication.infrastructure.provider.communication;

import com.keepguard.ms_communication.application.service.exception.MessageSendException;
import com.keepguard.ms_communication.application.service.exception.ProviderConnectionException;
import com.keepguard.ms_communication.domain.entity.Provider;
import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.domain.enums.ProviderTypeEnum;
import com.keepguard.ms_communication.infrastructure.provider.n8n.N8NConfigParser;
import com.keepguard.ms_communication.infrastructure.provider.n8n.N8NHttpClient;
import com.keepguard.ms_communication.infrastructure.provider.n8n.PayloadFactory;
import com.keepguard.ms_communication.infrastructure.provider.n8n.payload.EmailPayload;
import com.keepguard.ms_communication.infrastructure.provider.n8n.payload.SMSPayload;
import com.keepguard.ms_communication.infrastructure.provider.n8n.payload.WhatsAppPayload;
import com.keepguard.ms_communication.infrastructure.provider.n8n.payload.PushPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class N8NCommunicationProviderTest {

    @Mock
    private N8NHttpClient httpClient;

    @Mock
    private PayloadFactory payloadFactory;

    @Mock
    private N8NConfigParser configParser;

    @InjectMocks
    private N8NCommunicationProvider provider;

    private Provider testProvider;
    private String testUrl = "https://n8n.example.com/webhook";

    @BeforeEach
    void setUp() {
        testProvider = Provider.builder()
                .id(UUID.randomUUID())
                .name("Test N8N Provider")
                .providerType(ProviderTypeEnum.N8N)
                .communicationType(CommunicationTypeEnum.EMAIL)
                .url(testUrl)
                .configuration("{\"apiKey\":\"test-key\"}")
                .isActive(true)
                .build();
    }

    @Test
    void shouldSendEmailSuccessfully() {
        // Given
        String recipient = "test@example.com";
        String subject = "Test Subject";
        String content = "Test Content";
        EmailPayload emailPayload = mock(EmailPayload.class);
        ResponseEntity<String> response = new ResponseEntity<>("Success", HttpStatus.OK);

        when(payloadFactory.createEmailPayload(eq(testProvider), eq(recipient), eq(subject), eq(content)))
                .thenReturn(emailPayload);
        when(httpClient.sendEmail(eq(testUrl), eq(emailPayload))).thenReturn(response);

        // When
        boolean result = provider.sendMessage(testProvider, recipient, subject, content,
                CommunicationTypeEnum.EMAIL, "test", "test");

        // Then
        assertTrue(result);
        verify(httpClient).sendEmail(testUrl, emailPayload);
    }

    @Test
    void shouldSendSMSSuccessfully() {
        // Given
        String recipient = "+5511999999999";
        String content = "Test SMS";
        SMSPayload smsPayload = mock(SMSPayload.class);
        ResponseEntity<String> response = new ResponseEntity<>("Success", HttpStatus.OK);

        when(payloadFactory.createSMSPayload(eq(testProvider), eq(recipient), eq(content)))
                .thenReturn(smsPayload);
        when(httpClient.sendSMS(eq(testUrl), eq(smsPayload))).thenReturn(response);

        // When
        boolean result = provider.sendMessage(testProvider, recipient, null, content,
                CommunicationTypeEnum.SMS, "test", "test");

        // Then
        assertTrue(result);
        verify(httpClient).sendSMS(testUrl, smsPayload);
    }

    @Test
    void shouldSendWhatsAppSuccessfully() {
        // Given
        String recipient = "5511999999999";
        String content = "Test WhatsApp";
        String messageType = "template";
        String templateType = "notification";
        WhatsAppPayload whatsAppPayload = mock(WhatsAppPayload.class);
        ResponseEntity<String> response = new ResponseEntity<>("Success", HttpStatus.OK);

        when(payloadFactory.createWhatsAppPayload(eq(testProvider), eq(recipient), eq(content), eq(messageType), eq(templateType)))
                .thenReturn(whatsAppPayload);
        when(httpClient.sendWhatsApp(eq(testUrl), eq(whatsAppPayload))).thenReturn(response);

        // When
        boolean result = provider.sendMessage(testProvider, recipient, null, content,
                CommunicationTypeEnum.WHATSAPP, messageType, templateType);

        // Then
        assertTrue(result);
        verify(httpClient).sendWhatsApp(testUrl, whatsAppPayload);
    }

    @Test
    void shouldSendPushNotificationSuccessfully() {
        // Given
        String recipient = "device-token-123";
        String subject = "Test Push";
        String content = "Test Push Content";
        PushPayload pushPayload = mock(PushPayload.class);
        ResponseEntity<String> response = new ResponseEntity<>("Success", HttpStatus.OK);

        when(payloadFactory.createPushPayload(eq(testProvider), eq(recipient), eq(subject), eq(content)))
                .thenReturn(pushPayload);
        when(httpClient.sendPush(eq(testUrl), eq(pushPayload))).thenReturn(response);

        // When
        boolean result = provider.sendMessage(testProvider, recipient, subject, content,
                CommunicationTypeEnum.PUSH_NOTIFICATION, "test", "test");

        // Then
        assertTrue(result);
        verify(httpClient).sendPush(testUrl, pushPayload);
    }

    @Test
    void shouldSendPushSuccessfully() {
        // Given
        String recipient = "device-token-456";
        String subject = "Test Push";
        String content = "Test Push Content";
        PushPayload pushPayload = mock(PushPayload.class);
        ResponseEntity<String> response = new ResponseEntity<>("Success", HttpStatus.OK);

        when(payloadFactory.createPushPayload(eq(testProvider), eq(recipient), eq(subject), eq(content)))
                .thenReturn(pushPayload);
        when(httpClient.sendPush(eq(testUrl), eq(pushPayload))).thenReturn(response);

        // When
        boolean result = provider.sendMessage(testProvider, recipient, subject, content,
                CommunicationTypeEnum.PUSH, "test", "test");

        // Then
        assertTrue(result);
        verify(httpClient).sendPush(testUrl, pushPayload);
    }

    @Test
    void shouldSendSendGridEmailSuccessfully() {
        // Given
        String recipient = "test@example.com";
        String subject = "Test SendGrid";
        String content = "Test SendGrid Content";
        EmailPayload emailPayload = mock(EmailPayload.class);
        ResponseEntity<String> response = new ResponseEntity<>("Success", HttpStatus.OK);

        when(payloadFactory.createEmailPayload(eq(testProvider), eq(recipient), eq(subject), eq(content)))
                .thenReturn(emailPayload);
        when(httpClient.sendEmail(eq(testUrl), eq(emailPayload))).thenReturn(response);

        // When
        boolean result = provider.sendMessage(testProvider, recipient, subject, content,
                CommunicationTypeEnum.SENDGRID, "test", "test");

        // Then
        assertTrue(result);
        verify(httpClient).sendEmail(testUrl, emailPayload);
    }

    @Test
    void shouldHandleTelegramWithWhatsAppFallback() {
        // Given
        String recipient = "123456789";
        String content = "Test Telegram";
        String messageType = "template";
        String templateType = "notification";
        WhatsAppPayload whatsAppPayload = mock(WhatsAppPayload.class);
        ResponseEntity<String> response = new ResponseEntity<>("Success", HttpStatus.OK);

        when(payloadFactory.createWhatsAppPayload(eq(testProvider), eq(recipient), eq(content), eq(messageType), eq(templateType)))
                .thenReturn(whatsAppPayload);
        when(httpClient.sendWhatsApp(eq(testUrl), eq(whatsAppPayload))).thenReturn(response);

        // When
        boolean result = provider.sendMessage(testProvider, recipient, null, content,
                CommunicationTypeEnum.TELEGRAM, messageType, templateType);

        // Then
        assertTrue(result);
        verify(httpClient).sendWhatsApp(testUrl, whatsAppPayload);
    }

    @Test
    void shouldThrowExceptionWhenEmailSendingFails() {
        // Given
        String recipient = "test@example.com";
        String subject = "Test Subject";
        String content = "Test Content";
        EmailPayload emailPayload = mock(EmailPayload.class);
        ResponseEntity<String> response = new ResponseEntity<>("Error", HttpStatus.BAD_REQUEST);

        when(payloadFactory.createEmailPayload(eq(testProvider), eq(recipient), eq(subject), eq(content)))
                .thenReturn(emailPayload);
        when(httpClient.sendEmail(eq(testUrl), eq(emailPayload))).thenReturn(response);

        // When & Then
        assertThrows(MessageSendException.class, () ->
                provider.sendMessage(testProvider, recipient, subject, content,
                        CommunicationTypeEnum.EMAIL, "test", "test"));
    }

    @Test
    void shouldThrowExceptionWhenHttpClientThrowsException() {
        // Given
        String recipient = "test@example.com";
        String subject = "Test Subject";
        String content = "Test Content";

        when(payloadFactory.createEmailPayload(eq(testProvider), eq(recipient), eq(subject), eq(content)))
                .thenThrow(new RuntimeException("HTTP Error"));

        // When & Then
        MessageSendException exception = assertThrows(MessageSendException.class, () ->
                provider.sendMessage(testProvider, recipient, subject, content,
                        CommunicationTypeEnum.EMAIL, "test", "test"));

        assertTrue(exception.getMessage().contains("Erro ao enviar mensagem via N8N"));
        assertNotNull(exception.getCause());
    }

    @Test
    void shouldSupportN8NProvider() {
        // When
        boolean supports = provider.supports(testProvider);

        // Then
        assertTrue(supports);
    }

    @Test
    void shouldNotSupportNonN8NProvider() {
        // Given
        Provider nonN8NProvider = Provider.builder()
                .id(UUID.randomUUID())
                .name("Test Provider")
                .providerType(ProviderTypeEnum.SENDGRID)
                .communicationType(CommunicationTypeEnum.EMAIL)
                .url("https://sendgrid.example.com")
                .configuration("{}")
                .isActive(true)
                .build();

        // When
        boolean supports = provider.supports(nonN8NProvider);

        // Then
        assertFalse(supports);
    }

    @Test
    void shouldTestConnectionSuccessfully() {
        // Given
        ResponseEntity<String> response = new ResponseEntity<>("Connected", HttpStatus.OK);
        when(httpClient.testConnection(eq(testUrl), eq(CommunicationTypeEnum.EMAIL))).thenReturn(response);

        // When
        boolean result = provider.testConnection(testProvider);

        // Then
        assertTrue(result);
        verify(httpClient).testConnection(testUrl, CommunicationTypeEnum.EMAIL);
    }

    @Test
    void shouldThrowExceptionWhenConnectionTestFails() {
        // Given
        ResponseEntity<String> response = new ResponseEntity<>("Connection Failed", HttpStatus.INTERNAL_SERVER_ERROR);
        when(httpClient.testConnection(eq(testUrl), eq(CommunicationTypeEnum.EMAIL))).thenReturn(response);

        // When & Then
        assertThrows(ProviderConnectionException.class, () ->
                provider.testConnection(testProvider));
    }

    @Test
    void shouldThrowExceptionWhenConnectionTestThrowsException() {
        // Given
        when(httpClient.testConnection(eq(testUrl), eq(CommunicationTypeEnum.EMAIL)))
                .thenThrow(new RuntimeException("Connection Error"));

        // When & Then
        ProviderConnectionException exception = assertThrows(ProviderConnectionException.class, () ->
                provider.testConnection(testProvider));

        assertTrue(exception.getMessage().contains("Erro no teste de conectividade com N8N"));
        assertNotNull(exception.getCause());
    }

    @Test
    void shouldTestConnectionWithDifferentCommunicationTypes() {
        // Given
        testProvider = Provider.builder()
                .id(testProvider.getId())
                .name(testProvider.getName())
                .providerType(testProvider.getProviderType())
                .communicationType(CommunicationTypeEnum.SMS)
                .url(testProvider.getUrl())
                .configuration(testProvider.getConfiguration())
                .isActive(testProvider.getIsActive())
                .build();
        ResponseEntity<String> response = new ResponseEntity<>("Connected", HttpStatus.OK);
        when(httpClient.testConnection(eq(testUrl), eq(CommunicationTypeEnum.SMS))).thenReturn(response);

        // When
        boolean result = provider.testConnection(testProvider);

        // Then
        assertTrue(result);
        verify(httpClient).testConnection(testUrl, CommunicationTypeEnum.SMS);
    }
}
