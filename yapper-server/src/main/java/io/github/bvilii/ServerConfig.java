package io.github.bvilii;

public record ServerConfig(int port) {

    public ServerConfig {
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException(
                    "Port must be between 1 and 65535, but got: " + port
            );
        }
    }
}
