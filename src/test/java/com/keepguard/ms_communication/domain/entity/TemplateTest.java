package com.keepguard.ms_communication.domain.entity;

import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import com.keepguard.ms_communication.test.builder.TemplateTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a entidade Template
 */
class TemplateTest {
    
    private Template template;
    private UUID templateId;
    
    @BeforeEach
    void setUp() {
        templateId = UUID.randomUUID();
        
        template = TemplateTestBuilder.aTemplate()
            .withTemplateType(TemplateTypeEnum.AUTENTICACAO_EMAIL_TOKEN)
            .withMessageType(MessageTypeEnum.EMAIL)
            .withApplication("ms-auth")
            .withName("Email Authentication Template")
            .withDescription("Template para autenticação por email")
            .withSubject("Authentication Code")
            .withContent("Your authentication code is: {{code}}")
            .withVariables("code", "Authentication Code")
            .buildDomain();
    }
    
    @Test
    @DisplayName("Deve criar template com dados válidos")
    void shouldCreateTemplateWithValidData() {
        // Then
        assertEquals(TemplateTypeEnum.AUTENTICACAO_EMAIL_TOKEN, template.getTemplateType());
        assertEquals(MessageTypeEnum.EMAIL, template.getMessageType());
        assertEquals("ms-auth", template.getTenantId());
        assertEquals("Email Authentication Template", template.getName());
        assertEquals("Template para autenticação por email", template.getDescription());
        assertEquals("Authentication Code", template.getSubject());
        assertEquals("Your authentication code is: {{code}}", template.getContent());
        assertEquals("{code=Authentication Code}", template.getVariables());
        assertTrue(template.getIsActive());
    }
    
