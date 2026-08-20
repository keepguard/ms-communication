package com.keepguard.ms_communication.domain.entity;

import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Template {
    private UUID id;
    private TemplateTypeEnum templateType;
    private MessageTypeEnum messageType;
    private String tenantId;
    private String name;
    private String description;
    private String subject;
    private String content;
    private String variables; // JSON string
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Construtor padrão
    public Template() {}

    // Construtor completo
    public Template(UUID id, TemplateTypeEnum templateType, MessageTypeEnum messageType, String application,
                         String name, String description, String subject, String content, String variables,
                         List<String> variablesList, Boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.templateType = templateType;
        this.messageType = messageType;
        this.tenantId = application;
        this.name = name;
        this.description = description;
        this.subject = subject;
        this.content = content;
        this.variables = variables;
        // variablesList removido - usando apenas variables como String
        this.isActive = isActive != null ? isActive : true;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Factory method para criação
    public static Template create(TemplateTypeEnum templateType, MessageTypeEnum messageType, String tenantId,
                                      String name, String description, String subject, String content) {
        LocalDateTime now = LocalDateTime.now();
        return new Template(
            UUID.randomUUID(),
            templateType,
            messageType,
            tenantId,
            name,
            description,
            subject,
            content,
            "[]",
            List.of(),
            true,
            now,
            now
        );
    }

    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateContent(String content) {
        if (content != null && !content.trim().isEmpty()) {
            this.content = content;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void updateSubject(String subject) {
        this.subject = subject;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateVariables(String variables) {
        this.variables = variables;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(isActive);
    }

    public boolean hasVariables() {
        return variables != null && !variables.isEmpty();
    }

    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public TemplateTypeEnum getTemplateType() { return templateType; }
    public void setTemplateType(TemplateTypeEnum templateType) { this.templateType = templateType; }

    public MessageTypeEnum getMessageType() { return messageType; }
    public void setMessageType(MessageTypeEnum messageType) { this.messageType = messageType; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getVariables() { return variables; }
    public void setVariables(String variables) { this.variables = variables; }

    // variablesList removido - usando apenas variables como String

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Template{" +
                "id=" + id +
                ", templateType=" + templateType +
                ", messageType=" + messageType +
                ", tenantId='" + tenantId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                ", variables='" + variables + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
