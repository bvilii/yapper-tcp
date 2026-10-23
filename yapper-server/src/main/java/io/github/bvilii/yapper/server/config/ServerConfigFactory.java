package io.github.bvilii.yapper.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;


public class ServerConfigFactory {
    private static final Logger log = LoggerFactory.getLogger(ServerConfigFactory.class);
    private static final String DEFAULT_ENV_PORT_KEY = "SERVER_PORT";
    static final int DEFAULT_PORT = 9099;

    private ServerConfigFactory() {
    }

    public static ServerConfig load() {
        return load(System.getenv());
    }

    static ServerConfig load(Map<String, String> env) {
        Optional<ServerConfig> envConfig = loadFromEnv(env);
        if (envConfig.isEmpty()) {
            log.info("Env variable: {}, not set or is blank ---> falling back to default port: {}", DEFAULT_ENV_PORT_KEY, DEFAULT_PORT);
            return new ServerConfig(DEFAULT_PORT);
        }

        return envConfig.orElseThrow();
    }

    private static Optional<ServerConfig> loadFromEnv(Map<String, String> env) {
        String portStr = env.get(DEFAULT_ENV_PORT_KEY);
        if (portStr == null || portStr.isBlank()) {
            return Optional.empty();
        }

        try {
            int port = Integer.parseInt(portStr);
            return Optional.of(new ServerConfig(port));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid port configuration: " + portStr, e);
        }
    }

}
