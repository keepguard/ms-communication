package com.keepguard.ms_communication.infrastructure.context;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para CorrelationContext
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Correlation Context Tests")
class CorrelationContextTest {
    
    @InjectMocks
    private CorrelationContext correlationContext;
    
    private String testCorrelationId;
    
    @BeforeEach
    void setUp() {
        // Limpar MDC antes de cada teste
        MDC.clear();
        testCorrelationId = "test-correlation-id-123";
    }
    
    @AfterEach
    void tearDown() {
        // Limpar MDC após cada teste
        MDC.clear();
    }
    
    @Test
    @DisplayName("Deve gerar novo Correlation ID")
    void shouldGenerateNewCorrelationId() {
        // When
        String correlationId1 = correlationContext.getCorrelationId();
        String correlationId2 = correlationContext.getCorrelationId();
        
        // Then
        assertNotNull(correlationId1);
        assertNotNull(correlationId2);
        assertEquals(correlationId1, correlationId2);
        assertTrue(correlationId1.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
        assertTrue(correlationId2.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
    }
    
    @Test
    @DisplayName("Deve definir Correlation ID no contexto MDC")
    void shouldSetCorrelationIdInMDCContext() {
        // When
        correlationContext.setCorrelationId(testCorrelationId);
        
        // Then
        assertEquals(testCorrelationId, MDC.get(CorrelationContext.CORRELATION_ID_MDC_KEY));
        assertEquals(testCorrelationId, correlationContext.getCurrentCorrelationId());
    }
    
    @Test
    @DisplayName("Deve obter Correlation ID atual do contexto MDC")
    void shouldGetCurrentCorrelationIdFromMDCContext() {
        // Given
        MDC.put(CorrelationContext.CORRELATION_ID_MDC_KEY, testCorrelationId);
        
        // When
        String currentCorrelationId = correlationContext.getCurrentCorrelationId();
        
        // Then
        assertEquals(testCorrelationId, currentCorrelationId);
        assertEquals(testCorrelationId, MDC.get(CorrelationContext.CORRELATION_ID_MDC_KEY));
    }
    
    @Test
    @DisplayName("Deve retornar null quando Correlation ID não está definido no MDC")
    void shouldReturnNullWhenCorrelationIdNotSetInMDC() {
        // Given - MDC está limpo
        
        // When
        String currentCorrelationId = correlationContext.getCurrentCorrelationId();
        
        // Then
        assertNull(currentCorrelationId);
        assertNull(MDC.get(CorrelationContext.CORRELATION_ID_MDC_KEY));
    }
    
    @Test
    @DisplayName("Deve limpar Correlation ID do contexto MDC")
    void shouldClearCorrelationIdFromMDCContext() {
        // Given
        MDC.put(CorrelationContext.CORRELATION_ID_MDC_KEY, testCorrelationId);
        assertEquals(testCorrelationId, MDC.get(CorrelationContext.CORRELATION_ID_MDC_KEY));
        
        // When
        correlationContext.clearCorrelationId();
        
        // Then
        assertNull(MDC.get(CorrelationContext.CORRELATION_ID_MDC_KEY));
        assertNull(correlationContext.getCurrentCorrelationId());
    }
    
    @Test
    @DisplayName("Deve atualizar Correlation ID no contexto MDC")
    void shouldUpdateCorrelationIdInMDCContext() {
        // Given
        String newCorrelationId = "new-correlation-id-456";
        correlationContext.setCorrelationId(testCorrelationId);
        assertEquals(testCorrelationId, MDC.get(CorrelationContext.CORRELATION_ID_MDC_KEY));
        
        // When
        correlationContext.setCorrelationId(newCorrelationId);
        
        // Then
        assertEquals(newCorrelationId, MDC.get(CorrelationContext.CORRELATION_ID_MDC_KEY));
        assertEquals(newCorrelationId, correlationContext.getCurrentCorrelationId());
    }
    
    @Test
    @DisplayName("Deve definir Correlation ID com valor nulo")
    void shouldSetCorrelationIdWithNullValue() {
        // When
        correlationContext.setCorrelationId(null);
        
        // Then
        assertNull(MDC.get(CorrelationContext.CORRELATION_ID_MDC_KEY));
        assertNull(correlationContext.getCurrentCorrelationId());
    }
    
    @Test
    @DisplayName("Deve definir Correlation ID com valor vazio")
    void shouldSetCorrelationIdWithEmptyValue() {
        // When
        correlationContext.setCorrelationId("");
        
        // Then
        assertEquals("", MDC.get(CorrelationContext.CORRELATION_ID_MDC_KEY));
        assertEquals("", correlationContext.getCurrentCorrelationId());
    }
    
    @Test
    @DisplayName("Deve definir Correlation ID com valor contendo espaços")
    void shouldSetCorrelationIdWithValueContainingSpaces() {
        // Given
        String correlationIdWithSpaces = "  test-correlation-id  ";
        
        // When
        correlationContext.setCorrelationId(correlationIdWithSpaces);
        
        // Then
        assertEquals(correlationIdWithSpaces, MDC.get(CorrelationContext.CORRELATION_ID_MDC_KEY));
        assertEquals(correlationIdWithSpaces, correlationContext.getCurrentCorrelationId());
    }
    
    @Test
    @DisplayName("Deve gerar Correlation IDs únicos")
    void shouldGenerateUniqueCorrelationIds() {
        // When
        String[] correlationIds = new String[100];
        for (int i = 0; i < 100; i++) {
            MDC.clear();
            correlationIds[i] = correlationContext.getCorrelationId();
        }
        
        // Then
        for (int i = 0; i < correlationIds.length; i++) {
            for (int j = i + 1; j < correlationIds.length; j++) {
                assertNotEquals(correlationIds[i], correlationIds[j], 
                    "Correlation IDs should be unique");
            }
        }
    }
    
    @Test
    @DisplayName("Deve manter Correlation ID após múltiplas operações")
    void shouldMaintainCorrelationIdAfterMultipleOperations() {
        // Given
        String correlationId1 = correlationContext.getCorrelationId();
        String correlationId2 = correlationContext.getCorrelationId();
        
        // When
        correlationContext.setCorrelationId(correlationId1);
        String currentId1 = correlationContext.getCurrentCorrelationId();
        
        correlationContext.setCorrelationId(correlationId2);
        String currentId2 = correlationContext.getCurrentCorrelationId();
        
        // Then
        assertEquals(correlationId1, currentId1);
        assertEquals(correlationId2, currentId2);
        assertEquals(correlationId2, MDC.get(CorrelationContext.CORRELATION_ID_MDC_KEY));
    }
    
    @Test
    @DisplayName("Deve limpar Correlation ID mesmo quando não estava definido")
    void shouldClearCorrelationIdEvenWhenNotSet() {
        // Given - MDC está limpo
        assertNull(MDC.get(CorrelationContext.CORRELATION_ID_MDC_KEY));
        
        // When
        correlationContext.clearCorrelationId();
        
        // Then
        assertNull(MDC.get(CorrelationContext.CORRELATION_ID_MDC_KEY));
        assertNull(correlationContext.getCurrentCorrelationId());
    }
    
    @Test
    @DisplayName("Deve definir e limpar Correlation ID múltiplas vezes")
    void shouldSetAndClearCorrelationIdMultipleTimes() {
        // Given
        String correlationId1 = "correlation-1";
        String correlationId2 = "correlation-2";
        String correlationId3 = "correlation-3";
        
        // When & Then - Primeira vez
        correlationContext.setCorrelationId(correlationId1);
        assertEquals(correlationId1, correlationContext.getCurrentCorrelationId());
        
        correlationContext.clearCorrelationId();
        assertNull(correlationContext.getCurrentCorrelationId());
        
        // When & Then - Segunda vez
        correlationContext.setCorrelationId(correlationId2);
        assertEquals(correlationId2, correlationContext.getCurrentCorrelationId());
        
        correlationContext.clearCorrelationId();
        assertNull(correlationContext.getCurrentCorrelationId());
        
        // When & Then - Terceira vez
        correlationContext.setCorrelationId(correlationId3);
        assertEquals(correlationId3, correlationContext.getCurrentCorrelationId());
        
        correlationContext.clearCorrelationId();
        assertNull(correlationContext.getCurrentCorrelationId());
    }
    
    @Test
    @DisplayName("Deve verificar constantes do CorrelationContext")
    void shouldVerifyCorrelationContextConstants() {
        // Then
        assertEquals("X-Correlation-ID", CorrelationContext.CORRELATION_ID_HEADER);
        assertEquals("correlationId", CorrelationContext.CORRELATION_ID_MDC_KEY);
    }
    
    @Test
    @DisplayName("Deve trabalhar com Correlation ID de diferentes formatos")
    void shouldWorkWithCorrelationIdOfDifferentFormats() {
        // Given
        String[] differentFormats = {
            "simple-id",
            "123456789",
            "abc-def-ghi",
            "correlation-id-with-special-chars-!@#$%",
            "correlation-id-with-very-long-name-that-exceeds-normal-length-limits-and-should-still-work",
            "CORRELATION-ID-UPPERCASE",
            "Correlation-Id-Mixed-Case"
        };
        
        // When & Then
        for (String format : differentFormats) {
            correlationContext.setCorrelationId(format);
            assertEquals(format, correlationContext.getCurrentCorrelationId());
            assertEquals(format, MDC.get(CorrelationContext.CORRELATION_ID_MDC_KEY));
            
            correlationContext.clearCorrelationId();
            assertNull(correlationContext.getCurrentCorrelationId());
        }
    }
}
