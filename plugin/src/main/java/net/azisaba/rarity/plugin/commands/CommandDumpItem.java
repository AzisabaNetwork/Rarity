package net.azisaba.rarity.plugin.commands;

import net.azisaba.rarity.api.item.CraftItemStack;
import net.azisaba.rarity.api.item.ItemStack;
import net.azisaba.rarity.api.item.tag.CompoundTag;
import net.azisaba.rarity.plugin.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandDumpItem implements Command {
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return;
        }
        org.bukkit.inventory.ItemStack bukkitItem = ((Player) sender).getInventory().getItemInMainHand();
        String displayName = null;
        if (bukkitItem.hasItemMeta() && bukkitItem.getItemMeta().hasDisplayName()) {
            displayName = bukkitItem.getItemMeta().getDisplayName().replace('§', '&');
        }
        sender.sendMessage(ChatColor.GOLD + "Display Name: " + ChatColor.RESET + displayName);
        ItemStack nmsItem = CraftItemStack.STATIC.asNMSCopy(bukkitItem);
        if (nmsItem != null) {
            CompoundTag tag = nmsItem.getTag();
            if (tag != null) {
                sender.sendMessage(tag.toString());
            } else {
                sender.sendMessage("The item has no tag.");
            }
        } else {
            sender.sendMessage("The item is null.");
        }
    }

    @Override
    public @NotNull String getName() {
        return "dumpItem";
    }

    @Override
    public @NotNull String getDescription() {
        return "Dumps the item tag of the item in your main hand.";
    }

    @Override
    public @NotNull String getUsage() {
        return "";
    }
}
