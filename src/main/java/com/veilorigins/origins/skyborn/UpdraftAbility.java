package com.veilorigins.origins.skyborn;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class UpdraftAbility extends OriginAbility {
    private static final int COOLDOWN = 45;
    private static final int DURATION = 300; // 15s
    private static final int HUNGER_COST = 6;
    private static final double RADIUS = 2.0;
    private static final int HEIGHT = 20;

    private final Map<UUID, UpdraftData> updrafts = new HashMap<>();

    public UpdraftAbility() {
        super("updraft", COOLDOWN);
    }

    private static class UpdraftData {
        BlockPos pos;
        long endTime;

        public UpdraftData(BlockPos pos, long endTime) {
            this.pos = pos;
            this.endTime = endTime;
        }
    }

    @Override
    public void onActivate(Player player, Level level) {
        updrafts.put(player.getUUID(), new UpdraftData(player.blockPosition(), level.getGameTime() + DURATION));
        player.causeFoodExhaustion(HUNGER_COST);
        startCooldown();
    }

    @Override
    public void tick(Player player) {
        if (player.level().isClientSide())
            return;

        UUID id = player.getUUID();
        UpdraftData data = updrafts.get(id);

        if (data != null) {
            Level level = player.level();
            if (level.getGameTime() > data.endTime) {
                updrafts.remove(id);
                return;
            }

            BlockPos pos = data.pos;
            AABB column = new AABB(pos.getX() - RADIUS, pos.getY(), pos.getZ() - RADIUS,
                    pos.getX() + RADIUS, pos.getY() + HEIGHT, pos.getZ() + RADIUS);

            // Visuals
            if (level instanceof ServerLevel serverLevel && level.getGameTime() % 5 == 0) {
                for (int i = 0; i < 5; i++) {
                    double x = pos.getX() + (Math.random() - 0.5) * RADIUS * 2;
                    double z = pos.getZ() + (Math.random() - 0.5) * RADIUS * 2;
                    double y = pos.getY() + Math.random() * HEIGHT;
                    serverLevel.sendParticles(ParticleTypes.CLOUD, x, y, z, 1, 0, 0.1, 0, 0.05);
                }
            }

            // Logic
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, column);
            for (LivingEntity e : entities) {
                e.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 10, 2, false, false));
            }
        }
    }

    @Override
    public boolean canUse(Player player) {
        return !isOnCooldown() && player.getFoodData().getFoodLevel() >= HUNGER_COST;
    }

    @Override
    public int getResourceCost() {
        return 0; // Handled
    }
}
