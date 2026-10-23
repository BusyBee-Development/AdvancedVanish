---
id: libsdisguises-playerparticles
title: LibsDisguises & PlayerParticles
sidebar_position: 5
---

A disguise or a particle trail can give a vanished player away.

## LibsDisguises

With [LibsDisguises](https://www.spigotmc.org/resources/libs-disguises-free.81/) and
`hooks.libsdisguises: true`:

- A disguised player who vanishes is undisguised.
- A vanished player can't put a disguise on.

Both send `disguise-removed-because-vanish`.

## PlayerParticles

With [PlayerParticles](https://www.spigotmc.org/resources/playerparticles.40261/) and
`hooks.playerparticles: true`, a vanished player's active particles are cleared when they vanish and
again every second while they stay vanished, in case they turn some back on.

This only clears the particles that are running. Their saved particle setup in PlayerParticles is
not changed.

## The action bar

Not a plugin, but in the same spirit: with `hooks.actionbar: true` (the default) vanished players see
`messages.action-bar` (`You are in vanish.`) above their hotbar, refreshed every 1.5 seconds, so they
never forget they're hidden. Set the message to `""` to hide it.
