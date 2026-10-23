package net.busybee.advancedvanish.vanish;

import net.busybee.advancedvanish.AdvancedVanish;
import net.busybee.advancedvanish.api.event.PlayerUnVanishEvent;
import net.busybee.advancedvanish.api.event.PlayerVanishEvent;
import net.busybee.advancedvanish.api.event.PrePlayerUnVanishEvent;
import net.busybee.advancedvanish.api.event.PrePlayerVanishEvent;
import net.busybee.advancedvanish.config.Messages;
import net.busybee.advancedvanish.config.PotionSpec;
import net.busybee.advancedvanish.config.Settings;
import net.busybee.advancedvanish.priority.PriorityProvider;
import net.busybee.advancedvanish.util.Schedulers;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Who is vanished, and the work of hiding and showing them. Every change to a player runs on that
 * player's own thread, and every hide/show runs on the observer's thread, so it is safe on Folia.
 */
public final class VanishManager {

    /** Metadata key other plugins (EssentialsX, DiscordSRV, TAB, …) check to detect vanish. */
    public static final String METADATA_KEY = "vanished";

    /** Delay before join-time potion effects are applied, so the join does not wipe them. */
    private static final long JOIN_EFFECT_DELAY_TICKS = 10L;

    private final AdvancedVanish plugin;
    private final Set<UUID> vanished = ConcurrentHashMap.newKeySet();
    private final Set<UUID> vanishedView = Collections.unmodifiableSet(vanished);
    private final Set<UUID> interactEnabled = ConcurrentHashMap.newKeySet();
    private final Set<UUID> flightGranted = ConcurrentHashMap.newKeySet();
    private final Map<UUID, List<PotionEffect>> previousEffects = new ConcurrentHashMap<>();

    public VanishManager(AdvancedVanish plugin) {
        this.plugin = plugin;
    }

    public boolean isVanished(Player player) {
        return vanished.contains(player.getUniqueId());
    }

    public boolean isVanished(UUID uuid) {
        return vanished.contains(uuid);
    }

    /** Live, read-only view of vanished player UUIDs. Only online players are ever in it. */
    public Set<UUID> getVanished() {
        return vanishedView;
    }

    public int vanishedCount() {
        return vanished.size();
    }

    public boolean canInteract(Player player) {
        return interactEnabled.contains(player.getUniqueId());
    }

    /** Toggles {@code /vanish interact}. Returns the new state. */
    public boolean toggleInteract(Player player) {
        UUID uuid = player.getUniqueId();
        if (interactEnabled.remove(uuid)) return false;
        interactEnabled.add(uuid);
        return true;
    }

    /**
     * Whether {@code observer} can see {@code target}. A vanished player is visible only to players
     * with the vanish permission whose priority is equal or higher, and only while priority is in use.
     */
    public boolean canSee(Player observer, Player target) {
        if (!isVanished(target) || observer.equals(target)) return true;
        PriorityProvider priority = plugin.activePriority();
        return priority != null && canSee(observer, priority, priority.getPriority(target));
    }

    private boolean canSee(Player observer, PriorityProvider priority, int targetPriority) {
        return priority != null
                && observer.hasPermission(plugin.settings().permVanish)
                && priority.getPriority(observer) >= targetPriority;
    }

    /** Vanishes a player. Completes with whether they are vanished afterwards (false if an event cancelled it). */
    public CompletableFuture<Boolean> vanish(Player player, boolean onJoin) {
        return Schedulers.supply(plugin, player, () -> {
            applyVanish(player, onJoin);
            return isVanished(player);
        }, false);
    }

    /** Unvanishes a player. Completes with whether they are still vanished afterwards. */
    public CompletableFuture<Boolean> unVanish(Player player, boolean onLeave) {
        return Schedulers.supply(plugin, player, () -> {
            applyUnVanish(player, onLeave, false);
            return isVanished(player);
        }, false);
    }

    /** Called on the joining player's thread: hides every vanished player they are not allowed to see. */
    public void hideVanishedFrom(Player observer) {
        for (UUID uuid : vanished) {
            Player target = Bukkit.getPlayer(uuid);
            if (target != null && !target.equals(observer) && !canSee(observer, target)) {
                observer.hidePlayer(plugin, target);
            }
        }
    }

