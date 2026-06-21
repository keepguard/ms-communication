package com.keepguard.ms_communication;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.*;

class MsCommunicationApplicationTest {

    @Test
    @DisplayName("Deve executar o método main sem erros")
    void testMainMethod() {
        try (MockedStatic<SpringApplication> mockedSpringApplication = Mockito.mockStatic(SpringApplication.class)) {
            mockedSpringApplication.when(() -> SpringApplication.run(eq(MsCommunicationApplication.class), any(String[].class)))
                    .thenReturn(null);
            assertDoesNotThrow(() -> {
                MsCommunicationApplication.main(new String[]{});
            });
            mockedSpringApplication.verify(() ->
                    SpringApplication.run(eq(MsCommunicationApplication.class), any(String[].class))
            );
        }
    }

    @Test
    @DisplayName("Deve executar o método main com argumentos")
    void testMainMethodWithArguments() {
        try (MockedStatic<SpringApplication> mockedSpringApplication = Mockito.mockStatic(SpringApplication.class)) {
            mockedSpringApplication.when(() -> SpringApplication.run(eq(MsCommunicationApplication.class), any(String[].class)))
                    .thenReturn(null);
            String[] args = {"--spring.profiles.active=test", "--server.port=8082"};
            assertDoesNotThrow(() -> {
                MsCommunicationApplication.main(args);
            });
            mockedSpringApplication.verify(() ->
                    SpringApplication.run(MsCommunicationApplication.class, args)
            );
        }
    }

    @Test
    @DisplayName("Deve verificar se a classe possui a anotação @SpringBootApplication")
    void testSpringBootApplicationAnnotation() {
        assertTrue(MsCommunicationApplication.class.isAnnotationPresent(org.springframework.boot.autoconfigure.SpringBootApplication.class),
                "A classe MsCommunicationApplication deve possuir a anotação @SpringBootApplication");
    }

    @Test
    @DisplayName("Deve verificar se o método main é público e estático")
    void testMainMethodModifiers() throws NoSuchMethodException {
        java.lang.reflect.Method mainMethod = MsCommunicationApplication.class.getMethod("main", String[].class);
        assertTrue(java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers()),
                "O método main deve ser público");
        assertTrue(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers()),
                "O método main deve ser estático");
        assertEquals(void.class, mainMethod.getReturnType(),
                "O método main deve retornar void");
    }

    @Test
    @DisplayName("Deve verificar se a classe é pública")
    void testClassModifiers() {
        assertTrue(java.lang.reflect.Modifier.isPublic(MsCommunicationApplication.class.getModifiers()),
                "A classe MsCommunicationApplication deve ser pública");
    }

    @Test
    @DisplayName("Deve verificar se a classe não é abstrata")
    void testClassIsNotAbstract() {
        assertFalse(java.lang.reflect.Modifier.isAbstract(MsCommunicationApplication.class.getModifiers()),
                "A classe MsCommunicationApplication não deve ser abstrata");
    }

    @Test
    @DisplayName("Deve verificar se a classe não é interface")
    void testClassIsNotInterface() {
        assertFalse(MsCommunicationApplication.class.isInterface(),
                "MsCommunicationApplication deve ser uma classe, não uma interface");
    }

    @Test
    @DisplayName("Deve verificar se a classe está no pacote correto")
    void testPackageName() {
        assertEquals("com.keepguard.ms_communication", MsCommunicationApplication.class.getPackage().getName(),
                "A classe deve estar no pacote com.keepguard.ms_communication");
    }

    @Test
    @DisplayName("Deve verificar se a classe tem construtor padrão público")
    void testDefaultConstructor() throws NoSuchMethodException {
        java.lang.reflect.Constructor<?> constructor = MsCommunicationApplication.class.getConstructor();
        assertTrue(java.lang.reflect.Modifier.isPublic(constructor.getModifiers()),
                "O construtor padrão deve ser público");
    }

    @Test
    @DisplayName("Deve verificar se é possível instanciar a classe")
    void testClassInstantiation() {
        assertDoesNotThrow(() -> {
            MsCommunicationApplication instance = new MsCommunicationApplication();
            assertNotNull(instance, "Deve ser possível criar uma instância da classe");
        });
    }
}