package net.busybee.advancedvanish.hook;

import net.busybee.advancedvanish.AdvancedVanish;
import net.busybee.advancedvanish.config.Settings;
import net.busybee.advancedvanish.hook.impl.ActionBarHook;
import net.busybee.advancedvanish.hook.impl.DiscordSrvHook;
import net.busybee.advancedvanish.hook.impl.DynmapHook;
import net.busybee.advancedvanish.hook.impl.EssentialsHook;
import net.busybee.advancedvanish.hook.impl.LibsDisguisesHook;
import net.busybee.advancedvanish.hook.impl.PlaceholderApiHook;
import net.busybee.advancedvanish.hook.impl.PlayerParticlesHook;
import net.busybee.advancedvanish.hook.impl.ServerListHook;
import net.busybee.advancedvanish.hook.impl.SquaremapHook;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;

/** Turns hooks on and off from the {@code hooks} section. A hook whose plugin is missing is skipped quietly. */
public final class HookManager {

    private final AdvancedVanish plugin;
    private final List<Hook> active = new ArrayList<>();

    public HookManager(AdvancedVanish plugin) {
        this.plugin = plugin;
    }

    public void enable(Settings s) {
        add(s.hookServerList, "server-list", null, () -> new ServerListHook(plugin));
        add(s.hookActionBar, "actionbar", null, () -> new ActionBarHook(plugin));
        add(s.hookEssentials, "essentials", "Essentials", () -> new EssentialsHook(plugin));
        add(s.hookPlaceholders, "placeholders", "PlaceholderAPI", () -> new PlaceholderApiHook(plugin));
        add(s.hookDiscordSrv, "discordsrv", "DiscordSRV", () -> new DiscordSrvHook(plugin));
        add(s.hookDynmap, "dynmap", "dynmap", () -> new DynmapHook(plugin));
        add(s.hookSquaremap, "squaremap", "squaremap", () -> new SquaremapHook(plugin));
        add(s.hookLibsDisguises, "libsdisguises", "LibsDisguises", () -> new LibsDisguisesHook(plugin));
        add(s.hookPlayerParticles, "playerparticles", "PlayerParticles", () -> new PlayerParticlesHook(plugin));
    }

    public void disable() {
        for (Hook hook : active) {
            try {
                hook.disable();
            } catch (Throwable t) {
                plugin.getLogger().log(Level.WARNING, "Could not disable the " + hook.getClass().getSimpleName(), t);
            }
            HandlerList.unregisterAll(hook);
        }
        active.clear();
    }

    public void reload(Settings settings) {
        disable();
        enable(settings);
    }

    /** Names of the plugins the active hooks integrate with. */
    public String[] hookedPlugins() {
        return active.stream().map(Hook::requiredPlugin).filter(name -> name != null).toArray(String[]::new);
    }

    private void add(boolean enabled, String key, String requiredPlugin, Supplier<Hook> factory) {
        if (!enabled) return;
        if (requiredPlugin != null && !plugin.getServer().getPluginManager().isPluginEnabled(requiredPlugin)) return;
        Hook hook = null;
        try {
            hook = factory.get();
            plugin.getServer().getPluginManager().registerEvents(hook, plugin);
            hook.enable();
            active.add(hook);
        } catch (Throwable t) {
            if (hook != null) HandlerList.unregisterAll(hook);
            plugin.getLogger().log(Level.WARNING, "Could not enable the hooks." + key + " integration"
                    + (requiredPlugin == null ? "" : " (is " + requiredPlugin + " up to date?)"), t);
        }
    }
}
