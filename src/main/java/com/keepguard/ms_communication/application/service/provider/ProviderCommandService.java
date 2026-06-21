package com.keepguard.ms_communication.application.service.provider;

import com.keepguard.ms_communication.application.dto.provider.ProviderView;
import com.keepguard.ms_communication.domain.dto.provider.ProviderCreateCommandDTO;
import com.keepguard.ms_communication.domain.dto.provider.ProviderUpdateCommandDTO;
import com.keepguard.lib_common.logging.annotation.LogOperation;
import com.keepguard.ms_communication.application.port.out.metrics.MetricsPort;
import com.keepguard.ms_communication.application.service.exception.AlreadyExistsException;
import com.keepguard.ms_communication.application.service.exception.NotFoundException;
import com.keepguard.ms_communication.application.port.out.persistence.ProviderRepositoryPort;
import com.keepguard.ms_communication.application.mapper.ProviderApplicationMapper;
import com.keepguard.ms_communication.domain.entity.Provider;
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
public class ProviderCommandService  {

    private final ProviderRepositoryPort repositoryPort;
    private final ProviderApplicationMapper mapper;
    private final MetricsPort metricsPort;

    @LogOperation(
        operation = "CREATE_PROVIDER",
        description = "Criando novo provedor: {command.name}",
        audit = true,
        auditAction = "CREATE",
        auditEntityType = "PROVIDER"
    )
    public ProviderView create(ProviderCreateCommandDTO command) {
        log.info("Criando provedor: {}", command.getName());

        // Validar se já existe provedor com mesmo nome
        if (repositoryPort.existsByName(command.getName())) {
            throw new AlreadyExistsException("Provedor com nome '" + command.getName() + "' já existe");
        }

        // Criar entidade de domínio
        Provider provider = Provider.create(
            command.getName(),
            command.getProviderType(),
            command.getCommunicationType(),
            command.getUrl(),
            command.getConfiguration()
        );

        // Configurar campos opcionais
        if (command.getIsActive() != null) {
            provider.setIsActive(command.getIsActive());
        }
        if (command.getIsDefault() != null) {
            provider.setIsDefault(command.getIsDefault());
        }
        if (command.getPriority() != null) {
            provider.updatePriority(command.getPriority());
        }
        if (command.getMaxRetries() != null) {
            provider.setMaxRetries(command.getMaxRetries());
        }
        if (command.getTimeoutSeconds() != null) {
            provider.setTimeoutSeconds(command.getTimeoutSeconds());
        }
        if (command.getRateLimitPerMinute() != null) {
            provider.setRateLimitPerMinute(command.getRateLimitPerMinute());
        }
        if (command.getDailyLimit() != null) {
            provider.setDailyLimit(command.getDailyLimit());
        }
        if (command.getMonthlyLimit() != null) {
            provider.setMonthlyLimit(command.getMonthlyLimit());
        }

        // Salvar
        Provider savedProvider = repositoryPort.save(provider);

        metricsPort.incrementCounter("provider_created_total",
            Map.of("entity_id", savedProvider.getId().toString(), "type", command.getProviderType().name()));

        log.info("Provedor criado com sucesso: {}", savedProvider.getId());
        return mapper.toView(savedProvider);
    }

