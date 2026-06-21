package com.keepguard.ms_communication.adapters.out.email.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para requisição de envio de email via Google Sender Service
 * Seguindo os princípios de Clean Code e DDD
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailSendRequestDTO {

    @NotBlank(message = "Destinatário é obrigatório")
    @Email(message = "Destinatário deve ser um email válido")
    @JsonProperty("to")
    private String to;

    @NotBlank(message = "Assunto é obrigatório")
    @Size(max = 255, message = "Assunto não pode exceder 255 caracteres")
    @JsonProperty("subject")
    private String subject;

    @Size(max = 50000, message = "HTML não pode exceder 50000 caracteres")
    @JsonProperty("html")
    private String html;

    @Email(message = "CC deve ser um email válido")
    @JsonProperty("cc")
    private String cc;

    @Email(message = "Reply-To deve ser um email válido")
    @JsonProperty("replyTo")
    private String replyTo;
}
