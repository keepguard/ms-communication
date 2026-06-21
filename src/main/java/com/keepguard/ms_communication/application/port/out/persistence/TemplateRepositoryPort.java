package com.keepguard.ms_communication.application.port.out.persistence;

import com.keepguard.ms_communication.application.dto.common.PageResultView;
import com.keepguard.ms_communication.application.dto.template.TemplateSearchCriteriaView;
import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.ms_communication.domain.entity.Template;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TemplateRepositoryPort {

    Template save(Template template);

    Optional<Template> findById(UUID id);

    PageResultView<Template> search(TemplateSearchCriteriaView criteria);

    List<Template> findAllActive();

    List<Template> findByType(TemplateTypeEnum type);

    List<Template> findByMessageType(MessageTypeEnum messageType);

    void deleteById(UUID id);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);

    Optional<Template> findByTemplateTypeAndMessageTypeAndApplicationAndIsActive(
        TemplateTypeEnum templateType, MessageTypeEnum messageType, String application, Boolean isActive);
}

