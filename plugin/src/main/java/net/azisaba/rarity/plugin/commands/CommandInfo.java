package net.azisaba.rarity.plugin.commands;

import net.azisaba.rarity.api.RarityCondition;
import net.azisaba.rarity.api.item.CraftItemStack;
import net.azisaba.rarity.api.item.ItemStack;
import net.azisaba.rarity.plugin.SpigotPlugin;
import net.azisaba.rarity.plugin.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class CommandInfo implements Command {
    private final SpigotPlugin plugin;

    public CommandInfo(@NotNull SpigotPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String @NotNull [] args) throws SQLException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by a player.");
            return;
        }
        Player player = (Player) sender;
        ItemStack item = CraftItemStack.STATIC.asNMSCopy(player.getInventory().getItemInMainHand());
        if (item == null) {
            sender.sendMessage(ChatColor.RED + "You are not holding any item.");
            return;
        }
        String hashedItem = CraftItemStack.getItemHash(item);
        List<RarityCondition> conditions =
                plugin.getApi()
                        .getConditions()
                        .stream()
                        .filter(c -> c.getCompiledCondition().test(item, hashedItem))
                        .collect(Collectors.toList());
        ChatColor color;
        if (conditions.isEmpty()) {
            color = ChatColor.GOLD;
        } else if (conditions.size() == 1) {
            color = ChatColor.GREEN;
        } else {
            color = ChatColor.RED;
        }
        sender.sendMessage(ChatColor.YELLOW + "There are " + color + conditions.size() + ChatColor.YELLOW + " condition(s) that match this item.");
        for (RarityCondition c : conditions) {
            CommandListConditions.suggestRemoveCondition(sender, c);
        }
    }

    @Override
    public @NotNull String getName() {
        return "info";
    }

    @Override
    public @NotNull String getDescription() {
        return "Shows the information about item that you are holding.";
    }

    @Override
    public @NotNull String getUsage() {
        return "";
    }
}
