package com.veilorigins.network.packet;

import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.UnicodeFontHandler;
import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.config.VeilOriginsConfig;
import com.veilorigins.data.OriginData;
import com.veilorigins.network.ModPackets;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Packet sent from client to server when a player selects an origin via the
 * radial menu.
 */
public record SelectOriginPacket(String originId) implements CustomPacketPayload {

        public static final CustomPacketPayload.Type<SelectOriginPacket> TYPE = new CustomPacketPayload.Type<>(
                        ResourceLocation.fromNamespaceAndPath(VeilOrigins.MOD_ID, "select_origin"));

        public static final StreamCodec<ByteBuf, SelectOriginPacket> STREAM_CODEC = StreamCodec.composite(
                        ByteBufCodecs.STRING_UTF8,
                        SelectOriginPacket::originId,
                        SelectOriginPacket::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
                return TYPE;
        }

        /**
         * Handle the packet on the server side.
         */
        public static void handle(SelectOriginPacket packet, IPayloadContext context) {
                context.enqueueWork(() -> {
                        if (context.player() instanceof ServerPlayer player) {
                                // Check if player already has an origin
                                Origin currentOrigin = VeilOriginsAPI.getPlayerOrigin(player);

                                // Get the requested origin
                                Origin requestedOrigin = VeilOriginsAPI.getOrigin(packet.originId());

                                if (requestedOrigin == null) {
                                        VeilOrigins.LOGGER.warn("Player {} tried to select unknown origin: {}",
                                                        player.getName().getString(), packet.originId());
                                        player.sendSystemMessage(
                                                        Component.literal("Unknown origin: " + packet.originId())
                                                                        .withStyle(ChatFormatting.RED));
                                        return;
                                }

                                if (currentOrigin == null) {
                                        // First time selection - always allowed
                                        VeilOriginsAPI.setPlayerOrigin(player, requestedOrigin);

                                        // Sync the new origin to the client - critical for multiplayer!
                                        syncOriginToClient(player, requestedOrigin);

                                        String star = UnicodeFontHandler.getSymbol(UnicodeFontHandler.SYMBOL_STAR, "*");
                                        player.sendSystemMessage(Component.literal(
                                                        star + " You have become a ")
                                                        .withStyle(ChatFormatting.GREEN)
                                                        .append(Component.literal(requestedOrigin.getDisplayName())
                                                                        .withStyle(ChatFormatting.GOLD))
                                                        .append(Component.literal("! " + star)
                                                                        .withStyle(ChatFormatting.GREEN)));
                                        player.sendSystemMessage(Component.literal(
                                                        requestedOrigin.getDescription())
                                                        .withStyle(ChatFormatting.GRAY));

                                        // Notify player about their abilities
                                        int abilityCount = requestedOrigin.getAbilities().size();
                                        int passiveCount = requestedOrigin.getPassives().size();
                                        player.sendSystemMessage(Component.literal("You have ")
                                                        .withStyle(ChatFormatting.YELLOW)
                                                        .append(Component.literal(String.valueOf(abilityCount))
                                                                        .withStyle(ChatFormatting.WHITE))
                                                        .append(Component.literal(" active abilities and ")
                                                                        .withStyle(ChatFormatting.YELLOW))
                                                        .append(Component.literal(String.valueOf(passiveCount))
                                                                        .withStyle(ChatFormatting.WHITE))
                                                        .append(Component.literal(" passives.")
                                                                        .withStyle(ChatFormatting.YELLOW)));
                                        player.sendSystemMessage(Component.literal("Press ")
                                                        .withStyle(ChatFormatting.GRAY)
                                                        .append(Component.literal("[R]").withStyle(ChatFormatting.AQUA))
                                                        .append(Component.literal(" for Ability 1, ")
                                                                        .withStyle(ChatFormatting.GRAY))
                                                        .append(Component.literal("[V]").withStyle(ChatFormatting.AQUA))
                                                        .append(Component.literal(" for Ability 2, or ")
                                                                        .withStyle(ChatFormatting.GRAY))
                                                        .append(Component.literal("[G]").withStyle(ChatFormatting.AQUA))
                                                        .append(Component.literal(" for the Ability Menu.")
                                                                        .withStyle(ChatFormatting.GRAY)));

                                        VeilOrigins.LOGGER.info("Player {} selected origin: {}",
                                                        player.getName().getString(), requestedOrigin.getId());
                                } else {
                                        // Origin change - check config
                                        if (!VeilOriginsConfig.allowOriginChange) {
                                                player.sendSystemMessage(Component.literal(
                                                                "Origin changes are disabled on this server. You are a ")
                                                                .withStyle(ChatFormatting.RED)
                                                                .append(Component
                                                                                .literal(currentOrigin.getDisplayName())
                                                                                .withStyle(ChatFormatting.YELLOW))
                                                                .append(Component.literal(".")
                                                                                .withStyle(ChatFormatting.RED)));
                                                VeilOrigins.LOGGER.debug(
                                                                "Player {} tried to change origin but changes are disabled",
                                                                player.getName().getString());
                                                return;
                                        }

                                        // Origin change allowed
                                        VeilOriginsAPI.setPlayerOrigin(player, requestedOrigin);

                                        // Sync the new origin to the client - critical for multiplayer!
                                        syncOriginToClient(player, requestedOrigin);

                                        player.sendSystemMessage(Component.literal(
                                                        "Your origin has been changed to ")
                                                        .withStyle(ChatFormatting.GOLD)
                                                        .append(Component.literal(requestedOrigin.getDisplayName())
                                                                        .withStyle(ChatFormatting.YELLOW))
                                                        .append(Component.literal("!").withStyle(ChatFormatting.GOLD)));

                                        VeilOrigins.LOGGER.info("Player {} changed origin from {} to {}",
                                                        player.getName().getString(),
                                                        currentOrigin.getId(),
                                                        requestedOrigin.getId());
                                }
                        }
                });
        }

        /**
         * Helper method to sync origin data to the client.
         */
        private static void syncOriginToClient(ServerPlayer player, Origin origin) {
                OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);
                SyncOriginDataPacket syncPacket = new SyncOriginDataPacket(
                                origin.getId().toString(),
                                data.getOriginLevel(),
                                data.getOriginXP(),
                                data.getResourceBar());
                ModPackets.sendToPlayer(player, syncPacket);
                VeilOrigins.LOGGER.debug("Synced origin {} to client for player {}",
                                origin.getId(), player.getName().getString());
        }
}
