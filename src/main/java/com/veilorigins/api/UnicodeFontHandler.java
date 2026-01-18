package com.veilorigins.api;

import com.veilorigins.VeilOrigins;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * UnicodeFontHandler - A comprehensive Unicode font rendering system for
 * Minecraft 1.21.10+.
 * 
 * This handler provides support for rendering Unicode characters.
 * In NeoForge 1.21.10, Minecraft's built-in font has significantly improved
 * Unicode support, so we primarily delegate to it while providing our
 * convenient API and symbol constants.
 * 
 * @author Insanity Studios
 * @version 2.0.0
 * @since Minecraft 1.21.10
 */
@OnlyIn(Dist.CLIENT)
public class UnicodeFontHandler {

    // Singleton instance
    private static UnicodeFontHandler instance;

    // Settings
    private static final int DEFAULT_FONT_SIZE = 16;

    // Common Unicode symbols used in the mod
    public static final String SYMBOL_CHECK = "\u2713"; // ✓
    public static final String SYMBOL_CROSS = "\u2717"; // ✗
    public static final String SYMBOL_LIGHTNING = "\u26A1"; // ⚡
    public static final String SYMBOL_DIAMOND = "\u2666"; // ♦
    public static final String SYMBOL_HEART = "\u2665"; // ♥
    public static final String SYMBOL_STAR = "\u2605"; // ★
    public static final String SYMBOL_CIRCLE = "\u25CF"; // ●
    public static final String SYMBOL_ARROW_RIGHT = "\u2192"; // →
    public static final String SYMBOL_ARROW_LEFT = "\u2190"; // ←
    public static final String SYMBOL_ARROW_UP = "\u2191"; // ↑
    public static final String SYMBOL_ARROW_DOWN = "\u2193"; // ↓
    public static final String SYMBOL_INFINITY = "\u221E"; // ∞
    public static final String SYMBOL_SKULL = "\u2620"; // ☠
    public static final String SYMBOL_SUN = "\u2600"; // ☀
    public static final String SYMBOL_MOON = "\u263D"; // ☽
    public static final String SYMBOL_FIRE = "\u2668"; // ♨ (Hot springs, used as fire)
    public static final String SYMBOL_WATER = "\u2652"; // ♒ (Aquarius, used as water)
    
    // ASCII fallbacks for symbols
    private static final Map<String, String> ASCII_FALLBACKS = new HashMap<>();
    static {
        ASCII_FALLBACKS.put(SYMBOL_CHECK, "[OK]");
        ASCII_FALLBACKS.put(SYMBOL_CROSS, "[X]");
        ASCII_FALLBACKS.put(SYMBOL_LIGHTNING, "*");
        ASCII_FALLBACKS.put(SYMBOL_DIAMOND, "<>");
        ASCII_FALLBACKS.put(SYMBOL_HEART, "<3");
        ASCII_FALLBACKS.put(SYMBOL_STAR, "*");
        ASCII_FALLBACKS.put(SYMBOL_CIRCLE, "o");
        ASCII_FALLBACKS.put(SYMBOL_ARROW_RIGHT, "->");
        ASCII_FALLBACKS.put(SYMBOL_ARROW_LEFT, "<-");
        ASCII_FALLBACKS.put(SYMBOL_ARROW_UP, "^");
        ASCII_FALLBACKS.put(SYMBOL_ARROW_DOWN, "v");
        ASCII_FALLBACKS.put(SYMBOL_INFINITY, "INF");
        ASCII_FALLBACKS.put(SYMBOL_SKULL, "X_X");
        ASCII_FALLBACKS.put(SYMBOL_SUN, "(*)");
        ASCII_FALLBACKS.put(SYMBOL_MOON, "C");
        ASCII_FALLBACKS.put(SYMBOL_FIRE, "~");
        ASCII_FALLBACKS.put(SYMBOL_WATER, "~");
    }

