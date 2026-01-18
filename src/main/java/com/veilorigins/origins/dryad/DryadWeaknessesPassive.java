package com.veilorigins.origins.dryad;

import com.veilorigins.api.OriginPassive;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.ChatFormatting;

public class DryadWeaknessesPassive extends OriginPassive {
    private int tickCounter = 0;
    private int damageTickCounter = 0;

    public DryadWeaknessesPassive() {
        super("dryad_weaknesses");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;
        Level level = player.level();
        BlockPos playerPos = player.blockPosition();

        // Every 20 ticks (1 second)
        if (tickCounter >= 20) {
            tickCounter = 0;

            // Check if on fire - fire deals extra damage (handled in event handler for
            // damage modification)
            // This is just for visual/effect feedback
            if (player.isOnFire()) {
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 1, true, false));
            }

            // Check if in desert/dry biome - take damage
            if (isInDryBiome(level, playerPos)) {
                damageTickCounter++;
                // Take 0.5 HP damage every 2 seconds in dry biomes
                if (damageTickCounter >= 2) {
                    damageTickCounter = 0;
                    player.hurt(level.damageSources().dryOut(), 0.5f);
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0, true, false));
                }
            } else {
                damageTickCounter = 0;
            }

            // Check if in Nether - constant damage
            if (level.dimension() == Level.NETHER) {
                player.hurt(level.damageSources().onFire(), 1.0f);
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 1, true, false));
                player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 0, true, false));
            }

            // Check for complete darkness - wither effect
            int lightLevel = level.getBrightness(LightLayer.BLOCK, playerPos);
            int skyLight = level.getBrightness(LightLayer.SKY, playerPos);
            if (lightLevel <= 1 && skyLight <= 1 && !(level.getDayTime() < 13000)) {
                // Complete darkness - plant wilts
                player.addEffect(new MobEffectInstance(MobEffects.WITHER, 40, 0, true, false));
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0, true, false));
            }
        }
    }

    private boolean isInDryBiome(Level level, BlockPos pos) {
        var biomeHolder = level.getBiome(pos);

        // Check desert tag
        // Unfortunately there's no IS_DESERT tag, so we check by name
        Identifier biomeKey = level.registryAccess()
                .lookup(Registries.BIOME).orElseThrow()
                .getKey(biomeHolder.value());
        if (biomeKey != null) {
            String biomeName = biomeKey.getPath().toLowerCase();
            return biomeName.contains("desert") ||
                    biomeName.contains("badlands") ||
                    biomeName.contains("mesa") ||
                    biomeName.contains("savanna"); // Savannas are dry too
        }
        return false;
    }

    @Override
    public void onEquip(Player player) {
        player.displayClientMessage(net.minecraft.network.chat.Component.literal(
                ChatFormatting.RED
                        + "Warning: As a Dryad, fire deals 50% more damage. Desert biomes and the Nether are hostile to you."), false);
    }

    @Override
    public void onRemove(Player player) {
        damageTickCounter = 0;
    }
}
