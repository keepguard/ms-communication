package com.keepguard.ms_communication.domain.dto.message;

import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageSendCommandDTO {

    @NotBlank(message = "O header X-Tenant-Id é obrigatório")
    private UUID tenantId;

    @NotNull(message = "Tipo de comunicação é obrigatório")
    private CommunicationTypeEnum communicationType;
    
    @NotBlank(message = "Destinatário é obrigatório")
    private String recipient;
    
    private String codeUser; // Código do usuário para log
    private String subject;
    private String content;
    private String messageType;
    private String templateType;
    private Map<String, Object> variables;
}

