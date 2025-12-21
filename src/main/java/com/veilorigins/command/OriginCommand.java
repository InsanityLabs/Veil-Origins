package com.veilorigins.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.data.OriginData;
import com.veilorigins.network.ModPackets;
import com.veilorigins.network.packet.SyncOriginDataPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class OriginCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("veilorigins")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.literal("set")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("origin", StringArgumentType.string())
                                                .executes(OriginCommand::setOrigin))))
                        .then(Commands.literal("reset")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(OriginCommand::resetOrigin)))
                        .then(Commands.literal("list")
                                .executes(OriginCommand::listOrigins))
                        .then(Commands.literal("resetcooldowns")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(OriginCommand::resetCooldowns))));
    }

    private static int setOrigin(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = EntityArgument.getPlayer(context, "player");
            String originId = StringArgumentType.getString(context, "origin");

            Origin origin = VeilOriginsAPI.getOrigin("veil_origins:" + originId);

            if (origin == null) {
                context.getSource().sendFailure(Component.literal("Unknown origin: " + originId));
                return 0;
            }

            VeilOriginsAPI.setPlayerOrigin(player, origin);

            // Sync the new origin to the client - critical for multiplayer!
            syncOriginToClient(player, origin);

            context.getSource().sendSuccess(
                    () -> Component
                            .literal("Set " + player.getName().getString() + "'s origin to " + origin.getDisplayName()),
                    true);

            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
            return 0;
        }
    }

    private static int resetOrigin(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = EntityArgument.getPlayer(context, "player");

            VeilOriginsAPI.setPlayerOrigin(player, null);

            // Sync the cleared origin to the client - critical for multiplayer!
            OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);
            SyncOriginDataPacket syncPacket = new SyncOriginDataPacket(
                    "", // Empty string means no origin
                    data.getOriginLevel(),
                    data.getOriginXP(),
                    data.getResourceBar());
            ModPackets.sendToPlayer(player, syncPacket);

            context.getSource().sendSuccess(
                    () -> Component.literal("Reset " + player.getName().getString() + "'s origin"),
                    true);

            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
            return 0;
        }
    }

    private static int listOrigins(CommandContext<CommandSourceStack> context) {
        var origins = VeilOriginsAPI.getAllOrigins();

        context.getSource().sendSuccess(
                () -> Component.literal("Available Origins:"),
                false);

        origins.forEach((id, origin) -> {
            context.getSource().sendSuccess(
                    () -> Component.literal("- " + origin.getDisplayName() + " (" + id.getPath() + ")"),
                    false);
        });

        return 1;
    }

    private static int resetCooldowns(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = EntityArgument.getPlayer(context, "player");
            Origin origin = VeilOriginsAPI.getPlayerOrigin(player);

            if (origin == null) {
                context.getSource().sendFailure(Component.literal(player.getName().getString() + " has no origin!"));
                return 0;
            }

            // Reset all ability cooldowns
            origin.getAbilities().forEach(ability -> ability.setCooldown(0));

            // Reset resource bar to full
            OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);
            if (data != null) {
                data.setResourceBar(100.0f);
            }

            // Sync the updated resource bar to the client
            syncOriginToClient(player, origin);

            context.getSource().sendSuccess(
                    () -> Component.literal("Reset all cooldowns for " + player.getName().getString()),
                    true);

            player.sendSystemMessage(Component.literal("All ability cooldowns reset!").withStyle(ChatFormatting.GREEN));

            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
            return 0;
        }
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
