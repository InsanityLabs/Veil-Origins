package com.veilorigins.client;

import com.veilorigins.api.Origin;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Client-side cache for origin data synced from server.
 * This ensures HUD displays accurate values even when data attachments don't sync properly.
 */
public class ClientOriginData {
    
    // Cached origin data from server
    private static Origin currentOrigin = null;
    private static int level = 1;
    private static int xp = 0;
    private static float resourceBar = 100.0f;
    private static int skillPoints = 0;
    
    // Unlocked skills (synced from server)
    private static final Set<String> unlockedSkills = new HashSet<>();
    
    // Ability cooldowns (ability index -> remaining ticks)
    private static final Map<Integer, Integer> abilityCooldowns = new HashMap<>();
    
    // Max cooldowns for calculating percentage
    private static final Map<Integer, Integer> abilityMaxCooldowns = new HashMap<>();
    
    /**
     * Update all origin data from sync packet.
     */
    public static void updateFromSync(Origin origin, int newLevel, int newXp, float newResource, int newSkillPoints) {
        currentOrigin = origin;
        level = newLevel;
        xp = newXp;
        resourceBar = newResource;
        skillPoints = newSkillPoints;
    }
    
    /**
     * Update all origin data from sync packet including unlocked skills.
     */
    public static void updateFromSync(Origin origin, int newLevel, int newXp, float newResource, int newSkillPoints, String skillsStr) {
        currentOrigin = origin;
        level = newLevel;
        xp = newXp;
        resourceBar = newResource;
        skillPoints = newSkillPoints;
        
        // Parse and update unlocked skills
        unlockedSkills.clear();
        if (skillsStr != null && !skillsStr.isEmpty()) {
            String[] skills = skillsStr.split(",");
            for (String skill : skills) {
                String trimmed = skill.trim();
                if (!trimmed.isEmpty()) {
                    unlockedSkills.add(trimmed);
                }
            }
        }
    }
    
    /**
     * Update ability cooldown from sync packet.
     */
    public static void updateAbilityCooldown(int abilityIndex, int cooldownTicks, int maxCooldownTicks) {
        abilityCooldowns.put(abilityIndex, cooldownTicks);
        if (maxCooldownTicks > 0) {
            abilityMaxCooldowns.put(abilityIndex, maxCooldownTicks);
        }
    }
    
    /**
     * Clear all data (on disconnect or origin reset).
     */
    public static void clear() {
        currentOrigin = null;
        level = 1;
        xp = 0;
        resourceBar = 100.0f;
        skillPoints = 0;
        unlockedSkills.clear();
        abilityCooldowns.clear();
        abilityMaxCooldowns.clear();
    }
    
    // Getters
    public static Origin getOrigin() { return currentOrigin; }
    public static int getLevel() { return level; }
    public static int getXp() { return xp; }
    public static float getResourceBar() { return resourceBar; }
    public static int getSkillPoints() { return skillPoints; }
    public static Set<String> getUnlockedSkills() { return Collections.unmodifiableSet(unlockedSkills); }
    public static boolean hasSkill(String skillId) { return unlockedSkills.contains(skillId); }
    
    public static int getAbilityCooldown(int index) {
        return abilityCooldowns.getOrDefault(index, 0);
    }
    
    public static int getAbilityMaxCooldown(int index) {
        return abilityMaxCooldowns.getOrDefault(index, 1);
    }
    
    public static float getAbilityCooldownPercent(int index) {
        int cooldown = getAbilityCooldown(index);
        int maxCooldown = getAbilityMaxCooldown(index);
        if (maxCooldown <= 0) return 0;
        return (float) cooldown / maxCooldown;
    }
    
    /**
     * Tick down cooldowns client-side for smooth display.
     * Called every client tick.
     */
    public static void tickCooldowns() {
        abilityCooldowns.replaceAll((k, v) -> Math.max(0, v - 1));
    }
    
    /**
     * Get XP required for next level.
     */
    public static int getXpForNextLevel() {
        return com.veilorigins.progression.ProgressionSystem.getXPForLevel(level + 1);
    }
    
    /**
     * Get XP progress within current level (0.0 to 1.0).
     */
    public static float getXpProgress() {
        int currentLevelXp = com.veilorigins.progression.ProgressionSystem.getTotalXPForLevel(level);
        int nextLevelXp = com.veilorigins.progression.ProgressionSystem.getTotalXPForLevel(level + 1);
        int xpInLevel = xp - currentLevelXp;
        int xpNeeded = nextLevelXp - currentLevelXp;
        if (xpNeeded <= 0) return 1.0f;
        return Math.min(1.0f, Math.max(0.0f, (float) xpInLevel / xpNeeded));
    }
}
