package com.veilorigins.origins.starborne;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class SolarPoweredPassive extends OriginPassive {
    public SolarPoweredPassive() {
        super("solar_powered");
    }

    @Override
    public void onTick(Player player) {
        Level level = player.level();
        if (level.isClientSide())
            return;

        long time = level.getDayTime() % 24000;
        boolean isDay = time >= 0 && time < 12000;
        boolean canSeeSky = level.canSeeSky(player.blockPosition());

        if (isDay && canSeeSky) {
            // Direct sunlight
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, 40, 0, false, false));
        } else {
            // Check for light darkness
            int light = level.getMaxLocalRawBrightness(player.blockPosition());
            if (light == 0) {
                player.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, 40, 0, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0, false, false));
                // Damage handled in weaknesses
            }
        }
    }

    @Override
    public void onEquip(Player player) {
    }

    @Override
    public void onRemove(Player player) {
        player.removeEffect(MobEffects.REGENERATION);
        player.removeEffect(MobEffects.SPEED);
        player.removeEffect(MobEffects.MINING_FATIGUE);
        player.removeEffect(MobEffects.WEAKNESS);
    }
}
