package net.azisaba.rarity.common.network.packet;

import net.azisaba.rarity.api.item.ItemStack;
import net.azisaba.rarity.common.util.ReflectionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ServerboundSetCreativeSlot {
    static @NotNull ServerboundSetCreativeSlot getInstance(@Nullable Object o) {
        return (ServerboundSetCreativeSlot) ReflectionUtil.getImplInstance("network.packet.ServerboundSetCreativeSlot", o);
    }

    @Nullable
    ItemStack getItem();
}
