package com.veilorigins.origins.ethereal;

import com.veilorigins.api.OriginAbility;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.GameType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PhaseShiftAbility extends OriginAbility {
    private static final int COOLDOWN = 10 * 20;
    private static final int DURATION = 8 * 20; // 8s
    private static final int HUNGER_COST = 5;

    // Track original game mode
    private final Map<UUID, GameType> originalModes = new HashMap<>();
    private final Map<UUID, Integer> activePhases = new HashMap<>();

    public PhaseShiftAbility() {
        super("phase_shift", COOLDOWN);
    }

    @Override
    public void onActivate(Player player, Level level) {
        if (player.isSpectator())
            return;

        if (level instanceof net.minecraft.server.level.ServerLevel sl) {
            net.minecraft.server.level.ServerPlayer sp = (net.minecraft.server.level.ServerPlayer) player;
            originalModes.put(player.getUUID(), sp.gameMode.getGameModeForPlayer());
            sp.setGameMode(GameType.SPECTATOR);
            activePhases.put(player.getUUID(), DURATION);
            player.causeFoodExhaustion(HUNGER_COST);
            startCooldown();
        }
    }

    @Override
    public void tick(Player player) {
        if (player.level().isClientSide)
            return;

        UUID id = player.getUUID();
        if (activePhases.containsKey(id)) {
            int ticks = activePhases.get(id);
            if (ticks > 0) {
                activePhases.put(id, ticks - 1);
            } else {
                activePhases.remove(id);
                // Revert
                if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
                    GameType original = originalModes.getOrDefault(id, GameType.SURVIVAL);
                    sp.setGameMode(original);
                    originalModes.remove(id);

                    // Prevent suffocation on revert: tp to safe spot?
                    if (!player.level().noCollision(player.getBoundingBox())) {
                        player.randomTeleport(player.getX(), player.getY() + 1, player.getZ(), true); // Try to unstuck
                        if (!player.level().noCollision(player.getBoundingBox())) {
                            player.teleportTo(player.getX(), player.getY() + 5, player.getZ()); // Force up
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean canUse(Player player) {
        return !isOnCooldown() && player.getFoodData().getFoodLevel() >= HUNGER_COST;
    }

    @Override
    public int getResourceCost() {
        return 0;
    }

    public boolean isActive(Player player) {
        return activePhases.containsKey(player.getUUID());
    }
}
