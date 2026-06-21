package com.keepguard.ms_communication.infrastructure.provider.n8n.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SMSPayload {
    private String to;
    private String message;
    private String workflowType;
}