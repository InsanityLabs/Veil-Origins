package com.veilorigins.origins.technomancer;

import com.veilorigins.api.OriginAbility;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;

public class OverclockAbility extends OriginAbility {
    private static final int COOLDOWN = 120 * 20;
    private static final int DURATION = 600; // 30s
    private static final int HUNGER_COST = 10;

    // Track active overclocks
    private final Map<UUID, Integer> activeOverclocks = new HashMap<>();

    public OverclockAbility() {
        super("overclock", COOLDOWN);
    }

    @Override
    public void onActivate(Player player, Level level) {
        player.causeFoodExhaustion(HUNGER_COST);
        activeOverclocks.put(player.getUUID(), DURATION);

        // Initial effects
        applyEffects(player);

        startCooldown();
    }

    private void applyEffects(Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 40, 2, false, false)); // Speed III
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 40, 2, false, false)); // Haste III
        // +50% damage handled in EventHandler or via Strength? Strength II gives +6
        // damage (3 hearts) approx 50-100%.
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 1, false, false)); // Strength II
    }

    @Override
    public void tick(Player player) {
        UUID id = player.getUUID();
        if (activeOverclocks.containsKey(id)) {
            int ticks = activeOverclocks.get(id);
            if (ticks > 0) {
                activeOverclocks.put(id, ticks - 1);
                // Refresh effects
                if (ticks % 20 == 0)
                    applyEffects(player);
            } else {
                activeOverclocks.remove(id);
                // Overheat penalty
                player.hurt(player.damageSources().magic(), 6.0f); // 3 hearts
                player.sendSystemMessage(net.minecraft.network.chat.Component
                        .literal(ChatFormatting.RED + "System Overheat! Discharging thermal energy..."));
            }
        }
    }

    @Override
    public boolean canUse(Player player) {
        return !isOnCooldown() && player.getFoodData().getFoodLevel() >= HUNGER_COST;
    }

    @Override
    public int getResourceCost() {
        return 0;
    }
}
