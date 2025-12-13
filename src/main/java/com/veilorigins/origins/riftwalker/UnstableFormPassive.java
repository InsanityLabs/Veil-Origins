package com.veilorigins.origins.riftwalker;

import com.veilorigins.api.OriginPassive;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class UnstableFormPassive extends OriginPassive {
    private int tickCounter = 0;
    private final Random random = new Random();

    public UnstableFormPassive() {
        super("unstable_form");
    }

    @Override
    public void onTick(Player player) {
        tickCounter++;
        
        // Random teleport every 5 minutes (6000 ticks)
        if (tickCounter >= 6000) {
            tickCounter = 0;
            
            // Random teleport 3-5 blocks
            if (random.nextDouble() < 0.1) { // 10% chance
                randomTeleport(player);
            }
        }
    }

    public void onDamage(Player player) {
        // 10% chance to randomly teleport when taking damage
        if (random.nextDouble() < 0.1) {
            randomTeleport(player);
            player.causeFoodExhaustion(1.0f); // Cost 1 hunger
        }
    }

    private void randomTeleport(Player player) {
        Level level = player.level();
        Vec3 currentPos = player.position();
        
        // Random offset 3-5 blocks
        double distance = 3 + random.nextDouble() * 2;
        double angle = random.nextDouble() * Math.PI * 2;
        
        double offsetX = Math.cos(angle) * distance;
        double offsetZ = Math.sin(angle) * distance;
        
        Vec3 newPos = new Vec3(
            currentPos.x + offsetX,
            currentPos.y,
            currentPos.z + offsetZ
        );
        
        // Teleport
        player.teleportTo(newPos.x, newPos.y, newPos.z);
        
        // Effects
        level.playSound(null, currentPos.x, currentPos.y, currentPos.z, 
            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 0.5f, 1.5f);
        
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.PORTAL,
                currentPos.x, currentPos.y + 1, currentPos.z, 20, 0.3, 0.5, 0.3, 0.1);
            serverLevel.sendParticles(ParticleTypes.PORTAL,
                newPos.x, newPos.y + 1, newPos.z, 20, 0.3, 0.5, 0.3, 0.1);
        }
    }

    @Override
    public void onEquip(Player player) {
        // Called when player selects Riftwalker origin
    }

    @Override
    public void onRemove(Player player) {
        // Called when player changes from Riftwalker origin
    }
}
