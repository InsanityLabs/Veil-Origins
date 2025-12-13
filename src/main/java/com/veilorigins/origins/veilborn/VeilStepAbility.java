package com.veilorigins.origins.veilborn;

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

public class VeilStepAbility extends OriginAbility {
    private static final int TELEPORT_RANGE = 20;
    private static final int RESOURCE_COST = 3;

    public VeilStepAbility() {
        super("veil_step", 30);
    }

    @Override
    public void onActivate(Player player, Level level) {
        Vec3 lookVec = player.getLookAngle();
        Vec3 startPos = player.position();
        Vec3 endPos = startPos.add(lookVec.scale(TELEPORT_RANGE));
        
        // Raycast to find target position (can phase through 1-block walls)
        BlockHitResult hitResult = level.clip(new ClipContext(
            startPos.add(0, player.getEyeHeight(), 0),
            endPos.add(0, player.getEyeHeight(), 0),
            ClipContext.Block.COLLIDER,
            ClipContext.Fluid.NONE,
            player
        ));
        
        Vec3 targetPos = hitResult.getType() == HitResult.Type.MISS ? endPos : hitResult.getLocation();
        
        // Check if we can phase through a 1-block wall
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos hitBlockPos = hitResult.getBlockPos();
            Vec3 direction = lookVec.normalize();
            
            // Check up to 3 blocks ahead to find air space beyond wall
            boolean foundAirSpace = false;
            BlockPos airPos = null;
            
            for (int i = 1; i <= 3; i++) {
                BlockPos checkPos = hitBlockPos.offset(
                    (int)(direction.x * i),
                    (int)(direction.y * i),
                    (int)(direction.z * i)
                );
                
                // Check if this position and the one above it are air (player needs 2 blocks of space)
                if (level.getBlockState(checkPos).isAir() && 
                    level.getBlockState(checkPos.above()).isAir()) {
                    foundAirSpace = true;
                    airPos = checkPos;
                    break;
                }
            }
            
            // If we found air space within 3 blocks, phase through
            if (foundAirSpace && airPos != null) {
                targetPos = Vec3.atCenterOf(airPos);
            }
        }
        
        // Find safe ground position - check blocks below target
        BlockPos targetBlockPos = BlockPos.containing(targetPos);
        double safeY = targetPos.y;
        boolean foundGround = false;
        
        for (int i = 0; i < 10; i++) {
            BlockPos checkPos = targetBlockPos.below(i);
            BlockState blockState = level.getBlockState(checkPos);
            
            if (!blockState.isAir() && blockState.isSolid()) {
                safeY = checkPos.getY() + 1.0;
                foundGround = true;
                break;
            }
        }
        
        // If no ground found, check upwards
        if (!foundGround) {
            for (int i = 1; i < 10; i++) {
                BlockPos checkPos = targetBlockPos.above(i);
                BlockState blockBelow = level.getBlockState(checkPos.below());
                
                if (!blockBelow.isAir() && blockBelow.isSolid()) {
                    safeY = checkPos.getY();
                    break;
                }
            }
        }
        
        Vec3 finalPos = new Vec3(targetPos.x, safeY, targetPos.z);
        
        // Teleport player to safe position
        player.teleportTo(finalPos.x, finalPos.y, finalPos.z);
        
        // Play sound at both locations
        level.playSound(null, startPos.x, startPos.y, startPos.z, 
            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 0.8f);
        level.playSound(null, finalPos.x, finalPos.y, finalPos.z, 
            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.2f);
        
        if (level instanceof ServerLevel serverLevel) {
            // Create spectral trail visible to all players
            int steps = (int) startPos.distanceTo(finalPos);
            for (int i = 0; i <= steps; i++) {
                double progress = i / (double) steps;
                Vec3 particlePos = startPos.lerp(finalPos, progress);
                
                // Purple/black wispy particles (Veilborn theme)
                serverLevel.sendParticles(ParticleTypes.PORTAL, 
                    particlePos.x, particlePos.y + 1, particlePos.z, 
                    8, 0.3, 0.5, 0.3, 0.02);
                
                serverLevel.sendParticles(ParticleTypes.SMOKE, 
                    particlePos.x, particlePos.y + 1, particlePos.z, 
                    3, 0.2, 0.3, 0.2, 0.01);
            }
            
            // Burst at start and end positions
            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                startPos.x, startPos.y + 1, startPos.z, 30, 0.5, 0.5, 0.5, 0.1);
            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                finalPos.x, finalPos.y + 1, finalPos.z, 30, 0.5, 0.5, 0.5, 0.1);
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
