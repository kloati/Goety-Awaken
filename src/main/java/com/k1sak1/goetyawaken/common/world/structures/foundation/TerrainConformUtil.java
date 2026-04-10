package com.k1sak1.goetyawaken.common.world.structures.foundation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TerrainConformUtil {
    private static final int MAX_STRUCTS_PER_CHUNK = 8;
    private static final int MAX_FILL_DEPTH = 150;
    private static final int MAX_COLUMNS_PER_CHUNK = 12800;

    private static final TagKey<Biome> BIOME_IS_DESERT = TagKey.create(Registries.BIOME,
            new ResourceLocation("minecraft", "is_desert"));
    private static final TagKey<Biome> BIOME_IS_SWAMP = TagKey.create(Registries.BIOME,
            new ResourceLocation("minecraft", "is_swamp"));
    private static final TagKey<Biome> BIOME_IS_TAIGA = TagKey.create(Registries.BIOME,
            new ResourceLocation("minecraft", "is_taiga"));
    private static final TagKey<Biome> BIOME_IS_BADLANDS = TagKey.create(Registries.BIOME,
            new ResourceLocation("minecraft", "is_badlands"));

    public static void applyDuringSurface(WorldGenLevel level, StructureManager structureManager,
            ChunkAccess originChunk) {
        if (level instanceof WorldGenRegion region) {
            if (structureManager != null && originChunk != null) {
                if (!region.getLevel().dimension().equals(Level.OVERWORLD)) {
                    return;
                }

                ChunkPos originCp = originChunk.getPos();
                BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
                int processedStructs = 0;
                int minHeight = 0;
                int maxHeight = 512;

                for (StructureStart start : structureManager.startsForStructure(originCp, (s) -> true)) {
                    if (processedStructs >= MAX_STRUCTS_PER_CHUNK) {
                        return;
                    }

                    if (start != null && start.isValid() && !isUndergroundStructure(start, originChunk)) {
                        BoundingBox box = start.getBoundingBox();
                        if (box != null) {
                            int structureHeight = box.minY();
                            if (structureHeight >= minHeight && structureHeight <= maxHeight) {
                                Structure structure = start.getStructure();
                                if (structure != null) {
                                    ResourceLocation id = getStructureId(structureManager, structure);
                                    if (id != null) {

                                        if (!FoundationConfigManager.isStructureWhitelisted(id)) {
                                            continue;
                                        }

                                        List<BoundingBox> pieceBoxes = collectPieceBoxes(start);
                                        if (!pieceBoxes.isEmpty()) {
                                            ChunkPos cp = originChunk.getPos();
                                            int chunkMinX = cp.getMinBlockX();
                                            int chunkMinZ = cp.getMinBlockZ();
                                            int minBuildY = originChunk.getMinBuildHeight();
                                            int maxY = originChunk.getMaxBuildHeight() - 1;

                                            int[][] baseTerrainYMap = computeBaseTerrainYMap(originChunk, chunkMinX,
                                                    chunkMinZ, minBuildY, maxY, box.minY(), mpos);
                                            boolean[][] domain = initBoolMap(true);

                                            CavityData cavityData = detectCavities(originChunk, pieceBoxes,
                                                    baseTerrainYMap, domain,
                                                    chunkMinX, chunkMinZ, minBuildY, maxY, box.minY(), box.minY() + 20,
                                                    mpos);

                                            int[][] filledTopMap = initIntMap(Integer.MIN_VALUE);
                                            if (cavityData.any) {
                                                fillVerticalCavities(region, originChunk, cavityData, filledTopMap,
                                                        chunkMinX, chunkMinZ, minBuildY, maxY, mpos, pieceBoxes);
                                            }

                                            applySingleSlopeWithOgCompare(region, originChunk, cp, box, baseTerrainYMap,
                                                    filledTopMap, domain, cavityData, chunkMinX, chunkMinZ, minBuildY,
                                                    maxY, mpos, pieceBoxes);

                                            ++processedStructs;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static void applySingleSlopeWithOgCompare(WorldGenRegion region, ChunkAccess chunk, ChunkPos cp,
            BoundingBox box, int[][] ogTerrainYMap, int[][] filledTopMap, boolean[][] domain, CavityData cavityData,
            int chunkMinX, int chunkMinZ, int minBuildY, int maxY, BlockPos.MutableBlockPos mpos,
            List<BoundingBox> pieceBoxes) {

        int OUTER_RADIUS = 16;
        int columnsSlope = 0;
        SurfacePalette palette = sampleChunkSurfacePalette(chunk, chunkMinX, chunkMinZ);

        for (int lx = 0; lx < 16; ++lx) {
            for (int lz = 0; lz < 16; ++lz) {
                if (domain[lx][lz]) {
                    int worldX = chunkMinX + lx;
                    int worldZ = chunkMinZ + lz;
                    int terrainY = ogTerrainYMap[lx][lz];

                    boolean intersectsStructureColumn = false;
                    for (BoundingBox pb : pieceBoxes) {
                        if (worldX >= pb.minX() && worldX <= pb.maxX() && worldZ >= pb.minZ() && worldZ <= pb.maxZ()) {
                            intersectsStructureColumn = true;
                            break;
                        }
                    }

                    if (!intersectsStructureColumn) {
                        int surfaceY = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, worldX, worldZ);
                        if (surfaceY < minBuildY) {
                            surfaceY = minBuildY;
                        }
                        if (surfaceY > maxY) {
                            surfaceY = maxY;
                        }

                        boolean hasBeard = false;
                        if (surfaceY > terrainY) {
                            mpos.set(worldX, surfaceY, worldZ);
                            BlockState topState = chunk.getBlockState(mpos);
                            if (!topState.getFluidState().isEmpty()) {
                                hasBeard = false;
                            } else if (topState.is(Blocks.POWDER_SNOW)) {
                                hasBeard = false;
                            } else if (topState.isSolid()) {
                                hasBeard = true;
                            }
                        }

                        if (!hasBeard) {
                            BoundingBox nearestPiece = null;
                            double bestHoriz = Double.MAX_VALUE;
                            int lowestBaseY = Integer.MAX_VALUE;

                            for (BoundingBox pb : pieceBoxes) {
                                int dx = 0;
                                if (worldX < pb.minX()) {
                                    dx = pb.minX() - worldX;
                                } else if (worldX > pb.maxX()) {
                                    dx = worldX - pb.maxX();
                                }

                                int dz = 0;
                                if (worldZ < pb.minZ()) {
                                    dz = pb.minZ() - worldZ;
                                } else if (worldZ > pb.maxZ()) {
                                    dz = worldZ - pb.maxZ();
                                }

                                double horiz = Math.sqrt((double) dx * (double) dx + (double) dz * (double) dz);
                                if (horiz < bestHoriz) {
                                    bestHoriz = horiz;
                                    nearestPiece = pb;
                                }

                                if (pb.minY() < lowestBaseY) {
                                    lowestBaseY = pb.minY();
                                }
                            }

                            if (nearestPiece != null && bestHoriz <= (double) 16.0F) {
                                int baseStructY = lowestBaseY;
                                if (baseStructY < minBuildY) {
                                    baseStructY = minBuildY;
                                }
                                if (baseStructY > maxY) {
                                    baseStructY = maxY;
                                }

                                double t = bestHoriz / (double) 16.0F;
                                t = Math.max(0.0F, Math.min(1.0F, t));

                                double shapeNoise = perlin2D((double) worldX * 0.065, (double) worldZ * 0.065);
                                double exponent = (double) 0.5F + (shapeNoise + (double) 1.0F) * (double) 0.5F * 0.6;
                                double baseCurve = Math.pow(t, exponent);

                                int dely = baseStructY - terrainY;
                                if (dely < 0) {
                                    dely = 0;
                                }

                                double H_THRESHOLD = (double) 8.0F;
                                double h = (double) dely / (double) 8.0F;
                                h = Math.max(0.0F, Math.min(1.0F, h));

                                double wSmall = (double) 1.0F - h;
                                double smallBoost = 0.2 * ((double) 1.0F - t) * ((double) 1.0F - t);
                                double largeMod = 0.08 * Math.sin(t * Math.PI);
                                double curve = baseCurve + wSmall * smallBoost + h * largeMod;
                                curve = Math.max(0.0F, Math.min(1.0F, curve));

                                double microNoise = perlin2D((double) worldX * 0.15, (double) worldZ * 0.15);
                                double verticalOffset = microNoise * 2.6;
                                int targetY = (int) Math.round((double) baseStructY * ((double) 1.0F - curve)
                                        + (double) terrainY * curve + verticalOffset);

                                int dy = targetY - terrainY;
                                double tMin = 0.12;
                                double tMax = 0.88;
                                if (dy >= 10) {
                                    tMin = 0.03;
                                    tMax = 0.95;
                                } else if (dy >= 6) {
                                    tMin = 0.07;
                                    tMax = 0.92;
                                }

                                if (t > tMin && t < tMax) {
                                    double cellNoise = perlin2D((double) worldX * 0.02, (double) worldZ * 0.02);
                                    int cellSize = 1 + (int) Math
                                            .floor((cellNoise + (double) 1.0F) * (double) 0.5F * (double) 3.0F);
                                    cellSize = Math.max(1, Math.min(3, cellSize));

                                    int cellX = Math.floorDiv(worldX, cellSize);
                                    int cellZ = Math.floorDiv(worldZ, cellSize);
                                    double zoneNoise = perlin2D((double) cellX * 0.35, (double) cellZ * 0.35);

                                    int hardStep = dy >= 10 ? 4 : 3;
                                    int softStep = dy >= 10 ? 3 : 2;
                                    if (zoneNoise > 0.35) {
                                        targetY = targetY / hardStep * hardStep;
                                    } else if (zoneNoise > 0.1) {
                                        targetY = targetY / softStep * softStep;
                                    }
                                }

                                if (targetY > terrainY) {
                                    if (targetY > maxY) {
                                        targetY = maxY;
                                    }

                                    int height = targetY - terrainY;
                                    if (height > 0) {
                                        ++columnsSlope;
                                        if (columnsSlope > MAX_COLUMNS_PER_CHUNK) {
                                            return;
                                        }

                                        int coreDepth = height + 4;
                                        BlockState[] core = new BlockState[coreDepth];
                                        for (int i = 0; i < coreDepth; ++i) {
                                            int sampleY = terrainY - i;
                                            if (sampleY < minBuildY) {
                                                core[i] = Blocks.STONE.defaultBlockState();
                                            } else {
                                                mpos.set(worldX, sampleY, worldZ);
                                                BlockState s = chunk.getBlockState(mpos);
                                                if (!s.isAir() && (s.isSolid() || s.is(Blocks.POWDER_SNOW))
                                                        && s.getFluidState().isEmpty()) {
                                                    core[i] = s;
                                                } else {
                                                    core[i] = Blocks.STONE.defaultBlockState();
                                                }
                                            }
                                        }

                                        int placedTopY = Integer.MIN_VALUE;
                                        for (int i = 0; i < height; ++i) {
                                            int y = terrainY + 1 + i;
                                            if (y > maxY) {
                                                break;
                                            }

                                            mpos.set(worldX, y, worldZ);
                                            BlockState existing = chunk.getBlockState(mpos);
                                            if (canSlopeReplace(existing)
                                                    && (isFillable(existing) || existing.is(Blocks.POWDER_SNOW))) {
                                                int coreIndex = Math.min(coreDepth - 1, height - 1 - i);
                                                BlockState toPlace = core[coreIndex];
                                                chunk.setBlockState(mpos, toPlace, false);
                                                convertOrganicBelowIfNeeded(chunk, worldX, y, worldZ, mpos);
                                                placedTopY = y;
                                            }
                                        }

                                        if (placedTopY != Integer.MIN_VALUE) {
                                            Holder<Biome> biome = region.getBiome(BlockPos.containing((double) worldX,
                                                    (double) placedTopY, (double) worldZ));
                                            BlockState biomeTop = Blocks.GRASS_BLOCK.defaultBlockState();
                                            BlockState biomeFiller = Blocks.DIRT.defaultBlockState();

                                            if (biome != null) {
                                                if (biome.is(BIOME_IS_DESERT)) {
                                                    biomeTop = Blocks.SAND.defaultBlockState();
                                                    biomeFiller = Blocks.SAND.defaultBlockState();
                                                } else if (biome.is(BIOME_IS_BADLANDS)) {
                                                    biomeTop = Blocks.RED_SAND.defaultBlockState();
                                                    biomeFiller = Blocks.TERRACOTTA.defaultBlockState();
                                                } else if (biome.is(BIOME_IS_TAIGA)) {
                                                    biomeTop = Blocks.PODZOL.defaultBlockState();
                                                    biomeFiller = Blocks.DIRT.defaultBlockState();
                                                } else if (biome.value().coldEnoughToSnow(BlockPos.containing(
                                                        (double) worldX, (double) placedTopY, (double) worldZ))) {
                                                    biomeTop = Blocks.SNOW_BLOCK.defaultBlockState();
                                                    biomeFiller = Blocks.DIRT.defaultBlockState();
                                                }
                                            }

                                            boolean isUnderFluid = false;
                                            if (placedTopY + 1 <= maxY) {
                                                mpos.set(worldX, placedTopY + 1, worldZ);
                                                BlockState above = chunk.getBlockState(mpos);
                                                if (!above.getFluidState().isEmpty()) {
                                                    isUnderFluid = true;
                                                }
                                            }

                                            if (!isUnderFluid) {
                                                mpos.set(worldX, placedTopY, worldZ);
                                                chunk.setBlockState(mpos, biomeTop, false);

                                                for (int i = 1; i <= 2; ++i) {
                                                    int yy = placedTopY - i;
                                                    if (yy < minBuildY) {
                                                        break;
                                                    }
                                                    mpos.set(worldX, yy, worldZ);
                                                    BlockState existing = chunk.getBlockState(mpos);
                                                    if (!existing.isSolid() || isFillable(existing)) {
                                                        chunk.setBlockState(mpos, biomeFiller, false);
                                                    }
                                                }
                                            }

                                            convertOrganicBelowIfNeeded(chunk, worldX, placedTopY, worldZ, mpos);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static ResourceLocation getStructureId(StructureManager structureManager, Structure structure) {
        return structureManager.registryAccess().registryOrThrow(Registries.STRUCTURE).getKey(structure);
    }

    private static List<BoundingBox> collectPieceBoxes(StructureStart start) {
        List<BoundingBox> pieceBoxes = new ArrayList<>();
        for (StructurePiece piece : start.getPieces()) {
            BoundingBox pb = piece.getBoundingBox();
            if (pb != null) {
                pieceBoxes.add(pb);
            }
        }
        return pieceBoxes;
    }

    private static int[][] initIntMap(int fill) {
        int[][] map = new int[16][16];
        for (int lx = 0; lx < 16; ++lx) {
            for (int lz = 0; lz < 16; ++lz) {
                map[lx][lz] = fill;
            }
        }
        return map;
    }

    private static boolean[][] initBoolMap(boolean fill) {
        boolean[][] map = new boolean[16][16];
        for (int lx = 0; lx < 16; ++lx) {
            for (int lz = 0; lz < 16; ++lz) {
                map[lx][lz] = fill;
            }
        }
        return map;
    }

    private static int[][] computeBaseTerrainYMap(ChunkAccess chunk, int chunkMinX, int chunkMinZ,
            int minBuildY, int maxY, int baseY, BlockPos.MutableBlockPos mpos) {
        int[][] baseTerrainYMap = new int[16][16];
        for (int lx = 0; lx < 16; ++lx) {
            for (int lz = 0; lz < 16; ++lz) {
                int x = chunkMinX + lx;
                int z = chunkMinZ + lz;
                baseTerrainYMap[lx][lz] = findNaturalTerrainY(chunk, x, z, minBuildY, maxY, baseY, mpos);
            }
        }
        return baseTerrainYMap;
    }

    private static CavityData detectCavities(ChunkAccess chunk, List<BoundingBox> pieceBoxes,
            int[][] ogTerrainYMap, boolean[][] domain, int chunkMinX, int chunkMinZ,
            int minBuildY, int maxY, int baseY, int undersideY, BlockPos.MutableBlockPos mpos) {

        boolean[][] cavity = initBoolMap(false);
        int[][] topY = initIntMap(Integer.MIN_VALUE);
        int[][] botY = initIntMap(Integer.MIN_VALUE);
        boolean any = false;

        for (int lx = 0; lx < 16; ++lx) {
            for (int lz = 0; lz < 16; ++lz) {
                if (domain[lx][lz]) {
                    int x = chunkMinX + lx;
                    int z = chunkMinZ + lz;
                    boolean hasPiece = hasStructureAbove(chunk, pieceBoxes, x, z, minBuildY, maxY, mpos);
                    int top = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
                    if (top < minBuildY) {
                        top = minBuildY;
                    }
                    if (top > maxY) {
                        top = maxY;
                    }

                    boolean hasBeard = top > ogTerrainYMap[lx][lz];
                    if (hasPiece || hasBeard) {
                        int surfaceY = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) + 1;
                        if (surfaceY < minBuildY) {
                            surfaceY = minBuildY;
                        }
                        if (surfaceY > maxY) {
                            surfaceY = maxY;
                        }

                        int yStart = clampInt(baseY + FoundationConfigManager.getScanAbove(), minBuildY, maxY);
                        int yEnd = clampInt(baseY - FoundationConfigManager.getScanBelow(), minBuildY, maxY);
                        int undersideClampY = clampInt(undersideY, minBuildY, maxY);

                        mpos.set(x, undersideClampY, z);
                        if (chunk.getBlockState(mpos).isSolid()) {
                            undersideClampY = clampInt(undersideClampY - 1, minBuildY, maxY);
                        }

                        int yAlto = Integer.MIN_VALUE;
                        for (int y = yStart; y >= yEnd; --y) {
                            if (y <= undersideClampY) {
                                mpos.set(x, y, z);
                                BlockState s = chunk.getBlockState(mpos);
                                if (!s.isSolid() && y < surfaceY && (s.getFluidState().isEmpty() || s.isAir())) {
                                    yAlto = y;
                                    break;
                                }
                            }
                        }

                        if (yAlto == Integer.MIN_VALUE && undersideClampY >= minBuildY && undersideClampY < surfaceY) {
                            mpos.set(x, undersideClampY, z);
                            BlockState s = chunk.getBlockState(mpos);
                            if (!s.isSolid() && (s.getFluidState().isEmpty() || s.isAir())) {
                                yAlto = undersideClampY;
                            }
                        }

                        if (yAlto != Integer.MIN_VALUE) {
                            int yBasso = Integer.MIN_VALUE;
                            for (int y = yAlto; y >= minBuildY; --y) {
                                mpos.set(x, y, z);
                                BlockState s = chunk.getBlockState(mpos);
                                if (s.isSolid()) {
                                    yBasso = y;
                                    break;
                                }
                            }

                            if (yBasso != Integer.MIN_VALUE && yBasso < yAlto) {
                                cavity[lx][lz] = true;
                                topY[lx][lz] = yAlto;
                                botY[lx][lz] = yBasso;
                                any = true;
                            }
                        }
                    }
                }
            }
        }

        return new CavityData(cavity, topY, botY, any);
    }

    private static int fillVerticalCavities(WorldGenRegion region, ChunkAccess chunk, CavityData cavityData,
            int[][] filledTopMap, int chunkMinX, int chunkMinZ, int minBuildY, int maxY,
            BlockPos.MutableBlockPos mpos, List<BoundingBox> pieceBoxes) {

        int columnsFill = 0;
        for (int lx = 0; lx < 16; ++lx) {
            for (int lz = 0; lz < 16; ++lz) {
                if (cavityData.cavity[lx][lz]) {
                    ++columnsFill;
                    if (columnsFill > MAX_COLUMNS_PER_CHUNK) {
                        return 1;
                    }

                    int x = chunkMinX + lx;
                    int z = chunkMinZ + lz;
                    int yAlto = cavityData.topY[lx][lz];
                    int yBasso = cavityData.botY[lx][lz];
                    int fillFrom = yBasso;
                    int fillTo = yAlto + 4;

                    if (yBasso < minBuildY) {
                        fillFrom = minBuildY;
                    }
                    if (fillTo > maxY) {
                        fillTo = maxY;
                    }

                    int localStructMinY = Integer.MAX_VALUE;
                    for (BoundingBox pb : pieceBoxes) {
                        if (x >= pb.minX() && x <= pb.maxX() && z >= pb.minZ() && z <= pb.maxZ()
                                && pb.minY() < localStructMinY) {
                            localStructMinY = pb.minY();
                        }
                    }

                    if (localStructMinY != Integer.MAX_VALUE && fillTo > localStructMinY) {
                        fillTo = localStructMinY;
                    }

                    int minFrom = fillTo - MAX_FILL_DEPTH + 1;
                    if (fillFrom < minFrom) {
                        fillFrom = minFrom;
                    }

                    if (fillFrom <= fillTo) {
                        int topPlacedY = Integer.MIN_VALUE;
                        for (int y = fillFrom; y <= fillTo; ++y) {
                            mpos.set(x, y, z);
                            BlockState existing = chunk.getBlockState(mpos);
                            if (canSlopeReplace(existing)
                                    && (isFillable(existing) || existing.is(Blocks.POWDER_SNOW))) {
                                mpos.set(x, y - 1, z);
                                BlockState below = chunk.getBlockState(mpos);
                                if (isOrganicTop(below)) {
                                    chunk.setBlockState(mpos, Blocks.DIRT.defaultBlockState(), false);
                                }

                                mpos.set(x, y, z);
                                chunk.setBlockState(mpos, Blocks.STONE.defaultBlockState(), false);
                                convertOrganicBelowIfNeeded(chunk, x, y, z, mpos);
                                topPlacedY = y;
                            }
                        }

                        if (topPlacedY != Integer.MIN_VALUE) {
                            filledTopMap[lx][lz] = topPlacedY;
                            applySurfaceLayer(region, chunk, x, topPlacedY, z, mpos, true);
                        }
                    }
                }
            }
        }
        return 0;
    }

    private static int findNaturalTerrainY(ChunkAccess chunk, int x, int z, int minBuildY, int maxY, int structureBaseY,
            BlockPos.MutableBlockPos mpos) {
        int y = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
        if (y > structureBaseY) {
            y = structureBaseY;
        }

        for (int yy = y; yy >= minBuildY; --yy) {
            mpos.set(x, yy, z);
            BlockState s = chunk.getBlockState(mpos);
            if (s.getFluidState().isEmpty() && isNaturalTerrain(s)) {
                return yy;
            }
        }
        return minBuildY;
    }

    private static void applySurfaceLayer(WorldGenRegion region, ChunkAccess chunk, int x, int topPlacedY, int z,
            BlockPos.MutableBlockPos mpos, boolean fromCavity) {
        int minY = chunk.getMinBuildHeight();
        int maxY = chunk.getMaxBuildHeight() - 1;

        if (topPlacedY >= minY && topPlacedY <= maxY) {
            int surfaceY = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
            if (surfaceY < minY) {
                surfaceY = minY;
            }
            if (surfaceY > maxY) {
                surfaceY = maxY;
            }

            mpos.set(x, surfaceY, z);
            BlockState sampledTop = chunk.getBlockState(mpos);
            BlockState sampledUnder = sampledTop;

            for (int dy = 1; dy <= 4; ++dy) {
                int yy = surfaceY - dy;
                if (yy < minY) {
                    break;
                }
                mpos.set(x, yy, z);
                BlockState s = chunk.getBlockState(mpos);
                if (!s.isAir() && s.isSolid()) {
                    sampledUnder = s;
                    break;
                }
            }

            Holder<Biome> biome = chunk.getNoiseBiome(x >> 2, topPlacedY >> 2, z >> 2);
            if (biome != null && biome.value()
                    .coldEnoughToSnow(BlockPos.containing((double) x, (double) topPlacedY, (double) z))) {
                sampledTop = Blocks.SNOW_BLOCK.defaultBlockState();
                sampledUnder = Blocks.SNOW_BLOCK.defaultBlockState();
            }

            if (fromCavity && isOrganicTop(sampledTop)) {
                sampledTop = Blocks.DIRT.defaultBlockState();
            }

            mpos.set(x, topPlacedY, z);
            chunk.setBlockState(mpos, sampledTop, false);

            for (int i = 1; i <= 2; ++i) {
                int y = topPlacedY - i;
                if (y < minY) {
                    break;
                }
                mpos.set(x, y, z);
                BlockState existing = chunk.getBlockState(mpos);
                if (!existing.isSolid() || isNaturalTerrain(existing)) {
                    chunk.setBlockState(mpos, sampledUnder, false);
                }
            }
        }
    }

    private static SurfacePalette sampleChunkSurfacePalette(ChunkAccess chunk, int chunkMinX, int chunkMinZ) {
        int minY = chunk.getMinBuildHeight();
        int maxY = chunk.getMaxBuildHeight() - 1;
        BlockState fallbackTop = Blocks.GRASS_BLOCK.defaultBlockState();
        BlockState fallbackFiller = Blocks.DIRT.defaultBlockState();
        Map<Block, Integer> topCount = new HashMap<>();
        Map<Block, Integer> fillerCount = new HashMap<>();
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();

        for (int sx = 0; sx < 16; sx += 4) {
            for (int sz = 0; sz < 16; sz += 4) {
                int x = chunkMinX + sx;
                int z = chunkMinZ + sz;
                int surfaceY = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
                if (surfaceY < minY) {
                    surfaceY = minY;
                }
                if (surfaceY > maxY) {
                    surfaceY = maxY;
                }

                mpos.set(x, surfaceY, z);
                BlockState surfaceState = chunk.getBlockState(mpos);
                if (surfaceState.getFluidState().isEmpty()) {
                    BlockState top = null;
                    for (int dy = 0; dy <= 8; ++dy) {
                        int y = surfaceY - dy;
                        if (y < minY) {
                            break;
                        }
                        mpos.set(x, y, z);
                        BlockState s = chunk.getBlockState(mpos);
                        if (s.getFluidState().isEmpty() && !s.isAir() && s.isSolid() && isNaturalSurfaceCandidate(s)) {
                            top = s;
                            break;
                        }
                    }

                    if (top != null) {
                        BlockState filler = null;
                        for (int dy = 1; dy <= 5; ++dy) {
                            int y = surfaceY - dy - 1;
                            if (y < minY) {
                                break;
                            }
                            mpos.set(x, y, z);
                            BlockState s = chunk.getBlockState(mpos);
                            Holder<Biome> biomeHolder = chunk.getNoiseBiome(x >> 2, surfaceY >> 2, z >> 2);
                            if (!s.isAir() && s.isSolid() && isNaturalFillerCandidate(s, biomeHolder, y)) {
                                filler = s;
                                break;
                            }
                        }

                        if (filler == null) {
                            filler = fallbackFiller;
                        }

                        incr(topCount, top.getBlock());
                        incr(fillerCount, filler.getBlock());
                    }
                }
            }
        }

        Block topBlock = pickMostFrequent(topCount);
        Block fillerBlock = pickMostFrequent(fillerCount);
        if (topBlock == null) {
            topBlock = fallbackTop.getBlock();
        }
        if (fillerBlock == null) {
            fillerBlock = fallbackFiller.getBlock();
        }

        return new SurfacePalette(topBlock.defaultBlockState(), fillerBlock.defaultBlockState());
    }

    private static void incr(Map<Block, Integer> map, Block b) {
        Integer v = map.get(b);
        if (v == null) {
            v = 0;
        }
        map.put(b, v + 1);
    }

    private static Block pickMostFrequent(Map<Block, Integer> map) {
        Block best = null;
        int bestV = -1;
        for (Map.Entry<Block, Integer> e : map.entrySet()) {
            if (e.getValue() > bestV) {
                bestV = e.getValue();
                best = e.getKey();
            }
        }
        return best;
    }

    private static boolean isNaturalSurfaceCandidate(BlockState s) {
        if (s.is(BlockTags.LEAVES))
            return true;
        if (s.is(BlockTags.SNOW))
            return true;
        if (s.is(BlockTags.DIRT))
            return true;
        if (s.is(BlockTags.SAND))
            return true;
        if (s.is(BlockTags.BASE_STONE_OVERWORLD))
            return true;
        return s.is(Blocks.GRASS_BLOCK) || s.is(Blocks.PODZOL) || s.is(Blocks.MYCELIUM) ||
                s.is(Blocks.SNOW_BLOCK) || s.is(Blocks.GRAVEL);
    }

    private static boolean isNaturalFillerCandidate(BlockState s, Holder<Biome> biome, int y) {
        if (s.is(BlockTags.DIRT))
            return true;
        if (s.is(BlockTags.SAND))
            return true;

        if (y <= 0) {
            if (s.is(Blocks.DEEPSLATE) || s.is(Blocks.TUFF)) {
                return true;
            }
        } else if (s.is(BlockTags.BASE_STONE_OVERWORLD)) {
            return true;
        }

        if (biome != null) {
            if (biome.is(BIOME_IS_BADLANDS) && (s.is(BlockTags.TERRACOTTA) || s.is(Blocks.RED_SAND))) {
                return true;
            }
            if (biome.is(BIOME_IS_DESERT)
                    && (s.is(Blocks.SANDSTONE) || s.is(Blocks.SMOOTH_SANDSTONE) || s.is(Blocks.CUT_SANDSTONE))) {
                return true;
            }
            if (biome.is(BIOME_IS_SWAMP) && (s.is(Blocks.MUD) || s.is(Blocks.CLAY))) {
                return true;
            }
            if (biome.is(BIOME_IS_TAIGA)
                    && (s.is(Blocks.COARSE_DIRT) || s.is(Blocks.PODZOL) || s.is(Blocks.ROOTED_DIRT))) {
                return true;
            }
        }

        return s.is(Blocks.GRAVEL) || s.is(Blocks.CLAY) || s.is(Blocks.TERRACOTTA);
    }

    private static boolean isUndergroundStructure(StructureStart start, ChunkAccess chunk) {
        BoundingBox box = start.getBoundingBox();
        if (box == null) {
            return true;
        }

        int chunkMinX = chunk.getPos().getMinBlockX();
        int chunkMinZ = chunk.getPos().getMinBlockZ();
        int minY = chunk.getMinBuildHeight();
        int maxY = chunk.getMaxBuildHeight() - 1;

        int samples = 0;
        int undergroundHits = 0;

        for (int sx = box.minX(); sx <= box.maxX(); sx += 4) {
            for (int sz = box.minZ(); sz <= box.maxZ(); sz += 4) {
                int surface = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, sx, sz);
                if (surface < minY) {
                    surface = minY;
                }
                if (surface > maxY) {
                    surface = maxY;
                }

                if (box.maxY() < surface - 2) {
                    ++undergroundHits;
                }

                ++samples;
                if (samples >= 16) {
                    break;
                }
            }
            if (samples >= 16) {
                break;
            }
        }

        return (double) undergroundHits >= (double) samples * 0.6;
    }

    private static boolean canSlopeReplace(BlockState s) {
        if (s.isAir())
            return true;
        if (!s.getFluidState().isEmpty())
            return true;
        if (!s.is(Blocks.POWDER_SNOW) && !s.is(Blocks.ICE) && !s.is(Blocks.PACKED_ICE) &&
                !s.is(Blocks.BLUE_ICE) && !s.is(Blocks.SNOW)) {
            return s.canBeReplaced();
        }
        return true;
    }

    private static boolean isFillable(BlockState s) {
        if (s.isAir())
            return true;
        if (!s.getFluidState().isEmpty())
            return true;
        return s.is(Blocks.POWDER_SNOW) ? true : s.canBeReplaced();
    }

    private static int clampInt(int v, int lo, int hi) {
        if (v < lo)
            return lo;
        return v > hi ? hi : v;
    }

    private static boolean isOrganicTop(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.PODZOL) ||
                state.is(Blocks.MYCELIUM) || state.is(Blocks.FARMLAND) || state.is(Blocks.SNOW_BLOCK);
    }

    private static void convertOrganicBelowIfNeeded(ChunkAccess chunk, int x, int y, int z,
            BlockPos.MutableBlockPos mpos) {
        int belowY = y - 1;
        if (belowY >= chunk.getMinBuildHeight()) {
            mpos.set(x, y, z);
            BlockState placed = chunk.getBlockState(mpos);
            if (placed.isSolid()) {
                if (!placed.is(Blocks.STONE) && !placed.is(Blocks.DIRT) && !placed.is(Blocks.SAND) &&
                        !placed.is(Blocks.RED_SAND) && !placed.is(Blocks.TERRACOTTA) && !placed.is(Blocks.GRAVEL) &&
                        !placed.is(Blocks.CLAY) && !placed.is(Blocks.MUD)) {
                    if (!placed.is(Blocks.GRASS_BLOCK) && !placed.is(Blocks.PODZOL) &&
                            !placed.is(Blocks.MYCELIUM) && !placed.is(Blocks.SNOW_BLOCK)) {
                        mpos.set(x, belowY, z);
                        BlockState below = chunk.getBlockState(mpos);
                        if (below.isSolid()) {
                            if (below.is(Blocks.GRASS_BLOCK) || below.is(Blocks.PODZOL) ||
                                    below.is(Blocks.MYCELIUM) || below.is(Blocks.ROOTED_DIRT) ||
                                    below.is(Blocks.COARSE_DIRT) || below.is(Blocks.FARMLAND)) {
                                chunk.setBlockState(mpos, Blocks.DIRT.defaultBlockState(), false);
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean isNaturalTerrain(BlockState state) {
        return state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(BlockTags.DIRT) ||
                state.is(BlockTags.SAND) || state.is(BlockTags.TERRACOTTA) || state.is(BlockTags.SNOW) ||
                state.is(Blocks.POWDER_SNOW) || state.is(Blocks.GRAVEL) || state.is(Blocks.CLAY) ||
                state.is(Blocks.MUD) || state.is(Blocks.WATER) || state.is(Blocks.LAVA);
    }

    private static boolean hasStructureAbove(ChunkAccess chunk, List<BoundingBox> pieceBoxes,
            int x, int z, int minY, int maxY, BlockPos.MutableBlockPos mpos) {
        for (BoundingBox pb : pieceBoxes) {
            if (x >= pb.minX() && x <= pb.maxX() && z >= pb.minZ() && z <= pb.maxZ()) {
                int yStart = Math.min(pb.maxY(), maxY);
                int yEnd = Math.max(pb.minY(), minY);

                for (int y = yStart; y >= yEnd; --y) {
                    mpos.set(x, y, z);
                    BlockState s = chunk.getBlockState(mpos);
                    if (!s.isAir() && !s.canBeReplaced()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static double fade(double t) {
        return t * t * t * (t * (t * (double) 6.0F - (double) 15.0F) + (double) 10.0F);
    }

    private static double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }

    private static double grad(int hash, double x, double y) {
        int h = hash & 3;
        double u = h < 2 ? x : y;
        double v = h < 2 ? y : x;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }

    private static int hash(int x, int y) {
        int h = x * 374761393 + y * 668265263;
        h = (h ^ h >> 13) * 1274126177;
        return h;
    }

    private static double perlin2D(double x, double y) {
        int x0 = (int) Math.floor(x);
        int x1 = x0 + 1;
        int y0 = (int) Math.floor(y);
        int y1 = y0 + 1;
        double sx = fade(x - (double) x0);
        double sy = fade(y - (double) y0);
        double n0 = grad(hash(x0, y0), x - (double) x0, y - (double) y0);
        double n1 = grad(hash(x1, y0), x - (double) x1, y - (double) y0);
        double ix0 = lerp(n0, n1, sx);
        n0 = grad(hash(x0, y1), x - (double) x0, y - (double) y1);
        n1 = grad(hash(x1, y1), x - (double) x1, y - (double) y1);
        double ix1 = lerp(n0, n1, sx);
        return lerp(ix0, ix1, sy);
    }

    private static final class SurfacePalette {
        final BlockState top;
        final BlockState filler;

        SurfacePalette(BlockState top, BlockState filler) {
            this.top = top;
            this.filler = filler;
        }
    }

    private static final class CavityData {
        final boolean[][] cavity;
        final int[][] topY;
        final int[][] botY;
        final boolean any;

        private CavityData(boolean[][] cavity, int[][] topY, int[][] botY, boolean any) {
            this.cavity = cavity;
            this.topY = topY;
            this.botY = botY;
            this.any = any;
        }
    }
}
