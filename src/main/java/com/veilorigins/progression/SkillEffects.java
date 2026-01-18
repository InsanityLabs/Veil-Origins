package com.veilorigins.progression;

import com.veilorigins.VeilOrigins;
import com.veilorigins.data.OriginData;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.*;

/**
 * Handles applying and tracking skill effects for players.
 * Skills can provide:
 * - Attribute modifiers (health, armor, speed, damage, etc.)
 * - Special flags (checked by other systems)
 * - Passive abilities (checked during events)
 */
public class SkillEffects {
    
    // Track applied attribute modifiers per player
    private static final Map<UUID, Set<Identifier>> appliedModifiers = new HashMap<>();
    
    // Track active special effects per player (checked by other systems)
    private static final Map<UUID, Set<String>> activeEffects = new HashMap<>();
    
    // Track numeric bonuses per player
    private static final Map<UUID, Map<String, Double>> numericBonuses = new HashMap<>();
    
    /**
     * Apply all skill effects for a player based on their unlocked skills.
     * Called on login, respawn, and skill unlock.
     */
    public static void applyAllEffects(Player player, String originId) {
        if (player.level().isClientSide()) return;
        
        // Clear existing effects first
        removeAllEffects(player);
        
        OriginData.PlayerOriginData data = OriginData.get(player);
        Set<String> unlockedSkills = data.getUnlockedSkills();
        
        if (unlockedSkills.isEmpty()) {
            VeilOrigins.LOGGER.debug("No skills unlocked for player {}", player.getName().getString());
            return;
        }
        
        UUID playerId = player.getUUID();
        activeEffects.put(playerId, new HashSet<>());
        numericBonuses.put(playerId, new HashMap<>());
        appliedModifiers.put(playerId, new HashSet<>());
        
        // Apply effects based on origin and unlocked skills
        for (String skillId : unlockedSkills) {
            applySkillEffect(player, originId, skillId);
        }
        
        VeilOrigins.LOGGER.info("Applied {} skill effects for {} ({})", 
            unlockedSkills.size(), player.getName().getString(), originId);
    }
    
