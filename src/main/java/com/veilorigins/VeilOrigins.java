package com.veilorigins;

import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.config.VeilOriginsConfig;
import com.veilorigins.data.OriginData;
import com.veilorigins.progression.legendary.LegendaryAbilityRegistry;
import com.veilorigins.progression.SkillTreeRegistry;
import com.veilorigins.registry.ModItems;
import com.veilorigins.registry.ModOrigins;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
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

        // Register legendary abilities
        LegendaryAbilityRegistry.register();
        LOGGER.info("Veil Origins: Registered legendary abilities for all origins");

        // Register skill trees (OLD system - kept for compatibility)
        SkillTreeRegistry.init();
        LOGGER.info("Veil Origins: Registered old skill tree registry");
        
        // Initialize NEW skill trees (with actual effects) - MUST be done here for dedicated server
        com.veilorigins.progression.skill.SkillTrees.initialize();
        LOGGER.info("Veil Origins: Initialized new skill trees with {} trees", 
            com.veilorigins.progression.skill.SkillTrees.getTree("dryad") != null ? "dryad found" : "dryad NOT found");

        // Register packets
        modEventBus.addListener(com.veilorigins.network.ModPackets::register);

        modEventBus.addListener(this::commonSetup);

        // Register client events via separate class to avoid loading client classes on server
        if (FMLEnvironment.dist == Dist.CLIENT) {
            com.veilorigins.client.ClientSetup.register(modEventBus);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Veil Origins: Common setup complete");
        LOGGER.info("Veil Origins: Progression system initialized (Max Level: 50, Max Prestige: 10)");
        
        // Verify skill trees are initialized (they should be from constructor)
        var dryadTree = com.veilorigins.progression.skill.SkillTrees.getTree("dryad");
        if (dryadTree != null) {
            LOGGER.info("Veil Origins: Skill trees verified - dryad tree has {} skills", 
                dryadTree.getAllSkills().size());
        } else {
            LOGGER.error("Veil Origins: CRITICAL - Skill trees NOT initialized! Dryad tree is null!");
        }
    }
}
