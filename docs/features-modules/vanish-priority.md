---
id: vanish-priority
title: Vanish Priority
sidebar_position: 2
---

Vanish priority decides which vanished players a staff member can see. It needs **LuckPerms**.

## The rule

A player can see a vanished player when **all** of these are true:

- priority is active (`priority.enable: true` and LuckPerms installed),
- they have the vanish permission (`advancedvanish.vanish` by default),
- their priority is **equal to or higher** than the vanished player's.

Without priority (turned off, or no LuckPerms), **nobody** sees vanished players, staff included.

## Setting priorities

The priority is a LuckPerms meta value. The key is `priority.meta-key` (default
`advancedvanish-priority`). Anyone without the meta has priority 0.

```
/lp group mod meta set advancedvanish-priority 1
/lp group admin meta set advancedvanish-priority 5
/lp user Notch meta set advancedvanish-priority 10
```

With those values, admins see vanished mods, mods see other vanished mods, and mods can't see
vanished admins. Staff at the same priority always see each other.

Check your own with `/vanish priority`. It says `not-using-vanish-priority` when priority isn't
active.

## When changes apply

Who can see whom is worked out when a player vanishes and when someone joins. If you change a
priority or permission while players are online, the vanished player can run `/vanish` twice to
apply it straight away.

## Config

```yaml
priority:
  enable: true
  meta-key: "advancedvanish-priority"
```
