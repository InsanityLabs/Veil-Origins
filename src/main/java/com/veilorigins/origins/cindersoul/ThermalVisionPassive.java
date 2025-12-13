package com.veilorigins.origins.cindersoul;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class ThermalVisionPassive extends OriginPassive {
    private int tickCounter = 0;

    public ThermalVisionPassive() {
        super("thermal_vision");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;
        
        // Refresh every second (20 ticks)
        if (tickCounter % 20 == 0) {
            AABB area = new AABB(player.blockPosition()).inflate(15);
            List<LivingEntity> entities = player.level().getEntitiesOfClass(LivingEntity.class, area);
            
            for (LivingEntity entity : entities) {
                if (entity != player) {
                    // Apply glowing effect to visualize "heat signature"
                    // Duration 40 ticks to prevent flickering between updates
                    entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0, false, false));
                }
            }
        }
    }

    @Override
    public void onEquip(Player player) {}

    @Override
    public void onRemove(Player player) {
       // We can't easily remove glowing from everyone instantly without scanning again, 
       // but it will expire in 2 seconds anyway.
    }
}
