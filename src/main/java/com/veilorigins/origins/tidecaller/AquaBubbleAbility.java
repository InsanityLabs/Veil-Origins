package com.veilorigins.origins.tidecaller;

import com.veilorigins.api.OriginAbility;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;

/**
 * Aqua Bubble - Dual mode ability:
 * - In water: Creates a shared air bubble (water breathing for you AND nearby
 * players)
 * - On land: Fires a water ball projectile that damages enemies
 */
public class AquaBubbleAbility extends OriginAbility {
    private static final int COOLDOWN = 10 * 20; // 10 seconds
    private static final int AIR_BUBBLE_DURATION = 60 * 20; // 60 seconds of water breathing
    private static final int BUBBLE_RADIUS = 8; // 8 block radius for sharing water breathing
    private static final float WATER_BALL_DAMAGE = 6.0f;
    private static final float NETHER_MOB_DAMAGE = 12.0f;
    private static final int WATER_BALL_RANGE = 30;

    // Track active air bubbles per player
    private final Map<UUID, Long> airBubbleEndTimes = new HashMap<>();

    public AquaBubbleAbility() {
        super("aqua_bubble", COOLDOWN);
    }

    @Override
    public void onActivate(Player player, Level level) {
        if (level.isClientSide())
            return;

        if (player.isInWater() || player.isUnderWater()) {
            // IN WATER MODE: Create air bubble (water breathing effect)
            activateAirBubble(player, level);
        } else {
            // ON LAND MODE: Fire water ball projectile
            fireWaterBall(player, level);
        }

        startCooldown();
    }

