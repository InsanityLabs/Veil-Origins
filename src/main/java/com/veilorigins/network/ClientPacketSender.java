package com.veilorigins.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Client-side packet sender helper.
 * This class is separate to avoid loading Minecraft client classes on the server.
 * Only call methods in this class from client-side code!
 */
public class ClientPacketSender {

    public static <T extends CustomPacketPayload> void sendToServer(T packet) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() != null) {
            mc.getConnection().send(packet);
        }
    }
}
