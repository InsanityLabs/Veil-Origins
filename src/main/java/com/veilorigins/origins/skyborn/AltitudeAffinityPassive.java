package com.veilorigins.origins.skyborn;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class AltitudeAffinityPassive extends OriginPassive {
    public AltitudeAffinityPassive() {
        super("altitude_affinity");
    }

    @Override
    public void onTick(Player player) {
        if (player.getY() > 150) {
            // Regen
            if (player.getHealth() < player.getMaxHealth() && player.tickCount % 40 == 0) {
                player.heal(1.0f);
            }
        }

        // Breathe anywhere (Water Breathing + maybe something for void/space mods?)
        // Just giving water breathing covers "no oxygen limit" in vanilla contexts
        // usually.
        player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 40, 0, false, false));
    }

    @Override
    public void onEquip(Player player) {
    }

    @Override
    public void onRemove(Player player) {
        player.removeEffect(MobEffects.WATER_BREATHING);
    }
}
