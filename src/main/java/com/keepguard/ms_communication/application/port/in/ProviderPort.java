package com.keepguard.ms_communication.application.port.in;

import com.keepguard.ms_communication.application.dto.common.PageResultViewDTO;
import com.keepguard.ms_communication.application.dto.provider.ProviderSearchCriteriaViewDTO;
import com.keepguard.ms_communication.application.dto.provider.ProviderViewDTO;
import com.keepguard.ms_communication.application.dto.provider.ProviderCreateCommandDTO;
import com.keepguard.ms_communication.application.dto.provider.ProviderUpdateCommandDTO;
import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProviderPort {

    ProviderViewDTO create(ProviderCreateCommandDTO command);

    ProviderViewDTO update(ProviderUpdateCommandDTO command);

    Optional<ProviderViewDTO> getById(UUID id);

    PageResultViewDTO<ProviderViewDTO> search(ProviderSearchCriteriaViewDTO criteria);

    List<ProviderViewDTO> getAllActive();

    List<ProviderViewDTO> getByCommunicationType(CommunicationTypeEnum communicationType);

    Optional<ProviderViewDTO> getDefaultByCommunicationType(CommunicationTypeEnum communicationType);

    void delete(UUID id);

    ProviderViewDTO activate(UUID id);

    ProviderViewDTO deactivate(UUID id);

    ProviderViewDTO setAsDefault(UUID id);
}
