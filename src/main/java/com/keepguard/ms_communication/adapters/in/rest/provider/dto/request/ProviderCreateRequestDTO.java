package com.keepguard.ms_communication.adapters.in.rest.provider.dto.request;

import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.domain.enums.ProviderTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Dados para criação de um novo provedor de comunicação")
public class ProviderCreateRequestDTO {

    @Schema(description = "Nome do provedor",
            example = "N8N Email Provider",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Nome do provedor é obrigatório")
    private String name;

    @Schema(description = "Tipo do provedor",
            example = "N8N",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Tipo do provedor é obrigatório")
    private ProviderTypeEnum providerType;

    @Schema(description = "Tipo de comunicação suportada",
            example = "EMAIL",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Tipo de comunicação é obrigatório")
    private CommunicationTypeEnum communicationType;

    @Schema(description = "Indica se o provedor deve ser criado ativo",
            example = "true")
    private Boolean isActive = true;

    @Schema(description = "Indica se o provedor deve ser definido como padrão",
            example = "false")
    private Boolean isDefault = false;

    @Schema(description = "Prioridade do provedor (1-10, menor número = maior prioridade)",
            example = "1")
    @Min(value = 1, message = "Prioridade deve ser maior que 0")
    @Max(value = 10, message = "Prioridade deve ser menor ou igual a 10")
    private Integer priority = 1;

    @Schema(description = "URL de conexão com o provedor",
            example = "https://n8n.keepguard.com/webhook/email")
    private String url;

    @Schema(description = "Configuração JSON do provedor",
            example = "{\"apiKey\": \"secret-key\", \"workflowId\": \"123\"}")
    private String configuration; // JSON string

    @Schema(description = "Número máximo de tentativas de envio (1-10)",
            example = "3")
    @Min(value = 1, message = "Máximo de tentativas deve ser maior que 0")
    @Max(value = 10, message = "Máximo de tentativas deve ser menor ou igual a 10")
    private Integer maxRetries = 3;

    @Schema(description = "Timeout em segundos para conexão (5-300)",
            example = "30")
    @Min(value = 5, message = "Timeout deve ser maior que 5 segundos")
    @Max(value = 300, message = "Timeout deve ser menor ou igual a 300 segundos")
    private Integer timeoutSeconds = 30;

    @Schema(description = "Limite de mensagens por minuto",
            example = "60")
    private Integer rateLimitPerMinute;

    @Schema(description = "Limite diário de mensagens",
            example = "1000")
    private Integer dailyLimit;

    @Schema(description = "Limite mensal de mensagens",
            example = "30000")
    private Integer monthlyLimit;
}