package net.azisaba.rarity.api;

import net.azisaba.rarity.api.filter.FilterPredicate;
import net.azisaba.loreeditor.api.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RarityCondition {
    /**
     * Returns the condition as a string.
     * @return the condition
     */
    @NotNull String getRawCondition();

    /**
     * Returns the compiled condition function.
     * @return the filter predicate
     */
    @NotNull FilterPredicate getCompiledCondition();

    /**
     * Returns the target rarity of the condition.
     * @return the rarity
     */
    @NotNull Rarity getRarity();

    /**
     * Applies the condition to the item stack. If the condition is not met, null will be returned.
     * @param nmsItem the item
     * @return the rarity if the condition is met, null otherwise
     */
    default @Nullable Rarity apply(@NotNull ItemStack nmsItem, @NotNull String hashedItem) {
        return getCompiledCondition().test(nmsItem, hashedItem) ? getRarity() : null;
    }
}
