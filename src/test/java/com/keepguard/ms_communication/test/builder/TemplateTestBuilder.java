package com.keepguard.ms_communication.test.builder;

import com.keepguard.ms_communication.adapters.in.rest.template.dto.request.TemplateCreateRequestDTO;
import com.keepguard.ms_communication.adapters.in.rest.template.dto.request.TemplateUpdateRequestDTO;
import com.keepguard.ms_communication.domain.dto.template.TemplateCreateCommandDTO;
import com.keepguard.ms_communication.domain.dto.template.TemplateUpdateCommandDTO;
import com.keepguard.ms_communication.application.dto.template.TemplateView;
import com.keepguard.ms_communication.domain.entity.Template;
import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
// import com.keepguard.ms_communication.infrastructure.persistence.entity.TemplateJPA;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Builder para criação de objetos de teste relacionados a Template
 * Segue o padrão do ms-company
 */
public class TemplateTestBuilder {
    
    private UUID id = UUID.randomUUID();
    private UUID xApplicationUuid = UUID.randomUUID();
    private TemplateTypeEnum templateType = TemplateTypeEnum.CADASTRO_SUCESSO;
    private MessageTypeEnum messageType = MessageTypeEnum.EMAIL;
    private String application = "test-app";
    private String name = "Email Template";
    private String description = "Test template description";
    private String subject = "Welcome {{name}}";
    private String content = "Hello {{name}}, welcome to our platform!";
    private Map<String, Object> variables = new HashMap<>();
    private Boolean isActive = true;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    public static TemplateTestBuilder aTemplate() {
        return new TemplateTestBuilder();
    }
    
    public static TemplateTestBuilder builder() {
        return new TemplateTestBuilder();
    }
    
    public TemplateTestBuilder withXApplication(UUID xApplicationUuid) {
        this.xApplicationUuid = xApplicationUuid;
        return this;
    }
    
    public TemplateTestBuilder withId(UUID id) {
        this.id = id;
        return this;
    }
    
    public TemplateTestBuilder withTemplateType(TemplateTypeEnum templateType) {
        this.templateType = templateType;
        return this;
    }
    
    public TemplateTestBuilder withMessageType(MessageTypeEnum messageType) {
        this.messageType = messageType;
        return this;
    }
    
    public TemplateTestBuilder withApplication(String application) {
        this.application = application;
        return this;
    }
    
    public TemplateTestBuilder withName(String name) {
        this.name = name;
        return this;
    }
    
    public TemplateTestBuilder withDescription(String description) {
        this.description = description;
        return this;
    }
    
    public TemplateTestBuilder withSubject(String subject) {
        this.subject = subject;
        return this;
    }
    
    public TemplateTestBuilder withContent(String content) {
        this.content = content;
        return this;
    }
    
    public TemplateTestBuilder withVariables(Map<String, Object> variables) {
        this.variables = variables != null ? variables : new HashMap<>();
        return this;
    }
    
    public TemplateTestBuilder withIsActive(Boolean isActive) {
        this.isActive = isActive;
        return this;
    }
    
