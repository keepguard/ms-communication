package com.keepguard.ms_communication.adapters.in.rest.provider.mapper;

import com.keepguard.ms_communication.adapters.in.rest.provider.dto.request.ProviderCreateRequestDTO;
import com.keepguard.ms_communication.adapters.in.rest.provider.dto.request.ProviderUpdateRequestDTO;
import com.keepguard.ms_communication.adapters.in.rest.provider.dto.response.*;
import com.keepguard.ms_communication.application.dto.provider.ProviderView;
import com.keepguard.ms_communication.domain.dto.provider.ProviderCreateCommandDTO;
import com.keepguard.ms_communication.domain.dto.provider.ProviderUpdateCommandDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class ProviderAdapterMapper {

    public ProviderCreateCommandDTO toCreateCommand(ProviderCreateRequestDTO dto, UUID tenantId) {
        if (dto == null) {
            return null;
        }

        try {
            return ProviderCreateCommandDTO.builder()
                    .tenantId(tenantId)
                    .name(dto.getName())
                    .providerType(dto.getProviderType())
                    .communicationType(dto.getCommunicationType())
                    .isActive(dto.getIsActive())
                    .isDefault(dto.getIsDefault())
                    .priority(dto.getPriority())
                    .url(dto.getUrl())
                    .configuration(dto.getConfiguration())
                    .maxRetries(dto.getMaxRetries())
                    .timeoutSeconds(dto.getTimeoutSeconds())
                    .rateLimitPerMinute(dto.getRateLimitPerMinute())
                    .dailyLimit(dto.getDailyLimit())
                    .monthlyLimit(dto.getMonthlyLimit())
                    .build();
        } catch (Exception e) {
            log.error("Erro ao mapear ProviderCreateDTO para ProviderCreateCommandDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public ProviderUpdateCommandDTO toUpdateCommand(UUID id, ProviderUpdateRequestDTO dto, UUID tenantId) {
        if (dto == null) {
            return null;
        }

        try {
            return ProviderUpdateCommandDTO.builder()
                    .id(id)
                    .tenantId(tenantId)
                    .name(dto.getName())
                    .providerType(dto.getProviderType())
                    .communicationType(dto.getCommunicationType())
                    .isActive(dto.getIsActive())
                    .isDefault(dto.getIsDefault())
                    .priority(dto.getPriority())
                    .url(dto.getUrl())
                    .configuration(dto.getConfiguration())
                    .maxRetries(dto.getMaxRetries())
                    .timeoutSeconds(dto.getTimeoutSeconds())
                    .rateLimitPerMinute(dto.getRateLimitPerMinute())
                    .dailyLimit(dto.getDailyLimit())
                    .monthlyLimit(dto.getMonthlyLimit())
                    .build();
        } catch (Exception e) {
            log.error("Erro ao mapear ProviderUpdateDTO para ProviderUpdateCommandDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public ProviderCreateResponseDTO toCreateResponseDTO(ProviderView view) {
        if (view == null) {
            return null;
        }

        try {
            return ProviderCreateResponseDTO.builder()
                    .id(view.id())
                    .name(view.name())
                    .providerType(view.providerType())
                    .communicationType(view.communicationType())
                    .isActive(view.isActive())
                    .isDefault(view.isDefault())
                    .priority(view.priority())
                    .url(view.url())
                    .configuration(view.configuration())
                    .maxRetries(view.maxRetries())
                    .timeoutSeconds(view.timeoutSeconds())
                    .rateLimitPerMinute(view.rateLimitPerMinute())
                    .dailyLimit(view.dailyLimit())
                    .monthlyLimit(view.monthlyLimit())
                    .createdAt(view.createdAt())
                    .updatedAt(view.updatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Erro ao mapear ProviderView para ProviderCreateResponseDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public ProviderUpdateResponseDTO toUpdateResponseDTO(ProviderView view) {
        if (view == null) {
            return null;
        }

        try {
            return ProviderUpdateResponseDTO.builder()
                    .id(view.id())
                    .name(view.name())
                    .providerType(view.providerType())
                    .communicationType(view.communicationType())
                    .isActive(view.isActive())
                    .isDefault(view.isDefault())
                    .priority(view.priority())
                    .url(view.url())
                    .configuration(view.configuration())
                    .maxRetries(view.maxRetries())
                    .timeoutSeconds(view.timeoutSeconds())
                    .rateLimitPerMinute(view.rateLimitPerMinute())
                    .dailyLimit(view.dailyLimit())
                    .monthlyLimit(view.monthlyLimit())
                    .createdAt(view.createdAt())
                    .updatedAt(view.updatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Erro ao mapear ProviderView para ProviderUpdateResponseDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public ProviderGetProviderByIdResponseDTO toGetProviderByIdResponseDTO(ProviderView view) {
        if (view == null) {
            return null;
        }

        try {
            return ProviderGetProviderByIdResponseDTO.builder()
                    .id(view.id())
                    .name(view.name())
                    .providerType(view.providerType())
                    .communicationType(view.communicationType())
                    .isActive(view.isActive())
                    .isDefault(view.isDefault())
                    .priority(view.priority())
                    .url(view.url())
                    .configuration(view.configuration())
                    .maxRetries(view.maxRetries())
                    .timeoutSeconds(view.timeoutSeconds())
                    .rateLimitPerMinute(view.rateLimitPerMinute())
                    .dailyLimit(view.dailyLimit())
                    .monthlyLimit(view.monthlyLimit())
                    .createdAt(view.createdAt())
                    .updatedAt(view.updatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Erro ao mapear ProviderView para ProviderGetProviderByIdResponseDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public ProviderGetAllProvidersResponseDTO toGetAllProvidersResponseDTO(ProviderView view) {
        if (view == null) {
            return null;
        }

        try {
            return ProviderGetAllProvidersResponseDTO.builder()
                    .id(view.id())
                    .name(view.name())
                    .providerType(view.providerType())
                    .communicationType(view.communicationType())
                    .isActive(view.isActive())
                    .isDefault(view.isDefault())
                    .priority(view.priority())
                    .url(view.url())
                    .configuration(view.configuration())
                    .maxRetries(view.maxRetries())
                    .timeoutSeconds(view.timeoutSeconds())
                    .rateLimitPerMinute(view.rateLimitPerMinute())
                    .dailyLimit(view.dailyLimit())
                    .monthlyLimit(view.monthlyLimit())
                    .createdAt(view.createdAt())
                    .updatedAt(view.updatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Erro ao mapear ProviderView para ProviderGetAllProvidersResponseDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public ProviderGetActiveProvidersResponseDTO toGetActiveProvidersResponseDTO(ProviderView view) {
        if (view == null) {
            return null;
        }

        try {
            return ProviderGetActiveProvidersResponseDTO.builder()
                    .id(view.id())
                    .name(view.name())
                    .providerType(view.providerType())
                    .communicationType(view.communicationType())
                    .isActive(view.isActive())
                    .isDefault(view.isDefault())
                    .priority(view.priority())
                    .url(view.url())
                    .configuration(view.configuration())
                    .maxRetries(view.maxRetries())
                    .timeoutSeconds(view.timeoutSeconds())
                    .rateLimitPerMinute(view.rateLimitPerMinute())
                    .dailyLimit(view.dailyLimit())
                    .monthlyLimit(view.monthlyLimit())
                    .createdAt(view.createdAt())
                    .updatedAt(view.updatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Erro ao mapear ProviderView para ProviderGetActiveProvidersResponseDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public ProviderGetProvidersByCommunicationTypeResponseDTO toGetProvidersByCommunicationTypeResponseDTO(ProviderView view) {
        if (view == null) {
            return null;
        }

        try {
            return ProviderGetProvidersByCommunicationTypeResponseDTO.builder()
                    .id(view.id())
                    .name(view.name())
                    .providerType(view.providerType())
                    .communicationType(view.communicationType())
                    .isActive(view.isActive())
                    .isDefault(view.isDefault())
                    .priority(view.priority())
                    .url(view.url())
                    .configuration(view.configuration())
                    .maxRetries(view.maxRetries())
                    .timeoutSeconds(view.timeoutSeconds())
                    .rateLimitPerMinute(view.rateLimitPerMinute())
                    .dailyLimit(view.dailyLimit())
                    .monthlyLimit(view.monthlyLimit())
                    .createdAt(view.createdAt())
                    .updatedAt(view.updatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Erro ao mapear ProviderView para ProviderGetProvidersByCommunicationTypeResponseDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public ProviderGetDefaultProviderResponseDTO toGetDefaultProviderResponseDTO(ProviderView view) {
        if (view == null) {
            return null;
        }

        try {
            return ProviderGetDefaultProviderResponseDTO.builder()
                    .id(view.id())
                    .name(view.name())
                    .providerType(view.providerType())
                    .communicationType(view.communicationType())
                    .isActive(view.isActive())
                    .isDefault(view.isDefault())
                    .priority(view.priority())
                    .url(view.url())
                    .configuration(view.configuration())
                    .maxRetries(view.maxRetries())
                    .timeoutSeconds(view.timeoutSeconds())
                    .rateLimitPerMinute(view.rateLimitPerMinute())
                    .dailyLimit(view.dailyLimit())
                    .monthlyLimit(view.monthlyLimit())
                    .createdAt(view.createdAt())
                    .updatedAt(view.updatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Erro ao mapear ProviderView para ProviderGetDefaultProviderResponseDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public ProviderActivateProviderResponseDTO toActivateProviderResponseDTO(ProviderView view) {
        if (view == null) {
            return null;
        }

        try {
            return ProviderActivateProviderResponseDTO.builder()
                    .id(view.id())
                    .name(view.name())
                    .providerType(view.providerType())
                    .communicationType(view.communicationType())
                    .isActive(view.isActive())
                    .isDefault(view.isDefault())
                    .priority(view.priority())
                    .url(view.url())
                    .configuration(view.configuration())
                    .maxRetries(view.maxRetries())
                    .timeoutSeconds(view.timeoutSeconds())
                    .rateLimitPerMinute(view.rateLimitPerMinute())
                    .dailyLimit(view.dailyLimit())
                    .monthlyLimit(view.monthlyLimit())
                    .createdAt(view.createdAt())
                    .updatedAt(view.updatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Erro ao mapear ProviderView para ProviderActivateProviderResponseDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public ProviderDeactivateProviderResponseDTO toDeactivateProviderResponseDTO(ProviderView view) {
        if (view == null) {
            return null;
        }

        try {
            return ProviderDeactivateProviderResponseDTO.builder()
                    .id(view.id())
                    .name(view.name())
                    .providerType(view.providerType())
                    .communicationType(view.communicationType())
                    .isActive(view.isActive())
                    .isDefault(view.isDefault())
                    .priority(view.priority())
                    .url(view.url())
                    .configuration(view.configuration())
                    .maxRetries(view.maxRetries())
                    .timeoutSeconds(view.timeoutSeconds())
                    .rateLimitPerMinute(view.rateLimitPerMinute())
                    .dailyLimit(view.dailyLimit())
                    .monthlyLimit(view.monthlyLimit())
                    .createdAt(view.createdAt())
                    .updatedAt(view.updatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Erro ao mapear ProviderView para ProviderDeactivateProviderResponseDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public ProviderSetAsDefaultResponseDTO toSetAsDefaultResponseDTO(ProviderView view) {
        if (view == null) {
            return null;
        }

        try {
            return ProviderSetAsDefaultResponseDTO.builder()
                    .id(view.id())
                    .name(view.name())
                    .providerType(view.providerType())
                    .communicationType(view.communicationType())
                    .isActive(view.isActive())
                    .isDefault(view.isDefault())
                    .priority(view.priority())
                    .url(view.url())
                    .configuration(view.configuration())
                    .maxRetries(view.maxRetries())
                    .timeoutSeconds(view.timeoutSeconds())
                    .rateLimitPerMinute(view.rateLimitPerMinute())
                    .dailyLimit(view.dailyLimit())
                    .monthlyLimit(view.monthlyLimit())
                    .createdAt(view.createdAt())
                    .updatedAt(view.updatedAt())
                    .build();
        } catch (Exception e) {
            log.error("Erro ao mapear ProviderView para ProviderSetAsDefaultResponseDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public ProviderTestProviderConnectionResponseDTO toTestProviderConnectionResponseDTO(UUID providerId, String providerName, Boolean isConnected, java.time.LocalDateTime testedAt) {
        try {
            return ProviderTestProviderConnectionResponseDTO.builder()
                    .providerId(providerId)
                    .providerName(providerName)
                    .isConnected(isConnected)
                    .testedAt(testedAt)
                    .build();
        } catch (Exception e) {
            log.error("Erro ao mapear dados para ProviderTestProviderConnectionResponseDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

}
