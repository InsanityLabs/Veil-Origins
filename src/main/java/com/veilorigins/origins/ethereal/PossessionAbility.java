package com.veilorigins.origins.ethereal;

import com.veilorigins.api.OriginAbility;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;

/**
 * Possession - Take control of a mob, seeing through their eyes and controlling
 * their movement
 */
public class PossessionAbility extends OriginAbility {
    private static final int COOLDOWN = 60 * 20; // 60 seconds (reduced from 180)
    private static final int HUNGER_COST = 8;
    private static final int POSSESSION_DURATION = 30 * 20; // 30 seconds
    private static final Identifier SPEED_MODIFIER_ID = Identifier.fromNamespaceAndPath("veil_origins",
            "possession_speed");

    // Track possession state per player
    private final Map<UUID, PossessionState> possessedMobs = new HashMap<>();

    public PossessionAbility() {
        super("possession", COOLDOWN);
    }

    private static class PossessionState {
        Mob target;
        long endTime;
        Vec3 originalPlayerPos;
        boolean wasInvisible;

        PossessionState(Mob target, long endTime, Vec3 playerPos) {
            this.target = target;
            this.endTime = endTime;
            this.originalPlayerPos = playerPos;
            this.wasInvisible = target.isInvisible();
        }
    }

    @Override
    public void onActivate(Player player, Level level) {
        if (level.isClientSide())
            return;

        // Check if already possessing
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
            return lookVec.dot(toMob) > 0.5; // Must be mostly in front
        });

        Mob target = null;
        double closest = Double.MAX_VALUE;

        for (Mob m : mobs) {
            // Skip bosses (Ender Dragon, Wither, Elder Guardian, Warden)
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
            // Start possession
            long endTime = level.getGameTime() + POSSESSION_DURATION;
            possessedMobs.put(player.getUUID(), new PossessionState(target, endTime, player.position()));

            // Make player invisible (they'll observe from mob's perspective)
            player.setInvisible(true);

            // DON'T use riding - it breaks movement. Instead, sync position directly.
            // Mark the mob with a tag so we know it's possessed
            target.addTag("possessed_by:" + player.getUUID().toString());

            // Keep mob AI enabled but clear its current target
            target.setTarget(null);

            // Teleport player to be inside the mob (invisible, so this is fine)
            player.teleportTo(target.getX(), target.getY(), target.getZ());

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
                    Component.literal(
                            ChatFormatting.GRAY + "WASD to move the mob. Press R/V again or wait to end possession."), false);

            player.causeFoodExhaustion(HUNGER_COST);
            startCooldown();
        } else {
            player.displayClientMessage(
                    Component.literal(ChatFormatting.RED + "No possessable mob found! Look at a mob to possess it."), false);
        }
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
            if (currentTime >= state.endTime || !target.isAlive()) {
                endPossession(player, level);
                return;
            }

            // Keep player synced with mob position (player is invisible and inside mob)
            player.teleportTo(target.getX(), target.getY(), target.getZ());

            // Transfer player input to mob movement
            // Use player input to control the mob's navigation/velocity
            float forward = player.zza; // W/S key input (-1 to 1)
            float strafe = player.xxa; // A/D key input (-1 to 1)

            if (forward != 0 || strafe != 0) {
                // Calculate movement direction based on where player is looking
                float yaw = player.getYRot();
                double radYaw = Math.toRadians(yaw);

                // Forward/backward movement in the direction player faces
                double moveX = -Math.sin(radYaw) * forward + -Math.cos(radYaw) * strafe;
                double moveZ = Math.cos(radYaw) * forward + -Math.sin(radYaw) * strafe;

                float speed = (float) target
                        .getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED) * 3.5f;

                // Apply movement to the mob
                target.setDeltaMovement(
                        moveX * speed,
                        target.getDeltaMovement().y, // Keep vertical momentum
                        moveZ * speed);

                // Make mob face the movement direction for visual coherence
                target.setYRot(yaw);
                target.setYHeadRot(yaw);
                target.yRotO = yaw;

                // Also use navigation for pathfinding around obstacles
                Vec3 targetPos = target.position().add(moveX * 3, 0, moveZ * 3);
                target.getNavigation().moveTo(targetPos.x, targetPos.y, targetPos.z, 1.5);
            }

            // Clear mob's attack target to prevent it attacking while possessed
            if (target.getTarget() != null) {
                target.setTarget(null);
            }

            // Soul trail particles
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

            // Remove possession tag from mob
            if (target.isAlive()) {
                target.removeTag("possessed_by:" + player.getUUID().toString());
            }

            // Restore player visibility
            player.setInvisible(false);

            // Teleport player next to mob (not inside it)
            Vec3 exitPos = target.position().add(
                    level.random.nextDouble() * 2 - 1,
                    0.5,
                    level.random.nextDouble() * 2 - 1);
            player.teleportTo(exitPos.x, exitPos.y, exitPos.z);

            // Visual feedback
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.5f, 1.5f);

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.REVERSE_PORTAL,
                        player.getX(), player.getY() + 1, player.getZ(),
                        20, 0.5, 0.5, 0.5, 0.1);
            }

            player.displayClientMessage(Component.literal(ChatFormatting.GRAY + "Possession ended."), false);
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
}
