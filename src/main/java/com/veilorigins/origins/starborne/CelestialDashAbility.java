package com.veilorigins.origins.starborne;

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

public class CelestialDashAbility extends OriginAbility {
    private static final int COOLDOWN = 10;
    private static final int HUNGER_COST = 2;
    private static final double DASH_STRENGTH = 2.5;

    public CelestialDashAbility() {
        super("celestial_dash", COOLDOWN);
    }

    @Override
    public void onActivate(Player player, Level level) {
        Vec3 look = player.getLookAngle();
        Vec3 velocity = look.scale(DASH_STRENGTH);

        // Push player
        player.setDeltaMovement(velocity);
        player.hurtMarked = true; // Update client velocity
        // Reset fall distance
        player.fallDistance = 0;

        // Sound
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.PLAYERS, 1.0f, 1.5f);

        // Damage enemies in path (handled immediately or via tick? Instant is easier)
        // Check bounding box along path
        AABB pathBox = player.getBoundingBox().expandTowards(velocity.scale(2)).inflate(1.0);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, pathBox);
        for (LivingEntity target : targets) {
            if (target != player) {
                target.hurt(level.damageSources().magic(), 2.0f);
            }
        }

        if (level instanceof ServerLevel serverLevel) {
            // Trail of light
            for (int i = 0; i < 10; i++) {
                double d = i / 10.0;
                serverLevel.sendParticles(ParticleTypes.END_ROD,
                        player.getX() - (look.x * d * 2),
                        player.getY() - (look.y * d * 2) + 1,
                        player.getZ() - (look.z * d * 2),
                        1, 0, 0, 0, 0.01);
            }
        }

        player.causeFoodExhaustion(HUNGER_COST);
        startCooldown();
    }

    @Override
    public boolean canUse(Player player) {
        return !isOnCooldown() && player.getFoodData().getFoodLevel() >= 2; // Approximate check
    }

    @Override
    public int getResourceCost() {
        return 0; // Handled manually
    }
}
