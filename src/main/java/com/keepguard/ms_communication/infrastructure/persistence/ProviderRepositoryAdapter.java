package com.keepguard.ms_communication.infrastructure.persistence;

import com.keepguard.ms_communication.application.dto.common.PageResultView;
import com.keepguard.ms_communication.application.dto.provider.ProviderSearchCriteriaView;
import com.keepguard.ms_communication.application.port.out.persistence.ProviderRepositoryPort;
import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.domain.entity.Provider;
import com.keepguard.ms_communication.infrastructure.persistence.entity.ProviderJpaEntity;
import com.keepguard.ms_communication.infrastructure.persistence.mapper.ProviderJpaMapper;
import com.keepguard.ms_communication.infrastructure.persistence.spring.ProviderSpringRepository;
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
public class ProviderRepositoryAdapter implements ProviderRepositoryPort {

    private final ProviderSpringRepository springRepository;
    private final ProviderJpaMapper mapper;

    @Override
    public Provider save(Provider provider) {
        ProviderJpaEntity entity = mapper.toEntity(provider);
        ProviderJpaEntity savedEntity = springRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Provider> findById(UUID id) {
        return springRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public PageResultView<Provider> search(ProviderSearchCriteriaView criteria) {
        Pageable pageable = createPageable(criteria);

        Page<ProviderJpaEntity> page = springRepository.findWithFilters(
            criteria.name(),
            criteria.providerType() != null ? criteria.providerType().name() : null,
            criteria.communicationType() != null ? criteria.communicationType().name() : null,
            criteria.isActive(),
            criteria.isDefault(),
            pageable
        );

        List<Provider> content = page.getContent().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());

        return PageResultView.of(content, page.getNumber(), page.getSize(), page.getTotalElements());
    }

    @Override
    public List<Provider> findAllActive() {
        return springRepository.findAllActiveProviders().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Provider> findByCommunicationType(CommunicationTypeEnum communicationType) {
        return springRepository.findActiveProvidersByCommunicationType(communicationType).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Provider> findDefaultByCommunicationType(CommunicationTypeEnum communicationType) {
        return springRepository.findDefaultProviderByCommunicationType(communicationType)
                .map(mapper::toDomain);
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

    private Pageable createPageable(ProviderSearchCriteriaView criteria) {
        Sort sort = Sort.by(Sort.Direction.fromString(criteria.sortDirection()), criteria.sortBy());
        return PageRequest.of(criteria.page(), criteria.size(), sort);
    }
}
