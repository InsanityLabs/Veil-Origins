package com.veilorigins.origins.starborne;

import com.veilorigins.api.OriginAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StarlightBeaconAbility extends OriginAbility {
    private static final int COOLDOWN = 60 * 20; // 60 seconds in ticks
    private static final int DURATION = 30 * 20; // 30 seconds in ticks
    private static final int RADIUS = 15;
    private static final float PUSH_STRENGTH = 1.5f;
    private static final float INSTANT_KILL_DAMAGE = 1000f;

    private final Map<UUID, BeaconData> beacons = new HashMap<>();

    public StarlightBeaconAbility() {
        super("starlight_beacon", COOLDOWN);
    }

    private static class BeaconData {
        BlockPos pos;
        long endTime;

        public BeaconData(BlockPos pos, long endTime) {
            this.pos = pos;
            this.endTime = endTime;
        }
    }

    @Override
    public void onActivate(Player player, Level level) {
        BlockPos beaconPos = player.blockPosition();
        beacons.put(player.getUUID(), new BeaconData(beaconPos, level.getGameTime() + DURATION));

        player.sendSystemMessage(
                Component.literal("§e§lStarlight Beacon placed! §rHostile mobs will be killed or pushed away."));

        // Initial burst effect
        if (level instanceof ServerLevel serverLevel) {
            // Big light burst
            for (int i = 0; i < 100; i++) {
                double angle = Math.random() * Math.PI * 2;
                double distance = Math.random() * RADIUS;
                double x = beaconPos.getX() + 0.5 + Math.cos(angle) * distance;
                double z = beaconPos.getZ() + 0.5 + Math.sin(angle) * distance;
                double y = beaconPos.getY() + Math.random() * 5;

                serverLevel.sendParticles(ParticleTypes.END_ROD,
                        x, y, z, 1, 0, 0.5, 0, 0.1);
            }

            // Initial push/kill of all hostile mobs
            killAndPushHostileMobs(player, level, beaconPos);
        }

        startCooldown();
    }

    @Override
    public void tick(Player player) {
        Level level = player.level();
        UUID id = player.getUUID();
        BeaconData data = beacons.get(id);

        if (data != null) {
            if (level.getGameTime() > data.endTime) {
                beacons.remove(id);
                player.sendSystemMessage(Component.literal("§7Starlight Beacon faded."));
                return;
            }

            BlockPos pos = data.pos;

            // Visual beacon effect every 10 ticks
            if (level.getGameTime() % 10 == 0 && level instanceof ServerLevel serverLevel) {
                // Beacon pillar
                for (int y = 0; y < 10; y++) {
                    serverLevel.sendParticles(ParticleTypes.END_ROD,
                            pos.getX() + 0.5, pos.getY() + 1 + y, pos.getZ() + 0.5,
                            2, 0.1, 0.1, 0.1, 0.02);
                }

                // Ring effect on ground
                for (int i = 0; i < 20; i++) {
                    double angle = (i / 20.0) * Math.PI * 2;
                    double x = pos.getX() + 0.5 + Math.cos(angle) * RADIUS;
                    double z = pos.getZ() + 0.5 + Math.sin(angle) * RADIUS;

                    serverLevel.sendParticles(ParticleTypes.END_ROD,
                            x, pos.getY() + 0.5, z, 1, 0, 0, 0, 0);
                }
            }

            // Effect Logic every second (20 ticks)
            if (level.getGameTime() % 20 == 0) {
                AABB area = new AABB(pos).inflate(RADIUS);

                // Kill/push hostile mobs and apply beneficial effects
                killAndPushHostileMobs(player, level, pos);

                // Reveal invisible entities
                level.getEntities(null, area).forEach(e -> {
                    if (e.isInvisible()) {
                        e.setInvisible(false);
                        if (e instanceof LivingEntity le) {
                            le.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0, false, false));
                        }
                    }

                    // Damage Undead extra
                    if (e instanceof LivingEntity le && le.isInvertedHealAndHarm()) {
                        le.hurt(level.damageSources().magic(), 2.0f);
                    }

                    // Heal and buff players
                    if (e instanceof Player p) {
                        p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 0, false, false));
                        p.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0, false, true)); // Allies glow too
                    }
                });
            }
        }
    }

    /**
     * Kills newly spawned hostile mobs and pushes away others
     */
    private void killAndPushHostileMobs(Player player, Level level, BlockPos beaconPos) {
        AABB area = new AABB(beaconPos).inflate(RADIUS);
        Vec3 beaconCenter = new Vec3(beaconPos.getX() + 0.5, beaconPos.getY(), beaconPos.getZ() + 0.5);

        List<Entity> entities = level.getEntities(null, area);

        for (Entity entity : entities) {
            // Check if it's a hostile mob
            if (entity instanceof Monster monster ||
                    (entity instanceof Mob mob && mob.getType().getCategory() == MobCategory.MONSTER)) {

                if (entity instanceof LivingEntity livingEntity) {
                    // Check if mob just spawned (existence ticks < 100 = 5 seconds)
                    // Or if mob is undead
                    boolean isNewSpawn = entity.tickCount < 100;
                    boolean isUndead = livingEntity.isInvertedHealAndHarm();

                    if (isNewSpawn || isUndead) {
                        // Kill new spawns and undead instantly with light damage
                        livingEntity.hurt(level.damageSources().magic(), INSTANT_KILL_DAMAGE);

                        // Death particles
                        if (level instanceof ServerLevel serverLevel) {
                            serverLevel.sendParticles(ParticleTypes.END_ROD,
                                    entity.getX(), entity.getY() + 1, entity.getZ(),
                                    20, 0.5, 0.5, 0.5, 0.2);
                        }
                    } else {
                        // Push away established hostile mobs
                        Vec3 direction = entity.position().subtract(beaconCenter).normalize();
                        double distance = entity.position().distanceTo(beaconCenter);

                        // Stronger push the closer they are
                        double pushMultiplier = Math.max(0.5, (RADIUS - distance) / RADIUS) * PUSH_STRENGTH;

                        entity.setDeltaMovement(
                                direction.x * pushMultiplier,
                                0.3, // Slight upward push
                                direction.z * pushMultiplier);
                        entity.hurtMarked = true;

                        // Apply weakness and slowness
                        livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 1, false, false));
                        livingEntity
                                .addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0, false, false));
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
