package com.keepguard.ms_communication.adapters.in.rest.health;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HealthControllerTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    private HealthController healthController;

    @BeforeEach
    void setUp() {
        healthController = new HealthController(dataSource);
    }

    @Test
    void healthCheck_WithHealthyDatabase_ShouldReturn200() throws Exception {
        // Given
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(2)).thenReturn(true);

        // When
        ResponseEntity<Map<String, Object>> response = healthController.healthCheck();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("ms-communication", body.get("service"));
        assertEquals("UP", body.get("status"));
        assertNotNull(body.get("timestamp"));
        
        @SuppressWarnings("unchecked")
        Map<String, String> components = (Map<String, String>) body.get("components");
        assertNotNull(components);
        assertEquals("UP", components.get("database"));
        
        verify(dataSource).getConnection();
        verify(connection).isValid(2);
        verify(connection).close();
    }

    @Test
    void healthCheck_WithUnhealthyDatabase_ShouldReturn503() throws Exception {
        // Given
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(2)).thenReturn(false);

        // When
        ResponseEntity<Map<String, Object>> response = healthController.healthCheck();

        // Then
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("ms-communication", body.get("service"));
        assertEquals("DOWN", body.get("status"));
        assertNotNull(body.get("timestamp"));
        
        @SuppressWarnings("unchecked")
        Map<String, String> components = (Map<String, String>) body.get("components");
        assertNotNull(components);
        assertEquals("DOWN", components.get("database"));
        
        verify(dataSource).getConnection();
        verify(connection).isValid(2);
        verify(connection).close();
    }

    @Test
    void healthCheck_WithDatabaseException_ShouldReturn503() throws Exception {
        // Given
        when(dataSource.getConnection()).thenThrow(new RuntimeException("Database connection failed"));

        // When
        ResponseEntity<Map<String, Object>> response = healthController.healthCheck();

        // Then
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("ms-communication", body.get("service"));
        assertEquals("DOWN", body.get("status"));
        assertNotNull(body.get("timestamp"));
        
        @SuppressWarnings("unchecked")
        Map<String, String> components = (Map<String, String>) body.get("components");
        assertNotNull(components);
        assertEquals("DOWN", components.get("database"));
        
        verify(dataSource).getConnection();
        verify(connection, never()).isValid(anyInt());
    }

    @Test
    void health_WithHealthyDatabase_ShouldReturnUpHealth() throws Exception {
        // Given
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(2)).thenReturn(true);

        // When
        Health health = healthController.health();

        // Then
        assertEquals(Health.up().build().getStatus(), health.getStatus());
        assertEquals("ms-communication", health.getDetails().get("service"));
        assertEquals("UP", health.getDetails().get("database"));
        
        verify(dataSource).getConnection();
        verify(connection).isValid(2);
        verify(connection).close();
    }

    @Test
    void health_WithUnhealthyDatabase_ShouldReturnDownHealth() throws Exception {
        // Given
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(2)).thenReturn(false);

        // When
        Health health = healthController.health();

        // Then
        assertEquals(Health.down().build().getStatus(), health.getStatus());
        assertEquals("ms-communication", health.getDetails().get("service"));
        assertEquals("DOWN", health.getDetails().get("database"));
        
        verify(dataSource).getConnection();
        verify(connection).isValid(2);
        verify(connection).close();
    }

    @Test
    void health_WithDatabaseException_ShouldReturnDownHealth() throws Exception {
        // Given
        when(dataSource.getConnection()).thenThrow(new RuntimeException("Database connection failed"));

        // When
        Health health = healthController.health();

        // Then
        assertEquals(Health.down().build().getStatus(), health.getStatus());
        assertEquals("ms-communication", health.getDetails().get("service"));
        assertEquals("DOWN", health.getDetails().get("database"));
        
        verify(dataSource).getConnection();
        verify(connection, never()).isValid(anyInt());
    }
}
