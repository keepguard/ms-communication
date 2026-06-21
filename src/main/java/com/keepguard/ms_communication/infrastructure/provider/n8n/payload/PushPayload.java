package com.keepguard.ms_communication.infrastructure.provider.n8n.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushPayload {
    private String to;
    private String title;
    private String message;
    private String workflowType;
}