package com.veilorigins.api;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.veilorigins.VeilOrigins;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * UnicodeFontHandler - A comprehensive Unicode font rendering system for
 * Minecraft 1.21.1-1.21.3.
 * 
 * This handler provides support for rendering Unicode characters that
 * Minecraft's default font
 * doesn't support. It uses Java AWT to load TrueType fonts with full Unicode
 * support and
 * generates bitmap texture atlases for efficient rendering.
 * 
 * Features:
 * - Loads TrueType fonts with full Unicode support
 * - Generates texture atlases for efficient GPU rendering
 * - Caches glyphs for performance
 * - Seamlessly integrates with NeoForge's GuiGraphics system
 * - Graceful fallback to Minecraft's default font
 * - Support for custom font sizes and styles
 * 
 * Usage:
 * 
 * <pre>
 * // Initialize (call once during mod initialization)
 * UnicodeFontHandler.initialize();
 * 
 * // Render text
 * UnicodeFontHandler.drawString(graphics, "Hello ✓ World ⚡", x, y, color);
 * 
 * // Or use the instance for more control
 * UnicodeFontHandler handler = UnicodeFontHandler.getInstance();
 * handler.drawStringWithShadow(graphics, "Unicode: ♦♣♠♥", x, y, color);
 * </pre>
 * 
 * @author Insanity Studios
 * @version 1.0.0
 * @since Minecraft 1.21.1
 */
@OnlyIn(Dist.CLIENT)
public class UnicodeFontHandler {

    // Singleton instance
    private static UnicodeFontHandler instance;

    // Texture atlas settings
    private static final int ATLAS_SIZE = 512; // Size of texture atlas (512x512)
    private static final int DEFAULT_FONT_SIZE = 16;
    private static final int GLYPH_PADDING = 2; // Padding between glyphs

    // Character ranges to pre-cache
    // Basic Latin, Latin-1 Supplement, and common symbols
    private static final int[][] CHARACTER_RANGES = {
            { 0x0020, 0x007F }, // Basic Latin (ASCII)
            { 0x00A0, 0x00FF }, // Latin-1 Supplement
            { 0x2000, 0x206F }, // General Punctuation
            { 0x2100, 0x214F }, // Letterlike Symbols
            { 0x2190, 0x21FF }, // Arrows
            { 0x2200, 0x22FF }, // Mathematical Operators
            { 0x2300, 0x23FF }, // Miscellaneous Technical
            { 0x2500, 0x257F }, // Box Drawing
            { 0x2580, 0x259F }, // Block Elements
            { 0x25A0, 0x25FF }, // Geometric Shapes
            { 0x2600, 0x26FF }, // Miscellaneous Symbols (includes ⚡, ♦, etc.)
            { 0x2700, 0x27BF }, // Dingbats (includes ✓, ✗, etc.)
    };

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

    // Font and rendering
    private java.awt.Font awtFont;
    private int fontSize;
    private boolean initialized = false;
    private boolean useCustomFont = true;

    // Glyph cache
    private final Map<Character, GlyphInfo> glyphCache = new HashMap<>();

