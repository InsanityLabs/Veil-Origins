package com.veilorigins.client.gui;

/**
 * Font Awesome icon unicode mappings for the radial menu.
 * Using Font Awesome 7 Free Solid icons.
 */
public class IconHelper {

    // Generic icons for origins (Font Awesome Solid)
    public static final String ICON_GHOST = "\uf6e2"; // Ghost - for Veilborn
    public static final String ICON_MOUNTAIN = "\uf6fc"; // Mountain - for Stoneheart
    public static final String ICON_PAW = "\uf1b0"; // Paw - for Feralkin
    public static final String ICON_PORTAL = "\uf759"; // Portal/Door - for Riftwalker
    public static final String ICON_MOON = "\uf186"; // Moon - for Umbrakin
    public static final String ICON_VOID = "\uf7a4"; // Circle notch/void - for Voidtouched
    public static final String ICON_SNOWFLAKE = "\uf2dc"; // Snowflake - for Frostborn
    public static final String ICON_FIRE = "\uf06d"; // Fire - for Cindersoul
    public static final String ICON_WATER = "\uf773"; // Water - for Tidecaller
    public static final String ICON_STAR = "\uf005"; // Star - for Starborne
    public static final String ICON_CLOUD = "\uf0c2"; // Cloud - for Skyborn
    public static final String ICON_SEEDLING = "\uf4d8"; // Seedling - for Mycomorph/Dryad
    public static final String ICON_GEM = "\uf3a5"; // Gem - for Crystalline
    public static final String ICON_COG = "\uf013"; // Cog - for Technomancer
    public static final String ICON_EYE = "\uf06e"; // Eye - for Ethereal
    public static final String ICON_SKULL = "\uf54c"; // Skull - for Necromancer
    public static final String ICON_BOLT = "\uf0e7"; // Lightning bolt - for abilities
    public static final String ICON_HEART = "\uf004"; // Heart - for resource/health
    public static final String ICON_INFO = "\uf05a"; // Info circle
    public static final String ICON_QUESTION = "\uf059"; // Question - for unknown

    /**
     * Get icon for a given origin ID.
     */
    public static String getIconForOrigin(String originId) {
        if (originId == null)
            return ICON_QUESTION;

        // Match origin IDs to appropriate icons
        if (originId.contains("veilborn"))
            return ICON_GHOST;
        if (originId.contains("stoneheart"))
            return ICON_MOUNTAIN;
        if (originId.contains("feralkin"))
            return ICON_PAW;
        if (originId.contains("riftwalker"))
            return ICON_PORTAL;
        if (originId.contains("umbrakin"))
            return ICON_MOON;
        if (originId.contains("voidtouched"))
            return ICON_VOID;
        if (originId.contains("frostborn"))
            return ICON_SNOWFLAKE;
        if (originId.contains("cindersoul"))
            return ICON_FIRE;
        if (originId.contains("tidecaller"))
            return ICON_WATER;
        if (originId.contains("starborne"))
            return ICON_STAR;
        if (originId.contains("skyborn"))
            return ICON_CLOUD;
        if (originId.contains("mycomorph") || originId.contains("dryad"))
            return ICON_SEEDLING;
        if (originId.contains("crystalline"))
            return ICON_GEM;
        if (originId.contains("technomancer"))
            return ICON_COG;
        if (originId.contains("ethereal"))
            return ICON_EYE;
        if (originId.contains("necromancer"))
            return ICON_SKULL;

        return ICON_QUESTION; // Default for unknown
    }

    /**
     * Get icon for ability type.
     */
    public static String getIconForAbility(String abilityId) {
        if (abilityId == null)
            return ICON_BOLT;

        // Match common ability patterns
        if (abilityId.contains("fire") || abilityId.contains("flame") || abilityId.contains("burn"))
            return ICON_FIRE;
        if (abilityId.contains("water") || abilityId.contains("aqua") || abilityId.contains("ocean"))
            return ICON_WATER;
        if (abilityId.contains("ice") || abilityId.contains("frost") || abilityId.contains("freeze"))
            return ICON_SNOWFLAKE;
        if (abilityId.contains("lightning") || abilityId.contains("thunder") || abilityId.contains("bolt"))
            return ICON_BOLT;
        if (abilityId.contains("heal") || abilityId.contains("life") || abilityId.contains("regenerat"))
            return ICON_HEART;
        if (abilityId.contains("dead") || abilityId.contains("death") || abilityId.contains("necro"))
            return ICON_SKULL;
        if (abilityId.contains("summon") || abilityId.contains("raise"))
            return ICON_SKULL;
        if (abilityId.contains("plant") || abilityId.contains("nature") || abilityId.contains("grow"))
            return ICON_SEEDLING;
        if (abilityId.contains("portal") || abilityId.contains("teleport") || abilityId.contains("rift"))
            return ICON_PORTAL;
        if (abilityId.contains("shadow") || abilityId.contains("dark") || abilityId.contains("umbra"))
            return ICON_MOON;

        return ICON_BOLT; // Default ability icon
    }
}
