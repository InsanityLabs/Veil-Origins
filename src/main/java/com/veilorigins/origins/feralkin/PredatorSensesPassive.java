package com.veilorigins.origins.feralkin;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class PredatorSensesPassive extends OriginPassive {
    private static final int DETECTION_RANGE = 20;

    public PredatorSensesPassive() {
        super("predator_senses");
    }

    @Override
    public void onTick(Player player) {
        // Highlight wounded entities
        Vec3 pos = player.position();
        AABB area = new AABB(pos.x - DETECTION_RANGE, pos.y - DETECTION_RANGE, pos.z - DETECTION_RANGE,
                             pos.x + DETECTION_RANGE, pos.y + DETECTION_RANGE, pos.z + DETECTION_RANGE);
        
        List<LivingEntity> entities = player.level().getEntitiesOfClass(LivingEntity.class, area);
        
        for (LivingEntity entity : entities) {
            if (entity != player && entity.getHealth() < entity.getMaxHealth()) {
                // Apply glowing effect to wounded entities
                entity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 20, 0, false, false));
            }
        }
    }

    @Override
    public void onEquip(Player player) {
        // Grant night vision
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, Integer.MAX_VALUE, 0, false, false));
    }

    @Override
    public void onRemove(Player player) {
        player.removeEffect(MobEffects.NIGHT_VISION);
    }
}
