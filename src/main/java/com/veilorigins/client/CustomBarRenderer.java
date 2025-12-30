package com.veilorigins.client;

import com.veilorigins.api.CustomResourceBar;
import com.veilorigins.api.CustomResourceBar.BarPosition;
import com.veilorigins.api.CustomResourceBar.BarStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

/**
 * Renders custom resource bars based on CustomResourceBar configuration.
 * Supports multiple rendering styles: icons, solid bars, segmented bars.
 */
public class CustomBarRenderer {
    
    /**
     * Renders a custom resource bar.
     * 
     * @param graphics The GUI graphics context
     * @param font The font renderer
     * @param bar The custom bar configuration
     * @param currentValue Current resource value (0-100)
     * @param maxValue Maximum resource value (usually 100)
     * @param screenWidth Screen width for positioning
     * @param screenHeight Screen height for positioning
     */
    public static void render(GuiGraphics graphics, Font font, CustomResourceBar bar, 
                              float currentValue, float maxValue, int screenWidth, int screenHeight) {
        float percent = currentValue / maxValue;
        
        // Calculate position based on bar configuration
        int[] pos = calculatePosition(bar, screenWidth, screenHeight);
        int x = pos[0];
        int y = pos[1];
        
        // Render based on style
        switch (bar.getStyle()) {
            case ICONS -> renderIconBar(graphics, font, bar, percent, currentValue, x, y);
            case SOLID_BAR -> renderSolidBar(graphics, font, bar, percent, currentValue, x, y);
            case SEGMENTED_BAR -> renderSegmentedBar(graphics, font, bar, percent, currentValue, x, y);
            case REPLACE_HUNGER -> renderIconBar(graphics, font, bar, percent, currentValue, x, y);
            case REPLACE_HEALTH -> renderIconBar(graphics, font, bar, percent, currentValue, x, y);
        }
    }
    
    /**
     * Calculates the screen position for the bar based on its configuration.
     */
    private static int[] calculatePosition(CustomResourceBar bar, int screenWidth, int screenHeight) {
        int x, y;
        
        switch (bar.getPosition()) {
            case HOTBAR_LEFT -> {
                x = screenWidth / 2 - 91;
                y = screenHeight - 39;
            }
            case HOTBAR_RIGHT -> {
                x = screenWidth / 2 + 91 - (bar.getIconCount() * bar.getIconSpacing());
                y = screenHeight - 39;
            }
            case TOP_LEFT -> {
                x = 5;
                y = 5;
            }
            case TOP_RIGHT -> {
                x = screenWidth - bar.getBarWidth() - 5;
                y = 5;
            }
            case BOTTOM_LEFT -> {
                x = 5;
                y = screenHeight - bar.getBarHeight() - 50;
            }
            case BOTTOM_RIGHT -> {
                x = screenWidth - bar.getBarWidth() - 5;
                y = screenHeight - bar.getBarHeight() - 50;
            }
            default -> {
                x = screenWidth / 2;
                y = screenHeight / 2;
            }
        }
        
        // Apply offsets
        x += bar.getOffsetX();
        y += bar.getOffsetY();
        
        return new int[]{x, y};
    }
    
    /**
     * Renders an icon-based bar (like hearts or hunger).
     */
    private static void renderIconBar(GuiGraphics graphics, Font font, CustomResourceBar bar,
                                       float percent, float currentValue, int x, int y) {
        int iconCount = bar.getIconCount();
        int iconSize = bar.getIconSize();
        int iconSpacing = bar.getIconSpacing();
        
        // Calculate points (0-20 like vanilla)
        int points = (int) (percent * iconCount * 2);
        
        boolean isCritical = bar.isCritical(percent);
        boolean isLow = bar.isLow(percent);
        
        // Render icons from right to left (like vanilla hunger)
        boolean rightToLeft = bar.getPosition() == BarPosition.HOTBAR_RIGHT || 
                              bar.getStyle() == BarStyle.REPLACE_HUNGER;
        
        for (int i = iconCount - 1; i >= 0; i--) {
            int iconX;
            if (rightToLeft) {
                iconX = x + (iconCount - 1 - i) * iconSpacing;
            } else {
                iconX = x + i * iconSpacing;
            }
            int iconY = y;
            
            // Bounce animation when critical
            if (isCritical && bar.shouldBounceWhenCritical()) {
                long time = System.currentTimeMillis();
                if ((time / 250 + i) % 3 == 0) {
                    iconY -= 1;
                }
            }
            
            // Draw background/empty icon
            if (bar.getSpriteEmpty() != null) {
                blitSprite(graphics, bar.getSpriteEmpty(), iconX, iconY, iconSize, iconSize);
            } else {
                // Fallback to colored square
                graphics.fill(iconX, iconY, iconX + iconSize, iconY + iconSize, bar.getBackgroundColor());
            }
            
            // Calculate fill state for this icon (each icon = 2 points)
            int iconPoints = points - (i * 2);
            
            // Draw filled icon
            if (iconPoints >= 2) {
                if (bar.getSpriteFull() != null) {
                    blitSprite(graphics, bar.getSpriteFull(), iconX, iconY, iconSize, iconSize);
                } else {
                    graphics.fill(iconX + 1, iconY + 1, iconX + iconSize - 1, iconY + iconSize - 1, 
                                  bar.getColorForPercent(percent));
                }
            } else if (iconPoints == 1) {
                if (bar.getSpriteHalf() != null) {
                    blitSprite(graphics, bar.getSpriteHalf(), iconX, iconY, iconSize, iconSize);
                } else {
                    // Draw half-filled
                    graphics.fill(iconX + 1, iconY + 1, iconX + iconSize / 2, iconY + iconSize - 1, 
                                  bar.getColorForPercent(percent));
                }
            }
        }
        
        // Draw numeric value if enabled
        if (bar.shouldShowNumericValue()) {
            String valueText = String.format("%.0f", currentValue);
            int textX = rightToLeft ? x + iconCount * iconSpacing + 4 : x - font.width(valueText) - 4;
            int textColor = bar.getColorForPercent(percent);
            
            // Pulse color when low
            if (isLow && bar.shouldPulseWhenLow()) {
                long time = System.currentTimeMillis();
                if ((time / 500) % 2 == 0) {
                    textColor = bar.getCriticalColor();
                }
            }
            
            graphics.drawString(font, valueText, textX, y, textColor, true);
        }
    }
    
