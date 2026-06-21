package com.keepguard.ms_communication.application.mapper;

import com.keepguard.ms_communication.application.dto.template.TemplateCacheView;
import com.keepguard.ms_communication.domain.entity.Template;
import org.springframework.stereotype.Component;

@Component
public class TemplateCacheMapper {

    public TemplateCacheView toCacheView(Template template) {
        if (template == null) {
            return null;
        }

        return new TemplateCacheView(
            template.getId(),
            template.getTemplateType(),
            template.getMessageType(),
            template.getXApplication(),
            template.getName(),
            template.getDescription(),
            template.getSubject(),
            template.getContent(),
            template.getVariables(),
            template.getIsActive(),
            template.getCreatedAt(),
            template.getUpdatedAt()
        );
    }

    public Template toEntity(TemplateCacheView dto) {
        if (dto == null) {
            return null;
        }

        Template template = new Template();
        template.setId(dto.id());
        template.setTemplateType(dto.templateType());
        template.setMessageType(dto.messageType());
        template.setXApplication(dto.application());
        template.setName(dto.name());
        template.setDescription(dto.description());
        template.setSubject(dto.subject());
        template.setContent(dto.content());
        template.setVariables(dto.variables());
        template.setIsActive(dto.isActive());
        template.setCreatedAt(dto.createdAt());
        template.setUpdatedAt(dto.updatedAt());
        
        return template;
    }

}
