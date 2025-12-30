package com.veilorigins.client;

import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.OriginAbility;
import com.veilorigins.api.OriginPassive;
import com.veilorigins.api.CustomResourceBar;
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
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.gui.GuiLayer;

/**
 * Origin HUD Overlay - Displays origin-specific information on the player's screen.
 * Implements GuiLayer for NeoForge 1.21.10 GUI layer registration.
 */
public class OriginHudOverlay implements GuiLayer {
    private static final Identifier RESOURCE_BAR_TEXTURE = Identifier
            .fromNamespaceAndPath(VeilOrigins.MOD_ID, "textures/gui/resource_bar.png");

    private static final int BAR_WIDTH = 81;
    private static final int BAR_HEIGHT = 9;
    private static final int BAR_SEGMENT_SIZE = 8;

    // Colors
    private static final int BLOOD_COLOR_FULL = 0xFF8B0000;
    private static final int BLOOD_COLOR_MEDIUM = 0xFFB22222;
    private static final int BLOOD_COLOR_LOW = 0xFF4A0000;
    private static final int BLOOD_BAR_BORDER = 0xFF2A0000;
    private static final int BLOOD_BAR_BG = 0x88000000;

    private static final int ABILITY_READY = 0xFF00FF00;
    private static final int ABILITY_COOLDOWN = 0xFFFF6600;
    private static final int ABILITY_UNAVAILABLE = 0xFF555555;
    private static final int PASSIVE_ACTIVE = 0xFF00AAFF;

    private static final int COLOR_WHITE = 0xFFFFFFFF;
    private static final int COLOR_GRAY = 0xFFAAAAAA;
    private static final int COLOR_DARK_GRAY = 0xFF555555;
    private static final int COLOR_RED = 0xFFFF5555;
    private static final int COLOR_DARK_RED = 0xFFAA0000;
    private static final int COLOR_GREEN = 0xFF55FF55;
    private static final int COLOR_CYAN = 0xFF55FFFF;

    private static final String UNICODE_CHECK = UnicodeFontHandler.SYMBOL_CHECK;
    private static final String UNICODE_CROSS = UnicodeFontHandler.SYMBOL_CROSS;
    private static final String UNICODE_LIGHTNING = UnicodeFontHandler.SYMBOL_LIGHTNING;
    private static final String UNICODE_DIAMOND = UnicodeFontHandler.SYMBOL_DIAMOND;

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

    private static String getSymbol(String unicode, String ascii) {
        if (!VeilOriginsConfig.useUnicodeSymbols) {
            return ascii;
        }
        return UnicodeFontHandler.getSymbol(unicode, ascii);
    }

    private static String getCheckSymbol() {
        return getSymbol(UNICODE_CHECK, ASCII_CHECK);
    }

    private static String getCrossSymbol() {
        return getSymbol(UNICODE_CROSS, ASCII_CROSS);
    }

    private static String getLightningSymbol() {
        return getSymbol(UNICODE_LIGHTNING, ASCII_LIGHTNING);
    }

    private static String getDiamondSymbol() {
        return getSymbol(UNICODE_DIAMOND, ASCII_DIAMOND);
    }

    private static void renderOriginResourceBar(GuiGraphics graphics, Font font, int screenWidth, int screenHeight,
            OriginData.PlayerOriginData data, Origin origin) {
        String originPath = origin.getId().getPath();
        
        // Skip rendering blood bar here for vampires - it's rendered by VampireHudHandler
        // in place of the vanilla hunger bar
        boolean isBloodOrigin = originPath.equals("vampire") || originPath.equals("vampling");
        if (isBloodOrigin) {
            return; // Blood bar is rendered by VampireHudHandler
        }

        ResourceType resourceType = origin.getResourceType();
        if (resourceType == null) return;
        
        float resourceValue = data.getResourceBar();
        float maxValue = resourceType.getMaxAmount();
        
        // Check if this resource type has a custom bar configuration
        if (resourceType.hasCustomBar()) {
            CustomResourceBar customBar = resourceType.getCustomBar();
            
            // Skip if it replaces hunger (handled elsewhere)
            if (customBar.getStyle() == CustomResourceBar.BarStyle.REPLACE_HUNGER) {
                return;
            }
            
            // Use the custom bar renderer
            CustomBarRenderer.render(graphics, font, customBar, resourceValue, maxValue, screenWidth, screenHeight);
        } else {
            // Fall back to standard rendering
            int barX = screenWidth / 2 - BAR_WIDTH;
            int barY = screenHeight - 54;
            float resourcePercent = resourceValue / 100.0f;
            renderStandardResourceBar(graphics, font, barX, barY, resourcePercent, resourceValue, origin, resourceType);
        }
    }

