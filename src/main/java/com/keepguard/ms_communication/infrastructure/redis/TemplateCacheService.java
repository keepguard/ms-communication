package com.keepguard.ms_communication.infrastructure.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keepguard.ms_communication.application.dto.template.TemplateCacheView;
import com.keepguard.ms_communication.application.port.out.cache.TemplateCachePort;
import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateCacheService implements TemplateCachePort {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${cache.redis.ttl.template:604800}")
    private long templateTtlSeconds;

    @Value("${cache.redis.prefix.template:template_cache}")
    private String templateCachePrefix;

    @CircuitBreaker(name = "redisCache")
    public void cacheTemplate(TemplateTypeEnum templateType, MessageTypeEnum messageType, String application, TemplateCacheView template) {
        try {
            String key = buildTemplateKey(templateType, messageType, application);
            String value = objectMapper.writeValueAsString(template);
            redisTemplate.opsForValue().set(key, value, templateTtlSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Falha ao cachear template | key={}-{}-{}", templateType, messageType, application);
        }
    }

    @CircuitBreaker(name = "redisCache", fallbackMethod = "getTemplateFallback")
    @Retry(name = "redisCache")
    public TemplateCacheView getTemplateFromCache(TemplateTypeEnum templateType, MessageTypeEnum messageType, String application) {
        var key = buildTemplateKey(templateType, messageType, application);
        try {
            var value = redisTemplate.opsForValue().get(key);
            if (value == null || value.isBlank()) {
                return null;
            }
            return objectMapper.readValue(value, TemplateCacheView.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private TemplateCacheView getTemplateFallback(TemplateTypeEnum templateType, MessageTypeEnum messageType, String application, Exception ex) {
        log.warn("FALLBACK: Redis indisponivel");
        return null;
    }

    @CircuitBreaker(name = "redisCache")
    public void removeTemplateFromCache(TemplateTypeEnum templateType, MessageTypeEnum messageType, String application) {
        try {
            String key = buildTemplateKey(templateType, messageType, application);
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.warn("Falha ao remover template do cache | key={}-{}-{}", templateType, messageType, application);
        }
    }

    @CircuitBreaker(name = "redisCache")
    public void clearAllTemplateCache() {
        try {
            var pattern = basePrefix() + ":*";
            var keys = redisTemplate.keys(pattern);
            
            if (keys != null && !keys.isEmpty()) {
                var deletedCount = redisTemplate.delete(keys);
                log.info("Cache de templates limpo com sucesso. {} chave(s) removida(s)", deletedCount);
            } else {
                log.info("Nenhuma chave de cache de templates encontrada para remover");
            }
        } catch (Exception e) {
            log.warn("Falha ao limpar cache de templates");
        }
    }

    private String buildTemplateKey(TemplateTypeEnum templateType, MessageTypeEnum messageType, String application) {
        return basePrefix() + ":" + templateType.name() + ":" + messageType.name() + ":" + normalize(application);
    }

    private String basePrefix() {
        if (templateCachePrefix == null || templateCachePrefix.isBlank()) {
            return "template_cache";
        }
        return templateCachePrefix.replaceAll(":+$", "");
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }

}
