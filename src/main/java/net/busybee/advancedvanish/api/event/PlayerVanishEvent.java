package net.busybee.advancedvanish.api.event;

import org.bukkit.entity.Player;

import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called after a player has been vanished.
 */
public class PlayerVanishEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final boolean onJoin;

    public PlayerVanishEvent(@NotNull Player player, boolean onJoin) {
        super(player);
        this.onJoin = onJoin;
    }

    /** True when the player was vanished because they joined. */
    public boolean isOnJoin() {
        return onJoin;
    }


    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
