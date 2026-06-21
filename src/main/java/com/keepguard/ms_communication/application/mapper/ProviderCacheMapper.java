package com.keepguard.ms_communication.application.mapper;

import com.keepguard.ms_communication.application.dto.provider.ProviderCacheView;
import com.keepguard.ms_communication.domain.entity.Provider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProviderCacheMapper {

    public ProviderCacheView toCacheView(Provider provider) {
        if (provider == null) {
            return null;
        }

        return new ProviderCacheView(
            provider.getId(),
            provider.getName(),
            provider.getProviderType(),
            provider.getCommunicationType(),
            provider.getIsActive(),
            provider.getIsDefault(),
            provider.getPriority(),
            provider.getUrl(),
            provider.getConfiguration(),
            provider.getMaxRetries(),
            provider.getTimeoutSeconds(),
            provider.getRateLimitPerMinute(),
            provider.getDailyLimit(),
            provider.getMonthlyLimit(),
            provider.getCreatedAt(),
            provider.getUpdatedAt()
        );
    }

    public Provider toEntity(ProviderCacheView dto) {
        if (dto == null) {
            return null;
        }

        return new Provider(
            dto.id(),
            dto.name(),
            dto.providerType(),
            dto.communicationType(),
            dto.isActive(),
            dto.isDefault(),
            dto.priority(),
            dto.url(),
            dto.configuration(),
            dto.maxRetries(),
            dto.timeoutSeconds(),
            dto.rateLimitPerMinute(),
            dto.dailyLimit(),
            dto.monthlyLimit(),
            null, // variables - ProviderCacheView não tem este campo
            dto.createdAt(),
            dto.updatedAt()
        );
    }

    public List<ProviderCacheView> toCacheViewList(List<Provider> providers) {
        if (providers == null) {
            return null;
        }
        return providers.stream()
            .map(this::toCacheView)
            .collect(Collectors.toList());
    }

    public List<Provider> toEntityList(List<ProviderCacheView> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
            .map(this::toEntity)
            .collect(Collectors.toList());
    }

}
