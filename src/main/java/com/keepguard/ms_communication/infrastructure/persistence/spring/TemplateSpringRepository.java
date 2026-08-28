package com.keepguard.ms_communication.infrastructure.persistence.spring;

import com.keepguard.lib_common.communication.enums.MessageTypeEnum;
import com.keepguard.lib_common.communication.enums.TemplateTypeEnum;
import com.keepguard.ms_communication.infrastructure.persistence.entity.TemplateJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TemplateSpringRepository extends JpaRepository<TemplateJpaEntity, UUID> {

    @Query("SELECT t FROM TemplateJpaEntity t WHERE t.templateType = :templateType AND t.isActive = true")
    List<TemplateJpaEntity> findActiveTemplatesByType(@Param("templateType") TemplateTypeEnum templateType);

    @Query("SELECT t FROM TemplateJpaEntity t WHERE t.messageType = :messageType AND t.isActive = true")
    List<TemplateJpaEntity> findActiveTemplatesByMessageType(@Param("messageType") MessageTypeEnum messageType);

    @Query("SELECT t FROM TemplateJpaEntity t WHERE t.isActive = true ORDER BY t.name ASC")
    List<TemplateJpaEntity> findAllActiveTemplates();

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);

    @Query("SELECT t FROM TemplateJpaEntity t WHERE " +
           "(:name IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:messageType IS NULL OR t.messageType = :messageType) AND " +
           "(:templateType IS NULL OR t.templateType = :templateType) AND " +
           "(:isActive IS NULL OR t.isActive = :isActive)")
    Page<TemplateJpaEntity> findWithFilters(
        @Param("name") String name,
        @Param("messageType") String messageType,
        @Param("templateType") String templateType,
        @Param("isActive") Boolean isActive,
        Pageable pageable
    );

    @Query("SELECT t FROM TemplateJpaEntity t WHERE " +
           "t.templateType = :templateType AND " +
           "t.messageType = :messageType AND " +
           "t.companyId = :companyId AND " +
           "t.isActive = :isActive")
    Optional<TemplateJpaEntity> findByTemplateTypeAndMessageTypeAndTenantIdAndIsActive(
        @Param("templateType") TemplateTypeEnum templateType,
        @Param("messageType") MessageTypeEnum messageType,
        @Param("companyId") String companyId,
        @Param("isActive") Boolean isActive
    );
    
    // Método alternativo para evitar conflito com geração automática do Spring Data JPA
    @Query("SELECT t FROM TemplateJpaEntity t WHERE " +
           "t.templateType = :templateType AND " +
           "t.messageType = :messageType AND " +
           "t.companyId = :companyId AND " +
           "t.isActive = :isActive")
    Optional<TemplateJpaEntity> findTemplateByTypeMessageAndApp(
        @Param("templateType") TemplateTypeEnum templateType,
        @Param("messageType") MessageTypeEnum messageType,
        @Param("companyId") String companyId,
        @Param("isActive") Boolean isActive
    );
}
