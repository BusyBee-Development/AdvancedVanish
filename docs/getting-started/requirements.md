---
id: requirements
title: Requirements
sidebar_position: 1
---

## Server

- **Paper** or **Folia**, Minecraft **1.21** up to **26.x**. One jar covers the whole range.
- **Spigot and CraftBukkit are not supported.** AdvancedVanish is a Paper plugin (`paper-plugin.yml`)
  and uses Paper-only APIs such as the Brigadier command API and the server-list ping event.
  Paper forks like Purpur load it like Paper does.

## Java

- **Java 21 or newer.** The jar is built for Java 21, so it runs on 1.21.x servers (Java 21) and on
  26.x servers (which need Java 25 themselves).

## Internet access on first start

On its first start AdvancedVanish downloads two small libraries used by
[cross-server sync](../features-modules/cross-server-sync.md), Jedis (Redis) and HikariCP (MySQL),
into the server's `libraries/` folder. Later starts use the downloaded copies. They are downloaded
even when sync is off, so turning it on later doesn't need another restart to fetch them.

If your server has no internet access, see
[Troubleshooting](../advanced-optimization/troubleshooting.md#libraries-fail-to-download).

## Optional plugins

None are required. Each one adds an [integration](../integrations/placeholderapi.md) when present:

| Plugin | Adds |
|---|---|
| LuckPerms | [Vanish priority](../features-modules/vanish-priority.md) |
| EssentialsX | Hides vanished players from `/list`, `/whois` and similar commands |
| PlaceholderAPI | `%advancedvanish_...%` placeholders |
| DiscordSRV | Fake join/leave messages in Discord |
| dynmap / squaremap | Hides vanished players from the web map |
| LibsDisguises | Removes disguises on vanish |
| PlayerParticles | Clears particle effects while vanished |

ProtocolLib is **not** needed. The server-list hiding it used to provide is built in now.
