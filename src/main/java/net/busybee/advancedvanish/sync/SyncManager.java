package net.busybee.advancedvanish.sync;

import net.busybee.advancedvanish.AdvancedVanish;
import net.busybee.advancedvanish.api.event.PlayerUnVanishEvent;
import net.busybee.advancedvanish.api.event.PlayerVanishEvent;
import net.busybee.advancedvanish.config.Settings;
import net.busybee.advancedvanish.util.Schedulers;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Cross-server vanish sync. The state is read while the player logs in (already off the main
 * thread) and written whenever they vanish or unvanish by command, never on join, quit or shutdown.
 */
public final class SyncManager implements Listener {

    private final AdvancedVanish plugin;
    private final Map<UUID, Boolean> loginStates = new ConcurrentHashMap<>();
    private volatile SyncStore store;

    public SyncManager(AdvancedVanish plugin) {
        this.plugin = plugin;
    }

    /** Closes any open connection and connects with the given settings. Blocks. Returns the backend name, or null if off. */
    public synchronized @Nullable String start(Settings settings) {
        close();
        if (!settings.syncEnabled) return null;

        SyncStore candidate = switch (settings.syncMode) {
            case "redis" -> new RedisSyncStore(settings);
            case "sql", "mysql" -> new SqlSyncStore(settings);
            default -> null;
        };
        if (candidate == null) {
            plugin.getLogger().warning("cross-server-support.mode must be \"redis\" or \"sql\", not \"" + settings.syncMode + "\". Sync is off.");
            return null;
        }

        try {
            candidate.connect();
        } catch (Throwable t) {
            plugin.getLogger().log(Level.SEVERE, "Could not connect to " + candidate.name() + " for cross-server sync. Sync is off.", t);
            candidate.close();
            return null;
        }
        store = candidate;
        return candidate.name();
    }

    /** Reconnects off the server threads, for {@code /vanish reload}. */
    public void restartAsync(Settings settings) {
        Schedulers.async(plugin, () -> start(settings));
    }

    public synchronized void close() {
        SyncStore current = store;
        store = null;
        if (current != null) current.close();
        loginStates.clear();
    }

    public boolean isEnabled() {
        return store != null;
    }

    /** The state read at login, removed once used. Null when sync is off or the read failed. */
    public @Nullable Boolean takeLoginState(UUID uuid) {
        return loginStates.remove(uuid);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        SyncStore current = store;
        if (current == null || event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) return;
        try {
            Boolean state = current.get(event.getUniqueId());
            // No saved state means "not vanished": with sync on, the network decides, not vanish-on-join
            loginStates.put(event.getUniqueId(), state != null && state);
        } catch (Throwable t) {
            plugin.getLogger().log(Level.WARNING, "Could not read the vanish state of " + event.getName() + " from " + current.name(), t);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        loginStates.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onVanish(PlayerVanishEvent event) {
        if (!event.isOnJoin()) write(event.getPlayer().getUniqueId(), true);
    }

    @EventHandler
    public void onUnVanish(PlayerUnVanishEvent event) {
        if (!event.isOnLeave()) write(event.getPlayer().getUniqueId(), false);
    }

    private void write(UUID uuid, boolean vanished) {
        SyncStore current = store;
        if (current == null) return;
        Schedulers.async(plugin, () -> {
            try {
                current.set(uuid, vanished);
            } catch (Throwable t) {
                plugin.getLogger().log(Level.WARNING, "Could not save a vanish state to " + current.name(), t);
            }
        });
    }
}
