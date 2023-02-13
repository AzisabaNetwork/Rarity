package net.azisaba.rarity.common.network.packet;

import net.azisaba.rarity.api.item.ItemStack;
import net.azisaba.rarity.common.util.ReflectionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ClientboundEntityEquipment {
    static @NotNull ClientboundEntityEquipment getInstance(@Nullable Object o) {
        return (ClientboundEntityEquipment) ReflectionUtil.getImplInstance("network.packet.ClientboundEntityEquipment", o);
    }

    @Nullable
    ItemStack getItem();
}
