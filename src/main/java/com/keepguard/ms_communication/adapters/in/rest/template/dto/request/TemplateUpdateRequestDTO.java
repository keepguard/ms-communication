package com.keepguard.ms_communication.adapters.in.rest.template.dto.request;

import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class TemplateUpdateRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 200, message = "Nome deve ter no máximo 200 caracteres")
    private String name;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String description;

    @Size(max = 200, message = "Assunto deve ter no máximo 200 caracteres")
    private String subject;

    @NotBlank(message = "Conteúdo é obrigatório")
    private String content;

    private String variables; // JSON string

    @NotNull(message = "Tipo da mensagem é obrigatório")
    private MessageTypeEnum messageType;

    @NotNull(message = "Tipo do template é obrigatório")
    private TemplateTypeEnum templateType;

    private Boolean isActive;
}