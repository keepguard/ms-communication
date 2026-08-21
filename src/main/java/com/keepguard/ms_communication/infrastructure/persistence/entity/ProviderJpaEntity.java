package com.keepguard.ms_communication.infrastructure.persistence.entity;

import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.domain.enums.ProviderTypeEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "providers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderJpaEntity {
    @Id

    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", nullable = false, length = 50)
    private ProviderTypeEnum providerType;

    @Enumerated(EnumType.STRING)
    @Column(name = "communication_type", nullable = false, length = 30)
    private CommunicationTypeEnum communicationType;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private Boolean isDefault = false;

    @Column(name = "priority", nullable = false)
    @Builder.Default
    private Integer priority = 1;

    @Column(name = "url", length = 500)
    private String url;

    @Column(name = "configuration", columnDefinition = "TEXT")
    private String configuration;

    @Column(name = "max_retries")
    @Builder.Default
    private Integer maxRetries = 3;

    @Column(name = "timeout_seconds")
    @Builder.Default
    private Integer timeoutSeconds = 30;

    @Column(name = "rate_limit_per_minute")
    private Integer rateLimitPerMinute;

    @Column(name = "daily_limit")
    private Integer dailyLimit;

    @Column(name = "monthly_limit")
    private Integer monthlyLimit;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "variables", columnDefinition = "JSONB")
    private Map<String, Object> variables;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
