package com.veilorigins.origins.werewolf;

import com.veilorigins.api.OriginAbility;
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
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class HowlAbility extends OriginAbility {
    private static final int RESOURCE_COST = 3;
    private static final int RADIUS = 15;

    public HowlAbility() {
        super("howl", 200);
    }

    @Override
    public void onActivate(Player player, Level level) {
        Vec3 playerPos = player.position();
        AABB area = new AABB(
                playerPos.x - RADIUS, playerPos.y - RADIUS, playerPos.z - RADIUS,
                playerPos.x + RADIUS, playerPos.y + RADIUS, playerPos.z + RADIUS
        );

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area);
        
        for (LivingEntity entity : entities) {
            if (entity == player) {
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 1, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 1, false, false));
            } else {
                entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 0, false, false));
            }
        }

        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 50; i++) {
                double angle = Math.random() * Math.PI * 2;
                double distance = Math.random() * RADIUS;
                double x = playerPos.x + Math.cos(angle) * distance;
                double z = playerPos.z + Math.sin(angle) * distance;
                double y = playerPos.y + Math.random() * 3;
                
                serverLevel.sendParticles(ParticleTypes.CLOUD,
                        x, y, z, 1, 0.1, 0.1, 0.1, 0.02);
            }
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.WOLF_HOWL, SoundSource.PLAYERS, 3.0f, 0.8f);
        
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
