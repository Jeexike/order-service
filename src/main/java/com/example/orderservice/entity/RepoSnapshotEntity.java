package com.example.orderservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "repo_snapshot")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RepoSnapshotEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false, updatable = false)
    private UUID orderId;

    @Column(name = "link", nullable = false)
    private String link;

    @Column(name = "repository_name")
    private String repositoryName;

    @Column(name = "repository_updated_at")
    private OffsetDateTime repositoryUpdatedAt;

    @Column(name = "issues", columnDefinition = "text")
    private String issues;

    @Column(name = "issues_hash")
    private String issuesHash;

    @Column(name = "pull_requests", columnDefinition = "text")
    private String pullRequests;

    @Column(name = "pull_requests_hash")
    private String pullRequestsHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "last_checked_at", nullable = false)
    private Timestamp lastCheckedAt;
}
