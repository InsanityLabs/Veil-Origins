package com.veilorigins.progression;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.*;

/**
 * Defines unique XP sources for each origin.
 * Each origin gains XP in thematically appropriate ways.
 */
public class OriginXPSources {
    
    // XP amounts
    public static final int XP_TINY = 1;
    public static final int XP_SMALL = 3;
    public static final int XP_MEDIUM = 8;
    public static final int XP_LARGE = 15;
    public static final int XP_HUGE = 30;
    public static final int XP_MASSIVE = 50;
    
    /**
     * Get XP for killing an entity based on origin.
     */
    public static int getKillXP(String originId, LivingEntity killed) {
        EntityType<?> type = killed.getType();
        
        return switch (originId) {
            // VAMPIRE - Draining blood from living creatures
            case "vampire" -> {
                if (killed instanceof Player) yield XP_MASSIVE; // PvP blood is powerful
                if (type == EntityType.VILLAGER) yield XP_LARGE; // Villager blood
                if (killed instanceof Animal) yield XP_MEDIUM; // Animal blood
                if (killed instanceof Monster) yield XP_SMALL; // Monster blood is weak
                yield XP_TINY;
            }
            
            // VAMPLING - Learning to hunt
            case "vampling" -> {
                if (killed instanceof Animal) yield XP_MEDIUM; // Easier prey
                if (type == EntityType.VILLAGER) yield XP_SMALL; // Still learning
                yield XP_TINY;
            }
            
            // WEREWOLF - Hunting and killing prey
            case "werewolf" -> {
                if (killed instanceof Player) yield XP_HUGE; // Alpha dominance
                if (type == EntityType.WOLF) yield XP_TINY; // Don't kill your own kind
                if (killed instanceof Animal) yield XP_LARGE; // Hunting prey
                if (killed instanceof Monster) yield XP_MEDIUM; // Fighting threats
                yield XP_SMALL;
            }
            
            // WOLFLING - Pack hunting
            case "wolfling" -> {
                if (killed instanceof Animal) yield XP_MEDIUM;
                if (killed instanceof Monster) yield XP_SMALL;
                yield XP_TINY;
            }
            
            // CINDERSOUL - Burning enemies
            case "cindersoul" -> {
                if (killed.isOnFire()) yield XP_LARGE; // Burned to death
                if (type == EntityType.BLAZE) yield XP_TINY; // Fire immune
                if (type == EntityType.MAGMA_CUBE) yield XP_TINY;
                yield XP_SMALL;
            }
            
            // EMBERLING - Small flames
            case "emberling" -> {
                if (killed.isOnFire()) yield XP_MEDIUM;
                yield XP_TINY;
            }
            
            // FROSTBORN - Freezing enemies
            case "frostborn" -> {
                if (killed.isFullyFrozen()) yield XP_LARGE; // Frozen to death
                if (type == EntityType.STRAY) yield XP_MEDIUM; // Ice skeleton
                if (type == EntityType.POLAR_BEAR) yield XP_TINY; // Cold creatures
                yield XP_SMALL;
            }
            
            // VOIDTOUCHED - Void damage and End creatures
            case "voidtouched" -> {
                if (type == EntityType.ENDERMAN) yield XP_LARGE; // Ender creatures
                if (type == EntityType.ENDERMITE) yield XP_MEDIUM;
                if (type == EntityType.SHULKER) yield XP_HUGE;
                if (killed instanceof Player) yield XP_MASSIVE; // Void corruption
                yield XP_SMALL;
            }
            
            // NECROMANCER - Death and undead
            case "necromancer" -> {
                if (killed instanceof Player) yield XP_MASSIVE; // Soul harvest
                if (type == EntityType.VILLAGER) yield XP_LARGE; // Fresh corpse
                if (killed instanceof Animal) yield XP_MEDIUM; // Life force
                if (killed instanceof Zombie || killed instanceof Skeleton) yield XP_TINY; // Already dead
                yield XP_SMALL;
            }
            
            // DRYAD - Protecting nature (killing threats)
            case "dryad" -> {
                if (killed instanceof Monster) yield XP_MEDIUM; // Protecting the forest
                if (killed instanceof Animal) yield XP_TINY; // Nature's creatures
                yield XP_SMALL;
            }
            
            // STONEHEART - Crushing enemies
            case "stoneheart" -> {
                if (type == EntityType.SILVERFISH) yield XP_LARGE; // Stone pests
                if (killed instanceof Monster) yield XP_MEDIUM;
                yield XP_SMALL;
            }
            
            // SKYBORN - Aerial kills
            case "skyborn" -> {
                if (type == EntityType.PHANTOM) yield XP_LARGE; // Sky predators
                if (type == EntityType.PARROT || type == EntityType.BAT) yield XP_TINY;
                yield XP_SMALL;
            }
            
            // TIDECALLER - Aquatic dominance
            case "tidecaller" -> {
                if (type == EntityType.GUARDIAN) yield XP_LARGE;
                if (type == EntityType.ELDER_GUARDIAN) yield XP_MASSIVE;
                if (type == EntityType.DROWNED) yield XP_MEDIUM;
                if (type == EntityType.SQUID || type == EntityType.DOLPHIN) yield XP_TINY;
                yield XP_SMALL;
            }
            
            // STARBORNE - Light vs darkness
            case "starborne" -> {
                if (type == EntityType.PHANTOM) yield XP_LARGE; // Creatures of night
                if (killed instanceof Monster) yield XP_MEDIUM; // Dark creatures
                yield XP_SMALL;
            }
            
            // VEILBORN - Spectral kills
            case "veilborn" -> {
                if (killed instanceof Player) yield XP_HUGE; // Soul taking
                if (type == EntityType.PHANTOM) yield XP_MEDIUM;
                yield XP_SMALL;
            }
            
            // ETHEREAL - Spirit harvesting
            case "ethereal" -> {
                if (killed instanceof Player) yield XP_LARGE;
                if (type == EntityType.PHANTOM) yield XP_MEDIUM;
                yield XP_SMALL;
            }
            
            // CRYSTALLINE - Shattering enemies
            case "crystalline" -> {
                if (type == EntityType.SILVERFISH) yield XP_MEDIUM;
                if (killed instanceof Monster) yield XP_SMALL;
                yield XP_TINY;
            }
            
            // MYCOMORPH - Spreading spores
            case "mycomorph" -> {
                if (killed.hasEffect(net.minecraft.world.effect.MobEffects.POISON)) yield XP_LARGE;
                yield XP_SMALL;
            }
            
            // UMBRAKIN - Shadow kills
            case "umbrakin" -> {
                if (killed instanceof Player) yield XP_HUGE; // Assassination
                if (killed instanceof Monster) yield XP_MEDIUM;
                yield XP_SMALL;
            }
            
            // RIFTWALKER - Dimensional kills
            case "riftwalker" -> {
                if (type == EntityType.ENDERMAN) yield XP_LARGE;
                if (type == EntityType.ENDERMITE) yield XP_MEDIUM;
                yield XP_SMALL;
            }
            
            // TECHNOMANCER - Efficient elimination
            case "technomancer" -> {
                if (type == EntityType.IRON_GOLEM) yield XP_LARGE; // Machine vs machine
                if (killed instanceof Monster) yield XP_MEDIUM;
                yield XP_SMALL;
            }
            
            default -> XP_SMALL;
        };
    }
    
