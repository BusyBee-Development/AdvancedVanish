---
id: database-setup
title: Database Setup
sidebar_position: 3
---

A database is only needed for [cross-server sync](../features-modules/cross-server-sync.md).
Pick Redis or MySQL. Every server in the network must point at the same one.

The client libraries (Jedis and HikariCP) are downloaded into `libraries/` on first start. Paper
ships the MySQL driver, so nothing else needs installing.

## Redis

```yaml
cross-server-support:
  enabled: true
  mode: "redis"
  redis:
    ip: "127.0.0.1"
    port: 6379
    auth: ""
```

- `auth` is the Redis password (`requirepass`). Leave it empty if there is none.
- Each player is stored as the key `advancedvanish-<uuid>` with the value `true` or `false`, the same
  keys earlier versions used, so existing data carries over.

## MySQL / MariaDB

```yaml
cross-server-support:
  enabled: true
  mode: "sql"
  sql:
    ip: "127.0.0.1"
    port: 3306
    username: "root"
    password: ""
    database: "minecraft"
    table-prefix: "advancedvanish_"
```

- The database must already exist. The table is created on first connect.
- The table is `<table-prefix>states`, so `advancedvanish_states` by default. Only letters, digits
  and `_` are kept from the prefix.
- Columns: `uuid` (`CHAR(36)`, primary key) and `state` (`BOOLEAN`).
- Connections come from a small HikariCP pool (up to 3), with a 5 second connect timeout.

### Upgrading from the old table

Before this rewrite the table was always called `advancedvanish` and `table-prefix` did nothing. On
connect, if that old table exists, its rows are copied into the new table. Rows already in the new
table are kept. You can drop the old table once every server runs the new version.

## Checking the connection

The startup block shows `Storage  Redis` or `Storage  MySQL` when connected. A failed connection
logs a SEVERE error with the cause, and sync stays off until you fix the settings and run
`/vanish reload`.
