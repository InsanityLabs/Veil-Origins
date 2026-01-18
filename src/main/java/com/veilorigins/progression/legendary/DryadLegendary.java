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

/** Dryad Legendary: Nature's Wrath - Summon the full fury of nature to protect and heal. */
public class DryadLegendary extends LegendaryAbility {
    public DryadLegendary() {
        super("natures_wrath", "Nature's Wrath", "Summon the full fury of nature, healing allies and punishing enemies.", 300);
    }
    
    @Override
    protected void onActivate(Player player, Level level) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;
        
        // Nature's blessing
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 400, 2, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 1, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 400, 1, false, true));
        player.heal(10.0f);
        
        // Entangle and damage enemies
        AABB area = player.getBoundingBox().inflate(20);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player);
        
        for (LivingEntity entity : entities) {
            if (entity.isAlliedTo(player)) {
                // Heal allies
                entity.heal(6.0f);
                entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
            } else {
                // Damage and root enemies
                entity.hurt(level.damageSources().thorns(player), 8.0f);
                entity.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 1));
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 4)); // Rooted
            }
        }
        
        // Grow nature around
        for (int x = -12; x <= 12; x++) {
            for (int z = -12; z <= 12; z++) {
                if (x*x + z*z <= 144 && level.random.nextFloat() < 0.3f) {
                    BlockPos pos = player.blockPosition().offset(x, 0, z);
                    BlockPos below = pos.below();
                    if (level.getBlockState(pos).isAir() && level.getBlockState(below).is(Blocks.GRASS_BLOCK)) {
                        if (level.random.nextFloat() < 0.3f) {
                            level.setBlock(pos, Blocks.POPPY.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
        
        // Nature particles
        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getY() + 1, player.getZ(), 200, 10.0, 3.0, 10.0, 0.1);
        serverLevel.sendParticles(ParticleTypes.COMPOSTER, player.getX(), player.getY() + 1, player.getZ(), 100, 8.0, 2.0, 8.0, 0.1);
        
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CHORUS_FLOWER_GROW, SoundSource.PLAYERS, 2.0f, 1.0f);
    }
    
    @Override
    public int getResourceCost() { return 50; }
}
