# 🌙 Veil Origins

<p align="center">
  <strong>A comprehensive origin system for Minecraft 1.21.1 - 1.21.3</strong><br>
  <em>Transform your Minecraft experience with unique character origins, abilities, and passives.</em>
</p>

<p align="center">
  <img alt="NeoForge" src="https://img.shields.io/badge/NeoForge-21.1.216+-blue?style=flat-square">
  <img alt="Minecraft" src="https://img.shields.io/badge/Minecraft-1.21.1--1.21.3-green?style=flat-square">
  <img alt="Java" src="https://img.shields.io/badge/Java-21+-orange?style=flat-square">
  <img alt="License" src="https://img.shields.io/badge/License-Clopen%20v1.0-yellow?style=flat-square">
  <a href="https://github.com/InsanityLabs/Veil-Origins/actions/workflows/gradle.yml">
    <img alt="Java CI with Gradle" src="https://github.com/InsanityLabs/Veil-Origins/actions/workflows/gradle.yml/badge.svg?branch=main">
  </a>
</p>

---

## 📋 Table of Contents

- [Features](#-features)
- [Installation](#-installation)
- [Commands](#-commands)
- [Keybinds](#-keybinds)
- [Available Origins](#-available-origins)
- [Project Structure](#-project-structure)
- [Building from Source](#-building-from-source)
- [Related Mods](#-related-mods)
- [Contributing](#-contributing)
- [License](#-license)

---

## ✨ Features

- ✅ **19 Unique Origins** - Each with distinct abilities, passives, and playstyles
- ✅ **Core API System** - Extensible API for addon developers
- ✅ **Origin Management** - Full registration and player data attachment system
- ✅ **HUD Overlay** - Resource bar + ability cooldowns display
- ✅ **Ability System** - Active abilities with cooldowns and resource costs
- ✅ **Passive Effects** - Origin-specific buffs and debuffs
- ✅ **Admin Commands** - Full control over player origins
- ✅ **Multiplayer Sync** - Network packets for seamless multiplayer
- ✅ **Progression System** - XP, levels, and prestige tracking (WIP)

---

## 📦 Installation

### Requirements

- **Minecraft**: 1.21.1 - 1.21.3
- **NeoForge**: 21.1.216+
- **Java**: 21+

### Steps

1. Download the latest release from [Releases](#)
2. Place the `.jar` file in your Minecraft `mods` folder
3. Launch Minecraft with NeoForge
4. (Optional) Install companion mods: [Veil Quests](#), [Veil Share](#), [Veil Animations](#)

---

## 💻 Commands

All commands require operator permission level 2.

| Command                                | Description                              |
| -------------------------------------- | ---------------------------------------- |
| `/veilorigins list`                    | List all available origins               |
| `/veilorigins set <player> <origin>`   | Set a player's origin                    |
| `/veilorigins reset <player>`          | Reset/remove a player's origin           |
| `/veilorigins resetcooldowns <player>` | Reset all ability cooldowns for a player |

### Examples

```mcfunction
/veilorigins list
/veilorigins set @s vampire
/veilorigins set Steve werewolf
/veilorigins reset @a
/veilorigins resetcooldowns @s
```

---

## ⌨️ Keybinds

| Key | Action                                 |
| --- | -------------------------------------- |
| `R` | Activate Primary Ability (Ability 1)   |
| `V` | Activate Secondary Ability (Ability 2) |
| `O` | Show Origin Info                       |

> **Note**: Keybinds can be changed in Minecraft's Controls menu under the "Veil Origins" category.

---

## 🎭 Available Origins

### High Impact Origins

| Origin          | Description                                           | Abilities                         |
| --------------- | ----------------------------------------------------- | --------------------------------- |
| **Veilborn**    | Masters of the boundary between life and death        | Veil Step, Soul Harvest           |
| **Riftwalker**  | Dimensional travelers who bend space itself           | Dimensional Hop, Pocket Dimension |
| **Umbrakin**    | Shadow manipulators, creatures of darkness            | Shadow Meld, Darkness Bolt        |
| **Voidtouched** | Corrupted by the void, reality benders                | Void Tear, Reality Shift          |
| **Ethereal**    | Ghostly being phased between dimensions               | Phase Shift, Possession           |
| **Vampire**     | Immortal blood drinker with night powers              | Blood Drain, Bat Form             |
| **Werewolf**    | Cursed shapeshifter with moon-powered transformations | Wolf Form, Howl                   |

### Medium Impact Origins

| Origin           | Description                                 | Abilities                        |
| ---------------- | ------------------------------------------- | -------------------------------- |
| **Frostborn**    | Children of winter, masters of ice and cold | Ice Spike, Blizzard              |
| **Cindersoul**   | Born from volcanic fury, masters of flame   | Flame Burst, Lava Walk           |
| **Tidecaller**   | Ocean dwellers who command water            | Tidal Wave, Aqua Bubble          |
| **Starborne**    | Fallen from the cosmos, light-empowered     | Celestial Dash, Starlight Beacon |
| **Skyborn**      | Wind masters who never touch the ground     | Wind Blast, Updraft              |
| **Mycomorph**    | Part mushroom, part humanoid                | Spore Cloud, Fungal Network      |
| **Technomancer** | Cybernetic being, master of redstone        | Redstone Pulse, Overclock        |
| **Vampling**     | Lesser vampire with diluted bloodline       | Life Steal                       |

### Low Impact Origins

| Origin          | Description                                | Abilities                    |
| --------------- | ------------------------------------------ | ---------------------------- |
| **Stoneheart**  | Living stone, immovable and indestructible | Seismic Slam, Stone Skin     |
| **Feralkin**    | Beast shapeshifters with animal instincts  | Primal Roar, Beast Form      |
| **Crystalline** | Living crystal with resonant powers        | Crystal Spike, Ore Resonance |
| **Wolfling**    | Lesser werewolf with partial curse         | Pack Howl                    |

---

## 📁 Project Structure

```
veil_origins_mod/
├── src/
│   ├── main/
│   │   ├── java/com/veilorigins/
│   │   │   ├── api/              # Public API for addon developers
│   │   │   ├── client/           # Client-side rendering, HUD, input
│   │   │   ├── command/          # Admin commands
│   │   │   ├── data/             # Player data attachments
│   │   │   ├── event/            # Event handlers
│   │   │   ├── mixin/            # Mixins
│   │   │   ├── network/          # Network packets
│   │   │   ├── origins/          # All origin implementations
│   │   │   └── registry/         # Origin registration
│   │   └── resources/
│   │       ├── META-INF/         # Mod metadata
│   │       └── assets/veil_origins/
├── build.gradle
├── gradle.properties
├── Changelog.md
└── README.md
```

---

## 🔧 Building from Source

### Prerequisites

- Java 21 or higher
- Gradle (included via wrapper)

### Build Commands

```bash
# Build the mod
./gradlew build

# Clean build
./gradlew clean build

# Run client in development
./gradlew runClient

# Run server in development
./gradlew runServer
```

The compiled JAR will be located at:

```
build/libs/veil_origins_mod-1.0.2.jar
```



## 🤝 Contributing

We welcome contributions! Please see our [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

### Quick Start for Contributors

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Make your changes
4. Test your changes: `./gradlew build`
5. Submit a pull request

---

## 📄 License

This project is licensed under the **Insanity Labs & Insanity Studios Clopen Source License v1.0**.

### Key Terms:

- ✅ **Free to Use** - Mods are completely free for personal gameplay
- ✅ **View Source** - Source code is publicly available for learning
- ✅ **Modpack Inclusion** - Can be included in modpacks without permission
- ✅ **API Extensions** - Create addons using the public API
- ✅ **Contributions** - Submit improvements via pull requests
- ❌ **No Redistribution** - Cannot redistribute source code or modified binaries
- ❌ **No Unauthorized Ports** - Porting requires written approval
- 📝 **Content Creators** - Attribution required unless donating through official channels

See [LICENSE.md](LICENSE.md) for the full license text.

---

## 📞 Support

- **Issues**: Report bugs on [GitHub Issues](https://github.com/InsanityLabs/Veil-Origins/issues)
- **Discord**: Join our community [Discord Server (Soon)](#)
- **Authors**: Insanity Studios

---

<p align="center">Made with ❤️ by Insanity Studios</p>
