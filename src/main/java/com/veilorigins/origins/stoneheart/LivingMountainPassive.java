package com.veilorigins.origins.stoneheart;

import com.veilorigins.api.OriginPassive;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class LivingMountainPassive extends OriginPassive {
    private static final ResourceLocation HEALTH_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("veil_origins", "stoneheart_health_boost");
    private static final ResourceLocation ARMOR_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("veil_origins", "stoneheart_armor_boost");
    private static final ResourceLocation KNOCKBACK_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("veil_origins", "stoneheart_knockback_resist");

    public LivingMountainPassive() {
        super("living_mountain");
    }

    @Override
    public void onTick(Player player) {
        // Fall damage immunity handled in event
    }

    @Override
    public void onEquip(Player player) {
        // 50% more health (30 HP total)
        AttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
        if (health != null) {
            // Always remove first to handle respawn (Minecraft copies modifiers to new entity)
            health.removeModifier(HEALTH_MODIFIER_ID);
            health.addPermanentModifier(new AttributeModifier(
                HEALTH_MODIFIER_ID,
                0.5,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
            ));
        }
        
        // Natural armor (equivalent to iron armor)
        AttributeInstance armor = player.getAttribute(Attributes.ARMOR);
        if (armor != null) {
            armor.removeModifier(ARMOR_MODIFIER_ID);
            armor.addPermanentModifier(new AttributeModifier(
                ARMOR_MODIFIER_ID,
                6.0,
                AttributeModifier.Operation.ADD_VALUE
            ));
        }
        
        // Knockback resistance
        AttributeInstance knockback = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (knockback != null) {
            knockback.removeModifier(KNOCKBACK_MODIFIER_ID);
            knockback.addPermanentModifier(new AttributeModifier(
                KNOCKBACK_MODIFIER_ID,
                1.0,
                AttributeModifier.Operation.ADD_VALUE
            ));
        }
        
        player.setHealth(player.getMaxHealth());
    }

    @Override
    public void onRemove(Player player) {
        AttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
        if (health != null) {
            health.removeModifier(HEALTH_MODIFIER_ID);
        }
        
        AttributeInstance armor = player.getAttribute(Attributes.ARMOR);
        if (armor != null) {
            armor.removeModifier(ARMOR_MODIFIER_ID);
        }
        
        AttributeInstance knockback = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (knockback != null) {
            knockback.removeModifier(KNOCKBACK_MODIFIER_ID);
        }
    }
}
