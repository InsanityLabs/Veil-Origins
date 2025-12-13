package com.veilorigins.origins.veilborn;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class SpectralFormPassive extends OriginPassive {
    private int tickCounter = 0;

    public SpectralFormPassive() {
        super("spectral_form");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;
        
        // Night vision in darkness (light level < 7)
        if (player.level().getMaxLocalRawBrightness(player.blockPosition()) < 7) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false));
        }
        
        // Check every 20 ticks (1 second) for undead mobs
        if (tickCounter >= 20) {
            tickCounter = 0;
            
            Level level = player.level();
            AABB area = new AABB(
                player.getX() - 16, player.getY() - 8, player.getZ() - 16,
                player.getX() + 16, player.getY() + 8, player.getZ() + 16
            );
            
            // Make undead mobs ignore the player
            List<LivingEntity> undeadMobs = level.getEntitiesOfClass(LivingEntity.class, area, 
                entity -> entity instanceof Zombie || entity instanceof Skeleton || entity instanceof Phantom);
            
            for (LivingEntity undead : undeadMobs) {
                // Clear player as target if they're targeting us
                // Use Mob class which has target methods
                if (undead instanceof net.minecraft.world.entity.Mob mob) {
                    if (mob.getTarget() == player) {
                        mob.setTarget(null);
                    }
                }
            }
        }
        
        // Can see invisible entities - apply glowing effect to nearby invisible entities
        if (tickCounter % 10 == 0) {
            Level level = player.level();
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
        // Called when player selects Veilborn origin
    }

    @Override
    public void onRemove(Player player) {
        // Called when player changes from Veilborn origin
        player.removeEffect(MobEffects.NIGHT_VISION);
    }
}
