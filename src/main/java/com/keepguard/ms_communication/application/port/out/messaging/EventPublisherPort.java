package com.keepguard.ms_communication.application.port.out.messaging;

import com.keepguard.ms_communication.domain.event.MessageFailedEvent;
import com.keepguard.ms_communication.domain.event.MessageSentEvent;

public interface EventPublisherPort {
    
    /**
     * Publica evento de mensagem enviada com sucesso.
     */
    void publishMessageSentEvent(MessageSentEvent event);
    
    /**
     * Publica evento de falha no envio de mensagem.
     */
    void publishMessageFailedEvent(MessageFailedEvent event);
}
