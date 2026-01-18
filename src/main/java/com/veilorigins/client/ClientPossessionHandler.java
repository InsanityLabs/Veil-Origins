package com.veilorigins.client;

import com.veilorigins.VeilOrigins;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;

/**
 * Client-side handler for possession state.
 * Player moves normally with WASD - the mob follows the player on the server.
 */
public class ClientPossessionHandler {
    
    // Client-side possession state
    private static boolean isPossessing = false;
    private static int possessedMobId = -1;
    
    /**
     * Update possession state from server sync packet.
     */
    public static void updatePossessionState(boolean possessing, int mobId) {
        isPossessing = possessing;
        possessedMobId = mobId;
        VeilOrigins.LOGGER.debug("Client possession state updated: possessing={}, mobId={}", possessing, mobId);
    }
    
    /**
     * Check if local player is possessing.
     */
    public static boolean isPossessing() {
        return isPossessing;
    }
    
    /**
     * Get the possessed mob entity on client.
     */
    public static Mob getPossessedMob() {
        if (!isPossessing || possessedMobId == -1) return null;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return null;
        
        Entity entity = mc.level.getEntity(possessedMobId);
        if (entity instanceof Mob mob) {
            return mob;
        }
        return null;
    }
    
    /**
     * Clear possession state (on disconnect, etc.)
     */
    public static void clear() {
        isPossessing = false;
        possessedMobId = -1;
    }
}
