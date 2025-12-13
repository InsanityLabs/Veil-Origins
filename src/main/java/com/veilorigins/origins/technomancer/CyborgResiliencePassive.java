package com.veilorigins.origins.technomancer;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class CyborgResiliencePassive extends OriginPassive {
    public CyborgResiliencePassive() {
        super("cyborg_resilience");
    }

    @Override
    public void onTick(Player player) {
        // "Immune to poison"
        if (player.hasEffect(MobEffects.POISON)) {
            player.removeEffect(MobEffects.POISON);
        }

        // Night Vision
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false));
    }

    @Override
    public void onEquip(Player player) {
    }

    @Override
    public void onRemove(Player player) {
        player.removeEffect(MobEffects.NIGHT_VISION);
    }
}
