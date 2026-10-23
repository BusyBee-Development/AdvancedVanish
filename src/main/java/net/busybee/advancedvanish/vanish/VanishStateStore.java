package net.busybee.advancedvanish.vanish;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The vanish state a player had when they last left, for {@code keep-vanish-state}. With
 * {@code keep-vanish-state-persistent} it is also kept in {@code vanishStates.json} across restarts.
 */
public final class VanishStateStore {

    private static final Gson GSON = new Gson();
    private static final Type TYPE = new TypeToken<Map<UUID, Boolean>>() {
    }.getType();

    private final Map<UUID, Boolean> states = new ConcurrentHashMap<>();
    private final Path file;

    public VanishStateStore(Path dataFolder) {
        this.file = dataFolder.resolve("vanishStates.json");
    }

    public void put(UUID uuid, boolean vanished) {
        states.put(uuid, vanished);
    }

    /** Removes and returns the saved state, or null if there is none. */
    public Boolean take(UUID uuid) {
        return states.remove(uuid);
    }

    public Boolean peek(UUID uuid) {
        return states.get(uuid);
    }

    /** Merges the file into memory. A missing or broken file leaves memory as it is. */
    public void load() throws IOException {
        if (!Files.exists(file)) return;
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            Map<UUID, Boolean> loaded = GSON.fromJson(reader, TYPE);
            if (loaded != null) {
                loaded.forEach((uuid, state) -> {
                    if (uuid != null && state != null) states.put(uuid, state);
                });
            }
        } catch (JsonParseException e) {
            throw new IOException("vanishStates.json is not valid JSON: " + e.getMessage(), e);
        }
    }

    /** Writes memory to the file through a temp file, so a crash mid-write never leaves it half written. */
    public void save() throws IOException {
        Files.createDirectories(file.getParent());
        Path temp = file.resolveSibling(file.getFileName() + ".tmp");
        try (Writer writer = Files.newBufferedWriter(temp, StandardCharsets.UTF_8)) {
            GSON.toJson(Map.copyOf(states), TYPE, writer);
        }
        try {
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
