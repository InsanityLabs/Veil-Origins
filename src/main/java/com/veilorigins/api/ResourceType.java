package com.veilorigins.api;

import net.minecraft.world.entity.player.Player;

public class ResourceType {
    private final String name;
    private final int maxAmount;
    private final float regenRate;

    public ResourceType(String name, int maxAmount, float regenRate) {
        this.name = name;
        this.maxAmount = maxAmount;
        this.regenRate = regenRate;
    }

    public String getName() { return name; }
    public int getMaxAmount() { return maxAmount; }
    public float getRegenRate() { return regenRate; }

    public void onRegenTick(Player player, float amount) {
        // Override in subclasses for custom logic
    }
}
