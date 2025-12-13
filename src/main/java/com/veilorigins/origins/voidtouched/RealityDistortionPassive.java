package com.veilorigins.origins.voidtouched;

import com.veilorigins.api.OriginPassive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Random;

public class RealityDistortionPassive extends OriginPassive {
    private int tickCounter = 0;
    private final Random random = new Random();

    public RealityDistortionPassive() {
        super("reality_distortion");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;
        Level level = player.level();
        
        // 5% chance to phase through blocks when walking
        if (player.getDeltaMovement().horizontalDistance() > 0.1) {
            if (random.nextDouble() < 0.0005) { // Per tick chance
                Vec3 pos = player.position();
                Vec3 motion = player.getDeltaMovement();
                
                // Try to phase forward
                BlockPos ahead = player.blockPosition().offset(
                    (int)Math.signum(motion.x),
                    0,
                    (int)Math.signum(motion.z)
                );
                
                if (!level.getBlockState(ahead).isAir()) {
                    // Phase through
                    player.setPos(ahead.getX() + 0.5, pos.y, ahead.getZ() + 0.5);
                    
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.PORTAL,
                            pos.x, pos.y + 1, pos.z, 10, 0.3, 0.5, 0.3, 0.1);
                    }
                }
            }
        }
        
        // Check every 20 ticks (1 second)
        if (tickCounter >= 20) {
            tickCounter = 0;
            
            // Items dropped have 10% chance to vanish
            AABB itemArea = new AABB(
                player.getX() - 2, player.getY() - 1, player.getZ() - 2,
                player.getX() + 2, player.getY() + 2, player.getZ() + 2
            );
            
            List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, itemArea);
            for (ItemEntity item : items) {
                if (item.getAge() < 20 && random.nextDouble() < 0.1) {
                    // Vanish into void
                    if (level instanceof ServerLevel serverLevel) {
                        Vec3 itemPos = item.position();
                        serverLevel.sendParticles(ParticleTypes.PORTAL,
                            itemPos.x, itemPos.y, itemPos.z, 20, 0.2, 0.2, 0.2, 0.2);
                    }
                    item.discard();
                }
            }
            
            // Visual glitches for nearby players
            AABB playerArea = new AABB(
                player.getX() - 8, player.getY() - 4, player.getZ() - 8,
                player.getX() + 8, player.getY() + 4, player.getZ() + 8
            );
            
            List<Player> nearbyPlayers = level.getEntitiesOfClass(Player.class, playerArea,
                p -> p != player);
            
            if (!nearbyPlayers.isEmpty() && level instanceof ServerLevel serverLevel) {
                Vec3 pos = player.position();
                serverLevel.sendParticles(ParticleTypes.PORTAL,
                    pos.x, pos.y + 1, pos.z, 5, 0.5, 0.5, 0.5, 0.05);
            }
        }
    }

    @Override
    public void onEquip(Player player) {
        // Called when player selects Voidtouched origin
    }

    @Override
    public void onRemove(Player player) {
        // Called when player changes from Voidtouched origin
    }
}
