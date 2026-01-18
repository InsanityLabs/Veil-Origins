package com.veilorigins.progression;

import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.progression.legendary.LegendaryAbilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Handles XP awards for various player actions.
 * Each origin has unique XP sources based on their theme.
 */
@EventBusSubscriber(modid = VeilOrigins.MOD_ID)
public class ProgressionEventHandler {
    
    private static int tickCounter = 0;
    
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (!(player instanceof ServerPlayer serverPlayer)) return;
        
        tickCounter++;
        
        // Tick hybrid system
        HybridSystem.tickPlayer(player);
        
        // Tick legendary ability cooldowns (every tick)
        LegendaryAbilityRegistry.tickCooldowns();
        
        // Every 60 ticks (3 seconds), check for passive XP gains
        if (tickCounter % 60 == 0) {
            awardPassiveXP(serverPlayer);
        }
        
        // Every 100 ticks (5 seconds), tick hybrid system global
        if (tickCounter % 100 == 0) {
            HybridSystem.tick();
        }
        
        // Reset counter to prevent overflow
        if (tickCounter >= 6000) {
            tickCounter = 0;
        }
    }
    
    @SubscribeEvent
    public static void onEntityKill(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            LivingEntity killed = event.getEntity();
            Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
            
            if (origin == null) return;
            
            String originId = origin.getId().getPath();
            int xp = OriginXPSources.getKillXP(originId, killed);
            
            if (xp > 0) {
                ProgressionSystem.awardXP(player, xp, "kill_" + killed.getType().toShortString());
            }
        }
    }
    
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;
        
        Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
        if (origin == null) return;
        
        String originId = origin.getId().getPath();
        Block block = event.getState().getBlock();
        int xp = OriginXPSources.getMineXP(originId, block);
        
        if (xp > 0) {
            ProgressionSystem.awardXP(player, xp, "mine_" + block.getName().getString());
        }
    }
    
    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickItem event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (event.getLevel().isClientSide()) return;
        
        Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
        if (origin == null) return;
        
        String originId = origin.getId().getPath();
        Item item = event.getItemStack().getItem();
        int xp = OriginXPSources.getConsumeXP(originId, item);
        
        if (xp > 0) {
            ProgressionSystem.awardXP(player, xp, "use_" + item.toString());
        }
    }
    
    /**
     * Award passive XP based on origin-themed environmental conditions.
     */
    private static void awardPassiveXP(ServerPlayer player) {
        Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
        if (origin == null) return;
        
        String originId = origin.getId().getPath();
        Level level = player.level();
        BlockPos pos = player.blockPosition();
        
        // Build context
        OriginXPSources.PassiveContext ctx = new OriginXPSources.PassiveContext();
        
        // Time of day
        long dayTime = level.getDayTime() % 24000;
        ctx.isDay = dayTime < 12500 || dayTime > 23500;
        ctx.isNight = !ctx.isDay;
        // Full moon is when moon phase is 0 (calculate from day count)
        ctx.isFullMoon = (level.getDayTime() / 24000L) % 8 == 0;
        
        // Sky and weather
        ctx.canSeeSky = level.canSeeSky(pos);
        ctx.inRain = level.isRaining() && ctx.canSeeSky;
        ctx.inSnow = level.isRaining() && ctx.canSeeSky && level.getBiome(pos).value().coldEnoughToSnow(pos, level.getSeaLevel());
        
        // Water
        ctx.inWater = player.isInWater();
        ctx.inDeepWater = player.isUnderWater() && player.getY() < level.getSeaLevel() - 10;
        
        // Fire and lava
        ctx.onFire = player.isOnFire();
        ctx.nearLava = isNearBlock(level, pos, Blocks.LAVA, 3);
        
        // Dimensions
        ctx.inNether = level.dimension() == Level.NETHER;
        ctx.inEnd = level.dimension() == Level.END;
        ctx.nearVoid = player.getY() < 0;
        
        // Altitude
        ctx.underground = player.getY() < 50 && !ctx.canSeeSky;
        ctx.deepUnderground = player.getY() < 0;
        ctx.highAltitude = player.getY() > 150;
        ctx.veryHighAltitude = player.getY() > 200;
        ctx.isFlying = player.getAbilities().flying;
        
        // Biome
        Biome biome = level.getBiome(pos).value();
        ctx.inColdBiome = biome.coldEnoughToSnow(pos, level.getSeaLevel());
        // Check if in forest-like biome (has trees)
        ctx.inForest = level.getBiome(pos).toString().toLowerCase().contains("forest");
        
        // Light level
        int lightLevel = level.getBrightness(LightLayer.BLOCK, pos);
        ctx.lowLight = lightLevel < 7;
        
        // Nearby entities/blocks
        ctx.nearUndead = !level.getEntitiesOfClass(Monster.class, player.getBoundingBox().inflate(10), 
            e -> e.getType() == net.minecraft.world.entity.EntityType.ZOMBIE || 
                 e.getType() == net.minecraft.world.entity.EntityType.SKELETON).isEmpty();
        ctx.nearAmethyst = isNearBlock(level, pos, Blocks.AMETHYST_CLUSTER, 5) || 
                          isNearBlock(level, pos, Blocks.BUDDING_AMETHYST, 5);
        ctx.nearRedstone = isNearBlock(level, pos, Blocks.REDSTONE_WIRE, 3) ||
                          isNearBlock(level, pos, Blocks.REDSTONE_BLOCK, 5);
        
        // Get passive XP
        OriginXPSources.PassiveXPResult result = OriginXPSources.getPassiveXP(originId, ctx);
        
        if (result.xp > 0) {
            ProgressionSystem.awardXP(player, result.xp, result.reason);
        }
    }
    
    /**
     * Check if a specific block is near the player.
     */
    private static boolean isNearBlock(Level level, BlockPos center, Block block, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if (level.getBlockState(center.offset(x, y, z)).is(block)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
