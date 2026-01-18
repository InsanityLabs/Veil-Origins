package com.veilorigins.client.gui;

import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.OriginAbility;
import com.veilorigins.api.OriginPassive;
import com.veilorigins.api.ImpactLevel;
import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.client.ClientOriginData;
import com.veilorigins.data.OriginData;
import com.veilorigins.network.ModPackets;
import com.veilorigins.network.packet.ActivateAbilityPacket;
import com.veilorigins.progression.ProgressionSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Comprehensive Origin & Ability Dashboard
 * Shows full origin info, progression, abilities, passives, and stats.
 */
public class AbilityBarScreen extends Screen {
    
    // Layout constants
    private static final int PANEL_PADDING = 12;
    private static final int SECTION_GAP = 8;
    
    // Colors - Dark theme with accent colors
    private static final int COLOR_BG = 0xEE0a0a12;
    private static final int COLOR_PANEL = 0xDD12121f;
    private static final int COLOR_PANEL_BORDER = 0xFF2a2a45;
    private static final int COLOR_PANEL_HEADER = 0xFF1a1a2a;
    private static final int COLOR_ACCENT = 0xFF5566aa;
    private static final int COLOR_TEXT = 0xFFe8e8f0;
    private static final int COLOR_TEXT_DIM = 0xFF888899;
    private static final int COLOR_TEXT_BRIGHT = 0xFFFFFFFF;
    private static final int COLOR_POSITIVE = 0xFF55dd55;
    private static final int COLOR_NEGATIVE = 0xFFdd5555;
    private static final int COLOR_WARNING = 0xFFddaa55;
    private static final int COLOR_XP = 0xFF55ddaa;
    private static final int COLOR_COOLDOWN = 0xFFff6666;
    private static final int COLOR_READY = 0xFF66ff66;
    
    // State
    private Origin playerOrigin;
    private List<OriginAbility> abilities;
    private List<OriginPassive> passives;
    private int hoveredAbility = -1;
    private float animProgress = 0;
    
    // Cached data
    private int originColor;
    private String resourceName;
    
    public AbilityBarScreen() {
        super(Component.translatable("screen.veil_origins.ability_dashboard"));
    }
    
    @Override
    protected void init() {
        super.init();
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) { onClose(); return; }
        
        playerOrigin = VeilOriginsAPI.getPlayerOrigin(mc.player);
        if (playerOrigin == null) { onClose(); return; }
        
