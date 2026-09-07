package com.keepguard.ms_communication.application.port.in;

import com.keepguard.ms_communication.application.dto.common.PageResultViewDTO;
import com.keepguard.ms_communication.application.dto.template.TemplateSearchCriteriaViewDTO;
import com.keepguard.ms_communication.application.dto.template.TemplateViewDTO;
import com.keepguard.ms_communication.application.dto.template.TemplateCreateCommandDTO;
import com.keepguard.ms_communication.application.dto.template.TemplateUpdateCommandDTO;
import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TemplatePort {

    TemplateViewDTO create(TemplateCreateCommandDTO command);

    TemplateViewDTO update(TemplateUpdateCommandDTO command);

    Optional<TemplateViewDTO> getById(UUID id);

    PageResultViewDTO<TemplateViewDTO> search(TemplateSearchCriteriaViewDTO criteria);

    List<TemplateViewDTO> getAllActive();

    List<TemplateViewDTO> getByType(TemplateTypeEnum type);

    List<TemplateViewDTO> getByMessageType(MessageTypeEnum messageType);

    void delete(UUID id);

    TemplateViewDTO activate(UUID id);

    TemplateViewDTO deactivate(UUID id);
}
