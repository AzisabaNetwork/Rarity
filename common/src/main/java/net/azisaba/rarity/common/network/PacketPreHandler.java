package net.azisaba.rarity.common.network;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.azisaba.rarity.api.Rarity;
import net.azisaba.rarity.api.RarityAPIProvider;
import net.azisaba.rarity.api.item.ItemStack;
import net.azisaba.rarity.api.item.tag.CompoundTag;
import net.azisaba.rarity.api.item.tag.ListTag;
import net.azisaba.rarity.api.item.tag.StringTag;
import net.azisaba.rarity.common.chat.Component;
import net.azisaba.rarity.common.network.packet.ClientboundSetSlot;
import net.azisaba.rarity.common.network.packet.ClientboundWindowItems;
import net.azisaba.rarity.common.network.packet.ServerboundClickContainerSlot;
import net.azisaba.rarity.common.network.packet.ServerboundSetCreativeSlot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.util.PerformanceCounter;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class PacketPreHandler extends ChannelDuplexHandler {
    private static final PerformanceCounter PROCESS_ITEM_PERF_COUNTER = new PerformanceCounter(PerformanceCounter.Unit.NANOSECONDS);
    private static final Gson GSON = new Gson();
    private final Plugin plugin;
    private final Player player;

    public PacketPreHandler(@NotNull Plugin plugin, @NotNull Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    public static @NotNull String getPerformanceStats(boolean multiline) {
        return PROCESS_ITEM_PERF_COUNTER.getDetails(multiline);
    }

    @Override
    public void channelRead(@NotNull ChannelHandlerContext ctx, @NotNull Object msg) throws Exception {
        // client -> server
        try {
            if (msg.getClass().getSimpleName().contains("SetCreativeSlot")) {
                ServerboundSetCreativeSlot packet = ServerboundSetCreativeSlot.getInstance(msg);
                reverseProcessItemStack(packet.getItem());
            } else if (msg.getClass().getSimpleName().contains("WindowClick")) {
                ServerboundClickContainerSlot packet = ServerboundClickContainerSlot.getInstance(msg);
                reverseProcessItemStack(packet.getItem());
            } else if (msg.getClass().getSimpleName().contains("CloseWindow")) {
                if (player.getOpenInventory().getType() == InventoryType.MERCHANT) {
                    // re-add lore after trading
                    Bukkit.getScheduler().runTask(plugin, player::updateInventory);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        // server -> client
        try {
            if (msg.getClass().getSimpleName().contains("WindowItems")) {
                if (player.getOpenInventory().getType() != InventoryType.MERCHANT) {
                    ClientboundWindowItems packet = ClientboundWindowItems.getInstance(msg);
                    packet.getItems().forEach(i -> {
                        PROCESS_ITEM_PERF_COUNTER.recordStart();
                        try {
                            processItemStack(i);
                        } finally {
                            PROCESS_ITEM_PERF_COUNTER.recordEnd();
                        }
                    });
                }
            } else if (msg.getClass().getSimpleName().contains("SetSlot")) {
                ClientboundSetSlot packet = ClientboundSetSlot.getInstance(msg);
                PROCESS_ITEM_PERF_COUNTER.recordStart();
                try {
                    processItemStack(packet.getItem());
                } finally {
                    PROCESS_ITEM_PERF_COUNTER.recordEnd();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.write(ctx, msg, promise);
    }

    public void processItemStack(@Nullable ItemStack item) {
        if (item == null) return;
        CompoundTag tag = item.getTag();
        boolean hadTag = tag != null;
        if (tag == null) {
            tag = CompoundTag.getInstance(null).constructor();
        }
        if (tag.hasKeyOfType("Rarity", 10)) {
            return;
        }
        CompoundTag rarityTag = CompoundTag.getInstance(null).constructor();
        AtomicReference<CompoundTag> displayTag = new AtomicReference<>(tag.getCompound("display"));
        AtomicInteger lines = new AtomicInteger();
        boolean hadDisplayTag = tag.hasKeyOfType("display", 10);
        boolean hadLoreTag = false;
        Rarity rarity = RarityAPIProvider.get().getRarityByItemStack(item);
        if (rarity == null) {
            return;
        }
        String rarityDisplayName = ChatColor.translateAlternateColorCodes('&', rarity.getDisplayName(player));
        Consumer<ListTag> addLore = list -> {
            list.add(StringTag.create("{\"text\":\" \"}"));
            JsonObject json = new JsonObject();
            json.addProperty("text", rarityDisplayName);
            json.addProperty("italic", false);
            json.addProperty("color", "white");
            list.add(StringTag.create(GSON.toJson(json)));
            lines.addAndGet(2);
            displayTag.get().set("Lore", list);
        };
        if (displayTag.get().hasKeyOfType("Lore", 8) || displayTag.get().hasKeyOfType("Lore", 9)) {
            hadLoreTag = true;
            if (displayTag.get().hasKeyOfType("Lore", 8)) {
                // string
                try {
                    Component component = Component.STATIC.deserialize(displayTag.get().getString("Lore"));
                    if (component != null) {
                        component.addSiblingText(" ");
                        component.addSiblingText(rarityDisplayName);
                        lines.addAndGet(2);
                        displayTag.get().setString("Lore", Component.STATIC.serialize(component));
                        tag.set("display", displayTag.get());
                    }
                } catch (JsonParseException ignore) {
                    // ignore
                }
            } else {
                // list
                ListTag list = displayTag.get().getList("Lore", 8);
                addLore.accept(list);
                tag.set("display", displayTag.get());
            }
        } else {
            ListTag list = ListTag.getInstance(null).constructor();
            addLore.accept(list);
            tag.set("display", displayTag.get());
        }
        if (lines.get() >= 1) {
            rarityTag.setInt("ModifyCount", lines.get());
        }
        rarityTag.setBoolean("HadDisplayTag", hadDisplayTag);
        rarityTag.setBoolean("HadLoreTag", hadLoreTag);
        rarityTag.setBoolean("HadTag", hadTag);
        tag.set("Rarity", rarityTag);
        item.setTag(tag);
    }

    public static void reverseProcessItemStack(@Nullable ItemStack item) {
        if (item == null) return;
        CompoundTag tag = item.getTag();
        if (tag == null) return;
        if (!tag.hasKeyOfType("Rarity", 10)) {
            return;
        }
        CompoundTag rarityTag = tag.getCompound("Rarity");
        boolean hadTag = rarityTag.getBoolean("HadTag");
        if (!hadTag) {
            item.setTag(null);
            return;
        }
        Runnable removeTags = () -> {
            tag.remove("Rarity");
            item.setTag(tag);
        };
        boolean hadDisplayTag = rarityTag.getBoolean("HadDisplayTag");
        if (!hadDisplayTag) {
            tag.remove("display");
            removeTags.run();
            return;
        }
        boolean hadLoreTag = rarityTag.getBoolean("HadLoreTag");
        int count = rarityTag.getInt("ModifyCount");
        CompoundTag displayTag = tag.getCompound("display");
        if (!hadLoreTag) {
            displayTag.remove("Lore");
            tag.set("display", displayTag);
            removeTags.run();
            return;
        }
        if (displayTag.hasKeyOfType("Lore", 8) || displayTag.hasKeyOfType("Lore", 9)) {
            if (displayTag.hasKeyOfType("Lore", 8)) {
                try {
                    Component component = Component.STATIC.deserialize(displayTag.getString("Lore"));
                    if (component != null) {
                        for (int i = 0; i < count; i++) {
                            component.getSiblings().remove(component.getSiblings().size() - 1);
                        }
                        displayTag.setString("Lore", Component.STATIC.serialize(component));
                        tag.set("display", displayTag);
                    }
                } catch (JsonParseException ignored) {
                }
            } else {
                ListTag list = displayTag.getList("Lore", 8);
                for (int i = 0; i < count; i++) {
                    list.removeAt(list.size() - 1);
                }
                displayTag.set("Lore", list);
                tag.set("display", displayTag);
            }
        }
        removeTags.run();
    }
}
