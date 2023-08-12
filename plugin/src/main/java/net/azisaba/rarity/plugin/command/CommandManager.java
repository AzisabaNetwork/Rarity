package net.azisaba.rarity.plugin.command;

import net.azisaba.rarity.plugin.SpigotPlugin;
import net.azisaba.rarity.plugin.commands.CommandAddTranslation;
import net.azisaba.rarity.plugin.commands.CommandClearConditionsOf;
import net.azisaba.rarity.plugin.commands.CommandCreateCondition;
import net.azisaba.rarity.plugin.commands.CommandCreateRarity;
import net.azisaba.rarity.plugin.commands.CommandDeleteRarity;
import net.azisaba.rarity.plugin.commands.CommandDumpItem;
import net.azisaba.rarity.plugin.commands.CommandHash;
import net.azisaba.rarity.plugin.commands.CommandHelp;
import net.azisaba.rarity.plugin.commands.CommandInfo;
import net.azisaba.rarity.plugin.commands.CommandListConditions;
import net.azisaba.rarity.plugin.commands.CommandRefresh;
import net.azisaba.rarity.plugin.commands.CommandDeleteCondition;
import net.azisaba.rarity.plugin.commands.CommandTest;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class CommandManager {
    private CommandManager() { throw new AssertionError(); }

    private static final List<Command> COMMANDS = new ArrayList<>();

    public static void registerCommand(@NotNull Command command) {
        COMMANDS.add(Objects.requireNonNull(command, "command"));
    }

    @Contract(pure = true)
    public static @NotNull List<@NotNull Command> getCommands() {
        return COMMANDS;
    }

    public static @Nullable Command getCommand(@NotNull String name) {
        for (Command command : COMMANDS) {
            if (command.getName().equalsIgnoreCase(name)) return command;
        }
        return null;
    }

    static void registerCommands(@NotNull SpigotPlugin plugin) {
        COMMANDS.clear();
        registerCommand(new CommandHelp());
        registerCommand(new CommandDumpItem());
        registerCommand(new CommandTest());
        registerCommand(new CommandRefresh(plugin));
        registerCommand(new CommandCreateRarity(plugin));
        registerCommand(new CommandClearConditionsOf(plugin));
        registerCommand(new CommandDeleteRarity(plugin));
        registerCommand(new CommandListConditions(plugin));
        registerCommand(new CommandInfo(plugin));
        registerCommand(new CommandCreateCondition(plugin));
        registerCommand(new CommandDeleteCondition(plugin));
        registerCommand(new CommandAddTranslation(plugin));
        registerCommand(new CommandHash());
    }
}
