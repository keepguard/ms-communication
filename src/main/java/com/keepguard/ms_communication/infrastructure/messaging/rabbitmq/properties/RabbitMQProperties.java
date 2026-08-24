package com.keepguard.ms_communication.infrastructure.messaging.rabbitmq.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propriedades de configuração do RabbitMQ externalizadas.
 * 
 * <p>Esta classe centraliza todas as configurações do RabbitMQ, permitindo
 * fácil customização através de application.yml e variáveis de ambiente.</p>
 * 
 * <p>Características:</p>
 * <ul>
 *   <li>Configuração externalizada (@ConfigurationProperties)</li>
 *   <li>Validação automática de propriedades</li>
 *   <li>Suporte a diferentes ambientes</li>
 *   <li>Valores padrão sensatos</li>
 * </ul>
 * 
 * @author KeepGuard Team
 * @version 1.1.2
 * @since 1.1.2
 */
@Data
@Component
@ConfigurationProperties(prefix = "rabbitmq")
public class RabbitMQProperties {
    
    /**
     * Host do RabbitMQ.
     */
    private String host = "localhost";
    
    /**
     * Porta do RabbitMQ.
     */
    private Integer port = 5672;
    
    /**
     * Usuário para conexão.
     */
    private String username = "guest";
    
    /**
     * Senha para conexão.
     */
    private String password = "guest";
    
    /**
     * Virtual host.
     */
    private String virtualHost = "/";
    
    /**
     * Configurações do Publisher.
     */
    private Publisher publisher = new Publisher();
    
    /**
     * Configurações do Consumer.
     */
    private Consumer consumer = new Consumer();
    
    /**
     * Configurações das filas e exchanges.
     */
    private Queues queues = new Queues();
    
    @Data
    public static class Publisher {
        private Boolean confirmType = true;
        private Boolean mandatory = true;
        private Integer connectionTimeout = 30000;
        private Integer requestedHeartbeat = 60;
        private Integer requestedChannelMax = 0;
    }
    
    @Data
    public static class Consumer {
        private Integer prefetchCount = 1;
        private Boolean autoAck = false;
        private Integer connectionTimeout = 30000;
        private Integer requestedHeartbeat = 60;
        private Integer requestedChannelMax = 0;
        private Integer concurrency = 2;
        private Integer maxConcurrency = 10;
    }
    
    @Data
    public static class Queues {
        
        // Filas de mensageria (padrão enterprise)
        private String messageSend = "ms.communication.message.send.local";
        private String messageSendRequestsDlt = "ms.communication.message.send.dlt.local";
        
        // Exchanges (valores genéricos - serão sobrescritos pelo perfil ativo)
        private String messageExchange = "exchange-name-will-be-overwritten-by-profile";
        private String deadLetterExchange = "exchange-dlt-name-will-be-overwritten-by-profile";
        
        // Exchange para publicar no srv-email-google-sender (NOVO)
        private String emailSenderExchange = "srv-email-google-sender-exchange-dev";
        private String emailSenderRoutingKey = "email.google.send";
        
        // Fila direta para srv-sms-sender
        private String smsQueue = "keepguard.notifications.sms";
        
        // Routing Keys
        private String routingKeyMessageSend = "communication.message.send";
        private String routingKeyMessageFailed = "message.failed";
        
        // Configurações gerais
        private Boolean durable = true;
        private Boolean autoDelete = false;
        private Boolean exclusive = false;
    }
}
