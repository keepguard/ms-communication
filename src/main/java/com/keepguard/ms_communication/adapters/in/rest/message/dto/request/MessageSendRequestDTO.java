package com.keepguard.ms_communication.adapters.in.rest.message.dto.request;

import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Map;

@Data
public class MessageSendRequestDTO {

    @NotNull(message = "Tipo da mensagem é obrigatório")
    private MessageTypeEnum messageType;

    @NotBlank(message = "Destinatário é obrigatório")
    @Size(max = 200, message = "Destinatário deve ter no máximo 200 caracteres")
    private String recipient;

    @NotNull(message = "Tipo do template é obrigatório")
    private TemplateTypeEnum templateType;

    @Size(max = 200, message = "Assunto deve ter no máximo 200 caracteres")
    private String subject;

    @Size(max = 1000, message = "Conteúdo deve ter no máximo 1000 caracteres")
    private String content;

    @NotNull(message = "Tipo de comunicação é obrigatório")
    private CommunicationTypeEnum communicationType;

    @Size(max = 100, message = "CodeUser deve ter no máximo 100 caracteres")
    private String codeUser; // Código do usuário para log

    private Map<String, Object> variables; // Variáveis para substituição no template
}