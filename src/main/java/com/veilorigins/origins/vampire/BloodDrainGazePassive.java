package com.veilorigins.origins.vampire;

import com.veilorigins.api.OriginPassive;
import com.veilorigins.data.OriginData;
import com.veilorigins.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.squid.Squid;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.ChatFormatting;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Passive blood drain ability for Vampires and Vamplings.
 * When crouching and looking at a creature with blood for 5 seconds, the
 * vampire begins
 * to drain blood from the creature, dealing damage over time and healing the
 * vampire.
 * 
 * Valid targets include:
 * - Large animals (cows, sheep, pigs, horses, llamas, donkeys, etc.)
 * - Villagers and wandering traders
 * - Pillagers, vindicators, evokers, and other illagers
 * - Witches, players
 * 
 * Invalid targets (no blood or too small):
 * - Undead (zombies, skeletons, phantoms, etc.)
 * - Constructs (iron golems, snow golems)
 * - Tiny creatures (bats, silverfish, endermites, bees)
 * - Slimes, blazes, and other non-organic creatures
 */
public class BloodDrainGazePassive extends OriginPassive {

    // Track drain state per player
    private static final Map<UUID, DrainState> drainStates = new HashMap<>();

    // Entity types that have NO blood (blacklist)
    private static final Set<String> BLOODLESS_ENTITIES = new HashSet<>();

    // Entity types that are too small to drain
    private static final Set<String> TOO_SMALL_ENTITIES = new HashSet<>();

    static {
        // Undead - no blood
        BLOODLESS_ENTITIES.add("zombie");
        BLOODLESS_ENTITIES.add("zombie_villager");
        BLOODLESS_ENTITIES.add("husk");
        BLOODLESS_ENTITIES.add("drowned");
        BLOODLESS_ENTITIES.add("skeleton");
        BLOODLESS_ENTITIES.add("stray");
        BLOODLESS_ENTITIES.add("wither_skeleton");
        BLOODLESS_ENTITIES.add("phantom");
        BLOODLESS_ENTITIES.add("wither");
        BLOODLESS_ENTITIES.add("zoglin");
        BLOODLESS_ENTITIES.add("zombified_piglin");
        BLOODLESS_ENTITIES.add("skeleton_horse");
        BLOODLESS_ENTITIES.add("zombie_horse");

        // Constructs - no blood (metal/stone/magic)
        BLOODLESS_ENTITIES.add("iron_golem");
        BLOODLESS_ENTITIES.add("snow_golem");
        BLOODLESS_ENTITIES.add("shulker");
        BLOODLESS_ENTITIES.add("armor_stand");

        // Fire/elemental creatures - no blood
        BLOODLESS_ENTITIES.add("blaze");
        BLOODLESS_ENTITIES.add("magma_cube");
        BLOODLESS_ENTITIES.add("ghast");

        // Slimes - no blood (just goo)
        BLOODLESS_ENTITIES.add("slime");

        // Void/ender creatures
        BLOODLESS_ENTITIES.add("enderman");
        BLOODLESS_ENTITIES.add("endermite");
        BLOODLESS_ENTITIES.add("ender_dragon");
        BLOODLESS_ENTITIES.add("shulker");

        // Warden - partially sculk, not normal blood
        BLOODLESS_ENTITIES.add("warden");

        // Magical/spectral creatures
        BLOODLESS_ENTITIES.add("allay");
        BLOODLESS_ENTITIES.add("vex");

        // Too small to drain effectively
        TOO_SMALL_ENTITIES.add("bat");
        TOO_SMALL_ENTITIES.add("bee");
        TOO_SMALL_ENTITIES.add("rabbit");
        TOO_SMALL_ENTITIES.add("chicken");
        TOO_SMALL_ENTITIES.add("parrot");
        TOO_SMALL_ENTITIES.add("frog");
        TOO_SMALL_ENTITIES.add("tadpole");
        TOO_SMALL_ENTITIES.add("cod");
        TOO_SMALL_ENTITIES.add("salmon");
        TOO_SMALL_ENTITIES.add("tropical_fish");
        TOO_SMALL_ENTITIES.add("pufferfish");
        TOO_SMALL_ENTITIES.add("silverfish");
        TOO_SMALL_ENTITIES.add("cave_spider"); // Debatable, but small
    }

