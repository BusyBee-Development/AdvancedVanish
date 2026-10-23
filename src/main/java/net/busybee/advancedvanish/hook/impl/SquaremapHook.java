package net.busybee.advancedvanish.hook.impl;

import net.busybee.advancedvanish.AdvancedVanish;
import net.busybee.advancedvanish.api.event.PlayerUnVanishEvent;
import net.busybee.advancedvanish.api.event.PlayerVanishEvent;
import net.busybee.advancedvanish.hook.Hook;
import org.bukkit.event.EventHandler;
import xyz.jpenilla.squaremap.api.PlayerManager;
import xyz.jpenilla.squaremap.api.SquaremapProvider;

import java.util.UUID;

/** Hides vanished players from the squaremap web map. */
public final class SquaremapHook implements Hook {

    private final AdvancedVanish plugin;
    private final PlayerManager players = SquaremapProvider.get().playerManager();

    public SquaremapHook(AdvancedVanish plugin) {
        this.plugin = plugin;
    }

    @Override
    public String requiredPlugin() {
        return "squaremap";
    }

    @Override
    public void enable() {
        for (UUID uuid : plugin.vanish().getVanished()) players.hide(uuid);
    }

    @EventHandler
    public void onVanish(PlayerVanishEvent event) {
        players.hide(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onUnVanish(PlayerUnVanishEvent event) {
        players.show(event.getPlayer().getUniqueId());
    }
}
