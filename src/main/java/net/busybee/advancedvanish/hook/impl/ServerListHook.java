package net.busybee.advancedvanish.hook.impl;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import net.busybee.advancedvanish.AdvancedVanish;
import net.busybee.advancedvanish.hook.Hook;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.Set;
import java.util.UUID;

/** Takes vanished players out of the server-list player count and hover list. Uses the Paper ping event, no ProtocolLib. */
public final class ServerListHook implements Hook {

    private final AdvancedVanish plugin;

    public ServerListHook(AdvancedVanish plugin) {
        this.plugin = plugin;
    }

    @Override
    public String requiredPlugin() {
        return null;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPing(PaperServerListPingEvent event) {
        Set<UUID> vanished = plugin.vanish().getVanished();
        if (vanished.isEmpty()) return;
        event.setNumPlayers(Math.max(0, event.getNumPlayers() - vanished.size()));
        event.getListedPlayers().removeIf(info -> vanished.contains(info.id()));
    }
}
