package com.veilorigins.client.gui;

import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.OriginAbility;
import com.veilorigins.api.OriginPassive;
import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.network.ModPackets;
import com.veilorigins.network.packet.SelectOriginPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A card carousel screen for selecting origins.
 * Shows 8 origin cards in a horizontal carousel with smooth scrolling.
 */
public class OriginCardCarouselScreen extends Screen {
    
    // Card dimensions
    private static final int CARD_WIDTH = 180;
    private static final int CARD_HEIGHT = 260;
    private static final int CARD_SPACING = 20;
    private static final int VISIBLE_CARDS = 3; // Cards visible at once (center + partial sides)
    
    // Colors
    private static final int COLOR_CARD_BG = 0xDD1a1a2e;
    private static final int COLOR_CARD_BORDER = 0xFF3a3a5e;
    private static final int COLOR_CARD_HOVER = 0xFF4a4a7e;
    private static final int COLOR_CARD_SELECTED = 0xFF6a6aae;
    private static final int COLOR_TEXT_TITLE = 0xFFFFFFFF;
    private static final int COLOR_TEXT_DESC = 0xFFAAAAAA;
    private static final int COLOR_TEXT_ABILITY = 0xFF88CCFF;
    private static final int COLOR_TEXT_PASSIVE = 0xFF88FF88;
    private static final int COLOR_BUTTON = 0xFF3a5a3a;
    private static final int COLOR_BUTTON_HOVER = 0xFF4a7a4a;
    private static final int COLOR_REFRESH = 0xFF5a3a3a;
    private static final int COLOR_REFRESH_HOVER = 0xFF7a4a4a;
    
    // State
    private List<Origin> displayedOrigins = new ArrayList<>();
    private int selectedIndex = 0;
    private float scrollOffset = 0;
    private float targetScrollOffset = 0;
    private int hoveredCard = -1;
    private boolean confirmHovered = false;
    private boolean refreshHovered = false;
    private boolean leftArrowHovered = false;
    private boolean rightArrowHovered = false;
    
    // Animation
    private float cardAnimationProgress = 0;
    private static final float SCROLL_SPEED = 0.15f;
    
    public OriginCardCarouselScreen() {
        super(Component.translatable("screen.veil_origins.origin_select"));
        refreshOrigins();
    }
    
    /**
     * Shuffle and pick 8 random origins to display.
     */
    public void refreshOrigins() {
        List<Origin> allOrigins = new ArrayList<>(VeilOriginsAPI.getAllOrigins().values());
        Collections.shuffle(allOrigins);
        
        displayedOrigins.clear();
        int count = Math.min(8, allOrigins.size());
        for (int i = 0; i < count; i++) {
            displayedOrigins.add(allOrigins.get(i));
        }
        
        selectedIndex = displayedOrigins.size() / 2; // Start in middle
        targetScrollOffset = selectedIndex;
        scrollOffset = selectedIndex;
        cardAnimationProgress = 0;
    }
    
    @Override
    protected void init() {
        super.init();
    }
    
    @Override
    public void tick() {
        super.tick();
        
        // Smooth scroll animation
        scrollOffset = Mth.lerp(SCROLL_SPEED, scrollOffset, targetScrollOffset);
        
        // Card entrance animation
        if (cardAnimationProgress < 1.0f) {
            cardAnimationProgress = Math.min(1.0f, cardAnimationProgress + 0.05f);
        }
    }
    
    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Don't render the default blurred background - we draw our own
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Dark background (our custom one, no blur)
        graphics.fill(0, 0, width, height, 0xE0101020);
        
        // Title
        String title = "Choose Your Origin";
        int titleWidth = font.width(title);
        graphics.drawString(font, title, (width - titleWidth) / 2, 20, COLOR_TEXT_TITLE, true);
        
        // Subtitle
        String subtitle = "Select an origin to define your abilities and playstyle";
        int subtitleWidth = font.width(subtitle);
        graphics.drawString(font, subtitle, (width - subtitleWidth) / 2, 35, COLOR_TEXT_DESC, false);
        
        // Update hover states
        updateHoverStates(mouseX, mouseY);
        
        // Render cards
        renderCards(graphics, mouseX, mouseY, partialTick);
        
        // Render navigation arrows
        renderNavigationArrows(graphics, mouseX, mouseY);
        
        // Render bottom buttons
        renderBottomButtons(graphics, mouseX, mouseY);
        
