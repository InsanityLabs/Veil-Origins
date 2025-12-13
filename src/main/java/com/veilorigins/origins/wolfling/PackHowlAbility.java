package com.veilorigins.origins.wolfling;

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

public class PackHowlAbility extends OriginAbility {
    private static final int RESOURCE_COST = 2;
    private static final int RADIUS = 10;

    public PackHowlAbility() {
        super("pack_howl", 300);
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
                entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 0, false, false));
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 100, 0, false, false));
            } else {
                entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0, false, false));
            }
        }

        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 30; i++) {
                double angle = Math.random() * Math.PI * 2;
                double distance = Math.random() * RADIUS;
                double x = playerPos.x + Math.cos(angle) * distance;
                double z = playerPos.z + Math.sin(angle) * distance;
                double y = playerPos.y + Math.random() * 2;
                
                serverLevel.sendParticles(ParticleTypes.CLOUD,
                        x, y, z, 1, 0.1, 0.1, 0.1, 0.01);
            }
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.WOLF_HOWL, SoundSource.PLAYERS, 2.0f, 1.0f);
        
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
