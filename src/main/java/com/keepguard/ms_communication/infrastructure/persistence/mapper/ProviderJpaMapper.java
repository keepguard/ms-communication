package com.keepguard.ms_communication.infrastructure.persistence.mapper;

import com.keepguard.ms_communication.domain.entity.Provider;
import com.keepguard.ms_communication.infrastructure.persistence.entity.ProviderJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class ProviderJpaMapper {

    public ProviderJpaEntity toEntity(Provider domain) {
        if (domain == null) {
            return null;
        }

        return ProviderJpaEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .providerType(domain.getProviderType())
                .communicationType(domain.getCommunicationType())
                .isActive(domain.getIsActive())
                .isDefault(domain.getIsDefault())
                .priority(domain.getPriority())
                .url(domain.getUrl())
                .configuration(domain.getConfiguration())
                .maxRetries(domain.getMaxRetries())
                .timeoutSeconds(domain.getTimeoutSeconds())
                .rateLimitPerMinute(domain.getRateLimitPerMinute())
                .dailyLimit(domain.getDailyLimit())
                .monthlyLimit(domain.getMonthlyLimit())
                .variables(domain.getVariables())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public Provider toDomain(ProviderJpaEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Provider(
                entity.getId(),
                entity.getName(),
                entity.getProviderType(),
                entity.getCommunicationType(),
                entity.getIsActive(),
                entity.getIsDefault(),
                entity.getPriority(),
                entity.getUrl(),
                entity.getConfiguration(),
                entity.getMaxRetries(),
                entity.getTimeoutSeconds(),
                entity.getRateLimitPerMinute(),
                entity.getDailyLimit(),
                entity.getMonthlyLimit(),
                entity.getVariables(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
