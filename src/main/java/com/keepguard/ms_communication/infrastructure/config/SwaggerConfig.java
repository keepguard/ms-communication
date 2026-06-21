package com.keepguard.ms_communication.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("KeepGuard Communication API")
                        .version("1.0.0")
                        .description("API para gerenciamento de templates e envio de mensagens para múltiplas aplicações. " +
                                   "Esta API fornece funcionalidades de envio de mensagens (email, SMS, WhatsApp, push), " +
                                   "gerenciamento de templates personalizados por aplicação e configuração de provedores de comunicação.")
                        .contact(new Contact()
                                .name("KeepGuard Team")
                                .email("suporte@keepguard.com")
                                .url("https://keepguard.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8082")
                                .description("Servidor Local")
                ));
    }
}