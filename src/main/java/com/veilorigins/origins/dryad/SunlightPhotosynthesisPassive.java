package com.veilorigins.origins.dryad;

import com.veilorigins.api.OriginPassive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;

public class SunlightPhotosynthesisPassive extends OriginPassive {
    private int tickCounter = 0;
    private int lastFoodLevel = -1;

    public SunlightPhotosynthesisPassive() {
        super("sunlight_photosynthesis");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;
        Level level = player.level();
        BlockPos playerPos = player.blockPosition();

        // Check if in direct sunlight (sky visible and daytime)
        boolean inSunlight = level.canSeeSky(playerPos.above()) &&
                level.isDay() &&
                !level.isRaining() &&
                level.getBrightness(LightLayer.SKY, playerPos.above()) >= 15;

        // Every 20 ticks (1 second)
        if (tickCounter >= 20) {
            tickCounter = 0;

            if (inSunlight) {
                // Regenerate health slowly in sunlight (0.5 HP per second)
                if (player.getHealth() < player.getMaxHealth()) {
                    player.heal(0.5f);

                    // Photosynthesis particles
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                                player.getX(), player.getY() + player.getBbHeight() + 0.3, player.getZ(),
                                3, 0.3, 0.2, 0.3, 0.02);
                    }
                }

                // Freeze hunger in sunlight (don't let it decrease)
                if (lastFoodLevel >= 0 && player.getFoodData().getFoodLevel() < lastFoodLevel) {
                    player.getFoodData().setFoodLevel(lastFoodLevel);
                }
            }

            // Update last food level
            lastFoodLevel = player.getFoodData().getFoodLevel();

            // Check for nearby flowers - gain buffs
            int nearbyFlowers = countNearbyFlowers(level, playerPos, 5);
            if (nearbyFlowers >= 5) {
                // Many flowers = strength boost
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 0, true, false));
            }
            if (nearbyFlowers >= 3) {
                // Some flowers = speed boost
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 0, true, false));
            }
        }

        // Ambient leaf particles every 10 ticks when moving
        if (tickCounter % 10 == 0 && player.getDeltaMovement().horizontalDistance() > 0.1) {
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.CHERRY_LEAVES,
                        player.getX(), player.getY() + 0.5, player.getZ(),
                        1, 0.2, 0.2, 0.2, 0.01);
            }
        }
    }

    private int countNearbyFlowers(Level level, BlockPos center, int radius) {
        int count = 0;
        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-radius, -2, -radius),
                center.offset(radius, 2, radius))) {
            Block block = level.getBlockState(pos).getBlock();
            if (block instanceof FlowerBlock ||
                    block == Blocks.ROSE_BUSH ||
                    block == Blocks.LILAC ||
                    block == Blocks.PEONY ||
                    block == Blocks.SUNFLOWER) {
                count++;
            }
        }
        return count;
    }

    @Override
    public void onEquip(Player player) {
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                "§aAs a Dryad, you photosynthesize in sunlight. Flowers empower you."));
    }

    @Override
    public void onRemove(Player player) {
        lastFoodLevel = -1;
    }
}
