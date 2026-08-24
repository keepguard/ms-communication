package com.keepguard.ms_communication.infrastructure.provider.strategy;

import com.keepguard.ms_communication.infrastructure.provider.CommunicationProvider;
import com.keepguard.ms_communication.domain.entity.Provider;
import com.keepguard.ms_communication.domain.enums.ProviderTypeEnum;

import java.util.List;

public sealed interface ProviderStrategy {

    record N8NStrategy() implements ProviderStrategy {
        @Override
        public ProviderTypeEnum getProviderType() {
            return ProviderTypeEnum.N8N;
        }

        @Override
        public CommunicationProvider getCommunicationProvider(List<CommunicationProvider> providers) {
            return providers.stream()
                    .filter(cp -> cp.supports(Provider.builder().providerType(ProviderTypeEnum.N8N).build()))
                    .findFirst()
                    .orElse(null);
        }
    }

    record SendGridStrategy() implements ProviderStrategy {
        @Override
        public ProviderTypeEnum getProviderType() {
            return ProviderTypeEnum.SENDGRID;
        }

        @Override
        public CommunicationProvider getCommunicationProvider(List<CommunicationProvider> providers) {
            return providers.stream()
                    .filter(cp -> cp.supports(Provider.builder().providerType(ProviderTypeEnum.SENDGRID).build()))
                    .findFirst()
                    .orElse(null);
        }
    }

    record EmailGoogleSenderStrategy() implements ProviderStrategy {
        @Override
        public ProviderTypeEnum getProviderType() {
            return ProviderTypeEnum.EMAIL_GOOGLE_SENDER;
        }

        @Override
        public CommunicationProvider getCommunicationProvider(List<CommunicationProvider> providers) {
            return providers.stream()
                    .filter(cp -> cp.supports(Provider.builder().providerType(ProviderTypeEnum.EMAIL_GOOGLE_SENDER).build()))
                    .findFirst()
                    .orElse(null);
        }
    }

    record SmsSenderStrategy() implements ProviderStrategy {
        @Override
        public ProviderTypeEnum getProviderType() {
            return ProviderTypeEnum.SRV_SMS_SENDER;
        }

        @Override
        public CommunicationProvider getCommunicationProvider(List<CommunicationProvider> providers) {
            return providers.stream()
                    .filter(cp -> cp.supports(Provider.builder().providerType(ProviderTypeEnum.SRV_SMS_SENDER).build()))
                    .findFirst()
                    .orElse(null);
        }
    }

    ProviderTypeEnum getProviderType();

    default boolean supports(Provider provider) {
        return getProviderType().equals(provider.getProviderType());
    }

    CommunicationProvider getCommunicationProvider(List<CommunicationProvider> providers);

    static ProviderStrategy fromProviderType(ProviderTypeEnum providerType) {
        return switch (providerType) {
            case N8N -> new N8NStrategy();
            case SENDGRID -> new SendGridStrategy();
            case EMAIL_GOOGLE_SENDER -> new EmailGoogleSenderStrategy();
            case SRV_SMS_SENDER -> new SmsSenderStrategy();
        };
    }
}