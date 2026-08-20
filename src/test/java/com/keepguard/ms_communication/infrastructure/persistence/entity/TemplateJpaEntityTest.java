package com.keepguard.ms_communication.infrastructure.persistence.entity;

import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para TemplateJpaEntity
 */
class TemplateJpaEntityTest {

    private TemplateJpaEntity templateJpaEntity;
    private UUID testId;
    private LocalDateTime testDateTime;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        testDateTime = LocalDateTime.now();
        
        templateJpaEntity = new TemplateJpaEntity();
    }

    @Test
    @DisplayName("Deve criar TemplateJpaEntity com construtor padrão")
    void shouldCreateTemplateJpaEntityWithDefaultConstructor() {
        // When
        TemplateJpaEntity entity = new TemplateJpaEntity();

        // Then
        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getTemplateType());
        assertNull(entity.getMessageType());
        assertNull(entity.getTenantId());
        assertNull(entity.getName());
        assertNull(entity.getDescription());
        assertNull(entity.getSubject());
        assertNull(entity.getContent());
        assertNull(entity.getVariables());
        assertTrue(entity.getIsActive());
        assertNull(entity.getCreatedAt());
        assertNull(entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve criar TemplateJpaEntity com construtor completo")
    void shouldCreateTemplateJpaEntityWithFullConstructor() {
        // Given
        String variables = "{\"userName\":\"string\",\"activationLink\":\"string\"}";

        // When
        TemplateJpaEntity entity = new TemplateJpaEntity(
            testId,
            TemplateTypeEnum.CADASTRO_SUCESSO,
            MessageTypeEnum.EMAIL,
            "test-app",
            "Welcome Template",
            "Template for welcome emails",
            "Welcome to KeepGuard",
            "Hello {{userName}}, welcome to KeepGuard! Click here: {{activationLink}}",
            variables,
            true,
            testDateTime,
            testDateTime
        );

        // Then
        assertNotNull(entity);
        assertEquals(testId, entity.getId());
        assertEquals(TemplateTypeEnum.CADASTRO_SUCESSO, entity.getTemplateType());
        assertEquals(MessageTypeEnum.EMAIL, entity.getMessageType());
        assertEquals("test-app", entity.getTenantId());
        assertEquals("Welcome Template", entity.getName());
        assertEquals("Template for welcome emails", entity.getDescription());
        assertEquals("Welcome to KeepGuard", entity.getSubject());
        assertEquals("Hello {{userName}}, welcome to KeepGuard! Click here: {{activationLink}}", entity.getContent());
        assertEquals(variables, entity.getVariables());
        assertTrue(entity.getIsActive());
        assertEquals(testDateTime, entity.getCreatedAt());
        assertEquals(testDateTime, entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve criar TemplateJpaEntity com builder")
    void shouldCreateTemplateJpaEntityWithBuilder() {
        // Given
        String variables = "{\"userName\":\"string\",\"companyName\":\"string\"}";

        // When
        TemplateJpaEntity entity = TemplateJpaEntity.builder()
            .id(testId)
            .templateType(TemplateTypeEnum.NOTIFICACAO_GERAL)
            .messageType(MessageTypeEnum.SMS)
            .tenantId("notification-app")
            .name("SMS Notification Template")
            .description("Template for SMS notifications")
            .subject(null)
            .content("Hello {{userName}}, notification from {{companyName}}")
            .variables(variables)
            .isActive(true)
            .createdAt(testDateTime)
            .updatedAt(testDateTime)
            .build();

        // Then
        assertNotNull(entity);
        assertEquals(testId, entity.getId());
        assertEquals(TemplateTypeEnum.NOTIFICACAO_GERAL, entity.getTemplateType());
        assertEquals(MessageTypeEnum.SMS, entity.getMessageType());
        assertEquals("notification-app", entity.getTenantId());
        assertEquals("SMS Notification Template", entity.getName());
        assertEquals("Template for SMS notifications", entity.getDescription());
        assertNull(entity.getSubject());
        assertEquals("Hello {{userName}}, notification from {{companyName}}", entity.getContent());
        assertEquals(variables, entity.getVariables());
        assertTrue(entity.getIsActive());
        assertEquals(testDateTime, entity.getCreatedAt());
        assertEquals(testDateTime, entity.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve usar valor padrão isActive quando não especificado")
    void shouldUseDefaultIsActiveWhenNotSpecified() {
        // When
        TemplateJpaEntity entity = TemplateJpaEntity.builder()
            .templateType(TemplateTypeEnum.CADASTRO_SUCESSO)
            .messageType(MessageTypeEnum.EMAIL)
            .tenantId("test-app")
            .name("Test Template")
            .description("Test Description")
            .content("Test Content")
            .build();

        // Then
        assertTrue(entity.getIsActive());
    }

    @Test
    @DisplayName("Deve definir timestamps automaticamente no PrePersist")
    void shouldSetTimestampsAutomaticallyOnPrePersist() {
        // Given
        TemplateJpaEntity entity = TemplateJpaEntity.builder()
            .templateType(TemplateTypeEnum.CADASTRO_SUCESSO)
            .messageType(MessageTypeEnum.EMAIL)
            .tenantId("test-app")
            .name("Test Template")
            .description("Test Description")
            .content("Test Content")
            .build();

        // When
        entity.prePersist();

        // Then
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
        assertTrue(entity.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(entity.getUpdatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        // Allow small time difference between createdAt and updatedAt
        assertTrue(Math.abs(entity.getCreatedAt().getNano() - entity.getUpdatedAt().getNano()) < 1000000);
    }

    @Test
    @DisplayName("Deve atualizar updatedAt no PreUpdate")
    void shouldUpdateUpdatedAtOnPreUpdate() throws InterruptedException {
        // Given
        TemplateJpaEntity entity = TemplateJpaEntity.builder()
            .templateType(TemplateTypeEnum.CADASTRO_SUCESSO)
            .messageType(MessageTypeEnum.EMAIL)
            .tenantId("test-app")
            .name("Test Template")
            .description("Test Description")
            .content("Test Content")
            .build();
        
        entity.prePersist();
        LocalDateTime originalUpdatedAt = entity.getUpdatedAt();
        
        // Aguarda um milissegundo para garantir que o timestamp seja diferente
        Thread.sleep(1);

        // When
        entity.preUpdate();

        // Then
        assertNotNull(entity.getUpdatedAt());
        assertTrue(entity.getUpdatedAt().isAfter(originalUpdatedAt));
    }

    @Test
    @DisplayName("Deve permitir definir todos os campos")
    void shouldAllowSettingAllFields() {
        // Given
        String variables = "{\"userName\":\"string\",\"resetLink\":\"string\"}";

        // When
        templateJpaEntity.setId(testId);
        templateJpaEntity.setTemplateType(TemplateTypeEnum.RECUPERACAO_SENHA);
        templateJpaEntity.setMessageType(MessageTypeEnum.EMAIL);
        templateJpaEntity.setTenantId("auth-app");
        templateJpaEntity.setName("Password Reset Template");
        templateJpaEntity.setDescription("Template for password reset emails");
        templateJpaEntity.setSubject("Reset your password");
        templateJpaEntity.setContent("Hello {{userName}}, click here to reset: {{resetLink}}");
        templateJpaEntity.setVariables(variables);
        templateJpaEntity.setIsActive(false);
        templateJpaEntity.setCreatedAt(testDateTime);
        templateJpaEntity.setUpdatedAt(testDateTime);

        // Then
        assertEquals(testId, templateJpaEntity.getId());
        assertEquals(TemplateTypeEnum.RECUPERACAO_SENHA, templateJpaEntity.getTemplateType());
        assertEquals(MessageTypeEnum.EMAIL, templateJpaEntity.getMessageType());
        assertEquals("auth-app", templateJpaEntity.getTenantId());
        assertEquals("Password Reset Template", templateJpaEntity.getName());
        assertEquals("Template for password reset emails", templateJpaEntity.getDescription());
        assertEquals("Reset your password", templateJpaEntity.getSubject());
        assertEquals("Hello {{userName}}, click here to reset: {{resetLink}}", templateJpaEntity.getContent());
        assertEquals(variables, templateJpaEntity.getVariables());
        assertFalse(templateJpaEntity.getIsActive());
        assertEquals(testDateTime, templateJpaEntity.getCreatedAt());
        assertEquals(testDateTime, templateJpaEntity.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve permitir campos nulos")
    void shouldAllowNullFields() {
        // When
        templateJpaEntity.setId(null);
        templateJpaEntity.setTemplateType(null);
        templateJpaEntity.setMessageType(null);
        templateJpaEntity.setTenantId(null);
        templateJpaEntity.setName(null);
        templateJpaEntity.setDescription(null);
        templateJpaEntity.setSubject(null);
        templateJpaEntity.setContent(null);
        templateJpaEntity.setVariables(null);
        templateJpaEntity.setCreatedAt(null);
        templateJpaEntity.setUpdatedAt(null);

        // Then
        assertNull(templateJpaEntity.getId());
        assertNull(templateJpaEntity.getTemplateType());
        assertNull(templateJpaEntity.getMessageType());
        assertNull(templateJpaEntity.getTenantId());
        assertNull(templateJpaEntity.getName());
        assertNull(templateJpaEntity.getDescription());
        assertNull(templateJpaEntity.getSubject());
        assertNull(templateJpaEntity.getContent());
        assertNull(templateJpaEntity.getVariables());
        assertNull(templateJpaEntity.getCreatedAt());
        assertNull(templateJpaEntity.getUpdatedAt());
    }

    @Test
    @DisplayName("Deve permitir variáveis JSON complexas")
    void shouldAllowComplexJsonVariables() {
        // Given
        String complexVariables = """
            {
                "userName": "string",
                "userEmail": "string",
                "companyName": "string",
                "activationLink": "string",
                "expirationDate": "string",
                "supportEmail": "string",
                "phoneNumber": "string",
                "address": {
                    "street": "string",
                    "city": "string",
                    "state": "string",
                    "zipCode": "string"
                },
                "preferences": {
                    "notifications": true,
                    "marketing": false,
                    "language": "pt-BR"
                }
            }
            """;

        // When
        templateJpaEntity.setVariables(complexVariables);

        // Then
        assertEquals(complexVariables, templateJpaEntity.getVariables());
        assertTrue(templateJpaEntity.getVariables().contains("userName"));
        assertTrue(templateJpaEntity.getVariables().contains("address"));
        assertTrue(templateJpaEntity.getVariables().contains("preferences"));
    }

    @Test
    @DisplayName("Deve permitir conteúdo HTML")
    void shouldAllowHtmlContent() {
        // Given
        String htmlContent = """
            <html>
                <body>
                    <h1>Welcome to KeepGuard!</h1>
                    <p>Hello <strong>{{userName}}</strong>,</p>
                    <p>Welcome to our platform. Please click the button below to activate your account:</p>
                    <a href="{{activationLink}}" style="background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">
                        Activate Account
                    </a>
                    <p>If you have any questions, please contact us at {{supportEmail}}</p>
                    <p>Best regards,<br>The KeepGuard Team</p>
                </body>
            </html>
            """;

        // When
        templateJpaEntity.setContent(htmlContent);

        // Then
        assertEquals(htmlContent, templateJpaEntity.getContent());
        assertTrue(templateJpaEntity.getContent().contains("<html>"));
        assertTrue(templateJpaEntity.getContent().contains("{{userName}}"));
        assertTrue(templateJpaEntity.getContent().contains("{{activationLink}}"));
    }

    @Test
    @DisplayName("Deve testar toString")
    void shouldTestToString() {
        // Given
        templateJpaEntity.setId(testId);
        templateJpaEntity.setTemplateType(TemplateTypeEnum.CADASTRO_SUCESSO);
        templateJpaEntity.setMessageType(MessageTypeEnum.EMAIL);
        templateJpaEntity.setTenantId("test-app");
        templateJpaEntity.setName("Welcome Template");
        templateJpaEntity.setIsActive(true);

        // When
        String result = templateJpaEntity.toString();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("TemplateJpaEntity"));
        assertTrue(result.contains(testId.toString()));
        assertTrue(result.contains("CADASTRO_SUCESSO"));
        assertTrue(result.contains("EMAIL"));
        assertTrue(result.contains("test-app"));
        assertTrue(result.contains("Welcome Template"));
    }

    @Test
    @DisplayName("Deve testar equals e hashCode")
    void shouldTestEqualsAndHashCode() {
        // Given
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        
        TemplateJpaEntity entity1 = TemplateJpaEntity.builder()
            .id(id1)
            .templateType(TemplateTypeEnum.CADASTRO_SUCESSO)
            .messageType(MessageTypeEnum.EMAIL)
            .tenantId("test-app")
            .name("Template 1")
            .content("Content 1")
            .build();

        TemplateJpaEntity entity2 = TemplateJpaEntity.builder()
            .id(id1)
            .templateType(TemplateTypeEnum.CADASTRO_SUCESSO)
            .messageType(MessageTypeEnum.EMAIL)
            .tenantId("test-app")
            .name("Template 1")
            .content("Content 1")
            .build();

        TemplateJpaEntity entity3 = TemplateJpaEntity.builder()
            .id(id2)
            .templateType(TemplateTypeEnum.NOTIFICACAO_GERAL)
            .messageType(MessageTypeEnum.SMS)
            .tenantId("notification-app")
            .name("Template 2")
            .content("Content 2")
            .build();

        // When & Then
        assertEquals(entity1, entity2);
        assertNotEquals(entity1, entity3);
        assertEquals(entity1.hashCode(), entity2.hashCode());
        assertNotEquals(entity1.hashCode(), entity3.hashCode());
    }

    @Test
    @DisplayName("Deve criar entidade com diferentes tipos de template")
    void shouldCreateEntityWithDifferentTemplateTypes() {
        // Given
        TemplateTypeEnum[] templateTypes = {
            TemplateTypeEnum.CADASTRO_SUCESSO,
            TemplateTypeEnum.NOTIFICACAO_GERAL,
            TemplateTypeEnum.ALERTA_SEGURANCA,
            TemplateTypeEnum.RECUPERACAO_SENHA,
            TemplateTypeEnum.CONFIRMACAO_ACAO
        };

        // When & Then
        for (TemplateTypeEnum type : templateTypes) {
            TemplateJpaEntity entity = TemplateJpaEntity.builder()
                .templateType(type)
                .messageType(MessageTypeEnum.EMAIL)
                .tenantId("test-app")
                .name("Template " + type.name())
                .description("Description for " + type.name())
                .content("Content for " + type.name())
                .build();

            assertNotNull(entity);
            assertEquals(type, entity.getTemplateType());
        }
    }

    @Test
    @DisplayName("Deve criar entidade com diferentes tipos de mensagem")
    void shouldCreateEntityWithDifferentMessageTypes() {
        // Given
        MessageTypeEnum[] messageTypes = {
            MessageTypeEnum.EMAIL,
            MessageTypeEnum.SMS,
            MessageTypeEnum.WHATSAPP,
            MessageTypeEnum.PUSH
        };

        // When & Then
        for (MessageTypeEnum type : messageTypes) {
            TemplateJpaEntity entity = TemplateJpaEntity.builder()
                .templateType(TemplateTypeEnum.CADASTRO_SUCESSO)
                .messageType(type)
                .tenantId("test-app")
                .name("Template for " + type.name())
                .description("Description for " + type.name())
                .content("Content for " + type.name())
                .build();

            assertNotNull(entity);
            assertEquals(type, entity.getMessageType());
        }
    }

    @Test
    @DisplayName("Deve permitir isActive false")
    void shouldAllowIsActiveFalse() {
        // When
        templateJpaEntity.setIsActive(false);

        // Then
        assertFalse(templateJpaEntity.getIsActive());
    }

    @Test
    @DisplayName("Deve permitir isActive true")
    void shouldAllowIsActiveTrue() {
        // When
        templateJpaEntity.setIsActive(true);

        // Then
        assertTrue(templateJpaEntity.getIsActive());
    }

    @Test
    @DisplayName("Deve permitir isActive null")
    void shouldAllowIsActiveNull() {
        // When
        templateJpaEntity.setIsActive(null);

        // Then
        assertNull(templateJpaEntity.getIsActive());
    }

    @Test
    @DisplayName("Deve permitir strings vazias")
    void shouldAllowEmptyStrings() {
        // When
        templateJpaEntity.setTenantId("");
        templateJpaEntity.setName("");
        templateJpaEntity.setDescription("");
        templateJpaEntity.setSubject("");
        templateJpaEntity.setContent("");
        templateJpaEntity.setVariables("");

        // Then
        assertEquals("", templateJpaEntity.getTenantId());
        assertEquals("", templateJpaEntity.getName());
        assertEquals("", templateJpaEntity.getDescription());
        assertEquals("", templateJpaEntity.getSubject());
        assertEquals("", templateJpaEntity.getContent());
        assertEquals("", templateJpaEntity.getVariables());
    }
}
