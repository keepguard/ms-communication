package com.keepguard.ms_communication.application.mapper;

import com.keepguard.ms_communication.application.dto.template.TemplateView;
import com.keepguard.ms_communication.domain.dto.template.TemplateCreateCommandDTO;
import com.keepguard.ms_communication.domain.dto.template.TemplateUpdateCommandDTO;
import com.keepguard.ms_communication.domain.entity.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class TemplateApplicationMapper {

    public TemplateCreateCommandDTO toCreateCommand(com.keepguard.ms_communication.domain.dto.template.TemplateCreateCommandDTO dto) {
        if (dto == null) {
            return null;
        }

        try {
            // Apenas retorna o mesmo DTO já que são do mesmo tipo
            return dto;
        } catch (Exception e) {
            log.error("Erro ao mapear TemplateCreateCommandDTO para TemplateCreateCommand: {}", e.getMessage(), e);
            throw e;
        }
    }

    public TemplateUpdateCommandDTO toUpdateCommand(com.keepguard.ms_communication.domain.dto.template.TemplateUpdateCommandDTO dto) {
        if (dto == null) {
            return null;
        }

        try {
            // Apenas retorna o mesmo DTO já que são do mesmo tipo
            return dto;
        } catch (Exception e) {
            log.error("Erro ao mapear TemplateUpdateCommandDTO para TemplateUpdateCommand: {}", e.getMessage(), e);
            throw e;
        }
    }


    public Template toDomain(TemplateCreateCommandDTO command) {
        if (command == null) {
            return null;
        }

        try {
            Template template = Template.create(
                command.getTemplateType(),
                command.getMessageType(),
                command.getApplication(),
                command.getName(),
                command.getDescription(),
                command.getSubject(),
                command.getContent()
            );

            // Configurar propriedades adicionais
            if (command.getIsActive() != null) {
                template.setIsActive(command.getIsActive());
            }
            if (command.getVariables() != null) {
                template.setVariables(command.getVariables());
            }

            return template;
        } catch (Exception e) {
            log.error("Erro ao mapear TemplateCreateCommand para Template: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Template toDomain(TemplateUpdateCommandDTO command, Template existingTemplate) {
        if (command == null || existingTemplate == null) {
            return null;
        }

        try {
            // Atualizar propriedades básicas
            if (command.getName() != null) {
                existingTemplate.setName(command.getName());
            }
            if (command.getDescription() != null) {
                existingTemplate.setDescription(command.getDescription());
            }
            if (command.getMessageType() != null) {
                existingTemplate.setMessageType(command.getMessageType());
            }
            if (command.getTemplateType() != null) {
                existingTemplate.setTemplateType(command.getTemplateType());
            }
            if (command.getContent() != null) {
                existingTemplate.updateContent(command.getContent());
            }
            if (command.getSubject() != null) {
                existingTemplate.updateSubject(command.getSubject());
            }
            if (command.getIsActive() != null) {
                existingTemplate.setIsActive(command.getIsActive());
            }
            if (command.getVariables() != null) {
                existingTemplate.updateVariables(command.getVariables());
            }

            // Atualizar timestamp
            existingTemplate.setUpdatedAt(java.time.LocalDateTime.now());

            return existingTemplate;
        } catch (Exception e) {
            log.error("Erro ao mapear TemplateUpdateCommand para Template: {}", e.getMessage(), e);
            throw e;
        }
    }

    public TemplateView toView(Template template) {
        if (template == null) {
            return null;
        }

        try {
            return new TemplateView(
                template.getId(),
                template.getName(),
                template.getDescription(),
                template.getMessageType(),
                template.getTemplateType(),
                template.getCompanyId(),
                template.getContent(),
                template.getSubject(),
                template.getIsActive(),
                template.getVariables(),
                template.getCreatedAt(),
                template.getUpdatedAt()
            );
        } catch (Exception e) {
            log.error("Erro ao mapear Template para TemplateView: {}", e.getMessage(), e);
            throw e;
        }
    }
}
