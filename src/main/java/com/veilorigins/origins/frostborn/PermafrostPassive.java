package com.veilorigins.origins.frostborn;

import com.veilorigins.api.OriginPassive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.ChatFormatting;

public class PermafrostPassive extends OriginPassive {
    private int tickCounter = 0;

    public PermafrostPassive() {
        super("permafrost");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;
        Level level = player.level();

        // Check every 2 ticks for more responsive ice creation (prevents clipping)
        if (tickCounter >= 2) {
            tickCounter = 0;

            BlockPos playerPos = player.blockPosition();
            net.minecraft.world.phys.Vec3 movement = player.getDeltaMovement();

            // Create ice in a wider pattern including movement direction
            // This prevents clipping through ice when running fast
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    // Prioritize the center and movement direction
                    BlockPos checkPos = playerPos.offset(x, -1, z);
                    BlockPos aboveCheck = playerPos.offset(x, 0, z);

                    if (level.getBlockState(checkPos).is(Blocks.WATER)) {
                        // Use regular ice for stability (doesn't decay like frosted ice)
                        level.setBlock(checkPos, Blocks.ICE.defaultBlockState(), 3);
                    }

                    // Also check for water at foot level
                    if (level.getBlockState(aboveCheck).is(Blocks.WATER)) {
                        level.setBlock(aboveCheck, Blocks.ICE.defaultBlockState(), 3);
                    }
                }
            }

            // If moving quickly, freeze blocks ahead in movement direction
            if (movement.horizontalDistance() > 0.1) {
                double moveX = movement.x > 0 ? 1 : (movement.x < 0 ? -1 : 0);
                double moveZ = movement.z > 0 ? 1 : (movement.z < 0 ? -1 : 0);

                // Create ice 2-3 blocks ahead in movement direction
                for (int ahead = 2; ahead <= 4; ahead++) {
                    BlockPos aheadPos = playerPos.offset((int) (moveX * ahead), -1, (int) (moveZ * ahead));
                    if (level.getBlockState(aheadPos).is(Blocks.WATER)) {
                        level.setBlock(aheadPos, Blocks.ICE.defaultBlockState(), 3);
                    }
                    // Also sides when moving diagonally
                    if (moveX != 0 && moveZ != 0) {
                        BlockPos side1 = aheadPos.offset((int) moveX, 0, 0);
                        BlockPos side2 = aheadPos.offset(0, 0, (int) moveZ);
                        if (level.getBlockState(side1).is(Blocks.WATER)) {
                            level.setBlock(side1, Blocks.ICE.defaultBlockState(), 3);
                        }
                        if (level.getBlockState(side2).is(Blocks.WATER)) {
                            level.setBlock(side2, Blocks.ICE.defaultBlockState(), 3);
                        }
                    }
                }
            }

            // Create snow particles where you walk (cosmetic)
            if (movement.horizontalDistance() > 0.1) {
                BlockPos below = playerPos.below();
                if (level.getBlockState(below).isSolid() && level.getBlockState(playerPos).isAir()) {
                    if (level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                                playerPos.getX(), playerPos.getY(), playerPos.getZ(),
                                2, 0.3, 0.1, 0.3, 0.01);
                    }
                }
            }
        }

        // Note: Immune to cold damage is handled in event handler
        // Note: Snow doesn't slow you down is handled in event handler
    }

    @Override
    public void onEquip(Player player) {
        player.displayClientMessage(net.minecraft.network.chat.Component
                .literal(ChatFormatting.AQUA + "As Frostborn, you freeze water and are immune to cold."), false);
    }

    @Override
    public void onRemove(Player player) {
        // Called when player changes from Frostborn origin
    }
}
