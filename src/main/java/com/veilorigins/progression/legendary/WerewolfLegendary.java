package com.veilorigins.progression.legendary;

import com.veilorigins.progression.LegendaryAbility;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * Werewolf Legendary: Alpha's Fury
 * Transform into the ultimate alpha werewolf, summoning a pack and gaining immense power.
 */
public class WerewolfLegendary extends LegendaryAbility {
    
    public WerewolfLegendary() {
        super("alphas_fury", "Alpha's Fury", 
            "Unleash your inner alpha for 20 seconds. Summon a wolf pack, gain massive strength, and terrify all enemies.",
            300); // 5 minute cooldown
    }
    
    @Override
    protected void onActivate(Player player, Level level) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;
        
        // Apply alpha transformation effects
        player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 400, 3, false, true)); // Strength IV
        player.addEffect(new MobEffectInstance(MobEffects.SPEED, 400, 2, false, true)); // Speed III
        player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 400, 2, false, true)); // Jump III
        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 400, 1, false, true)); // Resistance II
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 400, 1, false, true)); // Regen II
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, false, true));
        
        // Summon wolf pack using EntityType.create()
        for (int i = 0; i < 6; i++) {
            var wolf = EntityType.WOLF.create(serverLevel, null, player.blockPosition(), EntitySpawnReason.MOB_SUMMONED, false, false);
            if (wolf != null) {
                double angle = (Math.PI * 2 / 6) * i;
                double offsetX = Math.cos(angle) * 3;
                double offsetZ = Math.sin(angle) * 3;
                wolf.setPos(player.getX() + offsetX, player.getY(), player.getZ() + offsetZ);
                wolf.tame(player);
                wolf.setOrderedToSit(false);
                wolf.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 400, 2));
                wolf.addEffect(new MobEffectInstance(MobEffects.SPEED, 400, 1));
                level.addFreshEntity(wolf);
            }
        }
        
        // Terrifying howl - fear all nearby enemies
        AABB area = player.getBoundingBox().inflate(25);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, 
            e -> e != player && !e.getType().equals(EntityType.WOLF) && !e.isAlliedTo(player));
        
        for (LivingEntity entity : entities) {
            // Apply fear effects
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 2));
            entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 1));
            
            // Initial damage from howl shockwave
            entity.hurt(level.damageSources().sonicBoom(player), 8.0f);
            
            // Knockback
            double dx = entity.getX() - player.getX();
            double dz = entity.getZ() - player.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist > 0) {
                entity.push(dx / dist * 1.5, 0.5, dz / dist * 1.5);
            }
        }
        
        // Visual effects - shockwave
        for (int i = 0; i < 360; i += 10) {
            double angle = Math.toRadians(i);
            for (double r = 1; r <= 10; r += 1) {
                double x = player.getX() + Math.cos(angle) * r;
                double z = player.getZ() + Math.sin(angle) * r;
                serverLevel.sendParticles(ParticleTypes.SMOKE,
                    x, player.getY() + 0.1, z, 1, 0, 0.1, 0, 0);
            }
        }
        
        serverLevel.sendParticles(ParticleTypes.EXPLOSION,
            player.getX(), player.getY() + 1, player.getZ(),
            5, 1.0, 0.5, 1.0, 0);
        
        // Sound - epic howl (use ENDER_DRAGON_GROWL as WOLF_HOWL doesn't exist in 1.21.11)
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.ENDER_DRAGON_GROWL, SoundSource.PLAYERS, 3.0f, 0.6f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.RAVAGER_ROAR, SoundSource.PLAYERS, 1.0f, 0.8f);
    }
    
    @Override
    public int getResourceCost() {
        return 60; // Costs 60 lunar power
    }
}
