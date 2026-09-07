package com.keepguard.ms_communication.adapters.out.feign;

import feign.Logger;
import feign.Request;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

public class DynamicEmailSenderClientConfig {

    @Bean
    public Logger.Level dynamicEmailSenderLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean
    public Request.Options dynamicEmailSenderRequestOptions() {
        return new Request.Options(Duration.ofSeconds(10), Duration.ofSeconds(30), true);
    }
}
