package com.veilorigins.event;

import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.config.VeilOriginsConfig;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

/**
 * Handles player size scaling based on their origin.
 * Uses Minecraft 1.21's native SCALE attribute.
 */
@EventBusSubscriber(modid = VeilOrigins.MOD_ID)
public class OriginSizeHandler {

    // Unique ID for our scale modifier
    private static final Identifier ORIGIN_SCALE_MODIFIER_ID = Identifier
            .fromNamespaceAndPath(VeilOrigins.MOD_ID, "origin_scale");

    // Cache to track if we've applied the scale this session
    private static final java.util.Map<java.util.UUID, String> lastAppliedOrigin = new java.util.HashMap<>();

    /**
     * Apply scaling when a player logs in
     */
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide()) {
            applyOriginScale(player);
        }
    }

    /**
     * Apply scaling when a player respawns
     */
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide()) {
            applyOriginScale(player);
        }
    }

    /**
     * Apply scaling when a player changes dimensions
     */
    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide()) {
            applyOriginScale(player);
        }
    }

    /**
     * Periodically check and update scale (in case origin changes)
     * This runs less frequently to avoid performance issues
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide())
            return;

        // Only check every 20 ticks (1 second) to save performance
        if (player.tickCount % 20 != 0)
            return;

        Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
        String currentOriginPath = origin != null ? origin.getId().getPath() : "";
        String lastOrigin = lastAppliedOrigin.get(player.getUUID());

        // Only update if origin changed
        if (!currentOriginPath.equals(lastOrigin)) {
            applyOriginScale(player);
        }
    }

    /**
     * Apply the appropriate scale based on the player's origin
     */
    public static void applyOriginScale(Player player) {
        Origin origin = VeilOriginsAPI.getPlayerOrigin(player);

        // Get the target scale
        double targetScale;
        String originPath;

        if (origin != null) {
            originPath = origin.getId().getPath();
            targetScale = VeilOriginsConfig.getOriginSize(originPath);
        } else {
            originPath = "";
            targetScale = 1.0;
        }

        // Update scale attribute
        setPlayerScale(player, targetScale);

        // Cache the applied origin
        lastAppliedOrigin.put(player.getUUID(), originPath);

        if (origin != null && targetScale != 1.0) {
            VeilOrigins.LOGGER.debug("Applied scale {} to player {} with origin {}",
                    targetScale, player.getName().getString(), originPath);
        }
    }

    /**
     * Set the player's scale using the native SCALE attribute
     */
    private static void setPlayerScale(Player player, double scale) {
        // Get the SCALE attribute - this exists natively in MC 1.21+
        AttributeInstance scaleAttribute = player.getAttribute(Attributes.SCALE);

        if (scaleAttribute == null) {
            VeilOrigins.LOGGER.warn("SCALE attribute not found for player {}", player.getName().getString());
            return;
        }

        // Remove any existing origin scale modifier
        AttributeModifier existingModifier = scaleAttribute.getModifier(ORIGIN_SCALE_MODIFIER_ID);
        if (existingModifier != null) {
            scaleAttribute.removeModifier(ORIGIN_SCALE_MODIFIER_ID);
        }

        // If scale is 1.0, we don't need to add any modifier (use base scale)
        if (Math.abs(scale - 1.0) < 0.001) {
            return;
        }

        // Create a new modifier
        // The modifier value is (scale - 1.0) because we're adding to the base value of
        // 1.0
        // Using MULTIPLY_BASE operation: newValue = baseValue * (1 + modifier)
        // So for scale 0.85, we want baseValue * 0.85, meaning modifier = 0.85 - 1 =
        // -0.15
        double modifierValue = scale - 1.0;

        AttributeModifier scaleModifier = new AttributeModifier(
                ORIGIN_SCALE_MODIFIER_ID,
                modifierValue,
                AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

        scaleAttribute.addPermanentModifier(scaleModifier);
    }

    /**
     * Remove scale modifier from a player (called when origin is removed)
     */
    public static void removeOriginScale(Player player) {
        AttributeInstance scaleAttribute = player.getAttribute(Attributes.SCALE);
        if (scaleAttribute != null) {
            scaleAttribute.removeModifier(ORIGIN_SCALE_MODIFIER_ID);
        }
        lastAppliedOrigin.remove(player.getUUID());
    }

    /**
     * Force refresh the scale for a player (useful after config reload)
     */
    public static void refreshPlayerScale(Player player) {
        lastAppliedOrigin.remove(player.getUUID());
        applyOriginScale(player);
    }
}