    /**
     * Get XP for consuming/using an item based on origin.
     */
    public static int getConsumeXP(String originId, Item item) {
        return switch (originId) {
            // VAMPIRE - Blood-related items
            case "vampire", "vampling" -> {
                if (item == Items.ROTTEN_FLESH) yield XP_SMALL; // Desperate
                if (item == Items.COOKED_BEEF || item == Items.COOKED_PORKCHOP) yield XP_MEDIUM; // Cooked blood
                if (item == Items.BEEF || item == Items.PORKCHOP) yield XP_LARGE; // Raw blood
                yield 0;
            }
            
            // CINDERSOUL/EMBERLING - Fire items
            case "cindersoul" -> {
                if (item == Items.BLAZE_POWDER) yield XP_LARGE;
                if (item == Items.MAGMA_CREAM) yield XP_MEDIUM;
                if (item == Items.FIRE_CHARGE) yield XP_SMALL;
                yield 0;
            }
            case "emberling" -> {
                if (item == Items.COAL || item == Items.CHARCOAL) yield XP_SMALL;
                yield 0;
            }
            
            // FROSTBORN - Cold items
            case "frostborn" -> {
                if (item == Items.SNOWBALL) yield XP_TINY;
                if (item == Items.POWDER_SNOW_BUCKET) yield XP_MEDIUM;
                if (item == Items.ICE) yield XP_SMALL;
                yield 0;
            }
            
            // DRYAD - Plant items
            case "dryad" -> {
                if (item == Items.APPLE || item == Items.GOLDEN_APPLE) yield XP_MEDIUM;
                if (item == Items.SWEET_BERRIES || item == Items.GLOW_BERRIES) yield XP_SMALL;
                if (item == Items.MELON_SLICE || item == Items.CARROT) yield XP_SMALL;
                yield 0;
            }
            
            // MYCOMORPH - Mushrooms
            case "mycomorph" -> {
                if (item == Items.RED_MUSHROOM || item == Items.BROWN_MUSHROOM) yield XP_MEDIUM;
                if (item == Items.MUSHROOM_STEW) yield XP_LARGE;
                if (item == Items.SUSPICIOUS_STEW) yield XP_HUGE;
                yield 0;
            }
            
            // TIDECALLER - Sea food
            case "tidecaller" -> {
                if (item == Items.COD || item == Items.SALMON) yield XP_MEDIUM;
                if (item == Items.TROPICAL_FISH) yield XP_LARGE;
                if (item == Items.PUFFERFISH) yield XP_HUGE; // Dangerous but powerful
                yield 0;
            }
            
            // VOIDTOUCHED - Ender items
            case "voidtouched" -> {
                if (item == Items.ENDER_PEARL) yield XP_MEDIUM;
                if (item == Items.CHORUS_FRUIT) yield XP_LARGE;
                if (item == Items.ENDER_EYE) yield XP_HUGE;
                yield 0;
            }
            
            // STARBORNE - Glowing items
            case "starborne" -> {
                if (item == Items.GLOW_BERRIES) yield XP_MEDIUM;
                if (item == Items.GLOWSTONE_DUST) yield XP_LARGE;
                if (item == Items.NETHER_STAR) yield XP_MASSIVE;
                yield 0;
            }
            
            default -> 0;
        };
    }
    
