package com.keepguard.ms_communication.infrastructure.provider.n8n;

import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.infrastructure.provider.n8n.payload.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para N8NHttpClient
 */
@ExtendWith(MockitoExtension.class)
class N8NHttpClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private N8NHttpClient n8nHttpClient;

    private String testUrl;
    private EmailPayload emailPayload;
    private SMSPayload smsPayload;
    private WhatsAppPayload whatsAppPayload;
    private PushPayload pushPayload;

    @BeforeEach
    void setUp() {
        testUrl = "https://n8n.example.com/webhook/email";
        
        emailPayload = EmailPayload.builder()
            .to("test@example.com")
            .subject("Test Subject")
            .message("Test Message")
            .html("<p>Test HTML</p>")
            .cc("cc@example.com")
            .replyTo("reply@example.com")
            .build();

        smsPayload = SMSPayload.builder()
            .to("+5511999999999")
            .message("Test SMS")
            .workflowType("sms")
            .build();

        whatsAppPayload = WhatsAppPayload.builder()
            .chatId("5511999999999")
            .text("Test WhatsApp")
            .messageType("text")
            .templateType("notification")
            .build();

        pushPayload = PushPayload.builder()
            .to("device-token-123")
            .title("Test Title")
            .message("Test Push Message")
            .workflowType("push")
            .build();
    }

    @Test
    @DisplayName("Deve enviar email com sucesso")
    void shouldSendEmailSuccessfully() {
        // Given
        ResponseEntity<String> expectedResponse = new ResponseEntity<>("Email sent successfully", HttpStatus.OK);
        when(restTemplate.postForEntity(eq(testUrl), any(HttpEntity.class), eq(String.class)))
            .thenReturn(expectedResponse);

        // When
        ResponseEntity<String> response = n8nHttpClient.sendEmail(testUrl, emailPayload);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Email sent successfully", response.getBody());

        verify(restTemplate).postForEntity(eq(testUrl), any(HttpEntity.class), eq(String.class));
    }

    @Test
    @DisplayName("Deve enviar SMS com sucesso")
    void shouldSendSMSSuccessfully() {
        // Given
        String smsUrl = "https://n8n.example.com/webhook/sms";
        ResponseEntity<String> expectedResponse = new ResponseEntity<>("SMS sent successfully", HttpStatus.OK);
        when(restTemplate.postForEntity(eq(smsUrl), any(HttpEntity.class), eq(String.class)))
            .thenReturn(expectedResponse);

        // When
        ResponseEntity<String> response = n8nHttpClient.sendSMS(smsUrl, smsPayload);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("SMS sent successfully", response.getBody());

        verify(restTemplate).postForEntity(eq(smsUrl), any(HttpEntity.class), eq(String.class));
    }

    @Test
    @DisplayName("Deve enviar WhatsApp com sucesso")
    void shouldSendWhatsAppSuccessfully() {
        // Given
        String whatsAppUrl = "https://n8n.example.com/webhook/whatsapp";
        ResponseEntity<String> expectedResponse = new ResponseEntity<>("WhatsApp sent successfully", HttpStatus.OK);
        when(restTemplate.postForEntity(eq(whatsAppUrl), any(HttpEntity.class), eq(String.class)))
            .thenReturn(expectedResponse);

        // When
        ResponseEntity<String> response = n8nHttpClient.sendWhatsApp(whatsAppUrl, whatsAppPayload);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("WhatsApp sent successfully", response.getBody());

        verify(restTemplate).postForEntity(eq(whatsAppUrl), any(HttpEntity.class), eq(String.class));
    }

    @Test
    @DisplayName("Deve enviar push notification com sucesso")
    void shouldSendPushSuccessfully() {
        // Given
        String pushUrl = "https://n8n.example.com/webhook/push";
        ResponseEntity<String> expectedResponse = new ResponseEntity<>("Push sent successfully", HttpStatus.OK);
        when(restTemplate.postForEntity(eq(pushUrl), any(HttpEntity.class), eq(String.class)))
            .thenReturn(expectedResponse);

        // When
        ResponseEntity<String> response = n8nHttpClient.sendPush(pushUrl, pushPayload);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Push sent successfully", response.getBody());

        verify(restTemplate).postForEntity(eq(pushUrl), any(HttpEntity.class), eq(String.class));
    }

    @Test
    @DisplayName("Deve testar conexão com sucesso")
    void shouldTestConnectionSuccessfully() {
        // Given
        String testUrl = "https://n8n.example.com/webhook/test";
        ResponseEntity<String> expectedResponse = new ResponseEntity<>("Connection successful", HttpStatus.OK);
        when(restTemplate.postForEntity(eq(testUrl), any(HttpEntity.class), eq(String.class)))
            .thenReturn(expectedResponse);

        // When
        ResponseEntity<String> response = n8nHttpClient.testConnection(testUrl, CommunicationTypeEnum.EMAIL);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Connection successful", response.getBody());

        verify(restTemplate).postForEntity(eq(testUrl), any(HttpEntity.class), eq(String.class));
    }

    @Test
    @DisplayName("Deve testar conexão com diferentes tipos de comunicação")
    void shouldTestConnectionWithDifferentCommunicationTypes() {
        // Given
        String testUrl = "https://n8n.example.com/webhook/test";
        ResponseEntity<String> expectedResponse = new ResponseEntity<>("Connection successful", HttpStatus.OK);
        when(restTemplate.postForEntity(eq(testUrl), any(HttpEntity.class), eq(String.class)))
            .thenReturn(expectedResponse);

        CommunicationTypeEnum[] communicationTypes = {
            CommunicationTypeEnum.EMAIL,
            CommunicationTypeEnum.SMS,
            CommunicationTypeEnum.WHATSAPP,
            CommunicationTypeEnum.PUSH,
            CommunicationTypeEnum.SENDGRID
        };

        // When & Then
        for (CommunicationTypeEnum type : communicationTypes) {
            ResponseEntity<String> response = n8nHttpClient.testConnection(testUrl, type);
            
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Connection successful", response.getBody());
        }

        verify(restTemplate, times(communicationTypes.length))
            .postForEntity(eq(testUrl), any(HttpEntity.class), eq(String.class));
    }

    @Test
    @DisplayName("Deve lidar com erro HTTP ao enviar email")
    void shouldHandleHttpErrorWhenSendingEmail() {
        // Given
        ResponseEntity<String> errorResponse = new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        when(restTemplate.postForEntity(eq(testUrl), any(HttpEntity.class), eq(String.class)))
            .thenReturn(errorResponse);

        // When
        ResponseEntity<String> response = n8nHttpClient.sendEmail(testUrl, emailPayload);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal Server Error", response.getBody());

        verify(restTemplate).postForEntity(eq(testUrl), any(HttpEntity.class), eq(String.class));
    }

    @Test
    @DisplayName("Deve lidar com exceção do RestTemplate ao enviar email")
    void shouldHandleRestTemplateExceptionWhenSendingEmail() {
        // Given
        when(restTemplate.postForEntity(eq(testUrl), any(HttpEntity.class), eq(String.class)))
            .thenThrow(new RestClientException("Connection failed"));

        // When & Then
        RestClientException exception = assertThrows(RestClientException.class, () -> {
            n8nHttpClient.sendEmail(testUrl, emailPayload);
        });

        assertEquals("Connection failed", exception.getMessage());
        verify(restTemplate).postForEntity(eq(testUrl), any(HttpEntity.class), eq(String.class));
    }

    @Test
    @DisplayName("Deve lidar com exceção do RestTemplate ao enviar SMS")
    void shouldHandleRestTemplateExceptionWhenSendingSMS() {
        // Given
        String smsUrl = "https://n8n.example.com/webhook/sms";
        when(restTemplate.postForEntity(eq(smsUrl), any(HttpEntity.class), eq(String.class)))
            .thenThrow(new RestClientException("SMS provider unavailable"));

        // When & Then
        RestClientException exception = assertThrows(RestClientException.class, () -> {
            n8nHttpClient.sendSMS(smsUrl, smsPayload);
        });

        assertEquals("SMS provider unavailable", exception.getMessage());
        verify(restTemplate).postForEntity(eq(smsUrl), any(HttpEntity.class), eq(String.class));
    }

    @Test
    @DisplayName("Deve lidar com exceção do RestTemplate ao enviar WhatsApp")
    void shouldHandleRestTemplateExceptionWhenSendingWhatsApp() {
        // Given
        String whatsAppUrl = "https://n8n.example.com/webhook/whatsapp";
        when(restTemplate.postForEntity(eq(whatsAppUrl), any(HttpEntity.class), eq(String.class)))
            .thenThrow(new RestClientException("WhatsApp API error"));

        // When & Then
        RestClientException exception = assertThrows(RestClientException.class, () -> {
            n8nHttpClient.sendWhatsApp(whatsAppUrl, whatsAppPayload);
        });

        assertEquals("WhatsApp API error", exception.getMessage());
        verify(restTemplate).postForEntity(eq(whatsAppUrl), any(HttpEntity.class), eq(String.class));
    }

    @Test
    @DisplayName("Deve lidar com exceção do RestTemplate ao enviar push")
    void shouldHandleRestTemplateExceptionWhenSendingPush() {
        // Given
        String pushUrl = "https://n8n.example.com/webhook/push";
        when(restTemplate.postForEntity(eq(pushUrl), any(HttpEntity.class), eq(String.class)))
            .thenThrow(new RestClientException("Push service error"));

        // When & Then
        RestClientException exception = assertThrows(RestClientException.class, () -> {
            n8nHttpClient.sendPush(pushUrl, pushPayload);
        });

        assertEquals("Push service error", exception.getMessage());
        verify(restTemplate).postForEntity(eq(pushUrl), any(HttpEntity.class), eq(String.class));
    }

    @Test
    @DisplayName("Deve lidar com exceção do RestTemplate ao testar conexão")
    void shouldHandleRestTemplateExceptionWhenTestingConnection() {
        // Given
        String testUrl = "https://n8n.example.com/webhook/test";
        when(restTemplate.postForEntity(eq(testUrl), any(HttpEntity.class), eq(String.class)))
            .thenThrow(new RestClientException("Connection test failed"));

        // When & Then
        RestClientException exception = assertThrows(RestClientException.class, () -> {
            n8nHttpClient.testConnection(testUrl, CommunicationTypeEnum.EMAIL);
        });

        assertEquals("Connection test failed", exception.getMessage());
        verify(restTemplate).postForEntity(eq(testUrl), any(HttpEntity.class), eq(String.class));
    }

    @Test
    @DisplayName("Deve criar headers corretos para todas as requisições")
    void shouldCreateCorrectHeadersForAllRequests() {
        // Given
        ResponseEntity<String> expectedResponse = new ResponseEntity<>("Success", HttpStatus.OK);
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
            .thenReturn(expectedResponse);

        // When
        n8nHttpClient.sendEmail(testUrl, emailPayload);
        n8nHttpClient.sendSMS("https://n8n.example.com/webhook/sms", smsPayload);
        n8nHttpClient.sendWhatsApp("https://n8n.example.com/webhook/whatsapp", whatsAppPayload);
        n8nHttpClient.sendPush("https://n8n.example.com/webhook/push", pushPayload);
        n8nHttpClient.testConnection("https://n8n.example.com/webhook/test", CommunicationTypeEnum.EMAIL);

        // Then
        verify(restTemplate, times(5)).postForEntity(anyString(), any(HttpEntity.class), eq(String.class));
        
        // Verificar que todos os HttpEntity têm headers com Content-Type JSON
        verify(restTemplate, times(5)).postForEntity(
            anyString(), 
            argThat(entity -> {
                if (entity instanceof HttpEntity) {
                    HttpEntity<?> httpEntity = (HttpEntity<?>) entity;
                    HttpHeaders headers = httpEntity.getHeaders();
                    return headers.getContentType() != null && 
                           headers.getContentType().toString().contains("application/json");
                }
                return false;
            }), 
            eq(String.class)
        );
    }

    @Test
    @DisplayName("Deve lançar exceção ao enviar email com payload nulo")
    void shouldThrowExceptionWhenSendingEmailWithNullPayload() {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            n8nHttpClient.sendEmail(testUrl, null);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção ao enviar SMS com payload nulo")
    void shouldThrowExceptionWhenSendingSMSWithNullPayload() {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            n8nHttpClient.sendSMS("https://n8n.example.com/webhook/sms", null);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção ao enviar WhatsApp com payload nulo")
    void shouldThrowExceptionWhenSendingWhatsAppWithNullPayload() {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            n8nHttpClient.sendWhatsApp("https://n8n.example.com/webhook/whatsapp", null);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção ao enviar push com payload nulo")
    void shouldThrowExceptionWhenSendingPushWithNullPayload() {
        // When & Then
        assertThrows(NullPointerException.class, () -> {
            n8nHttpClient.sendPush("https://n8n.example.com/webhook/push", null);
        });
    }


    @Test
    @DisplayName("Deve lidar com timeout na requisição")
    void shouldHandleTimeoutInRequest() {
        // Given
        when(restTemplate.postForEntity(eq(testUrl), any(HttpEntity.class), eq(String.class)))
            .thenThrow(new RestClientException("Read timeout"));

        // When & Then
        RestClientException exception = assertThrows(RestClientException.class, () -> {
            n8nHttpClient.sendEmail(testUrl, emailPayload);
        });

        assertEquals("Read timeout", exception.getMessage());
        verify(restTemplate).postForEntity(eq(testUrl), any(HttpEntity.class), eq(String.class));
    }

    @Test
    @DisplayName("Deve lidar com resposta vazia")
    void shouldHandleEmptyResponse() {
        // Given
        ResponseEntity<String> emptyResponse = new ResponseEntity<>("", HttpStatus.NO_CONTENT);
        when(restTemplate.postForEntity(eq(testUrl), any(HttpEntity.class), eq(String.class)))
            .thenReturn(emptyResponse);

        // When
        ResponseEntity<String> response = n8nHttpClient.sendEmail(testUrl, emailPayload);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals("", response.getBody());

        verify(restTemplate).postForEntity(eq(testUrl), any(HttpEntity.class), eq(String.class));
    }

    @Test
    @DisplayName("Deve lidar com resposta nula")
    void shouldHandleNullResponse() {
        // Given
        ResponseEntity<String> nullResponse = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.postForEntity(eq(testUrl), any(HttpEntity.class), eq(String.class)))
            .thenReturn(nullResponse);

        // When
        ResponseEntity<String> response = n8nHttpClient.sendEmail(testUrl, emailPayload);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());

        verify(restTemplate).postForEntity(eq(testUrl), any(HttpEntity.class), eq(String.class));
    }
}