    // Configuration
    private final float damagePerTick; // Damage dealt per tick while draining
    private final float healPerTick; // Health restored per tick while draining
    private final float resourcePerTick; // Blood essence gained per tick
    private final int ticksToStartDrain; // Ticks of looking before drain starts (5 seconds = 100 ticks)
    private final double maxDrainDistance; // Maximum distance to drain from
    private final boolean isFullVampire;

    /**
     * Creates a Blood Drain Gaze passive.
     * 
     * @param isFullVampire true for Vampire, false for Vampling (affects drain
     *                      rates)
     */
    public BloodDrainGazePassive(boolean isFullVampire) {
        super("blood_drain_gaze");
        this.isFullVampire = isFullVampire;

        // Full vampire drains faster and more efficiently
        this.damagePerTick = isFullVampire ? 0.5f : 0.25f; // 10 or 5 damage per second
        this.healPerTick = isFullVampire ? 0.25f : 0.1f; // 5 or 2 healing per second
        this.resourcePerTick = isFullVampire ? 1.0f : 0.5f; // Resource restoration
        this.ticksToStartDrain = 100; // 5 seconds for both
        this.maxDrainDistance = isFullVampire ? 8.0 : 5.0; // Vampire has longer range
    }

    @Override
    public void onTick(Player player) {
        if (player.level().isClientSide())
            return;

        UUID playerId = player.getUUID();
        DrainState state = drainStates.computeIfAbsent(playerId, k -> new DrainState());

        // Check if player is crouching
        if (!player.isShiftKeyDown()) {
            // Reset state if not crouching
            if (state.targetEntityId != null) {
                resetDrainState(player, state);
            }
            return;
        }

        // Get the entity the player is looking at
        LivingEntity targetEntity = getTargetedCreature(player);

        // Check if looking at a valid creature with blood
        if (targetEntity == null) {
            // Reset if not looking at a valid target
            if (state.targetEntityId != null) {
                resetDrainState(player, state);
            }
            return;
        }

        // Check if same target
        if (state.targetEntityId == null || !state.targetEntityId.equals(targetEntity.getUUID())) {
            // New target - reset progress
            state.targetEntityId = targetEntity.getUUID();
            state.ticksLooking = 0;
            state.isDraining = false;

            player.displayClientMessage(
                    Component.literal(
                            ChatFormatting.DARK_GRAY + "[Focusing on " + targetEntity.getName().getString() + "...]"),
                    true);
        }

        // Increment looking time
        state.ticksLooking++;

        // Show progress while charging
        if (!state.isDraining && state.ticksLooking < ticksToStartDrain) {
            int percent = (state.ticksLooking * 100) / ticksToStartDrain;
            if (state.ticksLooking % 20 == 0) { // Update every second
                player.displayClientMessage(
                        Component.literal(
                                ChatFormatting.DARK_RED + "" + ChatFormatting.BOLD + "\u2588".repeat(percent / 10)
                                        + ChatFormatting.DARK_GRAY + "\u2591".repeat(10 - percent / 10)
                                        + " " + ChatFormatting.RED + percent + "%"),
                        true);

                // Play subtle sound while focusing
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.SCULK_CLICKING, SoundSource.PLAYERS, 0.2f, 1.5f);
            }
        }

        // Start draining after threshold
        if (state.ticksLooking >= ticksToStartDrain) {
            if (!state.isDraining) {
                state.isDraining = true;
                player.displayClientMessage(
                        Component.literal(ChatFormatting.DARK_RED + "\u26A1 Blood Drain Activated! \u26A1"),
                        true);

                // Initial sound
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.WARDEN_HEARTBEAT, SoundSource.PLAYERS, 0.5f, 1.5f);
            }

