package com.veilorigins.origins.crystalline;

import com.veilorigins.api.OriginAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Ore Resonance - Sense and highlight ores through walls using glowing markers
 */
public class OreResonanceAbility extends OriginAbility {
    private static final int COOLDOWN = 15 * 20; // 15 seconds (reduced from 60)
    private static final int HUNGER_COST = 3;
    private static final int RADIUS = 20;
    private static final int GLOW_DURATION = 5 * 20; // 5 seconds of glowing

    // Track glowing ore markers per player
    private final Map<UUID, List<GlowMarker>> activeMarkers = new HashMap<>();

    public OreResonanceAbility() {
        super("ore_resonance", COOLDOWN);
    }

    private static class GlowMarker {
        BlockPos orePos;
        long endTime;
        String oreType;

        GlowMarker(BlockPos pos, long endTime, String type) {
            this.orePos = pos;
            this.endTime = endTime;
            this.oreType = type;
        }
    }

    @Override
    public void onActivate(Player player, Level level) {
        if (level.isClientSide)
            return;

        BlockPos center = player.blockPosition();
        List<GlowMarker> markers = new ArrayList<>();
        long endTime = level.getGameTime() + GLOW_DURATION;

        // Scan for ores in radius
        int oresFound = 0;
        Map<String, Integer> oreCounts = new HashMap<>();

        for (int x = -RADIUS; x <= RADIUS; x++) {
            for (int y = -RADIUS; y <= RADIUS; y++) {
                for (int z = -RADIUS; z <= RADIUS; z++) {
                    // Spherical check
                    if (x * x + y * y + z * z > RADIUS * RADIUS)
                        continue;

                    BlockPos pos = center.offset(x, y, z);
                    BlockState state = level.getBlockState(pos);

                    if (isOre(state)) {
                        String oreName = getOreName(state);
                        markers.add(new GlowMarker(pos.immutable(), endTime, oreName));
                        oreCounts.merge(oreName, 1, Integer::sum);
                        oresFound++;
                    }
                }
            }
        }

        // Store markers
        activeMarkers.put(player.getUUID(), markers);

        // Sound and visual feedback
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 2.0f, 0.5f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.5f, 2.0f);

        // Pulse effect from player
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                    player.getX(), player.getY() + 1, player.getZ(),
                    50, 1.0, 0.5, 1.0, 0.5);
        }

        // Message with ore breakdown
        if (oresFound > 0) {
            StringBuilder message = new StringBuilder("§d§lOre Resonance: §r§7Found ");
            message.append(oresFound).append(" ores! §8(");
            oreCounts.forEach((ore, count) -> message.append(getOreColor(ore)).append(ore).append("§8: §f")
                    .append(count).append(" "));
            message.append("§8)");
            player.sendSystemMessage(Component.literal(message.toString()));
        } else {
            player.sendSystemMessage(Component.literal("§7No ores detected nearby..."));
        }

        player.causeFoodExhaustion(HUNGER_COST);
        startCooldown();
    }

    @Override
    public void tick(Player player) {
        Level level = player.level();
        if (level.isClientSide)
            return;

        UUID id = player.getUUID();
        List<GlowMarker> markers = activeMarkers.get(id);

        if (markers != null && !markers.isEmpty()) {
            long currentTime = level.getGameTime();
            ServerLevel serverLevel = (ServerLevel) level;

            // Show glowing particles at ore locations
            if (currentTime % 5 == 0) { // Every 5 ticks (4x per second)
                for (GlowMarker marker : markers) {
                    if (currentTime < marker.endTime) {
                        // Different colored particles based on ore type
                        serverLevel.sendParticles(getParticleForOre(marker.oreType),
                                marker.orePos.getX() + 0.5,
                                marker.orePos.getY() + 0.5,
                                marker.orePos.getZ() + 0.5,
                                2, 0.2, 0.2, 0.2, 0.01);
                    }
                }
            }

            // Remove expired markers
            markers.removeIf(m -> currentTime >= m.endTime);

            if (markers.isEmpty()) {
                activeMarkers.remove(id);
                player.sendSystemMessage(Component.literal("§7Ore Resonance faded."));
            }
        }
    }

    private boolean isOre(BlockState state) {
        String name = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
        return name.contains("_ore") || name.equals("ancient_debris");
    }

    private String getOreName(BlockState state) {
        String name = BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath();
        // Clean up name for display
        return name.replace("_ore", "")
                .replace("deepslate_", "")
                .replace("_", " ");
    }

    private String getOreColor(String oreName) {
        return switch (oreName) {
            case "diamond" -> "§b";
            case "emerald" -> "§a";
            case "gold", "nether gold" -> "§6";
            case "iron" -> "§f";
            case "copper" -> "§6";
            case "coal" -> "§8";
            case "redstone" -> "§c";
            case "lapis" -> "§9";
            case "ancient debris" -> "§4";
            case "nether quartz" -> "§f";
            default -> "§7";
        };
    }

    private net.minecraft.core.particles.ParticleOptions getParticleForOre(String oreName) {
        return switch (oreName) {
            case "diamond" -> ParticleTypes.END_ROD;
            case "emerald" -> ParticleTypes.HAPPY_VILLAGER;
            case "gold", "nether gold", "copper" -> ParticleTypes.FLAME;
            case "redstone" -> ParticleTypes.DUST_PLUME;
            case "lapis" -> ParticleTypes.ENCHANT;
            case "ancient debris" -> ParticleTypes.SOUL_FIRE_FLAME;
            default -> ParticleTypes.END_ROD;
        };
    }

    @Override
    public boolean canUse(Player player) {
        return !isOnCooldown() && player.getFoodData().getFoodLevel() >= HUNGER_COST;
    }

    @Override
    public int getResourceCost() {
        return HUNGER_COST;
    }
}
