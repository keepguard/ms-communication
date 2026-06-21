package com.keepguard.ms_communication.application.port.out.cache;

import com.keepguard.ms_communication.application.dto.provider.ProviderCacheView;
import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;

import java.util.List;

public interface ProviderCachePort {

    // By Provider ID
    void cacheProviderById(String providerId, ProviderCacheView provider);
    ProviderCacheView getProviderByIdFromCache(String providerId);
    void removeProviderFromCacheById(String providerId);

    // Providers By Communication Type
    void cacheProvidersByType(CommunicationTypeEnum communicationType, List<ProviderCacheView> providers);
    List<ProviderCacheView> getProvidersByTypeFromCache(CommunicationTypeEnum communicationType);
    void removeProvidersByTypeFromCache(CommunicationTypeEnum communicationType);

    // Clear All
    void clearAllProviderCache();

}
