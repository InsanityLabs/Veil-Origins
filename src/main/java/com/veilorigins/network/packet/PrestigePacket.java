package com.veilorigins.network.packet;

import com.veilorigins.VeilOrigins;
import com.veilorigins.progression.ProgressionSystem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Packet sent when player wants to prestige their origin.
 */
public record PrestigePacket() implements CustomPacketPayload {
    
    public static final CustomPacketPayload.Type<PrestigePacket> TYPE = 
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(VeilOrigins.MOD_ID, "prestige"));
    
    public static final StreamCodec<FriendlyByteBuf, PrestigePacket> STREAM_CODEC = 
        StreamCodec.unit(new PrestigePacket());
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public static void handle(PrestigePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (!(player instanceof ServerPlayer serverPlayer)) return;
            
            boolean success = ProgressionSystem.prestige(serverPlayer);
            VeilOrigins.LOGGER.info("Player {} prestige attempt: {}", 
                serverPlayer.getName().getString(), success ? "SUCCESS" : "FAILED");
        });
    }
}
