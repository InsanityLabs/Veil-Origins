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
import java.util.List;

/** Voidtouched Legendary: Void Singularity - Create a black hole that pulls and destroys everything. */
public class VoidtouchedLegendary extends LegendaryAbility {
    public VoidtouchedLegendary() {
        super("void_singularity", "Void Singularity", "Create a void singularity that pulls and destroys all enemies.", 360);
    }
    
    @Override
    protected void onActivate(Player player, Level level) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;
        
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 4, false, true));
        
        // Create singularity effect
        AABB area = player.getBoundingBox().inflate(30);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player);
        
        for (LivingEntity entity : entities) {
            // Pull toward player
            double dx = player.getX() - entity.getX();
            double dy = player.getY() - entity.getY();
            double dz = player.getZ() - entity.getZ();
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            
            if (dist > 0 && dist < 30) {
                double force = (30 - dist) / 30 * 2;
                entity.push(dx / dist * force, dy / dist * force * 0.5, dz / dist * force);
            }
            
            // Void damage
            float damage = 15.0f - (float)(dist / 3);
            entity.hurt(level.damageSources().magic(), Math.max(5, damage));
            entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 2));
            entity.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 60, 0));
        }
        
        // Void particles - spiral inward
        for (int i = 0; i < 360; i += 5) {
            double angle = Math.toRadians(i);
            for (double r = 15; r > 0; r -= 1) {
                double x = player.getX() + Math.cos(angle + r * 0.3) * r;
                double z = player.getZ() + Math.sin(angle + r * 0.3) * r;
                serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL, x, player.getY() + 1, z, 1, 0, 0.5, 0, 0);
            }
        }
        
        serverLevel.sendParticles(ParticleTypes.PORTAL, player.getX(), player.getY() + 1, player.getZ(), 100, 2.0, 2.0, 2.0, 0.1);
        
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 2.0f, 0.3f);
    }
    
    @Override
    public int getResourceCost() { return 70; }
}
