package db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;


public class DbConfig {
    private static final HikariConfig config = new HikariConfig();
    private final HikariDataSource ds;

    public DbConfig() {
        System.out.println(System.getenv());
        config.setJdbcUrl("jdbc:postgresql://"+System.getenv("DB_HOST")+":"+System.getenv("DB_PORT")+"/"+System.getenv("DB_NAME"));
        config.setUsername(System.getenv("POSTGRES_USER"));
        config.setPassword(System.getenv("POSTGRES_PASSWORD"));
        config.setMaximumPoolSize(10); // Max 10 connections in the pool
        config.setMinimumIdle(2);      // Minimum idle connections
        config.setConnectionTimeout(30000); // 30 seconds timeout
        ds = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }


}
