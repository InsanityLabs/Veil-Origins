package com.veilorigins.origins.ethereal;

import com.veilorigins.VeilOrigins;
import com.veilorigins.api.OriginAbility;
import com.veilorigins.network.ModPackets;
import com.veilorigins.network.packet.SyncPossessionPacket;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;

/**
 * Possession - Take control of a mob. Player moves normally with WASD,
 * and the possessed mob follows the player's position. Player is invisible,
 * mob is visible but controlled by player movement.
 */
public class PossessionAbility extends OriginAbility {
    private static final int COOLDOWN = 60 * 20; // 60 seconds
    private static final int HUNGER_COST = 8;
    private static final int POSSESSION_DURATION = 30 * 20; // 30 seconds

    // Track possession state per player (server-side)
    private static final Map<UUID, PossessionState> possessedMobs = new HashMap<>();

    public PossessionAbility() {
        super("possession", COOLDOWN);
    }

    private static class PossessionState {
        Mob target;
        long endTime;
        Vec3 originalMobPos;
        boolean aiWasEnabled;

        PossessionState(Mob target, long endTime) {
            this.target = target;
            this.endTime = endTime;
            this.originalMobPos = target.position();
            this.aiWasEnabled = !target.isNoAi();
        }
    }
    
    /**
     * Check if a player is currently possessing a mob.
     */
    public static boolean isPossessing(Player player) {
        return possessedMobs.containsKey(player.getUUID());
    }
    
    /**
     * Get the mob being possessed by a player, or null if not possessing.
     */
    public static Mob getPossessedMob(Player player) {
        PossessionState state = possessedMobs.get(player.getUUID());
        return state != null ? state.target : null;
    }

    @Override
    public void onActivate(Player player, Level level) {
        if (level.isClientSide())
            return;

        // Check if already possessing - toggle off
        if (possessedMobs.containsKey(player.getUUID())) {
            endPossession(player, level);
            return;
        }

        // Find target - look in direction player is facing
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        AABB search = player.getBoundingBox().expandTowards(lookVec.scale(10)).inflate(2);

        List<Mob> mobs = level.getEntitiesOfClass(Mob.class, search, mob -> {
            // Check if mob is in front of player
            Vec3 toMob = mob.position().subtract(eyePos).normalize();
            return lookVec.dot(toMob) > 0.5 && mob.isAlive();
        });

        Mob target = null;
        double closest = Double.MAX_VALUE;

        for (Mob m : mobs) {
            // Skip bosses
            String mobName = m.getType().toString().toLowerCase();
            if (mobName.contains("dragon") || mobName.contains("wither") ||
                    mobName.contains("elder_guardian") || mobName.contains("warden")) {
                continue;
            }

            double d = player.distanceToSqr(m);
            if (d < closest) {
                closest = d;
                target = m;
            }
        }

        if (target != null) {
            startPossession(player, target, level);
        } else {
            player.displayClientMessage(
                    Component.literal(ChatFormatting.RED + "No possessable mob found! Look at a mob to possess it."), false);
        }
    }
    
    private void startPossession(Player player, Mob target, Level level) {
        long endTime = level.getGameTime() + POSSESSION_DURATION;
        PossessionState state = new PossessionState(target, endTime);
        possessedMobs.put(player.getUUID(), state);

        // CRITICAL: Disable mob AI completely
        target.setNoAi(true);
        
        // Clear any existing navigation/targets
        target.getNavigation().stop();
        target.setTarget(null);
        
        // Stop all movement
        target.setDeltaMovement(Vec3.ZERO);

        // Make player invisible using effect (syncs to client properly)
        if (player instanceof ServerPlayer serverPlayer) {
            // Add long invisibility effect
            serverPlayer.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, POSSESSION_DURATION + 100, 0, false, false, false));
            
