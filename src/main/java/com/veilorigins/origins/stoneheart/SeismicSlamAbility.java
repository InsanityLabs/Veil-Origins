package com.veilorigins.origins.stoneheart;

import com.veilorigins.api.OriginAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SeismicSlamAbility extends OriginAbility {
    private static final int RADIUS = 10;
    private static final float DAMAGE = 4.0f;
    private static final int RESOURCE_COST = 6;

    public SeismicSlamAbility() {
        super("seismic_slam", 30);
    }

    @Override
    public void onActivate(Player player, Level level) {
        Vec3 pos = player.position();
        
        // Play sound
        if (!level.isClientSide()) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(), 
                SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.0f, 0.8f);
        }
        
        // Find entities in radius
        AABB area = new AABB(pos.x - RADIUS, pos.y - 2, pos.z - RADIUS,
                             pos.x + RADIUS, pos.y + 2, pos.z + RADIUS);
        List<Entity> entities = level.getEntities(player, area);
        
        for (Entity entity : entities) {
            if (entity instanceof LivingEntity living && entity != player) {
                // Damage
                living.hurt(level.damageSources().playerAttack(player), DAMAGE);
                
                // Knockback
                Vec3 direction = living.position().subtract(pos).normalize();
                living.setDeltaMovement(direction.x * 1.5, 0.5, direction.z * 1.5);
            }
        }
        
        // Visual effects
        if (level instanceof ServerLevel serverLevel) {
            BlockPos playerPos = player.blockPosition();
            BlockState groundBlock = level.getBlockState(playerPos.below());
            
            // Ripple effect - expanding rings of particles
            for (int ring = 0; ring < 5; ring++) {
                int ringRadius = ring * 2 + 1;
                for (int i = 0; i < ringRadius * 8; i++) {
                    double angle = (i / (double)(ringRadius * 8)) * Math.PI * 2;
                    double x = pos.x + Math.cos(angle) * ringRadius;
                    double z = pos.z + Math.sin(angle) * ringRadius;
                    
                    // Ground crack particles
                    serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, groundBlock),
                        x, pos.y + 0.1, z, 3, 0.2, 0.1, 0.2, 0.1);
                    
                    // Dust clouds
                    serverLevel.sendParticles(ParticleTypes.POOF,
                        x, pos.y + 0.5, z, 2, 0.1, 0.1, 0.1, 0.02);
                }
            }
            
            // Spawn flying debris blocks
            for (int i = 0; i < 8; i++) {
                double angle = (i / 8.0) * Math.PI * 2;
                double distance = 3 + Math.random() * 2;
                double x = pos.x + Math.cos(angle) * distance;
                double z = pos.z + Math.sin(angle) * distance;
                
                BlockPos debrisPos = new BlockPos((int)x, (int)pos.y, (int)z);
                BlockState debrisBlock = level.getBlockState(debrisPos);
                
                // Use stone/dirt/gravel for debris
                if (debrisBlock.isAir() || !debrisBlock.isSolid()) {
                    debrisBlock = Math.random() > 0.5 ? Blocks.STONE.defaultBlockState() : Blocks.COBBLESTONE.defaultBlockState();
                }
                
                FallingBlockEntity fallingBlock = FallingBlockEntity.fall(level, debrisPos, debrisBlock);
                fallingBlock.setPos(x, pos.y + 0.5, z);
                
                // Launch outward and up
                Vec3 launchDir = new Vec3(Math.cos(angle), 0, Math.sin(angle));
                fallingBlock.setDeltaMovement(
                    launchDir.x * (0.5 + Math.random() * 0.5),
                    0.3 + Math.random() * 0.4,
                    launchDir.z * (0.5 + Math.random() * 0.5)
                );
                
                level.addFreshEntity(fallingBlock);
            }
            
            // Central explosion effect
            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER,
                pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
        }
        
        startCooldown();
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
