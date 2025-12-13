package com.veilorigins.origins.frostborn;

import com.veilorigins.api.OriginPassive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class PermafrostPassive extends OriginPassive {
    private int tickCounter = 0;

    public PermafrostPassive() {
        super("permafrost");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;
        Level level = player.level();
        
        // Check every 5 ticks
        if (tickCounter >= 5) {
            tickCounter = 0;
            
            BlockPos playerPos = player.blockPosition();
            
            // Water you touch freezes (walk on water)
            BlockPos below = playerPos.below();
            if (level.getBlockState(below).is(Blocks.WATER)) {
                level.setBlock(below, Blocks.FROSTED_ICE.defaultBlockState(), 3);
            }
            
            // Freeze water around you
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos checkPos = playerPos.offset(x, 0, z);
                    if (level.getBlockState(checkPos).is(Blocks.WATER)) {
                        level.setBlock(checkPos, Blocks.ICE.defaultBlockState(), 3);
                    }
                }
            }
            
            // Create snow layer where you walk (cosmetic)
            if (player.getDeltaMovement().horizontalDistance() > 0.1) {
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
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§bAs Frostborn, you freeze water and are immune to cold."));
    }

    @Override
    public void onRemove(Player player) {
        // Called when player changes from Frostborn origin
    }
}
