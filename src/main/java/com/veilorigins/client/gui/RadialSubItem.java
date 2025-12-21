package com.veilorigins.client.gui;

/**
 * Represents a subsection item in the radial menu.
 * Used for showing abilities preview in origin selection mode.
 */
public class RadialSubItem {
    private final String id;
    private final String name;
    private final String description;

    public RadialSubItem(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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
}
