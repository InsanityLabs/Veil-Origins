package com.veilorigins.origins.cindersoul;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.Optional;

public class HeatAffinityPassive extends OriginPassive {
    private int tickCounter = 0;

    public HeatAffinityPassive() {
        super("heat_affinity");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;

        // Regenerate health when in fire/lava (1 HP per 2 seconds = 40 ticks)
        if (tickCounter % 40 == 0) {
            if (player.isInLava() || player.isOnFire()
                    || player.level().getBlockState(player.blockPosition()).is(Blocks.FIRE)) {
                if (player.getHealth() < player.getMaxHealth()) {
                    player.heal(1.0f);
                }
            }
        }

        // Food cooking - every 5 seconds (100 ticks) to not be too OP/laggy
        if (tickCounter % 100 == 0 && !player.level().isClientSide) {
            cookOneItem(player);
        }
    }

    private void cookOneItem(Player player) {
        Level level = player.level();
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!stack.isEmpty()) {
                Optional<net.minecraft.world.item.crafting.RecipeHolder<SmeltingRecipe>> recipe = level
                        .getRecipeManager()
                        .getRecipeFor(RecipeType.SMELTING, new SingleRecipeInput(stack), level);

                if (recipe.isPresent()) {
                    ItemStack result = recipe.get().value().getResultItem(level.registryAccess()).copy();
                    // Only cook if it's food? Spec says "Food automatically cooks", but logic
                    // "Smelts dropped items" in FlameBurst implies any item.
                    // "Food automatically cooks in inventory"
                    if (result.has(net.minecraft.core.component.DataComponents.FOOD)) {
                        stack.shrink(1);
                        if (stack.isEmpty()) {
                            player.getInventory().setItem(i, result);
                        } else {
                            if (!player.getInventory().add(result)) {
                                player.drop(result, false);
                            }
                        }
                        // Cook one item per cycle
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void onEquip(Player player) {
    }

    @Override
    public void onRemove(Player player) {
    }
}