    /**
     * Create a shared air bubble - gives water breathing to Tidecaller and nearby
     * players
     */
    private void activateAirBubble(Player player, Level level) {
        UUID id = player.getUUID();
        long endTime = level.getGameTime() + AIR_BUBBLE_DURATION;
        airBubbleEndTimes.put(id, endTime);

        // Give water breathing effect to self
        player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, AIR_BUBBLE_DURATION, 0, false, true));
        player.setAirSupply(player.getMaxAirSupply());

        // Give water breathing to nearby players underwater
        int playersBuffed = shareWaterBreathing(player, level, AIR_BUBBLE_DURATION);

        // Visual feedback - large bubble sphere
        if (level instanceof ServerLevel serverLevel) {
            // Create sphere effect
            for (int ring = 1; ring <= 3; ring++) {
                int radius = ring * 3;
                for (int i = 0; i < radius * 8; i++) {
                    double angle = (i / (double) (radius * 8)) * Math.PI * 2;
                    double x = player.getX() + Math.cos(angle) * radius;
                    double z = player.getZ() + Math.sin(angle) * radius;

                    serverLevel.sendParticles(ParticleTypes.BUBBLE,
                            x, player.getY() + 1, z, 3, 0.1, 0.3, 0.1, 0.05);
                }
            }

            // Central bubble pop
            serverLevel.sendParticles(ParticleTypes.BUBBLE_POP,
                    player.getX(), player.getY() + 1, player.getZ(),
                    30, 0.5, 0.5, 0.5, 0.15);
        }

        // Sound
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BUBBLE_COLUMN_BUBBLE_POP, SoundSource.PLAYERS, 1.0f, 1.0f);

        // Message
        if (playersBuffed > 0) {
            player.displayClientMessage(Component.literal(ChatFormatting.AQUA + "" + ChatFormatting.BOLD + "Aqua Bubble! "
                    + ChatFormatting.RESET + ChatFormatting.GRAY + "Water Breathing shared with " + ChatFormatting.AQUA
                    +
                    playersBuffed + ChatFormatting.GRAY + " nearby player" + (playersBuffed > 1 ? "s" : "") + "!"), false);
        } else {
            player.displayClientMessage(Component
                    .literal(ChatFormatting.AQUA + "" + ChatFormatting.BOLD + "Aqua Bubble! " + ChatFormatting.RESET
                            + ChatFormatting.GRAY + "Water Breathing for 60 seconds. " + ChatFormatting.DARK_GRAY
                            + "(Stay near allies to share!)"), false);
        }
    }

    /**
     * Share water breathing with nearby underwater players
     */
    private int shareWaterBreathing(Player tidecaller, Level level, int duration) {
        AABB bubbleArea = tidecaller.getBoundingBox().inflate(BUBBLE_RADIUS);
        List<Player> nearbyPlayers = level.getEntitiesOfClass(Player.class, bubbleArea,
                p -> p != tidecaller && p.isInWater());

        int count = 0;
        for (Player nearbyPlayer : nearbyPlayers) {
            nearbyPlayer.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, duration, 0, false, true));
            nearbyPlayer.setAirSupply(nearbyPlayer.getMaxAirSupply());
            nearbyPlayer
                    .displayClientMessage(Component.literal(ChatFormatting.AQUA + "" + tidecaller.getName().getString() +
                            ChatFormatting.GRAY + " is sharing their Aqua Bubble with you!"), false);
            count++;

            // Particles on buffed player
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.BUBBLE_POP,
                        nearbyPlayer.getX(), nearbyPlayer.getY() + 1, nearbyPlayer.getZ(),
                        15, 0.3, 0.3, 0.3, 0.1);
            }
        }

        return count;
    }

    /**
     * Fire a water ball projectile that damages enemies
     */
    private void fireWaterBall(Player player, Level level) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 endPos = eyePos.add(lookVec.scale(WATER_BALL_RANGE));

        // Raycast to find where the water ball lands
        BlockHitResult blockHit = level.clip(new ClipContext(
                eyePos, endPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));

        Vec3 hitPos = blockHit.getType() == HitResult.Type.BLOCK ? blockHit.getLocation() : endPos;

        // Visual trail of water particles
        if (level instanceof ServerLevel serverLevel) {
            double distance = eyePos.distanceTo(hitPos);
            int particles = (int) (distance * 2);

            for (int i = 0; i < particles; i++) {
                double t = i / (double) particles;
                Vec3 particlePos = eyePos.add(hitPos.subtract(eyePos).scale(t));

                serverLevel.sendParticles(ParticleTypes.SPLASH,
                        particlePos.x, particlePos.y, particlePos.z,
                        2, 0.1, 0.1, 0.1, 0.1);
                serverLevel.sendParticles(ParticleTypes.DRIPPING_WATER,
                        particlePos.x, particlePos.y, particlePos.z,
                        1, 0.05, 0.05, 0.05, 0);
            }

            // Explosion of water at hit location
            serverLevel.sendParticles(ParticleTypes.SPLASH,
                    hitPos.x, hitPos.y, hitPos.z, 50, 1.0, 1.0, 1.0, 0.5);
            serverLevel.sendParticles(ParticleTypes.BUBBLE,
                    hitPos.x, hitPos.y, hitPos.z, 30, 0.8, 0.8, 0.8, 0.2);
        }

        // Sound effects
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.TRIDENT_THROW.value(), SoundSource.PLAYERS, 1.0f, 0.8f);
        level.playSound(null, hitPos.x, hitPos.y, hitPos.z,
                SoundEvents.GENERIC_SPLASH, SoundSource.PLAYERS, 1.0f, 1.0f);

        // Damage entities at impact location
        AABB damageArea = new AABB(hitPos.x - 3, hitPos.y - 3, hitPos.z - 3,
                hitPos.x + 3, hitPos.y + 3, hitPos.z + 3);
        List<Entity> entities = level.getEntities(player, damageArea);
        int entitiesHit = 0;

        for (Entity entity : entities) {
            if (entity instanceof LivingEntity living && entity != player) {
                // Check if it's a nether/fire mob
                String mobType = entity.getType().toString().toLowerCase();
                boolean isNetherMob = mobType.contains("blaze") || mobType.contains("magma") ||
                        mobType.contains("strider") || mobType.contains("piglin") ||
                        mobType.contains("hoglin") || mobType.contains("ghast") ||
                        mobType.contains("wither_skeleton") || mobType.contains("zombified_piglin") ||
                        entity.isOnFire();

                float damage = isNetherMob ? NETHER_MOB_DAMAGE : WATER_BALL_DAMAGE;

                living.hurt(level.damageSources().drown(), damage);
                living.extinguishFire();

                // Apply slowness
                living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 1, false, true));

                // Knockback from impact
                Vec3 knockback = living.position().subtract(hitPos).normalize().scale(0.5);
                living.setDeltaMovement(living.getDeltaMovement().add(knockback.x, 0.3, knockback.z));
                living.hurtMarked = true;

                entitiesHit++;

                // Extra particles on nether mobs
                if (isNetherMob && level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.CLOUD,
                            living.getX(), living.getY() + 1, living.getZ(),
                            15, 0.3, 0.5, 0.3, 0.1);
                }
            }
        }

        if (entitiesHit > 0) {
            player.displayClientMessage(Component.literal(ChatFormatting.AQUA + "" + ChatFormatting.BOLD + "Water Ball! "
                    + ChatFormatting.RESET + ChatFormatting.GRAY + "Hit " + entitiesHit + " enemies!"), false);
        } else {
            player.displayClientMessage(Component.literal(ChatFormatting.AQUA + "Water Ball fired!"), false);
        }
    }

    @Override
    public void tick(Player player) {
        Level level = player.level();
        if (level.isClientSide())
            return;

        UUID id = player.getUUID();
        Long endTime = airBubbleEndTimes.get(id);

        if (endTime != null) {
            long currentTime = level.getGameTime();

            if (currentTime >= endTime) {
                airBubbleEndTimes.remove(id);
            } else {
                // Keep air supply full for Tidecaller
                if (player.isInWater()) {
                    player.setAirSupply(player.getMaxAirSupply());
                }

                // Continuously share water breathing with nearby underwater players
                // Refresh every second (20 ticks)
                if (currentTime % 20 == 0) {
                    AABB bubbleArea = player.getBoundingBox().inflate(BUBBLE_RADIUS);
                    List<Player> nearbyPlayers = level.getEntitiesOfClass(Player.class, bubbleArea,
                            p -> p != player && p.isInWater());

                    for (Player nearbyPlayer : nearbyPlayers) {
                        // Give them water breathing for 3 seconds (60 ticks) - refreshed every second
                        if (!nearbyPlayer.hasEffect(MobEffects.WATER_BREATHING) ||
                                nearbyPlayer.getEffect(MobEffects.WATER_BREATHING).getDuration() < 40) {
                            nearbyPlayer
                                    .addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 60, 0, false, true));
                            nearbyPlayer.setAirSupply(nearbyPlayer.getMaxAirSupply());
                        }
                    }

                    // Bubble particles around Tidecaller to show active bubble
                    if (player.isInWater() && level instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.BUBBLE,
                                player.getX(), player.getY() + 1, player.getZ(),
                                5, 0.3, 0.3, 0.3, 0.05);

                        // Ring particles to show radius
                        if (currentTime % 40 == 0) { // Every 2 seconds
                            for (int i = 0; i < 16; i++) {
                                double angle = (i / 16.0) * Math.PI * 2;
                                double x = player.getX() + Math.cos(angle) * BUBBLE_RADIUS;
                                double z = player.getZ() + Math.sin(angle) * BUBBLE_RADIUS;

                                serverLevel.sendParticles(ParticleTypes.BUBBLE_POP,
                                        x, player.getY() + 1, z, 1, 0, 0, 0, 0);
                            }
                        }
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
        return 0;
    }
}
