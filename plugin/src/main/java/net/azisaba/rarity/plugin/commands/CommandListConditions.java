package net.azisaba.rarity.plugin.commands;

import net.azisaba.rarity.api.RarityCondition;
import net.azisaba.loreeditor.common.util.ChatUtil;
import net.azisaba.rarity.plugin.SpigotPlugin;
import net.azisaba.rarity.plugin.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CommandListConditions implements Command {
    private final SpigotPlugin plugin;

    public CommandListConditions(@NotNull SpigotPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String @NotNull [] args) throws SQLException {
        @Subst("id") String id = args[0];
        if (!id.matches("^[a-z0-9_]+$")) {
            sender.sendMessage(ChatColor.RED + "The id must be lowercase letters, numbers, and underscores.");
            return;
        }
        plugin.getApi()
                .getConditions()
                .stream()
                .filter(c -> c.getRarity().getId().equals(id))
                .sorted(Comparator.comparingInt(c -> c.getRarity().getWeight()))
                .forEach(c -> suggestRemoveCondition(sender, c));
    }

    public static void suggestRemoveCondition(@NotNull CommandSender sender, @NotNull RarityCondition condition) {
        String message = ChatColor.GRAY + "[" + ChatColor.translateAlternateColorCodes('&', condition.getRarity().getDisplayName()) + ChatColor.GRAY + "] " + ChatColor.RESET + condition.getRawCondition();
        String hover = "Click to delete the condition";
        String click = "/rarity deleteCondition " + condition.getRarity().getId() + " " + condition.getRawCondition();
        sender.spigot().sendMessage(ChatUtil.INSTANCE.createComponentWithHoverWithSuggestCommand(message, hover, click));
    }

    @Override
    public @NotNull String getName() {
        return "listConditions";
    }

    @Override
    public @NotNull String getDescription() {
        return "Shows the list of conditions that use the rarity.";
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
