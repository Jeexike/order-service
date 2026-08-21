package com.example.orderservice.github;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.orderservice.exception.InvalidGitHubLinkException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

@DisplayName("GitHubClient — валидация ссылки")
class GitHubClientInvalidLinkTest {

    private final GitHubClient client =
            new GitHubClient(RestClient.builder().baseUrl("http://localhost").build());

    @Test
    @DisplayName("невалидная ссылка → InvalidGitHubLinkException")
    void invalidLink_throws() {
        assertThatThrownBy(() -> client.fetch("https://gitlab.com/owner/repo"))
                .isInstanceOf(InvalidGitHubLinkException.class);
    }

    @Test
    @DisplayName("пустая строка → InvalidGitHubLinkException")
    void emptyLink_throws() {
        assertThatThrownBy(() -> client.fetch(""))
                .isInstanceOf(InvalidGitHubLinkException.class);
    }
}