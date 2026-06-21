package com.keepguard.ms_communication.domain.entity;

import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.domain.enums.ProviderTypeEnum;
import com.keepguard.ms_communication.test.builder.ProviderTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para a entidade Provider
 */
class ProviderTest {
    
    private Provider provider;
    private UUID providerId;
    
    @BeforeEach
    void setUp() {
        providerId = UUID.randomUUID();
        
        provider = ProviderTestBuilder.aProvider()
            .withName("Test Provider")
            .asN8N()
            .withUrl("https://api.example.com")
            .withConfiguration("apiKey", "test-api-key")
            .buildDomain();
    }
    
    @Test
    @DisplayName("Deve criar provider com dados válidos")
    void shouldCreateProviderWithValidData() {
        // Then
        assertEquals("N8N Provider", provider.getName());
        assertEquals(ProviderTypeEnum.N8N, provider.getProviderType());
        assertEquals(CommunicationTypeEnum.SMS, provider.getCommunicationType());
        assertEquals("https://api.example.com", provider.getUrl());
        assertTrue(provider.getConfiguration().contains("test-api-key"));
        assertTrue(provider.getIsActive());
        assertFalse(provider.getIsDefault());
        assertEquals(Integer.valueOf(1), provider.getPriority());
        assertEquals(Integer.valueOf(3), provider.getMaxRetries());
        assertEquals(Integer.valueOf(30), provider.getTimeoutSeconds());
    }
    
