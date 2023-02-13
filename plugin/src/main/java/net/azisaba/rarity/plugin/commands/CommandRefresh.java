package net.azisaba.rarity.plugin.commands;

import net.azisaba.rarity.plugin.SpigotPlugin;
import net.azisaba.rarity.plugin.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandRefresh implements Command {
    private final SpigotPlugin plugin;

    public CommandRefresh(@NotNull SpigotPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        new Thread(() -> {
            sender.sendMessage(ChatColor.GOLD + "Refreshing the data, please wait...");
            long start = System.currentTimeMillis();
            plugin.getApi().refresh();
            sender.sendMessage(ChatColor.GREEN + "Refresh completed in " + (System.currentTimeMillis() - start) + "ms.");
        }).start();
    }

    @Override
    public @NotNull String getName() {
        return "refresh";
    }

    @Override
    public @NotNull String getDescription() {
        return "Request refresh of the data.";
    }

    @Override
    public @NotNull String getUsage() {
        return "";
    }
}
