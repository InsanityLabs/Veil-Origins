package com.veilorigins.origins.voidtouched;

import com.veilorigins.api.OriginAbility;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class RealityShiftAbility extends OriginAbility {
    private static final int RESOURCE_COST = 6;
    private static final int RANGE = 20;

    public RealityShiftAbility() {
        super("reality_shift", 30);
    }

    @Override
    public void onActivate(Player player, Level level) {
        Vec3 lookVec = player.getLookAngle();
        Vec3 startPos = player.position().add(0, player.getEyeHeight(), 0);
        Vec3 endPos = startPos.add(lookVec.scale(RANGE));
        
        // Find target entity
        AABB searchBox = new AABB(startPos, endPos).inflate(2.0);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, searchBox,
            entity -> entity != player);
        
        LivingEntity target = null;
        double closestDistance = Double.MAX_VALUE;
        
        for (LivingEntity entity : entities) {
            double distance = entity.position().distanceTo(startPos);
            if (distance < closestDistance && distance <= RANGE) {
                closestDistance = distance;
                target = entity;
            }
        }
        
        if (target == null) {
            player.sendSystemMessage(Component.literal("§cNo valid target found!"));
            return;
        }
        
        // Store positions
        Vec3 playerPos = player.position();
        Vec3 targetPos = target.position();
        
        // Swap positions
        player.teleportTo(targetPos.x, targetPos.y, targetPos.z);
        target.teleportTo(playerPos.x, playerPos.y, playerPos.z);
        
        // Disorient target
        target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0));
        
        // Effects
        level.playSound(null, playerPos.x, playerPos.y, playerPos.z,
            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 0.8f);
        level.playSound(null, targetPos.x, targetPos.y, targetPos.z,
            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0f, 1.2f);
        
        if (level instanceof ServerLevel serverLevel) {
            // Particles at both locations
            serverLevel.sendParticles(ParticleTypes.PORTAL,
                playerPos.x, playerPos.y + 1, playerPos.z, 50, 0.5, 0.5, 0.5, 0.3);
            serverLevel.sendParticles(ParticleTypes.PORTAL,
                targetPos.x, targetPos.y + 1, targetPos.z, 50, 0.5, 0.5, 0.5, 0.3);
        }
        
        player.sendSystemMessage(Component.literal("§5Reality shifted!"));
        
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
