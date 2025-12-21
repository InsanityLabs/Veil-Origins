package com.veilorigins.api;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * UnicodeFontUtils - Utility class for rendering formatted text with Unicode
 * support.
 * 
 * Provides convenient methods for creating components with Unicode symbols and
 * rendering text with various formatting options. Works in conjunction with
 * UnicodeFontHandler for full Unicode support.
 * 
 * @author Insanity Studios
 * @version 1.0.0
 */
@OnlyIn(Dist.CLIENT)
public final class UnicodeFontUtils {

    private UnicodeFontUtils() {
        // Utility class - prevent instantiation
    }

    // ==================== COMPONENT CREATION ====================

    /**
     * Creates a Component with a Unicode symbol and text.
     * 
     * @param symbol The Unicode symbol (use UnicodeFontHandler constants)
     * @param text   The text following the symbol
     * @param color  The color formatting
     * @return A formatted MutableComponent
     */
    public static MutableComponent symbolText(String symbol, String text, ChatFormatting color) {
        return Component.literal(symbol + " " + text).withStyle(color);
    }

    /**
     * Creates a Component with a checkmark symbol.
     */
    public static MutableComponent checkText(String text, ChatFormatting color) {
        String symbol = UnicodeFontHandler.getSymbol(UnicodeFontHandler.SYMBOL_CHECK, "!");
        return symbolText(symbol, text, color);
    }

    /**
     * Creates a Component with a cross symbol.
     */
    public static MutableComponent crossText(String text, ChatFormatting color) {
        String symbol = UnicodeFontHandler.getSymbol(UnicodeFontHandler.SYMBOL_CROSS, "X");
        return symbolText(symbol, text, color);
    }

    /**
     * Creates a Component with a lightning symbol.
     */
    public static MutableComponent energyText(String text, ChatFormatting color) {
        String symbol = UnicodeFontHandler.getSymbol(UnicodeFontHandler.SYMBOL_LIGHTNING, "^");
        return symbolText(symbol, text, color);
    }

    /**
     * Creates a Component with a diamond symbol.
     */
    public static MutableComponent diamondText(String text, ChatFormatting color) {
        String symbol = UnicodeFontHandler.getSymbol(UnicodeFontHandler.SYMBOL_DIAMOND, "*");
        return symbolText(symbol, text, color);
    }

    /**
     * Creates a Component with a heart symbol.
     */
    public static MutableComponent heartText(String text, ChatFormatting color) {
        String symbol = UnicodeFontHandler.getSymbol(UnicodeFontHandler.SYMBOL_HEART, "<3");
        return symbolText(symbol, text, color);
    }

    /**
     * Creates a Component with a star symbol.
     */
    public static MutableComponent starText(String text, ChatFormatting color) {
        String symbol = UnicodeFontHandler.getSymbol(UnicodeFontHandler.SYMBOL_STAR, "*");
        return symbolText(symbol, text, color);
    }

    /**
     * Creates a Component with an arrow symbol.
     * 
     * @param direction 0=right, 1=left, 2=up, 3=down
     */
    public static MutableComponent arrowText(String text, int direction, ChatFormatting color) {
        String symbol;
        String fallback;
        switch (direction) {
            case 0:
                symbol = UnicodeFontHandler.SYMBOL_ARROW_RIGHT;
                fallback = "->";
                break;
            case 1:
                symbol = UnicodeFontHandler.SYMBOL_ARROW_LEFT;
                fallback = "<-";
                break;
            case 2:
                symbol = UnicodeFontHandler.SYMBOL_ARROW_UP;
                fallback = "^";
                break;
            case 3:
                symbol = UnicodeFontHandler.SYMBOL_ARROW_DOWN;
                fallback = "v";
                break;
            default:
                symbol = UnicodeFontHandler.SYMBOL_ARROW_RIGHT;
                fallback = "->";
        }
        return symbolText(UnicodeFontHandler.getSymbol(symbol, fallback), text, color);
    }

    // ==================== STATUS INDICATORS ====================

