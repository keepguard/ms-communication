package com.keepguard.ms_communication.application.port.in.service;

import com.keepguard.ms_communication.application.dto.common.PageResultView;
import com.keepguard.ms_communication.application.dto.provider.ProviderSearchCriteriaView;
import com.keepguard.ms_communication.application.dto.provider.ProviderView;
import com.keepguard.ms_communication.domain.dto.provider.ProviderCreateCommandDTO;
import com.keepguard.ms_communication.domain.dto.provider.ProviderUpdateCommandDTO;
import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProviderPort {

    ProviderView create(ProviderCreateCommandDTO command);

    ProviderView update(ProviderUpdateCommandDTO command);

    Optional<ProviderView> getById(UUID id);

    PageResultView<ProviderView> search(ProviderSearchCriteriaView criteria);

    List<ProviderView> getAllActive();

    List<ProviderView> getByCommunicationType(CommunicationTypeEnum communicationType);

    Optional<ProviderView> getDefaultByCommunicationType(CommunicationTypeEnum communicationType);

    void delete(UUID id);

    ProviderView activate(UUID id);

    ProviderView deactivate(UUID id);

    ProviderView setAsDefault(UUID id);
}
