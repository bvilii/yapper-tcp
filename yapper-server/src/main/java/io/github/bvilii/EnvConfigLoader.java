package io.github.bvilii;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EnvConfigLoader {
    private static final Logger log = LoggerFactory.getLogger(EnvConfigLoader.class);
    public static final String DEFAULT_ENV_PORT_KEY = "SERVER_PORT";
    private static final int DEFAULT_PORT = 9099;

    private EnvConfigLoader(){}

    public static ServerConfig load() {
        String portStr = System.getenv(DEFAULT_ENV_PORT_KEY);

        if (portStr == null || portStr.isBlank()) {
            log.info("Env key: {} not set, falling back to default port: {}", DEFAULT_ENV_PORT_KEY, DEFAULT_PORT);
            return new ServerConfig(DEFAULT_PORT);
        }

        try {
            int port = Integer.parseInt(portStr);
            return new ServerConfig(port);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid port configuration: " + portStr, e);
        }
    }

}