    private static void renderBloodBar(GuiGraphics graphics, Font font, int x, int y, float percent,
            float currentValue, boolean isFullVampire) {
        int totalWidth = BAR_WIDTH;
        int filledWidth = (int) (totalWidth * percent);

        graphics.fill(x - 1, y - 1, x + totalWidth + 1, y + BAR_HEIGHT + 1, BLOOD_BAR_BORDER);
        graphics.fill(x, y, x + totalWidth, y + BAR_HEIGHT, BLOOD_BAR_BG);

        int bloodColor;
        if (percent > 0.5f) {
            bloodColor = BLOOD_COLOR_FULL;
        } else if (percent > 0.25f) {
            bloodColor = BLOOD_COLOR_MEDIUM;
        } else {
            bloodColor = BLOOD_COLOR_LOW;
            long time = System.currentTimeMillis();
            if ((time / 500) % 2 == 0) {
                bloodColor = 0xFF600000;
            }
        }

        if (filledWidth > 0) {
            graphics.fill(x, y, x + filledWidth, y + BAR_HEIGHT, bloodColor);
        }

        graphics.fill(x, y, x + totalWidth, y + 1, 0x33FFFFFF);

        int textX = x + totalWidth + 4;
        int textY = y + 1;

        int labelColor = isFullVampire ? COLOR_DARK_RED : COLOR_RED;
        String valueText = String.format("%.0f", currentValue);
        String bloodLabel = getDiamondSymbol() + " Blood: ";

        graphics.drawString(font, bloodLabel, textX, textY, labelColor, false);
        graphics.drawString(font, valueText, textX + font.width(bloodLabel), textY, COLOR_WHITE, false);
    }

    private static void renderStandardResourceBar(GuiGraphics graphics, Font font, int x, int y, float percent,
            float currentValue, Origin origin, ResourceType resourceType) {
        int totalWidth = BAR_WIDTH;
        int filledWidth = (int) (totalWidth * percent);

        int color = getOriginColor(origin);
        int bgColor = 0x88000000;
        int borderColor = darkenColor(color, 0.5f);

        graphics.fill(x - 1, y - 1, x + totalWidth + 1, y + BAR_HEIGHT + 1, borderColor);
        graphics.fill(x, y, x + totalWidth, y + BAR_HEIGHT, bgColor);

        if (filledWidth > 0) {
            int lightColor = lightenColor(color, 0.3f);
            graphics.fill(x, y, x + filledWidth, y + BAR_HEIGHT / 2, lightColor);
            graphics.fill(x, y + BAR_HEIGHT / 2, x + filledWidth, y + BAR_HEIGHT, color);
        }

        for (int i = 1; i < 10; i++) {
            int segmentX = x + (i * BAR_SEGMENT_SIZE);
            if (segmentX < x + totalWidth) {
                graphics.fill(segmentX, y, segmentX + 1, y + BAR_HEIGHT, 0x33000000);
            }
        }

        graphics.fill(x, y, x + totalWidth, y + 1, 0x33FFFFFF);

        String resourceName = resourceType != null ? formatResourceName(resourceType.getName()) : "Resource";
        String valueText = String.format("%.0f", currentValue);
        int textX = x + totalWidth + 4;
        int textY = y + 1;

        graphics.drawString(font, resourceName + ": ", textX, textY, color, false);
        graphics.drawString(font, valueText, textX + font.width(resourceName + ": "), textY, COLOR_WHITE, false);
    }

