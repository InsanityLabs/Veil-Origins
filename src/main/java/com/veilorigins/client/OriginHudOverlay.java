package com.veilorigins.client;

import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.OriginAbility;
import com.veilorigins.api.OriginPassive;
import com.veilorigins.api.CustomResourceBar;
import com.veilorigins.api.ResourceType;
import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.config.VeilOriginsConfig;
import com.veilorigins.origins.vampire.VampiricDoubleJumpPassive;
import com.veilorigins.progression.ProgressionSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.world.entity.player.Player;

/**
 * Origin HUD Overlay - Displays origin-specific information on the player's screen.
 * Uses ClientOriginData for accurate server-synced values.
 */
public class OriginHudOverlay implements LayeredDraw.Layer {

    // Colors - Modern palette
    private static final int COLOR_WHITE = 0xFFFFFFFF;
    private static final int COLOR_GRAY = 0xFFAAAAAA;
    private static final int COLOR_DARK_GRAY = 0xFF666666;
    private static final int COLOR_DARKER_GRAY = 0xFF333333;
    private static final int COLOR_RED = 0xFFFF5555;
    private static final int COLOR_GREEN = 0xFF55FF55;
    private static final int COLOR_GOLD = 0xFFFFAA00;
    private static final int COLOR_CYAN = 0xFF55FFFF;
    private static final int COLOR_PURPLE = 0xFFAA55FF;
    
    // Panel colors
    private static final int PANEL_BG = 0xCC1A1A2E;
    private static final int PANEL_BORDER = 0xFF3D3D5C;
    private static final int PANEL_HIGHLIGHT = 0x33FFFFFF;