    /**
     * Get XP for mining/breaking a block based on origin.
     */
    public static int getMineXP(String originId, Block block) {
        return switch (originId) {
            // STONEHEART - Mining stone
            case "stoneheart" -> {
                if (block == Blocks.STONE || block == Blocks.DEEPSLATE) yield XP_TINY;
                if (block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE) yield XP_LARGE;
                if (block == Blocks.ANCIENT_DEBRIS) yield XP_HUGE;
                if (block.toString().contains("ore")) yield XP_SMALL;
                yield 0;
            }
            
            // DRYAD - Harvesting plants (but not destroying trees)
            case "dryad" -> {
                if (block == Blocks.WHEAT || block == Blocks.CARROTS || block == Blocks.POTATOES) yield XP_SMALL;
                if (block == Blocks.MELON || block == Blocks.PUMPKIN) yield XP_MEDIUM;
                if (block.toString().contains("log") || block.toString().contains("leaves")) yield 0; // No XP for killing trees
                yield 0;
            }
            
            // CRYSTALLINE - Mining crystals
            case "crystalline" -> {
                if (block == Blocks.AMETHYST_CLUSTER) yield XP_LARGE;
                if (block == Blocks.BUDDING_AMETHYST) yield XP_HUGE;
                if (block == Blocks.DIAMOND_ORE || block == Blocks.EMERALD_ORE) yield XP_MEDIUM;
                yield 0;
            }
            
            // MYCOMORPH - Harvesting mushrooms
            case "mycomorph" -> {
                if (block == Blocks.RED_MUSHROOM || block == Blocks.BROWN_MUSHROOM) yield XP_MEDIUM;
                if (block == Blocks.RED_MUSHROOM_BLOCK || block == Blocks.BROWN_MUSHROOM_BLOCK) yield XP_LARGE;
                if (block == Blocks.MUSHROOM_STEM) yield XP_SMALL;
                yield 0;
            }
            
            // FROSTBORN - Ice blocks
            case "frostborn" -> {
                if (block == Blocks.ICE || block == Blocks.PACKED_ICE) yield XP_SMALL;
                if (block == Blocks.BLUE_ICE) yield XP_MEDIUM;
                if (block == Blocks.SNOW_BLOCK) yield XP_TINY;
                yield 0;
            }
            
            // CINDERSOUL - Nether blocks
            case "cindersoul" -> {
                if (block == Blocks.MAGMA_BLOCK) yield XP_MEDIUM;
                if (block == Blocks.GLOWSTONE) yield XP_SMALL;
                if (block == Blocks.ANCIENT_DEBRIS) yield XP_LARGE;
                yield 0;
            }
            
            // VOIDTOUCHED - End blocks
            case "voidtouched" -> {
                if (block == Blocks.END_STONE) yield XP_TINY;
                if (block == Blocks.PURPUR_BLOCK) yield XP_SMALL;
                if (block == Blocks.CHORUS_FLOWER) yield XP_MEDIUM;
                yield 0;
            }
            
            default -> 0;
        };
    }
    