        abilities = playerOrigin.getAbilities();
        passives = playerOrigin.getPassives();
        originColor = getOriginThemeColor();
        resourceName = getResourceName();
        animProgress = 0;
    }
    
    @Override
    public void tick() {
        super.tick();
        if (animProgress < 1.0f) animProgress = Math.min(1.0f, animProgress + 0.08f);
    }
    
    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Custom background - no blur
    }
    
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (playerOrigin == null) return;
        
        // Full screen dark background
        graphics.fill(0, 0, width, height, COLOR_BG);
        
        // Calculate layout - three column design
        int contentWidth = Math.min(width - 40, 900);
        int contentX = (width - contentWidth) / 2;
        int contentY = 25;
        
        // Apply animation
        int animOffset = (int)((1.0f - animProgress) * 30);
        contentY += animOffset;
        
        // Left column: Origin Info + Stats (35%)
        int leftWidth = (int)(contentWidth * 0.35f) - SECTION_GAP;
        int leftX = contentX;
        
        // Center column: Abilities (40%)
        int centerWidth = (int)(contentWidth * 0.40f) - SECTION_GAP;
        int centerX = leftX + leftWidth + SECTION_GAP;
        
        // Right column: Passives + Progression (25%)
        int rightWidth = contentWidth - leftWidth - centerWidth - SECTION_GAP * 2;
        int rightX = centerX + centerWidth + SECTION_GAP;
        
        // Update hover state
        hoveredAbility = -1;
        
        // Render columns
        renderOriginPanel(graphics, leftX, contentY, leftWidth);
        renderStatsPanel(graphics, leftX, contentY + 195, leftWidth);
        renderAbilitiesPanel(graphics, centerX, contentY, centerWidth, mouseX, mouseY);
        renderPassivesPanel(graphics, rightX, contentY, rightWidth);
        renderProgressionPanel(graphics, rightX, contentY + 180, rightWidth);
        
        // Title bar
        renderTitleBar(graphics);
        
        // Instructions
        renderInstructions(graphics);
        
        super.render(graphics, mouseX, mouseY, partialTick);
    }
    
    private void renderTitleBar(GuiGraphics graphics) {
        // Title background
        graphics.fill(0, 0, width, 22, 0xDD000008);
        graphics.fill(0, 21, width, 22, originColor);
        
        // Title text
        String title = "⚔ " + playerOrigin.getDisplayName() + " Dashboard";
        graphics.drawString(font, title, (width - font.width(title)) / 2, 7, COLOR_TEXT_BRIGHT, true);
        
        // ESC hint
        graphics.drawString(font, "[ESC] Close", 10, 7, COLOR_TEXT_DIM, false);
    }

    
    private void renderOriginPanel(GuiGraphics graphics, int x, int y, int w) {
        int h = 185;
        
        // Panel background
        drawPanel(graphics, x, y, w, h, "ORIGIN INFO");
        
        int innerX = x + PANEL_PADDING;
        int innerY = y + 28;
        int innerW = w - PANEL_PADDING * 2;
        
        // Origin name with color accent
        graphics.fill(innerX, innerY, innerX + 4, innerY + 16, originColor);
        graphics.drawString(font, playerOrigin.getDisplayName(), innerX + 10, innerY + 4, COLOR_TEXT_BRIGHT, true);
        innerY += 22;
        
        // Impact level with stars
        String impact = "Impact: " + getImpactStars(playerOrigin.getImpactLevel());
        int impactColor = getImpactColor(playerOrigin.getImpactLevel());
        graphics.drawString(font, impact, innerX, innerY, impactColor, false);
        innerY += 14;
        
        // Divider
        graphics.fill(innerX, innerY, innerX + innerW, innerY + 1, COLOR_PANEL_BORDER);
        innerY += 8;
        
        // Description (word wrapped)
        String desc = playerOrigin.getDescription();
        List<String> lines = wrapText(desc, innerW);
        for (int i = 0; i < Math.min(lines.size(), 4); i++) {
            graphics.drawString(font, lines.get(i), innerX, innerY, COLOR_TEXT_DIM, false);
            innerY += 10;
        }
        if (lines.size() > 4) {
            graphics.drawString(font, "...", innerX, innerY, COLOR_TEXT_DIM, false);
            innerY += 10;
        }
        innerY += 6;
        
        // Resource bar
        graphics.fill(innerX, innerY, innerX + innerW, innerY + 1, COLOR_PANEL_BORDER);
        innerY += 8;
        
        graphics.drawString(font, resourceName, innerX, innerY, originColor, false);
        innerY += 12;
        
        float resource = ClientOriginData.getResourceBar();
        drawResourceBar(graphics, innerX, innerY, innerW, 12, resource, originColor);
        innerY += 18;
        
        // Resource value
        String resourceText = String.format("%.0f / 100", resource);
        graphics.drawString(font, resourceText, innerX + (innerW - font.width(resourceText)) / 2, innerY, COLOR_TEXT, false);
    }
    
    private void renderStatsPanel(GuiGraphics graphics, int x, int y, int w) {
        int h = height - y - 50;
        h = Math.min(h, 150);
        
        drawPanel(graphics, x, y, w, h, "MODIFIERS");
        
        int innerX = x + PANEL_PADDING;
        int innerY = y + 28;
        
        // Health modifier
        float health = playerOrigin.getHealthModifier();
        renderStatRow(graphics, innerX, innerY, w - PANEL_PADDING * 2, "❤ Health", health);
        innerY += 18;
        
        // Speed modifier
        float speed = playerOrigin.getSpeedModifier();
        renderStatRow(graphics, innerX, innerY, w - PANEL_PADDING * 2, "⚡ Speed", speed);
        innerY += 18;
        
        // Damage modifier
        float damage = playerOrigin.getDamageModifier();
        renderStatRow(graphics, innerX, innerY, w - PANEL_PADDING * 2, "⚔ Damage", damage);
        innerY += 22;
        
        // Divider
        graphics.fill(innerX, innerY, innerX + w - PANEL_PADDING * 2, innerY + 1, COLOR_PANEL_BORDER);
        innerY += 8;
        
        // Ability count
        graphics.drawString(font, "Abilities: " + abilities.size(), innerX, innerY, COLOR_TEXT_DIM, false);
        innerY += 12;
        graphics.drawString(font, "Passives: " + passives.size(), innerX, innerY, COLOR_TEXT_DIM, false);
    }
    
    private void renderStatRow(GuiGraphics graphics, int x, int y, int w, String label, float value) {
        graphics.drawString(font, label, x, y, COLOR_TEXT, false);
        
        String valueStr;
        int valueColor;
        if (value > 1.0f) {
            valueStr = String.format("+%.0f%%", (value - 1) * 100);
            valueColor = COLOR_POSITIVE;
        } else if (value < 1.0f) {
            valueStr = String.format("%.0f%%", (value - 1) * 100);
            valueColor = COLOR_NEGATIVE;
        } else {
            valueStr = "±0%";
            valueColor = COLOR_TEXT_DIM;
        }
        
        graphics.drawString(font, valueStr, x + w - font.width(valueStr), y, valueColor, false);
    }
    
    private void renderAbilitiesPanel(GuiGraphics graphics, int x, int y, int w, int mouseX, int mouseY) {
        int h = height - y - 50;
        
        drawPanel(graphics, x, y, w, h, "ABILITIES (" + abilities.size() + ")");
        
        int innerX = x + PANEL_PADDING;
        int innerY = y + 28;
        int innerW = w - PANEL_PADDING * 2;
        
        if (abilities.isEmpty()) {
            graphics.drawString(font, "No abilities available", innerX, innerY, COLOR_TEXT_DIM, false);
            return;
        }
        
        int slotHeight = 58;
        int slotGap = 6;
        
        for (int i = 0; i < abilities.size(); i++) {
            int slotY = innerY + i * (slotHeight + slotGap);
            if (slotY + slotHeight > y + h - 10) break;
            
            OriginAbility ability = abilities.get(i);
            boolean isHovered = mouseX >= innerX && mouseX <= innerX + innerW &&
                               mouseY >= slotY && mouseY <= slotY + slotHeight;
            
            if (isHovered) hoveredAbility = i;
            
            renderAbilitySlot(graphics, ability, i, innerX, slotY, innerW, slotHeight, isHovered);
        }
    }
    
    private void renderAbilitySlot(GuiGraphics graphics, OriginAbility ability, int index, 
                                    int x, int y, int w, int h, boolean hovered) {
        boolean onCooldown = ability.isOnCooldown();
        int cooldown = ability.getCooldown();
        int maxCooldown = ability.getMaxCooldown();
        
        // Slot background
        int bgColor = hovered ? 0xFF1f1f35 : 0xFF15152a;
        graphics.fill(x, y, x + w, y + h, bgColor);
        
        // Left accent bar (color based on status)
        int accentColor = onCooldown ? COLOR_COOLDOWN : COLOR_READY;
        graphics.fill(x, y, x + 3, y + h, accentColor);
        
        // Border
        int borderColor = hovered ? originColor : COLOR_PANEL_BORDER;
        drawBorder(graphics, x, y, w, h, borderColor, 1);
        
        // Cooldown overlay
        if (onCooldown && maxCooldown > 0) {
            float pct = (float) cooldown / maxCooldown;
            int overlayW = (int)(w * pct);
            graphics.fill(x + w - overlayW, y + 1, x + w - 1, y + h - 1, 0x44000000);
        }
        
        int contentX = x + 10;
        int contentY = y + 6;
        
        // Icon
        String icon = getAbilityIcon(ability);
        graphics.drawString(font, icon, contentX, contentY, COLOR_TEXT_BRIGHT, true);
        
        // Keybind badge
        String keybind = "[" + getKeybindHint(index) + "]";
        int keybindX = contentX + 20;
        graphics.fill(keybindX, contentY - 1, keybindX + font.width(keybind) + 4, contentY + 10, 0xAA000000);
        graphics.drawString(font, keybind, keybindX + 2, contentY, 0xFFffff88, false);
        
        // Ability name
        String name = formatAbilityName(ability.getId());
        graphics.drawString(font, name, keybindX + font.width(keybind) + 10, contentY, COLOR_TEXT_BRIGHT, false);
        
        contentY += 14;
        
        // Description
        String desc = getAbilityDescription(ability);
        if (desc.length() > 45) desc = desc.substring(0, 42) + "...";
        graphics.drawString(font, desc, contentX, contentY, COLOR_TEXT_DIM, false);
        
        contentY += 14;
        
        // Stats row
        // Cost
        int cost = ability.getResourceCost();
        String costStr = "Cost: " + cost;
        graphics.drawString(font, costStr, contentX, contentY, cost > 0 ? originColor : COLOR_TEXT_DIM, false);
        
        // Cooldown
        String cdStr = "CD: " + (maxCooldown / 20) + "s";
        graphics.drawString(font, cdStr, contentX + 70, contentY, COLOR_TEXT_DIM, false);
        
        // Status (right side)
        String status;
        int statusColor;
        if (onCooldown) {
            status = formatCooldown(cooldown);
            statusColor = COLOR_COOLDOWN;
        } else {
            status = "READY";
            statusColor = COLOR_READY;
        }
        graphics.drawString(font, status, x + w - font.width(status) - 8, contentY, statusColor, true);
    }

    
    private void renderPassivesPanel(GuiGraphics graphics, int x, int y, int w) {
        int h = 170;
        
        drawPanel(graphics, x, y, w, h, "PASSIVES (" + passives.size() + ")");
        
        int innerX = x + PANEL_PADDING;
        int innerY = y + 28;
        int innerW = w - PANEL_PADDING * 2;
        
        if (passives.isEmpty()) {
            graphics.drawString(font, "No passives", innerX, innerY, COLOR_TEXT_DIM, false);
            return;
        }
        
        for (int i = 0; i < Math.min(passives.size(), 6); i++) {
            OriginPassive passive = passives.get(i);
            String name = formatPassiveName(passive.getId());
            String icon = getPassiveIcon(passive);
            
            // Passive row
            graphics.fill(innerX, innerY, innerX + innerW, innerY + 18, 0x44000000);
            graphics.drawString(font, icon + " " + name, innerX + 4, innerY + 5, COLOR_TEXT, false);
            
            innerY += 22;
        }
        
        if (passives.size() > 6) {
            graphics.drawString(font, "+" + (passives.size() - 6) + " more...", innerX, innerY, COLOR_TEXT_DIM, false);
        }
    }
    
    private void renderProgressionPanel(GuiGraphics graphics, int x, int y, int w) {
        int h = height - y - 50;
        h = Math.min(h, 165);
        
        drawPanel(graphics, x, y, w, h, "PROGRESSION");
        
        int innerX = x + PANEL_PADDING;
        int innerY = y + 28;
        int innerW = w - PANEL_PADDING * 2;
        
        int level = ClientOriginData.getLevel();
        int xp = ClientOriginData.getXp();
        int skillPoints = ClientOriginData.getSkillPoints();
        float xpProgress = ClientOriginData.getXpProgress();
        int xpNeeded = ClientOriginData.getXpForNextLevel();
        
        // Level display (large)
        String levelStr = "LVL " + level;
        graphics.drawString(font, levelStr, innerX, innerY, 0xFFffcc00, true);
        innerY += 16;
        
        // XP bar
        graphics.drawString(font, "Experience", innerX, innerY, COLOR_TEXT_DIM, false);
        innerY += 10;
        
        drawProgressBar(graphics, innerX, innerY, innerW, 10, xpProgress, COLOR_XP);
        innerY += 14;
        
        String xpText = xp + " / " + xpNeeded + " XP";
        graphics.drawString(font, xpText, innerX, innerY, COLOR_TEXT_DIM, false);
        innerY += 18;
        
        // Skill points
        graphics.fill(innerX, innerY, innerX + innerW, innerY + 1, COLOR_PANEL_BORDER);
        innerY += 8;
        
        graphics.drawString(font, "Skill Points", innerX, innerY, COLOR_TEXT_DIM, false);
        innerY += 12;
        
        String spStr = String.valueOf(skillPoints);
        int spColor = skillPoints > 0 ? COLOR_POSITIVE : COLOR_TEXT_DIM;
        graphics.drawString(font, spStr, innerX, innerY, spColor, true);
        
        if (skillPoints > 0) {
            graphics.drawString(font, " available", innerX + font.width(spStr), innerY, COLOR_TEXT_DIM, false);
        }
        innerY += 18;
        
        // Unlocked skills count
        int unlockedCount = ClientOriginData.getUnlockedSkills().size();
        graphics.drawString(font, "Skills: " + unlockedCount + " unlocked", innerX, innerY, COLOR_TEXT_DIM, false);
    }
    
    private void renderInstructions(GuiGraphics graphics) {
        String instructions = "Click ability or press keybind to activate • [T] Skill Tree • [ESC] Close";
        int textWidth = font.width(instructions);
        graphics.fill(0, height - 25, width, height, 0xCC000008);
        graphics.drawString(font, instructions, (width - textWidth) / 2, height - 17, COLOR_TEXT_DIM, false);
    }
    
    // ========== Helper Methods ==========
    
    private void drawPanel(GuiGraphics graphics, int x, int y, int w, int h, String title) {
        // Background
        graphics.fill(x, y, x + w, y + h, COLOR_PANEL);
        
        // Header
        graphics.fill(x, y, x + w, y + 22, COLOR_PANEL_HEADER);
        graphics.fill(x, y + 21, x + w, y + 22, originColor);
        
        // Title
        graphics.drawString(font, title, x + 8, y + 7, COLOR_TEXT, false);
        
        // Border
        drawBorder(graphics, x, y, w, h, COLOR_PANEL_BORDER, 1);
    }
    
    private void drawBorder(GuiGraphics graphics, int x, int y, int w, int h, int color, int thickness) {
        graphics.fill(x, y, x + w, y + thickness, color);
        graphics.fill(x, y + h - thickness, x + w, y + h, color);
        graphics.fill(x, y, x + thickness, y + h, color);
        graphics.fill(x + w - thickness, y, x + w, y + h, color);
    }
    
    private void drawResourceBar(GuiGraphics graphics, int x, int y, int w, int h, float value, int color) {
        // Background
        graphics.fill(x, y, x + w, y + h, 0xFF111118);
        
        // Fill
        int fillW = (int)(w * (value / 100f));
        if (fillW > 0) {
            graphics.fill(x, y, x + fillW, y + h, color);
            // Highlight
            graphics.fill(x, y, x + fillW, y + 2, brighten(color, 40));
        }
        
        // Border
        drawBorder(graphics, x, y, w, h, 0xFF333344, 1);
    }
    
    private void drawProgressBar(GuiGraphics graphics, int x, int y, int w, int h, float progress, int color) {
        graphics.fill(x, y, x + w, y + h, 0xFF111118);
        
        int fillW = (int)(w * progress);
        if (fillW > 0) {
            graphics.fill(x, y, x + fillW, y + h, color);
        }
        
        drawBorder(graphics, x, y, w, h, 0xFF333344, 1);
    }
    
    private int brighten(int color, int amount) {
        int a = (color >> 24) & 0xFF;
        int r = Math.min(255, ((color >> 16) & 0xFF) + amount);
        int g = Math.min(255, ((color >> 8) & 0xFF) + amount);
        int b = Math.min(255, (color & 0xFF) + amount);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
    
    private List<String> wrapText(String text, int maxWidth) {
        java.util.List<String> lines = new java.util.ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        
        for (String word : words) {
            String test = line.length() == 0 ? word : line + " " + word;
            if (font.width(test) <= maxWidth) {
                if (line.length() > 0) line.append(" ");
                line.append(word);
            } else {
                if (line.length() > 0) lines.add(line.toString());
                line = new StringBuilder(word);
            }
        }
        if (line.length() > 0) lines.add(line.toString());
        return lines;
    }
    
    private String getImpactStars(ImpactLevel level) {
        return switch (level) {
            case LOW -> "★☆☆";
            case MEDIUM -> "★★☆";
            case HIGH -> "★★★";
        };
    }
    
    private int getImpactColor(ImpactLevel level) {
        return switch (level) {
            case LOW -> COLOR_POSITIVE;
            case MEDIUM -> COLOR_WARNING;
            case HIGH -> COLOR_NEGATIVE;
        };
    }
    
    private int getOriginThemeColor() {
        if (playerOrigin == null) return COLOR_ACCENT;
        String id = playerOrigin.getId().toString().toLowerCase();
        
        if (id.contains("fire") || id.contains("cinder")) return 0xFFff6644;
        if (id.contains("water") || id.contains("tide")) return 0xFF4488ff;
        if (id.contains("frost") || id.contains("ice")) return 0xFF88ddff;
        if (id.contains("nature") || id.contains("dryad")) return 0xFF44ff66;
        if (id.contains("shadow") || id.contains("umbra")) return 0xFF9955cc;
        if (id.contains("vampire") || id.contains("blood")) return 0xFFcc3333;
        if (id.contains("wolf") || id.contains("lycan") || id.contains("werewolf")) return 0xFFbb8844;
        if (id.contains("necro")) return 0xFF7788aa;
        if (id.contains("storm")) return 0xFF8888ff;
        if (id.contains("phantom")) return 0xFFaaaadd;
        if (id.contains("demon")) return 0xFFff4466;
        if (id.contains("angel") || id.contains("celestial")) return 0xFFffffaa;
        
        return 0xFFffaa44;
    }
    
    private String getResourceName() {
        if (playerOrigin == null) return "Resource";
        String id = playerOrigin.getId().toString().toLowerCase();
        
        if (id.contains("vampire") || id.contains("blood")) return "Blood";
        if (id.contains("necro")) return "Soul Energy";
        if (id.contains("wolf") || id.contains("lycan") || id.contains("werewolf")) return "Rage";
        if (id.contains("phantom")) return "Ectoplasm";
        if (id.contains("fire") || id.contains("cinder")) return "Heat";
        if (id.contains("frost") || id.contains("ice")) return "Frost";
        if (id.contains("nature") || id.contains("dryad")) return "Nature";
        if (id.contains("shadow") || id.contains("umbra")) return "Shadow";
        if (id.contains("storm")) return "Charge";
        if (id.contains("demon")) return "Corruption";
        if (id.contains("angel") || id.contains("celestial")) return "Grace";
        
        return "Energy";
    }

    
    private String getAbilityIcon(OriginAbility ability) {
        String id = ability.getId().toLowerCase();
        
        if (id.contains("fire") || id.contains("flame") || id.contains("burn")) return "🔥";
        if (id.contains("ice") || id.contains("frost") || id.contains("freeze")) return "❄";
        if (id.contains("lightning") || id.contains("thunder") || id.contains("shock")) return "⚡";
        if (id.contains("heal") || id.contains("regen")) return "💚";
        if (id.contains("shield") || id.contains("protect") || id.contains("barrier")) return "🛡";
        if (id.contains("dash") || id.contains("speed") || id.contains("rush") || id.contains("charge")) return "💨";
        if (id.contains("shadow") || id.contains("dark") || id.contains("void")) return "🌑";
        if (id.contains("light") || id.contains("holy") || id.contains("radiant")) return "✨";
        if (id.contains("nature") || id.contains("plant") || id.contains("vine")) return "🌿";
        if (id.contains("blood") || id.contains("drain") || id.contains("life")) return "🩸";
        if (id.contains("summon") || id.contains("raise") || id.contains("call")) return "👻";
        if (id.contains("transform") || id.contains("shift") || id.contains("morph")) return "🐺";
        if (id.contains("teleport") || id.contains("blink") || id.contains("phase") || id.contains("step")) return "🌀";
        if (id.contains("possess") || id.contains("control")) return "👁";
        if (id.contains("burst") || id.contains("explode") || id.contains("nova")) return "💥";
        if (id.contains("feral")) return "🐾";
        
        return "⚔";
    }
    
    private String getPassiveIcon(OriginPassive passive) {
        String id = passive.getId().toLowerCase();
        
        if (id.contains("regen") || id.contains("heal")) return "💚";
        if (id.contains("resist") || id.contains("armor")) return "🛡";
        if (id.contains("speed") || id.contains("swift")) return "💨";
        if (id.contains("strength") || id.contains("power")) return "💪";
        if (id.contains("night") || id.contains("dark")) return "🌙";
        if (id.contains("sun") || id.contains("light") || id.contains("day")) return "☀";
        if (id.contains("water") || id.contains("swim")) return "🌊";
        if (id.contains("fire") || id.contains("heat")) return "🔥";
        if (id.contains("cold") || id.contains("frost")) return "❄";
        if (id.contains("undead") || id.contains("death")) return "💀";
        if (id.contains("blood")) return "🩸";
        if (id.contains("hunger") || id.contains("food")) return "🍖";
        
        return "◆";
    }
    
    private String getKeybindHint(int index) {
        return switch (index) {
            case 0 -> "R";
            case 1 -> "V";
            default -> String.valueOf(index + 1);
        };
    }
    
    private String formatCooldown(int ticks) {
        int seconds = ticks / 20;
        if (seconds >= 60) {
            return String.format("%dm%ds", seconds / 60, seconds % 60);
        }
        return seconds + "s";
    }
    
    private String formatAbilityName(String id) {
        String[] parts = id.split("_");
        StringBuilder result = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                result.append(Character.toUpperCase(part.charAt(0)))
                      .append(part.substring(1)).append(" ");
            }
        }
        return result.toString().trim();
    }
    
    private String formatPassiveName(String id) {
        return formatAbilityName(id);
    }
    
    private String getAbilityDescription(OriginAbility ability) {
        String id = ability.getId().toLowerCase();
        
        if (id.contains("flame_burst")) return "Release a burst of flames around you";
        if (id.contains("blood_drain")) return "Drain life from nearby enemies";
        if (id.contains("shadow_step")) return "Teleport through shadows";
        if (id.contains("raise_dead")) return "Summon undead minions to fight";
        if (id.contains("feral_charge")) return "Charge forward with primal fury";
        if (id.contains("nature_call")) return "Call upon nature's power";
        if (id.contains("frost_nova")) return "Freeze enemies in place";
        if (id.contains("possess")) return "Take control of a creature";
        if (id.contains("transform")) return "Transform into your beast form";
        if (id.contains("howl")) return "Let out a terrifying howl";
        
        return "Activate to unleash its power";
    }
    
    // ========== Input Handling ==========
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && hoveredAbility >= 0 && hoveredAbility < abilities.size()) {
            activateAbility(hoveredAbility);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Number keys 1-9
        if (keyCode >= 49 && keyCode <= 57) {
            int index = keyCode - 49;
            if (index < abilities.size()) {
                activateAbility(index);
                return true;
            }
        }
        
        // R for ability 1
        if (keyCode == 82 && abilities.size() > 0) {
            activateAbility(0);
            return true;
        }
        
        // V for ability 2
        if (keyCode == 86 && abilities.size() > 1) {
            activateAbility(1);
            return true;
        }
        
        // T for skill tree
        if (keyCode == 84) {
            Minecraft.getInstance().setScreen(new SkillTreeScreen(null));
            return true;
        }
        
        // Escape
        if (keyCode == 256) {
            onClose();
            return true;
        }
        
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    
    private void activateAbility(int index) {
        if (index < 0 || index >= abilities.size()) return;
        
        OriginAbility ability = abilities.get(index);
        
        if (ability.isOnCooldown()) {
            Minecraft.getInstance().player.displayClientMessage(
                Component.literal("Ability on cooldown: " + formatCooldown(ability.getCooldown()))
                    .withStyle(style -> style.withColor(0xFF5555)),
                true);
            return;
        }
        
        ModPackets.sendToServer(new ActivateAbilityPacket(index));
        VeilOrigins.LOGGER.debug("Activated ability {} at index {}", ability.getId(), index);
        onClose();
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
