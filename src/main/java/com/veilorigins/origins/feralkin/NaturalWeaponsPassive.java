package com.veilorigins.origins.feralkin;

import com.veilorigins.api.OriginPassive;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class NaturalWeaponsPassive extends OriginPassive {
    private static final String ATTACK_UUID = "feralkin_attack_boost";

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
            attack.addPermanentModifier(new AttributeModifier(
                ResourceLocation.fromNamespaceAndPath("veil_origins", ATTACK_UUID),
                2.0,
                AttributeModifier.Operation.ADD_VALUE
            ));
        }
    }

    @Override
    public void onRemove(Player player) {
        AttributeInstance attack = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attack != null) {
            attack.removeModifier(ResourceLocation.fromNamespaceAndPath("veil_origins", ATTACK_UUID));
        }
    }
}
