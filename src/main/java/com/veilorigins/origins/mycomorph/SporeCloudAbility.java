package com.veilorigins.origins.mycomorph;

import com.veilorigins.api.OriginAbility;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;

import java.util.List;

public class SporeCloudAbility extends OriginAbility {
    private static final int COOLDOWN = 30;
    private static final int DURATION = 400; // 20s
    private static final int HUNGER_COST = 4;
    private static final double RADIUS = 10.0;

    public SporeCloudAbility() {
        super("spore_cloud", COOLDOWN);
    }

    @Override
    public void onActivate(Player player, Level level) {
        player.causeFoodExhaustion(HUNGER_COST);

        // Trigger immediately (pulse effect or Area Effect Cloud entity?)
        // "Release cloud of spores... Poison enemies, Heal allies... Spreads mycelium"

        // Simple implementation: Instant effect + Mycelium spread + Visuals
        // If we want it to linger like Dragon Breath, we need an AreaEffectCloud
        // entity.
        // Let's spawn an AreaEffectCloudEntity?

        // Using direct logic for simplicity/reliability without custom entities for
        // now, or just pulses.
        // Spec says Duration: 20s.
        // Let's spawn a vanilla AreaEffectCloud event?

        net.minecraft.world.entity.AreaEffectCloud cloud = new net.minecraft.world.entity.AreaEffectCloud(level,
                player.getX(), player.getY(), player.getZ());
        cloud.setRadius((float) RADIUS);
        cloud.setRadiusOnUse(0f);
        cloud.setDuration(DURATION);
        cloud.setParticle(ParticleTypes.MYCELIUM);

        // Poison enemies
        cloud.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 1)); // Poison II 5s per tick inside?

        // "Heal allies" - vanilla cloud applies same effects to everyone.
        // We can't differentiate easily with vanilla AreaEffectCloud unless we custom
        // subclass it.
        // Let's do instant effect area around player + visual cloud, but maybe repeat
        // every second via a tick handler if we track it?

        // Actually, let's just do instant burst for mycelium + immediate effects +
        // maybe regen for self?
        // Spec implies duration.
        // We can use the Ability's built-in tick system!

        startCooldown();
        setDurationTimer(player, DURATION); // We need to add this capability to OriginAbility or just track it here.
    }

    // Tracking active duration locally
    private int durationTicks = 0;

    // We need to override tick to handle duration logic if we don't put it in a
    // map.
    // OriginEventHandler calls tick(player) on all abilities.
    // But abilities are singleton per Origin instance in registry?
    // Wait, ModOrigins registers new instances: .addAbility(new
    // SporeCloudAbility()).
    // And VeilOriginsAPI.getPlayerOrigin(player) returns the registered Origin
    // object.
    // Does it return a unique instance per player?
    // OriginRegistry is usually shared.
    // SO WE MUST USE A MAP for player specific state!

    private final java.util.Map<java.util.UUID, Integer> activeClouds = new java.util.HashMap<>();

    private void setDurationTimer(Player player, int ticks) {
        activeClouds.put(player.getUUID(), ticks);
    }

    @Override
    public void tick(Player player) {
        // Handle active duration
        if (activeClouds.containsKey(player.getUUID())) {
            int ticks = activeClouds.get(player.getUUID());
            if (ticks > 0) {
                activeClouds.put(player.getUUID(), ticks - 1);
                performEffect(player);
            } else {
                activeClouds.remove(player.getUUID());
            }
        }
    }

    private void performEffect(Player player) {
        Level level = player.level();
        if (level.isClientSide)
            return;

        BlockPos center = player.blockPosition();

        // Visuals (Server -> Client)
        if (level instanceof ServerLevel serverLevel && level.getGameTime() % 10 == 0) {
            serverLevel.sendParticles(ParticleTypes.MYCELIUM,
                    player.getX(), player.getY() + 1, player.getZ(),
                    20, 3.0, 1.0, 3.0, 0.05);
        }

        if (level.getGameTime() % 20 == 0) {
            AABB area = player.getBoundingBox().inflate(RADIUS);
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area);

            for (LivingEntity e : entities) {
                if (e instanceof Player p && (p == player || isAlly(player, p))) {
                    e.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 40, 0, false, false));
                } else {
                    e.addEffect(new MobEffectInstance(MobEffects.POISON, 40, 1, false, false));
                }
            }

            // Spread Mycelium randomly
            for (int i = 0; i < 5; i++) {
                BlockPos pos = center.offset(
                        (int) ((Math.random() - 0.5) * RADIUS * 2),
                        (int) ((Math.random() - 0.5) * 4),
                        (int) ((Math.random() - 0.5) * RADIUS * 2));
                if (level.getBlockState(pos).is(net.minecraft.world.level.block.Blocks.GRASS_BLOCK) ||
                        level.getBlockState(pos).is(net.minecraft.world.level.block.Blocks.DIRT)) {
                    level.setBlockAndUpdate(pos, net.minecraft.world.level.block.Blocks.MYCELIUM.defaultBlockState());
                }
            }
        }
    }

    private boolean isAlly(Player source, Player target) {
        return source.isAlliedTo(target) || source.getTeam() != null && source.getTeam().isAlliedTo(target.getTeam());
    }

    @Override
    public boolean canUse(Player player) {
        return !isOnCooldown() && player.getFoodData().getFoodLevel() >= HUNGER_COST;
    }

    @Override
    public int getResourceCost() {
        return 0;
    }
}
