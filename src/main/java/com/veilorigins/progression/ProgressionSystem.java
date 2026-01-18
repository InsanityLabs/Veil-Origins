package com.veilorigins.progression;

import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.data.OriginData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.ChatFormatting;

/**
 * Handles origin progression: XP, leveling, prestige, and stat bonuses.
 */
public class ProgressionSystem {
    
    public static final int MAX_LEVEL = 50;
    public static final int MAX_PRESTIGE = 10;
    public static final float PRESTIGE_STAT_BONUS = 0.05f; // +5% per prestige
    
    // XP required for each level (exponential curve)
    public static int getXPForLevel(int level) {
        if (level <= 1) return 0;
        // Base: 100 XP for level 2, scaling up
        return (int) (100 * Math.pow(1.15, level - 1));
    }
    
    // Total XP needed to reach a level from level 1
    public static int getTotalXPForLevel(int level) {
        int total = 0;
        for (int i = 2; i <= level; i++) {
            total += getXPForLevel(i);
        }
        return total;
    }
    
    /**
     * Award XP to a player for their current origin.
     */
    public static void awardXP(Player player, int amount, String reason) {
        if (player.level().isClientSide()) return;
        
        OriginData.PlayerOriginData data = OriginData.get(player);
        if (data.getOriginId() == null) return;
        
        int currentLevel = data.getOriginLevel();
        if (currentLevel >= MAX_LEVEL) return; // Can't gain XP at max level
        
        // Apply prestige bonus
        float prestigeMultiplier = 1.0f + (data.getPrestigeLevel() * 0.1f);
        int adjustedAmount = (int) (amount * prestigeMultiplier);
        
        data.addXP(adjustedAmount);
        
        // Check for level up
        checkLevelUp(player, data);
        
        VeilOrigins.LOGGER.debug("Player {} gained {} XP ({}) - Total: {}", 
            player.getName().getString(), adjustedAmount, reason, data.getOriginXP());
    }
    
    /**
     * Check if player should level up and handle it.
     */
    private static void checkLevelUp(Player player, OriginData.PlayerOriginData data) {
        int currentLevel = data.getOriginLevel();
        int currentXP = data.getOriginXP();
        
        while (currentLevel < MAX_LEVEL) {
            int xpNeeded = getTotalXPForLevel(currentLevel + 1);
            if (currentXP >= xpNeeded) {
                currentLevel++;
                data.setOriginLevel(currentLevel);
                onLevelUp(player, currentLevel);
            } else {
                break;
            }
        }
    }

    
    /**
     * Called when a player levels up.
     */
    private static void onLevelUp(Player player, int newLevel) {
        Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
        if (origin == null) return;
        
        // Award skill point
        OriginData.PlayerOriginData data = OriginData.get(player);
        data.addSkillPoints(1);
        
        // Play level up sound
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 1.0f);
        
