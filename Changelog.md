# Changelog

## v1.0.5 - The Progression Update (2026-01-06)

### Added

#### New Origin Selection Screen - Card Carousel
- **Replaced radial menu with card carousel** for origin selection
  - 8 origin cards displayed in horizontal carousel with smooth scrolling
  - Cards scale based on distance from center (3D depth effect)
  - Each card shows: origin name, impact level (stars), description, abilities, passives
  - Color-coded accent bars based on origin theme
  - Navigation: arrow keys, mouse scroll, click arrows, or click cards directly
  - Shuffle button (R key) to get new random origin options
  - Smooth animations and hover effects

#### New Ability Dashboard
- **Completely redesigned ability menu** with comprehensive origin information
- **Three-column layout:**
  - **Left Column - Origin Info & Stats:**
    - Origin name with theme color accent
    - Impact level with stars (★☆☆, ★★☆, ★★★)
    - Full description (word-wrapped)
    - Resource bar with origin-specific name (Blood, Rage, Soul Energy, Heat, etc.)
    - Stat modifiers display (Health, Speed, Damage) with +/- percentages
  - **Center Column - Abilities:**
    - Detailed ability slots with icon, keybind badge, name
    - Description and stats (cost, cooldown)
    - Status indicator (READY in green, countdown in red)
    - Color-coded accent bar showing ability state
    - Hover highlighting with origin theme color
  - **Right Column - Passives & Progression:**
    - List of passive abilities with thematic icons
    - Level display
    - XP progress bar with current/needed values
    - Skill points available
    - Unlocked skills count
- Press [T] from ability menu to jump directly to skill tree
- Smooth open animation

#### Complete Skill Tree System
- **22 Unique Skill Trees** - Each origin has a detailed skill tree with 25-35 skills
  - Tiered progression (Tier 1-5) with level requirements
  - Mutually exclusive branches for meaningful choices
  - Synergy skills that require multiple paths
  - Forbidden skills with powerful effects but drawbacks
  - Ascended skills that transcend origin weaknesses

- **Skill Tree GUI** - Press the skill tree keybind to open
  - Zoomable and pannable tree view
  - Color-coded branches (Offense, Defense, Utility, Elemental, etc.)
  - Visual indicators for unlocked, available, and locked skills
  - Detailed tooltips with effects, requirements, and exclusions
  - Click to unlock skills with skill points

- **Skill Effects System** - Skills now apply real gameplay effects
  - Attribute modifiers (health, armor, speed, damage, attack speed)
  - Special effects (lifesteal, stealth damage, summons, etc.)
  - Resource modifiers (regen rate, max resource)
  - Cooldown reduction bonuses
  - Damage type bonuses and resistances

#### Complete Progression System
- **Origin Leveling (1-50)** - Gain XP through combat, exploration, and origin-themed activities
  - XP scales exponentially with level
  - Each level grants +1 Skill Point
  - Milestone rewards at levels 10, 25, and 50
  - Level 10: 10% cooldown reduction
  - Level 25: 15% ability power increase
  - Level 50: Unlocks Legendary Ability and Hybrid Mode

- **Prestige System (0-10)** - Reset to level 1 with permanent bonuses after reaching level 50
  - Each prestige grants +5% to all origin stats permanently
  - Cosmetic aura changes with prestige level
  - +10% XP gain bonus per prestige level
  - Maximum 10 prestiges per origin

- **Skill Points** - Earned on level up, used for skill tree talents
  - 1 skill point per level (50 total before prestige)
  - Persists across sessions and server restarts

