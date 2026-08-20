package com.keepguard.ms_communication.infrastructure.persistence.mapper;

import com.keepguard.ms_communication.domain.entity.Template;
import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import com.keepguard.ms_communication.infrastructure.persistence.entity.TemplateJpaEntity;
import com.keepguard.ms_communication.test.builder.TemplateTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para TemplateJpaMapper
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Template JPA Mapper Tests")
class TemplateJpaMapperTest {
    
    @InjectMocks
    private TemplateJpaMapper templateJpaMapper;
    
    private Template template;
    private TemplateJpaEntity templateJpaEntity;
    private UUID templateId;
    private LocalDateTime now;
    
    @BeforeEach
    void setUp() {
        templateId = UUID.randomUUID();
        now = LocalDateTime.now();
        
        template = TemplateTestBuilder.aTemplate()
            .withId(templateId)
            .withTemplateType(TemplateTypeEnum.CADASTRO_SUCESSO)
            .withMessageType(MessageTypeEnum.EMAIL)
            .withApplication("test-app")
            .withName("Test Template")
            .withDescription("Test template description")
            .withSubject("Test Subject")
            .withContent("Test content with {{variable}}")
            .withIsActive(true)
            .withCreatedAt(now)
            .withUpdatedAt(now)
            .buildDomain();
        
        String variables = "{\"userName\":\"John Doe\",\"activationLink\":\"https://example.com/activate\"}";
        template.setVariables(variables);
        
        templateJpaEntity = TemplateJpaEntity.builder()
            .id(templateId)
            .templateType(TemplateTypeEnum.CADASTRO_SUCESSO)
            .messageType(MessageTypeEnum.EMAIL)
            .tenantId("test-app")
            .name("Test Template")
            .description("Test template description")
            .subject("Test Subject")
            .content("Test content with {{variable}}")
            .variables(variables)
            .isActive(true)
            .createdAt(now)
            .updatedAt(now)
            .build();
    }
    
