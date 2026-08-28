package com.keepguard.ms_communication.adapters.out.email.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Payload da fila do srv-email-google-sender (snake_case obrigatório).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("tenant_id")
    private String tenantId;

    @JsonProperty("x_correlation_id")
    private String xCorrelationId;

    @JsonProperty("to")
    private String to;

    @JsonProperty("subject")
    private String subject;

    @JsonProperty("html")
    private String html;

    @JsonProperty("cc")
    private String cc;

    @JsonProperty("reply_to")
    private String replyTo;
}
