# Veil Origins - Custom Mod Specification

**Version:** 1.0.0  
**Minecraft:** 1.21.1 - 1.21.10 (NeoForge)  
**Installation:** Server + Client Required  
**Dependencies:** Geckolib (for animations), Caelus API (for flight mechanics)

---

## 🎯 Core Concept

A comprehensive origin system built from the ground up for Veilbound SMP. Provides 15+ unique playable species with deep mechanics, progression systems, and full API support for addon developers to create custom origins. Unlike the original Origins mod, Veil Origins is designed specifically for long-term SMP gameplay with balanced progression and integration hooks.

---

## 🔧 Developer API

### Creating Custom Origins

**API Structure:**

```java
public class CustomOriginAddon implements VeilOriginsAPI {

    @Override
    public void registerOrigin(OriginBuilder builder) {
        builder.setId("modid:origin_name")
               .setDisplayName("Display Name")
               .setDescription("Lore text")
               .setImpactLevel(ImpactLevel.MEDIUM)
               .addAbility(AbilityType.ACTIVE, new CustomAbility())
               .addAbility(AbilityType.PASSIVE, new CustomPassive())
               .setStartingItems(ItemStack...)
               .setFoodModifier(0.8f) // 80% hunger drain
               .setHealthModifier(1.2f) // 120% health (24 HP)
               .addWeakness(DamageType.FIRE)
               .addResistance(DamageType.COLD)
               .build();
    }
}
```

**Ability System:**

```java
public interface IOriginAbility {
    String getId();
    String getDisplayName();
    int getCooldown(); // ticks
    ResourceCost getCost(); // mana, hunger, custom resource
    void onActivate(Player player, Level level);
    void onTick(Player player); // for passive abilities
    boolean canUse(Player player);
}
```

**Power System:**

```java
public class CustomPower extends OriginPower {
    @Override
    public void applyEffect(Player player) {
        // Custom logic
    }

    @Override
    public boolean shouldApply(Player player, Level level) {
        // Conditional activation (time of day, dimension, etc.)
        return level.isNight();
    }
}
```

**Integration Hooks:**

- `VeilOriginsAPI.onOriginSelected(Player, Origin, Origin)` - fired when origin changes
- `VeilOriginsAPI.getPlayerOrigin(Player)` - get player's current origin
- `VeilOriginsAPI.modifyAbility(Origin, Ability, Modifier)` - other mods can modify origin abilities
- `VeilOriginsAPI.addCustomResource(Origin, ResourceType)` - add custom resource bars

**Addon Examples:**

- Create new origins from other mods' content
- Add abilities that interact with other mods (Create, Ars Nouveau, etc.)
- Modify existing origins with new powers
- Create origin transformation items/rituals

---

## 🌟 Built-In Origins (17+)

### 1. VEILBORN

**Impact Level:** High  
**Theme:** Masters of the boundary between life and death

**Description:**
Born from the Veil itself, you exist between worlds. Death is not your enemy—it is your ally. You can manipulate soul essence and communicate with the deceased, but the living world drains your strength.

**Abilities:**

**[ACTIVE] Veil Step** (Cooldown: 30s, Cost: 3 Soul Essence)

