package net.busybee.advancedvanish.config;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Checks the bundled config.yml against the defaults in {@link Settings}, and the migration merge. */
class ConfigTest {

    private static YamlConfiguration bundled() throws IOException, InvalidConfigurationException {
        try (Reader reader = new InputStreamReader(
                Objects.requireNonNull(ConfigTest.class.getClassLoader().getResourceAsStream("config.yml")),
                StandardCharsets.UTF_8)) {
            YamlConfiguration config = new YamlConfiguration();
            config.load(reader);
            return config;
        }
    }

    @Test
    void bundledConfigMatchesCodeDefaults() throws Exception {
        Settings fromFile = new Settings(bundled());
        Settings fromDefaults = new Settings(new YamlConfiguration());

        for (var field : Settings.class.getFields()) {
            if (field.getName().equals("potionEffects") || field.getName().equals("invalidPotionEffects")) continue;
            assertEquals(field.get(fromDefaults), field.get(fromFile), "default for " + field.getName());
        }
    }

    @Test
    void bundledConfigHasVersionAndValidEffects() throws Exception {
        YamlConfiguration config = bundled();
        assertTrue(config.getInt("config-version") > 0, "config-version was filtered in");
        Settings settings = new Settings(config);
        assertEquals(2, settings.potionEffects.size());
        assertTrue(settings.invalidPotionEffects.isEmpty());
    }

    @Test
    void mergeKeepsValuesOfTheSameType() {
        YamlConfiguration old = new YamlConfiguration();
        old.set("vanish-on-join", false);
        old.set("messages.vanish-on", "<green>Poof");
        old.set("priority.enable", "yes");       // wrong type: not copied
        old.set("removed-option", true);         // gone from the new file: not copied
        old.set("config-version", 9);

        YamlConfiguration fresh = new YamlConfiguration();
        fresh.set("vanish-on-join", true);
        fresh.set("messages.vanish-on", "You are now in vanish.");
        fresh.set("priority.enable", true);
        fresh.set("config-version", 10);

        ConfigLoader.merge(old, fresh, 9);

        assertFalse(fresh.getBoolean("vanish-on-join"));
        assertEquals("<green>Poof", fresh.getString("messages.vanish-on"));
        assertTrue(fresh.getBoolean("priority.enable"));
        assertFalse(fresh.contains("removed-option"));
        assertEquals(10, fresh.getInt("config-version"));
    }

    @Test
    void mergeConvertsLegacyColoursFromVersionsBeforeMiniMessage() {
        YamlConfiguration old = new YamlConfiguration();
        old.set("messages.vanish-on", "&aYou vanished");
        YamlConfiguration fresh = new YamlConfiguration();
        fresh.set("messages.vanish-on", "");

        ConfigLoader.merge(old, fresh, 7);

        assertEquals("<green>You vanished", fresh.getString("messages.vanish-on"));
    }

    @Test
    void placeholdersAreReplacedInOrder() {
        assertEquals("Set Steve's vanish to on",
                Messages.apply("Set %target-name%'s vanish to %vanish-status%", "%target-name%", "Steve", "%vanish-status%", "on"));
    }
}