#### Legendary Abilities
- **Ultimate powers unlocked at Level 50** - Activated by holding R + Attack
- Each origin has a unique legendary ability with powerful effects:
  - **Veilborn - Veil Mastery**: Become invulnerable and drain life from all nearby enemies
  - **Vampire - Blood Moon Rising**: Transform into a bat swarm, draining all nearby creatures
  - **Werewolf - Alpha's Fury**: Summon a wolf pack and unleash a devastating howl
  - **Stoneheart - Mountain's Wrath**: Create a massive earthquake, becoming invulnerable
  - **Frostborn - Absolute Zero**: Freeze everything in a massive radius
  - **Cindersoul - Volcanic Eruption**: Rain fire and destruction on enemies
  - **Tidecaller - Tsunami**: Summon a devastating tidal wave
  - **Starborne - Supernova**: Explode with stellar energy, blinding and damaging all
  - **Skyborn - Eye of the Storm**: Become the center of a devastating tornado
  - **Umbrakin - Eternal Night**: Plunge the area into darkness, becoming invisible and deadly
  - **Riftwalker - Dimensional Collapse**: Tear reality apart, scattering enemies
  - **Voidtouched - Void Singularity**: Create a black hole that pulls and destroys
  - **Mycomorph - Fungal Apocalypse**: Release devastating spores that infect all enemies
  - **Crystalline - Prismatic Burst**: Release stored crystal energy in an explosion
  - **Technomancer - System Overload**: Overclock all systems with an EMP blast
  - **Ethereal - Spectral Dominion**: Become fully ethereal and terrify enemies
  - **Dryad - Nature's Wrath**: Summon nature's fury to heal allies and punish enemies
  - **Necromancer - Army of the Dead**: Raise a massive undead army
  - **Vampling - Blood Frenzy**: Enter a blood frenzy with speed and lifesteal
  - **Wolfling - Pack Leader**: Summon wolves and gain pack bonuses

#### Origin Hybrid System
- **Combine two origins at Level 50** - Activate via command or future GUI
  - Duration: 300 seconds (5 minutes)
  - Cooldown: 1 hour after deactivation
  - Combines first ability and first passive from each origin
  - Warning messages at 60s, 30s, 10s, and 5s remaining
  - Cannot hybrid with the same origin

#### Passive XP Gains
- Origins now gain XP passively based on themed activities:
  - Cindersoul: XP in the Nether
  - Tidecaller: XP while in water
  - Starborne: XP in sunlight during day
  - Umbrakin: XP at night or in darkness
  - Frostborn: XP in cold biomes
  - Skyborn: XP at high altitude (Y > 150)
  - Stoneheart: XP underground (Y < 50)
  - Dryad: XP during photosynthesis (sunlight)
  - Werewolf/Wolfling: XP at night
  - Vampire/Vampling: XP at night

#### New Admin Commands
- `/veilorigins setlevel <player> <1-50>` - Set player's origin level
- `/veilorigins addxp <player> <amount>` - Add XP to player
- `/veilorigins setprestige <player> <0-10>` - Set prestige level
- `/veilorigins addskillpoints <player> <amount>` - Add skill points
- `/veilorigins info <player>` - Show detailed progression info
- `/veilorigins hybrid activate <player> <origin>` - Force activate hybrid mode
- `/veilorigins hybrid deactivate <player>` - Force deactivate hybrid mode

### Fixed

#### UI Blur Rendering Issue
- **Fixed blur rendering over UI elements** in Minecraft 1.21.11
  - Default `renderBackground()` adds blur effect that was rendering ON TOP of custom UI
  - Added `renderBackground()` override to all custom screens to prevent blur
  - Affected screens: SkillTreeScreen, OriginCardCarouselScreen, AbilityBarScreen, RadialMenuScreen, HudConfigScreen

#### Dedicated Server Crash - NoClassDefFoundError: LocalPlayer
- **Fixed server crash when players join dedicated servers**
  - Root cause: Client-only class `LocalPlayer` was referenced in network packet handlers
  - Created `ClientPacketSender.java` to isolate client-side packet sending code
  - Created `SyncOriginDataPacketHandler.java` for client-side packet handling
  - Server now properly handles all network packets without client class references

#### "Modifier is already applied" Error on Respawn
- **Fixed crash when players respawn after dying**
  - Root cause: Player entity is replaced on respawn, but UUID stays the same
  - Changed `PLAYER_ORIGINS` cache from `Map<Player, Origin>` to `Map<UUID, Origin>`
  - Updated all passive classes to remove modifiers before adding (prevents duplicates)
  - Added `.copyOnDeath()` to attachment type to preserve data across respawns

#### Ethereal Possession Ability
- **Fixed possession not working correctly**
  - Rewrote possession so mob follows player (not player teleporting to mob)
  - Player moves normally with WASD, possessed mob syncs to player position
  - Player gets invisibility effect while possessing
  - Fixed mob AI restoration after possession ends
  - Properly clears targets, recomputes navigation, gives movement impulse to "wake up" mob

