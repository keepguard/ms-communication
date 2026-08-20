package com.keepguard.ms_communication.test.builder;

import com.keepguard.ms_communication.adapters.in.rest.provider.dto.request.ProviderCreateRequestDTO;
import com.keepguard.ms_communication.adapters.in.rest.provider.dto.response.ProviderCreateResponseDTO;
import com.keepguard.ms_communication.adapters.in.rest.provider.dto.request.ProviderUpdateRequestDTO;
import com.keepguard.ms_communication.domain.dto.provider.ProviderCreateCommandDTO;
import com.keepguard.ms_communication.domain.dto.provider.ProviderUpdateCommandDTO;
import com.keepguard.ms_communication.application.dto.provider.ProviderView;
import com.keepguard.ms_communication.domain.entity.Provider;
import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.domain.enums.ProviderTypeEnum;
// import com.keepguard.ms_communication.infrastructure.persistence.entity.ProviderJPA;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Builder para criação de objetos de teste relacionados a Provider
 * Segue o padrão do ms-company
 */
public class ProviderTestBuilder {
    
    private UUID id = UUID.randomUUID();
    private UUID tenantId = UUID.randomUUID();
    private String name = "N8N Provider";
    private ProviderTypeEnum providerType = ProviderTypeEnum.SENDGRID;
    private CommunicationTypeEnum communicationType = CommunicationTypeEnum.EMAIL;
    private Boolean isActive = true;
    private Boolean isDefault = false;
    private Integer priority = 1;
    private String url = "https://api.sendgrid.com/v3/mail/send";
    private Map<String, Object> configuration = new HashMap<>();
    private Integer maxRetries = 3;
    private Integer timeoutSeconds = 30;
    private Integer rateLimitPerMinute = 100;
    private Integer dailyLimit = 10000;
    private Integer monthlyLimit = 300000;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    public static ProviderTestBuilder aProvider() {
        return new ProviderTestBuilder();
    }
    
    public static ProviderTestBuilder builder() {
        return new ProviderTestBuilder();
    }
    
    public ProviderTestBuilder withTenantId(UUID tenantId) {
        this.tenantId = tenantId;
        return this;
    }
    
    public ProviderTestBuilder withId(UUID id) {
        this.id = id;
        return this;
    }
    
    public ProviderTestBuilder withName(String name) {
        this.name = name;
        return this;
    }
    
    public ProviderTestBuilder withProviderType(ProviderTypeEnum providerType) {
        this.providerType = providerType;
        return this;
    }
    
    public ProviderTestBuilder withCommunicationType(CommunicationTypeEnum communicationType) {
        this.communicationType = communicationType;
        return this;
    }
    
    public ProviderTestBuilder withIsActive(Boolean isActive) {
        this.isActive = isActive;
        return this;
    }
    
