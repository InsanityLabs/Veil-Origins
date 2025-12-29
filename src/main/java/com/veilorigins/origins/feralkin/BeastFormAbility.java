package com.veilorigins.origins.feralkin;

import com.veilorigins.api.OriginAbility;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class BeastFormAbility extends OriginAbility {
    private static final int DURATION = 60 * 20; // 60 seconds
    private static final int RESOURCE_COST = 8;

    public BeastFormAbility() {
        super("beast_form", 120);
    }

    @Override
    public void onActivate(Player player, Level level) {
        // Play transformation sound
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDER_DRAGON_GROWL,
                SoundSource.PLAYERS, 1.5f, 0.7f);

        // Apply beast form effects
        player.addEffect(new MobEffectInstance(MobEffects.SPEED, DURATION, 1)); // Speed II
        player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, DURATION, 1)); // Strength II (+4 damage)
        player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, DURATION, 1)); // Jump boost for climbing

        startCooldown();
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
