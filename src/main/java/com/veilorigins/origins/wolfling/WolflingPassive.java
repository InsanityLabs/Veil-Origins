package com.veilorigins.origins.wolfling;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.ChatFormatting;

public class WolflingPassive extends OriginPassive {

    public WolflingPassive() {
        super("wolfling_nature");
    }

    @Override
    public void onTick(Player player) {
        Level level = player.level();
        long dayTime = level.getDayTime() % 24000;
        boolean isNight = dayTime >= 13000 && dayTime <= 23000;

        // Minor enhanced abilities at night
        if (isNight) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 25, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false));
        } else {
            if (player.hasEffect(MobEffects.NIGHT_VISION)) {
                player.removeEffect(MobEffects.NIGHT_VISION);
            }
        }

        // Slight natural regeneration
        if (player.getHealth() < player.getMaxHealth() && player.tickCount % 150 == 0) {
            player.heal(0.5f);
        }
    }

    @Override
    public void onEquip(Player player) {
        player.sendSystemMessage(net.minecraft.network.chat.Component
                .literal(ChatFormatting.YELLOW + "As a Wolfling, you are faster at night with enhanced senses."));
    }

    @Override
    public void onRemove(Player player) {
        player.removeEffect(MobEffects.NIGHT_VISION);
    }
}
