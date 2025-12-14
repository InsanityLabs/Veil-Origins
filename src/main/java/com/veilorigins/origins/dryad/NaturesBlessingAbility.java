package com.veilorigins.origins.dryad;

import com.veilorigins.api.OriginAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class NaturesBlessingAbility extends OriginAbility {
    private static final int RESOURCE_COST = 8;
    private static final double RADIUS = 10.0;
    private static final float HEAL_AMOUNT = 6.0f; // 3 hearts

    public NaturesBlessingAbility() {
        super("natures_blessing", 45); // 45 second cooldown
    }

    @Override
    public void onActivate(Player player, Level level) {
        BlockPos playerPos = player.blockPosition();

        // Heal self
        player.heal(HEAL_AMOUNT);
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 1)); // Regen II for 5 seconds
        player.addEffect(new MobEffectInstance(MobEffects.SATURATION, 40, 0)); // Restore some hunger

        // Find and heal nearby allies (non-hostile entities)
        AABB area = new AABB(playerPos).inflate(RADIUS);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area,
                entity -> entity != player && entity.isAlive() && !isHostile(entity));

        for (LivingEntity entity : entities) {
            entity.heal(HEAL_AMOUNT / 2); // Allies get half healing
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0)); // Regen I for 3 seconds

            // Healing particles on each ally
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HEART,
                        entity.getX(), entity.getY() + entity.getBbHeight() + 0.5, entity.getZ(),
                        5, 0.3, 0.3, 0.3, 0.1);
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        entity.getX(), entity.getY() + entity.getBbHeight() / 2, entity.getZ(),
                        10, 0.5, 0.5, 0.5, 0.1);
            }
        }

        // Grow plants and crops in area
        for (BlockPos pos : BlockPos.betweenClosed(
                playerPos.offset((int) -RADIUS, -2, (int) -RADIUS),
                playerPos.offset((int) RADIUS, 2, (int) RADIUS))) {

            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();

            // Grow crops
            if (block instanceof CropBlock cropBlock) {
                if (!cropBlock.isMaxAge(state)) {
                    // Grow by 2-3 stages
                    int growth = 2 + level.random.nextInt(2);
                    int newAge = Math.min(cropBlock.getMaxAge(),
                            state.getValue(CropBlock.AGE) + growth);
                    level.setBlock(pos, state.setValue(CropBlock.AGE, newAge), 3);

                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                                3, 0.2, 0.2, 0.2, 0.05);
                    }
                }
            }
            // Apply bonemeal effect to other growable blocks
            else if (block instanceof BonemealableBlock bonemealable && level instanceof ServerLevel serverLevel) {
                if (bonemealable.isValidBonemealTarget(level, pos, state) && Math.random() < 0.5) {
                    bonemealable.performBonemeal(serverLevel, level.random, pos, state);

                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            3, 0.2, 0.2, 0.2, 0.05);
                }
            }
            // Convert dirt to grass
            else if (state.is(Blocks.DIRT) && level.getBlockState(pos.above()).isAir() && Math.random() < 0.3) {
                level.setBlock(pos, Blocks.GRASS_BLOCK.defaultBlockState(), 3);
            }
            // Spawn flowers on grass
            else if (state.is(Blocks.GRASS_BLOCK) && level.getBlockState(pos.above()).isAir() && Math.random() < 0.1) {
                Block flower = getRandomFlower(level);
                level.setBlock(pos.above(), flower.defaultBlockState(), 3);
            }
        }

        // Visual effects
        if (level instanceof ServerLevel serverLevel) {
            // Expanding ring of nature particles
            for (double r = 1; r <= RADIUS; r += 0.5) {
                for (int i = 0; i < 16; i++) {
                    double angle = (i / 16.0) * Math.PI * 2;
                    double x = playerPos.getX() + 0.5 + Math.cos(angle) * r;
                    double z = playerPos.getZ() + 0.5 + Math.sin(angle) * r;

                    serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                            x, playerPos.getY() + 0.5, z, 1, 0.1, 0.1, 0.1, 0.01);
                }
            }

            // Central burst of leaves and petals
            serverLevel.sendParticles(ParticleTypes.CHERRY_LEAVES,
                    playerPos.getX() + 0.5, playerPos.getY() + 1.5, playerPos.getZ() + 0.5,
                    50, 2.0, 1.0, 2.0, 0.1);

            // Hearts around player
            serverLevel.sendParticles(ParticleTypes.HEART,
                    player.getX(), player.getY() + player.getBbHeight() + 0.5, player.getZ(),
                    8, 0.5, 0.3, 0.5, 0.1);
        }

        // Sound effects
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.5f, 1.5f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0f, 1.2f);

        startCooldown();
    }

    private boolean isHostile(LivingEntity entity) {
        // Check if entity is a monster/hostile
        return entity.getType().getCategory().getName().equals("monster");
    }

    private Block getRandomFlower(Level level) {
        Block[] flowers = {
                Blocks.DANDELION,
                Blocks.POPPY,
                Blocks.BLUE_ORCHID,
                Blocks.ALLIUM,
                Blocks.AZURE_BLUET,
                Blocks.OXEYE_DAISY,
                Blocks.CORNFLOWER,
                Blocks.LILY_OF_THE_VALLEY
        };
        return flowers[level.random.nextInt(flowers.length)];
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
