package com.keepguard.ms_communication.application.dto.template;

import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Dados de template para cache")
public record TemplateCacheView(
    @Schema(description = "ID único do template")
    UUID id,

    @Schema(description = "Tipo do template")
    TemplateTypeEnum templateType,

    @Schema(description = "Tipo da mensagem")
    MessageTypeEnum messageType,

    @Schema(description = "Aplicação")
    String application,

    @Schema(description = "Nome do template")
    String name,

    @Schema(description = "Descrição")
    String description,

    @Schema(description = "Assunto")
    String subject,

    @Schema(description = "Conteúdo")
    String content,

    @Schema(description = "Variáveis (JSON)")
    String variables,

    @Schema(description = "Template ativo")
    Boolean isActive,

    @Schema(description = "Data de criação")
    LocalDateTime createdAt,

    @Schema(description = "Data de atualização")
    LocalDateTime updatedAt
) {}
