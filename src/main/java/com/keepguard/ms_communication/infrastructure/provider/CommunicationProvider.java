package com.keepguard.ms_communication.infrastructure.provider;

import com.keepguard.ms_communication.domain.entity.Provider;
import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;

public interface CommunicationProvider {

    boolean sendMessage(Provider provider, String recipient, String subject, String content,
                       CommunicationTypeEnum communicationType, String messageType, String templateType);

    boolean supports(Provider provider);

    boolean testConnection(Provider provider);
}