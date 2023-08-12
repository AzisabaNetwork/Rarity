package net.azisaba.rarity.plugin;

import net.azisaba.loreeditor.api.item.CraftItemStack;
import net.azisaba.loreeditor.api.item.ItemStack;
import net.azisaba.rarity.api.*;
import net.azisaba.rarity.api.filter.FilterParser;
import net.azisaba.rarity.api.filter.FilterPredicate;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.util.StringReader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class SpigotRarityAPI implements RarityAPI {
    private final @NotNull SpigotPlugin plugin;
    private @NotNull Map<String, Rarity> rarityMap = new HashMap<>();
    private @NotNull List<RarityCondition> conditions = new ArrayList<>();
    // Map<item hash, rarity>
    private final Map<String, Rarity> rarityCache = new HashMap<>();

    public SpigotRarityAPI(@NotNull SpigotPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull Rarity getRarityById(@NotNull @RarityKey String id) {
        Rarity rarity = rarityMap.get(id);
        if (rarity == null) {
            throw new NoSuchElementException("Rarity not found: " + id);
        }
        return rarity;
    }

    @Override
    public @Nullable Rarity getRarityByItemStack(@NotNull ItemStack nmsItem) {
        String itemHash = CraftItemStack.getItemHash(nmsItem);
        if (rarityCache.containsKey(itemHash)) {
            return rarityCache.get(itemHash);
        }
        for (RarityCondition condition : conditions) {
            Rarity rarity = condition.apply(nmsItem, itemHash);
            if (rarity != null) {
                rarityCache.put(itemHash, rarity);
                return rarity;
            }
        }
        rarityCache.put(itemHash, null);
        return null;
    }

    @Override
    public void refresh() {
        try {
            List<RarityTranslation> translations = new ArrayList<>();
            plugin.getDatabaseManager().queryVoid("SELECT * FROM `rarity_translations`", ps -> {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        @Subst("id") String id = rs.getString("id");
                        String language = rs.getString("lang");
                        String displayName = rs.getString("display_name");
                        RarityTranslation translation = new RarityTranslation.SimpleRarityTranslation(id, language, displayName);
                        translations.add(translation);
                    }
                }
            });
            plugin.getDatabaseManager().queryVoid("SELECT * FROM `rarity_rarities`", ps -> {
                try (ResultSet rs = ps.executeQuery()) {
                    Map<String, Rarity> rarityMap1 = new HashMap<>();
                    while (rs.next()) {
                        @Subst("id") String id = rs.getString("id");
                        int weight = rs.getInt("weight");
                        String displayName = rs.getString("display_name");
                        Rarity rarity = new Rarity.SimpleRarity(id, weight, displayName, translations.stream().filter(t -> t.getId().equals(id)).collect(Collectors.toList()));
                        rarityMap1.put(id, rarity);
                    }
                    rarityMap = rarityMap1;
                }
            });
            plugin.getDatabaseManager().queryVoid("SELECT * FROM `rarity_conditions`", ps -> {
                try (ResultSet rs = ps.executeQuery()) {
                    List<RarityCondition> conditions1 = new ArrayList<>();
                    while (rs.next()) {
                        @Subst("id") String id = rs.getString("id");
                        String condition = rs.getString("condition");
                        try {
                            Rarity rarity = Objects.requireNonNull(rarityMap.get(id), "rarity not found: " + id);
                            FilterPredicate predicate = FilterParser.parse(StringReader.create(condition));
                            conditions1.add(new RarityCondition() {
                                @Override
                                public @NotNull String getRawCondition() {
                                    return condition;
                                }

                                @Override
                                public @NotNull FilterPredicate getCompiledCondition() {
                                    return predicate;
                                }

                                @Override
                                public @NotNull Rarity getRarity() {
                                    return rarity;
                                }
                            });
                        } catch (Exception e) {
                            plugin.getLogger().warning("Failed to process condition of id " + id + ": " + condition);
                            e.printStackTrace();
                        }
                    }
                    conditions = conditions1;
                }
            });
            rarityCache.clear();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public @NotNull Map<String, Rarity> getRarityMap() {
        return rarityMap;
    }

    public @NotNull List<RarityCondition> getConditions() {
        return conditions;
    }
}
