package com.veilorigins.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

/**
 * Key bindings for the Veil Origins mod.
 * Updated for NeoForge 1.21.8 using String-based category.
 */
@EventBusSubscriber(modid = "veil_origins", value = Dist.CLIENT)
public class KeyBindings {
    
    // Custom category for our key bindings (String-based for NeoForge 21.8)
    public static final String VEIL_ORIGINS_CATEGORY = "key.categories.veil_origins";

    // Ability 1 keybinding (default R)
    public static final KeyMapping ABILITY_1 = new KeyMapping(
            "key.veil_origins.ability1",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            VEIL_ORIGINS_CATEGORY);

    // Ability 2 keybinding (default V)
    public static final KeyMapping ABILITY_2 = new KeyMapping(
            "key.veil_origins.ability2",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            VEIL_ORIGINS_CATEGORY);

    // Resource info keybinding (default O)
    public static final KeyMapping RESOURCE_INFO = new KeyMapping(
            "key.veil_origins.resource_info",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_O,
            VEIL_ORIGINS_CATEGORY);

    /**
     * Opens the radial menu for ability selection or origin selection.
     * Default: G key
     */
    public static final KeyMapping RADIAL_MENU = new KeyMapping(
            "key.veil_origins.radial_menu",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            VEIL_ORIGINS_CATEGORY);

    /**
     * Opens the HUD configuration screen.
     * Default: H key
     */
    public static final KeyMapping HUD_CONFIG = new KeyMapping(
            "key.veil_origins.hud_config",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            VEIL_ORIGINS_CATEGORY);

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        // Register all key mappings (no need to register category in 21.8 - it's auto-created from String)
        event.register(ABILITY_1);
        event.register(ABILITY_2);
        event.register(RESOURCE_INFO);
        event.register(RADIAL_MENU);
        event.register(HUD_CONFIG);
    }
}
