package com.veilorigins.origins.feralkin;

import com.veilorigins.api.OriginPassive;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class NaturalWeaponsPassive extends OriginPassive {
    private static final ResourceLocation ATTACK_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("veil_origins", "feralkin_attack_boost");

    public NaturalWeaponsPassive() {
        super("natural_weapons");
    }

    @Override
    public void onTick(Player player) {
        // Unarmed damage boost handled in event
    }

    @Override
    public void onEquip(Player player) {
        // Boost unarmed attack damage
        AttributeInstance attack = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attack != null) {
            // Always remove first to handle respawn (Minecraft copies modifiers to new entity)
            attack.removeModifier(ATTACK_MODIFIER_ID);
            attack.addPermanentModifier(new AttributeModifier(
                ATTACK_MODIFIER_ID,
                2.0,
                AttributeModifier.Operation.ADD_VALUE
            ));
        }
    }

    @Override
    public void onRemove(Player player) {
        AttributeInstance attack = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attack != null) {
            attack.removeModifier(ATTACK_MODIFIER_ID);
        }
    }
}
