package io.github.bvilii.yapper.server;

import com.zaxxer.hikari.HikariDataSource;
import io.github.bvilii.yapper.server.config.ServerConfig;
import io.github.bvilii.yapper.server.config.ServerConfigFactory;
import io.github.bvilii.yapper.server.persistence.DataSourceFactory;

public class Main {
    public static void main(String[] args) {
        ServerConfig config = ServerConfigFactory.load();
        HikariDataSource dataSource = DataSourceFactory.create(config);
    }
}
