package com.veilorigins.origins.vampling;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.ChatFormatting;

public class VamplingPassive extends OriginPassive {
    private int tickCounter = 0;

    public VamplingPassive() {
        super("vampling_nature");
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

        // Mild sunlight weakness (not burning, just weakness)
        if (tickCounter % 20 == 0) {
            boolean isDay = level.isDay();
            boolean canSeeSky = level.canSeeSky(player.blockPosition());

            if (isDay && canSeeSky && lightLevel > 12) {
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 25, 0, false, false));
            }
        }

        // Minor strength at night
        if (lightLevel < 7) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 25, 0, false, false));
        }
    }

    @Override
    public void onEquip(Player player) {
        player.sendSystemMessage(net.minecraft.network.chat.Component
                .literal(ChatFormatting.RED + " As a Vampling, you are stronger at night but weakened in sunlight."));
    }

    @Override
    public void onRemove(Player player) {
        player.removeEffect(MobEffects.NIGHT_VISION);
    }
}
