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

/** Umbrakin Legendary: Eternal Night - Plunge the area into darkness and become one with shadows. */
public class UmbrakinLegendary extends LegendaryAbility {
    public UmbrakinLegendary() {
        super("eternal_night", "Eternal Night", "Plunge the area into darkness, becoming invisible and deadly.", 240);
    }
    
    @Override
    protected void onActivate(Player player, Level level) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;
        
        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 400, 0, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.SPEED, 400, 2, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 400, 2, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, false, true));
        
        // Blind and weaken all enemies
        AABB area = player.getBoundingBox().inflate(20);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player);
        
        for (LivingEntity entity : entities) {
            entity.hurt(level.damageSources().magic(), 5.0f);
            entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 200, 0));
            entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 200, 0));
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 2));
        }
        
        // Dark particles
        serverLevel.sendParticles(ParticleTypes.SQUID_INK, player.getX(), player.getY() + 1, player.getZ(), 100, 8.0, 3.0, 8.0, 0.1);
        serverLevel.sendParticles(ParticleTypes.SMOKE, player.getX(), player.getY() + 1, player.getZ(), 200, 10.0, 2.0, 10.0, 0.05);
        
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WARDEN_EMERGE, SoundSource.PLAYERS, 1.5f, 0.5f);
    }
    
    @Override
    public int getResourceCost() { return 50; }
}
