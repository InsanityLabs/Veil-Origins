package com.veilorigins.network.packet;

import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.OriginAbility;
import com.veilorigins.api.VeilOriginsAPI;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ActivateAbilityPacket(int abilityIndex) implements CustomPacketPayload {
    
    public static final CustomPacketPayload.Type<ActivateAbilityPacket> TYPE = 
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(VeilOrigins.MOD_ID, "activate_ability"));
    
    public static final StreamCodec<ByteBuf, ActivateAbilityPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.INT,
        ActivateAbilityPacket::abilityIndex,
        ActivateAbilityPacket::new
    );
    
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
                    
                    if (ability.canUse(serverPlayer) && !ability.isOnCooldown()) {
                        ability.onActivate(serverPlayer, serverPlayer.level());
                        VeilOrigins.LOGGER.info("Player {} activated ability {}", 
                            serverPlayer.getName().getString(), packet.abilityIndex);
                    }
                }
            }
        });
    }
}
