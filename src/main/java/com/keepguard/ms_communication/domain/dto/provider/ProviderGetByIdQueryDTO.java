package com.keepguard.ms_communication.domain.dto.provider;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderGetByIdQueryDTO {
    
    @NotNull(message = "ID do provedor é obrigatório")
    private UUID id;
}

