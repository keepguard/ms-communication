package com.keepguard.ms_communication.adapters.out.feign;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BillingUserClientConfig {

    @Bean
    public Logger.Level billingUserClientLoggerLevel() {
        return Logger.Level.BASIC;
    }
}
