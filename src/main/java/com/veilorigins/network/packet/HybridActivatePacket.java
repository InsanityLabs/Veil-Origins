package com.veilorigins.network.packet;

import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.progression.HybridSystem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Packet sent when player wants to activate hybrid mode with a secondary origin.
 */
public record HybridActivatePacket(String secondaryOriginId) implements CustomPacketPayload {
    
    public static final CustomPacketPayload.Type<HybridActivatePacket> TYPE = 
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(VeilOrigins.MOD_ID, "hybrid_activate"));
    
    public static final StreamCodec<FriendlyByteBuf, HybridActivatePacket> STREAM_CODEC = 
        StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, HybridActivatePacket::secondaryOriginId,
            HybridActivatePacket::new
        );
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public static void handle(HybridActivatePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (!(player instanceof ServerPlayer serverPlayer)) return;
            
            Origin secondaryOrigin = VeilOriginsAPI.getOrigin(packet.secondaryOriginId);
            if (secondaryOrigin == null) {
                VeilOrigins.LOGGER.warn("Player {} tried to hybrid with unknown origin: {}", 
                    serverPlayer.getName().getString(), packet.secondaryOriginId);
                return;
            }
            
            boolean success = HybridSystem.activateHybrid(serverPlayer, secondaryOrigin);
            VeilOrigins.LOGGER.info("Player {} hybrid activation with {}: {}", 
                serverPlayer.getName().getString(), packet.secondaryOriginId, success ? "SUCCESS" : "FAILED");
        });
    }
}
