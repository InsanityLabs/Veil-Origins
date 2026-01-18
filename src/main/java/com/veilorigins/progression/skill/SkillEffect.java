package com.veilorigins.progression.skill;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.core.Holder;
import java.util.UUID;

/**
 * Represents an effect that a skill applies when unlocked.
 * Skills can modify attributes, grant abilities, or unlock features.
 */
public class SkillEffect {
    
    public enum EffectType {
        ATTRIBUTE_MODIFIER,  // Modifies player attributes (speed, health, damage, etc.)
        ABILITY_UNLOCK,      // Unlocks a new ability
        PASSIVE_BOOST,       // Boosts existing passive effects
        RESOURCE_MODIFIER,   // Modifies resource regen/max
        COOLDOWN_REDUCTION,  // Reduces ability cooldowns
        DAMAGE_TYPE_BONUS,   // Bonus damage of specific type
        RESISTANCE_BONUS,    // Resistance to damage type
        SPECIAL              // Custom effect handled by code
    }
    
    private final EffectType type;
    private final String targetId;  // Attribute ID, ability ID, or special effect ID
    private final double value;
    private final AttributeModifier.Operation operation;
    private final String description;
    
    // For attribute modifiers
    private final UUID modifierId;
    
    private SkillEffect(EffectType type, String targetId, double value, 
                        AttributeModifier.Operation operation, String description) {
        this.type = type;
        this.targetId = targetId;
        this.value = value;
        this.operation = operation;
        this.description = description;
        this.modifierId = UUID.randomUUID();
    }
    
    // Factory methods for common effects
    
    public static SkillEffect speedBoost(double percent) {
        return new SkillEffect(EffectType.ATTRIBUTE_MODIFIER, "minecraft:generic.movement_speed",
            percent / 100.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
            String.format("+%.0f%% Movement Speed", percent));
    }
    
    public static SkillEffect healthBoost(double hearts) {
        return new SkillEffect(EffectType.ATTRIBUTE_MODIFIER, "minecraft:generic.max_health",
            hearts * 2, AttributeModifier.Operation.ADD_VALUE,
            String.format("+%.0f Hearts", hearts));
    }
    
    public static SkillEffect damageBoost(double percent) {
        return new SkillEffect(EffectType.ATTRIBUTE_MODIFIER, "minecraft:generic.attack_damage",
            percent / 100.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
            String.format("+%.0f%% Attack Damage", percent));
    }
    
    public static SkillEffect armorBoost(double points) {
        return new SkillEffect(EffectType.ATTRIBUTE_MODIFIER, "minecraft:generic.armor",
            points, AttributeModifier.Operation.ADD_VALUE,
            String.format("+%.0f Armor", points));
    }
    
    public static SkillEffect armorToughnessBoost(double points) {
        return new SkillEffect(EffectType.ATTRIBUTE_MODIFIER, "minecraft:generic.armor_toughness",
            points, AttributeModifier.Operation.ADD_VALUE,
            String.format("+%.0f Armor Toughness", points));
    }
    
    public static SkillEffect knockbackResistance(double percent) {
        return new SkillEffect(EffectType.ATTRIBUTE_MODIFIER, "minecraft:generic.knockback_resistance",
            percent / 100.0, AttributeModifier.Operation.ADD_VALUE,
            String.format("+%.0f%% Knockback Resistance", percent));
    }
    
    public static SkillEffect attackSpeed(double percent) {
        return new SkillEffect(EffectType.ATTRIBUTE_MODIFIER, "minecraft:generic.attack_speed",
            percent / 100.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
            String.format("+%.0f%% Attack Speed", percent));
    }
    
    public static SkillEffect resourceRegen(double percent) {
        return new SkillEffect(EffectType.RESOURCE_MODIFIER, "regen",
            percent, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
            String.format("+%.0f%% Resource Regeneration", percent));
    }
    
    public static SkillEffect resourceMax(double percent) {
        return new SkillEffect(EffectType.RESOURCE_MODIFIER, "max",
            percent, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
            String.format("+%.0f%% Max Resource", percent));
    }
    
    public static SkillEffect cooldownReduction(double percent) {
        return new SkillEffect(EffectType.COOLDOWN_REDUCTION, "all",
            percent, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL,
            String.format("-%.0f%% Ability Cooldowns", percent));
    }
    
    public static SkillEffect abilityUnlock(String abilityId, String abilityName) {
        return new SkillEffect(EffectType.ABILITY_UNLOCK, abilityId,
            1, AttributeModifier.Operation.ADD_VALUE,
            "Unlocks: " + abilityName);
    }
    
    public static SkillEffect special(String effectId, String description) {
        return new SkillEffect(EffectType.SPECIAL, effectId,
            1, AttributeModifier.Operation.ADD_VALUE, description);
    }
    
    public static SkillEffect fireResistance(double percent) {
        return new SkillEffect(EffectType.RESISTANCE_BONUS, "fire",
            percent, AttributeModifier.Operation.ADD_VALUE,
            String.format("+%.0f%% Fire Resistance", percent));
    }
    
    public static SkillEffect fireDamage(double percent) {
        return new SkillEffect(EffectType.DAMAGE_TYPE_BONUS, "fire",
            percent, AttributeModifier.Operation.ADD_VALUE,
            String.format("+%.0f%% Fire Damage", percent));
    }
    
    // Getters
    public EffectType getType() { return type; }
    public String getTargetId() { return targetId; }
    public double getValue() { return value; }
    public AttributeModifier.Operation getOperation() { return operation; }
    public String getDescription() { return description; }
    public UUID getModifierId() { return modifierId; }
}
