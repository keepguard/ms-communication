package com.keepguard.ms_communication.infrastructure.persistence;

import com.keepguard.ms_communication.application.dto.common.PageResultView;
import com.keepguard.ms_communication.application.dto.template.TemplateSearchCriteriaView;
import com.keepguard.ms_communication.domain.entity.Template;
import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import com.keepguard.ms_communication.infrastructure.persistence.entity.TemplateJpaEntity;
import com.keepguard.ms_communication.infrastructure.persistence.mapper.TemplateJpaMapper;
import com.keepguard.ms_communication.infrastructure.persistence.spring.TemplateSpringRepository;
import com.keepguard.ms_communication.test.builder.TemplateTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para TemplateRepositoryAdapter
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Template Repository Adapter Tests")
class TemplateRepositoryAdapterTest {

    @InjectMocks
    private TemplateRepositoryAdapter templateRepositoryAdapter;
    
    @Mock
    private TemplateSpringRepository springRepository;
    
    @Mock
    private TemplateJpaMapper mapper;
    
    private Template template;
    private TemplateJpaEntity templateJpaEntity;
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
        
        templateJpaEntity = TemplateJpaEntity.builder()
            .id(templateId)
            .name("Test Template")
            .templateType(TemplateTypeEnum.CADASTRO_SUCESSO)
            .messageType(MessageTypeEnum.EMAIL)
            .isActive(true)
            .build();
    }
    
    @Test
    @DisplayName("Deve salvar template com sucesso")
    void shouldSaveTemplateSuccessfully() {
        // Given
        when(mapper.toEntity(template)).thenReturn(templateJpaEntity);
        when(springRepository.save(templateJpaEntity)).thenReturn(templateJpaEntity);
        when(mapper.toDomain(templateJpaEntity)).thenReturn(template);
        
        // When
        Template result = templateRepositoryAdapter.save(template);
        
        // Then
        assertNotNull(result);
        assertEquals(template, result);
        
        verify(mapper).toEntity(template);
        verify(springRepository).save(templateJpaEntity);
        verify(mapper).toDomain(templateJpaEntity);
    }
    
    @Test
    @DisplayName("Deve encontrar template por ID com sucesso")
    void shouldFindTemplateByIdSuccessfully() {
        // Given
        when(springRepository.findById(templateId)).thenReturn(Optional.of(templateJpaEntity));
        when(mapper.toDomain(templateJpaEntity)).thenReturn(template);
        
        // When
        Optional<Template> result = templateRepositoryAdapter.findById(templateId);
        
        // Then
        assertTrue(result.isPresent());
        assertEquals(template, result.get());
        
        verify(springRepository).findById(templateId);
        verify(mapper).toDomain(templateJpaEntity);
    }
    
    @Test
    @DisplayName("Deve retornar Optional vazio quando template não encontrado por ID")
    void shouldReturnEmptyOptionalWhenTemplateNotFoundById() {
        // Given
        when(springRepository.findById(templateId)).thenReturn(Optional.empty());
        
        // When
        Optional<Template> result = templateRepositoryAdapter.findById(templateId);
        
        // Then
        assertFalse(result.isPresent());
        
        verify(springRepository).findById(templateId);
        verify(mapper, never()).toDomain(any());
    }
    
    @Test
    @DisplayName("Deve buscar templates com critérios de busca com sucesso")
    void shouldSearchTemplatesWithCriteriaSuccessfully() {
        // Given
        TemplateSearchCriteriaView criteria = new TemplateSearchCriteriaView(
            0, 10, "name", "ASC", "Test", MessageTypeEnum.EMAIL, 
            TemplateTypeEnum.CADASTRO_SUCESSO, true
        );
        
        Page<TemplateJpaEntity> page = new PageImpl<>(List.of(templateJpaEntity));
        when(springRepository.findWithFilters(
            eq("Test"), eq("EMAIL"), eq("CADASTRO_SUCESSO"), eq(true), any(Pageable.class)
        )).thenReturn(page);
        when(mapper.toDomain(templateJpaEntity)).thenReturn(template);
        
        // When
        PageResultView<Template> result = templateRepositoryAdapter.search(criteria);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.content().size());
        assertEquals(template, result.content().get(0));
        assertEquals(0, result.page());
        assertEquals(1, result.size());
        assertEquals(1, result.totalElements());
        
        verify(springRepository).findWithFilters(
            eq("Test"), eq("EMAIL"), eq("CADASTRO_SUCESSO"), eq(true), any(Pageable.class)
        );
        verify(mapper).toDomain(templateJpaEntity);
    }
    
    @Test
    @DisplayName("Deve buscar templates com critérios nulos")
    void shouldSearchTemplatesWithNullCriteria() {
        // Given
        TemplateSearchCriteriaView criteria = new TemplateSearchCriteriaView(
            0, 10, "name", "ASC", null, null, null, null
        );
        
        Page<TemplateJpaEntity> page = new PageImpl<>(List.of(templateJpaEntity));
        when(springRepository.findWithFilters(
            isNull(), isNull(), isNull(), isNull(), any(Pageable.class)
        )).thenReturn(page);
        when(mapper.toDomain(templateJpaEntity)).thenReturn(template);
        
        // When
        PageResultView<Template> result = templateRepositoryAdapter.search(criteria);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.content().size());
        
        verify(springRepository).findWithFilters(
            isNull(), isNull(), isNull(), isNull(), any(Pageable.class)
        );
    }
    
    @Test
    @DisplayName("Deve encontrar todos os templates ativos")
    void shouldFindAllActiveTemplates() {
        // Given
        when(springRepository.findAllActiveTemplates()).thenReturn(List.of(templateJpaEntity));
        when(mapper.toDomain(templateJpaEntity)).thenReturn(template);
        
        // When
        List<Template> result = templateRepositoryAdapter.findAllActive();
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(template, result.get(0));
        
        verify(springRepository).findAllActiveTemplates();
        verify(mapper).toDomain(templateJpaEntity);
    }
    
    @Test
    @DisplayName("Deve encontrar templates por tipo")
    void shouldFindTemplatesByType() {
        // Given
        TemplateTypeEnum type = TemplateTypeEnum.CADASTRO_SUCESSO;
        when(springRepository.findActiveTemplatesByType(type))
            .thenReturn(List.of(templateJpaEntity));
        when(mapper.toDomain(templateJpaEntity)).thenReturn(template);
        
        // When
        List<Template> result = templateRepositoryAdapter.findByType(type);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(template, result.get(0));
        
        verify(springRepository).findActiveTemplatesByType(type);
        verify(mapper).toDomain(templateJpaEntity);
    }
    
    @Test
    @DisplayName("Deve encontrar templates por tipo de mensagem")
    void shouldFindTemplatesByMessageType() {
        // Given
        MessageTypeEnum messageType = MessageTypeEnum.EMAIL;
        when(springRepository.findActiveTemplatesByMessageType(messageType))
            .thenReturn(List.of(templateJpaEntity));
        when(mapper.toDomain(templateJpaEntity)).thenReturn(template);
        
        // When
        List<Template> result = templateRepositoryAdapter.findByMessageType(messageType);
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(template, result.get(0));
        
        verify(springRepository).findActiveTemplatesByMessageType(messageType);
        verify(mapper).toDomain(templateJpaEntity);
    }
    
    @Test
    @DisplayName("Deve deletar template por ID")
    void shouldDeleteTemplateById() {
        // Given
        doNothing().when(springRepository).deleteById(templateId);
        
        // When
        templateRepositoryAdapter.deleteById(templateId);
        
        // Then
        verify(springRepository).deleteById(templateId);
    }
    
    @Test
    @DisplayName("Deve verificar se existe template por nome")
    void shouldCheckIfTemplateExistsByName() {
        // Given
        String name = "Test Template";
        when(springRepository.existsByName(name)).thenReturn(true);
        
        // When
        boolean result = templateRepositoryAdapter.existsByName(name);
        
        // Then
        assertTrue(result);
        verify(springRepository).existsByName(name);
    }
    
    @Test
    @DisplayName("Deve verificar se existe template por nome e ID diferente")
    void shouldCheckIfTemplateExistsByNameAndIdNot() {
        // Given
        String name = "Test Template";
        UUID differentId = UUID.randomUUID();
        when(springRepository.existsByNameAndIdNot(name, differentId)).thenReturn(true);
        
        // When
        boolean result = templateRepositoryAdapter.existsByNameAndIdNot(name, differentId);
        
        // Then
        assertTrue(result);
        verify(springRepository).existsByNameAndIdNot(name, differentId);
    }
    
    @Test
    @DisplayName("Deve retornar false quando template não existe por nome")
    void shouldReturnFalseWhenTemplateNotExistsByName() {
        // Given
        String name = "Non Existent Template";
        when(springRepository.existsByName(name)).thenReturn(false);
        
        // When
        boolean result = templateRepositoryAdapter.existsByName(name);
        
        // Then
        assertFalse(result);
        verify(springRepository).existsByName(name);
    }
    
    @Test
    @DisplayName("Deve retornar false quando template não existe por nome e ID diferente")
    void shouldReturnFalseWhenTemplateNotExistsByNameAndIdNot() {
        // Given
        String name = "Non Existent Template";
        UUID differentId = UUID.randomUUID();
        when(springRepository.existsByNameAndIdNot(name, differentId)).thenReturn(false);
        
        // When
        boolean result = templateRepositoryAdapter.existsByNameAndIdNot(name, differentId);
        
        // Then
        assertFalse(result);
        verify(springRepository).existsByNameAndIdNot(name, differentId);
    }
    
    @Test
    @DisplayName("Deve buscar templates com página vazia")
    void shouldSearchTemplatesWithEmptyPage() {
        // Given
        TemplateSearchCriteriaView criteria = new TemplateSearchCriteriaView(
            0, 10, "name", "ASC", "NonExistent", null, null, null
        );
        
        Page<TemplateJpaEntity> emptyPage = new PageImpl<>(List.of());
        when(springRepository.findWithFilters(
            eq("NonExistent"), isNull(), isNull(), isNull(), any(Pageable.class)
        )).thenReturn(emptyPage);
        
        // When
        PageResultView<Template> result = templateRepositoryAdapter.search(criteria);
        
        // Then
        assertNotNull(result);
        assertTrue(result.content().isEmpty());
        assertEquals(0, result.totalElements());
        
        verify(springRepository).findWithFilters(
            eq("NonExistent"), isNull(), isNull(), isNull(), any(Pageable.class)
        );
        verify(mapper, never()).toDomain(any());
    }
    
    @Test
    @DisplayName("Deve buscar templates com diferentes tipos de ordenação")
    void shouldSearchTemplatesWithDifferentSortOrders() {
        // Given
        TemplateSearchCriteriaView criteriaAsc = new TemplateSearchCriteriaView(
            0, 10, "name", "ASC", null, null, null, null
        );
        TemplateSearchCriteriaView criteriaDesc = new TemplateSearchCriteriaView(
            0, 10, "name", "DESC", null, null, null, null
        );
        
        Page<TemplateJpaEntity> page = new PageImpl<>(List.of(templateJpaEntity));
        when(springRepository.findWithFilters(
            any(), any(), any(), any(), any(Pageable.class)
        )).thenReturn(page);
        when(mapper.toDomain(templateJpaEntity)).thenReturn(template);
        
        // When
        PageResultView<Template> resultAsc = templateRepositoryAdapter.search(criteriaAsc);
        PageResultView<Template> resultDesc = templateRepositoryAdapter.search(criteriaDesc);
        
        // Then
        assertNotNull(resultAsc);
        assertNotNull(resultDesc);
        assertEquals(1, resultAsc.content().size());
        assertEquals(1, resultDesc.content().size());
        
        verify(springRepository, times(2)).findWithFilters(
            any(), any(), any(), any(), any(Pageable.class)
        );
    }
    
    @Test
    @DisplayName("Deve encontrar templates com diferentes tipos")
    void shouldFindTemplatesWithDifferentTypes() {
        // Given
        TemplateTypeEnum[] types = {
            TemplateTypeEnum.CADASTRO_SUCESSO,
            TemplateTypeEnum.RECUPERACAO_SENHA,
            TemplateTypeEnum.NOTIFICACAO_GERAL,
            TemplateTypeEnum.ALERTA_SEGURANCA,
            TemplateTypeEnum.CONFIRMACAO_ACAO
        };
        
        for (TemplateTypeEnum type : types) {
            when(springRepository.findActiveTemplatesByType(type))
                .thenReturn(List.of(templateJpaEntity));
            when(mapper.toDomain(templateJpaEntity)).thenReturn(template);
            
            // When
            List<Template> result = templateRepositoryAdapter.findByType(type);
            
            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
        }
    }
    
    @Test
    @DisplayName("Deve encontrar templates com diferentes tipos de mensagem")
    void shouldFindTemplatesWithDifferentMessageTypes() {
        // Given
        MessageTypeEnum[] messageTypes = {
            MessageTypeEnum.EMAIL,
            MessageTypeEnum.SMS,
            MessageTypeEnum.WHATSAPP,
            MessageTypeEnum.PUSH
        };
        
        for (MessageTypeEnum messageType : messageTypes) {
            when(springRepository.findActiveTemplatesByMessageType(messageType))
                .thenReturn(List.of(templateJpaEntity));
            when(mapper.toDomain(templateJpaEntity)).thenReturn(template);
            
            // When
            List<Template> result = templateRepositoryAdapter.findByMessageType(messageType);
            
            // Then
            assertNotNull(result);
            assertEquals(1, result.size());
        }
    }
}
