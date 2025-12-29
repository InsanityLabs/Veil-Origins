package com.veilorigins.origins.mycomorph;

import com.veilorigins.api.OriginPassive;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class DecomposerPassive extends OriginPassive {
    private int tickCounter = 0;

    public DecomposerPassive() {
        super("decomposer");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;

        // Convert grass to mycelium (1 block per 30 seconds = 600 ticks)
        if (tickCounter % 600 == 0 && !player.level().isClientSide()) {
            BlockPos pos = player.blockPosition().below();
            Level level = player.level();
            if (level.getBlockState(pos).is(Blocks.GRASS_BLOCK) || level.getBlockState(pos).is(Blocks.DIRT)) {
                level.setBlockAndUpdate(pos, Blocks.MYCELIUM.defaultBlockState());
            }
        }

        // Grow nearby mushrooms fast?
        // 3x speed implies random tick boost. Hard to target specific blocks.
        // We can random tick blocks around player occasionally.
        if (tickCounter % 20 == 0 && !player.level().isClientSide()) {
            BlockPos pos = player.blockPosition();
            int range = 5;
            // Try 3 random attempts
            for (int i = 0; i < 3; i++) {
                BlockPos p = pos.offset(
                        (int) ((Math.random() - 0.5) * range * 2),
                        (int) ((Math.random() - 0.5) * range),
                        (int) ((Math.random() - 0.5) * range * 2));
                if (player.level().getBlockState(p).is(Blocks.RED_MUSHROOM)
                        || player.level().getBlockState(p).is(Blocks.BROWN_MUSHROOM)) {
                    player.level().getBlockState(p).randomTick((net.minecraft.server.level.ServerLevel) player.level(),
                            p, player.level().random);
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
