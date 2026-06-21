package com.keepguard.ms_communication.domain.dto.provider;

import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.domain.enums.ProviderTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderUpdateCommandDTO {
    
    @NotNull(message = "ID do provedor é obrigatório")
    private UUID id;
    
    @NotNull(message = "O header X-Application é obrigatório")
    private UUID xApplicationUuid;
    
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
}

