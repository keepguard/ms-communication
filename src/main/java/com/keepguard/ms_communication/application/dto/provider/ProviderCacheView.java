package com.keepguard.ms_communication.application.dto.provider;

import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.domain.enums.ProviderTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados de provider para cache")
public record ProviderCacheView(
    @Schema(description = "ID único do provider")
    UUID id,

    @Schema(description = "Nome do provider")
    String name,

    @Schema(description = "Tipo do provider")
    ProviderTypeEnum providerType,

    @Schema(description = "Tipo de comunicação")
    CommunicationTypeEnum communicationType,

    @Schema(description = "Provider ativo")
    Boolean isActive,

    @Schema(description = "Provider padrão")
    Boolean isDefault,

    @Schema(description = "Prioridade")
    Integer priority,

    @Schema(description = "URL")
    String url,

    @Schema(description = "Configuração (JSON)")
    String configuration,

    @Schema(description = "Máximo de tentativas")
    Integer maxRetries,

    @Schema(description = "Timeout em segundos")
    Integer timeoutSeconds,

    @Schema(description = "Rate limit por minuto")
    Integer rateLimitPerMinute,

    @Schema(description = "Limite diário")
    Integer dailyLimit,

    @Schema(description = "Limite mensal")
    Integer monthlyLimit,

    @Schema(description = "Data de criação")
    LocalDateTime createdAt,

    @Schema(description = "Data de atualização")
    LocalDateTime updatedAt
) {}
