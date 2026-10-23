---
id: discordsrv
title: DiscordSRV
sidebar_position: 3
---

[DiscordSRV](https://modrinth.com/plugin/discordsrv) already skips join and quit messages for
players with the `vanished` metadata, which AdvancedVanish sets. So a staff member who joins vanished
never shows up in Discord.

With `hooks.discordsrv: true`, the [fake join and leave messages](../configuration/main-configuration.md#join-leave-messages)
are posted to Discord too:

- `fake-leave-message-on-vanish` is sent to Discord as a leave message when someone runs `/vanish`.
- `fake-join-message-on-unvanish` is sent as a join message when they unvanish.

Both still need their `enable: true` under `join-leave-messages`. With them off, the hook sends
nothing. Colours are stripped by DiscordSRV, and the text goes through its normal join/leave format.
