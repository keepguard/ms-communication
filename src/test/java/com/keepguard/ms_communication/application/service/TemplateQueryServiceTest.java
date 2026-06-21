package com.keepguard.ms_communication.application.service;

import com.keepguard.ms_communication.application.dto.common.PageResultView;
import com.keepguard.ms_communication.application.dto.template.TemplateSearchCriteriaView;
import com.keepguard.ms_communication.application.dto.template.TemplateView;
import com.keepguard.ms_communication.application.mapper.TemplateApplicationMapper;
import com.keepguard.ms_communication.application.port.out.persistence.TemplateRepositoryPort;
import com.keepguard.ms_communication.application.service.exception.NotFoundException;
import com.keepguard.ms_communication.application.service.template.TemplateQueryService;
import com.keepguard.ms_communication.domain.entity.Template;
import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import com.keepguard.ms_communication.test.builder.TemplateTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para TemplateQueryService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Template Query Service Tests")
class TemplateQueryServiceTest {

    @InjectMocks
    private TemplateQueryService templateQueryService;
    
    @Mock
    private TemplateRepositoryPort repositoryPort;
    
    @Mock
    private TemplateApplicationMapper mapper;
    
    private Template template;
    private TemplateView templateView;
    private UUID templateId;
    
    @BeforeEach
    void setUp() {
        templateId = UUID.randomUUID();
        
        template = TemplateTestBuilder.aTemplate()
            .withId(templateId)
            .withName("Test Template")
            .withTemplateType(TemplateTypeEnum.CADASTRO_SUCESSO)
            .withMessageType(MessageTypeEnum.EMAIL)
            .withIsActive(true)
            .buildDomain();
        
        templateView = new TemplateView(
            templateId,
            "Test Template",
            "Test template description",
            MessageTypeEnum.EMAIL,
            TemplateTypeEnum.CADASTRO_SUCESSO,
            "test-app",
            "Test content",
            "Test subject",
            true,
            "{}",
            java.time.LocalDateTime.now(),
            java.time.LocalDateTime.now()
        );
    }
    
    @Test
    @DisplayName("Deve buscar template por ID com sucesso")
    void shouldGetTemplateByIdSuccessfully() {
        // Given
        when(repositoryPort.findById(templateId)).thenReturn(Optional.of(template));
        when(mapper.toView(template)).thenReturn(templateView);
        
        // When
        TemplateView result = templateQueryService.getById(templateId);
        
        // Then
        assertNotNull(result);
        assertEquals(templateView, result);
        assertEquals(templateId, result.id());
        assertEquals("Test Template", result.name());
        assertEquals(TemplateTypeEnum.CADASTRO_SUCESSO, result.templateType());
        assertEquals(MessageTypeEnum.EMAIL, result.messageType());
        assertTrue(result.isActive());
        
        verify(repositoryPort).findById(templateId);
        verify(mapper).toView(template);
    }
    
