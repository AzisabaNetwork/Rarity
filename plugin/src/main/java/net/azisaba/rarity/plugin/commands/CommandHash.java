package net.azisaba.rarity.plugin.commands;

import net.azisaba.rarity.api.item.CraftItemStack;
import net.azisaba.rarity.api.item.ItemStack;
import net.azisaba.rarity.common.util.ChatUtil;
import net.azisaba.rarity.plugin.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandHash implements Command {
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return;
        }
        org.bukkit.inventory.ItemStack bukkitItem = ((Player) sender).getInventory().getItemInMainHand();
        ItemStack nmsItem = CraftItemStack.STATIC.asNMSCopy(bukkitItem);
        if (nmsItem == null) {
            sender.sendMessage("The item is null.");
            return;
        }
        String itemHash = CraftItemStack.getItemHash(nmsItem);
        sender.spigot().sendMessage(ChatUtil.INSTANCE.createComponentWithHoverWithSuggestCommand(itemHash, "Click to copy", itemHash));
    }

    @Override
    public @NotNull String getName() {
        return "hash";
    }

    @Override
    public @NotNull String getDescription() {
        return "Get the item hash.";
    }

    @Override
    public @NotNull String getUsage() {
        return "";
    }
}
