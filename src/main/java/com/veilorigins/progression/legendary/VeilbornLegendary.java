package com.veilorigins.progression.legendary;

import com.veilorigins.progression.LegendaryAbility;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * Veilborn Legendary: Veil Mastery
 * Become one with the Veil, gaining invulnerability and pulling all nearby souls.
 */
public class VeilbornLegendary extends LegendaryAbility {
    
    public VeilbornLegendary() {
        super("veil_mastery", "Veil Mastery", 
            "Become one with the Veil for 10 seconds. Invulnerable, invisible, and drain life from all nearby enemies.",
            180); // 3 minute cooldown
    }
    
    @Override
    protected void onActivate(Player player, Level level) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;
        
        // Apply effects to player
        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 200, 0, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 200, 4, false, true)); // Resistance V = invuln
        player.addEffect(new MobEffectInstance(MobEffects.SPEED, 200, 2, false, true));
        
        // Drain life from nearby enemies
        AABB area = player.getBoundingBox().inflate(15);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, 
            e -> e != player && !e.isAlliedTo(player));
        
        float totalDrained = 0;
        for (LivingEntity entity : entities) {
            float damage = 4.0f;
            entity.hurt(level.damageSources().magic(), damage);
            entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 100, 1));
            totalDrained += damage * 0.5f;
            
            // Soul drain particles
            serverLevel.sendParticles(ParticleTypes.SOUL,
                entity.getX(), entity.getY() + 1, entity.getZ(),
                10, 0.3, 0.5, 0.3, 0.05);
        }
        
        // Heal player based on drained life
        player.heal(totalDrained);
        
        // Visual effects
        serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
            player.getX(), player.getY() + 1, player.getZ(),
            50, 1.0, 1.0, 1.0, 0.1);
        
        // Sound
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.0f, 0.5f);
    }
    
    @Override
    public int getResourceCost() {
        return 50; // Costs 50 soul energy
    }
}
