package com.keepguard.ms_communication.application.port.out.email;

import com.keepguard.ms_communication.adapters.out.email.dto.EmailSendRequestDTO;
import com.keepguard.ms_communication.adapters.out.email.dto.EmailSendResponseDTO;

public interface DynamicEmailSenderPort {
    
    /**
     * Envia email usando a URL fornecida
     */
    EmailSendResponseDTO sendEmail(String url, EmailSendRequestDTO request);
    
    /**
     * Testa a conexão com o serviço de email
     */
    boolean testConnection(String url);
}
