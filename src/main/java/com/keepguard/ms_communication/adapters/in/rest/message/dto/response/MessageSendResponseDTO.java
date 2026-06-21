package com.keepguard.ms_communication.adapters.in.rest.message.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta do envio de mensagem")
public class MessageSendResponseDTO {

    @Schema(description = "Indica se a mensagem foi enviada com sucesso",
            example = "true")
    private boolean success;

    @Schema(description = "Mensagem de status do envio",
            example = "Mensagem enviada com sucesso")
    private String message;
}