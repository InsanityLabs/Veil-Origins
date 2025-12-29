package com.veilorigins.client.gui;

import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.OriginAbility;
import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.network.ModPackets;
import com.veilorigins.network.packet.ActivateAbilityPacket;
import com.veilorigins.network.packet.SelectOriginPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Radial menu screen for Veil Origins.
 * Operates in two modes:
 * 1. ORIGIN_SELECT - For selecting an origin when player first joins
 * 2. ABILITY_SELECT - For selecting abilities to trigger during gameplay
 */
public class RadialMenuScreen extends Screen {

    public enum MenuMode {
        ORIGIN_SELECT, // For selecting origin on first join
        ABILITY_SELECT // For selecting abilities to trigger
    }

    private final MenuMode mode;
    private int hoveredSegment = -1;
    private int selectedSegment = -1;
    private int expandedSegment = -1;
    private int hoveredSubsection = -1;

    // Menu items based on mode
    private List<RadialMenuItem> menuItems = new ArrayList<>();

    // Radial menu dimensions
    private static final int OUTER_RADIUS = 140;
    private static final int INNER_RADIUS = 65;
    private static final int CENTER_RADIUS = 55;
    private static final int SUBSECTION_OUTER_OFFSET = 45;
    private static final int SUBSECTION_INNER_OFFSET = 5;

    // Colors - Matching the reference design (solid dark grays)
    private static final int COLOR_SEGMENT_DEFAULT = 0xFF3a3a3a; // Dark gray solid fill
    private static final int COLOR_SEGMENT_HOVERED = 0xFF4d4d4d; // Lighter gray on hover
    private static final int COLOR_SEGMENT_SELECTED = 0xFF555555;
    private static final int COLOR_CENTER = 0xFF252525; // Darker center
    private static final int COLOR_CENTER_HOVERED = 0xFF333333;
    private static final int COLOR_DIVIDER = 0xFF555555; // Light gray divider lines
    private static final int COLOR_SUBSECTION = 0xFF3a3a3a; // Same as segments
    private static final int COLOR_SUBSECTION_HOVERED = 0xFF4d4d4d;
    private static final int COLOR_ICON = 0xFF888888; // Gray icons
    private static final int COLOR_ICON_HOVERED = 0xFFCCCCCC; // Brighter icons on hover

    public RadialMenuScreen(MenuMode mode) {
        super(Component.translatable("screen.veil_origins.radial_menu"));
        this.mode = mode;
    }

    @Override
    protected void init() {
        super.init();
        menuItems.clear();

        if (mode == MenuMode.ORIGIN_SELECT) {
            initOriginSelectMenu();
        } else {
            initAbilitySelectMenu();
        }
    }

    private void initOriginSelectMenu() {
        Map<ResourceLocation, Origin> allOrigins = VeilOriginsAPI.getAllOrigins();

        // Convert to list and shuffle for random selection
        List<Origin> originList = new ArrayList<>(allOrigins.values());
        java.util.Collections.shuffle(originList);

        // Only show 8 random origins (more manageable for a wheel)
        int maxOrigins = Math.min(8, originList.size());

        for (int i = 0; i < maxOrigins; i++) {
            Origin origin = originList.get(i);
            RadialMenuItem item = new RadialMenuItem(
                    origin.getId().toString(),
                    origin.getDisplayName(),
                    origin.getDescription(),
                    getOriginColorByType(origin), // Use colored segments
                    origin);

            // Add abilities as subsections
            for (OriginAbility ability : origin.getAbilities()) {
                item.addSubsection(new RadialSubItem(
                        ability.getId(),
                        ability.getId().substring(0, 1).toUpperCase() + ability.getId().substring(1).replace("_", " "),
                        "Ability: " + ability.getId()));
            }

            menuItems.add(item);
        }
    }

    /**
     * Refresh the origin selection with new random origins.
     */
    public void refreshOrigins() {
        if (mode == MenuMode.ORIGIN_SELECT) {
            menuItems.clear();
            initOriginSelectMenu();
        }
    }

