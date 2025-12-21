package com.veilorigins.origins.voidtouched;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;
import net.minecraft.ChatFormatting;

public class VoidWalkerPassive extends OriginPassive {
    private int tickCounter = 0;

    public VoidWalkerPassive() {
        super("void_walker");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;

        // Check every 20 ticks (1 second)
        if (tickCounter >= 20) {
            tickCounter = 0;
            Level level = player.level();

            // Ender dragon ignores you
            AABB area = new AABB(
                    player.getX() - 32, player.getY() - 16, player.getZ() - 32,
                    player.getX() + 32, player.getY() + 16, player.getZ() + 32);

            List<EnderDragon> dragons = level.getEntitiesOfClass(EnderDragon.class, area);
            for (EnderDragon dragon : dragons) {
                if (dragon.getTarget() == player) {
                    dragon.setTarget(null);
                }
            }

            // Endermen are neutral toward you
            List<EnderMan> endermen = level.getEntitiesOfClass(EnderMan.class, area);
            for (EnderMan enderman : endermen) {
                if (enderman.getTarget() == player) {
                    enderman.setTarget(null);
                }
            }
        }

        // Note: 75% void damage reduction is handled in event handler
        // Note: Can see in void (night vision in void) would need dimension check
    }

    @Override
    public void onEquip(Player player) {
        player.sendSystemMessage(net.minecraft.network.chat.Component
                .literal(ChatFormatting.DARK_PURPLE + "The void has marked you. You are resistant to its embrace."));
    }

    @Override
    public void onRemove(Player player) {
        // Called when player changes from Voidtouched origin
    }
}
