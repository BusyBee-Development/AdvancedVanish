---
id: migration-guide
title: Migration Guide
sidebar_position: 2
---

Upgrading from the original AdvancedVanish by quantiom (1.2.x, 2026.2 or 2026.5.0, package
`me.quantiom.advancedvanish`) to the BusyBee rewrite (26.10.0 and later).

## For server owners

1. Check you run **Paper or Folia** 1.21+. Spigot isn't supported any more.
2. Stop the server, replace the jar, start it.
3. That's it. Your `config.yml` is used as it is (same keys, same `config-version`), and so are your
   permission nodes and `vanishStates.json`.

### What you can remove

- **ProtocolLib** is no longer used. If nothing else needs it, you can remove it. Server-list hiding
  is built in now.

### What behaves differently

| Before | Now |
|---|---|
| Staff online when someone vanished couldn't see them without priority, but staff who joined later could | Without priority nobody sees vanished players, consistently. With priority, the [priority rule](../features-modules/vanish-priority.md) applies everywhere |
| Unvanishing took flight away from everyone except spectators | Only flight that vanishing granted is taken away. Creative players and `/fly` users keep theirs |
| `when-vanished.fly.keep-on-unvanish` had no effect | It works |
| `receive-damage-from-entities: true` still blocked all damage | Vanished players take entity damage when it's on |
| Every shutdown sent fake join messages and reset everyone's cross-server state to visible | Shutdowns are quiet and leave the network state alone |
| On 26.x, vanish potion effects showed a countdown timer (the version check didn't recognise 26.x) | They are infinite (∞) on every version |
| `sql.table-prefix` did nothing; the table was `advancedvanish` | The table is `advancedvanish_states`. Old rows are [copied over automatically](../configuration/database-setup.md#upgrading-from-the-old-table) |
| The update checker pointed at the original Spigot page | It checks the BusyBee GitHub releases |
| Arrows and other projectiles fired by a vanished player ignored the attack rules | Projectiles count as an attack by their shooter |
The old config and README listed bPermissions, GroupManager and PermissionsEx for priority. Only
LuckPerms was ever implemented, and that's still the case.

## For developers

The API moved package and became thread-safe, so code that used it needs a small update. Plugins
that only check the `vanished` metadata don't need any change.

| Before | Now |
|---|---|
| `me.quantiom.advancedvanish.util.AdvancedVanishAPI.INSTANCE` | `net.busybee.advancedvanish.api.AdvancedVanishAPI` (static methods) |
| `isPlayerVanished(player)` | `isVanished(player)` |
| `vanishPlayer(player, onJoin)` | `vanish(player)`, returns `CompletableFuture<Boolean>` |
| `unVanishPlayer(player, onLeave)` | `unVanish(player)`, returns `CompletableFuture<Boolean>` |
| `getVanishedPlayers()` (mutable list) | `getVanishedPlayers()` (read-only set) |
| `canSee(player, target)` returned `false` for visible targets | `canSee(observer, target)` returns `true` for visible targets |
| `me.quantiom.advancedvanish.event.*` | `net.busybee.advancedvanish.api.event.*` |
| `event.player`, `event.onJoin`, `event.onLeave` (Kotlin) | `getPlayer()`, `isOnJoin()`, `isOnLeave()` |
| `PrePlayerUnVanishEvent.fromLeave` | `isOnLeave()` |

The four event names are unchanged. Details on the [Developer API](../integrations/developer-api.md)
page.
