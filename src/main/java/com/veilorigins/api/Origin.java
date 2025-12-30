package com.veilorigins.api;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import java.util.ArrayList;
import java.util.List;

public class Origin {
    private final Identifier id;
    private final String displayName;
    private final String description;
    private final ImpactLevel impactLevel;
    private final List<OriginAbility> abilities;
    private final List<OriginPassive> passives;
    private float healthModifier = 1.0f;
    private float speedModifier = 1.0f;
    private float damageModifier = 1.0f;
    private ResourceType resourceType;

    public Origin(Identifier id, String displayName, String description, ImpactLevel impactLevel) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.impactLevel = impactLevel;
        this.abilities = new ArrayList<>();
        this.passives = new ArrayList<>();
    }

    public Identifier getId() { return id; }
    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
    public ImpactLevel getImpactLevel() { return impactLevel; }
    public List<OriginAbility> getAbilities() { return abilities; }
    public List<OriginPassive> getPassives() { return passives; }
    public float getHealthModifier() { return healthModifier; }
    public float getSpeedModifier() { return speedModifier; }
    public float getDamageModifier() { return damageModifier; }
    public ResourceType getResourceType() { return resourceType; }

    public void setHealthModifier(float modifier) { this.healthModifier = modifier; }
    public void setSpeedModifier(float modifier) { this.speedModifier = modifier; }
    public void setDamageModifier(float modifier) { this.damageModifier = modifier; }
    public void setResourceType(ResourceType type) { this.resourceType = type; }

    public void addAbility(OriginAbility ability) { abilities.add(ability); }
    public void addPassive(OriginPassive passive) { passives.add(passive); }

    public OriginAbility getAbility(String abilityId) {
        return abilities.stream()
            .filter(a -> a.getId().equals(abilityId))
            .findFirst()
            .orElse(null);
    }

    public void tick(Player player) {
        passives.forEach(passive -> passive.onTick(player));
    }
}
