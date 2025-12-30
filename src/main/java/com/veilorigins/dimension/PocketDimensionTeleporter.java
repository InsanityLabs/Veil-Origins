package com.veilorigins.dimension;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;

public class PocketDimensionTeleporter {
    public static final ResourceKey<Level> POCKET_DIMENSION = ResourceKey.create(
        net.minecraft.core.registries.Registries.DIMENSION,
        Identifier.fromNamespaceAndPath("veil_origins", "pocket_dimension")
    );

    public static final BlockPos SPAWN_POS = new BlockPos(0, 64, 0);
}
