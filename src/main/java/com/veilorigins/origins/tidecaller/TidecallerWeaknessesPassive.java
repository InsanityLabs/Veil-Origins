package com.veilorigins.origins.tidecaller;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;

public class TidecallerWeaknessesPassive extends OriginPassive {
    public TidecallerWeaknessesPassive() {
        super("tidecaller_weaknesses");
    }

    @Override
    public void onTick(Player player) {
        // Punish holding fire-related items
        if (player.getMainHandItem().is(Items.FLINT_AND_STEEL) || player.getOffhandItem().is(Items.FLINT_AND_STEEL) ||
            player.getMainHandItem().is(Items.LAVA_BUCKET) || player.getOffhandItem().is(Items.LAVA_BUCKET)) {
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 40, 1, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 40, 2, false, false)); // Mining Fatigue makes it hard to use
        }
    }

    @Override
    public void onEquip(Player player) {}

    @Override
    public void onRemove(Player player) {}
}
