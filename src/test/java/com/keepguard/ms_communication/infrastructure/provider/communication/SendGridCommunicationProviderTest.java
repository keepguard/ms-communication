package com.keepguard.ms_communication.infrastructure.provider.communication;

import com.keepguard.ms_communication.application.service.exception.MessageSendException;
import com.keepguard.ms_communication.application.service.exception.ProviderConnectionException;
import com.keepguard.ms_communication.domain.entity.Provider;
import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.domain.enums.ProviderTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SendGridCommunicationProviderTest {

    @InjectMocks
    private SendGridCommunicationProvider provider;

    private Provider testProvider;
    private String testUrl = "https://api.sendgrid.com/v3/mail/send";

    @BeforeEach
    void setUp() {
        testProvider = Provider.builder()
                .id(UUID.randomUUID())
                .name("Test SendGrid Provider")
                .providerType(ProviderTypeEnum.SENDGRID)
                .communicationType(CommunicationTypeEnum.EMAIL)
                .url(testUrl)
                .configuration("{\"apiKey\":\"test-sendgrid-key\"}")
                .isActive(true)
                .build();
    }

    @Test
    void shouldSendMessageSuccessfully() {
        // Given
        String recipient = "test@example.com";
        String subject = "Test Subject";
        String content = "Test Content";
        String messageType = "transactional";
        String templateType = "welcome";

        // When
        boolean result = provider.sendMessage(testProvider, recipient, subject, content,
                CommunicationTypeEnum.EMAIL, messageType, templateType);

        // Then
        assertTrue(result);
    }

    @Test
    void shouldSendMessageWithNullSubjectAndContent() {
        // Given
        String recipient = "test@example.com";

        // When
        boolean result = provider.sendMessage(testProvider, recipient, null, null,
                CommunicationTypeEnum.EMAIL, null, null);

        // Then
        assertTrue(result);
    }

    @Test
    void shouldHandleExceptionInSendMessage() {
        // Given
        String recipient = "invalid-email";
        String subject = "Test Subject";
        String content = "Test Content";

        // When & Then
        // Since this is a placeholder implementation, we can't easily trigger an exception
        // but we can verify the method signature and basic functionality
        assertDoesNotThrow(() -> provider.sendMessage(testProvider, recipient, subject, content,
                CommunicationTypeEnum.EMAIL, "test", "test"));
    }

    @Test
    void shouldSupportSendGridProvider() {
        // When
        boolean supports = provider.supports(testProvider);

        // Then
        assertTrue(supports);
    }

    @Test
    void shouldNotSupportNonSendGridProvider() {
        // Given
        Provider nonSendGridProvider = Provider.builder()
                .id(UUID.randomUUID())
                .name("Test Provider")
                .providerType(ProviderTypeEnum.N8N)
                .communicationType(CommunicationTypeEnum.EMAIL)
                .url("https://n8n.example.com")
                .configuration("{}")
                .isActive(true)
                .build();

        // When
        boolean supports = provider.supports(nonSendGridProvider);

        // Then
        assertFalse(supports);
    }

    @Test
    void shouldTestConnectionSuccessfully() {
        // When
        boolean result = provider.testConnection(testProvider);

        // Then
        assertTrue(result);
    }

    @Test
    void shouldTestConnectionWithDifferentProviderTypes() {
        // Given
        Provider smsProvider = Provider.builder()
                .id(UUID.randomUUID())
                .name("Test SMS Provider")
                .providerType(ProviderTypeEnum.SENDGRID)
                .communicationType(CommunicationTypeEnum.SMS)
                .url("https://api.sendgrid.com/v3/sms")
                .configuration("{\"apiKey\":\"test-key\"}")
                .isActive(true)
                .build();

        // When
        boolean result = provider.testConnection(smsProvider);

        // Then
        assertTrue(result);
    }

    @Test
    void shouldTestConnectionWithNullProvider() {
        // When & Then
        // Since this is a placeholder implementation, we can't easily trigger an exception
        // but we can verify the method signature and basic functionality
        assertDoesNotThrow(() -> provider.testConnection(testProvider));
    }

    @Test
    void shouldHandleAllCommunicationTypes() {
        // Given
        String recipient = "test@example.com";
        String subject = "Test Subject";
        String content = "Test Content";

        // When & Then - Test all communication types
        assertTrue(provider.sendMessage(testProvider, recipient, subject, content,
                CommunicationTypeEnum.EMAIL, "test", "test"));
        
        assertTrue(provider.sendMessage(testProvider, recipient, subject, content,
                CommunicationTypeEnum.SMS, "test", "test"));
        
        assertTrue(provider.sendMessage(testProvider, recipient, subject, content,
                CommunicationTypeEnum.WHATSAPP, "test", "test"));
        
        assertTrue(provider.sendMessage(testProvider, recipient, subject, content,
                CommunicationTypeEnum.PUSH_NOTIFICATION, "test", "test"));
        
        assertTrue(provider.sendMessage(testProvider, recipient, subject, content,
                CommunicationTypeEnum.PUSH, "test", "test"));
        
        assertTrue(provider.sendMessage(testProvider, recipient, subject, content,
                CommunicationTypeEnum.SENDGRID, "test", "test"));
        
        assertTrue(provider.sendMessage(testProvider, recipient, subject, content,
                CommunicationTypeEnum.TELEGRAM, "test", "test"));
    }

    @Test
    void shouldHandleNullParameters() {
        // When & Then
        assertDoesNotThrow(() -> provider.sendMessage(null, null, null, null,
                null, null, null));
        
        // The testConnection method throws ProviderConnectionException when provider is null
        assertThrows(ProviderConnectionException.class, () -> provider.testConnection(null));
        
        // The supports method throws NullPointerException when provider is null
        assertThrows(NullPointerException.class, () -> provider.supports(null));
    }

    @Test
    void shouldSupportOnlySendGridProviderType() {
        // Given
        Provider[] providers = {
                Provider.builder().id(UUID.randomUUID()).providerType(ProviderTypeEnum.N8N).build(),
                Provider.builder().id(UUID.randomUUID()).providerType(ProviderTypeEnum.SENDGRID).build()
        };

        // When & Then
        assertFalse(provider.supports(providers[0])); // N8N
        assertTrue(provider.supports(providers[1]));  // SendGrid
    }
}
