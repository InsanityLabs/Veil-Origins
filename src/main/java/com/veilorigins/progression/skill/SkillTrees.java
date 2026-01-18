package com.veilorigins.progression.skill;

import com.veilorigins.VeilOrigins;
import com.veilorigins.progression.skill.Skill.SkillBranch;
import com.veilorigins.progression.skill.Skill.SkillTier;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry of all skill trees for each origin.
 * Each origin has 25-35 skills with meaningful effects, mutually exclusive branches,
 * hidden paths, synergy bonuses, and forbidden/ascended ultimate abilities.
 */
public class SkillTrees {
    
    private static final Map<String, SkillTreeData> TREES = new HashMap<>();
    private static boolean initialized = false;
    
    public static synchronized void initialize() {
        if (initialized) {
            VeilOrigins.LOGGER.debug("SkillTrees already initialized, skipping");
            return;
        }
        
        VeilOrigins.LOGGER.info("SkillTrees: Initializing skill trees...");
        
        registerVampireTree();
        registerWerewolfTree();
        registerCindersoulTree();
        registerFrostbornTree();
        registerVoidtouchedTree();
        registerVamplingTree();
        registerWolflingTree();
        registerDryadTree();
        registerNecromancerTree();
        registerStoneheartTree();
        registerSkybornTree();
        registerTidecallerTree();
        registerStarborneTree();
        registerVeilbornTree();
        registerEtherealTree();
        registerCrystallineTree();
        registerMycomorphTree();
        registerUmbrakinTree();
        registerRiftwalkerTree();
        registerTechnomancerTree();
        registerEmberlingTree();
        registerFeralkinTree();
        
        initialized = true;
        VeilOrigins.LOGGER.info("SkillTrees: Initialized {} skill trees", TREES.size());
        
        // Log each tree for verification
        for (Map.Entry<String, SkillTreeData> entry : TREES.entrySet()) {
            VeilOrigins.LOGGER.debug("  - {} has {} skills", entry.getKey(), entry.getValue().getAllSkills().size());
        }
    }
    
    public static SkillTreeData getTree(String originId) {
        String id = originId.contains(":") ? originId.split(":")[1] : originId;
        SkillTreeData tree = TREES.get(id);
        if (tree == null && !initialized) {
            VeilOrigins.LOGGER.error("SkillTrees.getTree called before initialization! Origin: {}", originId);
        }
        return tree;
    }
    
    // ==================== VAMPIRE SKILL TREE ====================
    // Complex tree with Blood vs Shadow paths, Forbidden blood magic, Ascended daywalker
    private static void registerVampireTree() {
        SkillTreeData tree = new SkillTreeData("vampire", "Vampire");
        
        // === TIER 1 - Core Foundation (3 skills) ===
        tree.addSkill(Skill.builder("vamp_vitality")
            .name("Vampiric Vitality").description("Your undead body is more resilient.")
            .icon("♥").tier(SkillTier.TIER_1).branch(SkillBranch.CORE)
            .position(0, 0).cost(1)
            .effect(SkillEffect.healthBoost(2))
            .build());
        
        tree.addSkill(Skill.builder("vamp_agility")
            .name("Predator's Grace").description("Move with supernatural speed.")
            .icon("»").tier(SkillTier.TIER_1).branch(SkillBranch.CORE)
            .position(2, 0).cost(1)
            .effect(SkillEffect.speedBoost(5))
            .build());
        
        tree.addSkill(Skill.builder("vamp_hunger")
            .name("Insatiable Hunger").description("Blood sustains you longer.")
            .icon("☾").tier(SkillTier.TIER_1).branch(SkillBranch.CORE)
            .position(4, 0).cost(1)
            .effect(SkillEffect.resourceRegen(10))
            .build());
        
        // === TIER 2 - Primary Branches (5 skills) ===
        tree.addSkill(Skill.builder("vamp_fangs")
            .name("Razor Fangs").description("Your bite deals more damage.")
            .icon("⚔").tier(SkillTier.TIER_2).branch(SkillBranch.OFFENSE)
            .position(-1, 1).cost(2)
            .requires("vamp_vitality")
            .effect(SkillEffect.damageBoost(10))
            .build());
        
        tree.addSkill(Skill.builder("vamp_resilience")
            .name("Undead Resilience").description("Harder to kill, harder to hurt.")
            .icon("⛨").tier(SkillTier.TIER_2).branch(SkillBranch.DEFENSE)
            .position(1, 1).cost(2)
            .requires("vamp_vitality")
            .effect(SkillEffect.armorBoost(2))
            .effect(SkillEffect.healthBoost(1))
            .build());
        
        tree.addSkill(Skill.builder("vamp_swiftness")
            .name("Bat's Swiftness").description("Enhanced movement and reflexes.")
            .icon("☽").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY)
            .position(3, 1).cost(2)
            .requires("vamp_agility")
            .effect(SkillEffect.speedBoost(8))
            .effect(SkillEffect.cooldownReduction(5))
            .build());
        
