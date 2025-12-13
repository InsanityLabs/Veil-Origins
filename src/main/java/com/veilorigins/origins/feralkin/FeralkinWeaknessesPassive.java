package com.veilorigins.origins.feralkin;

import com.veilorigins.api.OriginPassive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class FeralkinWeaknessesPassive extends OriginPassive {
    private int tickCounter = 0;
    private int civilizedSeconds = 0; // Track seconds in civilized area
    private static final int CIVILIZED_GRACE_SECONDS = 60; // 60 seconds grace period
    private boolean hasWarned = false;

    public FeralkinWeaknessesPassive() {
        super("feralkin_weaknesses");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;

        // Check every 20 ticks (1 second)
        if (tickCounter >= 20) {
            tickCounter = 0;
            Level level = player.level();

            // Check for heavy armor (iron/diamond/netherite)
            checkHeavyArmor(player);

            // Villages/civilized areas - with grace period
            boolean inCivilized = isInCivilizedArea(player, level);

            if (inCivilized) {
                civilizedSeconds++;

                // Warning at 45 seconds (15 seconds remaining)
                if (civilizedSeconds == 45 && !hasWarned) {
                    player.sendSystemMessage(Component.literal(
                            "§eYou're starting to feel uncomfortable in this civilized area... (15s remaining)"));
                    hasWarned = true;
                }

                // Apply effects after grace period
                if (civilizedSeconds >= CIVILIZED_GRACE_SECONDS) {
                    player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0, false, false));
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0, false, false));

                    // Notify every 10 seconds
                    if ((civilizedSeconds - CIVILIZED_GRACE_SECONDS) % 10 == 0) {
                        player.sendSystemMessage(
                                Component.literal("§cToo many artificial smells! Leave this civilized area!"));
                    }
                }
            } else {
                // Reset timer when leaving civilized area
                if (civilizedSeconds > 0) {
                    civilizedSeconds = 0;
                    hasWarned = false;
                    player.sendSystemMessage(Component.literal("§aYou feel more comfortable in the wilderness."));
                }
            }
        }
    }

    private void checkHeavyArmor(Player player) {
        for (ItemStack armor : player.getArmorSlots()) {
            if (armor.getItem() instanceof ArmorItem armorItem) {
                if (armorItem.getMaterial().value() == ArmorMaterials.IRON.value() ||
                        armorItem.getMaterial().value() == ArmorMaterials.DIAMOND.value() ||
                        armorItem.getMaterial().value() == ArmorMaterials.NETHERITE.value()) {
                    // Remove heavy armor
                    player.getInventory().removeItem(armor);
                    player.drop(armor, false);
                    player.sendSystemMessage(Component.literal("§cHeavy armor feels too restrictive!"));
                }
            }
        }
    }

    private boolean isInCivilizedArea(Player player, Level level) {
        BlockPos pos = player.blockPosition();
        int civilizedScore = 0;

        // Check for village-like structures in a larger radius
        for (int x = -12; x <= 12; x += 2) {
            for (int y = -6; y <= 6; y += 2) {
                for (int z = -12; z <= 12; z += 2) {
                    BlockPos checkPos = pos.offset(x, y, z);
                    BlockState state = level.getBlockState(checkPos);
                    Block block = state.getBlock();

                    // Check for civilization indicators
                    if (isCivilizedBlock(block)) {
                        civilizedScore++;
                    }
                }
            }
        }

        // If 3+ civilized blocks nearby, consider it civilized
        return civilizedScore >= 3;
    }

    private boolean isCivilizedBlock(Block block) {
        // Check specific blocks
        if (block == Blocks.CRAFTING_TABLE ||
                block == Blocks.FURNACE ||
                block == Blocks.BLAST_FURNACE ||
                block == Blocks.SMOKER ||
                block == Blocks.CHEST ||
                block == Blocks.TRAPPED_CHEST ||
                block == Blocks.BARREL ||
                block == Blocks.BELL ||
                block == Blocks.ANVIL ||
                block == Blocks.CHIPPED_ANVIL ||
                block == Blocks.DAMAGED_ANVIL ||
                block == Blocks.LECTERN ||
                block == Blocks.CARTOGRAPHY_TABLE ||
                block == Blocks.FLETCHING_TABLE ||
                block == Blocks.GRINDSTONE ||
                block == Blocks.LOOM ||
                block == Blocks.SMITHING_TABLE ||
                block == Blocks.STONECUTTER ||
                block == Blocks.COMPOSTER ||
                block == Blocks.BREWING_STAND ||
                block == Blocks.CAULDRON ||
                block == Blocks.ENCHANTING_TABLE ||
                block == Blocks.BOOKSHELF ||
                block == Blocks.LANTERN ||
                block == Blocks.SOUL_LANTERN) {
            return true;
        }

        // Check for beds (any color)
        String blockName = BuiltInRegistries.BLOCK.getKey(block).getPath();
        if (blockName.contains("bed") || blockName.contains("door") && !blockName.contains("iron")) {
            return true;
        }

        return false;
    }

    @Override
    public void onEquip(Player player) {
        player.sendSystemMessage(Component
                .literal("§7As Feralkin, you cannot wear heavy armor and feel uncomfortable in civilized areas."));
    }

    @Override
    public void onRemove(Player player) {
        player.removeEffect(MobEffects.CONFUSION);
        player.removeEffect(MobEffects.WEAKNESS);
        civilizedSeconds = 0;
        hasWarned = false;
    }
}
