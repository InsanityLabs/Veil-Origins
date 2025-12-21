package com.veilorigins.origins.riftwalker;

import com.veilorigins.api.OriginAbility;
import com.veilorigins.dimension.PocketDimensionTeleporter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.ChatFormatting;

public class PocketDimensionAbility extends OriginAbility {
    private static final int RESOURCE_COST = 10;
    private static final int MAX_DURATION = 60 * 20; // 60 seconds in ticks
    private int dimensionTimer = 0;
    private BlockPos returnPos = null;
    private ResourceKey<Level> returnDimension = null;

    public PocketDimensionAbility() {
        super("pocket_dimension", 120); // 2 minute cooldown
    }

    @Override
    public void onActivate(Player player, Level level) {
        if (!(player instanceof ServerPlayer serverPlayer))
            return;

        MinecraftServer server = serverPlayer.getServer();
        if (server == null)
            return;

        // Check if player is in pocket dimension
        if (level.dimension().location().toString().equals("veil_origins:pocket_dimension")) {
            // Return to overworld
            returnFromPocketDimension(serverPlayer, server);
        } else {
            // Enter pocket dimension
            enterPocketDimension(serverPlayer, server);
        }

        startCooldown();
    }

    private void enterPocketDimension(ServerPlayer player, MinecraftServer server) {
        // Store return position
        returnPos = player.blockPosition();
        returnDimension = player.level().dimension();
        dimensionTimer = MAX_DURATION;

        // Get or create pocket dimension
        ServerLevel pocketDim = server.getLevel(PocketDimensionTeleporter.POCKET_DIMENSION);
        if (pocketDim == null) {
            player.sendSystemMessage(Component.literal(ChatFormatting.RED + "Pocket dimension not available!"));
            return;
        }

        // Teleport to pocket dimension
        Vec3 spawnPos = new Vec3(0.5, 64, 0.5);
        player.teleportTo(pocketDim, spawnPos.x, spawnPos.y, spawnPos.z, 0, 0);

        // Effects
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 1.0f, 1.0f);

        if (pocketDim instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.PORTAL,
                    spawnPos.x, spawnPos.y + 1, spawnPos.z, 100, 1, 1, 1, 0.5);
        }

        player.sendSystemMessage(
                Component.literal(ChatFormatting.LIGHT_PURPLE + "Entered Pocket Dimension - Time remaining: 60s"));
    }

    private void returnFromPocketDimension(ServerPlayer player, MinecraftServer server) {
        if (returnDimension == null || returnPos == null) {
            // Default to overworld spawn
            returnDimension = Level.OVERWORLD;
            returnPos = server.overworld().getSharedSpawnPos();
        }

        ServerLevel returnLevel = server.getLevel(returnDimension);
        if (returnLevel == null) {
            returnLevel = server.overworld();
        }

        // Clean up pocket dimension before leaving
        ServerLevel pocketDim = server.getLevel(PocketDimensionTeleporter.POCKET_DIMENSION);
        if (pocketDim != null) {
            resetPocketDimension(pocketDim);
        }

        // Teleport back
        player.teleportTo(returnLevel, returnPos.getX() + 0.5, returnPos.getY(), returnPos.getZ() + 0.5,
                player.getYRot(), player.getXRot());

        // Effects
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PORTAL_TRAVEL, SoundSource.PLAYERS, 1.0f, 1.2f);

        if (returnLevel instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                    player.getX(), player.getY() + 1, player.getZ(), 50, 0.5, 0.5, 0.5, 0.3);
        }

        player.sendSystemMessage(Component.literal(ChatFormatting.LIGHT_PURPLE + "Returned from Pocket Dimension"));

        // Reset
        returnPos = null;
        returnDimension = null;
        dimensionTimer = 0;
    }

    private void resetPocketDimension(ServerLevel pocketDim) {
        // Clear any blocks placed by the player (except the spawn platform)
        BlockPos center = PocketDimensionTeleporter.SPAWN_POS;

        // Clear a 32x32x32 area around spawn (except the platform itself)
        for (int x = -16; x <= 16; x++) {
            for (int y = -16; y <= 16; y++) {
                for (int z = -16; z <= 16; z++) {
                    BlockPos pos = center.offset(x, y, z);

                    // Don't clear the spawn platform (y=63) or glowstone lights
                    if (pos.getY() == 63 && Math.abs(x) <= 8 && Math.abs(z) <= 8) {
                        continue; // Keep platform
                    }
                    if (pos.getY() == 64 &&
                            ((Math.abs(x) == 4 || Math.abs(x) == 11) && (Math.abs(z) == 4 || Math.abs(z) == 11))) {
                        continue; // Keep glowstone
                    }

                    // Clear everything else
                    BlockState currentState = pocketDim.getBlockState(pos);
                    if (!currentState.isAir()) {
                        pocketDim.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    @Override
    public void tick(Player player) {
        if (dimensionTimer > 0) {
            dimensionTimer--;

            // Debug every second
            if (dimensionTimer % 20 == 0) {
                int secondsLeft = dimensionTimer / 20;
                // Only show countdown every 10 seconds
                if (secondsLeft % 10 == 0 && secondsLeft > 10) {
                    player.sendSystemMessage(Component
                            .literal(ChatFormatting.GRAY + "Pocket Dimension: " + secondsLeft + "s remaining"));
                }
            }

            // Warning messages
            if (dimensionTimer == 10 * 20) {
                player.sendSystemMessage(Component.literal(ChatFormatting.RED + "" + ChatFormatting.BOLD
                        + "WARNING: 10 seconds remaining in Pocket Dimension!"));
            } else if (dimensionTimer == 5 * 20) {
                player.sendSystemMessage(Component
                        .literal(ChatFormatting.RED + "" + ChatFormatting.BOLD + "WARNING: 5 seconds remaining!"));
            } else if (dimensionTimer == 0) {
                // Force return
                if (player instanceof ServerPlayer serverPlayer) {
                    MinecraftServer server = serverPlayer.getServer();
                    if (server != null) {
                        returnFromPocketDimension(serverPlayer, server);
                        player.sendSystemMessage(Component.literal(
                                ChatFormatting.RED + "" + ChatFormatting.BOLD + "FORCED RETURN: Time expired!"));
                    }
                }
            }
        }
    }

    @Override
    public boolean canUse(Player player) {
        return !isOnCooldown();
    }

    @Override
    public int getResourceCost() {
        return RESOURCE_COST;
    }
}
