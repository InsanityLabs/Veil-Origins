package com.veilorigins.client.gui;

import com.veilorigins.VeilOrigins;
import com.veilorigins.config.VeilOriginsConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

/**
 * HUD Configuration Screen - Allows players to:
 * - Toggle individual HUD elements on/off
 * - Adjust HUD opacity
 * 
 * Accessible via the H key or from Minecraft Options
 */
public class HudConfigScreen extends Screen {
    private static final int BUTTON_HEIGHT = 20;
    private static final int PADDING = 5;

    private final Screen parent;

    // Toggle buttons (cycle between enabled/disabled)
    private Button showOriginHudButton;
    private Button showResourceBarButton;
    private Button showXpBarButton;
    private Button showAbilityIndicatorsButton;
    private Button showPassiveIndicatorsButton;
    private Button showCooldownOverlaysButton;
    private Button showKeybindHintsButton;
    private Button useUnicodeSymbolsButton;

    // Opacity value (0-100)
    private int currentOpacity;

    public HudConfigScreen(Screen parent) {
        super(Component.translatable("veil_origins.screen.hud_config.title"));
        this.parent = parent;
        this.currentOpacity = VeilOriginsConfig.hudOpacity;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int startY = 50;
        int buttonWidth = 200;
        int y = startY;

        // ========== HUD ELEMENT TOGGLES ==========

        // Show Origin HUD (info panel)
        showOriginHudButton = createToggleButton(
                centerX - buttonWidth - 5, y, buttonWidth,
                "veil_origins.config.showOriginHud",
                VeilOriginsConfig.showOriginHud,
                () -> {
                    VeilOriginsConfig.showOriginHud = !VeilOriginsConfig.showOriginHud;
                    VeilOriginsConfig.SHOW_ORIGIN_HUD.set(VeilOriginsConfig.showOriginHud);
                    return VeilOriginsConfig.showOriginHud;
                });
        this.addRenderableWidget(showOriginHudButton);

        // Show Resource Bar
        showResourceBarButton = createToggleButton(
                centerX + 5, y, buttonWidth,
                "veil_origins.config.showResourceBar",
                VeilOriginsConfig.showResourceBar,
                () -> {
                    VeilOriginsConfig.showResourceBar = !VeilOriginsConfig.showResourceBar;
                    VeilOriginsConfig.SHOW_RESOURCE_BAR.set(VeilOriginsConfig.showResourceBar);
                    return VeilOriginsConfig.showResourceBar;
                });
        this.addRenderableWidget(showResourceBarButton);
        y += BUTTON_HEIGHT + PADDING;

        // Show XP Bar
        showXpBarButton = createToggleButton(
                centerX - buttonWidth - 5, y, buttonWidth,
                "veil_origins.config.showXpBar",
                VeilOriginsConfig.showXpBar,
                () -> {
                    VeilOriginsConfig.showXpBar = !VeilOriginsConfig.showXpBar;
                    VeilOriginsConfig.SHOW_XP_BAR.set(VeilOriginsConfig.showXpBar);
                    return VeilOriginsConfig.showXpBar;
                });
        this.addRenderableWidget(showXpBarButton);

        // Show Ability Indicators
        showAbilityIndicatorsButton = createToggleButton(
                centerX + 5, y, buttonWidth,
                "veil_origins.config.showAbilityIndicators",
                VeilOriginsConfig.showAbilityIndicators,
                () -> {
                    VeilOriginsConfig.showAbilityIndicators = !VeilOriginsConfig.showAbilityIndicators;
                    VeilOriginsConfig.SHOW_ABILITY_INDICATORS.set(VeilOriginsConfig.showAbilityIndicators);
                    return VeilOriginsConfig.showAbilityIndicators;
                });
        this.addRenderableWidget(showAbilityIndicatorsButton);
        y += BUTTON_HEIGHT + PADDING;

        // Show Passive Indicators
        showPassiveIndicatorsButton = createToggleButton(
                centerX - buttonWidth - 5, y, buttonWidth,
                "veil_origins.config.showPassiveIndicators",
                VeilOriginsConfig.showPassiveIndicators,
                () -> {
                    VeilOriginsConfig.showPassiveIndicators = !VeilOriginsConfig.showPassiveIndicators;
                    VeilOriginsConfig.SHOW_PASSIVE_INDICATORS.set(VeilOriginsConfig.showPassiveIndicators);
                    return VeilOriginsConfig.showPassiveIndicators;
                });
        this.addRenderableWidget(showPassiveIndicatorsButton);

        // Show Cooldown Overlays
        showCooldownOverlaysButton = createToggleButton(
                centerX + 5, y, buttonWidth,
                "veil_origins.config.showCooldownOverlays",
                VeilOriginsConfig.showCooldownOverlays,
                () -> {
                    VeilOriginsConfig.showCooldownOverlays = !VeilOriginsConfig.showCooldownOverlays;
                    VeilOriginsConfig.SHOW_COOLDOWN_OVERLAYS.set(VeilOriginsConfig.showCooldownOverlays);
                    return VeilOriginsConfig.showCooldownOverlays;
                });
        this.addRenderableWidget(showCooldownOverlaysButton);
        y += BUTTON_HEIGHT + PADDING;

        // Show Keybind Hints
        showKeybindHintsButton = createToggleButton(
                centerX - buttonWidth - 5, y, buttonWidth,
                "veil_origins.config.showKeybindHints",
                VeilOriginsConfig.showKeybindHints,
                () -> {
                    VeilOriginsConfig.showKeybindHints = !VeilOriginsConfig.showKeybindHints;
                    VeilOriginsConfig.SHOW_KEYBIND_HINTS.set(VeilOriginsConfig.showKeybindHints);
                    return VeilOriginsConfig.showKeybindHints;
                });
        this.addRenderableWidget(showKeybindHintsButton);

        // Use Unicode Symbols
        useUnicodeSymbolsButton = createToggleButton(
                centerX + 5, y, buttonWidth,
                "veil_origins.config.useUnicodeSymbols",
                VeilOriginsConfig.useUnicodeSymbols,
                () -> {
                    VeilOriginsConfig.useUnicodeSymbols = !VeilOriginsConfig.useUnicodeSymbols;
                    VeilOriginsConfig.USE_UNICODE_SYMBOLS.set(VeilOriginsConfig.useUnicodeSymbols);
                    return VeilOriginsConfig.useUnicodeSymbols;
                });
        this.addRenderableWidget(useUnicodeSymbolsButton);
        y += BUTTON_HEIGHT + PADDING + 15;

        // ========== OPACITY CONTROLS ==========

        // Opacity decrease button
        this.addRenderableWidget(Button.builder(Component.literal("-10"), button -> {
            if (currentOpacity > 0) {
                currentOpacity = Math.max(0, currentOpacity - 10);
                VeilOriginsConfig.HUD_OPACITY.set(currentOpacity);
                VeilOriginsConfig.hudOpacity = currentOpacity;
            }
        }).bounds(centerX - 100, y, 40, BUTTON_HEIGHT).build());

        // Opacity increase button
        this.addRenderableWidget(Button.builder(Component.literal("+10"), button -> {
            if (currentOpacity < 100) {
                currentOpacity = Math.min(100, currentOpacity + 10);
                VeilOriginsConfig.HUD_OPACITY.set(currentOpacity);
                VeilOriginsConfig.hudOpacity = currentOpacity;
            }
        }).bounds(centerX + 60, y, 40, BUTTON_HEIGHT).build());

        y += BUTTON_HEIGHT + PADDING + 20;

        // ========== QUICK PRESET BUTTONS ==========

        // Enable All button
        this.addRenderableWidget(Button.builder(
                Component.translatable("veil_origins.screen.hud_config.enable_all"),
                button -> setAllToggles(true)).bounds(centerX - 155, y, 150, BUTTON_HEIGHT).build());

        // Disable All button
        this.addRenderableWidget(Button.builder(
                Component.translatable("veil_origins.screen.hud_config.disable_all"),
                button -> setAllToggles(false)).bounds(centerX + 5, y, 150, BUTTON_HEIGHT).build());

        // ========== DONE BUTTON ==========
        this.addRenderableWidget(Button.builder(
                Component.translatable("gui.done"),
                button -> onClose()).bounds(centerX - 100, this.height - 28, 200, BUTTON_HEIGHT).build());
    }

