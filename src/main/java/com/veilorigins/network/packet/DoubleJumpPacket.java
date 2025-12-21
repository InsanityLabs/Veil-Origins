package com.veilorigins.network.packet;

import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.origins.vampire.VampiricDoubleJumpPassive;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Packet sent from client to server when a player attempts a double jump.
 * The server validates and executes the double jump if allowed.
 */
public record DoubleJumpPacket() implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<DoubleJumpPacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(VeilOrigins.MOD_ID, "double_jump"));

    public static final StreamCodec<ByteBuf, DoubleJumpPacket> CODEC = StreamCodec.unit(new DoubleJumpPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(DoubleJumpPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                Origin origin = VeilOriginsAPI.getPlayerOrigin(serverPlayer);
                if (origin == null)
                    return;

                // Check if player has the VampiricDoubleJumpPassive
                origin.getPassives().stream()
                        .filter(passive -> passive instanceof VampiricDoubleJumpPassive)
                        .findFirst()
                        .ifPresent(passive -> {
                            VampiricDoubleJumpPassive doubleJumpPassive = (VampiricDoubleJumpPassive) passive;
                            doubleJumpPassive.requestDoubleJump(serverPlayer);
                        });
            }
        });
    }
}
