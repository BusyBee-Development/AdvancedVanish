---
id: placeholderapi
title: PlaceholderAPI
sidebar_position: 1
---

With [PlaceholderAPI](https://modrinth.com/plugin/placeholderapi) installed and `hooks.placeholders: true`,
AdvancedVanish registers its own expansion. No `/papi ecloud download` needed.

## Placeholders

| Placeholder | Returns |
|---|---|
| `%advancedvanish_is_vanished%` | `Yes` or `No` for the player |
| `%advancedvanish_vanished_players%` | Names of vanished players, comma separated, or `None` |
| `%advancedvanish_player_count%` | Online players minus vanished players |

Use `%advancedvanish_player_count%` in your tab list, scoreboard or MOTD so the count doesn't give
vanished staff away.

## Renaming them

Both the identifier and each placeholder name can be changed:

```yaml
placeholders:
  identifier: "advancedvanish"
  is-vanished: "is_vanished"
  vanished-players: "vanished_players"
  player-count: "player_count"
```

With `identifier: "av"` and `player-count: "online"`, the count becomes `%av_online%`. Run
`/vanish reload` after changing them.

## Testing

```
/papi parse me %advancedvanish_is_vanished%
```