    /**
     * Get passive XP rate per second based on conditions.
     * Returns XP amount and reason, or 0 if no passive XP.
     */
    public static PassiveXPResult getPassiveXP(String originId, PassiveContext ctx) {
        return switch (originId) {
            // VAMPIRE - Night time, avoiding sun
            case "vampire" -> {
                if (ctx.isNight && !ctx.canSeeSky) yield new PassiveXPResult(XP_SMALL, "lurking_in_shadows");
                if (ctx.isNight && ctx.canSeeSky) yield new PassiveXPResult(XP_TINY, "night_hunt");
                yield PassiveXPResult.NONE;
            }
            
            // VAMPLING - Learning at night
            case "vampling" -> {
                if (ctx.isNight) yield new PassiveXPResult(XP_TINY, "night_learning");
                yield PassiveXPResult.NONE;
            }
            
            // WEREWOLF - Full moon, night hunting
            case "werewolf" -> {
                if (ctx.isFullMoon && ctx.isNight) yield new PassiveXPResult(XP_MEDIUM, "full_moon_power");
                if (ctx.isNight) yield new PassiveXPResult(XP_SMALL, "night_prowl");
                yield PassiveXPResult.NONE;
            }
            
            // WOLFLING - Pack presence
            case "wolfling" -> {
                if (ctx.isNight) yield new PassiveXPResult(XP_TINY, "night_howl");
                yield PassiveXPResult.NONE;
            }
            
            // CINDERSOUL - Nether, lava, fire
            case "cindersoul" -> {
                if (ctx.inNether) yield new PassiveXPResult(XP_MEDIUM, "nether_home");
                if (ctx.nearLava) yield new PassiveXPResult(XP_SMALL, "lava_warmth");
                if (ctx.onFire) yield new PassiveXPResult(XP_TINY, "embracing_flames");
                yield PassiveXPResult.NONE;
            }
            
            // EMBERLING - Near fire
            case "emberling" -> {
                if (ctx.nearLava) yield new PassiveXPResult(XP_SMALL, "fire_comfort");
                yield PassiveXPResult.NONE;
            }
            
            // FROSTBORN - Cold biomes, snow
            case "frostborn" -> {
                if (ctx.inColdBiome && ctx.inSnow) yield new PassiveXPResult(XP_MEDIUM, "blizzard_power");
                if (ctx.inColdBiome) yield new PassiveXPResult(XP_SMALL, "cold_embrace");
                if (ctx.inWater && ctx.inColdBiome) yield new PassiveXPResult(XP_MEDIUM, "frozen_depths");
                yield PassiveXPResult.NONE;
            }
            
            // VOIDTOUCHED - End dimension, void proximity
            case "voidtouched" -> {
                if (ctx.inEnd) yield new PassiveXPResult(XP_MEDIUM, "void_resonance");
                if (ctx.nearVoid) yield new PassiveXPResult(XP_SMALL, "void_whispers");
                yield PassiveXPResult.NONE;
            }
            
            // NECROMANCER - Near undead, graveyards
            case "necromancer" -> {
                if (ctx.nearUndead) yield new PassiveXPResult(XP_SMALL, "death_presence");
                if (ctx.isNight && !ctx.canSeeSky) yield new PassiveXPResult(XP_TINY, "dark_meditation");
                yield PassiveXPResult.NONE;
            }
            
            // DRYAD - Sunlight, forests, rain
            case "dryad" -> {
                if (ctx.isDay && ctx.canSeeSky && ctx.inRain) yield new PassiveXPResult(XP_MEDIUM, "rain_blessing");
                if (ctx.isDay && ctx.canSeeSky) yield new PassiveXPResult(XP_SMALL, "photosynthesis");
                if (ctx.inForest) yield new PassiveXPResult(XP_TINY, "forest_connection");
                yield PassiveXPResult.NONE;
            }
            
            // STONEHEART - Underground, caves
            case "stoneheart" -> {
                if (ctx.deepUnderground) yield new PassiveXPResult(XP_MEDIUM, "deep_earth_power");
                if (ctx.underground) yield new PassiveXPResult(XP_SMALL, "stone_communion");
                yield PassiveXPResult.NONE;
            }
            
            // SKYBORN - High altitude, flying
            case "skyborn" -> {
                if (ctx.veryHighAltitude) yield new PassiveXPResult(XP_MEDIUM, "cloud_walking");
                if (ctx.highAltitude) yield new PassiveXPResult(XP_SMALL, "sky_freedom");
                if (ctx.isFlying) yield new PassiveXPResult(XP_TINY, "wind_riding");
                yield PassiveXPResult.NONE;
            }
            
            // TIDECALLER - Water, ocean
            case "tidecaller" -> {
                if (ctx.inDeepWater) yield new PassiveXPResult(XP_MEDIUM, "ocean_depths");
                if (ctx.inWater) yield new PassiveXPResult(XP_SMALL, "water_embrace");
                if (ctx.inRain) yield new PassiveXPResult(XP_TINY, "rain_connection");
                yield PassiveXPResult.NONE;
            }
            
            // STARBORNE - Night sky, stars
            case "starborne" -> {
                if (ctx.isNight && ctx.canSeeSky && ctx.highAltitude) yield new PassiveXPResult(XP_MEDIUM, "stargazing");
                if (ctx.isNight && ctx.canSeeSky) yield new PassiveXPResult(XP_SMALL, "star_connection");
                if (ctx.isDay && ctx.canSeeSky) yield new PassiveXPResult(XP_TINY, "solar_absorption");
                yield PassiveXPResult.NONE;
            }
            
            // VEILBORN - Between dimensions
            case "veilborn" -> {
                if (ctx.lowLight) yield new PassiveXPResult(XP_SMALL, "veil_proximity");
                yield PassiveXPResult.NONE;
            }
            
            // ETHEREAL - Spirit realm connection
            case "ethereal" -> {
                if (ctx.isNight && ctx.lowLight) yield new PassiveXPResult(XP_SMALL, "spirit_communion");
                yield PassiveXPResult.NONE;
            }
            
            // CRYSTALLINE - Near crystals
            case "crystalline" -> {
                if (ctx.nearAmethyst) yield new PassiveXPResult(XP_MEDIUM, "crystal_resonance");
                if (ctx.underground) yield new PassiveXPResult(XP_TINY, "mineral_absorption");
                yield PassiveXPResult.NONE;
            }
            
            // MYCOMORPH - Dark, humid places
            case "mycomorph" -> {
                if (ctx.lowLight && ctx.underground) yield new PassiveXPResult(XP_MEDIUM, "spore_growth");
                if (ctx.lowLight) yield new PassiveXPResult(XP_SMALL, "fungal_spread");
                yield PassiveXPResult.NONE;
            }
            
            // UMBRAKIN - Shadows, darkness
            case "umbrakin" -> {
                if (ctx.isNight && !ctx.canSeeSky) yield new PassiveXPResult(XP_MEDIUM, "shadow_mastery");
                if (ctx.lowLight) yield new PassiveXPResult(XP_SMALL, "darkness_embrace");
                yield PassiveXPResult.NONE;
            }
            
            // RIFTWALKER - Dimensional instability
            case "riftwalker" -> {
                if (ctx.inEnd) yield new PassiveXPResult(XP_MEDIUM, "rift_energy");
                if (ctx.inNether) yield new PassiveXPResult(XP_SMALL, "dimensional_flux");
                yield PassiveXPResult.NONE;
            }
            
            // TECHNOMANCER - Near redstone
            case "technomancer" -> {
                if (ctx.nearRedstone) yield new PassiveXPResult(XP_MEDIUM, "power_absorption");
                yield PassiveXPResult.NONE;
            }
            
            default -> PassiveXPResult.NONE;
        };
    }
    
    /**
     * Context for passive XP calculations.
     */
    public static class PassiveContext {
        public boolean isDay;
        public boolean isNight;
        public boolean isFullMoon;
        public boolean canSeeSky;
        public boolean inWater;
        public boolean inDeepWater;
        public boolean inRain;
        public boolean inSnow;
        public boolean onFire;
        public boolean nearLava;
        public boolean inNether;
        public boolean inEnd;
        public boolean nearVoid;
        public boolean underground;
        public boolean deepUnderground;
        public boolean highAltitude;
        public boolean veryHighAltitude;
        public boolean isFlying;
        public boolean inColdBiome;
        public boolean inForest;
        public boolean lowLight;
        public boolean nearUndead;
        public boolean nearAmethyst;
        public boolean nearRedstone;
    }
    
    /**
     * Result of passive XP calculation.
     */
    public static class PassiveXPResult {
        public static final PassiveXPResult NONE = new PassiveXPResult(0, "");
        
        public final int xp;
        public final String reason;
        
        public PassiveXPResult(int xp, String reason) {
            this.xp = xp;
            this.reason = reason;
        }
    }
}
