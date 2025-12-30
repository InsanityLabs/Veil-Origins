package com.veilorigins.event;

import com.veilorigins.VeilOrigins;
import com.veilorigins.api.Origin;
import com.veilorigins.api.VeilOriginsAPI;
import com.veilorigins.data.OriginData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = VeilOrigins.MOD_ID)
public class PlayerEventHandler {
    
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        
        OriginData.PlayerOriginData data = player.getData(OriginData.PLAYER_ORIGIN);
        Identifier originId = data.getOriginId();
        
        if (originId != null) {
            // Restore origin from saved data
            Origin origin = VeilOriginsAPI.getOrigin(originId);
            if (origin != null) {
                VeilOriginsAPI.setPlayerOrigin(player, origin);
            }
        }
    }
    
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            // Preserve origin data on death
            OriginData.PlayerOriginData oldData = event.getOriginal().getData(OriginData.PLAYER_ORIGIN);
            OriginData.PlayerOriginData newData = event.getEntity().getData(OriginData.PLAYER_ORIGIN);
            
            newData.setOriginId(oldData.getOriginId());
            newData.setOriginLevel(oldData.getOriginLevel());
            newData.setOriginXP(oldData.getOriginXP());
            newData.setPrestigeLevel(oldData.getPrestigeLevel());
            newData.setResourceBar(100.0f); // Reset resource on death
            
            // Restore origin
            if (oldData.getOriginId() != null) {
                Origin origin = VeilOriginsAPI.getOrigin(oldData.getOriginId());
                if (origin != null) {
                    VeilOriginsAPI.setPlayerOrigin((ServerPlayer) event.getEntity(), origin);
                }
            }
        }
    }
}
