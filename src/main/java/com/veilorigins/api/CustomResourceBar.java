package com.veilorigins.api;

import net.minecraft.resources.Identifier;

/**
 * Defines custom resource bar rendering configuration for origins.
 * Supports both sprite-based rendering (like vanilla hearts) and solid color bars.
 */
public class CustomResourceBar {
    
    /**
     * The rendering style for the resource bar.
     */
    public enum BarStyle {
        /** Renders as individual icons (like hearts/hunger) */
        ICONS,
        /** Renders as a solid horizontal bar */
        SOLID_BAR,
        /** Renders as a segmented bar with divisions */
        SEGMENTED_BAR,
        /** Uses vanilla hunger bar position and replaces it */
        REPLACE_HUNGER,
        /** Uses vanilla health bar position and replaces it */
        REPLACE_HEALTH
    }
    
    /**
     * Position anchor for the bar on screen.
     */
    public enum BarPosition {
        /** Above the hotbar, left side (like health) */
        HOTBAR_LEFT,
        /** Above the hotbar, right side (like hunger) */
        HOTBAR_RIGHT,
        /** Top left corner */
        TOP_LEFT,
        /** Top right corner */
        TOP_RIGHT,
        /** Bottom left corner */
        BOTTOM_LEFT,
        /** Bottom right corner */
        BOTTOM_RIGHT,
        /** Custom position with offset */
        CUSTOM
    }
    
    private final String name;
    private final BarStyle style;
    private final BarPosition position;
    
    // Colors (ARGB format)
    private int primaryColor = 0xFFFFFFFF;
    private int secondaryColor = 0xFF888888;  // For gradients or low state
    private int criticalColor = 0xFFFF0000;   // When resource is critically low
    private int backgroundColor = 0x88000000;
    private int borderColor = 0xFF222222;
    
    // Sprite textures (for ICONS style)
    private Identifier spriteEmpty;
    private Identifier spriteFull;
    private Identifier spriteHalf;
    private Identifier spriteBackground;  // Optional background sprite
    private Identifier spriteOverlay;     // Optional overlay (like saturation)
    
    // Icon configuration
    private int iconSize = 9;
    private int iconSpacing = 8;
    private int iconCount = 10;  // Number of icons to display
    
    // Bar configuration
    private int barWidth = 81;
    private int barHeight = 9;
    
    // Position offsets (for CUSTOM position)
    private int offsetX = 0;
    private int offsetY = 0;
    
    // Thresholds
    private float criticalThreshold = 0.2f;  // Below 20% shows critical state
    private float lowThreshold = 0.5f;       // Below 50% shows secondary color
    
    // Animation
    private boolean pulseWhenLow = true;
    private boolean bounceWhenCritical = true;
    private boolean showNumericValue = true;
    
    /**
     * Creates a custom resource bar with the specified style.
     */
    public CustomResourceBar(String name, BarStyle style, BarPosition position) {
        this.name = name;
        this.style = style;
        this.position = position;
    }
    
    // Builder pattern methods for easy configuration
    
    public CustomResourceBar colors(int primary, int secondary, int critical) {
        this.primaryColor = primary;
        this.secondaryColor = secondary;
        this.criticalColor = critical;
        return this;
    }
    
    public CustomResourceBar backgroundColor(int color) {
        this.backgroundColor = color;
        return this;
    }
    
    public CustomResourceBar borderColor(int color) {
        this.borderColor = color;
        return this;
    }
    
    public CustomResourceBar sprites(Identifier empty, Identifier full, Identifier half) {
        this.spriteEmpty = empty;
        this.spriteFull = full;
        this.spriteHalf = half;
        return this;
    }
    
    public CustomResourceBar backgroundSprite(Identifier sprite) {
        this.spriteBackground = sprite;
        return this;
    }
    
    public CustomResourceBar overlaySprite(Identifier sprite) {
        this.spriteOverlay = sprite;
        return this;
    }
    
    public CustomResourceBar iconConfig(int size, int spacing, int count) {
        this.iconSize = size;
        this.iconSpacing = spacing;
        this.iconCount = count;
        return this;
    }
    
    public CustomResourceBar barSize(int width, int height) {
        this.barWidth = width;
        this.barHeight = height;
        return this;
    }
    
    public CustomResourceBar offset(int x, int y) {
        this.offsetX = x;
        this.offsetY = y;
        return this;
    }
    
    public CustomResourceBar thresholds(float critical, float low) {
        this.criticalThreshold = critical;
        this.lowThreshold = low;
        return this;
    }
    
    public CustomResourceBar animations(boolean pulseWhenLow, boolean bounceWhenCritical) {
        this.pulseWhenLow = pulseWhenLow;
        this.bounceWhenCritical = bounceWhenCritical;
        return this;
    }
    