    // Texture atlas
    private DynamicTexture atlasTexture;
    private ResourceLocation atlasLocation;
    private NativeImage atlasImage;
    private int atlasCurrentX = GLYPH_PADDING;
    private int atlasCurrentY = GLYPH_PADDING;
    private int atlasRowHeight = 0;

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
            // Try to load custom font
            if (fontPath != null) {
                try (InputStream fontStream = getClass().getResourceAsStream(fontPath)) {
                    if (fontStream != null) {
                        java.awt.Font baseFont = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, fontStream);
                        this.awtFont = baseFont.deriveFont(java.awt.Font.PLAIN, size);
                        VeilOrigins.LOGGER.info("Loaded custom Unicode font from: {}", fontPath);
                    } else {
                        loadFallbackFont(size);
                    }
                }
            } else {
                loadFallbackFont(size);
            }

            // Create texture atlas
            createTextureAtlas();

            // Pre-cache common characters
            precacheCharacterRanges();

            // Upload atlas to GPU
            uploadAtlas();

            initialized = true;
            VeilOrigins.LOGGER.info("UnicodeFontHandler initialized successfully with font size {}", fontSize);
            return true;

        } catch (Exception e) {
            VeilOrigins.LOGGER.error("Failed to initialize UnicodeFontHandler", e);
            useCustomFont = false;
            return false;
        }
    }

    /**
     * Loads a fallback font that supports Unicode.
     * Tries several system fonts known to have good Unicode coverage.
     */
    private void loadFallbackFont(int size) {
        // List of fonts with good Unicode support, in order of preference
        String[] unicodeFonts = {
                "Segoe UI Symbol", // Windows - excellent Unicode coverage
                "Segoe UI Emoji", // Windows - emoji and symbols
                "Arial Unicode MS", // Windows - comprehensive Unicode
                "Noto Sans", // Cross-platform - Google's Unicode font
                "DejaVu Sans", // Cross-platform - good coverage
                "Symbola", // Cross-platform - symbols
                "Apple Symbols", // macOS
                "Lucida Sans Unicode", // Cross-platform
                "FreeSans", // Linux
                "Liberation Sans", // Linux
                java.awt.Font.SANS_SERIF // Ultimate fallback
        };

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] availableFonts = ge.getAvailableFontFamilyNames();
        java.util.Set<String> fontSet = new java.util.HashSet<>(java.util.Arrays.asList(availableFonts));

        for (String fontName : unicodeFonts) {
            if (fontSet.contains(fontName) || fontName.equals(java.awt.Font.SANS_SERIF)) {
                this.awtFont = new java.awt.Font(fontName, java.awt.Font.PLAIN, size);
                // Test if the font can display our target characters
                if (canDisplayUnicodeSymbols(this.awtFont)) {
                    VeilOrigins.LOGGER.info("Using system font for Unicode: {}", fontName);
                    return;
                }
            }
        }

        // Ultimate fallback
        this.awtFont = new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.PLAIN, size);
        VeilOrigins.LOGGER.warn("Using generic Sans-Serif font - some Unicode symbols may not render correctly");
    }

    /**
     * Tests if a font can display the common Unicode symbols used in the mod.
     */
    private boolean canDisplayUnicodeSymbols(java.awt.Font font) {
        String testString = SYMBOL_CHECK + SYMBOL_CROSS + SYMBOL_LIGHTNING + SYMBOL_DIAMOND;
        for (char c : testString.toCharArray()) {
            if (!font.canDisplay(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates the texture atlas for storing glyph bitmaps.
     */
    private void createTextureAtlas() {
        atlasImage = new NativeImage(NativeImage.Format.RGBA, ATLAS_SIZE, ATLAS_SIZE, true);
        // Clear to transparent
        for (int x = 0; x < ATLAS_SIZE; x++) {
            for (int y = 0; y < ATLAS_SIZE; y++) {
                atlasImage.setPixelRGBA(x, y, 0);
            }
        }
    }

    /**
     * Pre-caches characters from the defined character ranges.
     */
    private void precacheCharacterRanges() {
        for (int[] range : CHARACTER_RANGES) {
            for (int codepoint = range[0]; codepoint <= range[1]; codepoint++) {
                char c = (char) codepoint;
                if (awtFont.canDisplay(c)) {
                    cacheGlyph(c);
                }
            }
        }
        VeilOrigins.LOGGER.debug("Pre-cached {} glyphs", glyphCache.size());
    }

    /**
     * Caches a single glyph, rendering it to the texture atlas.
     * 
     * @param c The character to cache
     * @return The GlyphInfo for the cached character
     */
    private GlyphInfo cacheGlyph(char c) {
        if (glyphCache.containsKey(c)) {
            return glyphCache.get(c);
        }

        if (!awtFont.canDisplay(c)) {
            GlyphInfo invalid = GlyphInfo.invalid(c);
            glyphCache.put(c, invalid);
            return invalid;
        }

        try {
            // Create a temporary image to measure and render the glyph
            BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics2D tempG2d = tempImage.createGraphics();
            tempG2d.setFont(awtFont);
            FontRenderContext frc = tempG2d.getFontRenderContext();

            // Get glyph metrics
            GlyphVector gv = awtFont.createGlyphVector(frc, String.valueOf(c));
            Rectangle2D bounds = gv.getVisualBounds();

            int glyphWidth = (int) Math.ceil(bounds.getWidth()) + GLYPH_PADDING * 2;
            int glyphHeight = (int) Math.ceil(bounds.getHeight()) + GLYPH_PADDING * 2;
            int advance = (int) Math.ceil(awtFont.getStringBounds(String.valueOf(c), frc).getWidth());

            tempG2d.dispose();

            // Handle zero-width characters
            if (glyphWidth <= GLYPH_PADDING * 2 || glyphHeight <= GLYPH_PADDING * 2) {
                GlyphInfo spaceGlyph = new GlyphInfo(c, 0, 0, 0, 0, 0, 0, 0, 0, advance, true);
                glyphCache.put(c, spaceGlyph);
                return spaceGlyph;
            }

            // Check if we need to move to the next row
            if (atlasCurrentX + glyphWidth > ATLAS_SIZE - GLYPH_PADDING) {
                atlasCurrentX = GLYPH_PADDING;
                atlasCurrentY += atlasRowHeight + GLYPH_PADDING;
                atlasRowHeight = 0;
            }

            // Check if atlas is full
            if (atlasCurrentY + glyphHeight > ATLAS_SIZE - GLYPH_PADDING) {
                VeilOrigins.LOGGER.warn("Texture atlas full, cannot cache glyph for: {}", c);
                GlyphInfo invalid = GlyphInfo.invalid(c);
                glyphCache.put(c, invalid);
                return invalid;
            }

            // Render the glyph to a BufferedImage
            BufferedImage glyphImage = new BufferedImage(glyphWidth, glyphHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = glyphImage.createGraphics();

            // Enable anti-aliasing for smooth text
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

            // Draw the character in white (we'll color it during rendering)
            g2d.setFont(awtFont);
            g2d.setColor(Color.WHITE);

            // Calculate baseline position
            FontMetrics fm = g2d.getFontMetrics();
            int xPos = (int) (-bounds.getX()) + GLYPH_PADDING;
            int yPos = fm.getAscent() + GLYPH_PADDING - (int) bounds.getY() - fm.getAscent();

            g2d.drawString(String.valueOf(c), xPos, yPos + fm.getAscent());
            g2d.dispose();

            // Copy to atlas
            for (int px = 0; px < glyphWidth; px++) {
                for (int py = 0; py < glyphHeight; py++) {
                    int rgb = glyphImage.getRGB(px, py);
                    int atlasX = atlasCurrentX + px;
                    int atlasY = atlasCurrentY + py;
                    if (atlasX < ATLAS_SIZE && atlasY < ATLAS_SIZE) {
                        // Convert ARGB to ABGR for NativeImage
                        int a = (rgb >> 24) & 0xFF;
                        int r = (rgb >> 16) & 0xFF;
                        int g = (rgb >> 8) & 0xFF;
                        int b = rgb & 0xFF;
                        int abgr = (a << 24) | (b << 16) | (g << 8) | r;
                        atlasImage.setPixelRGBA(atlasX, atlasY, abgr);
                    }
                }
            }

            // Calculate texture coordinates
            float u0 = (float) atlasCurrentX / ATLAS_SIZE;
            float v0 = (float) atlasCurrentY / ATLAS_SIZE;
            float u1 = (float) (atlasCurrentX + glyphWidth) / ATLAS_SIZE;
            float v1 = (float) (atlasCurrentY + glyphHeight) / ATLAS_SIZE;

            // Create glyph info
            GlyphInfo info = new GlyphInfo(
                    c, glyphWidth, glyphHeight,
                    u0, v0, u1, v1,
                    (int) bounds.getX(), (int) bounds.getY(),
                    advance, true);

            glyphCache.put(c, info);

            // Update atlas position
            atlasCurrentX += glyphWidth + GLYPH_PADDING;
            atlasRowHeight = Math.max(atlasRowHeight, glyphHeight);

            return info;

        } catch (Exception e) {
            VeilOrigins.LOGGER.error("Failed to cache glyph for character: {}", c, e);
            GlyphInfo invalid = GlyphInfo.invalid(c);
            glyphCache.put(c, invalid);
            return invalid;
        }
    }

    /**
     * Uploads the texture atlas to the GPU.
     */
    private void uploadAtlas() {
        if (atlasTexture != null) {
            atlasTexture.close();
        }

        atlasTexture = new DynamicTexture(atlasImage);
        atlasLocation = ResourceLocation.fromNamespaceAndPath(VeilOrigins.MOD_ID, "unicode_font_atlas");

        Minecraft.getInstance().getTextureManager().register(atlasLocation, atlasTexture);
        VeilOrigins.LOGGER.debug("Uploaded Unicode font atlas to GPU");
    }

    /**
     * Refreshes the texture atlas after adding new glyphs.
     * Should be called after dynamically caching new characters.
     */
    public void refreshAtlas() {
        if (atlasTexture != null && atlasImage != null) {
            atlasTexture.upload();
        }
    }

    // ==================== PUBLIC RENDERING METHODS ====================

    /**
     * Draws a string using the Unicode font.
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
     * Draws a string with optional shadow using the Unicode font.
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

        // Check if we need to use custom rendering
        if (!useCustomFont || !initialized || !needsCustomRendering(text)) {
            // Fall back to Minecraft's default font
            return drawWithMinecraftFont(graphics, text, x, y, color, shadow);
        }

        // Draw shadow first if requested
        if (shadow) {
            drawStringInternal(graphics, text, x + 1, y + 1, darkenColor(color), false);
        }

        return drawStringInternal(graphics, text, x, y, color, true);
    }

    /**
     * Draws a string with shadow using the Unicode font.
     * Convenience method that always applies shadow.
     */
    public int drawStringWithShadow(GuiGraphics graphics, String text, int x, int y, int color) {
        return drawString(graphics, text, x, y, color, true);
    }

    /**
     * Internal string drawing method.
     */
    private int drawStringInternal(GuiGraphics graphics, String text, int x, int y, int color, boolean updateAtlas) {
        if (atlasLocation == null) {
            return drawWithMinecraftFont(graphics, text, x, y, color, false);
        }

        int currentX = x;
        boolean atlasUpdated = false;

        // Extract color components
        float alpha = ((color >> 24) & 0xFF) / 255.0f;
        float red = ((color >> 16) & 0xFF) / 255.0f;
        float green = ((color >> 8) & 0xFF) / 255.0f;
        float blue = (color & 0xFF) / 255.0f;

        // Handle fully transparent color
        if (alpha == 0) {
            alpha = 1.0f;
        }

        for (char c : text.toCharArray()) {
            GlyphInfo glyph = glyphCache.get(c);

            // Cache glyph if not already cached
            if (glyph == null) {
                glyph = cacheGlyph(c);
                atlasUpdated = true;
            }

            if (!glyph.isValid) {
                // Fall back to Minecraft font for this character
                Font mcFont = Minecraft.getInstance().font;
                graphics.drawString(mcFont, String.valueOf(c), currentX, y, color, false);
                currentX += mcFont.width(String.valueOf(c));
                continue;
            }

            // Skip zero-width characters (spaces handled by advance)
            if (glyph.width > 0 && glyph.height > 0) {
                // Render the glyph
                RenderSystem.setShaderTexture(0, atlasLocation);
                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(red, green, blue, alpha);

                graphics.blit(
                        atlasLocation,
                        currentX, y,
                        glyph.width, glyph.height,
                        glyph.u0 * ATLAS_SIZE, glyph.v0 * ATLAS_SIZE,
                        glyph.width, glyph.height,
                        ATLAS_SIZE, ATLAS_SIZE);

                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            }

            currentX += glyph.advance;
        }

        // Refresh atlas if we added new glyphs
        if (atlasUpdated && updateAtlas) {
            refreshAtlas();
        }

        return currentX - x;
    }

    /**
     * Falls back to using Minecraft's default font.
     */
    private int drawWithMinecraftFont(GuiGraphics graphics, String text, int x, int y, int color, boolean shadow) {
        Font mcFont = Minecraft.getInstance().font;
        graphics.drawString(mcFont, text, x, y, color, shadow);
        return mcFont.width(text);
    }

    /**
     * Checks if the text contains characters that need custom rendering.
     */
    private boolean needsCustomRendering(String text) {
        for (char c : text.toCharArray()) {
            // Check if character is outside basic ASCII and Latin-1
            if (c > 0x00FF) {
                return true;
            }
        }
        return false;
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

        if (!useCustomFont || !initialized) {
            return Minecraft.getInstance().font.width(text);
        }

        int width = 0;
        for (char c : text.toCharArray()) {
            GlyphInfo glyph = glyphCache.get(c);
            if (glyph == null) {
                glyph = cacheGlyph(c);
            }

            if (glyph.isValid) {
                width += glyph.advance;
            } else {
                width += Minecraft.getInstance().font.width(String.valueOf(c));
            }
        }

        return width;
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
     * Darkens a color for shadow rendering.
     */
    private int darkenColor(int color) {
        int a = (color >> 24) & 0xFF;
        int r = Math.max(0, ((color >> 16) & 0xFF) / 4);
        int g = Math.max(0, ((color >> 8) & 0xFF) / 4);
        int b = Math.max(0, (color & 0xFF) / 4);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    // ==================== STATIC CONVENIENCE METHODS ====================

    /**
     * Static method to draw a string using the singleton instance.
     * Initializes the handler if necessary.
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
     * Gets a symbol, checking if it can be rendered by the handler.
     * Returns the ASCII fallback if the Unicode symbol cannot be displayed.
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
        if (!handler.useCustomFont) {
            return ascii;
        }

        // Check if all characters in the unicode string can be displayed
        for (char c : unicode.toCharArray()) {
            if (!handler.awtFont.canDisplay(c)) {
                return ascii;
            }
        }

        return unicode;
    }

    /**
     * Cleans up resources when the mod is unloaded.
     */
    public void cleanup() {
        if (atlasTexture != null) {
            atlasTexture.close();
            atlasTexture = null;
        }
        if (atlasImage != null) {
            atlasImage.close();
            atlasImage = null;
        }
        glyphCache.clear();
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
