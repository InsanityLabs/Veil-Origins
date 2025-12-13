package com.veilorigins.origins.frostborn;

import com.veilorigins.api.OriginPassive;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Random;

public class ColdAuraPassive extends OriginPassive {
    private int tickCounter = 0;
    private final Random random = new Random();

    public ColdAuraPassive() {
        super("cold_aura");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;
        Level level = player.level();
        
        // Check every 20 ticks (1 second)
        if (tickCounter >= 20) {
            tickCounter = 0;
            
            // Nearby enemies (5 blocks) have Slowness I
            AABB area = new AABB(
                player.getX() - 5, player.getY() - 3, player.getZ() - 5,
                player.getX() + 5, player.getY() + 3, player.getZ() + 5
            );
            
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area,
                entity -> entity != player);
            
            for (LivingEntity entity : entities) {
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 0, false, false));
            }
            
            // Fire in vicinity has 50% chance to extinguish
            BlockPos playerPos = player.blockPosition();
            for (int x = -3; x <= 3; x++) {
                for (int y = -2; y <= 2; y++) {
                    for (int z = -3; z <= 3; z++) {
                        BlockPos checkPos = playerPos.offset(x, y, z);
                        if (level.getBlockState(checkPos).is(Blocks.FIRE)) {
                            if (random.nextDouble() < 0.5) {
                                level.removeBlock(checkPos, false);
                            }
                        }
                    }
                }
            }
        }
        
        // Note: Crops grow slower is a complex mechanic - skipping for now
        // Note: Campfires/torches flicker is visual only - skipping for now
    }

    @Override
    public void onEquip(Player player) {
        // Called when player selects Frostborn origin
    }

    @Override
    public void onRemove(Player player) {
        // Called when player changes from Frostborn origin
    }
}
