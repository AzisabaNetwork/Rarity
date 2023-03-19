package net.azisaba.rarity.v1_19_R3.network.packet;

import net.azisaba.rarity.api.item.ItemStack;
import net.azisaba.rarity.common.network.packet.ServerboundSetCreativeSlot;
import net.azisaba.rarity.v1_19_R3.item.ItemStackImpl;
import net.minecraft.network.protocol.game.PacketPlayInSetCreativeSlot;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ServerboundSetCreativeSlotImpl implements ServerboundSetCreativeSlot {
    private final PacketPlayInSetCreativeSlot handle;

    public ServerboundSetCreativeSlotImpl(PacketPlayInSetCreativeSlot handle) {
        this.handle = handle;
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull ServerboundSetCreativeSlotImpl getInstance(Object handle) {
        return new ServerboundSetCreativeSlotImpl((PacketPlayInSetCreativeSlot) handle);
    }

    @Override
    public @Nullable ItemStack getItem() {
        return ItemStackImpl.getInstance(handle.c());
    }
}
