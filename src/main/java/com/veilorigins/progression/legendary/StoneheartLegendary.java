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

/**
 * Stoneheart Legendary: Mountain's Wrath
 * Become an immovable mountain, creating a massive earthquake.
 */
public class StoneheartLegendary extends LegendaryAbility {
    
    public StoneheartLegendary() {
        super("mountains_wrath", "Mountain's Wrath", 
            "Become the mountain itself. Create a devastating earthquake and become completely invulnerable for 10 seconds.",
            240);
    }
    
    @Override
    protected void onActivate(Player player, Level level) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;
        
        // Complete invulnerability
        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 200, 4, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 4, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 200, 2, false, true)); // Rooted
        
        // Massive earthquake
        AABB area = player.getBoundingBox().inflate(20);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player);
        
        for (LivingEntity entity : entities) {
            float distance = (float) entity.distanceTo(player);
            float damage = Math.max(2, 15 - distance);
            entity.hurt(level.damageSources().sonicBoom(player), damage);
            entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 3));
            
            // Launch into air
            entity.push(0, 1.5, 0);
        }
        
        // Visual earthquake effect
        for (int x = -15; x <= 15; x++) {
            for (int z = -15; z <= 15; z++) {
                if (level.random.nextFloat() < 0.3f) {
                    BlockPos pos = player.blockPosition().offset(x, 0, z);
                    serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, 3, 0.3, 0.5, 0.3, 0.02);
                }
            }
        }
        
        serverLevel.sendParticles(ParticleTypes.EXPLOSION,
            player.getX(), player.getY(), player.getZ(), 10, 3.0, 0.5, 3.0, 0);
        
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 2.0f, 0.5f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.RAVAGER_ROAR, SoundSource.PLAYERS, 1.5f, 0.5f);
    }
    
    @Override
    public int getResourceCost() { return 50; }
}
