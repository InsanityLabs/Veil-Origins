package com.veilorigins.progression.legendary;

import com.veilorigins.progression.LegendaryAbility;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for legendary abilities mapped to origins.
 */
public class LegendaryAbilityRegistry {
    
    private static final Map<Identifier, LegendaryAbility> LEGENDARY_ABILITIES = new HashMap<>();
    
    public static void register() {
        // Register legendary abilities for each origin
        registerLegendary("veil_origins:veilborn", new VeilbornLegendary());
        registerLegendary("veil_origins:vampire", new VampireLegendary());
        registerLegendary("veil_origins:vampling", new VamplingLegendary());
        registerLegendary("veil_origins:werewolf", new WerewolfLegendary());
        registerLegendary("veil_origins:wolfling", new WolflingLegendary());
        registerLegendary("veil_origins:stoneheart", new StoneheartLegendary());
        registerLegendary("veil_origins:feralkin", new FeralkinLegendary());
        registerLegendary("veil_origins:frostborn", new FrostbornLegendary());
        registerLegendary("veil_origins:cindersoul", new CindersoulLegendary());
        registerLegendary("veil_origins:tidecaller", new TidecallerLegendary());
        registerLegendary("veil_origins:starborne", new StarborneLegendary());
        registerLegendary("veil_origins:skyborn", new SkybornLegendary());
        registerLegendary("veil_origins:umbrakin", new UmbrakinLegendary());
        registerLegendary("veil_origins:riftwalker", new RiftwalkerLegendary());
        registerLegendary("veil_origins:voidtouched", new VoidtouchedLegendary());
        registerLegendary("veil_origins:mycomorph", new MycomorphLegendary());
        registerLegendary("veil_origins:crystalline", new CrystallineLegendary());
        registerLegendary("veil_origins:technomancer", new TechnomancerLegendary());
        registerLegendary("veil_origins:ethereal", new EtherealLegendary());
        registerLegendary("veil_origins:dryad", new DryadLegendary());
        registerLegendary("veil_origins:necromancer", new NecromancerLegendary());
    }
    
    private static void registerLegendary(String originId, LegendaryAbility ability) {
        LEGENDARY_ABILITIES.put(Identifier.parse(originId), ability);
    }
    
    public static LegendaryAbility getLegendary(Identifier originId) {
        return LEGENDARY_ABILITIES.get(originId);
    }
    
    public static boolean hasLegendary(Identifier originId) {
        return LEGENDARY_ABILITIES.containsKey(originId);
    }
    
    /**
     * Tick all legendary ability cooldowns.
     */
    public static void tickCooldowns() {
        for (LegendaryAbility ability : LEGENDARY_ABILITIES.values()) {
            ability.tickCooldown();
        }
    }
}
