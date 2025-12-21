package com.veilorigins.client;

import com.veilorigins.VeilOrigins;
import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.client.gui.RadialMenuScreen;
import com.veilorigins.config.VeilOriginsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;

/**
 * Client-side event handler for managing the radial menu auto-open on first
 * join.
 */
@EventBusSubscriber(modid = VeilOrigins.MOD_ID, value = Dist.CLIENT)
public class ClientEventHandler {

    // Track if we've already shown the origin select menu this session
    private static boolean hasShownOriginSelect = false;

    // Delay before showing the menu (in ticks, to let the world load)
    private static int joinDelayTicks = 0;
    private static boolean pendingOriginSelect = false;

    @SubscribeEvent
    public static void onClientPlayerJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        // Reset state on login
        hasShownOriginSelect = false;

        // Check if we should auto-open origin selection
        if (VeilOriginsConfig.openOriginSelectOnFirstJoin) {
            // Set a delay to allow the world to fully load
            pendingOriginSelect = true;
            joinDelayTicks = 40; // 2 seconds delay
            VeilOrigins.LOGGER.debug("Scheduled origin selection check after world load");
        }
    }

    @SubscribeEvent
    public static void onClientPlayerLeave(ClientPlayerNetworkEvent.LoggingOut event) {
        // Reset state on logout
        hasShownOriginSelect = false;
        pendingOriginSelect = false;
        joinDelayTicks = 0;
    }

    /**
     * Called each client tick to handle delayed origin selection menu opening.
     * This is registered separately in ClientTickHandler or called via
     * ClientTickEvent.
     */
    public static void tickOriginSelectDelay() {
        if (!pendingOriginSelect)
            return;

        if (joinDelayTicks > 0) {
            joinDelayTicks--;
            return;
        }

        // Time to check and potentially open the menu
        pendingOriginSelect = false;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null)
            return;
        if (hasShownOriginSelect)
            return;
        if (mc.screen != null)
            return; // Don't interrupt other screens

        // Check if player already has an origin
        if (!VeilOriginsAPI.hasOrigin(player)) {
            VeilOrigins.LOGGER.info("Player has no origin, opening origin selection menu");
            hasShownOriginSelect = true;
            mc.setScreen(new RadialMenuScreen(RadialMenuScreen.MenuMode.ORIGIN_SELECT));

            // Also send a chat message
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal(
                            "§e✦ Welcome to Veil Origins! ✦\n§7Please select your origin to begin your journey."),
                    false);
        } else {
            VeilOrigins.LOGGER.debug("Player already has origin: {}",
                    VeilOriginsAPI.getPlayerOrigin(player).getId());
        }
    }

    /**
     * Force open the origin selection menu (for commands or other triggers).
     */
    public static void openOriginSelectionMenu() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.setScreen(new RadialMenuScreen(RadialMenuScreen.MenuMode.ORIGIN_SELECT));
        }
    }

    /**
     * Force open the ability selection menu.
     */
    public static void openAbilityMenu() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && VeilOriginsAPI.hasOrigin(mc.player)) {
            mc.setScreen(new RadialMenuScreen(RadialMenuScreen.MenuMode.ABILITY_SELECT));
        }
    }
}
