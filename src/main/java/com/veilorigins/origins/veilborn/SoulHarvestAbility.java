package com.veilorigins.origins.veilborn;

import com.veilorigins.api.OriginAbility;
import com.veilorigins.data.OriginData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.ChatFormatting;

import java.util.List;

public class SoulHarvestAbility extends OriginAbility {
    private static final int HARVEST_RADIUS = 10;
    private static final int RESOURCE_COST = 5;

    public SoulHarvestAbility() {
        super("soul_harvest", 60);
    }

    @Override
    public void onActivate(Player player, Level level) {
        Vec3 pos = player.position();

        // Find dead entities (recently killed) - we'll use living entities with low
        // health as proxy
        AABB area = new AABB(pos.x - HARVEST_RADIUS, pos.y - HARVEST_RADIUS, pos.z - HARVEST_RADIUS,
                pos.x + HARVEST_RADIUS, pos.y + HARVEST_RADIUS, pos.z + HARVEST_RADIUS);

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area,
                entity -> entity.isDeadOrDying() && entity != player);

        float soulEssenceGained = 0;
        int soullessCount = 0;

        for (LivingEntity entity : entities) {
            // Check if entity has a soul (exclude slimes, golems, etc.)
            if (isSoulless(entity)) {
                soullessCount++;
                continue;
            }

            // Extract soul essence based on mob's max health
            float essence = entity.getMaxHealth() / 10f;
            soulEssenceGained += essence;

            // Visual effect - soul particles flowing to player
            if (level instanceof ServerLevel serverLevel) {
                Vec3 entityPos = entity.position();
                for (int i = 0; i < 10; i++) {
                    double progress = i / 10.0;
                    Vec3 particlePos = entityPos.lerp(pos, progress);
                    serverLevel.sendParticles(ParticleTypes.SOUL,
                            particlePos.x, particlePos.y + 1, particlePos.z,
                            2, 0.1, 0.1, 0.1, 0.02);
                }
            }
        }

        // Notify about soulless creatures
        if (soullessCount > 0 && soulEssenceGained == 0) {
            player.sendSystemMessage(
                    Component.literal(ChatFormatting.GRAY + "These creatures have no souls to harvest..."));
        }

        // Grant soul essence to player (add to resource bar)
        if (soulEssenceGained > 0) {
            OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);
            if (data != null) {
                data.addResource(soulEssenceGained * 5); // Scale for resource bar
            }

            // Play sound
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.SOUL_ESCAPE, SoundSource.PLAYERS, 1.0f, 0.8f);

            // Particle burst around player
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME,
                        pos.x, pos.y + 1, pos.z, 20, 0.5, 0.5, 0.5, 0.05);
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

    private boolean isSoulless(LivingEntity entity) {
        // Slimes and magma cubes are just animated goo - no soul
        if (entity instanceof Slime || entity instanceof MagmaCube) {
            return true;
        }

        // Golems are constructs - no soul
        if (entity instanceof IronGolem || entity instanceof SnowGolem) {
            return true;
        }

        // Add more soulless creatures here as needed
        // (e.g., armor stands, shulkers, etc.)

        return false;
    }
}
