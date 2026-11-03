package io.github.bvilii.yapper.server.config;

import java.util.Objects;

public record ServerConfig(int port, String dbUrl, String dbUser, String dbPassword) {

    public ServerConfig {
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException(
                    "Port must be between 1 and 65535, but got: " + port
            );
        }

        Objects.requireNonNull(dbUrl, "dbUrl must not be null");
        Objects.requireNonNull(dbUser, "dbUser must not be null");
        Objects.requireNonNull(dbPassword, "dbPassword must not be null");
    }

    @Override
    public String toString() {
        return "ServerConfig[port=" + port + ", dbUrl=" + dbUrl + ", dbUser=" + dbUser + ", dbPassword=***]";
    }
}
