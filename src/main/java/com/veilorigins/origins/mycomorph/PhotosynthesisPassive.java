package com.veilorigins.origins.mycomorph;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class PhotosynthesisPassive extends OriginPassive {
    private int tickCounter = 0;

    public PhotosynthesisPassive() {
        super("photosynthesis");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;

        boolean inSun = player.level().isDay() && player.level().canSeeSky(player.blockPosition());

        if (inSun) {
            // Regen health slowly (0.5 HP per 5 seconds = 100 ticks)
            if (tickCounter % 100 == 0) {
                if (player.getHealth() < player.getMaxHealth()) {
                    player.heal(1.0f);
                }
            }
            // Freeze hunger (Sat + Hunger)
            // Just add saturation to counter exhaustion? Or setFoodLevel constant?
            // "Hunger frozen"
            if (player.getFoodData().getFoodLevel() < 20) {
                // Slowly restore or just maintain? "Don't need to eat"
                // Let's prevent drain.
                // We can simply set exhaustion to 0 constantly or add small saturation.
                if (player.getFoodData().getExhaustionLevel() > 0) {
                    player.getFoodData().setExhaustion(0);
                }
            }
        }
    }

    @Override
    public void onEquip(Player player) {
    }

    @Override
    public void onRemove(Player player) {
    }
}
