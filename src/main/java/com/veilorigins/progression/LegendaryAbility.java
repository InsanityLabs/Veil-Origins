package com.veilorigins.progression;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Base class for legendary abilities - ultimate powers unlocked at level 50.
 * Activated by holding R and attacking (punching).
 */
public abstract class LegendaryAbility {
    private final String id;
    private final String name;
    private final String description;
    private final int cooldownSeconds;
    private int currentCooldown = 0;
    
    public LegendaryAbility(String id, String name, String description, int cooldownSeconds) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.cooldownSeconds = cooldownSeconds;
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getCooldownSeconds() { return cooldownSeconds; }
    public int getCurrentCooldown() { return currentCooldown; }
    
    public boolean isOnCooldown() {
        return currentCooldown > 0;
    }
    
    public void tickCooldown() {
        if (currentCooldown > 0) {
            currentCooldown--;
        }
    }
    
    public void startCooldown() {
        this.currentCooldown = cooldownSeconds * 20; // Convert to ticks
    }
    
    /**
     * Check if the legendary ability can be activated.
     */
    public boolean canActivate(Player player) {
        if (isOnCooldown()) return false;
        if (!ProgressionSystem.canUseLegendaryAbility(player)) return false;
        return true;
    }
    
    /**
     * Activate the legendary ability.
     */
    public final void activate(Player player, Level level) {
        if (!canActivate(player)) return;
        
        onActivate(player, level);
        startCooldown();
    }
    
    /**
     * Override this to implement the legendary ability effect.
     */
    protected abstract void onActivate(Player player, Level level);
    
    /**
     * Get the resource cost (if any).
     */
    public int getResourceCost() {
        return 0;
    }
}
