package net.azisaba.rarity.api;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface RarityTranslation {
    /**
     * Returns the rarity id.
     * @return the id
     */
    @RarityKey
    @NotNull String getId();

    /**
     * Returns the language.
     * @return the language
     */
    @NotNull String getLanguage();

    /**
     * Returns the default display name.
     * @return the display name
     */
    @NotNull String getDisplayName();

    final class SimpleRarityTranslation implements RarityTranslation {
        private final @RarityKey String id;
        private final String language;
        private final String displayName;

        @Contract(pure = true)
        public SimpleRarityTranslation(@NotNull @RarityKey String id, @NotNull String language, @NotNull String displayName) {
            this.id = id;
            this.language = language;
            this.displayName = displayName;
        }

        @Contract(pure = true)
        @RarityKey
        @Override
        public @NotNull String getId() {
            return id;
        }

        @Contract(pure = true)
        @Override
        public @NotNull String getLanguage() {
            return language;
        }

        @Contract(pure = true)
        @Override
        public @NotNull String getDisplayName() {
            return displayName;
        }
    }
}
