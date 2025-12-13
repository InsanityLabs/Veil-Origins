package com.veilorigins.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.data.OriginData;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class OriginHudOverlay implements LayeredDraw.Layer {
    private static final ResourceLocation RESOURCE_BAR_TEXTURE = 
        ResourceLocation.fromNamespaceAndPath(VeilOrigins.MOD_ID, "textures/gui/resource_bar.png");
    
    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        
        if (player == null || mc.options.hideGui) return;
        
        Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
        if (origin == null) return;
        
        OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);
        
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();
        
        // Position above hotbar
        int barX = screenWidth / 2 - 91;
        int barY = screenHeight - 49;
        
        renderResourceBar(graphics, barX, barY, data.getResourceBar(), origin);
        renderAbilityCooldowns(graphics, screenWidth, screenHeight, player, origin);
    }
    
    private void renderResourceBar(GuiGraphics graphics, int x, int y, float resource, Origin origin) {
        // Background bar
        graphics.fill(x, y, x + 182, y + 5, 0xFF000000);
        
        // Resource bar color based on origin
        int color = getOriginColor(origin);
        int barWidth = (int) (182 * (resource / 100.0f));
        graphics.fill(x + 1, y + 1, x + 1 + barWidth, y + 4, color);
        
        // Border
        graphics.fill(x, y, x + 182, y + 1, 0xFFFFFFFF);
        graphics.fill(x, y + 4, x + 182, y + 5, 0xFFFFFFFF);
        graphics.fill(x, y, x + 1, y + 5, 0xFFFFFFFF);
        graphics.fill(x + 181, y, x + 182, y + 5, 0xFFFFFFFF);
    }
    
    private void renderAbilityCooldowns(GuiGraphics graphics, int screenWidth, int screenHeight, Player player, Origin origin) {
        int x = screenWidth - 60;
        int y = screenHeight - 80;
        
        for (int i = 0; i < origin.getAbilities().size(); i++) {
            var ability = origin.getAbilities().get(i);
            int abilityY = y - (i * 25);
            
            // Ability icon background
            graphics.fill(x, abilityY, x + 20, abilityY + 20, 0x88000000);
            
            if (ability.isOnCooldown()) {
                // Cooldown overlay
                int cooldownHeight = (int) (20 * (ability.getCooldown() / (float) (ability.getCooldown() + 1)));
                graphics.fill(x, abilityY, x + 20, abilityY + cooldownHeight, 0xAA000000);
                
                // Cooldown text
                String cdText = String.valueOf(ability.getCooldown() / 20);
                graphics.drawString(Minecraft.getInstance().font, cdText, x + 6, abilityY + 6, 0xFFFFFF);
            } else {
                // Ready indicator
                graphics.fill(x + 2, abilityY + 2, x + 18, abilityY + 18, 0xFF00FF00);
            }
            
            // Keybind hint
            String key = i == 0 ? "R" : "V";
            graphics.drawString(Minecraft.getInstance().font, key, x + 22, abilityY + 6, 0xFFFFFF);
        }
    }
    
    private int getOriginColor(Origin origin) {
        String id = origin.getId().getPath();
        return switch (id) {
            case "veilborn" -> 0xFF9B59B6; // Purple
            case "cindersoul" -> 0xFFE67E22; // Orange
            case "riftwalker" -> 0xFF8E44AD; // Dark purple
            case "tidecaller" -> 0xFF3498DB; // Blue
            case "starborne" -> 0xFFF1C40F; // Yellow
            case "stoneheart" -> 0xFF7F8C8D; // Gray
            case "frostborn" -> 0xFF5DADE2; // Light blue
            case "umbrakin" -> 0xFF2C3E50; // Dark gray
            case "feralkin" -> 0xFF27AE60; // Green
            case "voidtouched" -> 0xFF6C3483; // Void purple
            case "skyborn" -> 0xFFECF0F1; // White
            case "mycomorph" -> 0xFF58D68D; // Light green
            case "crystalline" -> 0xFFAED6F1; // Crystal blue
            case "technomancer" -> 0xFFE74C3C; // Red
            case "ethereal" -> 0xFFBDC3C7; // Ghost gray
            default -> 0xFFFFFFFF; // White
        };
    }
}
