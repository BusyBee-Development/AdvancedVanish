---
id: folia-support
title: Folia Support
sidebar_position: 1
---

AdvancedVanish runs natively on [Folia](https://papermc.io/software/folia), using the same jar as
Paper. The startup block says `on Folia` when it detects it.

## How it stays region-safe

Folia splits the world into regions that tick on different threads, and a player may only be changed
from the thread that owns them.

- **Vanishing and unvanishing** a player (potion effects, flight, metadata, events) always runs on
  that player's own thread. `/vanish set` from the console or from a player in another region is
  handed over to the target's thread, and the reply is sent once it's done.
- **Hiding and showing** run on each observer's thread, one small task per online player.
- **Join and quit** handling runs on the joining or leaving player's thread, where Folia already
  calls those events.
- **The action bar** is sent from one background timer, since sending a packet is thread-safe.
- **Database work** (Redis, MySQL, the GitHub update check) never runs on a region thread.
- **On shutdown** players are restored on Folia's shutdown thread, which owns every region at that
  point.

## Integrations on Folia

A hook only starts when its plugin is loaded, and Folia only loads plugins marked as Folia-supported.
EssentialsX, for example, doesn't load on Folia, so its hook stays inactive there. LuckPerms, which
priority needs, supports Folia. The built-in server-list and action-bar hooks work everywhere.

## Tested on

The bot test suite runs against Paper 1.21.8, Folia 1.21.8 and Folia 26.2, and the code is compiled
against the Paper 26.2 and 26.3 APIs to catch anything removed.
