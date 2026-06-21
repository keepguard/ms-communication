package com.keepguard.ms_communication.domain.dto.template;

import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateCreateCommandDTO {
    
    @NotNull(message = "O header X-Application é obrigatório")
    private UUID xApplicationUuid;
    
    @NotBlank(message = "Nome é obrigatório")
    private String name;
    
    private String description;
    
    @NotBlank(message = "Aplicação é obrigatória")
    private String application;
    
    @NotNull(message = "Tipo da mensagem é obrigatório")
    private MessageTypeEnum messageType;
    
    @NotNull(message = "Tipo do template é obrigatório")
    private TemplateTypeEnum templateType;
    
    private String content;
    private String subject;
    private Boolean isActive;
    private String variables;
}

