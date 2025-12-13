package com.veilorigins.client;

import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.network.ModPackets;
import com.veilorigins.network.packet.ActivateAbilityPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = VeilOrigins.MOD_ID, value = Dist.CLIENT)
public class KeyInputHandler {
    
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        
        if (player == null) return;
        
        Origin origin = VeilOriginsAPI.getPlayerOrigin(player);
        if (origin == null) return;
        
        // Check ability 1 key
        if (KeyBindings.ABILITY_1.consumeClick()) {
            if (origin.getAbilities().size() > 0) {
                ModPackets.sendToServer(new ActivateAbilityPacket(0));
                VeilOrigins.LOGGER.debug("Sent ability 1 activation packet");
            }
        }
        
        // Check ability 2 key
        if (KeyBindings.ABILITY_2.consumeClick()) {
            if (origin.getAbilities().size() > 1) {
                ModPackets.sendToServer(new ActivateAbilityPacket(1));
                VeilOrigins.LOGGER.debug("Sent ability 2 activation packet");
            }
        }
        
        // Check resource info key
        if (KeyBindings.RESOURCE_INFO.consumeClick()) {
            player.displayClientMessage(
                net.minecraft.network.chat.Component.literal(
                    String.format("Origin: %s | Abilities: %d",
                        origin.getDisplayName(),
                        origin.getAbilities().size())
                ),
                false
            );
        }
    }
}