            // Sync possession state to client
            ModPackets.sendToPlayer(serverPlayer, new SyncPossessionPacket(true, target.getId()));
        }

        // Tag the mob
        target.addTag("possessed_by:" + player.getUUID().toString());

        // Teleport MOB to PLAYER (not the other way around!)
        target.teleportTo(player.getX(), player.getY(), player.getZ());
        target.setYRot(player.getYRot());
        target.setYHeadRot(player.getYRot());
        target.setYBodyRot(player.getYRot());

        // Visual/audio feedback
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PHANTOM_AMBIENT, SoundSource.PLAYERS, 1.0f, 0.5f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.5f, 2.0f);

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SOUL,
                    player.getX(), player.getY() + 1, player.getZ(),
                    30, 0.5, 0.5, 0.5, 0.1);
        }

        player.displayClientMessage(
                Component.literal(ChatFormatting.DARK_PURPLE + "" + ChatFormatting.BOLD + "Possessing "
                        + target.getName().getString() + "! " + ChatFormatting.RESET + ChatFormatting.GRAY
                        + "(30 seconds)"), false);
        player.displayClientMessage(
                Component.literal(ChatFormatting.GRAY + "Move normally with WASD - the mob follows you!"), false);

        player.causeFoodExhaustion(HUNGER_COST);
        startCooldown();
        
        VeilOrigins.LOGGER.info("Player {} started possessing mob {}", player.getName().getString(), target.getName().getString());
    }

    @Override
    public void tick(Player player) {
        Level level = player.level();
        if (level.isClientSide())
            return;

        UUID id = player.getUUID();
        PossessionState state = possessedMobs.get(id);

        if (state != null) {
            long currentTime = level.getGameTime();
            Mob target = state.target;

            // Check if possession should end
            if (currentTime >= state.endTime || !target.isAlive() || target.isRemoved()) {
                endPossession(player, level);
                return;
            }

            // SYNC MOB TO PLAYER POSITION (not the other way around!)
            // The mob follows the player's movement
            target.teleportTo(player.getX(), player.getY(), player.getZ());
            
            // Sync rotation too
            target.setYRot(player.getYRot());
            target.setYHeadRot(player.getYRot());
            target.yRotO = player.getYRot();
            target.setYBodyRot(player.getYRot());
            target.setXRot(player.getXRot());
            
            // Copy movement for animation
            target.setDeltaMovement(player.getDeltaMovement());
            
            // Force network sync
            target.hurtMarked = true;

            // Ensure AI stays disabled
            if (!target.isNoAi()) {
                target.setNoAi(true);
            }

            // Soul trail particles occasionally
            if (currentTime % 10 == 0 && level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SOUL,
                        target.getX(), target.getY() + target.getBbHeight() + 0.5, target.getZ(),
                        2, 0.2, 0.2, 0.2, 0.02);
            }

            // Warning when time is running out
            long remaining = state.endTime - currentTime;
            if (remaining == 5 * 20) {
                player.displayClientMessage(
                        Component.literal(ChatFormatting.YELLOW + "Possession ending in 5 seconds..."), false);
            }
        }
    }

    private void endPossession(Player player, Level level) {
        UUID id = player.getUUID();
        PossessionState state = possessedMobs.remove(id);

        if (state != null) {
            Mob target = state.target;

            // Remove possession tag and restore AI
            if (target.isAlive() && !target.isRemoved()) {
                target.removeTag("possessed_by:" + player.getUUID().toString());
                
                // Stop any residual movement
                target.setDeltaMovement(Vec3.ZERO);
                target.getNavigation().stop();
                
                // Move mob slightly away from player so they're not overlapping
                Vec3 offset = player.getLookAngle().scale(1.5);
                target.teleportTo(player.getX() + offset.x, player.getY(), player.getZ() + offset.z);
                
                // Restore AI if it was enabled before
                if (state.aiWasEnabled) {
                    target.setNoAi(false);
                    
                    // Force the mob to reinitialize its brain/goals
                    // This makes the mob "wake up" and start behaving normally again
                    target.setTarget(null);
                    target.setLastHurtByMob(null);
                    target.setAggressive(false);
                    
                    // Clear and restart navigation
                    target.getNavigation().stop();
                    target.getNavigation().recomputePath();
                    
                    // Give the mob a small random movement to "wake it up"
                    double randomX = (level.random.nextDouble() - 0.5) * 0.1;
                    double randomZ = (level.random.nextDouble() - 0.5) * 0.1;
                    target.setDeltaMovement(randomX, 0, randomZ);
                    target.hurtMarked = true;
                    
                    // Force entity update
                    target.setOnGround(target.onGround());
                }
            }

            // Remove invisibility and sync to client
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.removeEffect(MobEffects.INVISIBILITY);
                ModPackets.sendToPlayer(serverPlayer, new SyncPossessionPacket(false, -1));
            }

            // Visual feedback
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.5f, 1.5f);

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                        player.getX(), player.getY() + 1, player.getZ(),
                        20, 0.5, 0.5, 0.5, 0.1);
            }

            player.displayClientMessage(Component.literal(ChatFormatting.GRAY + "Possession ended."), false);
            
            VeilOrigins.LOGGER.info("Player {} ended possession", player.getName().getString());
        }
    }

    @Override
    public boolean canUse(Player player) {
        // Can always use if possessing (to end possession early)
        if (possessedMobs.containsKey(player.getUUID())) {
            return true;
        }
        return !isOnCooldown() && player.getFoodData().getFoodLevel() >= HUNGER_COST;
    }

    @Override
    public int getResourceCost() {
        return HUNGER_COST;
    }
    
    /**
     * Clean up when player disconnects.
     */
    public static void onPlayerDisconnect(UUID playerId) {
        PossessionState state = possessedMobs.remove(playerId);
        
        if (state != null && state.target.isAlive()) {
            Mob target = state.target;
            target.removeTag("possessed_by:" + playerId.toString());
            
            if (state.aiWasEnabled) {
                target.setNoAi(false);
                
                // Restore mob brain
                target.setTarget(null);
                target.setLastHurtByMob(null);
                target.setAggressive(false);
                target.getNavigation().stop();
                target.getNavigation().recomputePath();
                target.hurtMarked = true;
            }
        }
    }
}