#### HUD Sync and Data Bleeding
- **Fixed HUD not syncing level, XP, resource bar, cooldowns on dedicated server**
  - Created `ClientOriginData.java` for client-side cache of synced data
  - Sync interval changed from 20 ticks to 5 ticks (4x faster updates)
  - Now tracks and syncs level/XP changes immediately
  
- **Fixed data bleeding between servers/worlds**
  - Added `ClientOriginData.clear()` and `ClientPossessionHandler.clear()` on login AND logout
  - Added `VeilOriginsAPI.clearClientCache()` to clear origin cache when switching servers
  - Client now properly clears all cached data before receiving new server data

#### Skill Tree Persistence and Functionality
- **Fixed skills not saving/syncing on dedicated servers**
  - Added `unlockedSkills` field to `SyncOriginDataPacket` (comma-separated string)
  - Skills now persist in world data via player data attachments
  - Skills sync from server to client on login, respawn, and skill unlock

- **Fixed "Skill not found" warnings on dedicated server**
  - Root cause: `SkillTrees.initialize()` was called in `commonSetup` which runs asynchronously
  - Moved initialization to mod constructor to ensure trees are ready before players join
  - Added `synchronized` guard and logging to prevent double-initialization

- **Fixed skills not applying actual effects**
  - Unified skill system to use `SkillTrees.java` (detailed skills with effects)
  - `SkillEffectHandler` now properly applies attribute modifiers from skill definitions
  - Skills apply health, armor, speed, damage, attack speed, and special effects

### Technical
- Added `SkillTrees.java` - Registry of 22 detailed skill trees with 25-35 skills each
- Added `SkillTreeData.java` - Data structure for skill tree with connections
- Added `Skill.java` - Skill node with tiers, branches, prerequisites, exclusions, effects
- Added `SkillEffect.java` - Effect definitions (attribute modifiers, special effects)
- Added `SkillEffectHandler.java` - Applies/removes skill effects to players
- Added `SkillTreeScreen.java` - Interactive GUI for viewing and unlocking skills
- Added `ClientOriginData.java` - Client-side cache for synced origin data
- Added `ClientPacketSender.java` - Client-side packet sending isolation
- Added `SyncOriginDataPacketHandler.java` - Client-side sync packet handling
- Updated `UnlockSkillPacket.java` - Uses `SkillTrees` for skill lookup and validation
- Updated `SyncOriginDataPacket.java` - Added `unlockedSkills` field
- Updated `OriginEventHandler.java` - Uses `SkillEffectHandler` for skill effects
- Updated `VeilOriginsAPI.java` - Added `clearClientCache()` method
- Updated `ClientEventHandler.java` - Clears all caches on login/logout

---

## v1.0.4 - 1.21.11 (2025-12-29)

### Updated

- **Minecraft 1.21.11 with NeoForge 21.11.14-beta**
- Parchment mappings updated to 2025.12.20 for 1.21.11

### Migration Changes (1.21.10 → 1.21.11)

