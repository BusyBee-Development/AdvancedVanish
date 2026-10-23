package net.busybee.advancedvanish.listener;

import net.busybee.advancedvanish.AdvancedVanish;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

/**
 * Opens chests, barrels and shulker boxes for vanished players without the lid animation or
 * sound, by showing a read-only copy of the contents. Ender chests open the player's own
 * ender chest directly, since nobody else sees that one.
 */
public final class SilentContainers implements Listener {

    private final AdvancedVanish plugin;

    public SilentContainers(AdvancedVanish plugin) {
        this.plugin = plugin;
    }

    /** Opens {@code block} silently if it is a supported container. Returns false for anything else. */
    public boolean tryOpen(Player player, Block block) {
        Material type = block.getType();
        if (type == Material.ENDER_CHEST) {
            player.openInventory(player.getEnderChest());
            plugin.messages().send(player, "opening-container-silently", "%type%", "enderchest");
            return true;
        }

        String label = label(type);
        if (label == null) return false;

        BlockState state = block.getState(false);
        if (!(state instanceof Container container)) return false;

        Inventory source = container.getInventory();
        Holder holder = new Holder();
        Inventory copy = Bukkit.createInventory(holder, source.getSize(),
                Component.text(Character.toUpperCase(label.charAt(0)) + label.substring(1)));
        holder.inventory = copy;
        copy.setContents(source.getContents());

        player.openInventory(copy);
        plugin.messages().send(player, "opening-container-silently", "%type%", label);
        return true;
    }

    /** The {@code %type%} name for a container that animates when opened, or null. Covers copper chests on 1.21.9+. */
    private static String label(Material type) {
        String name = type.name();
        if (name.endsWith("SHULKER_BOX")) return "shulker";
        if (type == Material.BARREL) return "barrel";
        if (name.endsWith("CHEST")) return "chest";
        return null;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onClick(InventoryClickEvent event) {
        if (event.getView().getTopInventory().getHolder(false) instanceof Holder) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDrag(InventoryDragEvent event) {
        if (event.getView().getTopInventory().getHolder(false) instanceof Holder) event.setCancelled(true);
    }

    /** Marks an inventory as a silent copy, so clicks in it are cancelled. */
    private static final class Holder implements InventoryHolder {

        private Inventory inventory;

        @Override
        public @NotNull Inventory getInventory() {
            return inventory;
        }
    }
}
