package com.keepguard.ms_communication.infrastructure.context;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CorrelationContext {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    public static final String CORRELATION_ID_MDC_KEY = "correlationId";

    public String getCorrelationId() {
        return UUID.randomUUID().toString();
    }

    public void setCorrelationId(String correlationId) {
        MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
    }

    public String getCurrentCorrelationId() {
        return MDC.get(CORRELATION_ID_MDC_KEY);
    }

    public void clearCorrelationId() {
        MDC.remove(CORRELATION_ID_MDC_KEY);
    }
}