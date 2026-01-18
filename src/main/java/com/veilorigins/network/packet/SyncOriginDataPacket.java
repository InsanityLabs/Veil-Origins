package com.veilorigins.network.packet;

import com.veilorigins.VeilOrigins;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

/**
 * Packet sent from server to client to sync origin data.
 * Handler is registered via RegisterClientPayloadHandlersEvent on the client side.
 */
public record SyncOriginDataPacket(
        String originId,
        int level,
        int xp,
        float resourceBar,
        int skillPoints,
        String unlockedSkills) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncOriginDataPacket> TYPE = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(VeilOrigins.MOD_ID, "sync_origin_data"));

    public static final StreamCodec<ByteBuf, SyncOriginDataPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            SyncOriginDataPacket::originId,
            ByteBufCodecs.INT,
            SyncOriginDataPacket::level,
            ByteBufCodecs.INT,
            SyncOriginDataPacket::xp,
            ByteBufCodecs.FLOAT,
            SyncOriginDataPacket::resourceBar,
            ByteBufCodecs.INT,
            SyncOriginDataPacket::skillPoints,
            ByteBufCodecs.STRING_UTF8,
            SyncOriginDataPacket::unlockedSkills,
            SyncOriginDataPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
