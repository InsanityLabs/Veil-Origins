package com.veilorigins.origins.feralkin;

import com.veilorigins.api.OriginAbility;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class PrimalRoarAbility extends OriginAbility {
    private static final int RADIUS = 10;
    private static final int RESOURCE_COST = 5;

    public PrimalRoarAbility() {
        super("primal_roar", 45);
    }

    @Override
    public void onActivate(Player player, Level level) {
        Vec3 pos = player.position();

        // Play roar sound
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.RAVAGER_ROAR,
                SoundSource.PLAYERS, 2.0f, 0.8f);

        // Find entities in radius
        AABB area = new AABB(pos.x - RADIUS, pos.y - RADIUS, pos.z - RADIUS,
                pos.x + RADIUS, pos.y + RADIUS, pos.z + RADIUS);
        List<Entity> entities = level.getEntities(player, area);

        for (Entity entity : entities) {
            if (entity instanceof Monster monster) {
                // Fear enemies - they flee
                monster.setLastHurtByMob(player);
                monster.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 160, 1));
            } else if (entity instanceof Animal animal) {
                // Rally animals to fight for you
                animal.setLastHurtByMob(null);
                animal.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 200, 0));
            }
        }

        // Particles
        if (level instanceof ServerLevel serverLevel) {
            for (int i = 0; i < 30; i++) {
                double angle = Math.random() * Math.PI * 2;
                double distance = RADIUS * Math.random();
                double x = pos.x + Math.cos(angle) * distance;
                double z = pos.z + Math.sin(angle) * distance;

                serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER,
                        x, pos.y + 1, z, 1, 0, 0, 0, 0);
            }
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
