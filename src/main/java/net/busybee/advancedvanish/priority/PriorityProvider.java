package net.busybee.advancedvanish.priority;

import org.bukkit.entity.Player;

/** Supplies a player's vanish priority. Higher priorities can see vanished players with an equal or lower one. */
public interface PriorityProvider {

    /** Name shown in the startup summary. */
    String name();

    int getPriority(Player player);
}
