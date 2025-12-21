package com.veilorigins.origins.stoneheart;

import com.veilorigins.api.OriginAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.ChatFormatting;

public class StoneSkinAbility extends OriginAbility {
    private static final int RESOURCE_COST = 6;
    private static final int DURATION = 20 * 20; // 20 seconds
    private int activeDuration = 0;
    private boolean isActive = false;

    public StoneSkinAbility() {
        super("stone_skin", 90);
    }

    @Override
    public void onActivate(Player player, Level level) {
        isActive = true;
        activeDuration = DURATION;

        // Apply slowness to prevent movement (rooted)
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, DURATION, -10, false, false));

        // Visual feedback
        player.sendSystemMessage(Component.literal(ChatFormatting.GRAY + "You become as immovable as stone!"));

        startCooldown();
    }

    @Override
    public void tick(Player player) {
        if (isActive && activeDuration > 0) {
            activeDuration--;

            // Keep player rooted
            player.setDeltaMovement(0, player.getDeltaMovement().y, 0);

            // Warning when ending
            if (activeDuration == 3 * 20) {
                player.sendSystemMessage(
                        Component.literal(ChatFormatting.YELLOW + "Stone Skin ending in 3 seconds..."));
            }

            if (activeDuration == 0) {
                isActive = false;
                player.sendSystemMessage(Component.literal(ChatFormatting.GRAY + "Stone Skin ended."));
            }
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean shouldReflectDamage() {
        return isActive;
    }

    @Override
    public boolean canUse(Player player) {
        return !isOnCooldown() && player.onGround();
    }

    @Override
    public int getResourceCost() {
        return RESOURCE_COST;
    }
}
