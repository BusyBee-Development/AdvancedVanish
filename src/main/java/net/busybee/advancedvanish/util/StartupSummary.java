package net.busybee.advancedvanish.util;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * The one console block a plugin prints once it has enabled. Copied verbatim into every BusyBee
 * plugin; see {@code 01 - Directives/STARTUP_LOG_STANDARD.md}.
 */
public final class StartupSummary {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final JavaPlugin plugin;
    private final List<String[]> rows = new ArrayList<>();

    public StartupSummary(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /** A plain label/value row. The value is shown as-is (tags are escaped). */
    public StartupSummary row(String label, String value) {
        rows.add(new String[]{label, "<gray>" + MM.escapeTags(value) + "</gray>"});
        return this;
    }

    /** {@code 12/14 enabled (off: minion, vessel)}. */
    public StartupSummary modules(int enabled, int total, Collection<String> off) {
        rows.add(new String[]{"Modules", "<green>" + enabled + "</green><gray>/" + total + " enabled</gray>"
                + (off.isEmpty() ? "" : " <dark_gray>(off: " + MM.escapeTags(String.join(", ", off)) + ")</dark_gray>")});
        return this;
    }

    /** Lists whichever of the given plugins are enabled, or "none". */
    public StartupSummary hooks(String... pluginNames) {
        List<String> found = new ArrayList<>();
        for (String name : pluginNames) {
            if (plugin.getServer().getPluginManager().isPluginEnabled(name)) found.add(name);
        }
        return row("Hooks", found.isEmpty() ? "none" : String.join(", ", found));
    }

    /** Prints the block. {@code startedAt} is {@link System#currentTimeMillis()} taken at the top of onEnable. */
    public void print(long startedAt) {
        rows.add(new String[]{"Ready in", "<yellow>" + (System.currentTimeMillis() - startedAt) + "ms</yellow>"});

        int width = 0;
        for (String[] row : rows) width = Math.max(width, row[0].length());

        plugin.getComponentLogger().info(MM.deserialize("<gold>" + plugin.getName() + "</gold> <white>v"
                + plugin.getPluginMeta().getVersion() + "</white> <dark_gray>on</dark_gray> <gray>"
                + (isFolia() ? "Folia" : "Paper") + "</gray>"));
        for (String[] row : rows) {
            plugin.getComponentLogger().info(MM.deserialize("  <dark_gray>" + pad(row[0], width) + "</dark_gray> " + row[1]));
        }
    }

    private static String pad(String s, int width) {
        return s + " ".repeat(width - s.length());
    }

    private static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
