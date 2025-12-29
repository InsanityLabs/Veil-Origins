package com.veilorigins.origins.stoneheart;

import com.veilorigins.api.OriginPassive;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.ChatFormatting;

public class StoneheartWeaknessesPassive extends OriginPassive {
    private int tickCounter = 0;
    private boolean hasWarnedElytra = false;

    public StoneheartWeaknessesPassive() {
        super("stoneheart_weaknesses");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;
        Level level = player.level();

        // Sink in water like an anvil EVERY TICK (cannot swim at all)
        if (player.isInWater() && !player.onGround()) {
            // Force player out of swimming state
            player.setSwimming(false);

            // Get current movement
            double currentY = player.getDeltaMovement().y;

            // Clamp any upward movement - only allow downward or neutral
            double newY = Math.min(currentY, -0.08);

            // Apply strong downward velocity, reduce horizontal movement significantly
            player.setDeltaMovement(
                    player.getDeltaMovement().x * 0.4,
                    newY - 0.05, // Additional downward pull each tick
                    player.getDeltaMovement().z * 0.4);
        }

        // Elytra flight impossible EVERY TICK (too heavy - cannot bypass by spamming)
        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chestplate.is(Items.ELYTRA) && player.isFallFlying()) {
            player.stopFallFlying();
            // Only show warning once per second to avoid chat spam
            if (!hasWarnedElytra) {
                player.displayClientMessage(
                        Component.literal(ChatFormatting.RED + "You're too heavy to fly with elytra!"), false);
                hasWarnedElytra = true;
            }
        } else if (!player.isFallFlying()) {
            hasWarnedElytra = false;
        }

        // Check every 20 ticks (1 second) for other periodic checks
        if (tickCounter >= 20) {
            tickCounter = 0;
        }

        // Cannot use boats properly (too heavy) - boat cannot move, sinks in water
        if (player.isPassenger() && player.getVehicle() != null) {
            if (player.getVehicle().getType().toString().contains("boat")) {
                var vehicle = player.getVehicle();

                // If boat is in water, it sinks - eject player and destroy boat
                if (vehicle.isInWater()) {
                    player.stopRiding();
                    // In 1.21.10, kill requires ServerLevel
                    if (level instanceof ServerLevel serverLevel) {
                        vehicle.kill(serverLevel);
                    }
                    player.displayClientMessage(
                            Component.literal(ChatFormatting.RED + "You're too heavy! The boat sinks beneath you!"), false);
                } else {
                    // On land, boat just can't move - freeze it in place
                    vehicle.setDeltaMovement(0, vehicle.getDeltaMovement().y, 0);
                }
            }
        }
    }

    @Override
    public void onEquip(Player player) {
        player.displayClientMessage(Component
                .literal(ChatFormatting.GRAY + "As Stoneheart, you cannot swim, use boats, or fly with elytra."), false);
    }

    @Override
    public void onRemove(Player player) {
        // Called when player changes from Stoneheart origin
    }
}
