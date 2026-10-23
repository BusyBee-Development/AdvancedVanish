---
id: message-customization
title: Message Customization
sidebar_position: 2
---

Every message is in `config.yml` and uses [MiniMessage](https://docs.advntr.dev/minimessage/format.html)
formatting: `<red>`, `<bold>`, `<gradient:gold:yellow>`, `<#ff8800>` and so on. Legacy `&` codes
don't work. `"&4Hello"` becomes `"<dark_red>Hello"`.

- Set a message to `""` to send nothing.
- A message can be a list of lines instead of one string.
- Placeholders are written `%like-this%`.

## Prefix

```yaml
messages:
  prefix:
    enabled: true
    value: "<red>[AdvancedVanish]<white> "
```

The prefix goes in front of every line in `messages`, and in front of the `command-handler-messages`
when `command-handler-messages.use-prefix` is on.

## messages

| Key | Placeholders | Sent when |
|---|---|---|
| `no-permission` | | A command is used without permission |
| `config-reloaded` | | `/vanish reload` finishes |
| `version-command` | `%version%` | `/vanish version` |
| `vanish-on` | | You vanish (also when you join vanished) |
| `vanish-off` | | You unvanish |
| `cannot-chat-while-vanished` | | You chat while vanished |
| `cannot-place-blocks-while-vanished` | | You place a block while vanished |
| `cannot-break-blocks-while-vanished` | | You break a block while vanished |
| `cannot-attack-entities-while-vanished` | | You attack something while vanished |
| `cannot-drop-items-while-vanished` | | You drop an item while vanished |
| `opening-container-silently` | `%type%` | A [silent container](../features-modules/silent-containers.md) opens |
| `vanish-priority` | `%priority%` | `/vanish priority` |
| `not-using-vanish-priority` | | `/vanish priority` while priority is off |
| `vanished-list` | `%vanished-players%` | `/vanish list` (`None` when empty) |
| `vanish-status-command` | `%target-name%`, `%vanish-status%`, `%vanish-status-word%` | `/vanish status` |
| `vanish-set-other-command` | `%target-name%`, `%vanish-status%`, `%vanish-status-word%` | `/vanish set` and `/vanish toggle` |
| `vanish-set-other-command-already` | `%target-name%`, `%vanish-status%`, `%vanish-status-word%` | `/vanish set` to the state they're already in |
| `must-be-vanished-to-use-command` | | `/vanish interact` while visible |
| `vanish-interact-toggled` | `%interact-status%` | `/vanish interact` |
| `action-bar` | | Shown above the hotbar while vanished (with the `actionbar` hook) |
| `disguise-removed-because-vanish` | | A LibsDisguises disguise is removed or blocked |

`%vanish-status%` is `on` or `off`. `%vanish-status-word%` is `vanished` or `not vanished`.
`%interact-status%` is `on` or `off`. `%type%` is `chest`, `barrel`, `shulker` or `enderchest`.

## command-handler-messages

Replies for command mistakes:

| Key | Placeholders | Sent when |
|---|---|---|
| `use-prefix` | | `true` puts the prefix in front of these |
| `unknown-command` | | An unknown subcommand, e.g. `/vanish banana` |
| `invalid-syntax` | `%command%`, `%syntax%` | A missing or wrong argument, e.g. `/vanish status` |
| `error-performing-command` | | A command failed unexpectedly (details go to the console) |
| `could-not-find-player` | `%search%` | The player isn't online, or you can't see them |
| `generic-error` | `%error%` | Other errors, e.g. a player-only command run from the console |

## Fake join and leave messages

Those two live under `join-leave-messages`, not `messages`, and are sent without the prefix. See
[Main Configuration](main-configuration.md#join-leave-messages).
