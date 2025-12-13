package com.veilorigins.origins.riftwalker;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class SpatialAwarenessPassive extends OriginPassive {
    private int tickCounter = 0;

    public SpatialAwarenessPassive() {
        super("spatial_awareness");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;
        
        // No fall damage - cancel fall damage
        if (player.fallDistance > 3.0f) {
            player.fallDistance = 0;
        }
        
        // Every 20 ticks (1 second), apply glowing to nearby entities
        if (tickCounter >= 20) {
            tickCounter = 0;
            
            Level level = player.level();
            AABB area = new AABB(
                player.getX() - 32, player.getY() - 32, player.getZ() - 32,
                player.getX() + 32, player.getY() + 32, player.getZ() + 32
            );
            
            // Make all entities glow (minimap effect simulation)
            List<Entity> entities = level.getEntities(player, area);
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity living && !entity.isSpectator()) {
                    living.addEffect(new MobEffectInstance(MobEffects.GLOWING, 30, 0, false, false));
                }
            }
        }
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