    /**
     * Apply effects for a single skill.
     */
    private static void applySkillEffect(Player player, String originId, String skillId) {
        // Universal skill effects that work for any origin
        switch (skillId) {
            // === ATTRIBUTE MODIFIERS ===
            // Health boosts
            case "nature_bond", "stone_skin", "crystal_skin", "spirit_form", "death_touch",
                 "circuit_mind", "starlight", "void_gaze", "rift_sense", "spore_cloud",
                 "rage_build", "time_sense", "soul_link" -> {} // Tier 1 - no stat boost, just unlock
            
            // Speed boosts
            case "quick_step" -> addSpeedBonus(player, skillId, 0.1);
            case "swim_speed" -> addSpeedBonus(player, skillId, 0.15);
            case "night_stalker" -> { addSpeedBonus(player, skillId, 0.05); addEffect(player, "night_speed_boost"); }
            case "wind_rider" -> addSpeedBonus(player, skillId, 0.1);
            case "snow_walker" -> addEffect(player, "no_snow_slow");
            case "blazing_speed" -> addEffect(player, "fire_speed_boost");
            
            // Armor boosts
            case "thick_fur" -> addArmorBonus(player, skillId, 2);
            case "bark_skin" -> { addArmorBonus(player, skillId, 2); addEffect(player, "tree_armor_boost"); }
            case "ice_armor" -> { addArmorBonus(player, skillId, 3); addEffect(player, "cold_biome_armor"); }
            case "bone_armor" -> addArmorBonus(player, skillId, 3);
            case "void_armor" -> { addArmorBonus(player, skillId, 2); addEffect(player, "void_resist"); }
            case "energy_shield" -> addEffect(player, "energy_absorb");
            case "thick_skin" -> addArmorBonus(player, skillId, 3);
            
            // Health boosts
            case "photosynthesis" -> addEffect(player, "sun_regen");
            case "regeneration" -> addEffect(player, "passive_regen");
            case "rapid_healing" -> addEffect(player, "fast_regen");
            case "symbiosis" -> addEffect(player, "mushroom_heal");
            case "ocean_heart" -> addEffect(player, "water_regen");
            case "mountain_heart" -> addEffect(player, "stone_regen");
            
            // Damage boosts
            case "heavy_blow" -> addDamageBonus(player, skillId, 0.15);
            case "shadow_strike" -> { addDamageBonus(player, skillId, 0.1); addEffect(player, "stealth_damage"); }
            case "ghost_touch" -> addEffect(player, "armor_pierce");
            case "vine_whip" -> addNumericBonus(player, "vine_range", 3);
            case "shock_touch" -> addEffect(player, "lightning_melee");
            case "cold_touch" -> addEffect(player, "slow_melee");
            case "ember_touch" -> addEffect(player, "fire_melee");
            case "toxic_touch" -> addEffect(player, "poison_melee");
            case "death_touch_effect" -> addEffect(player, "wither_melee");
            case "void_touch" -> addEffect(player, "void_damage");
            case "temporal_strike" -> addEffect(player, "slow_strike");
            
            // Biome/condition bonuses
            case "forest_affinity" -> addEffect(player, "forest_damage_boost");
            case "ocean_affinity" -> addEffect(player, "water_damage_boost");
            case "cave_dweller" -> addEffect(player, "underground_damage_boost");
            case "sky_affinity" -> addEffect(player, "high_altitude_boost");
            case "night_affinity" -> addEffect(player, "night_damage_boost");
            case "nether_affinity" -> addEffect(player, "nether_damage_boost");
            case "end_affinity" -> addEffect(player, "end_damage_boost");
            case "mushroom_affinity" -> addEffect(player, "mushroom_biome_boost");
            case "night_power" -> addEffect(player, "night_power_boost");
            
            // Detection abilities
            case "miner_instinct", "gem_sight" -> addEffect(player, "ore_sight");
            case "dark_vision", "cosmic_sense", "fungal_sight" -> addEffect(player, "night_vision");
            case "soul_sight", "keen_senses" -> addEffect(player, "entity_glow");
            case "blood_scent" -> addEffect(player, "injured_detection");
            case "tremor_sense" -> addEffect(player, "movement_detection");
            case "machine_sense" -> addEffect(player, "redstone_detection");
            case "corpse_sense" -> addEffect(player, "undead_detection");
            case "aqua_vision" -> addEffect(player, "water_vision");
            
            // Resistance/immunity
            case "heat_resistance" -> addEffect(player, "fire_resist_25");
            case "frost_resistance" -> addEffect(player, "freeze_immune");
            case "fire_lord" -> addEffect(player, "fire_immune");
            case "ice_lord" -> addEffect(player, "cold_biome_power");
            case "void_lord" -> addEffect(player, "void_immune");
            case "featherfall" -> addEffect(player, "no_fall_damage");
            case "light_bones" -> addEffect(player, "reduced_fall_damage");
            case "fortify" -> addKnockbackResist(player, skillId, 0.5);
            case "unstoppable" -> addEffect(player, "rage_knockback_immune");
            
            // Special abilities
            case "shadow_step" -> addEffect(player, "silent_movement");
            case "shadow_meld" -> addEffect(player, "darkness_invis");
            case "bat_form_speed" -> addNumericBonus(player, "bat_speed", 0.25);
            case "blood_pool" -> addNumericBonus(player, "blood_max", 25);
            case "vampiric_bite" -> addNumericBonus(player, "lifesteal", 0.1);
            case "pack_hunter" -> addEffect(player, "pack_damage_boost");
            case "lunar_strength" -> addEffect(player, "full_moon_boost");
            case "undead_affinity" -> addEffect(player, "undead_neutral");
            case "alpha_presence" -> addEffect(player, "wolf_tame");
            
            // Cooldown reductions
            case "veil_walker", "haste" -> addNumericBonus(player, "cooldown_reduction", 0.1);
            case "veil_lord", "time_lord" -> addNumericBonus(player, "cooldown_reduction", 0.25);
            
            // Attack speed
            case "overclock" -> addAttackSpeedBonus(player, skillId, 0.2);
            case "frenzy" -> addEffect(player, "rage_attack_speed");
            case "blood_frenzy" -> addEffect(player, "low_health_attack_speed");
            
            // Master tier bonuses
            case "nosferatu" -> {
                addEffect(player, "sun_immune");
                addDamageBonus(player, skillId, 0.25);
                addHealthBonus(player, skillId, 4);
            }
            case "lycan_lord" -> {
                addEffect(player, "permanent_wolf");
                addDamageBonus(player, skillId, 0.5);
                addHealthBonus(player, skillId, 4);
            }
            case "phoenix_rebirth" -> addEffect(player, "phoenix_revive");
            case "winter_eternal" -> addEffect(player, "create_snow");
            case "world_tree" -> addEffect(player, "healing_grove");
            case "living_gem" -> addEffect(player, "magic_immune");
            case "transcendence" -> addEffect(player, "full_ethereal");
            case "army_of_dead" -> addEffect(player, "mass_summon");
            case "singularity" -> addEffect(player, "black_hole");
            case "cosmic_form" -> addEffect(player, "star_form");
            case "void_sovereign" -> addEffect(player, "end_control");
            case "multiverse" -> addEffect(player, "clone_self");
            case "hive_mind" -> addEffect(player, "fungi_network");
            case "avatar_of_war" -> addEffect(player, "war_form");
            case "master_of_time" -> addEffect(player, "time_control");
            case "eternal_bond" -> addEffect(player, "unbreakable_bond");
            
            // Summon abilities
            case "raise_skeleton" -> addNumericBonus(player, "skeleton_summon", 1);
            case "raise_zombie" -> addNumericBonus(player, "zombie_summon", 3);
            case "pack_alpha" -> addNumericBonus(player, "wolf_summon", 2);
            case "drone_deploy" -> addEffect(player, "drone_summon");
            case "forest_wrath" -> addEffect(player, "treant_summon");
            
            default -> {
                // Log unknown skills for debugging
                VeilOrigins.LOGGER.debug("No effect defined for skill: {}", skillId);
            }
        }
    }
    
