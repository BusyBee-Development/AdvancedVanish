package net.busybee.advancedvanish.config;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;

/**
 * One entry of {@code when-vanished.give-potion-effects}, written as {@code "NAME:LEVEL"}.
 * The name may be a modern key ({@code invisibility}, {@code minecraft:jump_boost}) or an old
 * Bukkit constant name ({@code JUMP}, {@code INCREASE_DAMAGE}).
 *
 * @param key       the effect's registry key
 * @param amplifier zero-based amplifier (level 1 = amplifier 0)
 */
public record PotionSpec(NamespacedKey key, int amplifier) {

    /** Old Bukkit constant names whose registry key is different. */
    private static final Map<String, String> LEGACY_NAMES = Map.ofEntries(
            Map.entry("slow", "slowness"),
            Map.entry("fast_digging", "haste"),
            Map.entry("slow_digging", "mining_fatigue"),
            Map.entry("increase_damage", "strength"),
            Map.entry("heal", "instant_health"),
            Map.entry("harm", "instant_damage"),
            Map.entry("jump", "jump_boost"),
            Map.entry("confusion", "nausea"),
            Map.entry("damage_resistance", "resistance"));

    /** Parses {@code "NAME:LEVEL"}. Returns null for anything malformed. */
    public static @Nullable PotionSpec parse(String raw) {
        if (raw == null) return null;
        int split = raw.lastIndexOf(':');
        if (split <= 0 || split == raw.length() - 1) return null;

        String name = raw.substring(0, split).trim().toLowerCase(Locale.ROOT);
        int level;
        try {
            level = Integer.parseInt(raw.substring(split + 1).trim());
        } catch (NumberFormatException e) {
            return null;
        }
        if (level < 1) return null;

        NamespacedKey key;
        if (name.contains(":")) {
            key = NamespacedKey.fromString(name);
        } else {
            key = NamespacedKey.fromString(NamespacedKey.MINECRAFT + ":" + LEGACY_NAMES.getOrDefault(name, name));
        }
        return key == null ? null : new PotionSpec(key, level - 1);
    }
}
