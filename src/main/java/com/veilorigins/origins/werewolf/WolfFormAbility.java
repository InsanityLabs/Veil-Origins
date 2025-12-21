package com.veilorigins.origins.werewolf;

import com.veilorigins.api.OriginAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.ChatFormatting;

public class WolfFormAbility extends OriginAbility {
    private static final int RESOURCE_COST = 8;
    private static final int DURATION = 20 * 20;
    private int activeDuration = 0;
    private boolean isActive = false;

    public WolfFormAbility() {
        super("wolf_form", 400);
    }

    @Override
    public void onActivate(Player player, Level level) {
        isActive = true;
        activeDuration = DURATION;

        player.sendSystemMessage(Component.literal(ChatFormatting.GOLD + "You transform into a werewolf!"));
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.WOLF_GROWL, SoundSource.PLAYERS, 2.0f, 0.7f);

        startCooldown();
    }

    @Override
    public void tick(Player player) {
        if (isActive && activeDuration > 0) {
            activeDuration--;

            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 5, 2, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 5, 2, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.JUMP, 5, 1, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 5, 1, false, false));

            if (activeDuration == 5 * 20) {
                player.sendSystemMessage(Component.literal(ChatFormatting.YELLOW + "Wolf form ending in 5 seconds..."));
            }

            if (activeDuration == 0) {
                isActive = false;
                player.sendSystemMessage(Component.literal(ChatFormatting.GOLD + "You return to human form."));
            }
        }
    }

    @Override
    public boolean canUse(Player player) {
        return !isOnCooldown();
    }

    @Override
    public int getResourceCost() {
        return RESOURCE_COST;
    }
}
