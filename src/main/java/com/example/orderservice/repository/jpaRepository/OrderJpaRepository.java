package com.example.orderservice.repository.jpaRepository;

import com.example.orderservice.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {

    @Query("SELECT o FROM OrderEntity o WHERE o.partner.id = :partnerId")
    List<OrderEntity> findByPartnerId(@Param("partnerId") UUID partnerId);
}

