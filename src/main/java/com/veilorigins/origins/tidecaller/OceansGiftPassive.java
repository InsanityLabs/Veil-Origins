package com.veilorigins.origins.tidecaller;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class OceansGiftPassive extends OriginPassive { // Fixed class name
    public OceansGiftPassive() {
        super("oceans_gift");
    }

    @Override
    public void onTick(Player player) {
        if (player.isInWaterOrRain()) { 
             // Grants:
             // - Underwater Breathing (Conduit Power)
             // - Night Vision underwater (Conduit Power)
             // - Break blocks normal speed (Conduit Power)
             // - Swim Speed (Dolphins Grace)
             player.addEffect(new MobEffectInstance(MobEffects.CONDUIT_POWER, 40, 0, false, false));
             player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 40, 0, false, false));
             player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 40, 0, false, false)); // Redundant but safe
        }
    }

    @Override
    public void onEquip(Player player) {}

    @Override
    public void onRemove(Player player) {
        player.removeEffect(MobEffects.CONDUIT_POWER);
        player.removeEffect(MobEffects.DOLPHINS_GRACE);
        player.removeEffect(MobEffects.WATER_BREATHING);
    }
}