        // Render card count indicator
        renderCardIndicator(graphics);
    }
    
    private void updateHoverStates(int mouseX, int mouseY) {
        hoveredCard = -1;
        confirmHovered = false;
        refreshHovered = false;
        leftArrowHovered = false;
        rightArrowHovered = false;
        
        int centerX = width / 2;
        int centerY = height / 2 - 20;
        
        // Check card hovers
        for (int i = 0; i < displayedOrigins.size(); i++) {
            float offset = i - scrollOffset;
            if (Math.abs(offset) > 2) continue;
            
            int cardX = (int) (centerX + offset * (CARD_WIDTH + CARD_SPACING) - CARD_WIDTH / 2);
            int cardY = centerY - CARD_HEIGHT / 2;
            
            // Scale based on distance from center
            float scale = 1.0f - Math.abs(offset) * 0.15f;
            int scaledWidth = (int) (CARD_WIDTH * scale);
            int scaledHeight = (int) (CARD_HEIGHT * scale);
            int scaledX = cardX + (CARD_WIDTH - scaledWidth) / 2;
            int scaledY = cardY + (CARD_HEIGHT - scaledHeight) / 2;
            
            if (mouseX >= scaledX && mouseX <= scaledX + scaledWidth &&
                mouseY >= scaledY && mouseY <= scaledY + scaledHeight) {
                hoveredCard = i;
            }
        }
        
        // Check button hovers
        int buttonY = height - 60;
        int confirmX = width / 2 - 110;
        int refreshX = width / 2 + 10;
        
        if (mouseX >= confirmX && mouseX <= confirmX + 100 &&
            mouseY >= buttonY && mouseY <= buttonY + 30) {
            confirmHovered = true;
        }
        
        if (mouseX >= refreshX && mouseX <= refreshX + 100 &&
            mouseY >= buttonY && mouseY <= buttonY + 30) {
            refreshHovered = true;
        }
        
        // Check arrow hovers
        int arrowY = height / 2 - 20;
        if (mouseX >= 20 && mouseX <= 50 && mouseY >= arrowY - 20 && mouseY <= arrowY + 20) {
            leftArrowHovered = true;
        }
        if (mouseX >= width - 50 && mouseX <= width - 20 && mouseY >= arrowY - 20 && mouseY <= arrowY + 20) {
            rightArrowHovered = true;
        }
    }
    
    private void renderCards(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int centerX = width / 2;
        int centerY = height / 2 - 20;
        
        // Interpolate scroll for smooth animation
        float smoothScroll = Mth.lerp(partialTick, scrollOffset, 
            Mth.lerp(SCROLL_SPEED, scrollOffset, targetScrollOffset));
        
        // Render cards from back to front (furthest first)
        List<int[]> renderOrder = new ArrayList<>();
        for (int i = 0; i < displayedOrigins.size(); i++) {
            float offset = i - smoothScroll;
            renderOrder.add(new int[]{i, (int)(Math.abs(offset) * 1000)});
        }
        renderOrder.sort((a, b) -> b[1] - a[1]); // Sort by distance, furthest first
        
        for (int[] entry : renderOrder) {
            int i = entry[0];
            float offset = i - smoothScroll;
            
            // Only render cards within view
            if (Math.abs(offset) > 2.5f) continue;
            
            Origin origin = displayedOrigins.get(i);
            renderCard(graphics, origin, i, centerX, centerY, offset);
        }
    }
    
    private void renderCard(GuiGraphics graphics, Origin origin, int index, 
                           int centerX, int centerY, float offset) {
        // Calculate position and scale
        float absOffset = Math.abs(offset);
        float scale = (1.0f - absOffset * 0.15f) * cardAnimationProgress;
        float alpha = 1.0f - absOffset * 0.3f;
        
        int cardX = (int) (centerX + offset * (CARD_WIDTH + CARD_SPACING) - CARD_WIDTH / 2);
        int cardY = centerY - CARD_HEIGHT / 2;
        
        // Apply scale (centered)
        int scaledWidth = (int) (CARD_WIDTH * scale);
        int scaledHeight = (int) (CARD_HEIGHT * scale);
        int scaledX = cardX + (CARD_WIDTH - scaledWidth) / 2;
        int scaledY = cardY + (CARD_HEIGHT - scaledHeight) / 2;
        
        // Determine colors
        boolean isSelected = index == selectedIndex;
        boolean isHovered = index == hoveredCard;
        int borderColor = isSelected ? COLOR_CARD_SELECTED : (isHovered ? COLOR_CARD_HOVER : COLOR_CARD_BORDER);
        int bgColor = applyAlpha(COLOR_CARD_BG, alpha);
        
        // Card background
        graphics.fill(scaledX, scaledY, scaledX + scaledWidth, scaledY + scaledHeight, bgColor);
        
        // Card border (thicker for selected)
        int borderWidth = isSelected ? 3 : 2;
        drawBorder(graphics, scaledX, scaledY, scaledWidth, scaledHeight, borderColor, borderWidth);
        
        // Origin color accent bar at top
        int accentColor = getOriginAccentColor(origin);
        graphics.fill(scaledX + 2, scaledY + 2, scaledX + scaledWidth - 2, scaledY + 8, accentColor);
        
        // Content (only if card is large enough)
        if (scale > 0.5f) {
            renderCardContent(graphics, origin, scaledX, scaledY, scaledWidth, scaledHeight, scale);
        }
    }
    
    private void renderCardContent(GuiGraphics graphics, Origin origin, 
                                   int x, int y, int w, int h, float scale) {
        int padding = (int)(10 * scale);
        int contentX = x + padding;
        int contentY = y + 15; // Below accent bar
        int contentWidth = w - padding * 2;
        
        // Origin name
        String name = origin.getDisplayName();
        int nameScale = scale > 0.8f ? 1 : 0;
        graphics.drawString(font, name, contentX, contentY, COLOR_TEXT_TITLE, true);
        contentY += 14;
        
        // Impact level
        String impact = "Impact: " + getImpactStars(origin.getImpactLevel());
        graphics.drawString(font, impact, contentX, contentY, 0xFFFFAA44, false);
        contentY += 12;
        
        // Divider
        graphics.fill(contentX, contentY, contentX + contentWidth, contentY + 1, 0x44FFFFFF);
        contentY += 6;
        
        // Description (wrapped)
        if (scale > 0.7f) {
            String desc = origin.getDescription();
            List<String> descLines = wrapText(desc, contentWidth);
            int maxDescLines = 3;
            for (int i = 0; i < Math.min(descLines.size(), maxDescLines); i++) {
                graphics.drawString(font, descLines.get(i), contentX, contentY, COLOR_TEXT_DESC, false);
                contentY += 10;
            }
            if (descLines.size() > maxDescLines) {
                graphics.drawString(font, "...", contentX, contentY - 10, COLOR_TEXT_DESC, false);
            }
            contentY += 4;
        }
        
        // Abilities section
        if (scale > 0.75f && !origin.getAbilities().isEmpty()) {
            graphics.drawString(font, "Abilities:", contentX, contentY, COLOR_TEXT_ABILITY, false);
            contentY += 10;
            
            int maxAbilities = 2;
            List<OriginAbility> abilities = origin.getAbilities();
            for (int i = 0; i < Math.min(abilities.size(), maxAbilities); i++) {
                String abilityName = "• " + formatName(abilities.get(i).getId());
                graphics.drawString(font, abilityName, contentX + 4, contentY, 0xFFCCCCCC, false);
                contentY += 10;
            }
            if (abilities.size() > maxAbilities) {
                graphics.drawString(font, "  +" + (abilities.size() - maxAbilities) + " more", 
                    contentX + 4, contentY, 0xFF888888, false);
                contentY += 10;
            }
            contentY += 2;
        }
        
        // Passives section
        if (scale > 0.75f && !origin.getPassives().isEmpty()) {
            graphics.drawString(font, "Passives:", contentX, contentY, COLOR_TEXT_PASSIVE, false);
            contentY += 10;
            
            int maxPassives = 2;
            List<OriginPassive> passives = origin.getPassives();
            for (int i = 0; i < Math.min(passives.size(), maxPassives); i++) {
                String passiveName = "• " + formatName(passives.get(i).getId());
                graphics.drawString(font, passiveName, contentX + 4, contentY, 0xFFCCCCCC, false);
                contentY += 10;
            }
            if (passives.size() > maxPassives) {
                graphics.drawString(font, "  +" + (passives.size() - maxPassives) + " more", 
                    contentX + 4, contentY, 0xFF888888, false);
            }
        }
    }

    
    private void renderNavigationArrows(GuiGraphics graphics, int mouseX, int mouseY) {
        int arrowY = height / 2 - 20;
        
        // Left arrow
        int leftColor = leftArrowHovered ? 0xFFFFFFFF : 0xFFAAAAAA;
        if (selectedIndex > 0) {
            graphics.drawString(font, "◀", 30, arrowY - 4, leftColor, true);
        }
        
        // Right arrow
        int rightColor = rightArrowHovered ? 0xFFFFFFFF : 0xFFAAAAAA;
        if (selectedIndex < displayedOrigins.size() - 1) {
            graphics.drawString(font, "▶", width - 40, arrowY - 4, rightColor, true);
        }
    }
    
    private void renderBottomButtons(GuiGraphics graphics, int mouseX, int mouseY) {
        int buttonY = height - 60;
        int buttonHeight = 30;
        int buttonWidth = 100;
        
        // Confirm button
        int confirmX = width / 2 - 110;
        int confirmColor = confirmHovered ? COLOR_BUTTON_HOVER : COLOR_BUTTON;
        graphics.fill(confirmX, buttonY, confirmX + buttonWidth, buttonY + buttonHeight, confirmColor);
        drawBorder(graphics, confirmX, buttonY, buttonWidth, buttonHeight, 0xFF5a8a5a, 1);
        String confirmText = "Confirm";
        int confirmTextWidth = font.width(confirmText);
        graphics.drawString(font, confirmText, confirmX + (buttonWidth - confirmTextWidth) / 2, 
            buttonY + (buttonHeight - 8) / 2, COLOR_TEXT_TITLE, true);
        
        // Refresh button
        int refreshX = width / 2 + 10;
        int refreshColor = refreshHovered ? COLOR_REFRESH_HOVER : COLOR_REFRESH;
        graphics.fill(refreshX, buttonY, refreshX + buttonWidth, buttonY + buttonHeight, refreshColor);
        drawBorder(graphics, refreshX, buttonY, buttonWidth, buttonHeight, 0xFF8a5a5a, 1);
        String refreshText = "Shuffle [R]";
        int refreshTextWidth = font.width(refreshText);
        graphics.drawString(font, refreshText, refreshX + (buttonWidth - refreshTextWidth) / 2, 
            buttonY + (buttonHeight - 8) / 2, COLOR_TEXT_TITLE, true);
        
        // Instructions
        String instructions = "← → or scroll to browse • Click card or press Enter to select";
        int instrWidth = font.width(instructions);
        graphics.drawString(font, instructions, (width - instrWidth) / 2, height - 20, 0xFF666666, false);
    }
    
    private void renderCardIndicator(GuiGraphics graphics) {
        int indicatorY = height - 85;
        int dotSpacing = 12;
        int totalWidth = (displayedOrigins.size() - 1) * dotSpacing;
        int startX = (width - totalWidth) / 2;
        
        for (int i = 0; i < displayedOrigins.size(); i++) {
            int dotX = startX + i * dotSpacing;
            int color = i == selectedIndex ? 0xFFFFFFFF : 0xFF555555;
            graphics.fill(dotX - 2, indicatorY - 2, dotX + 2, indicatorY + 2, color);
        }
    }
    
    private void drawBorder(GuiGraphics graphics, int x, int y, int w, int h, int color, int thickness) {
        // Top
        graphics.fill(x, y, x + w, y + thickness, color);
        // Bottom
        graphics.fill(x, y + h - thickness, x + w, y + h, color);
        // Left
        graphics.fill(x, y, x + thickness, y + h, color);
        // Right
        graphics.fill(x + w - thickness, y, x + w, y + h, color);
    }
    
    private int applyAlpha(int color, float alpha) {
        int a = (int) ((color >> 24 & 0xFF) * alpha);
        return (a << 24) | (color & 0x00FFFFFF);
    }
    
    private String getImpactStars(com.veilorigins.api.ImpactLevel level) {
        int stars = switch (level) {
            case LOW -> 1;
            case MEDIUM -> 3;
            case HIGH -> 5;
        };
        return "★".repeat(stars) + "☆".repeat(5 - stars);
    }
    
    private int getOriginAccentColor(Origin origin) {
        String id = origin.getId().toString().toLowerCase();
        
        if (id.contains("fire") || id.contains("cinder") || id.contains("flame")) return 0xFFCC4422;
        if (id.contains("water") || id.contains("tide") || id.contains("aqua")) return 0xFF2266AA;
        if (id.contains("frost") || id.contains("ice") || id.contains("cold")) return 0xFF66AADD;
        if (id.contains("stone") || id.contains("earth") || id.contains("rock")) return 0xFF886644;
        if (id.contains("void") || id.contains("dark") || id.contains("shadow") || id.contains("umbra")) return 0xFF442266;
        if (id.contains("nature") || id.contains("plant") || id.contains("dryad") || id.contains("verdant")) return 0xFF44AA44;
        if (id.contains("star") || id.contains("celestial") || id.contains("light") || id.contains("astral")) return 0xFFDDDD44;
        if (id.contains("necro") || id.contains("death") || id.contains("undead")) return 0xFF444466;
        if (id.contains("wolf") || id.contains("feral") || id.contains("beast") || id.contains("lycan")) return 0xFFAA7744;
        if (id.contains("vampire") || id.contains("blood")) return 0xFF992222;
        if (id.contains("rift") || id.contains("dimension") || id.contains("portal")) return 0xFF6644AA;
        if (id.contains("storm") || id.contains("thunder") || id.contains("lightning")) return 0xFF8888DD;
        if (id.contains("phantom") || id.contains("ghost") || id.contains("spirit")) return 0xFF9988CC;
        
        // Default - hash-based color
        int hash = origin.getDisplayName().hashCode();
        int r = 80 + Math.abs(hash % 100);
        int g = 80 + Math.abs((hash >> 8) % 100);
        int b = 80 + Math.abs((hash >> 16) % 100);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
    
    private String formatName(String id) {
        String[] parts = id.split("_");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                result.append(Character.toUpperCase(part.charAt(0)))
                      .append(part.substring(1)).append(" ");
            }
        }
        return result.toString().trim();
    }
    
    private List<String> wrapText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder current = new StringBuilder();
        
        for (String word : words) {
            String test = current.isEmpty() ? word : current + " " + word;
            if (font.width(test) > maxWidth) {
                if (!current.isEmpty()) {
                    lines.add(current.toString());
                    current = new StringBuilder(word);
                } else {
                    lines.add(word);
                }
            } else {
                current = new StringBuilder(test);
            }
        }
        if (!current.isEmpty()) {
            lines.add(current.toString());
        }
        return lines;
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left click
            // Check card clicks
            if (hoveredCard >= 0 && hoveredCard < displayedOrigins.size()) {
                if (hoveredCard == selectedIndex) {
                    // Double-click on selected card confirms
                    confirmSelection();
                } else {
                    // Select the card
                    selectedIndex = hoveredCard;
                    targetScrollOffset = selectedIndex;
                }
                return true;
            }
            
            // Check confirm button
            if (confirmHovered) {
                confirmSelection();
                return true;
            }
            
            // Check refresh button
            if (refreshHovered) {
                refreshOrigins();
                return true;
            }
            
            // Check arrows
            if (leftArrowHovered && selectedIndex > 0) {
                selectedIndex--;
                targetScrollOffset = selectedIndex;
                return true;
            }
            if (rightArrowHovered && selectedIndex < displayedOrigins.size() - 1) {
                selectedIndex++;
                targetScrollOffset = selectedIndex;
                return true;
            }
        }
        
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY > 0 && selectedIndex > 0) {
            selectedIndex--;
            targetScrollOffset = selectedIndex;
        } else if (scrollY < 0 && selectedIndex < displayedOrigins.size() - 1) {
            selectedIndex++;
            targetScrollOffset = selectedIndex;
        }
        return true;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Left arrow
        if (keyCode == 263 && selectedIndex > 0) {
            selectedIndex--;
            targetScrollOffset = selectedIndex;
            return true;
        }
        // Right arrow
        if (keyCode == 262 && selectedIndex < displayedOrigins.size() - 1) {
            selectedIndex++;
            targetScrollOffset = selectedIndex;
            return true;
        }
        // Enter - confirm
        if (keyCode == 257 || keyCode == 335) {
            confirmSelection();
            return true;
        }
        // R - refresh
        if (keyCode == 82) {
            refreshOrigins();
            return true;
        }
        // Escape - close
        if (keyCode == 256) {
            onClose();
            return true;
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    private void confirmSelection() {
        if (selectedIndex >= 0 && selectedIndex < displayedOrigins.size()) {
            Origin selected = displayedOrigins.get(selectedIndex);
            
            // Send selection to server
            ModPackets.sendToServer(new SelectOriginPacket(selected.getId().toString()));
            
            // Show confirmation message
            Minecraft.getInstance().player.displayClientMessage(
                Component.literal("You have chosen the ")
                    .append(Component.literal(selected.getDisplayName())
                        .withStyle(style -> style.withColor(getOriginAccentColor(selected))))
                    .append(Component.literal(" origin!")),
                false);
            
            VeilOrigins.LOGGER.info("Player selected origin: {}", selected.getId());
            
            onClose();
        }
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
