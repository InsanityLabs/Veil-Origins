package com.veilorigins.origins.tidecaller;

import com.veilorigins.api.OriginAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.ChatFormatting;

public class TidalWaveAbility extends OriginAbility {
    private static final int COOLDOWN = 30 * 20; // 30 seconds
    private static final int HUNGER_COST = 6;
    private static final int RADIUS = 15;
    private static final int WATER_DURATION = 15 * 20; // 15 seconds

    // Track temporary water blocks to clean up
    private final List<WaterBlock> temporaryWater = new ArrayList<>();

    public TidalWaveAbility() {
        super("tidal_wave", COOLDOWN);
    }

    private static class WaterBlock {
        BlockPos pos;
        long removeTime;

        WaterBlock(BlockPos pos, long removeTime) {
            this.pos = pos;
            this.removeTime = removeTime;
        }
    }

    @Override
    public void onActivate(Player player, Level level) {
        if (level.isClientSide())
            return;

        BlockPos center = player.blockPosition();
        long currentTime = level.getGameTime();

        // Sound and visuals
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.TRIDENT_RIPTIDE_3.value(), SoundSource.PLAYERS, 1.0f, 0.5f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.GENERIC_SPLASH, SoundSource.PLAYERS, 1.5f, 0.8f);

        if (level instanceof ServerLevel serverLevel) {
            // Big wave effect
            for (int ring = 1; ring <= RADIUS; ring += 2) {
                for (int i = 0; i < ring * 8; i++) {
                    double angle = (i / (double) (ring * 8)) * Math.PI * 2;
                    double x = player.getX() + Math.cos(angle) * ring;
                    double z = player.getZ() + Math.sin(angle) * ring;

                    serverLevel.sendParticles(ParticleTypes.SPLASH,
                            x, player.getY() + 0.5, z, 5, 0.2, 0.3, 0.2, 0.3);
                    serverLevel.sendParticles(ParticleTypes.BUBBLE,
                            x, player.getY() + 0.5, z, 3, 0.2, 0.3, 0.2, 0.1);
                }
            }
        }

        // Push entities and damage
        AABB area = new AABB(center).inflate(RADIUS);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area);
        int enemiesHit = 0;

        for (LivingEntity entity : entities) {
            if (entity != player) {
                // Push away from player
                Vec3 direction = entity.position().subtract(player.position()).normalize();
                double distance = entity.distanceTo(player);
                double pushStrength = Math.max(0.5, (RADIUS - distance) / RADIUS) * 2.0;

                entity.setDeltaMovement(entity.getDeltaMovement().add(
                        direction.x * pushStrength,
                        0.5,
                        direction.z * pushStrength));
                entity.hurtMarked = true;

                // Base water damage
                float damage = 4.0f;

                // Extra damage to nether/fire mobs
                String mobType = entity.getType().toString().toLowerCase();
                if (mobType.contains("blaze") || mobType.contains("magma") ||
                        mobType.contains("strider") || mobType.contains("piglin") ||
                        mobType.contains("hoglin") || mobType.contains("ghast") ||
                        mobType.contains("wither_skeleton") || entity.isOnFire()) {
                    damage = 10.0f; // Extra damage to fire/nether mobs
                    entity.extinguishFire(); // Extinguish them

                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.CLOUD,
                                entity.getX(), entity.getY() + 1, entity.getZ(),
                                10, 0.3, 0.3, 0.3, 0.1);
                    }
                }

                entity.hurt(level.damageSources().drown(), damage);
                enemiesHit++;
            }
        }

        // Extinguish fires
        int firesExtinguished = 0;
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-RADIUS, -3, -RADIUS),
                center.offset(RADIUS, 3, RADIUS))) {
            if (level.getBlockState(pos).is(Blocks.FIRE)) {
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                firesExtinguished++;
            }
        }

        // Create temporary water source blocks in a ring pattern
        for (int ring = 3; ring <= RADIUS; ring += 3) {
            for (int i = 0; i < ring * 4; i++) {
                double angle = (i / (double) (ring * 4)) * Math.PI * 2;
                int x = center.getX() + (int) (Math.cos(angle) * ring);
                int z = center.getZ() + (int) (Math.sin(angle) * ring);

                // Find ground level
                BlockPos waterPos = new BlockPos(x, center.getY(), z);
                while (level.isEmptyBlock(waterPos) && waterPos.getY() > level.dimensionType().minY()) {
                    waterPos = waterPos.below();
                }
                waterPos = waterPos.above();

                if (level.isEmptyBlock(waterPos) && !level.isEmptyBlock(waterPos.below())) {
                    level.setBlockAndUpdate(waterPos, Blocks.WATER.defaultBlockState());
                    temporaryWater.add(new WaterBlock(waterPos.immutable(), currentTime + WATER_DURATION));
                }
            }
        }

        // Feedback message
        if (enemiesHit > 0 || firesExtinguished > 0) {
            player.displayClientMessage(Component.literal(ChatFormatting.AQUA + "" + ChatFormatting.BOLD + "Tidal Wave! "
                    + ChatFormatting.RESET + ChatFormatting.GRAY + "Hit " + enemiesHit + " enemies" +
                    (firesExtinguished > 0 ? ", extinguished " + firesExtinguished + " fires" : "")), false);
        }

        player.causeFoodExhaustion(HUNGER_COST);
        startCooldown();
    }

    @Override
    public void tick(Player player) {
        if (player.level().isClientSide())
            return;

        Level level = player.level();
        long time = level.getGameTime();

        // Clean up expired water blocks
        Iterator<WaterBlock> it = temporaryWater.iterator();
        while (it.hasNext()) {
            WaterBlock wb = it.next();
            if (time >= wb.removeTime) {
                // Only remove if it's still water (don't break player-placed water)
                if (level.getBlockState(wb.pos).is(Blocks.WATER)) {
                    level.setBlockAndUpdate(wb.pos, Blocks.AIR.defaultBlockState());
                }
                it.remove();
            }
        }
    }

    @Override
    public boolean canUse(Player player) {
        return !isOnCooldown() && player.getFoodData().getFoodLevel() >= HUNGER_COST;
    }

    @Override
    public int getResourceCost() {
        return HUNGER_COST;
    }
}
