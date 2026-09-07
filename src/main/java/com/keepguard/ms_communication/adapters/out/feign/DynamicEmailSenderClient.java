package com.keepguard.ms_communication.adapters.out.feign;

import com.keepguard.ms_communication.adapters.out.email.dto.EmailSendRequestDTO;
import com.keepguard.ms_communication.adapters.out.email.dto.EmailSendResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.net.URI;

/**
 * Cliente Feign para sender de e-mail HTTP dinâmico (URL por provedor).
 * O {@link URI} no primeiro argumento deve incluir o path ({@code /send/mail} ou {@code /health}).
 */
@FeignClient(
        name = "dynamic-email-sender",
        url = "http://email-placeholder.local",
        configuration = DynamicEmailSenderClientConfig.class
)
public interface DynamicEmailSenderClient {

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    EmailSendResponseDTO sendMail(
            URI uri,
            @RequestBody EmailSendRequestDTO body,
            @RequestHeader("User-Agent") String userAgent,
            @RequestHeader("Accept") String accept);

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    String health(URI uri, @RequestHeader("Accept") String accept);
}
