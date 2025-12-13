package com.veilorigins;

import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.client.KeyBindings;
import com.veilorigins.client.OriginHudOverlay;
import com.veilorigins.config.VeilOriginsConfig;
import com.veilorigins.data.OriginData;
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

        // Register config
        modContainer.registerConfig(ModConfig.Type.COMMON, VeilOriginsConfig.SPEC);
        LOGGER.info("Veil Origins: Registered configuration");

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
            modEventBus.addListener(this::registerKeyMappings);
            modEventBus.addListener(this::registerGuiLayers);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Veil Origins: Common setup complete");
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        LOGGER.info("Veil Origins: Client setup complete");
    }

    private void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.ABILITY_1);
        event.register(KeyBindings.ABILITY_2);
        event.register(KeyBindings.RESOURCE_INFO);
        LOGGER.info("Veil Origins: Registered {} keybindings", 3);
    }

    private void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR,
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "origin_hud"),
                new OriginHudOverlay());
        LOGGER.info("Veil Origins: Registered HUD overlay");
    }
}
