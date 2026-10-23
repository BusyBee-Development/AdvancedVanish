package net.busybee.advancedvanish.api;

import net.busybee.advancedvanish.AdvancedVanish;
import net.busybee.advancedvanish.priority.PriorityProvider;
import net.busybee.advancedvanish.vanish.VanishManager;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Public API for other plugins. All methods are safe to call from any thread; changes to a player
 * run on that player's own thread, so the vanish and unvanish methods return a future.
 *
 * <p>Listen to {@link net.busybee.advancedvanish.api.event.PrePlayerVanishEvent},
 * {@link net.busybee.advancedvanish.api.event.PlayerVanishEvent},
 * {@link net.busybee.advancedvanish.api.event.PrePlayerUnVanishEvent} and
 * {@link net.busybee.advancedvanish.api.event.PlayerUnVanishEvent} to react to changes.
 */
public final class AdvancedVanishAPI {

    private AdvancedVanishAPI() {
    }

    public static boolean isVanished(Player player) {
        return manager().isVanished(player);
    }

    public static boolean isVanished(UUID uuid) {
        return manager().isVanished(uuid);
    }

    /** Read-only live view of the UUIDs of vanished online players. */
    public static Set<UUID> getVanishedPlayers() {
        return manager().getVanished();
    }

    /** Vanishes a player. Completes with true if they are vanished afterwards (false if an event cancelled it). */
    public static CompletableFuture<Boolean> vanish(Player player) {
        return manager().vanish(player, false);
    }

    /** Unvanishes a player. Completes with true if they are still vanished afterwards. */
    public static CompletableFuture<Boolean> unVanish(Player player) {
        return manager().unVanish(player, false);
    }

    /** Whether {@code observer} is allowed to see {@code target}, taking vanish priority into account. */
    public static boolean canSee(Player observer, Player target) {
        return manager().canSee(observer, target);
    }

    /** The player's vanish priority, or 0 when priority is not in use. */
    public static int getPriority(Player player) {
        PriorityProvider priority = plugin().activePriority();
        return priority == null ? 0 : priority.getPriority(player);
    }

    private static VanishManager manager() {
        return plugin().vanish();
    }

    private static AdvancedVanish plugin() {
        AdvancedVanish plugin = AdvancedVanish.getInstance();
        if (plugin == null) throw new IllegalStateException("AdvancedVanish is not enabled");
        return plugin;
    }
}
