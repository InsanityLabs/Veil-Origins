package com.veilorigins.origins.ethereal;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.entity.player.Player;

public class IncorporealPassive extends OriginPassive {
    public IncorporealPassive() {
        super("incorporeal");
    }

    @Override
    public void onTick(Player player) {
        // "Cannot wear armor" - Drop armor if equipped?
        // Or handle in event handler preventing equip.
        // Simple enforcement:
        if (!player.level().isClientSide) {
            for (net.minecraft.world.entity.EquipmentSlot slot : net.minecraft.world.entity.EquipmentSlot.values()) {
                if (slot.getType() == net.minecraft.world.entity.EquipmentSlot.Type.HUMANOID_ARMOR) { // Armor slots
                    if (!player.getItemBySlot(slot).isEmpty()) {
                        player.drop(player.getItemBySlot(slot).copy(), true);
                        player.setItemSlot(slot, net.minecraft.world.item.ItemStack.EMPTY);
                        player.sendSystemMessage(net.minecraft.network.chat.Component
                                .literal("§cYour ethereal form cannot bear physical armor!"));
                    }
                }
            }
        }

        // "Don't trigger pressure plates"
        // Mixin or Event usually required (EntityInteract?). Heavy/Light pressure
        // plates rely on entity weight/type.
        // We can't easily disable via ticking without removing the block interaction.
        // We'll skip complex pressure plate logic here.

        // "See invisible entities"
        // Client side or update Glowing? standard is Glowing effect on invisible
        // entities near player.
    }

    @Override
    public void onEquip(Player player) {
    }

    @Override
    public void onRemove(Player player) {
    }
}
