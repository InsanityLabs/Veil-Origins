package com.veilorigins.progression.legendary;

import com.veilorigins.progression.LegendaryAbility;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Wolfling Legendary: Pack Leader
 * Summon a small wolf pack and gain enhanced pack bonuses.
 */
public class WolflingLegendary extends LegendaryAbility {
    
    public WolflingLegendary() {
        super("pack_leader", "Pack Leader", 
            "Become the pack leader for 15 seconds. Summon wolves and gain pack bonuses.",
            200);
    }
    
    @Override
    protected void onActivate(Player player, Level level) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;
        
        player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 300, 1, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.SPEED, 300, 1, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 300, 0, false, true));
        
        // Summon 3 wolves using EntityType.create()
        for (int i = 0; i < 3; i++) {
            var wolf = EntityType.WOLF.create(serverLevel, null, player.blockPosition(), EntitySpawnReason.MOB_SUMMONED, false, false);
            if (wolf != null) {
                double angle = (Math.PI * 2 / 3) * i;
                wolf.setPos(player.getX() + Math.cos(angle) * 2, player.getY(), player.getZ() + Math.sin(angle) * 2);
                wolf.tame(player);
                level.addFreshEntity(wolf);
            }
        }
        
        serverLevel.sendParticles(ParticleTypes.SMOKE,
            player.getX(), player.getY() + 1, player.getZ(), 30, 1.0, 0.5, 1.0, 0.05);
        
        // Use ENDER_DRAGON_GROWL as WOLF_HOWL doesn't exist in 1.21.11
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.ENDER_DRAGON_GROWL, SoundSource.PLAYERS, 2.0f, 1.0f);
    }
    
    @Override
    public int getResourceCost() { return 40; }
}
