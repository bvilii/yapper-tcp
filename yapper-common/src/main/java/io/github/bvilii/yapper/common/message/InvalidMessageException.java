package io.github.bvilii.yapper.common.message;

public class InvalidMessageException extends Exception {
    public InvalidMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}