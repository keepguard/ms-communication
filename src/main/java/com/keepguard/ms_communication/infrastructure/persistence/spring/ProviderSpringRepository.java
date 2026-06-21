package com.keepguard.ms_communication.infrastructure.persistence.spring;

import com.keepguard.lib_common.communication.enums.CommunicationTypeEnum;
import com.keepguard.ms_communication.infrastructure.persistence.entity.ProviderJpaEntity;
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
public interface ProviderSpringRepository extends JpaRepository<ProviderJpaEntity, UUID> {

    @Query("SELECT p FROM ProviderJpaEntity p WHERE p.communicationType = :communicationType AND p.isActive = true ORDER BY p.priority ASC")
    List<ProviderJpaEntity> findActiveProvidersByCommunicationType(@Param("communicationType") CommunicationTypeEnum communicationType);

    @Query("SELECT p FROM ProviderJpaEntity p WHERE p.isActive = true ORDER BY p.priority ASC")
    List<ProviderJpaEntity> findAllActiveProviders();

    @Query("SELECT p FROM ProviderJpaEntity p WHERE p.communicationType = :communicationType AND p.isDefault = true AND p.isActive = true")
    Optional<ProviderJpaEntity> findDefaultProviderByCommunicationType(@Param("communicationType") CommunicationTypeEnum communicationType);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);

    @Query("SELECT p FROM ProviderJpaEntity p WHERE " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:providerType IS NULL OR p.providerType = :providerType) AND " +
           "(:communicationType IS NULL OR p.communicationType = :communicationType) AND " +
           "(:isActive IS NULL OR p.isActive = :isActive) AND " +
           "(:isDefault IS NULL OR p.isDefault = :isDefault)")
    Page<ProviderJpaEntity> findWithFilters(
        @Param("name") String name,
        @Param("providerType") String providerType,
        @Param("communicationType") String communicationType,
        @Param("isActive") Boolean isActive,
        @Param("isDefault") Boolean isDefault,
        Pageable pageable
    );
}