        // Send level up message
        Component message = Component.literal("")
            .append(Component.literal("★ LEVEL UP! ★").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD))
            .append(Component.literal(" " + origin.getDisplayName() + " Level " + newLevel).withStyle(ChatFormatting.YELLOW))
            .append(Component.literal(" (+1 Skill Point)").withStyle(ChatFormatting.AQUA));
        player.displayClientMessage(message, false);
        
        // Milestone rewards
        if (newLevel == 10 || newLevel == 25 || newLevel == 50) {
            onMilestoneReached(player, newLevel, origin);
        }
        
        // Apply level-based stat bonuses
        applyLevelBonuses(player, newLevel);
        
        VeilOrigins.LOGGER.info("Player {} reached {} level {}", 
            player.getName().getString(), origin.getDisplayName(), newLevel);
    }
    
    /**
     * Handle milestone level rewards.
     */
    private static void onMilestoneReached(Player player, int level, Origin origin) {
        Component milestone;
        
        switch (level) {
            case 10:
                milestone = Component.literal("✦ Milestone: Cooldowns reduced by 10%!").withStyle(ChatFormatting.AQUA);
                break;
            case 25:
                milestone = Component.literal("✦ Milestone: Ability power increased by 15%!").withStyle(ChatFormatting.LIGHT_PURPLE);
                break;
            case 50:
                milestone = Component.literal("✦ MASTER MILESTONE: Legendary Ability Unlocked! (R + Attack)").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
                player.displayClientMessage(Component.literal("✦ You can now PRESTIGE or activate HYBRID MODE!").withStyle(ChatFormatting.DARK_PURPLE), false);
                break;
            default:
                return;
        }
        
        player.displayClientMessage(milestone, false);
        
        // Extra sound for milestones
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.0f, 1.0f);
    }
    
    /**
     * Apply stat bonuses based on level.
     */
    private static void applyLevelBonuses(Player player, int level) {
        // Bonuses are calculated dynamically when needed
        // This method can trigger visual effects or notifications
    }
    
    /**
     * Get cooldown reduction multiplier based on level.
     * Level 10: 10% reduction, Level 25: 20%, Level 50: 30%
     */
    public static float getCooldownMultiplier(Player player) {
        OriginData.PlayerOriginData data = OriginData.get(player);
        int level = data.getOriginLevel();
        int prestige = data.getPrestigeLevel();
        
        float reduction = 0f;
        if (level >= 10) reduction += 0.10f;
        if (level >= 25) reduction += 0.10f;
        if (level >= 50) reduction += 0.10f;
        
        // Prestige bonus: +2% per prestige level
        reduction += prestige * 0.02f;
        
        return 1.0f - reduction;
    }
    
    /**
     * Get damage multiplier based on level.
     */
    public static float getDamageMultiplier(Player player) {
        OriginData.PlayerOriginData data = OriginData.get(player);
        int level = data.getOriginLevel();
        int prestige = data.getPrestigeLevel();
        
        float bonus = 1.0f;
        if (level >= 25) bonus += 0.15f;
        if (level >= 50) bonus += 0.15f;
        
        // Prestige bonus: +5% per prestige level
        bonus += prestige * PRESTIGE_STAT_BONUS;
        
        return bonus;
    }
    
    /**
     * Prestige the player's origin - reset to level 1 with permanent bonuses.
     */
    public static boolean prestige(Player player) {
        OriginData.PlayerOriginData data = OriginData.get(player);
        
        if (data.getOriginLevel() < MAX_LEVEL) {
            player.displayClientMessage(
                Component.literal("You must be level 50 to prestige!").withStyle(ChatFormatting.RED), 
                true);
            return false;
        }
        
        if (data.getPrestigeLevel() >= MAX_PRESTIGE) {
            player.displayClientMessage(
                Component.literal("You have reached maximum prestige!").withStyle(ChatFormatting.GOLD), 
                true);
            return false;
        }
        
        // Perform prestige
        int newPrestige = data.getPrestigeLevel() + 1;
        data.setPrestigeLevel(newPrestige);
        data.setOriginLevel(1);
        data.setOriginXP(0);
        
        // Announce prestige
        Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
        String originName = origin != null ? origin.getDisplayName() : "Unknown";
        
        Component message = Component.literal("")
            .append(Component.literal("★★★ PRESTIGE " + newPrestige + "! ★★★").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD))
            .append(Component.literal("\n" + originName + " gains +5% to all stats permanently!").withStyle(ChatFormatting.LIGHT_PURPLE));
        
        player.displayClientMessage(message, false);
        
        // Play prestige sound
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0f, 1.0f);
        
        VeilOrigins.LOGGER.info("Player {} prestiged to level {} for {}", 
            player.getName().getString(), newPrestige, originName);
        
        return true;
    }
    
    /**
     * Check if player can use legendary ability.
     */
    public static boolean canUseLegendaryAbility(Player player) {
        OriginData.PlayerOriginData data = OriginData.get(player);
        return data.getOriginLevel() >= MAX_LEVEL;
    }
    
    /**
     * Check if player can activate hybrid mode.
     */
    public static boolean canActivateHybrid(Player player) {
        OriginData.PlayerOriginData data = OriginData.get(player);
        return data.getOriginLevel() >= MAX_LEVEL;
    }
    
    /**
     * Get XP progress to next level as percentage (0-1).
     */
    public static float getLevelProgress(Player player) {
        OriginData.PlayerOriginData data = OriginData.get(player);
        int level = data.getOriginLevel();
        
        if (level >= MAX_LEVEL) return 1.0f;
        
        int currentXP = data.getOriginXP();
        int xpForCurrentLevel = getTotalXPForLevel(level);
        int xpForNextLevel = getTotalXPForLevel(level + 1);
        int xpNeeded = xpForNextLevel - xpForCurrentLevel;
        int xpProgress = currentXP - xpForCurrentLevel;
        
        return Math.max(0, Math.min(1, (float) xpProgress / xpNeeded));
    }
}
