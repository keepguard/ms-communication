package com.keepguard.ms_communication.adapters.out.sms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsQueueMessageDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("companyId")
    private String companyId;

    @JsonProperty("recipient")
    private String recipient;

    @JsonProperty("body")
    private String body;

    @JsonProperty("senderId")
    private String senderId;

    @JsonProperty("traceId")
    private String traceId;

    @JsonProperty("correlationId")
    private String correlationId;
}
