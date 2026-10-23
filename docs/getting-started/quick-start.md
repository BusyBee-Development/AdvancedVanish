---
id: quick-start
title: Quick Start
sidebar_position: 3
---

Five minutes from download to a working staff vanish.

## 1. Install

Put the jar in `plugins/` and restart. See [Installation](installation.md).

## 2. Give your staff the permission

Every permission defaults to **op**. For staff who aren't op, give the vanish node and whichever
commands they need:

```
/lp group staff permission set advancedvanish.vanish true
/lp group staff permission set advancedvanish.list-command true
/lp group staff permission set advancedvanish.status-command true
```

The full list is on [Commands & Permissions](../core-reference/commands-permissions.md).

## 3. Vanish

Run `/vanish` (or `/v`). You disappear for everyone who isn't allowed to see you, get Invisibility
and Night Vision, and can fly. Run it again to reappear.

By default (`vanish-on-join: true`) anyone with `advancedvanish.vanish` also **joins vanished**.
Turn that off in `config.yml` if your staff should join visible.

## 4. Decide what vanished staff can do

By default vanished players can't chat, break or place blocks, interact, attack, pick up or drop
items, take damage, lose hunger or be targeted by mobs. Chests open silently. Change any of it in
the `when-vanished` section (see [Vanish Behaviour](../features-modules/vanish-behaviour.md)).

Need to flip a lever just once? `/vanish interact` lifts the block, item, interact and attack
rules for you until you unvanish.

## 5. Let senior staff see junior staff (optional)

With LuckPerms installed, give each rank a priority:

```
/lp group mod meta set advancedvanish-priority 1
/lp group admin meta set advancedvanish-priority 5
```

Admins now see vanished mods, but mods can't see vanished admins. See
[Vanish Priority](../features-modules/vanish-priority.md).

## 6. Reload

After editing `config.yml`, run `/vanish reload`. No restart needed.
