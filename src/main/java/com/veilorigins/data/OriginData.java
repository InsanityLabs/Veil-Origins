package com.veilorigins.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import com.veilorigins.VeilOrigins;

import java.util.*;
import java.util.function.Supplier;

public class OriginData {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = 
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, VeilOrigins.MOD_ID);

    // Codec for PlayerOriginData serialization
    public static final Codec<PlayerOriginData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.STRING.optionalFieldOf("originId", "").forGetter(d -> d.originId != null ? d.originId.toString() : ""),
            Codec.INT.optionalFieldOf("originLevel", 1).forGetter(PlayerOriginData::getOriginLevel),
            Codec.INT.optionalFieldOf("originXP", 0).forGetter(PlayerOriginData::getOriginXP),
            Codec.INT.optionalFieldOf("prestigeLevel", 0).forGetter(PlayerOriginData::getPrestigeLevel),
            Codec.FLOAT.optionalFieldOf("resourceBar", 100.0f).forGetter(PlayerOriginData::getResourceBar),
            Codec.INT.optionalFieldOf("skillPoints", 0).forGetter(PlayerOriginData::getSkillPoints),
            Codec.LONG.optionalFieldOf("totalXPEarned", 0L).forGetter(PlayerOriginData::getTotalXPEarned),
            Codec.STRING.optionalFieldOf("unlockedSkills", "").forGetter(d -> String.join(",", d.unlockedSkills))
        ).apply(instance, PlayerOriginData::fromCodec)
    );

    public static final Supplier<AttachmentType<PlayerOriginData>> PLAYER_ORIGIN = ATTACHMENT_TYPES.register(
        "player_origin", () -> AttachmentType.builder(PlayerOriginData::new)
            .serialize(CODEC)
            .copyOnDeath()
            .build()
    );

    public static PlayerOriginData get(net.minecraft.world.entity.player.Player player) {
        return player.getData(PLAYER_ORIGIN);
    }

    public static class PlayerOriginData {
        private ResourceLocation originId;
        private int originLevel = 1;
        private int originXP = 0;
        private int prestigeLevel = 0;
        private float resourceBar = 100.0f;
        private int skillPoints = 0;
        private long totalXPEarned = 0;
        private final Set<String> unlockedSkills = new HashSet<>();

        public PlayerOriginData() {}

        // Factory method for Codec deserialization
        public static PlayerOriginData fromCodec(String originIdStr, int level, int xp, int prestige, 
                                                  float resource, int skills, long totalXP, String skillsStr) {
            PlayerOriginData data = new PlayerOriginData();
            if (originIdStr != null && !originIdStr.isEmpty()) {
                data.originId = ResourceLocation.parse(originIdStr);
            }
            data.originLevel = level;
            data.originXP = xp;
            data.prestigeLevel = prestige;
            data.resourceBar = resource;
            data.skillPoints = skills;
            data.totalXPEarned = totalXP;
            
            if (skillsStr != null && !skillsStr.isEmpty()) {
                String[] skillArray = skillsStr.split(",");
                for (String skill : skillArray) {
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

        public ResourceLocation getOriginId() { return originId; }
        public void setOriginId(ResourceLocation id) { this.originId = id; }
        
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
