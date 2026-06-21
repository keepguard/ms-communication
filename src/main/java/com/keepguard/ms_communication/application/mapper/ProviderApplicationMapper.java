package com.keepguard.ms_communication.application.mapper;

import com.keepguard.ms_communication.application.dto.provider.ProviderView;
import com.keepguard.ms_communication.domain.dto.provider.ProviderCreateCommandDTO;
import com.keepguard.ms_communication.domain.dto.provider.ProviderUpdateCommandDTO;
import com.keepguard.ms_communication.domain.entity.Provider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class ProviderApplicationMapper {

    public ProviderCreateCommandDTO toCreateCommand(com.keepguard.ms_communication.domain.dto.provider.ProviderCreateCommandDTO dto) {
        if (dto == null) {
            return null;
        }

        try {
            // Apenas retorna o mesmo DTO já que são do mesmo tipo
            return dto;
        } catch (Exception e) {
            log.error("Erro ao mapear ProviderCreateCommandDTO para ProviderCreateCommand: {}", e.getMessage(), e);
            throw e;
        }
    }

    public ProviderUpdateCommandDTO toUpdateCommand(com.keepguard.ms_communication.domain.dto.provider.ProviderUpdateCommandDTO dto) {
        if (dto == null) {
            return null;
        }

        try {
            // Apenas retorna o mesmo DTO já que são do mesmo tipo
            return dto;
        } catch (Exception e) {
            log.error("Erro ao mapear ProviderUpdateCommandDTO para ProviderUpdateCommand: {}", e.getMessage(), e);
            throw e;
        }
    }

    public ProviderView toView(Provider provider) {
        if (provider == null) {
            return null;
        }

        try {
            return new ProviderView(
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
        } catch (Exception e) {
            log.error("Erro ao mapear Provider para ProviderView: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Provider toDomain(ProviderCreateCommandDTO command) {
        if (command == null) {
            return null;
        }

        try {
            Provider provider = Provider.create(
                command.getName(),
                command.getProviderType(),
                command.getCommunicationType(),
                command.getUrl(),
                command.getConfiguration()
            );

            // Configurar propriedades adicionais
            if (command.getIsActive() != null) {
                provider.setIsActive(command.getIsActive());
            }
            if (command.getIsDefault() != null) {
                provider.setIsDefault(command.getIsDefault());
            }
            if (command.getPriority() != null) {
                provider.setPriority(command.getPriority());
            }
            if (command.getMaxRetries() != null) {
                provider.setMaxRetries(command.getMaxRetries());
            }
            if (command.getTimeoutSeconds() != null) {
                provider.setTimeoutSeconds(command.getTimeoutSeconds());
            }
            if (command.getRateLimitPerMinute() != null) {
                provider.setRateLimitPerMinute(command.getRateLimitPerMinute());
            }
            if (command.getDailyLimit() != null) {
                provider.setDailyLimit(command.getDailyLimit());
            }
            if (command.getMonthlyLimit() != null) {
                provider.setMonthlyLimit(command.getMonthlyLimit());
            }

            return provider;
        } catch (Exception e) {
            log.error("Erro ao mapear ProviderCreateCommand para Provider: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Provider toDomain(ProviderUpdateCommandDTO command, Provider existingProvider) {
        if (command == null || existingProvider == null) {
            return null;
        }

        try {
            // Atualizar propriedades básicas
            if (command.getName() != null) {
                existingProvider.setName(command.getName());
            }
            if (command.getProviderType() != null) {
                existingProvider.setProviderType(command.getProviderType());
            }
            if (command.getCommunicationType() != null) {
                existingProvider.setCommunicationType(command.getCommunicationType());
            }
            if (command.getIsActive() != null) {
                existingProvider.setIsActive(command.getIsActive());
            }
            if (command.getIsDefault() != null) {
                existingProvider.setIsDefault(command.getIsDefault());
            }
            if (command.getPriority() != null) {
                existingProvider.setPriority(command.getPriority());
            }
            if (command.getUrl() != null) {
                existingProvider.setUrl(command.getUrl());
            }
            if (command.getConfiguration() != null) {
                existingProvider.setConfiguration(command.getConfiguration());
            }
            if (command.getMaxRetries() != null) {
                existingProvider.setMaxRetries(command.getMaxRetries());
            }
            if (command.getTimeoutSeconds() != null) {
                existingProvider.setTimeoutSeconds(command.getTimeoutSeconds());
            }
            if (command.getRateLimitPerMinute() != null) {
                existingProvider.setRateLimitPerMinute(command.getRateLimitPerMinute());
            }
            if (command.getDailyLimit() != null) {
                existingProvider.setDailyLimit(command.getDailyLimit());
            }
            if (command.getMonthlyLimit() != null) {
                existingProvider.setMonthlyLimit(command.getMonthlyLimit());
            }

            // Atualizar timestamp
            existingProvider.setUpdatedAt(java.time.LocalDateTime.now());

            return existingProvider;
        } catch (Exception e) {
            log.error("Erro ao mapear ProviderUpdateCommand para Provider: {}", e.getMessage(), e);
            throw e;
        }
    }

}
