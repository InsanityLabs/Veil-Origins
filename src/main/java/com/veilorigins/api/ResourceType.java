package com.veilorigins.api;

import net.minecraft.world.entity.player.Player;

/**
 * Defines a resource type for an origin with optional custom bar rendering.
 */
public class ResourceType {
    private final String name;
    private final int maxAmount;
    private final float regenRate;
    private CustomResourceBar customBar;

    public ResourceType(String name, int maxAmount, float regenRate) {
        this.name = name;
        this.maxAmount = maxAmount;
        this.regenRate = regenRate;
    }
    
    /**
     * Creates a ResourceType with a custom bar configuration.
     */
    public ResourceType(String name, int maxAmount, float regenRate, CustomResourceBar customBar) {
        this.name = name;
        this.maxAmount = maxAmount;
        this.regenRate = regenRate;
        this.customBar = customBar;
    }

    public String getName() { return name; }
    public int getMaxAmount() { return maxAmount; }
    public float getRegenRate() { return regenRate; }
    
    /**
     * Gets the custom bar configuration, or null if using default rendering.
     */
    public CustomResourceBar getCustomBar() { return customBar; }
    
    /**
     * Sets a custom bar configuration for this resource type.
     */
    public ResourceType setCustomBar(CustomResourceBar customBar) {
        this.customBar = customBar;
        return this;
    }
    
    /**
     * Checks if this resource type has custom bar rendering.
     */
    public boolean hasCustomBar() {
        return customBar != null;
    }

    public void onRegenTick(Player player, float amount) {
        // Override in subclasses for custom logic
    }
    
    // Static factory methods for common resource types
    
    /**
     * Creates a blood resource type for vampires.
     */
    public static ResourceType blood(String modId) {
        return new ResourceType("blood", 100, 0.0f, CustomResourceBar.bloodBar(modId));
    }
    
    /**
     * Creates a mana resource type.
     */
    public static ResourceType mana() {
        return new ResourceType("mana", 100, 0.5f, CustomResourceBar.manaBar());
    }
    
    /**
     * Creates a heat resource type for fire-based origins.
     */
    public static ResourceType heat() {
        return new ResourceType("internal_heat", 100, 0.1f, CustomResourceBar.heatBar());
    }
    
    /**
     * Creates a hydration resource type for water-based origins.
     */
    public static ResourceType hydration() {
        return new ResourceType("hydration", 100, 0.0f, CustomResourceBar.hydrationBar());
    }
    
    /**
     * Creates a stellar energy resource type.
     */
    public static ResourceType stellarEnergy() {
        return new ResourceType("stellar_energy", 100, 0.2f, CustomResourceBar.stellarBar());
    }
}
