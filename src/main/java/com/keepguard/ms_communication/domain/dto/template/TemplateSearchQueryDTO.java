package com.keepguard.ms_communication.domain.dto.template;

import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateSearchQueryDTO {
    
    private UUID id;
    private String name;
    private String description;
    private MessageTypeEnum messageType;
    private TemplateTypeEnum templateType;
    private Boolean isActive;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}

