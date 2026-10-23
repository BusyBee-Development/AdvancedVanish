package net.busybee.advancedvanish.hook.impl;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import net.busybee.advancedvanish.AdvancedVanish;
import net.busybee.advancedvanish.api.event.PlayerUnVanishEvent;
import net.busybee.advancedvanish.api.event.PlayerVanishEvent;
import net.busybee.advancedvanish.hook.Hook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.UUID;

/** Marks vanished players as hidden in EssentialsX, so /list, /whois, /msg and similar commands skip them. */
public final class EssentialsHook implements Hook {

    private final AdvancedVanish plugin;
    private final Essentials essentials;

    public EssentialsHook(AdvancedVanish plugin) {
        this.plugin = plugin;
        this.essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
    }

    @Override
    public String requiredPlugin() {
        return "Essentials";
    }

    @Override
    public void enable() {
        for (UUID uuid : plugin.vanish().getVanished()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) setHidden(player, true);
        }
    }

    @EventHandler
    public void onVanish(PlayerVanishEvent event) {
        setHidden(event.getPlayer(), true);
    }

    @EventHandler
    public void onUnVanish(PlayerUnVanishEvent event) {
        setHidden(event.getPlayer(), false);
    }

    private void setHidden(Player player, boolean hidden) {
        User user = essentials.getUser(player);
        if (user != null) user.setHidden(hidden);
    }
}
