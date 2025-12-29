package com.veilorigins.item;

import com.veilorigins.api.Origin;
import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.data.OriginData;
import com.veilorigins.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Blood Bottle item for vampires.
 * Can be consumed to restore blood resource or filled by draining blood from mobs.
 */
public class BloodBottleItem extends Item {
    
    private final int bloodAmount; // 0 = empty, 50 = half, 100 = full
    
    public BloodBottleItem(Properties properties, int bloodAmount) {
        super(properties);
        this.bloodAmount = bloodAmount;
    }
    
    public int getBloodAmount() {
        return bloodAmount;
    }
    
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Only vampires can drink blood
        Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
        if (origin == null) {
            if (!level.isClientSide()) {
                player.displayClientMessage(
                        Component.literal("Only vampires can drink blood...").withStyle(ChatFormatting.GRAY), true);
            }
            return InteractionResultHolder.fail(stack);
        }
        
        String originPath = origin.getId().getPath();
        boolean isVampire = originPath.equals("vampire") || originPath.equals("vampling");
        
        if (!isVampire) {
            if (!level.isClientSide()) {
                player.displayClientMessage(
                        Component.literal("Only vampires can drink blood...").withStyle(ChatFormatting.GRAY), true);
            }
            return InteractionResultHolder.fail(stack);
        }
        
        // Can't drink empty bottles
        if (bloodAmount == 0) {
            if (!level.isClientSide()) {
                player.displayClientMessage(
                        Component.literal("This bottle is empty.").withStyle(ChatFormatting.GRAY), true);
            }
            return InteractionResultHolder.fail(stack);
        }
        
        // Check if blood bar is already full
        OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);
        if (data.getResourceBar() >= 100) {
            if (!level.isClientSide()) {
                player.displayClientMessage(
                        Component.literal("Your blood is already full.").withStyle(ChatFormatting.DARK_RED), true);
            }
            return InteractionResultHolder.fail(stack);
        }
        
        // Start drinking
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }
    
    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        if (!(entity instanceof Player player)) {
            return stack;
        }
        
        if (!level.isClientSide()) {
            // Add blood to the player's resource bar
            OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);
            float currentBlood = data.getResourceBar();
            float newBlood = Math.min(100, currentBlood + bloodAmount);
            data.setResourceBar(newBlood);
            
            // Play drinking sound
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0F, 1.0F);
            
            // Give regeneration effect based on amount consumed
            Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
            boolean isFullVampire = origin != null && origin.getId().getPath().equals("vampire");
            
            int regenDuration = bloodAmount == 100 ? 100 : 60; // 5 or 3 seconds
            int regenLevel = isFullVampire ? 1 : 0;
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, regenDuration, regenLevel, false, true));
            
            player.displayClientMessage(
                    Component.literal("The blood revitalizes you!").withStyle(ChatFormatting.DARK_RED), true);
        }
        
        // Consume the item and return empty bottle
        stack.shrink(1);
        
        ItemStack emptyBottle = new ItemStack(ModItems.BLOOD_BOTTLE_EMPTY.get());
        if (stack.isEmpty()) {
            return emptyBottle;
        } else {
            // Add empty bottle to inventory
            if (!player.getInventory().add(emptyBottle)) {
                player.drop(emptyBottle, false);
            }
            return stack;
        }
    }
    
    // Note: getUseDuration signature may differ in 1.21.1 - keeping method for functionality
    public int getUseDuration(@NotNull ItemStack stack) {
        return 32; // Same as vanilla drinking
    }
    
    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.DRINK;
    }
    
    @Override
    public void appendHoverText(@NotNull ItemStack stack, Item.@NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        
        if (bloodAmount == 0) {
            tooltip.add(Component.literal("Empty - fill by draining blood").withStyle(ChatFormatting.GRAY));
        } else if (bloodAmount == 50) {
            tooltip.add(Component.literal("Half Full - restores 50 blood").withStyle(ChatFormatting.DARK_RED));
        } else if (bloodAmount == 100) {
            tooltip.add(Component.literal("Full - restores 100 blood").withStyle(ChatFormatting.RED));
        }
        
        tooltip.add(Component.literal("Only vampires can drink this").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }
}
