package net.busybee.advancedvanish.priority;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;

import java.util.function.Supplier;

/** Reads the priority from a LuckPerms meta value ({@code /lp user <name> meta set <meta-key> <number>}). */
public final class LuckPermsPriority implements PriorityProvider {

    private final LuckPerms luckPerms = LuckPermsProvider.get();
    private final Supplier<String> metaKey;

    public LuckPermsPriority(Supplier<String> metaKey) {
        this.metaKey = metaKey;
    }

    @Override
    public String name() {
        return "LuckPerms";
    }

    @Override
    public int getPriority(Player player) {
        User user = luckPerms.getUserManager().getUser(player.getUniqueId());
        if (user == null) return 0;
        String value = user.getCachedData().getMetaData().getMetaValue(metaKey.get());
        if (value == null) return 0;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
