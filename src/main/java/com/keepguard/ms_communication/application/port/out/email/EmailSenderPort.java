package com.keepguard.ms_communication.application.port.out.email;

import com.keepguard.ms_communication.adapters.out.email.dto.EmailSendRequestDTO;
import com.keepguard.ms_communication.adapters.out.email.dto.EmailSendResponseDTO;

/**
 * Port para comunicação com serviços externos de envio de email
 * Seguindo os princípios de Arquitetura Hexagonal e DDD
 * 
 * Esta interface define o contrato para envio de emails através de
 * diferentes provedores externos, mantendo a independência da infraestrutura
 */
public interface EmailSenderPort {

    /**
     * Envia um email através do serviço externo
     * 
     * @param request DTO contendo os dados do email a ser enviado
     * @return DTO contendo a resposta do serviço externo
     * @throws com.keepguard.ms_communication.application.service.exception.EmailSendException 
     *         quando ocorre erro no envio do email
     */
    EmailSendResponseDTO sendEmail(EmailSendRequestDTO request);

    /**
     * Testa a conexão com o serviço externo
     * 
     * @return true se a conexão estiver funcionando, false caso contrário
     */
    boolean testConnection();

    /**
     * Verifica se este port suporta o tipo de provider especificado
     * 
     * @param providerType tipo do provider
     * @return true se suporta, false caso contrário
     */
    boolean supports(com.keepguard.ms_communication.domain.enums.ProviderTypeEnum providerType);
}
