package com.example.orderservice.github.dto;

import java.util.List;

public record RepoSnapshotData(
        GitHubResponse repository, List<IssueResponse> issues, List<PullRequestResponse> pullRequests) {}
