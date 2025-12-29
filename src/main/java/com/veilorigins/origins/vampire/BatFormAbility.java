package com.veilorigins.origins.vampire;

import com.veilorigins.api.OriginAbility;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.ChatFormatting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Bat Form Ability - Transform into a bat with flight, invisibility, and decoy
 * bats
 */
public class BatFormAbility extends OriginAbility {
    private static final int RESOURCE_COST = 25; // Costs 25 blood
    private static final int DURATION = 15 * 20; // 15 seconds
    private static final int COOLDOWN = 60 * 20; // 60 seconds

    // Track bat form state per player
    private final Map<UUID, BatFormState> batFormStates = new HashMap<>();

    public BatFormAbility() {
        super("bat_form", COOLDOWN);
    }

    private static class BatFormState {
        long endTime;
        Bat batEntity;
        UUID batUUID;

        BatFormState(long endTime) {
            this.endTime = endTime;
        }
    }

    @Override
    public void onActivate(Player player, Level level) {
        if (level.isClientSide)
            return;

        UUID playerId = player.getUUID();

        // Check if already in bat form - toggle off
        if (batFormStates.containsKey(playerId)) {
            endBatForm(player);
            return;
        }

        // Start bat form
        long endTime = level.getGameTime() + DURATION;
        BatFormState state = new BatFormState(endTime);
        batFormStates.put(playerId, state);

        // Spawn main bat entity at player position
        Bat batEntity = new Bat(EntityType.BAT, level);
        batEntity.setPos(player.getX(), player.getY(), player.getZ());
        batEntity.setInvulnerable(true);
        batEntity.setNoAi(true);
        batEntity.setSilent(true);
        batEntity.setPersistenceRequired();
        batEntity.setResting(false);
        level.addFreshEntity(batEntity);
        state.batEntity = batEntity;
        state.batUUID = batEntity.getUUID();

        // Spawn 5-8 decoy bats around the player for confusion
        int decoyCount = 5 + level.random.nextInt(4);
        for (int i = 0; i < decoyCount; i++) {
            Bat decoyBat = new Bat(EntityType.BAT, level);
            double angle = Math.random() * Math.PI * 2;
            double distance = 3 + Math.random() * 2;
            decoyBat.setPos(
                    player.getX() + Math.cos(angle) * distance,
                    player.getY() + Math.random() * 2 - 1,
                    player.getZ() + Math.sin(angle) * distance);
            decoyBat.setSilent(false);
            level.addFreshEntity(decoyBat);
        }

        // Make player invisible
        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, DURATION + 20, 0, false, false));

        // IMPORTANT: Enable flight and sync to client
        enableFlight(player);

        // Give initial upward boost
        player.setDeltaMovement(player.getDeltaMovement().add(0, 0.5, 0));
        player.hurtMarked = true; // Force velocity sync

        // Particles
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SMOKE,
                    player.getX(), player.getY() + 1, player.getZ(),
                    30, 0.5, 0.5, 0.5, 0.1);
        }

        player.sendSystemMessage(Component.literal(ChatFormatting.DARK_PURPLE + "" + ChatFormatting.BOLD + "Bat Form! "
                + ChatFormatting.RESET + ChatFormatting.GRAY + "You transform into a bat! (15 seconds)"));
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BAT_AMBIENT, SoundSource.PLAYERS, 1.5f, 1.0f);

        startCooldown();
    }

    /**
     * Enable flight and sync abilities to client
     */
    private void enableFlight(Player player) {
        if (player.isCreative() || player.isSpectator())
            return;

        // Set flight abilities
        player.getAbilities().mayfly = true;
        player.getAbilities().flying = true;
        player.getAbilities().setFlyingSpeed(0.08f); // Faster flight speed

        // THIS IS CRITICAL: Sync abilities to client
        player.onUpdateAbilities();

        // For ServerPlayer, we need to send the sync packet
        if (player instanceof ServerPlayer serverPlayer) {
            // Force a full abilities refresh
            serverPlayer.onUpdateAbilities();

            // Additional sync - resend abilities after a tick delay
            // This is handled in tick()
        }
    }

    /**
     * Disable flight and sync abilities to client
     */
    private void disableFlight(Player player) {
        if (player.isCreative() || player.isSpectator())
            return;

        player.getAbilities().mayfly = false;
        player.getAbilities().flying = false;
        player.getAbilities().setFlyingSpeed(0.05f); // Reset to default
        player.onUpdateAbilities();

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.onUpdateAbilities();
        }
    }

    @Override
    public void tick(Player player) {
        if (player.level().isClientSide)
            return;

        UUID playerId = player.getUUID();
        BatFormState state = batFormStates.get(playerId);

        if (state != null) {
            Level level = player.level();
            long currentTime = level.getGameTime();

            // Check if bat form should end
            if (currentTime >= state.endTime) {
                endBatForm(player);
                return;
            }

            // Keep re-enabling flight every tick to ensure it stays on
            // This is a workaround for sync issues
            if (!player.getAbilities().mayfly && !player.isCreative() && !player.isSpectator()) {
                enableFlight(player);
            }

            // Force flying state if player is in midair
            if (!player.onGround() && !player.getAbilities().flying && !player.isCreative()) {
                player.getAbilities().flying = true;
                player.onUpdateAbilities();
            }

            // Keep bat entity synced with player position
            if (state.batEntity != null && state.batEntity.isAlive()) {
                Vec3 playerPos = player.position();
                state.batEntity.setPos(playerPos.x, playerPos.y, playerPos.z);
                state.batEntity.setDeltaMovement(player.getDeltaMovement());
                state.batEntity.setYRot(player.getYRot());
                state.batEntity.setXRot(player.getXRot());
                state.batEntity.setResting(false);
            }

            // Speed boost while in bat form
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 5, 2, false, false));

            // Bat sounds occasionally
            if (currentTime % 40 == 0) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.BAT_AMBIENT, SoundSource.PLAYERS, 0.5f, 1.0f + (float) (Math.random() * 0.2));
            }

            // Warning before ending
            long remaining = state.endTime - currentTime;
            if (remaining == 3 * 20) {
                player.sendSystemMessage(Component.literal(ChatFormatting.YELLOW + "Bat form ending in 3 seconds..."));
            } else if (remaining == 1 * 20) {
                player.sendSystemMessage(Component.literal(
                        ChatFormatting.RED + "" + ChatFormatting.BOLD + "Bat form ending! Find a safe landing!"));
            }
        }
    }

    private void endBatForm(Player player) {
        UUID playerId = player.getUUID();
        BatFormState state = batFormStates.remove(playerId);

        if (state != null) {
            // Remove bat entity
            if (state.batEntity != null && state.batEntity.isAlive()) {
                state.batEntity.discard();
            }
        }

        // Remove invisibility
        player.removeEffect(MobEffects.INVISIBILITY);

        // Remove flight ability
        disableFlight(player);

        // Smoke poof effect
        if (player.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SMOKE,
                    player.getX(), player.getY() + 1, player.getZ(),
                    30, 0.5, 0.5, 0.5, 0.1);
        }

        player.sendSystemMessage(Component.literal(ChatFormatting.GRAY + "You return to your normal form."));
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BAT_DEATH, SoundSource.PLAYERS, 0.8f, 1.0f);
    }

    /**
     * Check if a player is currently in bat form
     */
    public boolean isInBatForm(Player player) {
        return batFormStates.containsKey(player.getUUID());
    }

    @Override
    public boolean canUse(Player player) {
        // Can always use to toggle off if in bat form
        if (batFormStates.containsKey(player.getUUID())) {
            return true;
        }
        return !isOnCooldown();
    }

    @Override
    public int getResourceCost() {
        return RESOURCE_COST;
    }
}
