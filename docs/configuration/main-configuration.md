---
id: main-configuration
title: Main Configuration
sidebar_position: 1
---

Everything lives in `plugins/AdvancedVanish/config.yml`. Apply changes with `/vanish reload`.

## General

| Key | Default | What it does |
|---|---|---|
| `check-for-updates` | `true` | On startup, checks GitHub for a newer release and logs one line if there is one |
| `vanish-on-join` | `true` | Players with the vanish permission join vanished |
| `keep-vanish-state` | `false` | Remember whether each player was vanished when they left ([details](../features-modules/persistent-state.md)) |
| `keep-vanish-state-persistent` | `false` | Keep those states in `vanishStates.json` across restarts |

The three join options are ignored while [cross-server sync](../features-modules/cross-server-sync.md)
is on.

## cross-server-support

| Key | Default | What it does |
|---|---|---|
| `enabled` | `false` | Share vanish states between servers |
| `mode` | `"redis"` | `"redis"` or `"sql"` (MySQL/MariaDB) |
| `redis.ip` / `redis.port` / `redis.auth` | `"127.0.0.1"` / `6379` / `""` | Redis connection. Leave `auth` empty for no password |
| `sql.ip` / `sql.port` | `"127.0.0.1"` / `3306` | MySQL host and port |
| `sql.username` / `sql.password` | `"root"` / `""` | MySQL login |
| `sql.database` | `"minecraft"` | Database to use (must exist) |
| `sql.table-prefix` | `"advancedvanish_"` | Table name prefix: the table is `<prefix>states` |

See [Database Setup](database-setup.md).

## when-vanished

`true` allows the action while vanished, `false` blocks it. Every option is explained on
[Vanish Behaviour](../features-modules/vanish-behaviour.md).

| Key | Default |
|---|---|
| `join-messages` | `false` |
| `leave-messages` | `false` |
| `send-messages` | `false` |
| `break-blocks` | `false` |
| `place-blocks` | `false` |
| `interact` | `false` |
| `attack-entities` | `false` |
| `receive-damage-from-entities` | `false` |
| `open-and-use-chests` | `false` |
| `pick-up-items` | `false` |
| `drop-items` | `false` |
| `give-potion-effects` | `["INVISIBILITY:2", "NIGHT_VISION:1"]` |
| `lose-hunger` | `false` |
| `mob-targeting` | `false` |
| `fly.enable` | `true` |
| `fly.keep-on-unvanish` | `false` |

### give-potion-effects

Each entry is `"NAME:LEVEL"`. `NAME` is a vanilla effect id in any case (`INVISIBILITY`,
`night_vision`, `minecraft:jump_boost`). Old Bukkit names such as `JUMP`, `SLOW`, `INCREASE_DAMAGE`
and `DAMAGE_RESISTANCE` still work. `LEVEL` starts at 1, so `"SPEED:2"` is Speed II. Effects last
until the player unvanishes and show no particles. A malformed or unknown entry is skipped with a
console warning.

## permissions

The permission node behind each check. See
[Commands & Permissions](../core-reference/commands-permissions.md#renaming-permission-nodes).

## join-leave-messages

| Key | Default | What it does |
|---|---|---|
| `fake-join-message-on-unvanish.enable` | `false` | Broadcast a fake join message when someone unvanishes |
| `fake-join-message-on-unvanish.message` | `"<yellow>%player-name% has joined the game"` | The message, MiniMessage format |
| `fake-leave-message-on-vanish.enable` | `false` | Broadcast a fake leave message when someone vanishes |
| `fake-leave-message-on-vanish.message` | `"<yellow>%player-name% has left the game."` | The message, MiniMessage format |

Fake messages are only sent for `/vanish` and the API, never when a player joins vanished or quits.

## hooks

Each integration can be switched off. A hook whose plugin isn't installed is skipped silently.

| Key | Default | Needs |
|---|---|---|
| `essentials` | `true` | EssentialsX |
| `server-list` | `true` | nothing |
| `placeholders` | `true` | PlaceholderAPI |
| `actionbar` | `true` | nothing |
| `discordsrv` | `true` | DiscordSRV |
| `dynmap` | `true` | dynmap |
| `squaremap` | `true` | squaremap |
| `libsdisguises` | `true` | LibsDisguises |
| `playerparticles` | `true` | PlayerParticles |

What each one does is in [Integrations](../integrations/placeholderapi.md).

## placeholders

| Key | Default | What it does |
|---|---|---|
| `identifier` | `"advancedvanish"` | The first part of every placeholder |
| `is-vanished` | `"is_vanished"` | Name of the is-vanished placeholder |
| `vanished-players` | `"vanished_players"` | Name of the vanished-players placeholder |
| `player-count` | `"player_count"` | Name of the visible-player-count placeholder |

See [PlaceholderAPI](../integrations/placeholderapi.md).

## priority

| Key | Default | What it does |
|---|---|---|
| `enable` | `true` | Use [vanish priority](../features-modules/vanish-priority.md) when LuckPerms is installed |
| `meta-key` | `"advancedvanish-priority"` | LuckPerms meta key holding the priority |

## messages and command-handler-messages

See [Message Customization](message-customization.md).

## config-version

Managed by the plugin. Don't change it. When an update raises it, your file is backed up and your
values are carried over (see [Installation](../getting-started/installation.md#updating)).