    public TemplateTestBuilder withCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }
    
    public TemplateTestBuilder withUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }
    
    public TemplateTestBuilder asWelcomeEmail() {
        this.templateType = TemplateTypeEnum.CADASTRO_SUCESSO;
        this.messageType = MessageTypeEnum.EMAIL;
        this.name = "Welcome Email Template";
        this.description = "Template for welcome emails";
        this.subject = "Welcome {{name}}!";
        this.content = "Hello {{name}}, welcome to our platform! We're excited to have you here.";
        this.variables.put("name", "User Name");
        return this;
    }
    
    public TemplateTestBuilder asPasswordReset() {
        this.templateType = TemplateTypeEnum.RECUPERACAO_SENHA;
        this.messageType = MessageTypeEnum.EMAIL;
        this.name = "Password Reset Template";
        this.description = "Template for password reset emails";
        this.subject = "Reset your password";
        this.content = "Hello {{name}}, click the link to reset your password: {{resetLink}}";
        this.variables.put("name", "User Name");
        this.variables.put("resetLink", "Reset Link");
        return this;
    }
    
    public TemplateTestBuilder asSmsVerification() {
        this.templateType = TemplateTypeEnum.AUTENTICACAO_SMS_TOKEN;
        this.messageType = MessageTypeEnum.SMS;
        this.name = "SMS Verification Template";
        this.description = "Template for SMS verification codes";
        this.subject = null; // SMS não tem subject
        this.content = "Your verification code is: {{code}}";
        this.variables.put("code", "Verification Code");
        return this;
    }
    
    public TemplateTestBuilder asWhatsAppNotification() {
        this.templateType = TemplateTypeEnum.NOTIFICACAO_GERAL;
        this.messageType = MessageTypeEnum.WHATSAPP;
        this.name = "WhatsApp Notification Template";
        this.description = "Template for WhatsApp notifications";
        this.subject = null; // WhatsApp não tem subject
        this.content = "Hello {{name}}, you have a new notification: {{message}}";
        this.variables.put("name", "User Name");
        this.variables.put("message", "Notification Message");
        return this;
    }
    
    public TemplateTestBuilder asPushNotification() {
        this.templateType = TemplateTypeEnum.NOTIFICACAO_GERAL;
        this.messageType = MessageTypeEnum.PUSH_NOTIFICATION;
        this.name = "Push Notification Template";
        this.description = "Template for push notifications";
        this.subject = "New Notification";
        this.content = "Hello {{name}}, you have a new notification: {{message}}";
        this.variables.put("name", "User Name");
        this.variables.put("message", "Notification Message");
        return this;
    }
    
    public TemplateTestBuilder asInactive() {
        this.isActive = false;
        return this;
    }
    
    public TemplateTestBuilder withEmailTemplate() {
        return asWelcomeEmail();
    }
    
    public TemplateTestBuilder withSmsTemplate() {
        return asSmsVerification();
    }
    
    public TemplateTestBuilder withWhatsAppTemplate() {
        return asWhatsAppNotification();
    }
    
    public TemplateTestBuilder withPushTemplate() {
        return asPushNotification();
    }
    
    public TemplateTestBuilder withPasswordResetTemplate() {
        return asPasswordReset();
    }
    
    public TemplateTestBuilder withWelcomeTemplate() {
        return asWelcomeEmail();
    }
    
    public TemplateTestBuilder withNotificationTemplate() {
        return asPushNotification();
    }
    
    public TemplateTestBuilder withVariables(String key, Object value) {
        this.variables.put(key, value);
        return this;
    }
    
    public Template buildDomain() {
        Template template = new Template();
        template.setId(id);
        template.setName(name);
        template.setDescription(description);
        template.setMessageType(messageType);
        template.setTemplateType(templateType);
        template.setContent(content);
        template.setSubject(subject);
        template.setXApplication(application);
        template.setIsActive(isActive);
        template.setVariables(variables.toString());
        template.setCreatedAt(createdAt);
        template.setUpdatedAt(updatedAt);
        return template;
    }
    
    public TemplateView buildView() {
        return new TemplateView(
            id,
            name,
            description,
            messageType,
            templateType,
            application,
            content,
            subject,
            isActive,
            variables.toString(),
            createdAt,
            updatedAt
        );
    }
    
    public TemplateCreateCommandDTO buildCreateCommand() {
        return TemplateCreateCommandDTO.builder()
                .xApplicationUuid(xApplicationUuid)
                .name(name)
                .description(description)
                .application(application)
                .messageType(messageType)
                .templateType(templateType)
                .content(content)
                .subject(subject)
                .isActive(isActive)
                .variables(variables.toString())
                .build();
    }
    
    public TemplateUpdateCommandDTO buildUpdateCommand() {
        return TemplateUpdateCommandDTO.builder()
                .id(id)
                .xApplicationUuid(xApplicationUuid)
                .name(name)
                .description(description)
                .messageType(messageType)
                .templateType(templateType)
                .content(content)
                .subject(subject)
                .isActive(isActive)
                .variables(variables.toString())
                .build();
    }
    
    public TemplateCreateRequestDTO buildCreateDTO() {
        TemplateCreateRequestDTO dto = new TemplateCreateRequestDTO();
        dto.setTemplateType(templateType);
        dto.setMessageType(messageType);
        // dto.setApplication(application); // Método não existe
        dto.setName(name);
        dto.setDescription(description);
        dto.setSubject(subject);
        dto.setContent(content);
        dto.setVariables(variables.toString());
        dto.setIsActive(isActive);
        return dto;
    }
    
    public TemplateUpdateRequestDTO buildUpdateDTO() {
        TemplateUpdateRequestDTO dto = new TemplateUpdateRequestDTO();
        dto.setTemplateType(templateType);
        dto.setMessageType(messageType);
        // dto.setApplication(application); // Método não existe
        dto.setName(name);
        dto.setDescription(description);
        dto.setSubject(subject);
        dto.setContent(content);
        dto.setVariables(variables.toString());
        dto.setIsActive(isActive);
        return dto;
    }
    
    // public TemplateJPA buildJPA() {
    //     TemplateJPA jpa = new TemplateJPA();
    //     jpa.setTemplateType(templateType);
    //     jpa.setMessageType(messageType);
    //     jpa.setApplication(application);
    //     jpa.setName(name);
    //     jpa.setDescription(description);
    //     jpa.setSubject(subject);
    //     jpa.setContent(content);
    //     jpa.setVariables(variables);
    //     jpa.setIsActive(isActive);
    //     jpa.setCreatedAt(createdAt);
    //     jpa.setUpdatedAt(updatedAt);
    //     return jpa;
    // }
}
