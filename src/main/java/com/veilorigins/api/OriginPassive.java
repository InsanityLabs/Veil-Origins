package com.veilorigins.api;

import net.minecraft.world.entity.player.Player;

public abstract class OriginPassive {
    private final String id;

    public OriginPassive(String id) {
        this.id = id;
    }

    public String getId() { return id; }

    public abstract void onTick(Player player);
    public void onEquip(Player player) {}
    public void onRemove(Player player) {}
}
