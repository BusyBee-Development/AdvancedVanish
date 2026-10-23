---
id: vanish-behaviour
title: Vanish Behaviour
sidebar_position: 1
---

What happens to a player when they vanish, and what they can still do.

## When a player vanishes

- They are **hidden** from every player who isn't allowed to see them (see
  [Vanish Priority](vanish-priority.md)): gone from the world, the tab list and player-name tab
  completion.
- The potion effects in `when-vanished.give-potion-effects` are applied with infinite duration.
  Effects they already had of the same type are remembered and given back on unvanish.
- With `when-vanished.fly.enable` they can fly.
- They get the `vanished` metadata that EssentialsX, DiscordSRV, TAB and many other plugins check.
- If `join-leave-messages.fake-leave-message-on-vanish.enable` is on, everyone sees a fake leave
  message.

## When a player unvanishes

- They become visible again, their previous potion effects come back, and the `/vanish interact`
  override is switched off.
- **Flight** is taken away only if vanishing granted it. Players who could already fly (creative,
  spectator, `/fly`) keep it. Others keep it too with `when-vanished.fly.keep-on-unvanish: true` or
  the `advancedvanish.keep-fly` permission.
- If `join-leave-messages.fake-join-message-on-unvanish.enable` is on, everyone sees a fake join
  message.

## Joining and leaving

A player with `advancedvanish.vanish` joins vanished when:

1. [cross-server sync](cross-server-sync.md) is on and the network says they were vanished, or else
2. `keep-vanish-state` is on and they were vanished when they last left (see
   [Saved Vanish States](persistent-state.md)), or else
3. `vanish-on-join` is on (the default), or they have `advancedvanish.join-vanished`.

Vanished players' join and quit messages are hidden unless `when-vanished.join-messages` or
`when-vanished.leave-messages` is on. They are vanished before other plugins' join handlers run, so
those plugins already see them as vanished.

When a vanished player quits, their effects and flight are restored quietly (no fake join message),
so nothing lingers in their player data. The same happens to everyone still vanished when the server
stops.

## What vanished players can do

Each `when-vanished` option is `false` by default, which **blocks** the action.

| Option | When `false` | `/vanish interact` lifts it |
|---|---|---|
| `send-messages` | Chat is blocked (`cannot-chat-while-vanished`) | no |
| `break-blocks` | Breaking blocks is blocked | yes |
| `place-blocks` | Placing blocks is blocked | yes |
| `interact` | Using blocks and items is blocked: doors, buttons, pressure plates, eating, throwing | yes |
| `open-and-use-chests` | Containers open [silently and read-only](silent-containers.md) | n/a |
| `pick-up-items` | Picking up items is blocked | yes |
| `drop-items` | Dropping items is blocked | yes |
| `attack-entities` | Attacking mobs is blocked | yes |
| `receive-damage-from-entities` | Mobs and players can't hurt them | no |
| `lose-hunger` | The hunger bar doesn't change | no |
| `mob-targeting` | Mobs ignore them | no |

Two rules are fixed:

- A vanished player can **never attack another player**. It would give them away.
- A vanished player takes **no damage from anything else** (fall, fire, drowning, void). Only
  entity damage is controlled by `receive-damage-from-entities`.

## /vanish interact

`/vanish interact` toggles an override for the current vanish session. While it's on, the rules
marked "yes" above don't apply, so a staff member can open a door or break a block without
unvanishing. It turns itself off when they unvanish.
