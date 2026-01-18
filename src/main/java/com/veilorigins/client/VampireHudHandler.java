package com.veilorigins.client;

import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.config.VeilOriginsConfig;
import com.veilorigins.data.OriginData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

/**
 * Handles vampire-specific HUD modifications.
 * - Hides the vanilla hunger bar for vampires
 * - Renders blood bar with custom sprites in its place
 * - Supports all states: empty, full, half, less_half, withered, absorption
 */
@EventBusSubscriber(modid = VeilOrigins.MOD_ID, value = Dist.CLIENT)
public class VampireHudHandler {

    // Blood bar sprite textures (sprite path format - no textures/ prefix, no .png)
    private static final Identifier BLOOD_EMPTY = Identifier.fromNamespaceAndPath(
            VeilOrigins.MOD_ID, "origins/vampire_blood/hud_bar/blood_empty");
    private static final Identifier BLOOD_FULL = Identifier.fromNamespaceAndPath(
            VeilOrigins.MOD_ID, "origins/vampire_blood/hud_bar/blood_full");
    private static final Identifier BLOOD_HALF = Identifier.fromNamespaceAndPath(
            VeilOrigins.MOD_ID, "origins/vampire_blood/hud_bar/blood_half");
    private static final Identifier BLOOD_LESS_HALF = Identifier.fromNamespaceAndPath(
            VeilOrigins.MOD_ID, "origins/vampire_blood/hud_bar/blood_less_half");
    private static final Identifier BLOOD_WITHERED = Identifier.fromNamespaceAndPath(
            VeilOrigins.MOD_ID, "origins/vampire_blood/hud_bar/blood_withered");
    private static final Identifier BLOOD_ABSORPTION = Identifier.fromNamespaceAndPath(
            VeilOrigins.MOD_ID, "origins/vampire_blood/hud_bar/blood_absorption");

    // Sprite dimensions (standard Minecraft icon size)
    private static final int ICON_SIZE = 9;
    private static final int ICON_SPACING = 8;

    /**
     * Cancel the vanilla food bar rendering for vampires and render blood bar instead.
     */
    @SubscribeEvent
    public static void onRenderGuiLayerPre(RenderGuiLayerEvent.Pre event) {
        if (!event.getName().equals(VanillaGuiLayers.FOOD_LEVEL)) return;
        
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        
        if (player == null) return;
        
        Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
        if (origin == null) return;
        
        String originPath = origin.getId().getPath();
        if (originPath.equals("vampire") || originPath.equals("vampling")) {
            // Cancel vanilla food bar rendering
            event.setCanceled(true);
            
            // Render blood bar immediately
            if (!mc.options.hideGui) {
                renderVampireBloodBar(event.getGuiGraphics(), mc, player, origin, originPath.equals("vampire"));
            }
        }
    }

    /**
     * Renders the vampire blood bar in place of the hunger bar using sprites.
     * Supports all states: normal, withered (hunger effect), and absorption (saturation).
     */
    private static void renderVampireBloodBar(GuiGraphics graphics, Minecraft mc, Player player, Origin origin, boolean isFullVampire) {
        if (!VeilOriginsConfig.showResourceBar) return;
        
        OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);
        float bloodValue = data.getResourceBar(); // 0-100
        
        // Get saturation from food data (we'll use this for absorption display)
        FoodData foodData = player.getFoodData();
        float saturation = foodData.getSaturationLevel();
        
        // Check for hunger effect (withered state)
        boolean hasHungerEffect = player.hasEffect(MobEffects.HUNGER);
        
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        Font font = mc.font;
        
        // Position to match vanilla hunger bar (right side of hotbar)
        int rightEdge = screenWidth / 2 + 91;
        int y = screenHeight - 39;
        
        // Calculate blood points (0-20 like vanilla hunger)
        int bloodPoints = (int) (bloodValue / 5); // Convert 0-100 to 0-20
        
        // Calculate saturation/absorption points (shown as golden overlay)
        int absorptionPoints = (int) (saturation / 2.5f); // Convert saturation to 0-8 range for display
        absorptionPoints = Math.min(absorptionPoints, 20); // Cap at 20
        
        // Check for critically low blood (withered visual)
        boolean isCriticallyLow = bloodValue < 20;
        
        // Render 10 blood icons from right to left (like vanilla)
        for (int i = 9; i >= 0; i--) {
            int iconX = rightEdge - (10 - i) * ICON_SPACING - ICON_SIZE;
            int iconY = y;
            
            // Add bounce animation when critically low or has hunger effect
            if ((isCriticallyLow || hasHungerEffect) && bloodValue < 10) {
                long time = System.currentTimeMillis();
                if ((time / 250 + i) % 3 == 0) {
                    iconY -= 1;
                }
            }
            
            // Determine which background texture to use
            Identifier bgTexture = hasHungerEffect ? BLOOD_WITHERED : BLOOD_EMPTY;
            
            // Draw background (empty icon)
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, bgTexture, iconX, iconY, ICON_SIZE, ICON_SIZE);
            
            // Calculate fill state for this icon (each icon = 2 blood points)
            int iconBloodPoints = bloodPoints - (i * 2);
            
            // Draw blood fill based on amount
            if (iconBloodPoints >= 2) {
                // Full blood icon
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BLOOD_FULL, iconX, iconY, ICON_SIZE, ICON_SIZE);
            } else if (iconBloodPoints == 1) {
                // Half blood icon
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BLOOD_HALF, iconX, iconY, ICON_SIZE, ICON_SIZE);
            }
            
            // Draw absorption overlay if player has saturation
            if (saturation > 0) {
                int iconAbsorptionPoints = absorptionPoints - (i * 2);
                if (iconAbsorptionPoints >= 2) {
                    // Full absorption overlay
                    graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BLOOD_ABSORPTION, iconX, iconY, ICON_SIZE, ICON_SIZE);
                }
            }
        }
        
        // Draw blood value text to the right of the bar
        String bloodText = String.format("%.0f", bloodValue);
        int textColor = isFullVampire ? 0xFFAA0000 : 0xFFFF5555;
        
        // Change color based on state
        if (hasHungerEffect) {
            textColor = 0xFF556600; // Sickly green-yellow when withered
        } else if (isCriticallyLow) {
            // Pulse red when critically low
            long time = System.currentTimeMillis();
            if ((time / 500) % 2 == 0) {
                textColor = 0xFFFF0000;
            }
        } else if (saturation > 10) {
            // Golden tint when high saturation
            textColor = 0xFFFFAA00;
        }
        
        graphics.drawString(font, bloodText, rightEdge + 4, y, textColor, true);
        
        // Show saturation value if significant
        if (saturation > 5) {
            String satText = "+" + String.format("%.0f", saturation);
            graphics.drawString(font, satText, rightEdge + 4, y - 10, 0xFFFFDD00, true);
        }
    }
}
