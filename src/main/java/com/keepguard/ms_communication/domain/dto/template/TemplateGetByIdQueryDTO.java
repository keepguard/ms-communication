package com.keepguard.ms_communication.domain.dto.template;

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
public class TemplateGetByIdQueryDTO {
    
    @NotNull(message = "ID do template é obrigatório")
    private UUID id;
    
    @NotNull(message = "O header X-Application é obrigatório")
    private UUID xApplicationUuid;
}

