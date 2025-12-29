package com.veilorigins.origins.skyborn;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class SkybornWeaknessesPassive extends OriginPassive {
    private int tickCounter = 0;

    public SkybornWeaknessesPassive() {
        super("skyborn_weaknesses");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;

        if (tickCounter % 20 == 0) {
            double y = player.getY();

            // Slowness underground < 40
            if (y < 40) {
                player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 0, false, false));
                // Mining Fatigure in caves (implied by "in caves", approximated by y < 40 and
                // low light?)
                // Spec says "Mining Fatigue I in caves". Let's stick to Y level + not seeing
                // sky check?
                if (!player.level().canSeeSky(player.blockPosition())) {
                    player.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, 40, 0, false, false));
                }
            }

            // Claustrophobia (Enclosed spaces)
            // Check if blocks around head are solid?
            if (isEnclosed(player)) {
                player.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 100, 0, false, false)); // Nausea
            }
        }
    }

    private boolean isEnclosed(Player player) {
        // Simple check: Block above head and around are solid?
        // "Enclosed spaces"
        // Let's check 2 block radius for walls on at least 3 sides + ceiling?
        // Too expensive to run every second?
        // Simple check: Is block directly above 2 blocks up solid?
        return !player.level().canSeeSky(player.blockPosition()) && player.getY() < 60; // Simple "Deep
                                                                                        // underground/inside"
                                                                                        // approximation
    }

    @Override
    public void onEquip(Player player) {
    }

    @Override
    public void onRemove(Player player) {
        player.removeEffect(MobEffects.SLOWNESS);
        player.removeEffect(MobEffects.MINING_FATIGUE);
    }
}
