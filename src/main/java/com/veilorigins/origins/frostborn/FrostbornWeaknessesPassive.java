package com.veilorigins.origins.frostborn;

import com.veilorigins.api.OriginPassive;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.ChatFormatting;

public class FrostbornWeaknessesPassive extends OriginPassive {
    private int tickCounter = 0;

    public FrostbornWeaknessesPassive() {
        super("frostborn_weaknesses");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;
        Level level = player.level();

        // Check every 20 ticks (1 second)
        if (tickCounter >= 20) {
            tickCounter = 0;

            BlockPos playerPos = player.blockPosition();

            // Standing near fire/lava deals 1 HP per second
            boolean nearFire = false;
            for (int x = -3; x <= 3; x++) {
                for (int y = -2; y <= 2; y++) {
                    for (int z = -3; z <= 3; z++) {
                        BlockPos checkPos = playerPos.offset(x, y, z);
                        if (level.getBlockState(checkPos).is(Blocks.FIRE) ||
                                level.getBlockState(checkPos).is(Blocks.LAVA) ||
                                level.getBlockState(checkPos).is(Blocks.CAMPFIRE) ||
                                level.getBlockState(checkPos).is(Blocks.SOUL_CAMPFIRE)) {
                            nearFire = true;
                            break;
                        }
                    }
                }
            }

            if (nearFire) {
                player.hurt(level.damageSources().onFire(), 1.0f);
            }

            // Desert/hot biomes deal 0.5 HP per second
            Biome biome = level.getBiome(playerPos).value();
            if (biome.getBaseTemperature() > 1.0f) { // Hot biomes
                player.hurt(level.damageSources().onFire(), 0.5f);
            }
        }

        // Note: Fire damage increased by 100% is handled in event handler
        // Note: Lava is instakill is handled in event handler
        // Note: Cannot eat hot food would require item consumption event
    }

    @Override
    public void onEquip(Player player) {
        player.displayClientMessage(net.minecraft.network.chat.Component
                .literal(ChatFormatting.AQUA + "As Frostborn, fire and heat are lethal to you."), false);
    }

    @Override
    public void onRemove(Player player) {
        // Called when player changes from Frostborn origin
    }
}
