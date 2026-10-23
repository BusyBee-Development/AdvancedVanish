package net.busybee.advancedvanish.hook.impl;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.busybee.advancedvanish.AdvancedVanish;
import net.busybee.advancedvanish.config.Settings;
import net.busybee.advancedvanish.hook.Hook;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.StringJoiner;
import java.util.UUID;

/** Registers {@code %advancedvanish_is_vanished%}, {@code _vanished_players} and {@code _player_count} (names are configurable). */
public final class PlaceholderApiHook implements Hook {

    private final AdvancedVanish plugin;
    private final Expansion expansion = new Expansion();

    public PlaceholderApiHook(AdvancedVanish plugin) {
        this.plugin = plugin;
    }

    @Override
    public String requiredPlugin() {
        return "PlaceholderAPI";
    }

    @Override
    public void enable() {
        expansion.register();
    }

    @Override
    public void disable() {
        expansion.unregister();
    }

    private final class Expansion extends PlaceholderExpansion {

        @Override
        public @NotNull String getIdentifier() {
            return plugin.settings().placeholderIdentifier;
        }

        @Override
        public @NotNull String getAuthor() {
            return "quantiom, BusyBee";
        }

        @Override
        public @NotNull String getVersion() {
            return plugin.getPluginMeta().getVersion();
        }

        @Override
        public boolean persist() {
            return true;
        }

        @Override
        public String onRequest(OfflinePlayer player, @NotNull String params) {
            Settings settings = plugin.settings();
            String key = params.toLowerCase(Locale.ROOT);

            if (key.equals(settings.placeholderIsVanished)) {
                return player != null && plugin.vanish().isVanished(player.getUniqueId()) ? "Yes" : "No";
            }
            if (key.equals(settings.placeholderVanishedPlayers)) {
                StringJoiner names = new StringJoiner(", ");
                for (UUID uuid : plugin.vanish().getVanished()) {
                    Player vanished = Bukkit.getPlayer(uuid);
                    if (vanished != null) names.add(vanished.getName());
                }
                return names.length() == 0 ? "None" : names.toString();
            }
            if (key.equals(settings.placeholderPlayerCount)) {
                return String.valueOf(Math.max(0, Bukkit.getOnlinePlayers().size() - plugin.vanish().vanishedCount()));
            }
            return null;
        }
    }
}
