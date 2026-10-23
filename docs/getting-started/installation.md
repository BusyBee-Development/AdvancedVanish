---
id: installation
title: Installation
sidebar_position: 2
---

1. Check the [Requirements](requirements.md): Paper or Folia 1.21+, Java 21+.
2. Download the latest jar from [GitHub Releases](https://github.com/BusyBee-Development/AdvancedVanish/releases).
3. Drop it into your server's `plugins/` folder. Remove any other vanish plugin first.
4. Start (or restart) the server. AdvancedVanish creates `plugins/AdvancedVanish/config.yml`.
5. Edit the config if you want to (see [Main Configuration](../configuration/main-configuration.md)),
   then apply it with `/vanish reload`.

## Verifying it's working

The console prints one block when the plugin has enabled:

```
[AdvancedVanish] AdvancedVanish v26.10.0 on Paper
[AdvancedVanish]   Storage  None
[AdvancedVanish]   Priority LuckPerms
[AdvancedVanish]   Hooks    Essentials, PlaceholderAPI
[AdvancedVanish]   Ready in 18ms
```

- **Storage** is `Redis` or `MySQL` when [cross-server sync](../features-modules/cross-server-sync.md)
  is connected, `File` when [persistent states](../features-modules/persistent-state.md) are on,
  otherwise `None`.
- **Priority** is `LuckPerms` when [vanish priority](../features-modules/vanish-priority.md) is
  active, otherwise `off`.
- **Hooks** lists the installed plugins AdvancedVanish integrates with.

Then run `/vanish` in-game. You should see `You are now in vanish.` and other players should lose
sight of you.

## Updating

Replace the jar and restart. `config.yml` carries a `config-version`. When an update changes the
config format, the plugin saves your old file as `config-backup-<timestamp>.yml`, writes the new
default config, and copies every value you had set into it. The console logs one warning when that
happens. If the version hasn't changed, your file is left untouched.

Coming from the original AdvancedVanish by quantiom? Read the
[Migration Guide](../advanced-optimization/migration-guide.md) first.

## Uninstalling

Remove the jar and restart. `plugins/AdvancedVanish/` (config, backups and `vanishStates.json`) is
left on disk; delete it for a clean removal. With MySQL sync, the `advancedvanish_states` table stays
in your database.
