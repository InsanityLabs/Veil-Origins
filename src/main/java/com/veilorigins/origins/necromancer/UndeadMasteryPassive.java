package com.veilorigins.origins.necromancer;

import com.veilorigins.api.OriginPassive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class UndeadMasteryPassive extends OriginPassive {
    private int tickCounter = 0;

    public UndeadMasteryPassive() {
        super("undead_mastery");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;
        Level level = player.level();
        BlockPos playerPos = player.blockPosition();

        // Every 10 ticks
        if (tickCounter >= 10) {
            tickCounter = 0;

            // Find undead mobs in radius
            AABB searchArea = new AABB(playerPos).inflate(16);

            // Find zombies
            List<Zombie> zombies = level.getEntitiesOfClass(Zombie.class, searchArea);
            for (Zombie zombie : zombies) {
                // Don't attack the necromancer
                if (zombie.getTarget() == player) {
                    zombie.setTarget(null);
                }

                // Summoned minions get buffs and follow player
                if (zombie.getTags().contains("necromancer_summon") &&
                        zombie.getTags().contains("owner:" + player.getUUID().toString())) {

                    // Minions try to stay near player but attack player's target
                    if (player.getLastHurtMob() != null && player.getLastHurtMob().isAlive()) {
                        zombie.setTarget(player.getLastHurtMob());
                    } else if (zombie.distanceToSqr(player) > 100) { // More than 10 blocks away
                        // Move toward player
                        zombie.getNavigation().moveTo(player, 1.0);
                    }

                    // Ambient particles on minions
                    if (level instanceof ServerLevel serverLevel && Math.random() < 0.1) {
                        serverLevel.sendParticles(ParticleTypes.SOUL,
                                zombie.getX(), zombie.getY() + zombie.getBbHeight(), zombie.getZ(),
                                1, 0.2, 0.1, 0.2, 0.01);
                    }
                }
            }

            // Find skeletons
            List<AbstractSkeleton> skeletons = level.getEntitiesOfClass(AbstractSkeleton.class, searchArea);
            for (AbstractSkeleton skeleton : skeletons) {
                if (skeleton.getTarget() == player) {
                    skeleton.setTarget(null);
                }

                if (skeleton.getTags().contains("necromancer_summon") &&
                        skeleton.getTags().contains("owner:" + player.getUUID().toString())) {

                    if (player.getLastHurtMob() != null && player.getLastHurtMob().isAlive()) {
                        skeleton.setTarget(player.getLastHurtMob());
                    } else if (skeleton.distanceToSqr(player) > 100) {
                        skeleton.getNavigation().moveTo(player, 1.0);
                    }

                    if (level instanceof ServerLevel serverLevel && Math.random() < 0.1) {
                        serverLevel.sendParticles(ParticleTypes.SOUL,
                                skeleton.getX(), skeleton.getY() + skeleton.getBbHeight(), skeleton.getZ(),
                                1, 0.2, 0.1, 0.2, 0.01);
                    }
                }
            }

            // Count active minions for potential bonuses
            int activeMinions = 0;
            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, searchArea)) {
                if (entity.getTags().contains("necromancer_summon") &&
                        entity.getTags().contains("owner:" + player.getUUID().toString())) {
                    activeMinions++;
                }
            }

            // More minions = more power particles around player
            if (activeMinions > 0 && level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SOUL,
                        player.getX(), player.getY() + 1, player.getZ(),
                        activeMinions, 0.5, 0.3, 0.5, 0.02);
            }
        }
    }

    @Override
    public void onEquip(Player player) {
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                "§5As a Necromancer, undead creatures will not harm you. You command the dead."));
    }

    @Override
    public void onRemove(Player player) {
        // Clean up summoned minions
        Level level = player.level();
        if (level instanceof ServerLevel serverLevel) {
            AABB searchArea = new AABB(player.blockPosition()).inflate(64);
            for (LivingEntity entity : serverLevel.getEntitiesOfClass(LivingEntity.class, searchArea)) {
                if (entity.getTags().contains("necromancer_summon") &&
                        entity.getTags().contains("owner:" + player.getUUID().toString())) {
                    entity.discard();
                }
            }
        }
    }
}
