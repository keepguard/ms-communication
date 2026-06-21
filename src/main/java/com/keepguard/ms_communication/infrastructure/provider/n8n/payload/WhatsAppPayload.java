package com.keepguard.ms_communication.infrastructure.provider.n8n.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhatsAppPayload {
    private String chatId;
    private String text;
    private String messageType;
    private String templateType;
}