    /**
     * Creates a toggle button that shows ON/OFF state.
     */
    private Button createToggleButton(int x, int y, int width, String translationKey, boolean initialState,
            ToggleAction action) {
        return Button.builder(getToggleText(translationKey, initialState), button -> {
            boolean newState = action.toggle();
            button.setMessage(getToggleText(translationKey, newState));
        }).bounds(x, y, width, BUTTON_HEIGHT).build();
    }

    /**
     * Gets the display text for a toggle button.
     */
    private Component getToggleText(String translationKey, boolean enabled) {
        Component stateText = enabled
                ? Component.literal("ON").withStyle(ChatFormatting.GREEN)
                : Component.literal("OFF").withStyle(ChatFormatting.RED);
        return Component.translatable(translationKey).append(": ").append(stateText);
    }

    /**
     * Sets all toggles to the specified state.
     */
    private void setAllToggles(boolean enabled) {
        // Update config values
        VeilOriginsConfig.SHOW_ORIGIN_HUD.set(enabled);
        VeilOriginsConfig.SHOW_RESOURCE_BAR.set(enabled);
        VeilOriginsConfig.SHOW_XP_BAR.set(enabled);
        VeilOriginsConfig.SHOW_ABILITY_INDICATORS.set(enabled);
        VeilOriginsConfig.SHOW_PASSIVE_INDICATORS.set(enabled);
        VeilOriginsConfig.SHOW_COOLDOWN_OVERLAYS.set(enabled);
        VeilOriginsConfig.SHOW_KEYBIND_HINTS.set(enabled);

        // Update cached values
        VeilOriginsConfig.showOriginHud = enabled;
        VeilOriginsConfig.showResourceBar = enabled;
        VeilOriginsConfig.showXpBar = enabled;
        VeilOriginsConfig.showAbilityIndicators = enabled;
        VeilOriginsConfig.showPassiveIndicators = enabled;
        VeilOriginsConfig.showCooldownOverlays = enabled;
        VeilOriginsConfig.showKeybindHints = enabled;

        // Update button messages
        showOriginHudButton.setMessage(getToggleText("veil_origins.config.showOriginHud", enabled));
        showResourceBarButton.setMessage(getToggleText("veil_origins.config.showResourceBar", enabled));
        showXpBarButton.setMessage(getToggleText("veil_origins.config.showXpBar", enabled));
        showAbilityIndicatorsButton.setMessage(getToggleText("veil_origins.config.showAbilityIndicators", enabled));
        showPassiveIndicatorsButton.setMessage(getToggleText("veil_origins.config.showPassiveIndicators", enabled));
        showCooldownOverlaysButton.setMessage(getToggleText("veil_origins.config.showCooldownOverlays", enabled));
        showKeybindHintsButton.setMessage(getToggleText("veil_origins.config.showKeybindHints", enabled));
    }

