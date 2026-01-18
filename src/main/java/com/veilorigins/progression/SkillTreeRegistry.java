package com.veilorigins.progression;

import com.veilorigins.progression.SkillTree.SkillNode;
import com.veilorigins.progression.SkillTree.SkillTier;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.*;

/**
 * Registry for all origin skill trees.
 * Each origin has a unique skill tree with 12 skills across 4 tiers.
 */
public class SkillTreeRegistry {
    
    private static final Map<String, SkillTree> SKILL_TREES = new HashMap<>();
    
    public static void init() {
        // Register skill trees for all 21 origins
        registerVeilbornTree();
        registerVampireTree();
        registerWerewolfTree();
        registerCindersoulTree();
        registerFrostbornTree();
        registerTidecallerTree();
        registerStoneheartTree();
        registerSkybornTree();
        registerUmbrakinTree();
        registerDryadTree();
        registerCrystallineTree();
        registerEtherealTree();
        registerNecromancerTree();
        registerTechnomancerTree();
        registerStarborneTree();
        registerVoidtouchedTree();
        registerRiftwalkerTree();
        registerMycomorphTree();
        registerBerserkerTree();
        registerChronoshiftTree();
        registerSoulboundTree();
    }
    
    public static SkillTree getSkillTree(String originId) {
        return SKILL_TREES.get(originId);
    }
    
    public static SkillTree getSkillTree(Identifier originId) {
        return SKILL_TREES.get(originId.getPath());
    }
    
    public static Collection<SkillTree> getAllSkillTrees() {
        return SKILL_TREES.values();
    }
    
