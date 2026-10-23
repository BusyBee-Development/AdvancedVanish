package net.busybee.advancedvanish.hook;

import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

/** An integration with another plugin or a Paper feature. Hooks are also listeners. */
public interface Hook extends Listener {

    /** The plugin this hook needs, or null when it only uses Paper. */
    @Nullable String requiredPlugin();

    default void enable() {
    }

    default void disable() {
    }
}
