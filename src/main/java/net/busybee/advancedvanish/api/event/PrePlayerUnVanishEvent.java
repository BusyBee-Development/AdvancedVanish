package net.busybee.advancedvanish.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called before a player is made visible again. Cancel it to keep them vanished.
 */
public class PrePlayerUnVanishEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final boolean onLeave;
    private boolean cancelled;

    public PrePlayerUnVanishEvent(@NotNull Player player, boolean onLeave) {
        super(player);
        this.onLeave = onLeave;
    }

    /** True when the player is being unvanished because they are leaving (quit or server shutdown). */
    public boolean isOnLeave() {
        return onLeave;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
