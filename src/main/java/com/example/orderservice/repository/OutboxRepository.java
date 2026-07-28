package com.example.orderservice.repository;

import com.example.orderservice.entity.OutboxEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxRepository extends JpaRepository<OutboxEntity, UUID> {

    List<OutboxEntity> findAllByOrderByCreatedAtAsc(Pageable pageable);
}