            // Perform blood drain
            drainBlood(player, targetEntity, state);
        }
    }

    /**
     * Gets a valid creature with blood that the player is looking at within range.
     */
    private LivingEntity getTargetedCreature(Player player) {
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();
        Vec3 endPos = eyePos.add(lookVec.scale(maxDrainDistance));

        // Create a bounding box for the ray
        AABB searchBox = player.getBoundingBox()
                .expandTowards(lookVec.scale(maxDrainDistance))
                .inflate(1.0);

        // Find the closest valid entity in the line of sight
        LivingEntity closestEntity = null;
        double closestDistance = maxDrainDistance;

        for (Entity entity : player.level().getEntities(player, searchBox,
                e -> e instanceof LivingEntity && e.isAlive() && hasBlood((LivingEntity) e))) {

            AABB entityBox = entity.getBoundingBox().inflate(0.3);
            Optional<Vec3> hitResult = entityBox.clip(eyePos, endPos);

            if (hitResult.isPresent()) {
                double distance = eyePos.distanceTo(hitResult.get());
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestEntity = (LivingEntity) entity;
                }
            }
        }

        return closestEntity;
    }

    /**
     * Determines if a living entity has blood that can be drained.
     * Uses entity type ID for reliable checking across different mob types.
     */
    private boolean hasBlood(LivingEntity entity) {
        // Get the entity type ID (e.g., "minecraft:cow" -> "cow")
        String entityId = EntityType.getKey(entity.getType()).getPath();

        // Check blacklists first
        if (BLOODLESS_ENTITIES.contains(entityId)) {
            return false;
        }
        if (TOO_SMALL_ENTITIES.contains(entityId)) {
            return false;
        }

        // Players can be drained (PvP)
        if (entity instanceof Player) {
            return true;
        }

        // Animals are valid (except those in blacklist)
        if (entity instanceof Animal) {
            return true;
        }

        // Squids have blood (blue blood!)
        if (entity instanceof Squid) {
            return true;
        }

        // Villagers and traders
        if (entity instanceof AbstractVillager) {
            return true;
        }

        // Check if it's a mob that's NOT undead
        if (entity instanceof Mob mob) {
            // Undead mobs are inverted for healing (healed by harming, hurt by healing)
            if (mob.isInvertedHealAndHarm()) {
                return false; // Undead
            }
        }

        // Monsters that aren't undead or elemental likely have blood
        // This catches illagers (pillager, vindicator, evoker, illusioner), witch,
        // ravager, etc.
        if (entity instanceof Monster) {
            // Double-check it's not in our bloodless list
            return !BLOODLESS_ENTITIES.contains(entityId);
        }

        // For any other living entity, check size
        // Must be reasonably sized (at least 0.5 blocks tall)
        if (entity.getBbHeight() >= 0.5f && entity.getBbWidth() >= 0.3f) {
            return true;
        }

        return false;
    }

    /**
     * Performs the blood draining effect on the target creature.
     */
    private void drainBlood(Player player, LivingEntity target, DrainState state) {
        // Deal damage to the target
        target.hurt(player.damageSources().magic(), damagePerTick);

        // Heal the player
        player.heal(healPerTick);

        // Restore blood essence resource
        OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);
        data.addResource(resourcePerTick);
        
        // Try to fill blood bottle in offhand if draining an animal (every 20 ticks)
        if (target instanceof Animal && state.ticksLooking % 20 == 0) {
            tryFillBloodBottle(player);
        }

        // Visual effects every 10 ticks
        if (state.ticksLooking % 10 == 0) {
            spawnDrainParticles(player, target);
        }

        // Sound effect every 20 ticks
        if (state.ticksLooking % 20 == 0) {
            player.level().playSound(null, target.getX(), target.getY(), target.getZ(),
                    SoundEvents.HONEY_DRINK, SoundSource.PLAYERS, 0.3f, 0.5f);
        }

        // Apply weakness to the target
        target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 1, false, false));
        target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 2, false, false));

        // Check if target died
        if (target.isDeadOrDying()) {
            onTargetDrained(player, target);
            resetDrainState(player, state);
        }
    }
    
    /**
     * Tries to fill a blood bottle in the player's offhand.
     */
    private void tryFillBloodBottle(Player player) {
        ItemStack offhand = player.getOffhandItem();
        if (offhand.getItem() == ModItems.BLOOD_BOTTLE_EMPTY.get()) {
            // Fill empty -> half
            offhand.shrink(1);
            ItemStack filledStack = new ItemStack(ModItems.BLOOD_BOTTLE_HALF.get());
            if (!player.getInventory().add(filledStack)) {
                player.drop(filledStack, false);
            }
            player.displayClientMessage(
                    Component.literal(ChatFormatting.GRAY + "(Bottle filling...)"),
                    true);
        } else if (offhand.getItem() == ModItems.BLOOD_BOTTLE_HALF.get()) {
            // Fill half -> full
            offhand.shrink(1);
            ItemStack filledStack = new ItemStack(ModItems.BLOOD_BOTTLE_FULL.get());
            if (!player.getInventory().add(filledStack)) {
                player.drop(filledStack, false);
            }
            player.displayClientMessage(
                    Component.literal(ChatFormatting.DARK_RED + "(Bottle filled!)"),
                    true);
        }
    }

    /**
     * Spawns blood drain particle effects between player and target.
     */
    private void spawnDrainParticles(Player player, LivingEntity target) {
        if (!(player.level() instanceof ServerLevel serverLevel))
            return;

        Vec3 playerPos = player.getEyePosition();
        Vec3 targetPos = target.position().add(0, target.getBbHeight() / 2, 0);
        Vec3 direction = playerPos.subtract(targetPos).normalize();
        double distance = playerPos.distanceTo(targetPos);

        // Spawn particles along the line from target to player
        for (double d = 0; d < distance; d += 0.5) {
            Vec3 particlePos = targetPos.add(direction.scale(d));

            // Crimson/blood colored particles
            serverLevel.sendParticles(ParticleTypes.CRIMSON_SPORE,
                    particlePos.x, particlePos.y, particlePos.z,
                    1, 0.1, 0.1, 0.1, 0.02);
        }

        // Particles at the target
        serverLevel.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
                targetPos.x, targetPos.y, targetPos.z,
                1, 0.2, 0.2, 0.2, 0);

        // Particles at the player (absorption effect)
        serverLevel.sendParticles(ParticleTypes.HEART,
                playerPos.x, playerPos.y - 0.5, playerPos.z,
                1, 0.2, 0.2, 0.2, 0);
    }

    /**
     * Called when a target is fully drained.
     */
    private void onTargetDrained(Player player, LivingEntity target) {
        player.displayClientMessage(
                Component.literal(ChatFormatting.DARK_RED + "\u2726 " + target.getName().getString()
                        + " has been drained! \u2726"),
                false);

        // Bonus effects for fully draining a target
        int duration = isFullVampire ? 200 : 100;
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, duration, 1, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, duration, 0, false, true));

        // Sound effect
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.5f, 0.5f);

        // Significant resource boost
        OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);
        data.addResource(isFullVampire ? 30.0f : 15.0f);
    }

    /**
     * Resets the drain state when the player stops looking at a target.
     */
    private void resetDrainState(Player player, DrainState state) {
        if (state.isDraining) {
            player.displayClientMessage(
                    Component.literal(ChatFormatting.DARK_GRAY + "[Blood drain interrupted]"),
                    true);
        }
        state.targetEntityId = null;
        state.ticksLooking = 0;
        state.isDraining = false;
    }

    @Override
    public void onEquip(Player player) {
        player.displayClientMessage(Component.literal(
                ChatFormatting.DARK_RED + "\u263D Blood Gaze: " + ChatFormatting.GRAY
                        + "Crouch and stare at a creature with blood for 5 seconds to drain it!"), false);
    }

    @Override
    public void onRemove(Player player) {
        drainStates.remove(player.getUUID());
    }

    /**
     * Internal class to track drain state per player.
     */
    private static class DrainState {
        UUID targetEntityId = null;
        int ticksLooking = 0;
        boolean isDraining = false;
    }
}
