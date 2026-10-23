---
id: intro
title: Introduction
sidebar_position: 1
---

AdvancedVanish is a vanish plugin for **Paper** and **Folia** servers from Minecraft 1.21 to 26.x.
Staff disappear from the world, the tab list, the server list and other plugins, with full control
over what they can still do while hidden, vanish priority through LuckPerms, silent chest opening,
and vanish states shared across a network through Redis or MySQL.

**Credit where it's due:** AdvancedVanish was created by **[quantiom](https://github.com/quantiom/AdvancedVanish)**
in 2020 and released under the MIT license. BusyBee maintains this rewrite, which ports it to Java,
Gradle and the Paper plugin format, adds proper Folia support and fixes a number of long-standing
bugs. The config format, permissions and behaviour you know are kept.

## What's in this guide

- **[Getting Started](getting-started/quick-start.md)**: install the plugin and vanish for the first time.
- **[Core Reference](core-reference/commands-permissions.md)**: every command and permission node.
- **[Features & Modules](features-modules/vanish-behaviour.md)**: what vanish does, priority, silent containers, saved states and cross-server sync.
- **[Configuration](configuration/main-configuration.md)**: every `config.yml` key, the messages and the database settings.
- **[Integrations](integrations/placeholderapi.md)**: PlaceholderAPI, EssentialsX, DiscordSRV, web maps, LibsDisguises, PlayerParticles and the developer API.
- **[Advanced Optimization](advanced-optimization/folia-support.md)**: Folia specifics, upgrading from the original AdvancedVanish, and troubleshooting.
- **[Support](support/faq.md)**: FAQ, common issues and where to get help.

## Key features

- **Real vanish**: hidden from players, the tab list, tab completion, the server list and mob targeting.
- **Configurable restrictions** for chat, blocks, items, damage, hunger and interaction, with a per-player `/vanish interact` override.
- **Vanish priority** through LuckPerms meta, so senior staff can see junior staff but not the other way round.
- **Silent containers**: chests, barrels and shulker boxes open without animation or sound.
- **Cross-server sync** through Redis or MySQL, plus optional saved states across restarts.
- **Native Folia support**: every change runs on the thread that owns the player.
- **Integrations** with EssentialsX, PlaceholderAPI, DiscordSRV, dynmap, squaremap, LibsDisguises and PlayerParticles.

## Quick links

- **Source code:** [github.com/BusyBee-Development/AdvancedVanish](https://github.com/BusyBee-Development/AdvancedVanish)
- **Report an issue / request a feature:** [GitHub Issues](https://github.com/BusyBee-Development/AdvancedVanish/issues)
- **Download:** [GitHub Releases](https://github.com/BusyBee-Development/AdvancedVanish/releases)
- **Discord:** [discord.gg/abdm29q7af](https://discord.gg/abdm29q7af)

Ready to go? Start with the **[Quick Start](getting-started/quick-start.md)** guide.
