package com.veilorigins.origins.tidecaller;

import com.veilorigins.api.OriginPassive;
import com.veilorigins.data.OriginData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class HydrationDependencyPassive extends OriginPassive {
    public HydrationDependencyPassive() {
        super("hydration_dependency");
    }

    @Override
    public void onTick(Player player) {
        OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);
        if (data.getResourceBar() <= 0) {
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0, false, false));
            // Hunger drains 2x faster (simulated by adding exhaustion)
            // Normal exhaustion for walking is small. We add a continuous tax.
            player.causeFoodExhaustion(0.05f);
        }
    }

    @Override
    public void onEquip(Player player) {}

    @Override
    public void onRemove(Player player) {
        player.removeEffect(MobEffects.WEAKNESS);
    }
}