    @Test
    @DisplayName("Deve converter Template para TemplateJpaEntity com sucesso")
    void shouldConvertTemplateToEntitySuccessfully() {
        // When
        TemplateJpaEntity result = templateJpaMapper.toEntity(template);
        
        // Then
        assertNotNull(result);
        assertEquals(template.getId(), result.getId());
        assertEquals(template.getTemplateType(), result.getTemplateType());
        assertEquals(template.getMessageType(), result.getMessageType());
        assertEquals(template.getTenantId(), result.getTenantId());
        assertEquals(template.getName(), result.getName());
        assertEquals(template.getDescription(), result.getDescription());
        assertEquals(template.getSubject(), result.getSubject());
        assertEquals(template.getContent(), result.getContent());
        assertEquals(template.getVariables(), result.getVariables());
        assertEquals(template.getIsActive(), result.getIsActive());
        assertEquals(template.getCreatedAt(), result.getCreatedAt());
        assertEquals(template.getUpdatedAt(), result.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Deve converter TemplateJpaEntity para Template com sucesso")
    void shouldConvertEntityToTemplateSuccessfully() {
        // When
        Template result = templateJpaMapper.toDomain(templateJpaEntity);
        
        // Then
        assertNotNull(result);
        assertEquals(templateJpaEntity.getId(), result.getId());
        assertEquals(templateJpaEntity.getTemplateType(), result.getTemplateType());
        assertEquals(templateJpaEntity.getMessageType(), result.getMessageType());
        assertEquals(templateJpaEntity.getTenantId(), result.getTenantId());
        assertEquals(templateJpaEntity.getName(), result.getName());
        assertEquals(templateJpaEntity.getDescription(), result.getDescription());
        assertEquals(templateJpaEntity.getSubject(), result.getSubject());
        assertEquals(templateJpaEntity.getContent(), result.getContent());
        assertEquals(templateJpaEntity.getVariables(), result.getVariables());
        assertEquals(templateJpaEntity.getIsActive(), result.getIsActive());
        assertEquals(templateJpaEntity.getCreatedAt(), result.getCreatedAt());
        assertEquals(templateJpaEntity.getUpdatedAt(), result.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Deve retornar null quando Template é null")
    void shouldReturnNullWhenTemplateIsNull() {
        // When
        TemplateJpaEntity result = templateJpaMapper.toEntity(null);
        
        // Then
        assertNull(result);
    }
    
    @Test
    @DisplayName("Deve retornar null quando TemplateJpaEntity é null")
    void shouldReturnNullWhenEntityIsNull() {
        // When
        Template result = templateJpaMapper.toDomain(null);
        
        // Then
        assertNull(result);
    }
    
    @Test
    @DisplayName("Deve converter Template com diferentes tipos de template")
    void shouldConvertTemplateWithDifferentTemplateTypes() {
        // Given
        TemplateTypeEnum[] templateTypes = {
            TemplateTypeEnum.CADASTRO_SUCESSO,
            TemplateTypeEnum.RECUPERACAO_SENHA,
            TemplateTypeEnum.NOTIFICACAO_GERAL,
            TemplateTypeEnum.ALERTA_SEGURANCA,
            TemplateTypeEnum.CONFIRMACAO_ACAO
        };
        
        for (TemplateTypeEnum templateType : templateTypes) {
            Template testTemplate = TemplateTestBuilder.aTemplate()
                .withTemplateType(templateType)
                .buildDomain();
            
            // When
            TemplateJpaEntity result = templateJpaMapper.toEntity(testTemplate);
            
            // Then
            assertNotNull(result);
            assertEquals(templateType, result.getTemplateType());
        }
    }
    
    @Test
    @DisplayName("Deve converter Template com diferentes tipos de mensagem")
    void shouldConvertTemplateWithDifferentMessageTypes() {
        // Given
        MessageTypeEnum[] messageTypes = {
            MessageTypeEnum.EMAIL,
            MessageTypeEnum.SMS,
            MessageTypeEnum.WHATSAPP,
            MessageTypeEnum.PUSH
        };
        
        for (MessageTypeEnum messageType : messageTypes) {
            Template testTemplate = TemplateTestBuilder.aTemplate()
                .withMessageType(messageType)
                .buildDomain();
            
            // When
            TemplateJpaEntity result = templateJpaMapper.toEntity(testTemplate);
            
            // Then
            assertNotNull(result);
            assertEquals(messageType, result.getMessageType());
        }
    }
    
    @Test
    @DisplayName("Deve converter Template com diferentes aplicações")
    void shouldConvertTemplateWithDifferentApplications() {
        // Given
        String[] applications = {
            "test-app",
            "user-service",
            "auth-service",
            "notification-service",
            null
        };
        
        for (String application : applications) {
            Template testTemplate = TemplateTestBuilder.aTemplate()
                .withApplication(application)
                .buildDomain();
            
            // When
            TemplateJpaEntity result = templateJpaMapper.toEntity(testTemplate);
            
            // Then
            assertNotNull(result);
            assertEquals(application, result.getTenantId());
        }
    }
    
    @Test
    @DisplayName("Deve converter Template com diferentes nomes")
    void shouldConvertTemplateWithDifferentNames() {
        // Given
        String[] names = {
            "Welcome Template",
            "Password Reset",
            "Security Alert",
            "Confirmation Email",
            "SMS Notification"
        };
        
        for (String name : names) {
            Template testTemplate = TemplateTestBuilder.aTemplate()
                .withName(name)
                .buildDomain();
            
            // When
            TemplateJpaEntity result = templateJpaMapper.toEntity(testTemplate);
            
            // Then
            assertNotNull(result);
            assertEquals(name, result.getName());
        }
    }
    
    @Test
    @DisplayName("Deve converter Template com diferentes descrições")
    void shouldConvertTemplateWithDifferentDescriptions() {
        // Given
        String[] descriptions = {
            "Template for welcome emails",
            "Template for password reset",
            "Template for security alerts",
            "Template for confirmations",
            null
        };
        
        for (String description : descriptions) {
            Template testTemplate = TemplateTestBuilder.aTemplate()
                .withDescription(description)
                .buildDomain();
            
            // When
            TemplateJpaEntity result = templateJpaMapper.toEntity(testTemplate);
            
            // Then
            assertNotNull(result);
            assertEquals(description, result.getDescription());
        }
    }
    
    @Test
    @DisplayName("Deve converter Template com diferentes assuntos")
    void shouldConvertTemplateWithDifferentSubjects() {
        // Given
        String[] subjects = {
            "Welcome to our platform!",
            "Reset your password",
            "Security alert",
            "Confirm your action",
            null
        };
        
        for (String subject : subjects) {
            Template testTemplate = TemplateTestBuilder.aTemplate()
                .withSubject(subject)
                .buildDomain();
            
            // When
            TemplateJpaEntity result = templateJpaMapper.toEntity(testTemplate);
            
            // Then
            assertNotNull(result);
            assertEquals(subject, result.getSubject());
        }
    }
    
    @Test
    @DisplayName("Deve converter Template com diferentes conteúdos")
    void shouldConvertTemplateWithDifferentContents() {
        // Given
        String[] contents = {
            "Hello {{userName}}, welcome!",
            "Click here to reset: {{resetLink}}",
            "Security alert: {{alertMessage}}",
            "Confirm action: {{confirmationLink}}",
            "Simple message without variables"
        };
        
        for (String content : contents) {
            Template testTemplate = TemplateTestBuilder.aTemplate()
                .withContent(content)
                .buildDomain();
            
            // When
            TemplateJpaEntity result = templateJpaMapper.toEntity(testTemplate);
            
            // Then
            assertNotNull(result);
            assertEquals(content, result.getContent());
        }
    }
    
    @Test
    @DisplayName("Deve converter Template com diferentes variáveis")
    void shouldConvertTemplateWithDifferentVariables() {
        // Given
        String[] variablesArray = {
            "{\"userName\":\"John Doe\",\"activationLink\":\"https://example.com/activate\"}",
            "{\"resetLink\":\"https://example.com/reset\",\"expirationTime\":\"24 hours\"}",
            "{}",
            null
        };
        
        for (String variables : variablesArray) {
            Template testTemplate = TemplateTestBuilder.aTemplate()
                .buildDomain();
            testTemplate.setVariables(variables);
            
            // When
            TemplateJpaEntity result = templateJpaMapper.toEntity(testTemplate);
            
            // Then
            assertNotNull(result);
            assertEquals(variables, result.getVariables());
        }
    }
    
    @Test
    @DisplayName("Deve converter Template com diferentes estados ativo")
    void shouldConvertTemplateWithDifferentActiveStates() {
        // Given
        boolean[] activeStates = {true, false};
        
        for (boolean isActive : activeStates) {
            Template testTemplate = TemplateTestBuilder.aTemplate()
                .withIsActive(isActive)
                .buildDomain();
            
            // When
            TemplateJpaEntity result = templateJpaMapper.toEntity(testTemplate);
            
            // Then
            assertNotNull(result);
            assertEquals(isActive, result.getIsActive());
        }
    }
    
    @Test
    @DisplayName("Deve converter Template com diferentes timestamps")
    void shouldConvertTemplateWithDifferentTimestamps() {
        // Given
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();
        
        Template testTemplate = TemplateTestBuilder.aTemplate()
            .withCreatedAt(createdAt)
            .withUpdatedAt(updatedAt)
            .buildDomain();
        
        // When
        TemplateJpaEntity result = templateJpaMapper.toEntity(testTemplate);
        
        // Then
        assertNotNull(result);
        assertEquals(createdAt, result.getCreatedAt());
        assertEquals(updatedAt, result.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Deve converter Template com todos os campos nulos")
    void shouldConvertTemplateWithAllNullFields() {
        // Given
        Template testTemplate = new Template(
            templateId,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            false,
            null,
            null
        );
        
        // When
        TemplateJpaEntity result = templateJpaMapper.toEntity(testTemplate);
        
        // Then
        assertNotNull(result);
        assertNull(result.getTemplateType());
        assertNull(result.getMessageType());
        assertNull(result.getTenantId());
        assertNull(result.getName());
        assertNull(result.getDescription());
        assertNull(result.getSubject());
        assertNull(result.getContent());
        assertNull(result.getVariables());
        assertFalse(result.getIsActive());
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Deve converter TemplateJpaEntity com todos os campos nulos")
    void shouldConvertEntityWithAllNullFields() {
        // Given
        TemplateJpaEntity testEntity = TemplateJpaEntity.builder()
            .id(templateId)
            .templateType(null)
            .messageType(null)
            .tenantId(null)
            .name(null)
            .description(null)
            .subject(null)
            .content(null)
            .variables(null)
            .isActive(false)
            .createdAt(null)
            .updatedAt(null)
            .build();
        
        // When
        Template result = templateJpaMapper.toDomain(testEntity);
        
        // Then
        assertNotNull(result);
        assertNull(result.getTemplateType());
        assertNull(result.getMessageType());
        assertNull(result.getTenantId());
        assertNull(result.getName());
        assertNull(result.getDescription());
        assertNull(result.getSubject());
        assertNull(result.getContent());
        assertNull(result.getVariables());
        assertFalse(result.getIsActive());
        assertNull(result.getCreatedAt());
        assertNull(result.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Deve converter Template com variáveis contendo diferentes tipos de dados")
    void shouldConvertTemplateWithVariablesContainingDifferentDataTypes() {
        // Given
        String variables = "{\"userName\":\"John Doe\",\"age\":25,\"isActive\":true,\"balance\":100.50,\"items\":[\"item1\",\"item2\"]}";
        
        Template testTemplate = TemplateTestBuilder.aTemplate()
            .buildDomain();
        testTemplate.setVariables(variables);
        
        // When
        TemplateJpaEntity result = templateJpaMapper.toEntity(testTemplate);
        
        // Then
        assertNotNull(result);
        assertNotNull(result.getVariables());
        assertEquals(variables, result.getVariables());
    }
}
