---
id: commands-permissions
title: Commands & Permissions
sidebar_position: 1
---

Everything runs under one command, `/vanish`, with subcommands.

**Aliases:** `/v`, `/advancedvanish`. AdvancedVanish registers `/vanish` and `/v` through Paper's
command API, so they take over from EssentialsX's own `/vanish` when both are installed.

## Commands

| Command | Description | Permission | Default |
|---|---|---|---|
| `/vanish` | Toggle your own vanish | `advancedvanish.vanish` | op |
| `/vanish help` | Show the command list | `advancedvanish.help-command` | op |
| `/vanish version` | Show the running version | `advancedvanish.version-command` | op |
| `/vanish reload` | Reload `config.yml`, messages, priority and hooks (alias `reloadconfig`) | `advancedvanish.reload-config-command` | op |
| `/vanish interact` | Toggle interacting with the world while vanished | `advancedvanish.interact-command` | op |
| `/vanish priority` | Show your vanish priority | `advancedvanish.priority-command` | op |
| `/vanish list` | List vanished players | `advancedvanish.list-command` | op |
| `/vanish status <player>` | Check whether a player is vanished | `advancedvanish.status-command` | op |
| `/vanish set <player> <on\|off>` | Vanish or unvanish another player | `advancedvanish.set-other-command` | op |
| `/vanish toggle <player>` | Toggle another player's vanish | `advancedvanish.toggle-other-command` | op |

`/vanish`, `/vanish interact` and `/vanish priority` are player-only. Everything else works from the
console too. `set` also accepts `true` and `false`.

Player names tab-complete, but only for players you can see, and a vanished player you can't see is
reported as not found. Running a command without permission sends the `no-permission` message.

## Other permissions

| Permission | Grants | Default |
|---|---|---|
| `advancedvanish.join-vanished` | Join vanished even when `vanish-on-join` is off. Needs `advancedvanish.vanish` too | op |
| `advancedvanish.keep-fly` | Keep flight after unvanishing | op |

`advancedvanish.vanish` does double duty: it also lets a player see vanished players, but only when
[vanish priority](../features-modules/vanish-priority.md) is active and their priority is high
enough.

## Renaming permission nodes

Every node the plugin checks is set in the `permissions` section of `config.yml`, so you can point a
check at a node you already use:

```yaml
permissions:
  vanish: "advancedvanish.vanish"
  version-command: "advancedvanish.version-command"
  help-command: "advancedvanish.help-command"
  reload-config-command: "advancedvanish.reload-config-command"
  interact-command: "advancedvanish.interact-command"
  priority-command: "advancedvanish.priority-command"
  list-command: "advancedvanish.list-command"
  status-command: "advancedvanish.status-command"
  toggle-other-command: "advancedvanish.toggle-other-command"
  set-other-command: "advancedvanish.set-other-command"
  keep-fly-on-unvanish: "advancedvanish.keep-fly"
  join-vanished: "advancedvanish.join-vanished"
```

The defaults above (all op) belong to the default node names. A renamed node gets whatever default
your permissions plugin gives it.
