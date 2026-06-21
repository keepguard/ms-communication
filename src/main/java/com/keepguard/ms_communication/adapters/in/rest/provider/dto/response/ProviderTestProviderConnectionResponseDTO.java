package com.keepguard.ms_communication.adapters.in.rest.provider.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Schema(description = "Resposta do teste de conectividade do provedor")
public class ProviderTestProviderConnectionResponseDTO {

    @Schema(description = "ID único do provedor testado",
            example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID providerId;

    @Schema(description = "Nome do provedor testado",
            example = "N8N Email Provider")
    private String providerName;

    @Schema(description = "Indica se o provedor está conectado e funcionando",
            example = "true")
    private Boolean isConnected;

    @Schema(description = "Data e hora em que o teste foi realizado",
            example = "2024-01-15T10:30:00")
    private LocalDateTime testedAt;
}