    public ProviderTestBuilder withIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
        return this;
    }
    
    public ProviderTestBuilder withPriority(Integer priority) {
        this.priority = priority;
        return this;
    }
    
    public ProviderTestBuilder withUrl(String url) {
        this.url = url;
        return this;
    }
    
    public ProviderTestBuilder withConfiguration(Map<String, Object> configuration) {
        this.configuration = configuration;
        return this;
    }
    
    public ProviderTestBuilder withMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
        return this;
    }
    
    public ProviderTestBuilder withTimeoutSeconds(Integer timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
        return this;
    }
    
    public ProviderTestBuilder withRateLimitPerMinute(Integer rateLimitPerMinute) {
        this.rateLimitPerMinute = rateLimitPerMinute;
        return this;
    }
    
    public ProviderTestBuilder withDailyLimit(Integer dailyLimit) {
        this.dailyLimit = dailyLimit;
        return this;
    }
    
    public ProviderTestBuilder withMonthlyLimit(Integer monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
        return this;
    }
    
    public ProviderTestBuilder withCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }
    
    public ProviderTestBuilder withUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }
    
    public ProviderTestBuilder asSendGrid() {
        this.name = "SendGrid Provider";
        this.providerType = ProviderTypeEnum.SENDGRID;
        this.communicationType = CommunicationTypeEnum.EMAIL;
        this.url = "https://api.sendgrid.com/v3/mail/send";
        this.configuration.put("apiKey", "test-api-key");
        this.configuration.put("fromEmail", "noreply@example.com");
        return this;
    }
    
    public ProviderTestBuilder asN8N() {
        this.name = "N8N Provider";
        this.providerType = ProviderTypeEnum.N8N;
        this.communicationType = CommunicationTypeEnum.SMS;
        this.url = "https://n8n.example.com/webhook/sms";
        this.configuration.put("webhookUrl", "https://n8n.example.com/webhook/sms");
        this.configuration.put("apiKey", "test-n8n-key");
        return this;
    }
    
    public ProviderTestBuilder withN8NProvider() {
        return asN8N();
    }
    
    public ProviderTestBuilder withSendGridProvider() {
        return asSendGrid();
    }
    
    public ProviderTestBuilder withTwilioProvider() {
        this.name = "Twilio Provider";
        this.providerType = ProviderTypeEnum.SENDGRID; // Usando SENDGRID como fallback
        this.communicationType = CommunicationTypeEnum.SMS;
        this.url = "https://api.twilio.com/2010-04-01/Accounts/ACxxxxx/Messages.json";
        this.configuration.put("accountSid", "test-account-sid");
        this.configuration.put("authToken", "test-auth-token");
        return this;
    }
    
    public ProviderTestBuilder withEmailProvider() {
        this.name = "Email Provider";
        this.providerType = ProviderTypeEnum.SENDGRID;
        this.communicationType = CommunicationTypeEnum.SENDGRID;
        this.url = "https://api.sendgrid.com/v3/mail/send";
        this.configuration.put("apiKey", "test-sendgrid-key");
        this.configuration.put("fromEmail", "noreply@example.com");
        return this;
    }
    
    public ProviderTestBuilder asDefault() {
        this.isDefault = true;
        this.priority = 1;
        return this;
    }
    
    public ProviderTestBuilder asInactive() {
        this.isActive = false;
        return this;
    }
    
    public ProviderTestBuilder withConfiguration(String key, Object value) {
        this.configuration.put(key, value);
        return this;
    }
    
    public Provider buildDomain() {
        Provider provider = new Provider();
        provider.setId(id);
        provider.setName(name);
        provider.setProviderType(providerType);
        provider.setCommunicationType(communicationType);
        provider.setIsActive(isActive);
        provider.setIsDefault(isDefault);
        provider.setPriority(priority);
        provider.setUrl(url);
        provider.setConfiguration(configuration.toString());
        provider.setMaxRetries(maxRetries);
        provider.setTimeoutSeconds(timeoutSeconds);
        provider.setRateLimitPerMinute(rateLimitPerMinute);
        provider.setDailyLimit(dailyLimit);
        provider.setMonthlyLimit(monthlyLimit);
        provider.setCreatedAt(createdAt);
        provider.setUpdatedAt(updatedAt);
        return provider;
    }
    
    public ProviderView buildView() {
        return new ProviderView(
            id,
            name,
            providerType,
            communicationType,
            isActive,
            isDefault,
            priority,
            url,
            configuration.toString(),
            maxRetries,
            timeoutSeconds,
            rateLimitPerMinute,
            dailyLimit,
            monthlyLimit,
            createdAt,
            updatedAt
        );
    }
    
    public ProviderCreateCommandDTO buildCreateCommand() {
        return ProviderCreateCommandDTO.builder()
                .tenantId(tenantId)
                .name(name)
                .providerType(providerType)
                .communicationType(communicationType)
                .isActive(isActive)
                .isDefault(isDefault)
                .priority(priority)
                .url(url)
                .configuration(configuration.toString())
                .maxRetries(maxRetries)
                .timeoutSeconds(timeoutSeconds)
                .rateLimitPerMinute(rateLimitPerMinute)
                .dailyLimit(dailyLimit)
                .monthlyLimit(monthlyLimit)
                .build();
    }
    
    public ProviderUpdateCommandDTO buildUpdateCommand() {
        return ProviderUpdateCommandDTO.builder()
                .id(id)
                .tenantId(tenantId)
                .name(name)
                .providerType(providerType)
                .communicationType(communicationType)
                .isActive(isActive)
                .isDefault(isDefault)
                .priority(priority)
                .url(url)
                .configuration(configuration.toString())
                .maxRetries(maxRetries)
                .timeoutSeconds(timeoutSeconds)
                .rateLimitPerMinute(rateLimitPerMinute)
                .dailyLimit(dailyLimit)
                .monthlyLimit(monthlyLimit)
                .build();
    }
    
    public ProviderCreateRequestDTO buildCreateDTO() {
        ProviderCreateRequestDTO dto = new ProviderCreateRequestDTO();
        dto.setName(name);
        dto.setProviderType(providerType);
        dto.setCommunicationType(communicationType);
        dto.setIsActive(isActive);
        dto.setIsDefault(isDefault);
        dto.setPriority(priority);
        dto.setUrl(url);
        dto.setConfiguration(configuration.toString());
        dto.setMaxRetries(maxRetries);
        dto.setTimeoutSeconds(timeoutSeconds);
        dto.setRateLimitPerMinute(rateLimitPerMinute);
        dto.setDailyLimit(dailyLimit);
        dto.setMonthlyLimit(monthlyLimit);
        return dto;
    }
    
    public ProviderUpdateRequestDTO buildUpdateDTO() {
        ProviderUpdateRequestDTO dto = new ProviderUpdateRequestDTO();
        dto.setName(name);
        dto.setProviderType(providerType);
        dto.setCommunicationType(communicationType);
        dto.setIsActive(isActive);
        dto.setIsDefault(isDefault);
        dto.setPriority(priority);
        dto.setUrl(url);
        dto.setConfiguration(configuration.toString());
        dto.setMaxRetries(maxRetries);
        dto.setTimeoutSeconds(timeoutSeconds);
        dto.setRateLimitPerMinute(rateLimitPerMinute);
        dto.setDailyLimit(dailyLimit);
        dto.setMonthlyLimit(monthlyLimit);
        return dto;
    }
    
    public ProviderCreateResponseDTO buildResponseDTO() {
        return ProviderCreateResponseDTO.builder()
            .id(id)
            .name(name)
            .providerType(providerType)
            .communicationType(communicationType)
            .isActive(isActive)
            .isDefault(isDefault)
            .priority(priority)
            .url(url)
            .configuration(configuration.toString())
            .maxRetries(maxRetries)
            .timeoutSeconds(timeoutSeconds)
            .rateLimitPerMinute(rateLimitPerMinute)
            .dailyLimit(dailyLimit)
            .monthlyLimit(monthlyLimit)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();
    }
    
    // public ProviderJPA buildJPA() {
    //     ProviderJPA jpa = new ProviderJPA();
    //     jpa.setName(name);
    //     jpa.setProviderType(providerType);
    //     jpa.setCommunicationType(communicationType);
    //     jpa.setIsActive(isActive);
    //     jpa.setIsDefault(isDefault);
    //     jpa.setPriority(priority);
    //     jpa.setUrl(url);
    //     jpa.setConfiguration(configuration);
    //     jpa.setMaxRetries(maxRetries);
    //     jpa.setTimeoutSeconds(timeoutSeconds);
    //     jpa.setRateLimitPerMinute(rateLimitPerMinute);
    //     jpa.setDailyLimit(dailyLimit);
    //     jpa.setMonthlyLimit(monthlyLimit);
    //     jpa.setCreatedAt(createdAt);
    //     jpa.setUpdatedAt(updatedAt);
    //     return jpa;
    // }
}