    private static void renderOriginInfo(GuiGraphics graphics, Font font, int screenWidth, int screenHeight,
            OriginData.PlayerOriginData data, Origin origin) {
        int x = 5;
        int y = 5;

        int panelWidth = 130;
        int panelHeight = VeilOriginsConfig.showXpBar ? 42 : 28;

        graphics.fill(x, y, x + panelWidth, y + panelHeight, 0xAA000000);
        graphics.fill(x, y, x + panelWidth, y + 1, 0x33FFFFFF);
        graphics.fill(x, y + panelHeight - 1, x + panelWidth, y + panelHeight, 0x11000000);

        int originColor = getOriginColor(origin);
        String originName = origin.getDisplayName();
        graphics.drawString(font, originName, x + 5, y + 5, originColor, true);

        int level = data.getOriginLevel();
        int xp = data.getOriginXP();

        graphics.drawString(font, "Level: ", x + 5, y + 17, COLOR_GRAY, false);
        graphics.drawString(font, String.valueOf(level), x + 5 + font.width("Level: "), y + 17, COLOR_WHITE, false);

        if (VeilOriginsConfig.showXpBar) {
            int xpBarWidth = 100;
            int xpBarHeight = 4;
            int xpBarX = x + 5;
            int xpBarY = y + 30;

            int xpForNextLevel = Math.max(1, level * 100);
            float xpPercent = Math.min(1.0f, (float) xp / xpForNextLevel);

            graphics.fill(xpBarX - 1, xpBarY - 1, xpBarX + xpBarWidth + 1, xpBarY + xpBarHeight + 1, 0xFF222222);
            graphics.fill(xpBarX, xpBarY, xpBarX + xpBarWidth, xpBarY + xpBarHeight, 0xFF333333);

            if (xpPercent > 0) {
                graphics.fill(xpBarX, xpBarY, xpBarX + (int) (xpBarWidth * xpPercent), xpBarY + xpBarHeight, COLOR_GREEN);
            }

            String xpText = xp + "/" + xpForNextLevel;
            graphics.drawString(font, xpText, xpBarX + xpBarWidth + 4, xpBarY - 1, COLOR_GREEN, false);
        }
    }

    private static void renderAbilityIndicators(GuiGraphics graphics, Font font, int screenWidth, int screenHeight,
            Player player, Origin origin) {
        int x = screenWidth - 90;
        int startY = screenHeight - 70;

        java.util.List<OriginAbility> abilities = origin.getAbilities();

        for (int i = 0; i < abilities.size(); i++) {
            OriginAbility ability = abilities.get(i);
            int abilityY = startY - (i * 28);

            int boxSize = 20;
            boolean canUse = ability.canUse(player);

            int bgColor;
            if (!canUse && !ability.isOnCooldown()) {
                bgColor = ABILITY_UNAVAILABLE;
            } else if (ability.isOnCooldown()) {
                bgColor = 0xAA333333;
            } else {
                bgColor = 0xAA222244;
            }
            graphics.fill(x, abilityY, x + boxSize, abilityY + boxSize, bgColor);

            if (ability.isOnCooldown()) {
                if (VeilOriginsConfig.showCooldownOverlays) {
                    float cooldownPercent = ability.getCooldown() / (float) (ability.getCooldown() + 1);
                    int cooldownHeight = (int) (boxSize * cooldownPercent);
                    graphics.fill(x, abilityY, x + boxSize, abilityY + cooldownHeight, 0xBB000000);
                }

                int secondsRemaining = ability.getCooldown() / 20;
                String cdText = secondsRemaining > 0 ? String.valueOf(secondsRemaining) : "<1";
                int textWidth = font.width(cdText);
                graphics.drawString(font, cdText, x + (boxSize - textWidth) / 2, abilityY + 6, COLOR_RED, false);
            } else if (!canUse) {
                graphics.fill(x + 2, abilityY + 2, x + boxSize - 2, abilityY + boxSize - 2, ABILITY_UNAVAILABLE);
                String crossSymbol = getCrossSymbol();
                int crossWidth = font.width(crossSymbol);
                graphics.drawString(font, crossSymbol, x + (boxSize - crossWidth) / 2, abilityY + 6, COLOR_DARK_GRAY, false);
            } else {
                graphics.fill(x + 2, abilityY + 2, x + boxSize - 2, abilityY + boxSize - 2, ABILITY_READY);
                String checkSymbol = getCheckSymbol();
                int checkWidth = font.width(checkSymbol);
                graphics.drawString(font, checkSymbol, x + (boxSize - checkWidth) / 2, abilityY + 6, COLOR_WHITE, false);
            }

            int borderColor;
            if (!canUse && !ability.isOnCooldown()) {
                borderColor = ABILITY_UNAVAILABLE;
            } else if (ability.isOnCooldown()) {
                borderColor = ABILITY_COOLDOWN;
            } else {
                borderColor = ABILITY_READY;
            }
            drawBoxBorder(graphics, x, abilityY, boxSize, boxSize, borderColor);

            String abilityName = formatAbilityName(ability.getId());
            int nameColor = canUse ? COLOR_WHITE : COLOR_DARK_GRAY;
            graphics.drawString(font, abilityName, x + boxSize + 4, abilityY + 2, nameColor, false);

            if (VeilOriginsConfig.showKeybindHints) {
                String keybind = "[" + getKeybindForIndex(i) + "]";
                graphics.drawString(font, keybind, x + boxSize + 4, abilityY + 12, COLOR_GRAY, false);
            }
        }
    }

