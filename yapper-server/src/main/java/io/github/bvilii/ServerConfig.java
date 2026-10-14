package io.github.bvilii;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServerConfig {
    private static final Logger log = LoggerFactory.getLogger(ServerConfig.class);
    private static final int DEFAULT_PORT = 9099;
    public static final String DEFAULT_RESOURCE = "config.properties";
    public static final String PORT_KEY = "server.port";
    public static final int MIN_PORT = 1;
    public static final int MAX_PORT = 65535;

    private final int port;


    public ServerConfig(int port) {
        this.port = port;
    }

    public static ServerConfig load() {
        return load(DEFAULT_RESOURCE);
    }

    public static ServerConfig load(String resourcePath) {

        try (InputStream input = ServerConfig.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (input == null) {
                log.warn("Config file '{}' not found, using defaults: {}", resourcePath, DEFAULT_PORT);
                return new ServerConfig(DEFAULT_PORT);
            }
            return load(input);
        } catch (IOException e) {
            log.warn("Error reading {}, falling back to defaults {}", resourcePath, DEFAULT_PORT, e);
            return new ServerConfig(DEFAULT_PORT);
        }
    }

    public static ServerConfig load(InputStream input) {
        Properties properties = new Properties();
        try {
            properties.load(input);
        } catch (IOException e) {
            log.warn("Error reading config file, falling back to defaults {}", DEFAULT_PORT, e);
            return new ServerConfig(DEFAULT_PORT);
        }
        return new ServerConfig(parsePort(properties.getProperty(PORT_KEY)));


    }

    private static int parsePort(String value) {
        if(value == null){
        return DEFAULT_PORT;
        }


        try {
           int port = Integer.parseInt(value.trim());
            if(port < MIN_PORT || port > MAX_PORT){
                log.warn("Invalid port number: {}, falling back to defaults {}", port, DEFAULT_PORT);
                return DEFAULT_PORT;
            }
            return port;
        } catch (NumberFormatException e) {
            log.warn("Invalid port number format: {}, falling back to defaults {}", value.trim(), DEFAULT_PORT);
            return DEFAULT_PORT;
        }

    }

    public int getPort() {
        return port;
    }
}
