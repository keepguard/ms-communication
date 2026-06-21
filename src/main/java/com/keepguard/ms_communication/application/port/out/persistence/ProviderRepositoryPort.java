package com.keepguard.ms_communication.application.port.out.persistence;

import com.keepguard.ms_communication.application.dto.common.PageResultView;
import com.keepguard.ms_communication.application.dto.provider.ProviderSearchCriteriaView;
import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.domain.entity.Provider;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProviderRepositoryPort {

    Provider save(Provider provider);

    Optional<Provider> findById(UUID id);

    PageResultView<Provider> search(ProviderSearchCriteriaView criteria);

    List<Provider> findAllActive();

    List<Provider> findByCommunicationType(CommunicationTypeEnum communicationType);

    Optional<Provider> findDefaultByCommunicationType(CommunicationTypeEnum communicationType);

    void deleteById(UUID id);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);
}

