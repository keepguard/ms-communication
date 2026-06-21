package com.keepguard.ms_communication.application.service.template;

import com.keepguard.ms_communication.application.dto.common.PageResultView;
import com.keepguard.ms_communication.application.dto.template.TemplateSearchCriteriaView;
import com.keepguard.ms_communication.application.dto.template.TemplateView;
import com.keepguard.ms_communication.application.service.exception.NotFoundException;
import com.keepguard.ms_communication.application.port.out.persistence.TemplateRepositoryPort;
import com.keepguard.ms_communication.application.mapper.TemplateApplicationMapper;
import com.keepguard.ms_communication.domain.entity.Template;
import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TemplateQueryService  {

    private final TemplateRepositoryPort repositoryPort;
    private final TemplateApplicationMapper mapper;

    public TemplateView getById(UUID id) {
        log.debug("Buscando template por ID: {}", id);

        return repositoryPort.findById(id)
                .map(mapper::toView)
                .orElseThrow(() -> new NotFoundException("Template não encontrado: " + id));
    }

    public PageResultView<TemplateView> search(TemplateSearchCriteriaView criteria) {
        log.debug("Buscando templates com critérios: {}", criteria);

        PageResultView<Template> domainResult =
                repositoryPort.search(criteria);

        List<TemplateView> content = domainResult.content().stream()
                .map(mapper::toView)
                .collect(Collectors.toList());

        return PageResultView.of(content, domainResult.page(), domainResult.size(), domainResult.totalElements());
    }

    public List<TemplateView> getAllActive() {
        log.debug("Listando todos os templates ativos");

        return repositoryPort.findAllActive().stream()
                .map(mapper::toView)
                .collect(Collectors.toList());
    }

    public List<TemplateView> getByType(TemplateTypeEnum type) {
        log.debug("Listando templates por tipo: {}", type);

        return repositoryPort.findByType(type).stream()
                .map(mapper::toView)
                .collect(Collectors.toList());
    }

    public List<TemplateView> getByMessageType(MessageTypeEnum messageType) {
        log.debug("Listando templates por tipo de mensagem: {}", messageType);

        return repositoryPort.findByMessageType(messageType).stream()
                .map(mapper::toView)
                .collect(Collectors.toList());
    }

    public boolean existsById(UUID id) {
        return repositoryPort.findById(id).isPresent();
    }

    public boolean existsByName(String name) {
        return repositoryPort.existsByName(name);
    }
}
