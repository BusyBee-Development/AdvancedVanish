package net.busybee.advancedvanish.api.event;

import org.bukkit.entity.Player;

import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called after a player has been made visible again.
 */
public class PlayerUnVanishEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final boolean onLeave;

    public PlayerUnVanishEvent(@NotNull Player player, boolean onLeave) {
        super(player);
        this.onLeave = onLeave;
    }

    /** True when the player was unvanished because they left (quit or server shutdown). */
    public boolean isOnLeave() {
        return onLeave;
    }


    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
