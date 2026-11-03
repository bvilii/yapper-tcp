package io.github.bvilii.yapper.server.persistence;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.bvilii.yapper.server.config.ServerConfig;

public class DataSourceFactory {

    private DataSourceFactory() {
    }

    public static HikariDataSource create(ServerConfig config) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.dbUrl());
        hikariConfig.setUsername(config.dbUser());
        hikariConfig.setPassword(config.dbPassword());
        hikariConfig.setPoolName("yapper-db");

        return new HikariDataSource(hikariConfig);
    }
}
