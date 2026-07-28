package com.example.orderservice.repository;

import com.example.orderservice.entity.RepoSnapshotEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoSnapshotRepository extends JpaRepository<RepoSnapshotEntity, UUID> {

    Optional<RepoSnapshotEntity> findByOrderId(UUID orderId);
}
