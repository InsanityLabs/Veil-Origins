package com.veilorigins.origins.skyborn;

import com.veilorigins.api.OriginAbility;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;

import java.util.List;

public class WindBlastAbility extends OriginAbility {
    private static final int COOLDOWN = 15;
    private static final int HUNGER_COST = 3;
    private static final double PUSH_STRENGTH = 2.0;
    private static final double SELF_LAUNCH = 1.5;

    public WindBlastAbility() {
        super("wind_blast", COOLDOWN);
    }

    @Override
    public void onActivate(Player player, Level level) {
        Vec3 look = player.getLookAngle();
        boolean isLookingDown = look.y < -0.5;

        // Sound
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.BREWING_STAND_BREW, SoundSource.PLAYERS, 1.0f, 2.0f); // Gust sound like breeze?

        if (isLookingDown) {
            // Launch self
            player.setDeltaMovement(player.getDeltaMovement().add(0, SELF_LAUNCH, 0));
            player.hurtMarked = true;
        } else {
            // Push entities
            AABB area = player.getBoundingBox().expandTowards(look.scale(5)).inflate(2);
            List<Entity> entities = level.getEntities(player, area);

            for (Entity e : entities) {
                if (e instanceof LivingEntity || e instanceof Projectile) {
                    Vec3 pushDir = e.position().subtract(player.position()).normalize().scale(PUSH_STRENGTH);
                    e.setDeltaMovement(e.getDeltaMovement().add(pushDir));
                    e.hurtMarked = true;

                    if (e instanceof Projectile p) {
                        // Deflect
                        p.setDeltaMovement(p.getDeltaMovement().scale(-1));
                    }
                }
            }

            // Extinguish fire
            if (!level.isClientSide()) {
                BlockPos center = player.blockPosition();
                for (BlockPos pos : BlockPos.betweenClosed(center.offset(-3, -1, -3), center.offset(3, 3, 3))) {
                    if (level.getBlockState(pos).is(Blocks.FIRE)) {
                        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                    }
                }
            }
        }

        player.causeFoodExhaustion(HUNGER_COST);
        startCooldown();
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
