package com.example.orderservice.repository.jpaRepository;

import com.example.orderservice.entity.PartnerEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PartnerJpaRepository extends JpaRepository<PartnerEntity, UUID> {

    @Query("SELECT p FROM PartnerEntity p LEFT JOIN FETCH p.orders WHERE p.id = :id")
    PartnerEntity findByIdWithOrders(@Param("id") UUID id);
}
