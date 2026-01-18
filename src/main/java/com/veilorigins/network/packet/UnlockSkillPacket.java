package com.veilorigins.network.packet;

import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.data.OriginData;
import com.veilorigins.network.ModPackets;
import com.veilorigins.progression.skill.Skill;
import com.veilorigins.progression.skill.SkillTreeData;
import com.veilorigins.progression.skill.SkillTrees;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashSet;
import java.util.Set;

/**
 * Packet sent when player wants to unlock a skill in their skill tree.
 * Uses SkillTrees (skill package) which has the detailed skill definitions.
 */
public record UnlockSkillPacket(String skillId) implements CustomPacketPayload {
    
    public static final CustomPacketPayload.Type<UnlockSkillPacket> TYPE = 
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(VeilOrigins.MOD_ID, "unlock_skill"));
    
    public static final StreamCodec<FriendlyByteBuf, UnlockSkillPacket> STREAM_CODEC = 
        StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, UnlockSkillPacket::skillId,
            UnlockSkillPacket::new
        );
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public static void handle(UnlockSkillPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (!(player instanceof ServerPlayer serverPlayer)) return;
            
            // Get player's origin
            Origin origin = VeilOriginsAPI.getPlayerOrigin(serverPlayer);
            if (origin == null) {
                serverPlayer.displayClientMessage(
                    Component.literal("§cYou must have an origin to unlock skills!"), true);
                return;
            }
            
            // Get origin ID (strip namespace if present)
            String originId = origin.getId().getPath();
            
            // Get skill tree for origin from SkillTrees (the detailed one)
            SkillTreeData skillTree = SkillTrees.getTree(originId);
            if (skillTree == null) {
                VeilOrigins.LOGGER.warn("No skill tree found for origin: {} (available trees need to be initialized)", originId);
                serverPlayer.displayClientMessage(
                    Component.literal("§eNo skill tree available for this origin."), true);
                return;
            }
            
            // Get the skill
            Skill skill = skillTree.getSkill(packet.skillId);
            if (skill == null) {
                VeilOrigins.LOGGER.warn("Skill not found: {} in tree {} (tree has {} skills)", 
                    packet.skillId, originId, skillTree.getAllSkills().size());
                // Log available skills for debugging
                VeilOrigins.LOGGER.debug("Available skills in {}: {}", originId, 
                    skillTree.getAllSkills().stream().map(s -> s.getId()).toList());
                serverPlayer.displayClientMessage(
                    Component.literal("§cSkill not found!"), true);
                return;
            }
            
            // Get player data
            OriginData.PlayerOriginData data = OriginData.get(serverPlayer);
            
            // Check if already unlocked
            if (data.hasSkill(packet.skillId)) {
                serverPlayer.displayClientMessage(
                    Component.literal("§eYou already have this skill!"), true);
                return;
            }
            
            // Calculate excluded skills (from mutually exclusive choices already made)
            Set<String> excludedSkills = calculateExcludedSkills(skillTree, data.getUnlockedSkills());
            
            // Check if this skill is excluded
            if (excludedSkills.contains(packet.skillId)) {
                serverPlayer.displayClientMessage(
                    Component.literal("§cThis skill is locked due to a previous choice!"), true);
                return;
            }
            
            // Check if can unlock (prerequisites and level)
            if (!skill.canUnlock(data.getUnlockedSkills(), excludedSkills, data.getOriginLevel())) {
                serverPlayer.displayClientMessage(
                    Component.literal("§cYou don't meet the requirements for this skill!"), true);
                return;
            }
            
            // Check skill points
            int cost = skill.getSkillPointCost();
            if (data.getSkillPoints() < cost) {
                serverPlayer.displayClientMessage(
                    Component.literal("§cNot enough skill points! Need " + cost + ", have " + data.getSkillPoints()), true);
                return;
            }
            
            // Unlock the skill
            if (data.spendSkillPoints(cost)) {
                data.unlockSkill(packet.skillId);
                
                // Apply skill effects using the SkillEffectHandler (from skill package)
                com.veilorigins.progression.skill.SkillEffectHandler.applyAllSkillEffects(
                    serverPlayer, originId);
                
                serverPlayer.displayClientMessage(
                    Component.literal("§a✓ Unlocked: §e" + skill.getName()), false);
                
                // Sync updated data to client (including new skill)
                String skillsStr = String.join(",", data.getUnlockedSkills());
                SyncOriginDataPacket syncPacket = new SyncOriginDataPacket(
                    origin.getId().toString(),
                    data.getOriginLevel(),
                    data.getOriginXP(),
                    data.getResourceBar(),
                    data.getSkillPoints(),
                    skillsStr);
                ModPackets.sendToPlayer(serverPlayer, syncPacket);
                
                VeilOrigins.LOGGER.info("Player {} unlocked skill: {} (cost: {} points)", 
                    serverPlayer.getName().getString(), skill.getName(), cost);
            }
        });
    }
    
    /**
     * Calculate which skills are excluded based on already unlocked skills.
     */
    private static Set<String> calculateExcludedSkills(SkillTreeData tree, Set<String> unlockedSkills) {
        Set<String> excluded = new HashSet<>();
        for (String skillId : unlockedSkills) {
            Skill skill = tree.getSkill(skillId);
            if (skill != null) {
                excluded.addAll(skill.getExclusions());
            }
        }
        return excluded;
    }
}
