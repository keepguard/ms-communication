package com.keepguard.ms_communication.infrastructure.persistence.mapper;

import com.keepguard.ms_communication.domain.entity.Template;
import com.keepguard.ms_communication.infrastructure.persistence.entity.TemplateJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class TemplateJpaMapper {

    public TemplateJpaEntity toEntity(Template domain) {
        if (domain == null) {
            return null;
        }

        return TemplateJpaEntity.builder()
                .id(domain.getId())
                .templateType(domain.getTemplateType())
                .messageType(domain.getMessageType())
                .xApplication(domain.getXApplication())
                .name(domain.getName())
                .description(domain.getDescription())
                .subject(domain.getSubject())
                .content(domain.getContent())
                .variables(domain.getVariables())
                .isActive(domain.getIsActive())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public Template toDomain(TemplateJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Template(
                entity.getId(),
                entity.getTemplateType(),
                entity.getMessageType(),
                entity.getXApplication(),
                entity.getName(),
                entity.getDescription(),
                entity.getSubject(),
                entity.getContent(),
                entity.getVariables(),
                null, // variablesList será convertido quando necessário
                entity.getIsActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
