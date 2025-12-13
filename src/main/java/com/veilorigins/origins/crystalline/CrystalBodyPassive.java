package com.veilorigins.origins.crystalline;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class CrystalBodyPassive extends OriginPassive {
    public CrystalBodyPassive() {
        super("crystal_body");
    }

    @Override
    public void onTick(Player player) {
        // Charging logic in EventHandler or here?
        // "Stores sunlight as energy" -> Resource bar logic.
        // Let's assume EventHandler handles resource gain from sun, similar to
        // Starborne/Mycomorph.

        // "Refracts light (slight invisibility)"
        int light = player.level().getMaxLocalRawBrightness(player.blockPosition());
        if (light > 12) {
            // Invisibility for translucency effect?
            // Doesn't work perfectly for "translucent".
            // We can just give resistance or something?
            // Spec "30% transparent". Visuals handled by Client events optimally.
            // Logic wise: maybe slight stealth?
        }

        // "Reflects projectiles occasionally (15% chance)"
        // Handled in EventHandler (LivingDamageEvent) or LivingHurtEvent.
        // We will note this for EventHandler update.
    }

    @Override
    public void onEquip(Player player) {
        // Attribute modifiers could go here
    }

    @Override
    public void onRemove(Player player) {
    }
}
