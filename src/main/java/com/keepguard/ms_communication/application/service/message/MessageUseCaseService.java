package com.keepguard.ms_communication.application.service.message;

import com.keepguard.ms_communication.application.dto.message.MessageSendCommandDTO;
import com.keepguard.ms_communication.application.port.in.MessagePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageUseCaseService implements MessagePort {

    private final MessageCommandService commandService;

    @Override
    public void sendWithProvider(UUID providerId, MessageSendCommandDTO command) {
        commandService.sendWithProvider(providerId, command);
    }

    @Override
    public boolean sendWithFallback(MessageSendCommandDTO command) {
        return commandService.sendWithFallback(command);
    }
}
