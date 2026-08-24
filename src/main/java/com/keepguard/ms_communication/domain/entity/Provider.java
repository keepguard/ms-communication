package com.keepguard.ms_communication.domain.entity;

import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.domain.enums.ProviderTypeEnum;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Builder
public class Provider {
    private UUID id;
    private String name;
    private ProviderTypeEnum providerType;
    private CommunicationTypeEnum communicationType;
    private Boolean isActive;
    private Boolean isDefault;
    private Integer priority;
    private String url;
    private String configuration;
    private Integer maxRetries;
    private Integer timeoutSeconds;
    private Integer rateLimitPerMinute;
    private Integer dailyLimit;
    private Integer monthlyLimit;
    private Map<String, Object> variables; // Variáveis para templates
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Construtor padrão
    public Provider() {}

    // Construtor completo
    public Provider(UUID id, String name, ProviderTypeEnum providerType, CommunicationTypeEnum communicationType,
                         Boolean isActive, Boolean isDefault, Integer priority, String url, String configuration,
                         Integer maxRetries, Integer timeoutSeconds, Integer rateLimitPerMinute,
                         Integer dailyLimit, Integer monthlyLimit, Map<String, Object> variables, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.providerType = providerType;
        this.communicationType = communicationType;
        this.isActive = isActive != null ? isActive : true;
        this.isDefault = isDefault != null ? isDefault : false;
        this.priority = priority != null ? priority : 1;
        this.url = url;
        this.configuration = configuration;
        this.maxRetries = maxRetries != null ? maxRetries : 3;
        this.timeoutSeconds = timeoutSeconds != null ? timeoutSeconds : 30;
        this.rateLimitPerMinute = rateLimitPerMinute;
        this.dailyLimit = dailyLimit;
        this.monthlyLimit = monthlyLimit;
        this.variables = variables;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Factory method para criação
    public static Provider create(String name, ProviderTypeEnum providerType, CommunicationTypeEnum communicationType,
                                      String url, String configuration) {
        LocalDateTime now = LocalDateTime.now();
        return new Provider(
            null,
            name,
            providerType,
            communicationType,
            true,
            false,
            1,
            url,
            configuration,
            3,
            30,
            null,
            null,
            null,
            null,
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

    public void setAsDefault() {
        this.isDefault = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void unsetAsDefault() {
        this.isDefault = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePriority(Integer priority) {
        if (priority != null && priority > 0) {
            this.priority = priority;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public boolean isActive() {
        return Boolean.TRUE.equals(isActive);
    }

    public boolean isDefault() {
        return Boolean.TRUE.equals(isDefault);
    }

    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ProviderTypeEnum getProviderType() { return providerType; }
    public void setProviderType(ProviderTypeEnum providerType) { this.providerType = providerType; }

    public CommunicationTypeEnum getCommunicationType() { return communicationType; }
    public void setCommunicationType(CommunicationTypeEnum communicationType) { this.communicationType = communicationType; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Boolean getIsDefault() { return isDefault; }
    public void setIsDefault(Boolean isDefault) { this.isDefault = isDefault; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getConfiguration() { return configuration; }
    public void setConfiguration(String configuration) { this.configuration = configuration; }

    public Integer getMaxRetries() { return maxRetries; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }

    public Integer getTimeoutSeconds() { return timeoutSeconds; }
    public void setTimeoutSeconds(Integer timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }

    public Integer getRateLimitPerMinute() { return rateLimitPerMinute; }
    public void setRateLimitPerMinute(Integer rateLimitPerMinute) { this.rateLimitPerMinute = rateLimitPerMinute; }

    public Integer getDailyLimit() { return dailyLimit; }
    public void setDailyLimit(Integer dailyLimit) { this.dailyLimit = dailyLimit; }

    public Integer getMonthlyLimit() { return monthlyLimit; }
    public void setMonthlyLimit(Integer monthlyLimit) { this.monthlyLimit = monthlyLimit; }

    public Map<String, Object> getVariables() { return variables; }
    public void setVariables(Map<String, Object> variables) { this.variables = variables; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Provider{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", providerType=" + providerType +
                ", communicationType=" + communicationType +
                ", isActive=" + isActive +
                ", isDefault=" + isDefault +
                ", priority=" + priority +
                ", url='" + url + '\'' +
                ", configuration='" + configuration + '\'' +
                ", maxRetries=" + maxRetries +
                ", timeoutSeconds=" + timeoutSeconds +
                ", rateLimitPerMinute=" + rateLimitPerMinute +
                ", dailyLimit=" + dailyLimit +
                ", monthlyLimit=" + monthlyLimit +
                ", variables=" + variables +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