    // Font and rendering
    private java.awt.Font awtFont;
    private int fontSize;
    private boolean initialized = false;

    /**
     * Glyph information structure containing rendering data for a single character.
     */
    public static class GlyphInfo {
        public final char character;
        public final int width;
        public final int height;
        public final float u0, v0, u1, v1; // Texture coordinates
        public final int xOffset;
        public final int yOffset;
        public final int advance;
        public final boolean isValid;

        public GlyphInfo(char character, int width, int height, float u0, float v0, float u1, float v1,
                int xOffset, int yOffset, int advance, boolean isValid) {
            this.character = character;
            this.width = width;
            this.height = height;
            this.u0 = u0;
            this.v0 = v0;
            this.u1 = u1;
            this.v1 = v1;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.advance = advance;
            this.isValid = isValid;
        }

        /**
         * Creates an invalid glyph info for characters that couldn't be rendered.
         */
        public static GlyphInfo invalid(char character) {
            return new GlyphInfo(character, 0, 0, 0, 0, 0, 0, 0, 0, 8, false);
        }
    }

    /**
     * Private constructor for singleton pattern.
     */
    private UnicodeFontHandler() {
        this.fontSize = DEFAULT_FONT_SIZE;
    }

    /**
     * Gets the singleton instance of the UnicodeFontHandler.
     * Creates and initializes the instance if it doesn't exist.
     * 
     * @return The UnicodeFontHandler instance
     */
    public static UnicodeFontHandler getInstance() {
        if (instance == null) {
            instance = new UnicodeFontHandler();
        }
        return instance;
    }

    /**
     * Initializes the Unicode font handler.
     * This should be called during client mod initialization.
     * 
     * @return true if initialization was successful
     */
    public static boolean initialize() {
        UnicodeFontHandler handler = getInstance();
        return handler.init();
    }

    /**
     * Initializes the font handler with the default font.
     * 
     * @return true if initialization was successful
     */
    public boolean init() {
        return init(null, DEFAULT_FONT_SIZE);
    }

    /**
     * Initializes the font handler with a custom font.
     * 
     * @param fontPath Path to the TrueType font file (null for system default)
     * @param size     Font size in pixels
     * @return true if initialization was successful
     */
    public boolean init(@Nullable String fontPath, int size) {
        if (initialized) {
            VeilOrigins.LOGGER.debug("UnicodeFontHandler already initialized");
            return true;
        }

        this.fontSize = size;

        try {
            // Load a system font for symbol detection
            loadFallbackFont(size);
            
            initialized = true;
            VeilOrigins.LOGGER.info("UnicodeFontHandler initialized successfully with font size {}", fontSize);
            return true;

        } catch (Exception e) {
            VeilOrigins.LOGGER.error("Failed to initialize UnicodeFontHandler", e);
            return false;
        }
    }

    /**
     * Loads a fallback font that supports Unicode.
     * This is used for symbol detection, not rendering.
     */
    private void loadFallbackFont(int size) {
        // List of fonts with good Unicode support
        String[] unicodeFonts = {
                "Segoe UI Symbol",
                "Segoe UI Emoji",
                "Arial Unicode MS",
                "Noto Sans",
                "DejaVu Sans",
                java.awt.Font.SANS_SERIF
        };

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] availableFonts = ge.getAvailableFontFamilyNames();
        java.util.Set<String> fontSet = new java.util.HashSet<>(java.util.Arrays.asList(availableFonts));

        for (String fontName : unicodeFonts) {
            if (fontSet.contains(fontName) || fontName.equals(java.awt.Font.SANS_SERIF)) {
                this.awtFont = new java.awt.Font(fontName, java.awt.Font.PLAIN, size);
                VeilOrigins.LOGGER.info("Using system font for Unicode detection: {}", fontName);
                return;
            }
        }

