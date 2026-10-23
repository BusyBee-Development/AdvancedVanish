package net.busybee.advancedvanish.config;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Every value in config.yml, read once per load so event handlers never touch the YAML tree.
 * Defaults match the bundled config.yml.
 */
public final class Settings {

    // General
    public final boolean checkForUpdates;
    public final boolean vanishOnJoin;
    public final boolean keepVanishState;
    public final boolean keepVanishStatePersistent;

    // Cross-server sync
    public final boolean syncEnabled;
    public final String syncMode;
    public final String redisHost;
    public final int redisPort;
    public final String redisAuth;
    public final String sqlHost;
    public final int sqlPort;
    public final String sqlUsername;
    public final String sqlPassword;
    public final String sqlDatabase;
    public final String sqlTablePrefix;

    // when-vanished
    public final boolean joinMessages;
    public final boolean leaveMessages;
    public final boolean sendMessages;
    public final boolean breakBlocks;
    public final boolean placeBlocks;
    public final boolean interact;
    public final boolean attackEntities;
    public final boolean receiveDamageFromEntities;
    public final boolean openAndUseChests;
    public final boolean pickUpItems;
    public final boolean dropItems;
    public final List<PotionSpec> potionEffects;
    public final List<String> invalidPotionEffects;
    public final boolean loseHunger;
    public final boolean mobTargeting;
    public final boolean flyEnable;
    public final boolean flyKeepOnUnvanish;

    // Permissions
    public final String permVanish;
    public final String permVersion;
    public final String permHelp;
    public final String permReload;
    public final String permInteract;
    public final String permPriority;
    public final String permList;
    public final String permStatus;
    public final String permToggleOther;
    public final String permSetOther;
    public final String permKeepFly;
    public final String permJoinVanished;

    // Fake join/leave messages
    public final boolean fakeJoinOnUnvanish;
    public final String fakeJoinMessage;
    public final boolean fakeLeaveOnVanish;
    public final String fakeLeaveMessage;

    // Hooks
    public final boolean hookEssentials;
    public final boolean hookServerList;
    public final boolean hookPlaceholders;
    public final boolean hookActionBar;
    public final boolean hookDiscordSrv;
    public final boolean hookDynmap;
    public final boolean hookSquaremap;
    public final boolean hookLibsDisguises;
    public final boolean hookPlayerParticles;

    // Placeholders
    public final String placeholderIdentifier;
    public final String placeholderIsVanished;
    public final String placeholderVanishedPlayers;
    public final String placeholderPlayerCount;

    // Priority
    public final boolean priorityEnabled;
    public final String priorityMetaKey;

