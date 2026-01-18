package com.veilorigins.progression.legendary;

import com.veilorigins.progression.LegendaryAbility;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import java.util.List;

/** Necromancer Legendary: Army of the Dead - Raise a massive undead army to overwhelm enemies. */
public class NecromancerLegendary extends LegendaryAbility {
    public NecromancerLegendary() {
        super("army_of_dead", "Army of the Dead", "Raise a massive undead army to overwhelm your enemies.", 360);
    }
    
    @Override
    protected void onActivate(Player player, Level level) {
        if (level.isClientSide()) return;
        ServerLevel serverLevel = (ServerLevel) level;
        
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 400, 1, false, true));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 1, false, true));
        
        // Summon massive undead army
        for (int i = 0; i < 8; i++) {
            double angle = (Math.PI * 2 / 8) * i;
            double x = player.getX() + Math.cos(angle) * 4;
            double z = player.getZ() + Math.sin(angle) * 4;
            
            if (i % 2 == 0) {
                Zombie zombie = EntityType.ZOMBIE.create(serverLevel);
                if (zombie != null) {
                    zombie.setPos(x, player.getY(), z);
                    zombie.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
                    zombie.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 1));
                    zombie.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 600, 1));
                    level.addFreshEntity(zombie);
                }
            } else {
                Skeleton skeleton = EntityType.SKELETON.create(serverLevel);
                if (skeleton != null) {
                    skeleton.setPos(x, player.getY(), z);
                    skeleton.setItemSlot(net.minecraft.world.entity.EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
                    skeleton.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 600, 1));
                    level.addFreshEntity(skeleton);
                }
            }
        }
        
        // Death wave damage
        AABB area = player.getBoundingBox().inflate(15);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area, 
            e -> e != player && !e.getType().equals(EntityType.ZOMBIE) && !e.getType().equals(EntityType.SKELETON));
        
        for (LivingEntity entity : entities) {
            entity.hurt(level.damageSources().wither(), 10.0f);
            entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 200, 1));
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 200, 2));
        }
        
        // Death particles
        serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, player.getX(), player.getY() + 1, player.getZ(), 150, 6.0, 2.0, 6.0, 0.1);
        serverLevel.sendParticles(ParticleTypes.SMOKE, player.getX(), player.getY(), player.getZ(), 200, 8.0, 1.0, 8.0, 0.05);
        
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WITHER_SPAWN, SoundSource.PLAYERS, 1.5f, 0.7f);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.WARDEN_ROAR, SoundSource.PLAYERS, 1.0f, 0.5f);
    }
    
    @Override
    public int getResourceCost() { return 70; }
}
