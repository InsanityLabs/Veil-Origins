package com.veilorigins.origins.umbrakin;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class ShadowFormPassive extends OriginPassive {
    private int tickCounter = 0;

    public ShadowFormPassive() {
        super("shadow_form");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;
        Level level = player.level();
        int lightLevel = level.getMaxLocalRawBrightness(player.blockPosition());
        
        // Night vision - persistent in darkness, removed in bright light
        // Use 220 ticks (11 seconds) to avoid the flashing warning at 10 seconds
        if (lightLevel < 11) {
            // In darkness or dim light, maintain night vision
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false));
        } else {
            // In bright light, remove night vision
            if (player.hasEffect(MobEffects.NIGHT_VISION)) {
                player.removeEffect(MobEffects.NIGHT_VISION);
            }
        }
        
        // Speed II at night (light level below 7)
        if (lightLevel < 7) {
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, 25, 1, false, false));
        }
        
        // Check every 20 ticks (1 second)
        if (tickCounter >= 20) {
            tickCounter = 0;
            
            // Invisible to mobs in darkness
            if (lightLevel < 7) {
                AABB area = new AABB(
                    player.getX() - 16, player.getY() - 8, player.getZ() - 16,
                    player.getX() + 16, player.getY() + 8, player.getZ() + 16
                );
                
                List<Mob> mobs = level.getEntitiesOfClass(Mob.class, area);
                for (Mob mob : mobs) {
                    if (mob.getTarget() == player) {
                        mob.setTarget(null);
                    }
                }
            }
            
            // Can see invisible entities
            AABB area = new AABB(
                player.getX() - 20, player.getY() - 10, player.getZ() - 20,
                player.getX() + 20, player.getY() + 10, player.getZ() + 20
            );
            
            List<Entity> entities = level.getEntities(player, area, Entity::isInvisible);
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity living) {
                    living.addEffect(new MobEffectInstance(MobEffects.GLOWING, 30, 0, false, false));
                }
            }
        }
    }

    @Override
    public void onEquip(Player player) {
        // Called when player selects Umbrakin origin
    }

    @Override
    public void onRemove(Player player) {
        player.removeEffect(MobEffects.NIGHT_VISION);
        player.removeEffect(MobEffects.SPEED);
    }
}
