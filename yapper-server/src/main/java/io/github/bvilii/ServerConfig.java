package io.github.bvilii;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ServerConfig {
    private static final Logger log = LoggerFactory.getLogger(ServerConfig.class);
    private int port;

    public ServerConfig(int port){
        this.port = port;
    }

    public static ServerConfig load(){
        Properties properties = new Properties();

        try (InputStream input = ServerConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if(input == null){
                log.warn("Unable to find config.properties in resources");
            }else {
                properties.load(input);
            }
        } catch (IOException e) {
            log.warn("Error reading config.properties", e);
        }

        int port = Integer.parseInt(properties.getProperty("server.port", "9099"));
        return new ServerConfig(port);
    }

    public int getPort() {
        return port;
    }
}