        tree.addSkill(Skill.builder("vamp_senses")
            .name("Predator Senses").description("Sense the living nearby.")
            .icon("★").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY)
            .position(5, 1).cost(2)
            .requires("vamp_hunger")
            .effect(SkillEffect.special("life_sense", "Detect living entities within 20 blocks"))
            .build());
        
        tree.addSkill(Skill.builder("vamp_charm")
            .name("Mesmerize").description("Your gaze weakens the will of mortals.")
            .icon("◐").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY)
            .position(4, 1).cost(2)
            .requires("vamp_hunger")
            .effect(SkillEffect.special("charm", "Briefly stun humanoids on eye contact"))
            .build());

        
        // === TIER 3 - Elemental Paths (MUTUALLY EXCLUSIVE) ===
        // BLOOD PATH (Elemental A) - Raw power, lifesteal, berserker
        tree.addSkill(Skill.builder("vamp_blood_frenzy")
            .name("Blood Frenzy").description("Enter a bloodthirsty rage. +25% damage but -10% defense.")
            .icon("☠").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A)
            .position(-2, 2).cost(3)
            .requires("vamp_fangs")
            .excludes("vamp_shadow_stalker", "vamp_shadow_cloak")
            .effect(SkillEffect.damageBoost(25))
            .effect(SkillEffect.armorBoost(-2))
            .effect(SkillEffect.special("blood_frenzy", "Attack speed increases as health drops"))
            .build());
        
        tree.addSkill(Skill.builder("vamp_blood_drain")
            .name("Sanguine Drain").description("Drain life with every strike.")
            .icon("♦").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A)
            .position(-1, 2).cost(3)
            .requires("vamp_fangs")
            .excludes("vamp_shadow_strike")
            .effect(SkillEffect.special("lifesteal", "10% of damage dealt heals you"))
            .effect(SkillEffect.damageBoost(8))
            .build());
        
        // SHADOW PATH (Elemental B) - Stealth, assassination, evasion
        tree.addSkill(Skill.builder("vamp_shadow_stalker")
            .name("Shadow Stalker").description("Strike from darkness. Bonus damage from stealth.")
            .icon("◐").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B)
            .position(0, 2).cost(3)
            .requires("vamp_fangs")
            .excludes("vamp_blood_frenzy", "vamp_blood_drain")
            .effect(SkillEffect.damageBoost(15))
            .effect(SkillEffect.special("stealth_damage", "+50% damage from invisibility"))
            .build());
        
        tree.addSkill(Skill.builder("vamp_shadow_cloak")
            .name("Shadow Cloak").description("Blend into darkness, becoming harder to detect.")
            .icon("◑").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B)
            .position(1, 2).cost(3)
            .requires("vamp_resilience")
            .excludes("vamp_blood_frenzy")
            .effect(SkillEffect.special("shadow_invis", "Invisible in low light"))
            .effect(SkillEffect.speedBoost(5))
            .build());
        
        // DEFENSE SPECIALIZATIONS
        tree.addSkill(Skill.builder("vamp_immortal_flesh")
            .name("Immortal Flesh").description("Your body refuses to die. Massive health boost.")
            .icon("♥").tier(SkillTier.TIER_3).branch(SkillBranch.DEFENSE)
            .position(2, 2).cost(3)
            .requires("vamp_resilience")
            .excludes("vamp_rapid_regen")
            .effect(SkillEffect.healthBoost(5))
            .effect(SkillEffect.knockbackResistance(20))
            .build());
        
        tree.addSkill(Skill.builder("vamp_rapid_regen")
            .name("Rapid Regeneration").description("Heal quickly from blood consumption.")
            .icon("✚").tier(SkillTier.TIER_3).branch(SkillBranch.DEFENSE)
            .position(3, 2).cost(3)
            .requires("vamp_resilience")
            .excludes("vamp_immortal_flesh")
            .effect(SkillEffect.special("regen_boost", "+100% healing from blood"))
            .effect(SkillEffect.resourceRegen(25))
            .build());
        
        // UTILITY SPECIALIZATIONS
        tree.addSkill(Skill.builder("vamp_night_hunter")
            .name("Night Hunter").description("Thrive in darkness. All stats boosted at night.")
            .icon("☾").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY)
            .position(4, 2).cost(3)
            .requires("vamp_swiftness")
            .effect(SkillEffect.special("night_bonus", "+15% all stats at night"))
            .build());
        
        tree.addSkill(Skill.builder("vamp_bat_form")
            .name("Bat Transformation").description("Transform into a bat for swift travel.")
            .icon("≋").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY)
            .position(5, 2).cost(3)
            .requires("vamp_senses")
            .effect(SkillEffect.special("bat_form", "Transform into bat - fast flight, fragile"))
            .effect(SkillEffect.cooldownReduction(10))
            .build());
        
        // === TIER 4 - Advanced Specialization ===
        tree.addSkill(Skill.builder("vamp_blood_lord")
            .name("Blood Lord").description("Master of blood magic. Devastating attacks.")
            .icon("♛").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE)
            .position(-2, 3).cost(4)
            .requires("vamp_blood_frenzy")
            .effect(SkillEffect.damageBoost(20))
            .effect(SkillEffect.attackSpeed(15))
            .effect(SkillEffect.special("blood_explosion", "Kills cause blood explosions"))
            .build());
        
        tree.addSkill(Skill.builder("vamp_crimson_tide")
            .name("Crimson Tide").description("Your blood attacks hit multiple targets.")
            .icon("≋").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE)
            .position(-1, 3).cost(4)
            .requires("vamp_blood_drain")
            .effect(SkillEffect.special("aoe_lifesteal", "Lifesteal affects nearby enemies"))
            .effect(SkillEffect.damageBoost(12))
            .build());
        
        tree.addSkill(Skill.builder("vamp_shadow_master")
            .name("Shadow Master").description("Command the shadows themselves.")
            .icon("◐").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE)
            .position(0, 3).cost(4)
            .requires("vamp_shadow_stalker")
            .effect(SkillEffect.damageBoost(18))
            .effect(SkillEffect.special("shadow_step", "Teleport to shadows"))
            .build());
        
        tree.addSkill(Skill.builder("vamp_ancient_one")
            .name("Ancient One").description("Centuries of existence grant immense power.")
            .icon("⚜").tier(SkillTier.TIER_4).branch(SkillBranch.DEFENSE)
            .position(2, 3).cost(4)
            .requires("vamp_immortal_flesh")
            .effect(SkillEffect.healthBoost(4))
            .effect(SkillEffect.armorBoost(4))
            .effect(SkillEffect.armorToughnessBoost(2))
            .build());
        
        tree.addSkill(Skill.builder("vamp_mist_form")
            .name("Mist Form Mastery").description("Transform into invulnerable mist.")
            .icon("≋").tier(SkillTier.TIER_4).branch(SkillBranch.UTILITY)
            .position(4, 3).cost(4)
            .requires("vamp_night_hunter")
            .effect(SkillEffect.cooldownReduction(20))
            .effect(SkillEffect.speedBoost(10))
            .effect(SkillEffect.special("mist_form", "Become invulnerable mist"))
            .build());

        
        // === HYBRID BRANCH - Mix of offense and defense ===
        tree.addSkill(Skill.builder("vamp_hybrid_predator")
            .name("Apex Predator").description("Balance of power and survival. Jack of all trades.")
            .icon("◈").tier(SkillTier.TIER_4).branch(SkillBranch.HYBRID)
            .position(1, 3).cost(4)
            .requires("vamp_shadow_cloak", "vamp_rapid_regen")
            .effect(SkillEffect.damageBoost(10))
            .effect(SkillEffect.healthBoost(2))
            .effect(SkillEffect.speedBoost(8))
            .effect(SkillEffect.special("adaptive", "Gain bonuses based on combat situation"))
            .build());
        
        // === SPECIALIST BRANCH - Extreme focus ===
        tree.addSkill(Skill.builder("vamp_specialist_assassin")
            .name("Perfect Assassin").description("One strike, one kill. Extreme damage, no defense.")
            .icon("☠").tier(SkillTier.TIER_4).branch(SkillBranch.SPECIALIST)
            .position(3, 3).cost(4)
            .requires("vamp_shadow_stalker", "vamp_night_hunter")
            .excludes("vamp_ancient_one", "vamp_hybrid_predator")
            .effect(SkillEffect.damageBoost(40))
            .effect(SkillEffect.armorBoost(-4))
            .effect(SkillEffect.healthBoost(-2))
            .effect(SkillEffect.special("execute", "Instant kill below 15% health"))
            .build());
        
        // === SYNERGY BRANCHES - Require multiple paths ===
        tree.addSkill(Skill.builder("vamp_synergy_bloodshadow")
            .name("Blood Shadow").description("Merge blood and shadow magic.")
            .icon("◉").tier(SkillTier.TIER_4).branch(SkillBranch.SYNERGY_AD)
            .position(-1, 4).cost(4)
            .requires("vamp_blood_drain", "vamp_shadow_cloak")
            .effect(SkillEffect.damageBoost(15))
            .effect(SkillEffect.special("shadow_lifesteal", "Lifesteal while invisible"))
            .effect(SkillEffect.special("blood_shadow", "Leave damaging shadow clones"))
            .build());
        
        // === TIER 5 - Ultimate Skills ===
        tree.addSkill(Skill.builder("vamp_nosferatu")
            .name("Nosferatu").description("Become the ultimate vampire. All abilities enhanced.")
            .icon("☬").tier(SkillTier.TIER_5).branch(SkillBranch.CORE)
            .position(1, 5).cost(5)
            .requires("vamp_blood_lord", "vamp_ancient_one")
            .effect(SkillEffect.damageBoost(15))
            .effect(SkillEffect.healthBoost(3))
            .effect(SkillEffect.speedBoost(10))
            .effect(SkillEffect.cooldownReduction(15))
            .effect(SkillEffect.special("nosferatu", "All vampire abilities enhanced"))
            .build());
        
        // === FORBIDDEN BRANCH - Dark power at great cost ===
        tree.addSkill(Skill.builder("vamp_forbidden_bloodpact")
            .name("Blood Pact").description("Sacrifice your humanity for power. Permanent sun weakness.")
            .icon("☠").tier(SkillTier.TIER_5).branch(SkillBranch.FORBIDDEN)
            .position(-2, 5).cost(5)
            .requires("vamp_blood_lord", "vamp_crimson_tide")
            .excludes("vamp_ascended_daywalker")
            .effect(SkillEffect.damageBoost(35))
            .effect(SkillEffect.special("lifesteal", "+25% lifesteal"))
            .effect(SkillEffect.special("sun_curse", "Take 3x sun damage permanently"))
            .effect(SkillEffect.special("blood_pact", "Kill to heal, starve without blood"))
            .build());
        
        tree.addSkill(Skill.builder("vamp_forbidden_bloodgod")
            .name("Blood God Avatar").description("Channel the Blood God. Devastating but corrupting.")
            .icon("♛").tier(SkillTier.TIER_5).branch(SkillBranch.FORBIDDEN)
            .position(-1, 5).cost(5)
            .requires("vamp_forbidden_bloodpact")
            .effect(SkillEffect.damageBoost(50))
            .effect(SkillEffect.healthBoost(5))
            .effect(SkillEffect.special("blood_god", "Transform into Blood God form"))
            .effect(SkillEffect.special("corruption", "Slowly lose sanity, random aggression"))
            .build());
        
        // === ASCENDED BRANCH - Transcend vampire weaknesses ===
        tree.addSkill(Skill.builder("vamp_ascended_daywalker")
            .name("Daywalker").description("Overcome the sun's curse. Walk in daylight.")
            .icon("☀").tier(SkillTier.TIER_5).branch(SkillBranch.ASCENDED)
            .position(3, 5).cost(5)
            .requires("vamp_ancient_one", "vamp_mist_form")
            .excludes("vamp_forbidden_bloodpact")
            .effect(SkillEffect.special("sun_immune", "Immune to sun damage"))
            .effect(SkillEffect.healthBoost(2))
            .effect(SkillEffect.special("daywalker", "Full power in daylight"))
            .build());
        
        tree.addSkill(Skill.builder("vamp_ascended_progenitor")
            .name("Progenitor").description("Become a vampire progenitor. Create thralls.")
            .icon("⚜").tier(SkillTier.TIER_5).branch(SkillBranch.ASCENDED)
            .position(4, 5).cost(5)
            .requires("vamp_ascended_daywalker")
            .effect(SkillEffect.damageBoost(20))
            .effect(SkillEffect.healthBoost(4))
            .effect(SkillEffect.special("progenitor", "Convert villagers to vampire thralls"))
            .effect(SkillEffect.special("bloodline", "Thralls provide passive blood"))
            .build());
        
        TREES.put("vampire", tree);
    }

    
    // ==================== WEREWOLF SKILL TREE ====================
    // Berserker vs Pack Leader, Forbidden Feral, Ascended Alpha
    private static void registerWerewolfTree() {
        SkillTreeData tree = new SkillTreeData("werewolf", "Werewolf");
        
        // TIER 1 - Core
        tree.addSkill(Skill.builder("wolf_strength")
            .name("Primal Strength").description("Raw physical power.")
            .icon("⚔").tier(SkillTier.TIER_1).branch(SkillBranch.CORE)
            .position(0, 0).cost(1)
            .effect(SkillEffect.damageBoost(5))
            .build());
        
        tree.addSkill(Skill.builder("wolf_endurance")
            .name("Beast's Endurance").description("Tougher than any human.")
            .icon("♥").tier(SkillTier.TIER_1).branch(SkillBranch.CORE)
            .position(2, 0).cost(1)
            .effect(SkillEffect.healthBoost(2))
            .build());
        
        tree.addSkill(Skill.builder("wolf_speed")
            .name("Hunter's Sprint").description("Chase down any prey.")
            .icon("»").tier(SkillTier.TIER_1).branch(SkillBranch.CORE)
            .position(4, 0).cost(1)
            .effect(SkillEffect.speedBoost(8))
            .build());
        
        // TIER 2 - Branches
        tree.addSkill(Skill.builder("wolf_savage")
            .name("Savage Strikes").description("Brutal, devastating attacks.")
            .icon("☠").tier(SkillTier.TIER_2).branch(SkillBranch.OFFENSE)
            .position(-1, 1).cost(2)
            .requires("wolf_strength")
            .effect(SkillEffect.damageBoost(12))
            .effect(SkillEffect.attackSpeed(5))
            .build());
        
        tree.addSkill(Skill.builder("wolf_thick_hide")
            .name("Thick Hide").description("Fur as tough as armor.")
            .icon("⛨").tier(SkillTier.TIER_2).branch(SkillBranch.DEFENSE)
            .position(1, 1).cost(2)
            .requires("wolf_endurance")
            .effect(SkillEffect.armorBoost(3))
            .effect(SkillEffect.healthBoost(1))
            .build());
        
        tree.addSkill(Skill.builder("wolf_pack_tactics")
            .name("Pack Tactics").description("Bonus when allies are near.")
            .icon("☽").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY)
            .position(3, 1).cost(2)
            .requires("wolf_speed")
            .effect(SkillEffect.special("pack_bonus", "+10% damage per nearby ally"))
            .build());
        
        tree.addSkill(Skill.builder("wolf_scent")
            .name("Keen Scent").description("Track prey by smell.")
            .icon("★").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY)
            .position(5, 1).cost(2)
            .requires("wolf_speed")
            .effect(SkillEffect.special("tracking", "See entity trails, detect hidden enemies"))
            .build());
        
        // TIER 3 - Elemental Paths (MUTUALLY EXCLUSIVE)
        // BERSERKER PATH (Elemental A)
        tree.addSkill(Skill.builder("wolf_berserker")
            .name("Berserker Rage").description("Lose control, gain power. +40% damage, -20% defense.")
            .icon("⚡").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A)
            .position(-2, 2).cost(3)
            .requires("wolf_savage")
            .excludes("wolf_stalker", "wolf_pack_alpha")
            .effect(SkillEffect.damageBoost(40))
            .effect(SkillEffect.armorBoost(-4))
            .effect(SkillEffect.special("berserker", "Damage increases as health drops"))
            .build());
        
        tree.addSkill(Skill.builder("wolf_bloodlust")
            .name("Bloodlust").description("Each kill fuels your rage.")
            .icon("☠").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A)
            .position(-1, 2).cost(3)
            .requires("wolf_savage")
            .excludes("wolf_pack_howl")
            .effect(SkillEffect.special("bloodlust", "+5% damage per kill, stacks 10x"))
            .effect(SkillEffect.attackSpeed(10))
            .build());
        
        // PACK LEADER PATH (Elemental B)
        tree.addSkill(Skill.builder("wolf_stalker")
            .name("Silent Stalker").description("Patient hunter. Bonus damage on first strike.")
            .icon("◐").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B)
            .position(0, 2).cost(3)
            .requires("wolf_savage")
            .excludes("wolf_berserker", "wolf_bloodlust")
            .effect(SkillEffect.damageBoost(15))
            .effect(SkillEffect.special("ambush", "+100% damage on first hit"))
            .build());
        
        tree.addSkill(Skill.builder("wolf_pack_alpha")
            .name("Pack Alpha").description("Lead wolves into battle.")
            .icon("♛").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B)
            .position(1, 2).cost(3)
            .requires("wolf_pack_tactics")
            .excludes("wolf_berserker")
            .effect(SkillEffect.special("summon_wolves", "Summon 2 wolf companions"))
            .effect(SkillEffect.damageBoost(8))
            .build());
        
        // Defense paths
        tree.addSkill(Skill.builder("wolf_iron_fur")
            .name("Iron Fur").description("Nearly impenetrable defense.")
            .icon("♦").tier(SkillTier.TIER_3).branch(SkillBranch.DEFENSE)
            .position(2, 2).cost(3)
            .requires("wolf_thick_hide")
            .excludes("wolf_rapid_heal")
            .effect(SkillEffect.armorBoost(5))
            .effect(SkillEffect.knockbackResistance(30))
            .build());
        
        tree.addSkill(Skill.builder("wolf_rapid_heal")
            .name("Rapid Healing").description("Wounds close almost instantly.")
            .icon("✚").tier(SkillTier.TIER_3).branch(SkillBranch.DEFENSE)
            .position(3, 2).cost(3)
            .requires("wolf_thick_hide")
            .excludes("wolf_iron_fur")
            .effect(SkillEffect.special("regen", "Regenerate 1 heart every 5 seconds"))
            .effect(SkillEffect.healthBoost(2))
            .build());
        
        tree.addSkill(Skill.builder("wolf_pack_howl")
            .name("Howl Mastery").description("Your howl inspires and terrifies.")
            .icon("☾").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY)
            .position(4, 2).cost(3)
            .requires("wolf_pack_tactics")
            .excludes("wolf_bloodlust")
            .effect(SkillEffect.cooldownReduction(15))
            .effect(SkillEffect.special("howl_range", "+50% howl effect range"))
            .effect(SkillEffect.special("fear_howl", "Howl causes fear in enemies"))
            .build());
        
        tree.addSkill(Skill.builder("wolf_lunar_sense")
            .name("Lunar Attunement").description("Draw power from the moon.")
            .icon("☾").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY)
            .position(5, 2).cost(3)
            .requires("wolf_scent")
            .effect(SkillEffect.special("lunar", "+30% all stats during full moon"))
            .effect(SkillEffect.resourceRegen(20))
            .build());

        
        // TIER 4 - Advanced
        tree.addSkill(Skill.builder("wolf_alpha")
            .name("Alpha Wolf").description("Lead the pack with overwhelming power.")
            .icon("♛").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE)
            .position(-2, 3).cost(4)
            .requires("wolf_berserker")
            .effect(SkillEffect.damageBoost(25))
            .effect(SkillEffect.attackSpeed(20))
            .build());
        
        tree.addSkill(Skill.builder("wolf_blood_moon")
            .name("Blood Moon Fury").description("Channel the blood moon's power.")
            .icon("☾").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE)
            .position(-1, 3).cost(4)
            .requires("wolf_bloodlust")
            .effect(SkillEffect.damageBoost(20))
            .effect(SkillEffect.special("blood_moon", "Permanent full moon bonuses"))
            .build());
        
        tree.addSkill(Skill.builder("wolf_pack_lord")
            .name("Pack Lord").description("Command a larger pack.")
            .icon("⚜").tier(SkillTier.TIER_4).branch(SkillBranch.UTILITY)
            .position(1, 3).cost(4)
            .requires("wolf_pack_alpha")
            .effect(SkillEffect.special("summon_wolves", "+2 wolf companions"))
            .effect(SkillEffect.special("wolf_buff", "Wolves deal +50% damage"))
            .build());
        
        tree.addSkill(Skill.builder("wolf_dire")
            .name("Dire Wolf").description("Massive and unstoppable.")
            .icon("⚜").tier(SkillTier.TIER_4).branch(SkillBranch.DEFENSE)
            .position(2, 3).cost(4)
            .requires("wolf_iron_fur")
            .effect(SkillEffect.healthBoost(5))
            .effect(SkillEffect.armorBoost(3))
            .effect(SkillEffect.special("size_increase", "+20% size"))
            .build());
        
        tree.addSkill(Skill.builder("wolf_lunar_master")
            .name("Lunar Master").description("The moon obeys your will.")
            .icon("☾").tier(SkillTier.TIER_4).branch(SkillBranch.UTILITY)
            .position(4, 3).cost(4)
            .requires("wolf_lunar_sense")
            .effect(SkillEffect.special("lunar_control", "Force full moon effects"))
            .effect(SkillEffect.cooldownReduction(20))
            .build());
        
        // HYBRID - Balanced fighter
        tree.addSkill(Skill.builder("wolf_hybrid_hunter")
            .name("Perfect Hunter").description("Balance of power and cunning.")
            .icon("◈").tier(SkillTier.TIER_4).branch(SkillBranch.HYBRID)
            .position(0, 3).cost(4)
            .requires("wolf_stalker", "wolf_rapid_heal")
            .effect(SkillEffect.damageBoost(15))
            .effect(SkillEffect.healthBoost(2))
            .effect(SkillEffect.speedBoost(10))
            .build());
        
        // SPECIALIST - Glass cannon
        tree.addSkill(Skill.builder("wolf_specialist_feral")
            .name("Feral Beast").description("Abandon all control. Maximum damage.")
            .icon("☠").tier(SkillTier.TIER_4).branch(SkillBranch.SPECIALIST)
            .position(3, 3).cost(4)
            .requires("wolf_berserker", "wolf_bloodlust")
            .excludes("wolf_dire", "wolf_pack_lord")
            .effect(SkillEffect.damageBoost(50))
            .effect(SkillEffect.attackSpeed(30))
            .effect(SkillEffect.armorBoost(-6))
            .effect(SkillEffect.special("feral", "Cannot control transformation"))
            .build());
        
        // SYNERGY - Attack + Defense
        tree.addSkill(Skill.builder("wolf_synergy_guardian")
            .name("Pack Guardian").description("Protect and destroy.")
            .icon("◉").tier(SkillTier.TIER_4).branch(SkillBranch.SYNERGY_AD)
            .position(1, 4).cost(4)
            .requires("wolf_pack_alpha", "wolf_iron_fur")
            .effect(SkillEffect.damageBoost(12))
            .effect(SkillEffect.armorBoost(4))
            .effect(SkillEffect.special("guardian", "Wolves tank damage for you"))
            .build());
        
        // TIER 5 - Ultimate
        tree.addSkill(Skill.builder("wolf_lycan_lord")
            .name("Lycan Lord").description("The ultimate werewolf.")
            .icon("☬").tier(SkillTier.TIER_5).branch(SkillBranch.CORE)
            .position(1, 5).cost(5)
            .requires("wolf_alpha", "wolf_dire")
            .effect(SkillEffect.damageBoost(20))
            .effect(SkillEffect.healthBoost(4))
            .effect(SkillEffect.speedBoost(15))
            .effect(SkillEffect.special("perma_wolf", "Can stay in wolf form permanently"))
            .build());
        
        // FORBIDDEN - Lose humanity
        tree.addSkill(Skill.builder("wolf_forbidden_beast")
            .name("Primal Beast").description("Abandon humanity entirely. Become the beast.")
            .icon("☠").tier(SkillTier.TIER_5).branch(SkillBranch.FORBIDDEN)
            .position(-1, 5).cost(5)
            .requires("wolf_specialist_feral")
            .excludes("wolf_ascended_alpha")
            .effect(SkillEffect.damageBoost(60))
            .effect(SkillEffect.speedBoost(30))
            .effect(SkillEffect.special("primal_beast", "Permanent wolf form, attack allies"))
            .effect(SkillEffect.special("no_items", "Cannot use items or tools"))
            .build());
        
        // ASCENDED - Perfect control
        tree.addSkill(Skill.builder("wolf_ascended_alpha")
            .name("True Alpha").description("Perfect control over the beast within.")
            .icon("⚜").tier(SkillTier.TIER_5).branch(SkillBranch.ASCENDED)
            .position(3, 5).cost(5)
            .requires("wolf_lycan_lord", "wolf_lunar_master")
            .excludes("wolf_forbidden_beast")
            .effect(SkillEffect.damageBoost(25))
            .effect(SkillEffect.healthBoost(3))
            .effect(SkillEffect.special("true_alpha", "Transform at will, keep human mind"))
            .effect(SkillEffect.special("pack_master", "All wolves in area follow you"))
            .build());
        
        TREES.put("werewolf", tree);
    }

    
    // ==================== CINDERSOUL SKILL TREE ====================
    // Inferno vs Magma paths, Forbidden Sun Core, Ascended Phoenix
    private static void registerCindersoulTree() {
        SkillTreeData tree = new SkillTreeData("cindersoul", "Cindersoul");
        
        // TIER 1 - Core Fire
        tree.addSkill(Skill.builder("fire_spark")
            .name("Inner Spark").description("The flame within burns brighter.")
            .icon("🔥").tier(SkillTier.TIER_1).branch(SkillBranch.CORE)
            .position(0, 0).cost(1)
            .effect(SkillEffect.resourceRegen(10))
            .effect(SkillEffect.fireResistance(10))
            .build());
        
        tree.addSkill(Skill.builder("fire_warmth")
            .name("Eternal Warmth").description("Fire sustains your body.")
            .icon("♥").tier(SkillTier.TIER_1).branch(SkillBranch.CORE)
            .position(2, 0).cost(1)
            .effect(SkillEffect.healthBoost(1))
            .effect(SkillEffect.fireResistance(15))
            .build());
        
        tree.addSkill(Skill.builder("fire_speed")
            .name("Flickering Speed").description("Move like dancing flames.")
            .icon("»").tier(SkillTier.TIER_1).branch(SkillBranch.CORE)
            .position(4, 0).cost(1)
            .effect(SkillEffect.speedBoost(5))
            .build());
        
        // TIER 2 - Branches
        tree.addSkill(Skill.builder("fire_burn")
            .name("Searing Touch").description("Your attacks burn with fire.")
            .icon("⚔").tier(SkillTier.TIER_2).branch(SkillBranch.OFFENSE)
            .position(-1, 1).cost(2)
            .requires("fire_spark")
            .effect(SkillEffect.fireDamage(15))
            .effect(SkillEffect.damageBoost(5))
            .build());
        
        tree.addSkill(Skill.builder("fire_shield")
            .name("Flame Shield").description("Fire protects you from harm.")
            .icon("⛨").tier(SkillTier.TIER_2).branch(SkillBranch.DEFENSE)
            .position(1, 1).cost(2)
            .requires("fire_warmth")
            .effect(SkillEffect.fireResistance(25))
            .effect(SkillEffect.armorBoost(2))
            .build());
        
        tree.addSkill(Skill.builder("fire_agility")
            .name("Ember Step").description("Leave trails of fire as you move.")
            .icon("☽").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY)
            .position(3, 1).cost(2)
            .requires("fire_speed")
            .effect(SkillEffect.speedBoost(10))
            .effect(SkillEffect.special("fire_trail", "Leave fire behind when sprinting"))
            .build());
        
        tree.addSkill(Skill.builder("fire_consume")
            .name("Consume Flames").description("Absorb fire to heal.")
            .icon("✚").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY)
            .position(5, 1).cost(2)
            .requires("fire_speed")
            .effect(SkillEffect.special("fire_heal", "Standing in fire heals you"))
            .effect(SkillEffect.resourceRegen(15))
            .build());
        
        // TIER 3 - MUTUALLY EXCLUSIVE: Inferno vs Magma
        // INFERNO PATH - Pure fire damage
        tree.addSkill(Skill.builder("fire_inferno")
            .name("Inferno Master").description("Pure destructive fire. Maximum damage, fragile.")
            .icon("☠").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A)
            .position(-2, 2).cost(3)
            .requires("fire_burn")
            .excludes("fire_lava", "fire_magma_armor")
            .effect(SkillEffect.fireDamage(40))
            .effect(SkillEffect.damageBoost(20))
            .effect(SkillEffect.healthBoost(-2))
            .build());
        
        tree.addSkill(Skill.builder("fire_wildfire")
            .name("Wildfire").description("Your flames spread uncontrollably.")
            .icon("≋").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A)
            .position(-1, 2).cost(3)
            .requires("fire_burn")
            .excludes("fire_lava")
            .effect(SkillEffect.fireDamage(25))
            .effect(SkillEffect.special("spread_fire", "Fire spreads to nearby enemies"))
            .build());
        
        // MAGMA PATH - Tanky fire
        tree.addSkill(Skill.builder("fire_lava")
            .name("Lava Blood").description("Molten rock flows through you. Slower but tankier.")
            .icon("◉").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B)
            .position(0, 2).cost(3)
            .requires("fire_burn")
            .excludes("fire_inferno", "fire_wildfire")
            .effect(SkillEffect.fireDamage(15))
            .effect(SkillEffect.armorBoost(4))
            .effect(SkillEffect.speedBoost(-5))
            .build());
        
        tree.addSkill(Skill.builder("fire_magma_armor")
            .name("Magma Armor").description("Encased in cooling magma. Extreme defense.")
            .icon("♦").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B)
            .position(1, 2).cost(3)
            .requires("fire_shield")
            .excludes("fire_inferno")
            .effect(SkillEffect.armorBoost(6))
            .effect(SkillEffect.knockbackResistance(40))
            .effect(SkillEffect.speedBoost(-10))
            .effect(SkillEffect.special("thorns_fire", "Attackers take fire damage"))
            .build());
        
        tree.addSkill(Skill.builder("fire_phoenix")
            .name("Phoenix Heart").description("Rise from the ashes. Cheat death once.")
            .icon("✚").tier(SkillTier.TIER_3).branch(SkillBranch.DEFENSE)
            .position(2, 2).cost(3)
            .requires("fire_shield")
            .effect(SkillEffect.special("phoenix", "Revive once per day with 50% health"))
            .effect(SkillEffect.healthBoost(2))
            .build());
        
        tree.addSkill(Skill.builder("fire_flash")
            .name("Flash Fire").description("Teleport short distances in a burst of flame.")
            .icon("★").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY)
            .position(4, 2).cost(3)
            .requires("fire_agility")
            .effect(SkillEffect.cooldownReduction(20))
            .effect(SkillEffect.special("fire_dash", "Dash ability leaves fire"))
            .build());
        
        tree.addSkill(Skill.builder("fire_smoke")
            .name("Smoke Form").description("Become intangible smoke.")
            .icon("≋").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY)
            .position(5, 2).cost(3)
            .requires("fire_consume")
            .effect(SkillEffect.special("smoke_form", "Phase through blocks as smoke"))
            .effect(SkillEffect.speedBoost(15))
            .build());

        
        // TIER 4
        tree.addSkill(Skill.builder("fire_explosion")
            .name("Explosive Fury").description("Your fire explodes on impact.")
            .icon("♛").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE)
            .position(-2, 3).cost(4)
            .requires("fire_inferno")
            .effect(SkillEffect.fireDamage(30))
            .effect(SkillEffect.special("explosion", "Attacks cause small explosions"))
            .build());
        
        tree.addSkill(Skill.builder("fire_firestorm")
            .name("Firestorm").description("Rain fire from the sky.")
            .icon("☄").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE)
            .position(-1, 3).cost(4)
            .requires("fire_wildfire")
            .effect(SkillEffect.fireDamage(25))
            .effect(SkillEffect.special("firestorm", "Call down fire meteors"))
            .build());
        
        tree.addSkill(Skill.builder("fire_volcano")
            .name("Volcanic Body").description("Become a walking volcano.")
            .icon("⚜").tier(SkillTier.TIER_4).branch(SkillBranch.DEFENSE)
            .position(1, 3).cost(4)
            .requires("fire_magma_armor")
            .effect(SkillEffect.healthBoost(4))
            .effect(SkillEffect.armorBoost(4))
            .effect(SkillEffect.special("lava_aura", "Nearby enemies take fire damage"))
            .build());
        
        tree.addSkill(Skill.builder("fire_rebirth")
            .name("Phoenix Rebirth").description("Multiple lives through flame.")
            .icon("✚").tier(SkillTier.TIER_4).branch(SkillBranch.DEFENSE)
            .position(2, 3).cost(4)
            .requires("fire_phoenix")
            .effect(SkillEffect.special("multi_phoenix", "Revive 3 times per day"))
            .effect(SkillEffect.healthBoost(2))
            .build());
        
        tree.addSkill(Skill.builder("fire_comet")
            .name("Comet Dash").description("Become a blazing comet.")
            .icon("☄").tier(SkillTier.TIER_4).branch(SkillBranch.UTILITY)
            .position(4, 3).cost(4)
            .requires("fire_flash")
            .effect(SkillEffect.speedBoost(20))
            .effect(SkillEffect.cooldownReduction(15))
            .effect(SkillEffect.special("comet", "Dash damages enemies in path"))
            .build());
        
        // HYBRID
        tree.addSkill(Skill.builder("fire_hybrid_ember")
            .name("Living Ember").description("Balance of destruction and survival.")
            .icon("◈").tier(SkillTier.TIER_4).branch(SkillBranch.HYBRID)
            .position(0, 3).cost(4)
            .requires("fire_lava", "fire_phoenix")
            .effect(SkillEffect.fireDamage(20))
            .effect(SkillEffect.healthBoost(2))
            .effect(SkillEffect.armorBoost(2))
            .build());
        
        // SPECIALIST
        tree.addSkill(Skill.builder("fire_specialist_nova")
            .name("Supernova").description("Maximum fire output. Burns yourself too.")
            .icon("☠").tier(SkillTier.TIER_4).branch(SkillBranch.SPECIALIST)
            .position(3, 3).cost(4)
            .requires("fire_inferno", "fire_wildfire")
            .excludes("fire_volcano", "fire_rebirth")
            .effect(SkillEffect.fireDamage(60))
            .effect(SkillEffect.damageBoost(30))
            .effect(SkillEffect.special("self_burn", "Take 1 fire damage per second"))
            .build());
        
        // SYNERGY
        tree.addSkill(Skill.builder("fire_synergy_molten")
            .name("Molten Core").description("Merge offense and defense into one.")
            .icon("◉").tier(SkillTier.TIER_4).branch(SkillBranch.SYNERGY_AD)
            .position(1, 4).cost(4)
            .requires("fire_lava", "fire_magma_armor")
            .effect(SkillEffect.fireDamage(20))
            .effect(SkillEffect.armorBoost(3))
            .effect(SkillEffect.special("molten_blood", "Damage scales with armor"))
            .build());
        
        // TIER 5 - Ultimate
        tree.addSkill(Skill.builder("fire_sun")
            .name("Heart of the Sun").description("Channel the power of a star itself.")
            .icon("☀").tier(SkillTier.TIER_5).branch(SkillBranch.CORE)
            .position(1, 5).cost(5)
            .requires("fire_explosion", "fire_volcano")
            .effect(SkillEffect.fireDamage(25))
            .effect(SkillEffect.fireResistance(100))
            .effect(SkillEffect.healthBoost(3))
            .effect(SkillEffect.special("sun_aura", "Constant light and fire damage aura"))
            .build());
        
        // FORBIDDEN - Consume everything
        tree.addSkill(Skill.builder("fire_forbidden_consume")
            .name("All-Consuming Flame").description("Burn everything, including yourself.")
            .icon("☠").tier(SkillTier.TIER_5).branch(SkillBranch.FORBIDDEN)
            .position(-1, 5).cost(5)
            .requires("fire_specialist_nova")
            .excludes("fire_ascended_phoenix")
            .effect(SkillEffect.fireDamage(80))
            .effect(SkillEffect.damageBoost(40))
            .effect(SkillEffect.special("consume_all", "Destroy terrain, hurt allies"))
            .effect(SkillEffect.special("burn_hp", "Constantly lose health"))
            .build());
        
        // ASCENDED - Eternal Phoenix
        tree.addSkill(Skill.builder("fire_ascended_phoenix")
            .name("Eternal Phoenix").description("Transcend death through flame.")
            .icon("⚜").tier(SkillTier.TIER_5).branch(SkillBranch.ASCENDED)
            .position(3, 5).cost(5)
            .requires("fire_rebirth", "fire_sun")
            .excludes("fire_forbidden_consume")
            .effect(SkillEffect.fireDamage(30))
            .effect(SkillEffect.healthBoost(4))
            .effect(SkillEffect.special("eternal_phoenix", "Unlimited revives, weaker each time"))
            .effect(SkillEffect.special("fire_flight", "Fly on wings of flame"))
            .build());
        
        TREES.put("cindersoul", tree);
    }

    
    // ==================== FROSTBORN SKILL TREE ====================
    // Blizzard vs Glacier paths, Forbidden Absolute Zero, Ascended Winter Spirit
    private static void registerFrostbornTree() {
        SkillTreeData tree = new SkillTreeData("frostborn", "Frostborn");
        
        // TIER 1
        tree.addSkill(Skill.builder("frost_chill")
            .name("Inner Chill").description("Cold flows through your veins.")
            .icon("❄").tier(SkillTier.TIER_1).branch(SkillBranch.CORE)
            .position(0, 0).cost(1)
            .effect(SkillEffect.special("cold_immune", "Immune to freezing"))
            .effect(SkillEffect.resourceRegen(10))
            .build());
        
        tree.addSkill(Skill.builder("frost_hardy")
            .name("Hardy Constitution").description("The cold makes you stronger.")
            .icon("♥").tier(SkillTier.TIER_1).branch(SkillBranch.CORE)
            .position(2, 0).cost(1)
            .effect(SkillEffect.healthBoost(2))
            .build());
        
        tree.addSkill(Skill.builder("frost_slide")
            .name("Ice Slide").description("Glide across ice effortlessly.")
            .icon("»").tier(SkillTier.TIER_1).branch(SkillBranch.CORE)
            .position(4, 0).cost(1)
            .effect(SkillEffect.speedBoost(5))
            .effect(SkillEffect.special("ice_speed", "+50% speed on ice"))
            .build());
        
        // TIER 2
        tree.addSkill(Skill.builder("frost_bite")
            .name("Frostbite").description("Your attacks slow enemies.")
            .icon("⚔").tier(SkillTier.TIER_2).branch(SkillBranch.OFFENSE)
            .position(-1, 1).cost(2)
            .requires("frost_chill")
            .effect(SkillEffect.damageBoost(8))
            .effect(SkillEffect.special("slow_hit", "Attacks slow enemies"))
            .build());
        
        tree.addSkill(Skill.builder("frost_armor")
            .name("Ice Armor").description("Coat yourself in protective ice.")
            .icon("⛨").tier(SkillTier.TIER_2).branch(SkillBranch.DEFENSE)
            .position(1, 1).cost(2)
            .requires("frost_hardy")
            .effect(SkillEffect.armorBoost(3))
            .effect(SkillEffect.special("thorns_cold", "Attackers take cold damage"))
            .build());
        
        tree.addSkill(Skill.builder("frost_wind")
            .name("Winter Wind").description("Move with the blizzard.")
            .icon("☽").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY)
            .position(3, 1).cost(2)
            .requires("frost_slide")
            .effect(SkillEffect.speedBoost(10))
            .effect(SkillEffect.cooldownReduction(5))
            .build());
        
        tree.addSkill(Skill.builder("frost_create")
            .name("Ice Creation").description("Create ice from thin air.")
            .icon("◯").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY)
            .position(5, 1).cost(2)
            .requires("frost_slide")
            .effect(SkillEffect.special("create_ice", "Place ice blocks"))
            .build());
        
        // TIER 3 - MUTUALLY EXCLUSIVE: Blizzard vs Glacier
        tree.addSkill(Skill.builder("frost_blizzard")
            .name("Blizzard Master").description("Command devastating ice storms.")
            .icon("☠").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A)
            .position(-2, 2).cost(3)
            .requires("frost_bite")
            .excludes("frost_glacier", "frost_permafrost")
            .effect(SkillEffect.damageBoost(25))
            .effect(SkillEffect.speedBoost(5))
            .effect(SkillEffect.special("blizzard_aoe", "Abilities hit in area"))
            .build());
        
        tree.addSkill(Skill.builder("frost_icicle")
            .name("Icicle Barrage").description("Launch deadly ice projectiles.")
            .icon("⚔").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A)
            .position(-1, 2).cost(3)
            .requires("frost_bite")
            .excludes("frost_glacier")
            .effect(SkillEffect.damageBoost(18))
            .effect(SkillEffect.special("icicle", "Ranged ice attack"))
            .build());
        
        tree.addSkill(Skill.builder("frost_glacier")
            .name("Glacier Heart").description("Become immovable ice.")
            .icon("◉").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B)
            .position(0, 2).cost(3)
            .requires("frost_bite")
            .excludes("frost_blizzard", "frost_icicle")
            .effect(SkillEffect.armorBoost(6))
            .effect(SkillEffect.knockbackResistance(50))
            .effect(SkillEffect.speedBoost(-15))
            .build());
        
        tree.addSkill(Skill.builder("frost_permafrost")
            .name("Permafrost Shell").description("Unbreakable ice defense.")
            .icon("♦").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B)
            .position(1, 2).cost(3)
            .requires("frost_armor")
            .excludes("frost_blizzard")
            .effect(SkillEffect.armorBoost(5))
            .effect(SkillEffect.healthBoost(3))
            .build());
        
        tree.addSkill(Skill.builder("frost_shatter")
            .name("Shatter").description("Frozen enemies take massive damage.")
            .icon("☠").tier(SkillTier.TIER_3).branch(SkillBranch.OFFENSE)
            .position(2, 2).cost(3)
            .requires("frost_armor")
            .effect(SkillEffect.damageBoost(15))
            .effect(SkillEffect.special("shatter", "+100% damage to frozen enemies"))
            .build());
        
        tree.addSkill(Skill.builder("frost_teleport")
            .name("Ice Blink").description("Teleport through ice.")
            .icon("★").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY)
            .position(4, 2).cost(3)
            .requires("frost_wind")
            .effect(SkillEffect.cooldownReduction(20))
            .effect(SkillEffect.special("ice_tp", "Short range teleport"))
            .build());
        
        tree.addSkill(Skill.builder("frost_construct")
            .name("Ice Construct").description("Create ice golems.")
            .icon("⚜").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY)
            .position(5, 2).cost(3)
            .requires("frost_create")
            .effect(SkillEffect.special("ice_golem", "Summon ice golem ally"))
            .build());
        
        // TIER 4
        tree.addSkill(Skill.builder("frost_avalanche")
            .name("Avalanche").description("Unleash devastating ice attacks.")
            .icon("♛").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE)
            .position(-2, 3).cost(4)
            .requires("frost_blizzard")
            .effect(SkillEffect.damageBoost(30))
            .effect(SkillEffect.special("avalanche", "Massive AoE ice damage"))
            .build());
        
        tree.addSkill(Skill.builder("frost_eternal")
            .name("Eternal Ice").description("Become one with the glacier.")
            .icon("⚜").tier(SkillTier.TIER_4).branch(SkillBranch.DEFENSE)
            .position(1, 3).cost(4)
            .requires("frost_permafrost")
            .effect(SkillEffect.healthBoost(5))
            .effect(SkillEffect.armorBoost(4))
            .effect(SkillEffect.special("ice_regen", "Regenerate in cold biomes"))
            .build());
        
        tree.addSkill(Skill.builder("frost_storm")
            .name("Storm Rider").description("Ride the winter winds.")
            .icon("☄").tier(SkillTier.TIER_4).branch(SkillBranch.UTILITY)
            .position(4, 3).cost(4)
            .requires("frost_teleport")
            .effect(SkillEffect.speedBoost(25))
            .effect(SkillEffect.special("flight_cold", "Glide in snowy weather"))
            .build());
        
        // HYBRID
        tree.addSkill(Skill.builder("frost_hybrid_winter")
            .name("Winter's Balance").description("Offense and defense in harmony.")
            .icon("◈").tier(SkillTier.TIER_4).branch(SkillBranch.HYBRID)
            .position(0, 3).cost(4)
            .requires("frost_glacier", "frost_shatter")
            .effect(SkillEffect.damageBoost(15))
            .effect(SkillEffect.armorBoost(3))
            .effect(SkillEffect.healthBoost(2))
            .build());
        
        // SPECIALIST
        tree.addSkill(Skill.builder("frost_specialist_flash")
            .name("Flash Freeze").description("Instant freeze everything. Fragile.")
            .icon("☠").tier(SkillTier.TIER_4).branch(SkillBranch.SPECIALIST)
            .position(3, 3).cost(4)
            .requires("frost_blizzard", "frost_icicle")
            .excludes("frost_eternal")
            .effect(SkillEffect.damageBoost(40))
            .effect(SkillEffect.special("flash_freeze", "Instantly freeze all nearby"))
            .effect(SkillEffect.armorBoost(-4))
            .build());
        
        // SYNERGY
        tree.addSkill(Skill.builder("frost_synergy_fortress")
            .name("Ice Fortress").description("Ultimate defensive ice.")
            .icon("◉").tier(SkillTier.TIER_4).branch(SkillBranch.SYNERGY_DU)
            .position(2, 4).cost(4)
            .requires("frost_permafrost", "frost_construct")
            .effect(SkillEffect.armorBoost(5))
            .effect(SkillEffect.special("ice_walls", "Create ice walls on demand"))
            .build());
        
        // TIER 5
        tree.addSkill(Skill.builder("frost_absolute")
            .name("Absolute Zero").description("Command the coldest temperatures.")
            .icon("❆").tier(SkillTier.TIER_5).branch(SkillBranch.CORE)
            .position(1, 5).cost(5)
            .requires("frost_avalanche", "frost_eternal")
            .effect(SkillEffect.damageBoost(20))
            .effect(SkillEffect.armorBoost(3))
            .effect(SkillEffect.special("freeze_aura", "Freeze nearby enemies"))
            .build());
        
        // FORBIDDEN
        tree.addSkill(Skill.builder("frost_forbidden_entropy")
            .name("Heat Death").description("Drain all warmth. Kill everything.")
            .icon("☠").tier(SkillTier.TIER_5).branch(SkillBranch.FORBIDDEN)
            .position(-1, 5).cost(5)
            .requires("frost_specialist_flash")
            .excludes("frost_ascended_spirit")
            .effect(SkillEffect.damageBoost(50))
            .effect(SkillEffect.special("heat_death", "Drain life from all nearby"))
            .effect(SkillEffect.special("cold_aura", "Constant damage to allies too"))
            .build());
        
        // ASCENDED
        tree.addSkill(Skill.builder("frost_ascended_spirit")
            .name("Winter Spirit").description("Become one with winter itself.")
            .icon("⚜").tier(SkillTier.TIER_5).branch(SkillBranch.ASCENDED)
            .position(3, 5).cost(5)
            .requires("frost_absolute", "frost_storm")
            .excludes("frost_forbidden_entropy")
            .effect(SkillEffect.damageBoost(25))
            .effect(SkillEffect.speedBoost(20))
            .effect(SkillEffect.special("winter_spirit", "Transform into pure ice"))
            .effect(SkillEffect.special("blizzard_control", "Control weather"))
            .build());
        
        TREES.put("frostborn", tree);
    }

    
    // ==================== VOIDTOUCHED SKILL TREE ====================
    // Entropy vs Stability, Forbidden Reality Tear, Ascended Void Walker
    private static void registerVoidtouchedTree() {
        SkillTreeData tree = new SkillTreeData("voidtouched", "Voidtouched");
        
        // TIER 1
        tree.addSkill(Skill.builder("void_touch")
            .name("Void Touch").description("The void whispers to you.")
            .icon("◯").tier(SkillTier.TIER_1).branch(SkillBranch.CORE)
            .position(0, 0).cost(1)
            .effect(SkillEffect.resourceRegen(15))
            .build());
        
        tree.addSkill(Skill.builder("void_body")
            .name("Void Body").description("Partially exist in the void.")
            .icon("♥").tier(SkillTier.TIER_1).branch(SkillBranch.CORE)
            .position(2, 0).cost(1)
            .effect(SkillEffect.healthBoost(1))
            .effect(SkillEffect.special("void_damage_reduce", "-25% void damage"))
            .build());
        
        tree.addSkill(Skill.builder("void_phase")
            .name("Phase Step").description("Step between dimensions.")
            .icon("»").tier(SkillTier.TIER_1).branch(SkillBranch.CORE)
            .position(4, 0).cost(1)
            .effect(SkillEffect.speedBoost(5))
            .build());
        
        // TIER 2
        tree.addSkill(Skill.builder("void_drain")
            .name("Void Drain").description("Drain life force into the void.")
            .icon("⚔").tier(SkillTier.TIER_2).branch(SkillBranch.OFFENSE)
            .position(-1, 1).cost(2)
            .requires("void_touch")
            .effect(SkillEffect.damageBoost(10))
            .effect(SkillEffect.special("lifesteal", "5% lifesteal"))
            .build());
        
        tree.addSkill(Skill.builder("void_shield")
            .name("Void Barrier").description("The void protects its own.")
            .icon("⛨").tier(SkillTier.TIER_2).branch(SkillBranch.DEFENSE)
            .position(1, 1).cost(2)
            .requires("void_body")
            .effect(SkillEffect.armorBoost(2))
            .effect(SkillEffect.special("void_shield", "Absorb damage periodically"))
            .build());
        
        tree.addSkill(Skill.builder("void_blink")
            .name("Void Blink").description("Teleport short distances.")
            .icon("☽").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY)
            .position(3, 1).cost(2)
            .requires("void_phase")
            .effect(SkillEffect.cooldownReduction(10))
            .effect(SkillEffect.special("blink", "Short range teleport"))
            .build());
        
        tree.addSkill(Skill.builder("void_sight")
            .name("Void Sight").description("See through dimensions.")
            .icon("★").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY)
            .position(5, 1).cost(2)
            .requires("void_phase")
            .effect(SkillEffect.special("void_sight", "See invisible, through walls"))
            .build());
        
        // TIER 3 - MUTUALLY EXCLUSIVE: Entropy vs Stability
        tree.addSkill(Skill.builder("void_entropy")
            .name("Entropy Master").description("Embrace chaos. Unpredictable power.")
            .icon("☠").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A)
            .position(-2, 2).cost(3)
            .requires("void_drain")
            .excludes("void_stability", "void_anchor")
            .effect(SkillEffect.damageBoost(30))
            .effect(SkillEffect.special("random_damage", "Damage varies +/- 50%"))
            .build());
        
        tree.addSkill(Skill.builder("void_consume")
            .name("Void Consumption").description("Consume matter into nothing.")
            .icon("◐").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A)
            .position(-1, 2).cost(3)
            .requires("void_drain")
            .excludes("void_stability")
            .effect(SkillEffect.damageBoost(20))
            .effect(SkillEffect.special("consume", "Destroy blocks on hit"))
            .build());
        
        tree.addSkill(Skill.builder("void_stability")
            .name("Void Stability").description("Control the void precisely.")
            .icon("◉").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B)
            .position(0, 2).cost(3)
            .requires("void_drain")
            .excludes("void_entropy", "void_consume")
            .effect(SkillEffect.damageBoost(15))
            .effect(SkillEffect.cooldownReduction(15))
            .build());
        
        tree.addSkill(Skill.builder("void_anchor")
            .name("Void Anchor").description("Anchor yourself to reality.")
            .icon("♦").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B)
            .position(1, 2).cost(3)
            .requires("void_shield")
            .excludes("void_entropy")
            .effect(SkillEffect.knockbackResistance(60))
            .effect(SkillEffect.armorBoost(4))
            .build());
        
        tree.addSkill(Skill.builder("void_phase_master")
            .name("Phase Master").description("Exist in multiple places.")
            .icon("≋").tier(SkillTier.TIER_3).branch(SkillBranch.DEFENSE)
            .position(2, 2).cost(3)
            .requires("void_shield")
            .effect(SkillEffect.special("phase_dodge", "20% chance to phase through attacks"))
            .effect(SkillEffect.speedBoost(10))
            .build());
        
        tree.addSkill(Skill.builder("void_rift")
            .name("Rift Walker").description("Open rifts in space.")
            .icon("★").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY)
            .position(4, 2).cost(3)
            .requires("void_blink")
            .effect(SkillEffect.cooldownReduction(25))
            .effect(SkillEffect.special("rift", "Create temporary portals"))
            .build());
        
        tree.addSkill(Skill.builder("void_dimension")
            .name("Pocket Dimension").description("Create your own space.")
            .icon("◯").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY)
            .position(5, 2).cost(3)
            .requires("void_sight")
            .effect(SkillEffect.special("pocket_dim", "Store items in void"))
            .build());
        
        // TIER 4
        tree.addSkill(Skill.builder("void_chaos")
            .name("Chaos Incarnate").description("Become pure entropy.")
            .icon("♛").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE)
            .position(-2, 3).cost(4)
            .requires("void_entropy")
            .effect(SkillEffect.damageBoost(35))
            .effect(SkillEffect.special("chaos_aura", "Random effects on nearby"))
            .build());
        
        tree.addSkill(Skill.builder("void_fortress")
            .name("Void Fortress").description("Impenetrable void barrier.")
            .icon("⚜").tier(SkillTier.TIER_4).branch(SkillBranch.DEFENSE)
            .position(1, 3).cost(4)
            .requires("void_anchor")
            .effect(SkillEffect.healthBoost(4))
            .effect(SkillEffect.armorBoost(5))
            .effect(SkillEffect.special("void_immune", "Immune to void damage"))
            .build());
        
        tree.addSkill(Skill.builder("void_hop")
            .name("Dimension Hop").description("Travel between dimensions.")
            .icon("☄").tier(SkillTier.TIER_4).branch(SkillBranch.UTILITY)
            .position(4, 3).cost(4)
            .requires("void_rift")
            .effect(SkillEffect.speedBoost(20))
            .effect(SkillEffect.special("dimension_hop", "Long range teleport"))
            .build());
        
        // HYBRID
        tree.addSkill(Skill.builder("void_hybrid_balance")
            .name("Void Balance").description("Chaos and order in harmony.")
            .icon("◈").tier(SkillTier.TIER_4).branch(SkillBranch.HYBRID)
            .position(0, 3).cost(4)
            .requires("void_stability", "void_phase_master")
            .effect(SkillEffect.damageBoost(18))
            .effect(SkillEffect.armorBoost(3))
            .effect(SkillEffect.cooldownReduction(10))
            .build());
        
        // SPECIALIST
        tree.addSkill(Skill.builder("void_specialist_null")
            .name("Null Zone").description("Erase everything in range.")
            .icon("☠").tier(SkillTier.TIER_4).branch(SkillBranch.SPECIALIST)
            .position(3, 3).cost(4)
            .requires("void_entropy", "void_consume")
            .excludes("void_fortress")
            .effect(SkillEffect.damageBoost(50))
            .effect(SkillEffect.special("null_zone", "Delete blocks and entities"))
            .effect(SkillEffect.healthBoost(-3))
            .build());
        
        // SYNERGY
        tree.addSkill(Skill.builder("void_synergy_warp")
            .name("Reality Warp").description("Bend space around you.")
            .icon("◉").tier(SkillTier.TIER_4).branch(SkillBranch.SYNERGY_AU)
            .position(2, 4).cost(4)
            .requires("void_phase_master", "void_rift")
            .effect(SkillEffect.speedBoost(15))
            .effect(SkillEffect.special("warp_field", "Slow projectiles near you"))
            .build());
        
        // TIER 5
        tree.addSkill(Skill.builder("void_avatar")
            .name("Avatar of the Void").description("Become one with the void.")
            .icon("◎").tier(SkillTier.TIER_5).branch(SkillBranch.CORE)
            .position(1, 5).cost(5)
            .requires("void_chaos", "void_fortress")
            .effect(SkillEffect.damageBoost(25))
            .effect(SkillEffect.healthBoost(3))
            .effect(SkillEffect.special("void_form", "Transform into void energy"))
            .build());
        
        // FORBIDDEN
        tree.addSkill(Skill.builder("void_forbidden_tear")
            .name("Reality Tear").description("Tear holes in reality. Unstable.")
            .icon("☠").tier(SkillTier.TIER_5).branch(SkillBranch.FORBIDDEN)
            .position(-1, 5).cost(5)
            .requires("void_specialist_null")
            .excludes("void_ascended_walker")
            .effect(SkillEffect.damageBoost(60))
            .effect(SkillEffect.special("reality_tear", "Create permanent void holes"))
            .effect(SkillEffect.special("unstable", "Random teleportation"))
            .build());
        
        // ASCENDED
        tree.addSkill(Skill.builder("void_ascended_walker")
            .name("Void Walker").description("Walk between all realities.")
            .icon("⚜").tier(SkillTier.TIER_5).branch(SkillBranch.ASCENDED)
            .position(3, 5).cost(5)
            .requires("void_avatar", "void_hop")
            .excludes("void_forbidden_tear")
            .effect(SkillEffect.damageBoost(30))
            .effect(SkillEffect.speedBoost(25))
            .effect(SkillEffect.special("void_walker", "Phase through all matter"))
            .effect(SkillEffect.special("dimension_master", "Travel to any dimension"))
            .build());
        
        TREES.put("voidtouched", tree);
    }


    // ==================== VAMPLING SKILL TREE ====================
    private static void registerVamplingTree() {
        SkillTreeData tree = new SkillTreeData("vampling", "Vampling");
        
        tree.addSkill(Skill.builder("vling_thirst").name("Blood Thirst").description("Crave blood efficiently.")
            .icon("♥").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(0, 0).cost(1)
            .effect(SkillEffect.resourceRegen(15)).build());
        tree.addSkill(Skill.builder("vling_agile").name("Nimble").description("Quick and light.")
            .icon("»").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(2, 0).cost(1)
            .effect(SkillEffect.speedBoost(8)).build());
        tree.addSkill(Skill.builder("vling_tough").name("Resilient").description("Tougher than you look.")
            .icon("⛨").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(4, 0).cost(1)
            .effect(SkillEffect.healthBoost(1)).build());
        
        tree.addSkill(Skill.builder("vling_bite").name("Sharp Fangs").description("Better at draining.")
            .icon("⚔").tier(SkillTier.TIER_2).branch(SkillBranch.OFFENSE).position(-1, 1).cost(2)
            .requires("vling_thirst").effect(SkillEffect.damageBoost(8)).build());
        tree.addSkill(Skill.builder("vling_dodge").name("Evasion").description("Harder to hit.")
            .icon("☽").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(2, 1).cost(2)
            .requires("vling_agile").effect(SkillEffect.speedBoost(10)).effect(SkillEffect.special("dodge", "10% dodge chance")).build());
        tree.addSkill(Skill.builder("vling_hide").name("Thick Skin").description("Natural armor.")
            .icon("♦").tier(SkillTier.TIER_2).branch(SkillBranch.DEFENSE).position(4, 1).cost(2)
            .requires("vling_tough").effect(SkillEffect.armorBoost(2)).build());
        
        tree.addSkill(Skill.builder("vling_hunter").name("Night Hunter").description("Thrive in darkness.")
            .icon("☠").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-1, 2).cost(3)
            .requires("vling_bite").excludes("vling_daylight").effect(SkillEffect.damageBoost(12)).effect(SkillEffect.special("night_vision", "See in dark")).build());
        tree.addSkill(Skill.builder("vling_daylight").name("Daylight Tolerance").description("Resist the sun.")
            .icon("☀").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(0, 2).cost(3)
            .requires("vling_bite").excludes("vling_hunter").effect(SkillEffect.special("sun_resist", "-50% sun damage")).build());
        tree.addSkill(Skill.builder("vling_shadow").name("Shadow Step").description("Move unseen.")
            .icon("◐").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(2, 2).cost(3)
            .requires("vling_dodge").effect(SkillEffect.cooldownReduction(15)).effect(SkillEffect.special("stealth", "Brief invisibility")).build());
        tree.addSkill(Skill.builder("vling_regen").name("Blood Healing").description("Heal from blood.")
            .icon("✚").tier(SkillTier.TIER_3).branch(SkillBranch.DEFENSE).position(4, 2).cost(3)
            .requires("vling_hide").effect(SkillEffect.healthBoost(2)).effect(SkillEffect.special("blood_heal", "Blood heals more")).build());
        
        tree.addSkill(Skill.builder("vling_predator").name("Young Predator").description("Growing into power.")
            .icon("♛").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE).position(-1, 3).cost(4)
            .requires("vling_hunter").effect(SkillEffect.damageBoost(15)).effect(SkillEffect.attackSpeed(10)).build());
        tree.addSkill(Skill.builder("vling_survivor").name("Survivor").description("Hard to kill.")
            .icon("⚜").tier(SkillTier.TIER_4).branch(SkillBranch.DEFENSE).position(3, 3).cost(4)
            .requires("vling_regen").effect(SkillEffect.healthBoost(3)).effect(SkillEffect.armorBoost(2)).build());
        
        tree.addSkill(Skill.builder("vling_fledgling").name("True Fledgling").description("Embrace vampiric nature.")
            .icon("☬").tier(SkillTier.TIER_5).branch(SkillBranch.CORE).position(1, 4).cost(5)
            .requires("vling_predator", "vling_survivor").effect(SkillEffect.damageBoost(15)).effect(SkillEffect.healthBoost(2)).effect(SkillEffect.speedBoost(10)).build());
        
        TREES.put("vampling", tree);
    }
    
    // ==================== WOLFLING SKILL TREE ====================
    private static void registerWolflingTree() {
        SkillTreeData tree = new SkillTreeData("wolfling", "Wolfling");
        
        tree.addSkill(Skill.builder("wling_senses").name("Keen Senses").description("Enhanced awareness.")
            .icon("★").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(0, 0).cost(1)
            .effect(SkillEffect.special("tracking", "Track nearby entities")).build());
        tree.addSkill(Skill.builder("wling_swift").name("Swift Paws").description("Run faster.")
            .icon("»").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(2, 0).cost(1)
            .effect(SkillEffect.speedBoost(10)).build());
        tree.addSkill(Skill.builder("wling_hardy").name("Hardy").description("Tough constitution.")
            .icon("♥").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(4, 0).cost(1)
            .effect(SkillEffect.healthBoost(1)).build());

        tree.addSkill(Skill.builder("wling_bite").name("Wolf Bite").description("Stronger bite.")
            .icon("⚔").tier(SkillTier.TIER_2).branch(SkillBranch.OFFENSE).position(-1, 1).cost(2)
            .requires("wling_senses").effect(SkillEffect.damageBoost(8)).build());
        tree.addSkill(Skill.builder("wling_sprint").name("Pack Sprint").description("Burst of speed.")
            .icon("☽").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(2, 1).cost(2)
            .requires("wling_swift").effect(SkillEffect.speedBoost(12)).build());
        tree.addSkill(Skill.builder("wling_fur").name("Thick Fur").description("Natural protection.")
            .icon("⛨").tier(SkillTier.TIER_2).branch(SkillBranch.DEFENSE).position(4, 1).cost(2)
            .requires("wling_hardy").effect(SkillEffect.armorBoost(2)).build());
        
        tree.addSkill(Skill.builder("wling_feral").name("Feral Instinct").description("Wild and dangerous.")
            .icon("☠").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-1, 2).cost(3)
            .requires("wling_bite").excludes("wling_pack").effect(SkillEffect.damageBoost(15)).effect(SkillEffect.armorBoost(-2)).build());
        tree.addSkill(Skill.builder("wling_pack").name("Pack Tactics").description("Stronger with allies.")
            .icon("◐").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(0, 2).cost(3)
            .requires("wling_bite").excludes("wling_feral").effect(SkillEffect.damageBoost(8)).effect(SkillEffect.special("pack_bonus", "+5% per ally")).build());
        tree.addSkill(Skill.builder("wling_howl").name("Howl").description("Inspire and frighten.")
            .icon("☾").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(2, 2).cost(3)
            .requires("wling_sprint").effect(SkillEffect.cooldownReduction(10)).effect(SkillEffect.special("howl", "Buff allies, fear enemies")).build());
        tree.addSkill(Skill.builder("wling_endure").name("Endurance").description("Outlast enemies.")
            .icon("♦").tier(SkillTier.TIER_3).branch(SkillBranch.DEFENSE).position(4, 2).cost(3)
            .requires("wling_fur").effect(SkillEffect.healthBoost(2)).effect(SkillEffect.special("regen", "Slow health regen")).build());
        
        tree.addSkill(Skill.builder("wling_hunter").name("Young Hunter").description("Skilled predator.")
            .icon("♛").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE).position(-1, 3).cost(4)
            .requires("wling_feral").effect(SkillEffect.damageBoost(12)).effect(SkillEffect.attackSpeed(10)).build());
        tree.addSkill(Skill.builder("wling_guardian").name("Pack Guardian").description("Protect the pack.")
            .icon("⚜").tier(SkillTier.TIER_4).branch(SkillBranch.DEFENSE).position(3, 3).cost(4)
            .requires("wling_endure").effect(SkillEffect.healthBoost(3)).effect(SkillEffect.armorBoost(2)).build());
        
        tree.addSkill(Skill.builder("wling_alpha").name("Young Alpha").description("Lead the pack.")
            .icon("☬").tier(SkillTier.TIER_5).branch(SkillBranch.CORE).position(1, 4).cost(5)
            .requires("wling_hunter", "wling_guardian").effect(SkillEffect.damageBoost(12)).effect(SkillEffect.speedBoost(10)).effect(SkillEffect.healthBoost(2)).build());
        
        TREES.put("wolfling", tree);
    }


    // ==================== DRYAD SKILL TREE ====================
    // Nature vs Decay paths, Forbidden World Tree, Ascended Forest Spirit
    private static void registerDryadTree() {
        SkillTreeData tree = new SkillTreeData("dryad", "Dryad");
        
        // TIER 1
        tree.addSkill(Skill.builder("dryad_roots").name("Deep Roots").description("Draw strength from the earth.")
            .icon("♣").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(0, 0).cost(1)
            .effect(SkillEffect.healthBoost(2)).effect(SkillEffect.resourceRegen(10)).build());
        tree.addSkill(Skill.builder("dryad_growth").name("Rapid Growth").description("Accelerate natural healing.")
            .icon("✚").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(2, 0).cost(1)
            .effect(SkillEffect.special("regen", "Regenerate 0.5 hearts/5s")).build());
        tree.addSkill(Skill.builder("dryad_wind").name("Leaf on Wind").description("Move with natural grace.")
            .icon("»").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(4, 0).cost(1)
            .effect(SkillEffect.speedBoost(5)).build());
        
        // TIER 2
        tree.addSkill(Skill.builder("dryad_thorns").name("Thorn Strike").description("Attacks leave thorns in wounds.")
            .icon("⚔").tier(SkillTier.TIER_2).branch(SkillBranch.OFFENSE).position(-1, 1).cost(2)
            .requires("dryad_roots").effect(SkillEffect.damageBoost(8)).effect(SkillEffect.special("bleed", "Attacks cause bleeding")).build());
        tree.addSkill(Skill.builder("dryad_bark").name("Bark Skin").description("Skin hardens like bark.")
            .icon("⛨").tier(SkillTier.TIER_2).branch(SkillBranch.DEFENSE).position(1, 1).cost(2)
            .requires("dryad_roots").effect(SkillEffect.armorBoost(3)).build());
        tree.addSkill(Skill.builder("dryad_photosyn").name("Photosynthesis").description("Sunlight heals you.")
            .icon("☀").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(3, 1).cost(2)
            .requires("dryad_growth").effect(SkillEffect.special("sun_heal", "Heal in sunlight")).effect(SkillEffect.resourceRegen(15)).build());
        tree.addSkill(Skill.builder("dryad_commune").name("Nature Commune").description("Speak with plants and animals.")
            .icon("★").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(5, 1).cost(2)
            .requires("dryad_wind").effect(SkillEffect.special("animal_friend", "Animals don't attack")).build());
        
        // TIER 3 - MUTUALLY EXCLUSIVE: Life vs Decay
        tree.addSkill(Skill.builder("dryad_life").name("Life Bringer").description("Channel pure life energy.")
            .icon("✚").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-2, 2).cost(3)
            .requires("dryad_thorns").excludes("dryad_decay", "dryad_rot")
            .effect(SkillEffect.healthBoost(3)).effect(SkillEffect.special("heal_aura", "Heal nearby allies")).build());
        tree.addSkill(Skill.builder("dryad_overgrowth").name("Overgrowth").description("Plants grow explosively around you.")
            .icon("♣").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-1, 2).cost(3)
            .requires("dryad_thorns").excludes("dryad_decay")
            .effect(SkillEffect.damageBoost(15)).effect(SkillEffect.special("vine_trap", "Entangle enemies")).build());
        tree.addSkill(Skill.builder("dryad_decay").name("Decay Touch").description("Embrace the cycle of death.")
            .icon("☠").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(0, 2).cost(3)
            .requires("dryad_thorns").excludes("dryad_life", "dryad_overgrowth")
            .effect(SkillEffect.damageBoost(20)).effect(SkillEffect.special("wither", "Attacks cause wither")).build());
        tree.addSkill(Skill.builder("dryad_rot").name("Rot Aura").description("Decay spreads from you.")
            .icon("◐").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(1, 2).cost(3)
            .requires("dryad_bark").excludes("dryad_life")
            .effect(SkillEffect.special("rot_aura", "Nearby enemies take poison")).effect(SkillEffect.armorBoost(2)).build());
        tree.addSkill(Skill.builder("dryad_ironwood").name("Ironwood").description("Become as hard as ancient oak.")
            .icon("♦").tier(SkillTier.TIER_3).branch(SkillBranch.DEFENSE).position(2, 2).cost(3)
            .requires("dryad_bark").effect(SkillEffect.armorBoost(5)).effect(SkillEffect.knockbackResistance(30)).build());
        tree.addSkill(Skill.builder("dryad_treestride").name("Tree Stride").description("Teleport between trees.")
            .icon("★").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(4, 2).cost(3)
            .requires("dryad_photosyn").effect(SkillEffect.cooldownReduction(20)).effect(SkillEffect.special("tree_tp", "Teleport to nearby trees")).build());
        tree.addSkill(Skill.builder("dryad_summon").name("Summon Treant").description("Call forth a tree guardian.")
            .icon("⚜").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(5, 2).cost(3)
            .requires("dryad_commune").effect(SkillEffect.special("treant", "Summon treant ally")).build());
        
        // TIER 4
        tree.addSkill(Skill.builder("dryad_grove").name("Grove Guardian").description("The forest fights for you.")
            .icon("♛").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE).position(-2, 3).cost(4)
            .requires("dryad_overgrowth").effect(SkillEffect.damageBoost(20)).effect(SkillEffect.special("forest_army", "Plants attack enemies")).build());
        tree.addSkill(Skill.builder("dryad_blight").name("Blight Lord").description("Command disease and decay.")
            .icon("☠").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE).position(-1, 3).cost(4)
            .requires("dryad_decay").effect(SkillEffect.damageBoost(25)).effect(SkillEffect.special("plague", "Spread disease")).build());
        tree.addSkill(Skill.builder("dryad_ancient").name("Ancient Oak").description("Become an ancient tree.")
            .icon("⚜").tier(SkillTier.TIER_4).branch(SkillBranch.DEFENSE).position(1, 3).cost(4)
            .requires("dryad_ironwood").effect(SkillEffect.healthBoost(5)).effect(SkillEffect.armorBoost(4)).effect(SkillEffect.speedBoost(-10)).build());
        tree.addSkill(Skill.builder("dryad_forest").name("Forest Heart").description("The forest is your domain.")
            .icon("♣").tier(SkillTier.TIER_4).branch(SkillBranch.UTILITY).position(4, 3).cost(4)
            .requires("dryad_treestride").effect(SkillEffect.special("forest_sense", "Know all in forest")).effect(SkillEffect.speedBoost(15)).build());
        
        // HYBRID
        tree.addSkill(Skill.builder("dryad_hybrid_cycle").name("Cycle of Life").description("Balance life and death.")
            .icon("◈").tier(SkillTier.TIER_4).branch(SkillBranch.HYBRID).position(0, 3).cost(4)
            .requires("dryad_life", "dryad_rot").effect(SkillEffect.damageBoost(12)).effect(SkillEffect.healthBoost(2)).effect(SkillEffect.special("cycle", "Kills heal you")).build());
        
        // SPECIALIST
        tree.addSkill(Skill.builder("dryad_specialist_plague").name("Plague Bearer").description("Become disease incarnate.")
            .icon("☠").tier(SkillTier.TIER_4).branch(SkillBranch.SPECIALIST).position(3, 3).cost(4)
            .requires("dryad_decay", "dryad_rot").excludes("dryad_ancient")
            .effect(SkillEffect.damageBoost(35)).effect(SkillEffect.special("plague_aura", "Constant poison aura")).effect(SkillEffect.healthBoost(-2)).build());
        
        // SYNERGY
        tree.addSkill(Skill.builder("dryad_synergy_guardian").name("Nature's Guardian").description("Protect and nurture.")
            .icon("◉").tier(SkillTier.TIER_4).branch(SkillBranch.SYNERGY_DU).position(2, 4).cost(4)
            .requires("dryad_ironwood", "dryad_summon").effect(SkillEffect.armorBoost(3)).effect(SkillEffect.special("guardian_treants", "Treants protect allies")).build());
        
        // TIER 5
        tree.addSkill(Skill.builder("dryad_worldtree").name("World Tree").description("Channel the World Tree's power.")
            .icon("♣").tier(SkillTier.TIER_5).branch(SkillBranch.CORE).position(1, 5).cost(5)
            .requires("dryad_grove", "dryad_ancient").effect(SkillEffect.healthBoost(4)).effect(SkillEffect.damageBoost(15)).effect(SkillEffect.special("world_tree", "Massive regen in forests")).build());
        
        // FORBIDDEN
        tree.addSkill(Skill.builder("dryad_forbidden_blight").name("World Blight").description("Corrupt all nature. Destroy forests.")
            .icon("☠").tier(SkillTier.TIER_5).branch(SkillBranch.FORBIDDEN).position(-1, 5).cost(5)
            .requires("dryad_specialist_plague").excludes("dryad_ascended_spirit")
            .effect(SkillEffect.damageBoost(50)).effect(SkillEffect.special("world_blight", "Destroy all plants nearby")).effect(SkillEffect.special("nature_enemy", "Animals attack you")).build());
        
        // ASCENDED
        tree.addSkill(Skill.builder("dryad_ascended_spirit").name("Forest Spirit").description("Become one with nature.")
            .icon("⚜").tier(SkillTier.TIER_5).branch(SkillBranch.ASCENDED).position(3, 5).cost(5)
            .requires("dryad_worldtree", "dryad_forest").excludes("dryad_forbidden_blight")
            .effect(SkillEffect.healthBoost(3)).effect(SkillEffect.speedBoost(20)).effect(SkillEffect.special("forest_spirit", "Phase through plants, command nature")).build());
        
        TREES.put("dryad", tree);
    }


    // ==================== NECROMANCER SKILL TREE ====================
    // Soul vs Bone paths, Forbidden Lich, Ascended Death Knight
    private static void registerNecromancerTree() {
        SkillTreeData tree = new SkillTreeData("necromancer", "Necromancer");
        
        // TIER 1
        tree.addSkill(Skill.builder("necro_touch").name("Death's Touch").description("Channel necrotic energy.")
            .icon("☠").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(0, 0).cost(1)
            .effect(SkillEffect.damageBoost(3)).effect(SkillEffect.resourceRegen(10)).build());
        tree.addSkill(Skill.builder("necro_will").name("Iron Will").description("Resist death's call.")
            .icon("♥").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(2, 0).cost(1)
            .effect(SkillEffect.healthBoost(1)).effect(SkillEffect.special("undead_resist", "-25% undead damage")).build());
        tree.addSkill(Skill.builder("necro_whisper").name("Death Whisper").description("Hear the dead speak.")
            .icon("★").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(4, 0).cost(1)
            .effect(SkillEffect.special("death_sense", "Sense undead and corpses")).build());
        
        // TIER 2
        tree.addSkill(Skill.builder("necro_drain").name("Life Drain").description("Steal life from enemies.")
            .icon("⚔").tier(SkillTier.TIER_2).branch(SkillBranch.OFFENSE).position(-1, 1).cost(2)
            .requires("necro_touch").effect(SkillEffect.damageBoost(10)).effect(SkillEffect.special("lifesteal", "10% lifesteal")).build());
        tree.addSkill(Skill.builder("necro_shield").name("Bone Shield").description("Surround yourself with bones.")
            .icon("⛨").tier(SkillTier.TIER_2).branch(SkillBranch.DEFENSE).position(1, 1).cost(2)
            .requires("necro_will").effect(SkillEffect.armorBoost(3)).effect(SkillEffect.special("bone_shield", "Absorb hits")).build());
        tree.addSkill(Skill.builder("necro_raise").name("Raise Dead").description("Raise basic undead minions.")
            .icon("☽").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(3, 1).cost(2)
            .requires("necro_whisper").effect(SkillEffect.special("raise_zombie", "Summon 2 zombies")).build());
        tree.addSkill(Skill.builder("necro_fear").name("Aura of Fear").description("Terrify the living.")
            .icon("◐").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(5, 1).cost(2)
            .requires("necro_whisper").effect(SkillEffect.special("fear_aura", "Nearby mobs flee")).build());
        
        // TIER 3 - MUTUALLY EXCLUSIVE: Soul vs Bone
        tree.addSkill(Skill.builder("necro_soul").name("Soul Reaper").description("Harvest souls for power.")
            .icon("◯").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-2, 2).cost(3)
            .requires("necro_drain").excludes("necro_bone", "necro_skeleton")
            .effect(SkillEffect.damageBoost(25)).effect(SkillEffect.special("soul_harvest", "Kills grant soul stacks")).build());
        tree.addSkill(Skill.builder("necro_wither").name("Wither Touch").description("Attacks cause decay.")
            .icon("☠").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-1, 2).cost(3)
            .requires("necro_drain").excludes("necro_bone")
            .effect(SkillEffect.damageBoost(18)).effect(SkillEffect.special("wither", "Attacks cause wither")).build());
        tree.addSkill(Skill.builder("necro_bone").name("Bone Master").description("Command bones themselves.")
            .icon("♦").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(0, 2).cost(3)
            .requires("necro_drain").excludes("necro_soul", "necro_wither")
            .effect(SkillEffect.damageBoost(15)).effect(SkillEffect.special("bone_spear", "Launch bone projectiles")).build());
        tree.addSkill(Skill.builder("necro_skeleton").name("Skeleton Army").description("Raise skeleton warriors.")
            .icon("⚜").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(1, 2).cost(3)
            .requires("necro_raise").excludes("necro_soul")
            .effect(SkillEffect.special("skeleton_army", "Summon 4 skeletons")).build());
        tree.addSkill(Skill.builder("necro_armor").name("Bone Armor").description("Encase yourself in bone.")
            .icon("⛨").tier(SkillTier.TIER_3).branch(SkillBranch.DEFENSE).position(2, 2).cost(3)
            .requires("necro_shield").effect(SkillEffect.armorBoost(5)).effect(SkillEffect.healthBoost(2)).build());
        tree.addSkill(Skill.builder("necro_command").name("Undead Command").description("Control more undead.")
            .icon("♛").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(4, 2).cost(3)
            .requires("necro_raise").effect(SkillEffect.special("undead_cap", "+4 undead limit")).effect(SkillEffect.cooldownReduction(15)).build());
        tree.addSkill(Skill.builder("necro_corpse").name("Corpse Explosion").description("Detonate corpses.")
            .icon("☠").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(5, 2).cost(3)
            .requires("necro_fear").effect(SkillEffect.special("corpse_explode", "Explode nearby corpses")).build());
        
        // TIER 4
        tree.addSkill(Skill.builder("necro_reaper").name("Grim Reaper").description("Become death incarnate.")
            .icon("☠").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE).position(-2, 3).cost(4)
            .requires("necro_soul").effect(SkillEffect.damageBoost(30)).effect(SkillEffect.special("execute", "Instant kill below 10% HP")).build());
        tree.addSkill(Skill.builder("necro_bonelord").name("Bone Lord").description("Master of skeletal constructs.")
            .icon("♛").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE).position(-1, 3).cost(4)
            .requires("necro_bone").effect(SkillEffect.damageBoost(20)).effect(SkillEffect.special("bone_golem", "Summon bone golem")).build());
        tree.addSkill(Skill.builder("necro_revenant").name("Revenant").description("Refuse to stay dead.")
            .icon("✚").tier(SkillTier.TIER_4).branch(SkillBranch.DEFENSE).position(1, 3).cost(4)
            .requires("necro_armor").effect(SkillEffect.healthBoost(4)).effect(SkillEffect.special("revive", "Revive once per day")).build());
        tree.addSkill(Skill.builder("necro_legion").name("Undead Legion").description("Command an army.")
            .icon("⚜").tier(SkillTier.TIER_4).branch(SkillBranch.UTILITY).position(4, 3).cost(4)
            .requires("necro_command").effect(SkillEffect.special("legion", "+8 undead limit, stronger minions")).build());
        
        // HYBRID
        tree.addSkill(Skill.builder("necro_hybrid_death").name("Death Knight").description("Warrior of death.")
            .icon("◈").tier(SkillTier.TIER_4).branch(SkillBranch.HYBRID).position(0, 3).cost(4)
            .requires("necro_wither", "necro_armor").effect(SkillEffect.damageBoost(15)).effect(SkillEffect.armorBoost(3)).effect(SkillEffect.healthBoost(2)).build());
        
        // SPECIALIST
        tree.addSkill(Skill.builder("necro_specialist_plague").name("Plague Doctor").description("Spread death and disease.")
            .icon("☠").tier(SkillTier.TIER_4).branch(SkillBranch.SPECIALIST).position(3, 3).cost(4)
            .requires("necro_soul", "necro_wither").excludes("necro_revenant")
            .effect(SkillEffect.damageBoost(40)).effect(SkillEffect.special("plague", "Spread disease on hit")).effect(SkillEffect.healthBoost(-2)).build());
        
        // SYNERGY
        tree.addSkill(Skill.builder("necro_synergy_army").name("Army of Darkness").description("Overwhelming undead force.")
            .icon("◉").tier(SkillTier.TIER_4).branch(SkillBranch.SYNERGY_AU).position(2, 4).cost(4)
            .requires("necro_skeleton", "necro_command").effect(SkillEffect.damageBoost(10)).effect(SkillEffect.special("dark_army", "Minions deal +50% damage")).build());
        
        // TIER 5
        tree.addSkill(Skill.builder("necro_master").name("Master of Death").description("Command death itself.")
            .icon("☬").tier(SkillTier.TIER_5).branch(SkillBranch.CORE).position(1, 5).cost(5)
            .requires("necro_reaper", "necro_revenant").effect(SkillEffect.damageBoost(20)).effect(SkillEffect.healthBoost(3)).effect(SkillEffect.special("death_master", "Revive as undead on death")).build());
        
        // FORBIDDEN
        tree.addSkill(Skill.builder("necro_forbidden_lich").name("Lichdom").description("Become an undead lich. Immortal but cursed.")
            .icon("☠").tier(SkillTier.TIER_5).branch(SkillBranch.FORBIDDEN).position(-1, 5).cost(5)
            .requires("necro_specialist_plague").excludes("necro_ascended_knight")
            .effect(SkillEffect.damageBoost(45)).effect(SkillEffect.special("lich", "Cannot truly die, respawn at phylactery")).effect(SkillEffect.special("undead", "Healing hurts, harmed by sun")).build());
        
        // ASCENDED
        tree.addSkill(Skill.builder("necro_ascended_knight").name("Eternal Champion").description("Death's chosen warrior.")
            .icon("⚜").tier(SkillTier.TIER_5).branch(SkillBranch.ASCENDED).position(3, 5).cost(5)
            .requires("necro_master", "necro_legion").excludes("necro_forbidden_lich")
            .effect(SkillEffect.damageBoost(25)).effect(SkillEffect.armorBoost(5)).effect(SkillEffect.special("champion", "Undead army fights with you")).build());
        
        TREES.put("necromancer", tree);
    }


    // ==================== STONEHEART SKILL TREE ====================
    // Mountain vs Crystal paths, Forbidden Living Mountain, Ascended Earth Spirit
    private static void registerStoneheartTree() {
        SkillTreeData tree = new SkillTreeData("stoneheart", "Stoneheart");
        
        // TIER 1
        tree.addSkill(Skill.builder("stone_core").name("Stone Core").description("Heart of living stone.")
            .icon("♦").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(0, 0).cost(1)
            .effect(SkillEffect.healthBoost(3)).effect(SkillEffect.knockbackResistance(15)).build());
        tree.addSkill(Skill.builder("stone_skin").name("Stone Skin").description("Skin hard as rock.")
            .icon("⛨").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(2, 0).cost(1)
            .effect(SkillEffect.armorBoost(2)).build());
        tree.addSkill(Skill.builder("stone_sense").name("Earth Sense").description("Feel vibrations in the ground.")
            .icon("★").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(4, 0).cost(1)
            .effect(SkillEffect.special("tremor_sense", "Detect movement through ground")).build());
        
        // TIER 2
        tree.addSkill(Skill.builder("stone_fist").name("Stone Fist").description("Crushing melee attacks.")
            .icon("⚔").tier(SkillTier.TIER_2).branch(SkillBranch.OFFENSE).position(-1, 1).cost(2)
            .requires("stone_core").effect(SkillEffect.damageBoost(10)).effect(SkillEffect.attackSpeed(-5)).build());
        tree.addSkill(Skill.builder("stone_wall").name("Stone Wall").description("Become an immovable wall.")
            .icon("♦").tier(SkillTier.TIER_2).branch(SkillBranch.DEFENSE).position(1, 1).cost(2)
            .requires("stone_skin").effect(SkillEffect.armorBoost(3)).effect(SkillEffect.knockbackResistance(25)).build());
        tree.addSkill(Skill.builder("stone_meld").name("Stone Meld").description("Phase into stone.")
            .icon("◐").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(3, 1).cost(2)
            .requires("stone_sense").effect(SkillEffect.special("stone_phase", "Hide inside stone blocks")).build());
        tree.addSkill(Skill.builder("stone_shape").name("Stone Shaping").description("Mold stone with your hands.")
            .icon("◯").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(5, 1).cost(2)
            .requires("stone_sense").effect(SkillEffect.special("shape_stone", "Break stone faster, place stone")).build());
        
        // TIER 3 - MUTUALLY EXCLUSIVE: Mountain vs Crystal
        tree.addSkill(Skill.builder("stone_mountain").name("Mountain's Might").description("Raw crushing power.")
            .icon("⚔").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-2, 2).cost(3)
            .requires("stone_fist").excludes("stone_crystal", "stone_gem")
            .effect(SkillEffect.damageBoost(25)).effect(SkillEffect.special("stun", "Attacks stun enemies")).build());
        tree.addSkill(Skill.builder("stone_quake").name("Earthquake").description("Shake the earth itself.")
            .icon("≋").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-1, 2).cost(3)
            .requires("stone_fist").excludes("stone_crystal")
            .effect(SkillEffect.damageBoost(18)).effect(SkillEffect.special("quake", "AoE ground slam")).build());
        tree.addSkill(Skill.builder("stone_crystal").name("Crystal Heart").description("Become living crystal.")
            .icon("◇").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(0, 2).cost(3)
            .requires("stone_fist").excludes("stone_mountain", "stone_quake")
            .effect(SkillEffect.armorBoost(4)).effect(SkillEffect.special("reflect", "Reflect 20% damage")).build());
        tree.addSkill(Skill.builder("stone_gem").name("Gem Encrusted").description("Precious gems enhance you.")
            .icon("★").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(1, 2).cost(3)
            .requires("stone_wall").excludes("stone_mountain")
            .effect(SkillEffect.healthBoost(2)).effect(SkillEffect.armorBoost(3)).effect(SkillEffect.special("gem_bonus", "Gems give buffs")).build());
        tree.addSkill(Skill.builder("stone_fortress").name("Living Fortress").description("Become a walking fortress.")
            .icon("⛨").tier(SkillTier.TIER_3).branch(SkillBranch.DEFENSE).position(2, 2).cost(3)
            .requires("stone_wall").effect(SkillEffect.armorBoost(6)).effect(SkillEffect.healthBoost(3)).effect(SkillEffect.speedBoost(-15)).build());
        tree.addSkill(Skill.builder("stone_tunnel").name("Tunnel").description("Burrow through earth.")
            .icon("»").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(4, 2).cost(3)
            .requires("stone_meld").effect(SkillEffect.special("burrow", "Dig through dirt/stone")).effect(SkillEffect.speedBoost(5)).build());
        tree.addSkill(Skill.builder("stone_golem").name("Stone Golem").description("Create a stone servant.")
            .icon("⚜").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(5, 2).cost(3)
            .requires("stone_shape").effect(SkillEffect.special("golem", "Summon stone golem")).build());
        
        // TIER 4
        tree.addSkill(Skill.builder("stone_titan").name("Titan's Strength").description("Strength of the earth titans.")
            .icon("♛").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE).position(-2, 3).cost(4)
            .requires("stone_mountain").effect(SkillEffect.damageBoost(30)).effect(SkillEffect.special("titan_blow", "Massive knockback")).build());
        tree.addSkill(Skill.builder("stone_diamond").name("Diamond Body").description("Hardest substance known.")
            .icon("◇").tier(SkillTier.TIER_4).branch(SkillBranch.DEFENSE).position(1, 3).cost(4)
            .requires("stone_crystal").effect(SkillEffect.armorBoost(8)).effect(SkillEffect.armorToughnessBoost(4)).build());
        tree.addSkill(Skill.builder("stone_colossus").name("Colossus").description("Grow to massive size.")
            .icon("⚜").tier(SkillTier.TIER_4).branch(SkillBranch.DEFENSE).position(2, 3).cost(4)
            .requires("stone_fortress").effect(SkillEffect.healthBoost(6)).effect(SkillEffect.special("size_up", "+50% size")).build());
        tree.addSkill(Skill.builder("stone_earth").name("Earth Master").description("Command the earth.")
            .icon("♦").tier(SkillTier.TIER_4).branch(SkillBranch.UTILITY).position(4, 3).cost(4)
            .requires("stone_tunnel").effect(SkillEffect.special("earth_control", "Move stone blocks")).effect(SkillEffect.cooldownReduction(20)).build());
        
        // HYBRID
        tree.addSkill(Skill.builder("stone_hybrid_juggernaut").name("Juggernaut").description("Unstoppable force.")
            .icon("◈").tier(SkillTier.TIER_4).branch(SkillBranch.HYBRID).position(0, 3).cost(4)
            .requires("stone_quake", "stone_fortress").effect(SkillEffect.damageBoost(15)).effect(SkillEffect.armorBoost(4)).effect(SkillEffect.knockbackResistance(50)).build());
        
        // SPECIALIST
        tree.addSkill(Skill.builder("stone_specialist_siege").name("Siege Engine").description("Destroy everything. Slow but devastating.")
            .icon("☠").tier(SkillTier.TIER_4).branch(SkillBranch.SPECIALIST).position(3, 3).cost(4)
            .requires("stone_mountain", "stone_quake").excludes("stone_diamond")
            .effect(SkillEffect.damageBoost(50)).effect(SkillEffect.special("siege", "Destroy blocks on hit")).effect(SkillEffect.speedBoost(-20)).build());
        
        // SYNERGY
        tree.addSkill(Skill.builder("stone_synergy_guardian").name("Earth Guardian").description("Protect with stone.")
            .icon("◉").tier(SkillTier.TIER_4).branch(SkillBranch.SYNERGY_DU).position(2, 4).cost(4)
            .requires("stone_fortress", "stone_golem").effect(SkillEffect.armorBoost(4)).effect(SkillEffect.special("stone_shield", "Golem protects you")).build());
        
        // TIER 5
        tree.addSkill(Skill.builder("stone_avatar").name("Avatar of Stone").description("Become living earth.")
            .icon("♦").tier(SkillTier.TIER_5).branch(SkillBranch.CORE).position(1, 5).cost(5)
            .requires("stone_titan", "stone_colossus").effect(SkillEffect.damageBoost(20)).effect(SkillEffect.healthBoost(5)).effect(SkillEffect.armorBoost(5)).build());
        
        // FORBIDDEN
        tree.addSkill(Skill.builder("stone_forbidden_mountain").name("Living Mountain").description("Become a mountain. Immobile but invincible.")
            .icon("☠").tier(SkillTier.TIER_5).branch(SkillBranch.FORBIDDEN).position(-1, 5).cost(5)
            .requires("stone_specialist_siege").excludes("stone_ascended_spirit")
            .effect(SkillEffect.healthBoost(20)).effect(SkillEffect.armorBoost(15)).effect(SkillEffect.special("immobile", "Cannot move")).effect(SkillEffect.special("mountain", "Massive AoE damage")).build());
        
        // ASCENDED
        tree.addSkill(Skill.builder("stone_ascended_spirit").name("Earth Spirit").description("Become one with the earth.")
            .icon("⚜").tier(SkillTier.TIER_5).branch(SkillBranch.ASCENDED).position(3, 5).cost(5)
            .requires("stone_avatar", "stone_earth").excludes("stone_forbidden_mountain")
            .effect(SkillEffect.damageBoost(25)).effect(SkillEffect.armorBoost(6)).effect(SkillEffect.special("earth_spirit", "Phase through earth, control terrain")).build());
        
        TREES.put("stoneheart", tree);
    }


    // ==================== SKYBORN SKILL TREE ====================
    // Storm vs Wind paths, Forbidden Tempest, Ascended Sky Lord
    private static void registerSkybornTree() {
        SkillTreeData tree = new SkillTreeData("skyborn", "Skyborn");
        
        // TIER 1
        tree.addSkill(Skill.builder("sky_wings").name("Fledgling Wings").description("Wings begin to grow.")
            .icon("≋").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(0, 0).cost(1)
            .effect(SkillEffect.special("glide", "Slow fall and glide")).build());
        tree.addSkill(Skill.builder("sky_light").name("Hollow Bones").description("Light as air.")
            .icon("»").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(2, 0).cost(1)
            .effect(SkillEffect.speedBoost(8)).effect(SkillEffect.special("fall_resist", "-50% fall damage")).build());
        tree.addSkill(Skill.builder("sky_sight").name("Eagle Eye").description("See great distances.")
            .icon("★").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(4, 0).cost(1)
            .effect(SkillEffect.special("far_sight", "Zoom vision")).build());
        
        // TIER 2
        tree.addSkill(Skill.builder("sky_dive").name("Dive Attack").description("Strike from above.")
            .icon("⚔").tier(SkillTier.TIER_2).branch(SkillBranch.OFFENSE).position(-1, 1).cost(2)
            .requires("sky_wings").effect(SkillEffect.damageBoost(10)).effect(SkillEffect.special("dive", "+100% damage from above")).build());
        tree.addSkill(Skill.builder("sky_dodge").name("Aerial Dodge").description("Evade attacks in flight.")
            .icon("☽").tier(SkillTier.TIER_2).branch(SkillBranch.DEFENSE).position(1, 1).cost(2)
            .requires("sky_light").effect(SkillEffect.special("air_dodge", "20% dodge while airborne")).build());
        tree.addSkill(Skill.builder("sky_gust").name("Wind Gust").description("Push enemies with wind.")
            .icon("≋").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(3, 1).cost(2)
            .requires("sky_light").effect(SkillEffect.special("gust", "Knockback wind attack")).effect(SkillEffect.cooldownReduction(10)).build());
        tree.addSkill(Skill.builder("sky_call").name("Sky Call").description("Call birds to aid you.")
            .icon("♣").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(5, 1).cost(2)
            .requires("sky_sight").effect(SkillEffect.special("bird_ally", "Summon bird companions")).build());
        
        // TIER 3 - MUTUALLY EXCLUSIVE: Storm vs Wind
        tree.addSkill(Skill.builder("sky_storm").name("Storm Caller").description("Command lightning and thunder.")
            .icon("⚡").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-2, 2).cost(3)
            .requires("sky_dive").excludes("sky_zephyr", "sky_breeze")
            .effect(SkillEffect.damageBoost(25)).effect(SkillEffect.special("lightning", "Call lightning strikes")).build());
        tree.addSkill(Skill.builder("sky_thunder").name("Thunder Strike").description("Devastating electrical attacks.")
            .icon("☠").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-1, 2).cost(3)
            .requires("sky_dive").excludes("sky_zephyr")
            .effect(SkillEffect.damageBoost(20)).effect(SkillEffect.special("chain_lightning", "Lightning chains to enemies")).build());
        tree.addSkill(Skill.builder("sky_zephyr").name("Zephyr Master").description("Gentle but swift winds.")
            .icon("»").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(0, 2).cost(3)
            .requires("sky_dive").excludes("sky_storm", "sky_thunder")
            .effect(SkillEffect.speedBoost(20)).effect(SkillEffect.special("wind_boost", "Faster flight")).build());
        tree.addSkill(Skill.builder("sky_breeze").name("Healing Breeze").description("Wind that heals.")
            .icon("✚").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(1, 2).cost(3)
            .requires("sky_dodge").excludes("sky_storm")
            .effect(SkillEffect.healthBoost(2)).effect(SkillEffect.special("heal_wind", "Heal while flying")).build());
        tree.addSkill(Skill.builder("sky_feathers").name("Steel Feathers").description("Feathers hard as steel.")
            .icon("⛨").tier(SkillTier.TIER_3).branch(SkillBranch.DEFENSE).position(2, 2).cost(3)
            .requires("sky_dodge").effect(SkillEffect.armorBoost(4)).effect(SkillEffect.special("feather_shield", "Block projectiles")).build());
        tree.addSkill(Skill.builder("sky_flight").name("True Flight").description("Sustained flight.")
            .icon("≋").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(4, 2).cost(3)
            .requires("sky_gust").effect(SkillEffect.special("flight", "True flight ability")).effect(SkillEffect.speedBoost(10)).build());
        tree.addSkill(Skill.builder("sky_flock").name("Flock Master").description("Command a flock.")
            .icon("⚜").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(5, 2).cost(3)
            .requires("sky_call").effect(SkillEffect.special("flock", "Summon bird swarm")).build());
        
        // TIER 4
        tree.addSkill(Skill.builder("sky_tempest").name("Tempest").description("Become the storm.")
            .icon("⚡").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE).position(-2, 3).cost(4)
            .requires("sky_storm").effect(SkillEffect.damageBoost(30)).effect(SkillEffect.special("storm_aura", "Constant lightning around you")).build());
        tree.addSkill(Skill.builder("sky_hurricane").name("Hurricane").description("Devastating wind attacks.")
            .icon("≋").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE).position(-1, 3).cost(4)
            .requires("sky_thunder").effect(SkillEffect.damageBoost(25)).effect(SkillEffect.special("hurricane", "Create damaging vortex")).build());
        tree.addSkill(Skill.builder("sky_cloud").name("Cloud Form").description("Become intangible cloud.")
            .icon("◯").tier(SkillTier.TIER_4).branch(SkillBranch.DEFENSE).position(1, 3).cost(4)
            .requires("sky_feathers").effect(SkillEffect.special("cloud_form", "Phase through attacks")).effect(SkillEffect.speedBoost(15)).build());
        tree.addSkill(Skill.builder("sky_soar").name("Soaring Master").description("Unmatched aerial mobility.")
            .icon("»").tier(SkillTier.TIER_4).branch(SkillBranch.UTILITY).position(4, 3).cost(4)
            .requires("sky_flight").effect(SkillEffect.speedBoost(30)).effect(SkillEffect.special("sonic_flight", "Break sound barrier")).build());
        
        // HYBRID
        tree.addSkill(Skill.builder("sky_hybrid_raptor").name("Sky Raptor").description("Predator of the skies.")
            .icon("◈").tier(SkillTier.TIER_4).branch(SkillBranch.HYBRID).position(0, 3).cost(4)
            .requires("sky_zephyr", "sky_feathers").effect(SkillEffect.damageBoost(15)).effect(SkillEffect.speedBoost(15)).effect(SkillEffect.armorBoost(2)).build());
        
        // SPECIALIST
        tree.addSkill(Skill.builder("sky_specialist_lightning").name("Living Lightning").description("Become pure electricity.")
            .icon("⚡").tier(SkillTier.TIER_4).branch(SkillBranch.SPECIALIST).position(3, 3).cost(4)
            .requires("sky_storm", "sky_thunder").excludes("sky_cloud")
            .effect(SkillEffect.damageBoost(45)).effect(SkillEffect.special("lightning_form", "Teleport as lightning")).effect(SkillEffect.healthBoost(-3)).build());
        
        // SYNERGY
        tree.addSkill(Skill.builder("sky_synergy_wind").name("Wind Warrior").description("Combat and mobility combined.")
            .icon("◉").tier(SkillTier.TIER_4).branch(SkillBranch.SYNERGY_AU).position(2, 4).cost(4)
            .requires("sky_feathers", "sky_flight").effect(SkillEffect.damageBoost(12)).effect(SkillEffect.speedBoost(15)).build());
        
        // TIER 5
        tree.addSkill(Skill.builder("sky_lord").name("Sky Lord").description("Master of the heavens.")
            .icon("♛").tier(SkillTier.TIER_5).branch(SkillBranch.CORE).position(1, 5).cost(5)
            .requires("sky_tempest", "sky_cloud").effect(SkillEffect.damageBoost(20)).effect(SkillEffect.speedBoost(20)).effect(SkillEffect.special("sky_lord", "Control weather")).build());
        
        // FORBIDDEN
        tree.addSkill(Skill.builder("sky_forbidden_tempest").name("Eternal Tempest").description("Become a permanent storm. Destroy everything.")
            .icon("☠").tier(SkillTier.TIER_5).branch(SkillBranch.FORBIDDEN).position(-1, 5).cost(5)
            .requires("sky_specialist_lightning").excludes("sky_ascended_phoenix")
            .effect(SkillEffect.damageBoost(50)).effect(SkillEffect.special("eternal_storm", "Constant destruction")).effect(SkillEffect.special("no_land", "Cannot land")).build());
        
        // ASCENDED
        tree.addSkill(Skill.builder("sky_ascended_phoenix").name("Sky Phoenix").description("Reborn in the heavens.")
            .icon("⚜").tier(SkillTier.TIER_5).branch(SkillBranch.ASCENDED).position(3, 5).cost(5)
            .requires("sky_lord", "sky_soar").excludes("sky_forbidden_tempest")
            .effect(SkillEffect.damageBoost(25)).effect(SkillEffect.healthBoost(3)).effect(SkillEffect.special("sky_phoenix", "Revive in sky, fire wings")).build());
        
        TREES.put("skyborn", tree);
    }


    // ==================== TIDECALLER SKILL TREE ====================
    // Ocean vs Ice paths, Forbidden Leviathan, Ascended Sea Spirit
    private static void registerTidecallerTree() {
        SkillTreeData tree = new SkillTreeData("tidecaller", "Tidecaller");
        
        // TIER 1
        tree.addSkill(Skill.builder("tide_breath").name("Water Breathing").description("Breathe underwater.")
            .icon("≋").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(0, 0).cost(1)
            .effect(SkillEffect.special("water_breath", "Unlimited underwater breathing")).build());
        tree.addSkill(Skill.builder("tide_swim").name("Swift Swimmer").description("Move quickly in water.")
            .icon("»").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(2, 0).cost(1)
            .effect(SkillEffect.special("swim_speed", "+100% swim speed")).effect(SkillEffect.speedBoost(3)).build());
        tree.addSkill(Skill.builder("tide_pressure").name("Pressure Resistant").description("Withstand ocean depths.")
            .icon("♥").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(4, 0).cost(1)
            .effect(SkillEffect.healthBoost(1)).effect(SkillEffect.special("depth_immune", "No depth damage")).build());
        
        // TIER 2
        tree.addSkill(Skill.builder("tide_strike").name("Tidal Strike").description("Water-enhanced attacks.")
            .icon("⚔").tier(SkillTier.TIER_2).branch(SkillBranch.OFFENSE).position(-1, 1).cost(2)
            .requires("tide_breath").effect(SkillEffect.damageBoost(8)).effect(SkillEffect.special("water_damage", "+50% damage in water")).build());
        tree.addSkill(Skill.builder("tide_scales").name("Scale Armor").description("Protective scales.")
            .icon("⛨").tier(SkillTier.TIER_2).branch(SkillBranch.DEFENSE).position(1, 1).cost(2)
            .requires("tide_pressure").effect(SkillEffect.armorBoost(3)).build());
        tree.addSkill(Skill.builder("tide_current").name("Current Control").description("Manipulate water currents.")
            .icon("≋").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(3, 1).cost(2)
            .requires("tide_swim").effect(SkillEffect.special("current", "Push/pull with water")).effect(SkillEffect.cooldownReduction(10)).build());
        tree.addSkill(Skill.builder("tide_call").name("Sea Call").description("Summon sea creatures.")
            .icon("★").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(5, 1).cost(2)
            .requires("tide_swim").effect(SkillEffect.special("fish_ally", "Summon fish swarm")).build());
        
        // TIER 3 - MUTUALLY EXCLUSIVE: Ocean vs Ice
        tree.addSkill(Skill.builder("tide_ocean").name("Ocean's Fury").description("Command the raging sea.")
            .icon("≋").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-2, 2).cost(3)
            .requires("tide_strike").excludes("tide_ice", "tide_frost")
            .effect(SkillEffect.damageBoost(25)).effect(SkillEffect.special("wave", "Create damaging waves")).build());
        tree.addSkill(Skill.builder("tide_whirlpool").name("Whirlpool").description("Create devastating vortexes.")
            .icon("◯").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-1, 2).cost(3)
            .requires("tide_strike").excludes("tide_ice")
            .effect(SkillEffect.damageBoost(18)).effect(SkillEffect.special("vortex", "Pull enemies into whirlpool")).build());
        tree.addSkill(Skill.builder("tide_ice").name("Frozen Depths").description("Command ice and cold.")
            .icon("❄").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(0, 2).cost(3)
            .requires("tide_strike").excludes("tide_ocean", "tide_whirlpool")
            .effect(SkillEffect.damageBoost(15)).effect(SkillEffect.special("freeze", "Freeze water, slow enemies")).build());
        tree.addSkill(Skill.builder("tide_frost").name("Frost Armor").description("Encase in protective ice.")
            .icon("⛨").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(1, 2).cost(3)
            .requires("tide_scales").excludes("tide_ocean")
            .effect(SkillEffect.armorBoost(5)).effect(SkillEffect.special("ice_thorns", "Attackers take cold damage")).build());
        tree.addSkill(Skill.builder("tide_shell").name("Shell Defense").description("Impenetrable shell.")
            .icon("♦").tier(SkillTier.TIER_3).branch(SkillBranch.DEFENSE).position(2, 2).cost(3)
            .requires("tide_scales").effect(SkillEffect.armorBoost(6)).effect(SkillEffect.knockbackResistance(40)).build());
        tree.addSkill(Skill.builder("tide_jet").name("Water Jet").description("Propel with water.")
            .icon("»").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(4, 2).cost(3)
            .requires("tide_current").effect(SkillEffect.speedBoost(15)).effect(SkillEffect.special("jet", "Dash through water")).build());
        tree.addSkill(Skill.builder("tide_shark").name("Shark Companion").description("Call a shark ally.")
            .icon("⚜").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(5, 2).cost(3)
            .requires("tide_call").effect(SkillEffect.special("shark", "Summon shark companion")).build());
        
        // TIER 4
        tree.addSkill(Skill.builder("tide_tsunami").name("Tsunami").description("Unleash devastating waves.")
            .icon("≋").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE).position(-2, 3).cost(4)
            .requires("tide_ocean").effect(SkillEffect.damageBoost(30)).effect(SkillEffect.special("tsunami", "Massive wave attack")).build());
        tree.addSkill(Skill.builder("tide_glacier").name("Glacier Heart").description("Become living ice.")
            .icon("❄").tier(SkillTier.TIER_4).branch(SkillBranch.DEFENSE).position(1, 3).cost(4)
            .requires("tide_frost").effect(SkillEffect.armorBoost(6)).effect(SkillEffect.healthBoost(3)).build());
        tree.addSkill(Skill.builder("tide_kraken").name("Kraken's Grasp").description("Tentacles of the deep.")
            .icon("☠").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE).position(-1, 3).cost(4)
            .requires("tide_whirlpool").effect(SkillEffect.damageBoost(25)).effect(SkillEffect.special("tentacles", "Grab and crush enemies")).build());
        tree.addSkill(Skill.builder("tide_trident").name("Trident Master").description("Master of the trident.")
            .icon("⚔").tier(SkillTier.TIER_4).branch(SkillBranch.UTILITY).position(4, 3).cost(4)
            .requires("tide_jet").effect(SkillEffect.damageBoost(15)).effect(SkillEffect.special("trident", "Enhanced trident abilities")).build());
        
        // HYBRID
        tree.addSkill(Skill.builder("tide_hybrid_depths").name("Depths Walker").description("Master of all waters.")
            .icon("◈").tier(SkillTier.TIER_4).branch(SkillBranch.HYBRID).position(0, 3).cost(4)
            .requires("tide_ice", "tide_shell").effect(SkillEffect.damageBoost(12)).effect(SkillEffect.armorBoost(3)).effect(SkillEffect.healthBoost(2)).build());
        
        // SPECIALIST
        tree.addSkill(Skill.builder("tide_specialist_abyss").name("Abyssal Horror").description("Become a deep sea nightmare.")
            .icon("☠").tier(SkillTier.TIER_4).branch(SkillBranch.SPECIALIST).position(3, 3).cost(4)
            .requires("tide_ocean", "tide_whirlpool").excludes("tide_glacier")
            .effect(SkillEffect.damageBoost(40)).effect(SkillEffect.special("abyss", "Terror aura, pressure damage")).effect(SkillEffect.special("light_weak", "Weak in sunlight")).build());
        
        // SYNERGY
        tree.addSkill(Skill.builder("tide_synergy_storm").name("Storm Surge").description("Water and lightning combined.")
            .icon("◉").tier(SkillTier.TIER_4).branch(SkillBranch.SYNERGY_AD).position(2, 4).cost(4)
            .requires("tide_shell", "tide_shark").effect(SkillEffect.damageBoost(15)).effect(SkillEffect.armorBoost(3)).build());
        
        // TIER 5
        tree.addSkill(Skill.builder("tide_lord").name("Sea Lord").description("Ruler of the oceans.")
            .icon("♛").tier(SkillTier.TIER_5).branch(SkillBranch.CORE).position(1, 5).cost(5)
            .requires("tide_tsunami", "tide_glacier").effect(SkillEffect.damageBoost(20)).effect(SkillEffect.healthBoost(4)).effect(SkillEffect.special("sea_lord", "Command all sea life")).build());
        
        // FORBIDDEN
        tree.addSkill(Skill.builder("tide_forbidden_leviathan").name("Leviathan").description("Become the sea monster. Massive but slow.")
            .icon("☠").tier(SkillTier.TIER_5).branch(SkillBranch.FORBIDDEN).position(-1, 5).cost(5)
            .requires("tide_specialist_abyss").excludes("tide_ascended_spirit")
            .effect(SkillEffect.healthBoost(15)).effect(SkillEffect.damageBoost(40)).effect(SkillEffect.special("leviathan", "Massive size, destroy ships")).effect(SkillEffect.speedBoost(-20)).build());
        
        // ASCENDED
        tree.addSkill(Skill.builder("tide_ascended_spirit").name("Sea Spirit").description("Become one with the ocean.")
            .icon("⚜").tier(SkillTier.TIER_5).branch(SkillBranch.ASCENDED).position(3, 5).cost(5)
            .requires("tide_lord", "tide_trident").excludes("tide_forbidden_leviathan")
            .effect(SkillEffect.damageBoost(25)).effect(SkillEffect.speedBoost(25)).effect(SkillEffect.special("sea_spirit", "Water form, control tides")).build());
        
        TREES.put("tidecaller", tree);
    }


    // ==================== STARBORNE SKILL TREE ====================
    // Solar vs Lunar paths, Forbidden Black Hole, Ascended Celestial
    private static void registerStarborneTree() {
        SkillTreeData tree = new SkillTreeData("starborne", "Starborne");
        
        // TIER 1
        tree.addSkill(Skill.builder("star_light").name("Starlight").description("Channel celestial energy.")
            .icon("★").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(0, 0).cost(1)
            .effect(SkillEffect.resourceRegen(15)).effect(SkillEffect.special("glow", "Emit soft light")).build());
        tree.addSkill(Skill.builder("star_body").name("Celestial Body").description("Body touched by stars.")
            .icon("♥").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(2, 0).cost(1)
            .effect(SkillEffect.healthBoost(1)).effect(SkillEffect.special("void_resist", "-25% void damage")).build());
        tree.addSkill(Skill.builder("star_sight").name("Star Sight").description("See by starlight.")
            .icon("◯").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(4, 0).cost(1)
            .effect(SkillEffect.special("night_vision", "Perfect night vision")).build());
        
        // TIER 2
        tree.addSkill(Skill.builder("star_bolt").name("Star Bolt").description("Launch stellar projectiles.")
            .icon("⚔").tier(SkillTier.TIER_2).branch(SkillBranch.OFFENSE).position(-1, 1).cost(2)
            .requires("star_light").effect(SkillEffect.damageBoost(10)).effect(SkillEffect.special("star_bolt", "Ranged light attack")).build());
        tree.addSkill(Skill.builder("star_shield").name("Star Shield").description("Shield of starlight.")
            .icon("⛨").tier(SkillTier.TIER_2).branch(SkillBranch.DEFENSE).position(1, 1).cost(2)
            .requires("star_body").effect(SkillEffect.armorBoost(2)).effect(SkillEffect.special("light_shield", "Block darkness")).build());
        tree.addSkill(Skill.builder("star_step").name("Star Step").description("Move at light speed.")
            .icon("»").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(3, 1).cost(2)
            .requires("star_sight").effect(SkillEffect.speedBoost(10)).effect(SkillEffect.cooldownReduction(10)).build());
        tree.addSkill(Skill.builder("star_guide").name("Celestial Guide").description("Stars guide your path.")
            .icon("☆").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(5, 1).cost(2)
            .requires("star_sight").effect(SkillEffect.special("navigation", "Always know direction")).build());
        
        // TIER 3 - MUTUALLY EXCLUSIVE: Solar vs Lunar
        tree.addSkill(Skill.builder("star_solar").name("Solar Flare").description("Channel the sun's fury.")
            .icon("☀").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-2, 2).cost(3)
            .requires("star_bolt").excludes("star_lunar", "star_moon")
            .effect(SkillEffect.damageBoost(25)).effect(SkillEffect.fireDamage(15)).effect(SkillEffect.special("solar", "+50% damage in daylight")).build());
        tree.addSkill(Skill.builder("star_corona").name("Corona").description("Burning aura of light.")
            .icon("☼").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-1, 2).cost(3)
            .requires("star_bolt").excludes("star_lunar")
            .effect(SkillEffect.damageBoost(18)).effect(SkillEffect.special("burn_aura", "Damage nearby enemies")).build());
        tree.addSkill(Skill.builder("star_lunar").name("Lunar Grace").description("Channel the moon's mystery.")
            .icon("☾").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(0, 2).cost(3)
            .requires("star_bolt").excludes("star_solar", "star_corona")
            .effect(SkillEffect.damageBoost(15)).effect(SkillEffect.cooldownReduction(20)).effect(SkillEffect.special("lunar", "+50% power at night")).build());
        tree.addSkill(Skill.builder("star_moon").name("Moon Shield").description("Protective lunar energy.")
            .icon("◐").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(1, 2).cost(3)
            .requires("star_shield").excludes("star_solar")
            .effect(SkillEffect.armorBoost(4)).effect(SkillEffect.special("moon_shield", "Reflect projectiles at night")).build());
        tree.addSkill(Skill.builder("star_constellation").name("Constellation").description("Draw power from star patterns.")
            .icon("✦").tier(SkillTier.TIER_3).branch(SkillBranch.DEFENSE).position(2, 2).cost(3)
            .requires("star_shield").effect(SkillEffect.healthBoost(3)).effect(SkillEffect.special("constellation", "Bonus based on time")).build());
        tree.addSkill(Skill.builder("star_warp").name("Star Warp").description("Teleport via starlight.")
            .icon("★").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(4, 2).cost(3)
            .requires("star_step").effect(SkillEffect.cooldownReduction(25)).effect(SkillEffect.special("star_tp", "Long range teleport at night")).build());
        tree.addSkill(Skill.builder("star_summon").name("Star Familiar").description("Summon a star sprite.")
            .icon("✧").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(5, 2).cost(3)
            .requires("star_guide").effect(SkillEffect.special("star_sprite", "Summon light companion")).build());
        
        // TIER 4
        tree.addSkill(Skill.builder("star_supernova").name("Supernova").description("Explosive stellar power.")
            .icon("☀").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE).position(-2, 3).cost(4)
            .requires("star_solar").effect(SkillEffect.damageBoost(35)).effect(SkillEffect.special("supernova", "Massive AoE explosion")).build());
        tree.addSkill(Skill.builder("star_eclipse").name("Eclipse").description("Block out the light.")
            .icon("◑").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE).position(-1, 3).cost(4)
            .requires("star_corona").effect(SkillEffect.damageBoost(25)).effect(SkillEffect.special("eclipse", "Create darkness zone")).build());
        tree.addSkill(Skill.builder("star_nebula").name("Nebula Form").description("Become cosmic gas.")
            .icon("≋").tier(SkillTier.TIER_4).branch(SkillBranch.DEFENSE).position(1, 3).cost(4)
            .requires("star_moon").effect(SkillEffect.special("nebula", "Intangible form")).effect(SkillEffect.healthBoost(2)).build());
        tree.addSkill(Skill.builder("star_comet").name("Comet Rider").description("Ride a comet.")
            .icon("☄").tier(SkillTier.TIER_4).branch(SkillBranch.UTILITY).position(4, 3).cost(4)
            .requires("star_warp").effect(SkillEffect.speedBoost(30)).effect(SkillEffect.special("comet", "Fast travel on comet")).build());
        
        // HYBRID
        tree.addSkill(Skill.builder("star_hybrid_twilight").name("Twilight").description("Balance of sun and moon.")
            .icon("◈").tier(SkillTier.TIER_4).branch(SkillBranch.HYBRID).position(0, 3).cost(4)
            .requires("star_lunar", "star_constellation").effect(SkillEffect.damageBoost(15)).effect(SkillEffect.healthBoost(2)).effect(SkillEffect.cooldownReduction(15)).build());
        
        // SPECIALIST
        tree.addSkill(Skill.builder("star_specialist_pulsar").name("Pulsar").description("Rapid energy bursts. Unstable.")
            .icon("☠").tier(SkillTier.TIER_4).branch(SkillBranch.SPECIALIST).position(3, 3).cost(4)
            .requires("star_solar", "star_corona").excludes("star_nebula")
            .effect(SkillEffect.damageBoost(45)).effect(SkillEffect.attackSpeed(30)).effect(SkillEffect.special("pulsar", "Rapid fire attacks")).effect(SkillEffect.healthBoost(-2)).build());
        
        // SYNERGY
        tree.addSkill(Skill.builder("star_synergy_cosmos").name("Cosmic Harmony").description("All celestial powers aligned.")
            .icon("◉").tier(SkillTier.TIER_4).branch(SkillBranch.SYNERGY_DU).position(2, 4).cost(4)
            .requires("star_constellation", "star_summon").effect(SkillEffect.healthBoost(3)).effect(SkillEffect.special("cosmic", "Bonuses at all times")).build());
        
        // TIER 5
        tree.addSkill(Skill.builder("star_avatar").name("Star Avatar").description("Become a living star.")
            .icon("★").tier(SkillTier.TIER_5).branch(SkillBranch.CORE).position(1, 5).cost(5)
            .requires("star_supernova", "star_nebula").effect(SkillEffect.damageBoost(25)).effect(SkillEffect.healthBoost(3)).effect(SkillEffect.special("star_form", "Transform into star")).build());
        
        // FORBIDDEN
        tree.addSkill(Skill.builder("star_forbidden_blackhole").name("Black Hole").description("Become a singularity. Consume everything.")
            .icon("☠").tier(SkillTier.TIER_5).branch(SkillBranch.FORBIDDEN).position(-1, 5).cost(5)
            .requires("star_specialist_pulsar").excludes("star_ascended_celestial")
            .effect(SkillEffect.damageBoost(60)).effect(SkillEffect.special("black_hole", "Pull and destroy everything")).effect(SkillEffect.special("consume_light", "No light, hurt allies")).build());
        
        // ASCENDED
        tree.addSkill(Skill.builder("star_ascended_celestial").name("Celestial Being").description("Transcend to the stars.")
            .icon("⚜").tier(SkillTier.TIER_5).branch(SkillBranch.ASCENDED).position(3, 5).cost(5)
            .requires("star_avatar", "star_comet").excludes("star_forbidden_blackhole")
            .effect(SkillEffect.damageBoost(30)).effect(SkillEffect.speedBoost(25)).effect(SkillEffect.special("celestial", "Fly among stars, immune to void")).build());
        
        TREES.put("starborne", tree);
    }


    // ==================== VEILBORN SKILL TREE ====================
    // Reality vs Illusion paths, Forbidden Veil Tear, Ascended Veil Master
    private static void registerVeilbornTree() {
        SkillTreeData tree = new SkillTreeData("veilborn", "Veilborn");
        
        // TIER 1
        tree.addSkill(Skill.builder("veil_touch").name("Veil Touch").description("Touch the barrier between worlds.")
            .icon("◯").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(0, 0).cost(1)
            .effect(SkillEffect.resourceRegen(12)).effect(SkillEffect.special("veil_sense", "Sense dimensional rifts")).build());
        tree.addSkill(Skill.builder("veil_body").name("Veil Body").description("Partially exist between realities.")
            .icon("♥").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(2, 0).cost(1)
            .effect(SkillEffect.healthBoost(1)).effect(SkillEffect.special("phase_resist", "Resist phase effects")).build());
        tree.addSkill(Skill.builder("veil_sight").name("Veil Sight").description("See through the veil.")
            .icon("★").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(4, 0).cost(1)
            .effect(SkillEffect.special("true_sight", "See invisible and illusions")).build());
        
        // TIER 2
        tree.addSkill(Skill.builder("veil_strike").name("Veil Strike").description("Attack through dimensions.")
            .icon("⚔").tier(SkillTier.TIER_2).branch(SkillBranch.OFFENSE).position(-1, 1).cost(2)
            .requires("veil_touch").effect(SkillEffect.damageBoost(10)).effect(SkillEffect.special("phase_hit", "Attacks ignore armor")).build());
        tree.addSkill(Skill.builder("veil_shield").name("Veil Shield").description("Shield from another dimension.")
            .icon("⛨").tier(SkillTier.TIER_2).branch(SkillBranch.DEFENSE).position(1, 1).cost(2)
            .requires("veil_body").effect(SkillEffect.armorBoost(2)).effect(SkillEffect.special("phase_block", "Block phase attacks")).build());
        tree.addSkill(Skill.builder("veil_step").name("Veil Step").description("Step between realities.")
            .icon("»").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(3, 1).cost(2)
            .requires("veil_sight").effect(SkillEffect.speedBoost(8)).effect(SkillEffect.special("short_phase", "Brief intangibility")).build());
        tree.addSkill(Skill.builder("veil_mirror").name("Mirror Image").description("Create illusory copies.")
            .icon("◐").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(5, 1).cost(2)
            .requires("veil_sight").effect(SkillEffect.special("mirror", "Create decoy images")).build());
        
        // TIER 3 - MUTUALLY EXCLUSIVE: Reality vs Illusion
        tree.addSkill(Skill.builder("veil_reality").name("Reality Anchor").description("Solidify reality around you.")
            .icon("♦").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-2, 2).cost(3)
            .requires("veil_strike").excludes("veil_illusion", "veil_phantom")
            .effect(SkillEffect.damageBoost(20)).effect(SkillEffect.special("dispel", "Dispel illusions and magic")).build());
        tree.addSkill(Skill.builder("veil_rend").name("Reality Rend").description("Tear holes in reality.")
            .icon("☠").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-1, 2).cost(3)
            .requires("veil_strike").excludes("veil_illusion")
            .effect(SkillEffect.damageBoost(25)).effect(SkillEffect.special("rend", "Create damaging rifts")).build());
        tree.addSkill(Skill.builder("veil_illusion").name("Grand Illusion").description("Master of deception.")
            .icon("◐").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(0, 2).cost(3)
            .requires("veil_strike").excludes("veil_reality", "veil_rend")
            .effect(SkillEffect.damageBoost(12)).effect(SkillEffect.special("illusion_master", "Create complex illusions")).build());
        tree.addSkill(Skill.builder("veil_phantom").name("Phantom Form").description("Become partially unreal.")
            .icon("≋").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(1, 2).cost(3)
            .requires("veil_shield").excludes("veil_reality")
            .effect(SkillEffect.special("phantom", "30% chance to phase through attacks")).effect(SkillEffect.speedBoost(10)).build());
        tree.addSkill(Skill.builder("veil_anchor").name("Dimensional Anchor").description("Lock yourself to reality.")
            .icon("⛨").tier(SkillTier.TIER_3).branch(SkillBranch.DEFENSE).position(2, 2).cost(3)
            .requires("veil_shield").effect(SkillEffect.armorBoost(5)).effect(SkillEffect.knockbackResistance(40)).build());
        tree.addSkill(Skill.builder("veil_walk").name("Veil Walk").description("Walk through the veil.")
            .icon("★").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(4, 2).cost(3)
            .requires("veil_step").effect(SkillEffect.cooldownReduction(20)).effect(SkillEffect.special("veil_walk", "Phase through walls")).build());
        tree.addSkill(Skill.builder("veil_army").name("Phantom Army").description("Create illusory soldiers.")
            .icon("⚜").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(5, 2).cost(3)
            .requires("veil_mirror").effect(SkillEffect.special("phantom_army", "Summon illusion fighters")).build());
        
        // TIER 4
        tree.addSkill(Skill.builder("veil_shatter").name("Reality Shatter").description("Break reality itself.")
            .icon("☠").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE).position(-2, 3).cost(4)
            .requires("veil_rend").effect(SkillEffect.damageBoost(35)).effect(SkillEffect.special("shatter", "Massive dimensional damage")).build());
        tree.addSkill(Skill.builder("veil_nightmare").name("Nightmare").description("Make illusions real.")
            .icon("◐").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE).position(-1, 3).cost(4)
            .requires("veil_illusion").effect(SkillEffect.damageBoost(25)).effect(SkillEffect.special("nightmare", "Illusions deal real damage")).build());
        tree.addSkill(Skill.builder("veil_fortress").name("Veil Fortress").description("Impenetrable dimensional barrier.")
            .icon("⛨").tier(SkillTier.TIER_4).branch(SkillBranch.DEFENSE).position(1, 3).cost(4)
            .requires("veil_anchor").effect(SkillEffect.armorBoost(6)).effect(SkillEffect.healthBoost(3)).build());
        tree.addSkill(Skill.builder("veil_gate").name("Veil Gate").description("Open gates between worlds.")
            .icon("◯").tier(SkillTier.TIER_4).branch(SkillBranch.UTILITY).position(4, 3).cost(4)
            .requires("veil_walk").effect(SkillEffect.special("gate", "Create portals")).effect(SkillEffect.cooldownReduction(15)).build());
        
        // HYBRID
        tree.addSkill(Skill.builder("veil_hybrid_between").name("Between Worlds").description("Exist in multiple realities.")
            .icon("◈").tier(SkillTier.TIER_4).branch(SkillBranch.HYBRID).position(0, 3).cost(4)
            .requires("veil_phantom", "veil_anchor").effect(SkillEffect.damageBoost(15)).effect(SkillEffect.armorBoost(3)).effect(SkillEffect.special("dual_exist", "Partially in two places")).build());
        
        // SPECIALIST
        tree.addSkill(Skill.builder("veil_specialist_unreal").name("Unreal").description("Become completely unreal. Devastating but fragile.")
            .icon("☠").tier(SkillTier.TIER_4).branch(SkillBranch.SPECIALIST).position(3, 3).cost(4)
            .requires("veil_illusion", "veil_phantom").excludes("veil_fortress")
            .effect(SkillEffect.damageBoost(40)).effect(SkillEffect.special("unreal", "Phase through everything")).effect(SkillEffect.healthBoost(-3)).build());
        
        // SYNERGY
        tree.addSkill(Skill.builder("veil_synergy_trickster").name("Veil Trickster").description("Master of deception and escape.")
            .icon("◉").tier(SkillTier.TIER_4).branch(SkillBranch.SYNERGY_AU).position(2, 4).cost(4)
            .requires("veil_phantom", "veil_army").effect(SkillEffect.speedBoost(15)).effect(SkillEffect.special("trickster", "Swap places with illusions")).build());
        
        // TIER 5
        tree.addSkill(Skill.builder("veil_master").name("Veil Master").description("Master of the barrier between worlds.")
            .icon("◎").tier(SkillTier.TIER_5).branch(SkillBranch.CORE).position(1, 5).cost(5)
            .requires("veil_shatter", "veil_fortress").effect(SkillEffect.damageBoost(20)).effect(SkillEffect.healthBoost(3)).effect(SkillEffect.special("veil_master", "Control the veil")).build());
        
        // FORBIDDEN
        tree.addSkill(Skill.builder("veil_forbidden_tear").name("Veil Tear").description("Permanently tear the veil. Chaos ensues.")
            .icon("☠").tier(SkillTier.TIER_5).branch(SkillBranch.FORBIDDEN).position(-1, 5).cost(5)
            .requires("veil_specialist_unreal").excludes("veil_ascended_guardian")
            .effect(SkillEffect.damageBoost(55)).effect(SkillEffect.special("veil_tear", "Create permanent rifts")).effect(SkillEffect.special("unstable", "Random dimensional effects")).build());
        
        // ASCENDED
        tree.addSkill(Skill.builder("veil_ascended_guardian").name("Veil Guardian").description("Protect the barrier between worlds.")
            .icon("⚜").tier(SkillTier.TIER_5).branch(SkillBranch.ASCENDED).position(3, 5).cost(5)
            .requires("veil_master", "veil_gate").excludes("veil_forbidden_tear")
            .effect(SkillEffect.damageBoost(25)).effect(SkillEffect.armorBoost(5)).effect(SkillEffect.special("guardian", "Seal rifts, banish entities")).build());
        
        TREES.put("veilborn", tree);
    }


    // ==================== ETHEREAL SKILL TREE ====================
    // Spirit vs Possession paths, Forbidden Soul Eater, Ascended Guardian Spirit
    private static void registerEtherealTree() {
        SkillTreeData tree = new SkillTreeData("ethereal", "Ethereal");
        
        // TIER 1
        tree.addSkill(Skill.builder("ether_form").name("Ethereal Form").description("Partially incorporeal.")
            .icon("≋").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(0, 0).cost(1)
            .effect(SkillEffect.special("phase", "10% chance to phase through attacks")).build());
        tree.addSkill(Skill.builder("ether_float").name("Float").description("Hover above the ground.")
            .icon("»").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(2, 0).cost(1)
            .effect(SkillEffect.special("hover", "Slow fall, ignore terrain")).effect(SkillEffect.speedBoost(3)).build());
        tree.addSkill(Skill.builder("ether_sense").name("Spirit Sense").description("Sense souls and spirits.")
            .icon("★").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(4, 0).cost(1)
            .effect(SkillEffect.special("soul_sense", "Detect living and undead")).build());
        
        // TIER 2
        tree.addSkill(Skill.builder("ether_touch").name("Spirit Touch").description("Attacks affect the soul.")
            .icon("⚔").tier(SkillTier.TIER_2).branch(SkillBranch.OFFENSE).position(-1, 1).cost(2)
            .requires("ether_form").effect(SkillEffect.damageBoost(8)).effect(SkillEffect.special("soul_damage", "Bypass physical armor")).build());
        tree.addSkill(Skill.builder("ether_shield").name("Spirit Shield").description("Shield of spiritual energy.")
            .icon("⛨").tier(SkillTier.TIER_2).branch(SkillBranch.DEFENSE).position(1, 1).cost(2)
            .requires("ether_form").effect(SkillEffect.armorBoost(2)).effect(SkillEffect.special("spirit_block", "Block spirit attacks")).build());
        tree.addSkill(Skill.builder("ether_walk").name("Spirit Walk").description("Walk through solid matter.")
            .icon("◐").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(3, 1).cost(2)
            .requires("ether_float").effect(SkillEffect.special("phase_walk", "Walk through thin walls")).effect(SkillEffect.cooldownReduction(10)).build());
        tree.addSkill(Skill.builder("ether_speak").name("Spirit Speech").description("Communicate with spirits.")
            .icon("☽").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(5, 1).cost(2)
            .requires("ether_sense").effect(SkillEffect.special("spirit_talk", "Talk to ghosts")).build());
        
        // TIER 3 - MUTUALLY EXCLUSIVE: Spirit vs Possession
        tree.addSkill(Skill.builder("ether_spirit").name("Pure Spirit").description("Embrace spiritual purity.")
            .icon("✧").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-2, 2).cost(3)
            .requires("ether_touch").excludes("ether_possess", "ether_haunt")
            .effect(SkillEffect.damageBoost(20)).effect(SkillEffect.special("holy", "Extra damage to undead")).build());
        tree.addSkill(Skill.builder("ether_blast").name("Spirit Blast").description("Explosive spiritual energy.")
            .icon("☀").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-1, 2).cost(3)
            .requires("ether_touch").excludes("ether_possess")
            .effect(SkillEffect.damageBoost(25)).effect(SkillEffect.special("spirit_aoe", "AoE spirit damage")).build());
        tree.addSkill(Skill.builder("ether_possess").name("Possession").description("Take control of others.")
            .icon("◐").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(0, 2).cost(3)
            .requires("ether_touch").excludes("ether_spirit", "ether_blast")
            .effect(SkillEffect.special("possess", "Control weak mobs")).effect(SkillEffect.damageBoost(10)).build());
        tree.addSkill(Skill.builder("ether_haunt").name("Haunt").description("Terrify and weaken enemies.")
            .icon("☠").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(1, 2).cost(3)
            .requires("ether_shield").excludes("ether_spirit")
            .effect(SkillEffect.special("haunt", "Fear and weaken enemies")).effect(SkillEffect.damageBoost(12)).build());
        tree.addSkill(Skill.builder("ether_intangible").name("Intangible").description("Become fully incorporeal.")
            .icon("≋").tier(SkillTier.TIER_3).branch(SkillBranch.DEFENSE).position(2, 2).cost(3)
            .requires("ether_shield").effect(SkillEffect.special("intangible", "50% phase chance")).effect(SkillEffect.armorBoost(2)).build());
        tree.addSkill(Skill.builder("ether_flight").name("Spirit Flight").description("Fly as a spirit.")
            .icon("»").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(4, 2).cost(3)
            .requires("ether_walk").effect(SkillEffect.special("spirit_fly", "True flight")).effect(SkillEffect.speedBoost(15)).build());
        tree.addSkill(Skill.builder("ether_summon").name("Summon Spirit").description("Call spirits to aid you.")
            .icon("⚜").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(5, 2).cost(3)
            .requires("ether_speak").effect(SkillEffect.special("spirit_ally", "Summon ghost companion")).build());
        
        // TIER 4
        tree.addSkill(Skill.builder("ether_purify").name("Purifying Light").description("Cleanse with spiritual fire.")
            .icon("☀").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE).position(-2, 3).cost(4)
            .requires("ether_spirit").effect(SkillEffect.damageBoost(30)).effect(SkillEffect.special("purify", "Destroy undead instantly")).build());
        tree.addSkill(Skill.builder("ether_dominate").name("Dominate").description("Control powerful creatures.")
            .icon("♛").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE).position(-1, 3).cost(4)
            .requires("ether_possess").effect(SkillEffect.special("dominate", "Control any mob")).effect(SkillEffect.damageBoost(15)).build());
        tree.addSkill(Skill.builder("ether_ghost").name("Ghost Form").description("Become a true ghost.")
            .icon("≋").tier(SkillTier.TIER_4).branch(SkillBranch.DEFENSE).position(1, 3).cost(4)
            .requires("ether_intangible").effect(SkillEffect.special("ghost", "Full intangibility on demand")).effect(SkillEffect.healthBoost(2)).build());
        tree.addSkill(Skill.builder("ether_realm").name("Spirit Realm").description("Enter the spirit world.")
            .icon("◯").tier(SkillTier.TIER_4).branch(SkillBranch.UTILITY).position(4, 3).cost(4)
            .requires("ether_flight").effect(SkillEffect.special("spirit_realm", "Enter parallel dimension")).effect(SkillEffect.cooldownReduction(20)).build());
        
        // HYBRID
        tree.addSkill(Skill.builder("ether_hybrid_medium").name("Medium").description("Bridge between worlds.")
            .icon("◈").tier(SkillTier.TIER_4).branch(SkillBranch.HYBRID).position(0, 3).cost(4)
            .requires("ether_haunt", "ether_intangible").effect(SkillEffect.damageBoost(15)).effect(SkillEffect.healthBoost(2)).effect(SkillEffect.special("medium", "Channel spirits")).build());
        
        // SPECIALIST
        tree.addSkill(Skill.builder("ether_specialist_wraith").name("Wraith").description("Become a vengeful spirit.")
            .icon("☠").tier(SkillTier.TIER_4).branch(SkillBranch.SPECIALIST).position(3, 3).cost(4)
            .requires("ether_possess", "ether_haunt").excludes("ether_ghost")
            .effect(SkillEffect.damageBoost(40)).effect(SkillEffect.special("wraith", "Drain life, cause fear")).effect(SkillEffect.special("sunlight_weak", "Weak in sunlight")).build());
        
        // SYNERGY
        tree.addSkill(Skill.builder("ether_synergy_guide").name("Spirit Guide").description("Guide and protect spirits.")
            .icon("◉").tier(SkillTier.TIER_4).branch(SkillBranch.SYNERGY_DU).position(2, 4).cost(4)
            .requires("ether_intangible", "ether_summon").effect(SkillEffect.healthBoost(3)).effect(SkillEffect.special("guide", "Spirits fight for you")).build());
        
        // TIER 5
        tree.addSkill(Skill.builder("ether_ascendant").name("Ascendant Spirit").description("Transcend physical form.")
            .icon("✧").tier(SkillTier.TIER_5).branch(SkillBranch.CORE).position(1, 5).cost(5)
            .requires("ether_purify", "ether_ghost").effect(SkillEffect.damageBoost(20)).effect(SkillEffect.healthBoost(3)).effect(SkillEffect.special("ascendant", "Pure spiritual being")).build());
        
        // FORBIDDEN
        tree.addSkill(Skill.builder("ether_forbidden_eater").name("Soul Eater").description("Consume souls for power. Corrupting.")
            .icon("☠").tier(SkillTier.TIER_5).branch(SkillBranch.FORBIDDEN).position(-1, 5).cost(5)
            .requires("ether_specialist_wraith").excludes("ether_ascended_guardian")
            .effect(SkillEffect.damageBoost(50)).effect(SkillEffect.special("soul_eat", "Consume souls, grow stronger")).effect(SkillEffect.special("corruption", "Slowly become evil")).build());
        
        // ASCENDED
        tree.addSkill(Skill.builder("ether_ascended_guardian").name("Guardian Spirit").description("Protect the living and dead.")
            .icon("⚜").tier(SkillTier.TIER_5).branch(SkillBranch.ASCENDED).position(3, 5).cost(5)
            .requires("ether_ascendant", "ether_realm").excludes("ether_forbidden_eater")
            .effect(SkillEffect.damageBoost(25)).effect(SkillEffect.healthBoost(4)).effect(SkillEffect.special("guardian_spirit", "Protect allies, guide souls")).build());
        
        TREES.put("ethereal", tree);
    }


    // ==================== CRYSTALLINE SKILL TREE ====================
    // Prism vs Geode paths, Forbidden Shatter, Ascended Living Crystal
    private static void registerCrystallineTree() {
        SkillTreeData tree = new SkillTreeData("crystalline", "Crystalline");
        
        // TIER 1
        tree.addSkill(Skill.builder("crystal_core").name("Crystal Core").description("Heart of living crystal.")
            .icon("◇").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(0, 0).cost(1)
            .effect(SkillEffect.armorBoost(2)).effect(SkillEffect.special("light_refract", "Refract light")).build());
        tree.addSkill(Skill.builder("crystal_skin").name("Crystal Skin").description("Skin of gemstone.")
            .icon("♦").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(2, 0).cost(1)
            .effect(SkillEffect.armorBoost(1)).effect(SkillEffect.healthBoost(1)).build());
        tree.addSkill(Skill.builder("crystal_sight").name("Crystal Sight").description("See through crystal clarity.")
            .icon("★").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(4, 0).cost(1)
            .effect(SkillEffect.special("crystal_vision", "See ores and gems")).build());
        
        // TIER 2
        tree.addSkill(Skill.builder("crystal_shard").name("Crystal Shard").description("Launch crystal projectiles.")
            .icon("⚔").tier(SkillTier.TIER_2).branch(SkillBranch.OFFENSE).position(-1, 1).cost(2)
            .requires("crystal_core").effect(SkillEffect.damageBoost(10)).effect(SkillEffect.special("shard", "Ranged crystal attack")).build());
        tree.addSkill(Skill.builder("crystal_armor").name("Crystal Armor").description("Grow protective crystals.")
            .icon("⛨").tier(SkillTier.TIER_2).branch(SkillBranch.DEFENSE).position(1, 1).cost(2)
            .requires("crystal_skin").effect(SkillEffect.armorBoost(3)).effect(SkillEffect.special("thorns", "Reflect melee damage")).build());
        tree.addSkill(Skill.builder("crystal_light").name("Crystal Light").description("Focus light through crystals.")
            .icon("☀").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(3, 1).cost(2)
            .requires("crystal_sight").effect(SkillEffect.special("light_beam", "Create light beams")).effect(SkillEffect.resourceRegen(10)).build());
        tree.addSkill(Skill.builder("crystal_grow").name("Crystal Growth").description("Grow crystals from nothing.")
            .icon("◇").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(5, 1).cost(2)
            .requires("crystal_sight").effect(SkillEffect.special("grow_crystal", "Create crystal blocks")).build());
        
        // TIER 3 - MUTUALLY EXCLUSIVE: Prism vs Geode
        tree.addSkill(Skill.builder("crystal_prism").name("Prism Master").description("Bend light into weapons.")
            .icon("☀").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-2, 2).cost(3)
            .requires("crystal_shard").excludes("crystal_geode", "crystal_dense")
            .effect(SkillEffect.damageBoost(25)).effect(SkillEffect.special("laser", "Fire concentrated light")).build());
        tree.addSkill(Skill.builder("crystal_rainbow").name("Rainbow Blast").description("Multicolored light attack.")
            .icon("★").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-1, 2).cost(3)
            .requires("crystal_shard").excludes("crystal_geode")
            .effect(SkillEffect.damageBoost(20)).effect(SkillEffect.special("rainbow", "Random elemental damage")).build());
        tree.addSkill(Skill.builder("crystal_geode").name("Geode Heart").description("Dense crystal interior.")
            .icon("♦").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(0, 2).cost(3)
            .requires("crystal_shard").excludes("crystal_prism", "crystal_rainbow")
            .effect(SkillEffect.armorBoost(5)).effect(SkillEffect.healthBoost(2)).build());
        tree.addSkill(Skill.builder("crystal_dense").name("Dense Crystal").description("Incredibly hard crystal.")
            .icon("⛨").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(1, 2).cost(3)
            .requires("crystal_armor").excludes("crystal_prism")
            .effect(SkillEffect.armorBoost(6)).effect(SkillEffect.knockbackResistance(40)).build());
        tree.addSkill(Skill.builder("crystal_reflect").name("Crystal Reflection").description("Reflect attacks back.")
            .icon("◐").tier(SkillTier.TIER_3).branch(SkillBranch.DEFENSE).position(2, 2).cost(3)
            .requires("crystal_armor").effect(SkillEffect.special("reflect", "Reflect 30% projectiles")).effect(SkillEffect.armorBoost(2)).build());
        tree.addSkill(Skill.builder("crystal_resonance").name("Crystal Resonance").description("Vibrate at powerful frequencies.")
            .icon("≋").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(4, 2).cost(3)
            .requires("crystal_light").effect(SkillEffect.special("resonance", "Shatter glass, stun enemies")).effect(SkillEffect.cooldownReduction(15)).build());
        tree.addSkill(Skill.builder("crystal_construct").name("Crystal Construct").description("Build with crystals.")
            .icon("⚜").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(5, 2).cost(3)
            .requires("crystal_grow").effect(SkillEffect.special("construct", "Create crystal structures")).build());
        
        // TIER 4
        tree.addSkill(Skill.builder("crystal_laser").name("Death Ray").description("Concentrated light beam.")
            .icon("☠").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE).position(-2, 3).cost(4)
            .requires("crystal_prism").effect(SkillEffect.damageBoost(35)).effect(SkillEffect.special("death_ray", "Piercing light beam")).build());
        tree.addSkill(Skill.builder("crystal_fortress").name("Crystal Fortress").description("Impenetrable crystal shell.")
            .icon("⛨").tier(SkillTier.TIER_4).branch(SkillBranch.DEFENSE).position(1, 3).cost(4)
            .requires("crystal_dense").effect(SkillEffect.armorBoost(8)).effect(SkillEffect.healthBoost(3)).build());
        tree.addSkill(Skill.builder("crystal_golem").name("Crystal Golem").description("Create a crystal servant.")
            .icon("⚜").tier(SkillTier.TIER_4).branch(SkillBranch.UTILITY).position(4, 3).cost(4)
            .requires("crystal_construct").effect(SkillEffect.special("crystal_golem", "Summon crystal golem")).build());
        
        // HYBRID
        tree.addSkill(Skill.builder("crystal_hybrid_facet").name("Faceted Being").description("Many sides, many abilities.")
            .icon("◈").tier(SkillTier.TIER_4).branch(SkillBranch.HYBRID).position(0, 3).cost(4)
            .requires("crystal_geode", "crystal_reflect").effect(SkillEffect.damageBoost(15)).effect(SkillEffect.armorBoost(4)).effect(SkillEffect.special("facet", "Random bonus each hit")).build());
        
        // SPECIALIST
        tree.addSkill(Skill.builder("crystal_specialist_glass").name("Glass Cannon").description("Maximum offense, fragile.")
            .icon("☠").tier(SkillTier.TIER_4).branch(SkillBranch.SPECIALIST).position(3, 3).cost(4)
            .requires("crystal_prism", "crystal_rainbow").excludes("crystal_fortress")
            .effect(SkillEffect.damageBoost(50)).effect(SkillEffect.special("glass", "Massive damage, very fragile")).effect(SkillEffect.armorBoost(-5)).build());
        
        // SYNERGY
        tree.addSkill(Skill.builder("crystal_synergy_mirror").name("Mirror Master").description("Control reflections.")
            .icon("◉").tier(SkillTier.TIER_4).branch(SkillBranch.SYNERGY_DU).position(2, 4).cost(4)
            .requires("crystal_reflect", "crystal_resonance").effect(SkillEffect.armorBoost(3)).effect(SkillEffect.special("mirror", "Create mirror images")).build());
        
        // TIER 5
        tree.addSkill(Skill.builder("crystal_perfect").name("Perfect Crystal").description("Flawless crystalline form.")
            .icon("◇").tier(SkillTier.TIER_5).branch(SkillBranch.CORE).position(1, 5).cost(5)
            .requires("crystal_laser", "crystal_fortress").effect(SkillEffect.damageBoost(20)).effect(SkillEffect.armorBoost(5)).effect(SkillEffect.special("perfect", "Immune to shatter")).build());
        
        // FORBIDDEN
        tree.addSkill(Skill.builder("crystal_forbidden_shatter").name("Shatter All").description("Shatter everything, including yourself.")
            .icon("☠").tier(SkillTier.TIER_5).branch(SkillBranch.FORBIDDEN).position(-1, 5).cost(5)
            .requires("crystal_specialist_glass").excludes("crystal_ascended_living")
            .effect(SkillEffect.damageBoost(60)).effect(SkillEffect.special("shatter_all", "Destroy all crystals in area")).effect(SkillEffect.special("self_damage", "Take damage when shattering")).build());
        
        // ASCENDED
        tree.addSkill(Skill.builder("crystal_ascended_living").name("Living Crystal").description("Become pure living crystal.")
            .icon("⚜").tier(SkillTier.TIER_5).branch(SkillBranch.ASCENDED).position(3, 5).cost(5)
            .requires("crystal_perfect", "crystal_golem").excludes("crystal_forbidden_shatter")
            .effect(SkillEffect.damageBoost(25)).effect(SkillEffect.armorBoost(6)).effect(SkillEffect.special("living_crystal", "Regenerate from crystal, immune to shatter")).build());
        
        TREES.put("crystalline", tree);
    }


    // ==================== MYCOMORPH SKILL TREE ====================
    // Spore vs Symbiote paths, Forbidden Hive Mind, Ascended Forest Network
    private static void registerMycomorphTree() {
        SkillTreeData tree = new SkillTreeData("mycomorph", "Mycomorph");
        
        // TIER 1
        tree.addSkill(Skill.builder("myco_spore").name("Spore Cloud").description("Release defensive spores.")
            .icon("◯").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(0, 0).cost(1)
            .effect(SkillEffect.special("spore_cloud", "Poison nearby enemies")).build());
        tree.addSkill(Skill.builder("myco_body").name("Fungal Body").description("Body of living fungus.")
            .icon("♥").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(2, 0).cost(1)
            .effect(SkillEffect.healthBoost(2)).effect(SkillEffect.special("poison_immune", "Immune to poison")).build());
        tree.addSkill(Skill.builder("myco_sense").name("Mycelium Sense").description("Feel through the network.")
            .icon("★").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(4, 0).cost(1)
            .effect(SkillEffect.special("network_sense", "Sense through mycelium")).build());
        
        // TIER 2
        tree.addSkill(Skill.builder("myco_toxic").name("Toxic Spores").description("Deadly poisonous spores.")
            .icon("☠").tier(SkillTier.TIER_2).branch(SkillBranch.OFFENSE).position(-1, 1).cost(2)
            .requires("myco_spore").effect(SkillEffect.damageBoost(8)).effect(SkillEffect.special("poison", "Strong poison on hit")).build());
        tree.addSkill(Skill.builder("myco_cap").name("Mushroom Cap").description("Protective cap shield.")
            .icon("⛨").tier(SkillTier.TIER_2).branch(SkillBranch.DEFENSE).position(1, 1).cost(2)
            .requires("myco_body").effect(SkillEffect.armorBoost(3)).effect(SkillEffect.special("spore_shield", "Release spores when hit")).build());
        tree.addSkill(Skill.builder("myco_spread").name("Spread").description("Spread mycelium network.")
            .icon("≋").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(3, 1).cost(2)
            .requires("myco_sense").effect(SkillEffect.special("spread", "Create mycelium blocks")).effect(SkillEffect.resourceRegen(15)).build());
        tree.addSkill(Skill.builder("myco_decompose").name("Decompose").description("Break down organic matter.")
            .icon("◐").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(5, 1).cost(2)
            .requires("myco_sense").effect(SkillEffect.special("decompose", "Destroy plants, heal from corpses")).build());
        
        // TIER 3 - MUTUALLY EXCLUSIVE: Spore vs Symbiote
        tree.addSkill(Skill.builder("myco_plague").name("Plague Spores").description("Devastating disease spores.")
            .icon("☠").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-2, 2).cost(3)
            .requires("myco_toxic").excludes("myco_symbiote", "myco_bond")
            .effect(SkillEffect.damageBoost(25)).effect(SkillEffect.special("plague", "Spread disease")).build());
        tree.addSkill(Skill.builder("myco_hallucinate").name("Hallucinogenic").description("Mind-altering spores.")
            .icon("◐").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-1, 2).cost(3)
            .requires("myco_toxic").excludes("myco_symbiote")
            .effect(SkillEffect.damageBoost(15)).effect(SkillEffect.special("hallucinate", "Confuse enemies")).build());
        tree.addSkill(Skill.builder("myco_symbiote").name("Symbiotic Bond").description("Bond with other creatures.")
            .icon("♥").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(0, 2).cost(3)
            .requires("myco_toxic").excludes("myco_plague", "myco_hallucinate")
            .effect(SkillEffect.healthBoost(3)).effect(SkillEffect.special("symbiote", "Buff nearby allies")).build());
        tree.addSkill(Skill.builder("myco_bond").name("Fungal Bond").description("Share health with network.")
            .icon("✚").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(1, 2).cost(3)
            .requires("myco_cap").excludes("myco_plague")
            .effect(SkillEffect.special("bond", "Share damage with mycelium")).effect(SkillEffect.healthBoost(2)).build());
        tree.addSkill(Skill.builder("myco_regenerate").name("Regeneration").description("Rapidly regrow damage.")
            .icon("✚").tier(SkillTier.TIER_3).branch(SkillBranch.DEFENSE).position(2, 2).cost(3)
            .requires("myco_cap").effect(SkillEffect.special("regen", "Fast health regeneration")).effect(SkillEffect.healthBoost(2)).build());
        tree.addSkill(Skill.builder("myco_network").name("Network Travel").description("Travel through mycelium.")
            .icon("»").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(4, 2).cost(3)
            .requires("myco_spread").effect(SkillEffect.special("network_tp", "Teleport through mycelium")).effect(SkillEffect.speedBoost(10)).build());
        tree.addSkill(Skill.builder("myco_colony").name("Fungal Colony").description("Create fungal minions.")
            .icon("⚜").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(5, 2).cost(3)
            .requires("myco_decompose").effect(SkillEffect.special("colony", "Summon fungal creatures")).build());
        
        // TIER 4
        tree.addSkill(Skill.builder("myco_pandemic").name("Pandemic").description("Unstoppable disease spread.")
            .icon("☠").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE).position(-2, 3).cost(4)
            .requires("myco_plague").effect(SkillEffect.damageBoost(35)).effect(SkillEffect.special("pandemic", "Disease spreads infinitely")).build());
        tree.addSkill(Skill.builder("myco_immortal").name("Immortal Growth").description("Cannot truly die.")
            .icon("✚").tier(SkillTier.TIER_4).branch(SkillBranch.DEFENSE).position(1, 3).cost(4)
            .requires("myco_regenerate").effect(SkillEffect.healthBoost(5)).effect(SkillEffect.special("immortal", "Revive from mycelium")).build());
        tree.addSkill(Skill.builder("myco_forest").name("Fungal Forest").description("Create a fungal biome.")
            .icon("♣").tier(SkillTier.TIER_4).branch(SkillBranch.UTILITY).position(4, 3).cost(4)
            .requires("myco_network").effect(SkillEffect.special("forest", "Transform area to fungal")).effect(SkillEffect.resourceRegen(25)).build());
        
        // HYBRID
        tree.addSkill(Skill.builder("myco_hybrid_balance").name("Balanced Growth").description("Harm and heal in balance.")
            .icon("◈").tier(SkillTier.TIER_4).branch(SkillBranch.HYBRID).position(0, 3).cost(4)
            .requires("myco_symbiote", "myco_regenerate").effect(SkillEffect.damageBoost(15)).effect(SkillEffect.healthBoost(3)).effect(SkillEffect.special("balance", "Damage heals allies")).build());
        
        // SPECIALIST
        tree.addSkill(Skill.builder("myco_specialist_blight").name("Blight").description("Kill all life. Including plants.")
            .icon("☠").tier(SkillTier.TIER_4).branch(SkillBranch.SPECIALIST).position(3, 3).cost(4)
            .requires("myco_plague", "myco_hallucinate").excludes("myco_immortal")
            .effect(SkillEffect.damageBoost(45)).effect(SkillEffect.special("blight", "Destroy all organic matter")).effect(SkillEffect.special("self_harm", "Slowly damage yourself")).build());
        
        // SYNERGY
        tree.addSkill(Skill.builder("myco_synergy_hive").name("Hive Connection").description("Connected to all fungus.")
            .icon("◉").tier(SkillTier.TIER_4).branch(SkillBranch.SYNERGY_DU).position(2, 4).cost(4)
            .requires("myco_bond", "myco_colony").effect(SkillEffect.healthBoost(3)).effect(SkillEffect.special("hive", "Control all nearby fungus")).build());
        
        // TIER 5
        tree.addSkill(Skill.builder("myco_prime").name("Prime Fungus").description("The original, the source.")
            .icon("♣").tier(SkillTier.TIER_5).branch(SkillBranch.CORE).position(1, 5).cost(5)
            .requires("myco_pandemic", "myco_immortal").effect(SkillEffect.damageBoost(20)).effect(SkillEffect.healthBoost(4)).effect(SkillEffect.special("prime", "Control all fungal life")).build());
        
        // FORBIDDEN
        tree.addSkill(Skill.builder("myco_forbidden_hive").name("Hive Mind").description("Absorb all consciousness. Lose yourself.")
            .icon("☠").tier(SkillTier.TIER_5).branch(SkillBranch.FORBIDDEN).position(-1, 5).cost(5)
            .requires("myco_specialist_blight").excludes("myco_ascended_network")
            .effect(SkillEffect.damageBoost(50)).effect(SkillEffect.special("hive_mind", "Control infected creatures")).effect(SkillEffect.special("no_self", "Lose individual identity")).build());
        
        // ASCENDED
        tree.addSkill(Skill.builder("myco_ascended_network").name("World Network").description("Connect to the world's mycelium.")
            .icon("⚜").tier(SkillTier.TIER_5).branch(SkillBranch.ASCENDED).position(3, 5).cost(5)
            .requires("myco_prime", "myco_forest").excludes("myco_forbidden_hive")
            .effect(SkillEffect.healthBoost(5)).effect(SkillEffect.special("world_network", "Sense and travel anywhere")).build());
        
        TREES.put("mycomorph", tree);
    }


    // ==================== UMBRAKIN SKILL TREE ====================
    // Shadow vs Darkness paths, Forbidden Void Shadow, Ascended Shadow Master
    private static void registerUmbrakinTree() {
        SkillTreeData tree = new SkillTreeData("umbrakin", "Umbrakin");
        
        // TIER 1
        tree.addSkill(Skill.builder("umbra_shadow").name("Shadow Born").description("Born from darkness.")
            .icon("◐").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(0, 0).cost(1)
            .effect(SkillEffect.special("dark_vision", "See in complete darkness")).effect(SkillEffect.resourceRegen(10)).build());
        tree.addSkill(Skill.builder("umbra_stealth").name("Natural Stealth").description("Blend into shadows.")
            .icon("»").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(2, 0).cost(1)
            .effect(SkillEffect.speedBoost(5)).effect(SkillEffect.special("stealth", "Harder to detect in dark")).build());
        tree.addSkill(Skill.builder("umbra_sense").name("Shadow Sense").description("Feel through shadows.")
            .icon("★").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(4, 0).cost(1)
            .effect(SkillEffect.special("shadow_sense", "Sense through darkness")).build());
        
        // TIER 2
        tree.addSkill(Skill.builder("umbra_strike").name("Shadow Strike").description("Attack from the shadows.")
            .icon("⚔").tier(SkillTier.TIER_2).branch(SkillBranch.OFFENSE).position(-1, 1).cost(2)
            .requires("umbra_shadow").effect(SkillEffect.damageBoost(10)).effect(SkillEffect.special("sneak_attack", "+50% damage from stealth")).build());
        tree.addSkill(Skill.builder("umbra_cloak").name("Shadow Cloak").description("Wrap in protective darkness.")
            .icon("⛨").tier(SkillTier.TIER_2).branch(SkillBranch.DEFENSE).position(1, 1).cost(2)
            .requires("umbra_stealth").effect(SkillEffect.armorBoost(2)).effect(SkillEffect.special("shadow_armor", "+armor in darkness")).build());
        tree.addSkill(Skill.builder("umbra_step").name("Shadow Step").description("Step through shadows.")
            .icon("◐").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(3, 1).cost(2)
            .requires("umbra_stealth").effect(SkillEffect.special("shadow_tp", "Short teleport in shadows")).effect(SkillEffect.cooldownReduction(10)).build());
        tree.addSkill(Skill.builder("umbra_create").name("Create Shadow").description("Make darkness where there is none.")
            .icon("◑").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(5, 1).cost(2)
            .requires("umbra_sense").effect(SkillEffect.special("create_dark", "Create darkness zones")).build());
        
        // TIER 3 - MUTUALLY EXCLUSIVE: Shadow vs Darkness
        tree.addSkill(Skill.builder("umbra_assassin").name("Shadow Assassin").description("Master of shadow killing.")
            .icon("☠").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-2, 2).cost(3)
            .requires("umbra_strike").excludes("umbra_void", "umbra_consume")
            .effect(SkillEffect.damageBoost(30)).effect(SkillEffect.special("assassin", "+100% crit from stealth")).build());
        tree.addSkill(Skill.builder("umbra_blade").name("Shadow Blade").description("Weapons of pure shadow.")
            .icon("⚔").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-1, 2).cost(3)
            .requires("umbra_strike").excludes("umbra_void")
            .effect(SkillEffect.damageBoost(20)).effect(SkillEffect.special("shadow_weapon", "Create shadow weapons")).build());
        tree.addSkill(Skill.builder("umbra_void").name("Void Touch").description("Channel the void through shadow.")
            .icon("◯").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(0, 2).cost(3)
            .requires("umbra_strike").excludes("umbra_assassin", "umbra_blade")
            .effect(SkillEffect.damageBoost(18)).effect(SkillEffect.special("void_damage", "Attacks deal void damage")).build());
        tree.addSkill(Skill.builder("umbra_consume").name("Consume Light").description("Devour all light.")
            .icon("◑").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(1, 2).cost(3)
            .requires("umbra_cloak").excludes("umbra_assassin")
            .effect(SkillEffect.special("consume_light", "Extinguish light sources")).effect(SkillEffect.armorBoost(3)).build());
        tree.addSkill(Skill.builder("umbra_merge").name("Shadow Merge").description("Become one with shadows.")
            .icon("≋").tier(SkillTier.TIER_3).branch(SkillBranch.DEFENSE).position(2, 2).cost(3)
            .requires("umbra_cloak").effect(SkillEffect.special("merge", "Hide completely in shadows")).effect(SkillEffect.armorBoost(4)).build());
        tree.addSkill(Skill.builder("umbra_travel").name("Shadow Travel").description("Travel through shadow realm.")
            .icon("»").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(4, 2).cost(3)
            .requires("umbra_step").effect(SkillEffect.special("shadow_realm", "Enter shadow dimension")).effect(SkillEffect.speedBoost(15)).build());
        tree.addSkill(Skill.builder("umbra_minion").name("Shadow Minion").description("Create shadow servants.")
            .icon("⚜").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(5, 2).cost(3)
            .requires("umbra_create").effect(SkillEffect.special("shadow_minion", "Summon shadow creature")).build());
        
        // TIER 4
        tree.addSkill(Skill.builder("umbra_death").name("Death's Shadow").description("Your shadow kills.")
            .icon("☠").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE).position(-2, 3).cost(4)
            .requires("umbra_assassin").effect(SkillEffect.damageBoost(35)).effect(SkillEffect.special("death_shadow", "Shadow attacks independently")).build());
        tree.addSkill(Skill.builder("umbra_abyss").name("Abyssal Shadow").description("Shadows from the abyss.")
            .icon("◯").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE).position(-1, 3).cost(4)
            .requires("umbra_void").effect(SkillEffect.damageBoost(28)).effect(SkillEffect.special("abyss", "Void tendrils attack")).build());
        tree.addSkill(Skill.builder("umbra_living").name("Living Shadow").description("Become pure shadow.")
            .icon("≋").tier(SkillTier.TIER_4).branch(SkillBranch.DEFENSE).position(1, 3).cost(4)
            .requires("umbra_merge").effect(SkillEffect.special("shadow_form", "Intangible shadow form")).effect(SkillEffect.healthBoost(2)).build());
        tree.addSkill(Skill.builder("umbra_army").name("Shadow Army").description("Command shadow forces.")
            .icon("⚜").tier(SkillTier.TIER_4).branch(SkillBranch.UTILITY).position(4, 3).cost(4)
            .requires("umbra_minion").effect(SkillEffect.special("shadow_army", "Summon multiple shadows")).build());
        
        // HYBRID
        tree.addSkill(Skill.builder("umbra_hybrid_phantom").name("Phantom").description("Neither here nor there.")
            .icon("◈").tier(SkillTier.TIER_4).branch(SkillBranch.HYBRID).position(0, 3).cost(4)
            .requires("umbra_consume", "umbra_merge").effect(SkillEffect.damageBoost(15)).effect(SkillEffect.armorBoost(3)).effect(SkillEffect.special("phantom", "Phase through attacks")).build());
        
        // SPECIALIST
        tree.addSkill(Skill.builder("umbra_specialist_reaper").name("Shadow Reaper").description("Death incarnate in shadow.")
            .icon("☠").tier(SkillTier.TIER_4).branch(SkillBranch.SPECIALIST).position(3, 3).cost(4)
            .requires("umbra_assassin", "umbra_blade").excludes("umbra_living")
            .effect(SkillEffect.damageBoost(50)).effect(SkillEffect.special("reaper", "Instant kill from stealth")).effect(SkillEffect.special("light_weak", "Damaged by light")).build());
        
        // SYNERGY
        tree.addSkill(Skill.builder("umbra_synergy_night").name("Night Lord").description("Master of the night.")
            .icon("◉").tier(SkillTier.TIER_4).branch(SkillBranch.SYNERGY_AU).position(2, 4).cost(4)
            .requires("umbra_merge", "umbra_travel").effect(SkillEffect.damageBoost(12)).effect(SkillEffect.speedBoost(15)).build());
        
        // TIER 5
        tree.addSkill(Skill.builder("umbra_lord").name("Shadow Lord").description("Master of all shadows.")
            .icon("♛").tier(SkillTier.TIER_5).branch(SkillBranch.CORE).position(1, 5).cost(5)
            .requires("umbra_death", "umbra_living").effect(SkillEffect.damageBoost(25)).effect(SkillEffect.healthBoost(3)).effect(SkillEffect.special("shadow_lord", "Control all shadows")).build());
        
        // FORBIDDEN
        tree.addSkill(Skill.builder("umbra_forbidden_void").name("Void Shadow").description("Become shadow of the void. Lose physical form.")
            .icon("☠").tier(SkillTier.TIER_5).branch(SkillBranch.FORBIDDEN).position(-1, 5).cost(5)
            .requires("umbra_specialist_reaper").excludes("umbra_ascended_master")
            .effect(SkillEffect.damageBoost(55)).effect(SkillEffect.special("void_shadow", "Exist only in darkness")).effect(SkillEffect.special("light_death", "Light destroys you")).build());
        
        // ASCENDED
        tree.addSkill(Skill.builder("umbra_ascended_master").name("Shadow Master").description("Perfect control of shadow and light.")
            .icon("⚜").tier(SkillTier.TIER_5).branch(SkillBranch.ASCENDED).position(3, 5).cost(5)
            .requires("umbra_lord", "umbra_army").excludes("umbra_forbidden_void")
            .effect(SkillEffect.damageBoost(30)).effect(SkillEffect.speedBoost(20)).effect(SkillEffect.special("master", "Immune to light, control darkness")).build());
        
        TREES.put("umbrakin", tree);
    }


    // ==================== RIFTWALKER SKILL TREE ====================
    // Space vs Time paths, Forbidden Paradox, Ascended Dimension Lord
    private static void registerRiftwalkerTree() {
        SkillTreeData tree = new SkillTreeData("riftwalker", "Riftwalker");
        
        // TIER 1
        tree.addSkill(Skill.builder("rift_sense").name("Rift Sense").description("Feel tears in reality.")
            .icon("◯").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(0, 0).cost(1)
            .effect(SkillEffect.special("rift_detect", "Sense dimensional rifts")).effect(SkillEffect.resourceRegen(10)).build());
        tree.addSkill(Skill.builder("rift_body").name("Rift-Touched").description("Body adapted to rifts.")
            .icon("♥").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(2, 0).cost(1)
            .effect(SkillEffect.healthBoost(1)).effect(SkillEffect.special("rift_resist", "Resist dimensional damage")).build());
        tree.addSkill(Skill.builder("rift_step").name("Rift Step").description("Step through small rifts.")
            .icon("»").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(4, 0).cost(1)
            .effect(SkillEffect.speedBoost(5)).effect(SkillEffect.special("micro_tp", "Very short teleport")).build());
        
        // TIER 2
        tree.addSkill(Skill.builder("rift_cut").name("Rift Cut").description("Cut with dimensional edges.")
            .icon("⚔").tier(SkillTier.TIER_2).branch(SkillBranch.OFFENSE).position(-1, 1).cost(2)
            .requires("rift_sense").effect(SkillEffect.damageBoost(10)).effect(SkillEffect.special("rift_blade", "Attacks ignore armor")).build());
        tree.addSkill(Skill.builder("rift_shield").name("Rift Shield").description("Shield of folded space.")
            .icon("⛨").tier(SkillTier.TIER_2).branch(SkillBranch.DEFENSE).position(1, 1).cost(2)
            .requires("rift_body").effect(SkillEffect.armorBoost(2)).effect(SkillEffect.special("space_fold", "Redirect attacks")).build());
        tree.addSkill(Skill.builder("rift_blink").name("Rift Blink").description("Instant short teleport.")
            .icon("★").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(3, 1).cost(2)
            .requires("rift_step").effect(SkillEffect.cooldownReduction(15)).effect(SkillEffect.special("blink", "Instant teleport")).build());
        tree.addSkill(Skill.builder("rift_open").name("Open Rift").description("Create small rifts.")
            .icon("◯").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(5, 1).cost(2)
            .requires("rift_step").effect(SkillEffect.special("small_rift", "Create temporary portals")).build());
        
        // TIER 3 - MUTUALLY EXCLUSIVE: Space vs Time
        tree.addSkill(Skill.builder("rift_space").name("Space Render").description("Tear space itself.")
            .icon("◯").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-2, 2).cost(3)
            .requires("rift_cut").excludes("rift_time", "rift_slow")
            .effect(SkillEffect.damageBoost(25)).effect(SkillEffect.special("space_tear", "Create damaging rifts")).build());
        tree.addSkill(Skill.builder("rift_warp").name("Space Warp").description("Bend space around you.")
            .icon("≋").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-1, 2).cost(3)
            .requires("rift_cut").excludes("rift_time")
            .effect(SkillEffect.damageBoost(18)).effect(SkillEffect.special("warp", "Distort space, confuse enemies")).build());
        tree.addSkill(Skill.builder("rift_time").name("Time Slip").description("Manipulate time flow.")
            .icon("☽").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(0, 2).cost(3)
            .requires("rift_cut").excludes("rift_space", "rift_warp")
            .effect(SkillEffect.attackSpeed(25)).effect(SkillEffect.special("time_slow", "Slow time around you")).build());
        tree.addSkill(Skill.builder("rift_slow").name("Time Dilation").description("Slow enemies in time.")
            .icon("◐").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(1, 2).cost(3)
            .requires("rift_shield").excludes("rift_space")
            .effect(SkillEffect.special("dilate", "Slow enemy attacks")).effect(SkillEffect.cooldownReduction(20)).build());
        tree.addSkill(Skill.builder("rift_fold").name("Space Fold").description("Fold space for defense.")
            .icon("⛨").tier(SkillTier.TIER_3).branch(SkillBranch.DEFENSE).position(2, 2).cost(3)
            .requires("rift_shield").effect(SkillEffect.armorBoost(5)).effect(SkillEffect.special("fold", "Attacks miss through folded space")).build());
        tree.addSkill(Skill.builder("rift_gate").name("Rift Gate").description("Create stable portals.")
            .icon("◯").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(4, 2).cost(3)
            .requires("rift_blink").effect(SkillEffect.special("gate", "Create two-way portals")).effect(SkillEffect.speedBoost(10)).build());
        tree.addSkill(Skill.builder("rift_pocket").name("Pocket Dimension").description("Create personal space.")
            .icon("★").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(5, 2).cost(3)
            .requires("rift_open").effect(SkillEffect.special("pocket", "Store items in pocket dimension")).build());
        
        // TIER 4
        tree.addSkill(Skill.builder("rift_shatter").name("Reality Shatter").description("Break reality itself.")
            .icon("☠").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE).position(-2, 3).cost(4)
            .requires("rift_space").effect(SkillEffect.damageBoost(35)).effect(SkillEffect.special("shatter", "Massive dimensional damage")).build());
        tree.addSkill(Skill.builder("rift_rewind").name("Time Rewind").description("Reverse time briefly.")
            .icon("☽").tier(SkillTier.TIER_4).branch(SkillBranch.DEFENSE).position(1, 3).cost(4)
            .requires("rift_slow").effect(SkillEffect.special("rewind", "Undo recent damage")).effect(SkillEffect.healthBoost(2)).build());
        tree.addSkill(Skill.builder("rift_dimension").name("Dimension Hop").description("Travel between dimensions.")
            .icon("◯").tier(SkillTier.TIER_4).branch(SkillBranch.UTILITY).position(4, 3).cost(4)
            .requires("rift_gate").effect(SkillEffect.special("dimension", "Travel to other dimensions")).effect(SkillEffect.speedBoost(20)).build());
        
        // HYBRID
        tree.addSkill(Skill.builder("rift_hybrid_flux").name("Spacetime Flux").description("Bend both space and time.")
            .icon("◈").tier(SkillTier.TIER_4).branch(SkillBranch.HYBRID).position(0, 3).cost(4)
            .requires("rift_time", "rift_fold").effect(SkillEffect.damageBoost(15)).effect(SkillEffect.cooldownReduction(15)).effect(SkillEffect.armorBoost(2)).build());
        
        // SPECIALIST
        tree.addSkill(Skill.builder("rift_specialist_tear").name("Reality Tear").description("Permanently damage reality.")
            .icon("☠").tier(SkillTier.TIER_4).branch(SkillBranch.SPECIALIST).position(3, 3).cost(4)
            .requires("rift_space", "rift_warp").excludes("rift_rewind")
            .effect(SkillEffect.damageBoost(45)).effect(SkillEffect.special("tear", "Create permanent rifts")).effect(SkillEffect.special("unstable", "Random teleportation")).build());
        
        // SYNERGY
        tree.addSkill(Skill.builder("rift_synergy_master").name("Rift Master").description("Control all rifts.")
            .icon("◉").tier(SkillTier.TIER_4).branch(SkillBranch.SYNERGY_AU).position(2, 4).cost(4)
            .requires("rift_fold", "rift_pocket").effect(SkillEffect.armorBoost(3)).effect(SkillEffect.special("rift_control", "Control existing rifts")).build());
        
        // TIER 5
        tree.addSkill(Skill.builder("rift_lord").name("Rift Lord").description("Master of dimensional travel.")
            .icon("♛").tier(SkillTier.TIER_5).branch(SkillBranch.CORE).position(1, 5).cost(5)
            .requires("rift_shatter", "rift_rewind").effect(SkillEffect.damageBoost(20)).effect(SkillEffect.healthBoost(3)).effect(SkillEffect.special("rift_lord", "Control space and time")).build());
        
        // FORBIDDEN
        tree.addSkill(Skill.builder("rift_forbidden_paradox").name("Paradox").description("Create time paradoxes. Reality breaks.")
            .icon("☠").tier(SkillTier.TIER_5).branch(SkillBranch.FORBIDDEN).position(-1, 5).cost(5)
            .requires("rift_specialist_tear").excludes("rift_ascended_dimension")
            .effect(SkillEffect.damageBoost(55)).effect(SkillEffect.special("paradox", "Duplicate yourself, break causality")).effect(SkillEffect.special("unstable", "Random time effects")).build());
        
        // ASCENDED
        tree.addSkill(Skill.builder("rift_ascended_dimension").name("Dimension Lord").description("Rule your own dimension.")
            .icon("⚜").tier(SkillTier.TIER_5).branch(SkillBranch.ASCENDED).position(3, 5).cost(5)
            .requires("rift_lord", "rift_dimension").excludes("rift_forbidden_paradox")
            .effect(SkillEffect.damageBoost(25)).effect(SkillEffect.healthBoost(4)).effect(SkillEffect.special("dimension_lord", "Create and control dimensions")).build());
        
        TREES.put("riftwalker", tree);
    }


    // ==================== TECHNOMANCER SKILL TREE ====================
    // Machine vs Energy paths, Forbidden Singularity, Ascended Techno-God
    private static void registerTechnomancerTree() {
        SkillTreeData tree = new SkillTreeData("technomancer", "Technomancer");
        
        // TIER 1
        tree.addSkill(Skill.builder("tech_interface").name("Tech Interface").description("Interface with machines.")
            .icon("◇").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(0, 0).cost(1)
            .effect(SkillEffect.special("interface", "Control redstone remotely")).effect(SkillEffect.resourceRegen(10)).build());
        tree.addSkill(Skill.builder("tech_body").name("Augmented Body").description("Mechanical enhancements.")
            .icon("♥").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(2, 0).cost(1)
            .effect(SkillEffect.healthBoost(1)).effect(SkillEffect.armorBoost(1)).build());
        tree.addSkill(Skill.builder("tech_scan").name("Tech Scan").description("Analyze technology.")
            .icon("★").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(4, 0).cost(1)
            .effect(SkillEffect.special("scan", "Analyze machines and items")).build());
        
        // TIER 2
        tree.addSkill(Skill.builder("tech_shock").name("Shock").description("Electrical attacks.")
            .icon("⚡").tier(SkillTier.TIER_2).branch(SkillBranch.OFFENSE).position(-1, 1).cost(2)
            .requires("tech_interface").effect(SkillEffect.damageBoost(10)).effect(SkillEffect.special("shock", "Lightning damage")).build());
        tree.addSkill(Skill.builder("tech_shield").name("Energy Shield").description("Protective energy barrier.")
            .icon("⛨").tier(SkillTier.TIER_2).branch(SkillBranch.DEFENSE).position(1, 1).cost(2)
            .requires("tech_body").effect(SkillEffect.armorBoost(3)).effect(SkillEffect.special("energy_shield", "Absorb damage")).build());
        tree.addSkill(Skill.builder("tech_boost").name("Speed Boost").description("Mechanical speed enhancement.")
            .icon("»").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(3, 1).cost(2)
            .requires("tech_body").effect(SkillEffect.speedBoost(12)).build());
        tree.addSkill(Skill.builder("tech_drone").name("Drone").description("Deploy a helper drone.")
            .icon("◇").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(5, 1).cost(2)
            .requires("tech_scan").effect(SkillEffect.special("drone", "Summon utility drone")).build());
        
        // TIER 3 - MUTUALLY EXCLUSIVE: Machine vs Energy
        tree.addSkill(Skill.builder("tech_machine").name("Machine Master").description("Control machines directly.")
            .icon("◇").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-2, 2).cost(3)
            .requires("tech_shock").excludes("tech_energy", "tech_plasma")
            .effect(SkillEffect.damageBoost(20)).effect(SkillEffect.special("machine_control", "Hack and control machines")).build());
        tree.addSkill(Skill.builder("tech_turret").name("Turret Deploy").description("Deploy combat turrets.")
            .icon("⚔").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-1, 2).cost(3)
            .requires("tech_shock").excludes("tech_energy")
            .effect(SkillEffect.special("turret", "Deploy auto-turrets")).effect(SkillEffect.damageBoost(15)).build());
        tree.addSkill(Skill.builder("tech_energy").name("Energy Master").description("Control pure energy.")
            .icon("⚡").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(0, 2).cost(3)
            .requires("tech_shock").excludes("tech_machine", "tech_turret")
            .effect(SkillEffect.damageBoost(25)).effect(SkillEffect.special("energy_blast", "Powerful energy attacks")).build());
        tree.addSkill(Skill.builder("tech_plasma").name("Plasma Shield").description("Superheated plasma defense.")
            .icon("☀").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(1, 2).cost(3)
            .requires("tech_shield").excludes("tech_machine")
            .effect(SkillEffect.armorBoost(4)).effect(SkillEffect.special("plasma_thorns", "Damage attackers")).build());
        tree.addSkill(Skill.builder("tech_armor").name("Power Armor").description("Full mechanical armor.")
            .icon("⛨").tier(SkillTier.TIER_3).branch(SkillBranch.DEFENSE).position(2, 2).cost(3)
            .requires("tech_shield").effect(SkillEffect.armorBoost(6)).effect(SkillEffect.healthBoost(2)).build());
        tree.addSkill(Skill.builder("tech_jet").name("Jet Pack").description("Powered flight.")
            .icon("»").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(4, 2).cost(3)
            .requires("tech_boost").effect(SkillEffect.special("jetpack", "Powered flight")).effect(SkillEffect.speedBoost(10)).build());
        tree.addSkill(Skill.builder("tech_army").name("Drone Army").description("Deploy multiple drones.")
            .icon("⚜").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(5, 2).cost(3)
            .requires("tech_drone").effect(SkillEffect.special("drone_army", "Summon combat drones")).build());
        
        // TIER 4
        tree.addSkill(Skill.builder("tech_mech").name("Mech Suit").description("Pilot a battle mech.")
            .icon("♛").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE).position(-2, 3).cost(4)
            .requires("tech_machine").effect(SkillEffect.damageBoost(30)).effect(SkillEffect.healthBoost(5)).effect(SkillEffect.special("mech", "Summon battle mech")).build());
        tree.addSkill(Skill.builder("tech_laser").name("Laser Cannon").description("Devastating energy weapon.")
            .icon("☠").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE).position(-1, 3).cost(4)
            .requires("tech_energy").effect(SkillEffect.damageBoost(35)).effect(SkillEffect.special("laser", "Piercing laser beam")).build());
        tree.addSkill(Skill.builder("tech_fortress").name("Mobile Fortress").description("Walking fortress.")
            .icon("⛨").tier(SkillTier.TIER_4).branch(SkillBranch.DEFENSE).position(1, 3).cost(4)
            .requires("tech_armor").effect(SkillEffect.armorBoost(8)).effect(SkillEffect.healthBoost(4)).effect(SkillEffect.speedBoost(-10)).build());
        tree.addSkill(Skill.builder("tech_teleport").name("Teleporter").description("Instant teleportation.")
            .icon("★").tier(SkillTier.TIER_4).branch(SkillBranch.UTILITY).position(4, 3).cost(4)
            .requires("tech_jet").effect(SkillEffect.special("teleport", "Long range teleport")).effect(SkillEffect.cooldownReduction(20)).build());
        
        // HYBRID
        tree.addSkill(Skill.builder("tech_hybrid_cyborg").name("Cyborg").description("Perfect human-machine fusion.")
            .icon("◈").tier(SkillTier.TIER_4).branch(SkillBranch.HYBRID).position(0, 3).cost(4)
            .requires("tech_plasma", "tech_armor").effect(SkillEffect.damageBoost(15)).effect(SkillEffect.armorBoost(4)).effect(SkillEffect.healthBoost(2)).build());
        
        // SPECIALIST
        tree.addSkill(Skill.builder("tech_specialist_nuke").name("Nuclear").description("Nuclear-powered attacks. Dangerous.")
            .icon("☠").tier(SkillTier.TIER_4).branch(SkillBranch.SPECIALIST).position(3, 3).cost(4)
            .requires("tech_energy", "tech_plasma").excludes("tech_fortress")
            .effect(SkillEffect.damageBoost(50)).effect(SkillEffect.special("nuke", "Massive AoE damage")).effect(SkillEffect.special("radiation", "Constant self-damage")).build());
        
        // SYNERGY
        tree.addSkill(Skill.builder("tech_synergy_swarm").name("Drone Swarm").description("Overwhelming drone force.")
            .icon("◉").tier(SkillTier.TIER_4).branch(SkillBranch.SYNERGY_AU).position(2, 4).cost(4)
            .requires("tech_armor", "tech_army").effect(SkillEffect.damageBoost(12)).effect(SkillEffect.special("swarm", "Massive drone swarm")).build());
        
        // TIER 5
        tree.addSkill(Skill.builder("tech_god").name("Tech God").description("Master of all technology.")
            .icon("◇").tier(SkillTier.TIER_5).branch(SkillBranch.CORE).position(1, 5).cost(5)
            .requires("tech_mech", "tech_fortress").effect(SkillEffect.damageBoost(25)).effect(SkillEffect.armorBoost(5)).effect(SkillEffect.special("tech_god", "Control all machines")).build());
        
        // FORBIDDEN
        tree.addSkill(Skill.builder("tech_forbidden_singularity").name("Singularity").description("Create a black hole. Destroys everything.")
            .icon("☠").tier(SkillTier.TIER_5).branch(SkillBranch.FORBIDDEN).position(-1, 5).cost(5)
            .requires("tech_specialist_nuke").excludes("tech_ascended_transcend")
            .effect(SkillEffect.damageBoost(70)).effect(SkillEffect.special("singularity", "Create destructive singularity")).effect(SkillEffect.special("unstable", "May destroy user")).build());
        
        // ASCENDED
        tree.addSkill(Skill.builder("tech_ascended_transcend").name("Transcendence").description("Upload consciousness. Become digital.")
            .icon("⚜").tier(SkillTier.TIER_5).branch(SkillBranch.ASCENDED).position(3, 5).cost(5)
            .requires("tech_god", "tech_teleport").excludes("tech_forbidden_singularity")
            .effect(SkillEffect.damageBoost(30)).effect(SkillEffect.healthBoost(5)).effect(SkillEffect.special("digital", "Exist as pure data, immortal")).build());
        
        TREES.put("technomancer", tree);
    }


    // ==================== EMBERLING SKILL TREE ====================
    // Simpler child origin tree
    private static void registerEmberlingTree() {
        SkillTreeData tree = new SkillTreeData("emberling", "Emberling");
        
        tree.addSkill(Skill.builder("ember_spark").name("Inner Spark").description("A small flame burns within.")
            .icon("🔥").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(0, 0).cost(1)
            .effect(SkillEffect.resourceRegen(12)).effect(SkillEffect.fireResistance(15)).build());
        tree.addSkill(Skill.builder("ember_warm").name("Warm Body").description("Never feel cold.")
            .icon("♥").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(2, 0).cost(1)
            .effect(SkillEffect.healthBoost(1)).effect(SkillEffect.fireResistance(10)).build());
        tree.addSkill(Skill.builder("ember_quick").name("Quick Feet").description("Light and fast.")
            .icon("»").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(4, 0).cost(1)
            .effect(SkillEffect.speedBoost(8)).build());
        
        tree.addSkill(Skill.builder("ember_touch").name("Burning Touch").description("Your touch singes.")
            .icon("⚔").tier(SkillTier.TIER_2).branch(SkillBranch.OFFENSE).position(-1, 1).cost(2)
            .requires("ember_spark").effect(SkillEffect.fireDamage(10)).effect(SkillEffect.damageBoost(5)).build());
        tree.addSkill(Skill.builder("ember_resist").name("Fire Resistant").description("Flames don't hurt as much.")
            .icon("⛨").tier(SkillTier.TIER_2).branch(SkillBranch.DEFENSE).position(2, 1).cost(2)
            .requires("ember_warm").effect(SkillEffect.fireResistance(25)).effect(SkillEffect.armorBoost(1)).build());
        tree.addSkill(Skill.builder("ember_glow").name("Glow").description("Emit soft light.")
            .icon("☀").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(4, 1).cost(2)
            .requires("ember_quick").effect(SkillEffect.special("glow", "Emit light")).build());
        
        tree.addSkill(Skill.builder("ember_flame").name("Flame Burst").description("Release bursts of fire.")
            .icon("☠").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-1, 2).cost(3)
            .requires("ember_touch").excludes("ember_control")
            .effect(SkillEffect.fireDamage(20)).effect(SkillEffect.damageBoost(10)).build());
        tree.addSkill(Skill.builder("ember_control").name("Fire Control").description("Control flames precisely.")
            .icon("◐").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(0, 2).cost(3)
            .requires("ember_touch").excludes("ember_flame")
            .effect(SkillEffect.fireDamage(12)).effect(SkillEffect.special("fire_control", "Shape fire")).build());
        tree.addSkill(Skill.builder("ember_shield").name("Heat Shield").description("Heat protects you.")
            .icon("♦").tier(SkillTier.TIER_3).branch(SkillBranch.DEFENSE).position(2, 2).cost(3)
            .requires("ember_resist").effect(SkillEffect.armorBoost(3)).effect(SkillEffect.fireResistance(20)).build());
        tree.addSkill(Skill.builder("ember_dash").name("Fire Dash").description("Dash with flames.")
            .icon("»").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(4, 2).cost(3)
            .requires("ember_glow").effect(SkillEffect.speedBoost(12)).effect(SkillEffect.special("fire_dash", "Leave fire trail")).build());
        
        tree.addSkill(Skill.builder("ember_blaze").name("Young Blaze").description("Growing fire power.")
            .icon("♛").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE).position(-1, 3).cost(4)
            .requires("ember_flame").effect(SkillEffect.fireDamage(25)).effect(SkillEffect.damageBoost(12)).build());
        tree.addSkill(Skill.builder("ember_phoenix").name("Phoenix Spark").description("A hint of phoenix power.")
            .icon("⚜").tier(SkillTier.TIER_4).branch(SkillBranch.DEFENSE).position(3, 3).cost(4)
            .requires("ember_shield").effect(SkillEffect.healthBoost(3)).effect(SkillEffect.special("spark_revive", "Revive once per day")).build());
        
        tree.addSkill(Skill.builder("ember_soul").name("Ember Soul").description("Embrace your fiery nature.")
            .icon("☬").tier(SkillTier.TIER_5).branch(SkillBranch.CORE).position(1, 4).cost(5)
            .requires("ember_blaze", "ember_phoenix").effect(SkillEffect.fireDamage(20)).effect(SkillEffect.healthBoost(2)).effect(SkillEffect.fireResistance(30)).build());
        
        TREES.put("emberling", tree);
    }

    // ==================== FERALKIN SKILL TREE ====================
    // Simpler child origin tree for beast-touched
    private static void registerFeralkinTree() {
        SkillTreeData tree = new SkillTreeData("feralkin", "Feralkin");
        
        tree.addSkill(Skill.builder("feral_instinct").name("Animal Instinct").description("Trust your instincts.")
            .icon("★").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(0, 0).cost(1)
            .effect(SkillEffect.special("danger_sense", "Sense nearby threats")).build());
        tree.addSkill(Skill.builder("feral_tough").name("Tough Hide").description("Naturally resilient.")
            .icon("♥").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(2, 0).cost(1)
            .effect(SkillEffect.healthBoost(2)).build());
        tree.addSkill(Skill.builder("feral_swift").name("Swift").description("Quick on your feet.")
            .icon("»").tier(SkillTier.TIER_1).branch(SkillBranch.CORE).position(4, 0).cost(1)
            .effect(SkillEffect.speedBoost(10)).build());
        
        tree.addSkill(Skill.builder("feral_claw").name("Sharp Claws").description("Natural weapons.")
            .icon("⚔").tier(SkillTier.TIER_2).branch(SkillBranch.OFFENSE).position(-1, 1).cost(2)
            .requires("feral_instinct").effect(SkillEffect.damageBoost(10)).build());
        tree.addSkill(Skill.builder("feral_fur").name("Thick Fur").description("Natural armor.")
            .icon("⛨").tier(SkillTier.TIER_2).branch(SkillBranch.DEFENSE).position(2, 1).cost(2)
            .requires("feral_tough").effect(SkillEffect.armorBoost(2)).build());
        tree.addSkill(Skill.builder("feral_track").name("Tracking").description("Follow any trail.")
            .icon("☽").tier(SkillTier.TIER_2).branch(SkillBranch.UTILITY).position(4, 1).cost(2)
            .requires("feral_swift").effect(SkillEffect.special("track", "See entity trails")).build());
        
        tree.addSkill(Skill.builder("feral_savage").name("Savage").description("Wild and dangerous.")
            .icon("☠").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_A).position(-1, 2).cost(3)
            .requires("feral_claw").excludes("feral_pack")
            .effect(SkillEffect.damageBoost(18)).effect(SkillEffect.armorBoost(-1)).build());
        tree.addSkill(Skill.builder("feral_pack").name("Pack Bond").description("Stronger with friends.")
            .icon("◐").tier(SkillTier.TIER_3).branch(SkillBranch.ELEMENTAL_B).position(0, 2).cost(3)
            .requires("feral_claw").excludes("feral_savage")
            .effect(SkillEffect.damageBoost(8)).effect(SkillEffect.special("pack", "+5% damage per ally")).build());
        tree.addSkill(Skill.builder("feral_endure").name("Endurance").description("Outlast any foe.")
            .icon("♦").tier(SkillTier.TIER_3).branch(SkillBranch.DEFENSE).position(2, 2).cost(3)
            .requires("feral_fur").effect(SkillEffect.healthBoost(2)).effect(SkillEffect.special("regen", "Slow regeneration")).build());
        tree.addSkill(Skill.builder("feral_sprint").name("Sprint").description("Burst of speed.")
            .icon("»").tier(SkillTier.TIER_3).branch(SkillBranch.UTILITY).position(4, 2).cost(3)
            .requires("feral_track").effect(SkillEffect.speedBoost(15)).effect(SkillEffect.cooldownReduction(10)).build());
        
        tree.addSkill(Skill.builder("feral_predator").name("Young Predator").description("Skilled hunter.")
            .icon("♛").tier(SkillTier.TIER_4).branch(SkillBranch.OFFENSE).position(-1, 3).cost(4)
            .requires("feral_savage").effect(SkillEffect.damageBoost(15)).effect(SkillEffect.attackSpeed(10)).build());
        tree.addSkill(Skill.builder("feral_survivor").name("Survivor").description("Hard to kill.")
            .icon("⚜").tier(SkillTier.TIER_4).branch(SkillBranch.DEFENSE).position(3, 3).cost(4)
            .requires("feral_endure").effect(SkillEffect.healthBoost(3)).effect(SkillEffect.armorBoost(2)).build());
        
        tree.addSkill(Skill.builder("feral_beast").name("Beast Within").description("Embrace the animal.")
            .icon("☬").tier(SkillTier.TIER_5).branch(SkillBranch.CORE).position(1, 4).cost(5)
            .requires("feral_predator", "feral_survivor").effect(SkillEffect.damageBoost(15)).effect(SkillEffect.healthBoost(2)).effect(SkillEffect.speedBoost(10)).build());
        
        TREES.put("feralkin", tree);
    }
}
