package net.busybee.advancedvanish.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.busybee.advancedvanish.AdvancedVanish;
import net.busybee.advancedvanish.config.Settings;
import net.busybee.advancedvanish.vanish.VanishManager;
import net.busybee.advancedvanish.vanish.VanishStateStore;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

/**
 * Join/quit handling and the {@code when-vanished} rules. Every handler checks the vanished set
 * first (one hash lookup) and returns straight away for everyone else.
 */
public final class VanishListener implements Listener {

    private final AdvancedVanish plugin;
    private final VanishManager vanish;
    private final SilentContainers containers;

    public VanishListener(AdvancedVanish plugin, SilentContainers containers) {
        this.plugin = plugin;
        this.vanish = plugin.vanish();
        this.containers = containers;
    }

    // Join and quit

    /** Runs first, so other plugins' join handlers already see the player as vanished. */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Settings settings = plugin.settings();
        VanishStateStore states = plugin.states();
        Boolean synced = plugin.sync().takeLoginState(uuid);

        boolean doVanish = false;
        if (player.hasPermission(settings.permVanish)) {
            if (plugin.sync().isEnabled() && synced != null) {
                doVanish = synced;
            } else if (settings.keepVanishState && states.peek(uuid) != null) {
                doVanish = states.take(uuid);
            } else if (settings.vanishOnJoin || player.hasPermission(settings.permJoinVanished)) {
                doVanish = true;
            }
        }

        if (doVanish) {
            vanish.vanish(player, true).thenAccept(vanished -> {
                if (vanished) plugin.messages().send(player, "vanish-on");
            });
        }

        vanish.hideVanishedFrom(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoinMessage(PlayerJoinEvent event) {
        if (!plugin.settings().joinMessages && vanish.isVanished(event.getPlayer())) event.joinMessage(null);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuitMessage(PlayerQuitEvent event) {
        if (!plugin.settings().leaveMessages && vanish.isVanished(event.getPlayer())) event.quitMessage(null);
    }

    /** Runs last, so other plugins' quit handlers still see the player as vanished. */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        boolean vanished = vanish.isVanished(player);
        if (vanished || player.hasPermission(plugin.settings().permVanish)) {
            plugin.states().put(player.getUniqueId(), vanished);
        }
        if (vanished) vanish.unVanish(player, true);
    }

    // when-vanished rules

    @EventHandler(priority = EventPriority.LOW)
    public void onChat(AsyncChatEvent event) {
        deny(event, event.getPlayer(), plugin.settings().sendMessages, "cannot-chat-while-vanished", false);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlace(BlockPlaceEvent event) {
        deny(event, event.getPlayer(), plugin.settings().placeBlocks, "cannot-place-blocks-while-vanished", true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBreak(BlockBreakEvent event) {
        deny(event, event.getPlayer(), plugin.settings().breakBlocks, "cannot-break-blocks-while-vanished", true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!vanish.isVanished(player)) return;
        Settings settings = plugin.settings();

        Block block = event.getClickedBlock();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && block != null && !settings.openAndUseChests
                && containers.tryOpen(player, block)) {
            event.setCancelled(true);
            return;
        }

        deny(event, player, settings.interact, null, true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            deny(event, player, plugin.settings().pickUpItems, null, true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDrop(PlayerDropItemEvent event) {
        deny(event, event.getPlayer(), plugin.settings().dropItems, "cannot-drop-items-while-vanished", true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onHunger(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player player) {
            deny(event, player, plugin.settings().loseHunger, null, false);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onTarget(EntityTargetLivingEntityEvent event) {
        if (event.getTarget() instanceof Player player) {
            deny(event, player, plugin.settings().mobTargeting, null, false);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent event) {
        Player attacker = attacker(event.getDamager());
        if (attacker == null || !vanish.isVanished(attacker)) return;

        // Hitting a player always gives a vanished attacker away, so it is never allowed
        boolean allowed = !(event.getEntity() instanceof Player)
                && (plugin.settings().attackEntities || vanish.canInteract(attacker));
        if (!allowed) {
            plugin.messages().send(attacker, "cannot-attack-entities-while-vanished");
            event.setCancelled(true);
        }
    }

    /** Vanished players take no damage, except from entities when receive-damage-from-entities is on. */
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player) || !vanish.isVanished(player)) return;
        if (event instanceof EntityDamageByEntityEvent && plugin.settings().receiveDamageFromEntities) return;
        event.setCancelled(true);
    }

    private static Player attacker(Entity damager) {
        if (damager instanceof Player player) return player;
        if (damager instanceof Projectile projectile && projectile.getShooter() instanceof Player shooter) return shooter;
        return null;
    }

    /**
     * Cancels {@code event} when {@code player} is vanished and the action is not allowed.
     *
     * @param messageKey        message to send, or null for none
     * @param interactOverrides whether {@code /vanish interact} lifts the rule
     */
    private void deny(Cancellable event, Player player, boolean allowed, String messageKey, boolean interactOverrides) {
        if (allowed || !vanish.isVanished(player)) return;
        if (interactOverrides && vanish.canInteract(player)) return;
        if (messageKey != null) plugin.messages().send(player, messageKey);
        event.setCancelled(true);
    }
}
