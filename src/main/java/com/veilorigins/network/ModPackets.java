package com.veilorigins.network;

import com.veilorigins.VeilOrigins;
import com.veilorigins.network.packet.ActivateAbilityPacket;
import com.veilorigins.network.packet.DoubleJumpPacket;
import com.veilorigins.network.packet.SelectOriginPacket;
import com.veilorigins.network.packet.SyncOriginDataPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModPackets {

        public static void register(RegisterPayloadHandlersEvent event) {
                PayloadRegistrar registrar = event.registrar(VeilOrigins.MOD_ID)
                                .versioned("1.0.0");

                registrar.playToServer(
                                ActivateAbilityPacket.TYPE,
                                ActivateAbilityPacket.CODEC,
                                ActivateAbilityPacket::handle);

                registrar.playToServer(
                                SelectOriginPacket.TYPE,
                                SelectOriginPacket.STREAM_CODEC,
                                SelectOriginPacket::handle);

                registrar.playToServer(
                                DoubleJumpPacket.TYPE,
                                DoubleJumpPacket.CODEC,
                                DoubleJumpPacket::handle);

                registrar.playToClient(
                                SyncOriginDataPacket.TYPE,
                                SyncOriginDataPacket.CODEC,
                                SyncOriginDataPacket::handle);
        }

        public static <T extends CustomPacketPayload> void sendToServer(T packet) {
                net.minecraft.client.Minecraft.getInstance().getConnection().send(packet);
        }

        public static <T extends CustomPacketPayload> void sendToPlayer(ServerPlayer player, T packet) {
                player.connection.send(packet);
        }
}
