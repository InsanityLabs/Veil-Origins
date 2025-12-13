package com.veilorigins.origins.starborne;

import com.veilorigins.api.OriginPassive;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.phys.Vec3;

public class WingsOfLightPassive extends OriginPassive {
    private int flightTickCounter = 0;
    private static final int HUNGER_DRAIN_INTERVAL = 200; // 10 seconds = 200 ticks
    private static final int HUNGER_DRAIN_AMOUNT = 2; // 1 food icon = 2 hunger points

    public WingsOfLightPassive() {
        super("wings_of_light");
    }

    @Override
    public void onTick(Player player) {
        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        // Grant flight ability if not already granted
        if (!player.getAbilities().mayfly) {
            player.getAbilities().mayfly = true;
            player.onUpdateAbilities();
        }

        FoodData food = player.getFoodData();

        // Handle hunger drain while flying
        if (player.getAbilities().flying) {
            flightTickCounter++;

            // Drain 1 food icon (2 hunger points) every 10 seconds
            if (flightTickCounter >= HUNGER_DRAIN_INTERVAL) {
                flightTickCounter = 0;

                int currentFood = food.getFoodLevel();

                // Drain hunger
                if (currentFood > 0) {
                    food.setFoodLevel(Math.max(0, currentFood - HUNGER_DRAIN_AMOUNT));

                    // Warning when food is low
                    if (currentFood <= 6 && currentFood > 0) {
                        player.sendSystemMessage(
                                Component.literal("§eYour wings of light are draining your energy..."));
                    }
                }

                // If out of food, disable flight
                if (food.getFoodLevel() <= 0) {
                    player.sendSystemMessage(Component.literal("§c§lYou're too hungry to maintain flight!"));
                    player.getAbilities().flying = false;
                    player.onUpdateAbilities();

                    // Give fall damage warning
                    if (!player.onGround() && player.getY() > player.level().getMinBuildHeight() + 5) {
                        player.sendSystemMessage(Component.literal("§cFind ground quickly!"));
                    }
                }
            }
        } else {
            // Reset counter when not flying
            flightTickCounter = 0;
        }
    }

    @Override
    public void onEquip(Player player) {
        if (!player.isCreative() && !player.isSpectator()) {
            player.getAbilities().mayfly = true;
            player.onUpdateAbilities();
            player.sendSystemMessage(
                    Component.literal("§e§lWings of Light: §rYou can fly! §7(Drains 1 hunger every 10 seconds)"));
        }
    }

    @Override
    public void onRemove(Player player) {
        if (!player.isCreative() && !player.isSpectator()) {
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }
        flightTickCounter = 0;
    }
}
