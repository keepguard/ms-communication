package com.keepguard.ms_communication.application.dto.template;

import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;

import java.time.LocalDateTime;
import java.util.UUID;

public record TemplateView(
    UUID id,
    String name,
    String description,
    MessageTypeEnum messageType,
    TemplateTypeEnum templateType,
    String xApplication,
    String content,
    String subject,
    Boolean isActive,
    String variables,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
