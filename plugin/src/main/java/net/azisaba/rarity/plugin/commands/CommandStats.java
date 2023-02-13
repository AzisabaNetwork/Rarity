package net.azisaba.rarity.plugin.commands;

import net.azisaba.rarity.common.network.PacketPreHandler;
import net.azisaba.rarity.plugin.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandStats implements Command {
    @Override
    public void execute(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        sender.sendMessage(ChatColor.GOLD + "PacketPreHandler#processItemStack:");
        sender.sendMessage(PacketPreHandler.getPerformanceStats(true));
    }

    @Override
    public @NotNull String getName() {
        return "stats";
    }

    @Override
    public @NotNull String getDescription() {
        return "Displays the performance stats.";
    }

    @Override
    public @NotNull String getUsage() {
        return "";
    }
}
