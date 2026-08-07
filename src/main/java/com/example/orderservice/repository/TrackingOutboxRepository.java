package com.example.orderservice.repository;

import com.example.orderservice.entity.TrackingOutboxEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackingOutboxRepository extends JpaRepository<TrackingOutboxEntity, UUID> {

    List<TrackingOutboxEntity> findAllByOrderByCreatedAtAsc(Pageable pageable);
}
