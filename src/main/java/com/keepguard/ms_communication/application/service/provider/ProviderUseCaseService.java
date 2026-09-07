package com.keepguard.ms_communication.application.service.provider;

import com.keepguard.ms_communication.application.dto.common.PageResultViewDTO;
import com.keepguard.ms_communication.application.dto.provider.ProviderSearchCriteriaViewDTO;
import com.keepguard.ms_communication.application.dto.provider.ProviderViewDTO;
import com.keepguard.ms_communication.application.port.in.ProviderPort;
import com.keepguard.ms_communication.application.dto.provider.ProviderCreateCommandDTO;
import com.keepguard.ms_communication.application.dto.provider.ProviderUpdateCommandDTO;
import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProviderUseCaseService implements ProviderPort {

    private final ProviderCommandService commandService;
    private final ProviderQueryService queryService;

    @Override
    public ProviderViewDTO create(ProviderCreateCommandDTO command) {
        return commandService.create(command);
    }

    @Override
    public ProviderViewDTO update(ProviderUpdateCommandDTO command) {
        return commandService.update(command.getId(), command);
    }

    @Override
    public Optional<ProviderViewDTO> getById(UUID id) {
        try {
            ProviderViewDTO view = queryService.getById(id);
            return Optional.of(view);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public PageResultViewDTO<ProviderViewDTO> search(ProviderSearchCriteriaViewDTO criteria) {
        PageResultViewDTO<ProviderViewDTO> result = queryService.search(criteria);
        return result;
    }

    @Override
    public List<ProviderViewDTO> getAllActive() {
        return queryService.getAllActive();
    }

    @Override
    public List<ProviderViewDTO> getByCommunicationType(CommunicationTypeEnum communicationType) {
        return queryService.getByCommunicationType(communicationType);
    }

    @Override
    public Optional<ProviderViewDTO> getDefaultByCommunicationType(CommunicationTypeEnum communicationType) {
        try {
            ProviderViewDTO view = queryService.getDefaultByCommunicationType(communicationType).orElse(null);
            return Optional.of(view);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void delete(UUID id) {
        commandService.delete(id);
    }

    @Override
    public ProviderViewDTO activate(UUID id) {
        return commandService.activate(id);
    }

    @Override
    public ProviderViewDTO deactivate(UUID id) {
        return commandService.deactivate(id);
    }

    @Override
    public ProviderViewDTO setAsDefault(UUID id) {
        return commandService.setAsDefault(id);
    }
}