    // === HELPER METHODS FOR APPLYING EFFECTS ===
    
    private static void addHealthBonus(Player player, String skillId, double hearts) {
        addAttributeModifier(player, skillId, Attributes.MAX_HEALTH, hearts * 2, AttributeModifier.Operation.ADD_VALUE);
        // Heal to new max if needed
        if (player.getHealth() < player.getMaxHealth()) {
            player.setHealth(Math.min(player.getHealth() + (float)(hearts * 2), player.getMaxHealth()));
        }
    }
    
    private static void addArmorBonus(Player player, String skillId, double armor) {
        addAttributeModifier(player, skillId, Attributes.ARMOR, armor, AttributeModifier.Operation.ADD_VALUE);
    }
    
    private static void addSpeedBonus(Player player, String skillId, double percent) {
        addAttributeModifier(player, skillId, Attributes.MOVEMENT_SPEED, percent, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
    
    private static void addDamageBonus(Player player, String skillId, double percent) {
        addAttributeModifier(player, skillId, Attributes.ATTACK_DAMAGE, percent, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
    
    private static void addAttackSpeedBonus(Player player, String skillId, double percent) {
        addAttributeModifier(player, skillId, Attributes.ATTACK_SPEED, percent, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
    }
    
    private static void addKnockbackResist(Player player, String skillId, double amount) {
        addAttributeModifier(player, skillId, Attributes.KNOCKBACK_RESISTANCE, amount, AttributeModifier.Operation.ADD_VALUE);
    }
    
    private static void addAttributeModifier(Player player, String skillId, Holder<Attribute> attribute, 
                                             double value, AttributeModifier.Operation operation) {
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return;
        
        Identifier modId = Identifier.fromNamespaceAndPath(VeilOrigins.MOD_ID, "skill_" + skillId);
        
        // Remove existing
        instance.removeModifier(modId);
        
        // Add new
        AttributeModifier modifier = new AttributeModifier(modId, value, operation);
        instance.addPermanentModifier(modifier);
        
        appliedModifiers.computeIfAbsent(player.getUUID(), k -> new HashSet<>()).add(modId);
    }
    
    private static void addEffect(Player player, String effectId) {
        activeEffects.computeIfAbsent(player.getUUID(), k -> new HashSet<>()).add(effectId);
    }
    
    private static void addNumericBonus(Player player, String bonusId, double value) {
        numericBonuses.computeIfAbsent(player.getUUID(), k -> new HashMap<>())
            .merge(bonusId, value, Double::sum);
    }
    
    /**
     * Remove all skill effects from a player.
     */
    public static void removeAllEffects(Player player) {
        UUID playerId = player.getUUID();
        
        // Remove attribute modifiers
        Set<Identifier> modifiers = appliedModifiers.remove(playerId);
        if (modifiers != null) {
            for (Identifier modId : modifiers) {
                removeModifierFromAll(player, modId);
            }
        }
        
        activeEffects.remove(playerId);
        numericBonuses.remove(playerId);
    }
    
    private static void removeModifierFromAll(Player player, Identifier modId) {
        Holder<Attribute>[] attrs = new Holder[] {
            Attributes.MAX_HEALTH, Attributes.ARMOR, Attributes.MOVEMENT_SPEED,
            Attributes.ATTACK_DAMAGE, Attributes.ATTACK_SPEED, Attributes.KNOCKBACK_RESISTANCE,
            Attributes.ARMOR_TOUGHNESS, Attributes.LUCK
        };
        for (Holder<Attribute> attr : attrs) {
            AttributeInstance inst = player.getAttribute(attr);
            if (inst != null) inst.removeModifier(modId);
        }
    }
    
    // === QUERY METHODS ===
    
    public static boolean hasEffect(Player player, String effectId) {
        Set<String> effects = activeEffects.get(player.getUUID());
        return effects != null && effects.contains(effectId);
    }
    
    public static double getNumericBonus(Player player, String bonusId) {
        Map<String, Double> bonuses = numericBonuses.get(player.getUUID());
        return bonuses != null ? bonuses.getOrDefault(bonusId, 0.0) : 0.0;
    }
    
    public static Set<String> getAllEffects(Player player) {
        Set<String> effects = activeEffects.get(player.getUUID());
        return effects != null ? Collections.unmodifiableSet(effects) : Collections.emptySet();
    }
    
    /**
     * Clean up when player disconnects.
     */
    public static void onPlayerDisconnect(UUID playerId) {
        appliedModifiers.remove(playerId);
        activeEffects.remove(playerId);
        numericBonuses.remove(playerId);
    }
}
