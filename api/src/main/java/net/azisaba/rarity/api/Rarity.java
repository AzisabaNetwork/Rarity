package net.azisaba.rarity.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Rarity {
    /**
     * Returns the rarity id.
     * @return the id
     */
    @RarityKey
    @NotNull String getId();

    /**
     * Returns the weight of the rarity.
     * @return the weight
     */
    int getWeight();

    /**
     * Returns the default display name.
     * @return the display name
     */
    @NotNull String getDisplayName();

    /**
     * Returns the rarity translations.
     * @return translations
     */
    @NotNull List<RarityTranslation> getTranslations();

    default @NotNull String getDisplayName(@NotNull Player player) {
        String locale = player.getLocale();
        for (RarityTranslation translation : getTranslations()) {
            if (translation.getLanguage().equals(locale) ||
                    translation.getLanguage().equals(locale.replaceAll("_.*", ""))) {
                return translation.getDisplayName();
            }
        }
        return getDisplayName();
    }

    final class SimpleRarity implements Rarity {
        private final @RarityKey String id;
        private final int weight;
        private final String displayName;
        private final List<RarityTranslation> translations;

        @Contract(pure = true)
        public SimpleRarity(@NotNull @RarityKey String id, int weight, @NotNull String displayName, @NotNull List<RarityTranslation> translations) {
            this.id = id;
            this.weight = weight;
            this.displayName = displayName;
            this.translations = translations;
        }

        @Contract(pure = true)
        @NotNull
        @RarityKey
        @Override
        public String getId() {
            return id;
        }

        @Contract(pure = true)
        @Override
        public int getWeight() {
            return weight;
        }

        @Contract(pure = true)
        @NotNull
        @Override
        public String getDisplayName() {
            return displayName;
        }

        @Contract(pure = true)
        @NotNull
        @Override
        public List<RarityTranslation> getTranslations() {
            return translations;
        }
    }
}
