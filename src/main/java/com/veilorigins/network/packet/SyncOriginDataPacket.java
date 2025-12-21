package com.veilorigins.network.packet;

import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.data.OriginData;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncOriginDataPacket(
        String originId,
        int level,
        int xp,
        float resourceBar) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncOriginDataPacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(VeilOrigins.MOD_ID, "sync_origin_data"));

    public static final StreamCodec<ByteBuf, SyncOriginDataPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SyncOriginDataPacket::originId,
            ByteBufCodecs.INT,
            SyncOriginDataPacket::level,
            ByteBufCodecs.INT,
            SyncOriginDataPacket::xp,
            ByteBufCodecs.FLOAT,
            SyncOriginDataPacket::resourceBar,
            SyncOriginDataPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncOriginDataPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            // Client-side handling - update local origin cache
            VeilOrigins.LOGGER.debug("Received origin data sync: {}", packet.originId);

            // Get the local player
            Player player = Minecraft.getInstance().player;
            if (player == null) {
                VeilOrigins.LOGGER.warn("Received origin sync but local player is null");
                return;
            }

            // Look up the origin by ID
            if (packet.originId == null || packet.originId.isEmpty()) {
                // Player has no origin - clear it
                VeilOriginsAPI.setPlayerOriginClient(player, null);
                VeilOrigins.LOGGER.info("Cleared local player's origin");
                return;
            }

            Origin origin = VeilOriginsAPI.getOrigin(packet.originId);
            if (origin == null) {
                VeilOrigins.LOGGER.warn("Received sync for unknown origin: {}", packet.originId);
                return;
            }

            // Set the origin on the client (without triggering passives - they run on
            // server)
            VeilOriginsAPI.setPlayerOriginClient(player, origin);

            // Update the player's origin data for display purposes
            OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);
            data.setOriginLevel(packet.level);
            data.setOriginXP(packet.xp);
            data.setResourceBar(packet.resourceBar);

            VeilOrigins.LOGGER.info("Synced origin {} for local player (level={}, xp={}, resource={})",
                    packet.originId, packet.level, packet.xp, packet.resourceBar);
        });
    }
}
