package com.veilorigins.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final String CATEGORY = "key.categories.veil_origins";
    
    public static final KeyMapping ABILITY_1 = new KeyMapping(
        "key.veil_origins.ability1",
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_R,
        CATEGORY
    );
    
    public static final KeyMapping ABILITY_2 = new KeyMapping(
        "key.veil_origins.ability2",
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_V,
        CATEGORY
    );
    
    public static final KeyMapping RESOURCE_INFO = new KeyMapping(
        "key.veil_origins.resource_info",
        KeyConflictContext.IN_GAME,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_O,
        CATEGORY
    );
}
