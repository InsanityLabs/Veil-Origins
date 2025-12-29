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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

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
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        
        // Only vampires can drink blood
        Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
        if (origin == null) {
            if (!level.isClientSide()) {
                player.displayClientMessage(
                        Component.literal("Only vampires can drink blood...").withStyle(ChatFormatting.GRAY), true);
            }
            return InteractionResult.FAIL;
        }
        
        String originPath = origin.getId().getPath();
        boolean isVampire = originPath.equals("vampire") || originPath.equals("vampling");
        
        if (!isVampire) {
            if (!level.isClientSide()) {
                player.displayClientMessage(
                        Component.literal("Only vampires can drink blood...").withStyle(ChatFormatting.GRAY), true);
            }
            return InteractionResult.FAIL;
        }
        
        // Can't drink empty bottles
        if (bloodAmount == 0) {
            if (!level.isClientSide()) {
                player.displayClientMessage(
                        Component.literal("This bottle is empty.").withStyle(ChatFormatting.GRAY), true);
            }
            return InteractionResult.FAIL;
        }
        
        // Check if blood bar is already full
        OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);
        if (data.getResourceBar() >= 100) {
            if (!level.isClientSide()) {
                player.displayClientMessage(
                        Component.literal("Your blood is already full.").withStyle(ChatFormatting.DARK_RED), true);
            }
            return InteractionResult.FAIL;
        }
        
        // Start drinking
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }
    
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
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
    
    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32; // Same as vanilla drinking
    }
    
    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.DRINK;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, 
                                Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);
        
        if (bloodAmount == 0) {
            tooltipAdder.accept(Component.literal("Empty - fill by draining blood").withStyle(ChatFormatting.GRAY));
        } else if (bloodAmount == 50) {
            tooltipAdder.accept(Component.literal("Half Full - restores 50 blood").withStyle(ChatFormatting.DARK_RED));
        } else if (bloodAmount == 100) {
            tooltipAdder.accept(Component.literal("Full - restores 100 blood").withStyle(ChatFormatting.RED));
        }
        
        tooltipAdder.accept(Component.literal("Only vampires can drink this").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
    }
}
