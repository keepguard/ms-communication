package com.keepguard.ms_communication.infrastructure.provider.n8n;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class N8NConfigParser {

    private final ObjectMapper objectMapper;

    public Optional<CommunicationTypeEnum> extractCommunicationType(String configuration) {
        try {
            JsonNode configNode = objectMapper.readTree(configuration);

            if (configNode.has("communicationType")) {
                String communicationTypeStr = configNode.get("communicationType").asText();
                return Optional.of(CommunicationTypeEnum.valueOf(communicationTypeStr.toUpperCase()));
            }

            // Fallback: try to extract from workflowType field
            if (configNode.has("workflowType")) {
                String workflowTypeStr = configNode.get("workflowType").asText();
                return mapWorkflowTypeToCommunicationType(workflowTypeStr);
            }

            return Optional.empty();

        } catch (Exception e) {
            log.warn("Erro ao extrair tipo de comunicação da configuração: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public Optional<String> extractReplyTo(String configuration) {
        try {
            JsonNode configNode = objectMapper.readTree(configuration);

            if (configNode.has("replyTo")) {
                return Optional.of(configNode.get("replyTo").asText());
            }

            return Optional.empty();

        } catch (Exception e) {
            log.warn("Erro ao extrair reply-to da configuração: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<CommunicationTypeEnum> mapWorkflowTypeToCommunicationType(String workflowType) {
        return switch (workflowType.toLowerCase()) {
            case "email" -> Optional.of(CommunicationTypeEnum.EMAIL);
            case "sms" -> Optional.of(CommunicationTypeEnum.SMS);
            case "whatsapp" -> Optional.of(CommunicationTypeEnum.WHATSAPP);
            case "push" -> Optional.of(CommunicationTypeEnum.PUSH_NOTIFICATION);
            default -> Optional.empty();
        };
    }
}