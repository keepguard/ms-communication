package com.keepguard.ms_communication.infrastructure.provider.strategy;

import com.keepguard.ms_communication.domain.entity.Provider;
import com.keepguard.ms_communication.domain.enums.ProviderTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class ProviderStrategyFactory {

    private final Map<ProviderTypeEnum, ProviderStrategy> strategies;

    public ProviderStrategyFactory() {
        this.strategies = Map.of(
                ProviderTypeEnum.N8N, new ProviderStrategy.N8NStrategy(),
                ProviderTypeEnum.SENDGRID, new ProviderStrategy.SendGridStrategy(),
                ProviderTypeEnum.EMAIL_GOOGLE_SENDER, new ProviderStrategy.EmailGoogleSenderStrategy()
        );
    }

    public Optional<ProviderStrategy> getStrategy(ProviderTypeEnum providerType) {
        return Optional.ofNullable(strategies.get(providerType));
    }

    public boolean hasStrategy(Provider provider) {
        return strategies.containsKey(provider.getProviderType());
    }

    public java.util.Set<ProviderTypeEnum> getAvailableProviderTypes() {
        return strategies.keySet();
    }

    public Optional<ProviderStrategy> getStrategyWithPatternMatching(Provider provider) {
        return switch (provider.getProviderType()) {
            case N8N -> Optional.of(new ProviderStrategy.N8NStrategy());
            case SENDGRID -> Optional.of(new ProviderStrategy.SendGridStrategy());
            case EMAIL_GOOGLE_SENDER -> Optional.of(new ProviderStrategy.EmailGoogleSenderStrategy());
        };
    }
}