    /**
     * Get a unique color for each origin type for visual distinction.
     */
    private int getOriginColorByType(Origin origin) {
        String id = origin.getId().toString().toLowerCase();

        // Assign distinct colors based on origin themes
        if (id.contains("fire") || id.contains("cinder") || id.contains("flame"))
            return 0xFFCC4422; // Red-orange
        if (id.contains("water") || id.contains("tide") || id.contains("aqua"))
            return 0xFF2266AA; // Blue
        if (id.contains("frost") || id.contains("ice") || id.contains("cold"))
            return 0xFF66AADD; // Light blue
        if (id.contains("stone") || id.contains("earth") || id.contains("rock"))
            return 0xFF886644; // Brown
        if (id.contains("void") || id.contains("dark") || id.contains("shadow"))
            return 0xFF442266; // Purple
        if (id.contains("nature") || id.contains("plant") || id.contains("dryad"))
            return 0xFF44AA44; // Green
        if (id.contains("star") || id.contains("celestial") || id.contains("light"))
            return 0xFFDDDD44; // Yellow
        if (id.contains("necro") || id.contains("death") || id.contains("undead"))
            return 0xFF444444; // Dark gray
        if (id.contains("tech") || id.contains("mech") || id.contains("machine"))
            return 0xFF888899; // Steel gray
        if (id.contains("sky") || id.contains("air") || id.contains("wind"))
            return 0xFF88CCEE; // Sky blue
        if (id.contains("veil") || id.contains("spirit") || id.contains("ghost"))
            return 0xFF9988CC; // Lavender
        if (id.contains("wolf") || id.contains("feral") || id.contains("beast"))
            return 0xFFAA7744; // Tan
        if (id.contains("crystal") || id.contains("gem"))
            return 0xFFDD66DD; // Magenta
        if (id.contains("vampire") || id.contains("blood"))
            return 0xFF992222; // Dark red
        if (id.contains("rift") || id.contains("dimension") || id.contains("portal"))
            return 0xFF6644AA; // Violet
        if (id.contains("ether") || id.contains("astral"))
            return 0xFFAABBCC; // Pale blue

        // Default - use a hash of the name for consistent random color
        int hash = origin.getDisplayName().hashCode();
        int r = 60 + Math.abs(hash % 120);
        int g = 60 + Math.abs((hash >> 8) % 120);
        int b = 60 + Math.abs((hash >> 16) % 120);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    private void initAbilitySelectMenu() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null)
            return;

        Origin playerOrigin = VeilOriginsAPI.getPlayerOrigin(mc.player);
        if (playerOrigin == null)
            return;

        // Add abilities as main menu items
        List<OriginAbility> abilities = playerOrigin.getAbilities();

        for (int i = 0; i < abilities.size(); i++) {
            OriginAbility ability = abilities.get(i);
            String displayName = formatAbilityName(ability.getId());

            boolean onCooldown = ability.isOnCooldown();
            int cooldownSeconds = ability.getCooldown() / 20;
            String description = onCooldown
                    ? "On cooldown: " + cooldownSeconds + "s remaining"
                    : "Ready to use! Cost: " + ability.getResourceCost();

            RadialMenuItem item = new RadialMenuItem(
                    ability.getId(),
                    displayName,
                    description,
                    onCooldown ? 0xFF555555 : getAbilityColor(i),
                    null);
            item.setAbilityIndex(i);
            item.setOnCooldown(onCooldown);

            menuItems.add(item);
        }

        // Add resource info as a special item
        if (playerOrigin.getResourceType() != null) {
            RadialMenuItem resourceItem = new RadialMenuItem(
                    "resource_info",
                    playerOrigin.getResourceType().getName(),
                    "View your resource status",
                    0xFF6666AA,
                    null);
            resourceItem.setSpecialAction("resource_info");
            menuItems.add(resourceItem);
        }

