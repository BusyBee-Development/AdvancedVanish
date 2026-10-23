package net.busybee.advancedvanish.sync;

import java.util.UUID;

/** Where vanish states are shared between servers. Every method blocks; callers run them off the server threads. */
public interface SyncStore {

    /** Name shown in the startup summary. */
    String name();

    /** Connects and checks the connection. Throws if the backend can't be reached. */
    void connect() throws Exception;

    void close();

    /** The saved state, or null if this player has none. */
    Boolean get(UUID uuid) throws Exception;

    void set(UUID uuid, boolean vanished) throws Exception;
}
