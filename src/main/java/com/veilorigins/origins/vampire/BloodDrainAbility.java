package com.veilorigins.origins.vampire;

import com.veilorigins.api.OriginAbility;
import com.veilorigins.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.ChatFormatting;

import java.util.List;

public class BloodDrainAbility extends OriginAbility {
    private static final int RESOURCE_COST = 0; // Free - this GIVES blood
    private static final float DRAIN_RANGE = 3.0f;
    private static final float DRAIN_DAMAGE = 2.0f;
    private static final float HEAL_AMOUNT = 2.0f;
    private static final float BLOOD_GAIN = 15.0f; // Gain 15 blood per drain

    public BloodDrainAbility() {
        super("blood_drain", 60);
    }

    @Override
    public void onActivate(Player player, Level level) {
        Vec3 playerPos = player.position();
        AABB searchBox = new AABB(playerPos.x - DRAIN_RANGE, playerPos.y - DRAIN_RANGE, playerPos.z - DRAIN_RANGE,
                playerPos.x + DRAIN_RANGE, playerPos.y + DRAIN_RANGE, playerPos.z + DRAIN_RANGE);

        List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, searchBox,
                entity -> entity != player && entity.isAlive());

        if (!nearbyEntities.isEmpty()) {
            LivingEntity target = nearbyEntities.get(0);
            
            target.hurt(level.damageSources().magic(), DRAIN_DAMAGE);
            player.heal(HEAL_AMOUNT);
            
            // Add blood to the vampire's resource bar
            com.veilorigins.data.OriginData.PlayerOriginData data = 
                player.getData(com.veilorigins.data.OriginData.PLAYER_ORIGIN);
            data.addResource(BLOOD_GAIN);
            
            // Check if draining an animal and holding empty blood bottle in offhand
            boolean filledBottle = false;
            if (target instanceof Animal) {
                ItemStack offhand = player.getOffhandItem();
                if (offhand.getItem() == ModItems.BLOOD_BOTTLE_EMPTY.get()) {
                    // Fill the bottle - empty -> half, half -> full
                    offhand.shrink(1);
                    ItemStack filledStack = new ItemStack(ModItems.BLOOD_BOTTLE_HALF.get());
                    if (!player.getInventory().add(filledStack)) {
                        player.drop(filledStack, false);
                    }
                    filledBottle = true;
                } else if (offhand.getItem() == ModItems.BLOOD_BOTTLE_HALF.get()) {
                    // Upgrade half to full
                    offhand.shrink(1);
                    ItemStack filledStack = new ItemStack(ModItems.BLOOD_BOTTLE_FULL.get());
                    if (!player.getInventory().add(filledStack)) {
                        player.drop(filledStack, false);
                    }
                    filledBottle = true;
                }
            }

            if (level instanceof ServerLevel serverLevel) {
                Vec3 targetPos = target.position().add(0, target.getBbHeight() / 2, 0);
                Vec3 playerEyePos = player.position().add(0, player.getEyeHeight(), 0);
                
                for (int i = 0; i < 20; i++) {
                    double progress = i / 20.0;
                    Vec3 particlePos = targetPos.lerp(playerEyePos, progress);
                    serverLevel.sendParticles(ParticleTypes.CRIMSON_SPORE,
                            particlePos.x, particlePos.y, particlePos.z, 1, 0.1, 0.1, 0.1, 0.01);
                }
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0f, 0.8f);
            
            String message = ChatFormatting.DARK_RED + "+" + (int)BLOOD_GAIN + " Blood";
            if (filledBottle) {
                message += ChatFormatting.GRAY + " (Bottle filled!)";
            }
            player.displayClientMessage(
                net.minecraft.network.chat.Component.literal(message),
                true);
        }

        startCooldown();
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