        this.awtFont = new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.PLAIN, size);
    }

    // ==================== PUBLIC RENDERING METHODS ====================

    /**
     * Draws a string using Minecraft's font with Unicode support.
     * 
     * @param graphics The GuiGraphics context
     * @param text     The text to render
     * @param x        X position
     * @param y        Y position
     * @param color    ARGB color (0xAARRGGBB format)
     * @return The width of the rendered text
     */
    public int drawString(GuiGraphics graphics, String text, int x, int y, int color) {
        return drawString(graphics, text, x, y, color, false);
    }

    /**
     * Draws a string with optional shadow using Minecraft's font.
     * 
     * @param graphics The GuiGraphics context
     * @param text     The text to render
     * @param x        X position
     * @param y        Y position
     * @param color    ARGB color
     * @param shadow   Whether to draw a drop shadow
     * @return The width of the rendered text
     */
    public int drawString(GuiGraphics graphics, String text, int x, int y, int color, boolean shadow) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        // Use Minecraft's font which has good Unicode support in 1.21.10
        Font mcFont = Minecraft.getInstance().font;
        graphics.drawString(mcFont, text, x, y, color, shadow);
        return mcFont.width(text);
    }

    /**
     * Draws a string with shadow using Minecraft's font.
     */
    public int drawStringWithShadow(GuiGraphics graphics, String text, int x, int y, int color) {
        return drawString(graphics, text, x, y, color, true);
    }

    /**
     * Calculates the width of a string.
     * 
     * @param text The text to measure
     * @return The width in pixels
     */
    public int getStringWidth(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        return Minecraft.getInstance().font.width(text);
    }

    /**
     * Gets the font height.
     * 
     * @return The font height in pixels
     */
    public int getFontHeight() {
        return fontSize;
    }

    /**
     * Refreshes the texture atlas (no-op in this version).
     */
    public void refreshAtlas() {
        // No custom atlas in this version - using Minecraft's font
    }

    // ==================== STATIC CONVENIENCE METHODS ====================

    /**
     * Static method to draw a string using the singleton instance.
     */
    public static int draw(GuiGraphics graphics, String text, int x, int y, int color) {
        return getInstance().drawString(graphics, text, x, y, color, false);
    }

    /**
     * Static method to draw a string with shadow using the singleton instance.
     */
    public static int drawWithShadow(GuiGraphics graphics, String text, int x, int y, int color) {
        return getInstance().drawString(graphics, text, x, y, color, true);
    }

    /**
     * Static method to get the width of a string.
     */
    public static int width(String text) {
        return getInstance().getStringWidth(text);
    }

    /**
     * Checks if the handler is initialized and ready to use.
     */
    public static boolean isReady() {
        return instance != null && instance.initialized;
    }

    /**
     * Gets a symbol, returning ASCII fallback if Unicode can't be displayed.
     * 
     * @param unicode The Unicode symbol
     * @param ascii   The ASCII fallback
     * @return The symbol to use
     */
    public static String getSymbol(String unicode, String ascii) {
        if (!isReady()) {
            return ascii;
        }

        UnicodeFontHandler handler = getInstance();
        if (handler.awtFont != null) {
            // Check if all characters can be displayed
            for (char c : unicode.toCharArray()) {
                if (!handler.awtFont.canDisplay(c)) {
                    return ascii;
                }
            }
        }

        return unicode;
    }
    
    /**
     * Gets a symbol with automatic ASCII fallback lookup.
     * 
     * @param unicode The Unicode symbol constant
     * @return The symbol or its ASCII fallback
     */
    public static String getSymbol(String unicode) {
        String fallback = ASCII_FALLBACKS.getOrDefault(unicode, "?");
        return getSymbol(unicode, fallback);
    }

    /**
     * Cleans up resources when the mod is unloaded.
     */
    public void cleanup() {
        initialized = false;
        VeilOrigins.LOGGER.info("UnicodeFontHandler cleaned up");
    }

    /**
     * Static cleanup method.
     */
    public static void cleanupInstance() {
        if (instance != null) {
            instance.cleanup();
            instance = null;
        }
    }
}