    // ========== VEILBORN SKILL TREE ==========
    private static void registerVeilbornTree() {
        SkillTree tree = new SkillTree(Identifier.fromNamespaceAndPath("veil_origins", "veilborn"));
        
        // Tier 1 - Basic (Levels 1-10)
        tree.addNode(new SkillNode("veil_affinity", "Veil Affinity", "Increases Veil Step range by 5 blocks", 
            1, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("soul_sight", "Soul Sight", "See entity health bars through walls (10 blocks)", 
            5, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("spectral_dodge", "Spectral Dodge", "5% chance to phase through attacks", 
            10, 2, List.of("veil_affinity"), p -> {}, SkillTier.BASIC));
        
        // Tier 2 - Advanced (Levels 11-25)
        tree.addNode(new SkillNode("veil_walker", "Veil Walker", "Veil Step cooldown reduced by 5 seconds", 
            15, 2, List.of("spectral_dodge"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("soul_leech", "Soul Leech", "Heal 1 heart when killing mobs", 
            18, 2, List.of("soul_sight"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("phantom_strike", "Phantom Strike", "+15% damage after using Veil Step", 
            25, 3, List.of("veil_walker"), p -> {}, SkillTier.ADVANCED));
        
        // Tier 3 - Expert (Levels 26-40)
        tree.addNode(new SkillNode("veil_mastery", "Veil Mastery", "Veil Step can pass through 3 block walls", 
            30, 3, List.of("phantom_strike"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("soul_harvest_boost", "Soul Harvest+", "Soul Harvest radius +10 blocks", 
            35, 3, List.of("soul_leech"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("spectral_form", "Spectral Form", "Gain invisibility for 3s after Veil Step", 
            40, 4, List.of("veil_mastery"), p -> {}, SkillTier.EXPERT));
        
        // Tier 4 - Master (Levels 41-50)
        tree.addNode(new SkillNode("veil_lord", "Veil Lord", "All cooldowns reduced by 25%", 
            45, 4, List.of("spectral_form", "soul_harvest_boost"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("soul_anchor", "Soul Anchor", "Mark location, teleport back within 30s", 
            48, 5, List.of("veil_lord"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("veil_sovereign", "Veil Sovereign", "Bring 1 ally through Veil Step", 
            50, 5, List.of("soul_anchor"), p -> {}, SkillTier.MASTER));
        
        SKILL_TREES.put("veilborn", tree);
    }
    
    // ========== VAMPIRE SKILL TREE ==========
    private static void registerVampireTree() {
        SkillTree tree = new SkillTree(Identifier.fromNamespaceAndPath("veil_origins", "vampire"));
        
        tree.addNode(new SkillNode("blood_scent", "Blood Scent", "Detect injured entities within 20 blocks", 
            1, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("night_stalker", "Night Stalker", "+10% movement speed at night", 
            5, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("vampiric_bite", "Vampiric Bite", "Lifesteal increased by 10%", 
            10, 2, List.of("blood_scent"), p -> {}, SkillTier.BASIC));
        
        tree.addNode(new SkillNode("bat_form_speed", "Swift Bat", "Bat form speed increased by 25%", 
            15, 2, List.of("night_stalker"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("blood_pool", "Blood Pool", "Max blood storage +25", 
            18, 2, List.of("vampiric_bite"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("mesmerize", "Mesmerize", "Charm passive mobs for 10 seconds", 
            25, 3, List.of("bat_form_speed"), p -> {}, SkillTier.ADVANCED));
        
        tree.addNode(new SkillNode("blood_frenzy", "Blood Frenzy", "+30% attack speed when below 50% health", 
            30, 3, List.of("blood_pool"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("shadow_cloak", "Shadow Cloak", "Invisibility in darkness", 
            35, 3, List.of("mesmerize"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("eternal_night", "Eternal Night", "Sun damage reduced by 50%", 
            40, 4, List.of("shadow_cloak"), p -> {}, SkillTier.EXPERT));
        
        tree.addNode(new SkillNode("blood_lord", "Blood Lord", "Lifesteal affects all damage types", 
            45, 4, List.of("blood_frenzy", "eternal_night"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("vampire_thrall", "Vampire Thrall", "Convert killed mobs to allies (30s)", 
            48, 5, List.of("blood_lord"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("nosferatu", "Nosferatu", "Immune to sun, +50% night power", 
            50, 5, List.of("vampire_thrall"), p -> {}, SkillTier.MASTER));
        
        SKILL_TREES.put("vampire", tree);
    }
    
    // ========== WEREWOLF SKILL TREE ==========
    private static void registerWerewolfTree() {
        SkillTree tree = new SkillTree(Identifier.fromNamespaceAndPath("veil_origins", "werewolf"));
        
        tree.addNode(new SkillNode("keen_senses", "Keen Senses", "See entities through walls (15 blocks)", 
            1, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("pack_hunter", "Pack Hunter", "+5% damage per nearby wolf/player", 
            5, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("thick_fur", "Thick Fur", "+2 armor in wolf form", 
            10, 2, List.of("keen_senses"), p -> {}, SkillTier.BASIC));
        
        tree.addNode(new SkillNode("lunar_strength", "Lunar Strength", "+20% damage during full moon", 
            15, 2, List.of("pack_hunter"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("savage_leap", "Savage Leap", "Pounce distance +5 blocks", 
            18, 2, List.of("thick_fur"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("howl_range", "Echoing Howl", "Howl buff range +10 blocks", 
            25, 3, List.of("lunar_strength"), p -> {}, SkillTier.ADVANCED));
        
        tree.addNode(new SkillNode("feral_rage", "Feral Rage", "Gain Strength II when below 30% health", 
            30, 3, List.of("savage_leap"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("alpha_presence", "Alpha Presence", "Wolves won't attack, can tame instantly", 
            35, 3, List.of("howl_range"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("regeneration", "Rapid Healing", "Regenerate 1 heart every 5 seconds", 
            40, 4, List.of("feral_rage"), p -> {}, SkillTier.EXPERT));
        
        tree.addNode(new SkillNode("apex_predator", "Apex Predator", "+50% damage to animals and players", 
            45, 4, List.of("alpha_presence", "regeneration"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("pack_alpha", "Pack Alpha", "Summon 2 wolf allies (5 min cooldown)", 
            48, 5, List.of("apex_predator"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("lycan_lord", "Lycan Lord", "Permanent wolf form option, +100% stats", 
            50, 5, List.of("pack_alpha"), p -> {}, SkillTier.MASTER));
        
        SKILL_TREES.put("werewolf", tree);
    }

    
    // ========== CINDERSOUL SKILL TREE ==========
    private static void registerCindersoulTree() {
        SkillTree tree = new SkillTree(Identifier.fromNamespaceAndPath("veil_origins", "cindersoul"));
        
        tree.addNode(new SkillNode("ember_touch", "Ember Touch", "Melee attacks ignite enemies (2s)", 
            1, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("heat_resistance", "Heat Resistance", "Fire damage reduced by 25%", 
            5, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("flame_burst", "Flame Burst", "Fireball explosion radius +2 blocks", 
            10, 2, List.of("ember_touch"), p -> {}, SkillTier.BASIC));
        
        tree.addNode(new SkillNode("nether_affinity", "Nether Affinity", "+15% damage in Nether", 
            15, 2, List.of("heat_resistance"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("blazing_speed", "Blazing Speed", "+20% speed when on fire", 
            18, 2, List.of("flame_burst"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("lava_walk", "Lava Walker", "Walk on lava for 5 seconds", 
            25, 3, List.of("nether_affinity"), p -> {}, SkillTier.ADVANCED));
        
        tree.addNode(new SkillNode("inferno_aura", "Inferno Aura", "Nearby enemies take fire damage", 
            30, 3, List.of("blazing_speed"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("phoenix_spark", "Phoenix Spark", "25% chance to not consume fire charge", 
            35, 3, List.of("lava_walk"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("molten_armor", "Molten Armor", "Attackers take fire damage", 
            40, 4, List.of("inferno_aura"), p -> {}, SkillTier.EXPERT));
        
        tree.addNode(new SkillNode("fire_lord", "Fire Lord", "Complete fire immunity", 
            45, 4, List.of("phoenix_spark", "molten_armor"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("meteor_strike", "Meteor Strike", "Call down fire from sky (3 min CD)", 
            48, 5, List.of("fire_lord"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("phoenix_rebirth", "Phoenix Rebirth", "Revive once per day with full health", 
            50, 5, List.of("meteor_strike"), p -> {}, SkillTier.MASTER));
        
        SKILL_TREES.put("cindersoul", tree);
    }
    
    // ========== FROSTBORN SKILL TREE ==========
    private static void registerFrostbornTree() {
        SkillTree tree = new SkillTree(Identifier.fromNamespaceAndPath("veil_origins", "frostborn"));
        
        tree.addNode(new SkillNode("cold_touch", "Cold Touch", "Melee attacks slow enemies", 
            1, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("frost_resistance", "Frost Resistance", "Freeze damage immunity", 
            5, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("ice_shard", "Ice Shard", "Ice projectile damage +25%", 
            10, 2, List.of("cold_touch"), p -> {}, SkillTier.BASIC));
        
        tree.addNode(new SkillNode("snow_walker", "Snow Walker", "No slowdown on snow/ice", 
            15, 2, List.of("frost_resistance"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("blizzard_range", "Blizzard Range", "Blizzard radius +5 blocks", 
            18, 2, List.of("ice_shard"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("ice_armor", "Ice Armor", "+3 armor in cold biomes", 
            25, 3, List.of("snow_walker"), p -> {}, SkillTier.ADVANCED));
        
        tree.addNode(new SkillNode("permafrost", "Permafrost", "Freeze water you walk on", 
            30, 3, List.of("blizzard_range"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("glacial_shield", "Glacial Shield", "Block attacks with ice (10s CD)", 
            35, 3, List.of("ice_armor"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("hypothermia", "Hypothermia", "Frozen enemies take +50% damage", 
            40, 4, List.of("permafrost"), p -> {}, SkillTier.EXPERT));
        
        tree.addNode(new SkillNode("ice_lord", "Ice Lord", "+50% damage in cold biomes", 
            45, 4, List.of("glacial_shield", "hypothermia"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("absolute_zero", "Absolute Zero", "Freeze all nearby enemies (5 min CD)", 
            48, 5, List.of("ice_lord"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("winter_eternal", "Winter Eternal", "Create permanent snow around you", 
            50, 5, List.of("absolute_zero"), p -> {}, SkillTier.MASTER));
        
        SKILL_TREES.put("frostborn", tree);
    }
    
    // ========== TIDECALLER SKILL TREE ==========
    private static void registerTidecallerTree() {
        SkillTree tree = new SkillTree(Identifier.fromNamespaceAndPath("veil_origins", "tidecaller"));
        
        tree.addNode(new SkillNode("water_breathing", "Deep Lungs", "Underwater breath +60 seconds", 
            1, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("swim_speed", "Swift Swimmer", "+30% swim speed", 
            5, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("water_jet", "Water Jet", "Water attack range +5 blocks", 
            10, 2, List.of("water_breathing"), p -> {}, SkillTier.BASIC));
        
        tree.addNode(new SkillNode("ocean_affinity", "Ocean Affinity", "+20% damage underwater", 
            15, 2, List.of("swim_speed"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("tidal_surge", "Tidal Surge", "Wave knockback +3 blocks", 
            18, 2, List.of("water_jet"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("aqua_vision", "Aqua Vision", "See clearly underwater", 
            25, 3, List.of("ocean_affinity"), p -> {}, SkillTier.ADVANCED));
        
        tree.addNode(new SkillNode("riptide", "Riptide", "Dash through water (5s CD)", 
            30, 3, List.of("tidal_surge"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("pressure_resist", "Pressure Resist", "No mining penalty underwater", 
            35, 3, List.of("aqua_vision"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("whirlpool", "Whirlpool", "Pull enemies toward you in water", 
            40, 4, List.of("riptide"), p -> {}, SkillTier.EXPERT));
        
        tree.addNode(new SkillNode("sea_lord", "Sea Lord", "Command fish and dolphins", 
            45, 4, List.of("pressure_resist", "whirlpool"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("tsunami", "Tsunami", "Create massive wave (5 min CD)", 
            48, 5, List.of("sea_lord"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("ocean_heart", "Ocean Heart", "Regenerate health in water", 
            50, 5, List.of("tsunami"), p -> {}, SkillTier.MASTER));
        
        SKILL_TREES.put("tidecaller", tree);
    }
    
    // ========== STONEHEART SKILL TREE ==========
    private static void registerStoneheartTree() {
        SkillTree tree = new SkillTree(Identifier.fromNamespaceAndPath("veil_origins", "stoneheart"));
        
        tree.addNode(new SkillNode("stone_skin", "Stone Skin", "+2 natural armor", 
            1, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("miner_instinct", "Miner Instinct", "See ores through walls (8 blocks)", 
            5, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("earth_punch", "Earth Punch", "Unarmed damage +3", 
            10, 2, List.of("stone_skin"), p -> {}, SkillTier.BASIC));
        
        tree.addNode(new SkillNode("cave_dweller", "Cave Dweller", "+15% damage underground", 
            15, 2, List.of("miner_instinct"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("tremor_sense", "Tremor Sense", "Detect movement through ground", 
            18, 2, List.of("earth_punch"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("fortify", "Fortify", "Reduce knockback by 50%", 
            25, 3, List.of("cave_dweller"), p -> {}, SkillTier.ADVANCED));
        
        tree.addNode(new SkillNode("earthquake", "Earthquake", "Ground slam radius +3 blocks", 
            30, 3, List.of("tremor_sense"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("metal_affinity", "Metal Affinity", "+25% pickaxe efficiency", 
            35, 3, List.of("fortify"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("stone_form", "Stone Form", "Become invulnerable (5s, 2 min CD)", 
            40, 4, List.of("earthquake"), p -> {}, SkillTier.EXPERT));
        
        tree.addNode(new SkillNode("earth_lord", "Earth Lord", "+4 armor, immune to fall damage", 
            45, 4, List.of("metal_affinity", "stone_form"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("tectonic_shift", "Tectonic Shift", "Raise stone walls (3 min CD)", 
            48, 5, List.of("earth_lord"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("mountain_heart", "Mountain Heart", "Regenerate near stone", 
            50, 5, List.of("tectonic_shift"), p -> {}, SkillTier.MASTER));
        
        SKILL_TREES.put("stoneheart", tree);
    }
    
    // ========== SKYBORN SKILL TREE ==========
    private static void registerSkybornTree() {
        SkillTree tree = new SkillTree(Identifier.fromNamespaceAndPath("veil_origins", "skyborn"));
        
        tree.addNode(new SkillNode("light_bones", "Light Bones", "Fall damage reduced by 50%", 
            1, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("wind_rider", "Wind Rider", "Glide speed +20%", 
            5, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("gust", "Gust", "Wind push range +3 blocks", 
            10, 2, List.of("light_bones"), p -> {}, SkillTier.BASIC));
        
        tree.addNode(new SkillNode("sky_affinity", "Sky Affinity", "+15% damage above Y=100", 
            15, 2, List.of("wind_rider"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("updraft", "Updraft", "Double jump height +2 blocks", 
            18, 2, List.of("gust"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("air_dash", "Air Dash", "Dash mid-air (8s CD)", 
            25, 3, List.of("sky_affinity"), p -> {}, SkillTier.ADVANCED));
        
        tree.addNode(new SkillNode("cyclone", "Cyclone", "Create vortex pulling enemies", 
            30, 3, List.of("updraft"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("featherfall", "Featherfall", "No fall damage", 
            35, 3, List.of("air_dash"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("lightning_call", "Lightning Call", "Strike enemies with lightning", 
            40, 4, List.of("cyclone"), p -> {}, SkillTier.EXPERT));
        
        tree.addNode(new SkillNode("sky_lord", "Sky Lord", "Permanent slow fall, +30% air damage", 
            45, 4, List.of("featherfall", "lightning_call"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("storm_form", "Storm Form", "Become lightning (3 min CD)", 
            48, 5, List.of("sky_lord"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("wind_sovereign", "Wind Sovereign", "True flight for 30 seconds", 
            50, 5, List.of("storm_form"), p -> {}, SkillTier.MASTER));
        
        SKILL_TREES.put("skyborn", tree);
    }

    
    // ========== UMBRAKIN SKILL TREE ==========
    private static void registerUmbrakinTree() {
        SkillTree tree = new SkillTree(Identifier.fromNamespaceAndPath("veil_origins", "umbrakin"));
        
        tree.addNode(new SkillNode("shadow_step", "Shadow Step", "Move silently", 
            1, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("dark_vision", "Dark Vision", "See in complete darkness", 
            5, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("shadow_strike", "Shadow Strike", "+20% damage from stealth", 
            10, 2, List.of("shadow_step"), p -> {}, SkillTier.BASIC));
        
        tree.addNode(new SkillNode("night_affinity", "Night Affinity", "+15% damage at night", 
            15, 2, List.of("dark_vision"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("shadow_meld", "Shadow Meld", "Invisibility in darkness (10s)", 
            18, 2, List.of("shadow_strike"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("umbral_dodge", "Umbral Dodge", "10% chance to dodge attacks", 
            25, 3, List.of("night_affinity"), p -> {}, SkillTier.ADVANCED));
        
        tree.addNode(new SkillNode("shadow_clone", "Shadow Clone", "Create decoy (30s CD)", 
            30, 3, List.of("shadow_meld"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("darkness_aura", "Darkness Aura", "Dim light around you", 
            35, 3, List.of("umbral_dodge"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("shadow_teleport", "Shadow Teleport", "Teleport to shadows (15s CD)", 
            40, 4, List.of("shadow_clone"), p -> {}, SkillTier.EXPERT));
        
        tree.addNode(new SkillNode("shadow_lord", "Shadow Lord", "+50% damage at night", 
            45, 4, List.of("darkness_aura", "shadow_teleport"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("void_embrace", "Void Embrace", "Phase through walls (3 min CD)", 
            48, 5, List.of("shadow_lord"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("eternal_shadow", "Eternal Shadow", "Create permanent darkness zone", 
            50, 5, List.of("void_embrace"), p -> {}, SkillTier.MASTER));
        
        SKILL_TREES.put("umbrakin", tree);
    }
    
    // ========== DRYAD SKILL TREE ==========
    private static void registerDryadTree() {
        SkillTree tree = new SkillTree(Identifier.fromNamespaceAndPath("veil_origins", "dryad"));
        
        tree.addNode(new SkillNode("nature_bond", "Nature Bond", "Animals won't attack unprovoked", 
            1, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("photosynthesis", "Photosynthesis", "Regenerate in sunlight", 
            5, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("vine_whip", "Vine Whip", "Vine attack range +3 blocks", 
            10, 2, List.of("nature_bond"), p -> {}, SkillTier.BASIC));
        
        tree.addNode(new SkillNode("forest_affinity", "Forest Affinity", "+15% damage in forests", 
            15, 2, List.of("photosynthesis"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("root_snare", "Root Snare", "Entangle duration +3 seconds", 
            18, 2, List.of("vine_whip"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("bark_skin", "Bark Skin", "+2 armor near trees", 
            25, 3, List.of("forest_affinity"), p -> {}, SkillTier.ADVANCED));
        
        tree.addNode(new SkillNode("growth_burst", "Growth Burst", "Instantly grow crops nearby", 
            30, 3, List.of("root_snare"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("treant_form", "Treant Form", "Transform into tree (stealth)", 
            35, 3, List.of("bark_skin"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("poison_thorns", "Poison Thorns", "Attackers get poisoned", 
            40, 4, List.of("growth_burst"), p -> {}, SkillTier.EXPERT));
        
        tree.addNode(new SkillNode("nature_lord", "Nature Lord", "Command animals", 
            45, 4, List.of("treant_form", "poison_thorns"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("forest_wrath", "Forest Wrath", "Summon treant ally (5 min CD)", 
            48, 5, List.of("nature_lord"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("world_tree", "World Tree", "Create healing grove", 
            50, 5, List.of("forest_wrath"), p -> {}, SkillTier.MASTER));
        
        SKILL_TREES.put("dryad", tree);
    }
    
    // ========== CRYSTALLINE SKILL TREE ==========
    private static void registerCrystallineTree() {
        SkillTree tree = new SkillTree(Identifier.fromNamespaceAndPath("veil_origins", "crystalline"));
        
        tree.addNode(new SkillNode("crystal_skin", "Crystal Skin", "+1 armor, reflect 5% damage", 
            1, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("gem_sight", "Gem Sight", "See valuable ores (12 blocks)", 
            5, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("shard_shot", "Shard Shot", "Crystal projectile damage +20%", 
            10, 2, List.of("crystal_skin"), p -> {}, SkillTier.BASIC));
        
        tree.addNode(new SkillNode("prismatic_light", "Prismatic Light", "Emit light level 10", 
            15, 2, List.of("gem_sight"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("refraction", "Refraction", "15% chance to deflect projectiles", 
            18, 2, List.of("shard_shot"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("crystal_growth", "Crystal Growth", "Grow crystals for resources", 
            25, 3, List.of("prismatic_light"), p -> {}, SkillTier.ADVANCED));
        
        tree.addNode(new SkillNode("diamond_form", "Diamond Form", "+5 armor (30s, 2 min CD)", 
            30, 3, List.of("refraction"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("resonance", "Resonance", "Damage nearby enemies with sound", 
            35, 3, List.of("crystal_growth"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("prism_beam", "Prism Beam", "Fire concentrated light beam", 
            40, 4, List.of("diamond_form"), p -> {}, SkillTier.EXPERT));
        
        tree.addNode(new SkillNode("crystal_lord", "Crystal Lord", "Reflect 25% damage", 
            45, 4, List.of("resonance", "prism_beam"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("crystal_prison", "Crystal Prison", "Trap enemy in crystal (3 min CD)", 
            48, 5, List.of("crystal_lord"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("living_gem", "Living Gem", "Become pure crystal, immune to magic", 
            50, 5, List.of("crystal_prison"), p -> {}, SkillTier.MASTER));
        
        SKILL_TREES.put("crystalline", tree);
    }
    
    // ========== ETHEREAL SKILL TREE ==========
    private static void registerEtherealTree() {
        SkillTree tree = new SkillTree(Identifier.fromNamespaceAndPath("veil_origins", "ethereal"));
        
        tree.addNode(new SkillNode("spirit_form", "Spirit Form", "Phase through 1 block walls", 
            1, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("soul_sight", "Soul Sight", "See invisible entities", 
            5, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("ghost_touch", "Ghost Touch", "Attacks ignore 10% armor", 
            10, 2, List.of("spirit_form"), p -> {}, SkillTier.BASIC));
        
        tree.addNode(new SkillNode("ethereal_drift", "Ethereal Drift", "Slow fall always active", 
            15, 2, List.of("soul_sight"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("possession", "Possession", "Control mobs briefly (30s CD)", 
            18, 2, List.of("ghost_touch"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("spirit_walk", "Spirit Walk", "Phase through 2 block walls", 
            25, 3, List.of("ethereal_drift"), p -> {}, SkillTier.ADVANCED));
        
        tree.addNode(new SkillNode("haunt", "Haunt", "Fear nearby enemies (20s CD)", 
            30, 3, List.of("possession"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("ectoplasm", "Ectoplasm", "Leave damaging trail", 
            35, 3, List.of("spirit_walk"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("banshee_wail", "Banshee Wail", "Scream damages and stuns", 
            40, 4, List.of("haunt"), p -> {}, SkillTier.EXPERT));
        
        tree.addNode(new SkillNode("spirit_lord", "Spirit Lord", "Attacks ignore 30% armor", 
            45, 4, List.of("ectoplasm", "banshee_wail"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("astral_projection", "Astral Projection", "Scout as spirit (3 min CD)", 
            48, 5, List.of("spirit_lord"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("transcendence", "Transcendence", "Become fully ethereal (30s)", 
            50, 5, List.of("astral_projection"), p -> {}, SkillTier.MASTER));
        
        SKILL_TREES.put("ethereal", tree);
    }
    
    // ========== NECROMANCER SKILL TREE ==========
    private static void registerNecromancerTree() {
        SkillTree tree = new SkillTree(Identifier.fromNamespaceAndPath("veil_origins", "necromancer"));
        
        tree.addNode(new SkillNode("death_touch", "Death Touch", "Wither enemies on hit (2s)", 
            1, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("corpse_sense", "Corpse Sense", "Detect undead (20 blocks)", 
            5, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("raise_skeleton", "Raise Skeleton", "Summon 1 skeleton ally", 
            10, 2, List.of("death_touch"), p -> {}, SkillTier.BASIC));
        
        tree.addNode(new SkillNode("undead_affinity", "Undead Affinity", "Undead won't attack", 
            15, 2, List.of("corpse_sense"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("soul_harvest", "Soul Harvest", "Gain health from kills", 
            18, 2, List.of("raise_skeleton"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("bone_armor", "Bone Armor", "+3 armor from bone shield", 
            25, 3, List.of("undead_affinity"), p -> {}, SkillTier.ADVANCED));
        
        tree.addNode(new SkillNode("raise_zombie", "Raise Zombie", "Summon zombie horde (3)", 
            30, 3, List.of("soul_harvest"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("death_aura", "Death Aura", "Wither nearby enemies", 
            35, 3, List.of("bone_armor"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("life_drain", "Life Drain", "Steal health at range", 
            40, 4, List.of("raise_zombie"), p -> {}, SkillTier.EXPERT));
        
        tree.addNode(new SkillNode("death_lord", "Death Lord", "Command all undead", 
            45, 4, List.of("death_aura", "life_drain"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("lich_form", "Lich Form", "Transform into lich (3 min CD)", 
            48, 5, List.of("death_lord"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("army_of_dead", "Army of the Dead", "Raise massive undead army", 
            50, 5, List.of("lich_form"), p -> {}, SkillTier.MASTER));
        
        SKILL_TREES.put("necromancer", tree);
    }

    
    // ========== TECHNOMANCER SKILL TREE ==========
    private static void registerTechnomancerTree() {
        SkillTree tree = new SkillTree(Identifier.fromNamespaceAndPath("veil_origins", "technomancer"));
        
        tree.addNode(new SkillNode("circuit_mind", "Circuit Mind", "+10% XP gain", 
            1, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("machine_sense", "Machine Sense", "Detect redstone (15 blocks)", 
            5, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("shock_touch", "Shock Touch", "Melee attacks deal lightning", 
            10, 2, List.of("circuit_mind"), p -> {}, SkillTier.BASIC));
        
        tree.addNode(new SkillNode("overclock", "Overclock", "+20% attack speed", 
            15, 2, List.of("machine_sense"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("emp_pulse", "EMP Pulse", "Disable nearby redstone", 
            18, 2, List.of("shock_touch"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("energy_shield", "Energy Shield", "Absorb 20% damage as energy", 
            25, 3, List.of("overclock"), p -> {}, SkillTier.ADVANCED));
        
        tree.addNode(new SkillNode("drone_deploy", "Drone Deploy", "Summon attack drone", 
            30, 3, List.of("emp_pulse"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("power_surge", "Power Surge", "Boost nearby machines", 
            35, 3, List.of("energy_shield"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("tesla_coil", "Tesla Coil", "Chain lightning attack", 
            40, 4, List.of("drone_deploy"), p -> {}, SkillTier.EXPERT));
        
        tree.addNode(new SkillNode("tech_lord", "Tech Lord", "Control all machines nearby", 
            45, 4, List.of("power_surge", "tesla_coil"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("mech_suit", "Mech Suit", "Summon power armor (5 min CD)", 
            48, 5, List.of("tech_lord"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("singularity", "Singularity", "Create black hole (5 min CD)", 
            50, 5, List.of("mech_suit"), p -> {}, SkillTier.MASTER));
        
        SKILL_TREES.put("technomancer", tree);
    }
    
    // ========== STARBORNE SKILL TREE ==========
    private static void registerStarborneTree() {
        SkillTree tree = new SkillTree(Identifier.fromNamespaceAndPath("veil_origins", "starborne"));
        
        tree.addNode(new SkillNode("starlight", "Starlight", "Glow at night", 
            1, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("cosmic_sense", "Cosmic Sense", "See in darkness", 
            5, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("star_bolt", "Star Bolt", "Ranged light attack", 
            10, 2, List.of("starlight"), p -> {}, SkillTier.BASIC));
        
        tree.addNode(new SkillNode("night_power", "Night Power", "+20% damage at night", 
            15, 2, List.of("cosmic_sense"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("constellation", "Constellation", "Mark enemies (tracking)", 
            18, 2, List.of("star_bolt"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("gravity_well", "Gravity Well", "Pull enemies toward point", 
            25, 3, List.of("night_power"), p -> {}, SkillTier.ADVANCED));
        
        tree.addNode(new SkillNode("meteor_shower", "Meteor Shower", "Rain stars on area", 
            30, 3, List.of("constellation"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("stellar_shield", "Stellar Shield", "Absorb damage as starlight", 
            35, 3, List.of("gravity_well"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("supernova", "Supernova", "Massive explosion (3 min CD)", 
            40, 4, List.of("meteor_shower"), p -> {}, SkillTier.EXPERT));
        
        tree.addNode(new SkillNode("star_lord", "Star Lord", "+50% damage at night", 
            45, 4, List.of("stellar_shield", "supernova"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("black_hole", "Black Hole", "Create gravity singularity", 
            48, 5, List.of("star_lord"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("cosmic_form", "Cosmic Form", "Become living star (30s)", 
            50, 5, List.of("black_hole"), p -> {}, SkillTier.MASTER));
        
        SKILL_TREES.put("starborne", tree);
    }
    
    // ========== VOIDTOUCHED SKILL TREE ==========
    private static void registerVoidtouchedTree() {
        SkillTree tree = new SkillTree(Identifier.fromNamespaceAndPath("veil_origins", "voidtouched"));
        
        tree.addNode(new SkillNode("void_gaze", "Void Gaze", "See through End blocks", 
            1, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("ender_step", "Ender Step", "Short range teleport", 
            5, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("void_touch", "Void Touch", "Attacks deal void damage", 
            10, 2, List.of("void_gaze"), p -> {}, SkillTier.BASIC));
        
        tree.addNode(new SkillNode("end_affinity", "End Affinity", "+25% damage in End", 
            15, 2, List.of("ender_step"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("blink", "Blink", "Teleport range +10 blocks", 
            18, 2, List.of("void_touch"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("void_armor", "Void Armor", "+2 armor, resist void damage", 
            25, 3, List.of("end_affinity"), p -> {}, SkillTier.ADVANCED));
        
        tree.addNode(new SkillNode("ender_pearl_free", "Ender Affinity", "Free ender pearl use", 
            30, 3, List.of("blink"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("void_rift", "Void Rift", "Create damaging void zone", 
            35, 3, List.of("void_armor"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("dimension_shift", "Dimension Shift", "Phase between dimensions", 
            40, 4, List.of("ender_pearl_free"), p -> {}, SkillTier.EXPERT));
        
        tree.addNode(new SkillNode("void_lord", "Void Lord", "Immune to void damage", 
            45, 4, List.of("void_rift", "dimension_shift"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("ender_dragon_call", "Dragon Call", "Summon ender dragon ally", 
            48, 5, List.of("void_lord"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("void_sovereign", "Void Sovereign", "Control the End", 
            50, 5, List.of("ender_dragon_call"), p -> {}, SkillTier.MASTER));
        
        SKILL_TREES.put("voidtouched", tree);
    }
    
    // ========== RIFTWALKER SKILL TREE ==========
    private static void registerRiftwalkerTree() {
        SkillTree tree = new SkillTree(Identifier.fromNamespaceAndPath("veil_origins", "riftwalker"));
        
        tree.addNode(new SkillNode("rift_sense", "Rift Sense", "Detect portals (30 blocks)", 
            1, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("phase_step", "Phase Step", "Short teleport (5 blocks)", 
            5, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("rift_blade", "Rift Blade", "Attacks create small rifts", 
            10, 2, List.of("rift_sense"), p -> {}, SkillTier.BASIC));
        
        tree.addNode(new SkillNode("dimension_walker", "Dimension Walker", "No nausea from portals", 
            15, 2, List.of("phase_step"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("rift_jump", "Rift Jump", "Teleport range +15 blocks", 
            18, 2, List.of("rift_blade"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("pocket_dimension", "Pocket Dimension", "Store items in void", 
            25, 3, List.of("dimension_walker"), p -> {}, SkillTier.ADVANCED));
        
        tree.addNode(new SkillNode("rift_storm", "Rift Storm", "Create multiple rifts", 
            30, 3, List.of("rift_jump"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("anchor_point", "Anchor Point", "Set return teleport point", 
            35, 3, List.of("pocket_dimension"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("banish", "Banish", "Send enemy to void (1 min CD)", 
            40, 4, List.of("rift_storm"), p -> {}, SkillTier.EXPERT));
        
        tree.addNode(new SkillNode("rift_lord", "Rift Lord", "Instant portal creation", 
            45, 4, List.of("anchor_point", "banish"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("reality_tear", "Reality Tear", "Massive rift attack", 
            48, 5, List.of("rift_lord"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("multiverse", "Multiverse", "Exist in multiple places", 
            50, 5, List.of("reality_tear"), p -> {}, SkillTier.MASTER));
        
        SKILL_TREES.put("riftwalker", tree);
    }
    
    // ========== MYCOMORPH SKILL TREE ==========
    private static void registerMycomorphTree() {
        SkillTree tree = new SkillTree(Identifier.fromNamespaceAndPath("veil_origins", "mycomorph"));
        
        tree.addNode(new SkillNode("spore_cloud", "Spore Cloud", "Release poison spores", 
            1, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("fungal_sight", "Fungal Sight", "See in mushroom biomes", 
            5, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("toxic_touch", "Toxic Touch", "Poison on melee hit", 
            10, 2, List.of("spore_cloud"), p -> {}, SkillTier.BASIC));
        
        tree.addNode(new SkillNode("mushroom_affinity", "Mushroom Affinity", "+20% in mushroom biomes", 
            15, 2, List.of("fungal_sight"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("decompose", "Decompose", "Break down organic matter", 
            18, 2, List.of("toxic_touch"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("mycelium_network", "Mycelium Network", "Sense through fungi", 
            25, 3, List.of("mushroom_affinity"), p -> {}, SkillTier.ADVANCED));
        
        tree.addNode(new SkillNode("fungal_growth", "Fungal Growth", "Spread mushrooms rapidly", 
            30, 3, List.of("decompose"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("hallucinogen", "Hallucinogen", "Confuse enemies", 
            35, 3, List.of("mycelium_network"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("symbiosis", "Symbiosis", "Heal from mushrooms", 
            40, 4, List.of("fungal_growth"), p -> {}, SkillTier.EXPERT));
        
        tree.addNode(new SkillNode("fungal_lord", "Fungal Lord", "Control all fungi", 
            45, 4, List.of("hallucinogen", "symbiosis"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("spore_storm", "Spore Storm", "Massive poison cloud", 
            48, 5, List.of("fungal_lord"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("hive_mind", "Hive Mind", "Connect all nearby fungi", 
            50, 5, List.of("spore_storm"), p -> {}, SkillTier.MASTER));
        
        SKILL_TREES.put("mycomorph", tree);
    }
    
    // ========== BERSERKER SKILL TREE ==========
    private static void registerBerserkerTree() {
        SkillTree tree = new SkillTree(Identifier.fromNamespaceAndPath("veil_origins", "berserker"));
        
        tree.addNode(new SkillNode("rage_build", "Rage Build", "Build rage faster", 
            1, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("battle_sense", "Battle Sense", "Detect hostile mobs", 
            5, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("heavy_blow", "Heavy Blow", "+15% melee damage", 
            10, 2, List.of("rage_build"), p -> {}, SkillTier.BASIC));
        
        tree.addNode(new SkillNode("bloodlust", "Bloodlust", "Heal on kill", 
            15, 2, List.of("battle_sense"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("unstoppable", "Unstoppable", "Immune to knockback in rage", 
            18, 2, List.of("heavy_blow"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("war_cry", "War Cry", "Fear nearby enemies", 
            25, 3, List.of("bloodlust"), p -> {}, SkillTier.ADVANCED));
        
        tree.addNode(new SkillNode("frenzy", "Frenzy", "+50% attack speed in rage", 
            30, 3, List.of("unstoppable"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("thick_skin", "Thick Skin", "+3 armor when raging", 
            35, 3, List.of("war_cry"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("execute", "Execute", "+100% damage to low health enemies", 
            40, 4, List.of("frenzy"), p -> {}, SkillTier.EXPERT));
        
        tree.addNode(new SkillNode("war_lord", "War Lord", "Rage never depletes in combat", 
            45, 4, List.of("thick_skin", "execute"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("rampage", "Rampage", "Each kill extends rage", 
            48, 5, List.of("war_lord"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("avatar_of_war", "Avatar of War", "Ultimate rage form", 
            50, 5, List.of("rampage"), p -> {}, SkillTier.MASTER));
        
        SKILL_TREES.put("berserker", tree);
    }
    
    // ========== CHRONOSHIFT SKILL TREE ==========
    private static void registerChronoshiftTree() {
        SkillTree tree = new SkillTree(Identifier.fromNamespaceAndPath("veil_origins", "chronoshift"));
        
        tree.addNode(new SkillNode("time_sense", "Time Sense", "See entity movement trails", 
            1, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("quick_step", "Quick Step", "+10% movement speed", 
            5, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("temporal_strike", "Temporal Strike", "Attacks slow enemies", 
            10, 2, List.of("time_sense"), p -> {}, SkillTier.BASIC));
        
        tree.addNode(new SkillNode("haste", "Haste", "+20% action speed", 
            15, 2, List.of("quick_step"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("rewind", "Rewind", "Return to position 5s ago", 
            18, 2, List.of("temporal_strike"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("time_dilation", "Time Dilation", "Slow nearby enemies", 
            25, 3, List.of("haste"), p -> {}, SkillTier.ADVANCED));
        
        tree.addNode(new SkillNode("temporal_dodge", "Temporal Dodge", "Phase through attacks", 
            30, 3, List.of("rewind"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("age_acceleration", "Age Acceleration", "Rapidly age enemies", 
            35, 3, List.of("time_dilation"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("time_stop", "Time Stop", "Freeze time briefly (3 min CD)", 
            40, 4, List.of("temporal_dodge"), p -> {}, SkillTier.EXPERT));
        
        tree.addNode(new SkillNode("time_lord", "Time Lord", "All cooldowns -30%", 
            45, 4, List.of("age_acceleration", "time_stop"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("temporal_clone", "Temporal Clone", "Create past self ally", 
            48, 5, List.of("time_lord"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("master_of_time", "Master of Time", "Control time at will", 
            50, 5, List.of("temporal_clone"), p -> {}, SkillTier.MASTER));
        
        SKILL_TREES.put("chronoshift", tree);
    }
    
    // ========== SOULBOUND SKILL TREE ==========
    private static void registerSoulboundTree() {
        SkillTree tree = new SkillTree(Identifier.fromNamespaceAndPath("veil_origins", "soulbound"));
        
        tree.addNode(new SkillNode("soul_link", "Soul Link", "Bond with one player", 
            1, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("empathy", "Empathy", "Sense bonded player's health", 
            5, 1, null, p -> {}, SkillTier.BASIC));
        tree.addNode(new SkillNode("shared_strength", "Shared Strength", "+10% damage near bond", 
            10, 2, List.of("soul_link"), p -> {}, SkillTier.BASIC));
        
        tree.addNode(new SkillNode("life_link", "Life Link", "Share damage with bond", 
            15, 2, List.of("empathy"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("soul_call", "Soul Call", "Teleport to bonded player", 
            18, 2, List.of("shared_strength"), p -> {}, SkillTier.ADVANCED));
        tree.addNode(new SkillNode("dual_power", "Dual Power", "+25% stats when together", 
            25, 3, List.of("life_link"), p -> {}, SkillTier.ADVANCED));
        
        tree.addNode(new SkillNode("soul_shield", "Soul Shield", "Protect bonded player", 
            30, 3, List.of("soul_call"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("shared_vision", "Shared Vision", "See through bond's eyes", 
            35, 3, List.of("dual_power"), p -> {}, SkillTier.EXPERT));
        tree.addNode(new SkillNode("resurrection", "Resurrection", "Revive bonded player", 
            40, 4, List.of("soul_shield"), p -> {}, SkillTier.EXPERT));
        
        tree.addNode(new SkillNode("soul_lord", "Soul Lord", "Bond with 2 players", 
            45, 4, List.of("shared_vision", "resurrection"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("soul_fusion", "Soul Fusion", "Merge powers temporarily", 
            48, 5, List.of("soul_lord"), p -> {}, SkillTier.MASTER));
        tree.addNode(new SkillNode("eternal_bond", "Eternal Bond", "Unbreakable soul connection", 
            50, 5, List.of("soul_fusion"), p -> {}, SkillTier.MASTER));
        
        SKILL_TREES.put("soulbound", tree);
    }
}
