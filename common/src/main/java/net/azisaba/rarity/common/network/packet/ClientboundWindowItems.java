package net.azisaba.rarity.common.network.packet;

import net.azisaba.rarity.api.item.ItemStack;
import net.azisaba.rarity.common.util.ReflectionUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ClientboundWindowItems {
    static @NotNull ClientboundWindowItems getInstance(@Nullable Object o) {
        return (ClientboundWindowItems) ReflectionUtil.getImplInstance("network.packet.ClientboundWindowItems", o);
    }

    /**
     * Returns the copy of items in the window. Modifications to the returned list will not affect the items in the
     * window (but modifying the item stacks in the list will).
     * @return items in the window
     */
    @NotNull
    List<ItemStack> getItems();
}
