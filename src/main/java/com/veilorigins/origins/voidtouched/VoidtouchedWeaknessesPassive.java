package com.veilorigins.origins.voidtouched;

import com.veilorigins.api.OriginPassive;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Random;
import net.minecraft.ChatFormatting;

public class VoidtouchedWeaknessesPassive extends OriginPassive {
    private int tickCounter = 0;
    private int randomTeleportTimer = 0;
    private static final int RANDOM_TELEPORT_INTERVAL = 5 * 60 * 20; // 5 minutes
    private final Random random = new Random();

    public VoidtouchedWeaknessesPassive() {
        super("voidtouched_weaknesses");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;
        randomTeleportTimer++;
        Level level = player.level();

        // Random teleport every 5 minutes
        if (randomTeleportTimer >= RANDOM_TELEPORT_INTERVAL) {
            randomTeleportTimer = 0;
            randomTeleport(player, level);
        }

        // Check every 20 ticks (1 second)
        if (tickCounter >= 20) {
            tickCounter = 0;

            // 1% chance per minute to take 2 HP void damage
            if (random.nextDouble() < (1.0 / 60.0 / 60.0)) { // Per second chance
                player.hurt(level.damageSources().fellOutOfWorld(), 2.0f);
                player.sendSystemMessage(net.minecraft.network.chat.Component
                        .literal(ChatFormatting.DARK_PURPLE + "The void tears at your essence..."));
            }
        }

        // Note: Bed explosion is handled in event handler
        // Note: Chorus fruit teleport to void is handled in event handler
    }

    private void randomTeleport(Player player, Level level) {
        Vec3 currentPos = player.position();

        // Random offset 1-3 blocks
        double distance = 1 + random.nextDouble() * 2;
        double angle = random.nextDouble() * Math.PI * 2;

        double offsetX = Math.cos(angle) * distance;
        double offsetZ = Math.sin(angle) * distance;

        Vec3 newPos = new Vec3(
                currentPos.x + offsetX,
                currentPos.y,
                currentPos.z + offsetZ);

        // Teleport
        player.teleportTo(newPos.x, newPos.y, newPos.z);

        // Effects
        level.playSound(null, currentPos.x, currentPos.y, currentPos.z,
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.5f, 0.5f);

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.PORTAL,
                    currentPos.x, currentPos.y + 1, currentPos.z, 20, 0.3, 0.5, 0.3, 0.1);
            serverLevel.sendParticles(ParticleTypes.PORTAL,
                    newPos.x, newPos.y + 1, newPos.z, 20, 0.3, 0.5, 0.3, 0.1);
        }

        player.sendSystemMessage(net.minecraft.network.chat.Component
                .literal(ChatFormatting.DARK_PURPLE + "Reality shifts around you..."));
    }

    @Override
    public void onEquip(Player player) {
        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(ChatFormatting.DARK_PURPLE
                + "As Voidtouched, reality is unstable around you. You randomly teleport and cannot use beds."));
    }

    @Override
    public void onRemove(Player player) {
        // Called when player changes from Voidtouched origin
    }
}
