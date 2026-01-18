package com.veilorigins.progression.skill;

import java.util.*;

/**
 * Holds the skill tree data for a specific origin.
 * Contains all skills, their connections, and branch information.
 */
public class SkillTreeData {
    
    private final String originId;
    private final String displayName;
    private final Map<String, Skill> skills = new LinkedHashMap<>();
    private final List<SkillConnection> connections = new ArrayList<>();
    
    // Tree dimensions for rendering
    private int minX = 0, maxX = 0, minY = 0, maxY = 0;
    
    public SkillTreeData(String originId, String displayName) {
        this.originId = originId;
        this.displayName = displayName;
    }
    
    public void addSkill(Skill skill) {
        skills.put(skill.getId(), skill);
        
        // Update bounds
        minX = Math.min(minX, skill.getGridX());
        maxX = Math.max(maxX, skill.getGridX());
        minY = Math.min(minY, skill.getGridY());
        maxY = Math.max(maxY, skill.getGridY());
        
        // Auto-create connections from prerequisites
        for (String prereqId : skill.getPrerequisites()) {
            connections.add(new SkillConnection(prereqId, skill.getId()));
        }
    }
    
    public Skill getSkill(String id) {
        return skills.get(id);
    }
    
    public Collection<Skill> getAllSkills() {
        return skills.values();
    }
    
    public List<SkillConnection> getConnections() {
        return connections;
    }
    
    public String getOriginId() { return originId; }
    public String getDisplayName() { return displayName; }
    public int getMinX() { return minX; }
    public int getMaxX() { return maxX; }
    public int getMinY() { return minY; }
    public int getMaxY() { return maxY; }
    public int getWidth() { return maxX - minX + 1; }
    public int getHeight() { return maxY - minY + 1; }
    
    /**
     * Get all skills that would be excluded if the given skill is unlocked.
     */
    public Set<String> getExcludedSkills(String skillId) {
        Skill skill = skills.get(skillId);
        if (skill == null) return Collections.emptySet();
        return new HashSet<>(skill.getExclusions());
    }
    
    /**
     * Get skills by branch.
     */
    public List<Skill> getSkillsByBranch(Skill.SkillBranch branch) {
        List<Skill> result = new ArrayList<>();
        for (Skill skill : skills.values()) {
            if (skill.getBranch() == branch) {
                result.add(skill);
            }
        }
        return result;
    }
    
    /**
     * Get skills by tier.
     */
    public List<Skill> getSkillsByTier(Skill.SkillTier tier) {
        List<Skill> result = new ArrayList<>();
        for (Skill skill : skills.values()) {
            if (skill.getTier() == tier) {
                result.add(skill);
            }
        }
        return result;
    }
    
    /**
     * Represents a connection between two skills in the tree.
     */
    public static class SkillConnection {
        private final String fromSkillId;
        private final String toSkillId;
        
        public SkillConnection(String from, String to) {
            this.fromSkillId = from;
            this.toSkillId = to;
        }
        
        public String getFromSkillId() { return fromSkillId; }
        public String getToSkillId() { return toSkillId; }
    }
}
