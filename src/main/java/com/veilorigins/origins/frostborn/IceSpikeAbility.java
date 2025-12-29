package com.veilorigins.origins.frostborn;

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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class IceSpikeAbility extends OriginAbility {
    private static final int RESOURCE_COST = 2;

    public IceSpikeAbility() {
        super("ice_spike", 8);
    }

    @Override
    public void onActivate(Player player, Level level) {
        Vec3 lookVec = player.getLookAngle();
        Vec3 startPos = player.position().add(0, player.getEyeHeight(), 0);
        Vec3 endPos = startPos.add(lookVec.scale(20));
        
        // Raycast
        BlockHitResult hitResult = level.clip(new net.minecraft.world.level.ClipContext(
            startPos, endPos,
            net.minecraft.world.level.ClipContext.Block.COLLIDER,
            net.minecraft.world.level.ClipContext.Fluid.ANY,
            player
        ));
        
        // Check for entity hit
        net.minecraft.world.phys.AABB searchBox = new net.minecraft.world.phys.AABB(
            startPos, hitResult.getLocation()
        ).inflate(1.0);
        
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
        
        Vec3 targetPos = hitEntity != null ? hitEntity.position() : hitResult.getLocation();
        
        if (hitEntity != null) {
            // Damage and slow target
            hitEntity.hurt(level.damageSources().magic(), 3.0f);
            hitEntity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 1));
        }
        
        // Freeze water at impact
        BlockPos impactPos = BlockPos.containing(targetPos);
        if (level.getBlockState(impactPos).is(Blocks.WATER)) {
            level.setBlock(impactPos, Blocks.ICE.defaultBlockState(), 3);
        }
        
        // Ice spike trail particles - blue theme
        if (level instanceof ServerLevel serverLevel) {
            int steps = (int) startPos.distanceTo(targetPos);
            
            for (int i = 0; i <= steps; i++) {
                double progress = i / (double) steps;
                Vec3 particlePos = startPos.lerp(targetPos, progress);
                
                // Blue soul fire particles for icy effect
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                    particlePos.x, particlePos.y, particlePos.z, 2, 0.1, 0.1, 0.1, 0.01);
                
                // Mix with snowflakes for texture
                serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                    particlePos.x, particlePos.y, particlePos.z, 1, 0.1, 0.1, 0.1, 0.01);
            }
            
            // Impact effect - intense blue burst
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                targetPos.x, targetPos.y, targetPos.z, 30, 0.3, 0.3, 0.3, 0.15);
            serverLevel.sendParticles(ParticleTypes.SOUL,
                targetPos.x, targetPos.y, targetPos.z, 15, 0.5, 0.5, 0.5, 0.1);
            serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                targetPos.x, targetPos.y, targetPos.z, 20, 0.3, 0.3, 0.3, 0.1);
        }
        
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 0.5f, 1.5f);
        
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