Based on the [1.21.10 → 1.21.11 Migration Primer](https://github.com/ChampionAsh5357/minecraft-mod-migration-primer):

- **MoonPhase API Change** - `Level#getMoonPhase()` removed, now calculated manually from day time
  - Moon phase cycles every 8 days (192000 ticks), phase 0 is full moon
  - Affects Werewolf full moon detection

---

## v1.0.4 (2025-12-29)

### Updated

- **Minecraft 1.21.10 with NeoForge 21.10.64**
- Parchment mappings updated to 2025.10.12 for 1.21.10

### Added

#### Vampire Blood System Overhaul
- **Blood Bar replaces Hunger Bar** - Vampires now have a blood bar that replaces the vanilla hunger bar
  - Blood drains slowly over time (faster when sprinting or healing)
  - High blood (80+) enables natural regeneration
  - Empty blood causes starvation damage
  - Blood syncs to hunger level for compatibility

- **Blood Bottle Items** - New consumable items for vampires
  - Empty Blood Bottle - craft from glass bottle
  - Half Blood Bottle - restores 50 blood
  - Full Blood Bottle - restores 100 blood
  - Drinking gives regeneration effect
  - Only vampires can drink blood bottles

- **Bottle Filling Mechanic** - Fill blood bottles while draining animals
  - Hold empty/half bottle in offhand while using Blood Drain ability
  - Hold empty/half bottle in offhand while using Blood Drain Gaze passive
  - Empty → Half → Full progression

#### Vampire Ability Blood Costs
- **Bat Form** - Costs 25 blood to activate
- **Vampiric Leap** - Costs 5 blood per leap
- **Blood Drain** - Free (gives +15 blood per drain)

#### Sun Damage Fix
- Fixed vampire sun damage not working properly
- Now correctly checks daytime (0-12500 or 23500-24000)
- Rain provides protection from sun
- Visual fire effect when burning

#### Custom Resource Bar API
- **CustomResourceBar** - New API class for configuring custom resource bars
  - Multiple styles: ICONS, SOLID_BAR, SEGMENTED_BAR, REPLACE_HUNGER, REPLACE_HEALTH
  - Configurable positions: HOTBAR_LEFT, HOTBAR_RIGHT, corners, custom
  - Custom colors (primary, secondary, critical, background, border)
  - Sprite support for icon-based bars
  - Animation options (pulse when low, bounce when critical)
  - Threshold settings for low/critical states
  - Factory methods: `bloodBar()`, `manaBar()`, `heatBar()`, `hydrationBar()`, `stellarBar()`

- **ResourceType** - Updated with custom bar support
  - `setCustomBar(CustomResourceBar)` - attach custom bar configuration
  - Factory methods for common resource types

- **CustomBarRenderer** - New renderer for custom bars

### Fixed

#### Radial Menu Clickability (1.21.10)
- **Fixed radial menu not responding to mouse clicks**
  - Root cause: NeoForge 21.10 changed `Screen` input handling API
    - `mouseClicked(double, double, int)` → `mouseClicked(MouseButtonEvent, boolean)`
    - `keyPressed(int, int, int)` → `keyPressed(KeyEvent)`
  - Implemented GLFW polling workaround in `tick()` method
  - Uses `GLFW.glfwGetMouseButton()` and `GLFW.glfwGetKey()` for direct input polling
  - Uses `GLFW.glfwGetCurrentContext()` to get the window handle
  - Edge detection ensures actions trigger only on key/button press
  - Menu now correctly handles segment clicks, subsection clicks, and center clicks
  - ESC key properly closes the menu
  - R key refreshes origins in origin select mode

#### Keybind Registration Issue (1.21.10)
- **Fixed duplicate keybind registration causing issues**
  - Root cause: Keybindings were being registered twice:
    - Once via `@EventBusSubscriber` in `KeyBindings.java`
    - Again via `modEventBus.addListener(this::registerKeyMappings)` in `VeilOrigins.java`
  - Removed duplicate registration from `VeilOrigins.java`
  - Keybinds are now registered only once via `@EventBusSubscriber`

### Other Fixes
- Vampire sun damage now works correctly
- Blood bar sprites render properly using `blitSprite` with `RenderPipelines.GUI_TEXTURED`

---

## v1.0.3 (2025-12-21) - RELEASED

### Added

#### Unicode Font Handler System

- **New `UnicodeFontHandler`** - Custom font rendering system for improved Unicode support

  - Uses Java AWT to load TrueType fonts with full Unicode character support
  - Generates bitmap texture atlases for efficient GPU rendering
  - Automatic glyph caching for performance
  - Graceful fallback to Minecraft's default font for unsupported characters
  - Auto-detects system fonts with good Unicode coverage (Segoe UI Symbol, Arial Unicode MS, DejaVu Sans, etc.)
  - Pre-caches common Unicode ranges: Basic Latin, Latin-1 Supplement, General Punctuation, Symbols, Dingbats

- **New `UnicodeFontUtils`** - Utility class for formatted text with Unicode

  - Symbol text helpers: `checkText()`, `crossText()`, `energyText()`, `diamondText()`, etc.
  - Status indicators with color-coded symbols
  - Progress bar generation using Unicode block characters (█░)
  - Cooldown formatting with automatic time conversion
  - Decorative elements: separators, boxed titles, bullet points

- **Unicode Symbol Constants** - Easy access to commonly used symbols:
  - ✓ ✗ ⚡ ♦ ♥ ★ ● → ← ↑ ↓ ∞ ☠ ☀ ☽

#### HUD Configuration System

- **New HUD Config Screen** - Press **H** to open a dedicated screen for customizing HUD elements

  - Toggle individual HUD elements on/off with visual ON/OFF indicators
  - Adjust HUD opacity (0-100%) with visual slider
  - "Enable All" / "Disable All" quick preset buttons
  - Changes save automatically

- **New Keybind** - `H` key opens the HUD Configuration screen (configurable in Controls)

- **Granular HUD Toggles** - Control visibility of each HUD element individually:

  - Origin Info Panel (name and level display)
  - Resource Bar (blood, heat, hydration, etc.)
  - XP Progress Bar
  - Ability Indicators (cooldown boxes)
  - Passive Indicators (e.g., double jump ready)
  - Cooldown Overlays (animated cooldown fill)
  - Keybind Hints ([R], [V] labels)
  - Unicode Symbols (✓/✗ vs ASCII)

- **Config Screen in Mod List** - Access mod settings from the NeoForge mod list "Config" button

#### Vampire & Vampling - New Passives

- **Vampiric Double Jump** - Sprint and double-tap jump while airborne to perform a damaging leap!

  - Deals damage to nearby enemies (6 HP for Vampire, 3 HP for Vampling)
  - Knocks back enemies hit
  - Provides forward momentum boost
  - Visual crimson spore particles and smoke effects
  - Audio feedback with bat takeoff and phantom flap sounds
  - Cooldown: 1.5s (Vampire) / 2s (Vampling)

- **Blood Drain Gaze** - Crouch and stare at a creature with blood for 5 seconds to drain it!
  - Works on any creature with blood: large animals, villagers, pillagers, players
  - Excludes undead (zombies, skeletons), constructs (iron golems), and tiny creatures (bats, chickens)
  - Progress bar UI while focusing
  - Crimson particle stream from target to player
  - Deals continuous damage while healing the vampire
  - Slows and weakens the target
  - Bonus effects when fully draining a creature (Regeneration II, Strength I)
  - Range: 8 blocks (Vampire) / 5 blocks (Vampling)

#### Werewolf & Wolfling - Dietary Preferences

- **Rotten Flesh Immunity** - Can eat rotten flesh without the hunger debuff

  - Also provides minor healing (2 HP for Werewolf, 1 HP for Wolfling)
  - "The rotten flesh satisfies your feral hunger!"

- **Raw Meat Preference** - Eating raw meat provides bonus effects
  - Regeneration (II for Werewolf, I for Wolfling)
  - Saturation bonus
  - "The raw meat invigorates you!"

#### Vampire & Vampling - Dietary Preferences

- **Raw Meat Bonus** - Eating raw meat provides regeneration

  - "The blood in the meat sustains you!"
  - Full Vampires also get Strength I boost

- **Cooked Meat Weakness** - Cooked meat provides little nourishment
  - Applies Weakness I effect
  - "The cooked meat provides little nourishment..."

### Changed

- **Code Quality: ChatFormatting** - Replaced all `§` section symbol color codes with proper `ChatFormatting` enum

  - Affected files: `OriginCommand`, `OriginEventHandler`, `SelectOriginPacket`, `FoodEventHandler`, `HudConfigScreen`, `RadialMenuScreen`
  - Uses `.withStyle()` and `.append()` for multi-colored Component messages
  - More maintainable, type-safe, and IDE-friendly

- **Unicode Symbol Rendering** - `OriginHudOverlay` now uses `UnicodeFontHandler.getSymbol()` for proper fallback

  - Automatically falls back to ASCII if Unicode symbol can't be rendered

- **Config Split** - Configuration now separated into two files:
  - `veil_origins-common.toml` - Gameplay settings (cooldowns, damage, abilities, sizes)
  - `veil_origins-client.toml` - Client-side HUD/display settings
- **Translation Keys** - Added translation keys for all config options for localization support

### Fixed

#### HUD Config Screen

- **Fixed background rendering above buttons** - Corrected render order so widgets display on top of background
- **Fixed Unicode minus symbol not rendering** - Changed `−10` (U+2212) to ASCII `-10`

#### Config Loading Crash

- **Fixed crash on startup: "Cannot get config value before config is loaded"**
  - Root cause: `onLoad` handler tried to access both COMMON and CLIENT config values when only one was loaded
  - Now checks which config spec is being loaded and only accesses values from that config

#### Multiplayer Origin Synchronization

- **Fixed non-host players unable to use origin abilities (R and V keybinds) in multiplayer**
  - Root cause: Origin data was not being synced from server to client for non-host players
  - `SyncOriginDataPacket.handle()` now properly updates the client-side origin cache
  - Added `VeilOriginsAPI.setPlayerOriginClient()` for client-side cache updates without triggering server-side passives
  - Sync packets are now sent:
    - When a player logs in
    - When a player respawns
    - When a player selects or changes their origin
    - When origin is set/reset via commands
  - Added periodic resource bar sync (every 1 second) to keep HUD accurate
  - Non-host players can now see their origin HUD elements and use all abilities

### Technical

- Added `UnicodeFontHandler` - Custom Unicode font rendering with AWT, texture atlas, and glyph caching
- Added `UnicodeFontUtils` - Utility methods for formatted text with Unicode symbols
- Added `HudConfigScreen` - New GUI screen for HUD configuration
- Added `OptionsScreenHandler` - Utility for opening HUD config from various contexts
- Added `HUD_CONFIG` keybind (H key) with handler in `KeyInputHandler`
- Registered `IConfigScreenFactory` for NeoForge mod list config button
- Split `ModConfigEvent` handler to load COMMON and CLIENT configs separately
- Added `VampiricDoubleJumpPassive` - Server-validated double jump with client-side input detection
- Added `BloodDrainGazePassive` - Crosshair-based drain mechanic with entity type filtering
- Added `FoodEventHandler` - Handles dietary effects for wolf and vampire origins
- Added `DoubleJumpPacket` - Network packet for double jump synchronization
- Entity blood detection uses type ID blacklist for reliable cross-version compatibility
- Initialized `UnicodeFontHandler` in `VeilOrigins.onClientSetup()` for early availability

---

## v1.0.2

### Added

#### New Origins

- **Dryad** - Nature spirit and forest guardian
  - Entangling Roots ability - trap enemies with vines, dealing damage and applying heavy slow
  - Nature's Blessing ability - heal self and allies, grow crops, spawn flowers
  - Sunlight Photosynthesis passive - regenerate health and freeze hunger in sunlight
  - Forest Bond passive - move faster in forests, walk through leaves, animals are friendly
  - Weaknesses: fire vulnerability, damage in deserts/Nether, withers in darkness
- **Necromancer** - Master of death and undeath
  - Raise Dead ability - summon 4 undead minions (zombies and skeletons) to fight for you
  - Life Siphon ability - drain life from enemies, healing yourself for 50% of damage dealt
  - Undead Mastery passive - undead won't attack you, minions follow your commands
  - Death Aura passive - damage nearby enemies, gain buffs at night
  - Weaknesses: sunlight damage, reduced healing effectiveness, weak near holy places

### Improved

- Updated spec documentation with new origins
- Total origin count now at 21

---

## v1.0.1

### Added

#### More Origins

- Cindersoul
- Crystalline
- Ethereal
- Mycomorph
- Skyborn
- Starborne
- Technomancer
- Tidecaller
- Vampire
- Vampling
- Werewolf
- Wolfling

#### Vampire Features

- Blood Drain ability - drain life from nearby enemies
- Bat Form ability - transform into a bat with flight, spawns decoy bats for confusion
- Night vision in darkness, strength and speed at night
- Sunlight damage with helmet protection

#### Werewolf Features

- Wolf Form ability - powerful beast transformation
- Howl ability - buffs self, debuffs enemies
- Enhanced abilities at night, extra power during full moon
- Natural regeneration

### Improved

- Frostborn Blizzard ability now places snow layers and has more intense particles
- Frostborn Ice Spike now has blue particles (soul fire flame)
- Umbrakin night vision no longer flashes
- Starborne Wings of Light flight with hunger drain

### Fixed

- Versioning issue (newest versions will have the correct version numbers)

---

## v1.0.0

### Supported Versions

- Minecraft: 1.21.1 - 1.21.3
- NeoForge: 21.1.x

### Added

#### Initial Origins

- Feralkin
- Frostborn
- Riftwalker
- Stoneheart
- Umbrakin
- Veilborn
- Voidtouched

#### API Handling

- Initial API handler for custom origins
- Docs coming soon
