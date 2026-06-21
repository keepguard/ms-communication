package com.keepguard.ms_communication.adapters.in.messaging.rabbitmq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * DTO para resultados de processamento de mensagens RabbitMQ.
 * 
 * <p>Este DTO representa o resultado do processamento de uma mensagem
 * enviada via RabbitMQ, seguindo os princípios da Arquitetura Hexagonal.</p>
 * 
 * <p>Características:</p>
 * <ul>
 *   <li>Imutabilidade através de record</li>
 *   <li>Serialização JSON automática</li>
 *   <li>Informações de resultado e erro</li>
 *   <li>Timestamp de processamento</li>
 * </ul>
 * 
 * @author KeepGuard Team
 * @version 1.1.2
 * @since 1.1.2
 */
public record MessageSendResultRabbitMQDTO(
    
    @JsonProperty("xCorrelationId")
    @NotNull(message = "ID de correlação é obrigatório")
    String xCorrelationId,
    
    @JsonProperty("success")
    @NotNull(message = "Status de sucesso é obrigatório")
    Boolean success,
    
    @JsonProperty("errorCode")
    String errorCode,
    
    @JsonProperty("errorMessage")
    String errorMessage,
    
    @JsonProperty("processedAt")
    @NotNull(message = "Data de processamento é obrigatória")
    LocalDateTime processedAt
    
) {
    
    /**
     * Retorna uma representação do resultado para logs.
     * 
     * @return String formatada para logging
     */
    public String toLogString() {
        return String.format(
            "MessageSendResultRabbitMQDTO{xCorrelationId=%s, success=%s, errorCode='%s', errorMessage='%s', processedAt=%s}",
            xCorrelationId, success, errorCode, errorMessage, processedAt
        );
    }
    
    /**
     * Verifica se o processamento foi bem-sucedido.
     * 
     * @return true se o processamento foi bem-sucedido
     */
    public boolean isSuccess() {
        return Boolean.TRUE.equals(success);
    }
    
    /**
     * Verifica se houve erro no processamento.
     * 
     * @return true se houve erro
     */
    public boolean hasError() {
        return !isSuccess() && (errorCode != null || errorMessage != null);
    }
}
