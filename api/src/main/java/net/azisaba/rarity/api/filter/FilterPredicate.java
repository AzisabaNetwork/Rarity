package net.azisaba.rarity.api.filter;

import net.azisaba.rarity.api.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;

public final class FilterPredicate implements BiPredicate<ItemStack, String> {
    public static final FilterPredicate TRUE = new FilterPredicate(Op.AND, Collections.emptyList());
    public static final FilterPredicate FALSE = new FilterPredicate(Op.AND, Collections.singletonList((i, h) -> false));
    private final Op op;
    private final List<BiPredicate<ItemStack, String>> predicates;

    public FilterPredicate(@NotNull Op op, @NotNull List<BiPredicate<ItemStack, String>> predicates) {
        this.op = op;
        this.predicates = predicates;
    }

    @Override
    public boolean test(ItemStack itemData, @NotNull String itemHash) {
        if (predicates.isEmpty()) {
            return true;
        }
        if (op == Op.AND) {
            return predicates.stream().allMatch(p -> p.test(itemData, itemHash));
        } else {
            return predicates.stream().anyMatch(p -> p.test(itemData, itemHash));
        }
    }

    enum Op {
        OR,
        AND,
    }
}
