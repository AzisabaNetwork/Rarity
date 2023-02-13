package net.azisaba.rarity.api.item.tag;

import net.azisaba.rarity.api.RarityAPIProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface StringTag extends Tag {
    static @NotNull StringTag create(@NotNull String text) {
        return getInstance(null).valueOf(text);
    }

    static @NotNull StringTag getInstance(@Nullable Object o) {
        return (StringTag) RarityAPIProvider.get().getImplInstance("item.tag.StringTag", o);
    }

    /**
     * Creates a new instance of the string tag. (static method)
     * @return the new tag
     */
    @NotNull
    StringTag valueOf(@NotNull String text);
}
