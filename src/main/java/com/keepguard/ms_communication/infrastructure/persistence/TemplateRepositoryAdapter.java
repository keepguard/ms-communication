package com.keepguard.ms_communication.infrastructure.persistence;

import com.keepguard.ms_communication.application.dto.common.PageResultView;
import com.keepguard.ms_communication.application.dto.template.TemplateSearchCriteriaView;
import com.keepguard.ms_communication.application.port.out.persistence.TemplateRepositoryPort;
import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.ms_communication.domain.entity.Template;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import com.keepguard.ms_communication.infrastructure.persistence.entity.TemplateJpaEntity;
import com.keepguard.ms_communication.infrastructure.persistence.mapper.TemplateJpaMapper;
import com.keepguard.ms_communication.infrastructure.persistence.spring.TemplateSpringRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.retry.annotation.Retry;

@Component
@RequiredArgsConstructor
@Retry(name = "databaseOperation")
@Bulkhead(name = "databaseOperation")
public class TemplateRepositoryAdapter implements TemplateRepositoryPort {

    private final TemplateSpringRepository springRepository;
    private final TemplateJpaMapper mapper;

    @Override
    public Template save(Template template) {
        TemplateJpaEntity entity = mapper.toEntity(template);
        TemplateJpaEntity savedEntity = springRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Template> findById(UUID id) {
        return springRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public PageResultView<Template> search(TemplateSearchCriteriaView criteria) {
        Pageable pageable = createPageable(criteria);

        Page<TemplateJpaEntity> page = springRepository.findWithFilters(
            criteria.name(),
            criteria.messageType() != null ? criteria.messageType().name() : null,
            criteria.templateType() != null ? criteria.templateType().name() : null,
            criteria.isActive(),
            pageable
        );

        List<Template> content = page.getContent().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());

        return PageResultView.of(content, page.getNumber(), page.getSize(), page.getTotalElements());
    }

    @Override
    public List<Template> findAllActive() {
        return springRepository.findAllActiveTemplates().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Template> findByType(TemplateTypeEnum type) {
        return springRepository.findActiveTemplatesByType(type).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Template> findByMessageType(MessageTypeEnum messageType) {
        return springRepository.findActiveTemplatesByMessageType(messageType).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        springRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return springRepository.existsByName(name);
    }

    @Override
    public boolean existsByNameAndIdNot(String name, UUID id) {
        return springRepository.existsByNameAndIdNot(name, id);
    }

    @Override
    public Optional<Template> findByTemplateTypeAndMessageTypeAndApplicationAndIsActive(
            TemplateTypeEnum templateType, MessageTypeEnum messageType, String application, Boolean isActive) {
        return springRepository.findTemplateByTypeMessageAndApp(
                templateType, messageType, application, isActive)
                .map(mapper::toDomain);
    }

    private Pageable createPageable(TemplateSearchCriteriaView criteria) {
        Sort sort = Sort.by(Sort.Direction.fromString(criteria.sortDirection()), criteria.sortBy());
        return PageRequest.of(criteria.page(), criteria.size(), sort);
    }
}
