package com.veilorigins.registry;

import com.mojang.serialization.MapCodec;
import com.veilorigins.VeilOrigins;
import com.veilorigins.dimension.PocketDimensionChunkGenerator;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModDimensions {
    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATORS = 
        DeferredRegister.create(Registries.CHUNK_GENERATOR, VeilOrigins.MOD_ID);

    public static final Supplier<MapCodec<PocketDimensionChunkGenerator>> POCKET_DIMENSION = 
        CHUNK_GENERATORS.register("pocket_dimension", () -> PocketDimensionChunkGenerator.CODEC);
}
