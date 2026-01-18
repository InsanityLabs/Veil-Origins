package com.veilorigins.progression.skill;

import com.veilorigins.VeilOrigins;
import com.veilorigins.data.OriginData;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.*;

/**
 * Handles applying and removing skill effects to/from players.
 * This bridges the gap between skill definitions and actual gameplay effects.
 */
public class SkillEffectHandler {
    
    // Track applied modifiers per player for cleanup
    private static final Map<UUID, Set<ResourceLocation>> appliedModifiers = new HashMap<>();
    
    // Track special effects per player
    private static final Map<UUID, Set<String>> activeSpecialEffects = new HashMap<>();
    
    // Track resource/cooldown modifiers per player
    private static final Map<UUID, SkillBonuses> playerBonuses = new HashMap<>();
    
    /**
     * Apply all skill effects for a player based on their unlocked skills.
     * Should be called on login, origin change, or skill unlock.
     */
    public static void applyAllSkillEffects(Player player, String originId) {
        if (player.level().isClientSide()) return;
        
        // First remove all existing skill modifiers
        removeAllSkillEffects(player);
        
        // Get player's unlocked skills
        OriginData.PlayerOriginData data = OriginData.get(player);
        Set<String> unlockedSkills = data.getUnlockedSkills();
        
        // Get skill tree for origin
        SkillTreeData tree = SkillTrees.getTree(originId);
        if (tree == null) {
            VeilOrigins.LOGGER.debug("No skill tree found for origin: {}", originId);
            return;
        }
        
        // Initialize bonuses tracker
        SkillBonuses bonuses = new SkillBonuses();
        
        // Apply effects from each unlocked skill
        for (String skillId : unlockedSkills) {
            Skill skill = tree.getSkill(skillId);
            if (skill == null) continue;
            
            for (SkillEffect effect : skill.getEffects()) {
                applyEffect(player, skill, effect, bonuses);
            }
        }
        
        // Store bonuses for this player
        playerBonuses.put(player.getUUID(), bonuses);
        
        VeilOrigins.LOGGER.debug("Applied {} skill effects for player {}", 
            unlockedSkills.size(), player.getName().getString());
    }

    
    /**
     * Apply a single skill effect to a player.
     */
    private static void applyEffect(Player player, Skill skill, SkillEffect effect, SkillBonuses bonuses) {
        switch (effect.getType()) {
            case ATTRIBUTE_MODIFIER -> applyAttributeModifier(player, skill, effect);
            case RESOURCE_MODIFIER -> applyResourceModifier(effect, bonuses);
            case COOLDOWN_REDUCTION -> applyCooldownReduction(effect, bonuses);
            case DAMAGE_TYPE_BONUS -> applyDamageBonus(effect, bonuses);
            case RESISTANCE_BONUS -> applyResistanceBonus(effect, bonuses);
            case ABILITY_UNLOCK -> trackAbilityUnlock(player, effect);
            case SPECIAL -> trackSpecialEffect(player, effect);
            case PASSIVE_BOOST -> applyPassiveBoost(effect, bonuses);
        }
    }
    
    /**
     * Apply an attribute modifier from a skill.
     */
    private static void applyAttributeModifier(Player player, Skill skill, SkillEffect effect) {
        // Parse attribute from target ID
        Holder<Attribute> attribute = getAttributeFromId(effect.getTargetId());
        if (attribute == null) {
            VeilOrigins.LOGGER.warn("Unknown attribute: {}", effect.getTargetId());
            return;
        }
        
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return;
        
        // Create unique modifier ID based on skill + effect
        ResourceLocation modifierId = ResourceLocation.fromNamespaceAndPath(
            VeilOrigins.MOD_ID, 
            "skill_" + skill.getId() + "_" + effect.getModifierId().toString().substring(0, 8)
        );
        
        // Remove existing modifier if present
        instance.removeModifier(modifierId);
        
        // Create and apply new modifier
        AttributeModifier modifier = new AttributeModifier(
            modifierId,
            effect.getValue(),
            effect.getOperation()
        );
        
        instance.addPermanentModifier(modifier);
        
        // Track for cleanup
        appliedModifiers.computeIfAbsent(player.getUUID(), k -> new HashSet<>()).add(modifierId);
        
        VeilOrigins.LOGGER.debug("Applied attribute modifier: {} = {} ({})", 
            effect.getTargetId(), effect.getValue(), effect.getDescription());
    }
    
    /**
     * Get attribute holder from string ID.
     */
    private static Holder<Attribute> getAttributeFromId(String id) {
        return switch (id) {
            case "minecraft:generic.movement_speed" -> Attributes.MOVEMENT_SPEED;
            case "minecraft:generic.max_health" -> Attributes.MAX_HEALTH;
            case "minecraft:generic.attack_damage" -> Attributes.ATTACK_DAMAGE;
            case "minecraft:generic.armor" -> Attributes.ARMOR;
            case "minecraft:generic.armor_toughness" -> Attributes.ARMOR_TOUGHNESS;
            case "minecraft:generic.knockback_resistance" -> Attributes.KNOCKBACK_RESISTANCE;
            case "minecraft:generic.attack_speed" -> Attributes.ATTACK_SPEED;
            case "minecraft:generic.luck" -> Attributes.LUCK;
            default -> null;
        };
    }
    
    private static void applyResourceModifier(SkillEffect effect, SkillBonuses bonuses) {
        if ("regen".equals(effect.getTargetId())) {
            bonuses.resourceRegenBonus += effect.getValue();
        } else if ("max".equals(effect.getTargetId())) {
            bonuses.resourceMaxBonus += effect.getValue();
        }
    }
    
