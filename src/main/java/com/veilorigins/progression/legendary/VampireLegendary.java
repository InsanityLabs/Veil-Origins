package com.veilorigins.progression.legendary;

import com.veilorigins.progression.LegendaryAbility;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Vampire Legendary: Blood Moon Rising
 * Transform into a swarm of bats, becoming invulnerable and draining all nearby creatures.
 */
public class VampireLegendary extends LegendaryAbility {
    
    public VampireLegendary() {
        super("blood_moon_rising", "Blood Moon Rising", 
            "Transform into a swarm of bats for 15 seconds. Invulnerable, fast, and drain blood from all nearby creatures.",
            240); // 4 minute cooldown
    }
    
    @Override
    protected void onActivate(Player player, Level level) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;
        
        // Apply transformation effects
        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 300, 0, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 300, 4, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 300, 3, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 300, 0, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, false, true));
        
        // Spawn bat swarm around player
        for (int i = 0; i < 20; i++) {
            Bat bat = new Bat(EntityType.BAT, level);
            double offsetX = (level.random.nextDouble() - 0.5) * 4;
            double offsetY = level.random.nextDouble() * 2;
            double offsetZ = (level.random.nextDouble() - 0.5) * 4;
            bat.setPos(player.getX() + offsetX, player.getY() + offsetY, player.getZ() + offsetZ);
            level.addFreshEntity(bat);
            
            // Mark bats for removal after duration (handled separately)
        }
        
        // Drain blood from nearby creatures
        AABB area = player.getBoundingBox().inflate(20);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, 
            e -> e != player && !(e instanceof Bat) && e.getType() != EntityType.ARMOR_STAND);
        
        float totalBlood = 0;
        for (LivingEntity entity : entities) {
            float damage = 6.0f;
            entity.hurt(level.damageSources().magic(), damage);
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1));
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
            totalBlood += damage * 0.75f;
            
            // Blood drain particles - line from entity to player
            Vec3 start = entity.position().add(0, entity.getBbHeight() / 2, 0);
            Vec3 end = player.position().add(0, player.getBbHeight() / 2, 0);
            Vec3 direction = end.subtract(start).normalize();
            double distance = start.distanceTo(end);
            
            for (double d = 0; d < distance; d += 0.5) {
                Vec3 pos = start.add(direction.scale(d));
                serverLevel.sendParticles(ParticleTypes.CRIMSON_SPORE,
                    pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
            }
        }
        
        // Heal player
        player.heal(totalBlood);
        
        // Visual effects
        serverLevel.sendParticles(ParticleTypes.CRIMSON_SPORE,
            player.getX(), player.getY() + 1, player.getZ(),
            100, 2.0, 2.0, 2.0, 0.2);
        
        // Sound
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.BAT_TAKEOFF, SoundSource.PLAYERS, 2.0f, 0.5f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.ENDER_DRAGON_GROWL, SoundSource.PLAYERS, 0.5f, 1.5f);
    }
    
    @Override
    public int getResourceCost() {
        return 40; // Costs 40 blood
    }
}
