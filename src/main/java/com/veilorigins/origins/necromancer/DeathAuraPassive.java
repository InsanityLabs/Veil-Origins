package com.veilorigins.origins.necromancer;

import com.veilorigins.api.OriginPassive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;

import java.util.List;
import net.minecraft.ChatFormatting;

public class DeathAuraPassive extends OriginPassive {
    private int tickCounter = 0;
    private static final double AURA_RADIUS = 5.0;

    public DeathAuraPassive() {
        super("death_aura");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;
        Level level = player.level();
        BlockPos playerPos = player.blockPosition();

        // Every 20 ticks (1 second)
        if (tickCounter >= 20) {
            tickCounter = 0;

            // Check if it's night time
            boolean isNight = !(level.getDayTime() < 13000);

            // Night time bonuses
            if (isNight) {
                // Speed and strength at night
                player.addEffect(new MobEffectInstance(MobEffects.SPEED, 40, 0, true, false));
                player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 40, 0, true, false));
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, true, false));
            }

            // In darkness, the aura is stronger
            int lightLevel = level.getBrightness(LightLayer.BLOCK, playerPos);
            int skyLight = level.getBrightness(LightLayer.SKY, playerPos);
            boolean inDarkness = lightLevel < 7 && skyLight < 7;

            // Death aura damages nearby enemies (except undead)
            AABB auraArea = new AABB(playerPos).inflate(AURA_RADIUS);
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, auraArea,
                    entity -> entity != player && entity.isAlive() && !isUndead(entity) && isHostile(entity));

            float auraDamage = inDarkness ? 1.0f : 0.5f;

            for (LivingEntity entity : entities) {
                // The aura drains life from living enemies
                entity.hurt(level.damageSources().wither(), auraDamage);

                // Visual effect
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.SOUL,
                            entity.getX(), entity.getY() + entity.getBbHeight() / 2, entity.getZ(),
                            3, 0.2, 0.3, 0.2, 0.02);
                }
            }

            // Aura visual particles around player
            if (level instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 8; i++) {
                    double angle = (i / 8.0) * Math.PI * 2;
                    double radius = AURA_RADIUS * 0.8;
                    double x = player.getX() + Math.cos(angle) * radius;
                    double z = player.getZ() + Math.sin(angle) * radius;

                    serverLevel.sendParticles(ParticleTypes.SOUL,
                            x, player.getY() + 0.1, z, 1, 0.1, 0.1, 0.1, 0.01);
                }

                // Rising soul particles
                if (Math.random() < 0.3) {
                    serverLevel.sendParticles(ParticleTypes.SOUL,
                            player.getX(), player.getY() + 0.5, player.getZ(),
                            2, 0.3, 0.5, 0.3, 0.02);
                }
            }
        }

        // Constant ambient particles (every 5 ticks)
        if (tickCounter % 5 == 0 && level instanceof ServerLevel serverLevel) {
            if (Math.random() < 0.2) {
                double offsetX = (Math.random() - 0.5) * 1.5;
                double offsetZ = (Math.random() - 0.5) * 1.5;
                serverLevel.sendParticles(ParticleTypes.MYCELIUM,
                        player.getX() + offsetX, player.getY() + 0.1, player.getZ() + offsetZ,
                        1, 0.1, 0.1, 0.1, 0.01);
            }
        }
    }

    private boolean isUndead(LivingEntity entity) {
        return entity.isInvertedHealAndHarm();
    }

    private boolean isHostile(LivingEntity entity) {
        return entity.getType().getCategory().getName().equals("monster");
    }

    @Override
    public void onEquip(Player player) {
        player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                ChatFormatting.DARK_PURPLE + "Your death aura damages nearby enemies. You are stronger at night."), false);
    }

    @Override
    public void onRemove(Player player) {
        // Remove night vision when leaving origin
        player.removeEffect(MobEffects.NIGHT_VISION);
    }
}
