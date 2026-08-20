package com.keepguard.ms_communication.application.service.template;

import com.keepguard.ms_communication.application.port.out.persistence.TemplateRepositoryPort;
import com.keepguard.ms_communication.domain.entity.Template;
import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import com.keepguard.lib_common.logging.annotation.LogOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TemplateProcessorService {

    private final TemplateRepositoryPort templateRepositoryPort;
    
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{([^}]+)\\}\\}");

    @LogOperation(
        operation = "PROCESS_TEMPLATE",
        description = "Processando template: {templateType} com variáveis: {variables}",
        audit = true,
        auditAction = "PROCESS",
        auditEntityType = "TEMPLATE"
    )
    public ProcessedTemplate processTemplate(TemplateTypeEnum templateType, MessageTypeEnum messageType, 
                                           String tenantId, Map<String, Object> variables) {
        log.info("Processando template: {} para aplicação: {}", templateType, tenantId);

        // Buscar template ativo
        Optional<Template> templateOpt = templateRepositoryPort.findByTemplateTypeAndMessageTypeAndApplicationAndIsActive(
            templateType, messageType, tenantId, true);

        if (templateOpt.isEmpty()) {
            log.warn("Template não encontrado: {} - {} - {}", templateType, messageType, tenantId);
            throw new TemplateNotFoundException("Template não encontrado: " + templateType);
        }

        Template template = templateOpt.get();
        log.info("Template encontrado: {} - {}", template.getName(), template.getId());

        // Processar subject e content substituindo variáveis
        String processedSubject = processVariables(template.getSubject(), variables);
        String processedContent = processVariables(template.getContent(), variables);

        log.info("Template processado com sucesso - Subject: {}, Content length: {}", 
                processedSubject, processedContent != null ? processedContent.length() : 0);

        return ProcessedTemplate.builder()
                .subject(processedSubject)
                .content(processedContent)
                .templateId(template.getId().toString())
                .build();
    }

    private String processVariables(String template, Map<String, Object> variables) {
        if (template == null || template.isEmpty()) {
            return template;
        }

        if (variables == null || variables.isEmpty()) {
            log.warn("Template contém variáveis mas nenhuma variável foi fornecida: {}", template);
            return template;
        }

        StringBuffer result = new StringBuffer();
        Matcher matcher = VARIABLE_PATTERN.matcher(template);

        while (matcher.find()) {
            String variableName = matcher.group(1).trim();
            Object variableValue = variables.get(variableName);
            
            if (variableValue != null) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(variableValue.toString()));
                log.debug("Substituindo variável {} por {}", variableName, variableValue);
            } else {
                log.warn("Variável {} não encontrada nas variáveis fornecidas", variableName);
                matcher.appendReplacement(result, Matcher.quoteReplacement("{{" + variableName + "}}"));
            }
        }
        
        matcher.appendTail(result);
        return result.toString();
    }

    public static class ProcessedTemplate {
        private String subject;
        private String content;
        private String templateId;

        public static ProcessedTemplateBuilder builder() {
            return new ProcessedTemplateBuilder();
        }

        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public String getTemplateId() { return templateId; }
        public void setTemplateId(String templateId) { this.templateId = templateId; }

        public static class ProcessedTemplateBuilder {
            private String subject;
            private String content;
            private String templateId;

            public ProcessedTemplateBuilder subject(String subject) {
                this.subject = subject;
                return this;
            }

            public ProcessedTemplateBuilder content(String content) {
                this.content = content;
                return this;
            }

            public ProcessedTemplateBuilder templateId(String templateId) {
                this.templateId = templateId;
                return this;
            }

            public ProcessedTemplate build() {
                ProcessedTemplate processedTemplate = new ProcessedTemplate();
                processedTemplate.setSubject(this.subject);
                processedTemplate.setContent(this.content);
                processedTemplate.setTemplateId(this.templateId);
                return processedTemplate;
            }
        }
    }

    public static class TemplateNotFoundException extends RuntimeException {
        public TemplateNotFoundException(String message) {
            super(message);
        }
    }
}
