package com.keepguard.ms_communication.application.dto.provider;

import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.domain.enums.ProviderTypeEnum;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProviderView(
    UUID id,
    String name,
    ProviderTypeEnum providerType,
    CommunicationTypeEnum communicationType,
    Boolean isActive,
    Boolean isDefault,
    Integer priority,
    String url,
    String configuration,
    Integer maxRetries,
    Integer timeoutSeconds,
    Integer rateLimitPerMinute,
    Integer dailyLimit,
    Integer monthlyLimit,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
