package net.azisaba.rarity.api;

import org.intellij.lang.annotations.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Rarity key is used to identify rarity.
 */
@Pattern("[a-z0-9_]+")
@Target({ElementType.METHOD,ElementType.FIELD,ElementType.PARAMETER,ElementType.LOCAL_VARIABLE})
public @interface RarityKey {
}
