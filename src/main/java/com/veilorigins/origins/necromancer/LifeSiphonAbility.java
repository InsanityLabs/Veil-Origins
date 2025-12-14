package com.veilorigins.origins.necromancer;

import com.veilorigins.api.OriginAbility;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class LifeSiphonAbility extends OriginAbility {
    private static final int RESOURCE_COST = 6;
    private static final double RANGE = 12.0;
    private static final float DAMAGE = 4.0f;
    private static final float HEAL_RATIO = 0.5f; // Heal 50% of damage dealt

    public LifeSiphonAbility() {
        super("life_siphon", 15); // 15 second cooldown
    }

    @Override
    public void onActivate(Player player, Level level) {
        Vec3 lookVec = player.getLookAngle();
        Vec3 startPos = player.position().add(0, player.getEyeHeight(), 0);

        // Find target entity in look direction
        LivingEntity target = null;
        double closestDistance = RANGE;

        AABB searchBox = new AABB(startPos, startPos.add(lookVec.scale(RANGE))).inflate(2.0);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, searchBox,
                entity -> entity != player && entity.isAlive());

        for (LivingEntity entity : entities) {
            Vec3 toEntity = entity.position().add(0, entity.getBbHeight() / 2, 0).subtract(startPos);
            double distance = toEntity.length();

            if (distance <= RANGE) {
                // Check if entity is roughly in look direction
                Vec3 normalized = toEntity.normalize();
                double dot = normalized.dot(lookVec);

                if (dot > 0.7 && distance < closestDistance) { // ~45 degree cone
                    closestDistance = distance;
                    target = entity;
                }
            }
        }

        if (target != null) {
            // Deal damage
            float actualDamage = target.hurt(level.damageSources().magic(), DAMAGE) ? DAMAGE : 0;

            if (actualDamage > 0) {
                // Apply wither effect
                target.addEffect(new MobEffectInstance(MobEffects.WITHER, 60, 0)); // 3 seconds
                target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 0)); // 4 seconds

                // Heal player
                float healAmount = actualDamage * HEAL_RATIO;
                player.heal(healAmount);

                // Visual effect - drain beam from target to player
                if (level instanceof ServerLevel serverLevel) {
                    Vec3 targetPos = target.position().add(0, target.getBbHeight() / 2, 0);
                    Vec3 playerCenter = player.position().add(0, player.getBbHeight() / 2, 0);

                    // Beam particles
                    int steps = (int) (closestDistance * 3);
                    for (int i = 0; i <= steps; i++) {
                        double progress = i / (double) steps;
                        Vec3 particlePos = targetPos.lerp(playerCenter, progress);

                        // Dark red soul-drain particles
                        serverLevel.sendParticles(ParticleTypes.SOUL,
                                particlePos.x, particlePos.y, particlePos.z,
                                1, 0.05, 0.05, 0.05, 0.01);
                        serverLevel.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
                                particlePos.x, particlePos.y, particlePos.z,
                                1, 0.1, 0.1, 0.1, 0.01);
                    }

                    // Impact effect on target
                    serverLevel.sendParticles(ParticleTypes.SOUL,
                            targetPos.x, targetPos.y, targetPos.z,
                            15, 0.3, 0.3, 0.3, 0.1);
                    serverLevel.sendParticles(ParticleTypes.SMOKE,
                            targetPos.x, targetPos.y, targetPos.z,
                            10, 0.3, 0.3, 0.3, 0.05);

                    // Heal effect on player
                    serverLevel.sendParticles(ParticleTypes.HEART,
                            player.getX(), player.getY() + player.getBbHeight() + 0.5, player.getZ(),
                            3, 0.3, 0.2, 0.3, 0.1);
                    serverLevel.sendParticles(ParticleTypes.SOUL,
                            player.getX(), player.getY() + player.getBbHeight() / 2, player.getZ(),
                            10, 0.3, 0.5, 0.3, 0.05);
                }

                // Sound effects
                level.playSound(null, target.getX(), target.getY(), target.getZ(),
                        SoundEvents.WITHER_HURT, SoundSource.PLAYERS, 0.5f, 1.2f);
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.PLAYER_BREATH, SoundSource.PLAYERS, 1.0f, 0.8f);
            }
        } else {
            // No target found - still show cast attempt
            if (level instanceof ServerLevel serverLevel) {
                Vec3 endPos = startPos.add(lookVec.scale(5));
                serverLevel.sendParticles(ParticleTypes.SOUL,
                        endPos.x, endPos.y, endPos.z,
                        5, 0.2, 0.2, 0.2, 0.02);
            }
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.WITHER_AMBIENT, SoundSource.PLAYERS, 0.3f, 1.5f);
        }

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
