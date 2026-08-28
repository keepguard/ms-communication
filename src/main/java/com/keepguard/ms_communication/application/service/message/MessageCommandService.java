package com.keepguard.ms_communication.application.service.message;

import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import com.keepguard.lib_common.logging.annotation.LogOperation;
import com.keepguard.ms_communication.application.port.in.service.MessagePort;
import com.keepguard.ms_communication.application.port.out.metrics.MetricsPort;
import com.keepguard.ms_communication.application.port.out.persistence.ProviderRepositoryPort;
import com.keepguard.ms_communication.application.service.exception.MessageSendException;
import com.keepguard.ms_communication.application.service.exception.NotFoundException;
import com.keepguard.ms_communication.application.service.template.TemplateProcessorService;
import com.keepguard.ms_communication.domain.dto.message.MessageSendCommandDTO;
import com.keepguard.ms_communication.domain.entity.Provider;
import com.keepguard.ms_communication.infrastructure.provider.CommunicationProvider;
import com.keepguard.ms_communication.infrastructure.provider.strategy.ProviderStrategy;
import com.keepguard.ms_communication.infrastructure.provider.strategy.ProviderStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageCommandService implements MessagePort {

    private final ProviderRepositoryPort providerRepositoryPort;
    private final ProviderStrategyFactory strategyFactory;
    private final MetricsPort metricsPort;
    private final List<CommunicationProvider> communicationProviders;
    private final TemplateProcessorService templateProcessorService;

    @LogOperation(
            operation = "SEND_MESSAGE_WITH_PROVIDER",
            description = "Enviando mensagem via provedor: {providerId} para: {command.recipient}",
            audit = true,
            auditAction = "SEND_MESSAGE",
            auditEntityType = "MESSAGE"
    )
    public void sendWithProvider(UUID providerId, MessageSendCommandDTO command) {
        log.info("Enviando mensagem via provedor: {} para: {}", providerId, command.getRecipient());

        // Buscar provedor
        Provider provider = providerRepositoryPort.findById(providerId)
                .orElseThrow(() -> new NotFoundException("Provedor não encontrado: " + providerId, "PROVIDER_NOT_FOUND", Map.of("providerId", providerId)));

        try {
            // Enviar via provedor
            boolean sent = sendViaProvider(provider, command);

            if (sent) {
                metricsPort.incrementCounter("message_sent_total",
                        Map.of("provider_id", providerId.toString(),
                                "type", command.getCommunicationType().name(),
                                "status", "SUCCESS"));
                log.info("Mensagem enviada com sucesso via provedor: {}", provider.getName());
            } else {
                metricsPort.incrementCounter("message_sent_total",
                        Map.of("provider_id", providerId.toString(),
                                "type", command.getCommunicationType().name(),
                                "status", "FAILED"));
                log.error("Falha ao enviar mensagem via provedor: {}", provider.getName());
            }

        } catch (Exception e) {
            metricsPort.incrementCounter("message_sent_total",
                    Map.of("provider_id", providerId.toString(),
                            "type", command.getCommunicationType().name(),
                            "status", "ERROR"));
            log.error("Erro ao enviar mensagem: {}", e.getMessage(), e);
            throw e;
        }
    }

    @LogOperation(
            operation = "SEND_MESSAGE_WITH_FALLBACK",
            description = "Enviando mensagem com fallback para: {command.recipient}",
            audit = true,
            auditAction = "SEND_MESSAGE",
            auditEntityType = "MESSAGE"
    )
    public boolean sendWithFallback(MessageSendCommandDTO command) {
        log.info("Enviando mensagem com fallback para: {}", command.getRecipient());

        // Buscar provedores ativos para o tipo de comunicação
        List<Provider> providers = providerRepositoryPort.findByCommunicationType(command.getCommunicationType());

        if (providers.isEmpty()) {
            throw new NotFoundException("Nenhum provedor ativo encontrado para tipo: " + command.getCommunicationType());
        }

        // Tentar enviar via cada provedor até conseguir
        for (Provider provider : providers) {
            try {

                log.info("Tentando enviar via provedor: {}", provider.getName());

                boolean sent = sendViaProvider(provider, command);

                if (sent) {
                    log.info("Mensagem enviada com sucesso via: {}", provider.getName());
                    return true;
                }

            } catch (Exception e) {
                log.warn("Falha ao enviar via provedor: {} (URL: {}) - Erro: {} - Tentando próximo provedor...",
                        provider.getName(), provider.getUrl(), e.getMessage());
            }
        }

        // Se nenhum provedor funcionou
        log.error("Todos os provedores falharam para tipo: {}", command.getCommunicationType());
        return false;
    }

    private boolean sendViaProvider(Provider provider, MessageSendCommandDTO command) {
        log.debug("Iniciando envio via provedor: {} (tipo: {})", provider.getName(), provider.getProviderType());

        // Processar template se necessário
        String finalSubject = command.getSubject();
        String finalContent = command.getContent();

        if (command.getTemplateType() != null && command.getVariables() != null && !command.getVariables().isEmpty()) {
            try {
                log.info("Processando template: {} com variáveis: {}", command.getTemplateType(), command.getVariables());

                MessageTypeEnum messageType = command.getMessageType() != null ?
                        MessageTypeEnum.valueOf(command.getMessageType()) : MessageTypeEnum.EMAIL;

                // Usar as variáveis exatamente como fornecidas no payload (sem modificações)
                Map<String, Object> variables = command.getVariables();
                log.info("Variáveis recebidas do payload: {}", variables);

                TemplateProcessorService.ProcessedTemplate processedTemplate = templateProcessorService.processTemplate(
                        TemplateTypeEnum.valueOf(command.getTemplateType()),
                        messageType,
                        command.getCompanyId().toString(), // UUID da empresa do header X-Tenant-Id
                        variables  // usar variáveis exatamente como fornecidas no payload
                );

                // Usar subject e content processados do template
                finalSubject = processedTemplate.getSubject();
                finalContent = processedTemplate.getContent();

                log.info("Template processado - Subject: {}, Content length: {}, Variáveis: {}",
                        finalSubject, finalContent != null ? finalContent.length() : 0, variables);

            } catch (Exception e) {
                log.warn("Erro ao processar template, usando valores originais: {}", e.getMessage());
                // Continua com os valores originais se houver erro no processamento do template
            }
        }

        // Obter estratégia do provedor
        Optional<ProviderStrategy> strategy = strategyFactory.getStrategy(provider.getProviderType());

        if (strategy.isEmpty()) {
            String errorMessage = "Estratégia não encontrada para provedor: " + provider.getProviderType();
            log.error(errorMessage);
            throw new MessageSendException(errorMessage);
        }

        // Obter o CommunicationProvider correspondente usando a estratégia
        CommunicationProvider communicationProvider = strategy.get().getCommunicationProvider(communicationProviders);

        if (communicationProvider == null) {
            String errorMessage = String.format("Nenhum provedor de comunicação encontrado para: %s", provider.getProviderType());
            log.error(errorMessage);
            throw new MessageSendException(errorMessage);
        }

        // Enviar mensagem usando o provedor de comunicação
        log.info("Enviando mensagem via provedor: {} para destinatário: {}", provider.getName(), command.getRecipient());

        return communicationProvider.sendMessage(
                provider,
                command.getRecipient(),
                finalSubject,
                finalContent,
                command.getCommunicationType(),
                command.getMessageType(),
                command.getTemplateType()
        );
    }

}
