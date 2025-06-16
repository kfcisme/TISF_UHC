
package me.wowkfccc.logplayeraction.logplayeraction;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class mySQLConnect {
    private final Logplayeraction plugin;
    private HikariDataSource ds;

    public mySQLConnect(Logplayeraction plugin) {
        this.plugin = plugin;
    }

    /**
     * Initialize HikariCP pool. Automatically includes public key retrieval.
     */
    public void connect() {
        if (ds != null && !ds.isClosed()) {
            return;
        }

        String host = plugin.getConfig().getString("database.host", "localhost");
        int port = plugin.getConfig().getInt("database.port", 3306);
        String database = plugin.getConfig().getString("database.database");
        String user = plugin.getConfig().getString("database.username");
        String pass = plugin.getConfig().getString("database.password");

        String jdbcUrl = String.format(
                "jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                host, port, database
        );

        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(jdbcUrl);
        cfg.setUsername(user);
        cfg.setPassword(pass);

        // Pool size and timeout settings
        cfg.setMaximumPoolSize(10);
        cfg.setConnectionTimeout(30000);
        cfg.setIdleTimeout(600000);
        cfg.setMaxLifetime(30000);
        cfg.setValidationTimeout(5000);
        cfg.setKeepaliveTime(150000);

        ds = new HikariDataSource(cfg);
        plugin.getLogger().info("MySQL connection pool initialized");
    }

    /**
     * Get a connection from the pool, reconnecting if necessary.
     */
    public Connection getConnection() throws SQLException {
        if (ds == null || ds.isClosed()) {
            connect();
        }
        return ds.getConnection();
    }

    /**
     * Check pool connectivity without leaking a connection.
     */
    public boolean isConnected() {
        if (ds == null || ds.isClosed()) {
            return false;
        }
        try (Connection conn = ds.getConnection()) {
            return conn != null && conn.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Close the pool when plugin disables.
     */
    public void disconnect() {
        if (ds != null && !ds.isClosed()) {
            ds.close();
            plugin.getLogger().info("MySQL connection pool closed");
        }
    }
}
