package com.keepguard.ms_communication.application.service.message;

import com.keepguard.lib_common.logging.annotation.LogOperation;
import com.keepguard.ms_communication.adapters.in.messaging.rabbitmq.dto.MessageSendRabbitMQDTO;
import com.keepguard.ms_communication.adapters.in.messaging.rabbitmq.mapper.MessageRabbitMQMapper;
import com.keepguard.ms_communication.application.port.in.messaging.MessageSendRabbitMQPort;
import com.keepguard.ms_communication.application.port.in.service.MessagePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageRabbitMQProcessorService implements MessageSendRabbitMQPort {
    
    private final MessageRabbitMQMapper messageRabbitMQMapper;
    private final MessagePort messagePort;

    @Override
    @LogOperation(
        operation = "PROCESS_RABBITMQ_MESSAGE_SEND",
        description = "Processando mensagem de envio via RabbitMQ - xCorrelationId: {rabbitMQMessage.xCorrelationId}, recipient: {rabbitMQMessage.recipient}",
        audit = true,
        auditAction = "PROCESS_MESSAGE_SEND",
        auditEntityType = "MESSAGE"
    )
    public void processMessageSend(MessageSendRabbitMQDTO rabbitMQMessage) {
        log.info("Iniciando processamento de mensagem RabbitMQ: xCorrelationId={}, recipient={}", 
            rabbitMQMessage.xCorrelationId(), rabbitMQMessage.recipient());
        
        try {
            // Converter DTO RabbitMQ para Command de domínio
            var command = messageRabbitMQMapper.toSendCommand(rabbitMQMessage);

            // Processar envio da mensagem
            messagePort.sendWithFallback(command);
            
            log.info("Processamento de mensagem RabbitMQ concluído: xCorrelationId={}", 
                rabbitMQMessage.xCorrelationId());
            
        } catch (IllegalArgumentException e) {
            // Erro de validação - rejeitar mensagem
            log.warn("Mensagem RabbitMQ rejeitada por validação: xCorrelationId={}, error={}", 
                rabbitMQMessage.xCorrelationId(), e.getMessage());
            
        } catch (Exception e) {
            // Erro de processamento - registrar falha
            log.error("Erro no processamento de mensagem RabbitMQ: xCorrelationId={}, error={}", 
                rabbitMQMessage.xCorrelationId(), e.getMessage(), e);
        }
    }
}
