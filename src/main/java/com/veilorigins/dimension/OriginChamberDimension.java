package com.veilorigins.dimension;

import com.veilorigins.VeilOrigins;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class OriginChamberDimension {
    public static final ResourceKey<Level> ORIGIN_CHAMBER_LEVEL = ResourceKey.create(
        Registries.DIMENSION,
        ResourceLocation.fromNamespaceAndPath(VeilOrigins.MOD_ID, "origin_chamber")
    );
    
    public static final ResourceKey<DimensionType> ORIGIN_CHAMBER_TYPE = ResourceKey.create(
        Registries.DIMENSION_TYPE,
        ResourceLocation.fromNamespaceAndPath(VeilOrigins.MOD_ID, "origin_chamber")
    );
}
