package com.veilorigins.origins.stoneheart;

import com.veilorigins.api.OriginPassive;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class EarthAffinityPassive extends OriginPassive {
    private static final ResourceLocation SPEED_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("veil_origins", "stoneheart_speed_reduction");

    public EarthAffinityPassive() {
        super("earth_affinity");
    }

    @Override
    public void onTick(Player player) {
        // Mining speed bonus handled in event
    }

    @Override
    public void onEquip(Player player) {
        // 50% slower movement speed
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            // Always remove first to handle respawn (Minecraft copies modifiers to new entity)
            speed.removeModifier(SPEED_MODIFIER_ID);
            speed.addPermanentModifier(new AttributeModifier(
                SPEED_MODIFIER_ID,
                -0.5,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE
            ));
        }
    }

    @Override
    public void onRemove(Player player) {
        AttributeInstance speed = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.removeModifier(SPEED_MODIFIER_ID);
        }
    }
}
