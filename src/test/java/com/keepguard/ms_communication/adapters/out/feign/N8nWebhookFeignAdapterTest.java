package com.keepguard.ms_communication.adapters.out.feign;

import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.infrastructure.provider.n8n.payload.EmailPayload;
import com.keepguard.ms_communication.infrastructure.provider.n8n.payload.PushPayload;
import com.keepguard.ms_communication.infrastructure.provider.n8n.payload.SMSPayload;
import com.keepguard.ms_communication.infrastructure.provider.n8n.payload.TestPayload;
import com.keepguard.ms_communication.infrastructure.provider.n8n.payload.WhatsAppPayload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class N8nWebhookFeignAdapterTest {

    @Mock
    private N8nWebhookClient n8nWebhookClient;

    @InjectMocks
    private N8nWebhookFeignAdapter adapter;

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
        ResponseEntity<String> expected = new ResponseEntity<>("Email sent successfully", HttpStatus.OK);
        when(n8nWebhookClient.post(eq(URI.create(testUrl)), eq(emailPayload))).thenReturn(expected);

        ResponseEntity<String> response = adapter.sendEmail(testUrl, emailPayload);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Email sent successfully", response.getBody());
        verify(n8nWebhookClient).post(URI.create(testUrl), emailPayload);
    }

    @Test
    @DisplayName("Deve enviar SMS com sucesso")
    void shouldSendSMSSuccessfully() {
        String smsUrl = "https://n8n.example.com/webhook/sms";
        ResponseEntity<String> expected = new ResponseEntity<>("SMS sent successfully", HttpStatus.OK);
        when(n8nWebhookClient.post(eq(URI.create(smsUrl)), eq(smsPayload))).thenReturn(expected);

        ResponseEntity<String> response = adapter.sendSMS(smsUrl, smsPayload);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(n8nWebhookClient).post(URI.create(smsUrl), smsPayload);
    }

    @Test
    @DisplayName("Deve enviar WhatsApp com sucesso")
    void shouldSendWhatsAppSuccessfully() {
        when(n8nWebhookClient.post(eq(URI.create(testUrl)), eq(whatsAppPayload)))
                .thenReturn(new ResponseEntity<>("ok", HttpStatus.OK));

        ResponseEntity<String> response = adapter.sendWhatsApp(testUrl, whatsAppPayload);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(n8nWebhookClient).post(URI.create(testUrl), whatsAppPayload);
    }

    @Test
    @DisplayName("Deve enviar push com sucesso")
    void shouldSendPushSuccessfully() {
        when(n8nWebhookClient.post(eq(URI.create(testUrl)), eq(pushPayload)))
                .thenReturn(new ResponseEntity<>("ok", HttpStatus.OK));

        ResponseEntity<String> response = adapter.sendPush(testUrl, pushPayload);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(n8nWebhookClient).post(URI.create(testUrl), pushPayload);
    }

    @Test
    @DisplayName("Deve testar conexão com payload de teste")
    void shouldTestConnection() {
        when(n8nWebhookClient.post(eq(URI.create(testUrl)), any(TestPayload.class)))
                .thenReturn(new ResponseEntity<>("ok", HttpStatus.OK));

        ResponseEntity<String> response = adapter.testConnection(testUrl, CommunicationTypeEnum.EMAIL);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        ArgumentCaptor<Object> bodyCaptor = ArgumentCaptor.forClass(Object.class);
        verify(n8nWebhookClient).post(eq(URI.create(testUrl)), bodyCaptor.capture());
        TestPayload payload = (TestPayload) bodyCaptor.getValue();
        assertEquals(true, payload.isTest());
        assertEquals("email", payload.getWorkflowType());
    }

    @Test
    @DisplayName("Deve propagar falha do Feign client")
    void shouldPropagateFeignFailure() {
        when(n8nWebhookClient.post(any(URI.class), any()))
                .thenThrow(new RuntimeException("boom"));

        assertThrows(RuntimeException.class, () -> adapter.sendEmail(testUrl, emailPayload));
    }
}
