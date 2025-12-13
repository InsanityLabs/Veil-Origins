# Veil Origins - Quick Start Guide

## 🔧 Building the Mod

The mod is currently building. This takes 3-5 minutes on first build.

### Build Command
```bash
.\gradlew.bat clean build --no-daemon
```

### What Changed
- **NeoForge Version**: Updated to 21.1.216 (matches your game)
- **Mod Loader**: Changed to `lowcodefml` (correct for NeoForge 21.1+)

## 📦 Installation

Once the build completes, the JAR will be at:
```
build/libs/veil_origins-1.0.0.jar
```

Copy this file to your Minecraft mods folder.

## 🎮 In-Game Usage

### Commands (Requires OP)
```
/veilorigins list                    # List all origins
/veilorigins set @s veilborn        # Become Veilborn
/veilorigins set @s stoneheart      # Become Stoneheart  
/veilorigins set @s feralkin        # Become Feralkin
/veilorigins reset @s               # Remove origin
```

### Keybinds
- **R** - Activate Ability 1
- **V** - Activate Ability 2
- **O** - Show Origin Info

### HUD
- **Resource Bar**: Above hotbar (colored by origin)
- **Ability Cooldowns**: Right side of screen

## 🌟 Available Origins

### 1. Veilborn (High Impact)
**Abilities:**
- **Veil Step** (R): Teleport 20 blocks forward
- **Spectral Form**: Night vision in darkness

**Resource**: Soul Energy (Purple bar)

### 2. Stoneheart (Low Impact)
**Abilities:**
- **Seismic Slam** (R): Ground pound with AoE damage
- **Living Mountain**: 50% more HP, natural armor, no fall damage
- **Earth Affinity**: 50% slower movement

**Resource**: Stone Armor (Gray bar)

### 3. Feralkin (Low Impact)
**Abilities:**
- **Primal Roar** (R): Fear enemies, rally animals
- **Beast Form** (V): 60s transformation with buffs
- **Predator Senses**: See wounded entities, night vision
- **Natural Weapons**: +2 attack damage

**Resource**: Feral Instinct (Green bar)

## 🐛 Troubleshooting

### "Mod is for older version of NeoForge"
- Make sure you rebuilt after updating gradle.properties
- Check that mods.toml has `modLoader="lowcodefml"`

### Mod doesn't load
- Verify NeoForge version: 21.1.216
- Check Minecraft version: 1.21.1
- Ensure Java 21 is being used

### Abilities don't work
- Make sure you have enough resource (check bar above hotbar)
- Wait for cooldown to finish (icons on right side)
- Some abilities require specific conditions (e.g., on ground)

## 📝 Development

### Adding More Origins
See `ModOrigins.java` for examples. Use the `OriginBuilder` pattern:

```java
Origin newOrigin = new OriginBuilder("veil_origins:origin_name")
    .setDisplayName("Display Name")
    .setDescription("Description")
    .setImpactLevel(ImpactLevel.MEDIUM)
    .addAbility(new CustomAbility())
    .addPassive(new CustomPassive())
    .build();
```

### Project Structure
- `api/` - Public API for addon developers
- `origins/` - Origin implementations
- `client/` - Client-side rendering and input
- `event/` - Event handlers
- `command/` - Commands

## 🔗 Resources

- Spec: `veil_origins_spec.md`
- Implementation Status: `IMPLEMENTATION_STATUS.md`
- README: `README.md`
