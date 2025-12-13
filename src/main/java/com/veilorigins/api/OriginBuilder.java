package com.veilorigins.api;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class OriginBuilder {
    private final ResourceLocation id;
    private String displayName;
    private String description;
    private ImpactLevel impactLevel = ImpactLevel.MEDIUM;
    private float healthModifier = 1.0f;
    private float speedModifier = 1.0f;
    private float damageModifier = 1.0f;
    private final List<OriginAbility> abilities = new ArrayList<>();
    private final List<OriginPassive> passives = new ArrayList<>();
    private ResourceType resourceType;
    private final List<ItemStack> startingItems = new ArrayList<>();

    public OriginBuilder(String id) {
        this.id = ResourceLocation.parse(id);
    }

    public OriginBuilder setDisplayName(String name) {
        this.displayName = name;
        return this;
    }

    public OriginBuilder setDescription(String desc) {
        this.description = desc;
        return this;
    }

    public OriginBuilder setImpactLevel(ImpactLevel level) {
        this.impactLevel = level;
        return this;
    }

    public OriginBuilder setHealthModifier(float modifier) {
        this.healthModifier = modifier;
        return this;
    }

    public OriginBuilder setSpeedModifier(float modifier) {
        this.speedModifier = modifier;
        return this;
    }

    public OriginBuilder setDamageModifier(float modifier) {
        this.damageModifier = modifier;
        return this;
    }

    public OriginBuilder addAbility(OriginAbility ability) {
        this.abilities.add(ability);
        return this;
    }

    public OriginBuilder addPassive(OriginPassive passive) {
        this.passives.add(passive);
        return this;
    }

    public OriginBuilder setResourceType(ResourceType type) {
        this.resourceType = type;
        return this;
    }

    public OriginBuilder addStartingItem(ItemStack item) {
        this.startingItems.add(item);
        return this;
    }

    public Origin build() {
        Origin origin = new Origin(id, displayName, description, impactLevel);
        origin.setHealthModifier(healthModifier);
        origin.setSpeedModifier(speedModifier);
        origin.setDamageModifier(damageModifier);
        origin.setResourceType(resourceType);
        abilities.forEach(origin::addAbility);
        passives.forEach(origin::addPassive);
        return origin;
    }
}
