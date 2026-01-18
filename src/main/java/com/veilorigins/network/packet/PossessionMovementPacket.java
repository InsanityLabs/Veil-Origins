package com.veilorigins.network.packet;

import com.veilorigins.VeilOrigins;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Packet sent from client to server to sync player movement input during possession.
 * NOTE: This packet is no longer actively used - possession now works by having
 * the mob follow the player's position directly. Kept for backwards compatibility.
 */
public record PossessionMovementPacket(float forward, float strafe, float yaw, boolean jump) implements CustomPacketPayload {
    
    public static final CustomPacketPayload.Type<PossessionMovementPacket> TYPE = 
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(VeilOrigins.MOD_ID, "possession_movement"));
    
    public static final StreamCodec<FriendlyByteBuf, PossessionMovementPacket> STREAM_CODEC = 
            StreamCodec.of(PossessionMovementPacket::encode, PossessionMovementPacket::decode);
    
    private static void encode(FriendlyByteBuf buf, PossessionMovementPacket packet) {
        buf.writeFloat(packet.forward);
        buf.writeFloat(packet.strafe);
        buf.writeFloat(packet.yaw);
        buf.writeBoolean(packet.jump);
    }
    
    private static PossessionMovementPacket decode(FriendlyByteBuf buf) {
        return new PossessionMovementPacket(
                buf.readFloat(),
                buf.readFloat(),
                buf.readFloat(),
                buf.readBoolean()
        );
    }
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public static void handle(PossessionMovementPacket packet, IPayloadContext context) {
        // No longer used - possession now works by mob following player position
        // Kept for backwards compatibility with any old clients
    }
}
