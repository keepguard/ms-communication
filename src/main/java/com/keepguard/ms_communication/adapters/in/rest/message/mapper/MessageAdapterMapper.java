package com.keepguard.ms_communication.adapters.in.rest.message.mapper;

import com.keepguard.ms_communication.adapters.in.rest.message.dto.request.MessageSendRequestDTO;
import com.keepguard.ms_communication.domain.dto.message.MessageSendCommandDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class MessageAdapterMapper {

    public MessageSendCommandDTO toSendCommand(MessageSendRequestDTO dto, UUID xApplicationUuid) {
        if (dto == null) {
            return null;
        }

        try {
            return MessageSendCommandDTO.builder()
                    .xApplicationUuid(xApplicationUuid)
                    .communicationType(dto.getCommunicationType())
                    .recipient(dto.getRecipient())
                    .codeUser(dto.getCodeUser())
                    .subject(dto.getSubject())
                    .content(dto.getContent())
                    .messageType(dto.getMessageType() != null ? dto.getMessageType().name() : null)
                    .templateType(dto.getTemplateType() != null ? dto.getTemplateType().name() : null)
                    .variables(dto.getVariables())
                    .build();
        } catch (Exception e) {
            log.error("Erro ao mapear MessageSendDTO para MessageSendCommandDTO: {}", e.getMessage(), e);
            throw e;
        }
    }
}
