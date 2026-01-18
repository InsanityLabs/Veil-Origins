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

/**
 * Vampling Legendary: Blood Frenzy
 * Enter a blood frenzy, gaining speed and lifesteal on all attacks.
 */
public class VamplingLegendary extends LegendaryAbility {
    
    public VamplingLegendary() {
        super("blood_frenzy", "Blood Frenzy", 
            "Enter a blood frenzy for 15 seconds. Gain massive speed and lifesteal on all attacks.",
            180);
    }
    
    @Override
    protected void onActivate(Player player, Level level) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;
        
        player.addEffect(new MobEffectInstance(MobEffects.SPEED, 300, 3, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 300, 1, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, false, true));
        
        // Initial blood burst damage
        AABB area = player.getBoundingBox().inflate(8);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player);
        
        for (LivingEntity entity : entities) {
            entity.hurt(level.damageSources().magic(), 3.0f);
            player.heal(1.5f);
        }
        
        serverLevel.sendParticles(ParticleTypes.CRIMSON_SPORE,
            player.getX(), player.getY() + 1, player.getZ(), 50, 1.5, 1.0, 1.5, 0.1);
        
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.BAT_TAKEOFF, SoundSource.PLAYERS, 1.5f, 0.8f);
    }
    
    @Override
    public int getResourceCost() { return 30; }
}
