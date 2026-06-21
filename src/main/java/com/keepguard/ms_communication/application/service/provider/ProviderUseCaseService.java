package com.keepguard.ms_communication.application.service.provider;

import com.keepguard.ms_communication.application.dto.common.PageResultView;
import com.keepguard.ms_communication.application.dto.provider.ProviderSearchCriteriaView;
import com.keepguard.ms_communication.application.dto.provider.ProviderView;
import com.keepguard.ms_communication.application.port.in.service.ProviderPort;
import com.keepguard.ms_communication.domain.dto.provider.ProviderCreateCommandDTO;
import com.keepguard.ms_communication.domain.dto.provider.ProviderUpdateCommandDTO;
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
    public ProviderView create(ProviderCreateCommandDTO command) {
        return commandService.create(command);
    }

    @Override
    public ProviderView update(ProviderUpdateCommandDTO command) {
        return commandService.update(command.getId(), command);
    }

    @Override
    public Optional<ProviderView> getById(UUID id) {
        try {
            ProviderView view = queryService.getById(id);
            return Optional.of(view);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public PageResultView<ProviderView> search(ProviderSearchCriteriaView criteria) {
        PageResultView<ProviderView> result = queryService.search(criteria);
        return result;
    }

    @Override
    public List<ProviderView> getAllActive() {
        return queryService.getAllActive();
    }

    @Override
    public List<ProviderView> getByCommunicationType(CommunicationTypeEnum communicationType) {
        return queryService.getByCommunicationType(communicationType);
    }

    @Override
    public Optional<ProviderView> getDefaultByCommunicationType(CommunicationTypeEnum communicationType) {
        try {
            ProviderView view = queryService.getDefaultByCommunicationType(communicationType).orElse(null);
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
    public ProviderView activate(UUID id) {
        return commandService.activate(id);
    }

    @Override
    public ProviderView deactivate(UUID id) {
        return commandService.deactivate(id);
    }

    @Override
    public ProviderView setAsDefault(UUID id) {
        return commandService.setAsDefault(id);
    }
}
