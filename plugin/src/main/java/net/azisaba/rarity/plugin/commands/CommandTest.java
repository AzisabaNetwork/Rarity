package net.azisaba.rarity.plugin.commands;

import net.azisaba.rarity.api.item.CraftItemStack;
import net.azisaba.rarity.api.item.ItemStack;
import net.azisaba.rarity.api.filter.FilterParser;
import net.azisaba.rarity.plugin.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.InvalidArgumentException;
import xyz.acrylicstyle.util.StringReader;

public class CommandTest implements Command {
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String @NotNull [] args) throws InvalidArgumentException {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by players.");
            return;
        }
        Player player = (Player) sender;
        ItemStack item = CraftItemStack.STATIC.asNMSCopy(player.getInventory().getItemInMainHand());
        if (item == null) {
            sender.sendMessage(ChatColor.RED + "You are not holding any item.");
            return;
        }
        String joined = String.join(" ", args);
        StringReader reader = StringReader.create(joined);
        boolean result = FilterParser.parse(reader).test(item, CraftItemStack.getItemHash(item));
        ChatColor color = result ? ChatColor.GREEN : ChatColor.RED;
        sender.sendMessage(ChatColor.GOLD + "Result: " + color + result);
    }

    @Override
    public @NotNull String getName() {
        return "test";
    }

    @Override
    public @NotNull String getDescription() {
        return "Test the condition.";
    }

    @Override
    public @NotNull String getUsage() {
        return "<condition>";
    }

    @Override
    public int getMinimumArguments() {
        return 1;
    }
}
