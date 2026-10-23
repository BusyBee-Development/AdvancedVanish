---
id: web-maps
title: dynmap & squaremap
sidebar_position: 4
---

Web maps show every online player's position, which would give vanished staff away.

| Plugin | Hook key | What happens |
|---|---|---|
| [dynmap](https://modrinth.com/plugin/dynmap) | `hooks.dynmap` | The player's visibility is turned off on vanish and back on when they unvanish |
| [squaremap](https://modrinth.com/plugin/squaremap) | `hooks.squaremap` | The player is hidden on vanish and shown again when they unvanish |

Players who are already vanished when the hook starts (for example after `/vanish reload`) are
hidden straight away.

Both hooks are on by default and only start when the map plugin is installed.
