package com.veilorigins.progression;

import net.minecraft.resources.Identifier;
import java.util.*;

/**
 * Represents a skill tree for an origin with unlockable talents.
 */
public class SkillTree {
    private final Identifier originId;
    private final List<SkillNode> nodes;
    private final Map<String, SkillNode> nodeMap;
    
    public SkillTree(Identifier originId) {
        this.originId = originId;
        this.nodes = new ArrayList<>();
        this.nodeMap = new HashMap<>();
    }
    
    public void addNode(SkillNode node) {
        nodes.add(node);
        nodeMap.put(node.getId(), node);
    }
    
    public SkillNode getNode(String id) {
        return nodeMap.get(id);
    }
    
    public List<SkillNode> getNodes() {
        return Collections.unmodifiableList(nodes);
    }
    
    public List<SkillNode> getAvailableNodes(Set<String> unlockedSkills, int playerLevel) {
        List<SkillNode> available = new ArrayList<>();
        for (SkillNode node : nodes) {
            if (!unlockedSkills.contains(node.getId()) && 
                node.canUnlock(unlockedSkills, playerLevel)) {
                available.add(node);
            }
        }
        return available;
    }
    
    public Identifier getOriginId() {
        return originId;
    }
    
    /**
     * Represents a single node in the skill tree.
     */
    public static class SkillNode {
        private final String id;
        private final String name;
        private final String description;
        private final int requiredLevel;
        private final int skillPointCost;
        private final List<String> prerequisites;
        private final SkillEffect effect;
        private final SkillTier tier;
        
        public SkillNode(String id, String name, String description, int requiredLevel, 
                        int skillPointCost, List<String> prerequisites, SkillEffect effect, SkillTier tier) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.requiredLevel = requiredLevel;
            this.skillPointCost = skillPointCost;
            this.prerequisites = prerequisites != null ? prerequisites : new ArrayList<>();
            this.effect = effect;
            this.tier = tier;
        }
        
        public boolean canUnlock(Set<String> unlockedSkills, int playerLevel) {
            if (playerLevel < requiredLevel) return false;
            for (String prereq : prerequisites) {
                if (!unlockedSkills.contains(prereq)) return false;
            }
            return true;
        }
        
        public String getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public int getRequiredLevel() { return requiredLevel; }
        public int getSkillPointCost() { return skillPointCost; }
        public List<String> getPrerequisites() { return prerequisites; }
        public SkillEffect getEffect() { return effect; }
        public SkillTier getTier() { return tier; }
    }
    
    /**
     * Skill tiers for visual organization.
     */
    public enum SkillTier {
        BASIC(1),      // Levels 1-10
        ADVANCED(2),   // Levels 11-25
        EXPERT(3),     // Levels 26-40
        MASTER(4);     // Levels 41-50
        
        private final int tier;
        SkillTier(int tier) { this.tier = tier; }
        public int getTier() { return tier; }
    }
    
    /**
     * Interface for skill effects.
     */
    @FunctionalInterface
    public interface SkillEffect {
        void apply(net.minecraft.world.entity.player.Player player);
    }
}
