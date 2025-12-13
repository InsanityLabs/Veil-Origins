package com.veilorigins.origins.vampling;

import com.veilorigins.api.OriginAbility;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class LifeStealAbility extends OriginAbility {
    private static final int RESOURCE_COST = 0;
    private static final float DRAIN_RANGE = 2.5f;
    private static final float DRAIN_DAMAGE = 1.5f;
    private static final float HEAL_AMOUNT = 1.5f;

    public LifeStealAbility() {
        super("life_steal", 100);
    }

    @Override
    public void onActivate(Player player, Level level) {
        Vec3 playerPos = player.position();
        AABB searchBox = new AABB(playerPos.x - DRAIN_RANGE, playerPos.y - DRAIN_RANGE, playerPos.z - DRAIN_RANGE,
                playerPos.x + DRAIN_RANGE, playerPos.y + DRAIN_RANGE, playerPos.z + DRAIN_RANGE);

        List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, searchBox,
                entity -> entity != player && entity.isAlive());

        if (!nearbyEntities.isEmpty()) {
            LivingEntity target = nearbyEntities.get(0);
            
            target.hurt(level.damageSources().magic(), DRAIN_DAMAGE);
            player.heal(HEAL_AMOUNT);

            if (level instanceof ServerLevel serverLevel) {
                Vec3 targetPos = target.position().add(0, target.getBbHeight() / 2, 0);
                Vec3 playerEyePos = player.position().add(0, player.getEyeHeight(), 0);
                
                for (int i = 0; i < 10; i++) {
                    double progress = i / 10.0;
                    Vec3 particlePos = targetPos.lerp(playerEyePos, progress);
                    serverLevel.sendParticles(ParticleTypes.CRIMSON_SPORE,
                            particlePos.x, particlePos.y, particlePos.z, 1, 0.05, 0.05, 0.05, 0.01);
                }
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 0.7f, 1.0f);
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
