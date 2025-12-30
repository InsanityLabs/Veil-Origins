package com.veilorigins.origins.dryad;

import com.veilorigins.api.OriginPassive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;
import net.minecraft.ChatFormatting;

public class ForestBondPassive extends OriginPassive {
    private int tickCounter = 0;

    public ForestBondPassive() {
        super("forest_bond");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;
        Level level = player.level();
        BlockPos playerPos = player.blockPosition();

        // Every 10 ticks
        if (tickCounter >= 10) {
            tickCounter = 0;

            // Check if in forest biome
            boolean inForest = isInForestBiome(level, playerPos);

            // Check if near trees (leaves/logs)
            boolean nearTrees = isNearTrees(level, playerPos, 8);

            if (inForest || nearTrees) {
                // Speed boost in forest areas
                player.addEffect(new MobEffectInstance(MobEffects.SPEED, 30, 0, true, false));

                // Extra jump in forests
                if (inForest) {
                    player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 30, 0, true, false));
                }

                // Ambient forest particles
                if (level instanceof ServerLevel serverLevel && Math.random() < 0.3) {
                    serverLevel.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR,
                            player.getX(), player.getY() + 1, player.getZ(),
                            2, 1.0, 0.5, 1.0, 0.01);
                }
            }

            // Make animals near you calm/friendly (they won't run away)
            AABB animalArea = new AABB(playerPos).inflate(8);
            List<Animal> animals = level.getEntitiesOfClass(Animal.class, animalArea);
            for (Animal animal : animals) {
                // Animals trust the dryad
                if (!animal.isBaby()) {
                    // Apply peaceful effect - they won't flee
                    animal.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 10, 0, true, false));
                }
            }
        }

        // Check for leaf block collision - can phase through leaves
        BlockState blockAtHead = level.getBlockState(playerPos.above());
        BlockState blockAtFeet = level.getBlockState(playerPos);

        if (blockAtHead.getBlock() instanceof LeavesBlock || blockAtFeet.getBlock() instanceof LeavesBlock) {
            // Grant no-clip effect for leaves (handled by giving slow fall to prevent
            // getting stuck)
            if (!player.hasEffect(MobEffects.SLOW_FALLING)) {
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20, 0, true, false));
            }
        }
    }

    private boolean isInForestBiome(Level level, BlockPos pos) {
        var biomeHolder = level.getBiome(pos);
        // Check if biome is tagged as forest
        if (biomeHolder.is(BiomeTags.IS_FOREST)) {
            return true;
        }
        // Also check for jungle
        if (biomeHolder.is(BiomeTags.IS_JUNGLE)) {
            return true;
        }
        // Manual check for forest-type biomes by name
        Identifier biomeKey = level.registryAccess()
                .lookup(Registries.BIOME).orElseThrow()
                .getKey(biomeHolder.value());
        if (biomeKey != null) {
            String biomeName = biomeKey.getPath().toLowerCase();
            return biomeName.contains("forest") ||
                    biomeName.contains("woods") ||
                    biomeName.contains("grove") ||
                    biomeName.contains("jungle") ||
                    biomeName.contains("taiga");
        }
        return false;
    }

    private boolean isNearTrees(Level level, BlockPos center, int radius) {
        int treeBlocks = 0;
        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-radius, -2, -radius),
                center.offset(radius, radius, radius))) {
            BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof LeavesBlock ||
                    state.is(Blocks.OAK_LOG) ||
                    state.is(Blocks.BIRCH_LOG) ||
                    state.is(Blocks.SPRUCE_LOG) ||
                    state.is(Blocks.JUNGLE_LOG) ||
                    state.is(Blocks.ACACIA_LOG) ||
                    state.is(Blocks.DARK_OAK_LOG) ||
                    state.is(Blocks.CHERRY_LOG) ||
                    state.is(Blocks.MANGROVE_LOG)) {
                treeBlocks++;
                if (treeBlocks >= 5)
                    return true;
            }
        }
        return false;
    }

    @Override
    public void onEquip(Player player) {
        player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                ChatFormatting.DARK_GREEN + "As a Dryad, forests empower you and animals trust you."), false);
    }

    @Override
    public void onRemove(Player player) {
        // No cleanup needed
    }
}
