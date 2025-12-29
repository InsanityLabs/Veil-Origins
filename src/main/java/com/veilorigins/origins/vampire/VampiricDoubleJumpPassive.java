package com.veilorigins.origins.vampire;

import com.veilorigins.api.OriginPassive;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.ChatFormatting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Vampiric Double Jump Passive - Allows vampires to perform a damaging double
 * jump.
 * When sprinting and pressing jump twice in the air, the vampire leaps forward
 * and damages any entities in their path.
 */
public class VampiricDoubleJumpPassive extends OriginPassive {

    // Track jump states per player
    private static final Map<UUID, JumpState> jumpStates = new HashMap<>();

    // Configuration
    private final float damageAmount;
    private final float jumpBoostStrength;
    private final float damageRadius;
    private final int cooldownTicks;
    private static final float BLOOD_COST = 5.0f; // Blood cost per leap

    /**
     * Creates a Vampiric Double Jump passive.
     * 
     * @param damageAmount Damage dealt to entities hit during double jump
     */
    public VampiricDoubleJumpPassive(float damageAmount) {
        super("vampiric_double_jump");
        this.damageAmount = damageAmount;
        this.jumpBoostStrength = damageAmount > 4.0f ? 0.8f : 0.6f; // Full vamp gets stronger jump
        this.damageRadius = 2.0f;
        this.cooldownTicks = damageAmount > 4.0f ? 30 : 40; // 1.5s or 2s cooldown
    }

    @Override
    public void onTick(Player player) {
        if (player.level().isClientSide())
            return;

        UUID playerId = player.getUUID();
        JumpState state = jumpStates.computeIfAbsent(playerId, k -> new JumpState());

        // Decrease cooldown
        if (state.cooldown > 0) {
            state.cooldown--;
        }

        // Track if player was on ground
        boolean isOnGround = player.onGround();

        // Reset jump capability when player lands
        if (isOnGround) {
            state.canDoubleJump = true;
            state.hasDoubleJumped = false;
            state.hasFallDamageImmunity = false; // Reset fall damage immunity on landing
        }

        // Track sprinting state when on ground
        if (isOnGround && player.isSprinting()) {
            state.wasSprintingOnGround = true;
        } else if (isOnGround) {
            state.wasSprintingOnGround = false;
        }
    }

    /**
     * Called from the network packet when the player requests a double jump.
     * This is the server-side handler for the jump request from the client.
     */
    public void requestDoubleJump(Player player) {
        if (player.level().isClientSide())
            return;

        UUID playerId = player.getUUID();
        JumpState state = jumpStates.computeIfAbsent(playerId, k -> new JumpState());

        // Validate the double jump
        if (!canPerformDoubleJump(player, state)) {
            return;
        }

        // Perform the double jump
        performDoubleJump(player, state);
    }

    /**
     * Checks if the player can perform a double jump.
     */
    private boolean canPerformDoubleJump(Player player, JumpState state) {
        // Must be in the air
        if (player.onGround())
            return false;

        // Must not have already double jumped
        if (state.hasDoubleJumped)
            return false;

        // Must be able to double jump
        if (!state.canDoubleJump)
            return false;

        // Must be off cooldown
        if (state.cooldown > 0)
            return false;

        // Must have been sprinting (or is currently sprinting)
        if (!player.isSprinting() && !state.wasSprintingOnGround)
            return false;

        // Must have enough blood
        com.veilorigins.data.OriginData.PlayerOriginData data = 
            player.getData(com.veilorigins.data.OriginData.PLAYER_ORIGIN);
        if (data.getResourceBar() < BLOOD_COST) {
            player.displayClientMessage(
                net.minecraft.network.chat.Component.literal(
                    ChatFormatting.RED + "Not enough blood for leap!"),
                true);
            return false;
        }

        return true;
    }

