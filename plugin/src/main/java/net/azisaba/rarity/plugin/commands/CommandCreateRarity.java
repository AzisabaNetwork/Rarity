package net.azisaba.rarity.plugin.commands;

import net.azisaba.rarity.plugin.SpigotPlugin;
import net.azisaba.rarity.plugin.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandCreateRarity implements Command {
    private final SpigotPlugin plugin;

    public CommandCreateRarity(@NotNull SpigotPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String @NotNull [] args) throws SQLException {
        @Subst("id") String id = args[0];
        if (!id.matches("^[a-z0-9_]+$")) {
            sender.sendMessage(ChatColor.RED + "The id must be lowercase letters, numbers, and underscores.");
            return;
        }
        int weight = Integer.parseInt(args[1]);
        String displayName = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        plugin.getDatabaseManager().queryVoid("INSERT INTO `rarity_rarities` (`id`, `weight`, `display_name`) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE `weight` = VALUES(`weight`), `display_name` = VALUES(`display_name`)", ps -> {
            ps.setString(1, id);
            ps.setInt(2, weight);
            ps.setString(3, displayName);
            ps.executeUpdate();
        });
        sender.sendMessage(ChatColor.GREEN + "Added (or updated) the rarity (id: " + id + "). Please execute " +
                ChatColor.GOLD + "/rarity refresh" + ChatColor.GREEN + " to reload the data.");
    }

    @Override
    public @NotNull String getName() {
        return "createRarity";
    }

    @Override
    public @NotNull String getDescription() {
        return "Create or update an new rarity.";
    }

    @Override
    public @NotNull String getUsage() {
        return "<id> <weight> <display name>";
    }

    @Override
    public int getMinimumArguments() {
        return 3;
    }

    @Override
    public @NotNull List<String> getSuggestions(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return Command.filter(plugin.getApi().getRarityMap().keySet().stream(), args[0]).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
