package com.keepguard.ms_communication.adapters.out.feign;

import feign.Logger;
import feign.Request;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

public class N8nWebhookClientConfig {

    @Bean
    public Logger.Level n8nWebhookLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    public Request.Options n8nWebhookRequestOptions() {
        return new Request.Options(Duration.ofSeconds(10), Duration.ofSeconds(30), true);
    }
}
