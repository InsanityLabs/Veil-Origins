package com.veilorigins.progression.legendary;

import com.veilorigins.progression.LegendaryAbility;
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

/** Riftwalker Legendary: Dimensional Collapse - Tear reality apart, teleporting enemies randomly. */
public class RiftwalkerLegendary extends LegendaryAbility {
    public RiftwalkerLegendary() {
        super("dimensional_collapse", "Dimensional Collapse", "Tear reality apart, scattering enemies across dimensions.", 300);
    }
    
    @Override
    protected void onActivate(Player player, Level level) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;
        
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 3, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 2, false, true));
        
        // Teleport and damage all enemies
        AABB area = player.getBoundingBox().inflate(25);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player);
        
        for (LivingEntity entity : entities) {
            // Damage
            entity.hurt(level.damageSources().magic(), 8.0f);
            entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
            
            // Random teleport
            double newX = entity.getX() + (level.random.nextDouble() - 0.5) * 40;
            double newZ = entity.getZ() + (level.random.nextDouble() - 0.5) * 40;
            entity.teleportTo(newX, entity.getY() + 5, newZ);
            
            // Particles at old and new location
            serverLevel.sendParticles(ParticleTypes.PORTAL, entity.getX(), entity.getY(), entity.getZ(), 30, 0.5, 1.0, 0.5, 0.5);
        }
        
        // Rift particles
        serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL, player.getX(), player.getY() + 1, player.getZ(), 200, 5.0, 3.0, 5.0, 0.5);
        serverLevel.sendParticles(ParticleTypes.PORTAL, player.getX(), player.getY() + 1, player.getZ(), 300, 8.0, 4.0, 8.0, 1.0);
        
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 2.0f, 0.5f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.END_PORTAL_SPAWN, SoundSource.PLAYERS, 1.0f, 1.0f);
    }
    
    @Override
    public int getResourceCost() { return 60; }
}
