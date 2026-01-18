package com.veilorigins.network;

import com.veilorigins.VeilOrigins;
import com.veilorigins.network.packet.ActivateAbilityPacket;
import com.veilorigins.network.packet.DoubleJumpPacket;
import com.veilorigins.network.packet.HybridActivatePacket;
import com.veilorigins.network.packet.LegendaryAbilityPacket;
import com.veilorigins.network.packet.PossessionMovementPacket;
import com.veilorigins.network.packet.PrestigePacket;
import com.veilorigins.network.packet.SelectOriginPacket;
import com.veilorigins.network.packet.SyncCooldownsPacket;
import com.veilorigins.network.packet.SyncOriginDataPacket;
import com.veilorigins.network.packet.SyncPossessionPacket;
import com.veilorigins.network.packet.UnlockSkillPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
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

                // Progression system packets
                registrar.playToServer(
                                LegendaryAbilityPacket.TYPE,
                                LegendaryAbilityPacket.STREAM_CODEC,
                                LegendaryAbilityPacket::handle);

                registrar.playToServer(
                                PrestigePacket.TYPE,
                                PrestigePacket.STREAM_CODEC,
                                PrestigePacket::handle);

                registrar.playToServer(
                                HybridActivatePacket.TYPE,
                                HybridActivatePacket.STREAM_CODEC,
                                HybridActivatePacket::handle);

                registrar.playToServer(
                                UnlockSkillPacket.TYPE,
                                UnlockSkillPacket.STREAM_CODEC,
                                UnlockSkillPacket::handle);

                // Possession movement packet - syncs WASD input from client to server
                registrar.playToServer(
                                PossessionMovementPacket.TYPE,
                                PossessionMovementPacket.STREAM_CODEC,
                                PossessionMovementPacket::handle);

                // Register client-bound packets
                registrar.playToClient(
                                SyncOriginDataPacket.TYPE,
                                SyncOriginDataPacket.CODEC,
                                ModPackets::handleSyncOriginData);

                registrar.playToClient(
                                SyncCooldownsPacket.TYPE,
                                SyncCooldownsPacket.STREAM_CODEC,
                                ModPackets::handleSyncCooldowns);

                registrar.playToClient(
                                SyncPossessionPacket.TYPE,
                                SyncPossessionPacket.STREAM_CODEC,
                                ModPackets::handleSyncPossession);
        }

        /**
         * Handler for SyncOriginDataPacket - delegates to client-only code via reflection
         * to avoid loading client classes on server.
         */
        private static void handleSyncOriginData(SyncOriginDataPacket packet, IPayloadContext context) {
                context.enqueueWork(() -> {
                        try {
                                // Use reflection to call the client handler to avoid class loading on server
                                Class<?> handlerClass = Class.forName("com.veilorigins.network.packet.SyncOriginDataPacketHandler");
                                java.lang.reflect.Method handleMethod = handlerClass.getMethod("handleClient", SyncOriginDataPacket.class);
                                handleMethod.invoke(null, packet);
                        } catch (Exception e) {
                                VeilOrigins.LOGGER.error("Failed to handle SyncOriginDataPacket", e);
                        }
                });
        }

        /**
         * Handler for SyncCooldownsPacket - delegates to client-only code via reflection.
         */
        private static void handleSyncCooldowns(SyncCooldownsPacket packet, IPayloadContext context) {
                context.enqueueWork(() -> {
                        try {
                                Class<?> handlerClass = Class.forName("com.veilorigins.network.packet.SyncOriginDataPacketHandler");
                                java.lang.reflect.Method handleMethod = handlerClass.getMethod("handleCooldownSync", SyncCooldownsPacket.class);
                                handleMethod.invoke(null, packet);
                        } catch (Exception e) {
                                VeilOrigins.LOGGER.error("Failed to handle SyncCooldownsPacket", e);
                        }
                });
        }

        /**
         * Handler for SyncPossessionPacket - delegates to client-only code via reflection.
         */
        private static void handleSyncPossession(SyncPossessionPacket packet, IPayloadContext context) {
                context.enqueueWork(() -> {
                        try {
                                Class<?> handlerClass = Class.forName("com.veilorigins.client.ClientPossessionHandler");
                                java.lang.reflect.Method handleMethod = handlerClass.getMethod("updatePossessionState", boolean.class, int.class);
                                handleMethod.invoke(null, packet.isPossessing(), packet.mobEntityId());
                        } catch (Exception e) {
                                VeilOrigins.LOGGER.error("Failed to handle SyncPossessionPacket", e);
                        }
                });
        }

        /**
         * Sends a packet to the server. Must only be called from client-side code.
         * This method delegates to a client-only helper to avoid loading client classes on server.
         */
        public static <T extends CustomPacketPayload> void sendToServer(T packet) {
                ClientPacketSender.sendToServer(packet);
        }

        public static <T extends CustomPacketPayload> void sendToPlayer(ServerPlayer player, T packet) {
                PacketDistributor.sendToPlayer(player, packet);
        }
}
