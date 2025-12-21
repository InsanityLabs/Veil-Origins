package com.veilorigins.origins.frostborn;

import com.veilorigins.api.OriginAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import net.minecraft.ChatFormatting;

public class BlizzardAbility extends OriginAbility {
    private static final int RESOURCE_COST = 10;
    private static final int DURATION = 30 * 20; // 30 seconds
    private static final int RADIUS = 20;
    private int activeDuration = 0;
    private boolean isActive = false;
    private Vec3 blizzardCenter = null;

    public BlizzardAbility() {
        super("blizzard", 120);
    }

    @Override
    public void onActivate(Player player, Level level) {
        isActive = true;
        activeDuration = DURATION;
        blizzardCenter = player.position();

        player.sendSystemMessage(Component.literal(ChatFormatting.AQUA + "You summon a blizzard!"));

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SNOW_GOLEM_HURT, SoundSource.PLAYERS, 2.0f, 0.5f);

        startCooldown();
    }

    @Override
    public void tick(Player player) {
        if (isActive && activeDuration > 0) {
            activeDuration--;
            Level level = player.level();

            if (blizzardCenter == null) {
                blizzardCenter = player.position();
            }

            // Intense snow particles - more frequent and denser
            if (level instanceof ServerLevel serverLevel) {
                // Main snowflake particles every tick
                for (int i = 0; i < 100; i++) {
                    double angle = Math.random() * Math.PI * 2;
                    double distance = Math.random() * RADIUS;
                    double x = blizzardCenter.x + Math.cos(angle) * distance;
                    double z = blizzardCenter.z + Math.sin(angle) * distance;
                    double y = blizzardCenter.y + Math.random() * 12;

                    serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                            x, y, z, 2, 0.2, 0.5, 0.2, 0.03);
                }

                // Add white particles for blizzard effect
                if (activeDuration % 2 == 0) {
                    for (int i = 0; i < 30; i++) {
                        double angle = Math.random() * Math.PI * 2;
                        double distance = Math.random() * RADIUS;
                        double x = blizzardCenter.x + Math.cos(angle) * distance;
                        double z = blizzardCenter.z + Math.sin(angle) * distance;
                        double y = blizzardCenter.y + Math.random() * 10;

                        serverLevel.sendParticles(ParticleTypes.WHITE_ASH,
                                x, y, z, 1, 0.3, 0.3, 0.3, 0.05);
                    }
                }
            }

            // Effects every second
            if (activeDuration % 20 == 0) {
                AABB area = new AABB(
                        blizzardCenter.x - RADIUS, blizzardCenter.y - 5, blizzardCenter.z - RADIUS,
                        blizzardCenter.x + RADIUS, blizzardCenter.y + 10, blizzardCenter.z + RADIUS);

                List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area);

                for (LivingEntity entity : entities) {
                    if (entity == player) {
                        // Player gets buffs
                        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 25, 1, false, false));
                        entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 25, 0, false, false));
                    } else {
                        // Enemies get slowed
                        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 25, 1, false, false));
                    }
                }

                // Freeze water and place snow layers in area
                for (int x = -RADIUS; x <= RADIUS; x += 2) {
                    for (int z = -RADIUS; z <= RADIUS; z += 2) {
                        BlockPos pos = BlockPos.containing(blizzardCenter).offset(x, 0, z);
                        BlockState state = level.getBlockState(pos);

                        // Freeze water
                        if (state.is(Blocks.WATER)) {
                            level.setBlock(pos, Blocks.ICE.defaultBlockState(), 3);
                        }

                        // Place snow layers on solid blocks
                        BlockPos abovePos = pos.above();
                        BlockState aboveState = level.getBlockState(abovePos);
                        BlockState belowState = level.getBlockState(pos);

                        // Check if we can place snow (air above solid block)
                        if (aboveState.isAir() && belowState.isSolidRender(level, pos)) {
                            // Random chance to place snow (30% per check)
                            if (Math.random() < 0.3) {
                                level.setBlock(abovePos, Blocks.SNOW.defaultBlockState(), 3);
                            }
                        }
                        // If there's already snow, try to increase layers
                        else if (aboveState.is(Blocks.SNOW)) {
                            int currentLayers = aboveState.getValue(SnowLayerBlock.LAYERS);
                            if (currentLayers < 8 && Math.random() < 0.2) {
                                level.setBlock(abovePos, aboveState.setValue(SnowLayerBlock.LAYERS, currentLayers + 1),
                                        3);
                            }
                        }
                    }
                }
            }

            // Warning when ending
            if (activeDuration == 5 * 20) {
                player.sendSystemMessage(Component.literal(ChatFormatting.YELLOW + "Blizzard ending in 5 seconds..."));
            }

            if (activeDuration == 0) {
                isActive = false;
                blizzardCenter = null;
                player.sendSystemMessage(Component.literal(ChatFormatting.AQUA + "Blizzard ended."));
            }
        }
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
