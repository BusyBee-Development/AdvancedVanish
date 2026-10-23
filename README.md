# AdvancedVanish

[![Wiki](https://img.shields.io/badge/Wiki-busybeedev.net-F5B800)](https://busybeedev.net/docs/advancedvanish/intro)
[![Discord](https://img.shields.io/badge/Discord-Join-5865F2?logo=discord&logoColor=white)](https://discord.com/invite/abdm29q7af)

A vanish plugin for Minecraft Paper and Folia servers, from 1.21 to 26.x. Staff disappear from the world, tab list and server list, with configurable restrictions, LuckPerms vanish priority, silent chests and cross-server sync.

## Features

- Vanish that hides staff from players, the tab list, tab completion and the server list
- Configurable rules for chat, blocks, items, damage, hunger and mob targeting while vanished
- Vanish priority through LuckPerms, so senior staff can see junior staff
- Silent chest, barrel and shulker box opening
- Cross-server vanish sync over Redis or MySQL, and saved vanish states across restarts
- Native Folia support, plus EssentialsX, PlaceholderAPI, DiscordSRV, dynmap and squaremap integrations

## Requirements

- Minecraft 1.21+ (up to 26.x)
- Paper or Folia
- Java 21+

## Installation

1. Download the latest release from [GitHub Releases](https://github.com/BusyBee-Development/AdvancedVanish/releases).
2. Drop the jar into your `plugins` folder.
3. Restart the server.

## Documentation

Everything else, including commands, permissions, configuration and integrations, is on the **[wiki](https://busybeedev.net/docs/advancedvanish/intro)**.

- [Quick Start](https://busybeedev.net/docs/advancedvanish/quick-start)
- [Commands & Permissions](https://busybeedev.net/docs/advancedvanish/commands-permissions)
- [Configuration](https://busybeedev.net/docs/advancedvanish/main-configuration)
- [FAQ](https://busybeedev.net/docs/advancedvanish/faq)
- [Migration Guide](https://busybeedev.net/docs/advancedvanish/migration-guide)
- [Developer API](https://busybeedev.net/docs/advancedvanish/developer-api)

## Support

- Bugs and feature requests: [GitHub Issues](https://github.com/BusyBee-Development/AdvancedVanish/issues)
- Questions and help: [Discord](https://discord.com/invite/abdm29q7af)

## Credits

- [quantiom](https://github.com/quantiom), original author of [AdvancedVanish](https://github.com/quantiom/AdvancedVanish)
- [R00tB33rMan](https://github.com/R00tB33rMan), 1.20.4 updates and version detection
- [tlm9201](https://github.com/tlm9201), UUID primary key fix for SQL sync
- casper, Jaren Knodel, PhoenixZTT, Save, AjMaacc, BrianMay and Austin, contributions to the original project (vanished metadata, squaremap, join messages, infinite effects and more)
- [djtmk](https://github.com/djtmk1), Paper and Folia rewrite, maintainer

## License

Licensed under the [MIT License](LICENSE). The original copyright notice from quantiom is kept.
