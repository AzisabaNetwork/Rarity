package net.azisaba.rarity.plugin;

import net.azisaba.loreeditor.api.event.EventBus;
import net.azisaba.loreeditor.api.event.ItemEvent;
import net.azisaba.loreeditor.libs.net.kyori.adventure.text.Component;
import net.azisaba.loreeditor.libs.net.kyori.adventure.text.format.TextDecoration;
import net.azisaba.loreeditor.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.azisaba.rarity.api.Rarity;
import net.azisaba.rarity.api.RarityAPIProvider;
import net.azisaba.rarity.common.sql.DatabaseConfig;
import net.azisaba.rarity.common.sql.DatabaseManager;
import net.azisaba.rarity.plugin.command.RarityCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

        EventBus.INSTANCE.register(this, ItemEvent.class, 0, e -> {
            Rarity rarity = RarityAPIProvider.get().getRarityByItemStack(e.getBukkitItem());
            if (rarity == null) {
                return;
            }
            String displayName = ChatColor.translateAlternateColorCodes('&', rarity.getDisplayName(e.getPlayer()));
            e.addLore(Component.space());
            e.addLore(LegacyComponentSerializer.legacySection().deserialize(displayName).decoration(TextDecoration.ITALIC, false));
        });
    }

    @Override
    public void onDisable() {
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