    /**
     * Performs the vampiric double jump - boosts the player and damages nearby
     * entities.
     */
    private void performDoubleJump(Player player, JumpState state) {
        // Consume blood for the leap
        com.veilorigins.data.OriginData.PlayerOriginData data = 
            player.getData(com.veilorigins.data.OriginData.PLAYER_ORIGIN);
        data.consumeResource(BLOOD_COST);
        
        state.hasDoubleJumped = true;
        state.canDoubleJump = false;
        state.cooldown = cooldownTicks;
        state.hasFallDamageImmunity = true; // Grant fall damage immunity after double jump

        // Get player's look direction and pitch
        Vec3 lookDir = player.getLookAngle();
        float pitch = player.getXRot(); // Negative = looking up, Positive = looking down
        Vec3 currentVelocity = player.getDeltaMovement();

        // Calculate horizontal direction (ignoring Y component)
        Vec3 horizontalDir = new Vec3(lookDir.x, 0, lookDir.z).normalize();

        // Base strength for the leap
        double totalStrength = jumpBoostStrength * 1.5; // Slightly stronger overall

        // Calculate upward and forward components based on look angle
        double upwardBoost;
        double forwardBoost;

        if (pitch < -45) {
            // Looking up (pitch < -45 degrees) - more vertical, less forward
            // Launch angle ~60-70 degrees upward
            upwardBoost = totalStrength * 0.85; // High upward component
            forwardBoost = totalStrength * 0.5; // Reduced forward component
        } else {
            // Looking forward or down - ~25 degrees launch angle (more horizontal)
            // sin(25°) ≈ 0.42, cos(25°) ≈ 0.91
            upwardBoost = totalStrength * 0.42; // Lower upward component
            forwardBoost = totalStrength * 0.91; // High forward component
        }

        Vec3 newVelocity = new Vec3(
                currentVelocity.x * 0.2 + horizontalDir.x * forwardBoost,
                upwardBoost,
                currentVelocity.z * 0.2 + horizontalDir.z * forwardBoost);

        player.setDeltaMovement(newVelocity);
        player.hurtMarked = true; // Sync velocity to client

        // Play vampiric sound effect
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BAT_TAKEOFF, SoundSource.PLAYERS, 1.0f, 0.8f);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PHANTOM_FLAP, SoundSource.PLAYERS, 0.5f, 1.2f);

        // Spawn particles
        spawnDoubleJumpParticles(player);

        // Damage entities in path
        damageNearbyEntities(player);

        player.displayClientMessage(
                net.minecraft.network.chat.Component.literal(ChatFormatting.DARK_RED + "\u26A1 Vampiric Leap! \u26A1"),
                true);
    }

    /**
     * Spawns visual particles for the double jump effect.
     */
    private void spawnDoubleJumpParticles(Player player) {
        if (player.level() instanceof ServerLevel serverLevel) {
            Vec3 pos = player.position();

            // Spawn dark crimson/red particles in a burst
            for (int i = 0; i < 15; i++) {
                double offsetX = (player.getRandom().nextDouble() - 0.5) * 1.5;
                double offsetY = player.getRandom().nextDouble() * 0.5;
                double offsetZ = (player.getRandom().nextDouble() - 0.5) * 1.5;

                serverLevel.sendParticles(ParticleTypes.CRIMSON_SPORE,
                        pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ,
                        1, 0, -0.2, 0, 0.1);
            }

            // Smoke ring effect
            for (int i = 0; i < 10; i++) {
                double angle = (Math.PI * 2.0 / 10) * i;
                double radius = 0.8;
                double particleX = pos.x + Math.cos(angle) * radius;
                double particleZ = pos.z + Math.sin(angle) * radius;

                serverLevel.sendParticles(ParticleTypes.SMOKE,
                        particleX, pos.y + 0.1, particleZ,
                        2, 0, 0.1, 0, 0.02);
            }
        }
    }

    /**
     * Damages entities near the player during the double jump.
     */
    private void damageNearbyEntities(Player player) {
        Vec3 playerPos = player.position();

        // Create a bounding box around the player
        AABB damageBox = new AABB(
                playerPos.x - damageRadius,
                playerPos.y - 0.5,
                playerPos.z - damageRadius,
                playerPos.x + damageRadius,
                playerPos.y + 2.0,
                playerPos.z + damageRadius);

        // Get all living entities in the area
        List<LivingEntity> entities = player.level().getEntitiesOfClass(
                LivingEntity.class,
                damageBox,
                entity -> entity != player && entity.isAlive());

        // Damage and knockback each entity
        for (LivingEntity entity : entities) {
            // Deal damage
            entity.hurt(player.damageSources().playerAttack(player), damageAmount);

            // Apply knockback away from player
            Vec3 knockbackDir = entity.position().subtract(playerPos).normalize();
            entity.setDeltaMovement(
                    knockbackDir.x * 0.5,
                    0.3,
                    knockbackDir.z * 0.5);
            entity.hurtMarked = true;

            // Spawn hit particles
            if (player.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.CRIT,
                        entity.getX(), entity.getY() + entity.getBbHeight() / 2, entity.getZ(),
                        5, 0.2, 0.2, 0.2, 0.1);
            }
        }

        // Notify if entities were hit
        if (!entities.isEmpty()) {
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal(
                            ChatFormatting.RED + "\u2726 Hit " + entities.size() + " "
                                    + (entities.size() == 1 ? "entity" : "entities") + "!"),
                    true);
        }
    }

    /**
     * Returns the cooldown remaining in ticks for the specified player.
     */
    public int getCooldownRemaining(Player player) {
        JumpState state = jumpStates.get(player.getUUID());
        return state != null ? state.cooldown : 0;
    }

    /**
     * Returns whether the player can currently double jump.
     */
    public boolean canDoubleJump(Player player) {
        JumpState state = jumpStates.get(player.getUUID());
        if (state == null)
            return true;
        return canPerformDoubleJump(player, state);
    }

    /**
     * Returns whether the player has fall damage immunity from a recent double
     * jump.
     * This immunity lasts until the player lands.
     */
    public boolean hasDoubleJumpImmunity(Player player) {
        JumpState state = jumpStates.get(player.getUUID());
        return state != null && state.hasFallDamageImmunity;
    }

    @Override
    public void onEquip(Player player) {
        player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                ChatFormatting.DARK_RED + "\u26A1 Vampiric Leap: " + ChatFormatting.GRAY
                        + "Sprint and double-tap jump while airborne to perform a damaging leap!"), false);
    }

    @Override
    public void onRemove(Player player) {
        jumpStates.remove(player.getUUID());
    }

    /**
     * Internal class to track jump state per player.
     */
    private static class JumpState {
        boolean canDoubleJump = true;
        boolean hasDoubleJumped = false;
        boolean wasSprintingOnGround = false;
        boolean hasFallDamageImmunity = false;
        int cooldown = 0;
    }
}
