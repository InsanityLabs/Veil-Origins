package com.veilorigins.dimension;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PocketDimensionChunkGenerator extends ChunkGenerator {
    public static final MapCodec<PocketDimensionChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
        instance -> instance.group(
            BiomeSource.CODEC.fieldOf("biome_source").forGetter(ChunkGenerator::getBiomeSource)
        ).apply(instance, PocketDimensionChunkGenerator::new)
    );

    public PocketDimensionChunkGenerator(BiomeSource biomeSource) {
        super(biomeSource);
    }

    @Override
    protected MapCodec<? extends ChunkGenerator> codec() {
        return CODEC;
    }

    @Override
    public void applyCarvers(WorldGenRegion region, long seed, RandomState randomState, 
                            net.minecraft.world.level.biome.BiomeManager biomeManager, 
                            StructureManager structureManager, ChunkAccess chunk) {
        // No carvers in pocket dimension
    }

    @Override
    public void buildSurface(WorldGenRegion region, StructureManager structureManager, 
                            RandomState randomState, ChunkAccess chunk) {
        // Generate floating platform at spawn
        ChunkPos chunkPos = chunk.getPos();
        
        if (chunkPos.x == 0 && chunkPos.z == 0) {
            // Create 16x16 platform at y=64
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    BlockPos pos = new BlockPos(chunkPos.getMinBlockX() + x, 63, chunkPos.getMinBlockZ() + z);
                    chunk.setBlockState(pos, Blocks.END_STONE.defaultBlockState(), 0);
                    
                    // Add some glowstone for light
                    if ((x == 4 || x == 11) && (z == 4 || z == 11)) {
                        chunk.setBlockState(pos.above(), Blocks.GLOWSTONE.defaultBlockState(), 0);
                    }
                }
            }
        }
    }

    @Override
    public void spawnOriginalMobs(WorldGenRegion region) {
        // No mob spawning in pocket dimension
    }

    @Override
    public int getGenDepth() {
        return 384;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Blender blender, RandomState randomState, 
                                                        StructureManager structureManager, 
                                                        ChunkAccess chunk) {
        return CompletableFuture.completedFuture(chunk);
    }

    @Override
    public int getSeaLevel() {
        return -64;
    }

    @Override
    public int getMinY() {
        return -64;
    }

    @Override
    public int getBaseHeight(int x, int z, Heightmap.Types types, LevelHeightAccessor level, 
                            RandomState randomState) {
        return 64;
    }

    @Override
    public NoiseColumn getBaseColumn(int x, int z, LevelHeightAccessor level, RandomState randomState) {
        BlockState[] states = new BlockState[level.getHeight()];
        for (int i = 0; i < states.length; i++) {
            states[i] = Blocks.AIR.defaultBlockState();
        }
        return new NoiseColumn(level.getMinY(), states);
    }

    @Override
    public void addDebugScreenInfo(List<String> info, RandomState randomState, BlockPos pos) {
        info.add("Pocket Dimension");
    }
}
