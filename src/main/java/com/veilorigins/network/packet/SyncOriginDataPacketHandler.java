package com.veilorigins.network.packet;

import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.client.ClientOriginData;
import com.veilorigins.data.OriginData;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

/**
 * Client-side handler for SyncOriginDataPacket.
 * This class is separate to avoid loading client classes (Minecraft) on the server.
 */
public class SyncOriginDataPacketHandler {

    public static void handleClient(SyncOriginDataPacket packet) {
        // Client-side handling - update local origin cache
        VeilOrigins.LOGGER.debug("Received origin data sync: {}", packet.originId());

        // Get the local player
        Player player = Minecraft.getInstance().player;
        if (player == null) {
            VeilOrigins.LOGGER.warn("Received origin sync but local player is null");
            return;
        }

        // Look up the origin by ID
        if (packet.originId() == null || packet.originId().isEmpty()) {
            // Player has no origin - clear it
            VeilOriginsAPI.setPlayerOriginClient(player, null);
            ClientOriginData.clear();
            VeilOrigins.LOGGER.info("Cleared local player's origin");
            return;
        }

        Origin origin = VeilOriginsAPI.getOrigin(packet.originId());
        if (origin == null) {
            VeilOrigins.LOGGER.warn("Received sync for unknown origin: {}", packet.originId());
            return;
        }

        // Set the origin on the client (without triggering passives - they run on server)
        VeilOriginsAPI.setPlayerOriginClient(player, origin);

        // Update the CLIENT-SIDE cache for HUD display (including unlocked skills)
        ClientOriginData.updateFromSync(origin, packet.level(), packet.xp(), packet.resourceBar(), 
                packet.skillPoints(), packet.unlockedSkills());

        // Also update the player's origin data attachment for compatibility
        OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);
        data.setOriginLevel(packet.level());
        data.setOriginXP(packet.xp());
        data.setResourceBar(packet.resourceBar());
        data.setSkillPoints(packet.skillPoints());
        
        // Sync unlocked skills to local attachment
        data.resetSkills();
        if (packet.unlockedSkills() != null && !packet.unlockedSkills().isEmpty()) {
            String[] skills = packet.unlockedSkills().split(",");
            for (String skill : skills) {
                String trimmed = skill.trim();
                if (!trimmed.isEmpty()) {
                    data.unlockSkill(trimmed);
                }
            }
        }

        VeilOrigins.LOGGER.info("Synced origin {} for local player (level={}, xp={}, resource={}, skillPoints={}, skills={})",
                packet.originId(), packet.level(), packet.xp(), packet.resourceBar(), packet.skillPoints(), packet.unlockedSkills());
    }
    
    /**
     * Handle cooldown sync packet.
     */
    public static void handleCooldownSync(SyncCooldownsPacket packet) {
        for (var entry : packet.cooldowns().entrySet()) {
            ClientOriginData.updateAbilityCooldown(
                entry.getKey(), 
                entry.getValue().remaining(), 
                entry.getValue().max()
            );
        }
    }
}
