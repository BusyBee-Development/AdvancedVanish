package net.busybee.advancedvanish.config;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@code messages} and {@code command-handler-messages} sections. Placeholders are written as
 * {@code %name%} and replaced before the MiniMessage is parsed, same as earlier versions. Messages
 * without placeholders are parsed once on load.
 */
public final class Messages {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final Map<String, List<String>> raw = new HashMap<>();
    private final Map<String, List<Component>> parsed = new HashMap<>();
    private final Map<String, String> commandHandler = new HashMap<>();
    private final Component prefix;
    private final boolean commandHandlerPrefix;

    public Messages(ConfigurationSection config) {
        boolean prefixEnabled = config.getBoolean("messages.prefix.enabled", true);
        String prefixValue = config.getString("messages.prefix.value", "<red>[AdvancedVanish]<white> ");
        this.prefix = prefixEnabled ? MM.deserialize(prefixValue) : Component.empty();

        ConfigurationSection section = config.getConfigurationSection("messages");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                List<String> lines = new ArrayList<>();
                if (section.isList(key)) {
                    lines.addAll(section.getStringList(key));
                } else if (section.isString(key)) {
                    lines.add(section.getString(key));
                } else {
                    continue;
                }
                lines.removeIf(String::isEmpty);
                raw.put(key, List.copyOf(lines));
                parsed.put(key, lines.stream().map(MM::deserialize).toList());
            }
        }

        ConfigurationSection handler = config.getConfigurationSection("command-handler-messages");
        this.commandHandlerPrefix = handler == null || handler.getBoolean("use-prefix", true);
        if (handler != null) {
            for (String key : handler.getKeys(false)) {
                if (handler.isString(key)) commandHandler.put(key, handler.getString(key));
            }
        }
    }

    /** Sends a {@code messages.<key>} entry. Pairs are placeholder/value: {@code "%name%", "Steve"}. */
    public void send(Audience to, String key, String... placeholders) {
        if (placeholders.length == 0) {
            List<Component> lines = parsed.get(key);
            if (lines == null) {
                to.sendMessage(missing(key));
                return;
            }
            for (Component line : lines) to.sendMessage(prefix.append(line));
            return;
        }

        List<String> lines = raw.get(key);
        if (lines == null) {
            to.sendMessage(missing(key));
            return;
        }
        for (String line : lines) to.sendMessage(prefix.append(MM.deserialize(apply(line, placeholders))));
    }

    /** The first line of a message as a component, without prefix, or null if it is empty or missing. */
    public @Nullable Component component(String key) {
        List<Component> lines = parsed.get(key);
        return lines == null || lines.isEmpty() ? null : lines.getFirst();
    }

    /** Sends a {@code command-handler-messages.<key>} entry, falling back to the built-in default. */
    public void sendCommandHandler(Audience to, String key, String fallback, String... placeholders) {
        String line = commandHandler.getOrDefault(key, fallback);
        if (line.isEmpty()) return;
        Component message = MM.deserialize(apply(line, placeholders));
        to.sendMessage(commandHandlerPrefix ? prefix.append(message) : message);
    }

    /** Parses a free-standing MiniMessage string (fake join/leave lines) with placeholders. */
    public static Component parse(String line, String... placeholders) {
        return MM.deserialize(apply(line, placeholders));
    }

    static String apply(String line, String... placeholders) {
        for (int i = 0; i + 1 < placeholders.length; i += 2) {
            line = line.replace(placeholders[i], placeholders[i + 1]);
        }
        return line;
    }

    private static Component missing(String key) {
        return MM.deserialize("<gray>Message not found for <red>" + MM.escapeTags(key) + "<gray>.");
    }
}
