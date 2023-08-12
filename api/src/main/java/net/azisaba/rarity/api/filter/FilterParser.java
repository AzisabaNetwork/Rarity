package net.azisaba.rarity.api.filter;

import net.azisaba.loreeditor.api.item.CraftItemStack;
import net.azisaba.loreeditor.api.item.ItemStack;
import net.azisaba.loreeditor.api.item.tag.CompoundTag;
import net.azisaba.loreeditor.api.item.tag.Tag;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import xyz.acrylicstyle.util.InvalidArgumentException;
import xyz.acrylicstyle.util.StringReader;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

public final class FilterParser {
    public static @NotNull FilterPredicate parse(@NotNull StringReader reader) throws InvalidArgumentException {
        reader.skipWhitespace();
        if (reader.isEOF()) return FilterPredicate.TRUE;
        FilterPredicate.Op currentOp = null;
        List<BiPredicate<ItemStack, String>> currentPredicates = new ArrayList<>();
        while (!reader.isEOF() && reader.peek() != ')') {
            reader.skipWhitespace();
            currentPredicates.add(parseSingle(reader));
            reader.skipWhitespace();
            if (reader.isEOF() || reader.peek() == ')') break;
            String read = reader.read(2);
            if (read.equals("&&")) {
                if (currentOp == null) currentOp = FilterPredicate.Op.AND;
                else if (currentOp == FilterPredicate.Op.OR) {
                    throw new IllegalArgumentException("Cannot change operator to " + read + " (Use parentheses)");
                }
            } else if (read.equals("||")) {
                if (currentOp == null) currentOp = FilterPredicate.Op.OR;
                else if (currentOp == FilterPredicate.Op.AND) {
                    throw new IllegalArgumentException("Cannot change operator to " + read + " (Use parentheses)");
                }
            } else {
                throw new InvalidArgumentException("Expected && or ||, but got " + read);
            }
            reader.skipWhitespace();
            if (reader.isEOF()) throw new InvalidArgumentException("Unexpected EOF");
        }
        if (currentOp == null) currentOp = FilterPredicate.Op.AND;
        return new FilterPredicate(currentOp, currentPredicates);
    }

    private static @NotNull BiPredicate<ItemStack, String> parseSingle(@NotNull StringReader reader) throws InvalidArgumentException {
        if (reader.isEOF()) {
            return FilterPredicate.TRUE;
        }
        if (reader.peek() == '(') {
            reader.skip();
            reader.skipWhitespace();
            FilterPredicate group = parse(reader);
            reader.skipWhitespace();
            if (reader.peek() == ')') {
                try {
                    reader.skip();
                } catch (InvalidArgumentException e) {
                    throw new AssertionError();
                }
                return group;
            }
            throw new IllegalArgumentException("Expected ')' but got '" + reader.peek() + "'");
        }
        reader.skipWhitespace();
        String left = reader.readUntilIf((c) -> !Character.isWhitespace(c) && c != '\n' && c != '\r' && c != '!' && c != '=');
        reader.skipWhitespace();
        EqOp op;
        if (reader.peek() == '=') {
            reader.skip();
            if (reader.peek() == '=') reader.skip();
            op = EqOp.EQUALS;
        } else if (reader.peek() == '!') {
            reader.skip();
            if (reader.peek() == '=') {
                reader.skip();
            } else {
                throw new IllegalArgumentException("Expected '=' after '!' but got '" + reader.peek() + "'");
            }
            op = EqOp.NOT_EQUALS;
        } else {
            throw new IllegalArgumentException("Expected '=', '==', or '!=' but got '" + reader.peek() + "'");
        }
        reader.skipWhitespace();
        String token = reader.readQuotedString('"', '\n', '\\');
        BiPredicate<ItemStack, String> predicate;
        if (left.equals("hash")) {
            if (token.startsWith("pattern:")) {
                Pattern pattern = Pattern.compile(token.substring(8));
                predicate = (i, h) -> pattern.matcher(h).matches();
            } else {
                predicate = (i, h) -> h.equals(token);
            }
        } else if (left.equals("type")) {
            if (token.startsWith("pattern:")) {
                Pattern pattern = Pattern.compile(token.substring(8));
                predicate = (i, h) -> pattern.matcher(CraftItemStack.STATIC.asCraftMirror(i).getType().name()).matches();
            } else {
                predicate = (i, h) -> CraftItemStack.STATIC.asCraftMirror(i).getType().name().equals(token);
            }
        } else if (left.equals("display_name")) {
            Pattern pattern;
            if (token.startsWith("pattern:")) {
                pattern = Pattern.compile(token.substring(8));
            } else {
                pattern = null;
            }
            predicate = (i, h) -> {
                org.bukkit.inventory.ItemStack bukkitItem = CraftItemStack.STATIC.asCraftMirror(i);
                if (bukkitItem.hasItemMeta() && bukkitItem.getItemMeta().hasDisplayName()) {
                    if (pattern == null) {
                        return bukkitItem.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', token));
                    } else {
                        return pattern.matcher(bukkitItem.getItemMeta().getDisplayName()).matches();
                    }
                } else {
                    return token.equals("null");
                }
            };
        } else if (left.equals("amount")) {
            if (token.startsWith("pattern:")) {
                Pattern pattern = Pattern.compile(token.substring(8));
                predicate = (i, h) -> pattern.matcher(String.valueOf(i.getCount())).matches();
            } else {
                int integer = Integer.parseInt(token);
                predicate = (i, h) -> i.getCount() == integer;
            }
        } else if (left.startsWith("tag.")) {
            Pattern pattern;
            if (token.startsWith("pattern:")) {
                pattern = Pattern.compile(token.substring(8));
            } else {
                pattern = null;
            }
            String path = left.substring(4);
            predicate = (i, h) -> {
                CompoundTag object = i.getTag();
                if (object == null) {
                    return false;
                }
                String[] arr = path.split("\\.");
                for (int idx = 0; idx < arr.length; idx++) {
                    boolean last = idx + 1 == arr.length;
                    if (last) {
                        Tag result = object.get(arr[idx]);
                        if (pattern == null || result == null) {
                            return (result != null ? result.asString() : "null").equals(token);
                        } else {
                            return pattern.matcher(result.asString()).matches();
                        }
                    } else {
                        if (!object.hasKeyOfType(arr[idx], 10)) {
                            return false;
                        }
                        object = object.getCompound(arr[idx]);
                    }
                }
                return false;
            };
        } else if (left.equals("tag")) {
            if (token.equals("null")) {
                predicate = (i, h) -> i.getTag() == null;
            } else {
                predicate = (i, h) -> i.getTag() != null;
            }
        } else if (left.equals("tag_size")) {
            if (token.startsWith("pattern:")) {
                Pattern pattern = Pattern.compile(token.substring(8));
                predicate = (i, h) -> {
                    int size = 0;
                    if (i.getTag() != null) {
                        size = i.getTag().size();
                    }
                    return pattern.matcher(String.valueOf(size)).matches();
                };
            } else {
                int integer = Integer.parseInt(token);
                predicate = (i, h) -> {
                    int size = 0;
                    if (i.getTag() != null) {
                        size = i.getTag().size();
                    }
                    return size == integer;
                };
            }
        } else {
            throw new IllegalArgumentException("Unknown left operand: " + left);
        }
        reader.skipWhitespace();
        if (op == EqOp.EQUALS) {
            return predicate;
        } else {
            return predicate.negate();
        }
    }
}
