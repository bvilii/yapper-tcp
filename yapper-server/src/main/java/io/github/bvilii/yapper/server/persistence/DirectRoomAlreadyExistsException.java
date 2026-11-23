package io.github.bvilii.yapper.server.persistence;

public class DirectRoomAlreadyExistsException extends RuntimeException {
    public DirectRoomAlreadyExistsException(Throwable cause) {
        super("A direct room between these users already exists", cause);
    }
}
