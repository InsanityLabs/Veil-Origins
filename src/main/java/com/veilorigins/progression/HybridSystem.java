package com.veilorigins.progression;

import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.OriginAbility;
import com.veilorigins.api.OriginPassive;
import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.data.OriginData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

import java.util.*;

/**
 * Handles the Hybrid system - combining two origins at level 50 for 300 seconds.
 */
public class HybridSystem {
    
    public static final int HYBRID_DURATION_SECONDS = 300; // 5 minutes
    public static final int HYBRID_COOLDOWN_SECONDS = 3600; // 1 hour cooldown
    
    // Track active hybrids: player UUID -> HybridState
    private static final Map<UUID, HybridState> activeHybrids = new HashMap<>();
    // Track cooldowns: player UUID -> remaining cooldown ticks
    private static final Map<UUID, Integer> hybridCooldowns = new HashMap<>();
    
    /**
     * Represents an active hybrid state.
     */
    public static class HybridState {
        private final Origin primaryOrigin;
        private final Origin secondaryOrigin;
        private int remainingTicks;
        private final List<OriginAbility> combinedAbilities;
        private final List<OriginPassive> combinedPassives;
        
        public HybridState(Origin primary, Origin secondary) {
            this.primaryOrigin = primary;
            this.secondaryOrigin = secondary;
            this.remainingTicks = HYBRID_DURATION_SECONDS * 20;
            this.combinedAbilities = new ArrayList<>();
            this.combinedPassives = new ArrayList<>();
            
            // Take first ability from each origin
            if (!primary.getAbilities().isEmpty()) {
                combinedAbilities.add(primary.getAbilities().get(0));
            }
            if (!secondary.getAbilities().isEmpty()) {
                combinedAbilities.add(secondary.getAbilities().get(0));
            }
            
            // Take first passive from each origin
            if (!primary.getPassives().isEmpty()) {
                combinedPassives.add(primary.getPassives().get(0));
            }
            if (!secondary.getPassives().isEmpty()) {
                combinedPassives.add(secondary.getPassives().get(0));
            }
        }
        
        public Origin getPrimaryOrigin() { return primaryOrigin; }
        public Origin getSecondaryOrigin() { return secondaryOrigin; }
        public int getRemainingTicks() { return remainingTicks; }
        public int getRemainingSeconds() { return remainingTicks / 20; }
        public List<OriginAbility> getCombinedAbilities() { return combinedAbilities; }
        public List<OriginPassive> getCombinedPassives() { return combinedPassives; }
        
        public void tick() {
            if (remainingTicks > 0) {
                remainingTicks--;
            }
        }
        
        public boolean isExpired() {
            return remainingTicks <= 0;
        }
        
        public String getDisplayName() {
            return primaryOrigin.getDisplayName() + " + " + secondaryOrigin.getDisplayName();
        }
    }
    
    /**
     * Check if a player can activate hybrid mode.
     */
    public static boolean canActivateHybrid(Player player) {
        UUID uuid = player.getUUID();
        
        // Check if already in hybrid mode
        if (activeHybrids.containsKey(uuid)) {
            return false;
        }
        
        // Check cooldown
        if (hybridCooldowns.containsKey(uuid) && hybridCooldowns.get(uuid) > 0) {
            return false;
        }
        
        // Check level requirement
        return ProgressionSystem.canActivateHybrid(player);
    }
    
    /**
     * Get remaining cooldown in seconds.
     */
    public static int getRemainingCooldown(Player player) {
        Integer cooldown = hybridCooldowns.get(player.getUUID());
        return cooldown != null ? cooldown / 20 : 0;
    }
    
    /**
     * Activate hybrid mode for a player.
     */
    public static boolean activateHybrid(Player player, Origin secondaryOrigin) {
        if (!canActivateHybrid(player)) {
            int cooldown = getRemainingCooldown(player);
            if (cooldown > 0) {
                player.displayClientMessage(
                    Component.literal("Hybrid mode on cooldown: " + formatTime(cooldown)).withStyle(ChatFormatting.RED),
                    true);
            }
            return false;
        }
        
        Origin primaryOrigin = VeilOriginsAPI.getPlayerOrigin(player);
        if (primaryOrigin == null) {
            player.displayClientMessage(
                Component.literal("You must have an origin to use hybrid mode!").withStyle(ChatFormatting.RED),
                true);
            return false;
        }
        
        if (primaryOrigin.getId().equals(secondaryOrigin.getId())) {
            player.displayClientMessage(
                Component.literal("Cannot hybrid with the same origin!").withStyle(ChatFormatting.RED),
                true);
            return false;
        }
        
        // Create hybrid state
        HybridState state = new HybridState(primaryOrigin, secondaryOrigin);
        activeHybrids.put(player.getUUID(), state);
        
        // Apply secondary origin's first passive
        for (OriginPassive passive : state.getCombinedPassives()) {
            if (passive != primaryOrigin.getPassives().get(0)) {
                passive.onEquip(player);
            }
        }
        
        // Announce hybrid activation
        Component message = Component.literal("")
            .append(Component.literal("⚡ HYBRID MODE ACTIVATED! ⚡").withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD))
            .append(Component.literal("\n" + state.getDisplayName()).withStyle(ChatFormatting.LIGHT_PURPLE))
            .append(Component.literal("\nDuration: " + HYBRID_DURATION_SECONDS + " seconds").withStyle(ChatFormatting.GRAY));
        
        player.displayClientMessage(message, false);
        