    /**
     * Creates a status indicator text with appropriate symbol.
     * 
     * @param label The label text
     * @param ready Whether the status is "ready" (true) or "not ready" (false)
     * @return A formatted component with green check or red cross
     */
    public static MutableComponent statusIndicator(String label, boolean ready) {
        if (ready) {
            return checkText(label, ChatFormatting.GREEN);
        } else {
            return crossText(label, ChatFormatting.RED);
        }
    }

    /**
     * Creates an energy/power indicator.
     * 
     * @param current Current value
     * @param max     Maximum value
     * @return Formatted text like "⚡ 50/100"
     */
    public static MutableComponent energyIndicator(int current, int max) {
        String symbol = UnicodeFontHandler.getSymbol(UnicodeFontHandler.SYMBOL_LIGHTNING, "E:");
        ChatFormatting color = current > max / 2 ? ChatFormatting.GREEN
                : current > max / 4 ? ChatFormatting.YELLOW : ChatFormatting.RED;
        return Component.literal(symbol + " " + current + "/" + max).withStyle(color);
    }

    /**
     * Creates a health/blood indicator.
     * 
     * @param current Current value
     * @param max     Maximum value
     * @return Formatted text like "♥ 50/100"
     */
    public static MutableComponent healthIndicator(int current, int max) {
        String symbol = UnicodeFontHandler.getSymbol(UnicodeFontHandler.SYMBOL_HEART, "HP:");
        ChatFormatting color = current > max / 2 ? ChatFormatting.GREEN
                : current > max / 4 ? ChatFormatting.YELLOW : ChatFormatting.RED;
        return Component.literal(symbol + " " + current + "/" + max).withStyle(color);
    }

    // ==================== PROGRESS BARS ====================

    /**
     * Creates a text-based progress bar using Unicode block characters.
     * 
     * @param percent Progress percentage (0.0 to 1.0)
     * @param width   Width in characters
     * @param filled  Unicode character for filled portion (default: █)
     * @param empty   Unicode character for empty portion (default: ░)
     * @return A progress bar string like "████░░░░░░"
     */
    public static String createProgressBar(float percent, int width, String filled, String empty) {
        // Use Unicode block characters if available
        String fillChar = UnicodeFontHandler.getSymbol(filled, "#");
        String emptyChar = UnicodeFontHandler.getSymbol(empty, "-");

        int filledCount = Math.round(percent * width);
        int emptyCount = width - filledCount;

        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < filledCount; i++) {
            bar.append(fillChar);
        }
        for (int i = 0; i < emptyCount; i++) {
            bar.append(emptyChar);
        }

