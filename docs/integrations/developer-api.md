---
id: developer-api
title: Developer API
sidebar_position: 6
---

Two ways for another plugin to work with AdvancedVanish.

## Just checking vanish (no dependency)

AdvancedVanish sets the `vanished` metadata on vanished players, the same convention EssentialsX,
SuperVanish and others use. Checking it works for any of those plugins and needs no dependency:

```java
boolean vanished = player.getMetadata("vanished").stream().anyMatch(MetadataValue::asBoolean);
```

## The API

For more than a yes/no, use `net.busybee.advancedvanish.api.AdvancedVanishAPI`. Compile against the
jar from [GitHub Releases](https://github.com/BusyBee-Development/AdvancedVanish/releases):

```kotlin
dependencies {
    compileOnly(files("libs/AdvancedVanish-26.10.0.jar"))
}
```

and declare AdvancedVanish as a soft dependency so it loads first (`softdepend: [AdvancedVanish]` in
`plugin.yml`, or a `dependencies.server.AdvancedVanish` entry with `load: BEFORE` and
`join-classpath: true` in `paper-plugin.yml`).

### Methods

All static, all safe to call from any thread.

| Method | Returns |
|---|---|
| `isVanished(Player)` / `isVanished(UUID)` | Whether the player is vanished |
| `getVanishedPlayers()` | Live read-only `Set<UUID>` of vanished online players |
| `vanish(Player)` | `CompletableFuture<Boolean>`: vanishes the player, completes with whether they are vanished afterwards |
| `unVanish(Player)` | `CompletableFuture<Boolean>`: unvanishes the player, completes with whether they are still vanished |
| `canSee(Player observer, Player target)` | Whether `observer` is allowed to see `target`, priority included |
| `getPriority(Player)` | The player's vanish priority, or `0` when priority is off |

`vanish` and `unVanish` return a future because the change runs on the player's own thread, which
on Folia may not be yours. On Paper, called from the main thread, the future is already complete.

```java
AdvancedVanishAPI.vanish(player).thenAccept(vanished -> {
    if (!vanished) getLogger().info("Another plugin cancelled the vanish");
});
```

`vanish`/`unVanish` behave like `/vanish`: fake join/leave messages are sent if enabled, and the state
is written to [cross-server sync](../features-modules/cross-server-sync.md).

### Events

In `net.busybee.advancedvanish.api.event`. All extend `PlayerEvent`.

| Event | Cancellable | Extra |
|---|---|---|
| `PrePlayerVanishEvent` | yes | `isOnJoin()` |
| `PlayerVanishEvent` | no | `isOnJoin()` |
| `PrePlayerUnVanishEvent` | yes | `isOnLeave()` |
| `PlayerUnVanishEvent` | no | `isOnLeave()` |

`isOnJoin()` is true when the player is vanished because they joined. `isOnLeave()` is true when they
are unvanished because they quit or the server is stopping. Events fire on the player's own thread.

```java
@EventHandler
public void onVanish(PrePlayerVanishEvent event) {
    if (inMinigame(event.getPlayer())) event.setCancelled(true);
}
```

Coming from the original `me.quantiom.advancedvanish` API? See the
[Migration Guide](../advanced-optimization/migration-guide.md#for-developers).
