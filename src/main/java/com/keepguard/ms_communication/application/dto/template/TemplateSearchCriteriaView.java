package com.keepguard.ms_communication.application.dto.template;

import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;

public record TemplateSearchCriteriaView(
    int page,
    int size,
    String sortBy,
    String sortDirection,
    String name,
    MessageTypeEnum messageType,
    TemplateTypeEnum templateType,
    Boolean isActive
) {
    public static TemplateSearchCriteriaView of(int page, int size) {
        return new TemplateSearchCriteriaView(page, size, null, "ASC", null, null, null, null);
    }

    public static TemplateSearchCriteriaView of(int page, int size, String name) {
        return new TemplateSearchCriteriaView(page, size, null, "ASC", name, null, null, null);
    }
}
