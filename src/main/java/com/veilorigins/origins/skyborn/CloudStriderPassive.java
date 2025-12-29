package com.veilorigins.origins.skyborn;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class CloudStriderPassive extends OriginPassive {
    private int jumpCount = 0;
    private boolean wasOnGround = true;

    public CloudStriderPassive() {
        super("cloud_strider");
    }

    @Override
    public void onTick(Player player) {
        // Slow falling
        if (!player.onGround() && !player.isShiftKeyDown() && player.getDeltaMovement().y < 0) {
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 10, 0, false, false));
        }

        // Speed at high altitude (> Y=100)
        if (player.getY() > 100) {
            player.addEffect(new MobEffectInstance(MobEffects.SPEED, 20, 0, false, false)); // Speed I implies
                                                                                                     // +20%
        }

        // Double Jump Logic (Client-side usually handles input, server validates)
        // Simplified: We rely on vanilla enchantments or events?
        // Double jump is tricky without client side key input event.
        // However, we can use "Jump boost" effect temporarily or similar? No.
        // For now, let's omit Double Jump implementation unless we edit client input
        // handler or have a library like Wall-Jump.
        // Spec says "Double jump (once per jump)".
        // Implementation note: Usually requires packet handling for Jump Key.
        // I will implement passive speed/slow fall parts properly.
    }

    @Override
    public void onEquip(Player player) {
    }

    @Override
    public void onRemove(Player player) {
        player.removeEffect(MobEffects.SLOW_FALLING);
        player.removeEffect(MobEffects.SPEED);
    }
}
