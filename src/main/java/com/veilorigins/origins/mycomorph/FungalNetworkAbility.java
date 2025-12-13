package com.veilorigins.origins.mycomorph;

import com.veilorigins.api.OriginAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.*;

public class FungalNetworkAbility extends OriginAbility {
    private static final int COOLDOWN = 90;
    private static final int HUNGER_COST = 8;
    private static final int MAX_NODES = 5;

    // Per player -> List of tagged mushroom positions
    private final Map<UUID, List<BlockPos>> networkNodes = new HashMap<>();

    // Per player -> Is currently viewing through mushroom? (We can't easily
    // implement "Camera" view without client side mods)
    // Spec says "See through mushrooms".
    // Implementing Camera switching is client-side heavy (GameOptions.setCameraType
    // etc).
    // Alternative: Just teleport player there temporarily like Ender Pearl stasis
    // chamber or show glowing outlines?
    // "Telepathically communicate with players near mushrooms" -> Chat message
    // broadcast?

    // Simplified Implementation:
    // "Ping" network: Show chat info about players near nodes.
    // "Tag" function: If looking at mushroom, add to nodes.

    public FungalNetworkAbility() {
        super("fungal_network", COOLDOWN);
    }

    @Override
    public void onActivate(Player player, Level level) {
        if (level.isClientSide)
            return;

        // Mode 1: Tag Node (Check what player is looking at)
        // If sneaking, maybe clear nodes?
        // Let's make it contextual: If looking at mushroom -> Tag. If not -> Ping
        // Network.

        BlockPos lookedPos = getLookedBlock(player, 5.0);
        boolean isMushroom = false;
        if (lookedPos != null) {
            net.minecraft.world.level.block.state.BlockState state = level.getBlockState(lookedPos);
            if (state.is(Blocks.RED_MUSHROOM_BLOCK) || state.is(Blocks.BROWN_MUSHROOM_BLOCK) ||
                    state.is(Blocks.RED_MUSHROOM) || state.is(Blocks.BROWN_MUSHROOM)) {
                isMushroom = true;
            }
        }

        UUID id = player.getUUID();
        networkNodes.putIfAbsent(id, new ArrayList<>());
        List<BlockPos> nodes = networkNodes.get(id);

        if (isMushroom) {
            // Tagging
            if (nodes.contains(lookedPos)) {
                player.sendSystemMessage(Component.literal("§cNode already tagged!"));
            } else {
                if (nodes.size() >= MAX_NODES) {
                    nodes.remove(0); // Remove oldest
                }
                nodes.add(lookedPos);
                player.sendSystemMessage(
                        Component.literal("§aFungal Node tagged! (" + nodes.size() + "/" + MAX_NODES + ")"));
            }
            // Low cost for tagging?
        } else {
            // Pinging Network
            if (nodes.isEmpty()) {
                player.sendSystemMessage(Component.literal("§eNo nodes tagged. Look at a mushroom to tag it."));
                return;
            }

            player.causeFoodExhaustion(HUNGER_COST);
            player.sendSystemMessage(Component.literal("§2--- Fungal Network Status ---"));

            for (int i = 0; i < nodes.size(); i++) {
                BlockPos pos = nodes.get(i);
                // Check players near pos
                List<Player> nearby = level.getEntitiesOfClass(Player.class,
                        new net.minecraft.world.phys.AABB(pos).inflate(10));

                String status = nearby.isEmpty() ? "Silent" : nearby.size() + " beings detected";
                player.sendSystemMessage(
                        Component.literal("Node " + (i + 1) + " [" + pos.toShortString() + "]: " + status));

                for (Player p : nearby) {
                    if (p != player) {
                        p.sendSystemMessage(Component.literal("§dYou feel a fungal presence watching you..."));
                    }
                }
            }

            startCooldown();
        }
    }

    private BlockPos getLookedBlock(Player player, double range) {
        // Simple raycast helper
        net.minecraft.world.phys.HitResult result = player.pick(range, 0f, false);
        if (result.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) {
            return ((net.minecraft.world.phys.BlockHitResult) result).getBlockPos();
        }
        return null;
    }

    @Override
    public boolean canUse(Player player) {
        // Tagging is always free? Activating network costs hunger/cooldown.
        // We handle cooldown starts only on Ping.
        // We need to return true if not on cooldown OR if tagging.
        return true;
    }

    @Override
    public int getResourceCost() {
        return 0;
    }
}
