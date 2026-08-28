package com.keepguard.ms_communication.domain.dto.template;

import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
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
public class TemplateUpdateCommandDTO {
    
    @NotNull(message = "ID do template é obrigatório")
    private UUID id;
    
    @NotNull(message = "O header X-Company-Id é obrigatório")
    private UUID companyId;
    
    private String name;
    private String description;
    private MessageTypeEnum messageType;
    private TemplateTypeEnum templateType;
    private String content;
    private String subject;
    private Boolean isActive;
    private String variables;
}

