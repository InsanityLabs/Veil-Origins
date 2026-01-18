package com.veilorigins.client;

import com.veilorigins.VeilOrigins;
import com.veilorigins.api.UnicodeFontHandler;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/**
 * Client-only setup class to avoid loading client classes on dedicated server.
 */
@OnlyIn(Dist.CLIENT)
public class ClientSetup {
    
    public static void register(IEventBus modEventBus) {
        VeilOrigins.LOGGER.info("Veil Origins: Registering client events...");
        modEventBus.addListener(ClientSetup::onClientSetup);
        modEventBus.addListener(ClientSetup::registerGuiLayers);
    }
    
    private static void onClientSetup(FMLClientSetupEvent event) {
        // Initialize Unicode font handler for rendering Unicode symbols in HUD
        event.enqueueWork(() -> {
            if (UnicodeFontHandler.initialize()) {
                VeilOrigins.LOGGER.info("Veil Origins: Unicode font handler initialized successfully");
            } else {
                VeilOrigins.LOGGER.warn("Veil Origins: Unicode font handler initialization failed, falling back to ASCII symbols");
            }
        });
        VeilOrigins.LOGGER.info("Veil Origins: Client setup complete");
    }

    private static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR,
                ResourceLocation.fromNamespaceAndPath(VeilOrigins.MOD_ID, "origin_hud"),
                new OriginHudOverlay());
        VeilOrigins.LOGGER.info("Veil Origins: Registered HUD overlay");
    }
}
