package com.veilorigins.config;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = "veil_origins", bus = EventBusSubscriber.Bus.MOD)
public class VeilOriginsConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // ==================== GENERAL SETTINGS ====================
    public static final ModConfigSpec.BooleanValue ENABLE_SIZE_SCALING;
    public static final ModConfigSpec.BooleanValue ENABLE_WEAKNESSES;
    public static final ModConfigSpec.BooleanValue ENABLE_RESISTANCES;
    public static final ModConfigSpec.BooleanValue SHOW_ORIGIN_PARTICLES;
    public static final ModConfigSpec.BooleanValue ENABLE_ORIGIN_SOUNDS;

    // ==================== ORIGIN SIZE SCALING ====================
    public static final ModConfigSpec.DoubleValue VEILBORN_SIZE;
    public static final ModConfigSpec.DoubleValue STONEHEART_SIZE;
    public static final ModConfigSpec.DoubleValue FERALKIN_SIZE;
    public static final ModConfigSpec.DoubleValue RIFTWALKER_SIZE;
    public static final ModConfigSpec.DoubleValue UMBRAKIN_SIZE;
    public static final ModConfigSpec.DoubleValue VOIDTOUCHED_SIZE;
    public static final ModConfigSpec.DoubleValue FROSTBORN_SIZE;
    public static final ModConfigSpec.DoubleValue CINDERSOUL_SIZE;
    public static final ModConfigSpec.DoubleValue TIDECALLER_SIZE;
    public static final ModConfigSpec.DoubleValue STARBORNE_SIZE;
    public static final ModConfigSpec.DoubleValue SKYBORN_SIZE;
    public static final ModConfigSpec.DoubleValue MYCOMORPH_SIZE;
    public static final ModConfigSpec.DoubleValue CRYSTALLINE_SIZE;
    public static final ModConfigSpec.DoubleValue TECHNOMANCER_SIZE;
    public static final ModConfigSpec.DoubleValue ETHEREAL_SIZE;

    // ==================== DAMAGE MODIFIERS ====================
    // Weakness multipliers (>1.0 = more damage taken)
    public static final ModConfigSpec.DoubleValue TIDECALLER_FIRE_WEAKNESS;
    public static final ModConfigSpec.DoubleValue TIDECALLER_LIGHTNING_WEAKNESS;
    public static final ModConfigSpec.DoubleValue STARBORNE_VOID_WEAKNESS;
    public static final ModConfigSpec.DoubleValue SKYBORN_GROUND_WEAKNESS;
    public static final ModConfigSpec.DoubleValue MYCOMORPH_FIRE_WEAKNESS;
    public static final ModConfigSpec.DoubleValue CRYSTALLINE_EXPLOSION_WEAKNESS;
    public static final ModConfigSpec.DoubleValue TECHNOMANCER_LIGHTNING_WEAKNESS;
    public static final ModConfigSpec.DoubleValue FROSTBORN_FIRE_WEAKNESS;
    public static final ModConfigSpec.DoubleValue VOIDTOUCHED_VOID_WEAKNESS;
    public static final ModConfigSpec.DoubleValue RIFTWALKER_VOID_WEAKNESS;

    // Resistance multipliers (<1.0 = less damage taken)
    public static final ModConfigSpec.DoubleValue SKYBORN_AIRBORNE_RESISTANCE;
    public static final ModConfigSpec.DoubleValue ETHEREAL_PHYSICAL_RESISTANCE;
    public static final ModConfigSpec.DoubleValue STONEHEART_REFLECT_PERCENTAGE;
    public static final ModConfigSpec.DoubleValue VOIDTOUCHED_VOID_RESISTANCE;

    // ==================== ABILITY COOLDOWNS (in ticks, 20 = 1 second)
    // ====================
    public static final ModConfigSpec.IntValue SEISMIC_SLAM_COOLDOWN;
    public static final ModConfigSpec.IntValue STONE_SKIN_COOLDOWN;
    public static final ModConfigSpec.IntValue FLAME_BURST_COOLDOWN;
    public static final ModConfigSpec.IntValue LAVA_WALK_COOLDOWN;
    public static final ModConfigSpec.IntValue TIDAL_WAVE_COOLDOWN;
    public static final ModConfigSpec.IntValue AQUA_BUBBLE_COOLDOWN;
    public static final ModConfigSpec.IntValue ICE_SPIKE_COOLDOWN;
    public static final ModConfigSpec.IntValue BLIZZARD_COOLDOWN;
    public static final ModConfigSpec.IntValue WIND_BLAST_COOLDOWN;
    public static final ModConfigSpec.IntValue UPDRAFT_COOLDOWN;
    public static final ModConfigSpec.IntValue CELESTIAL_DASH_COOLDOWN;
    public static final ModConfigSpec.IntValue STARLIGHT_BEACON_COOLDOWN;
    public static final ModConfigSpec.IntValue PHASE_SHIFT_COOLDOWN;
    public static final ModConfigSpec.IntValue POSSESSION_COOLDOWN;
    public static final ModConfigSpec.IntValue DIMENSIONAL_HOP_COOLDOWN;
    public static final ModConfigSpec.IntValue POCKET_DIMENSION_COOLDOWN;
    public static final ModConfigSpec.IntValue VOID_TEAR_COOLDOWN;
    public static final ModConfigSpec.IntValue REALITY_SHIFT_COOLDOWN;

    // ==================== ABILITY DAMAGE VALUES ====================
    public static final ModConfigSpec.DoubleValue SEISMIC_SLAM_DAMAGE;
    public static final ModConfigSpec.DoubleValue FLAME_BURST_DAMAGE;
    public static final ModConfigSpec.DoubleValue ICE_SPIKE_DAMAGE;
    public static final ModConfigSpec.DoubleValue VOID_TEAR_DAMAGE;
    public static final ModConfigSpec.DoubleValue DARKNESS_BOLT_DAMAGE;
    public static final ModConfigSpec.DoubleValue CRYSTAL_SPIKE_DAMAGE;
    public static final ModConfigSpec.DoubleValue REDSTONE_PULSE_DAMAGE;

    // ==================== ABILITY RANGES/RADIUS ====================
    public static final ModConfigSpec.IntValue SEISMIC_SLAM_RADIUS;
    public static final ModConfigSpec.IntValue FLAME_BURST_RADIUS;
    public static final ModConfigSpec.IntValue TIDAL_WAVE_RADIUS;
    public static final ModConfigSpec.IntValue BLIZZARD_RADIUS;
    public static final ModConfigSpec.IntValue PRIMAL_ROAR_RADIUS;
    public static final ModConfigSpec.IntValue VOID_TEAR_RANGE;
    public static final ModConfigSpec.IntValue DIMENSIONAL_HOP_RANGE;

    // ==================== ABILITY DURATIONS (in ticks) ====================
    public static final ModConfigSpec.IntValue STONE_SKIN_DURATION;
    public static final ModConfigSpec.IntValue BLIZZARD_DURATION;
    public static final ModConfigSpec.IntValue PHASE_SHIFT_DURATION;
    public static final ModConfigSpec.IntValue BEAST_FORM_DURATION;
    public static final ModConfigSpec.IntValue OVERCLOCK_DURATION;

    // ==================== RESOURCE GENERATION ====================
    public static final ModConfigSpec.DoubleValue CINDERSOUL_HEAT_REGEN;
    public static final ModConfigSpec.DoubleValue CINDERSOUL_COLD_DRAIN;
    public static final ModConfigSpec.DoubleValue TIDECALLER_WATER_REGEN;
    public static final ModConfigSpec.DoubleValue TIDECALLER_LAND_DRAIN;
    public static final ModConfigSpec.DoubleValue TIDECALLER_DESERT_MULTIPLIER;
    public static final ModConfigSpec.DoubleValue STARBORNE_DAY_REGEN;
    public static final ModConfigSpec.DoubleValue STARBORNE_DARK_DRAIN;
    public static final ModConfigSpec.DoubleValue CRYSTALLINE_SUNLIGHT_REGEN;
    public static final ModConfigSpec.DoubleValue TECHNOMANCER_IDLE_REGEN;
    public static final ModConfigSpec.DoubleValue ETHEREAL_SOLID_REGEN;

    // ==================== MISCELLANEOUS ====================
    public static final ModConfigSpec.IntValue SKYBORN_GROUND_THRESHOLD;
    public static final ModConfigSpec.DoubleValue WIND_BLAST_PUSH_STRENGTH;
    public static final ModConfigSpec.DoubleValue WIND_BLAST_SELF_LAUNCH;
    public static final ModConfigSpec.IntValue UMBRAKIN_LIGHT_DAMAGE_THRESHOLD;
    public static final ModConfigSpec.DoubleValue UMBRAKIN_SUNLIGHT_DAMAGE;

    public static final ModConfigSpec SPEC;

    static {
        // ==================== GENERAL SETTINGS ====================
        BUILDER.push("General Settings");

        ENABLE_SIZE_SCALING = BUILDER
                .comment("Enable or disable origin-based player size scaling.",
                        "When disabled, all players will be normal sized regardless of origin.")
                .define("enableSizeScaling", true);

        ENABLE_WEAKNESSES = BUILDER
                .comment("Enable origin weaknesses (extra damage from certain sources).",
                        "Disable if you want origins to only have benefits.")
                .define("enableWeaknesses", true);

        ENABLE_RESISTANCES = BUILDER
                .comment("Enable origin resistances (reduced damage from certain sources).")
                .define("enableResistances", true);

        SHOW_ORIGIN_PARTICLES = BUILDER
                .comment("Show particle effects for origin abilities.")
                .define("showOriginParticles", true);

        ENABLE_ORIGIN_SOUNDS = BUILDER
                .comment("Play sound effects for origin abilities.")
                .define("enableOriginSounds", true);

        BUILDER.pop();

        // ==================== ORIGIN SIZE SCALING ====================
        BUILDER.push("Origin Sizes");
        BUILDER.comment("Size multipliers for each origin.",
                "1.0 = normal size, 0.5 = half size, 2.0 = double size",
                "Recommended range: 0.5 to 1.5 for balanced gameplay");

        VEILBORN_SIZE = BUILDER.comment("Veilborn - Masters of the boundary between life and death")
                .defineInRange("veilbornSize", 0.95, 0.5, 2.0);
        STONEHEART_SIZE = BUILDER.comment("Stoneheart - Living stone, immovable and heavy")
                .defineInRange("stoneheartSize", 1.2, 0.5, 2.0);
        FERALKIN_SIZE = BUILDER.comment("Feralkin - Beast shapeshifters with animal instincts")
                .defineInRange("feralkinSize", 0.95, 0.5, 2.0);
        RIFTWALKER_SIZE = BUILDER.comment("Riftwalker - Dimensional travelers who bend space")
                .defineInRange("riftwalkerSize", 1.0, 0.5, 2.0);
        UMBRAKIN_SIZE = BUILDER.comment("Umbrakin - Shadow manipulators, creatures of darkness")
                .defineInRange("umbrakinSize", 0.9, 0.5, 2.0);
        VOIDTOUCHED_SIZE = BUILDER.comment("Voidtouched - Corrupted by the void, reality benders")
                .defineInRange("voidtouchedSize", 1.05, 0.5, 2.0);
        FROSTBORN_SIZE = BUILDER.comment("Frostborn - Children of winter, masters of ice")
                .defineInRange("frostbornSize", 1.0, 0.5, 2.0);
        CINDERSOUL_SIZE = BUILDER.comment("Cindersoul - Born from volcanic fury")
                .defineInRange("cindersoulSize", 1.0, 0.5, 2.0);
        TIDECALLER_SIZE = BUILDER.comment("Tidecaller - Ocean dwellers who command water")
                .defineInRange("tidecallerSize", 0.95, 0.5, 2.0);
        STARBORNE_SIZE = BUILDER.comment("Starborne - Fallen from the cosmos, light beings")
                .defineInRange("starborneSize", 0.85, 0.5, 2.0);
        SKYBORN_SIZE = BUILDER.comment("Skyborn - Wind masters who never touch the ground")
                .defineInRange("skybornSize", 0.9, 0.5, 2.0);
        MYCOMORPH_SIZE = BUILDER.comment("Mycomorph - Part mushroom, part humanoid")
                .defineInRange("mycomorphSize", 0.9, 0.5, 2.0);
        CRYSTALLINE_SIZE = BUILDER.comment("Crystalline - Living crystal with resonant powers")
                .defineInRange("crystallineSize", 1.1, 0.5, 2.0);
        TECHNOMANCER_SIZE = BUILDER.comment("Technomancer - Cybernetic being, part flesh part machine")
                .defineInRange("technomancerSize", 1.0, 0.5, 2.0);
        ETHEREAL_SIZE = BUILDER.comment("Ethereal - Ghostly being phased between dimensions")
                .defineInRange("etherealSize", 0.85, 0.5, 2.0);

        BUILDER.pop();

        // ==================== DAMAGE MODIFIERS ====================
        BUILDER.push("Damage Modifiers");
        BUILDER.comment("Multipliers for weakness/resistance damage.",
                ">1.0 means MORE damage taken (weakness)",
                "<1.0 means LESS damage taken (resistance)");

        BUILDER.push("Weaknesses");
        TIDECALLER_FIRE_WEAKNESS = BUILDER.comment("Tidecaller fire damage multiplier")
                .defineInRange("tidecallerFireWeakness", 1.5, 1.0, 5.0);
        TIDECALLER_LIGHTNING_WEAKNESS = BUILDER.comment("Tidecaller lightning damage multiplier")
                .defineInRange("tidecallerLightningWeakness", 1.25, 1.0, 5.0);
        STARBORNE_VOID_WEAKNESS = BUILDER.comment("Starborne void damage multiplier")
                .defineInRange("starborneVoidWeakness", 2.0, 1.0, 5.0);
        SKYBORN_GROUND_WEAKNESS = BUILDER.comment("Skyborn damage multiplier when on ground below threshold")
                .defineInRange("skybornGroundWeakness", 1.5, 1.0, 5.0);
        MYCOMORPH_FIRE_WEAKNESS = BUILDER.comment("Mycomorph fire damage multiplier")
                .defineInRange("mycomorphFireWeakness", 2.0, 1.0, 5.0);
        CRYSTALLINE_EXPLOSION_WEAKNESS = BUILDER.comment("Crystalline explosion damage multiplier")
                .defineInRange("crystallineExplosionWeakness", 1.75, 1.0, 5.0);
        TECHNOMANCER_LIGHTNING_WEAKNESS = BUILDER.comment("Technomancer lightning damage multiplier")
                .defineInRange("technomancerLightningWeakness", 3.0, 1.0, 5.0);
        FROSTBORN_FIRE_WEAKNESS = BUILDER.comment("Frostborn fire damage multiplier")
                .defineInRange("frostbornFireWeakness", 2.0, 1.0, 5.0);
        VOIDTOUCHED_VOID_WEAKNESS = BUILDER
                .comment("Voidtouched void damage multiplier (yes, they're affected by void too)")
                .defineInRange("voidtouchedVoidWeakness", 0.25, 0.0, 1.0);
        RIFTWALKER_VOID_WEAKNESS = BUILDER.comment("Riftwalker void damage multiplier")
                .defineInRange("riftwalkerVoidWeakness", 1.5, 1.0, 5.0);
        BUILDER.pop();

        BUILDER.push("Resistances");
        SKYBORN_AIRBORNE_RESISTANCE = BUILDER
                .comment("Skyborn damage multiplier when airborne (lower = more resistance)")
                .defineInRange("skybornAirborneResistance", 0.75, 0.0, 1.0);
        ETHEREAL_PHYSICAL_RESISTANCE = BUILDER.comment("Ethereal physical damage multiplier")
                .defineInRange("etherealPhysicalResistance", 0.5, 0.0, 1.0);
        STONEHEART_REFLECT_PERCENTAGE = BUILDER.comment("Stoneheart Stone Skin damage reflection percentage")
                .defineInRange("stoneheartReflectPercentage", 0.5, 0.0, 1.0);
        VOIDTOUCHED_VOID_RESISTANCE = BUILDER.comment("Voidtouched void damage resistance (multiplier)")
                .defineInRange("voidtouchedVoidResistance", 0.25, 0.0, 1.0);
        BUILDER.pop();

        BUILDER.pop();

        // ==================== ABILITY COOLDOWNS ====================
        BUILDER.push("Ability Cooldowns");
        BUILDER.comment("Cooldowns in ticks (20 ticks = 1 second)");

        SEISMIC_SLAM_COOLDOWN = BUILDER.comment("Stoneheart Seismic Slam cooldown")
                .defineInRange("seismicSlamCooldown", 600, 20, 6000);
        STONE_SKIN_COOLDOWN = BUILDER.comment("Stoneheart Stone Skin cooldown")
                .defineInRange("stoneSkinCooldown", 1200, 20, 6000);
        FLAME_BURST_COOLDOWN = BUILDER.comment("Cindersoul Flame Burst cooldown")
                .defineInRange("flameBurstCooldown", 300, 20, 6000);
        LAVA_WALK_COOLDOWN = BUILDER.comment("Cindersoul Lava Walk cooldown")
                .defineInRange("lavaWalkCooldown", 600, 20, 6000);
        TIDAL_WAVE_COOLDOWN = BUILDER.comment("Tidecaller Tidal Wave cooldown")
                .defineInRange("tidalWaveCooldown", 900, 20, 6000);
        AQUA_BUBBLE_COOLDOWN = BUILDER.comment("Tidecaller Aqua Bubble cooldown")
                .defineInRange("aquaBubbleCooldown", 400, 20, 6000);
        ICE_SPIKE_COOLDOWN = BUILDER.comment("Frostborn Ice Spike cooldown")
                .defineInRange("iceSpikeCooldown", 100, 20, 6000);
        BLIZZARD_COOLDOWN = BUILDER.comment("Frostborn Blizzard cooldown")
                .defineInRange("blizzardCooldown", 2400, 20, 6000);
        WIND_BLAST_COOLDOWN = BUILDER.comment("Skyborn Wind Blast cooldown")
                .defineInRange("windBlastCooldown", 300, 20, 6000);
        UPDRAFT_COOLDOWN = BUILDER.comment("Skyborn Updraft cooldown")
                .defineInRange("updraftCooldown", 200, 20, 6000);
        CELESTIAL_DASH_COOLDOWN = BUILDER.comment("Starborne Celestial Dash cooldown")
                .defineInRange("celestialDashCooldown", 100, 20, 6000);
        STARLIGHT_BEACON_COOLDOWN = BUILDER.comment("Starborne Starlight Beacon cooldown")
                .defineInRange("starlightBeaconCooldown", 1200, 20, 6000);
        PHASE_SHIFT_COOLDOWN = BUILDER.comment("Ethereal Phase Shift cooldown")
                .defineInRange("phaseShiftCooldown", 200, 20, 6000);
        POSSESSION_COOLDOWN = BUILDER.comment("Ethereal Possession cooldown")
                .defineInRange("possessionCooldown", 1200, 20, 6000);
        DIMENSIONAL_HOP_COOLDOWN = BUILDER.comment("Riftwalker Dimensional Hop cooldown")
                .defineInRange("dimensionalHopCooldown", 100, 20, 6000);
        POCKET_DIMENSION_COOLDOWN = BUILDER.comment("Riftwalker Pocket Dimension cooldown")
                .defineInRange("pocketDimensionCooldown", 600, 20, 6000);
        VOID_TEAR_COOLDOWN = BUILDER.comment("Voidtouched Void Tear cooldown")
                .defineInRange("voidTearCooldown", 400, 20, 6000);
        REALITY_SHIFT_COOLDOWN = BUILDER.comment("Voidtouched Reality Shift cooldown")
                .defineInRange("realityShiftCooldown", 600, 20, 6000);

        BUILDER.pop();

        // ==================== ABILITY DAMAGE VALUES ====================
        BUILDER.push("Ability Damage");
        BUILDER.comment("Damage values for various abilities");

        SEISMIC_SLAM_DAMAGE = BUILDER.comment("Stoneheart Seismic Slam damage")
                .defineInRange("seismicSlamDamage", 4.0, 0.0, 50.0);
        FLAME_BURST_DAMAGE = BUILDER.comment("Cindersoul Flame Burst damage")
                .defineInRange("flameBurstDamage", 4.0, 0.0, 50.0);
        ICE_SPIKE_DAMAGE = BUILDER.comment("Frostborn Ice Spike damage")
                .defineInRange("iceSpikeDamage", 6.0, 0.0, 50.0);
        VOID_TEAR_DAMAGE = BUILDER.comment("Voidtouched Void Tear damage")
                .defineInRange("voidTearDamage", 8.0, 0.0, 50.0);
        DARKNESS_BOLT_DAMAGE = BUILDER.comment("Umbrakin Darkness Bolt damage")
                .defineInRange("darknessBoltDamage", 6.0, 0.0, 50.0);
        CRYSTAL_SPIKE_DAMAGE = BUILDER.comment("Crystalline Crystal Spike damage")
                .defineInRange("crystalSpikeDamage", 5.0, 0.0, 50.0);
        REDSTONE_PULSE_DAMAGE = BUILDER.comment("Technomancer Redstone Pulse damage")
                .defineInRange("redstonePulseDamage", 4.0, 0.0, 50.0);

        BUILDER.pop();

        // ==================== ABILITY RANGES ====================
        BUILDER.push("Ability Ranges");
        BUILDER.comment("Range/radius values for abilities (in blocks)");

        SEISMIC_SLAM_RADIUS = BUILDER.comment("Stoneheart Seismic Slam radius")
                .defineInRange("seismicSlamRadius", 10, 1, 50);
        FLAME_BURST_RADIUS = BUILDER.comment("Cindersoul Flame Burst radius")
                .defineInRange("flameBurstRadius", 8, 1, 50);
        TIDAL_WAVE_RADIUS = BUILDER.comment("Tidecaller Tidal Wave radius")
                .defineInRange("tidalWaveRadius", 15, 1, 50);
        BLIZZARD_RADIUS = BUILDER.comment("Frostborn Blizzard radius")
                .defineInRange("blizzardRadius", 20, 1, 50);
        PRIMAL_ROAR_RADIUS = BUILDER.comment("Feralkin Primal Roar radius")
                .defineInRange("primalRoarRadius", 10, 1, 50);
        VOID_TEAR_RANGE = BUILDER.comment("Voidtouched Void Tear range")
                .defineInRange("voidTearRange", 30, 5, 100);
        DIMENSIONAL_HOP_RANGE = BUILDER.comment("Riftwalker Dimensional Hop range")
                .defineInRange("dimensionalHopRange", 50, 5, 200);

        BUILDER.pop();

        // ==================== ABILITY DURATIONS ====================
        BUILDER.push("Ability Durations");
        BUILDER.comment("Duration values for abilities (in ticks, 20 = 1 second)");

        STONE_SKIN_DURATION = BUILDER.comment("Stoneheart Stone Skin duration")
                .defineInRange("stoneSkinDuration", 200, 20, 2400);
        BLIZZARD_DURATION = BUILDER.comment("Frostborn Blizzard duration")
                .defineInRange("blizzardDuration", 600, 100, 2400);
        PHASE_SHIFT_DURATION = BUILDER.comment("Ethereal Phase Shift duration")
                .defineInRange("phaseShiftDuration", 160, 20, 600);
        BEAST_FORM_DURATION = BUILDER.comment("Feralkin Beast Form duration")
                .defineInRange("beastFormDuration", 1200, 100, 2400);
        OVERCLOCK_DURATION = BUILDER.comment("Technomancer Overclock duration")
                .defineInRange("overclockDuration", 200, 20, 1200);

        BUILDER.pop();

        // ==================== RESOURCE GENERATION ====================
        BUILDER.push("Resource Generation");
        BUILDER.comment("Resource regeneration/drain rates per tick");

        CINDERSOUL_HEAT_REGEN = BUILDER.comment("Cindersoul heat regeneration near heat sources")
                .defineInRange("cindersoulHeatRegen", 1.0, 0.0, 10.0);
        CINDERSOUL_COLD_DRAIN = BUILDER.comment("Cindersoul heat drain in cold areas")
                .defineInRange("cindersoulColdDrain", 0.5, 0.0, 10.0);
        TIDECALLER_WATER_REGEN = BUILDER.comment("Tidecaller hydration regen in water/rain")
                .defineInRange("tidecallerWaterRegen", 2.0, 0.0, 10.0);
        TIDECALLER_LAND_DRAIN = BUILDER.comment("Tidecaller hydration drain on land")
                .defineInRange("tidecallerLandDrain", 0.015, 0.0, 1.0);
        TIDECALLER_DESERT_MULTIPLIER = BUILDER.comment("Tidecaller drain multiplier in hot biomes")
                .defineInRange("tidecallerDesertMultiplier", 3.0, 1.0, 10.0);
        STARBORNE_DAY_REGEN = BUILDER.comment("Starborne energy regen during day with sky access")
                .defineInRange("starborneDayRegen", 1.0, 0.0, 10.0);
        STARBORNE_DARK_DRAIN = BUILDER.comment("Starborne energy drain in total darkness")
                .defineInRange("starborneDarkDrain", 0.5, 0.0, 10.0);
        CRYSTALLINE_SUNLIGHT_REGEN = BUILDER.comment("Crystalline charge regen in sunlight")
                .defineInRange("crystallineSunlightRegen", 0.5, 0.0, 10.0);
        TECHNOMANCER_IDLE_REGEN = BUILDER.comment("Technomancer power regen when standing still")
                .defineInRange("technomancerIdleRegen", 1.0, 0.0, 10.0);
        ETHEREAL_SOLID_REGEN = BUILDER.comment("Ethereal phase energy regen when solid")
                .defineInRange("etherealSolidRegen", 0.5, 0.0, 10.0);

        BUILDER.pop();

        // ==================== MISCELLANEOUS ====================
        BUILDER.push("Miscellaneous");

        SKYBORN_GROUND_THRESHOLD = BUILDER.comment("Y level below which Skyborn get ground weakness")
                .defineInRange("skybornGroundThreshold", 70, 0, 320);
        WIND_BLAST_PUSH_STRENGTH = BUILDER.comment("Skyborn Wind Blast push strength")
                .defineInRange("windBlastPushStrength", 2.0, 0.5, 5.0);
        WIND_BLAST_SELF_LAUNCH = BUILDER.comment("Skyborn Wind Blast self-launch when looking down")
                .defineInRange("windBlastSelfLaunch", 1.5, 0.5, 5.0);
        UMBRAKIN_LIGHT_DAMAGE_THRESHOLD = BUILDER.comment("Light level at which Umbrakin start taking damage")
                .defineInRange("umbrakinLightDamageThreshold", 11, 0, 15);
        UMBRAKIN_SUNLIGHT_DAMAGE = BUILDER.comment("Damage per second to Umbrakin in bright light")
                .defineInRange("umbrakinSunlightDamage", 1.0, 0.0, 10.0);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    // ==================== RUNTIME CACHED VALUES ====================
    // General
    public static boolean enableSizeScaling;
    public static boolean enableWeaknesses;
    public static boolean enableResistances;
    public static boolean showOriginParticles;
    public static boolean enableOriginSounds;

    // Sizes
    public static double veilbornSize, stoneheartSize, feralkinSize, riftwalkerSize, umbrakinSize;
    public static double voidtouchedSize, frostbornSize, cindersoulSize, tidecallerSize, starborneSize;
    public static double skybornSize, mycomorphSize, crystallineSize, technomancerSize, etherealSize;

    // Damage Modifiers
    public static double tidecallerFireWeakness, tidecallerLightningWeakness, starborneVoidWeakness;
    public static double skybornGroundWeakness, mycomorphFireWeakness, crystallineExplosionWeakness;
    public static double technomancerLightningWeakness, frostbornFireWeakness;
    public static double voidtouchedVoidWeakness, riftwalkerVoidWeakness;
    public static double skybornAirborneResistance, etherealPhysicalResistance;
    public static double stoneheartReflectPercentage, voidtouchedVoidResistance;

    // Cooldowns
    public static int seismicSlamCooldown, stoneSkinCooldown, flameBurstCooldown, lavaWalkCooldown;
    public static int tidalWaveCooldown, aquaBubbleCooldown, iceSpikeCooldown, blizzardCooldown;
    public static int windBlastCooldown, updraftCooldown, celestialDashCooldown, starlightBeaconCooldown;
    public static int phaseShiftCooldown, possessionCooldown, dimensionalHopCooldown, pocketDimensionCooldown;
    public static int voidTearCooldown, realityShiftCooldown;

    // Damage Values
    public static double seismicSlamDamage, flameBurstDamage, iceSpikeDamage, voidTearDamage;
    public static double darknessBoltDamage, crystalSpikeDamage, redstonePulseDamage;

    // Ranges
    public static int seismicSlamRadius, flameBurstRadius, tidalWaveRadius, blizzardRadius;
    public static int primalRoarRadius, voidTearRange, dimensionalHopRange;

    // Durations
    public static int stoneSkinDuration, blizzardDuration, phaseShiftDuration;
    public static int beastFormDuration, overclockDuration;

    // Resource Generation
    public static double cindersoulHeatRegen, cindersoulColdDrain;
    public static double tidecallerWaterRegen, tidecallerLandDrain, tidecallerDesertMultiplier;
    public static double starborneDayRegen, starborneDarkDrain;
    public static double crystallineSunlightRegen, technomancerIdleRegen, etherealSolidRegen;

    // Misc
    public static int skybornGroundThreshold, umbrakinLightDamageThreshold;
    public static double windBlastPushStrength, windBlastSelfLaunch, umbrakinSunlightDamage;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        // General
        enableSizeScaling = ENABLE_SIZE_SCALING.get();
        enableWeaknesses = ENABLE_WEAKNESSES.get();
        enableResistances = ENABLE_RESISTANCES.get();
        showOriginParticles = SHOW_ORIGIN_PARTICLES.get();
        enableOriginSounds = ENABLE_ORIGIN_SOUNDS.get();

        // Sizes
        veilbornSize = VEILBORN_SIZE.get();
        stoneheartSize = STONEHEART_SIZE.get();
        feralkinSize = FERALKIN_SIZE.get();
        riftwalkerSize = RIFTWALKER_SIZE.get();
        umbrakinSize = UMBRAKIN_SIZE.get();
        voidtouchedSize = VOIDTOUCHED_SIZE.get();
        frostbornSize = FROSTBORN_SIZE.get();
        cindersoulSize = CINDERSOUL_SIZE.get();
        tidecallerSize = TIDECALLER_SIZE.get();
        starborneSize = STARBORNE_SIZE.get();
        skybornSize = SKYBORN_SIZE.get();
        mycomorphSize = MYCOMORPH_SIZE.get();
        crystallineSize = CRYSTALLINE_SIZE.get();
        technomancerSize = TECHNOMANCER_SIZE.get();
        etherealSize = ETHEREAL_SIZE.get();

        // Damage Modifiers - Weaknesses
        tidecallerFireWeakness = TIDECALLER_FIRE_WEAKNESS.get();
        tidecallerLightningWeakness = TIDECALLER_LIGHTNING_WEAKNESS.get();
        starborneVoidWeakness = STARBORNE_VOID_WEAKNESS.get();
        skybornGroundWeakness = SKYBORN_GROUND_WEAKNESS.get();
        mycomorphFireWeakness = MYCOMORPH_FIRE_WEAKNESS.get();
        crystallineExplosionWeakness = CRYSTALLINE_EXPLOSION_WEAKNESS.get();
        technomancerLightningWeakness = TECHNOMANCER_LIGHTNING_WEAKNESS.get();
        frostbornFireWeakness = FROSTBORN_FIRE_WEAKNESS.get();
        voidtouchedVoidWeakness = VOIDTOUCHED_VOID_WEAKNESS.get();
        riftwalkerVoidWeakness = RIFTWALKER_VOID_WEAKNESS.get();

        // Damage Modifiers - Resistances
        skybornAirborneResistance = SKYBORN_AIRBORNE_RESISTANCE.get();
        etherealPhysicalResistance = ETHEREAL_PHYSICAL_RESISTANCE.get();
        stoneheartReflectPercentage = STONEHEART_REFLECT_PERCENTAGE.get();
        voidtouchedVoidResistance = VOIDTOUCHED_VOID_RESISTANCE.get();

        // Cooldowns
        seismicSlamCooldown = SEISMIC_SLAM_COOLDOWN.get();
        stoneSkinCooldown = STONE_SKIN_COOLDOWN.get();
        flameBurstCooldown = FLAME_BURST_COOLDOWN.get();
        lavaWalkCooldown = LAVA_WALK_COOLDOWN.get();
        tidalWaveCooldown = TIDAL_WAVE_COOLDOWN.get();
        aquaBubbleCooldown = AQUA_BUBBLE_COOLDOWN.get();
        iceSpikeCooldown = ICE_SPIKE_COOLDOWN.get();
        blizzardCooldown = BLIZZARD_COOLDOWN.get();
        windBlastCooldown = WIND_BLAST_COOLDOWN.get();
        updraftCooldown = UPDRAFT_COOLDOWN.get();
        celestialDashCooldown = CELESTIAL_DASH_COOLDOWN.get();
        starlightBeaconCooldown = STARLIGHT_BEACON_COOLDOWN.get();
        phaseShiftCooldown = PHASE_SHIFT_COOLDOWN.get();
        possessionCooldown = POSSESSION_COOLDOWN.get();
        dimensionalHopCooldown = DIMENSIONAL_HOP_COOLDOWN.get();
        pocketDimensionCooldown = POCKET_DIMENSION_COOLDOWN.get();
        voidTearCooldown = VOID_TEAR_COOLDOWN.get();
        realityShiftCooldown = REALITY_SHIFT_COOLDOWN.get();

        // Damage Values
        seismicSlamDamage = SEISMIC_SLAM_DAMAGE.get();
        flameBurstDamage = FLAME_BURST_DAMAGE.get();
        iceSpikeDamage = ICE_SPIKE_DAMAGE.get();
        voidTearDamage = VOID_TEAR_DAMAGE.get();
        darknessBoltDamage = DARKNESS_BOLT_DAMAGE.get();
        crystalSpikeDamage = CRYSTAL_SPIKE_DAMAGE.get();
        redstonePulseDamage = REDSTONE_PULSE_DAMAGE.get();

        // Ranges
        seismicSlamRadius = SEISMIC_SLAM_RADIUS.get();
        flameBurstRadius = FLAME_BURST_RADIUS.get();
        tidalWaveRadius = TIDAL_WAVE_RADIUS.get();
        blizzardRadius = BLIZZARD_RADIUS.get();
        primalRoarRadius = PRIMAL_ROAR_RADIUS.get();
        voidTearRange = VOID_TEAR_RANGE.get();
        dimensionalHopRange = DIMENSIONAL_HOP_RANGE.get();

        // Durations
        stoneSkinDuration = STONE_SKIN_DURATION.get();
        blizzardDuration = BLIZZARD_DURATION.get();
        phaseShiftDuration = PHASE_SHIFT_DURATION.get();
        beastFormDuration = BEAST_FORM_DURATION.get();
        overclockDuration = OVERCLOCK_DURATION.get();

        // Resource Generation
        cindersoulHeatRegen = CINDERSOUL_HEAT_REGEN.get();
        cindersoulColdDrain = CINDERSOUL_COLD_DRAIN.get();
        tidecallerWaterRegen = TIDECALLER_WATER_REGEN.get();
        tidecallerLandDrain = TIDECALLER_LAND_DRAIN.get();
        tidecallerDesertMultiplier = TIDECALLER_DESERT_MULTIPLIER.get();
        starborneDayRegen = STARBORNE_DAY_REGEN.get();
        starborneDarkDrain = STARBORNE_DARK_DRAIN.get();
        crystallineSunlightRegen = CRYSTALLINE_SUNLIGHT_REGEN.get();
        technomancerIdleRegen = TECHNOMANCER_IDLE_REGEN.get();
        etherealSolidRegen = ETHEREAL_SOLID_REGEN.get();

        // Misc
        skybornGroundThreshold = SKYBORN_GROUND_THRESHOLD.get();
        windBlastPushStrength = WIND_BLAST_PUSH_STRENGTH.get();
        windBlastSelfLaunch = WIND_BLAST_SELF_LAUNCH.get();
        umbrakinLightDamageThreshold = UMBRAKIN_LIGHT_DAMAGE_THRESHOLD.get();
        umbrakinSunlightDamage = UMBRAKIN_SUNLIGHT_DAMAGE.get();
    }

    /**
     * Get the configured size for a specific origin
     */
    public static double getOriginSize(String originPath) {
        if (!enableSizeScaling) {
            return 1.0;
        }

        return switch (originPath) {
            case "veilborn" -> veilbornSize;
            case "stoneheart" -> stoneheartSize;
            case "feralkin" -> feralkinSize;
            case "riftwalker" -> riftwalkerSize;
            case "umbrakin" -> umbrakinSize;
            case "voidtouched" -> voidtouchedSize;
            case "frostborn" -> frostbornSize;
            case "cindersoul" -> cindersoulSize;
            case "tidecaller" -> tidecallerSize;
            case "starborne" -> starborneSize;
            case "skyborn" -> skybornSize;
            case "mycomorph" -> mycomorphSize;
            case "crystalline" -> crystallineSize;
            case "technomancer" -> technomancerSize;
            case "ethereal" -> etherealSize;
            default -> 1.0;
        };
    }
}
