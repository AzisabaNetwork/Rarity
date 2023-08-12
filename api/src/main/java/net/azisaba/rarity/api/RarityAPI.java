package net.azisaba.rarity.api;

import net.azisaba.loreeditor.api.item.CraftItemStack;
import net.azisaba.loreeditor.api.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * This is the main interface of RarityAPI.
 * You can get the instance of this interface by {@link RarityAPIProvider#get()}.
 */
public interface RarityAPI {
    /**
     * Gets the rarity by the id.
     * @param id the id of the rarity
     * @return the rarity
     * @throws NoSuchElementException if the rarity is not found
     */
    @NotNull Rarity getRarityById(@NotNull @RarityKey String id) throws NoSuchElementException;

    /**
     * Gets the rarity by the item stack.
     * @param nmsItem the nms item stack
     * @return the rarity, or null if any of the conditions are not met
     */
    @Nullable Rarity getRarityByItemStack(@NotNull ItemStack nmsItem);

    /**
     * Gets the rarity by the bukkit item stack.
     * @param itemStack the bukkit item stack
     * @return the rarity, or null if any of the conditions are not met
     */
    default @Nullable Rarity getRarityByItemStack(@NotNull org.bukkit.inventory.ItemStack itemStack) {
        return getRarityByItemStack(Objects.requireNonNull(CraftItemStack.STATIC.asNMSCopy(itemStack), "invalid item stack: " + itemStack));
    }

    /**
     * Request the refresh of the rarities, conditions, and translations.
     * This method will block until the refresh is completed.
     */
    void refresh();
}
