package net.azisaba.rarity.common.util.function;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public interface SQLThrowableConsumer<T> {
    void accept(@NotNull T t) throws SQLException;
}
