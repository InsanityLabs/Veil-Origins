package com.veilorigins.origins.starborne;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class StarborneWeaknessesPassive extends OriginPassive {
    private int tickCounter = 0;

    public StarborneWeaknessesPassive() {
        super("starborne_weaknesses");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;

        if (tickCounter % 20 == 0) {
            // Cannot see in complete darkness
            int light = player.level().getMaxLocalRawBrightness(player.blockPosition());
            if (light == 0) {
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0, false, false));
                player.hurt(player.damageSources().magic(), 0.5f);
            }
        }
    }

    @Override
    public void onEquip(Player player) {
    }

    @Override
    public void onRemove(Player player) {
        player.removeEffect(MobEffects.BLINDNESS);
    }
}
