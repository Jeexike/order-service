package com.example.orderservice.github;

import com.example.orderservice.exception.InvalidGitHubLinkException;
import com.example.orderservice.github.dto.GitHubResponse;
import com.example.orderservice.github.dto.IssueResponse;
import com.example.orderservice.github.dto.PullRequestResponse;
import com.example.orderservice.github.dto.RepoSnapshotData;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class GitHubClient {

    private static final Pattern REPO_LINK_PATTERN =
            Pattern.compile("^https://github\\.com/(?<owner>[\\w.-]+)/(?<repo>[\\w.-]+)/?$");

    private final RestClient gitHubRestClient;

    public RepoSnapshotData fetch(String link) {
        String[] ownerAndRepo = parseOwnerAndRepo(link);
        String owner = ownerAndRepo[0];
        String repo = ownerAndRepo[1];

        GitHubResponse repository = gitHubRestClient
                .get()
                .uri("/repos/{owner}/{repo}", owner, repo)
                .retrieve()
                .body(GitHubResponse.class);

        List<IssueResponse> issues = gitHubRestClient
                .get()
                .uri("/repos/{owner}/{repo}/issues", owner, repo)
                .retrieve()
                .body(new ParameterizedTypeReference<List<IssueResponse>>() {});

        List<PullRequestResponse> pullRequests = gitHubRestClient
                .get()
                .uri("/repos/{owner}/{repo}/pulls", owner, repo)
                .retrieve()
                .body(new ParameterizedTypeReference<List<PullRequestResponse>>() {});

        return new RepoSnapshotData(repository, issues, pullRequests);
    }

    private String[] parseOwnerAndRepo(String link) {
        Matcher matcher = REPO_LINK_PATTERN.matcher(link);
        if (!matcher.matches()) {
            throw new InvalidGitHubLinkException(link);
        }
        return new String[] {matcher.group("owner"), matcher.group("repo")};
    }
}
