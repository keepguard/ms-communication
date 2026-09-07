package com.keepguard.ms_communication.application.port.out.cache;

import com.keepguard.ms_communication.application.dto.provider.ProviderCacheViewDTO;
import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;

import java.util.List;

public interface ProviderCachePort {

    // By Provider ID
    void cacheProviderById(String providerId, ProviderCacheViewDTO provider);
    ProviderCacheViewDTO getProviderByIdFromCache(String providerId);
    void removeProviderFromCacheById(String providerId);

    // Providers By Communication Type
    void cacheProvidersByType(CommunicationTypeEnum communicationType, List<ProviderCacheViewDTO> providers);
    List<ProviderCacheViewDTO> getProvidersByTypeFromCache(CommunicationTypeEnum communicationType);
    void removeProvidersByTypeFromCache(CommunicationTypeEnum communicationType);

    // Clear All
    void clearAllProviderCache();

}
