package com.veilorigins.origins.umbrakin;

import com.veilorigins.api.OriginAbility;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.ChatFormatting;

public class DarknessBoltAbility extends OriginAbility {
    private static final int RESOURCE_COST = 1;

    public DarknessBoltAbility() {
        super("darkness_bolt", 5);
    }

    @Override
    public void onActivate(Player player, Level level) {
        int lightLevel = level.getMaxLocalRawBrightness(player.blockPosition());

        // Only works in darkness (light level below 10)
        if (lightLevel >= 10) {
            player.displayClientMessage(Component.literal(
                    ChatFormatting.RED + "Too bright! Darkness Bolt requires darkness (light level below 10)."), false);
            return;
        }

        // Launch shadow bolt
        Vec3 lookVec = player.getLookAngle();
        Vec3 startPos = player.position().add(0, player.getEyeHeight(), 0);

        // Raycast to find target
        Vec3 endPos = startPos.add(lookVec.scale(20));
        HitResult hitResult = level.clip(new net.minecraft.world.level.ClipContext(
                startPos, endPos,
                net.minecraft.world.level.ClipContext.Block.COLLIDER,
                net.minecraft.world.level.ClipContext.Fluid.NONE,
                player));

        // Check for entity hit
        net.minecraft.world.phys.AABB searchBox = new net.minecraft.world.phys.AABB(
                startPos, hitResult.getLocation()).inflate(1.0);

        LivingEntity hitEntity = null;
        double closestDistance = Double.MAX_VALUE;

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, searchBox)) {
            if (entity != player) {
                double distance = entity.position().distanceTo(startPos);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    hitEntity = entity;
                }
            }
        }

        if (hitEntity != null) {
            // Damage and blind target
            hitEntity.hurt(level.damageSources().magic(), 4.0f);
            hitEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));

            // Shadow particles at hit location
            if (level instanceof ServerLevel serverLevel) {
                Vec3 hitPos = hitEntity.position();
                serverLevel.sendParticles(ParticleTypes.SMOKE,
                        hitPos.x, hitPos.y + 1, hitPos.z, 20, 0.3, 0.5, 0.3, 0.05);
            }
        }

        // Shadow bolt trail particles
        if (level instanceof ServerLevel serverLevel) {
            Vec3 targetPos = hitEntity != null ? hitEntity.position() : hitResult.getLocation();
            int steps = (int) startPos.distanceTo(targetPos);

            for (int i = 0; i <= steps; i++) {
                double progress = i / (double) steps;
                Vec3 particlePos = startPos.lerp(targetPos, progress);
                serverLevel.sendParticles(ParticleTypes.SMOKE,
                        particlePos.x, particlePos.y, particlePos.z, 2, 0.1, 0.1, 0.1, 0.01);
            }
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 0.5f, 0.5f);

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
