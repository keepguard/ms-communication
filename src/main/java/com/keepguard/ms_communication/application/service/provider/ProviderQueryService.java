package com.keepguard.ms_communication.application.service.provider;

import com.keepguard.ms_communication.application.dto.common.PageResultViewDTO;
import com.keepguard.ms_communication.application.dto.provider.ProviderSearchCriteriaViewDTO;
import com.keepguard.ms_communication.application.dto.provider.ProviderViewDTO;
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

    public ProviderViewDTO getById(UUID id) {
        log.debug("Buscando provedor por ID: {}", id);

        return repositoryPort.findById(id)
                .map(mapper::toView)
                .orElseThrow(() -> new NotFoundException("Provedor não encontrado: " + id));
    }

    public PageResultViewDTO<ProviderViewDTO> search(ProviderSearchCriteriaViewDTO criteria) {
        log.debug("Buscando provedores com critérios: {}", criteria);

        PageResultViewDTO<Provider> domainResult =
                repositoryPort.search(criteria);

        List<ProviderViewDTO> content = domainResult.content().stream()
                .map(mapper::toView)
                .collect(Collectors.toList());

        return PageResultViewDTO.of(content, domainResult.page(), domainResult.size(), domainResult.totalElements());
    }

    public List<ProviderViewDTO> getAllActive() {
        log.debug("Listando todos os provedores ativos");

        return repositoryPort.findAllActive().stream()
                .map(mapper::toView)
                .collect(Collectors.toList());
    }

    public List<ProviderViewDTO> getByCommunicationType(CommunicationTypeEnum communicationType) {
        log.debug("Listando provedores por tipo de comunicação: {}", communicationType);

        return repositoryPort.findByCommunicationType(communicationType).stream()
                .map(mapper::toView)
                .collect(Collectors.toList());
    }

    public Optional<ProviderViewDTO> getDefaultByCommunicationType(CommunicationTypeEnum communicationType) {
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
