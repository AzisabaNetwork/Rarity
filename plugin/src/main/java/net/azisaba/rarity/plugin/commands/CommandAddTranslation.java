package net.azisaba.rarity.plugin.commands;

import net.azisaba.rarity.plugin.SpigotPlugin;
import net.azisaba.rarity.plugin.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.InvalidArgumentException;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CommandAddTranslation implements Command {
    private final SpigotPlugin plugin;

    public CommandAddTranslation(@NotNull SpigotPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String @NotNull [] args) throws SQLException, InvalidArgumentException {
        @Subst("id") String id = args[0];
        if (!id.matches("^[a-z0-9_]+$")) {
            sender.sendMessage(ChatColor.RED + "The id must be lowercase letters, numbers, and underscores.");
            return;
        }
        String locale = args[1];
        if (!locale.matches("^[a-z0-9_]+$")) {
            sender.sendMessage(ChatColor.RED + "The locale must be lowercase letters, numbers, and underscores.");
            return;
        }
        String displayName = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        plugin.getDatabaseManager().queryVoid("INSERT INTO `rarity_translations` (`id`, `lang`, `display_name`) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE `display_name` = VALUES(`display_name`)", ps -> {
            ps.setString(1, id);
            ps.setString(2, locale);
            ps.setString(3, displayName);
            ps.executeUpdate();
        });
        sender.sendMessage(ChatColor.GREEN + "Created a new translation for [id: " + id + ", lang: " + locale + "]. " +
                "Please execute " + ChatColor.GOLD + "/rarity refresh" + ChatColor.GREEN + " to reload the data.");
    }

    @Override
    public @NotNull String getName() {
        return "addTranslation";
    }

    @Override
    public @NotNull String getDescription() {
        return "Add a new translation.";
    }

    @Override
    public @NotNull String getUsage() {
        return "<id> <locale> <display name>";
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