    @Test
    @DisplayName("Deve criar template com ID específico")
    void shouldCreateTemplateWithSpecificId() {
        // Given & When
        Template template = new Template(
            templateId,
            TemplateTypeEnum.AUTENTICACAO_EMAIL_TOKEN,
            MessageTypeEnum.EMAIL,
            "ms-auth",
            "Email Authentication Template",
            "Template para autenticação por email",
            "Authentication Code",
            "Your authentication code is: {{code}}",
            "[]",
            null,
            true,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        // Then
        assertEquals(templateId, template.getId());
    }
    
    @Test
    @DisplayName("Deve criar template de email")
    void shouldCreateEmailTemplate() {
        // Given & When
        Template emailTemplate = Template.create(
            TemplateTypeEnum.CADASTRO_SUCESSO,
            MessageTypeEnum.EMAIL,
            "ms-auth",
            "Welcome Email Template",
            "Template para email de boas-vindas",
            "Welcome to our platform!",
            "Hello {{name}}, welcome to our platform!"
        );
        
        // Then
        assertEquals(TemplateTypeEnum.CADASTRO_SUCESSO, emailTemplate.getTemplateType());
        assertEquals(MessageTypeEnum.EMAIL, emailTemplate.getMessageType());
        assertEquals("ms-auth", emailTemplate.getTenantId());
        assertEquals("Welcome Email Template", emailTemplate.getName());
        assertEquals("Template para email de boas-vindas", emailTemplate.getDescription());
        assertEquals("Welcome to our platform!", emailTemplate.getSubject());
        assertEquals("Hello {{name}}, welcome to our platform!", emailTemplate.getContent());
    }
    
    @Test
    @DisplayName("Deve criar template de SMS")
    void shouldCreateSmsTemplate() {
        // Given & When
        Template smsTemplate = Template.create(
            TemplateTypeEnum.AUTENTICACAO_SMS_TOKEN,
            MessageTypeEnum.SMS,
            "ms-auth",
            "SMS Authentication Template",
            "Template para autenticação por SMS",
            null, // SMS não tem subject
            "Your SMS code is: {{code}}"
        );
        
        // Then
        assertEquals(TemplateTypeEnum.AUTENTICACAO_SMS_TOKEN, smsTemplate.getTemplateType());
        assertEquals(MessageTypeEnum.SMS, smsTemplate.getMessageType());
        assertEquals("ms-auth", smsTemplate.getTenantId());
        assertEquals("SMS Authentication Template", smsTemplate.getName());
        assertEquals("Template para autenticação por SMS", smsTemplate.getDescription());
        assertNull(smsTemplate.getSubject());
        assertEquals("Your SMS code is: {{code}}", smsTemplate.getContent());
    }
    
    @Test
    @DisplayName("Deve criar template de WhatsApp")
    void shouldCreateWhatsAppTemplate() {
        // Given & When
        Template whatsappTemplate = Template.create(
            TemplateTypeEnum.AUTENTICACAO_WHATSAPP_TOKEN,
            MessageTypeEnum.WHATSAPP,
            "ms-auth",
            "WhatsApp Authentication Template",
            "Template para autenticação por WhatsApp",
            null, // WhatsApp não tem subject
            "Hello! Your WhatsApp code is: {{code}}"
        );
        
        // Then
        assertEquals(TemplateTypeEnum.AUTENTICACAO_WHATSAPP_TOKEN, whatsappTemplate.getTemplateType());
        assertEquals(MessageTypeEnum.WHATSAPP, whatsappTemplate.getMessageType());
        assertEquals("ms-auth", whatsappTemplate.getTenantId());
        assertEquals("WhatsApp Authentication Template", whatsappTemplate.getName());
        assertEquals("Template para autenticação por WhatsApp", whatsappTemplate.getDescription());
        assertNull(whatsappTemplate.getSubject());
        assertEquals("Hello! Your WhatsApp code is: {{code}}", whatsappTemplate.getContent());
    }
    
    @Test
    @DisplayName("Deve criar template de push")
    void shouldCreatePushTemplate() {
        // Given & When
        Template pushTemplate = Template.create(
            TemplateTypeEnum.NOTIFICACAO_GERAL,
            MessageTypeEnum.PUSH_NOTIFICATION,
            "ms-auth",
            "Push Notification Template",
            "Template para notificações push",
            "Notification",
            "{{title}}: {{message}}"
        );
        
        // Then
        assertEquals(TemplateTypeEnum.NOTIFICACAO_GERAL, pushTemplate.getTemplateType());
        assertEquals(MessageTypeEnum.PUSH_NOTIFICATION, pushTemplate.getMessageType());
        assertEquals("ms-auth", pushTemplate.getTenantId());
        assertEquals("Push Notification Template", pushTemplate.getName());
        assertEquals("Template para notificações push", pushTemplate.getDescription());
        assertEquals("Notification", pushTemplate.getSubject());
        assertEquals("{{title}}: {{message}}", pushTemplate.getContent());
    }
    
    @Test
    @DisplayName("Deve criar template de recuperação de senha")
    void shouldCreatePasswordRecoveryTemplate() {
        // Given & When
        Template passwordRecoveryTemplate = Template.create(
            TemplateTypeEnum.RECUPERACAO_SENHA,
            MessageTypeEnum.EMAIL,
            "ms-auth",
            "Password Recovery Template",
            "Template para recuperação de senha",
            "Password Recovery",
            "Click the link to reset your password: {{resetLink}}"
        );
        
        // Then
        assertEquals(TemplateTypeEnum.RECUPERACAO_SENHA, passwordRecoveryTemplate.getTemplateType());
        assertEquals(MessageTypeEnum.EMAIL, passwordRecoveryTemplate.getMessageType());
        assertEquals("ms-auth", passwordRecoveryTemplate.getTenantId());
        assertEquals("Password Recovery Template", passwordRecoveryTemplate.getName());
        assertEquals("Template para recuperação de senha", passwordRecoveryTemplate.getDescription());
        assertEquals("Password Recovery", passwordRecoveryTemplate.getSubject());
        assertEquals("Click the link to reset your password: {{resetLink}}", passwordRecoveryTemplate.getContent());
    }
    
    @Test
    @DisplayName("Deve criar template de alerta de segurança")
    void shouldCreateSecurityAlertTemplate() {
        // Given & When
        Template securityAlertTemplate = Template.create(
            TemplateTypeEnum.ALERTA_SEGURANCA,
            MessageTypeEnum.EMAIL,
            "ms-auth",
            "Security Alert Template",
            "Template para alertas de segurança",
            "Security Alert",
            "Security alert: {{message}}"
        );
        
        // Then
        assertEquals(TemplateTypeEnum.ALERTA_SEGURANCA, securityAlertTemplate.getTemplateType());
        assertEquals(MessageTypeEnum.EMAIL, securityAlertTemplate.getMessageType());
        assertEquals("ms-auth", securityAlertTemplate.getTenantId());
        assertEquals("Security Alert Template", securityAlertTemplate.getName());
        assertEquals("Template para alertas de segurança", securityAlertTemplate.getDescription());
        assertEquals("Security Alert", securityAlertTemplate.getSubject());
        assertEquals("Security alert: {{message}}", securityAlertTemplate.getContent());
    }
    
    @Test
    @DisplayName("Deve criar template de confirmação de ação")
    void shouldCreateActionConfirmationTemplate() {
        // Given & When
        Template actionConfirmationTemplate = Template.create(
            TemplateTypeEnum.CONFIRMACAO_ACAO,
            MessageTypeEnum.EMAIL,
            "ms-auth",
            "Action Confirmation Template",
            "Template para confirmação de ações",
            "Action Confirmation",
            "Please confirm your action: {{action}}"
        );
        
        // Then
        assertEquals(TemplateTypeEnum.CONFIRMACAO_ACAO, actionConfirmationTemplate.getTemplateType());
        assertEquals(MessageTypeEnum.EMAIL, actionConfirmationTemplate.getMessageType());
        assertEquals("ms-auth", actionConfirmationTemplate.getTenantId());
        assertEquals("Action Confirmation Template", actionConfirmationTemplate.getName());
        assertEquals("Template para confirmação de ações", actionConfirmationTemplate.getDescription());
        assertEquals("Action Confirmation", actionConfirmationTemplate.getSubject());
        assertEquals("Please confirm your action: {{action}}", actionConfirmationTemplate.getContent());
    }
    
    @Test
    @DisplayName("Deve ativar template")
    void shouldActivateTemplate() {
        // Given
        template.deactivate();
        assertFalse(template.getIsActive());
        
        // When
        template.activate();
        
        // Then
        assertTrue(template.getIsActive());
    }
    
    @Test
    @DisplayName("Deve desativar template")
    void shouldDeactivateTemplate() {
        // Given - template ativo por padrão
        assertTrue(template.getIsActive());
        
        // When
        template.deactivate();
        
        // Then
        assertFalse(template.getIsActive());
    }
    
    @Test
    @DisplayName("Deve verificar se template está ativo")
    void shouldCheckIfTemplateIsActive() {
        // Given - template ativo por padrão
        assertTrue(template.isActive());
        
        // When
        template.deactivate();
        
        // Then
        assertFalse(template.isActive());
    }
    
    @Test
    @DisplayName("Deve atualizar conteúdo do template")
    void shouldUpdateTemplateContent() {
        // Given
        String newContent = "Updated template content with {{newVariable}}";
        
        // When
        template.updateContent(newContent);
        
        // Then
        assertEquals(newContent, template.getContent());
    }
    
    @Test
    @DisplayName("Deve ignorar conteúdo vazio na atualização")
    void shouldIgnoreEmptyContentInUpdate() {
        // Given
        String originalContent = template.getContent();
        
        // When
        template.updateContent("");
        
        // Then
        assertEquals(originalContent, template.getContent());
    }
    
    @Test
    @DisplayName("Deve ignorar conteúdo nulo na atualização")
    void shouldIgnoreNullContentInUpdate() {
        // Given
        String originalContent = template.getContent();
        
        // When
        template.updateContent(null);
        
        // Then
        assertEquals(originalContent, template.getContent());
    }
    
    @Test
    @DisplayName("Deve atualizar assunto do template")
    void shouldUpdateTemplateSubject() {
        // Given
        String newSubject = "Updated template subject";
        
        // When
        template.updateSubject(newSubject);
        
        // Then
        assertEquals(newSubject, template.getSubject());
    }
    
    @Test
    @DisplayName("Deve atualizar variáveis do template")
    void shouldUpdateTemplateVariables() {
        // Given
        String newVariables = "[\"name\", \"code\", \"email\"]";
        
        // When
        template.updateVariables(newVariables);
        
        // Then
        assertEquals(newVariables, template.getVariables());
    }
    
    @Test
    @DisplayName("Deve verificar se template tem variáveis")
    void shouldCheckIfTemplateHasVariables() {
        // Given - template com variáveis por padrão (baseado no builder)
        assertTrue(template.hasVariables());
        
        // When
        template.updateVariables("[\"name\", \"code\"]");
        
        // Then
        assertTrue(template.hasVariables());
    }
    
    @Test
    @DisplayName("Deve implementar equals corretamente")
    void shouldImplementEqualsCorrectly() {
        Template template1 = Template.create(
            TemplateTypeEnum.AUTENTICACAO_EMAIL_TOKEN, MessageTypeEnum.EMAIL, "ms-auth",
            "Email Authentication Template", "Template para autenticação por email",
            "Authentication Code", "Your authentication code is: {{code}}"
        );
        
        Template template2 = Template.create(
            TemplateTypeEnum.AUTENTICACAO_EMAIL_TOKEN, MessageTypeEnum.EMAIL, "ms-auth",
            "Email Authentication Template", "Template para autenticação por email",
            "Authentication Code", "Your authentication code is: {{code}}"
        );
        
        Template template3 = Template.create(
            TemplateTypeEnum.AUTENTICACAO_SMS_TOKEN, MessageTypeEnum.SMS, "ms-auth",
            "SMS Authentication Template", "Template para autenticação por SMS",
            null, "Your SMS code is: {{code}}"
        );
        
        assertEquals(template1, template1);
        assertNotEquals(template1, template2); // IDs diferentes
        assertNotEquals(template1, template3);
        assertNotEquals(template1, null);
        assertNotEquals(template1, "not a template");
    }
    
    @Test
    @DisplayName("Deve implementar hashCode corretamente")
    void shouldImplementHashCodeCorrectly() {
        Template template1 = Template.create(
            TemplateTypeEnum.AUTENTICACAO_EMAIL_TOKEN, MessageTypeEnum.EMAIL, "ms-auth",
            "Email Authentication Template", "Template para autenticação por email",
            "Authentication Code", "Your authentication code is: {{code}}"
        );
        
        Template template2 = Template.create(
            TemplateTypeEnum.AUTENTICACAO_EMAIL_TOKEN, MessageTypeEnum.EMAIL, "ms-auth",
            "Email Authentication Template", "Template para autenticação por email",
            "Authentication Code", "Your authentication code is: {{code}}"
        );
        
        // HashCodes devem ser diferentes para IDs diferentes
        assertNotEquals(template1.hashCode(), template2.hashCode());
    }
    
    @Test
    @DisplayName("Deve implementar toString corretamente")
    void shouldImplementToStringCorrectly() {
        String toString = template.toString();
        
        assertTrue(toString.contains("Template"));
        assertTrue(toString.contains("name='Email Authentication Template'"));
        assertTrue(toString.contains("templateType=AUTENTICACAO_EMAIL_TOKEN"));
        assertTrue(toString.contains("isActive=true"));
    }
}