    /**
     * Renders a solid horizontal bar.
     */
    private static void renderSolidBar(GuiGraphics graphics, Font font, CustomResourceBar bar,
                                        float percent, float currentValue, int x, int y) {
        int width = bar.getBarWidth();
        int height = bar.getBarHeight();
        int filledWidth = (int) (width * percent);
        
        boolean isCritical = bar.isCritical(percent);
        boolean isLow = bar.isLow(percent);
        
        // Draw border
        graphics.fill(x - 1, y - 1, x + width + 1, y + height + 1, bar.getBorderColor());
        
        // Draw background
        graphics.fill(x, y, x + width, y + height, bar.getBackgroundColor());
        
        // Get fill color
        int fillColor = bar.getColorForPercent(percent);
        
        // Pulse when low
        if (isLow && bar.shouldPulseWhenLow()) {
            long time = System.currentTimeMillis();
            if ((time / 500) % 2 == 0) {
                fillColor = bar.getCriticalColor();
            }
        }
        
        // Draw filled portion
        if (filledWidth > 0) {
            graphics.fill(x, y, x + filledWidth, y + height, fillColor);
            
            // Add highlight on top
            graphics.fill(x, y, x + filledWidth, y + 1, lightenColor(fillColor, 0.3f));
        }
        
        // Draw numeric value if enabled
        if (bar.shouldShowNumericValue()) {
            String valueText = String.format("%.0f", currentValue);
            int textX = x + width + 4;
            graphics.drawString(font, bar.getName() + ": " + valueText, textX, y, fillColor, true);
        }
    }
    
    /**
     * Renders a segmented bar with divisions.
     */
    private static void renderSegmentedBar(GuiGraphics graphics, Font font, CustomResourceBar bar,
                                            float percent, float currentValue, int x, int y) {
        int width = bar.getBarWidth();
        int height = bar.getBarHeight();
        int filledWidth = (int) (width * percent);
        int segments = 10;
        int segmentWidth = width / segments;
        
        boolean isCritical = bar.isCritical(percent);
        boolean isLow = bar.isLow(percent);
        
        // Draw border
        graphics.fill(x - 1, y - 1, x + width + 1, y + height + 1, bar.getBorderColor());
        
        // Draw background
        graphics.fill(x, y, x + width, y + height, bar.getBackgroundColor());
        
        // Get fill color
        int fillColor = bar.getColorForPercent(percent);
        
        // Pulse when low
        if (isLow && bar.shouldPulseWhenLow()) {
            long time = System.currentTimeMillis();
            if ((time / 500) % 2 == 0) {
                fillColor = bar.getCriticalColor();
            }
        }
        
        // Draw filled portion
        if (filledWidth > 0) {
            graphics.fill(x, y, x + filledWidth, y + height, fillColor);
            
            // Add highlight
            graphics.fill(x, y, x + filledWidth, y + height / 2, lightenColor(fillColor, 0.2f));
        }
        
        // Draw segment dividers
        for (int i = 1; i < segments; i++) {
            int dividerX = x + i * segmentWidth;
            graphics.fill(dividerX, y, dividerX + 1, y + height, 0x44000000);
        }
        
        // Draw numeric value if enabled
        if (bar.shouldShowNumericValue()) {
            String valueText = String.format("%.0f", currentValue);
            int textX = x + width + 4;
            graphics.drawString(font, bar.getName() + ": " + valueText, textX, y, fillColor, true);
        }
    }
    
    /**
     * Helper to blit a sprite using the 1.21.10 API.
     */
    private static void blitSprite(GuiGraphics graphics, Identifier sprite, int x, int y, int width, int height) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, x, y, width, height);
    }
    
    /**
     * Lightens a color by a factor.
     */
    private static int lightenColor(int color, float factor) {
        int a = (color >> 24) & 0xFF;
        int r = Math.min(255, (int) (((color >> 16) & 0xFF) * (1 + factor)));
        int g = Math.min(255, (int) (((color >> 8) & 0xFF) * (1 + factor)));
        int b = Math.min(255, (int) ((color & 0xFF) * (1 + factor)));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
