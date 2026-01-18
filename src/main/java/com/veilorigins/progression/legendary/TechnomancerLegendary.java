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

/** Technomancer Legendary: System Overload - Overclock all systems to maximum, becoming a machine of destruction. */
public class TechnomancerLegendary extends LegendaryAbility {
    public TechnomancerLegendary() {
        super("system_overload", "System Overload", "Overclock all systems to maximum power for 15 seconds.", 300);
    }
    
    @Override
    protected void onActivate(Player player, Level level) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;
        
        // Maximum overclock
        player.addEffect(new MobEffectInstance(MobEffects.SPEED, 300, 3, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.HASTE, 300, 3, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 300, 2, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 300, 1, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, false, true));
        
        // EMP blast
        AABB area = player.getBoundingBox().inflate(15);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player);
        
        for (LivingEntity entity : entities) {
            entity.hurt(level.damageSources().lightningBolt(), 10.0f);
            entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 3));
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 2));
        }
        
        // Electric particles
        serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, player.getX(), player.getY() + 1, player.getZ(), 200, 6.0, 2.0, 6.0, 0.5);
        serverLevel.sendParticles(ParticleTypes.CRIT, player.getX(), player.getY() + 1, player.getZ(), 100, 4.0, 1.5, 4.0, 0.3);
        
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.PLAYERS, 1.5f, 1.5f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS, 2.0f, 1.0f);
    }
    
    @Override
    public int getResourceCost() { return 60; }
}