    private static void applyCooldownReduction(SkillEffect effect, SkillBonuses bonuses) {
        bonuses.cooldownReduction += effect.getValue();
    }
    
    private static void applyDamageBonus(SkillEffect effect, SkillBonuses bonuses) {
        bonuses.damageTypeBonuses.merge(effect.getTargetId(), effect.getValue(), Double::sum);
    }
    
    private static void applyResistanceBonus(SkillEffect effect, SkillBonuses bonuses) {
        bonuses.resistanceBonuses.merge(effect.getTargetId(), effect.getValue(), Double::sum);
    }
    
    private static void trackAbilityUnlock(Player player, SkillEffect effect) {
        // Track unlocked abilities - these are checked elsewhere when abilities are used
        activeSpecialEffects.computeIfAbsent(player.getUUID(), k -> new HashSet<>())
            .add("ability:" + effect.getTargetId());
    }
    
    private static void trackSpecialEffect(Player player, SkillEffect effect) {
        activeSpecialEffects.computeIfAbsent(player.getUUID(), k -> new HashSet<>())
            .add(effect.getTargetId());
    }
    
    private static void applyPassiveBoost(SkillEffect effect, SkillBonuses bonuses) {
        bonuses.passiveBoosts.merge(effect.getTargetId(), effect.getValue(), Double::sum);
    }

    
    /**
     * Remove all skill effects from a player.
     */
    public static void removeAllSkillEffects(Player player) {
        UUID playerId = player.getUUID();
        
        // Remove attribute modifiers
        Set<ResourceLocation> modifiers = appliedModifiers.remove(playerId);
        if (modifiers != null) {
            for (ResourceLocation modifierId : modifiers) {
                removeModifierFromAllAttributes(player, modifierId);
            }
        }
        
        // Clear special effects
        activeSpecialEffects.remove(playerId);
        
        // Clear bonuses
        playerBonuses.remove(playerId);
    }
    
    private static void removeModifierFromAllAttributes(Player player, ResourceLocation modifierId) {
        // Try to remove from all common attributes
        Holder<Attribute>[] attributes = new Holder[] {
            Attributes.MAX_HEALTH, Attributes.MOVEMENT_SPEED, Attributes.ATTACK_DAMAGE,
            Attributes.ARMOR, Attributes.ARMOR_TOUGHNESS, Attributes.KNOCKBACK_RESISTANCE,
            Attributes.ATTACK_SPEED, Attributes.LUCK
        };
        
        for (Holder<Attribute> attr : attributes) {
            AttributeInstance instance = player.getAttribute(attr);
            if (instance != null) {
                instance.removeModifier(modifierId);
            }
        }
    }
    
    // ==================== Query Methods ====================
    
    /**
     * Check if player has a special effect active.
     */
    public static boolean hasSpecialEffect(Player player, String effectId) {
        Set<String> effects = activeSpecialEffects.get(player.getUUID());
        return effects != null && effects.contains(effectId);
    }
    
    /**
     * Check if player has unlocked an ability via skills.
     */
    public static boolean hasUnlockedAbility(Player player, String abilityId) {
        return hasSpecialEffect(player, "ability:" + abilityId);
    }
    
    /**
     * Get player's cooldown reduction percentage from skills.
     */
    public static double getCooldownReduction(Player player) {
        SkillBonuses bonuses = playerBonuses.get(player.getUUID());
        return bonuses != null ? bonuses.cooldownReduction : 0;
    }
    
    /**
     * Get player's resource regen bonus from skills.
     */
    public static double getResourceRegenBonus(Player player) {
        SkillBonuses bonuses = playerBonuses.get(player.getUUID());
        return bonuses != null ? bonuses.resourceRegenBonus : 0;
    }
    
    /**
     * Get player's resource max bonus from skills.
     */
    public static double getResourceMaxBonus(Player player) {
        SkillBonuses bonuses = playerBonuses.get(player.getUUID());
        return bonuses != null ? bonuses.resourceMaxBonus : 0;
    }
    
    /**
     * Get player's damage bonus for a specific type.
     */
    public static double getDamageBonus(Player player, String damageType) {
        SkillBonuses bonuses = playerBonuses.get(player.getUUID());
        if (bonuses == null) return 0;
        return bonuses.damageTypeBonuses.getOrDefault(damageType, 0.0);
    }
    
    /**
     * Get player's resistance bonus for a specific type.
     */
    public static double getResistanceBonus(Player player, String damageType) {
        SkillBonuses bonuses = playerBonuses.get(player.getUUID());
        if (bonuses == null) return 0;
        return bonuses.resistanceBonuses.getOrDefault(damageType, 0.0);
    }
    
    /**
     * Get all active special effects for a player.
     */
    public static Set<String> getActiveSpecialEffects(Player player) {
        Set<String> effects = activeSpecialEffects.get(player.getUUID());
        return effects != null ? Collections.unmodifiableSet(effects) : Collections.emptySet();
    }
    
    // ==================== Bonuses Tracking ====================
    
    /**
     * Tracks non-attribute bonuses from skills.
     */
    public static class SkillBonuses {
        public double cooldownReduction = 0;
        public double resourceRegenBonus = 0;
        public double resourceMaxBonus = 0;
        public Map<String, Double> damageTypeBonuses = new HashMap<>();
        public Map<String, Double> resistanceBonuses = new HashMap<>();
        public Map<String, Double> passiveBoosts = new HashMap<>();
    }
}
