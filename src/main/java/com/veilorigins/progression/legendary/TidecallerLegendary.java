package com.veilorigins.progression.legendary;

import com.veilorigins.progression.LegendaryAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import java.util.List;

/** Tidecaller Legendary: Tsunami - Summon a massive tidal wave that sweeps away all enemies. */
public class TidecallerLegendary extends LegendaryAbility {
    public TidecallerLegendary() {
        super("tsunami", "Tsunami", "Summon a devastating tsunami that sweeps away all enemies.", 300);
    }
    
    @Override
    protected void onActivate(Player player, Level level) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;
        
        player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 400, 0, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 400, 1, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 400, 0, false, true));
        
        // Massive wave
        Vec3 look = player.getLookAngle().normalize();
        AABB area = player.getBoundingBox().inflate(30);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player);
        
        for (LivingEntity entity : entities) {
            entity.hurt(level.damageSources().drown(), 8.0f);
            entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 3));
            // Push in look direction
            entity.push(look.x * 3, 1.0, look.z * 3);
        }
        
        // Water particles in wave pattern
        for (int i = 0; i < 30; i++) {
            double dist = i;
            double x = player.getX() + look.x * dist;
            double z = player.getZ() + look.z * dist;
            serverLevel.sendParticles(ParticleTypes.SPLASH, x, player.getY() + 1, z, 50, 3.0, 2.0, 3.0, 0.5);
            serverLevel.sendParticles(ParticleTypes.BUBBLE, x, player.getY(), z, 30, 2.0, 1.0, 2.0, 0.1);
        }
        
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_SPLASH, SoundSource.PLAYERS, 3.0f, 0.5f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.TRIDENT_THUNDER.value(), SoundSource.PLAYERS, 1.0f, 1.0f);
    }
    
    @Override
    public int getResourceCost() { return 50; }
}
