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

/** Starborne Legendary: Supernova - Explode with stellar energy, blinding and damaging all enemies. */
public class StarborneLegendary extends LegendaryAbility {
    public StarborneLegendary() {
        super("supernova", "Supernova", "Explode with the power of a dying star, devastating all nearby enemies.", 300);
    }
    
    @Override
    protected void onActivate(Player player, Level level) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;
        
        player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 200, 0, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 2, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 40, 1, false, true));
        
        // Supernova explosion
        AABB area = player.getBoundingBox().inflate(25);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player);
        
        for (LivingEntity entity : entities) {
            float distance = (float) entity.distanceTo(player);
            float damage = Math.max(4, 20 - distance);
            entity.hurt(level.damageSources().magic(), damage);
            entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0));
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1));
            
            // Knockback
            double dx = entity.getX() - player.getX();
            double dz = entity.getZ() - player.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist > 0) entity.push(dx / dist * 2, 0.8, dz / dist * 2);
        }
        
        // Massive light explosion
        serverLevel.sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 1, player.getZ(), 500, 10.0, 5.0, 10.0, 0.5);
        serverLevel.sendParticles(ParticleTypes.FIREWORK, player.getX(), player.getY() + 1, player.getZ(), 50, 2.0, 2.0, 2.0, 0.3);
        
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 2.0f, 1.5f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 2.0f, 1.2f);
    }
    
    @Override
    public int getResourceCost() { return 70; }
}
