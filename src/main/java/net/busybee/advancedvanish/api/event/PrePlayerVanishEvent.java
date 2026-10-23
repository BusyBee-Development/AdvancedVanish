package net.busybee.advancedvanish.api.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called before a player is vanished. Cancel it to keep them visible.
 */
public class PrePlayerVanishEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final boolean onJoin;
    private boolean cancelled;

    public PrePlayerVanishEvent(@NotNull Player player, boolean onJoin) {
        super(player);
        this.onJoin = onJoin;
    }

    /** True when the player is being vanished because they joined. */
    public boolean isOnJoin() {
        return onJoin;
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
