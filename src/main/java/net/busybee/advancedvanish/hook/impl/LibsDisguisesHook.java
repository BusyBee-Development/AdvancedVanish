package net.busybee.advancedvanish.hook.impl;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.events.DisguiseEvent;
import net.busybee.advancedvanish.AdvancedVanish;
import net.busybee.advancedvanish.api.event.PlayerVanishEvent;
import net.busybee.advancedvanish.hook.Hook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

/** Removes a disguise when its wearer vanishes and blocks disguising while vanished. */
public final class LibsDisguisesHook implements Hook {

    private final AdvancedVanish plugin;

    public LibsDisguisesHook(AdvancedVanish plugin) {
        this.plugin = plugin;
    }

    @Override
    public String requiredPlugin() {
        return "LibsDisguises";
    }

    @EventHandler
    public void onVanish(PlayerVanishEvent event) {
        Player player = event.getPlayer();
        if (DisguiseAPI.isDisguised(player)) {
            DisguiseAPI.undisguiseToAll(player);
            plugin.messages().send(player, "disguise-removed-because-vanish");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onDisguise(DisguiseEvent event) {
        if (event.getEntity() instanceof Player player && plugin.vanish().isVanished(player)) {
            event.setCancelled(true);
            plugin.messages().send(player, "disguise-removed-because-vanish");
        }
    }
}
