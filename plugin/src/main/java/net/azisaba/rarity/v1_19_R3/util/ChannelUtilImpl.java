package net.azisaba.rarity.v1_19_R3.util;

import io.netty.channel.ChannelPipeline;
import net.azisaba.rarity.common.network.PacketPreHandler;
import net.azisaba.rarity.common.util.ChannelUtil;
import net.azisaba.rarity.v1_19_R3.entity.CraftPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;

public class ChannelUtilImpl implements ChannelUtil {
    @Override
    public void inject(@NotNull Plugin plugin, @NotNull Player player) {
        try {
            CraftPlayer.getInstance(player)
                    .getHandle()
                    .getPlayerConnection()
                    .getNetworkManager()
                    .getChannel()
                    .pipeline()
                    .addBefore("packet_handler", "rarity", new PacketPreHandler(plugin, player));
            plugin.getLogger().info("Injected packet handler for " + player.getName());
        } catch (NoSuchElementException e) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (!player.isOnline()) return;
                try {
                    CraftPlayer.getInstance(player)
                            .getHandle()
                            .getPlayerConnection()
                            .getNetworkManager()
                            .getChannel()
                            .pipeline()
                            .addBefore("packet_handler", "rarity", new PacketPreHandler(plugin, player));
                    plugin.getLogger().info("Injected packet handler for " + player.getName());
                } catch (NoSuchElementException ignore) {
                    plugin.getLogger().warning("Failed to inject packet handler to " + player.getName());
                }
            }, 10);
        }
    }

    @Override
    public void eject(@NotNull Player player) {
        try {
            ChannelPipeline pipeline = CraftPlayer.getInstance(player)
                    .getHandle()
                    .getPlayerConnection()
                    .getNetworkManager()
                    .getChannel()
                    .pipeline();
            if (pipeline.get("rarity") != null) pipeline.remove("rarity");
        } catch (RuntimeException ignore) {}
    }
}
