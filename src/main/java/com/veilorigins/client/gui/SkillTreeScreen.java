package com.veilorigins.client.gui;

import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.data.OriginData;
import com.veilorigins.network.ModPackets;
import com.veilorigins.network.packet.UnlockSkillPacket;
import com.veilorigins.progression.skill.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Advanced Skill Tree GUI with zoom, pan, and proper tooltips.
 */
public class SkillTreeScreen extends Screen {
    
    // Node rendering constants
    private static final int NODE_SIZE = 40;
    private static final int GRID_SPACING = 90;
    
    private final Screen parent;
    private SkillTreeData skillTree;
    private OriginData.PlayerOriginData playerData;
    private Origin playerOrigin;
    
    // View state
    private float zoom = 1.0f;
    private float panX = 0;
    private float panY = 0;
    private boolean isDragging = false;
    private double lastMouseX, lastMouseY;
    
    // Hover state
    private Skill hoveredSkill = null;
    
    // Excluded skills (from mutually exclusive choices)
    private Set<String> excludedSkills = new HashSet<>();
    
    public SkillTreeScreen(Screen parent) {
        super(Component.translatable("veil_origins.screen.skill_tree.title"));
        this.parent = parent;
    }
    
    @Override
    protected void init() {
        super.init();
        
        Player player = minecraft.player;
        if (player == null) { onClose(); return; }
        
        playerOrigin = VeilOriginsAPI.getPlayerOrigin(player);
        playerData = OriginData.get(player);
        
        if (playerOrigin == null) { onClose(); return; }
        
        // Get skill tree for this origin
        String originId = playerOrigin.getId().toString();
        if (originId.contains(":")) originId = originId.split(":")[1];
        skillTree = SkillTrees.getTree(originId);
        
        if (skillTree == null) {
            VeilOrigins.LOGGER.warn("No skill tree found for origin: {}", playerOrigin.getId());
            player.displayClientMessage(Component.literal("§eNo skill tree available for this origin yet."), false);
            onClose();
            return;
        }
        
        // Calculate excluded skills based on current unlocks
        calculateExcludedSkills();
        
        // Center the view
        panX = this.width / 2f;
        panY = this.height / 2f - 50;
        
        // Close button
        this.addRenderableWidget(Button.builder(
            Component.literal("Close"),
            button -> onClose()
        ).bounds(this.width / 2 - 40, this.height - 25, 80, 20).build());
        
        // Zoom buttons
        this.addRenderableWidget(Button.builder(
            Component.literal("+"),
            button -> zoom = Math.min(2.0f, zoom + 0.1f)
        ).bounds(this.width - 30, 50, 20, 20).build());
        
        this.addRenderableWidget(Button.builder(
            Component.literal("-"),
            button -> zoom = Math.max(0.5f, zoom - 0.1f)
        ).bounds(this.width - 30, 75, 20, 20).build());
        
        this.addRenderableWidget(Button.builder(
            Component.literal("R"),
            button -> { zoom = 1.0f; panX = this.width / 2f; panY = this.height / 2f - 50; }
        ).bounds(this.width - 30, 100, 20, 20).build());
    }
    