    @LogOperation(
        operation = "UPDATE_PROVIDER",
        description = "Atualizando provedor: {id}",
        audit = true,
        auditAction = "UPDATE",
        auditEntityType = "PROVIDER"
    )
    public ProviderView update(UUID id, ProviderUpdateCommandDTO command) {
        log.info("Atualizando provedor: {}", id);

        // Buscar provedor existente
        Provider existingProvider = repositoryPort.findById(id)
                .orElseThrow(() -> new NotFoundException("Provedor não encontrado: " + id));

        // Validar se nome já existe em outro provedor
        if (repositoryPort.existsByNameAndIdNot(command.getName(), id)) {
            throw new AlreadyExistsException("Provedor com nome '" + command.getName() + "' já existe");
        }

        // Atualizar campos
        existingProvider.setName(command.getName());
        existingProvider.setProviderType(command.getProviderType());
        existingProvider.setCommunicationType(command.getCommunicationType());
        existingProvider.setIsActive(command.getIsActive());
        existingProvider.setIsDefault(command.getIsDefault());
        existingProvider.updatePriority(command.getPriority());
        existingProvider.setUrl(command.getUrl());
        existingProvider.setConfiguration(command.getConfiguration());
        existingProvider.setMaxRetries(command.getMaxRetries());
        existingProvider.setTimeoutSeconds(command.getTimeoutSeconds());
        existingProvider.setRateLimitPerMinute(command.getRateLimitPerMinute());
        existingProvider.setDailyLimit(command.getDailyLimit());
        existingProvider.setMonthlyLimit(command.getMonthlyLimit());

        // Salvar
        Provider savedProvider = repositoryPort.save(existingProvider);

        metricsPort.incrementCounter("provider_updated_total",
            Map.of("entity_id", savedProvider.getId().toString()));

        log.info("Provedor atualizado com sucesso: {}", savedProvider.getId());
        return mapper.toView(savedProvider);
    }

    @LogOperation(
        operation = "DELETE_PROVIDER",
        description = "Removendo provedor: {id}",
        audit = true,
        auditAction = "DELETE",
        auditEntityType = "PROVIDER"
    )
    public void delete(UUID id) {
        log.info("Removendo provedor: {}", id);

        if (!repositoryPort.findById(id).isPresent()) {
            throw new NotFoundException("Provedor não encontrado: " + id);
        }

        repositoryPort.deleteById(id);
        
        metricsPort.incrementCounter("provider_deleted_total",
            Map.of("entity_id", id.toString()));

        log.info("Provedor removido com sucesso: {}", id);
    }

    @LogOperation(
        operation = "ACTIVATE_PROVIDER",
        description = "Ativando provedor: {id}",
        audit = true,
        auditAction = "ACTIVATE",
        auditEntityType = "PROVIDER"
    )
    public ProviderView activate(UUID id) {
        log.info("Ativando provedor: {}", id);

        Provider provider = repositoryPort.findById(id)
                .orElseThrow(() -> new NotFoundException("Provedor não encontrado: " + id));

        provider.activate();
        Provider savedProvider = repositoryPort.save(provider);

        log.info("Provedor ativado com sucesso: {}", id);
        return mapper.toView(savedProvider);
    }

    @LogOperation(
        operation = "DEACTIVATE_PROVIDER",
        description = "Desativando provedor: {id}",
        audit = true,
        auditAction = "DEACTIVATE",
        auditEntityType = "PROVIDER"
    )
    public ProviderView deactivate(UUID id) {
        log.info("Desativando provedor: {}", id);

        Provider provider = repositoryPort.findById(id)
                .orElseThrow(() -> new NotFoundException("Provedor não encontrado: " + id));

        provider.deactivate();
        Provider savedProvider = repositoryPort.save(provider);

        log.info("Provedor desativado com sucesso: {}", id);
        return mapper.toView(savedProvider);
    }

    @LogOperation(
        operation = "SET_PROVIDER_AS_DEFAULT",
        description = "Definindo provedor como padrão: {id}",
        audit = true,
        auditAction = "UPDATE",
        auditEntityType = "PROVIDER"
    )
    public ProviderView setAsDefault(UUID id) {
        log.info("Definindo provedor como padrão: {}", id);

        Provider provider = repositoryPort.findById(id)
                .orElseThrow(() -> new NotFoundException("Provedor não encontrado: " + id));

        provider.setAsDefault();
        Provider savedProvider = repositoryPort.save(provider);

        log.info("Provedor definido como padrão com sucesso: {}", id);
        return mapper.toView(savedProvider);
    }
}
