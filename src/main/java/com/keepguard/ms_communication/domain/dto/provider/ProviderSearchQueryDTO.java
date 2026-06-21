package com.keepguard.ms_communication.domain.dto.provider;

import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.domain.enums.ProviderTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderSearchQueryDTO {
    
    private UUID id;
    private String name;
    private ProviderTypeEnum providerType;
    private CommunicationTypeEnum communicationType;
    private Boolean isActive;
    private Boolean isDefault;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
}

