package net.azisaba.rarity.plugin.command;

import net.azisaba.rarity.plugin.SpigotPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RarityCommand implements TabExecutor {
    public RarityCommand(@NotNull SpigotPlugin plugin) {
        CommandManager.registerCommands(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be executed by players.");
                return true;
            }
            return true;
        }
        try {
            Command cmd = CommandManager.getCommand(args[0]);
            if (cmd == null || !sender.hasPermission("rarity.command.rarity." + args[0])) {
                sender.sendMessage(ChatColor.RED + "Unknown command: " + args[0]);
                return true;
            }
            String[] newArgs = Stream.of(args).skip(1).toArray(String[]::new);
            if (cmd.getMinimumArguments() > newArgs.length) {
                sender.sendMessage(ChatColor.RED + "Usage: " + cmd.getFullUsage());
                return true;
            }
            cmd.execute(sender, newArgs);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + e.getClass().getTypeName() + ": " + e.getMessage());
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Command.filter(
                    CommandManager.getCommands()
                            .stream()
                            .map(Command::getName)
                            .filter(name -> sender.hasPermission("rarity.command.rarity." + name)),
                    args[0]
            ).collect(Collectors.toList());
        }
        if (args.length >= 2) {
            Command cmd = CommandManager.getCommand(args[0]);
            if (cmd != null && sender.hasPermission("rarity.command.rarity." + args[0])) {
                return cmd.getSuggestions(sender, Stream.of(args).skip(1).toArray(String[]::new));
            }
        }
        return Collections.emptyList();
    }
}
