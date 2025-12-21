package com.veilorigins.origins.necromancer;

import com.veilorigins.api.OriginAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.ChatFormatting;

public class RaiseDeadAbility extends OriginAbility {
    private static final int RESOURCE_COST = 10;
    private static final int MAX_SUMMONS = 4;

    public RaiseDeadAbility() {
        super("raise_dead", 60); // 60 second cooldown
    }

    @Override
    public void onActivate(Player player, Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            startCooldown();
            return;
        }

        BlockPos playerPos = player.blockPosition();
        int summoned = 0;

        // Determine summon type (mixed zombies and skeletons)
        for (int i = 0; i < MAX_SUMMONS; i++) {
            // Find valid spawn position around player
            double angle = (i / (double) MAX_SUMMONS) * Math.PI * 2 + Math.random() * 0.5;
            double distance = 2 + Math.random() * 2;
            double x = playerPos.getX() + Math.cos(angle) * distance;
            double z = playerPos.getZ() + Math.sin(angle) * distance;
            BlockPos spawnPos = BlockPos.containing(x, playerPos.getY(), z);

            // Find ground level
            while (!serverLevel.getBlockState(spawnPos).isAir() && spawnPos.getY() < playerPos.getY() + 5) {
                spawnPos = spawnPos.above();
            }
            while (serverLevel.getBlockState(spawnPos.below()).isAir() && spawnPos.getY() > playerPos.getY() - 5) {
                spawnPos = spawnPos.below();
            }

            // Alternate between zombie and skeleton
            if (i % 2 == 0) {
                Zombie zombie = EntityType.ZOMBIE.create(serverLevel, null, spawnPos, MobSpawnType.MOB_SUMMONED, false,
                        false);
                if (zombie != null) {
                    // Make the zombie serve the necromancer
                    zombie.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
                    zombie.setPersistenceRequired();
                    zombie.setCustomName(net.minecraft.network.chat.Component.literal(
                            ChatFormatting.DARK_PURPLE + "" + player.getName().getString() + "'s Minion"));

                    // Give themed equipment
                    zombie.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND,
                            new ItemStack(Items.IRON_SWORD));

                    // Mark as player's summon using tags
                    zombie.addTag("necromancer_summon");
                    zombie.addTag("owner:" + player.getUUID().toString());

                    // Set to not despawn for a while
                    zombie.setNoAi(false);

                    serverLevel.addFreshEntity(zombie);
                    summoned++;

                    // Spawn particles at summon location
                    spawnSummonParticles(serverLevel, spawnPos);
                }
            } else {
                Skeleton skeleton = EntityType.SKELETON.create(serverLevel, null, spawnPos, MobSpawnType.MOB_SUMMONED,
                        false, false);
                if (skeleton != null) {
                    skeleton.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
                    skeleton.setPersistenceRequired();
                    skeleton.setCustomName(net.minecraft.network.chat.Component.literal(
                            ChatFormatting.DARK_PURPLE + "" + player.getName().getString() + "'s Archer"));

                    // Give better bow
                    skeleton.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND,
                            new ItemStack(Items.BOW));

                    skeleton.addTag("necromancer_summon");
                    skeleton.addTag("owner:" + player.getUUID().toString());

                    serverLevel.addFreshEntity(skeleton);
                    summoned++;

                    spawnSummonParticles(serverLevel, spawnPos);
                }
            }
        }

        // Central summoning effect
        serverLevel.sendParticles(ParticleTypes.SOUL,
                playerPos.getX() + 0.5, playerPos.getY() + 0.5, playerPos.getZ() + 0.5,
                50, 2.0, 1.0, 2.0, 0.1);
        serverLevel.sendParticles(ParticleTypes.SMOKE,
                playerPos.getX() + 0.5, playerPos.getY(), playerPos.getZ() + 0.5,
                30, 2.0, 0.2, 2.0, 0.05);

        // Sound effects
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 0.5f, 1.5f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.PLAYERS, 1.0f, 0.8f);

        // Notify player
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                ChatFormatting.DARK_PURPLE + "You raise " + summoned + " undead minions from the grave!"));

        startCooldown();
    }

    private void spawnSummonParticles(ServerLevel level, BlockPos pos) {
        // Rising soul particles
        for (int y = 0; y < 3; y++) {
            level.sendParticles(ParticleTypes.SOUL,
                    pos.getX() + 0.5, pos.getY() + y * 0.5, pos.getZ() + 0.5,
                    5, 0.3, 0.1, 0.3, 0.02);
        }

        // Purple magic effect
        level.sendParticles(ParticleTypes.WITCH,
                pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5,
                10, 0.3, 0.5, 0.3, 0.05);

        // Ground smoke
        level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE,
                pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                5, 0.3, 0.1, 0.3, 0.01);
    }

    @Override
    public boolean canUse(Player player) {
        return !isOnCooldown();
    }

    @Override
    public int getResourceCost() {
        return RESOURCE_COST;
    }
}
