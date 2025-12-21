package com.veilorigins.client.gui;

import com.veilorigins.api.Origin;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an item in the radial menu.
 * Can represent an origin (in origin select mode) or an ability (in ability
 * mode).
 */
public class RadialMenuItem {
    private final String id;
    private final String name;
    private final String description;
    private final int color;
    private final Origin origin; // Non-null if this represents an origin
    private final List<RadialSubItem> subsections = new ArrayList<>();

    private int abilityIndex = -1;
    private boolean onCooldown = false;
    private String specialAction = null;

    public RadialMenuItem(String id, String name, String description, int color, Origin origin) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.color = color;
        this.origin = origin;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getColor() {
        return color;
    }

    public Origin getOrigin() {
        return origin;
    }

    public List<RadialSubItem> getSubsections() {
        return subsections;
    }

    public void addSubsection(RadialSubItem subsection) {
        subsections.add(subsection);
    }

    public boolean hasSubsections() {
        return !subsections.isEmpty();
    }

    public int getAbilityIndex() {
        return abilityIndex;
    }

    public void setAbilityIndex(int abilityIndex) {
        this.abilityIndex = abilityIndex;
    }

    public boolean isOnCooldown() {
        return onCooldown;
    }

    public void setOnCooldown(boolean onCooldown) {
        this.onCooldown = onCooldown;
    }

    public String getSpecialAction() {
        return specialAction;
    }

    public void setSpecialAction(String specialAction) {
        this.specialAction = specialAction;
    }
}