- Teleport up to 20 blocks through solid objects
- Leave behind spectral trail visible to all players for 10 seconds
- Can teleport to death locations (yours or allies')
- Phase through 1-block thick walls

**[ACTIVE] Soul Harvest** (Cooldown: 60s, Cost: 5 Hunger)

- Extract soul essence from nearby dead mobs (10 block radius)
- Gain soul essence equal to mob's health/10
- Hostile mobs drop 2x soul essence when killed by Veilborn
- Can sense soul essence through walls (glowing effect)

**[PASSIVE] Spectral Form**

- Take 50% less damage from magic sources
- Invisible to undead mobs (skeletons, zombies, phantoms ignore you)
- Can see invisible entities and players
- Night vision in darkness (not in bright light)

**[PASSIVE] Life Drain**

- Regenerate health when near death (below 6 HP = Regeneration I)
- Standing near Veil Chambers grants Absorption hearts
- Proximity to living players drains your hunger 2x faster
- Cannot eat normal food (only soul essence or supernatural food)

**Starting Items:**

- 1x Soul Compass (points to nearest soul essence)
- 3x Soul Essence
- 1x Spectral Cloak (cosmetic)

**Weaknesses:**

- Sunlight applies Weakness I (strength restored underground or at night)
- Cannot regenerate health naturally (must use soul essence or potions)
- Holy/radiant damage deals +50% damage
- Take damage in consecrated areas (churches, temples if added by other mods)

**Resource Bar:** Soul Energy (0-100, recharges near death/soul essence)

---

### 2. CINDERSOUL

**Impact Level:** Medium  
**Theme:** Born from volcanic fury, masters of heat and flame

**Description:**
Your body burns with inner fire. You thrive in heat and can manipulate flames, but water and cold are your mortal enemies. The Nether feels like home, while oceans are deadly.

**Abilities:**

**[ACTIVE] Flame Burst** (Cooldown: 15s, Cost: 4 Hunger)

- Release explosion of fire in 8-block radius
- Damages enemies (4 HP fire damage)
- Lights blocks on fire
- Melts snow/ice instantly
- Smelts dropped items caught in blast

**[ACTIVE] Lava Walk** (Cooldown: 5s, Cost: 1 Hunger per 3 blocks)

- Create temporary obsidian platforms on lava
- Lasts 10 seconds before reverting to lava
- Can chain-cast to cross lava lakes
- Platforms others can use (team utility)

**[PASSIVE] Heat Affinity**

- Immune to fire and lava damage
- Regenerate health when standing in fire/lava (1 HP per 2 seconds)
- Deal +2 fire damage with melee attacks
- Food automatically cooks in inventory

**[PASSIVE] Thermal Vision**

- See heat signatures of mobs/players through walls (15 block range)
- Lava/magma blocks highlighted in overworld
- Can detect hidden Create furnaces, boilers, or heat sources

**Starting Items:**

- 1x Fire Charge (x8)
- 1x Flint and Steel (Unbreaking III)
- 1x Lava Bucket

**Weaknesses:**

- Water deals 1 HP damage per second (like drowning)
- Rain applies Slowness II and drains hunger 3x faster
- Snowfall/cold biomes deal continuous 0.5 HP per second damage
- Cannot swim (sink like cobblestone in water)
- Powder snow is lethal (3 HP per second)

**Resource Bar:** Internal Heat (0-100, depletes in water/cold, recharges near fire)

---

### 3. RIFTWALKER

**Impact Level:** High  
**Theme:** Dimensional travelers who bend space itself

**Description:**
You were born in a place between dimensions, giving you unique spatial awareness. You can manipulate space but your form is unstable, causing unpredictable side effects.

**Abilities:**

**[ACTIVE] Dimensional Hop** (Cooldown: 20s, Cost: 3 Ender Pearls)

- Teleport to any location you can see (up to 64 blocks)
- No fall damage after teleporting
- Can teleport while falling
- Leaves ender particle trail

**[ACTIVE] Pocket Dimension** (Cooldown: 120s, Cost: 10 Hunger)

- Open temporary 9-slot inventory (like ender chest but unique to you)
- Accessible anywhere in any dimension
- Lasts until you close it
- Can upgrade with more slots through progression

**[PASSIVE] Spatial Awareness**

- Minimap showing all entities within 32 blocks (even through walls)
- Sense teleportation of other players (message in chat with direction)
- Fall from any height without damage (teleport right before impact)
- Can see through portals to other side

**[PASSIVE] Unstable Form**

- 10% chance to randomly teleport 3-5 blocks when taking damage
- Cannot use boats or minecarts (phase through them)
- Crops/blocks you place have 5% chance to swap position with nearby blocks
- Random teleports cost 1 hunger

**Starting Items:**

- 16x Ender Pearls
- 1x Eye of Ender
- 1x Compass (modified to point at spawn across dimensions)

**Weaknesses:**

- Take +50% damage from void
- Chorus fruit teleports you randomly 240 blocks
- Cannot wear heavy armor (iron/diamond/netherite) - too unstable
- Beds explode (like Nether) because dimensionally incompatible

**Resource Bar:** Dimensional Stability (0-100, depletes with teleports, recharges over time)

---

### 4. TIDECALLER

**Impact Level:** Medium  
**Theme:** Ocean-dwelling masters of water and storms

**Description:**
The ocean is your home. You command water and breathe beneath the waves, but land weakens you. You must stay hydrated or suffer the consequences.

**Abilities:**

**[ACTIVE] Tidal Wave** (Cooldown: 45s, Cost: 8 Hunger)

- Create wave of water that pushes entities 10 blocks
- Damages fire-based enemies
- Extinguishes fires in 15-block radius
- Creates temporary water source blocks (disappear after 30 seconds)

**[ACTIVE] Aqua Bubble** (Cooldown: 30s, Duration: 60s)

- Create bubble of air underwater for allies
- 5-block radius sphere
- Allows land-dwellers to breathe underwater temporarily
- Can place torches/campfires inside bubble

**[PASSIVE] Ocean's Gift**

- Breathe underwater indefinitely
- Swim 50% faster than normal players
- Night vision underwater
- Can break blocks underwater at normal speed
- Dolphins follow you and grant speed boost

**[PASSIVE] Hydration Dependency**

- Must be in water or rain every 10 minutes or suffer Weakness
- Hunger drains 2x faster when fully dry
- Swimming or rain resets hydration timer
- Can carry water bottles to splash yourself (resets timer)

**Starting Items:**

- 1x Trident
- 1x Turtle Shell Helmet
- 8x Water Bucket
- 1x Heart of the Sea

**Weaknesses:**

- Take +25% damage from lightning
- Fire damage increased by 50%
- Desert/dry biomes drain hydration 3x faster
- Cannot use fire-based tools (furnaces take longer)

**Resource Bar:** Hydration (0-100, depletes on land, recharges in water)

---

### 5. STARBORNE

**Impact Level:** Medium  
**Theme:** Fallen from the sky, masters of air and light

**Description:**
You descended from the cosmos itself. Flight comes naturally, and darkness is your enemy. You draw power from the sun and stars but are fragile compared to other origins.

**Abilities:**

**[ACTIVE] Celestial Dash** (Cooldown: 10s, Cost: 2 Hunger)

- Dash forward 15 blocks at high speed
- Can be used mid-air (changes direction)
- Leaves trail of light particles
- Damages enemies you pass through (2 HP)

**[ACTIVE] Starlight Beacon** (Cooldown: 60s, Duration: 30s)

- Place beacon of light that:
  - Reveals invisible entities (15 block radius)
  - Damages undead mobs (1 HP per second in radius)
  - Grants Regeneration I to allies
  - Prevents mob spawning in area

**[PASSIVE] Wings of Light**

- Can fly/glide like Elytrian but without elytra
- Drains hunger: 1 per 5 seconds while flying
- Can hover in place (costs 2 hunger per second)
- Fall damage reduced by 75%

**[PASSIVE] Solar Powered**

- In direct sunlight: Regeneration I, Speed I
- At night: Speed and regeneration removed
- In darkness (caves): Mining Fatigue I, Weakness I
- Nearby light sources (torches) partially restore power (50% effectiveness)

**Starting Items:**

- 1x Glowstone (x16)
- 1x Lantern
- 1x Sun Crown (cosmetic helmet, provides light)

**Weaknesses:**

- 25% less health (15 HP instead of 20 HP)
- Cannot see in complete darkness (need light source)
- Darkness damage: 0.5 HP per second in light level 0
- Void damage increased by 100%

**Resource Bar:** Stellar Energy (0-100, charges in sunlight, depletes in darkness)

---

### 6. STONEHEART

**Impact Level:** Low  
**Theme:** Living stone, immovable and indestructible

**Description:**
Your body is made of living stone. You're incredibly durable and strong, but slow and heavy. Mining is your specialty, but water and heights are dangerous.

**Abilities:**

**[ACTIVE] Seismic Slam** (Cooldown: 30s, Cost: 6 Hunger)

- Slam ground creating shockwave
- Damages and launches entities in 10-block radius (4 HP + knockback)
- Breaks loose stone/ores in area
- Stuns enemies for 2 seconds

**[ACTIVE] Stone Skin** (Cooldown: 90s, Duration: 20s)

- Become completely immune to damage
- Cannot move during duration (rooted in place)
- Reflects 50% damage back to attackers
- Appears as stone texture overlay

**[PASSIVE] Living Mountain**

- 50% more health (30 HP)
- Natural armor (equivalent to iron armor without wearing any)
- Immune to fall damage (simply land and create small crater)
- Knockback resistance (cannot be pushed by explosions/attacks)

**[PASSIVE] Earth Affinity**

- Mine stone/ores 2x faster
- Fortune effect on stone-based blocks (even without fortune pickaxe)
- Can eat stone/ores to regain hunger (1 stone = 1 hunger)
- Can smell ores through walls (particles show direction to nearest ore)

**Starting Items:**

- 1x Iron Pickaxe (Efficiency III)
- 1x Stone Sword (Sharpness II)
- 16x Stone

**Weaknesses:**

- 50% slower movement speed
- Cannot sprint
- Cannot swim (sink like anvil)
- Water movement is 75% slower than land movement
- Cannot use boats (too heavy)
- Elytra flight impossible (too heavy)

**Resource Bar:** Stone Armor (0-100, reduces damage taken, recharges when standing still)

---

### 7. FROSTBORN

**Impact Level:** Medium  
**Theme:** Children of winter, masters of ice and cold

**Description:**
Born from eternal winter, you command ice and thrive in cold. Heat and fire are lethal to you. Your touch freezes, but flame melts you.

**Abilities:**

**[ACTIVE] Ice Spike** (Cooldown: 8s, Cost: 2 Hunger)

- Launch spike of ice (projectile)
- Damages enemies (3 HP)
- Freezes water where it lands
- Slows hit targets (Slowness II for 5 seconds)
- Creates temporary ice blocks

**[ACTIVE] Blizzard** (Cooldown: 120s, Duration: 30s, Cost: 10 Hunger)

- Create localized blizzard in 20-block radius
- Constant snow particles
- Slows all enemies in area
- Freezes water
- You gain Speed II and Regeneration I inside blizzard

**[PASSIVE] Permafrost**

- Immune to cold damage (powder snow, ice)
- Water you touch freezes into ice
- Can walk on water (freezes under your feet)
- Snow doesn't slow you down (moves at normal speed)
- Create snow layer wherever you walk (cosmetic)

**[PASSIVE] Cold Aura**

- Nearby enemies (5 blocks) have Slowness I
- Crops near you grow slower
- Fire in your vicinity has 50% chance to extinguish per second
- Campfires/torches flicker near you

**Starting Items:**

- 1x Ice Sword (custom weapon, freezes on hit)
- 16x Snowballs
- 1x Frost Walker II boots

**Weaknesses:**

- Fire damage increased by 100%
- Lava is instakill
- Standing near fire/lava deals 1 HP per second
- Desert/hot biomes deal 0.5 HP per second
- Cannot eat hot food (cooked meat, baked goods - only raw or frozen)

**Resource Bar:** Chill Factor (0-100, increases in cold, depletes in heat)

---

### 8. UMBRAKIN

**Impact Level:** High  
**Theme:** Shadow manipulators, creatures of darkness

**Description:**
Born from shadows, you control darkness itself. Night is your domain where you're strongest, but sunlight weakens and burns you like a vampire.

**Abilities:**

**[ACTIVE] Shadow Meld** (Cooldown: 20s, Duration: 10s, Cost: 4 Hunger)

- Become invisible in darkness (light level 7 or below)
- Leave no footsteps or sounds
- Move through shadows (phase through 1-block gaps)
- Attacking breaks invisibility

**[ACTIVE] Darkness Bolt** (Cooldown: 5s, Cost: 1 Hunger)

- Launch bolt of shadow energy
- Deals 4 HP damage
- Blinds target for 3 seconds
- Only works in darkness (light level below 10)

**[PASSIVE] Shadow Form**

- Invisible to mobs in darkness
- Night vision permanently active
- Speed II at night (light level below 7)
- Can see invisible entities

**[PASSIVE] Photophobia**

- Sunlight deals 1 HP per second (like zombies)
- Must wear helmet in sun or take damage
- Light level above 12 applies Weakness I
- Torches/lanterns near you cause minor discomfort (Nausea I)

**Starting Items:**

- 1x Shadow Cloak (leather armor dyed black, cosmetic)
- 1x Darkness Orb (custom item, creates darkness in 5-block radius for 30s)
- 64x Torch (ironic gift for survival)

**Weaknesses:**

- Sunlight damage (1 HP per second)
- Glowstone/sea lanterns deal proximity damage (0.5 HP per second within 3 blocks)
- Cannot place light sources above light level 7 (blocks refuse to be placed)
- Ars Nouveau light spells deal 2x damage

**Resource Bar:** Shadow Energy (0-100, charges in darkness, depletes in light)

---

### 9. FERALKIN

**Impact Level:** Low  
**Theme:** Beast shapeshifters with animal instincts

**Description:**
You're connected to primal nature, able to partially transform into a beast. Superior senses and mobility, but civilized tools feel unnatural.

**Abilities:**

**[ACTIVE] Primal Roar** (Cooldown: 45s, Cost: 5 Hunger)

- Release powerful roar
- Fears nearby enemies (10 block radius, flees for 8 seconds)
- Rallies nearby animals to fight for you temporarily
- Breaks concentration of spellcasters

**[ACTIVE] Beast Form** (Cooldown: 120s, Duration: 60s, Cost: 8 Hunger)

- Transform into partial beast form
- +50% movement speed
- +4 damage with melee attacks (claws/fangs)
- Can climb walls like spider
- Cannot use tools/weapons during transformation

**[PASSIVE] Predator Senses**

- See mob/player health bars
- Smell blood (wounded entities highlighted through walls)
- Hear heartbeats (detect hidden players within 20 blocks)
- Track players who damage you (see particle trail to their location)

**[PASSIVE] Natural Weapons**

- Unarmed attacks deal 3 HP damage (vs normal 1 HP)
- Can mine without tools (but slower than with tools)
- Natural armor (equivalent to leather armor)
- Raw meat provides better nutrition (2x hunger/saturation)

**Starting Items:**

- 16x Raw Beef
- 1x Wolf Pelt (cosmetic cape)
- 1x Fang Necklace (cosmetic)

**Weaknesses:**

- Cannot wear heavy armor (iron/diamond/netherite feels restrictive)
- Mining with tools is 25% slower (unnatural to you)
- Enchanted items feel wrong (15% chance to break enchantment on use)
- Villages/civilized areas give Nausea I (too many artificial smells)

**Resource Bar:** Feral Instinct (0-100, charges through combat, enables transformations)

---

### 10. VOIDTOUCHED

**Impact Level:** High  
**Theme:** Corrupted by the void, reality benders

**Description:**
The void has marked you. You can manipulate the fabric of reality itself, but you're unstable and dangerous—even to yourself.

**Abilities:**

**[ACTIVE] Void Tear** (Cooldown: 60s, Cost: 10 Hunger)

- Create rift in reality (5-block radius)
- Sucks in nearby entities (including blocks)
- Teleports them randomly within 50 blocks
- Deals 6 HP to entities passing through
- 10% chance to summon endermite

**[ACTIVE] Reality Shift** (Cooldown: 30s, Cost: 6 Hunger)

- Swap positions with target entity (player or mob)
- Can swap through walls
- Both entities teleport simultaneously
- Disorients target (Nausea for 5 seconds)

**[PASSIVE] Void Walker**

- Take 75% less void damage (not immune but highly resistant)
- Can see in the void (void isn't pitch black for you)
- Ender dragon ignores you
- Endermen are neutral toward you

**[PASSIVE] Reality Distortion**

- 5% chance to phase through solid blocks when walking
- Items you drop have 10% chance to vanish into void
- Random blocks near you occasionally flicker/become transparent
- Other players experience visual glitches when near you (particle effects)

**Starting Items:**

- 1x Void Shard (custom item, glitches textures nearby)
- 8x Ender Pearls
- 1x Void Touched Compass (points to End Portal)

**Weaknesses:**

- Randomly teleport 1-3 blocks every 5 minutes (uncontrollable)
- Cannot use beds (reality too unstable - explode like in Nether)
- Chorus fruit teleports you to void damage zone (dangerous)
- 1% chance per minute to take 2 HP void damage randomly

**Resource Bar:** Void Corruption (0-100, increases with void abilities, high corruption = negative effects)

---

### 11. SKYBORN

**Impact Level:** Medium  
**Theme:** Wind masters who never touch the ground

**Description:**
You were born above the clouds and feel most comfortable in the sky. Heights empower you, but the ground weakens your abilities.

**Abilities:**

**[ACTIVE] Wind Blast** (Cooldown: 15s, Cost: 3 Hunger)

- Launch gust of wind
- Pushes entities 15 blocks
- Can launch yourself for mobility
- Extinguishes fires
- Deflects projectiles

**[ACTIVE] Updraft** (Cooldown: 45s, Duration: 15s, Cost: 6 Hunger)

- Create column of rising air (20 blocks tall)
- Anyone in column levitates upward
- Can be used for base ascension
- Great team utility

**[PASSIVE] Cloud Strider**

- Permanent slow falling effect
- Double jump (once per jump, resets on landing)
- Can create temporary cloud blocks (like scaffolding but made of clouds)
- Run faster at high altitudes (+20% speed above Y=100)

**[PASSIVE] Altitude Affinity**

- Health regenerates faster at high altitude (above Y=150)
- Deal +25% damage when above target
- Take -25% damage when in air
- Can breathe at any altitude (no oxygen limit)

**Starting Items:**

- 1x Wind Charge (x16)
- 1x Feather (x32)
- 1x Sky Silk Cape (cosmetic, flows in wind)

**Weaknesses:**

- Take +50% damage when on ground (Y < 70)
- Slowness I underground (Y < 40)
- Mining Fatigue I in caves
- Claustrophobia in enclosed spaces (Nausea)

**Resource Bar:** Altitude Bonus (0-100, increases with height, grants passive bonuses)

---

### 12. MYCOMORPH

**Impact Level:** Medium  
**Theme:** Fungal being that spreads spores and growth

**Description:**
Part mushroom, part humanoid. You spread fungal growth wherever you go and can communicate with mushroom colonies. Nature is your ally, but fire is death.

**Abilities:**

**[ACTIVE] Spore Cloud** (Cooldown: 30s, Duration: 20s, Cost: 4 Hunger)

- Release cloud of spores (10 block radius)
- Poison enemies (Poison II)
- Heal allies (Regeneration I)
- Spreads mycelium in area

**[ACTIVE] Fungal Network** (Cooldown: 90s, Cost: 8 Hunger)

- Connect to mushroom networks
- Telepathically communicate with players near mushrooms
- See through mushrooms (toggle view to any mushroom you've tagged)
- Can tag up to 5 mushrooms as network nodes

**[PASSIVE] Decomposer**

- Slowly convert nearby grass/dirt to mycelium (1 block per 30 seconds)
- Can eat rotten flesh/poisonous potatoes with no negative effects
- Mushrooms grow near you at 3x speed
- Compost/organic items restore more hunger

**[PASSIVE] Photosynthesis**

- Slowly regenerate health in sunlight (0.5 HP per 5 seconds)
- Don't need to eat if standing in sunlight (hunger frozen)
- Mushroom blocks restore hunger when eaten
- Can plant mushrooms anywhere (ignore light requirements)

**Starting Items:**

- 1x Mushroom Stew (x8)
- 1x Mycelium (x16)
- 1x Mushroom Cap (cosmetic helmet)

**Weaknesses:**

- Fire damage increased by 100%
- Lava is instakill
- Cannot use fire-based tools (furnaces require external automation)
- Desert biomes drain hunger 2x faster (no moisture)
- Take 1 HP damage per second near fire sources

**Resource Bar:** Spore Count (0-100, generates over time, used for abilities)

---

### 13. CRYSTALLINE

**Impact Level:** Low  
**Theme:** Living crystal with resonant powers

**Description:**
Your body is made of living crystal. You can store energy and resonate with ores, but you're brittle and slower than flesh-and-blood beings.

**Abilities:**

**[ACTIVE] Crystal Spike** (Cooldown: 12s, Cost: 3 Hunger)

- Create spike of crystal from ground (projectile or melee)
- Damages enemies (4 HP)
- Spike remains as barrier (blocks movement for 10 seconds)
- Can create bridges/walls strategically

**[ACTIVE] Ore Resonance** (Cooldown: 60s, Cost: 5 Hunger)

- Sense all ores within 32 blocks
- Ores glow through walls for 30 seconds
- Higher tier ores glow brighter
- Can identify ore type by color

**[PASSIVE] Crystal Body**

- Naturally refracts light (slight invisibility in bright light - 30% transparent)
- Reflects projectiles occasionally (15% chance)
- Stores sunlight as energy (charges resource bar)
- Beautiful particle effects constantly

**[PASSIVE] Energy Storage**

- Can consume XP orbs to store as energy
- Energy can be released as burst damage on next attack
- Store up to 30 XP levels worth
- Makes beautiful humming sound when fully charged

**Starting Items:**

- 1x Crystal Shard (x16, custom item)
- 1x Amethyst Shard (x32)
- 1x Spyglass (you appreciate precision)

**Weaknesses:**

- Take +50% damage from pickaxes/axes (tools that break stone)
- Explosions deal +75% damage (fragile)
- 25% slower movement (heavy crystal body)
- Cannot wear armor (doesn't fit crystal form)

**Resource Bar:** Crystal Charge (0-100, stores energy from sunlight/XP)

---

### 14. TECHNOMANCER

**Impact Level:** Medium  
**Theme:** Cybernetic being, part flesh part machine

**Description:**
Enhanced with ancient technology, you interface with redstone naturally. Machines are extensions of your will, but you're vulnerable to electromagnetic effects.

**Abilities:**

**[ACTIVE] Redstone Pulse** (Cooldown: 20s, Cost: 4 Hunger)

- Send pulse through redstone within 20 blocks
- Activates all redstone mechanisms simultaneously
- Can hack locked doors/contraptions (50% chance)
- Temporarily disables enemy redstone traps

**[ACTIVE] Overclock** (Cooldown: 120s, Duration: 30s, Cost: 10 Hunger)

- Enter overclocked state
- Speed III, Haste III
- Attacks deal +50% damage
- Overheat at end (take 6 HP damage, cooldown on all abilities)

**[PASSIVE] Machine Affinity**

- Can see through Create contraptions (x-ray for machines only)
- Redstone components are highlighted in world
- Can hear machine sounds from further away
- Naturally understand machine schematics

**[PASSIVE] Cyborg Resilience**

- 20% damage reduction from physical sources
- Immune to poison (biological systems mostly gone)
- Can eat coal/redstone for food (weird but it works)
- Natural night vision (cybernetic eyes)

**Starting Items:**

- 1x Redstone (x64)
- 1x Engineer's Goggles (cosmetic, HUD effect)
- 1x Circuit Board (custom crafting component)

**Weaknesses:**

- Water deals 0.5 HP per second (short circuits)
- Lightning deals +200% damage
- EMPs from mods deal massive damage
- Must "recharge" - stand still for 10 seconds every hour or take damage

**Resource Bar:** Power Cells (0-100, depletes with abilities, recharges while standing still)

---

### 15. ETHEREAL

**Impact Level:** High  
**Theme:** Ghostly being phased between dimensions

**Description:**
You exist partially out of phase with reality. You can pass through objects but struggle to interact with the physical world meaningfully.

**Abilities:**

**[ACTIVE] Phase Shift** (Cooldown: 10s, Duration: 8s, Cost: 5 Hunger)

- Become completely intangible
- Pass through walls/entities
- Cannot attack or be attacked during
- Cannot interact with blocks
- Leave ghostly trail

**[ACTIVE] Possession** (Cooldown: 180s, Duration: 60s, Cost: 15 Hunger)

- Possess a mob (take control of it)
- See through mob's eyes
- Control mob's movements
- Cannot control boss mobs or players
- Mob returns to normal after duration

**[PASSIVE] Incorporeal**

- Take 50% less physical damage
- Cannot wear armor (phases through you)
- Can see invisible entities
- Don't trigger pressure plates or tripwires

**[PASSIVE] Ghostly**

- Permanent transparency (50% see-through)
- No footstep sounds
- Mobs have harder time detecting you (25% detection range)
- Can float/hover for short periods (3 seconds at a time)

**Starting Items:**

- 1x Soul Lantern
- 1x Ghostly Veil (cosmetic cape, semi-transparent)
- 8x Ectoplasm (custom item, crafting material)

**Weaknesses:**

- Cannot hold heavy items (iron blocks, anvils cause item to drop)
- Breaking blocks takes 50% longer (hard to interact with physical world)
- Cannot push buttons or pull levers (must use other methods)
- Food provides 50% less hunger (hard to digest physical matter)

**Resource Bar:** Phase Energy (0-100, depletes when intangible, recharges when solid)

---

### 16. DRYAD

**Impact Level:** Medium  
**Theme:** Nature spirit, forest guardian, plant controller

**Description:**
You are a spirit of the forest, born from the ancient trees themselves. Plants respond to your command, and nature heals you. However, fire is your greatest enemy, and you wither in dry, lifeless places.

**Abilities:**

**[ACTIVE] Entangling Roots** (Cooldown: 20s, Cost: 5 Nature Energy)

- Summon roots from the ground in an 8-block radius
- Roots trap and damage enemies (4 HP)
- Applies Slowness V (rooted), Poison I, and prevents jumping
- Creates temporary vine blocks around trapped enemies
- Beautiful green particle effects with leaf bursts

**[ACTIVE] Nature's Blessing** (Cooldown: 45s, Cost: 8 Nature Energy)

- Heal yourself for 6 HP and gain Regeneration II
- Heal nearby allies for 3 HP with Regeneration I
- Grow all crops in 10-block radius by 2-3 stages
- Apply bonemeal effect to growable blocks
- Convert dirt to grass, spawn flowers on grass
- Expanding ring of nature particles and heart effects

**[PASSIVE] Sunlight Photosynthesis**

- Regenerate 0.5 HP per second in direct sunlight
- Hunger doesn't deplete while in direct sunlight
- Gain Speed I when near 3+ flowers
- Gain Strength I when near 5+ flowers
- Ambient leaf particles follow you

**[PASSIVE] Forest Bond**

- Move faster in forest biomes (Speed I, Jump I)
- Can walk through leaves with slow falling effect
- Animals are calm around you and won't flee
- Nature particles appear when near trees

**Starting Items:**

- 16x Bone Meal
- 8x Oak Saplings
- 1x Flower Crown (cosmetic)

**Weaknesses:**

- Fire damage increased by 50%
- Take damage in desert/badlands biomes (0.5 HP every 2 seconds)
- Nether deals constant damage (1 HP per second)
- Complete darkness causes Wither and Weakness effects

**Resource Bar:** Nature Energy (0-100, recharges in sunlight and near plants)

---

### 17. NECROMANCER

**Impact Level:** High  
**Theme:** Master of death, undead summoner, life drainer

**Description:**
You have transcended the boundary between life and death, gaining power over the deceased. You can raise the dead to fight for you and drain life from the living. However, sunlight and holy places weaken your dark powers.

**Abilities:**

**[ACTIVE] Raise Dead** (Cooldown: 60s, Cost: 10 Death Essence)

- Summon 4 undead minions (2 zombies, 2 skeletons)
- Minions are named and marked as yours
- Zombies carry iron swords, skeletons have bows
- Minions follow you and attack your enemies
- Minions are removed when you change origins
- Dramatic soul/smoke particle effects on summoning

**[ACTIVE] Life Siphon** (Cooldown: 15s, Cost: 6 Death Essence)

- Target an enemy in a 12-block cone
- Deal 4 HP magic damage
- Heal yourself for 50% of damage dealt
- Apply Wither I and Weakness I to target
- Soul drain beam particles connect target to you

**[PASSIVE] Undead Mastery**

- Undead mobs will not attack you
- Your summoned minions gain buffs and follow commands
- Minions attack your last attacked target
- Soul particles emanate from active minions
- More minions = more power particles around you

**[PASSIVE] Death Aura**

- At night: Speed I, Strength I, Night Vision
- 5-block aura damages nearby living hostile mobs
- Aura deals 0.5-1.0 HP per second based on light level
- Constant dark soul particles around you
- Stronger effects while in darkness

**Starting Items:**

- 8x Bone
- 1x Skeleton Skull
- 1x Wither Rose
- 1x Dark Cloak (cosmetic)

**Weaknesses:**

- Weakness I in direct bright sunlight
- After 30+ seconds in sunlight, take 0.5-1.0 HP magic damage
- Healing potions/items are 50% less effective
- Weakness near beacons and holy places
- 90% base health (18 HP instead of 20)

**Resource Bar:** Death Essence (0-100, recharges at night and near death)

---

## 🎮 Origin Selection & Progression

### Selection System

**First Join:**

- New players spawn in **Origin Chamber** (custom dimension)
- Interactive selection room with portals representing each origin
- Walk through portal to experience 30-second trial of origin
- Hover above each portal shows detailed stats/abilities
- Choose origin by interacting with central altar

**Changing Origins:**
After initial selection, origins can be changed through:

1. **Origin Transformation Altar** (requires 250 deaths in Beloved Souls)

   - Costs rare materials
   - Resets origin progression
   - 7-day cooldown after transformation

2. **Rare Drop: Veil Shard**

   - 0.1% chance from boss mobs
   - Single use item
   - Instantly changes origin (no cooldown)

3. **Admin Commands** (for debugging/special events)
   ```
   /veilorigins set <player> <origin>
   /veilorigins reset <player>
   ```

### Progression System

**Origin Levels (1-50):**

- Gain XP for using origin abilities
- Gain XP for actions matching origin theme (e.g., Cindersoul gains XP in Nether)
- Each level unlocks enhanced abilities or reduces cooldowns
- Level 10, 25, 50 unlock major power upgrades

**Example Progression (Veilborn):**

- **Level 1:** Base abilities
- **Level 5:** Veil Step cooldown reduced to 25s
- **Level 10:** Soul Harvest radius increased to 15 blocks
- **Level 15:** Can see soul essence through walls at 20 blocks
- **Level 20:** Spectral Form grants Speed I at night
- **Level 25:** Veil Step can teleport 30 blocks
- **Level 30:** Soul Harvest gives temporary damage boost
- **Level 35:** New ability unlocked: Soul Shield
- **Level 40:** All cooldowns reduced by 20%
- **Level 50:** **Master Ability:** Veil Mastery - Can bring allies through Veil Step

**Prestige System:**

- After reaching Level 50, can prestige
- Resets to Level 1 but keeps cosmetic aura/title
- Each prestige grants +5% to all origin stats
- Max 10 prestiges per origin
- Cosmetic changes: aura color changes with prestige level

---

## 🔧 Technical Systems

### Origin Data Storage

**Player NBT Structure:**

```json
{
  "veilOrigin": "veilborn",
  "originLevel": 25,
  "originXP": 15420,
  "prestigeLevel": 0,
  "abilityUpgrades": {
    "veil_step": 2,
    "soul_harvest": 3
  },
  "resourceBar": 75,
  "cooldowns": {
    "veil_step": 0,
    "soul_harvest": 45
  }
}
```

### Resource Bar System

Each origin has custom resource bar displayed above hotbar:

- Visual bar showing current resource (0-100)
- Regeneration rate varies by origin
- Can be modified by other mods via API
- Color-coded by origin theme
- Pulses when full, dims when empty

### Ability Cooldown Display

**HUD Elements:**

- Ability icons on screen (keybind hints)
- Cooldown overlay (darkens icon when on CD)
- Resource cost indicator
- Visual/audio feedback when ability ready
- Configurable position in client config

---

## 🎨 Visual Design

### Particles & Effects

**Origin-Specific Particles:**

- Veilborn: Purple/black wispy particles
- Cindersoul: Orange flame particles
- Riftwalker: Purple ender particles
- Tidecaller: Blue water droplets
- Starborne: White/yellow star particles
- Stoneheart: Gray stone fragments
- Frostborn: White/blue snowflakes
- Umbrakin: Black shadow wisps
- Feralkin: Brown/green nature particles
- Voidtouched: Black/purple void corruption
- Skyborn: White cloud puffs
- Mycomorph: Green spore particles
- Crystalline: Multi-color light refractions
- Technomancer: Red/blue circuit particles
- Ethereal: Translucent ghost wisps

**Aura System:**

- Low-level players: Subtle particle trail
- Mid-level players: Visible aura around body
- High-level players: Intense particle effects
- Prestige players: Unique animated auras with multiple layers

### Model & Skin Modifications

**Optional Visual Changes:**

- Cindersoul: Glowing eyes, flame textures on skin
- Crystalline: Translucent overlay with crystal patterns
- Ethereal: Semi-transparent player model
- Stoneheart: Stone texture overlay
- Umbrakin: Shadow distortion effect
- Technomancer: Circuit pattern overlays, glowing cybernetic parts

**Configurable:**

- Players can toggle visual effects on/off
- Choose intensity of particle effects
- Select aura colors (unlocked through progression)

---

## 🌐 Multiplayer Considerations

### Team Synergies

Origins designed to complement each other:

**Example Combos:**

- **Tidecaller + Cindersoul:** Water vs Fire creates steam damage
- **Riftwalker + Voidtouched:** Combined teleportation network
- **Stoneheart + Mycomorph:** Tank + Healer duo
- **Starborne + Umbrakin:** Day/Night rotation coverage
- **Technomancer + any origin:** Machine support for everyone

### PvP Balance

**Impact Levels Explained:**

- **Low:** Straightforward abilities, good for PvP
- **Medium:** Balanced offensive/defensive
- **High:** Powerful but with significant weaknesses

**Combat Balancing:**

- Origins with high mobility have lower damage
- Tanky origins have mobility restrictions
- Glass cannon origins (Starborne) have low HP
- Utility origins support rather than dominate fights

### Cooperative Gameplay

**Shared Abilities:**

- Some abilities can affect teammates
- Aqua Bubble (Tidecaller) helps non-aquatic friends
- Starlight Beacon (Starborne) heals allies
- Stone Skin (Stoneheart) can shield nearby players at Level 40+

**Base Building Synergies:**

- Stoneheart mines fast → Technomancer automates → Cindersoul smelts
- Skyborn builds tall → Riftwalker teleports → Voidtouched creates shortcuts
- Mycomorph farms → Tidecaller irrigates → Feralkin harvests

---

## 📊 Configuration Files

### Server Config

```json
{
  "allowOriginChange": true,
  "originChangeMethod": "altar_only",
  "startingLevel": 1,
  "maxLevel": 50,
  "xpGainMultiplier": 1.0,
  "cooldownMultiplier": 1.0,
  "damageMultiplier": 1.0,
  "allowPrestige": true,
  "maxPrestige": 10,
  "originTrialDuration": 30,
  "showResourceBar": true,
  "enableParticles": true,
  "enableSounds": true,
  "pvpBalanceMode": "balanced",
  "dimensionalAbilities": true
}
```

### Client Config

```json
{
  "resourceBarPosition": "above_hotbar",
  "abilityIconPosition": "bottom_right",
  "particleIntensity": 1.0,
  "soundVolume": 1.0,
  "showOriginAura": true,
  "showAbilityCooldowns": true,
  "enableVisualEffects": true,
  "hudScale": 1.0,
  "keybinds": {
    "ability1": "R",
    "ability2": "V",
    "resourceInfo": "O"
  }
}
```

### Balance Config (Per Origin)

```json
{
  "veilborn": {
    "enabled": true,
    "healthModifier": 1.0,
    "speedModifier": 1.0,
    "damageModifier": 1.0,
    "ability1Cooldown": 30,
    "ability2Cooldown": 60,
    "resourceRegenRate": 1.0,
    "weaknessMultipliers": {
      "sunlight": 1.0,
      "holy": 1.5
    }
  }
}
```

---

## 🔌 API Documentation

### For Addon Developers

**Register Custom Origin:**

```java
@Mod.EventBusSubscriber(modid = "yourmod", bus = Mod.EventBusSubscriber.Bus.MOD)
public class YourOriginAddon {

    @SubscribeEvent
    public static void registerOrigins(VeilOriginsRegistryEvent event) {
        event.register(
            new OriginBuilder("yourmod:custom_origin")
                .setDisplayName("Custom Origin")
                .setDescription("Your custom origin description")
                .setImpactLevel(ImpactLevel.MEDIUM)
                .setHealthModifier(1.2f)
                .setSpeedModifier(0.9f)
                .addAbility(new CustomAbility())
                .addPassive(new CustomPassive())
                .setResourceType(ResourceType.MANA)
                .addStartingItem(new ItemStack(Items.DIAMOND))
                .addWeakness(DamageTypes.FIRE, 1.5f)
                .addResistance(DamageTypes.COLD, 0.5f)
                .build()
        );
    }
}
```

**Create Custom Ability:**

```java
public class CustomAbility extends OriginAbility {

    public CustomAbility() {
        super("custom_ability", 60); // name, cooldown in seconds
    }

    @Override
    public void onActivate(Player player, Level level) {
        // Your ability logic here
        player.addEffect(new MobEffectInstance(MobEffects.JUMP, 200, 2));
        spawnParticles(player);
        playSound(player);
    }

    @Override
    public boolean canUse(Player player) {
        // Check if ability can be used
        return !player.isInWater() && getPlayerResource(player) >= 10;
    }

    @Override
    public int getResourceCost() {
        return 10; // cost in resource points
    }
}
```

**Create Custom Passive:**

```java
public class CustomPassive extends OriginPassive {

    @Override
    public void onTick(Player player) {
        // Runs every tick while player has this origin
        if (player.isInWater()) {
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 20, 0));
        }
    }

    @Override
    public void onEquip(Player player) {
        // Runs when player selects this origin
        player.sendSystemMessage(Component.literal("You feel the power!"));
    }

    @Override
    public void onRemove(Player player) {
        // Runs when player changes from this origin
        player.clearFire();
    }
}
```

**Hook Into Origin Events:**

```java
@SubscribeEvent
public void onOriginChange(OriginChangeEvent event) {
    Player player = event.getPlayer();
    Origin oldOrigin = event.getOldOrigin();
    Origin newOrigin = event.getNewOrigin();

    // Your logic here
    if (newOrigin.getId().equals("veilborn")) {
        // Give custom item when becoming Veilborn
        player.addItem(new ItemStack(YourItems.CUSTOM_ITEM.get()));
    }
}

@SubscribeEvent
public void onAbilityUse(OriginAbilityEvent event) {
    Player player = event.getPlayer();
    OriginAbility ability = event.getAbility();

    // Modify ability behavior
    if (ability.getId().equals("veil_step")) {
        // Add custom effect to Veil Step
        event.addPostEffect(() -> {
            spawnCustomParticles(player);
        });
    }
}

@SubscribeEvent
public void onLevelUp(OriginLevelUpEvent event) {
    Player player = event.getPlayer();
    int newLevel = event.getNewLevel();

    // Custom rewards for leveling
    if (newLevel == 10) {
        player.addItem(new ItemStack(Items.DIAMOND));
    }
}
```

**Modify Existing Origin:**

```java
public class OriginModifier {

    public static void modifyVeilborn() {
        Origin veilborn = VeilOriginsAPI.getOrigin("veilorigins:veilborn");

        // Add new ability to existing origin
        veilborn.addAbility(new CustomAbility());

        // Modify existing ability
        OriginAbility veilStep = veilborn.getAbility("veil_step");
        veilStep.setCooldown(20); // reduce cooldown

        // Add new passive
        veilborn.addPassive(new CustomPassive());

        // Modify stats
        veilborn.setHealthModifier(1.1f);
    }
}
```

**Create Custom Resource Type:**

```java
public class CustomResourceType extends ResourceType {

    public CustomResourceType() {
        super("custom_energy", 100, 1.0f); // name, max, regen rate
    }

    @Override
    public void onRegenTick(Player player, float amount) {
        // Custom regeneration logic
        if (player.isCrouching()) {
            amount *= 2.0f; // regen faster while crouching
        }
        super.onRegenTick(player, amount);
    }

    @Override
    public void render(GuiGraphics graphics, int x, int y, float current, float max) {
        // Custom resource bar rendering
        // Your rendering code here
    }
}
```

**Integration with Other Mods:**

```java
// Example: Give bonus to Create machines based on origin
@SubscribeEvent
public void onCreateMachineSpeed(CreateMachineEvent event) {
    Player nearestPlayer = findNearestPlayer(event.getMachine());
    if (nearestPlayer != null) {
        Origin origin = VeilOriginsAPI.getPlayerOrigin(nearestPlayer);

        if (origin.getId().equals("technomancer")) {
            event.setSpeedMultiplier(1.5f); // Technomancers boost machines
        } else if (origin.getId().equals("blazeborn")) {
            if (event.getMachine().isFurnace()) {
                event.setSpeedMultiplier(2.0f); // Blazeborn boost furnaces
            }
        }
    }
}

// Example: Vampirism integration
@SubscribeEvent
public void onVampireTransformation(VampirismTransformEvent event) {
    Player player = event.getPlayer();
    Origin origin = VeilOriginsAPI.getPlayerOrigin(player);

    if (origin.getId().equals("veilborn")) {
        // Veilborn vampires are stronger
        event.setVampireLevel(event.getVampireLevel() + 2);
    } else if (origin.getId().equals("starborne")) {
        // Starborne can't become vampires (sun affinity)
        event.setCanceled(true);
        player.sendSystemMessage(Component.literal("Your celestial nature rejects the vampire curse!"));
    }
}
```

---

## 🎯 Planned Features (Future Updates)

### Version 1.1

- **Origin Hybrids:** Combine two origins at max level (limited powers from each)
- **Faction System:** Origins group into factions with shared buffs
- **Origin Quests:** Custom quest lines for each origin
- **Cosmetic Shop:** Spend origin XP on cosmetic effects

### Version 1.2

- **Origin Talents:** Skill tree for each origin (choose specialization)
- **Legendary Abilities:** Ultimate ability unlocked at Level 50
- **Origin Artifacts:** Unique items that enhance specific origins
- **Cross-Origin Combos:** Special effects when certain origins work together

### Version 1.3

- **Dynamic Origins:** Origins that change based on player actions
- **Origin Challenges:** Weekly challenges for each origin type
- **Leaderboards:** Track top players per origin
- **Origin Wars:** Server-wide events pitting origins against each other

### Version 2.0

- **Custom Origin Creator:** In-game GUI to build custom origins
- **Origin Trading:** Trade origin abilities/powers between players
- **Origin Fusion:** Temporarily combine origins for special events
- **Dimensional Origins:** New origins from other dimensions

---

## 📝 Community Contribution

### How to Contribute

**Creating Add-on Origins:**

1. Use the API to create your origin
2. Test thoroughly
3. Submit to Veil Origins GitHub
4. Community vote on inclusion in main mod

**Suggesting New Origins:**

- Submit origin concepts on Discord/GitHub
- Include: Name, Theme, 2 Active Abilities, 2 Passives, Weaknesses
- Community votes on best concepts
- Top voted origins get officially added

**Balancing Feedback:**

- Report overpowered/underpowered origins
- Suggest cooldown/damage adjustments
- Provide PvP/PvE testing data

---

## 🏆 Achievement System

**Origin-Specific Achievements:**

- "First Steps": Select your origin
- "Dedicated": Reach Level 10 with any origin
- "Master": Reach Level 50 with any origin
- "Prestiged": Prestige any origin
- "Collector": Unlock all 15 origins on one character
- "Jack of All Trades": Reach Level 25 with 5 different origins
- "Specialized": Reach Level 50 and Prestige 5 with one origin
- "Hybrid Experiment": Change origins 10 times
- "Origin Master": Reach Level 50 with all 15 origins
- "Legendary": Reach Prestige 10 with any origin

**Ability-Specific Achievements:**

- "Veil Walker": Use Veil Step 1000 times
- "Pyromancer": Deal 10,000 fire damage as Cindersoul
- "Reality Bender": Teleport 100,000 blocks as Riftwalker
- "Ocean Master": Spend 10 hours underwater as Tidecaller
- "Star Child": Fly 50,000 blocks as Starborne
- "Immovable": Tank 50,000 damage as Stoneheart
- "Winter's Wrath": Freeze 1,000 entities as Frostborn
- "Shadow Assassin": Kill 500 mobs while invisible as Umbrakin
- "Alpha Predator": Defeat 100 players as Feralkin
- "Void Lord": Survive 1 hour of void exposure as Voidtouched

---

## 📚 Lore & Worldbuilding

### The Veil

Long ago, the boundaries between worlds were solid. Life and death, fire and ice, shadow and light—all separate. Then came the Shattering, an event that cracked reality itself. The Veil—the membrane between dimensions—tore open.

Through these tears, new beings emerged. Some were born from the Veil itself (Veilborn, Riftwalkers). Others were transformed by exposure to otherworldly energies (Voidtouched, Umbrakin). Still others evolved naturally in the new, chaotic world (Feralkin, Mycomorph).

These beings are called the **Veil-Touched**—mortals transformed by the energies of the between-space. Each origin represents a different aspect of the Veil's influence.

### Origin Lore Snippets

**Veilborn:** "We who walk between life and death understand that existence is not binary. We are the bridge, the threshold, the doorway. Death does not scare us—we have already passed through and returned."

**Cindersoul:** "Fire is not destruction—it is transformation. We are the flame that forges, the heat that purifies. Our bodies burn with purpose."

**Riftwalker:** "Space is an illusion, distance is a suggestion. We learned to step through the cracks in reality. Sometimes, we forget which side is real."

**Tidecaller:** "The ocean is not water—it is potential, it is life, it is the source. We hear its song in our blood. On land, we are merely echoes of ourselves."

**Starborne:** "We fell from the sky like dying stars. The earth is foreign to us, gravity a constant reminder that we don't belong here. But the sky remembers us, and we will return."

---

## 🔊 Sound Design

**Ability Sounds:**

- Each origin has unique sound effects for abilities
- Layered audio: cast sound + impact sound + ambient sound
- Dynamic volume based on ability power level
- Positional audio (other players hear abilities from distance)

**Ambient Sounds:**

- Origins have constant low-volume ambient sound (breathing, crackling, humming)
- Intensifies during combat or ability use
- Can be toggled in client settings

**Music Themes:**

- Each origin has a unique music motif (plays occasionally)
- Music intensifies during major events (leveling up, unlocking abilities)
- Custom boss music when fighting as certain origins

---

This comprehensive specification provides everything needed to implement Veil Origins as a standalone mod with full API support for community extensions!
