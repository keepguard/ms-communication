package com.keepguard.ms_communication.adapters.in.rest.template.mapper;

import com.keepguard.ms_communication.adapters.in.rest.template.dto.request.TemplateCreateRequestDTO;
import com.keepguard.ms_communication.adapters.in.rest.template.dto.request.TemplateUpdateRequestDTO;
import com.keepguard.ms_communication.adapters.in.rest.template.dto.response.*;
import com.keepguard.ms_communication.application.dto.template.TemplateView;
import com.keepguard.ms_communication.domain.dto.template.TemplateCreateCommandDTO;
import com.keepguard.ms_communication.domain.dto.template.TemplateUpdateCommandDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class TemplateAdapterMapper {

    public TemplateCreateCommandDTO toCreateCommand(TemplateCreateRequestDTO dto, UUID xApplicationUuid) {
        if (dto == null) {
            return null;
        }

        try {
            return TemplateCreateCommandDTO.builder()
                    .xApplicationUuid(xApplicationUuid)
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .application(dto.getXApplication())
                    .messageType(dto.getMessageType())
                    .templateType(dto.getTemplateType())
                    .content(dto.getContent())
                    .subject(dto.getSubject())
                    .isActive(dto.getIsActive())
                    .variables(dto.getVariables())
                    .build();
        } catch (Exception e) {
            log.error("Erro ao mapear TemplateCreateDTO para TemplateCreateCommandDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public TemplateUpdateCommandDTO toUpdateCommand(UUID id, TemplateUpdateRequestDTO dto, UUID xApplicationUuid) {
        if (dto == null) {
            return null;
        }

        try {
            return TemplateUpdateCommandDTO.builder()
                    .id(id)
                    .xApplicationUuid(xApplicationUuid)
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .messageType(dto.getMessageType())
                    .templateType(dto.getTemplateType())
                    .content(dto.getContent())
                    .subject(dto.getSubject())
                    .isActive(dto.getIsActive())
                    .variables(dto.getVariables())
                    .build();
        } catch (Exception e) {
            log.error("Erro ao mapear TemplateUpdateDTO para TemplateUpdateCommandDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public TemplateCreateResponseDTO toCreateResponseDTO(TemplateView view) {
        if (view == null) {
            return null;
        }

        try {
            return TemplateCreateResponseDTO.builder()
                    .id(view.id())
                    .name(view.name())
                    .description(view.description())
                    .messageType(view.messageType())
                    .templateType(view.templateType())
                    .type(view.templateType())
                    .xApplication(view.xApplication())
                    .content(view.content())
                    .subject(view.subject())
                    .isActive(view.isActive())
                    .variables(view.variables())
                    .createdAt(view.createdAt())
                    .updatedAt(view.updatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Erro ao mapear TemplateView para TemplateCreateResponseDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public TemplateUpdateResponseDTO toUpdateResponseDTO(TemplateView view) {
        if (view == null) {
            return null;
        }

        try {
            return TemplateUpdateResponseDTO.builder()
                    .id(view.id())
                    .name(view.name())
                    .description(view.description())
                    .messageType(view.messageType())
                    .templateType(view.templateType())
                    .type(view.templateType())
                    .content(view.content())
                    .subject(view.subject())
                    .isActive(view.isActive())
                    .variables(view.variables())
                    .createdAt(view.createdAt())
                    .updatedAt(view.updatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Erro ao mapear TemplateView para TemplateUpdateResponseDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public TemplateGetTemplateByIdResponseDTO toGetTemplateByIdResponseDTO(TemplateView view) {
        if (view == null) {
            return null;
        }

        try {
            return TemplateGetTemplateByIdResponseDTO.builder()
                    .id(view.id())
                    .name(view.name())
                    .description(view.description())
                    .messageType(view.messageType())
                    .templateType(view.templateType())
                    .type(view.templateType())
                    .content(view.content())
                    .subject(view.subject())
                    .isActive(view.isActive())
                    .variables(view.variables())
                    .createdAt(view.createdAt())
                    .updatedAt(view.updatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Erro ao mapear TemplateView para TemplateGetTemplateByIdResponseDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public TemplateGetTemplateByTypeResponseDTO toGetTemplateByTypeResponseDTO(TemplateView view) {
        if (view == null) {
            return null;
        }

        try {
            return TemplateGetTemplateByTypeResponseDTO.builder()
                    .id(view.id())
                    .name(view.name())
                    .description(view.description())
                    .messageType(view.messageType())
                    .templateType(view.templateType())
                    .type(view.templateType())
                    .content(view.content())
                    .subject(view.subject())
                    .isActive(view.isActive())
                    .variables(view.variables())
                    .createdAt(view.createdAt())
                    .updatedAt(view.updatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Erro ao mapear TemplateView para TemplateGetTemplateByTypeResponseDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public TemplateGetTemplatesResponseDTO toGetTemplatesResponseDTO(TemplateView view) {
        if (view == null) {
            return null;
        }

        try {
            return TemplateGetTemplatesResponseDTO.builder()
                    .id(view.id())
                    .name(view.name())
                    .description(view.description())
                    .messageType(view.messageType())
                    .templateType(view.templateType())
                    .type(view.templateType())
                    .xApplication(view.xApplication())
                    .content(view.content())
                    .subject(view.subject())
                    .isActive(view.isActive())
                    .variables(view.variables())
                    .createdAt(view.createdAt())
                    .updatedAt(view.updatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Erro ao mapear TemplateView para TemplateGetTemplatesResponseDTO: {}", e.getMessage(), e);
            throw e;
        }
    }
}