    @Test
    @DisplayName("Deve lançar NotFoundException quando template não encontrado por ID")
    void shouldThrowNotFoundExceptionWhenTemplateNotFoundById() {
        // Given
        when(repositoryPort.findById(templateId)).thenReturn(Optional.empty());
        
        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            templateQueryService.getById(templateId);
        });
        
        assertEquals("Template não encontrado: " + templateId, exception.getMessage());
        
        verify(repositoryPort).findById(templateId);
        verify(mapper, never()).toView(any());
    }
    
    @Test
    @DisplayName("Deve buscar templates com critérios de busca com sucesso")
    void shouldSearchTemplatesWithCriteriaSuccessfully() {
        // Given
        TemplateSearchCriteriaView criteria = new TemplateSearchCriteriaView(
            0, 10, "name", "ASC", "Test", MessageTypeEnum.EMAIL, 
            TemplateTypeEnum.CADASTRO_SUCESSO, true
        );
        
        PageResultView<Template> domainResult = PageResultView.of(
            List.of(template), 0, 10, 1L
        );
        
        when(repositoryPort.search(criteria)).thenReturn(domainResult);
        when(mapper.toView(template)).thenReturn(templateView);
        
        // When
        PageResultView<TemplateView> result = templateQueryService.search(criteria);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(templateView, result.content().get(0));
        
        verify(repositoryPort).search(criteria);
        verify(mapper).toView(template);
    }
    
    @Test
    @DisplayName("Deve retornar lista vazia quando busca não retorna resultados")
    void shouldReturnEmptyListWhenSearchReturnsNoResults() {
        // Given
        TemplateSearchCriteriaView criteria = new TemplateSearchCriteriaView(
            0, 10, "name", "ASC", "NonExistent", null, null, null
        );
        
        PageResultView<Template> domainResult = PageResultView.of(
            List.of(), 0, 10, 0L
        );
        
        when(repositoryPort.search(criteria)).thenReturn(domainResult);
        
        // When
        PageResultView<TemplateView> result = templateQueryService.search(criteria);
        
        // Then
        assertNotNull(result);
        assertTrue(result.content().isEmpty());
        
        verify(repositoryPort).search(criteria);
        verify(mapper, never()).toView(any());
    }
    
    @Test
    @DisplayName("Deve listar todos os templates ativos")
    void shouldGetAllActiveTemplates() {
        // Given
        List<Template> templates = List.of(template);
        when(repositoryPort.findAllActive()).thenReturn(templates);
        when(mapper.toView(template)).thenReturn(templateView);
        
        // When
        List<TemplateView> result = templateQueryService.getAllActive();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(templateView, result.get(0));
        
        verify(repositoryPort).findAllActive();
        verify(mapper).toView(template);
    }
    
    @Test
    @DisplayName("Deve listar templates por tipo")
    void shouldGetTemplatesByType() {
        // Given
        TemplateTypeEnum type = TemplateTypeEnum.CADASTRO_SUCESSO;
        List<Template> templates = List.of(template);
        
        when(repositoryPort.findByType(type)).thenReturn(templates);
        when(mapper.toView(template)).thenReturn(templateView);
        
        // When
        List<TemplateView> result = templateQueryService.getByType(type);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(templateView, result.get(0));
        
        verify(repositoryPort).findByType(type);
        verify(mapper).toView(template);
    }
    
    @Test
    @DisplayName("Deve listar templates por tipo de mensagem")
    void shouldGetTemplatesByMessageType() {
        // Given
        MessageTypeEnum messageType = MessageTypeEnum.EMAIL;
        List<Template> templates = List.of(template);
        
        when(repositoryPort.findByMessageType(messageType)).thenReturn(templates);
        when(mapper.toView(template)).thenReturn(templateView);
        
        // When
        List<TemplateView> result = templateQueryService.getByMessageType(messageType);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(templateView, result.get(0));
        
        verify(repositoryPort).findByMessageType(messageType);
        verify(mapper).toView(template);
    }
    
    @Test
    @DisplayName("Deve verificar se template existe por ID quando existe")
    void shouldReturnTrueWhenTemplateExistsById() {
        // Given
        when(repositoryPort.findById(templateId)).thenReturn(Optional.of(template));
        
        // When
        boolean result = templateQueryService.existsById(templateId);
        
        // Then
        assertTrue(result);
        
        verify(repositoryPort).findById(templateId);
    }
    
    @Test
    @DisplayName("Deve retornar false quando template não existe por ID")
    void shouldReturnFalseWhenTemplateNotExistsById() {
        // Given
        when(repositoryPort.findById(templateId)).thenReturn(Optional.empty());
        
        // When
        boolean result = templateQueryService.existsById(templateId);
        
        // Then
        assertFalse(result);
        
        verify(repositoryPort).findById(templateId);
    }
    
    @Test
    @DisplayName("Deve verificar se template existe por nome quando existe")
    void shouldReturnTrueWhenTemplateExistsByName() {
        // Given
        String name = "Test Template";
        when(repositoryPort.existsByName(name)).thenReturn(true);
        
        // When
        boolean result = templateQueryService.existsByName(name);
        
        // Then
        assertTrue(result);
        
        verify(repositoryPort).existsByName(name);
    }
    
    @Test
    @DisplayName("Deve retornar false quando template não existe por nome")
    void shouldReturnFalseWhenTemplateNotExistsByName() {
        // Given
        String name = "Non Existent Template";
        when(repositoryPort.existsByName(name)).thenReturn(false);
        
        // When
        boolean result = templateQueryService.existsByName(name);
        
        // Then
        assertFalse(result);
        
        verify(repositoryPort).existsByName(name);
    }
    
    @Test
    @DisplayName("Deve buscar múltiplos templates com critérios de busca")
    void shouldSearchMultipleTemplatesWithCriteria() {
        // Given
        Template template2 = TemplateTestBuilder.aTemplate()
            .withId(UUID.randomUUID())
            .withName("Test Template 2")
            .buildDomain();
            
        TemplateView templateView2 = new TemplateView(
            template2.getId(),
            "Test Template 2",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
        
        TemplateSearchCriteriaView criteria = new TemplateSearchCriteriaView(
            0, 10, "name", "ASC", "Test", null, null, null
        );
        
        PageResultView<Template> domainResult = PageResultView.of(
            List.of(template, template2), 0, 10, 2L
        );
        
        when(repositoryPort.search(criteria)).thenReturn(domainResult);
        when(mapper.toView(template)).thenReturn(templateView);
        when(mapper.toView(template2)).thenReturn(templateView2);
        
        // When
        PageResultView<TemplateView> result = templateQueryService.search(criteria);
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.content().size());
        assertEquals(templateView, result.content().get(0));
        assertEquals(templateView2, result.content().get(1));
        
        verify(repositoryPort).search(criteria);
        verify(mapper).toView(template);
        verify(mapper).toView(template2);
    }
    
    @Test
    @DisplayName("Deve buscar templates para diferentes tipos")
    void shouldGetTemplatesForDifferentTypes() {
        // Given
        TemplateTypeEnum[] types = {
            TemplateTypeEnum.CADASTRO_SUCESSO,
            TemplateTypeEnum.RECUPERACAO_SENHA,
            TemplateTypeEnum.NOTIFICACAO_GERAL,
            TemplateTypeEnum.ALERTA_SEGURANCA,
            TemplateTypeEnum.CONFIRMACAO_ACAO
        };
        
        for (TemplateTypeEnum type : types) {
            Template testTemplate = TemplateTestBuilder.aTemplate()
                .withTemplateType(type)
                .buildDomain();
                
            TemplateView testTemplateView = new TemplateView(
                null, null, null, null, type, null, null, null, null, null, null, null
            );
            
            when(repositoryPort.findByType(type)).thenReturn(List.of(testTemplate));
            when(mapper.toView(testTemplate)).thenReturn(testTemplateView);
            
            // When
            List<TemplateView> result = templateQueryService.getByType(type);
            
            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(type, result.get(0).templateType());
        }
        
        verify(repositoryPort, times(types.length)).findByType(any());
    }
    
    @Test
    @DisplayName("Deve buscar templates para diferentes tipos de mensagem")
    void shouldGetTemplatesForDifferentMessageTypes() {
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
                
            TemplateView testTemplateView = new TemplateView(
                null, null, null, messageType, null, null, null, null, null, null, null, null
            );
            
            when(repositoryPort.findByMessageType(messageType)).thenReturn(List.of(testTemplate));
            when(mapper.toView(testTemplate)).thenReturn(testTemplateView);
            
            // When
            List<TemplateView> result = templateQueryService.getByMessageType(messageType);
            
            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(messageType, result.get(0).messageType());
        }
        
        verify(repositoryPort, times(messageTypes.length)).findByMessageType(any());
    }
    
    @Test
    @DisplayName("Deve buscar template por ID nulo")
    void shouldHandleNullIdWhenGettingTemplateById() {
        // Given
        when(repositoryPort.findById(null)).thenReturn(Optional.empty());
        
        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            templateQueryService.getById(null);
        });
        
        assertEquals("Template não encontrado: null", exception.getMessage());
        
        verify(repositoryPort).findById(null);
        verify(mapper, never()).toView(any());
    }
    
    @Test
    @DisplayName("Deve verificar existência com ID nulo")
    void shouldHandleNullIdWhenCheckingExistence() {
        // Given
        when(repositoryPort.findById(null)).thenReturn(Optional.empty());
        
        // When
        boolean result = templateQueryService.existsById(null);
        
        // Then
        assertFalse(result);
        
        verify(repositoryPort).findById(null);
    }
}
