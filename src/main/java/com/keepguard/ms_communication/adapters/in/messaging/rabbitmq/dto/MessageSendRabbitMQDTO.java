package com.keepguard.ms_communication.adapters.in.messaging.rabbitmq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;


public record MessageSendRabbitMQDTO(
    
    @JsonProperty("companyId")
    @NotBlank(message = "companyId é obrigatório")
    @Size(max = 100, message = "companyId deve ter no máximo 100 caracteres")
    String companyId,
    
    @JsonProperty("xCorrelationId")
    @NotBlank(message = "xCorrelationId é obrigatório")
    @Size(max = 100, message = "xCorrelationId deve ter no máximo 100 caracteres")
    String xCorrelationId,
    
    @JsonProperty("messageType")
    @NotNull(message = "Tipo da mensagem é obrigatório")
    MessageTypeEnum messageType,
    
    @JsonProperty("recipient")
    @NotBlank(message = "Destinatário é obrigatório")
    @Size(max = 200, message = "Destinatário deve ter no máximo 200 caracteres")
    String recipient,
    
    @JsonProperty("templateType")
    @NotNull(message = "Tipo do template é obrigatório")
    TemplateTypeEnum templateType,
    
    @JsonProperty("subject")
    @Size(max = 200, message = "Assunto deve ter no máximo 200 caracteres")
    String subject,
    
    @JsonProperty("content")
    @Size(max = 1000, message = "Conteúdo deve ter no máximo 1000 caracteres")
    String content,
    
    @JsonProperty("communicationType")
    @NotNull(message = "Tipo de comunicação é obrigatório")
    CommunicationTypeEnum communicationType,
    
    @JsonProperty("codeUser")
    @Size(max = 100, message = "CodeUser deve ter no máximo 100 caracteres")
    String codeUser,
    
    @JsonProperty("variables")
    Map<String, Object> variables
    
) {
    
    /**
     * Valida se a mensagem está em formato válido.
     * 
     * @return true se a mensagem for válida
     */
    public boolean isValid() {
        return companyId != null && !companyId.trim().isEmpty()
            && xCorrelationId != null && !xCorrelationId.trim().isEmpty()
            && messageType != null
            && recipient != null && !recipient.trim().isEmpty()
            && templateType != null
            && communicationType != null;
    }
    
    /**
     * Retorna uma representação da mensagem para logs.
     * 
     * @return String formatada para logging
     */
    public String toLogString() {
        return String.format(
            "MessageSendRabbitMQDTO{companyId='%s', xCorrelationId='%s', messageType=%s, recipient=%s, templateType=%s, communicationType=%s, codeUser='%s'}",
            companyId, xCorrelationId, messageType, recipient, templateType, communicationType, codeUser
        );
    }
    
}
