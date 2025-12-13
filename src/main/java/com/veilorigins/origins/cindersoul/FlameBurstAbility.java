package com.veilorigins.origins.cindersoul;

import com.veilorigins.api.OriginAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Optional;

public class FlameBurstAbility extends OriginAbility {
    private static final int RADIUS = 8;
    private static final int DAMAGE = 4;
    private static final int HUNGER_COST = 4;

    public FlameBurstAbility() {
        super("flame_burst", 15);
    }

    @Override
    public void onActivate(Player player, Level level) {
        BlockPos center = player.blockPosition();
        int creepersDetonated = 0;

        // Visuals and Sound
        level.playSound(null, center.getX(), center.getY(), center.getZ(), SoundEvents.FIRECHARGE_USE,
                SoundSource.PLAYERS, 1.0f, 0.5f);
        level.playSound(null, center.getX(), center.getY(), center.getZ(), SoundEvents.GENERIC_EXPLODE.value(),
                SoundSource.PLAYERS, 0.5f, 1.2f);

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FLAME,
                    player.getX(), player.getY() + 1, player.getZ(),
                    100, 3.0, 1.0, 3.0, 0.2);
            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                    player.getX(), player.getY() + 1, player.getZ(),
                    50, 2.0, 2.0, 2.0, 0.1);
        }

        // Damage Entities and DETONATE CREEPERS
        List<Entity> entities = level.getEntities(player,
                new AABB(center).inflate(RADIUS));

        for (Entity entity : entities) {
            if (entity instanceof Creeper creeper) {
                // DETONATE THE CREEPER! >:D
                creeper.ignite();
                creepersDetonated++;

                // Extra fire particles around creeper
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.FLAME,
                            creeper.getX(), creeper.getY() + 1, creeper.getZ(),
                            30, 0.5, 0.5, 0.5, 0.15);
                }
            } else if (entity instanceof LivingEntity living && living != player) {
                living.hurt(level.damageSources().inFire(), DAMAGE);
                living.setRemainingFireTicks(100); // 5 seconds of fire
            } else if (entity instanceof ItemEntity itemEntity) {
                // Smelt items
                smeltItem(itemEntity, level);
            }
        }

        // Notify player if creepers were detonated
        if (creepersDetonated > 0) {
            player.sendSystemMessage(Component.literal("§c§l" + creepersDetonated + " Creeper" +
                    (creepersDetonated > 1 ? "s" : "") + " ignited! §r§7Run!"));
        }

        // Environmental Effects (Blocks)
        if (!level.isClientSide) {
            for (BlockPos pos : BlockPos.betweenClosed(center.offset(-RADIUS, -2, -RADIUS),
                    center.offset(RADIUS, 2, RADIUS))) {
                if (pos.distSqr(center) <= RADIUS * RADIUS) {
                    BlockState state = level.getBlockState(pos);

                    // Melt Ice/Snow
                    if (state.is(Blocks.ICE) || state.is(Blocks.SNOW) || state.is(Blocks.SNOW_BLOCK)
                            || state.is(Blocks.POWDER_SNOW)) {
                        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                        if (Math.random() < 0.3) {
                            level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.FIRE_EXTINGUISH,
                                    SoundSource.BLOCKS, 0.5f, 1.0f);
                        }
                    }
                    // Light fire on top of blocks occasionally
                    else if (level.isEmptyBlock(pos) && !level.isEmptyBlock(pos.below()) && Math.random() < 0.05) {
                        if (Blocks.FIRE.defaultBlockState().canSurvive(level, pos)) {
                            level.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
                        }
                    }
                }
            }
        }

        player.causeFoodExhaustion(HUNGER_COST);
        startCooldown();
    }

    private void smeltItem(ItemEntity itemEntity, Level level) {
        ItemStack stack = itemEntity.getItem();

        // Simple smelting logic - look for smelting recipe
        Optional<net.minecraft.world.item.crafting.RecipeHolder<SmeltingRecipe>> recipe = level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), level);

        if (recipe.isPresent()) {
            ItemStack result = recipe.get().value().getResultItem(level.registryAccess()).copy();
            result.setCount(stack.getCount());
            itemEntity.setItem(result);

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                        itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(),
                        5, 0.2, 0.2, 0.2, 0.1);
            }
        }
    }

    @Override
    public boolean canUse(Player player) {
        return !isOnCooldown();
    }

    @Override
    public int getResourceCost() {
        return HUNGER_COST;
    }
}
