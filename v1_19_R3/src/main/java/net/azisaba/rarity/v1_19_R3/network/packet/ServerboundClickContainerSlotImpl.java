package net.azisaba.rarity.v1_19_R3.network.packet;

import net.azisaba.rarity.api.item.ItemStack;
import net.azisaba.rarity.common.network.packet.ServerboundClickContainerSlot;
import net.azisaba.rarity.v1_19_R3.item.ItemStackImpl;
import net.minecraft.network.protocol.game.PacketPlayInWindowClick;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ServerboundClickContainerSlotImpl implements ServerboundClickContainerSlot {
    private final PacketPlayInWindowClick handle;

    public ServerboundClickContainerSlotImpl(@NotNull PacketPlayInWindowClick handle) {
        this.handle = Objects.requireNonNull(handle, "handle");
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull ServerboundClickContainerSlotImpl getInstance(@NotNull Object handle) {
        return new ServerboundClickContainerSlotImpl((PacketPlayInWindowClick) handle);
    }

    @Override
    public @Nullable ItemStack getItem() {
        return ItemStackImpl.getInstance(handle.e());
    }
}
