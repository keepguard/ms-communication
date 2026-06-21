package com.keepguard.ms_communication.adapters.out.email.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO para comunicação RabbitMQ com o srv-email-google-sender.
 * 
 * <p>Este DTO é usado para serializar mensagens de email que serão
 * publicadas no RabbitMQ para o serviço srv-email-google-sender consumir.</p>
 * 
 * @author KeepGuard Team
 * @version 1.1.2
 * @since 1.1.2
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessageDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Endereço de email do destinatário
     */
    private String to;
    
    /**
     * Assunto do email
     */
    private String subject;
    
    /**
     * Conteúdo HTML do email
     */
    private String html;
    
    /**
     * Endereços de email em cópia (CC)
     */
    private String cc;
    
    /**
     * Endereço de email para resposta (Reply-To)
     */
    private String replyTo;
    
    /**
     * ID de correlação para rastreamento da mensagem
     */
    private String xCorrelationId;
}
