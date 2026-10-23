package net.busybee.advancedvanish.hook.impl;

import github.scarsz.discordsrv.DiscordSRV;
import net.busybee.advancedvanish.AdvancedVanish;
import net.busybee.advancedvanish.api.event.PlayerUnVanishEvent;
import net.busybee.advancedvanish.api.event.PlayerVanishEvent;
import net.busybee.advancedvanish.config.Messages;
import net.busybee.advancedvanish.config.Settings;
import net.busybee.advancedvanish.hook.Hook;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;

/**
 * Sends the fake leave/join messages to Discord too. DiscordSRV already skips real join/quit
 * messages for players with the "vanished" metadata, so nothing is posted twice.
 */
public final class DiscordSrvHook implements Hook {

    private final AdvancedVanish plugin;

    public DiscordSrvHook(AdvancedVanish plugin) {
        this.plugin = plugin;
    }

    @Override
    public String requiredPlugin() {
        return "DiscordSRV";
    }

    @EventHandler
    public void onVanish(PlayerVanishEvent event) {
        Settings settings = plugin.settings();
        if (event.isOnJoin() || !settings.fakeLeaveOnVanish) return;
        DiscordSRV.getPlugin().sendLeaveMessage(event.getPlayer(), legacy(settings.fakeLeaveMessage, event.getPlayer().getName()));
    }

    @EventHandler
    public void onUnVanish(PlayerUnVanishEvent event) {
        Settings settings = plugin.settings();
        if (event.isOnLeave() || !settings.fakeJoinOnUnvanish) return;
        DiscordSRV.getPlugin().sendJoinMessage(event.getPlayer(), legacy(settings.fakeJoinMessage, event.getPlayer().getName()));
    }

    private static String legacy(String message, String playerName) {
        return LegacyComponentSerializer.legacySection().serialize(Messages.parse(message, "%player-name%", playerName));
    }
}
