package com.veilorigins.origins.veilborn;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class VeilbornWeaknessesPassive extends OriginPassive {
    private int tickCounter = 0;

    public VeilbornWeaknessesPassive() {
        super("veilborn_weaknesses");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;
        
        // Check every 20 ticks (1 second)
        if (tickCounter >= 20) {
            tickCounter = 0;
            
            Level level = player.level();
            
            // Sunlight weakness - applies Weakness I in sunlight
            if (level.isDay() && level.canSeeSky(player.blockPosition())) {
                int lightLevel = level.getMaxLocalRawBrightness(player.blockPosition());
                if (lightLevel >= 12) {
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0, false, false));
                }
            }
        }
        
        // Cannot regenerate health naturally - disable natural regeneration
        // This is handled by removing the regeneration that would normally occur
        if (player.getFoodData().getFoodLevel() >= 18 && player.getHealth() < player.getMaxHealth()) {
            // Cancel natural regeneration by setting food exhaustion
            player.causeFoodExhaustion(0.1f);
        }
    }

    @Override
    public void onEquip(Player player) {
        // Called when player selects Veilborn origin
    }

    @Override
    public void onRemove(Player player) {
        // Called when player changes from Veilborn origin
        player.removeEffect(MobEffects.WEAKNESS);
    }
}
