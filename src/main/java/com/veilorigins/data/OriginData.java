package com.veilorigins.data;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import com.veilorigins.VeilOrigins;

import java.util.*;
import java.util.function.Supplier;

public class OriginData {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = 
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, VeilOrigins.MOD_ID);

    public static final Supplier<AttachmentType<PlayerOriginData>> PLAYER_ORIGIN = ATTACHMENT_TYPES.register(
        "player_origin", () -> AttachmentType.builder(PlayerOriginData::new)
            .serialize(new IAttachmentSerializer<PlayerOriginData>() {
                @Override
                public PlayerOriginData read(IAttachmentHolder holder, ValueInput input) {
                    PlayerOriginData data = new PlayerOriginData();
                    String originIdStr = input.getStringOr("originId", "");
                    if (originIdStr != null && !originIdStr.isEmpty()) {
                        data.originId = Identifier.parse(originIdStr);
                    }
                    data.originLevel = input.getIntOr("originLevel", 1);
                    data.originXP = input.getIntOr("originXP", 0);
                    data.prestigeLevel = input.getIntOr("prestigeLevel", 0);
                    data.resourceBar = input.getFloatOr("resourceBar", 100.0f);
                    data.skillPoints = input.getIntOr("skillPoints", 0);
                    data.totalXPEarned = input.getLongOr("totalXPEarned", 0L);
                    
                    // Read unlocked skills
                    String skillsStr = input.getStringOr("unlockedSkills", "");
                    if (skillsStr != null && !skillsStr.isEmpty()) {
                        String[] skills = skillsStr.split(",");
                        for (String skill : skills) {
                            String trimmed = skill.trim();
                            if (!trimmed.isEmpty()) {
                                data.unlockedSkills.add(trimmed);
                            }
                        }
                    }
                    
                    VeilOrigins.LOGGER.debug("Loaded origin data: origin={}, level={}, xp={}, skills={}", 
                        data.originId, data.originLevel, data.originXP, data.unlockedSkills);
                    
                    return data;
                }

                @Override
                public boolean write(PlayerOriginData data, ValueOutput output) {
                    if (data.originId != null) {
                        output.putString("originId", data.originId.toString());
                    } else {
                        output.putString("originId", "");
                    }
                    output.putInt("originLevel", data.originLevel);
                    output.putInt("originXP", data.originXP);
                    output.putInt("prestigeLevel", data.prestigeLevel);
                    output.putFloat("resourceBar", data.resourceBar);
                    output.putInt("skillPoints", data.skillPoints);
                    output.putLong("totalXPEarned", data.totalXPEarned);
                    
                    // Save unlocked skills as comma-separated string
                    String skillsStr = String.join(",", data.unlockedSkills);
                    output.putString("unlockedSkills", skillsStr);
                    
                    VeilOrigins.LOGGER.debug("Saved origin data: origin={}, level={}, xp={}, skills={}", 
                        data.originId, data.originLevel, data.originXP, skillsStr);
                    
                    return true;
                }
            })
            .copyOnDeath() // CRITICAL: Copy data when player dies/respawns
            .build()
    );

    public static PlayerOriginData get(net.minecraft.world.entity.player.Player player) {
        return player.getData(PLAYER_ORIGIN);
    }

    public static class PlayerOriginData {
        private Identifier originId;
        private int originLevel = 1;
        private int originXP = 0;
        private int prestigeLevel = 0;
        private float resourceBar = 100.0f;
        private int skillPoints = 0;
        private long totalXPEarned = 0;
        private final Set<String> unlockedSkills = new HashSet<>();

        public PlayerOriginData() {}

        public Identifier getOriginId() { return originId; }
        public void setOriginId(Identifier id) { this.originId = id; }
        
        public int getOriginLevel() { return originLevel; }
        public void setOriginLevel(int level) { this.originLevel = level; }
        
        public int getOriginXP() { return originXP; }
        public void setOriginXP(int xp) { this.originXP = xp; }
        
        public int getPrestigeLevel() { return prestigeLevel; }
        public void setPrestigeLevel(int level) { this.prestigeLevel = level; }
        
        public float getResourceBar() { return resourceBar; }
        public void setResourceBar(float value) { this.resourceBar = Math.max(0, Math.min(100, value)); }
        
        public int getSkillPoints() { return skillPoints; }
        public void setSkillPoints(int points) { this.skillPoints = points; }
        public void addSkillPoints(int points) { this.skillPoints += points; }
        public boolean spendSkillPoints(int cost) {
            if (skillPoints >= cost) {
                skillPoints -= cost;
                return true;
            }
            return false;
        }
        
        public long getTotalXPEarned() { return totalXPEarned; }
        
        public Set<String> getUnlockedSkills() { return Collections.unmodifiableSet(unlockedSkills); }
        public boolean hasSkill(String skillId) { return unlockedSkills.contains(skillId); }
        public void unlockSkill(String skillId) { 
            unlockedSkills.add(skillId);
            VeilOrigins.LOGGER.debug("Unlocked skill: {} (total: {})", skillId, unlockedSkills.size());
        }
        public void resetSkills() { 
            unlockedSkills.clear(); 
            VeilOrigins.LOGGER.debug("Reset all skills");
        }

        public void addXP(int amount) {
            this.originXP += amount;
            this.totalXPEarned += amount;
        }

        public void addResource(float amount) {
            setResourceBar(resourceBar + amount);
        }

        public void consumeResource(float amount) {
            setResourceBar(resourceBar - amount);
        }
    }
}
