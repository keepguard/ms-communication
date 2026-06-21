package com.keepguard.ms_communication.adapters.in.rest.provider.dto.response;

import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.domain.enums.ProviderTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Schema(description = "Resposta com dados do provedor de comunicação")
public class ProviderUpdateResponseDTO {
    @Schema(description = "ID único do provedor",
            example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Nome do provedor",
            example = "N8N Email Provider")
    private String name;

    @Schema(description = "Tipo do provedor",
            example = "N8N")
    private ProviderTypeEnum providerType;

    @Schema(description = "Tipo de comunicação suportada",
            example = "EMAIL")
    private CommunicationTypeEnum communicationType;

    @Schema(description = "Indica se o provedor está ativo",
            example = "true")
    private Boolean isActive;

    @Schema(description = "Indica se o provedor é o padrão para seu tipo",
            example = "false")
    private Boolean isDefault;

    @Schema(description = "Prioridade do provedor (menor número = maior prioridade)",
            example = "1")
    private Integer priority;

    @Schema(description = "URL de conexão com o provedor",
            example = "https://n8n.keepguard.com/webhook/email")
    private String url;

    @Schema(description = "Configuração JSON do provedor",
            example = "{\"apiKey\": \"secret-key\", \"workflowId\": \"123\"}")
    private String configuration;

    @Schema(description = "Número máximo de tentativas de envio",
            example = "3")
    private Integer maxRetries;

    @Schema(description = "Timeout em segundos para conexão",
            example = "30")
    private Integer timeoutSeconds;

    @Schema(description = "Limite de mensagens por minuto",
            example = "60")
    private Integer rateLimitPerMinute;

    @Schema(description = "Limite diário de mensagens",
            example = "1000")
    private Integer dailyLimit;

    @Schema(description = "Limite mensal de mensagens",
            example = "30000")
    private Integer monthlyLimit;

    @Schema(description = "Data e hora de criação do provedor",
            example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Data e hora da última atualização do provedor",
            example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;

}
