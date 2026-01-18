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

/** Frostborn Legendary: Absolute Zero - Create a devastating ice storm that freezes everything. */
public class FrostbornLegendary extends LegendaryAbility {
    public FrostbornLegendary() {
        super("absolute_zero", "Absolute Zero", "Unleash absolute zero, freezing everything in a massive radius.", 300);
    }
    
    @Override
    protected void onActivate(Player player, Level level) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;
        
        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 200, 2, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.SPEED, 200, 2, false, true));
        
        // Freeze all enemies
        AABB area = player.getBoundingBox().inflate(25);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player);
        for (LivingEntity entity : entities) {
            entity.hurt(level.damageSources().freeze(), 10.0f);
            entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 300, 4));
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 2));
            entity.setTicksFrozen(400);
        }
        
        // Create ice around
        for (int x = -10; x <= 10; x++) {
            for (int z = -10; z <= 10; z++) {
                if (x*x + z*z <= 100) {
                    BlockPos pos = player.blockPosition().offset(x, 0, z);
                    if (level.getBlockState(pos).isAir() && level.getBlockState(pos.below()).isSolid()) {
                        level.setBlock(pos, Blocks.SNOW.defaultBlockState(), 3);
                    }
                }
            }
        }
        
        serverLevel.sendParticles(ParticleTypes.SNOWFLAKE, player.getX(), player.getY() + 1, player.getZ(), 200, 10.0, 3.0, 10.0, 0.1);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 2.0f, 0.5f);
    }
    
    @Override
    public int getResourceCost() { return 60; }
}