    private void calculateExcludedSkills() {
        excludedSkills.clear();
        for (String skillId : playerData.getUnlockedSkills()) {
            Skill skill = skillTree.getSkill(skillId);
            if (skill != null) {
                excludedSkills.addAll(skill.getExclusions());
            }
        }
    }
    
    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Don't render the default blurred background - we draw our own
    }
    
    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Dark background (our custom one, no blur)
        graphics.fill(0, 0, this.width, this.height, 0xDD101020);
        
        if (skillTree == null || playerData == null) {
            super.render(graphics, mouseX, mouseY, partialTick);
            return;
        }
        
        // Draw grid background
        drawGridBackground(graphics);
        
        // Draw connections
        drawConnections(graphics);
        
        // Draw skill nodes
        hoveredSkill = null;
        for (Skill skill : skillTree.getAllSkills()) {
            drawSkillNode(graphics, skill, mouseX, mouseY);
        }
        
        // Draw UI overlay (not affected by zoom/pan)
        drawUIOverlay(graphics);
        
        // Draw tooltip last (on top)
        if (hoveredSkill != null) {
            drawSkillTooltip(graphics, hoveredSkill, mouseX, mouseY);
        }
        
        // Render widgets
        super.render(graphics, mouseX, mouseY, partialTick);
    }
    
    private void drawGridBackground(GuiGraphics graphics) {
        int gridSize = (int)(50 * zoom);
        int offsetX = (int)(panX % gridSize);
        int offsetY = (int)(panY % gridSize);
        
        for (int x = offsetX; x < this.width; x += gridSize) {
            graphics.fill(x, 0, x + 1, this.height, 0x20FFFFFF);
        }
        for (int y = offsetY; y < this.height; y += gridSize) {
            graphics.fill(0, y, this.width, y + 1, 0x20FFFFFF);
        }
    }
    
    private void drawConnections(GuiGraphics graphics) {
        for (SkillTreeData.SkillConnection conn : skillTree.getConnections()) {
            Skill from = skillTree.getSkill(conn.getFromSkillId());
            Skill to = skillTree.getSkill(conn.getToSkillId());
            if (from == null || to == null) continue;
            
            int[] fromPos = getScreenPosition(from.getGridX(), from.getGridY());
            int[] toPos = getScreenPosition(to.getGridX(), to.getGridY());
            
            boolean fromUnlocked = playerData.hasSkill(from.getId());
            boolean toUnlocked = playerData.hasSkill(to.getId());
            boolean toExcluded = excludedSkills.contains(to.getId());
            
            int color;
            if (toUnlocked) {
                color = 0xFF55FF55; // Green - completed
            } else if (toExcluded) {
                color = 0xFF553333; // Dark red - excluded
            } else if (fromUnlocked) {
                color = 0xFFFFFF55; // Yellow - available
            } else {
                color = 0xFF444444; // Gray - locked
            }
            
            drawConnection(graphics, fromPos[0], fromPos[1], toPos[0], toPos[1], color);
        }
    }
    
    private void drawConnection(GuiGraphics graphics, int x1, int y1, int x2, int y2, int color) {
        int midY = (y1 + y2) / 2;
        int thickness = Math.max(1, (int)(2 * zoom));
        
        // Vertical from start
        graphics.fill(x1 - thickness/2, y1, x1 + thickness/2 + 1, midY, color);
        // Horizontal
        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        graphics.fill(minX - thickness/2, midY - thickness/2, maxX + thickness/2 + 1, midY + thickness/2 + 1, color);
        // Vertical to end
        graphics.fill(x2 - thickness/2, midY, x2 + thickness/2 + 1, y2, color);
    }
    
    private void drawSkillNode(GuiGraphics graphics, Skill skill, int mouseX, int mouseY) {
        int[] pos = getScreenPosition(skill.getGridX(), skill.getGridY());
        int x = pos[0];
        int y = pos[1];
        int size = (int)(NODE_SIZE * zoom);
        int halfSize = size / 2;
        
        int left = x - halfSize;
        int top = y - halfSize;
        int right = x + halfSize;
        int bottom = y + halfSize;
        
        // Check if off screen
        if (right < 0 || left > this.width || bottom < 0 || top > this.height) return;
        
        boolean isUnlocked = playerData.hasSkill(skill.getId());
        boolean isExcluded = excludedSkills.contains(skill.getId());
        boolean canUnlock = skill.canUnlock(playerData.getUnlockedSkills(), excludedSkills, playerData.getOriginLevel());
        boolean hasPoints = playerData.getSkillPoints() >= skill.getSkillPointCost();
        boolean isHovered = mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bottom;
        
        if (isHovered) {
            hoveredSkill = skill;
        }
        
        // Determine colors
        int bgColor, borderColor;
        if (isUnlocked) {
            bgColor = 0xFF225522;
            borderColor = 0xFF55FF55;
        } else if (isExcluded) {
            bgColor = 0xFF331111;
            borderColor = 0xFF662222;
        } else if (canUnlock && hasPoints) {
            bgColor = 0xFF333322;
            borderColor = 0xFFFFFF55;
        } else if (canUnlock) {
            bgColor = 0xFF332222;
            borderColor = 0xFFFF8855;
        } else {
            bgColor = 0xFF222222;
            borderColor = 0xFF444444;
        }
        
        if (isHovered && !isExcluded) {
            bgColor = brighten(bgColor, 30);
            borderColor = 0xFFFFFFFF;
        }
        
        // Draw node
        int borderWidth = Math.max(2, (int)(3 * zoom));
        graphics.fill(left - borderWidth, top - borderWidth, right + borderWidth, bottom + borderWidth, borderColor);
        graphics.fill(left, top, right, bottom, bgColor);
        
        // Draw branch color indicator
        int branchColor = skill.getBranchColor();
        int indicatorSize = Math.max(4, (int)(8 * zoom));
        graphics.fill(right - indicatorSize - 2, top + 2, right - 2, top + indicatorSize + 2, branchColor);
        
        // Draw tier indicator
        int tierColor = skill.getTierColor();
        graphics.fill(left + 2, top + 2, left + indicatorSize + 2, top + indicatorSize + 2, tierColor);
        
        // Draw icon/name if zoomed in enough
        if (zoom >= 0.7f) {
            String icon = skill.getIcon();
            int iconWidth = this.font.width(icon);
            // Use brighter colors with shadow for visibility (like RadialMenuScreen)
            int textColor = isUnlocked ? 0xFFFFFFFF : (isExcluded ? 0xFF666666 : (canUnlock ? 0xFFEEEEEE : 0xFFAAAAAA));
            graphics.drawString(this.font, icon, x - iconWidth / 2, y - 8, textColor, true);
            
            // Draw abbreviated name
            String name = skill.getName();
            if (name.length() > 10) name = name.substring(0, 9) + "..";
            int nameWidth = this.font.width(name);
            if (nameWidth > size - 4) {
                name = name.substring(0, Math.min(6, name.length())) + "..";
                nameWidth = this.font.width(name);
            }
            graphics.drawString(this.font, name, x - nameWidth / 2, y + 2, textColor, true);
            
            // Draw cost
            String cost = String.valueOf(skill.getSkillPointCost());
            int costWidth = this.font.width(cost);
            int costColor = hasPoints || isUnlocked ? 0xFFFFFF00 : 0xFFFF4444;
            graphics.drawString(this.font, cost, x - costWidth / 2, bottom - 10, costColor, true);
        }
    }
    
    private void drawUIOverlay(GuiGraphics graphics) {
        int centerX = this.width / 2;
        
        // Title bar background
        graphics.fill(0, 0, this.width, 45, 0xCC000000);
        
        // Title - use full alpha color with shadow
        String title = skillTree.getDisplayName() + " Skill Tree";
        int titleWidth = this.font.width(title);
        graphics.drawString(this.font, title, centerX - titleWidth / 2, 8, 0xFFFFFFFF, true);
        
        // Origin info
        String info = String.format("Level %d | Skill Points: %d", 
            playerData.getOriginLevel(), playerData.getSkillPoints());
        int infoWidth = this.font.width(info);
        graphics.drawString(this.font, info, centerX - infoWidth / 2, 22, 0xFF55FF55, true);
        
        // Zoom indicator
        String zoomStr = String.format("Zoom: %.0f%%", zoom * 100);
        graphics.drawString(this.font, zoomStr, this.width - 70, 35, 0xFFAAAAAA, true);
        
        // Instructions
        String instructions = "Drag to pan | Scroll to zoom | Click to unlock";
        int instrWidth = this.font.width(instructions);
        graphics.drawString(this.font, instructions, centerX - instrWidth / 2, this.height - 40, 0xFF888888, true);
        
        // Branch legend - compact version
        int legendY = 55;
        int legendX = 10;
        graphics.drawString(this.font, "Branches:", legendX, legendY, 0xFFAAAAAA, true);
        legendY += 11;
        
        // Primary branches
        drawLegendItem(graphics, legendX, legendY, 0xFFFF4444, "Offense"); legendY += 9;
        drawLegendItem(graphics, legendX, legendY, 0xFF4444FF, "Defense"); legendY += 9;
        drawLegendItem(graphics, legendX, legendY, 0xFF44FF44, "Utility"); legendY += 9;
        
        // Elemental
        drawLegendItem(graphics, legendX, legendY, 0xFFFF8800, "Path A"); legendY += 9;
        drawLegendItem(graphics, legendX, legendY, 0xFF00CCFF, "Path B"); legendY += 9;
        
        // Advanced
        drawLegendItem(graphics, legendX, legendY, 0xFFAA44AA, "Hybrid"); legendY += 9;
        drawLegendItem(graphics, legendX, legendY, 0xFFFFFF00, "Specialist"); legendY += 9;
        
        // Secret
        drawLegendItem(graphics, legendX, legendY, 0xFF880000, "Forbidden"); legendY += 9;
        drawLegendItem(graphics, legendX, legendY, 0xFFFFFFAA, "Ascended"); legendY += 9;
        
        // Synergy
        drawLegendItem(graphics, legendX, legendY, 0xFFFF44FF, "Synergy");
    }
    
    private void drawLegendItem(GuiGraphics graphics, int x, int y, int color, String text) {
        graphics.fill(x, y, x + 8, y + 8, color);
        graphics.drawString(this.font, text, x + 12, y, 0xFFCCCCCC, true);
    }
    
    private void drawSkillTooltip(GuiGraphics graphics, Skill skill, int mouseX, int mouseY) {
        List<String> lines = new ArrayList<>();
        
        boolean isUnlocked = playerData.hasSkill(skill.getId());
        boolean isExcluded = excludedSkills.contains(skill.getId());
        boolean canUnlock = skill.canUnlock(playerData.getUnlockedSkills(), excludedSkills, playerData.getOriginLevel());
        boolean hasPoints = playerData.getSkillPoints() >= skill.getSkillPointCost();
        
        // Name with color
        String namePrefix = isUnlocked ? "§a" : (isExcluded ? "§4§m" : (canUnlock ? "§e" : "§7"));
        lines.add(namePrefix + "§l" + skill.getName());
        
        // Tier and branch
        lines.add("§5Tier " + skill.getTier().getTier() + " §7| §" + getBranchColorCode(skill.getBranch()) + skill.getBranch().name());
        
        // Description
        lines.add("");
        lines.add("§f" + skill.getDescription());
        
        // Effects
        if (!skill.getEffects().isEmpty()) {
            lines.add("");
            lines.add("§6Effects:");
            for (SkillEffect effect : skill.getEffects()) {
                lines.add("§7  • §f" + effect.getDescription());
            }
        }
        
        // Requirements
        lines.add("");
        lines.add("§6Requirements:");
        
        // Level
        boolean levelMet = playerData.getOriginLevel() >= skill.getTier().getLevelRequired();
        String levelColor = levelMet ? "§a" : "§c";
        lines.add(levelColor + "  Level " + skill.getTier().getLevelRequired() + (levelMet ? " ✓" : " ✗"));
        
        // Cost
        String costColor = hasPoints || isUnlocked ? "§a" : "§c";
        lines.add(costColor + "  Cost: " + skill.getSkillPointCost() + " SP" + (hasPoints || isUnlocked ? " ✓" : " ✗"));
        
        // Prerequisites
        for (String prereqId : skill.getPrerequisites()) {
            Skill prereq = skillTree.getSkill(prereqId);
            if (prereq != null) {
                boolean met = playerData.hasSkill(prereqId);
                String color = met ? "§a" : "§c";
                lines.add(color + "  Requires: " + prereq.getName() + (met ? " ✓" : " ✗"));
            }
        }
        
        // Exclusions warning
        if (!skill.getExclusions().isEmpty() && !isUnlocked) {
            lines.add("");
            lines.add("§c§lWARNING: Locks out:");
            for (String exclId : skill.getExclusions()) {
                Skill excl = skillTree.getSkill(exclId);
                if (excl != null) {
                    lines.add("§c  ✗ " + excl.getName());
                }
            }
        }
        
        // Status
        lines.add("");
        if (isUnlocked) {
            lines.add("§a§l✓ UNLOCKED");
        } else if (isExcluded) {
            lines.add("§4§l✗ LOCKED OUT");
            lines.add("§7(Another skill excludes this)");
        } else if (canUnlock && hasPoints) {
            lines.add("§e§lClick to unlock!");
        } else if (canUnlock) {
            lines.add("§cNeed more skill points");
        } else {
            lines.add("§7Requirements not met");
        }
        
        // Calculate tooltip dimensions
        int maxWidth = 0;
        for (String line : lines) {
            int w = this.font.width(line.replaceAll("§.", ""));
            if (w > maxWidth) maxWidth = w;
        }
        maxWidth = Math.min(maxWidth + 16, 280);
        int tooltipHeight = lines.size() * 10 + 8;
        
        // Position tooltip
        int tooltipX = mouseX + 15;
        int tooltipY = mouseY - 10;
        
        if (tooltipX + maxWidth > this.width - 10) {
            tooltipX = mouseX - maxWidth - 15;
        }
        if (tooltipY + tooltipHeight > this.height - 10) {
            tooltipY = this.height - tooltipHeight - 10;
        }
        if (tooltipY < 50) tooltipY = 50;
        
        // Draw tooltip background
        graphics.fill(tooltipX - 4, tooltipY - 4, tooltipX + maxWidth + 4, tooltipY + tooltipHeight + 4, 0xF0100010);
        graphics.fill(tooltipX - 3, tooltipY - 3, tooltipX + maxWidth + 3, tooltipY + tooltipHeight + 3, 0xFF5000AA);
        graphics.fill(tooltipX - 2, tooltipY - 2, tooltipX + maxWidth + 2, tooltipY + tooltipHeight + 2, 0xF0100010);
        
        // Draw text - use full alpha color with shadow for visibility
        int textY = tooltipY;
        for (String line : lines) {
            graphics.drawString(this.font, line, tooltipX, textY, 0xFFFFFFFF, true);
            textY += 10;
        }
    }
    
    private char getBranchColorCode(Skill.SkillBranch branch) {
        return switch (branch) {
            case CORE -> 'f';           // White
            case OFFENSE -> 'c';        // Red
            case DEFENSE -> '9';        // Blue
            case UTILITY -> 'a';        // Green
            case ELEMENTAL_A -> '6';    // Orange
            case ELEMENTAL_B -> 'b';    // Cyan
            case HYBRID -> 'e';         // Yellow
            case SPECIALIST -> 'd';     // Light Purple
            case FORBIDDEN -> '4';      // Dark Red
            case ASCENDED -> 'e';       // Yellow (golden)
            case SYNERGY_AD -> '5';     // Purple
            case SYNERGY_AU -> '2';     // Dark Green
            case SYNERGY_DU -> '3';     // Dark Cyan
        };
    }
    
    private int[] getScreenPosition(int gridX, int gridY) {
        float x = panX + gridX * GRID_SPACING * zoom;
        float y = panY + gridY * GRID_SPACING * zoom;
        return new int[]{(int)x, (int)y};
    }
    
    private int brighten(int color, int amount) {
        int a = (color >> 24) & 0xFF;
        int r = Math.min(255, ((color >> 16) & 0xFF) + amount);
        int g = Math.min(255, ((color >> 8) & 0xFF) + amount);
        int b = Math.min(255, (color & 0xFF) + amount);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
    
    private void tryUnlockSkill(Skill skill) {
        boolean isUnlocked = playerData.hasSkill(skill.getId());
        boolean isExcluded = excludedSkills.contains(skill.getId());
        boolean canUnlock = skill.canUnlock(playerData.getUnlockedSkills(), excludedSkills, playerData.getOriginLevel());
        boolean hasPoints = playerData.getSkillPoints() >= skill.getSkillPointCost();
        
        if (!isUnlocked && !isExcluded && canUnlock && hasPoints) {
            // Send unlock packet
            ModPackets.sendToServer(new UnlockSkillPacket(skill.getId()));
            VeilOrigins.LOGGER.debug("Sent unlock skill packet for: {}", skill.getId());
            
            // Optimistic update
            playerData.unlockSkill(skill.getId());
            playerData.spendSkillPoints(skill.getSkillPointCost());
            
            // Recalculate exclusions
            calculateExcludedSkills();
        }
    }
    
    // Mouse state tracking
    private boolean wasMousePressed = false;
    
    @Override
    public void tick() {
        super.tick();
        
        // Get mouse state using GLFW (same pattern as RadialMenuScreen)
        long windowHandle = org.lwjgl.glfw.GLFW.glfwGetCurrentContext();
        boolean isLeftPressed = org.lwjgl.glfw.GLFW.glfwGetMouseButton(windowHandle, org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT) == org.lwjgl.glfw.GLFW.GLFW_PRESS;
        
        // Get mouse position
        double mouseX = minecraft.mouseHandler.xpos() * width / minecraft.getWindow().getWidth();
        double mouseY = minecraft.mouseHandler.ypos() * height / minecraft.getWindow().getHeight();
        
        // Handle left click (unlock skill)
        if (isLeftPressed && !wasMousePressed) {
            if (hoveredSkill != null) {
                tryUnlockSkill(hoveredSkill);
            } else {
                // Start dragging
                isDragging = true;
                lastMouseX = mouseX;
                lastMouseY = mouseY;
            }
        }
        
        // Handle dragging
        if (isLeftPressed && isDragging) {
            panX += (float)(mouseX - lastMouseX);
            panY += (float)(mouseY - lastMouseY);
            lastMouseX = mouseX;
            lastMouseY = mouseY;
        }
        
        // Stop dragging on release
        if (!isLeftPressed) {
            isDragging = false;
        }
        
        wasMousePressed = isLeftPressed;
    }
    
    // Handle scroll wheel for zoom
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        float oldZoom = zoom;
        zoom += (float)(scrollY * 0.1);
        zoom = Math.max(0.4f, Math.min(2.5f, zoom));
        
        // Zoom toward mouse position
        float zoomFactor = zoom / oldZoom;
        panX = (float)(mouseX - (mouseX - panX) * zoomFactor);
        panY = (float)(mouseY - (mouseY - panY) * zoomFactor);
        
        return true;
    }
    
    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
    
    @Override
    public boolean isPauseScreen() {
        return true;
    }
}
