package net.busybee.advancedvanish.config;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

/**
 * Loads config.yml. When {@code config-version} differs from the bundled one, the old file is
 * backed up, replaced by the new default, and every value that still exists with the same type is
 * copied across. Configs older than version 8 have their legacy {@code &} colours converted to
 * MiniMessage on the way.
 */
public final class ConfigLoader {

    /** The version that introduced MiniMessage formatting. */
    private static final int MINIMESSAGE_VERSION = 8;

    private ConfigLoader() {
    }

    /** Returns the loaded config and whether it was migrated. */
    public static Result load(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists()) plugin.saveDefaultConfig();
        plugin.reloadConfig();

        int bundledVersion = bundledVersion(plugin);
        int currentVersion = plugin.getConfig().getInt("config-version", bundledVersion);
        if (currentVersion == bundledVersion) {
            return new Result(plugin.getConfig(), null);
        }

        File backup = new File(plugin.getDataFolder(), "config-backup-" + System.currentTimeMillis() + ".yml");
        try {
            Files.copy(file.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
            YamlConfiguration old = YamlConfiguration.loadConfiguration(backup);

            Files.delete(file.toPath());
            plugin.saveDefaultConfig();
            plugin.reloadConfig();

            FileConfiguration fresh = plugin.getConfig();
            merge(old, fresh, currentVersion);
            if (fresh instanceof YamlConfiguration yaml) yaml.options().width(120);
            plugin.saveConfig();
            plugin.reloadConfig();
        } catch (IOException e) {
            plugin.getLogger().severe("Could not migrate config.yml: " + e.getMessage());
            return new Result(plugin.getConfig(), null);
        }
        return new Result(plugin.getConfig(), backup.getName());
    }

    /** Copies every leaf value of {@code old} into {@code fresh} where the key exists in both with the same type. */
    static void merge(ConfigurationSection old, ConfigurationSection fresh, int oldVersion) {
        for (String key : old.getKeys(true)) {
            if (key.equals("config-version") || old.isConfigurationSection(key)) continue;
            Object oldValue = old.get(key);
            Object newValue = fresh.get(key);
            if (oldValue == null || newValue == null || oldValue.getClass() != newValue.getClass()) continue;

            if (oldValue instanceof String s && oldVersion < MINIMESSAGE_VERSION) {
                fresh.set(key, MiniMessage.miniMessage().serialize(LegacyComponentSerializer.legacyAmpersand().deserialize(s)));
            } else {
                fresh.set(key, oldValue);
            }
        }
    }

    private static int bundledVersion(JavaPlugin plugin) {
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(plugin.getResource("config.yml")), StandardCharsets.UTF_8)) {
            YamlConfiguration bundled = new YamlConfiguration();
            bundled.load(reader);
            return bundled.getInt("config-version");
        } catch (IOException | InvalidConfigurationException | NullPointerException e) {
            throw new IllegalStateException("The bundled config.yml is missing or broken", e);
        }
    }

    /**
     * @param config     the loaded config
     * @param backupName the backup file name when the config was migrated, otherwise null
     */
    public record Result(FileConfiguration config, String backupName) {
    }
}
