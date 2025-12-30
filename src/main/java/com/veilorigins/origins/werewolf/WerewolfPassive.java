package com.veilorigins.origins.werewolf;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.ChatFormatting;

public class WerewolfPassive extends OriginPassive {

    public WerewolfPassive() {
        super("werewolf_nature");
    }

    @Override
    public void onTick(Player player) {
        Level level = player.level();
        long dayTime = level.getDayTime() % 24000;
        boolean isNight = dayTime >= 13000 && dayTime <= 23000;
        // In 1.21.11, MoonPhase is an enum - calculate from day time
        // Moon phase cycles every 8 days (192000 ticks), phase 0 is full moon
        int moonPhaseIndex = (int) ((level.getDayTime() / 24000L) % 8L);
        boolean isFullMoon = moonPhaseIndex == 0;

        // Enhanced abilities at night
        if (isNight) {
            player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 25, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, 25, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false));

            // Extra power during full moon
            if (isFullMoon) {
                player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 25, 1, false, false));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 25, 0, false, false));
            }
        } else {
            if (player.hasEffect(MobEffects.NIGHT_VISION)) {
                player.removeEffect(MobEffects.NIGHT_VISION);
            }
        }

        // Natural regeneration
        if (player.getHealth() < player.getMaxHealth() && player.tickCount % 100 == 0) {
            player.heal(1.0f);
        }
    }

    @Override
    public void onEquip(Player player) {
        player.displayClientMessage(net.minecraft.network.chat.Component
                .literal(ChatFormatting.GOLD
                        + "As a Werewolf, you are most powerful at night, especially during the full moon."), false);
    }

    @Override
    public void onRemove(Player player) {
        player.removeEffect(MobEffects.NIGHT_VISION);
    }
}
