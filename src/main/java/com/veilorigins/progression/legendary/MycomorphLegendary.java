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

/** Mycomorph Legendary: Fungal Apocalypse - Spread deadly spores that infect and control enemies. */
public class MycomorphLegendary extends LegendaryAbility {
    public MycomorphLegendary() {
        super("fungal_apocalypse", "Fungal Apocalypse", "Release a devastating spore cloud that infects all enemies.", 300);
    }
    
    @Override
    protected void onActivate(Player player, Level level) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;
        
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 400, 2, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 400, 1, false, true));
        
        // Infect all enemies
        AABB area = player.getBoundingBox().inflate(25);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player);
        
        for (LivingEntity entity : entities) {
            entity.hurt(level.damageSources().magic(), 4.0f);
            entity.addEffect(new MobEffectInstance(MobEffects.POISON, 400, 2));
            entity.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 200, 0));
            entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 200, 2));
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1));
        }
        
        // Spread mycelium
        for (int x = -10; x <= 10; x++) {
            for (int z = -10; z <= 10; z++) {
                if (x*x + z*z <= 100 && level.random.nextFloat() < 0.4f) {
                    BlockPos pos = player.blockPosition().offset(x, -1, z);
                    if (level.getBlockState(pos).is(Blocks.GRASS_BLOCK) || level.getBlockState(pos).is(Blocks.DIRT)) {
                        level.setBlock(pos, Blocks.MYCELIUM.defaultBlockState(), 3);
                    }
                }
            }
        }
        
        // Spore particles
        serverLevel.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, player.getX(), player.getY() + 1, player.getZ(), 500, 12.0, 4.0, 12.0, 0.1);
        serverLevel.sendParticles(ParticleTypes.MYCELIUM, player.getX(), player.getY(), player.getZ(), 200, 10.0, 1.0, 10.0, 0.05);
        
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SCULK_CATALYST_BLOOM, SoundSource.PLAYERS, 2.0f, 0.7f);
    }
    
    @Override
    public int getResourceCost() { return 50; }
}
