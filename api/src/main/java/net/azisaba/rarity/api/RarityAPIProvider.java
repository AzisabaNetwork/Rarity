package net.azisaba.rarity.api;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class RarityAPIProvider {
    private static RarityAPI api;

    /**
     * Set the instance of RarityAPI.
     * @param api the instance of RarityAPI
     */
    @ApiStatus.Internal
    public static void set(@NotNull RarityAPI api) {
        RarityAPIProvider.api = Objects.requireNonNull(api, "api cannot be null");
    }

    /**
     * Get the instance of RarityAPI.
     * @return the instance of RarityAPI
     */
    @Contract(pure = true)
    public static @NotNull RarityAPI get() {
        return Objects.requireNonNull(api, "RarityAPI is not loaded yet");
    }
}
