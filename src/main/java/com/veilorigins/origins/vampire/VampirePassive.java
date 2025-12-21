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
            if (level.isDay() && level.canSeeSky(player.blockPosition()) && lightLevel >= 12) {
                // Check if wearing helmet
                if (player.getInventory().getArmor(3).isEmpty()) {
                    player.hurt(level.damageSources().onFire(), 2.0f);
                }
            }
        }

        // Strength at night
        if (lightLevel < 7) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 25, 1, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 25, 0, false, false));
        }
    }

    @Override
    public void onEquip(Player player) {
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(ChatFormatting.DARK_RED
                + "As a Vampire, you are powerful at night but burn in sunlight. Wear a helmet for protection!"));
    }

    @Override
    public void onRemove(Player player) {
        player.removeEffect(MobEffects.NIGHT_VISION);
    }
}
