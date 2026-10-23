package net.busybee.advancedvanish.hook.impl;

import dev.esophose.playerparticles.api.PlayerParticlesAPI;
import dev.esophose.playerparticles.particles.PPlayer;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.busybee.advancedvanish.AdvancedVanish;
import net.busybee.advancedvanish.api.event.PlayerVanishEvent;
import net.busybee.advancedvanish.hook.Hook;
import net.busybee.advancedvanish.util.Schedulers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.UUID;

/** Clears PlayerParticles effects from vanished players, and keeps clearing any they turn back on. */
public final class PlayerParticlesHook implements Hook {

    private static final long INTERVAL_TICKS = 20L;

    private final AdvancedVanish plugin;
    private ScheduledTask task;

    public PlayerParticlesHook(AdvancedVanish plugin) {
        this.plugin = plugin;
    }

    @Override
    public String requiredPlugin() {
        return "PlayerParticles";
    }

    @Override
    public void enable() {
        task = Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, t -> {
            for (UUID uuid : plugin.vanish().getVanished()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) Schedulers.run(plugin, player, () -> clear(player));
            }
        }, INTERVAL_TICKS, INTERVAL_TICKS);
    }

    @Override
    public void disable() {
        if (task != null) task.cancel();
        task = null;
    }

    @EventHandler
    public void onVanish(PlayerVanishEvent event) {
        clear(event.getPlayer());
    }

    private static void clear(Player player) {
        PPlayer pPlayer = PlayerParticlesAPI.getInstance().getPPlayer(player.getUniqueId());
        if (pPlayer != null) pPlayer.getActiveParticles().clear();
    }
}