        // Play activation sound
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.2f);
        
        VeilOrigins.LOGGER.info("Player {} activated hybrid mode: {} + {}", 
            player.getName().getString(), primaryOrigin.getDisplayName(), secondaryOrigin.getDisplayName());
        
        return true;
    }

    
    /**
     * Deactivate hybrid mode for a player.
     */
    public static void deactivateHybrid(Player player) {
        UUID uuid = player.getUUID();
        HybridState state = activeHybrids.remove(uuid);
        
        if (state != null) {
            // Remove secondary origin's passives
            Origin primary = state.getPrimaryOrigin();
            for (OriginPassive passive : state.getCombinedPassives()) {
                if (!primary.getPassives().contains(passive)) {
                    passive.onRemove(player);
                }
            }
            
            // Start cooldown
            hybridCooldowns.put(uuid, HYBRID_COOLDOWN_SECONDS * 20);
            
            // Announce deactivation
            player.displayClientMessage(
                Component.literal("Hybrid mode ended. Cooldown: " + formatTime(HYBRID_COOLDOWN_SECONDS))
                    .withStyle(ChatFormatting.GRAY),
                false);
            
            // Play deactivation sound
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BEACON_DEACTIVATE, SoundSource.PLAYERS, 1.0f, 1.0f);
            
            VeilOrigins.LOGGER.info("Player {} hybrid mode ended", player.getName().getString());
        }
    }
    
    /**
     * Check if a player is in hybrid mode.
     */
    public static boolean isInHybridMode(Player player) {
        return activeHybrids.containsKey(player.getUUID());
    }
    
    /**
     * Get the current hybrid state for a player.
     */
    public static HybridState getHybridState(Player player) {
        return activeHybrids.get(player.getUUID());
    }
    
    /**
     * Tick all active hybrids and cooldowns.
     */
    public static void tick() {
        // Tick active hybrids
        Iterator<Map.Entry<UUID, HybridState>> hybridIterator = activeHybrids.entrySet().iterator();
        while (hybridIterator.hasNext()) {
            Map.Entry<UUID, HybridState> entry = hybridIterator.next();
            HybridState state = entry.getValue();
            state.tick();
            
            // Warning messages at certain times
            int remaining = state.getRemainingSeconds();
            if (remaining == 60 || remaining == 30 || remaining == 10 || remaining == 5) {
                // Note: Would need player reference to send message
                // This is handled in the event handler
            }
        }
        
        // Tick cooldowns
        Iterator<Map.Entry<UUID, Integer>> cooldownIterator = hybridCooldowns.entrySet().iterator();
        while (cooldownIterator.hasNext()) {
            Map.Entry<UUID, Integer> entry = cooldownIterator.next();
            int newCooldown = entry.getValue() - 1;
            if (newCooldown <= 0) {
                cooldownIterator.remove();
            } else {
                entry.setValue(newCooldown);
            }
        }
    }
    
    /**
     * Tick hybrid for a specific player (called from event handler).
     */
    public static void tickPlayer(Player player) {
        UUID uuid = player.getUUID();
        HybridState state = activeHybrids.get(uuid);
        
        if (state != null) {
            // Check for expiration
            if (state.isExpired()) {
                deactivateHybrid(player);
                return;
            }
            
            // Warning messages
            int remaining = state.getRemainingSeconds();
            if (state.getRemainingTicks() % 20 == 0) { // Once per second
                if (remaining == 60 || remaining == 30 || remaining == 10) {
                    player.displayClientMessage(
                        Component.literal("⚠ Hybrid mode ending in " + remaining + " seconds!")
                            .withStyle(ChatFormatting.YELLOW),
                        true);
                } else if (remaining == 5) {
                    player.displayClientMessage(
                        Component.literal("⚠ Hybrid mode ending in 5 seconds!")
                            .withStyle(ChatFormatting.RED, ChatFormatting.BOLD),
                        true);
                }
            }
            
            // Tick combined passives
            for (OriginPassive passive : state.getCombinedPassives()) {
                passive.onTick(player);
            }
        }
    }
    
    /**
     * Get abilities available to a player (considering hybrid mode).
     */
    public static List<OriginAbility> getAvailableAbilities(Player player) {
        HybridState state = activeHybrids.get(player.getUUID());
        if (state != null) {
            return state.getCombinedAbilities();
        }
        
        Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
        return origin != null ? origin.getAbilities() : Collections.emptyList();
    }
    
    /**
     * Clean up when player logs out.
     */
    public static void onPlayerLogout(Player player) {
        UUID uuid = player.getUUID();
        HybridState state = activeHybrids.remove(uuid);
        if (state != null) {
            // Remove secondary passives
            Origin primary = state.getPrimaryOrigin();
            for (OriginPassive passive : state.getCombinedPassives()) {
                if (!primary.getPassives().contains(passive)) {
                    passive.onRemove(player);
                }
            }
        }
        // Keep cooldown - it persists across sessions
    }
    
    /**
     * Format time in minutes:seconds.
     */
    private static String formatTime(int seconds) {
        int minutes = seconds / 60;
        int secs = seconds % 60;
        if (minutes > 0) {
            return String.format("%d:%02d", minutes, secs);
        }
        return secs + "s";
    }
    
    /**
     * Get all available origins for hybrid selection (excluding current).
     */
    public static List<Origin> getAvailableHybridOrigins(Player player) {
        Origin current = VeilOriginsAPI.getPlayerOrigin(player);
        if (current == null) return Collections.emptyList();
        
        List<Origin> available = new ArrayList<>();
        for (Origin origin : VeilOriginsAPI.getAllOrigins().values()) {
            if (!origin.getId().equals(current.getId())) {
                available.add(origin);
            }
        }
        return available;
    }
}