    public Settings(ConfigurationSection c) {
        checkForUpdates = c.getBoolean("check-for-updates", true);
        vanishOnJoin = c.getBoolean("vanish-on-join", true);
        keepVanishState = c.getBoolean("keep-vanish-state", false);
        keepVanishStatePersistent = c.getBoolean("keep-vanish-state-persistent", false);

        syncEnabled = c.getBoolean("cross-server-support.enabled", false);
        syncMode = c.getString("cross-server-support.mode", "redis").toLowerCase(Locale.ROOT);
        redisHost = c.getString("cross-server-support.redis.ip", "127.0.0.1");
        redisPort = c.getInt("cross-server-support.redis.port", 6379);
        redisAuth = c.getString("cross-server-support.redis.auth", "");
        sqlHost = c.getString("cross-server-support.sql.ip", "127.0.0.1");
        sqlPort = c.getInt("cross-server-support.sql.port", 3306);
        sqlUsername = c.getString("cross-server-support.sql.username", "root");
        sqlPassword = c.getString("cross-server-support.sql.password", "");
        sqlDatabase = c.getString("cross-server-support.sql.database", "minecraft");
        sqlTablePrefix = c.getString("cross-server-support.sql.table-prefix", "advancedvanish_");

        String w = "when-vanished.";
        joinMessages = c.getBoolean(w + "join-messages", false);
        leaveMessages = c.getBoolean(w + "leave-messages", false);
        sendMessages = c.getBoolean(w + "send-messages", false);
        breakBlocks = c.getBoolean(w + "break-blocks", false);
        placeBlocks = c.getBoolean(w + "place-blocks", false);
        interact = c.getBoolean(w + "interact", false);
        attackEntities = c.getBoolean(w + "attack-entities", false);
        receiveDamageFromEntities = c.getBoolean(w + "receive-damage-from-entities", false);
        openAndUseChests = c.getBoolean(w + "open-and-use-chests", false);
        pickUpItems = c.getBoolean(w + "pick-up-items", false);
        dropItems = c.getBoolean(w + "drop-items", false);
        loseHunger = c.getBoolean(w + "lose-hunger", false);
        mobTargeting = c.getBoolean(w + "mob-targeting", false);
        flyEnable = c.getBoolean(w + "fly.enable", true);
        flyKeepOnUnvanish = c.getBoolean(w + "fly.keep-on-unvanish", false);

        List<PotionSpec> effects = new ArrayList<>();
        List<String> invalid = new ArrayList<>();
        for (String raw : c.getStringList(w + "give-potion-effects")) {
            PotionSpec spec = PotionSpec.parse(raw);
            if (spec != null) effects.add(spec);
            else invalid.add(raw);
        }
        potionEffects = List.copyOf(effects);
        invalidPotionEffects = List.copyOf(invalid);

        String p = "permissions.";
        permVanish = c.getString(p + "vanish", "advancedvanish.vanish");
        permVersion = c.getString(p + "version-command", "advancedvanish.version-command");
        permHelp = c.getString(p + "help-command", "advancedvanish.help-command");
        permReload = c.getString(p + "reload-config-command", "advancedvanish.reload-config-command");
        permInteract = c.getString(p + "interact-command", "advancedvanish.interact-command");
        permPriority = c.getString(p + "priority-command", "advancedvanish.priority-command");
        permList = c.getString(p + "list-command", "advancedvanish.list-command");
        permStatus = c.getString(p + "status-command", "advancedvanish.status-command");
        permToggleOther = c.getString(p + "toggle-other-command", "advancedvanish.toggle-other-command");
        permSetOther = c.getString(p + "set-other-command", "advancedvanish.set-other-command");
        permKeepFly = c.getString(p + "keep-fly-on-unvanish", "advancedvanish.keep-fly");
        permJoinVanished = c.getString(p + "join-vanished", "advancedvanish.join-vanished");

        String j = "join-leave-messages.";
        fakeJoinOnUnvanish = c.getBoolean(j + "fake-join-message-on-unvanish.enable", false);
        fakeJoinMessage = c.getString(j + "fake-join-message-on-unvanish.message", "<yellow>%player-name% has joined the game");
        fakeLeaveOnVanish = c.getBoolean(j + "fake-leave-message-on-vanish.enable", false);
        fakeLeaveMessage = c.getString(j + "fake-leave-message-on-vanish.message", "<yellow>%player-name% has left the game.");

        String h = "hooks.";
        hookEssentials = c.getBoolean(h + "essentials", true);
        hookServerList = c.getBoolean(h + "server-list", true);
        hookPlaceholders = c.getBoolean(h + "placeholders", true);
        hookActionBar = c.getBoolean(h + "actionbar", true);
        hookDiscordSrv = c.getBoolean(h + "discordsrv", true);
        hookDynmap = c.getBoolean(h + "dynmap", true);
        hookSquaremap = c.getBoolean(h + "squaremap", true);
        hookLibsDisguises = c.getBoolean(h + "libsdisguises", true);
        hookPlayerParticles = c.getBoolean(h + "playerparticles", true);

        placeholderIdentifier = c.getString("placeholders.identifier", "advancedvanish");
        placeholderIsVanished = c.getString("placeholders.is-vanished", "is_vanished").toLowerCase(Locale.ROOT);
        placeholderVanishedPlayers = c.getString("placeholders.vanished-players", "vanished_players").toLowerCase(Locale.ROOT);
        placeholderPlayerCount = c.getString("placeholders.player-count", "player_count").toLowerCase(Locale.ROOT);

        priorityEnabled = c.getBoolean("priority.enable", true);
        priorityMetaKey = c.getString("priority.meta-key", "advancedvanish-priority");
    }
}
