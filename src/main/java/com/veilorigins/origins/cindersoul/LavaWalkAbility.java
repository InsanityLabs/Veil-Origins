package com.veilorigins.origins.cindersoul;

import com.veilorigins.api.OriginAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;

/**
 * Lava Walk Ability - Works like Frost Walker but for lava!
 * When activated, converts lava to temporary obsidian under the player's feet
 * for 20 seconds.
 */
public class LavaWalkAbility extends OriginAbility {
    private static final int COOLDOWN = 60 * 20; // 60 seconds cooldown
    private static final int ABILITY_DURATION = 20 * 20; // 20 seconds of walking
    private static final int OBSIDIAN_REVERT_TIME = 5 * 20; // Obsidian reverts after 5 seconds
    private static final int WALK_RADIUS = 2; // Radius around player to convert

    // Track active lava walk per player
    private final Map<UUID, Long> activeUntil = new HashMap<>();

    // Track temporary obsidian blocks to revert to lava
    private final Map<UUID, List<RevertEntry>> revertMap = new HashMap<>();

    public LavaWalkAbility() {
        super("lava_walk", COOLDOWN);
    }

    private static class RevertEntry {
        BlockPos pos;
        long revertTime;

        public RevertEntry(BlockPos pos, long revertTime) {
            this.pos = pos;
            this.revertTime = revertTime;
        }
    }

    @Override
    public void onActivate(Player player, Level level) {
        if (level.isClientSide)
            return;

        UUID id = player.getUUID();
        long endTime = level.getGameTime() + ABILITY_DURATION;
        activeUntil.put(id, endTime);

        // Play activation sound
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 1.0f, 0.5f);

        // Visual effect
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FLAME,
                    player.getX(), player.getY(), player.getZ(),
                    30, 0.5, 0.3, 0.5, 0.1);
        }

        player.sendSystemMessage(Component.literal(ChatFormatting.GOLD + "" + ChatFormatting.BOLD
                + "Lava Walk activated! " + ChatFormatting.RESET + ChatFormatting.GRAY + "(20 seconds)"));

        // Immediately convert lava under player
        convertLavaAroundPlayer(player, level);

        startCooldown();
    }

    @Override
    public void tick(Player player) {
        Level level = player.level();
        if (level.isClientSide)
            return;

        UUID id = player.getUUID();
        long currentTime = level.getGameTime();

        // Check if ability is active
        Long endTime = activeUntil.get(id);
        if (endTime != null && currentTime < endTime) {
            // Ability is active - convert lava as player walks
            convertLavaAroundPlayer(player, level);

            // Warning when time is running out
            long remaining = endTime - currentTime;
            if (remaining == 5 * 20) { // 5 seconds left
                player.sendSystemMessage(Component.literal(ChatFormatting.YELLOW + "Lava Walk ending in 5 seconds..."));
            } else if (remaining == 2 * 20) { // 2 seconds left
                player.sendSystemMessage(Component.literal(
                        ChatFormatting.RED + "" + ChatFormatting.BOLD + "Lava Walk ending soon! Get to safety!"));
            }

            // Particle trail while active
            if (currentTime % 5 == 0 && level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.FLAME,
                        player.getX(), player.getY() + 0.1, player.getZ(),
                        3, 0.2, 0.1, 0.2, 0.02);
            }
        } else if (endTime != null) {
            // Ability just ended
            activeUntil.remove(id);
            player.sendSystemMessage(Component.literal(ChatFormatting.GRAY + "Lava Walk ended."));
        }

        // Handle reverting obsidian back to lava
        List<RevertEntry> entries = revertMap.get(id);
        if (entries != null && !entries.isEmpty()) {
            Iterator<RevertEntry> it = entries.iterator();
            while (it.hasNext()) {
                RevertEntry entry = it.next();
                if (currentTime >= entry.revertTime) {
                    // Check if it's still our temporary obsidian
                    if (level.getBlockState(entry.pos).is(Blocks.OBSIDIAN)) {
                        // Make sure player isn't standing on it
                        BlockPos playerBlock = player.blockPosition();
                        if (!entry.pos.closerThan(playerBlock, 2)) {
                            level.setBlockAndUpdate(entry.pos, Blocks.LAVA.defaultBlockState());

                            // Particles when reverting
                            if (level instanceof ServerLevel serverLevel) {
                                serverLevel.sendParticles(ParticleTypes.LAVA,
                                        entry.pos.getX() + 0.5, entry.pos.getY() + 1, entry.pos.getZ() + 0.5,
                                        5, 0.3, 0.1, 0.3, 0.01);
                            }
                        } else {
                            // Player is nearby, delay revert
                            entry.revertTime = currentTime + 20; // Delay 1 second
                            continue;
                        }
                    }
                    it.remove();
                }
            }

            if (entries.isEmpty()) {
                revertMap.remove(id);
            }
        }
    }

    /**
     * Convert lava blocks around the player to temporary obsidian (Frost Walker
     * style)
     */
    private void convertLavaAroundPlayer(Player player, Level level) {
        BlockPos playerPos = player.blockPosition();
        UUID id = player.getUUID();
        long currentTime = level.getGameTime();

        // Check blocks around and below player
        for (int x = -WALK_RADIUS; x <= WALK_RADIUS; x++) {
            for (int z = -WALK_RADIUS; z <= WALK_RADIUS; z++) {
                // Check distance (circular pattern)
                if (x * x + z * z > WALK_RADIUS * WALK_RADIUS + 1)
                    continue;

                // Check block at feet level and one below
                for (int y = -1; y <= 0; y++) {
                    BlockPos checkPos = playerPos.offset(x, y, z);

                    // Check if it's source lava
                    if (level.getBlockState(checkPos).is(Blocks.LAVA) &&
                            level.getFluidState(checkPos).isSource()) {

                        // Convert to obsidian
                        level.setBlockAndUpdate(checkPos, Blocks.OBSIDIAN.defaultBlockState());

                        // Play sound occasionally
                        if (level.random.nextFloat() < 0.3f) {
                            level.playSound(null, checkPos, SoundEvents.LAVA_EXTINGUISH,
                                    SoundSource.BLOCKS, 0.5f, 1.2f);
                        }

                        // Schedule revert
                        revertMap.computeIfAbsent(id, k -> new ArrayList<>())
                                .add(new RevertEntry(checkPos.immutable(), currentTime + OBSIDIAN_REVERT_TIME));

                        // Particles
                        if (level instanceof ServerLevel serverLevel) {
                            serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE,
                                    checkPos.getX() + 0.5, checkPos.getY() + 1, checkPos.getZ() + 0.5,
                                    3, 0.3, 0.1, 0.3, 0.02);
                        }
                    }
                }
            }
        }
    }

    /**
     * Check if the ability is currently active for a player
     */
    public boolean isActive(Player player) {
        Long endTime = activeUntil.get(player.getUUID());
        return endTime != null && player.level().getGameTime() < endTime;
    }

    @Override
    public boolean canUse(Player player) {
        return !isOnCooldown();
    }

    @Override
    public int getResourceCost() {
        return 0;
    }
}
