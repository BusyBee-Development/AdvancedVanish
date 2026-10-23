package net.busybee.advancedvanish.hook.impl;

import net.busybee.advancedvanish.AdvancedVanish;
import net.busybee.advancedvanish.api.event.PlayerUnVanishEvent;
import net.busybee.advancedvanish.api.event.PlayerVanishEvent;
import net.busybee.advancedvanish.hook.Hook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.dynmap.DynmapAPI;

import java.util.UUID;

/** Hides vanished players from the dynmap web map. */
public final class DynmapHook implements Hook {

    private final AdvancedVanish plugin;
    private final DynmapAPI dynmap;

    public DynmapHook(AdvancedVanish plugin) {
        this.plugin = plugin;
        this.dynmap = (DynmapAPI) Bukkit.getPluginManager().getPlugin("dynmap");
    }

    @Override
    public String requiredPlugin() {
        return "dynmap";
    }

    @Override
    public void enable() {
        for (UUID uuid : plugin.vanish().getVanished()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) dynmap.setPlayerVisiblity(player, false);
        }
    }

    @EventHandler
    public void onVanish(PlayerVanishEvent event) {
        dynmap.setPlayerVisiblity(event.getPlayer(), false);
    }

    @EventHandler
    public void onUnVanish(PlayerUnVanishEvent event) {
        dynmap.setPlayerVisiblity(event.getPlayer(), true);
    }
}