    /**
     * Renders a simple dark semi-transparent overlay as the background.
     * This avoids the blur effect that was rendering on top of menu elements.
     */
    private void renderCustomBackground(GuiGraphics guiGraphics) {
        // Fill the entire screen with a semi-transparent dark overlay (50% opacity)
        guiGraphics.fill(0, 0, this.width, this.height, 0x80000000); // 50% opacity black - less dark
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Render background FIRST
        this.renderCustomBackground(graphics);

        // Call super.render() to render all widgets (buttons, etc.) on top of
        // background
        super.render(graphics, mouseX, mouseY, partialTick);

        int centerX = this.width / 2;

        // Title (rendered after widgets so it's on top)
        graphics.drawCenteredString(this.font, this.title, centerX, 20, 0xFFFFFF);

        // Subtitle
        graphics.drawCenteredString(this.font,
                Component.translatable("veil_origins.screen.hud_config.subtitle"),
                centerX, 35, 0xAAAAAA);

        // Opacity section label
        int opacityY = 50 + (4 * (BUTTON_HEIGHT + PADDING)) + 5;
        graphics.drawCenteredString(this.font,
                Component.translatable("veil_origins.config.hudOpacity"),
                centerX, opacityY, 0xFFAA00);

        // Opacity value display
        int opacityValueY = opacityY + 15;
        graphics.drawCenteredString(this.font, currentOpacity + "%", centerX, opacityValueY + 5, 0xFFFFFF);

        // Opacity bar visualization
        int barWidth = 100;
        int barHeight = 6;
        int barX = centerX - barWidth / 2;
        int barY = opacityValueY + 22;

        // Bar background
        graphics.fill(barX - 1, barY - 1, barX + barWidth + 1, barY + barHeight + 1, 0xFF333333);
        graphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF555555);

        // Bar filled portion
        int filledWidth = (int) (barWidth * (currentOpacity / 100.0f));
        if (filledWidth > 0) {
            graphics.fill(barX, barY, barX + filledWidth, barY + barHeight, 0xFF55FF55);
        }

        // Instructions at bottom
        graphics.drawCenteredString(this.font,
                Component.translatable("veil_origins.screen.hud_config.hint"),
                centerX, this.height - 45, 0x666666);
    }

    @Override
    public void onClose() {
        // Force config spec to save by re-setting current values
        // NeoForge's CommentedFileConfig auto-saves when values are set
        VeilOriginsConfig.SHOW_ORIGIN_HUD.set(VeilOriginsConfig.showOriginHud);
        VeilOriginsConfig.SHOW_RESOURCE_BAR.set(VeilOriginsConfig.showResourceBar);
        VeilOriginsConfig.SHOW_XP_BAR.set(VeilOriginsConfig.showXpBar);
        VeilOriginsConfig.SHOW_ABILITY_INDICATORS.set(VeilOriginsConfig.showAbilityIndicators);
        VeilOriginsConfig.SHOW_PASSIVE_INDICATORS.set(VeilOriginsConfig.showPassiveIndicators);
        VeilOriginsConfig.SHOW_COOLDOWN_OVERLAYS.set(VeilOriginsConfig.showCooldownOverlays);
        VeilOriginsConfig.SHOW_KEYBIND_HINTS.set(VeilOriginsConfig.showKeybindHints);
        VeilOriginsConfig.USE_UNICODE_SYMBOLS.set(VeilOriginsConfig.useUnicodeSymbols);
        VeilOriginsConfig.HUD_OPACITY.set(VeilOriginsConfig.hudOpacity);

        VeilOrigins.LOGGER.info("Veil Origins: HUD configuration saved");
        this.minecraft.setScreen(parent);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    /**
     * Functional interface for toggle actions.
     */
    @FunctionalInterface
    private interface ToggleAction {
        boolean toggle();
    }
}
