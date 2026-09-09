package com.keepguard.ms_communication.adapters.out.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;
import java.util.UUID;

@FeignClient(
        name = "billing-user-service",
        url = "${USER_SERVICE_URL:http://localhost:8085}",
        configuration = BillingUserClientConfig.class
)
public interface BillingUserClient {

    @GetMapping("/internal/v1/users/{id}")
    Map<String, Object> getUserById(
            @PathVariable("id") UUID id,
            @RequestHeader("X-Company-Id") UUID companyId
    );
}