    private static void renderPassiveIndicators(GuiGraphics graphics, Font font, int screenWidth, int screenHeight,
            Player player, Origin origin) {
        int x = screenWidth - 90;
        int startY = screenHeight - 70 - (origin.getAbilities().size() * 28) - 15;

        java.util.List<OriginPassive> passives = origin.getPassives();
        int passiveIndex = 0;

        for (OriginPassive passive : passives) {
            if (passive instanceof VampiricDoubleJumpPassive doubleJumpPassive) {
                int passiveY = startY - (passiveIndex * 22);

                boolean canJump = doubleJumpPassive.canDoubleJump(player);
                int cooldown = doubleJumpPassive.getCooldownRemaining(player);

                int indicatorSize = 14;
                int bgColor = canJump ? 0xAA224422 : 0xAA442222;
                graphics.fill(x, passiveY, x + indicatorSize, passiveY + indicatorSize, bgColor);

                if (canJump && cooldown <= 0) {
                    graphics.fill(x + 2, passiveY + 2, x + indicatorSize - 2, passiveY + indicatorSize - 2, PASSIVE_ACTIVE);
                    String lightningSymbol = getLightningSymbol();
                    int symbolWidth = font.width(lightningSymbol);
                    graphics.drawString(font, lightningSymbol, x + (indicatorSize - symbolWidth) / 2, passiveY + 3, COLOR_WHITE, false);
                } else {
                    if (cooldown > 0) {
                        String cdText = String.valueOf(cooldown / 20);
                        int cdWidth = font.width(cdText);
                        graphics.drawString(font, cdText, x + (indicatorSize - cdWidth) / 2, passiveY + 3, COLOR_RED, false);
                    } else {
                        graphics.drawString(font, "-", x + (indicatorSize - font.width("-")) / 2, passiveY + 3, COLOR_DARK_GRAY, false);
                    }
                }

                int borderColor = canJump ? PASSIVE_ACTIVE : 0xFF555555;
                drawBoxBorder(graphics, x, passiveY, indicatorSize, indicatorSize, borderColor);

                graphics.drawString(font, "Double Jump", x + indicatorSize + 4, passiveY + 3, COLOR_CYAN, false);

                passiveIndex++;
            }
        }
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

    private static String formatResourceName(String name) {
        if (name == null || name.isEmpty())
            return "Resource";
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

    private static String formatAbilityName(String id) {
        if (id == null || id.isEmpty())
            return "Ability";
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
