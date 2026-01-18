package com.veilorigins.network.packet;

import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.OriginAbility;
import com.veilorigins.api.VeilOriginsAPI;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ActivateAbilityPacket(int abilityIndex) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ActivateAbilityPacket> TYPE = new CustomPacketPayload.Type<>(
            Identifier.fromNamespaceAndPath(VeilOrigins.MOD_ID, "activate_ability"));

    public static final StreamCodec<ByteBuf, ActivateAbilityPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            ActivateAbilityPacket::abilityIndex,
            ActivateAbilityPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ActivateAbilityPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                Origin origin = VeilOriginsAPI.getPlayerOrigin(serverPlayer);
                if (origin != null && packet.abilityIndex < origin.getAbilities().size()) {
                    OriginAbility ability = origin.getAbilities().get(packet.abilityIndex);

                    // Only check canUse() - it handles cooldown internally and may allow toggle-off
                    // during cooldown
                    if (ability.canUse(serverPlayer)) {
                        // Check if vampire/vampling and consume blood for ability cost
                        String originPath = origin.getId().getPath();
                        if (originPath.equals("vampire") || originPath.equals("vampling")) {
                            int bloodCost = ability.getResourceCost();
                            if (bloodCost > 0) {
                                com.veilorigins.data.OriginData.PlayerOriginData data = 
                                    serverPlayer.getData(com.veilorigins.data.OriginData.PLAYER_ORIGIN);
                                
                                // Check if player has enough blood
                                if (data.getResourceBar() < bloodCost) {
                                    serverPlayer.displayClientMessage(
                                        net.minecraft.network.chat.Component.literal(
                                            net.minecraft.ChatFormatting.RED + "Not enough blood! Need " + bloodCost + " blood."),
                                        true);
                                    return;
                                }
                                
                                // Consume blood
                                data.consumeResource(bloodCost);
                            }
                        }
                        
                        ability.onActivate(serverPlayer, serverPlayer.level());
                        VeilOrigins.LOGGER.info("Player {} activated ability {}",
                                serverPlayer.getName().getString(), packet.abilityIndex);
                    }
                }
            }
        });
    }
}
