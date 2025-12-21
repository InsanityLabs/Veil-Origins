package com.veilorigins.origins.crystalline;

import com.veilorigins.api.OriginAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.ChatFormatting;

/**
 * Crystal Spike - Summons a burst of crystal shards that damage and slow
 * enemies
 */
public class CrystalSpikeAbility extends OriginAbility {
    private static final int COOLDOWN = 5 * 20; // 5 seconds (reduced from 12)
    private static final int HUNGER_COST = 2;
    private static final float DAMAGE = 6.0f;
    private static final int SPIKE_DURATION = 8 * 20; // 8 seconds

    private final Map<BlockPos, Long> temporarySpikes = new HashMap<>();

    public CrystalSpikeAbility() {
        super("crystal_spike", COOLDOWN);
    }

    @Override
    public void onActivate(Player player, Level level) {
        if (level.isClientSide)
            return;

        Vec3 look = player.getLookAngle();
        BlockPos playerPos = player.blockPosition();
        List<BlockPos> spikesPlaced = new ArrayList<>();

        // Create a line of crystal spikes in front of player
        for (int i = 2; i <= 6; i++) {
            BlockPos spikePos = playerPos.offset(
                    (int) (look.x * i),
                    0,
                    (int) (look.z * i));

            // Find ground level
            while (level.isEmptyBlock(spikePos) && spikePos.getY() > level.getMinBuildHeight()) {
                spikePos = spikePos.below();
            }
            spikePos = spikePos.above(); // Place spike on top of ground

            // Place spike if valid
            if (level.isEmptyBlock(spikePos) && !level.isEmptyBlock(spikePos.below())) {
                // Alternate between amethyst cluster and pointed dripstone for variety
                if (i % 2 == 0) {
                    level.setBlockAndUpdate(spikePos, Blocks.AMETHYST_CLUSTER.defaultBlockState());
                } else {
                    level.setBlockAndUpdate(spikePos, Blocks.POINTED_DRIPSTONE.defaultBlockState());
                }
                temporarySpikes.put(spikePos.immutable(), level.getGameTime() + SPIKE_DURATION);
                spikesPlaced.add(spikePos);

                // Particles for each spike
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.END_ROD,
                            spikePos.getX() + 0.5, spikePos.getY() + 0.5, spikePos.getZ() + 0.5,
                            10, 0.2, 0.3, 0.2, 0.05);
                }
            }
        }

        // Sound effect
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.PLAYERS, 1.5f, 0.8f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.0f, 1.5f);

        // Damage entities hit by spikes
        for (BlockPos spikePos : spikesPlaced) {
            AABB damageBox = new AABB(spikePos).inflate(1.5);
            List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, damageBox);

            for (LivingEntity target : targets) {
                if (target != player) {
                    // Damage
                    target.hurt(level.damageSources().magic(), DAMAGE);

                    // Apply slowness and bleeding (wither)
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1, false, true));
                    target.addEffect(new MobEffectInstance(MobEffects.WITHER, 40, 0, false, true)); // Crystal shards
                                                                                                    // cause bleeding

                    // Knockback away from player
                    Vec3 knockback = target.position().subtract(player.position()).normalize().scale(0.5);
                    target.setDeltaMovement(target.getDeltaMovement().add(knockback.x, 0.3, knockback.z));
                    target.hurtMarked = true;
                }
            }
        }

        if (!spikesPlaced.isEmpty()) {
            player.sendSystemMessage(Component
                    .literal(ChatFormatting.LIGHT_PURPLE + "" + spikesPlaced.size() + " crystal spikes erupted!"));
        }

        player.causeFoodExhaustion(HUNGER_COST);
        startCooldown();
    }

    @Override
    public void tick(Player player) {
        if (player.level().isClientSide)
            return;

        Level level = player.level();
        long time = level.getGameTime();

        temporarySpikes.entrySet().removeIf(entry -> {
            if (time > entry.getValue()) {
                // Shatter effect when spike disappears
                if (level instanceof ServerLevel serverLevel) {
                    BlockPos pos = entry.getKey();
                    serverLevel.sendParticles(ParticleTypes.END_ROD,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            8, 0.3, 0.3, 0.3, 0.1);
                    level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_BREAK,
                            SoundSource.BLOCKS, 0.5f, 1.2f);
                }
                level.setBlockAndUpdate(entry.getKey(), Blocks.AIR.defaultBlockState());
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean canUse(Player player) {
        return !isOnCooldown() && player.getFoodData().getFoodLevel() >= HUNGER_COST;
    }

    @Override
    public int getResourceCost() {
        return HUNGER_COST;
    }
}
