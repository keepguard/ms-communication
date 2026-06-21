package com.keepguard.ms_communication.application.service.template;

import com.keepguard.ms_communication.application.dto.common.PageResultView;
import com.keepguard.ms_communication.application.dto.template.TemplateSearchCriteriaView;
import com.keepguard.ms_communication.application.dto.template.TemplateView;
import com.keepguard.ms_communication.application.port.in.service.TemplatePort;
import com.keepguard.ms_communication.domain.dto.template.TemplateCreateCommandDTO;
import com.keepguard.ms_communication.domain.dto.template.TemplateUpdateCommandDTO;
import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateUseCaseService implements TemplatePort {

    private final TemplateCommandService commandService;
    private final TemplateQueryService queryService;

    @Override
    public TemplateView create(TemplateCreateCommandDTO command) {
        return commandService.create(command);
    }

    @Override
    public TemplateView update(TemplateUpdateCommandDTO command) {
        return commandService.update(command.getId(), command);
    }

    @Override
    public Optional<TemplateView> getById(UUID id) {
        try {
            TemplateView view = queryService.getById(id);
            return Optional.of(view);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public PageResultView<TemplateView> search(TemplateSearchCriteriaView criteria) {
        PageResultView<TemplateView> result = queryService.search(criteria);
        return result;
    }

    @Override
    public List<TemplateView> getAllActive() {
        return queryService.getAllActive();
    }

    @Override
    public List<TemplateView> getByType(TemplateTypeEnum type) {
        return queryService.getByType(type);
    }

    @Override
    public List<TemplateView> getByMessageType(MessageTypeEnum messageType) {
        return queryService.getByMessageType(messageType);
    }

    @Override
    public void delete(UUID id) {
        commandService.delete(id);
    }

    @Override
    public TemplateView activate(UUID id) {
        commandService.activate(id);
        return queryService.getById(id);
    }

    @Override
    public TemplateView deactivate(UUID id) {
        commandService.deactivate(id);
        return queryService.getById(id);
    }
}
