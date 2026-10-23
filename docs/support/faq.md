---
id: faq
title: FAQ
sidebar_position: 1
---

## Does it work on Spigot?

No. It's a Paper plugin and needs Paper, Folia or a Paper fork, on 1.21 or newer.

## Which Minecraft versions does it support?

1.21 through 26.x, with one jar. It needs Java 21 or newer.

## Is this the same plugin as quantiom's AdvancedVanish?

It's a rewrite of it, maintained by BusyBee with quantiom's original work credited. The config,
permissions and commands are the same. See the [Migration Guide](../advanced-optimization/migration-guide.md).

## Do I need ProtocolLib?

No. Hiding vanished players from the server list is built in.

## Do my staff join vanished?

Yes, by default (`vanish-on-join: true`), if they have `advancedvanish.vanish`. Set it to `false`, or
use [saved states](../features-modules/persistent-state.md) to remember each player's choice.

## Can vanished staff see each other?

Only with [vanish priority](../features-modules/vanish-priority.md) (LuckPerms). Equal or higher
priority sees lower or equal priority.

## Does vanish hide players from the tab list and tab completion?

Yes, from both, for everyone who can't see them. The online count in the server list drops too, and
`%advancedvanish_player_count%` gives the visible count for your own tab list or scoreboard.

## Can a vanished player open chests without anyone noticing?

Yes. By default containers open as a silent read-only copy. See
[Silent Containers](../features-modules/silent-containers.md).

## Can vanished players chat?

Not by default. Set `when-vanished.send-messages: true` to allow it.

## Does it keep players vanished across servers?

With [cross-server sync](../features-modules/cross-server-sync.md) over Redis or MySQL, yes.

## Does it run on Folia?

Yes, natively. See [Folia Support](../advanced-optimization/folia-support.md).
