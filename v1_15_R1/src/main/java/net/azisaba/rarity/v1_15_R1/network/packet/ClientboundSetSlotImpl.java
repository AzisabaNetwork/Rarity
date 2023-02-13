package net.azisaba.rarity.v1_15_R1.network.packet;

import net.azisaba.rarity.api.item.ItemStack;
import net.azisaba.rarity.common.network.packet.ClientboundSetSlot;
import net.azisaba.rarity.common.util.ReflectionUtil;
import net.azisaba.rarity.v1_15_R1.item.ItemStackImpl;
import net.minecraft.server.v1_15_R1.PacketPlayOutSetSlot;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClientboundSetSlotImpl implements ClientboundSetSlot {
    private final PacketPlayOutSetSlot handle;

    public ClientboundSetSlotImpl(PacketPlayOutSetSlot handle) {
        this.handle = handle;
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull ClientboundSetSlotImpl getInstance(@NotNull Object handle) {
        return new ClientboundSetSlotImpl((PacketPlayOutSetSlot) handle);
    }

    public @NotNull PacketPlayOutSetSlot getHandle() {
        return handle;
    }

    @Override
    public @Nullable ItemStack getItem() {
        return ItemStackImpl.getInstance(ReflectionUtil.getField(handle, "c"));
    }
}
