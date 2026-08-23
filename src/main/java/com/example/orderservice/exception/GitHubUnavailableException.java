package com.example.orderservice.exception;

public class GitHubUnavailableException extends RuntimeException {

    public GitHubUnavailableException(String link, Exception cause) {
        super("GitHub API is currently unavailable for link: " + link, cause);
    }
}
