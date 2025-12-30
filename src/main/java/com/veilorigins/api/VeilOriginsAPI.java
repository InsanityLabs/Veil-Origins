package com.veilorigins.api;

import com.veilorigins.VeilOrigins;
import com.veilorigins.data.OriginData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.Identifier;
import java.util.HashMap;
import java.util.Map;

public class VeilOriginsAPI {
    private static final Map<Identifier, Origin> ORIGINS = new HashMap<>();
    // Runtime cache - repopulated from persistent data on login
    private static final Map<Player, Origin> PLAYER_ORIGINS = new HashMap<>();

    public static void registerOrigin(Origin origin) {
        ORIGINS.put(origin.getId(), origin);
    }

    public static Origin getOrigin(String id) {
        return ORIGINS.get(Identifier.parse(id));
    }

    public static Origin getOrigin(Identifier id) {
        return ORIGINS.get(id);
    }

    /**
     * Get the player's origin from cache, loading from persistent data if needed
     */
    public static Origin getPlayerOrigin(Player player) {
        // First check cache
        Origin cached = PLAYER_ORIGINS.get(player);
        if (cached != null) {
            return cached;
        }

        // Try to load from persistent data
        OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);
        if (data != null && data.getOriginId() != null) {
            Origin origin = ORIGINS.get(data.getOriginId());
            if (origin != null) {
                // Cache it but don't trigger onEquip (handled separately on login)
                PLAYER_ORIGINS.put(player, origin);
                return origin;
            }
        }

        return null;
    }

    /**
     * Set the player's origin, updating both cache and persistent data
     */
    public static void setPlayerOrigin(Player player, Origin origin) {
        Origin oldOrigin = PLAYER_ORIGINS.get(player);

        // Remove old origin effects
        if (oldOrigin != null) {
            oldOrigin.getPassives().forEach(passive -> passive.onRemove(player));
        }

        // Update cache
        if (origin != null) {
            PLAYER_ORIGINS.put(player, origin);
        } else {
            PLAYER_ORIGINS.remove(player);
        }

        // Persist to data attachment
        OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);
        data.setOriginId(origin != null ? origin.getId() : null);

        // Apply new origin effects
        if (origin != null) {
            origin.getPassives().forEach(passive -> passive.onEquip(player));
            VeilOrigins.LOGGER.info("Set origin {} for player {}", origin.getId(), player.getName().getString());
        }
    }

    /**
     * Client-side only: Set the player's origin in the local cache without
     * triggering passives.
     * This is used when receiving origin sync packets from the server.
     * Passives are handled on the server side, so we only need to update the cache
     * for client UI/abilities.
     */
    public static void setPlayerOriginClient(Player player, Origin origin) {
        if (origin != null) {
            PLAYER_ORIGINS.put(player, origin);
            VeilOrigins.LOGGER.debug("Client: Set origin {} for player {}", origin.getId(),
                    player.getName().getString());
        } else {
            PLAYER_ORIGINS.remove(player);
            VeilOrigins.LOGGER.debug("Client: Cleared origin for player {}", player.getName().getString());
        }
    }

    /**
     * Load origin from persistent data when player joins
     * Called from event handler on login
     */
    public static void loadPlayerOrigin(Player player) {
        OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);
        if (data != null && data.getOriginId() != null) {
            Origin origin = ORIGINS.get(data.getOriginId());
            if (origin != null) {
                // Cache the origin
                PLAYER_ORIGINS.put(player, origin);
                // Apply passive effects
                origin.getPassives().forEach(passive -> passive.onEquip(player));
                VeilOrigins.LOGGER.info("Loaded origin {} for player {}", origin.getId(), player.getName().getString());
            } else {
                VeilOrigins.LOGGER.warn("Player {} has unknown origin ID: {}", player.getName().getString(),
                        data.getOriginId());
            }
        }
    }

    /**
     * Clean up player from cache on logout
     */
    public static void unloadPlayer(Player player) {
        Origin origin = PLAYER_ORIGINS.remove(player);
        if (origin != null) {
            origin.getPassives().forEach(passive -> passive.onRemove(player));
        }
    }

    /**
     * Check if a player has an origin set
     */
    public static boolean hasOrigin(Player player) {
        return getPlayerOrigin(player) != null;
    }

    public static Map<Identifier, Origin> getAllOrigins() {
        return new HashMap<>(ORIGINS);
    }
}
