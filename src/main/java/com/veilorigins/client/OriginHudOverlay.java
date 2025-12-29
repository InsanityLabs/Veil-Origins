package com.veilorigins.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.OriginAbility;
import com.veilorigins.api.OriginPassive;
import com.veilorigins.api.ResourceType;
import com.veilorigins.api.UnicodeFontHandler;
import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.config.VeilOriginsConfig;
import com.veilorigins.data.OriginData;
import com.veilorigins.origins.vampire.VampiricDoubleJumpPassive;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * Origin HUD Overlay - Displays origin-specific information on the player's
 * screen.
 * 
 * Features:
 * - Origin resource bar (same size as health bar, positioned above health)
 * - Blood bar for vampires (with blood drop icon styling)
 * - Origin name and level display
 * - Active ability cooldowns with icons
 * - Passive ability indicators (like double jump ready/cooldown)
 */
public class OriginHudOverlay implements LayeredDraw.Layer {
    private static final ResourceLocation RESOURCE_BAR_TEXTURE = ResourceLocation
            .fromNamespaceAndPath(VeilOrigins.MOD_ID, "textures/gui/resource_bar.png");

    // Minecraft health bar dimensions for reference
    // Health bar: 81 pixels wide on each side, 10 hearts at 8 pixels each = 80 +
    // spacing
    // We use 81 pixels to match vanilla style
    private static final int BAR_WIDTH = 81;
    private static final int BAR_HEIGHT = 9;
    private static final int BAR_SEGMENT_SIZE = 8; // Size of each segment (like hearts)

    // Note: Vampire blood bar is now handled by VampireHudHandler which replaces the vanilla hunger bar

    // Colors for ability indicators
    private static final int ABILITY_READY = 0xFF00FF00; // Green - ready
    private static final int ABILITY_COOLDOWN = 0xFFFF6600; // Orange - on cooldown
    private static final int ABILITY_UNAVAILABLE = 0xFF555555; // Gray - unavailable
    private static final int PASSIVE_ACTIVE = 0xFF00AAFF; // Cyan - passive active

    // Text colors
    private static final int COLOR_WHITE = 0xFFFFFF;
    private static final int COLOR_GRAY = 0xAAAAAA;
    private static final int COLOR_DARK_GRAY = 0x555555;
    private static final int COLOR_RED = 0xFF5555;
    private static final int COLOR_DARK_RED = 0xAA0000;
    private static final int COLOR_GREEN = 0x55FF55;
    private static final int COLOR_CYAN = 0x55FFFF;

    // Unicode symbols - using UnicodeFontHandler constants for consistency
    private static final String UNICODE_CHECK = UnicodeFontHandler.SYMBOL_CHECK;
    private static final String UNICODE_CROSS = UnicodeFontHandler.SYMBOL_CROSS;
    private static final String UNICODE_LIGHTNING = UnicodeFontHandler.SYMBOL_LIGHTNING;
    private static final String UNICODE_DIAMOND = UnicodeFontHandler.SYMBOL_DIAMOND;

