package com.keepguard.ms_communication.adapters.in.rest.template.dto.response;

import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Schema(description = "Resposta com dados detalhados do template consultado por ID")
public class TemplateGetTemplateByIdResponseDTO {

    @Schema(description = "ID único do template",
            example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Tipo do template",
            example = "WELCOME")
    private TemplateTypeEnum type;

    @Schema(description = "Tipo do template (alias)",
            example = "WELCOME")
    private TemplateTypeEnum templateType;

    @Schema(description = "Tipo de mensagem do template",
            example = "EMAIL")
    private MessageTypeEnum messageType;

    @Schema(description = "Identificador da aplicação proprietária do template",
            example = "sistema-gestao")
    private String application;

    @Schema(description = "Nome do template",
            example = "Template de Boas-vindas")
    private String name;

    @Schema(description = "Descrição do template",
            example = "Template para enviar email de boas-vindas aos novos usuários")
    private String description;

    @Schema(description = "Assunto padrão da mensagem",
            example = "Bem-vindo ao KeepGuard")
    private String subject;

    @Schema(description = "Conteúdo do template com variáveis",
            example = "Olá {{userName}}, seja bem-vindo ao nosso sistema! Clique aqui para ativar sua conta: {{activationLink}}")
    private String content;

    @Schema(description = "Variáveis disponíveis no template (JSON string)",
            example = "[\"userName\", \"activationLink\", \"companyName\"]")
    private String variables;

    @Schema(description = "Indica se o template está ativo",
            example = "true")
    private Boolean isActive;

    @Schema(description = "Data e hora de criação do template",
            example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Data e hora da última atualização do template",
            example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;
}

