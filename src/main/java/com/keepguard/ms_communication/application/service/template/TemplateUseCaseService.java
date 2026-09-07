package com.keepguard.ms_communication.application.service.template;

import com.keepguard.ms_communication.application.dto.common.PageResultViewDTO;
import com.keepguard.ms_communication.application.dto.template.TemplateSearchCriteriaViewDTO;
import com.keepguard.ms_communication.application.dto.template.TemplateViewDTO;
import com.keepguard.ms_communication.application.port.in.TemplatePort;
import com.keepguard.ms_communication.application.dto.template.TemplateCreateCommandDTO;
import com.keepguard.ms_communication.application.dto.template.TemplateUpdateCommandDTO;
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
    public TemplateViewDTO create(TemplateCreateCommandDTO command) {
        return commandService.create(command);
    }

    @Override
    public TemplateViewDTO update(TemplateUpdateCommandDTO command) {
        return commandService.update(command.getId(), command);
    }

    @Override
    public Optional<TemplateViewDTO> getById(UUID id) {
        try {
            TemplateViewDTO view = queryService.getById(id);
            return Optional.of(view);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public PageResultViewDTO<TemplateViewDTO> search(TemplateSearchCriteriaViewDTO criteria) {
        PageResultViewDTO<TemplateViewDTO> result = queryService.search(criteria);
        return result;
    }

    @Override
    public List<TemplateViewDTO> getAllActive() {
        return queryService.getAllActive();
    }

    @Override
    public List<TemplateViewDTO> getByType(TemplateTypeEnum type) {
        return queryService.getByType(type);
    }

    @Override
    public List<TemplateViewDTO> getByMessageType(MessageTypeEnum messageType) {
        return queryService.getByMessageType(messageType);
    }

    @Override
    public void delete(UUID id) {
        commandService.delete(id);
    }

    @Override
    public TemplateViewDTO activate(UUID id) {
        commandService.activate(id);
        return queryService.getById(id);
    }

    @Override
    public TemplateViewDTO deactivate(UUID id) {
        commandService.deactivate(id);
        return queryService.getById(id);
    }
}
