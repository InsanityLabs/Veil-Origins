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

/** Skyborn Legendary: Eye of the Storm - Become the center of a devastating tornado. */
public class SkybornLegendary extends LegendaryAbility {
    public SkybornLegendary() {
        super("eye_of_storm", "Eye of the Storm", "Become the eye of a devastating tornado for 15 seconds.", 240);
    }
    
    @Override
    protected void onActivate(Player player, Level level) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;
        
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 300, 0, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.SPEED, 300, 3, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 300, 4, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 300, 1, false, true));
        
        // Launch player up
        player.push(0, 2.0, 0);
        
        // Tornado effect - pull and damage enemies
        AABB area = player.getBoundingBox().inflate(20);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player);
        
        for (LivingEntity entity : entities) {
            entity.hurt(level.damageSources().flyIntoWall(), 6.0f);
            // Pull toward player and launch up
            double dx = player.getX() - entity.getX();
            double dz = player.getZ() - entity.getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist > 0) {
                entity.push(dx / dist * 0.5, 1.5, dz / dist * 0.5);
            }
        }
        
        // Tornado particles
        for (int i = 0; i < 360; i += 15) {
            double angle = Math.toRadians(i);
            for (double h = 0; h < 10; h += 0.5) {
                double r = 3 + h * 0.5;
                double x = player.getX() + Math.cos(angle + h * 0.5) * r;
                double z = player.getZ() + Math.sin(angle + h * 0.5) * r;
                serverLevel.sendParticles(ParticleTypes.CLOUD, x, player.getY() + h, z, 1, 0, 0, 0, 0);
            }
        }
        
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ELYTRA_FLYING, SoundSource.PLAYERS, 2.0f, 0.5f);
    }
    
    @Override
    public int getResourceCost() { return 50; }
}
