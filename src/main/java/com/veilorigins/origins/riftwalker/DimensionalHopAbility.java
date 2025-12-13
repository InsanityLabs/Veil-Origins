package com.veilorigins.origins.riftwalker;

import com.veilorigins.api.OriginAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class DimensionalHopAbility extends OriginAbility {
    private static final int TELEPORT_RANGE = 64;
    private static final int RESOURCE_COST = 3;

    public DimensionalHopAbility() {
        super("dimensional_hop", 20);
    }

    @Override
    public void onActivate(Player player, Level level) {
        Vec3 startPos = player.position();
        Vec3 lookVec = player.getLookAngle();
        Vec3 endPos = startPos.add(lookVec.scale(TELEPORT_RANGE));
        
        // Raycast to find visible target position
        BlockHitResult hitResult = level.clip(new ClipContext(
            startPos.add(0, player.getEyeHeight(), 0),
            endPos.add(0, player.getEyeHeight(), 0),
            ClipContext.Block.VISUAL,
            ClipContext.Fluid.NONE,
            player
        ));
        
        Vec3 targetPos = hitResult.getType() == HitResult.Type.MISS ? endPos : hitResult.getLocation();
        
        // Find safe ground
        BlockPos targetBlockPos = BlockPos.containing(targetPos);
        double safeY = targetPos.y;
        boolean foundGround = false;
        
        for (int i = 0; i < 20; i++) {
            BlockPos checkPos = targetBlockPos.below(i);
            BlockState blockState = level.getBlockState(checkPos);
            
            if (!blockState.isAir() && blockState.isSolid()) {
                safeY = checkPos.getY() + 1.0;
                foundGround = true;
                break;
            }
        }
        
        if (!foundGround) {
            for (int i = 1; i < 20; i++) {
                BlockPos checkPos = targetBlockPos.above(i);
                BlockState blockBelow = level.getBlockState(checkPos.below());
                
                if (!blockBelow.isAir() && blockBelow.isSolid()) {
                    safeY = checkPos.getY();
                    break;
                }
            }
        }
        
        Vec3 finalPos = new Vec3(targetPos.x, safeY, targetPos.z);
        
        // Teleport player - no fall damage
        player.teleportTo(finalPos.x, finalPos.y, finalPos.z);
        player.fallDistance = 0;
        
        // Play sounds
        level.playSound(null, startPos.x, startPos.y, startPos.z, 
            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.5f);
        level.playSound(null, finalPos.x, finalPos.y, finalPos.z, 
            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.5f);
        
        if (level instanceof ServerLevel serverLevel) {
            // Ender particle trail
            int steps = (int) startPos.distanceTo(finalPos);
            for (int i = 0; i <= steps; i++) {
                double progress = i / (double) steps;
                Vec3 particlePos = startPos.lerp(finalPos, progress);
                
                serverLevel.sendParticles(ParticleTypes.PORTAL, 
                    particlePos.x, particlePos.y + 1, particlePos.z, 
                    10, 0.3, 0.5, 0.3, 0.05);
            }
            
            // Burst effects
            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                startPos.x, startPos.y + 1, startPos.z, 40, 0.5, 0.5, 0.5, 0.2);
            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                finalPos.x, finalPos.y + 1, finalPos.z, 40, 0.5, 0.5, 0.5, 0.2);
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
