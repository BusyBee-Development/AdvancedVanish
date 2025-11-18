# AdvancedVanish

A fully customizable and advanced vanish plugin for Spigot-based Minecraft servers.

## Features

-   Toggle vanish for yourself and other players.
-   Control whether you can interact with blocks while vanished.
-   View a list of vanished players.
-   Check the vanish status of any player.
-   Vanish priority system.
-   Support for various popular plugins like LuckPerms, PlaceholderAPI, and Essentials.

## Commands

| Command                         | Description                                  | Aliases                 |
|---------------------------------|----------------------------------------------|-------------------------|
| `/vanish`                       | Toggle your vanish status.                   | `/v`, `/advancedvanish` |
| `/vanish version`               | Shows the plugin version.                    |                         |
| `/vanish reload`                | Reloads the configuration and hooks.         |                         |
| `/vanish interact`              | Toggle block interaction while vanished.     |                         |
| `/vanish priority`              | Displays your vanish priority.               |                         |
| `/vanish list`                  | Lists all vanished players.                  |                         |
| `/vanish status <player>`       | Check if a player is vanished.               |                         |
| `/vanish set <player> <on/off>` | Set another player's vanish status.          |                         |
| `/vanish toggle <player>`       | Toggle another player's vanish status.       |                         |
| `/vanish help`                  | Displays the help message.                   |                         |

## Permissions

| Permission                             | Description                                        | Default    |
|----------------------------------------|----------------------------------------------------| ---------- |
| `advancedvanish.vanish`                | Allows toggling your own vanish status.            | `true`     |
| `advancedvanish.version-command`       | Allows using the `/vanish version` command.        | `true`     |
| `advancedvanish.reload-config-command` | Allows using the `/vanish reload` command.         | `op`       |
| `advancedvanish.interact-command`      | Allows using the `/vanish interact` command.       | `op`       |
| `advancedvanish.priority-command`      | Allows using the `/vanish priority` command.       | `true`     |
| `advancedvanish.list-command`          | Allows using the `/vanish list` command.           | `op`       |
| `advancedvanish.status-command`        | Allows using the `/vanish status` command.         | `op`       |
| `advancedvanish.set-other-command`     | Allows using the `/vanish set` command.            | `op`       |
| `advancedvanish.toggle-other-command`  | Allows using the `/vanish toggle` command.         | `op`       |
| `advancedvanish.help-command`          | Allows using the `/vanish help` command.           | `true`     |
| `advancedvanish.see-vanished`          | Allows seeing vanished players.                    | `op`       |

*Note: The actual permission nodes are configurable in `config.yml`.*

## Dependencies

AdvancedVanish has soft dependencies on the following plugins, which means it can integrate with them if they are installed, but they are not required for the plugin to run:

-   LuckPerms
-   PlaceholderAPI
-   GroupManager
-   PermissionsEx
-   bPermissions
-   DiscordSRV
-   Dynmap
-   squaremap
-   LibsDisguises
-   PlayerParticles
-   Essentials
-   ProtocolLib

## Configuration

The plugin is highly configurable. You can customize messages, permission nodes, and various other settings in the `config.yml` file.

