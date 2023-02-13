package net.azisaba.rarity.v1_15_R1.network.packet;

import net.azisaba.rarity.api.item.ItemStack;
import net.azisaba.rarity.common.network.packet.ClientboundEntityEquipment;
import net.azisaba.rarity.common.util.ReflectionUtil;
import net.azisaba.rarity.v1_15_R1.item.ItemStackImpl;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityEquipment;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClientboundEntityEquipmentImpl implements ClientboundEntityEquipment {
    private final PacketPlayOutEntityEquipment handle;

    public ClientboundEntityEquipmentImpl(PacketPlayOutEntityEquipment handle) {
        this.handle = handle;
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull ClientboundEntityEquipmentImpl getInstance(@NotNull Object handle) {
        return new ClientboundEntityEquipmentImpl((PacketPlayOutEntityEquipment) handle);
    }

    public @NotNull PacketPlayOutEntityEquipment getHandle() {
        return handle;
    }

    @Override
    public @Nullable ItemStack getItem() {
        return ItemStackImpl.getInstance(ReflectionUtil.getField(handle, "c"));
    }
}
