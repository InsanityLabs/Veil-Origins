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

/** Crystalline Legendary: Prismatic Burst - Release stored energy in a devastating crystal explosion. */
public class CrystallineLegendary extends LegendaryAbility {
    public CrystallineLegendary() {
        super("prismatic_burst", "Prismatic Burst", "Release all stored crystal energy in a devastating explosion.", 240);
    }
    
    @Override
    protected void onActivate(Player player, Level level) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;
        
        player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 100, 0, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 100, 2, false, true));
        
        // Crystal explosion
        AABB area = player.getBoundingBox().inflate(20);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player);
        
        for (LivingEntity entity : entities) {
            float distance = (float) entity.distanceTo(player);
            float damage = Math.max(5, 18 - distance);
            entity.hurt(level.damageSources().magic(), damage);
            entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 2));
            
            // Knockback
            double dx = entity.getX() - player.getX();
            double dz = entity.getZ() - player.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist > 0) entity.push(dx / dist * 1.5, 0.5, dz / dist * 1.5);
        }
        
        // Prismatic particles - multiple colors
        serverLevel.sendParticles(ParticleTypes.END_ROD, player.getX(), player.getY() + 1, player.getZ(), 200, 8.0, 3.0, 8.0, 0.3);
        serverLevel.sendParticles(ParticleTypes.GLOW, player.getX(), player.getY() + 1, player.getZ(), 100, 6.0, 2.0, 6.0, 0.2);
        serverLevel.sendParticles(ParticleTypes.ENCHANT, player.getX(), player.getY() + 1, player.getZ(), 150, 5.0, 2.0, 5.0, 0.5);
        
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.PLAYERS, 2.0f, 0.5f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.5f, 1.5f);
    }
    
    @Override
    public int getResourceCost() { return 80; }
}
