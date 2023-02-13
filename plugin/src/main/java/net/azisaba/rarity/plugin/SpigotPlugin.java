package net.azisaba.rarity.plugin;

import net.azisaba.rarity.api.RarityAPIProvider;
import net.azisaba.rarity.common.sql.DatabaseConfig;
import net.azisaba.rarity.common.sql.DatabaseManager;
import net.azisaba.rarity.common.util.ChannelUtil;
import net.azisaba.rarity.plugin.command.RarityCommand;
import net.azisaba.rarity.plugin.listener.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.reflector.Reflector;
import xyz.acrylicstyle.util.reflector.executor.MethodExecutorReflection;

import java.sql.SQLException;
import java.util.Objects;

public class SpigotPlugin extends JavaPlugin {
    private final SpigotRarityAPI api = new SpigotRarityAPI(this);
    private DatabaseManager databaseManager;

    @Override
    public void onLoad() {
        Reflector.classLoader = getClassLoader();
        Reflector.methodExecutor = new MethodExecutorReflection();
        RarityAPIProvider.set(api);
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();

        try {
            databaseManager = new DatabaseManager(DatabaseConfig.create(getConfig().getConfigurationSection("database")).createDataSource());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        getLogger().info("Performing initial data load...");
        long start = System.currentTimeMillis();
        api.refresh();
        getLogger().info("Initial data load completed in " + (System.currentTimeMillis() - start) + "ms.");

        // register command
        Bukkit.getPluginCommand("rarity").setExecutor(new RarityCommand(this));

        // register event handler
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);

        // inject packet pre handler
        for (Player player : Bukkit.getOnlinePlayers()) {
            ChannelUtil.INSTANCE.inject(this, player);
        }
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ChannelUtil.INSTANCE.eject(player);
        }

        if (databaseManager != null) {
            databaseManager.close();
        }
    }

    public @NotNull SpigotRarityAPI getApi() {
        return api;
    }

    public @NotNull DatabaseManager getDatabaseManager() {
        return Objects.requireNonNull(databaseManager, "databaseManager is not initialized");
    }
}
