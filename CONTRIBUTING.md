# Contributing to Veil Origins

First off, thank you for considering contributing to Veil Origins! 🎉

We welcome contributions from everyone and appreciate every contribution, whether it's a bug fix, new feature, or documentation improvement.

## 📋 Table of Contents

- [Code of Conduct](#-code-of-conduct)
- [How Can I Contribute?](#-how-can-i-contribute)
- [Getting Started](#-getting-started)
- [Development Workflow](#-development-workflow)
- [Coding Guidelines](#-coding-guidelines)
- [Submitting Changes](#-submitting-changes)
- [Adding New Origins](#-adding-new-origins)
- [Contributor License Agreement](#-contributor-license-agreement)
- [Questions?](#-questions)

---

## 📜 Code of Conduct

This project and everyone participating in it is governed by our commitment to a welcoming and inclusive environment. Please be respectful and constructive in all interactions.

**Key points:**

- Be welcoming and inclusive
- Be respectful of differing viewpoints
- Accept constructive criticism gracefully
- Focus on what is best for the community

---

## 🤝 How Can I Contribute?

### Reporting Bugs

Before creating a bug report, please check existing issues to avoid duplicates.

**When reporting bugs, include:**

- Minecraft version
- NeoForge version
- Veil Origins version
- Other mods installed
- Steps to reproduce
- Expected vs actual behavior
- Crash logs (if applicable)

### Suggesting Features

We love new ideas! When suggesting:

- Check if the feature already exists or has been requested
- Describe the feature clearly
- Explain why it would benefit users
- Include mockups or examples if possible

### Pull Requests

We actively welcome pull requests for:

- Bug fixes
- New origins
- New abilities or passives
- Documentation improvements
- Performance optimizations
- Translations

---

## 🚀 Getting Started

### Prerequisites

- **Java JDK 21+** - Required for compilation
- **Git** - For version control
- **An IDE** - IntelliJ IDEA or Eclipse recommended

### Setting Up the Development Environment

1. **Fork the repository**

   ```bash
   # Clone your fork
   git clone https://github.com/your-username/Veil-Bound-Mods.git
   cd Veil-Bound-Mods/veil_origins_mod
   ```

2. **Build the project**

   ```bash
   ./gradlew build
   ```

3. **Generate IDE files (optional)**

   ```bash
   # For IntelliJ IDEA
   ./gradlew idea

   # For Eclipse
   ./gradlew eclipse
   ```

4. **Run the development client**
   ```bash
   ./gradlew runClient
   ```

---

## 💻 Development Workflow

### Branching Strategy

- `main` - Stable release branch
- `dev` - Development branch for upcoming releases
- `feature/*` - Feature branches
- `bugfix/*` - Bug fix branches

### Workflow

1. Create a branch from `dev`:

   ```bash
   git checkout dev
   git pull origin dev
   git checkout -b feature/my-awesome-feature
   ```

2. Make your changes

3. Test locally:

   ```bash
   ./gradlew build
   ./gradlew runClient
   ```

4. Commit with meaningful messages:

   ```bash
   git commit -m "feat: add new ability for Veilborn origin"
   ```

5. Push and create a Pull Request

---

## 📝 Coding Guidelines

### Java Style Guide

- Use **4 spaces** for indentation (not tabs)
- **Class names**: PascalCase (e.g., `VeilStepAbility`)
- **Method/variable names**: camelCase (e.g., `getPlayerOrigin`)
- **Constants**: SCREAMING_SNAKE_CASE (e.g., `MAX_RESOURCE`)
- Maximum line length: **120 characters**

### File Organization

```
src/main/java/com/veilorigins/
├── api/              # Public API classes
├── origins/          # Origin implementations
│   └── <origin>/     # Each origin gets its own package
│       ├── *Ability.java
│       └── *Passive.java
├── client/           # Client-only code
├── network/          # Networking code
└── registry/         # Registration classes
```

### Documentation

- Add Javadoc comments to all public methods
- Include `@param`, `@return`, and `@throws` tags
- Document complex logic with inline comments

```java
/**
 * Activates the Veil Step ability for the player.
 *
 * @param player The player activating the ability
 * @param level The current level
 * @return true if activation was successful
 */
public boolean activate(Player player, Level level) {
    // Implementation
}
```

### Commit Messages

Follow [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` New features
- `fix:` Bug fixes
- `docs:` Documentation changes
- `style:` Code style changes (formatting, etc.)
- `refactor:` Code refactoring
- `test:` Adding/updating tests
- `chore:` Maintenance tasks

Examples:

```
feat: add Blood Moon event for werewolf origin
fix: resolve cooldown not resetting on death
docs: update README with new commands
```

---

## 🎭 Adding New Origins

### Step 1: Create the Origin Package

Create a new package under `src/main/java/com/veilorigins/origins/<originname>/`

### Step 2: Implement Abilities

```java
package com.veilorigins.origins.myorigin;

import com.veilorigins.api.Ability;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class MyAbility extends Ability {
    public MyAbility() {
        super(
            "veil_origins:my_ability",
            "My Ability",
            "Description of what this ability does",
            30,   // cooldown in seconds
            20f   // resource cost
        );
    }

    @Override
    public void onActivate(Player player, Level level) {
        // Ability logic here
    }

    @Override
    public boolean canActivate(Player player) {
        return super.canActivate(player) && additionalConditions;
    }
}
```

### Step 3: Implement Passives

```java
package com.veilorigins.origins.myorigin;

import com.veilorigins.api.Passive;
import net.minecraft.world.entity.player.Player;

public class MyPassive extends Passive {
    public MyPassive() {
        super(
            "veil_origins:my_passive",
            "My Passive",
            "Description of passive effect"
        );
    }

    @Override
    public void tick(Player player) {
        // Called every tick for active passive effects
    }

    @Override
    public void onApply(Player player) {
        // Called when origin is set
    }

    @Override
    public void onRemove(Player player) {
        // Called when origin is removed
    }
}
```

### Step 4: Register the Origin

Add your origin to `ModOrigins.java`:

```java
private static void registerMyOrigin() {
    Origin myOrigin = new OriginBuilder("veil_origins:myorigin")
            .setDisplayName("My Origin")
            .setDescription("Description of your origin")
            .setImpactLevel(ImpactLevel.MEDIUM)
            .setHealthModifier(1.0f)
            .setSpeedModifier(1.0f)
            .addAbility(new MyAbility())
            .addPassive(new MyPassive())
            .setResourceType(new ResourceType("my_resource", 100, 0.5f))
            .build();

    VeilOriginsAPI.registerOrigin(myOrigin);
}
```

Don't forget to call `registerMyOrigin()` in the `register()` method!

### Step 5: Add Assets (Optional)

If your origin needs textures or models:

- Place textures in `resources/assets/veil_origins/textures/`
- Add lang entries to `resources/assets/veil_origins/lang/en_us.json`

---

## 📜 Contributor License Agreement

By submitting a contribution to Veil Origins, you agree to the following terms as outlined in the [LICENSE.md](LICENSE.md):

- You grant Insanity Labs & Insanity Studios a **perpetual, irrevocable, worldwide, royalty-free license** to use, modify, and distribute your contribution
- Your contribution may be incorporated into the mod and all derivative works
- You will receive **attribution** in the in-game credits, README.md, and CONTRIBUTING.md upon acceptance
- You retain the right to use your contributed code in other personal projects

For full details, please read the [Clopen Source License](LICENSE.md).

---

## ❓ Questions?

- **Discord**: Join our community server for real-time help
- **GitHub Discussions**: For longer-form questions
- **Issues**: For bug reports and feature requests

---

## 🙏 Thank You!

Every contribution helps make Veil Origins better. Whether you're fixing a typo, adding a new origin, or improving performance - we appreciate your help!

---

<p align="center">Made with ❤️ by the Veil Origins Community</p>
