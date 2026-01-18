package com.veilorigins.progression.legendary;

import com.veilorigins.progression.LegendaryAbility;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import java.util.List;

/** Feralkin Legendary: Apex Predator - Ultimate beast transformation with devastating attacks. */
public class FeralkinLegendary extends LegendaryAbility {
    public FeralkinLegendary() {
        super("apex_predator", "Apex Predator", "Transform into the ultimate predator for 20 seconds.", 240);
    }
    
    @Override
    protected void onActivate(Player player, Level level) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;
        
        player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 400, 3, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.SPEED, 400, 2, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 400, 3, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, false, true));
        
        // Terrifying roar
        AABB area = player.getBoundingBox().inflate(15);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, e -> e != player);
        for (LivingEntity entity : entities) {
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 2));
            entity.hurt(level.damageSources().sonicBoom(player), 6.0f);
        }
        
        serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER, player.getX(), player.getY() + 1, player.getZ(), 30, 1.5, 1.0, 1.5, 0.1);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.RAVAGER_ROAR, SoundSource.PLAYERS, 2.0f, 0.7f);
    }
    
    @Override
    public int getResourceCost() { return 50; }
}
