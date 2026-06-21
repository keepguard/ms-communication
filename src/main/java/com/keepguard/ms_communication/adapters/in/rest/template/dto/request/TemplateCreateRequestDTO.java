package com.keepguard.ms_communication.adapters.in.rest.template.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@Schema(description = "Dados para criação de um novo template de comunicação")
public class TemplateCreateRequestDTO {

    @Schema(description = "Tipo do template (alias)",
            example = "WELCOME",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Tipo do template é obrigatório")
    private TemplateTypeEnum templateType;

    @Schema(description = "Tipo de mensagem do template",
            example = "EMAIL",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Tipo da mensagem é obrigatório")
    private MessageTypeEnum messageType;

    @Schema(description = "Nome do template",
            example = "Template de Boas-vindas",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
    private String name;

    @Schema(description = "Descrição do template",
            example = "Template para enviar email de boas-vindas aos novos usuários",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String description;

    @Schema(description = "Assunto padrão da mensagem",
            example = "Bem-vindo ao KeepGuard")
    @Size(max = 200, message = "Assunto deve ter no máximo 200 caracteres")
    private String subject;

    @Schema(description = "Conteúdo do template com variáveis (use {{variavel}} para variáveis)",
            example = "Olá {{userName}}, seja bem-vindo ao nosso sistema! Clique aqui para ativar sua conta: {{activationLink}}",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Conteúdo é obrigatório")
    private String content;

    @Schema(description = "Variáveis disponíveis no template (JSON string)",
            example = "[\"userName\", \"activationLink\", \"companyName\"]")
    private String variables; // JSON string

    @Schema(description = "Identificador da aplicação proprietária do template",
            example = "sistema-gestao",
            requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Aplicação é obrigatória")
    @Size(max = 100, message = "Aplicação deve ter no máximo 100 caracteres")
    @JsonProperty("xApplication")
    private String xApplication;

    @Schema(description = "Indica se o template está ativo",
            example = "true")
    private Boolean isActive;
}