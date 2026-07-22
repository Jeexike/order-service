package com.example.orderservice.repository.jpaRepository;

import com.example.orderservice.entity.OrderEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {

    List<OrderEntity> findByPartnerId(@Param("partnerId") UUID partnerId);
}
