---
id: essentials
title: EssentialsX
sidebar_position: 2
---

With [EssentialsX](https://essentialsx.net) installed and `hooks.essentials: true`, vanished players
are marked as **hidden** in Essentials. Essentials then leaves them out of `/list` and its other
player lookups that respect hidden players.

The flag is set when a player vanishes and cleared when they unvanish, quit, or the server stops, so
nobody stays hidden in Essentials by mistake.

## The /vanish command

Essentials has its own `/vanish` and `/v`. AdvancedVanish registers its commands through Paper's
command API, which takes priority, so `/vanish` and `/v` run AdvancedVanish. Don't use Essentials'
vanish alongside it: it knows nothing about AdvancedVanish's rules or priority.

## Folia

EssentialsX doesn't support Folia, so on Folia this hook is simply not active.
