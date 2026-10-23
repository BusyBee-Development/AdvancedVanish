---
id: cross-server-sync
title: Cross-Server Sync
sidebar_position: 5
---

On a network (Velocity, BungeeCord), cross-server sync keeps a staff member's vanish state when they
switch servers: vanish on the hub, and you arrive on survival still vanished.

## How it works

- When a player vanishes or unvanishes **by command** (or through the API), the new state is written
  to Redis or MySQL in the background.
- When a player logs in, their state is read while they connect, before they are in the world.
  Players with the vanish permission then join vanished or visible to match it. A player with no
  saved state joins visible.
- Joining, quitting and server shutdowns don't write anything, so a restart never resets anyone's
  state.

With sync on, `vanish-on-join`, `keep-vanish-state` and `keep-vanish-state-persistent` are ignored.
The network state decides.

## Setting it up

Every server in the network needs the same backend settings:

```yaml
cross-server-support:
  enabled: true
  # "redis" or "sql"
  mode: "redis"
  redis:
    ip: "127.0.0.1"
    port: 6379
    auth: ""
  sql:
    ip: "127.0.0.1"
    port: 3306
    username: "root"
    password: ""
    database: "minecraft"
    table-prefix: "advancedvanish_"
```

Connection details for each backend are on [Database Setup](../configuration/database-setup.md).

The startup block shows `Storage  Redis` or `Storage  MySQL` once connected. If the connection fails,
the console logs a SEVERE error and sync stays off. The plugin keeps running.

`/vanish reload` reconnects in the background with the new settings.
