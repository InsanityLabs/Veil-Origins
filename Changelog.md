# Changelog

## v1.0.4 (2025-12-29)

### Updated

- **Backported to Minecraft 1.21.8 with NeoForge 21.8.47**
- Parchment mappings updated to 2025.07.20 for 1.21.8

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

#### Radial Menu Clickability (1.21.8)
- **Fixed radial menu not responding to mouse clicks**
  - Root cause: Custom `handleMouseClick`, `handleKeyPress`, and `handleKeyRelease` methods were not being called
  - Added proper `@Override` annotations for `mouseClicked`, `keyPressed`, and `keyReleased` methods
  - Menu now correctly handles segment clicks, subsection clicks, and center clicks
  - ESC key properly closes the menu
  - R key refreshes origins in origin select mode

#### Keybind Registration Crash (1.21.8)
- **Fixed crash on startup due to duplicate keybind registration**
  - Root cause: Keybindings were being registered twice:
    - Once via `@EventBusSubscriber` in `KeyBindings.java`
    - Again via `modEventBus.addListener(this::registerKeyMappings)` in `VeilOrigins.java`
  - Removed duplicate registration from `VeilOrigins.java`
  - Keybinds are now registered only once via `@EventBusSubscriber`

#### API Compatibility (1.21.8)
- Fixed `FMLEnvironment.getDist()` → `FMLEnvironment.dist` for NeoForge 21.8
- Fixed `DeferredRegister.Items.registerItem()` to use direct `Item.Properties` instead of `Supplier<Item.Properties>`
- Fixed `KeyMapping` constructor to use String-based categories instead of `KeyMapping.Category`

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
