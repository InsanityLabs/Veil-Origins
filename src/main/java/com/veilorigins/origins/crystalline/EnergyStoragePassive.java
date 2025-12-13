package com.veilorigins.origins.crystalline;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.entity.player.Player;

public class EnergyStoragePassive extends OriginPassive {
    public EnergyStoragePassive() {
        super("energy_storage");
    }

    @Override
    public void onTick(Player player) {
        // Collect XP?
        // Vanilla XP Orb pickup happens on collision.
        // We can listen to PlayerPickupXpEvent or similar.
        // We'll put logic in EventHandler.
    }

    @Override
    public void onEquip(Player player) {
    }

    @Override
    public void onRemove(Player player) {
    }
}
