package com.keepguard.ms_communication.adapters.in.rest.provider.mapper;

import com.keepguard.ms_communication.adapters.in.rest.provider.dto.request.ProviderCreateRequestDTO;
import com.keepguard.ms_communication.adapters.in.rest.provider.dto.request.ProviderUpdateRequestDTO;
import com.keepguard.ms_communication.adapters.in.rest.provider.dto.response.*;
import com.keepguard.ms_communication.application.dto.provider.ProviderViewDTO;
import com.keepguard.ms_communication.application.dto.provider.ProviderCreateCommandDTO;
import com.keepguard.ms_communication.application.dto.provider.ProviderUpdateCommandDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class ProviderAdapterMapper {

    public ProviderCreateCommandDTO toCreateCommand(ProviderCreateRequestDTO dto, UUID companyId) {
        if (dto == null) {
            return null;
        }

        try {
            return ProviderCreateCommandDTO.builder()
                    .companyId(companyId)
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

    public ProviderUpdateCommandDTO toUpdateCommand(UUID id, ProviderUpdateRequestDTO dto, UUID companyId) {
        if (dto == null) {
            return null;
        }

        try {
            return ProviderUpdateCommandDTO.builder()
                    .id(id)
                    .companyId(companyId)
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

    public ProviderCreateResponseDTO toCreateResponseDTO(ProviderViewDTO view) {
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
            log.error("Erro ao mapear ProviderViewDTO para ProviderCreateResponseDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public ProviderUpdateResponseDTO toUpdateResponseDTO(ProviderViewDTO view) {
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
            log.error("Erro ao mapear ProviderViewDTO para ProviderUpdateResponseDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public ProviderGetProviderByIdResponseDTO toGetProviderByIdResponseDTO(ProviderViewDTO view) {
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
            log.error("Erro ao mapear ProviderViewDTO para ProviderGetProviderByIdResponseDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public ProviderGetAllProvidersResponseDTO toGetAllProvidersResponseDTO(ProviderViewDTO view) {
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
            log.error("Erro ao mapear ProviderViewDTO para ProviderGetAllProvidersResponseDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public ProviderGetActiveProvidersResponseDTO toGetActiveProvidersResponseDTO(ProviderViewDTO view) {
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
            log.error("Erro ao mapear ProviderViewDTO para ProviderGetActiveProvidersResponseDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public ProviderGetProvidersByCommunicationTypeResponseDTO toGetProvidersByCommunicationTypeResponseDTO(ProviderViewDTO view) {
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
            log.error("Erro ao mapear ProviderViewDTO para ProviderGetProvidersByCommunicationTypeResponseDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public ProviderGetDefaultProviderResponseDTO toGetDefaultProviderResponseDTO(ProviderViewDTO view) {
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
            log.error("Erro ao mapear ProviderViewDTO para ProviderGetDefaultProviderResponseDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public ProviderActivateProviderResponseDTO toActivateProviderResponseDTO(ProviderViewDTO view) {
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
            log.error("Erro ao mapear ProviderViewDTO para ProviderActivateProviderResponseDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public ProviderDeactivateProviderResponseDTO toDeactivateProviderResponseDTO(ProviderViewDTO view) {
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
            log.error("Erro ao mapear ProviderViewDTO para ProviderDeactivateProviderResponseDTO: {}", e.getMessage(), e);
            throw e;
        }
    }

    public ProviderSetAsDefaultResponseDTO toSetAsDefaultResponseDTO(ProviderViewDTO view) {
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
            log.error("Erro ao mapear ProviderViewDTO para ProviderSetAsDefaultResponseDTO: {}", e.getMessage(), e);
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
