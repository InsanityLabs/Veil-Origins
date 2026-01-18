package com.veilorigins.progression.skill;

import java.util.*;

/**
 * Represents a skill node in the skill tree.
 * Skills can have effects, prerequisites, exclusions, and branch paths.
 */
public class Skill {
    
    public enum SkillBranch {
        // Core branches
        CORE,           // Core skills - foundation
        
        // Primary branches (pick your main focus)
        OFFENSE,        // Damage-focused skills
        DEFENSE,        // Tank/survival skills
        UTILITY,        // Movement, resource, cooldown skills
        
        // Elemental paths (mutually exclusive pairs)
        ELEMENTAL_A,    // First elemental path
        ELEMENTAL_B,    // Second elemental path - exclusive with A
        
        // Advanced branches (unlock at higher tiers)
        HYBRID,         // Mix of offense/defense - weaker but versatile
        SPECIALIST,     // Extreme focus - high risk/reward
        
        // Secret branches (hidden until prerequisites met)
        FORBIDDEN,      // Dark/corrupted skills - powerful but costly
        ASCENDED,       // Transcendent skills - requires sacrifice
        
        // Synergy branches (require specific combinations)
        SYNERGY_AD,     // Attack + Defense synergy
        SYNERGY_AU,     // Attack + Utility synergy  
        SYNERGY_DU      // Defense + Utility synergy
    }
    
    public enum SkillTier {
        TIER_1(1, 0),    // Available immediately
        TIER_2(2, 5),    // Requires level 5
        TIER_3(3, 15),   // Requires level 15
        TIER_4(4, 25),   // Requires level 25
        TIER_5(5, 40);   // Requires level 40
        
        private final int tier;
        private final int levelRequired;
        
        SkillTier(int tier, int levelRequired) {
            this.tier = tier;
            this.levelRequired = levelRequired;
        }
        
        public int getTier() { return tier; }
        public int getLevelRequired() { return levelRequired; }
    }
    
    private final String id;
    private final String name;
    private final String description;
    private final String icon; // Unicode icon or texture path
    private final SkillTier tier;
    private final SkillBranch branch;
    private final int skillPointCost;
    
    // Position in the tree (for rendering)
    private final int gridX;
    private final int gridY;
    
    // Requirements
    private final List<String> prerequisites;  // Skills that must be unlocked first
    private final List<String> exclusions;     // Skills that become locked if this is chosen
    
    // Effects when unlocked
    private final List<SkillEffect> effects;
    