        // Add origin info item
        RadialMenuItem originInfoItem = new RadialMenuItem(
                "origin_info",
                "Origin Info",
                "View details about " + playerOrigin.getDisplayName(),
                0xFFAA66AA,
                null);
        originInfoItem.setSpecialAction("origin_info");
        menuItems.add(originInfoItem);
    }

    private String formatAbilityName(String id) {
        String[] parts = id.split("_");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                result.append(Character.toUpperCase(part.charAt(0)))
                        .append(part.substring(1))
                        .append(" ");
            }
        }
        return result.toString().trim();
    }

    private int getOriginColor(Origin origin) {
        // All origins use the same consistent gray styling
        return COLOR_SEGMENT_DEFAULT;
    }

    private int getAbilityColor(int index) {
        int[] colors = {
                0xFF4488FF, // Blue
                0xFFFF8844, // Orange
                0xFF44FF88, // Green
                0xFFFF44AA, // Pink
                0xFFAAAAFF, // Light purple
                0xFFFFAA44 // Gold
        };
        return colors[index % colors.length];
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Render custom dark transparent background overlay (no blur)
        renderCustomBackground(guiGraphics);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Calculate which segment is being hovered
        updateHoveredSegment(mouseX, mouseY, centerX, centerY);

        // Render the radial menu
        renderRadialMenu(guiGraphics, centerX, centerY);

        // Render center text
        renderCenterText(guiGraphics, centerX, centerY);

        // Render mode indicator
        renderModeIndicator(guiGraphics);
    }

    /**
     * Renders a simple dark semi-transparent overlay as the background.
     * This avoids the blur effect that was rendering on top of menu elements.
     */
    private void renderCustomBackground(GuiGraphics guiGraphics) {
        // Fill the entire screen with a semi-transparent dark overlay (50% opacity)
        guiGraphics.fill(0, 0, this.width, this.height, 0x80000000); // 50% opacity black - less dark
    }

    private void updateHoveredSegment(int mouseX, int mouseY, int centerX, int centerY) {
        int dx = mouseX - centerX;
        int dy = mouseY - centerY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < CENTER_RADIUS) {
            hoveredSegment = -1;
            hoveredSubsection = -1;
        } else if (distance < OUTER_RADIUS + SUBSECTION_OUTER_OFFSET && expandedSegment >= 0) {
            // Check if we're in the subsection area
            if (distance > OUTER_RADIUS + SUBSECTION_INNER_OFFSET && menuItems.size() > expandedSegment) {
                RadialMenuItem expandedItem = menuItems.get(expandedSegment);
                if (expandedItem.hasSubsections()) {
                    double angle = Math.atan2(dy, dx);
                    if (angle < 0)
                        angle += 2 * Math.PI;

                    float segmentAngle = (float) (2 * Math.PI / menuItems.size());
                    float startAngle = expandedSegment * segmentAngle - (float) Math.PI / 2;
                    if (startAngle < 0)
                        startAngle += 2 * Math.PI;

                    float subAngle = segmentAngle / expandedItem.getSubsections().size();
                    float relativeAngle = (float) angle - startAngle;
                    if (relativeAngle < 0)
                        relativeAngle += 2 * Math.PI;
                    if (relativeAngle > Math.PI)
                        relativeAngle -= 2 * Math.PI;

                    if (relativeAngle >= 0 && relativeAngle < segmentAngle) {
                        hoveredSubsection = (int) (relativeAngle / subAngle);
                        hoveredSubsection = Mth.clamp(hoveredSubsection, 0, expandedItem.getSubsections().size() - 1);
                        return;
                    }
                }
            }
        }

        if (distance >= INNER_RADIUS && distance <= OUTER_RADIUS && !menuItems.isEmpty()) {
            double angle = Math.atan2(dy, dx);
            if (angle < 0)
                angle += 2 * Math.PI;

            // Adjust for starting at top (subtract PI/2)
            angle += Math.PI / 2;
            if (angle > 2 * Math.PI)
                angle -= 2 * Math.PI;

            float segmentAngle = (float) (2 * Math.PI / menuItems.size());
            hoveredSegment = (int) (angle / segmentAngle);
            hoveredSegment = Mth.clamp(hoveredSegment, 0, menuItems.size() - 1);
            hoveredSubsection = -1;
        } else if (distance > OUTER_RADIUS) {
            hoveredSubsection = -1;
        }
    }

    private void renderRadialMenu(GuiGraphics guiGraphics, int centerX, int centerY) {
        if (menuItems.isEmpty())
            return;

        // Blend is now handled by GuiGraphics internally in 1.21.10

        int segmentCount = menuItems.size();
        float segmentAngle = 360f / segmentCount;

        // Render all segments - simple gray, only highlight on hover
        for (int i = 0; i < segmentCount; i++) {
            boolean isHovered = hoveredSegment == i;

            // Use simple gray colors - just highlight on hover
            int color = isHovered ? COLOR_SEGMENT_HOVERED : COLOR_SEGMENT_DEFAULT;

            renderSegment(guiGraphics, centerX, centerY, i, segmentAngle, INNER_RADIUS, OUTER_RADIUS, color);
        }

        // Render center circle
        boolean centerHovered = hoveredSegment == -1 &&
                Math.sqrt(Math
                        .pow(Minecraft.getInstance().mouseHandler.xpos() * width
                                / Minecraft.getInstance().getWindow().getWidth() - centerX, 2)
                        +
                        Math.pow(
                                Minecraft.getInstance().mouseHandler.ypos() * height
                                        / Minecraft.getInstance().getWindow().getHeight() - centerY,
                                2)) < CENTER_RADIUS;

        renderCircle(guiGraphics, centerX, centerY, CENTER_RADIUS, centerHovered ? COLOR_CENTER_HOVERED : COLOR_CENTER);

        // Render thin divider lines between segments
        for (int i = 0; i < segmentCount; i++) {
            renderSegmentDivider(guiGraphics, centerX, centerY, i, segmentAngle, INNER_RADIUS, OUTER_RADIUS);
        }

        // Render thin border circles
        renderCircleOutline(guiGraphics, centerX, centerY, OUTER_RADIUS, COLOR_DIVIDER, 1);
        renderCircleOutline(guiGraphics, centerX, centerY, INNER_RADIUS, COLOR_DIVIDER, 1);

        // Render segment icons/content
        for (int i = 0; i < segmentCount; i++) {
            boolean isHovered = hoveredSegment == i;
            renderSegmentContent(guiGraphics, centerX, centerY, i, segmentAngle, menuItems.get(i), isHovered);
        }
    }

    private void renderSegment(GuiGraphics guiGraphics, int cx, int cy, int index, float segmentAngle,
            int innerR, int outerR, int color) {
        // We're not filling segments now - just using outlines
        // The segments are defined by the divider lines and borders
        // Highlight is shown via different icon color on hover
    }

    private void renderSegmentDivider(GuiGraphics guiGraphics, int cx, int cy, int index, float segmentAngle,
            int innerR, int outerR) {
        // Draw a thin line at the START of each segment (creates dividers between
        // segments)
        float angle = (index * segmentAngle - 90) * Mth.DEG_TO_RAD;

        int innerX = (int) (cx + innerR * Mth.cos(angle));
        int innerY = (int) (cy + innerR * Mth.sin(angle));
        int outerX = (int) (cx + outerR * Mth.cos(angle));
        int outerY = (int) (cy + outerR * Mth.sin(angle));

        // Draw thin line (1px)
        drawLine(guiGraphics, innerX, innerY, outerX, outerY, COLOR_DIVIDER);
    }

    private void drawLine(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int color) {
        // Simple line drawing using fill for thin lines
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int steps = Math.max(dx, dy);
        if (steps == 0)
            return;

        float xStep = (float) (x2 - x1) / steps;
        float yStep = (float) (y2 - y1) / steps;

        for (int i = 0; i <= steps; i++) {
            int x = (int) (x1 + i * xStep);
            int y = (int) (y1 + i * yStep);
            guiGraphics.fill(x, y, x + 1, y + 1, color);
        }
    }

    private void renderSegmentContent(GuiGraphics guiGraphics, int cx, int cy, int index,
            float segmentAngle, RadialMenuItem item, boolean isHovered) {
        float midAngle = (index * segmentAngle + segmentAngle / 2 - 90) * Mth.DEG_TO_RAD;
        float radius = (OUTER_RADIUS + INNER_RADIUS) / 2f;

        int iconX = (int) (cx + radius * Mth.cos(midAngle));
        int iconY = (int) (cy + radius * Mth.sin(midAngle));

        // Use simple first letter as icon (until proper icon font is set up)
        String icon = item.getName().substring(0, 1).toUpperCase();

        // Gray icon that brightens on hover (matching reference design)
        int textColor = isHovered ? COLOR_ICON_HOVERED : COLOR_ICON;
        if (item.isOnCooldown()) {
            textColor = 0xFF555555; // Dimmed for cooldown
        }

        // Draw the icon centered at position - no transforms needed in 1.21.10
        int iconWidth = this.font.width(icon);
        guiGraphics.drawString(this.font, icon, iconX - iconWidth / 2, iconY - 4, textColor, false);
    }

    private void renderCircle(GuiGraphics guiGraphics, int cx, int cy, int radius, int color) {
        // Draw a small centered square - use a smaller size for the actual visible
        // center
        // int smallRadius = radius / 2; // Make it smaller
        // guiGraphics.fill(cx - smallRadius, cy - smallRadius, cx + smallRadius, cy +
        // smallRadius, color);
    }

    private void renderCircleOutline(GuiGraphics guiGraphics, int cx, int cy, int radius, int color, int width) {
        // Draw circle outline using simple lines in 1.21.10
        for (int i = 0; i < 64; i++) {
            float angle1 = (float) (i * 2 * Math.PI / 64);
            float angle2 = (float) ((i + 1) * 2 * Math.PI / 64);
            int x1 = (int) (cx + radius * Mth.cos(angle1));
            int y1 = (int) (cy + radius * Mth.sin(angle1));
            int x2 = (int) (cx + radius * Mth.cos(angle2));
            int y2 = (int) (cy + radius * Mth.sin(angle2));
            drawLine(guiGraphics, x1, y1, x2, y2, color);
        }
    }

    private void renderCenterText(GuiGraphics guiGraphics, int cx, int cy) {
        String label;
        String description = "";

        if (hoveredSubsection >= 0 && expandedSegment >= 0 && expandedSegment < menuItems.size()) {
            RadialMenuItem item = menuItems.get(expandedSegment);
            if (item.hasSubsections() && hoveredSubsection < item.getSubsections().size()) {
                RadialSubItem sub = item.getSubsections().get(hoveredSubsection);
                label = sub.getName();
                description = sub.getDescription();
            } else {
                label = "Select";
            }
        } else if (hoveredSegment >= 0 && hoveredSegment < menuItems.size()) {
            RadialMenuItem item = menuItems.get(hoveredSegment);
            label = item.getName();
            description = item.getDescription();
        } else if (selectedSegment >= 0 && selectedSegment < menuItems.size()) {
            label = menuItems.get(selectedSegment).getName();
        } else {
            label = mode == MenuMode.ORIGIN_SELECT ? "Choose Origin" : "Select Ability";
        }

        // Draw label
        int labelWidth = this.font.width(label);

        // Draw semi-transparent background box behind center label
        int bgX = cx - labelWidth / 2 - 3;
        int bgY = cy - 7;
        int bgWidth = labelWidth + 6;
        int bgHeight = 12;
        guiGraphics.fill(bgX, bgY, bgX + bgWidth, bgY + bgHeight, 0xAA000000);

        guiGraphics.drawString(this.font, label, cx - labelWidth / 2, cy - 4, 0xFFFFFFFF, true);

        // Draw description below the menu if hovering
        if (!description.isEmpty()) {
            int descWidth = this.font.width(description);
            int descY = cy + OUTER_RADIUS + 20;

            // Word wrap if too long
            if (descWidth > 300) {
                List<String> lines = wrapText(description, 300);
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    int lineWidth = this.font.width(line);
                    guiGraphics.drawString(this.font, line, cx - lineWidth / 2, descY + i * 12, 0xFFAAAAAA, true);
                }
            } else {
                guiGraphics.drawString(this.font, description, cx - descWidth / 2, descY, 0xFFAAAAAA, true);
            }
        }
    }

    private List<String> wrapText(String text, int maxWidth) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder current = new StringBuilder();

        for (String word : words) {
            String test = current.isEmpty() ? word : current + " " + word;
            if (this.font.width(test) > maxWidth) {
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

    private void renderModeIndicator(GuiGraphics guiGraphics) {
        // Using simple text without section symbols (§) which cause rendering issues
        String modeText = mode == MenuMode.ORIGIN_SELECT
                ? "[Origin Selection] - Click to choose your origin"
                : "[Ability Menu] - Click to activate an ability";

        int modeColor = mode == MenuMode.ORIGIN_SELECT ? 0xFFFFFF55 : 0xFF55FFFF; // Yellow or Cyan

        int textWidth = this.font.width(modeText);
        guiGraphics.drawString(this.font, modeText, (this.width - textWidth) / 2, 10, modeColor, true);

        if (mode == MenuMode.ORIGIN_SELECT) {
            // Show refresh hint
            String refreshText = "Press [R] to shuffle origins";
            int refreshWidth = this.font.width(refreshText);
            guiGraphics.drawString(this.font, refreshText, (this.width - refreshWidth) / 2, 24, 0xFFAAAAAA, true);

            // Show count
            int totalOrigins = VeilOriginsAPI.getAllOrigins().size();
            String countText = "Showing 8 of " + totalOrigins + " origins";
            int countWidth = this.font.width(countText);
            guiGraphics.drawString(this.font, countText, (this.width - countWidth) / 2, 38, 0xFFAAAAAA, true);
        }

        if (mode == MenuMode.ABILITY_SELECT) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                Origin origin = VeilOriginsAPI.getPlayerOrigin(mc.player);
                if (origin != null) {
                    String originText = "Current Origin: " + origin.getDisplayName();
                    int originWidth = this.font.width(originText);
                    guiGraphics.drawString(this.font, originText, (this.width - originWidth) / 2, 24,
                            getOriginColor(origin), true);
                }
            }
        }
    }

    private void handleSegmentClick(int index) {
        RadialMenuItem item = menuItems.get(index);
        selectedSegment = index;

        if (mode == MenuMode.ORIGIN_SELECT) {
            // Directly select the origin - no sub-menu needed
            selectOrigin(item);
        } else {
            // Ability select mode - either activate ability or trigger special action
            if (item.getSpecialAction() != null) {
                handleSpecialAction(item.getSpecialAction());
            } else if (item.getAbilityIndex() >= 0) {
                activateAbility(item.getAbilityIndex());
            }
        }

        VeilOrigins.LOGGER.debug("Clicked segment: {} ({})", index, item.getName());
    }

    private void handleSubsectionClick(int mainIndex, int subIndex) {
        RadialMenuItem item = menuItems.get(mainIndex);
        if (!item.hasSubsections() || subIndex >= item.getSubsections().size())
            return;

        RadialSubItem sub = item.getSubsections().get(subIndex);

        if (mode == MenuMode.ORIGIN_SELECT) {
            // In origin select mode, clicking a subsection confirms the origin selection
            selectOrigin(item);
        }

        VeilOrigins.LOGGER.debug("Clicked subsection: {} > {}", item.getName(), sub.getName());
    }

    private void selectOrigin(RadialMenuItem item) {
        if (item.getOrigin() != null) {
            // Send packet to server to set origin
            ModPackets.sendToServer(new SelectOriginPacket(item.getOrigin().getId().toString()));

            Minecraft.getInstance().player.displayClientMessage(
                    Component.literal("You have selected the " + item.getOrigin().getDisplayName() + " origin!")
                            .withStyle(style -> style.withColor(0x55FF55)),
                    false);

            onClose();
        }
    }

    private void activateAbility(int abilityIndex) {
        // Send ability activation packet
        ModPackets.sendToServer(new ActivateAbilityPacket(abilityIndex));

        VeilOrigins.LOGGER.debug("Activated ability at index: {}", abilityIndex);

        // Close menu after activation
        onClose();
    }

    private void handleSpecialAction(String action) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null)
            return;

        Origin origin = VeilOriginsAPI.getPlayerOrigin(mc.player);
        if (origin == null)
            return;

        switch (action) {
            case "resource_info" -> {
                if (origin.getResourceType() != null) {
                    mc.player.displayClientMessage(
                            Component.literal(origin.getResourceType().getName())
                                    .withStyle(ChatFormatting.AQUA)
                                    .append(Component.literal(": Max " + origin.getResourceType().getMaxAmount() +
                                            " | Regen: " + origin.getResourceType().getRegenRate() + "/tick")
                                            .withStyle(ChatFormatting.WHITE)),
                            false);
                }
            }
            case "origin_info" -> {
                mc.player.displayClientMessage(
                        Component.literal("=== " + origin.getDisplayName() + " ===")
                                .withStyle(ChatFormatting.LIGHT_PURPLE),
                        false);
                mc.player.displayClientMessage(
                        Component.literal(origin.getDescription())
                                .withStyle(ChatFormatting.GRAY),
                        false);
                mc.player.displayClientMessage(
                        Component.literal("Impact Level: ")
                                .withStyle(ChatFormatting.YELLOW)
                                .append(Component.literal(String.valueOf(origin.getImpactLevel()))
                                        .withStyle(ChatFormatting.WHITE)),
                        false);
                mc.player.displayClientMessage(
                        Component.literal("Abilities: ")
                                .withStyle(ChatFormatting.YELLOW)
                                .append(Component.literal(String.valueOf(origin.getAbilities().size()))
                                        .withStyle(ChatFormatting.WHITE))
                                .append(Component.literal(" | Passives: ")
                                        .withStyle(ChatFormatting.YELLOW))
                                .append(Component.literal(String.valueOf(origin.getPassives().size()))
                                        .withStyle(ChatFormatting.WHITE)),
                        false);
            }
        }

        onClose();
    }

    private int blendColors(int color1, int color2, float ratio) {
        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int a = (int) (a1 + (a2 - a1) * ratio);
        int r = (int) (r1 + (r2 - r1) * ratio);
        int g = (int) (g1 + (g2 - g1) * ratio);
        int b = (int) (b1 + (b2 - b1) * ratio);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        // Note: ClientInput fields have changed in 1.21.10
        // Player movement while radial menu is open is handled by the game now
    }

    /**
     * Brighten a color by a given amount.
     */
    private int brightenColor(int color, float amount) {
        int a = (color >> 24) & 0xFF;
        int r = Math.min(255, (int) (((color >> 16) & 0xFF) * (1 + amount)));
        int g = Math.min(255, (int) (((color >> 8) & 0xFF) * (1 + amount)));
        int b = Math.min(255, (int) ((color & 0xFF) * (1 + amount)));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    // Note: keyReleased signature has changed in 1.21.10
    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        // Let movement key releases pass through
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_W ||
                keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_A ||
                keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_S ||
                keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_D ||
                keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE ||
                keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT ||
                keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL) {
            return false;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Left click
            int centerX = this.width / 2;
            int centerY = this.height / 2;

            double dx = mouseX - centerX;
            double dy = mouseY - centerY;
            double distance = Math.sqrt(dx * dx + dy * dy);

            // Check if clicking center to close
            if (distance < CENTER_RADIUS) {
                onClose();
                return true;
            }

            // Check subsection click
            if (hoveredSubsection >= 0 && expandedSegment >= 0) {
                handleSubsectionClick(expandedSegment, hoveredSubsection);
                return true;
            }

            // Check segment click
            if (hoveredSegment >= 0 && hoveredSegment < menuItems.size()) {
                handleSegmentClick(hoveredSegment);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Close on ESC
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            onClose();
            return true;
        }
        // Refresh origins on R key (only in origin select mode)
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_R && mode == MenuMode.ORIGIN_SELECT) {
            refreshOrigins();
            return true;
        }

        // Let movement keys pass through to allow player to move
        // WASD, Space (jump), Shift (sneak)
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_W ||
                keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_A ||
                keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_S ||
                keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_D ||
                keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE ||
                keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT ||
                keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL) {
            return false; // Don't consume - let game handle it
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
