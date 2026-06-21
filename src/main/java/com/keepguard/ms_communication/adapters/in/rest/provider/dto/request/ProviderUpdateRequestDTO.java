package com.keepguard.ms_communication.adapters.in.rest.provider.dto.request;

import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.domain.enums.ProviderTypeEnum;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

@Data
public class ProviderUpdateRequestDTO {

    @NotBlank(message = "Nome do provedor é obrigatório")
    private String name;

    @NotNull(message = "Tipo do provedor é obrigatório")
    private ProviderTypeEnum providerType;

    @NotNull(message = "Tipo de comunicação é obrigatório")
    private CommunicationTypeEnum communicationType;

    private Boolean isActive;

    private Boolean isDefault;

    @Min(value = 1, message = "Prioridade deve ser maior que 0")
    @Max(value = 10, message = "Prioridade deve ser menor ou igual a 10")
    private Integer priority;

    private String url;

    private String configuration; // JSON string

    @Min(value = 1, message = "Máximo de tentativas deve ser maior que 0")
    @Max(value = 10, message = "Máximo de tentativas deve ser menor ou igual a 10")
    private Integer maxRetries;

    @Min(value = 5, message = "Timeout deve ser maior que 5 segundos")
    @Max(value = 300, message = "Timeout deve ser menor ou igual a 300 segundos")
    private Integer timeoutSeconds;

    private Integer rateLimitPerMinute;

    private Integer dailyLimit;

    private Integer monthlyLimit;
}