    @Test
    @DisplayName("Deve criar provider com ID específico")
    void shouldCreateProviderWithSpecificId() {
        // Given & When
        Provider provider = new Provider(
            providerId,
            "Test Provider",
            ProviderTypeEnum.N8N,
            CommunicationTypeEnum.EMAIL,
            true,
            false,
            1,
            "https://api.example.com",
            "{\"apiKey\": \"test-api-key\"}",
            3,
            30,
            null,
            null,
            null,
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        // Then
        assertEquals(providerId, provider.getId());
    }
    
    @Test
    @DisplayName("Deve criar provider N8N")
    void shouldCreateN8NProvider() {
        // Given & When
        Provider n8nProvider = Provider.create(
            "N8N Provider",
            ProviderTypeEnum.N8N,
            CommunicationTypeEnum.EMAIL,
            "https://n8n.example.com",
            "{\"url\": \"https://n8n.example.com\", \"apiKey\": \"n8n-api-key\"}"
        );
        
        // Then
        assertEquals("N8N Provider", n8nProvider.getName());
        assertEquals(ProviderTypeEnum.N8N, n8nProvider.getProviderType());
        assertEquals(CommunicationTypeEnum.EMAIL, n8nProvider.getCommunicationType());
        assertEquals("https://n8n.example.com", n8nProvider.getUrl());
        assertTrue(n8nProvider.getConfiguration().contains("n8n-api-key"));
    }
    
    @Test
    @DisplayName("Deve criar provider SendGrid")
    void shouldCreateSendGridProvider() {
        // Given & When
        Provider sendGridProvider = Provider.create(
            "SendGrid Provider",
            ProviderTypeEnum.SENDGRID,
            CommunicationTypeEnum.EMAIL,
            "https://api.sendgrid.com",
            "{\"apiKey\": \"sendgrid-api-key\", \"fromEmail\": \"noreply@example.com\"}"
        );
        
        // Then
        assertEquals("SendGrid Provider", sendGridProvider.getName());
        assertEquals(ProviderTypeEnum.SENDGRID, sendGridProvider.getProviderType());
        assertEquals(CommunicationTypeEnum.EMAIL, sendGridProvider.getCommunicationType());
        assertEquals("https://api.sendgrid.com", sendGridProvider.getUrl());
        assertTrue(sendGridProvider.getConfiguration().contains("sendgrid-api-key"));
    }
    
    @Test
    @DisplayName("Deve ativar provider")
    void shouldActivateProvider() {
        // Given
        provider.deactivate();
        assertFalse(provider.getIsActive());
        
        // When
        provider.activate();
        
        // Then
        assertTrue(provider.getIsActive());
    }
    
    @Test
    @DisplayName("Deve desativar provider")
    void shouldDeactivateProvider() {
        // Given - provider ativo por padrão
        assertTrue(provider.getIsActive());
        
        // When
        provider.deactivate();
        
        // Then
        assertFalse(provider.getIsActive());
    }
    
    @Test
    @DisplayName("Deve verificar se provider está ativo")
    void shouldCheckIfProviderIsActive() {
        // Given - provider ativo por padrão
        assertTrue(provider.isActive());
        
        // When
        provider.deactivate();
        
        // Then
        assertFalse(provider.isActive());
    }
    
    @Test
    @DisplayName("Deve definir provider como padrão")
    void shouldSetProviderAsDefault() {
        // Given - provider não é padrão por padrão
        assertFalse(provider.getIsDefault());
        
        // When
        provider.setAsDefault();
        
        // Then
        assertTrue(provider.getIsDefault());
    }
    
    @Test
    @DisplayName("Deve remover provider como padrão")
    void shouldUnsetProviderAsDefault() {
        // Given
        provider.setAsDefault();
        assertTrue(provider.getIsDefault());
        
        // When
        provider.unsetAsDefault();
        
        // Then
        assertFalse(provider.getIsDefault());
    }
    
    @Test
    @DisplayName("Deve verificar se provider é padrão")
    void shouldCheckIfProviderIsDefault() {
        // Given - provider não é padrão por padrão
        assertFalse(provider.isDefault());
        
        // When
        provider.setAsDefault();
        
        // Then
        assertTrue(provider.isDefault());
    }
    
    @Test
    @DisplayName("Deve atualizar prioridade do provider")
    void shouldUpdateProviderPriority() {
        // Given
        Integer newPriority = 5;
        
        // When
        provider.updatePriority(newPriority);
        
        // Then
        assertEquals(newPriority, provider.getPriority());
    }
    
    @Test
    @DisplayName("Deve ignorar prioridade inválida")
    void shouldIgnoreInvalidPriority() {
        // Given
        Integer originalPriority = provider.getPriority();
        Integer invalidPriority = -1;
        
        // When
        provider.updatePriority(invalidPriority);
        
        // Then
        assertEquals(originalPriority, provider.getPriority());
    }
    
    @Test
    @DisplayName("Deve ignorar prioridade nula")
    void shouldIgnoreNullPriority() {
        // Given
        Integer originalPriority = provider.getPriority();
        
        // When
        provider.updatePriority(null);
        
        // Then
        assertEquals(originalPriority, provider.getPriority());
    }
    
    @Test
    @DisplayName("Deve implementar equals corretamente")
    void shouldImplementEqualsCorrectly() {
        Provider provider1 = Provider.create(
            "Test Provider", ProviderTypeEnum.N8N, CommunicationTypeEnum.EMAIL,
            "https://api.example.com", "{\"apiKey\": \"test-api-key\"}"
        );
        
        Provider provider2 = Provider.create(
            "Test Provider", ProviderTypeEnum.N8N, CommunicationTypeEnum.EMAIL,
            "https://api.example.com", "{\"apiKey\": \"test-api-key\"}"
        );
        
        Provider provider3 = Provider.create(
            "Other Provider", ProviderTypeEnum.SENDGRID, CommunicationTypeEnum.SMS,
            "https://api.other.com", "{\"apiKey\": \"other-key\"}"
        );
        
        assertEquals(provider1, provider1);
        assertNotEquals(provider1, provider2); // IDs diferentes
        assertNotEquals(provider1, provider3);
        assertNotEquals(provider1, null);
        assertNotEquals(provider1, "not a provider");
    }
    
    @Test
    @DisplayName("Deve implementar hashCode corretamente")
    void shouldImplementHashCodeCorrectly() {
        Provider provider1 = Provider.create(
            "Test Provider", ProviderTypeEnum.N8N, CommunicationTypeEnum.EMAIL,
            "https://api.example.com", "{\"apiKey\": \"test-api-key\"}"
        );
        
        Provider provider2 = Provider.create(
            "Test Provider", ProviderTypeEnum.N8N, CommunicationTypeEnum.EMAIL,
            "https://api.example.com", "{\"apiKey\": \"test-api-key\"}"
        );
        
        // HashCodes devem ser diferentes para IDs diferentes
        assertNotEquals(provider1.hashCode(), provider2.hashCode());
    }
    
    @Test
    @DisplayName("Deve implementar toString corretamente")
    void shouldImplementToStringCorrectly() {
        String toString = provider.toString();
        
        assertTrue(toString.contains("Provider"));
        assertTrue(toString.contains("name='N8N Provider'"));
        assertTrue(toString.contains("providerType=N8N"));
        assertTrue(toString.contains("isActive=true"));
    }
}