package com.veilorigins.origins.vampire;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.ChatFormatting;

public class VampirePassive extends OriginPassive {
    private int tickCounter = 0;

    public VampirePassive() {
        super("vampire_nature");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;
        Level level = player.level();
        int lightLevel = level.getMaxLocalRawBrightness(player.blockPosition());

        // Night vision in darkness
        if (lightLevel < 11) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false));
        } else if (player.hasEffect(MobEffects.NIGHT_VISION)) {
            player.removeEffect(MobEffects.NIGHT_VISION);
        }

        // Check every 20 ticks (1 second)
        if (tickCounter >= 20) {
            tickCounter = 0;

            // Sunlight damage (2 HP per second) - helmet protects
            // Day time: 0-12000 is day, 12000-13000 is sunset, 13000-23000 is night, 23000-24000 is sunrise
            long dayTime = level.getDayTime() % 24000;
            boolean isDaytime = dayTime < 12500 || dayTime > 23500; // Day or sunrise
            
            if (isDaytime && level.canSeeSky(player.blockPosition()) && !level.isRaining()) {
                // Check if wearing helmet
                if (player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD).isEmpty()) {
                    player.hurt(level.damageSources().onFire(), 2.0f);
                    player.setRemainingFireTicks(20); // Set on fire briefly for visual effect
                }
            }
        }

        // Strength at night
        if (lightLevel < 7) {
            player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 25, 1, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, 25, 0, false, false));
        }
    }

    @Override
    public void onEquip(Player player) {
        player.displayClientMessage(net.minecraft.network.chat.Component.literal(ChatFormatting.DARK_RED
                + "As a Vampire, you are powerful at night but burn in sunlight. Wear a helmet for protection!"), false);
    }

    @Override
    public void onRemove(Player player) {
        player.removeEffect(MobEffects.NIGHT_VISION);
    }
}
