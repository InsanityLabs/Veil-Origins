package com.veilorigins.origins.stoneheart;

import com.veilorigins.api.OriginPassive;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class LivingMountainPassive extends OriginPassive {
    private static final String HEALTH_UUID = "stoneheart_health_boost";
    private static final String ARMOR_UUID = "stoneheart_armor_boost";
    private static final String KNOCKBACK_UUID = "stoneheart_knockback_resist";

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
            health.addPermanentModifier(new AttributeModifier(
                Identifier.fromNamespaceAndPath("veil_origins", HEALTH_UUID),
                0.5,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
            ));
        }
        
        // Natural armor (equivalent to iron armor)
        AttributeInstance armor = player.getAttribute(Attributes.ARMOR);
        if (armor != null) {
            armor.addPermanentModifier(new AttributeModifier(
                Identifier.fromNamespaceAndPath("veil_origins", ARMOR_UUID),
                6.0,
                AttributeModifier.Operation.ADD_VALUE
            ));
        }
        
        // Knockback resistance
        AttributeInstance knockback = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (knockback != null) {
            knockback.addPermanentModifier(new AttributeModifier(
                Identifier.fromNamespaceAndPath("veil_origins", KNOCKBACK_UUID),
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
            health.removeModifier(Identifier.fromNamespaceAndPath("veil_origins", HEALTH_UUID));
        }
        
        AttributeInstance armor = player.getAttribute(Attributes.ARMOR);
        if (armor != null) {
            armor.removeModifier(Identifier.fromNamespaceAndPath("veil_origins", ARMOR_UUID));
        }
        
        AttributeInstance knockback = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (knockback != null) {
            knockback.removeModifier(Identifier.fromNamespaceAndPath("veil_origins", KNOCKBACK_UUID));
        }
    }
}
