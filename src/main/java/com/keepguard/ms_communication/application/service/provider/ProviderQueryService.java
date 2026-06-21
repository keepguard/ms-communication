package com.keepguard.ms_communication.application.service.provider;

import com.keepguard.ms_communication.application.dto.common.PageResultView;
import com.keepguard.ms_communication.application.dto.provider.ProviderSearchCriteriaView;
import com.keepguard.ms_communication.application.dto.provider.ProviderView;
import com.keepguard.ms_communication.application.service.exception.NotFoundException;
import com.keepguard.ms_communication.application.port.out.persistence.ProviderRepositoryPort;
import com.keepguard.ms_communication.application.mapper.ProviderApplicationMapper;
import com.keepguard.ms_communication.domain.entity.Provider;
import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProviderQueryService  {

    private final ProviderRepositoryPort repositoryPort;
    private final ProviderApplicationMapper mapper;

    public ProviderView getById(UUID id) {
        log.debug("Buscando provedor por ID: {}", id);

        return repositoryPort.findById(id)
                .map(mapper::toView)
                .orElseThrow(() -> new NotFoundException("Provedor não encontrado: " + id));
    }

    public PageResultView<ProviderView> search(ProviderSearchCriteriaView criteria) {
        log.debug("Buscando provedores com critérios: {}", criteria);

        PageResultView<Provider> domainResult =
                repositoryPort.search(criteria);

        List<ProviderView> content = domainResult.content().stream()
                .map(mapper::toView)
                .collect(Collectors.toList());

        return PageResultView.of(content, domainResult.page(), domainResult.size(), domainResult.totalElements());
    }

    public List<ProviderView> getAllActive() {
        log.debug("Listando todos os provedores ativos");

        return repositoryPort.findAllActive().stream()
                .map(mapper::toView)
                .collect(Collectors.toList());
    }

    public List<ProviderView> getByCommunicationType(CommunicationTypeEnum communicationType) {
        log.debug("Listando provedores por tipo de comunicação: {}", communicationType);

        return repositoryPort.findByCommunicationType(communicationType).stream()
                .map(mapper::toView)
                .collect(Collectors.toList());
    }

    public Optional<ProviderView> getDefaultByCommunicationType(CommunicationTypeEnum communicationType) {
        log.debug("Buscando provedor padrão para tipo de comunicação: {}", communicationType);

        return repositoryPort.findDefaultByCommunicationType(communicationType)
                .map(mapper::toView);
    }

    public boolean existsById(UUID id) {
        return repositoryPort.findById(id).isPresent();
    }

    public boolean existsByName(String name) {
        return repositoryPort.existsByName(name);
    }
}
