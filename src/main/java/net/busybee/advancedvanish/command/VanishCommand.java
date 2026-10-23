package net.busybee.advancedvanish.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.busybee.advancedvanish.AdvancedVanish;
import net.busybee.advancedvanish.priority.PriorityProvider;
import net.busybee.advancedvanish.vanish.VanishManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.logging.Level;

/** {@code /vanish} (aliases {@code /v}, {@code /advancedvanish}) and its subcommands. */
@SuppressWarnings("UnstableApiUsage")
public final class VanishCommand {

    private static final List<String> HELP = List.of(
            "",
            "<red><strikethrough>----------</strikethrough><bold> AdvancedVanish </bold><red><strikethrough>----------",
            "<red>/vanish <dark_gray>- <white>Toggle vanish.",
            "<red>/vanish version <dark_gray>- <white>Shows the version of the plugin.",
            "<red>/vanish reload <dark_gray>- <white>Reloads the config and hooks.",
            "<red>/vanish interact <dark_gray>- <white>Toggles interacting with blocks while in vanish.",
            "<red>/vanish priority <dark_gray>- <white>Displays your vanish priority.",
            "<red>/vanish list <dark_gray>- <white>Displays a list of vanished players.",
            "<red>/vanish status <player> <dark_gray>- <white>Check if a player is in vanish.",
            "<red>/vanish set <player> <on/off> <dark_gray>- <white>Set another player's vanish.",
            "<red>/vanish toggle <player> <dark_gray>- <white>Toggle another player's vanish.",
            "<red><strikethrough>-----------------------------------",
            "");

    private final AdvancedVanish plugin;

    public VanishCommand(AdvancedVanish plugin) {
        this.plugin = plugin;
    }

