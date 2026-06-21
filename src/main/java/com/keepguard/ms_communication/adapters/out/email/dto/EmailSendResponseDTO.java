package com.keepguard.ms_communication.adapters.out.email.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para resposta de envio de email via Google Sender Service
 * Seguindo os princípios de Clean Code e DDD
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailSendResponseDTO {

    @JsonProperty("messageId")
    private String messageId;
}
