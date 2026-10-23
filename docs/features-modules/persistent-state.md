---
id: persistent-state
title: Saved Vanish States
sidebar_position: 4
---

By default every player with the vanish permission joins vanished (`vanish-on-join: true`). Saved
states remember the choice a player made instead.

## keep-vanish-state

```yaml
keep-vanish-state: false
keep-vanish-state-persistent: false
```

With `keep-vanish-state: true`, the plugin remembers whether each player with the vanish permission
was vanished when they left. When they come back, that wins over `vanish-on-join`: a staff member who
unvanished and logged off joins visible next time.

Players with no saved state yet (first join since the restart) fall back to `vanish-on-join` and
`advancedvanish.join-vanished`.

## keep-vanish-state-persistent

On its own, `keep-vanish-state` only lasts until the server restarts. Also set
`keep-vanish-state-persistent: true` to keep the states in `plugins/AdvancedVanish/vanishStates.json`.
The file is read on startup and on `/vanish reload`, and written on shutdown (including the states of
players who are still online). It's written to a temporary file first, so a crash can't leave it half
written.

## With cross-server sync

When [cross-server sync](cross-server-sync.md) is on, the network-wide state decides and these two
options are ignored.
