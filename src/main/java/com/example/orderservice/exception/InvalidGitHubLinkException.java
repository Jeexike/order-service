package com.example.orderservice.exception;

public class InvalidGitHubLinkException extends RuntimeException {

    public InvalidGitHubLinkException(String link) {
        super("Link is not a valid GitHub repository URL: " + link);
    }
}
