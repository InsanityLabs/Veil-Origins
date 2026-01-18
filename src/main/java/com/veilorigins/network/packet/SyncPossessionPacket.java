package com.veilorigins.network.packet;

import com.veilorigins.VeilOrigins;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/**
 * Packet sent from server to client to sync possession state.
 * Tells client whether they are possessing and the mob's entity ID.
 */
public record SyncPossessionPacket(boolean isPossessing, int mobEntityId) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncPossessionPacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(VeilOrigins.MOD_ID, "sync_possession"));

    public static final StreamCodec<ByteBuf, SyncPossessionPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            SyncPossessionPacket::isPossessing,
            ByteBufCodecs.INT,
            SyncPossessionPacket::mobEntityId,
            SyncPossessionPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
