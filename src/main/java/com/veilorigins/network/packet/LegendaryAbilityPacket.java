package com.veilorigins.network.packet;

import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.data.OriginData;
import com.veilorigins.progression.LegendaryAbility;
import com.veilorigins.progression.ProgressionSystem;
import com.veilorigins.progression.legendary.LegendaryAbilityRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Packet sent when player tries to activate their legendary ability (R + Attack).
 */
public record LegendaryAbilityPacket() implements CustomPacketPayload {
    
    public static final CustomPacketPayload.Type<LegendaryAbilityPacket> TYPE = 
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(VeilOrigins.MOD_ID, "legendary_ability"));
    
    public static final StreamCodec<FriendlyByteBuf, LegendaryAbilityPacket> STREAM_CODEC = 
        StreamCodec.unit(new LegendaryAbilityPacket());
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    
    public static void handle(LegendaryAbilityPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (!(player instanceof ServerPlayer serverPlayer)) return;
            
            Origin origin = VeilOriginsAPI.getPlayerOrigin(serverPlayer);
            if (origin == null) {
                VeilOrigins.LOGGER.debug("Player {} tried to use legendary ability without origin", 
                    serverPlayer.getName().getString());
                return;
            }
            
            // Check if player is level 50
            if (!ProgressionSystem.canUseLegendaryAbility(serverPlayer)) {
                serverPlayer.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("§cYou must be level 50 to use your Legendary Ability!"),
                    true);
                return;
            }
            
            // Get legendary ability for this origin
            LegendaryAbility legendary = LegendaryAbilityRegistry.getLegendary(origin.getId());
            if (legendary == null) {
                VeilOrigins.LOGGER.warn("No legendary ability registered for origin: {}", origin.getId());
                return;
            }
            
            // Check cooldown
            if (legendary.isOnCooldown()) {
                int remaining = legendary.getCurrentCooldown() / 20;
                serverPlayer.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("§cLegendary ability on cooldown: " + remaining + "s"),
                    true);
                return;
            }
            
            // Check resource cost
            OriginData.PlayerOriginData data = OriginData.get(serverPlayer);
            if (data.getResourceBar() < legendary.getResourceCost()) {
                serverPlayer.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("§cNot enough resource! Need " + legendary.getResourceCost()),
                    true);
                return;
            }
            
            // Consume resource and activate
            data.consumeResource(legendary.getResourceCost());
            legendary.activate(serverPlayer, serverPlayer.level());
            
            VeilOrigins.LOGGER.info("Player {} activated legendary ability: {}", 
                serverPlayer.getName().getString(), legendary.getName());
        });
    }
}
