package com.keepguard.ms_communication.application.service.template;

import com.keepguard.ms_communication.application.dto.template.TemplateView;
import com.keepguard.ms_communication.domain.dto.template.TemplateCreateCommandDTO;
import com.keepguard.ms_communication.domain.dto.template.TemplateUpdateCommandDTO;
import com.keepguard.lib_common.logging.annotation.LogOperation;
import com.keepguard.ms_communication.application.port.out.metrics.MetricsPort;
import com.keepguard.ms_communication.application.service.exception.AlreadyExistsException;
import com.keepguard.ms_communication.application.service.exception.NotFoundException;
import com.keepguard.ms_communication.application.port.out.persistence.TemplateRepositoryPort;
import com.keepguard.ms_communication.application.mapper.TemplateApplicationMapper;
import com.keepguard.ms_communication.domain.entity.Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TemplateCommandService  {

    private final TemplateRepositoryPort repositoryPort;
    private final TemplateApplicationMapper mapper;
    private final MetricsPort metricsPort;

    @LogOperation(
        operation = "CREATE_TEMPLATE",
        description = "Criando novo template: {command.name}",
        audit = true,
        auditAction = "CREATE",
        auditEntityType = "TEMPLATE"
    )
    public TemplateView create(TemplateCreateCommandDTO command) {
        log.info("Criando template: {} - Application: {}", command.getName(), command.getApplication());

        // Validar se já existe template com mesmo nome
        if (repositoryPort.existsByName(command.getName())) {
            throw new AlreadyExistsException("Template com nome '" + command.getName() + "' já existe");
        }

        // Criar entidade de domínio
        Template template = Template.create(
            command.getTemplateType(),
            command.getMessageType(),
            command.getApplication(), // usar a aplicação do comando
            command.getName(),
            command.getDescription(),
            command.getSubject(),
            command.getContent()
        );
        
        log.info("Template criado com companyId: {}", template.getCompanyId());
        log.info("Command application: {}", command.getApplication());

        // Configurar campos opcionais
        if (command.getIsActive() != null) {
            template.setIsActive(command.getIsActive());
        }
        if (command.getVariables() != null) {
            template.updateVariables(command.getVariables());
        }

        // Salvar
        Template savedTemplate = repositoryPort.save(template);

        metricsPort.incrementCounter("template_created_total",
            Map.of("entity_id", savedTemplate.getId().toString(), "type", command.getTemplateType().name()));

        log.info("Template criado com sucesso: {}", savedTemplate.getId());
        return mapper.toView(savedTemplate);
    }

    @LogOperation(
        operation = "UPDATE_TEMPLATE",
        description = "Atualizando template: {id}",
        audit = true,
        auditAction = "UPDATE",
        auditEntityType = "TEMPLATE"
    )
    public TemplateView update(UUID id, TemplateUpdateCommandDTO command) {
        log.info("Atualizando template: {}", id);

        // Buscar template existente
        Template existingTemplate = repositoryPort.findById(id)
                .orElseThrow(() -> new NotFoundException("Template não encontrado: " + id));

        // Validar se nome já existe em outro template
        if (repositoryPort.existsByNameAndIdNot(command.getName(), id)) {
            throw new AlreadyExistsException("Template com nome '" + command.getName() + "' já existe");
        }

        // Atualizar campos
        existingTemplate.setName(command.getName());
        existingTemplate.setDescription(command.getDescription());
        existingTemplate.setMessageType(command.getMessageType());
        existingTemplate.setTemplateType(command.getTemplateType());
        existingTemplate.updateContent(command.getContent());
        existingTemplate.updateSubject(command.getSubject());
        existingTemplate.setIsActive(command.getIsActive());
        if (command.getVariables() != null) {
            existingTemplate.updateVariables(command.getVariables());
        }

        // Salvar
        Template savedTemplate = repositoryPort.save(existingTemplate);

        metricsPort.incrementCounter("template_updated_total",
            Map.of("entity_id", savedTemplate.getId().toString()));

        log.info("Template atualizado com sucesso: {}", savedTemplate.getId());
        return mapper.toView(savedTemplate);
    }

    @LogOperation(
        operation = "DELETE_TEMPLATE",
        description = "Removendo template: {id}",
        audit = true,
        auditAction = "DELETE",
        auditEntityType = "TEMPLATE"
    )
    public void delete(UUID id) {
        log.info("Removendo template: {}", id);

        if (!repositoryPort.findById(id).isPresent()) {
            throw new NotFoundException("Template não encontrado: " + id);
        }

        repositoryPort.deleteById(id);
        
        metricsPort.incrementCounter("template_deleted_total",
            Map.of("entity_id", id.toString()));

        log.info("Template removido com sucesso: {}", id);
    }

    @LogOperation(
        operation = "ACTIVATE_TEMPLATE",
        description = "Ativando template: {id}",
        audit = true,
        auditAction = "ACTIVATE",
        auditEntityType = "TEMPLATE"
    )
    public void activate(UUID id) {
        log.info("Ativando template: {}", id);

        Template template = repositoryPort.findById(id)
                .orElseThrow(() -> new NotFoundException("Template não encontrado: " + id));

        template.activate();
        repositoryPort.save(template);

        log.info("Template ativado com sucesso: {}", id);
    }

    @LogOperation(
        operation = "DEACTIVATE_TEMPLATE",
        description = "Desativando template: {id}",
        audit = true,
        auditAction = "DEACTIVATE",
        auditEntityType = "TEMPLATE"
    )
    public void deactivate(UUID id) {
        log.info("Desativando template: {}", id);

        Template template = repositoryPort.findById(id)
                .orElseThrow(() -> new NotFoundException("Template não encontrado: " + id));

        template.deactivate();
        repositoryPort.save(template);

        log.info("Template desativado com sucesso: {}", id);
    }
}