        return bar.toString();
    }

    /**
     * Creates a progress bar with default Unicode characters.
     */
    public static String createProgressBar(float percent, int width) {
        return createProgressBar(percent, width, "█", "░");
    }

    /**
     * Creates a component with a labeled progress bar.
     * 
     * @param label   Label before the bar
     * @param percent Progress (0.0 to 1.0)
     * @param width   Bar width in characters
     * @param color   Text color
     * @return Formatted component like "Energy: ████░░░░░░ 40%"
     */
    public static MutableComponent labeledProgressBar(String label, float percent, int width, ChatFormatting color) {
        String bar = createProgressBar(percent, width);
        int percentDisplay = Math.round(percent * 100);
        return Component.literal(label + ": " + bar + " " + percentDisplay + "%").withStyle(color);
    }

    // ==================== COOLDOWN FORMATTING ====================

    /**
     * Formats a cooldown value for display.
     * 
     * @param ticks Remaining ticks
     * @return Formatted string (e.g., "2.5s", "45s", "1m 30s")
     */
    public static String formatCooldown(int ticks) {
        if (ticks <= 0) {
            return UnicodeFontHandler.getSymbol(UnicodeFontHandler.SYMBOL_CHECK, "Ready");
        }

        float seconds = ticks / 20.0f;

        if (seconds < 60) {
            return String.format("%.1fs", seconds);
        } else {
            int minutes = (int) (seconds / 60);
            int secs = (int) (seconds % 60);
            return minutes + "m " + secs + "s";
        }
    }

    /**
     * Creates a cooldown component with appropriate coloring.
     * 
     * @param ticks Remaining ticks
     * @return Colored component (green when ready, red when on cooldown)
     */
    public static MutableComponent cooldownComponent(int ticks) {
        String text = formatCooldown(ticks);
        ChatFormatting color = ticks <= 0 ? ChatFormatting.GREEN : ChatFormatting.RED;
        return Component.literal(text).withStyle(color);
    }

    // ==================== DIRECT RENDERING ====================

    /**
     * Draws text with a symbol prefix using the UnicodeFontHandler.
     * 
     * @param graphics The GuiGraphics context
     * @param symbol   The symbol to prefix
     * @param text     The text to draw
     * @param x        X position
     * @param y        Y position
     * @param color    ARGB color
     * @return Total width of rendered text
     */
    public static int drawSymbolText(GuiGraphics graphics, String symbol, String text, int x, int y, int color) {
        String fullText = symbol + " " + text;

        if (UnicodeFontHandler.isReady()) {
            return UnicodeFontHandler.draw(graphics, fullText, x, y, color);
        } else {
            Font font = Minecraft.getInstance().font;
            graphics.drawString(font, fullText, x, y, color, false);
            return font.width(fullText);
        }
    }

    /**
     * Draws a status indicator at the specified position.
     * 
     * @param graphics The GuiGraphics context
     * @param label    Label text
     * @param ready    Status state
     * @param x        X position
     * @param y        Y position
     * @return Width of rendered text
     */
    public static int drawStatusIndicator(GuiGraphics graphics, String label, boolean ready, int x, int y) {
        String symbol = ready ? UnicodeFontHandler.getSymbol(UnicodeFontHandler.SYMBOL_CHECK, "!")
                : UnicodeFontHandler.getSymbol(UnicodeFontHandler.SYMBOL_CROSS, "X");
        int color = ready ? 0xFF55FF55 : 0xFFFF5555;

        return drawSymbolText(graphics, symbol, label, x, y, color);
    }

    /**
     * Draws centered text at the specified position.
     * 
     * @param graphics The GuiGraphics context
     * @param text     Text to draw
     * @param centerX  Center X position
     * @param y        Y position
     * @param color    ARGB color
     */
    public static void drawCenteredText(GuiGraphics graphics, String text, int centerX, int y, int color) {
        int width = getTextWidth(text);
        int x = centerX - width / 2;

        if (UnicodeFontHandler.isReady()) {
            UnicodeFontHandler.draw(graphics, text, x, y, color);
        } else {
            Font font = Minecraft.getInstance().font;
            graphics.drawString(font, text, x, y, color, false);
        }
    }

    /**
     * Gets the width of text, using UnicodeFontHandler if available.
     */
    public static int getTextWidth(String text) {
        if (UnicodeFontHandler.isReady()) {
            return UnicodeFontHandler.width(text);
        }
        return Minecraft.getInstance().font.width(text);
    }

    // ==================== DECORATIVE ELEMENTS ====================

    /**
     * Creates a decorative separator line using Unicode box-drawing characters.
     * 
     * @param width Width in characters
     * @return A line string like "──────────"
     */
    public static String createSeparator(int width) {
        String lineChar = UnicodeFontHandler.getSymbol("─", "-");
        StringBuilder separator = new StringBuilder();
        for (int i = 0; i < width; i++) {
            separator.append(lineChar);
        }
        return separator.toString();
    }

    /**
     * Creates a boxed title using Unicode box-drawing characters.
     * 
     * @param title The title text
     * @return Formatted string like "╔══ Title ══╗"
     */
    public static String createBoxedTitle(String title) {
        String topLeft = UnicodeFontHandler.getSymbol("╔", "+");
        String topRight = UnicodeFontHandler.getSymbol("╗", "+");
        String line = UnicodeFontHandler.getSymbol("═", "=");

        return topLeft + line + line + " " + title + " " + line + line + topRight;
    }

    /**
     * Creates bullet point text.
     * 
     * @param text The text after the bullet
     * @return String like "• Text"
     */
    public static String bulletPoint(String text) {
        String bullet = UnicodeFontHandler.getSymbol("•", "-");
        return bullet + " " + text;
    }
}
