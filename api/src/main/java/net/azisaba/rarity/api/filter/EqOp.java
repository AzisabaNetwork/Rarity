package net.azisaba.rarity.api.filter;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiPredicate;

public enum EqOp implements BiPredicate<Object, Object> {
    EQUALS,
    NOT_EQUALS,
    ;

    @Override
    public boolean test(@Nullable Object o1, @Nullable Object o2) {
        if (this == EQUALS) {
            return Objects.equals(o1, o2);
        } else {
            return !Objects.equals(o1, o2);
        }
    }
}
