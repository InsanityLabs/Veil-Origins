package com.veilorigins.network.packet;

import com.veilorigins.VeilOrigins;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncOriginDataPacket(
    String originId,
    int level,
    int xp,
    float resourceBar
) implements CustomPacketPayload {
    
    public static final CustomPacketPayload.Type<SyncOriginDataPacket> TYPE = 
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(VeilOrigins.MOD_ID, "sync_origin_data"));
    
    public static final StreamCodec<ByteBuf, SyncOriginDataPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        SyncOriginDataPacket::originId,
        ByteBufCodecs.INT,
        SyncOriginDataPacket::level,
        ByteBufCodecs.INT,
        SyncOriginDataPacket::xp,
        ByteBufCodecs.FLOAT,
        SyncOriginDataPacket::resourceBar,
        SyncOriginDataPacket::new
    );
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public static void handle(SyncOriginDataPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            // Client-side handling - update local cache
            VeilOrigins.LOGGER.debug("Received origin data sync: {}", packet.originId);
        });
    }
}
