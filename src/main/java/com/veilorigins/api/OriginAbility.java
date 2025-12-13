package com.veilorigins.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public abstract class OriginAbility {
    private final String id;
    private int cooldown;
    private final int baseCooldown;

    public OriginAbility(String id, int cooldownSeconds) {
        this.id = id;
        this.baseCooldown = cooldownSeconds * 20;
        this.cooldown = baseCooldown;
    }

    public String getId() { return id; }
    public int getCooldown() { return cooldown; }
    public void setCooldown(int ticks) { this.cooldown = ticks; }

    public abstract void onActivate(Player player, Level level);
    public abstract boolean canUse(Player player);
    public abstract int getResourceCost();

    public void tick(Player player) {
        // Override in subclasses for custom tick behavior
    }

    public void tickCooldown() {
        if (cooldown > 0) cooldown--;
    }

    public boolean isOnCooldown() {
        return cooldown > 0;
    }

    public void startCooldown() {
        this.cooldown = baseCooldown;
    }
}
