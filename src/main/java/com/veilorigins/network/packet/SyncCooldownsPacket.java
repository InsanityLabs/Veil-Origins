package com.veilorigins.network.packet;

import com.veilorigins.VeilOrigins;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Packet sent from server to client to sync ability cooldowns.
 */
public record SyncCooldownsPacket(Map<Integer, CooldownData> cooldowns) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SyncCooldownsPacket> TYPE = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(VeilOrigins.MOD_ID, "sync_cooldowns"));

    public static final StreamCodec<ByteBuf, SyncCooldownsPacket> STREAM_CODEC = StreamCodec.of(
            SyncCooldownsPacket::encode,
            SyncCooldownsPacket::decode
    );
    
    public record CooldownData(int remaining, int max) {}

    private static void encode(ByteBuf buf, SyncCooldownsPacket packet) {
        buf.writeInt(packet.cooldowns.size());
        for (Map.Entry<Integer, CooldownData> entry : packet.cooldowns.entrySet()) {
            buf.writeInt(entry.getKey());
            buf.writeInt(entry.getValue().remaining());
            buf.writeInt(entry.getValue().max());
        }
    }

    private static SyncCooldownsPacket decode(ByteBuf buf) {
        int size = buf.readInt();
        Map<Integer, CooldownData> cooldowns = new HashMap<>();
        for (int i = 0; i < size; i++) {
            int index = buf.readInt();
            int remaining = buf.readInt();
            int max = buf.readInt();
            cooldowns.put(index, new CooldownData(remaining, max));
        }
        return new SyncCooldownsPacket(cooldowns);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
