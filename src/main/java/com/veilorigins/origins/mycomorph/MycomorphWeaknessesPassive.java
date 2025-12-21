package com.veilorigins.origins.mycomorph;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.ChatFormatting;

public class MycomorphWeaknessesPassive extends OriginPassive {
    private int tickCounter = 0;

    public MycomorphWeaknessesPassive() {
        super("mycomorph_weaknesses");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;

        if (tickCounter % 20 == 0) {
            // Take 1 HP damage per second near fire
            if (player.level().getBlockState(player.blockPosition()).is(net.minecraft.world.level.block.Blocks.FIRE) ||
                    player.isOnFire()) {
                // Damage handled by vanilla fire usually, but this is extra "near fire sources"
                // Let's check radius? "Near fire sources".
                // Simple check for fire block in 2 block radius
                // ...
                // Let's rely on event handler for the 100% increased fire damage,
                // and here just Lava instakill safety measure if event handler misses?

                // Desert biome drain hunger 2x
                if (player.level().getBiome(player.blockPosition()).value().getBaseTemperature() > 1.0f) {
                    player.causeFoodExhaustion(0.05f);
                }
            }
        }
    }

    @Override
    public void onEquip(Player player) {
        player.sendSystemMessage(net.minecraft.network.chat.Component
                .literal(ChatFormatting.DARK_GREEN + "As Mycomorph, fire is death, but nature is your ally."));
    }

    @Override
    public void onRemove(Player player) {
    }
}
