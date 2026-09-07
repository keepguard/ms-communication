package com.keepguard.ms_communication.adapters.out.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;

/**
 * Cliente Feign para webhooks N8N com URL dinâmica por tenant.
 * O {@link URI} no primeiro argumento sobrescreve o {@code url} do {@code @FeignClient}.
 */
@FeignClient(
        name = "n8n-webhook",
        url = "http://n8n-placeholder.local",
        configuration = N8nWebhookClientConfig.class
)
public interface N8nWebhookClient {

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> post(URI uri, @RequestBody Object body);
}
