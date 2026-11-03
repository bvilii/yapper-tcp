package io.github.bvilii.yapper.server.persistence;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String username, Throwable cause) {
        super("User already exists: " + username, cause);
    }
}