    private Skill(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.icon = builder.icon;
        this.tier = builder.tier;
        this.branch = builder.branch;
        this.skillPointCost = builder.skillPointCost;
        this.gridX = builder.gridX;
        this.gridY = builder.gridY;
        this.prerequisites = new ArrayList<>(builder.prerequisites);
        this.exclusions = new ArrayList<>(builder.exclusions);
        this.effects = new ArrayList<>(builder.effects);
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getIcon() { return icon; }
    public SkillTier getTier() { return tier; }
    public SkillBranch getBranch() { return branch; }
    public int getSkillPointCost() { return skillPointCost; }
    public int getGridX() { return gridX; }
    public int getGridY() { return gridY; }
    public List<String> getPrerequisites() { return Collections.unmodifiableList(prerequisites); }
    public List<String> getExclusions() { return Collections.unmodifiableList(exclusions); }
    public List<SkillEffect> getEffects() { return Collections.unmodifiableList(effects); }
    
    /**
     * Check if this skill can be unlocked given current state.
     */
    public boolean canUnlock(Set<String> unlockedSkills, Set<String> excludedSkills, int playerLevel) {
        // Check level requirement
        if (playerLevel < tier.getLevelRequired()) return false;
        
        // Check if excluded by another skill
        if (excludedSkills.contains(id)) return false;
        
        // Check prerequisites
        for (String prereq : prerequisites) {
            if (!unlockedSkills.contains(prereq)) return false;
        }
        
        return true;
    }
    
    /**
     * Get the color for this skill's branch.
     */
    public int getBranchColor() {
        return switch (branch) {
            case CORE -> 0xFFCCCCCC;          // Light Gray - foundation
            case OFFENSE -> 0xFFFF4444;       // Bright Red - aggression
            case DEFENSE -> 0xFF4444FF;       // Bright Blue - protection
            case UTILITY -> 0xFF44FF44;       // Bright Green - versatility
            case ELEMENTAL_A -> 0xFFFF8800;   // Orange - elemental fire/light
            case ELEMENTAL_B -> 0xFF00CCFF;   // Cyan - elemental ice/dark
            case HYBRID -> 0xFFAA44AA;        // Purple - mixed
            case SPECIALIST -> 0xFFFFFF00;    // Yellow - focused
            case FORBIDDEN -> 0xFF880000;     // Dark Red - corrupted
            case ASCENDED -> 0xFFFFFFAA;      // Pale Gold - transcendent
            case SYNERGY_AD -> 0xFFFF44FF;    // Magenta - attack+defense
            case SYNERGY_AU -> 0xFFFFAA44;    // Gold - attack+utility
            case SYNERGY_DU -> 0xFF44FFFF;    // Aqua - defense+utility
        };
    }
    
    /**
     * Get the tier color.
     */
    public int getTierColor() {
        return switch (tier) {
            case TIER_1 -> 0xFF666666;    // Dark Gray - common
            case TIER_2 -> 0xFF44AA44;    // Green - uncommon
            case TIER_3 -> 0xFF4444DD;    // Blue - rare
            case TIER_4 -> 0xFFAA44AA;    // Purple - epic
            case TIER_5 -> 0xFFFFAA00;    // Gold - legendary
        };
    }
    
    /**
     * Check if this branch is a "dark" or forbidden branch.
     */
    public boolean isForbiddenBranch() {
        return branch == SkillBranch.FORBIDDEN;
    }
    
    /**
     * Check if this branch requires synergy (multiple other branches).
     */
    public boolean isSynergyBranch() {
        return branch == SkillBranch.SYNERGY_AD || 
               branch == SkillBranch.SYNERGY_AU || 
               branch == SkillBranch.SYNERGY_DU;
    }
    
    // Builder pattern
    public static Builder builder(String id) {
        return new Builder(id);
    }
    
    public static class Builder {
        private final String id;
        private String name = "Unnamed Skill";
        private String description = "";
        private String icon = "★";
        private SkillTier tier = SkillTier.TIER_1;
        private SkillBranch branch = SkillBranch.CORE;
        private int skillPointCost = 1;
        private int gridX = 0;
        private int gridY = 0;
        private final List<String> prerequisites = new ArrayList<>();
        private final List<String> exclusions = new ArrayList<>();
        private final List<SkillEffect> effects = new ArrayList<>();
        
        public Builder(String id) {
            this.id = id;
        }
        
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String desc) { this.description = desc; return this; }
        public Builder icon(String icon) { this.icon = icon; return this; }
        public Builder tier(SkillTier tier) { this.tier = tier; return this; }
        public Builder branch(SkillBranch branch) { this.branch = branch; return this; }
        public Builder cost(int cost) { this.skillPointCost = cost; return this; }
        public Builder position(int x, int y) { this.gridX = x; this.gridY = y; return this; }
        public Builder requires(String... prereqs) { 
            prerequisites.addAll(Arrays.asList(prereqs)); 
            return this; 
        }
        public Builder excludes(String... exclusions) { 
            this.exclusions.addAll(Arrays.asList(exclusions)); 
            return this; 
        }
        public Builder effect(SkillEffect effect) { 
            this.effects.add(effect); 
            return this; 
        }
        
        public Skill build() {
            return new Skill(this);
        }
    }
}
