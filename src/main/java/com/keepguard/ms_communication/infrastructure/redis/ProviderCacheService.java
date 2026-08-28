package com.keepguard.ms_communication.infrastructure.redis;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keepguard.ms_communication.application.dto.provider.ProviderCacheView;
import com.keepguard.ms_communication.application.port.out.cache.ProviderCachePort;
import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProviderCacheService implements ProviderCachePort {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${cache.redis.ttl.provider:604800}")
    private long providerTtlSeconds;

    @Value("${cache.redis.ttl.providers-by-type:604800}")
    private long providersByTypeTtlSeconds;

    @Value("${cache.redis.prefix.provider:provider_cache}")
    private String providerCachePrefix;

    @Value("${cache.redis.prefix.providers-by-type:providers_by_type}")
    private String providersByTypeCachePrefix;

    @CircuitBreaker(name = "redisCache")
    public void cacheProviderById(String providerId, ProviderCacheView provider) {
        try {
            String key = providerKey(providerId);
            String value = objectMapper.writeValueAsString(provider);
            redisTemplate.opsForValue().set(key, value, providerTtlSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Falha ao cachear provider por ID | key={}", providerId);
        }
    }

    @CircuitBreaker(name = "redisCache", fallbackMethod = "getProviderFallback")
    @Retry(name = "redisCache")
    public ProviderCacheView getProviderByIdFromCache(String providerId) {
        var key = providerKey(providerId);
        try {
            var value = redisTemplate.opsForValue().get(key);
            if (value == null || value.isBlank()) {
                return null;
            }
            return objectMapper.readValue(value, ProviderCacheView.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ProviderCacheView getProviderFallback(String providerId, Exception ex) {
        log.warn("FALLBACK: Redis indisponivel");
        return null;
    }

    @CircuitBreaker(name = "redisCache")
    public void removeProviderFromCacheById(String providerId) {
        try {
            String key = providerKey(providerId);
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.warn("Falha ao remover provider do cache por ID | key={}", providerId);
        }
    }

    @CircuitBreaker(name = "redisCache")
    public void cacheProvidersByType(CommunicationTypeEnum communicationType, List<ProviderCacheView> providers) {
        try {
            String key = providersByTypeKey(communicationType);
            String value = objectMapper.writeValueAsString(providers);
            redisTemplate.opsForValue().set(key, value, providersByTypeTtlSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Falha ao cachear providers por tipo | key={}", communicationType);
        }
    }

    @CircuitBreaker(name = "redisCache", fallbackMethod = "getProvidersListFallback")
    @Retry(name = "redisCache")
    public List<ProviderCacheView> getProvidersByTypeFromCache(CommunicationTypeEnum communicationType) {
        var key = providersByTypeKey(communicationType);
        try {
            var value = redisTemplate.opsForValue().get(key);
            if (value == null || value.isBlank()) {
                return null;
            }
            return objectMapper.readValue(value, new TypeReference<List<ProviderCacheView>>() {});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<ProviderCacheView> getProvidersListFallback(CommunicationTypeEnum communicationType, Exception ex) {
        log.warn("FALLBACK: Redis indisponivel");
        return null;
    }

    @CircuitBreaker(name = "redisCache")
    public void removeProvidersByTypeFromCache(CommunicationTypeEnum communicationType) {
        try {
            String key = providersByTypeKey(communicationType);
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.warn("Falha ao remover providers do cache por tipo | key={}", communicationType);
        }
    }

    @CircuitBreaker(name = "redisCache")
    public void clearAllProviderCache() {
        try {
            var providerPattern = providerBasePrefix() + ":*";
            var providersByTypePattern = providersByTypeBasePrefix() + ":*";
            
            var providerKeys = redisTemplate.keys(providerPattern);
            var providersByTypeKeys = redisTemplate.keys(providersByTypePattern);
            
            long deletedCount = 0;
            
            if (providerKeys != null && !providerKeys.isEmpty()) {
                deletedCount += redisTemplate.delete(providerKeys);
            }
            
            if (providersByTypeKeys != null && !providersByTypeKeys.isEmpty()) {
                deletedCount += redisTemplate.delete(providersByTypeKeys);
            }
            
            if (deletedCount > 0) {
                log.info("Cache de providers limpo com sucesso. {} chave(s) removida(s)", deletedCount);
            } else {
                log.info("Nenhuma chave de cache de providers encontrada para remover");
            }
        } catch (Exception e) {
            log.warn("Falha ao limpar cache de providers");
        }
    }

    private String providerBasePrefix() {
        if (providerCachePrefix == null || providerCachePrefix.isBlank()) {
            return "provider_cache";
        }
        return providerCachePrefix.replaceAll(":+$", "");
    }

    private String providersByTypeBasePrefix() {
        if (providersByTypeCachePrefix == null || providersByTypeCachePrefix.isBlank()) {
            return "providers_by_type";
        }
        return providersByTypeCachePrefix.replaceAll(":+$", "");
    }

    private String providerKey(String providerId) {
        return providerBasePrefix() + ":" + normalize(providerId);
    }

    private String providersByTypeKey(CommunicationTypeEnum communicationType) {
        return providersByTypeBasePrefix() + ":" + communicationType.name();
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

}
