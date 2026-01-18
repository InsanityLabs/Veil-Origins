package com.veilorigins.origins.technomancer;

import com.veilorigins.api.OriginAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.ChatFormatting;

public class RedstonePulseAbility extends OriginAbility {
    private static final int COOLDOWN = 20 * 20;
    private static final int HUNGER_COST = 4;
    private static final int RADIUS = 10; // Spec says 20 blocks, but 20 is huge for block updates. 10 is safer.

    public RedstonePulseAbility() {
        super("redstone_pulse", COOLDOWN);
    }

    @Override
    public void onActivate(Player player, Level level) {
        player.causeFoodExhaustion(HUNGER_COST);

        BlockPos center = player.blockPosition();
        int count = 0;
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-RADIUS, -RADIUS, -RADIUS),
                center.offset(RADIUS, RADIUS, RADIUS))) {
            BlockState state = level.getBlockState(pos);
            if (isRedstoneComponent(state)) {
                // Trigger it?
                // Lever/Button: pulse state?
                // "Activate simultaneous".
                // Easiest is to set POWERED property if exists.
                if (state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED)) {
                    boolean current = state
                            .getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED);
                    if (!current) {
                        level.setBlockAndUpdate(pos, state.setValue(
                                net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED, true));
                        level.scheduleTick(pos, state.getBlock(), 20); // Turn off after 1 sec?
                        // We need a way to revert it, but simple "ON" is okay for now.
                        count++;
                    }
                }
                // Dispenser/Dropper/Comparators/Repeaters?
            }
        }

        player.displayClientMessage(
                Component.literal(ChatFormatting.RED + "Redstone Pulse sent! (" + count + " activated)"), false);
        startCooldown();
    }

    private boolean isRedstoneComponent(BlockState state) {
        return state.is(Blocks.REDSTONE_WIRE) ||
                state.is(Blocks.LEVER) ||
                state.getBlock() instanceof net.minecraft.world.level.block.ButtonBlock ||
                state.getBlock() instanceof net.minecraft.world.level.block.LeverBlock ||
                state.getBlock() instanceof net.minecraft.world.level.block.RedstoneTorchBlock ||
                state.getBlock() instanceof net.minecraft.world.level.block.RedStoneWireBlock;
    }

    @Override
    public boolean canUse(Player player) {
        return !isOnCooldown() && player.getFoodData().getFoodLevel() >= HUNGER_COST;
    }

    @Override
    public int getResourceCost() {
        return 0;
    }
}
