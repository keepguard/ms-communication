package com.keepguard.ms_communication.application.port.in.service;

import com.keepguard.ms_communication.application.dto.common.PageResultView;
import com.keepguard.ms_communication.application.dto.template.TemplateSearchCriteriaView;
import com.keepguard.ms_communication.application.dto.template.TemplateView;
import com.keepguard.ms_communication.domain.dto.template.TemplateCreateCommandDTO;
import com.keepguard.ms_communication.domain.dto.template.TemplateUpdateCommandDTO;
import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TemplatePort {

    TemplateView create(TemplateCreateCommandDTO command);

    TemplateView update(TemplateUpdateCommandDTO command);

    Optional<TemplateView> getById(UUID id);

    PageResultView<TemplateView> search(TemplateSearchCriteriaView criteria);

    List<TemplateView> getAllActive();

    List<TemplateView> getByType(TemplateTypeEnum type);

    List<TemplateView> getByMessageType(MessageTypeEnum messageType);

    void delete(UUID id);

    TemplateView activate(UUID id);

    TemplateView deactivate(UUID id);
}
