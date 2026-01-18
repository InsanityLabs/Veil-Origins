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

/** Ethereal Legendary: Spectral Dominion - Become completely ethereal and possess multiple entities. */
public class EtherealLegendary extends LegendaryAbility {
    public EtherealLegendary() {
        super("spectral_dominion", "Spectral Dominion", "Become fully ethereal, phasing through everything and terrifying enemies.", 300);
    }
    
    @Override
    protected void onActivate(Player player, Level level) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;
        
        // Complete ethereal form
        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 400, 0, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 400, 4, false, true)); // Invulnerable
        player.addEffect(new MobEffectInstance(MobEffects.SPEED, 400, 2, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 400, 0, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, false, true));
        
        // Terrify all enemies
        AABB area = player.getBoundingBox().inflate(20);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player);
        
        for (LivingEntity entity : entities) {
            entity.hurt(level.damageSources().magic(), 6.0f);
            entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 200, 0));
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 2));
            entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 2));
        }
        
        // Ghost particles
        serverLevel.sendParticles(ParticleTypes.SOUL, player.getX(), player.getY() + 1, player.getZ(), 100, 5.0, 2.0, 5.0, 0.1);
        serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT, player.getX(), player.getY() + 1, player.getZ(), 50, 3.0, 1.5, 3.0, 0.2);
        
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PHANTOM_AMBIENT, SoundSource.PLAYERS, 2.0f, 0.5f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.VEX_AMBIENT, SoundSource.PLAYERS, 1.5f, 0.7f);
    }
    
    @Override
    public int getResourceCost() { return 50; }
}
