package com.veilorigins.origins.mycomorph;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.entity.player.Player;

public class PhotosynthesisPassive extends OriginPassive {
    private int tickCounter = 0;

    public PhotosynthesisPassive() {
        super("photosynthesis");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;

        boolean inSun = player.level().getSunAngle(1.0F) < 0.5F && player.level().canSeeSky(player.blockPosition());

        if (inSun) {
            // Regen health slowly (0.5 HP per 5 seconds = 100 ticks)
            if (tickCounter % 100 == 0) {
                if (player.getHealth() < player.getMaxHealth()) {
                    player.heal(1.0f);
                }
            }
            // Freeze hunger - just add saturation to counter exhaustion
            // In 1.21.10 FoodData API changed, so we use addExhaustion with negative value or just heal food
            if (player.getFoodData().getFoodLevel() < 20) {
                // Slowly restore food by adding small saturation
                player.getFoodData().setSaturation(player.getFoodData().getSaturationLevel() + 0.1f);
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
