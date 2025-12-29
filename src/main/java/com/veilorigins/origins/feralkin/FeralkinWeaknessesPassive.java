package com.veilorigins.origins.feralkin;

import com.veilorigins.api.OriginPassive;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.ChatFormatting;

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
                    player.displayClientMessage(Component.literal(
                            ChatFormatting.YELLOW
                                    + "You're starting to feel uncomfortable in this civilized area... (15s remaining)"), false);
                    hasWarned = true;
                }

                // Apply effects after grace period
                if (civilizedSeconds >= CIVILIZED_GRACE_SECONDS) {
                    player.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 60, 0, false, false));
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0, false, false));

                    // Notify every 10 seconds
                    if ((civilizedSeconds - CIVILIZED_GRACE_SECONDS) % 10 == 0) {
                        player.displayClientMessage(
                                Component.literal(
                                        ChatFormatting.RED + "Too many artificial smells! Leave this civilized area!"), false);
                    }
                }
            } else {
                // Reset timer when leaving civilized area
                if (civilizedSeconds > 0) {
                    civilizedSeconds = 0;
                    hasWarned = false;
                    player.displayClientMessage(
                            Component.literal(ChatFormatting.GREEN + "You feel more comfortable in the wilderness."), false);
                }
            }
        }
    }

    private void checkHeavyArmor(Player player) {
        // Check all armor slots using EquipmentSlot
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            ItemStack armor = player.getItemBySlot(slot);
            if (!armor.isEmpty() && isHeavyArmor(armor)) {
                // Remove heavy armor
                player.setItemSlot(slot, ItemStack.EMPTY);
                player.drop(armor, false);
                player.displayClientMessage(
                        Component.literal(ChatFormatting.RED + "Heavy armor feels too restrictive!"), false);
            }
        }
    }

    private boolean isHeavyArmor(ItemStack stack) {
        // Check if the item is iron, diamond, or netherite armor by checking the item itself
        return stack.is(Items.IRON_HELMET) || stack.is(Items.IRON_CHESTPLATE) || 
               stack.is(Items.IRON_LEGGINGS) || stack.is(Items.IRON_BOOTS) ||
               stack.is(Items.DIAMOND_HELMET) || stack.is(Items.DIAMOND_CHESTPLATE) || 
               stack.is(Items.DIAMOND_LEGGINGS) || stack.is(Items.DIAMOND_BOOTS) ||
               stack.is(Items.NETHERITE_HELMET) || stack.is(Items.NETHERITE_CHESTPLATE) || 
               stack.is(Items.NETHERITE_LEGGINGS) || stack.is(Items.NETHERITE_BOOTS);
    }

    private boolean isInCivilizedArea(Player player, Level level) {
        BlockPos pos = player.blockPosition();
        int civilizedScore = 0;

        // Check for village-like structures in a larger radius
        int radius = 15;
        for (int x = -radius; x <= radius; x += 3) {
            for (int y = -3; y <= 5; y += 2) {
                for (int z = -radius; z <= radius; z += 3) {
                    BlockPos checkPos = pos.offset(x, y, z);
                    BlockState state = level.getBlockState(checkPos);
                    Block block = state.getBlock();

                    // Check for civilized blocks
                    if (block == Blocks.CRAFTING_TABLE ||
                            block == Blocks.FURNACE ||
                            block == Blocks.BLAST_FURNACE ||
                            block == Blocks.SMOKER ||
                            block == Blocks.BREWING_STAND ||
                            block == Blocks.ENCHANTING_TABLE ||
                            block == Blocks.ANVIL ||
                            block == Blocks.CHIPPED_ANVIL ||
                            block == Blocks.DAMAGED_ANVIL ||
                            block == Blocks.GRINDSTONE ||
                            block == Blocks.SMITHING_TABLE ||
                            block == Blocks.LOOM ||
                            block == Blocks.CARTOGRAPHY_TABLE ||
                            block == Blocks.FLETCHING_TABLE ||
                            block == Blocks.STONECUTTER ||
                            block == Blocks.BELL ||
                            block == Blocks.LECTERN ||
                            block == Blocks.COMPOSTER ||
                            block == Blocks.BARREL ||
                            block == Blocks.CHEST) {
                        civilizedScore += 2;
                    }

                    // Beds and doors
                    if (state.is(net.minecraft.tags.BlockTags.BEDS) ||
                            state.is(net.minecraft.tags.BlockTags.DOORS)) {
                        civilizedScore += 1;
                    }
                }
            }
        }

        // Need multiple civilized indicators to count as civilized
        return civilizedScore >= 8;
    }

    public String getDescription() {
        return "Cannot wear heavy armor (iron/diamond/netherite). Becomes uncomfortable in villages and civilized areas after 60 seconds.";
    }
}