    @Override
    public void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || mc.options.hideGui)
            return;

        // Use client-side cached data for accurate display
        Origin origin = ClientOriginData.getOrigin();
        if (origin == null) {
            origin = VeilOriginsAPI.getPlayerOrigin(player);
        }
        if (origin == null)
            return;

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        if (VeilOriginsConfig.showResourceBar) {
            renderOriginResourceBar(graphics, mc.font, screenWidth, screenHeight, origin);
        }

        if (VeilOriginsConfig.showOriginHud) {
            renderOriginInfo(graphics, mc.font, screenWidth, screenHeight, origin);
        }

        if (VeilOriginsConfig.showAbilityIndicators) {
            renderAbilityIndicators(graphics, mc.font, screenWidth, screenHeight, player, origin);
        }

        if (VeilOriginsConfig.showPassiveIndicators) {
            renderPassiveIndicators(graphics, mc.font, screenWidth, screenHeight, player, origin);
        }
    }

    private static void renderOriginResourceBar(GuiGraphics graphics, Font font, int screenWidth, int screenHeight, Origin origin) {
        String originPath = origin.getId().getPath();
        
        // Skip for vampires - handled by VampireHudHandler
        if (originPath.equals("vampire") || originPath.equals("vampling")) {
            return;
        }

        ResourceType resourceType = origin.getResourceType();
        if (resourceType == null) return;
        
        float resourceValue = ClientOriginData.getResourceBar();
        float maxValue = resourceType.getMaxAmount();
        
        if (resourceType.hasCustomBar()) {
            CustomResourceBar customBar = resourceType.getCustomBar();
            if (customBar.getStyle() == CustomResourceBar.BarStyle.REPLACE_HUNGER) {
                return;
            }
            CustomBarRenderer.render(graphics, font, customBar, resourceValue, maxValue, screenWidth, screenHeight);
        } else {
            int barWidth = 82;
            int barHeight = 5;
            int barX = screenWidth / 2 - barWidth / 2;
            int barY = screenHeight - 52;
            
            float percent = resourceValue / maxValue;
            int originColor = getOriginColor(origin);
            
            // Background
            graphics.fill(barX - 1, barY - 1, barX + barWidth + 1, barY + barHeight + 1, 0xFF000000);
            graphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF222222);
            
            // Fill
            int fillWidth = (int) (barWidth * percent);
            if (fillWidth > 0) {
                graphics.fill(barX, barY, barX + fillWidth, barY + barHeight, originColor);
                // Highlight
                graphics.fill(barX, barY, barX + fillWidth, barY + 1, lightenColor(originColor, 0.4f));
            }
            
            // Resource name and value
            String text = String.format("%.0f", resourceValue);
            int textWidth = font.width(text);
            graphics.drawString(font, text, barX + barWidth / 2 - textWidth / 2, barY - 9, originColor, true);
        }
    }

    private static void renderOriginInfo(GuiGraphics graphics, Font font, int screenWidth, int screenHeight, Origin origin) {
        int x = 4;
        int y = 4;
        
        int level = ClientOriginData.getLevel();
        int xp = ClientOriginData.getXp();
        float xpProgress = ClientOriginData.getXpProgress();
        int originColor = getOriginColor(origin);
        
        // Calculate panel size
        int panelWidth = 140;
        int panelHeight = VeilOriginsConfig.showXpBar ? 48 : 32;
        
        // Draw panel background with rounded corners effect
        drawPanel(graphics, x, y, panelWidth, panelHeight);
        
        // Origin name with icon
        String originName = origin.getDisplayName();
        graphics.drawString(font, originName, x + 6, y + 6, originColor, true);
        
        // Level display - more prominent
        String levelText = "Lv." + level;
        int levelX = x + panelWidth - font.width(levelText) - 6;
        
        // Level badge background
        int badgeWidth = font.width(levelText) + 8;
        int badgeX = levelX - 4;
        graphics.fill(badgeX, y + 4, badgeX + badgeWidth, y + 16, darkenColor(originColor, 0.6f));
        graphics.fill(badgeX, y + 4, badgeX + badgeWidth, y + 5, originColor);
        graphics.drawString(font, levelText, levelX, y + 6, COLOR_WHITE, true);
        
        // XP Bar
        if (VeilOriginsConfig.showXpBar) {
            int xpBarX = x + 6;
            int xpBarY = y + 22;
            int xpBarWidth = panelWidth - 12;
            int xpBarHeight = 8;
            
            // XP bar background
            graphics.fill(xpBarX, xpBarY, xpBarX + xpBarWidth, xpBarY + xpBarHeight, 0xFF1A1A1A);
            graphics.fill(xpBarX, xpBarY, xpBarX + xpBarWidth, xpBarY + 1, 0xFF000000);
            
            // XP fill with gradient effect
            int fillWidth = (int) (xpBarWidth * xpProgress);
            if (fillWidth > 0) {
                int xpColor = 0xFF44DD44;
                graphics.fill(xpBarX, xpBarY + 1, xpBarX + fillWidth, xpBarY + xpBarHeight, xpColor);
                graphics.fill(xpBarX, xpBarY + 1, xpBarX + fillWidth, xpBarY + 3, lightenColor(xpColor, 0.3f));
            }
            
            // XP text
            int xpForNext = ProgressionSystem.getXPForLevel(level + 1);
            int currentLevelXp = ProgressionSystem.getTotalXPForLevel(level);
            int xpInLevel = xp - currentLevelXp;
            int xpNeeded = xpForNext;
            
            String xpText;
            if (level >= ProgressionSystem.MAX_LEVEL) {
                xpText = "MAX LEVEL";
            } else {
                xpText = xpInLevel + " / " + xpNeeded + " XP";
            }
            
            int xpTextWidth = font.width(xpText);
            graphics.drawString(font, xpText, xpBarX + xpBarWidth / 2 - xpTextWidth / 2, xpBarY + xpBarHeight + 3, COLOR_GRAY, false);
        }
    }

    private static void renderAbilityIndicators(GuiGraphics graphics, Font font, int screenWidth, int screenHeight,
            Player player, Origin origin) {
        int x = screenWidth - 100;
        int startY = screenHeight - 80;

        java.util.List<OriginAbility> abilities = origin.getAbilities();

        for (int i = 0; i < abilities.size(); i++) {
            OriginAbility ability = abilities.get(i);
            int abilityY = startY - (i * 32);

            // Get synced cooldown data
            int cooldownTicks = ClientOriginData.getAbilityCooldown(i);
            int maxCooldown = ClientOriginData.getAbilityMaxCooldown(i);
            boolean isOnCooldown = cooldownTicks > 0;
            boolean canUse = ability.canUse(player) && !isOnCooldown;

            int boxSize = 24;
            
            // Ability box with modern styling
            int bgColor = canUse ? 0xCC224422 : (isOnCooldown ? 0xCC222222 : 0xCC442222);
            int borderColor = canUse ? COLOR_GREEN : (isOnCooldown ? COLOR_GOLD : COLOR_RED);
            
            // Draw box
            graphics.fill(x, abilityY, x + boxSize, abilityY + boxSize, bgColor);
            drawBoxBorder(graphics, x, abilityY, boxSize, boxSize, borderColor);

            if (isOnCooldown && VeilOriginsConfig.showCooldownOverlays) {
                // Cooldown sweep effect (fills from bottom to top as cooldown progresses)
                float cooldownPercent = (float) cooldownTicks / Math.max(1, maxCooldown);
                int cooldownHeight = (int) (boxSize * cooldownPercent);
                graphics.fill(x + 1, abilityY + boxSize - cooldownHeight, x + boxSize - 1, abilityY + boxSize - 1, 0xAA000000);
                
                // Cooldown time text
                int secondsRemaining = (cooldownTicks + 19) / 20; // Round up
                String cdText = String.valueOf(secondsRemaining);
                int textWidth = font.width(cdText);
                graphics.drawString(font, cdText, x + (boxSize - textWidth) / 2, abilityY + (boxSize - 8) / 2, COLOR_GOLD, true);
            } else if (canUse) {
                // Ready indicator
                graphics.drawString(font, "✓", x + (boxSize - font.width("✓")) / 2, abilityY + (boxSize - 8) / 2, COLOR_GREEN, true);
            } else {
                // Unavailable indicator
                graphics.drawString(font, "✗", x + (boxSize - font.width("✗")) / 2, abilityY + (boxSize - 8) / 2, COLOR_RED, true);
            }

            // Ability name
            String abilityName = formatAbilityName(ability.getId());
            int nameColor = canUse ? COLOR_WHITE : COLOR_DARK_GRAY;
            graphics.drawString(font, abilityName, x + boxSize + 4, abilityY + 4, nameColor, true);

            // Keybind hint
            if (VeilOriginsConfig.showKeybindHints) {
                String keybind = "[" + getKeybindForIndex(i) + "]";
                graphics.drawString(font, keybind, x + boxSize + 4, abilityY + 14, COLOR_GRAY, false);
            }
        }
    }

    private static void renderPassiveIndicators(GuiGraphics graphics, Font font, int screenWidth, int screenHeight,
            Player player, Origin origin) {
        int x = screenWidth - 100;
        int startY = screenHeight - 80 - (origin.getAbilities().size() * 32) - 10;

        java.util.List<OriginPassive> passives = origin.getPassives();
        int passiveIndex = 0;

        for (OriginPassive passive : passives) {
            if (passive instanceof VampiricDoubleJumpPassive doubleJumpPassive) {
                int passiveY = startY - (passiveIndex * 24);

                boolean canJump = doubleJumpPassive.canDoubleJump(player);
                int cooldown = doubleJumpPassive.getCooldownRemaining(player);

                int indicatorSize = 18;
                int bgColor = canJump ? 0xCC223344 : 0xCC332233;
                int borderColor = canJump ? COLOR_CYAN : COLOR_PURPLE;
                
                graphics.fill(x, passiveY, x + indicatorSize, passiveY + indicatorSize, bgColor);
                drawBoxBorder(graphics, x, passiveY, indicatorSize, indicatorSize, borderColor);

                if (canJump && cooldown <= 0) {
                    graphics.drawString(font, "⚡", x + (indicatorSize - font.width("⚡")) / 2, passiveY + 5, COLOR_CYAN, true);
                } else if (cooldown > 0) {
                    String cdText = String.valueOf(cooldown / 20);
                    int cdWidth = font.width(cdText);
                    graphics.drawString(font, cdText, x + (indicatorSize - cdWidth) / 2, passiveY + 5, COLOR_GOLD, true);
                }

                graphics.drawString(font, "Double Jump", x + indicatorSize + 4, passiveY + 5, COLOR_CYAN, true);
                passiveIndex++;
            }
        }
    }

    private static void drawPanel(GuiGraphics graphics, int x, int y, int width, int height) {
        // Main background
        graphics.fill(x, y, x + width, y + height, PANEL_BG);
        // Border
        graphics.fill(x, y, x + width, y + 1, PANEL_BORDER);
        graphics.fill(x, y + height - 1, x + width, y + height, PANEL_BORDER);
        graphics.fill(x, y, x + 1, y + height, PANEL_BORDER);
        graphics.fill(x + width - 1, y, x + width, y + height, PANEL_BORDER);
        // Top highlight
        graphics.fill(x + 1, y + 1, x + width - 1, y + 2, PANEL_HIGHLIGHT);
    }

    private static void drawBoxBorder(GuiGraphics graphics, int x, int y, int width, int height, int color) {
        graphics.fill(x, y, x + width, y + 1, color);
        graphics.fill(x, y + height - 1, x + width, y + height, color);
        graphics.fill(x, y, x + 1, y + height, color);
        graphics.fill(x + width - 1, y, x + width, y + height, color);
    }

    private static int getOriginColor(Origin origin) {
        String id = origin.getId().getPath();
        return switch (id) {
            case "veilborn" -> 0xFF9B59B6;
            case "cindersoul" -> 0xFFE67E22;
            case "riftwalker" -> 0xFF8E44AD;
            case "tidecaller" -> 0xFF3498DB;
            case "starborne" -> 0xFFF1C40F;
            case "stoneheart" -> 0xFF7F8C8D;
            case "frostborn" -> 0xFF5DADE2;
            case "umbrakin" -> 0xFF2C3E50;
            case "feralkin" -> 0xFF27AE60;
            case "voidtouched" -> 0xFF6C3483;
            case "skyborn" -> 0xFFECF0F1;
            case "mycomorph" -> 0xFF58D68D;
            case "crystalline" -> 0xFFAED6F1;
            case "technomancer" -> 0xFFE74C3C;
            case "ethereal" -> 0xFFBDC3C7;
            case "vampire" -> 0xFF8B0000;
            case "vampling" -> 0xFFB22222;
            case "werewolf" -> 0xFF8B4513;
            case "werepup" -> 0xFFCD853F;
            case "dryad" -> 0xFF228B22;
            case "necromancer" -> 0xFF4B0082;
            default -> 0xFFFFFFFF;
        };
    }

    private static int darkenColor(int color, float factor) {
        int a = (color >> 24) & 0xFF;
        int r = (int) (((color >> 16) & 0xFF) * factor);
        int g = (int) (((color >> 8) & 0xFF) * factor);
        int b = (int) ((color & 0xFF) * factor);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int lightenColor(int color, float factor) {
        int a = (color >> 24) & 0xFF;
        int r = Math.min(255, (int) (((color >> 16) & 0xFF) * (1 + factor)));
        int g = Math.min(255, (int) (((color >> 8) & 0xFF) * (1 + factor)));
        int b = Math.min(255, (int) ((color & 0xFF) * (1 + factor)));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static String formatAbilityName(String id) {
        if (id == null || id.isEmpty()) return "Ability";
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

    private static String getKeybindForIndex(int index) {
        return switch (index) {
            case 0 -> "R";
            case 1 -> "V";
            case 2 -> "G";
            case 3 -> "B";
            default -> String.valueOf(index + 1);
        };
    }
}
