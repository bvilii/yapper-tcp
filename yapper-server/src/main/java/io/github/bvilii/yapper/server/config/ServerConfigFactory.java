package io.github.bvilii.yapper.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ServerConfigFactory {
    private static final Logger log = LoggerFactory.getLogger(ServerConfigFactory.class);
    private static final String DEFAULT_ENV_PORT_KEY = "SERVER_PORT";
    private static final String DB_URL_KEY = "DB_URL";
    private static final String DB_USER_KEY = "DB_USER";
    private static final String DB_PASSWORD_KEY = "DB_PASSWORD";
    static final int DEFAULT_PORT = 9099;

    private ServerConfigFactory() {
    }

    public static ServerConfig load() {
        return load(System.getenv());
    }

    static ServerConfig load(Map<String, String> env) {
        int port = parsePort(env);
        String dbUrl = requireEnv(env, DB_URL_KEY);
        String dbUser = requireEnv(env, DB_USER_KEY);
        String dbPassword = requireEnv(env, DB_PASSWORD_KEY);
        return new ServerConfig(port, dbUrl, dbUser, dbPassword);
    }

    private static int parsePort(Map<String, String> env) {
        String portStr = env.get(DEFAULT_ENV_PORT_KEY);
        if (portStr == null || portStr.isBlank()) {
            log.info("Env variable: {}, not set or is blank ---> falling back to default port: {}", DEFAULT_ENV_PORT_KEY, DEFAULT_PORT);
            return DEFAULT_PORT;
        }

        try {
            return Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid port configuration: " + portStr, e);
        }
    }

    private static String requireEnv(Map<String, String> env, String key) {
        String value = env.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Required env variable not set or blank: " + key);
        }
        return value;
    }
}