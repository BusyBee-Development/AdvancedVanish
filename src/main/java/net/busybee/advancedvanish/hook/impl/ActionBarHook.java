package net.busybee.advancedvanish.hook.impl;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.busybee.advancedvanish.AdvancedVanish;
import net.busybee.advancedvanish.api.event.PlayerUnVanishEvent;
import net.busybee.advancedvanish.api.event.PlayerVanishEvent;
import net.busybee.advancedvanish.hook.Hook;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/** Keeps {@code messages.action-bar} above the hotbar of every vanished player. */
public final class ActionBarHook implements Hook {

    /** The vanilla action bar fades after about 3 seconds, so refresh a little sooner. */
    private static final long REFRESH_MILLIS = 1500L;

    private final AdvancedVanish plugin;
    private ScheduledTask task;

    public ActionBarHook(AdvancedVanish plugin) {
        this.plugin = plugin;
    }

    @Override
    public String requiredPlugin() {
        return null;
    }

    @Override
    public void enable() {
        // Sending a packet is thread-safe, so one async timer covers every region
        task = Bukkit.getAsyncScheduler().runAtFixedRate(plugin, t -> {
            Component message = plugin.messages().component("action-bar");
            if (message == null) return;
            for (UUID uuid : plugin.vanish().getVanished()) {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null) player.sendActionBar(message);
            }
        }, REFRESH_MILLIS, REFRESH_MILLIS, TimeUnit.MILLISECONDS);
    }

    @Override
    public void disable() {
        if (task != null) task.cancel();
        task = null;
    }

    @EventHandler
    public void onVanish(PlayerVanishEvent event) {
        Component message = plugin.messages().component("action-bar");
        if (message != null) event.getPlayer().sendActionBar(message);
    }

    @EventHandler
    public void onUnVanish(PlayerUnVanishEvent event) {
        event.getPlayer().sendActionBar(Component.empty());
    }
}
