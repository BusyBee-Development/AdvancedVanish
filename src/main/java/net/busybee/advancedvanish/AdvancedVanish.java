package net.busybee.advancedvanish;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.busybee.advancedvanish.command.VanishCommand;
import net.busybee.advancedvanish.config.ConfigLoader;
import net.busybee.advancedvanish.config.Messages;
import net.busybee.advancedvanish.config.PotionSpec;
import net.busybee.advancedvanish.config.Settings;
import net.busybee.advancedvanish.hook.HookManager;
import net.busybee.advancedvanish.listener.SilentContainers;
import net.busybee.advancedvanish.listener.VanishListener;
import net.busybee.advancedvanish.priority.LuckPermsPriority;
import net.busybee.advancedvanish.priority.PriorityProvider;
import net.busybee.advancedvanish.sync.SyncManager;
import net.busybee.advancedvanish.util.StartupSummary;
import net.busybee.advancedvanish.util.UpdateChecker;
import net.busybee.advancedvanish.vanish.VanishManager;
import net.busybee.advancedvanish.vanish.VanishStateStore;
import org.bukkit.Registry;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.logging.Level;

/**
 * AdvancedVanish, originally written by quantiom (MIT), rewritten for Paper and Folia by BusyBee.
 */
public final class AdvancedVanish extends JavaPlugin {

    private static AdvancedVanish instance;

    private volatile Settings settings;
    private volatile Messages messages;
    private volatile PriorityProvider priority;
    private VanishManager vanish;
    private VanishStateStore states;
    private SyncManager sync;
    private HookManager hooks;

    /** The running plugin, or null while it is disabled. */
    public static @Nullable AdvancedVanish getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        long startedAt = System.currentTimeMillis();
        instance = this;

        applyConfig(ConfigLoader.load(this));

        vanish = new VanishManager(this);
        states = new VanishStateStore(getDataFolder().toPath());
        loadStates();

        sync = new SyncManager(this);
        silenceHikari();
        String syncBackend = sync.start(settings);

        setupPriority();

        SilentContainers containers = new SilentContainers(this);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(containers, this);
        pm.registerEvents(new VanishListener(this, containers), this);
        pm.registerEvents(sync, this);

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                event -> new VanishCommand(this).register(event.registrar()));

        hooks = new HookManager(this);
        hooks.enable(settings);

        if (settings.checkForUpdates) UpdateChecker.check(this);

        new StartupSummary(this)
                .row("Storage", syncBackend != null ? syncBackend : persistent() ? "File" : "None")
                .row("Priority", activePriority() == null ? "off" : priority.name())
                .hooks(hooks.hookedPlugins())
                .print(startedAt);
    }

    @Override
    public void onDisable() {
        if (vanish != null) {
            if (persistent()) {
                for (Player player : getServer().getOnlinePlayers()) {
                    boolean vanished = vanish.isVanished(player);
                    if (vanished || player.hasPermission(settings.permVanish)) states.put(player.getUniqueId(), vanished);
                }
            }
            vanish.shutdown();
        }
        if (persistent()) {
            try {
                states.save();
            } catch (IOException e) {
                getLogger().log(Level.WARNING, "Could not save vanishStates.json", e);
            }
        }
        if (hooks != null) hooks.disable();
        if (sync != null) sync.close();
        instance = null;
    }

    /** {@code /vanish reload}: config, messages, priority and hooks now; the sync connection in the background. */
    public void reload() {
        applyConfig(ConfigLoader.load(this));
        loadStates();
        setupPriority();
        hooks.reload(settings);
        sync.restartAsync(settings);
    }

    public Settings settings() {
        return settings;
    }

    public Messages messages() {
        return messages;
    }

    public VanishManager vanish() {
        return vanish;
    }

    public VanishStateStore states() {
        return states;
    }

    public SyncManager sync() {
        return sync;
    }

    /** The priority provider when {@code priority.enable} is on and a supported permissions plugin is present. */
    public @Nullable PriorityProvider activePriority() {
        return settings.priorityEnabled ? priority : null;
    }

    private void applyConfig(ConfigLoader.Result result) {
        if (result.backupName() != null) {
            getLogger().warning("config.yml was updated to the new format. Your old file was saved as "
                    + result.backupName() + " and its values were copied over.");
        }
        FileConfiguration config = result.config();
        Settings loaded = new Settings(config);
        for (String raw : loaded.invalidPotionEffects) {
            getLogger().warning("Ignoring when-vanished.give-potion-effects entry \"" + raw + "\": use NAME:LEVEL, e.g. INVISIBILITY:1");
        }
        for (PotionSpec spec : loaded.potionEffects) {
            if (Registry.EFFECT.get(spec.key()) == null) {
                getLogger().warning("Ignoring unknown potion effect \"" + spec.key() + "\" in when-vanished.give-potion-effects");
            }
        }
        settings = loaded;
        messages = new Messages(config);
    }

    private boolean persistent() {
        return settings != null && settings.keepVanishState && settings.keepVanishStatePersistent;
    }

    private void loadStates() {
        if (!persistent()) return;
        try {
            states.load();
        } catch (IOException e) {
            getLogger().log(Level.WARNING, "Could not read vanishStates.json", e);
        }
    }

    private void setupPriority() {
        priority = null;
        if (!getServer().getPluginManager().isPluginEnabled("LuckPerms")) return;
        try {
            priority = new LuckPermsPriority(() -> settings.priorityMetaKey);
        } catch (Throwable t) {
            getLogger().log(Level.WARNING, "LuckPerms is installed but its API could not be loaded; vanish priority is off", t);
        }
    }

    /** HikariCP logs its pool start at INFO through log4j; keep it to warnings, per the startup log standard. */
    private void silenceHikari() {
        if (!settings.syncEnabled) return;
        try {
            org.apache.logging.log4j.core.config.Configurator.setLevel("com.zaxxer.hikari", org.apache.logging.log4j.Level.WARN);
        } catch (Throwable ignored) {
            // log4j-core not on the classpath; the INFO lines just stay visible
        }
    }
}
