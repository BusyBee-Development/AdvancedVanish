---
id: troubleshooting
title: Troubleshooting
sidebar_position: 3
---

## The plugin doesn't load

- **Spigot or CraftBukkit:** AdvancedVanish needs Paper or Folia (or a Paper fork).
- **Below 1.21:** the oldest supported version is 1.21.
- **Java older than 21:** the console shows `UnsupportedClassVersionError`. Update Java.

## Libraries fail to download

On first start the plugin downloads Jedis and HikariCP from Paper's Maven Central mirror into
`libraries/`. If the server can't reach it, the plugin fails to load with a library error.

- Allow outbound HTTPS from the server for the first start, or
- set the `PAPER_DEFAULT_CENTRAL_REPOSITORY` environment variable to a Maven mirror you can reach.
  AdvancedVanish uses it in place of the default mirror.

Once downloaded, the libraries are reused and no internet access is needed.

## Players can still see a vanished player

- They might be allowed to: with [vanish priority](../features-modules/vanish-priority.md), anyone
  with the vanish permission and an equal or higher priority sees vanished players. Check with
  `/lp user <name> meta info`.
- Permissions or priorities changed while both were online: have the vanished player run `/vanish`
  twice.
- Another plugin is showing them again. Try without other vanish plugins, and with any NPC or
  tab-list plugin that re-sends players.

## Staff can't see each other

That's the default without priority: nobody sees vanished players. Install LuckPerms, keep
`priority.enable: true`, and give staff the vanish permission. Equal priorities (0 by default) see
each other.

## /vanish runs another plugin's vanish

AdvancedVanish's `/vanish` and `/v` take priority over Bukkit-registered commands, such as
EssentialsX's. If another plugin also uses Paper's command API for `/vanish`, use
`/advancedvanish` (or `/advancedvanish:vanish`) and remove the other vanish plugin.

## Cross-server sync isn't working

- The startup block should say `Storage  Redis` or `Storage  MySQL`. If it says `None`, look for the
  SEVERE connection error above it.
- Every server must use the same backend, host, database and `table-prefix`.
- MySQL: the database in `sql.database` must exist; the table is created for you.
- After fixing the settings, `/vanish reload` reconnects.

## "config.yml was updated to the new format"

An update raised `config-version`. Your old file is saved as `config-backup-<timestamp>.yml` next to
the new one and every value with the same key and type was copied over. Compare the two if a value
looks wrong.

## A potion effect entry is ignored

The console names the entry. Use `NAME:LEVEL` with a vanilla effect id and a level of 1 or more,
for example `INVISIBILITY:1`. See
[give-potion-effects](../configuration/main-configuration.md#give-potion-effects).
