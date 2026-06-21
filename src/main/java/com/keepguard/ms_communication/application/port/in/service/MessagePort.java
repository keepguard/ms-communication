package com.keepguard.ms_communication.application.port.in.service;

import com.keepguard.ms_communication.domain.dto.message.MessageSendCommandDTO;

import java.util.UUID;

public interface MessagePort {

    void sendWithProvider(UUID providerId, MessageSendCommandDTO command);

    boolean sendWithFallback(MessageSendCommandDTO command);
}
