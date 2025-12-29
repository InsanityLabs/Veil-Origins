package com.veilorigins.origins.cindersoul;

import com.veilorigins.api.OriginPassive;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

@SuppressWarnings("deprecation")
public class CindersoulWeaknessesPassive extends OriginPassive {
    private int tickCounter = 0;

    public CindersoulWeaknessesPassive() {
        super("cindersoul_weaknesses");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;
        Level level = player.level();

        // Continuous checks (movement)
        if (player.isInWater() && !player.onGround()) {
            // Sink like cobblestone
            player.setDeltaMovement(player.getDeltaMovement().x * 0.8, -0.3, player.getDeltaMovement().z * 0.8);
        }

        // Periodic checks (every second)
        if (tickCounter % 20 == 0) {

            // Water damage
            if (player.isInWater()) {
                player.hurt(player.damageSources().drown(), 1.0f);
            }

            // Rain Check
            BlockPos pos = player.blockPosition();
            if (level.isRainingAt(pos)) {
                player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 1, false, false)); // Slowness
                                                                                                            // II
                player.causeFoodExhaustion(0.3f); // Drains hunger faster (approx 3x normal rate when accumulated)
            }

            // Cold Biome / Snow damage
            Biome biome = level.getBiome(pos).value();
            if (biome.coldEnoughToSnow(pos, level.getSeaLevel())) { // Checks if biome is cold at position (accounts for height)
                // continuous 0.5 HP per second -> 0.5 damage
                player.hurt(player.damageSources().freeze(), 0.5f);
            }

            // Powder Snow
            if (player.isInPowderSnow) {
                player.hurt(player.damageSources().freeze(), 3.0f);
            }
        }
    }

    @Override
    public void onEquip(Player player) {
    }

    @Override
    public void onRemove(Player player) {
        player.removeEffect(MobEffects.SLOWNESS);
    }
}
