package com.veilorigins.origins.necromancer;

import com.veilorigins.api.OriginPassive;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.ChatFormatting;

public class NecromancerWeaknessesPassive extends OriginPassive {
    private int tickCounter = 0;
    private int sunlightDamageCounter = 0;

    public NecromancerWeaknessesPassive() {
        super("necromancer_weaknesses");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;
        Level level = player.level();
        BlockPos playerPos = player.blockPosition();

        // Every 20 ticks (1 second)
        if (tickCounter >= 20) {
            tickCounter = 0;

            // Check if in direct bright sunlight
            boolean inBrightSunlight = level.canSeeSky(playerPos.above()) &&
                    (level.getDayTime() < 13000) &&
                    !level.isRaining() &&
                    level.getBrightness(LightLayer.SKY, playerPos.above()) >= 15;

            if (inBrightSunlight) {
                // Weakness in sunlight
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0, true, false));

                sunlightDamageCounter++;
                // After 30 seconds in direct sunlight, start taking damage
                if (sunlightDamageCounter >= 30) {
                    player.hurt(level.damageSources().magic(), 0.5f);
                }
                // After 60 seconds, take more damage
                if (sunlightDamageCounter >= 60) {
                    player.hurt(level.damageSources().magic(), 1.0f);
                }
            } else {
                // Reset counter when not in bright sunlight
                sunlightDamageCounter = 0;
            }

            // Healing items are 50% less effective - handled in event handler
            // Holy/radiant damage deals +50% - handled in event handler

            // Check for consecrated ground (if near a beacon with healing effect)
            // This is a simplified check - in full implementation would check for temples
            // etc.
            if (isNearBeacon(level, playerPos)) {
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 1, true, false));
                player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 0, true, false));
            }
        }
    }

    private boolean isNearBeacon(Level level, BlockPos playerPos) {
        // Check for beacons in a small radius
        for (BlockPos pos : BlockPos.betweenClosed(
                playerPos.offset(-20, -5, -20),
                playerPos.offset(20, 5, 20))) {
            if (level.getBlockState(pos).is(net.minecraft.world.level.block.Blocks.BEACON)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onEquip(Player player) {
        player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                ChatFormatting.RED + "Warning: As a Necromancer, prolonged sunlight exposure is harmful. "
                        + "Healing is less effective. Holy places weaken you."), false);
    }

    @Override
    public void onRemove(Player player) {
        sunlightDamageCounter = 0;
    }
}
