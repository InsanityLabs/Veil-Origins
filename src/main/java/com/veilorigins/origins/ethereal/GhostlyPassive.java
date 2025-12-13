package com.veilorigins.origins.ethereal;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class GhostlyPassive extends OriginPassive {
    public GhostlyPassive() {
        super("ghostly");
    }

    @Override
    public void onTick(Player player) {
        // "Transparency" -> Visual client side.

        // "Silent footsteps"
        // Can be done via modifying attribute or event.
        // We can silence player via `setSilent(true)`?
        player.setSilent(true);

        // "Mobs detection range" -> Attribute `generic.follow_range` modifications on
        // mobs? Hard to apply to all mobs vs specific player.
        // Usually handled by stealth mechanics (invisibility helps).
        // Let's give Invisibility effect continuously? But spec says "50% see-through".
        // Semi-transparent usually implies custom rendering.
    }

    @Override
    public void onEquip(Player player) {
        player.setSilent(true);
    }

    @Override
    public void onRemove(Player player) {
        player.setSilent(false);
    }
}