    // ASCII fallback symbols
    private static final String ASCII_CHECK = "!";
    private static final String ASCII_CROSS = "X";
    private static final String ASCII_LIGHTNING = "^";
    private static final String ASCII_DIAMOND = "*";

    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || mc.options.hideGui)
            return;

        Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
        if (origin == null)
            return;

        OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        // Render components based on config settings
        if (VeilOriginsConfig.showResourceBar) {
            renderOriginResourceBar(graphics, mc.font, screenWidth, screenHeight, data, origin);
        }

        if (VeilOriginsConfig.showOriginHud) {
            renderOriginInfo(graphics, mc.font, screenWidth, screenHeight, data, origin);
        }

        if (VeilOriginsConfig.showAbilityIndicators) {
            renderAbilityIndicators(graphics, mc.font, screenWidth, screenHeight, player, origin);
        }

        if (VeilOriginsConfig.showPassiveIndicators) {
            renderPassiveIndicators(graphics, mc.font, screenWidth, screenHeight, player, origin);
        }
    }

    /**
     * Gets the appropriate symbol based on config settings.
     * Uses Unicode by default, falls back to ASCII if configured.
     * Now utilizes UnicodeFontHandler for proper symbol selection.
     */
    private String getSymbol(String unicode, String ascii) {
        if (!VeilOriginsConfig.useUnicodeSymbols) {
            return ascii;
        }
        // Use UnicodeFontHandler to verify the symbol can be rendered
        return UnicodeFontHandler.getSymbol(unicode, ascii);
    }

    /**
     * Gets the checkmark/ready symbol.
     */
    private String getCheckSymbol() {
        return getSymbol(UNICODE_CHECK, ASCII_CHECK);
    }

    /**
     * Gets the cross/unavailable symbol.
     */
    private String getCrossSymbol() {
        return getSymbol(UNICODE_CROSS, ASCII_CROSS);
    }

    /**
     * Gets the lightning/energy symbol.
     */
    private String getLightningSymbol() {
        return getSymbol(UNICODE_LIGHTNING, ASCII_LIGHTNING);
    }

    /**
     * Gets the diamond/blood symbol.
     */
    private String getDiamondSymbol() {
        return getSymbol(UNICODE_DIAMOND, ASCII_DIAMOND);
    }

    /**
     * Renders the origin resource bar above the health bar.
     * Styled to match Minecraft's vanilla health/armor bar positioning.
     * Note: Vampires/Vamplings use VampireHudHandler which replaces the vanilla hunger bar.
     */
    private void renderOriginResourceBar(GuiGraphics graphics, Font font, int screenWidth, int screenHeight,
            OriginData.PlayerOriginData data, Origin origin) {
        // Skip vampires/vamplings - their blood bar is handled by VampireHudHandler
        // which replaces the vanilla hunger bar with custom sprites
        String originPath = origin.getId().getPath();
        if (originPath.equals("vampire") || originPath.equals("vampling")) {
            return; // Blood bar is rendered by VampireHudHandler
        }

        // Enable blending for proper transparency
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Position bar above the health bar
        // Health bar is at screenHeight - 39, armor is above that
        // We position our bar above armor (which is at -49 if present)
        int barX = screenWidth / 2 - BAR_WIDTH; // Left side of center (like health bar)
        int barY = screenHeight - 54; // Above the armor bar position

        ResourceType resourceType = origin.getResourceType();
        float resourcePercent = data.getResourceBar() / 100.0f;

        renderStandardResourceBar(graphics, font, barX, barY, resourcePercent, data.getResourceBar(), origin,
                resourceType);

        // Disable blending after done
        RenderSystem.disableBlend();
    }

    // Blood bar rendering moved to VampireHudHandler - replaces vanilla hunger bar with custom sprites

    /**
     * Renders the standard resource bar for non-blood origins.
     * Uses RESOURCE_BAR_TEXTURE for textured background if available.
     */
    private void renderStandardResourceBar(GuiGraphics graphics, Font font, int x, int y, float percent,
            float currentValue, Origin origin, ResourceType resourceType) {
        int totalWidth = BAR_WIDTH;

        // Try to render textured background (if texture exists, otherwise fallback to
        // fill)
        // The texture would be a 81x9 (BAR_WIDTH x BAR_HEIGHT) pixel image
        try {
            // Attempt to use the resource bar texture for a more polished look
            RenderSystem.setShaderTexture(0, RESOURCE_BAR_TEXTURE);
        } catch (Exception e) {
            // Texture not found, will use solid colors as fallback
        }

        int filledWidth = (int) (totalWidth * percent);

        int color = getOriginColor(origin);
        int bgColor = 0x88000000;
        int borderColor = darkenColor(color, 0.5f);

        // Draw border
        graphics.fill(x - 1, y - 1, x + totalWidth + 1, y + BAR_HEIGHT + 1, borderColor);

        // Draw background
        graphics.fill(x, y, x + totalWidth, y + BAR_HEIGHT, bgColor);

        // Draw filled portion
        if (filledWidth > 0) {
            // Gradient effect - lighter at top
            int lightColor = lightenColor(color, 0.3f);
            graphics.fill(x, y, x + filledWidth, y + BAR_HEIGHT / 2, lightColor);
            graphics.fill(x, y + BAR_HEIGHT / 2, x + filledWidth, y + BAR_HEIGHT, color);
        }

        // Draw segments for visual appeal - using BAR_SEGMENT_SIZE
        for (int i = 1; i < 10; i++) {
            int segmentX = x + (i * BAR_SEGMENT_SIZE);
            if (segmentX < x + totalWidth) {
                graphics.fill(segmentX, y, segmentX + 1, y + BAR_HEIGHT, 0x33000000);
            }
        }

        // Draw highlight
        graphics.fill(x, y, x + totalWidth, y + 1, 0x33FFFFFF);

        // Draw text - position to the right of the bar
        String resourceName = resourceType != null ? formatResourceName(resourceType.getName()) : "Resource";
        String valueText = String.format("%.0f", currentValue);
        int textX = x + totalWidth + 4;
        int textY = y + 1;

        graphics.drawString(font, resourceName + ": ", textX, textY, color, false);
        graphics.drawString(font, valueText, textX + font.width(resourceName + ": "), textY, COLOR_WHITE, false);
    }

    /**
     * Renders origin name and level info in the top-left area.
     */
    private void renderOriginInfo(GuiGraphics graphics, Font font, int screenWidth, int screenHeight,
            OriginData.PlayerOriginData data, Origin origin) {
        // Position in top-left corner with some padding
        int x = 5;
        int y = 5;

        // Draw semi-transparent background panel
        int panelWidth = 130;
        int panelHeight = VeilOriginsConfig.showXpBar ? 42 : 28; // Shorter panel if XP bar is hidden

        // Background with gradient
        graphics.fill(x, y, x + panelWidth, y + panelHeight, 0xAA000000);
        graphics.fill(x, y, x + panelWidth, y + 1, 0x33FFFFFF); // Top highlight
        graphics.fill(x, y + panelHeight - 1, x + panelWidth, y + panelHeight, 0x11000000); // Bottom shadow

        // Origin name with color
        int originColor = getOriginColor(origin);
        String originName = origin.getDisplayName();
        graphics.drawString(font, originName, x + 5, y + 5, originColor, true);

        // Level display
        int level = data.getOriginLevel();
        int xp = data.getOriginXP();

        graphics.drawString(font, "Level: ", x + 5, y + 17, COLOR_GRAY, false);
        graphics.drawString(font, String.valueOf(level), x + 5 + font.width("Level: "), y + 17, COLOR_WHITE, false);

        // XP bar mini display (only if enabled in config)
        if (VeilOriginsConfig.showXpBar) {
            int xpBarWidth = 100;
            int xpBarHeight = 4;
            int xpBarX = x + 5;
            int xpBarY = y + 30;

            // XP needed for next level (simple formula)
            int xpForNextLevel = Math.max(1, level * 100);
            float xpPercent = Math.min(1.0f, (float) xp / xpForNextLevel);

            // XP bar background
            graphics.fill(xpBarX - 1, xpBarY - 1, xpBarX + xpBarWidth + 1, xpBarY + xpBarHeight + 1, 0xFF222222);
            graphics.fill(xpBarX, xpBarY, xpBarX + xpBarWidth, xpBarY + xpBarHeight, 0xFF333333);

            // XP bar filled
            if (xpPercent > 0) {
                graphics.fill(xpBarX, xpBarY, xpBarX + (int) (xpBarWidth * xpPercent), xpBarY + xpBarHeight,
                        COLOR_GREEN);
            }

            // XP text to the right of the bar
            String xpText = xp + "/" + xpForNextLevel;
            graphics.drawString(font, xpText, xpBarX + xpBarWidth + 4, xpBarY - 1, COLOR_GREEN, false);
        }
    }

    /**
     * Renders ability cooldown indicators on the right side of the screen.
     */
    private void renderAbilityIndicators(GuiGraphics graphics, Font font, int screenWidth, int screenHeight,
            Player player, Origin origin) {
        // Position on the right side, above inventory area
        int x = screenWidth - 90;
        int startY = screenHeight - 70;

        java.util.List<OriginAbility> abilities = origin.getAbilities();

        for (int i = 0; i < abilities.size(); i++) {
            OriginAbility ability = abilities.get(i);
            int abilityY = startY - (i * 28);

            // Ability box background
            int boxSize = 20;

            // Check if ability can be used (has enough resources, etc.)
            boolean canUse = ability.canUse(player);

            // Determine background color based on state
            int bgColor;
            if (!canUse && !ability.isOnCooldown()) {
                bgColor = ABILITY_UNAVAILABLE; // Gray - unavailable (not enough resources)
            } else if (ability.isOnCooldown()) {
                bgColor = 0xAA333333; // Dark gray - on cooldown
            } else {
                bgColor = 0xAA222244; // Ready
            }
            graphics.fill(x, abilityY, x + boxSize, abilityY + boxSize, bgColor);

            // Ability status icon/indicator
            if (ability.isOnCooldown()) {
                // Cooldown overlay (only if enabled)
                if (VeilOriginsConfig.showCooldownOverlays) {
                    float cooldownPercent = ability.getCooldown() / (float) (ability.getCooldown() + 1);
                    int cooldownHeight = (int) (boxSize * cooldownPercent);
                    graphics.fill(x, abilityY, x + boxSize, abilityY + cooldownHeight, 0xBB000000);
                }

                // Cooldown number
                int secondsRemaining = ability.getCooldown() / 20;
                String cdText = secondsRemaining > 0 ? String.valueOf(secondsRemaining) : "<1";
                int textWidth = font.width(cdText);
                graphics.drawString(font, cdText, x + (boxSize - textWidth) / 2, abilityY + 6, COLOR_RED, false);
            } else if (!canUse) {
                // Unavailable indicator (not enough resources)
                graphics.fill(x + 2, abilityY + 2, x + boxSize - 2, abilityY + boxSize - 2, ABILITY_UNAVAILABLE);
                String crossSymbol = getCrossSymbol();
                int crossWidth = font.width(crossSymbol);
                graphics.drawString(font, crossSymbol, x + (boxSize - crossWidth) / 2, abilityY + 6, COLOR_DARK_GRAY,
                        false);
            } else {
                // Ready indicator
                graphics.fill(x + 2, abilityY + 2, x + boxSize - 2, abilityY + boxSize - 2, ABILITY_READY);
                String checkSymbol = getCheckSymbol();
                int checkWidth = font.width(checkSymbol);
                graphics.drawString(font, checkSymbol, x + (boxSize - checkWidth) / 2, abilityY + 6, COLOR_WHITE,
                        false);
            }

            // Border based on status
            int borderColor;
            if (!canUse && !ability.isOnCooldown()) {
                borderColor = ABILITY_UNAVAILABLE;
            } else if (ability.isOnCooldown()) {
                borderColor = ABILITY_COOLDOWN;
            } else {
                borderColor = ABILITY_READY;
            }
            drawBoxBorder(graphics, x, abilityY, boxSize, boxSize, borderColor);

            // Ability name and keybind - positioned to the right of box
            String abilityName = formatAbilityName(ability.getId());
            int nameColor = canUse ? COLOR_WHITE : COLOR_DARK_GRAY;
            graphics.drawString(font, abilityName, x + boxSize + 4, abilityY + 2, nameColor, false);

            // Keybind hint (only if enabled)
            if (VeilOriginsConfig.showKeybindHints) {
                String keybind = "[" + getKeybindForIndex(i) + "]";
                graphics.drawString(font, keybind, x + boxSize + 4, abilityY + 12, COLOR_GRAY, false);
            }
        }
    }

    /**
     * Renders passive ability indicators (like double jump ready state).
     */
    private void renderPassiveIndicators(GuiGraphics graphics, Font font, int screenWidth, int screenHeight,
            Player player, Origin origin) {
        // Position above the active abilities
        int x = screenWidth - 90;
        int startY = screenHeight - 70 - (origin.getAbilities().size() * 28) - 15;

        java.util.List<OriginPassive> passives = origin.getPassives();
        int passiveIndex = 0;

        for (OriginPassive passive : passives) {
            // Only show passives that have visual indicators
            if (passive instanceof VampiricDoubleJumpPassive doubleJumpPassive) {
                int passiveY = startY - (passiveIndex * 22);

                boolean canJump = doubleJumpPassive.canDoubleJump(player);
                int cooldown = doubleJumpPassive.getCooldownRemaining(player);

                // Mini indicator for passive ability
                int indicatorSize = 14;
                int bgColor = canJump ? 0xAA224422 : 0xAA442222;
                graphics.fill(x, passiveY, x + indicatorSize, passiveY + indicatorSize, bgColor);

                if (canJump && cooldown <= 0) {
                    // Ready to use
                    graphics.fill(x + 2, passiveY + 2, x + indicatorSize - 2, passiveY + indicatorSize - 2,
                            PASSIVE_ACTIVE);
                    String lightningSymbol = getLightningSymbol();
                    int symbolWidth = font.width(lightningSymbol);
                    graphics.drawString(font, lightningSymbol, x + (indicatorSize - symbolWidth) / 2, passiveY + 3,
                            COLOR_WHITE, false);
                } else {
                    // On cooldown or unavailable
                    if (cooldown > 0) {
                        String cdText = String.valueOf(cooldown / 20);
                        int cdWidth = font.width(cdText);
                        graphics.drawString(font, cdText, x + (indicatorSize - cdWidth) / 2, passiveY + 3, COLOR_RED,
                                false);
                    } else {
                        graphics.drawString(font, "-", x + (indicatorSize - font.width("-")) / 2, passiveY + 3,
                                COLOR_DARK_GRAY, false);
                    }
                }

                // Border
                int borderColor = canJump ? PASSIVE_ACTIVE : 0xFF555555;
                drawBoxBorder(graphics, x, passiveY, indicatorSize, indicatorSize, borderColor);

                // Label
                graphics.drawString(font, "Double Jump", x + indicatorSize + 4, passiveY + 3, COLOR_CYAN, false);

                passiveIndex++;
            }
            // Add more passive type checks here as needed
        }
    }

    /**
     * Draws a border around a box.
     */
    private void drawBoxBorder(GuiGraphics graphics, int x, int y, int width, int height, int color) {
        graphics.fill(x, y, x + width, y + 1, color); // Top
        graphics.fill(x, y + height - 1, x + width, y + height, color); // Bottom
        graphics.fill(x, y, x + 1, y + height, color); // Left
        graphics.fill(x + width - 1, y, x + width, y + height, color); // Right
    }

    /**
     * Returns the color associated with an origin.
     */
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
            case "vampire" -> 0xFF8B0000; // Dark red (blood)
            case "vampling" -> 0xFFB22222; // Lighter blood red
            case "werewolf" -> 0xFF8B4513; // Brown
            case "werepup" -> 0xFFCD853F; // Lighter brown
            case "dryad" -> 0xFF228B22; // Forest green
            case "necromancer" -> 0xFF4B0082; // Indigo
            default -> 0xFFFFFFFF; // White
        };
    }

    /**
     * Darkens a color by a given factor.
     */
    private int darkenColor(int color, float factor) {
        int a = (color >> 24) & 0xFF;
        int r = (int) (((color >> 16) & 0xFF) * factor);
        int g = (int) (((color >> 8) & 0xFF) * factor);
        int b = (int) ((color & 0xFF) * factor);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Lightens a color by a given factor.
     */
    private int lightenColor(int color, float factor) {
        int a = (color >> 24) & 0xFF;
        int r = Math.min(255, (int) (((color >> 16) & 0xFF) * (1 + factor)));
        int g = Math.min(255, (int) (((color >> 8) & 0xFF) * (1 + factor)));
        int b = Math.min(255, (int) ((color & 0xFF) * (1 + factor)));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /**
     * Formats a resource name for display.
     */
    private String formatResourceName(String name) {
        if (name == null || name.isEmpty())
            return "Resource";
        // Convert snake_case to Title Case
        StringBuilder result = new StringBuilder();
        for (String word : name.split("_")) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return result.toString().trim();
    }

    /**
     * Formats an ability ID for display.
     */
    private String formatAbilityName(String id) {
        if (id == null || id.isEmpty())
            return "Ability";
        // Convert snake_case to Title Case
        StringBuilder result = new StringBuilder();
        for (String word : id.split("_")) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }
        }
        return result.toString().trim();
    }

    /**
     * Gets the keybind string for an ability index.
     */
    private String getKeybindForIndex(int index) {
        return switch (index) {
            case 0 -> "R";
            case 1 -> "V";
            case 2 -> "G";
            case 3 -> "B";
            default -> String.valueOf(index + 1);
        };
    }
}
