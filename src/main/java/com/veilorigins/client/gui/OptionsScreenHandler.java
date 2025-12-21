package com.veilorigins.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

/**
 * Utility class for opening the HUD config screen from various places.
 */
public class OptionsScreenHandler {

    /**
     * Opens the HUD config screen. Can be called from anywhere.
     * 
     * @param parent The screen to return to when done
     */
    public static void openHudConfigScreen(Screen parent) {
        Minecraft.getInstance().setScreen(new HudConfigScreen(parent));
    }

    /**
     * Opens the HUD config screen from current screen.
     */
    public static void openHudConfigScreen() {
        Minecraft mc = Minecraft.getInstance();
        openHudConfigScreen(mc.screen);
    }
}
