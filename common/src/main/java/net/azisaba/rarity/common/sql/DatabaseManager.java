package net.azisaba.rarity.common.sql;

import com.zaxxer.hikari.HikariDataSource;
import net.azisaba.rarity.common.util.function.SQLThrowableConsumer;
import net.azisaba.rarity.common.util.function.SQLThrowableFunction;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseManager {
    private final @NotNull HikariDataSource dataSource;

    public DatabaseManager(@NotNull HikariDataSource dataSource) throws SQLException {
        this.dataSource = dataSource;
        createTables();
    }

    @SuppressWarnings("SqlNoDataSourceInspection")
    private void createTables() throws SQLException {
        useStatement(statement -> {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `rarity_rarities` (" +
                    "  `id` VARCHAR(32) NOT NULL," +
                    "  `weight` INT NOT NULL," +
                    "  `display_name` VARCHAR(64) NOT NULL," +
                    "  PRIMARY KEY (`id`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `rarity_translations` (" +
                    "  `id` VARCHAR(32) NOT NULL," + // rarity id
                    "  `lang` VARCHAR(5) NOT NULL," + // language (Locale#getLanguage())
                    "  `display_name` VARCHAR(255) NOT NULL," +
                    "  PRIMARY KEY (`id`, `lang`)," +
                    "  FOREIGN KEY (`id`) REFERENCES `rarity_rarities` (`id`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `rarity_conditions` (" +
                    "  `id` VARCHAR(32) NOT NULL," + // rarity id
                    "  `condition` VARCHAR(512) NOT NULL UNIQUE," + // condition
                    "  PRIMARY KEY (`id`, `condition`)," +
                    "  FOREIGN KEY (`id`) REFERENCES `rarity_rarities` (`id`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci");
        });
    }

    @NotNull
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Contract(pure = true)
    public <R> R use(@NotNull SQLThrowableFunction<Connection, R> action) throws SQLException {
        try (Connection connection = getConnection()) {
            return action.apply(connection);
        }
    }

    @Contract(pure = true)
    public void use(@NotNull SQLThrowableConsumer<Connection> action) throws SQLException {
        try (Connection connection = getConnection()) {
            action.accept(connection);
        }
    }

    public void queryVoid(@Language("SQL") @NotNull String sql, @NotNull SQLThrowableConsumer<PreparedStatement> action) throws SQLException {
        use(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                action.accept(statement);
            }
        });
    }

    @Contract
    public <R> R query(@Language("SQL") @NotNull String sql, @NotNull SQLThrowableFunction<PreparedStatement, R> action) throws SQLException {
        return use(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                return action.apply(statement);
            }
        });
    }

    @Contract(pure = true)
    public void useStatement(@NotNull SQLThrowableConsumer<Statement> action) throws SQLException {
        use(connection -> {
            try (Statement statement = connection.createStatement()) {
                action.accept(statement);
            }
        });
    }

    /**
     * Closes the data source.
     */
    public void close() {
        dataSource.close();
    }
}
