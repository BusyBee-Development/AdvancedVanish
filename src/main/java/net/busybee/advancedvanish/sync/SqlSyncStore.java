package net.busybee.advancedvanish.sync;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.busybee.advancedvanish.config.Settings;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

/**
 * MySQL/MariaDB table {@code <table-prefix>states (uuid, state)} behind a small HikariCP pool.
 * Rows from the unprefixed {@code advancedvanish} table used before 26.10 are copied in once.
 */
final class SqlSyncStore implements SyncStore {

    private static final String LEGACY_TABLE = "advancedvanish";

    private final Settings settings;
    private final String table;
    private HikariDataSource dataSource;

    SqlSyncStore(Settings settings) {
        this.settings = settings;
        String prefix = settings.sqlTablePrefix == null ? "" : settings.sqlTablePrefix.replaceAll("[^A-Za-z0-9_]", "");
        this.table = prefix + "states";
    }

    @Override
    public String name() {
        return "MySQL";
    }

    @Override
    public void connect() throws SQLException {
        HikariConfig config = new HikariConfig();
        config.setPoolName("AdvancedVanish");
        config.setJdbcUrl("jdbc:mysql://" + settings.sqlHost + ":" + settings.sqlPort + "/" + settings.sqlDatabase);
        config.setUsername(settings.sqlUsername);
        config.setPassword(settings.sqlPassword);
        config.setMaximumPoolSize(3);
        config.setMinimumIdle(1);
        config.setConnectionTimeout(5000);
        dataSource = new HikariDataSource(config);

        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + table + "` ("
                    + "uuid CHAR(36) NOT NULL PRIMARY KEY, "
                    + "state BOOLEAN NOT NULL)");
            if (!table.equals(LEGACY_TABLE) && tableExists(connection, LEGACY_TABLE)) {
                statement.executeUpdate("INSERT IGNORE INTO `" + table + "` (uuid, state) SELECT uuid, state FROM `" + LEGACY_TABLE + "`");
            }
        }
    }

    @Override
    public void close() {
        if (dataSource != null) dataSource.close();
        dataSource = null;
    }

    @Override
    public Boolean get(UUID uuid) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT state FROM `" + table + "` WHERE uuid = ?")) {
            statement.setString(1, uuid.toString());
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getBoolean(1) : null;
            }
        }
    }

    @Override
    public void set(UUID uuid, boolean vanished) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO `" + table + "` (uuid, state) VALUES (?, ?) ON DUPLICATE KEY UPDATE state = VALUES(state)")) {
            statement.setString(1, uuid.toString());
            statement.setBoolean(2, vanished);
            statement.executeUpdate();
        }
    }

    private static boolean tableExists(Connection connection, String name) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        try (ResultSet tables = meta.getTables(connection.getCatalog(), null, name, new String[]{"TABLE"})) {
            return tables.next();
        }
    }
}
