package net.azisaba.rarity.plugin.commands;

import net.azisaba.rarity.plugin.SpigotPlugin;
import net.azisaba.rarity.plugin.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandDeleteRarity implements Command {
    private final SpigotPlugin plugin;

    public CommandDeleteRarity(@NotNull SpigotPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String @NotNull [] args) throws SQLException {
        @Subst("id") String id = args[0];
        if (!id.matches("^[a-z0-9_]+$")) {
            sender.sendMessage(ChatColor.RED + "The id must be lowercase letters, numbers, and underscores.");
            return;
        }
        long count = plugin.getApi().getConditions().stream().filter(c -> c.getRarity().getId().equals(id)).count();
        if (count > 0) {
            sender.sendMessage(ChatColor.RED + "There are " + count + " conditions that use this rarity. Please delete them first.");
            return;
        }
        plugin.getDatabaseManager().queryVoid("DELETE FROM `rarity_rarities` WHERE `id` = ?", ps -> {
            ps.setString(1, id);
            ps.executeUpdate();
        });
        sender.sendMessage(ChatColor.GREEN + "Deletes a rarity (id: " + id + "). Please execute " +
                ChatColor.GOLD + "/rarity refresh" + ChatColor.GREEN + " to reload the data.");
    }

    @Override
    public @NotNull String getName() {
        return "deleteRarity";
    }

    @Override
    public @NotNull String getDescription() {
        return "Delete a rarity.";
    }

    @Override
    public @NotNull String getUsage() {
        return "<id>";
    }

    @Override
    public int getMinimumArguments() {
        return 1;
    }

    @Override
    public @NotNull List<String> getSuggestions(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return Command.filter(plugin.getApi().getRarityMap().keySet().stream(), args[0]).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