    public CustomResourceBar showNumericValue(boolean show) {
        this.showNumericValue = show;
        return this;
    }
    
    // Getters
    
    public String getName() { return name; }
    public BarStyle getStyle() { return style; }
    public BarPosition getPosition() { return position; }
    
    public int getPrimaryColor() { return primaryColor; }
    public int getSecondaryColor() { return secondaryColor; }
    public int getCriticalColor() { return criticalColor; }
    public int getBackgroundColor() { return backgroundColor; }
    public int getBorderColor() { return borderColor; }
    
    public Identifier getSpriteEmpty() { return spriteEmpty; }
    public Identifier getSpriteFull() { return spriteFull; }
    public Identifier getSpriteHalf() { return spriteHalf; }
    public Identifier getSpriteBackground() { return spriteBackground; }
    public Identifier getSpriteOverlay() { return spriteOverlay; }
    
    public int getIconSize() { return iconSize; }
    public int getIconSpacing() { return iconSpacing; }
    public int getIconCount() { return iconCount; }
    
    public int getBarWidth() { return barWidth; }
    public int getBarHeight() { return barHeight; }
    
    public int getOffsetX() { return offsetX; }
    public int getOffsetY() { return offsetY; }
    
    public float getCriticalThreshold() { return criticalThreshold; }
    public float getLowThreshold() { return lowThreshold; }
    
    public boolean shouldPulseWhenLow() { return pulseWhenLow; }
    public boolean shouldBounceWhenCritical() { return bounceWhenCritical; }
    public boolean shouldShowNumericValue() { return showNumericValue; }
    
    /**
     * Gets the appropriate color based on the current resource percentage.
     */
    public int getColorForPercent(float percent) {
        if (percent <= criticalThreshold) {
            return criticalColor;
        } else if (percent <= lowThreshold) {
            return secondaryColor;
        }
        return primaryColor;
    }
    
    /**
     * Checks if the resource is in critical state.
     */
    public boolean isCritical(float percent) {
        return percent <= criticalThreshold;
    }
    
    /**
     * Checks if the resource is in low state.
     */
    public boolean isLow(float percent) {
        return percent <= lowThreshold;
    }
    
    // Static factory methods for common bar types
    
    /**
     * Creates a blood bar configuration (vampire style).
     */
    public static CustomResourceBar bloodBar(String modId) {
        return new CustomResourceBar("Blood", BarStyle.REPLACE_HUNGER, BarPosition.HOTBAR_RIGHT)
                .colors(0xFF8B0000, 0xFFB22222, 0xFF4A0000)
                .sprites(
                    Identifier.fromNamespaceAndPath(modId, "origins/vampire_blood/hud_bar/blood_empty"),
                    Identifier.fromNamespaceAndPath(modId, "origins/vampire_blood/hud_bar/blood_full"),
                    Identifier.fromNamespaceAndPath(modId, "origins/vampire_blood/hud_bar/blood_half")
                )
                .thresholds(0.2f, 0.5f)
                .animations(true, true);
    }
    
    /**
     * Creates a mana bar configuration.
     */
    public static CustomResourceBar manaBar() {
        return new CustomResourceBar("Mana", BarStyle.SOLID_BAR, BarPosition.HOTBAR_LEFT)
                .colors(0xFF3498DB, 0xFF2980B9, 0xFF1A5276)
                .thresholds(0.15f, 0.4f)
                .barSize(81, 5)
                .offset(0, -12);
    }
    
    /**
     * Creates a heat/fire bar configuration.
     */
    public static CustomResourceBar heatBar() {
        return new CustomResourceBar("Heat", BarStyle.SEGMENTED_BAR, BarPosition.HOTBAR_LEFT)
                .colors(0xFFE67E22, 0xFFD35400, 0xFF922B21)
                .thresholds(0.25f, 0.5f)
                .barSize(81, 7)
                .offset(0, -12);
    }
    
    /**
     * Creates a hydration bar configuration.
     */
    public static CustomResourceBar hydrationBar() {
        return new CustomResourceBar("Hydration", BarStyle.SOLID_BAR, BarPosition.HOTBAR_LEFT)
                .colors(0xFF5DADE2, 0xFF3498DB, 0xFF1A5276)
                .thresholds(0.2f, 0.4f)
                .barSize(81, 5)
                .offset(0, -12);
    }
    
    /**
     * Creates a stellar energy bar configuration.
     */
    public static CustomResourceBar stellarBar() {
        return new CustomResourceBar("Stellar Energy", BarStyle.SOLID_BAR, BarPosition.HOTBAR_LEFT)
                .colors(0xFFF1C40F, 0xFFD4AC0D, 0xFF7D6608)
                .thresholds(0.15f, 0.4f)
                .barSize(81, 5)
                .offset(0, -12);
    }
}
