package com.veilorigins.api;

import com.veilorigins.VeilOrigins;
import com.veilorigins.data.OriginData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import java.util.HashMap;
import java.util.Map;

public class VeilOriginsAPI {
    private static final Map<ResourceLocation, Origin> ORIGINS = new HashMap<>();
    // Runtime cache - uses UUID as key to survive player entity replacement on respawn
    private static final Map<java.util.UUID, Origin> PLAYER_ORIGINS = new HashMap<>();

    public static void registerOrigin(Origin origin) {
        ORIGINS.put(origin.getId(), origin);
    }

    public static Origin getOrigin(String id) {
        return ORIGINS.get(ResourceLocation.parse(id));
    }

    public static Origin getOrigin(ResourceLocation id) {
        return ORIGINS.get(id);
    }

    /**
     * Get the player's origin from cache, loading from persistent data if needed
     */
    public static Origin getPlayerOrigin(Player player) {
        // First check cache using UUID
        Origin cached = PLAYER_ORIGINS.get(player.getUUID());
        if (cached != null) {
            return cached;
        }

        // Try to load from persistent data
        OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);
        if (data != null && data.getOriginId() != null) {
            Origin origin = ORIGINS.get(data.getOriginId());
            if (origin != null) {
                // Cache it but don't trigger onEquip (handled separately on login)
                PLAYER_ORIGINS.put(player.getUUID(), origin);
                return origin;
            }
        }

        return null;
    }

    /**
     * Set the player's origin, updating both cache and persistent data
     */
    public static void setPlayerOrigin(Player player, Origin origin) {
        Origin oldOrigin = PLAYER_ORIGINS.get(player.getUUID());

        // Remove old origin effects
        if (oldOrigin != null) {
            try {
                oldOrigin.getPassives().forEach(passive -> {
                    try {
                        passive.onRemove(player);
                    } catch (Exception e) {
                        VeilOrigins.LOGGER.error("Error removing passive {}: {}", passive.getId(), e.getMessage());
                    }
                });
            } catch (Exception e) {
                VeilOrigins.LOGGER.error("Error removing old origin passives: {}", e.getMessage());
            }
            // Remove old skill effects
            com.veilorigins.progression.skill.SkillEffectHandler.removeAllSkillEffects(player);
            // Remove origin scale
            com.veilorigins.event.OriginSizeHandler.removeOriginScale(player);
        }

        // Update cache using UUID
        if (origin != null) {
            PLAYER_ORIGINS.put(player.getUUID(), origin);
        } else {
            PLAYER_ORIGINS.remove(player.getUUID());
        }

        // Persist to data attachment
        OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);
        data.setOriginId(origin != null ? origin.getId() : null);

        // Apply new origin effects
        if (origin != null) {
            origin.getPassives().forEach(passive -> {
                try {
                    passive.onEquip(player);
                } catch (Exception e) {
                    VeilOrigins.LOGGER.error("Error applying passive {}: {}", passive.getId(), e.getMessage());
                }
            });
            // Apply skill effects for the new origin
            com.veilorigins.progression.skill.SkillEffectHandler.applyAllSkillEffects(
                player, origin.getId().getPath());
            VeilOrigins.LOGGER.info("Set origin {} for player {}", origin.getId(), player.getName().getString());
        } else {
            VeilOrigins.LOGGER.info("Cleared origin for player {}", player.getName().getString());
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
            PLAYER_ORIGINS.put(player.getUUID(), origin);
            VeilOrigins.LOGGER.debug("Client: Set origin {} for player {}", origin.getId(),
                    player.getName().getString());
        } else {
            PLAYER_ORIGINS.remove(player.getUUID());
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
                // Check if already cached (avoid double-loading)
                if (PLAYER_ORIGINS.containsKey(player.getUUID())) {
                    VeilOrigins.LOGGER.debug("Origin already loaded for player {}, skipping", player.getName().getString());
                    return;
                }
                
                // Cache the origin using UUID
                PLAYER_ORIGINS.put(player.getUUID(), origin);
                // Apply passive effects with error handling
                origin.getPassives().forEach(passive -> {
                    try {
                        passive.onEquip(player);
                    } catch (Exception e) {
                        VeilOrigins.LOGGER.error("Error applying passive {} for player {}: {}", 
                            passive.getId(), player.getName().getString(), e.getMessage());
                    }
                });
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
        Origin origin = PLAYER_ORIGINS.remove(player.getUUID());
        if (origin != null) {
            origin.getPassives().forEach(passive -> passive.onRemove(player));
        }
    }

    /**
     * Clear all client-side cached data.
     * Called when disconnecting from a server to prevent data bleeding between servers.
     */
    public static void clearClientCache() {
        PLAYER_ORIGINS.clear();
        VeilOrigins.LOGGER.debug("Cleared all client-side origin cache");
    }

    /**
     * Check if a player has an origin set
     */
    public static boolean hasOrigin(Player player) {
        return getPlayerOrigin(player) != null;
    }

    public static Map<ResourceLocation, Origin> getAllOrigins() {
        return new HashMap<>(ORIGINS);
    }
}
