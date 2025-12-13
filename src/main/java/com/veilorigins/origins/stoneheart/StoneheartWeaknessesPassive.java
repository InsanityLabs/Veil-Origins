package com.veilorigins.origins.stoneheart;

import com.veilorigins.api.OriginPassive;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

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
        
        // Check every 20 ticks (1 second)
        if (tickCounter >= 20) {
            tickCounter = 0;
            
            // Sink in water like an anvil (cannot swim)
            if (player.isInWater() && !player.onGround()) {
                // Apply strong downward velocity
                player.setDeltaMovement(player.getDeltaMovement().x * 0.5, -0.5, player.getDeltaMovement().z * 0.5);
                
                // Slow horizontal movement in water (75% slower)
                if (player.isSwimming()) {
                    player.setDeltaMovement(
                        player.getDeltaMovement().x * 0.25,
                        player.getDeltaMovement().y,
                        player.getDeltaMovement().z * 0.25
                    );
                }
            }
            
            // Elytra flight impossible (too heavy)
            ItemStack chestplate = player.getInventory().getArmor(2);
            if (chestplate.getItem() instanceof ElytraItem && player.isFallFlying()) {
                player.stopFallFlying();
                if (!hasWarnedElytra) {
                    player.sendSystemMessage(Component.literal("§cYou're too heavy to fly with elytra!"));
                    hasWarnedElytra = true;
                }
            } else if (!player.isFallFlying()) {
                hasWarnedElytra = false;
            }
        }
        
        // Cannot use boats (too heavy) - eject from boat
        if (player.isPassenger() && player.getVehicle() != null) {
            if (player.getVehicle().getType().toString().contains("boat")) {
                // Store vehicle reference before stopping riding
                var vehicle = player.getVehicle();
                player.stopRiding();
                player.sendSystemMessage(Component.literal("§cYou're too heavy for boats!"));
                // Sink the boat
                if (vehicle != null) {
                    vehicle.kill();
                }
            }
        }
    }

    @Override
    public void onEquip(Player player) {
        player.sendSystemMessage(Component.literal("§7As Stoneheart, you cannot swim, use boats, or fly with elytra."));
    }

    @Override
    public void onRemove(Player player) {
        // Called when player changes from Stoneheart origin
    }
}