    /**
     * Restores every vanished player on plugin disable: effects, flight and metadata go back, quit
     * style ({@code onLeave = true}), so nothing is broadcast and cross-server state is kept.
     */
    public void shutdown() {
        for (UUID uuid : List.copyOf(vanished)) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            try {
                applyUnVanish(player, true, true);
            } catch (Throwable t) {
                plugin.getLogger().log(Level.WARNING, "Could not restore " + player.getName() + " on disable", t);
            }
        }
        vanished.clear();
        interactEnabled.clear();
        flightGranted.clear();
        previousEffects.clear();
    }

    private void applyVanish(Player player, boolean onJoin) {
        if (isVanished(player)) return;

        PrePlayerVanishEvent pre = new PrePlayerVanishEvent(player, onJoin);
        Bukkit.getPluginManager().callEvent(pre);
        if (pre.isCancelled()) return;

        Settings settings = plugin.settings();
        UUID uuid = player.getUniqueId();
        vanished.add(uuid);
        // Metadata is deprecated on 26.x but is still how other plugins detect vanish, so keep setting it
        player.setMetadata(METADATA_KEY, new FixedMetadataValue(plugin, true));

        applyEffects(player, settings, onJoin);

        if (settings.flyEnable && !player.getAllowFlight()) {
            flightGranted.add(uuid);
            player.setAllowFlight(true);
        }

        hideFromOthers(player);

        if (!onJoin && settings.fakeLeaveOnVanish) {
            broadcast(Messages.parse(settings.fakeLeaveMessage, "%player-name%", player.getName()));
        }

        Bukkit.getPluginManager().callEvent(new PlayerVanishEvent(player, onJoin));
    }

    private void applyUnVanish(Player player, boolean onLeave, boolean disabling) {
        if (!isVanished(player)) return;

        PrePlayerUnVanishEvent pre = new PrePlayerUnVanishEvent(player, onLeave);
        Bukkit.getPluginManager().callEvent(pre);
        if (pre.isCancelled()) return;

        Settings settings = plugin.settings();
        UUID uuid = player.getUniqueId();
        vanished.remove(uuid);
        interactEnabled.remove(uuid);
        player.removeMetadata(METADATA_KEY, plugin);

        restoreEffects(player);

        if (flightGranted.remove(uuid)
                && player.getGameMode() != GameMode.CREATIVE
                && player.getGameMode() != GameMode.SPECTATOR
                && !settings.flyKeepOnUnvanish
                && !player.hasPermission(settings.permKeepFly)) {
            player.setFlying(false);
            player.setAllowFlight(false);
        }

        if (disabling) {
            // Schedulers no longer run once the plugin is disabling; everyone is leaving anyway if the server stops
            if (!Bukkit.isStopping()) {
                for (Player observer : Bukkit.getOnlinePlayers()) observer.showPlayer(plugin, player);
            }
        } else {
            showToOthers(player);
        }

        if (!onLeave && settings.fakeJoinOnUnvanish) {
            broadcast(Messages.parse(settings.fakeJoinMessage, "%player-name%", player.getName()));
        }

        Bukkit.getPluginManager().callEvent(new PlayerUnVanishEvent(player, onLeave));
    }

    private void hideFromOthers(Player target) {
        PriorityProvider priority = plugin.activePriority();
        int targetPriority = priority == null ? 0 : priority.getPriority(target);

        for (Player observer : Bukkit.getOnlinePlayers()) {
            if (observer.equals(target) || canSee(observer, priority, targetPriority)) continue;
            Schedulers.run(plugin, observer, () -> {
                if (isVanished(target) && target.isOnline()) observer.hidePlayer(plugin, target);
            });
        }
    }

    private void showToOthers(Player target) {
        for (Player observer : Bukkit.getOnlinePlayers()) {
            if (observer.equals(target)) continue;
            Schedulers.run(plugin, observer, () -> {
                if (!isVanished(target)) observer.showPlayer(plugin, target);
            });
        }
    }

    private void applyEffects(Player player, Settings settings, boolean onJoin) {
        if (settings.potionEffects.isEmpty()) return;

        List<PotionEffect> previous = new ArrayList<>();
        List<PotionEffect> toApply = new ArrayList<>();
        for (PotionSpec spec : settings.potionEffects) {
            PotionEffectType type = Registry.EFFECT.get(spec.key());
            if (type == null) continue;

            PotionEffect existing = player.getPotionEffect(type);
            // A zero-duration marker means "had nothing", so unvanish only removes ours
            previous.add(existing != null ? existing : new PotionEffect(type, 0, 0));
            toApply.add(new PotionEffect(type, PotionEffect.INFINITE_DURATION, spec.amplifier(), false, false, true));
        }
        if (previous.isEmpty()) return;
        previousEffects.put(player.getUniqueId(), previous);

        if (onJoin) {
            Schedulers.runLater(plugin, player, () -> {
                if (isVanished(player)) player.addPotionEffects(toApply);
            }, JOIN_EFFECT_DELAY_TICKS);
        } else {
            player.addPotionEffects(toApply);
        }
    }

    private void restoreEffects(Player player) {
        List<PotionEffect> previous = previousEffects.remove(player.getUniqueId());
        if (previous == null) return;
        for (PotionEffect effect : previous) {
            player.removePotionEffect(effect.getType());
            if (effect.getDuration() != 0) player.addPotionEffect(effect);
        }
    }

    private static void broadcast(Component message) {
        for (Player online : Bukkit.getOnlinePlayers()) online.sendMessage(message);
    }
}
