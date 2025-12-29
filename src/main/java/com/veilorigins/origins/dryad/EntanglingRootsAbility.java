package com.veilorigins.origins.dryad;

import com.veilorigins.api.OriginAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class EntanglingRootsAbility extends OriginAbility {
    private static final int RESOURCE_COST = 5;
    private static final double RADIUS = 8.0;
    private static final float DAMAGE = 4.0f;

    public EntanglingRootsAbility() {
        super("entangling_roots", 20); // 20 second cooldown
    }

    @Override
    public void onActivate(Player player, Level level) {
        BlockPos playerPos = player.blockPosition();

        // Find all living entities in radius (except the player)
        AABB area = new AABB(playerPos).inflate(RADIUS);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area,
                entity -> entity != player && entity.isAlive());

        for (LivingEntity entity : entities) {
            // Apply damage
            entity.hurt(level.damageSources().magic(), DAMAGE);

            // Apply root effect (immobilization via slow + no jump)
            entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 4)); // 5 seconds, level 5
                                                                                           // (rooted)
            entity.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 100, 128)); // Negative jump = can't jump
            entity.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0)); // Minor poison for 3 seconds

            // Spawn vine particles around each trapped entity
            if (level instanceof ServerLevel serverLevel) {
                BlockPos entityPos = entity.blockPosition();

                // Rising vine effect
                for (int i = 0; i < 20; i++) {
                    double angle = Math.random() * Math.PI * 2;
                    double radius = 0.5 + Math.random() * 0.5;
                    double x = entityPos.getX() + 0.5 + Math.cos(angle) * radius;
                    double z = entityPos.getZ() + 0.5 + Math.sin(angle) * radius;
                    double y = entityPos.getY() + Math.random() * 2;

                    serverLevel.sendParticles(ParticleTypes.COMPOSTER,
                            x, y, z, 1, 0.1, 0.1, 0.1, 0.01);
                }

                // Leaf burst
                serverLevel.sendParticles(ParticleTypes.CHERRY_LEAVES,
                        entityPos.getX() + 0.5, entityPos.getY() + 1, entityPos.getZ() + 0.5,
                        15, 0.5, 0.5, 0.5, 0.05);
            }
        }

        // Ground effect - spawn particles in circle around player
        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 50; i++) {
                double angle = (i / 50.0) * Math.PI * 2;
                for (double r = 1; r <= RADIUS; r += 2) {
                    double x = playerPos.getX() + 0.5 + Math.cos(angle) * r;
                    double z = playerPos.getZ() + 0.5 + Math.sin(angle) * r;

                    serverLevel.sendParticles(ParticleTypes.COMPOSTER,
                            x, playerPos.getY() + 0.1, z, 1, 0.1, 0.1, 0.1, 0.01);
                }
            }

            // Central burst of leaves
            serverLevel.sendParticles(ParticleTypes.CHERRY_LEAVES,
                    playerPos.getX() + 0.5, playerPos.getY() + 1, playerPos.getZ() + 0.5,
                    30, 1.0, 0.5, 1.0, 0.1);
        }

        // Place temporary vine blocks (will decay)
        for (LivingEntity entity : entities) {
            BlockPos basePos = entity.blockPosition();
            // Try to place a vine on adjacent blocks
            for (BlockPos offset : BlockPos.betweenClosed(basePos.offset(-1, 0, -1), basePos.offset(1, 1, 1))) {
                if (level.getBlockState(offset).isAir() && Math.random() < 0.3) {
                    // Check if there's a solid block to attach to
                    if (level.getBlockState(offset.above()).isSolid() ||
                            level.getBlockState(offset.north()).isSolid() ||
                            level.getBlockState(offset.south()).isSolid() ||
                            level.getBlockState(offset.east()).isSolid() ||
                            level.getBlockState(offset.west()).isSolid()) {
                        level.setBlock(offset, Blocks.VINE.defaultBlockState(), 3);
                    }
                }
            }
        }

        // Sound effects
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.GRASS_BREAK, SoundSource.PLAYERS, 1.0f, 0.5f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ROOTS_PLACE, SoundSource.PLAYERS, 1.0f, 0.8f);

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
