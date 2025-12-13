package com.veilorigins.origins.umbrakin;

import com.veilorigins.api.OriginPassive;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class PhotophobiaPassive extends OriginPassive {
    private int tickCounter = 0;

    public PhotophobiaPassive() {
        super("photophobia");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;
        Level level = player.level();
        
        // Check every 20 ticks (1 second)
        if (tickCounter >= 20) {
            tickCounter = 0;
            
            int lightLevel = level.getMaxLocalRawBrightness(player.blockPosition());
            
            // Sunlight damage (1 HP per second)
            if (level.isDay() && level.canSeeSky(player.blockPosition()) && lightLevel >= 12) {
                // Check if wearing helmet
                if (player.getInventory().getArmor(3).isEmpty()) {
                    player.hurt(level.damageSources().onFire(), 1.0f);
                }
            }
            
            // Light level above 12 applies Weakness I
            if (lightLevel >= 12) {
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 0, false, false));
            }
            
            // Torches/lanterns nearby cause discomfort
            if (hasNearbyLightSources(player, level)) {
                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 40, 0, false, false));
            }
            
            // Glowstone/sea lanterns deal proximity damage
            if (hasNearbyBrightBlocks(player, level)) {
                player.hurt(level.damageSources().magic(), 0.5f);
            }
        }
        
        // Note: "Cannot place light sources above light level 7" would require
        // a block placement event handler - this is handled in the event system
    }

    private boolean hasNearbyLightSources(Player player, Level level) {
        BlockPos pos = player.blockPosition();
        
        for (int x = -3; x <= 3; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -3; z <= 3; z++) {
                    BlockPos checkPos = pos.offset(x, y, z);
                    if (level.getBlockState(checkPos).is(Blocks.TORCH) ||
                        level.getBlockState(checkPos).is(Blocks.WALL_TORCH) ||
                        level.getBlockState(checkPos).is(Blocks.LANTERN) ||
                        level.getBlockState(checkPos).is(Blocks.SOUL_LANTERN)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean hasNearbyBrightBlocks(Player player, Level level) {
        BlockPos pos = player.blockPosition();
        
        for (int x = -3; x <= 3; x++) {
            for (int y = -2; y <= 2; y++) {
                for (int z = -3; z <= 3; z++) {
                    BlockPos checkPos = pos.offset(x, y, z);
                    if (level.getBlockState(checkPos).is(Blocks.GLOWSTONE) ||
                        level.getBlockState(checkPos).is(Blocks.SEA_LANTERN)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void onEquip(Player player) {
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal("§8As Umbrakin, sunlight burns you and bright lights cause discomfort."));
    }

    @Override
    public void onRemove(Player player) {
        player.removeEffect(MobEffects.WEAKNESS);
        player.removeEffect(MobEffects.CONFUSION);
    }
}
