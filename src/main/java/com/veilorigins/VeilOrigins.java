package com.veilorigins;

import com.veilorigins.api.UnicodeFontHandler;
import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.client.KeyBindings;
import com.veilorigins.client.OriginHudOverlay;
import com.veilorigins.config.VeilOriginsConfig;
import com.veilorigins.data.OriginData;
import com.veilorigins.registry.ModItems;
import com.veilorigins.registry.ModOrigins;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("veil_origins")
public class VeilOrigins {
    public static final String MOD_ID = "veil_origins";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public VeilOrigins(IEventBus modEventBus, ModContainer modContainer) {
        // Register attachment types
        OriginData.ATTACHMENT_TYPES.register(modEventBus);

        // Register items and creative tab
        ModItems.register(modEventBus);

        // Register configs - COMMON for gameplay settings, CLIENT for HUD settings
        modContainer.registerConfig(ModConfig.Type.COMMON, VeilOriginsConfig.COMMON_SPEC);
        modContainer.registerConfig(ModConfig.Type.CLIENT, VeilOriginsConfig.CLIENT_SPEC);
        LOGGER.info("Veil Origins: Registered COMMON and CLIENT configuration files");

        // Register config screen for the mod list (client-side only)
        if (FMLEnvironment.dist == Dist.CLIENT) {
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
            LOGGER.info("Veil Origins: Registered config screen factory");
        }

        // Register dimensions
        com.veilorigins.registry.ModDimensions.CHUNK_GENERATORS.register(modEventBus);
        LOGGER.info("Veil Origins: Registered dimension chunk generators");

        // Register origins immediately
        LOGGER.info("Veil Origins: Starting origin registration...");
        ModOrigins.register();
        LOGGER.info("Veil Origins: Registered {} origins", VeilOriginsAPI.getAllOrigins().size());
        VeilOriginsAPI.getAllOrigins().forEach((id, origin) -> LOGGER.info("  - {} ({})", origin.getDisplayName(), id));

        // Register packets
        modEventBus.addListener(com.veilorigins.network.ModPackets::register);

        modEventBus.addListener(this::commonSetup);

        // Register client events directly on the mod event bus
        if (FMLEnvironment.dist == Dist.CLIENT) {
            LOGGER.info("Veil Origins: Registering client events...");
            modEventBus.addListener(this::onClientSetup);
            // Note: Keybindings are registered via @EventBusSubscriber in KeyBindings class
            modEventBus.addListener(this::registerGuiLayers);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Veil Origins: Common setup complete");
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        // Initialize Unicode font handler for rendering Unicode symbols in HUD
        event.enqueueWork(() -> {
            if (UnicodeFontHandler.initialize()) {
                LOGGER.info("Veil Origins: Unicode font handler initialized successfully");
            } else {
                LOGGER.warn("Veil Origins: Unicode font handler initialization failed, falling back to ASCII symbols");
            }
        });
        LOGGER.info("Veil Origins: Client setup complete");
    }


    private void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "origin_hud"),
                new OriginHudOverlay());
        LOGGER.info("Veil Origins: Registered HUD overlay");
    }
}
