package com.veilorigins.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.data.OriginData;
import com.veilorigins.network.ModPackets;
import com.veilorigins.network.packet.SyncOriginDataPacket;
import com.veilorigins.progression.HybridSystem;
import com.veilorigins.progression.ProgressionSystem;
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
                                        .executes(OriginCommand::resetCooldowns)))
                        // Progression commands
                        .then(Commands.literal("setlevel")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("level", IntegerArgumentType.integer(1, 50))
                                                .executes(OriginCommand::setLevel))))
                        .then(Commands.literal("addxp")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(1, 100000))
                                                .executes(OriginCommand::addXP))))
                        .then(Commands.literal("setprestige")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("prestige", IntegerArgumentType.integer(0, 10))
                                                .executes(OriginCommand::setPrestige))))
                        .then(Commands.literal("addskillpoints")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("points", IntegerArgumentType.integer(1, 100))
                                                .executes(OriginCommand::addSkillPoints))))
                        .then(Commands.literal("resetskills")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(OriginCommand::resetSkills)))
                        .then(Commands.literal("info")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(OriginCommand::showInfo)))
                        .then(Commands.literal("hybrid")
                                .then(Commands.literal("activate")
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .then(Commands.argument("secondary", StringArgumentType.string())
                                                        .executes(OriginCommand::activateHybrid))))
                                .then(Commands.literal("deactivate")
                                        .then(Commands.argument("player", EntityArgument.player())
                                                .executes(OriginCommand::deactivateHybrid)))));
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
            String skillsStr = String.join(",", data.getUnlockedSkills());
            SyncOriginDataPacket syncPacket = new SyncOriginDataPacket(
                    "", // Empty string means no origin
                    data.getOriginLevel(),
                    data.getOriginXP(),
                    data.getResourceBar(),
                    data.getSkillPoints(),
                    skillsStr);
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

            player.displayClientMessage(Component.literal("All ability cooldowns reset!").withStyle(ChatFormatting.GREEN), false);

            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
            return 0;
        }
    }
    
    private static int setLevel(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = EntityArgument.getPlayer(context, "player");
            int level = IntegerArgumentType.getInteger(context, "level");
            
            OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);
            data.setOriginLevel(level);
            data.setOriginXP(ProgressionSystem.getTotalXPForLevel(level));
            
            Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
            if (origin != null) {
                syncOriginToClient(player, origin);
            }
            
            context.getSource().sendSuccess(
                    () -> Component.literal("Set " + player.getName().getString() + "'s level to " + level),
                    true);
            
            player.displayClientMessage(
                Component.literal("Your origin level has been set to " + level + "!").withStyle(ChatFormatting.GOLD),
                false);
            
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
            return 0;
        }
    }
    
    private static int addXP(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = EntityArgument.getPlayer(context, "player");
            int amount = IntegerArgumentType.getInteger(context, "amount");
            
            ProgressionSystem.awardXP(player, amount, "command");
            
            context.getSource().sendSuccess(
                    () -> Component.literal("Added " + amount + " XP to " + player.getName().getString()),
                    true);
            
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
            return 0;
        }
    }
    
    private static int setPrestige(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = EntityArgument.getPlayer(context, "player");
            int prestige = IntegerArgumentType.getInteger(context, "prestige");
            
            OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);
            data.setPrestigeLevel(prestige);
            
            Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
            if (origin != null) {
                syncOriginToClient(player, origin);
            }
            
            context.getSource().sendSuccess(
                    () -> Component.literal("Set " + player.getName().getString() + "'s prestige to " + prestige),
                    true);
            
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
            return 0;
        }
    }
    
    private static int addSkillPoints(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = EntityArgument.getPlayer(context, "player");
            int points = IntegerArgumentType.getInteger(context, "points");
            
            OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);
            data.addSkillPoints(points);
            
            // Sync to client so skill tree UI updates
            Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
            if (origin != null) {
                syncOriginToClient(player, origin);
            }
            
            context.getSource().sendSuccess(
                    () -> Component.literal("Added " + points + " skill points to " + player.getName().getString()),
                    true);
            
            player.displayClientMessage(
                Component.literal("+" + points + " Skill Points!").withStyle(ChatFormatting.AQUA),
                false);
            
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
            return 0;
        }
    }
    
    private static int resetSkills(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = EntityArgument.getPlayer(context, "player");
            
            OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);
            
            // Count unlocked skills to refund points
            int refundedPoints = data.getUnlockedSkills().size(); // Simplified - 1 point per skill
            
            data.resetSkills();
            data.addSkillPoints(refundedPoints);
            
            // Sync to client so skill tree UI updates
            Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
            if (origin != null) {
                syncOriginToClient(player, origin);
            }
            
            context.getSource().sendSuccess(
                    () -> Component.literal("Reset skills for " + player.getName().getString() + " (refunded " + refundedPoints + " points)"),
                    true);
            
            player.displayClientMessage(
                Component.literal("Your skills have been reset! Refunded " + refundedPoints + " skill points.").withStyle(ChatFormatting.YELLOW),
                false);
            
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
            return 0;
        }
    }
    
    private static int showInfo(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = EntityArgument.getPlayer(context, "player");
            Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
            OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);
            
            context.getSource().sendSuccess(
                    () -> Component.literal("=== " + player.getName().getString() + "'s Origin Info ===").withStyle(ChatFormatting.GOLD),
                    false);
            
            if (origin == null) {
                context.getSource().sendSuccess(
                        () -> Component.literal("No origin selected").withStyle(ChatFormatting.GRAY),
                        false);
            } else {
                context.getSource().sendSuccess(
                        () -> Component.literal("Origin: " + origin.getDisplayName()).withStyle(ChatFormatting.YELLOW),
                        false);
            }
            
            context.getSource().sendSuccess(
                    () -> Component.literal("Level: " + data.getOriginLevel() + "/50").withStyle(ChatFormatting.GREEN),
                    false);
            context.getSource().sendSuccess(
                    () -> Component.literal("XP: " + data.getOriginXP()).withStyle(ChatFormatting.AQUA),
                    false);
            context.getSource().sendSuccess(
                    () -> Component.literal("Prestige: " + data.getPrestigeLevel() + "/10").withStyle(ChatFormatting.LIGHT_PURPLE),
                    false);
            context.getSource().sendSuccess(
                    () -> Component.literal("Skill Points: " + data.getSkillPoints()).withStyle(ChatFormatting.BLUE),
                    false);
            context.getSource().sendSuccess(
                    () -> Component.literal("Unlocked Skills: " + data.getUnlockedSkills().size()).withStyle(ChatFormatting.DARK_AQUA),
                    false);
            context.getSource().sendSuccess(
                    () -> Component.literal("Resource: " + String.format("%.1f", data.getResourceBar()) + "/100").withStyle(ChatFormatting.RED),
                    false);
            
            // Hybrid status
            if (HybridSystem.isInHybridMode(player)) {
                HybridSystem.HybridState state = HybridSystem.getHybridState(player);
                context.getSource().sendSuccess(
                        () -> Component.literal("HYBRID MODE: " + state.getDisplayName() + " (" + state.getRemainingSeconds() + "s)").withStyle(ChatFormatting.DARK_PURPLE),
                        false);
            }
            
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
            return 0;
        }
    }
    
    private static int activateHybrid(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = EntityArgument.getPlayer(context, "player");
            String secondaryId = StringArgumentType.getString(context, "secondary");
            
            Origin secondary = VeilOriginsAPI.getOrigin("veil_origins:" + secondaryId);
            if (secondary == null) {
                context.getSource().sendFailure(Component.literal("Unknown origin: " + secondaryId));
                return 0;
            }
            
            // Force level 50 for admin command
            OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);
            if (data.getOriginLevel() < 50) {
                data.setOriginLevel(50);
            }
            
            boolean success = HybridSystem.activateHybrid(player, secondary);
            
            if (success) {
                context.getSource().sendSuccess(
                        () -> Component.literal("Activated hybrid mode for " + player.getName().getString()),
                        true);
            } else {
                context.getSource().sendFailure(Component.literal("Failed to activate hybrid mode"));
            }
            
            return success ? 1 : 0;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("Error: " + e.getMessage()));
            return 0;
        }
    }
    
    private static int deactivateHybrid(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = EntityArgument.getPlayer(context, "player");
            
            if (!HybridSystem.isInHybridMode(player)) {
                context.getSource().sendFailure(Component.literal(player.getName().getString() + " is not in hybrid mode"));
                return 0;
            }
            
            HybridSystem.deactivateHybrid(player);
            
            context.getSource().sendSuccess(
                    () -> Component.literal("Deactivated hybrid mode for " + player.getName().getString()),
                    true);
            
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
        String skillsStr = String.join(",", data.getUnlockedSkills());
        SyncOriginDataPacket syncPacket = new SyncOriginDataPacket(
                origin.getId().toString(),
                data.getOriginLevel(),
                data.getOriginXP(),
                data.getResourceBar(),
                data.getSkillPoints(),
                skillsStr);
        ModPackets.sendToPlayer(player, syncPacket);
        VeilOrigins.LOGGER.debug("Synced origin {} to client for player {}",
                origin.getId(), player.getName().getString());
    }
}