    public void register(Commands commands) {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal("vanish")
                .executes(run(this::toggleSelf))
                .then(Commands.literal("version").executes(run(this::version)))
                .then(Commands.literal("reload").executes(run(this::reload)))
                .then(Commands.literal("reloadconfig").executes(run(this::reload)))
                .then(Commands.literal("interact").executes(run(this::interact)))
                .then(Commands.literal("priority").executes(run(this::priority)))
                .then(Commands.literal("list").executes(run(this::list)))
                .then(Commands.literal("help").executes(run(this::help)))
                .then(Commands.literal("status")
                        .executes(usage("/vanish status", "<player>"))
                        .then(Commands.argument("player", StringArgumentType.word())
                                .suggests(visiblePlayers())
                                .executes(run(this::status))))
                .then(Commands.literal("set")
                        .executes(usage("/vanish set", "<player> <on/off>"))
                        .then(Commands.argument("player", StringArgumentType.word())
                                .suggests(visiblePlayers())
                                .executes(usage("/vanish set", "<player> <on/off>"))
                                .then(Commands.argument("state", StringArgumentType.word())
                                        .suggests((ctx, builder) -> {
                                            String typed = builder.getRemainingLowerCase();
                                            for (String option : List.of("on", "off")) {
                                                if (option.startsWith(typed)) builder.suggest(option);
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(run(this::set)))))
                .then(Commands.literal("toggle")
                        .executes(usage("/vanish toggle", "<player>"))
                        .then(Commands.argument("player", StringArgumentType.word())
                                .suggests(visiblePlayers())
                                .executes(run(this::toggle))))
                .then(Commands.argument("args", StringArgumentType.greedyString())
                        .executes(run(ctx -> {
                            plugin.messages().sendCommandHandler(ctx.getSource().getSender(), "unknown-command", "<red>Invalid arguments.");
                            return Command.SINGLE_SUCCESS;
                        })));

        commands.register(root.build(), "Toggle vanish and manage vanished players.", List.of("v", "advancedvanish"));
    }

    // Subcommands

    private int toggleSelf(CommandContext<CommandSourceStack> ctx) {
        Player player = self(ctx);
        if (player == null || !allowed(player, plugin.settings().permVanish)) return 0;

        VanishManager vanish = plugin.vanish();
        boolean turningOn = !vanish.isVanished(player);
        CompletableFuture<Boolean> result = turningOn ? vanish.vanish(player, false) : vanish.unVanish(player, false);
        result.thenAccept(vanished -> {
            if (vanished == turningOn) plugin.messages().send(player, vanished ? "vanish-on" : "vanish-off");
        });
        return Command.SINGLE_SUCCESS;
    }

    private int version(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        if (!allowed(sender, plugin.settings().permVersion)) return 0;
        plugin.messages().send(sender, "version-command", "%version%", "v" + plugin.getPluginMeta().getVersion());
        return Command.SINGLE_SUCCESS;
    }

    private int reload(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        if (!allowed(sender, plugin.settings().permReload)) return 0;
        plugin.reload();
        plugin.messages().send(sender, "config-reloaded");
        return Command.SINGLE_SUCCESS;
    }

    private int interact(CommandContext<CommandSourceStack> ctx) {
        Player player = self(ctx);
        if (player == null || !allowed(player, plugin.settings().permInteract)) return 0;

        if (!plugin.vanish().isVanished(player)) {
            plugin.messages().send(player, "must-be-vanished-to-use-command");
            return 0;
        }
        boolean enabled = plugin.vanish().toggleInteract(player);
        plugin.messages().send(player, "vanish-interact-toggled", "%interact-status%", enabled ? "on" : "off");
        return Command.SINGLE_SUCCESS;
    }

    private int priority(CommandContext<CommandSourceStack> ctx) {
        Player player = self(ctx);
        if (player == null || !allowed(player, plugin.settings().permPriority)) return 0;

        PriorityProvider priority = plugin.activePriority();
        if (priority == null) {
            plugin.messages().send(player, "not-using-vanish-priority");
        } else {
            plugin.messages().send(player, "vanish-priority", "%priority%", String.valueOf(priority.getPriority(player)));
        }
        return Command.SINGLE_SUCCESS;
    }

    private int list(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        if (!allowed(sender, plugin.settings().permList)) return 0;

        StringJoiner names = new StringJoiner(", ");
        for (UUID uuid : plugin.vanish().getVanished()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) names.add(player.getName());
        }
        plugin.messages().send(sender, "vanished-list", "%vanished-players%", names.length() == 0 ? "None" : names.toString());
        return Command.SINGLE_SUCCESS;
    }

    private int help(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        if (!allowed(sender, plugin.settings().permHelp)) return 0;
        MiniMessage mm = MiniMessage.miniMessage();
        for (String line : HELP) sender.sendMessage(mm.deserialize(line));
        return Command.SINGLE_SUCCESS;
    }

    private int status(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        if (!allowed(sender, plugin.settings().permStatus)) return 0;
        Player target = target(ctx);
        if (target == null) return 0;

        sendStatus(sender, "vanish-status-command", target, plugin.vanish().isVanished(target));
        return Command.SINGLE_SUCCESS;
    }

    private int set(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        if (!allowed(sender, plugin.settings().permSetOther)) return 0;

        Boolean wanted = switch (StringArgumentType.getString(ctx, "state").toLowerCase(Locale.ROOT)) {
            case "on", "true" -> true;
            case "off", "false" -> false;
            default -> null;
        };
        if (wanted == null) {
            plugin.messages().sendCommandHandler(sender, "invalid-syntax", "<red>Usage: %command% %syntax%",
                    "%command%", "/vanish set", "%syntax%", "<player> <on/off>");
            return 0;
        }

        Player target = target(ctx);
        if (target == null) return 0;

        VanishManager vanish = plugin.vanish();
        if (vanish.isVanished(target) == wanted) {
            sendStatus(sender, "vanish-set-other-command-already", target, wanted);
            return Command.SINGLE_SUCCESS;
        }
        (wanted ? vanish.vanish(target, false) : vanish.unVanish(target, false))
                .thenAccept(vanished -> sendStatus(sender, "vanish-set-other-command", target, vanished));
        return Command.SINGLE_SUCCESS;
    }

    private int toggle(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        if (!allowed(sender, plugin.settings().permToggleOther)) return 0;
        Player target = target(ctx);
        if (target == null) return 0;

        VanishManager vanish = plugin.vanish();
        (vanish.isVanished(target) ? vanish.unVanish(target, false) : vanish.vanish(target, false))
                .thenAccept(vanished -> sendStatus(sender, "vanish-set-other-command", target, vanished));
        return Command.SINGLE_SUCCESS;
    }

    // Helpers

    private void sendStatus(CommandSender sender, String key, Player target, boolean vanished) {
        plugin.messages().send(sender, key,
                "%target-name%", target.getName(),
                "%vanish-status%", vanished ? "on" : "off",
                "%vanish-status-word%", vanished ? "vanished" : "not vanished");
    }

    private boolean allowed(CommandSender sender, String permission) {
        if (sender.hasPermission(permission)) return true;
        plugin.messages().send(sender, "no-permission");
        return false;
    }

    /** The player running the command, or null (with a message) for the console. */
    private Player self(CommandContext<CommandSourceStack> ctx) {
        if (ctx.getSource().getExecutor() instanceof Player player) return player;
        if (ctx.getSource().getSender() instanceof Player player) return player;
        plugin.messages().sendCommandHandler(ctx.getSource().getSender(), "generic-error", "<red>Error: %error%",
                "%error%", "Only players can use this command.");
        return null;
    }

    /** Looks up the {@code player} argument. Players the sender can't see count as not found. */
    private Player target(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getSender();
        String name = StringArgumentType.getString(ctx, "player");
        Player target = Bukkit.getPlayerExact(name);
        if (target == null) target = Bukkit.getPlayer(name);
        if (target != null && sender instanceof Player viewer && !viewer.equals(target) && !viewer.canSee(target)) {
            target = null;
        }
        if (target == null) {
            plugin.messages().sendCommandHandler(sender, "could-not-find-player",
                    "<red>Couldn't find a player by the name of %search%.", "%search%", name);
        }
        return target;
    }

    private SuggestionProvider<CommandSourceStack> visiblePlayers() {
        return (ctx, builder) -> {
            CommandSender sender = ctx.getSource().getSender();
            String typed = builder.getRemainingLowerCase();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (sender instanceof Player viewer && !viewer.equals(player) && !viewer.canSee(player)) continue;
                if (player.getName().toLowerCase(Locale.ROOT).startsWith(typed)) builder.suggest(player.getName());
            }
            return builder.buildFuture();
        };
    }

    private Command<CommandSourceStack> usage(String command, String syntax) {
        return ctx -> {
            plugin.messages().sendCommandHandler(ctx.getSource().getSender(), "invalid-syntax",
                    "<red>Usage: %command% %syntax%", "%command%", command, "%syntax%", syntax);
            return 0;
        };
    }

    /** Wraps a handler so an unexpected error is logged and reported with {@code error-performing-command}. */
    private Command<CommandSourceStack> run(Function<CommandContext<CommandSourceStack>, Integer> handler) {
        return ctx -> {
            try {
                return handler.apply(ctx);
            } catch (RuntimeException e) {
                plugin.getLogger().log(Level.SEVERE, "Error running /" + ctx.getInput(), e);
                plugin.messages().sendCommandHandler(ctx.getSource().getSender(), "error-performing-command",
                        "<red>There was an error performing this command.");
                return 0;
            }
        };
    }
}
