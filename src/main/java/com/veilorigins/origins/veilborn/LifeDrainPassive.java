package com.veilorigins.origins.veilborn;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class LifeDrainPassive extends OriginPassive {
    private static final int DRAIN_RADIUS = 8;
    private int tickCounter = 0;

    public LifeDrainPassive() {
        super("life_drain");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;
        
        // Check every 20 ticks (1 second)
        if (tickCounter >= 20) {
            tickCounter = 0;
            
            // Regenerate health when near death (below 6 HP)
            if (player.getHealth() <= 6.0f) {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 0, false, false));
            }
            
            // Drain hunger when near living players
            Level level = player.level();
            AABB area = new AABB(
                player.getX() - DRAIN_RADIUS, player.getY() - DRAIN_RADIUS, player.getZ() - DRAIN_RADIUS,
                player.getX() + DRAIN_RADIUS, player.getY() + DRAIN_RADIUS, player.getZ() + DRAIN_RADIUS
            );
            
            List<Player> nearbyPlayers = level.getEntitiesOfClass(Player.class, area, 
                p -> p != player && !p.isSpectator());
            
            if (!nearbyPlayers.isEmpty()) {
                // Drain hunger 2x faster when near living players
                player.causeFoodExhaustion(0.1f);
            }
        }
    }

    @Override
    public void onEquip(Player player) {
        // Called when player selects Veilborn origin
    }

    @Override
    public void onRemove(Player player) {
        // Called when player changes from Veilborn origin
        player.removeEffect(MobEffects.REGENERATION);
    }
}
