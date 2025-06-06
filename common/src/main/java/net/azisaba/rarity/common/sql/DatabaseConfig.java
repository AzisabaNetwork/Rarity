package net.azisaba.rarity.common.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mariadb.jdbc.Driver;

import java.util.Objects;
import java.util.Properties;

public final class DatabaseConfig {
    private final String driver;
    private final String scheme;
    private final String hostname;
    private final int port;
    private final String name;
    private final String username;
    private final String password;
    private final Properties properties;

    @Contract("_ -> new")
    public static @NotNull DatabaseConfig create(@NotNull ConfigurationSection section) {
        String driver = section.getString("driver");
        String scheme = Objects.requireNonNull(section.getString("scheme"));
        String hostname = Objects.requireNonNull(section.getString("hostname"));
        int port = section.getInt("port", 3306);
        String name = Objects.requireNonNull(section.getString("name"));
        String username = section.getString("username");
        String password = section.getString("password");
        Properties properties = new Properties();
        ConfigurationSection props = section.getConfigurationSection("properties");
        if (props != null) {
            props.getValues(true).forEach(((key, value) -> properties.setProperty(key, String.valueOf(value))));
        }
        return new DatabaseConfig(driver, scheme, hostname, port, name, username, password, properties);
    }

    public DatabaseConfig(
            @Nullable String driver,
            @NotNull String scheme,
            @NotNull String hostname,
            int port,
            @NotNull String name,
            @Nullable String username,
            @Nullable String password,
            @NotNull Properties properties
    ) {
        this.driver = driver;
        this.scheme = scheme;
        this.hostname = hostname;
        this.port = port;
        this.name = name;
        this.username = username;
        this.password = password;
        this.properties = properties;
    }

    @Contract(pure = true)
    @Nullable
    public String driver() {
        return driver;
    }

    @Contract(pure = true)
    @NotNull
    public String scheme() {
        return scheme;
    }

    @Contract(pure = true)
    @NotNull
    public String hostname() {
        return hostname;
    }

    @Contract(pure = true)
    public int port() {
        return port;
    }

    @Contract(pure = true)
    @NotNull
    public String name() {
        return name;
    }

    @Contract(pure = true)
    @Nullable
    public String username() {
        return username;
    }

    @Contract(pure = true)
    @Nullable
    public String password() {
        return password;
    }

    @Contract(pure = true)
    @NotNull
    public Properties properties() {
        return properties;
    }

    @Contract(pure = true)
    @NotNull
    public String toUrl() {
        return scheme() + "://" + hostname() + ":" + port() + "/" + name();
    }

    @Contract(" -> new")
    public @NotNull HikariDataSource createDataSource() {
        new Driver();
        HikariConfig config = new HikariConfig();
        if (driver() != null) {
            config.setDriverClassName(driver());
        }
        config.setJdbcUrl(toUrl());
        config.setUsername(username());
        config.setPassword(password());
        config.setDataSourceProperties(properties());
        return new HikariDataSource(config);
    }
}
