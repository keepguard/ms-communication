package com.keepguard.ms_communication.application.dto.provider;

import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.domain.enums.ProviderTypeEnum;

public record ProviderSearchCriteriaView(
    int page,
    int size,
    String sortBy,
    String sortDirection,
    String name,
    ProviderTypeEnum providerType,
    CommunicationTypeEnum communicationType,
    Boolean isActive,
    Boolean isDefault
) {
    public static ProviderSearchCriteriaView of(int page, int size) {
        return new ProviderSearchCriteriaView(page, size, null, "ASC", null, null, null, null, null);
    }

    public static ProviderSearchCriteriaView of(int page, int size, String name) {
        return new ProviderSearchCriteriaView(page, size, null, "ASC", name, null, null, null, null);
    }
}
