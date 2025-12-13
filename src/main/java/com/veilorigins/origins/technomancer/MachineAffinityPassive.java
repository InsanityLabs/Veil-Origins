package com.veilorigins.origins.technomancer;

import com.veilorigins.api.OriginPassive;
import net.minecraft.world.entity.player.Player;

public class MachineAffinityPassive extends OriginPassive {
    public MachineAffinityPassive() {
        super("machine_affinity");
    }

    @Override
    public void onTick(Player player) {
        // Visual highlighting of Redstone/Machines handled client side or via Particles
        // in OreResonance style?
        // Spec: "Can see through Create contraptions... Redstone components
        // highlighted".
        // Server side cannot render Highlights (Glowing) on Blocks easily.
        // We'll skip complex highlighting logic here and assume client mod or particles
        // if needed.
        // Or similar to Ore Resonance? Technomancer might have particles near redstone.
    }

    @Override
    public void onEquip(Player player) {
    }

    @Override
    public void onRemove(Player player) {
    }
}
