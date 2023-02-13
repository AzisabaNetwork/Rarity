package net.azisaba.rarity.common.network.packet;

import net.azisaba.rarity.api.item.ItemStack;
import net.azisaba.rarity.common.util.ReflectionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ClientboundSetSlot {
    static @NotNull ClientboundSetSlot getInstance(@Nullable Object o) {
        return (ClientboundSetSlot) ReflectionUtil.getImplInstance("network.packet.ClientboundSetSlot", o);
    }

    @Nullable
    ItemStack getItem();
}
