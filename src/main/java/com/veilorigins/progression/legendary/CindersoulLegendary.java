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
import java.util.List;

/** Cindersoul Legendary: Volcanic Eruption - Become a living volcano, raining fire and destruction. */
public class CindersoulLegendary extends LegendaryAbility {
    public CindersoulLegendary() {
        super("volcanic_eruption", "Volcanic Eruption", "Erupt like a volcano, raining fire on all enemies.", 300);
    }
    
    @Override
    protected void onActivate(Player player, Level level) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;
        
        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 400, 0, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 400, 2, false, true));
        
        // Fire explosion
        AABB area = player.getBoundingBox().inflate(20);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player);
        for (LivingEntity entity : entities) {
            entity.hurt(level.damageSources().onFire(), 12.0f);
            entity.igniteForSeconds(10);
            entity.push(0, 1.0, 0);
        }
        
        // Set fires around
        for (int x = -8; x <= 8; x++) {
            for (int z = -8; z <= 8; z++) {
                if (level.random.nextFloat() < 0.3f && x*x + z*z <= 64) {
                    BlockPos pos = player.blockPosition().offset(x, 0, z);
                    if (level.getBlockState(pos).isAir() && level.getBlockState(pos.below()).isSolid()) {
                        level.setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);
                    }
                }
            }
        }
        
        serverLevel.sendParticles(ParticleTypes.LAVA, player.getX(), player.getY() + 2, player.getZ(), 100, 5.0, 3.0, 5.0, 0.5);
        serverLevel.sendParticles(ParticleTypes.FLAME, player.getX(), player.getY() + 1, player.getZ(), 200, 8.0, 2.0, 8.0, 0.2);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 2.0f, 0.7f);
    }
    
    @Override
    public int getResourceCost() { return 60; }
}
