package com.veilorigins.origins.riftwalker;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.entity.player.Player;

public class RiftwalkerWeaknessesPassive extends OriginPassive {

    public RiftwalkerWeaknessesPassive() {
        super("riftwalker_weaknesses");
    }

    @Override
    public void onTick(Player player) {
        // Weaknesses are handled through damage modifiers in events
        // This passive exists for organization and future expansion
    }

    @Override
    public void onEquip(Player player) {
        // Called when player selects Riftwalker origin
    }

    @Override
    public void onRemove(Player player) {
        // Called when player changes from Riftwalker origin
    }
}
