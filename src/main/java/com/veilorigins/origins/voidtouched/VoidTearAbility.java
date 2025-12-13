package com.veilorigins.origins.voidtouched;

import com.veilorigins.api.OriginAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Random;

public class VoidTearAbility extends OriginAbility {
    private static final int RESOURCE_COST = 10;
    private static final int RADIUS = 5;
    private static final Random random = new Random();

    public VoidTearAbility() {
        super("void_tear", 60);
    }

    @Override
    public void onActivate(Player player, Level level) {
        Vec3 pos = player.position();
        
        // Create rift particles
        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 100; i++) {
                double angle = random.nextDouble() * Math.PI * 2;
                double distance = random.nextDouble() * RADIUS;
                double x = pos.x + Math.cos(angle) * distance;
                double z = pos.z + Math.sin(angle) * distance;
                double y = pos.y + random.nextDouble() * 3;
                
                serverLevel.sendParticles(ParticleTypes.PORTAL,
                    x, y, z, 5, 0.2, 0.2, 0.2, 0.5);
                serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                    x, y, z, 3, 0.1, 0.1, 0.1, 0.3);
            }
        }
        
        // Void tear sound
        level.playSound(null, pos.x, pos.y, pos.z,
            SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 1.0f, 0.5f);
        
        // Find entities in radius
        AABB area = new AABB(pos.x - RADIUS, pos.y - RADIUS, pos.z - RADIUS,
                             pos.x + RADIUS, pos.y + RADIUS, pos.z + RADIUS);
        List<Entity> entities = level.getEntities(player, area);
        
        for (Entity entity : entities) {
            // Damage entities
            if (entity instanceof LivingEntity living) {
                living.hurt(level.damageSources().magic(), 6.0f);
            }
            
            // Teleport randomly within 50 blocks
            double randomX = pos.x + (random.nextDouble() - 0.5) * 100;
            double randomY = pos.y + (random.nextDouble() - 0.5) * 20;
            double randomZ = pos.z + (random.nextDouble() - 0.5) * 100;
            
            entity.teleportTo(randomX, randomY, randomZ);
            
            // Particle effect at destination
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.PORTAL,
                    randomX, randomY, randomZ, 20, 0.5, 0.5, 0.5, 0.2);
            }
        }
        
        // 10% chance to summon endermite
        if (random.nextDouble() < 0.1) {
            Endermite endermite = new Endermite(EntityType.ENDERMITE, level);
            endermite.setPos(pos.x, pos.y, pos.z);
            level.addFreshEntity(endermite);
        }
        
        startCooldown();
    }

    @Override
    public boolean canUse(Player player) {
        return !isOnCooldown();
    }

    @Override
    public int getResourceCost() {
        return RESOURCE_COST;
    }
}
