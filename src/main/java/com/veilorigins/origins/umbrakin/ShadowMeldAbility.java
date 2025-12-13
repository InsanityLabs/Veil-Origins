package com.veilorigins.origins.umbrakin;

import com.veilorigins.api.OriginAbility;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ShadowMeldAbility extends OriginAbility {
    private static final int RESOURCE_COST = 4;
    private static final int DURATION = 10 * 20; // 10 seconds
    private int activeDuration = 0;
    private boolean isActive = false;

    public ShadowMeldAbility() {
        super("shadow_meld", 20);
    }

    @Override
    public void onActivate(Player player, Level level) {
        int lightLevel = level.getMaxLocalRawBrightness(player.blockPosition());
        
        // Only works in darkness (light level 7 or below)
        if (lightLevel > 7) {
            player.sendSystemMessage(Component.literal("§cToo bright! Shadow Meld requires darkness (light level 7 or below)."));
            return;
        }
        
        isActive = true;
        activeDuration = DURATION;
        
        // Apply invisibility
        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, DURATION, 0, false, false));
        
        // Shadow particles
        if (level instanceof ServerLevel serverLevel) {
            Vec3 pos = player.position();
            serverLevel.sendParticles(ParticleTypes.SMOKE,
                pos.x, pos.y + 1, pos.z, 30, 0.5, 0.5, 0.5, 0.05);
        }
        
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.5f, 0.5f);
        
        player.sendSystemMessage(Component.literal("§8You meld into the shadows..."));
        
        startCooldown();
    }

    @Override
    public void tick(Player player) {
        if (isActive && activeDuration > 0) {
            activeDuration--;
            
            // Check if still in darkness
            int lightLevel = player.level().getMaxLocalRawBrightness(player.blockPosition());
            if (lightLevel > 7) {
                // Break invisibility if entering light
                player.removeEffect(MobEffects.INVISIBILITY);
                isActive = false;
                activeDuration = 0;
                player.sendSystemMessage(Component.literal("§cThe light breaks your shadow meld!"));
            }
            
            if (activeDuration == 0) {
                isActive = false;
                player.sendSystemMessage(Component.literal("§8Shadow Meld ended."));
            }
        }
    }

    public void breakInvisibility(Player player) {
        if (isActive) {
            player.removeEffect(MobEffects.INVISIBILITY);
            isActive = false;
            activeDuration = 0;
            player.sendSystemMessage(Component.literal("§cAttacking breaks your shadow meld!"));
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
