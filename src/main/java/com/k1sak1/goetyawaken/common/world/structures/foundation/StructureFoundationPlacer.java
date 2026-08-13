package com.k1sak1.goetyawaken.common.world.structures.foundation;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;

import java.util.List;

public class StructureFoundationPlacer {

    private static final int SLOPE_RADIUS = 14;
    private static final int MAX_COLUMNS_MODIFIED = 10000;
    private static final int MAX_FILL_DEPTH = 120;
    private static final int STRUCTS_PER_CHUNK = 8;

    private static final class StructureColumnInfo {
        final int[] baseYByColumn;
        final int footprintMinX, footprintMinZ, footprintMaxX, footprintMaxZ;
        final int globalBaseY;

        StructureColumnInfo(int[] b, int x0, int z0, int x1, int z1, int gy) {
            baseYByColumn = b;
            footprintMinX = x0;
            footprintMinZ = z0;
            footprintMaxX = x1;
            footprintMaxZ = z1;
            globalBaseY = gy;
        }

        boolean includesColumn(int lx, int lz) {
            return baseYByColumn[lx | (lz << 4)] != Integer.MAX_VALUE;
        }

        int baseYAt(int lx, int lz) {
            return baseYByColumn[lx | (lz << 4)];
        }

        boolean hasAny() {
            return globalBaseY != Integer.MAX_VALUE;
        }
    }

    private static final class SurfaceMaterials {
        final BlockState top, filler;

        SurfaceMaterials(BlockState t, BlockState f) {
            top = t;
            filler = f;
        }
    }

    public static void processChunk(WorldGenLevel level, StructureManager sm, ChunkAccess chunk) {
        if (!(level instanceof WorldGenRegion region))
            return;
        if (sm == null || chunk == null)
            return;
        if (!region.getLevel().dimension().equals(Level.OVERWORLD))
            return;

        ChunkPos cp = chunk.getPos();
        int cmx = cp.getMinBlockX(), cmz = cp.getMinBlockZ();
        int minY = chunk.getMinBuildHeight(), maxY = chunk.getMaxBuildHeight() - 1;
        BlockPos.MutableBlockPos cur = new BlockPos.MutableBlockPos();
        int done = 0;

        for (StructureStart st : chunk.getAllStarts().values()) {
            if (done >= STRUCTS_PER_CHUNK)
                break;
            if (st == null || !st.isValid())
                continue;
            BoundingBox ob = st.getBoundingBox();
            if (ob == null || isStructureUnderground(st, chunk))
                continue;
            int by = ob.minY();
            if (by < 0 || by > 400)
                continue;
            Structure str = st.getStructure();
            if (str == null)
                continue;
            ResourceLocation id = sm.registryAccess().registryOrThrow(Registries.STRUCTURE).getKey(str);
            if (id == null || !FoundationConfigManager.isStructureWhitelisted(id))
                continue;

            StructureColumnInfo ci = computeColumnInfo(st, cmx, cmz);
            if (!ci.hasAny())
                continue;

            int[][] nt = snapshotTerrainBase(chunk, cmx, cmz, minY, by);
            fillUnderStructure(chunk, ci, cmx, cmz, minY, maxY, cur);
            blendPerimeter(region, chunk, ci, nt, cmx, cmz, minY, maxY, cur, st.getPieces());
            done++;
        }
    }

    private static StructureColumnInfo computeColumnInfo(StructureStart st, int cmx, int cmz) {
        int[] col = new int[256];
        for (int i = 0; i < 256; i++)
            col[i] = Integer.MAX_VALUE;
        int gx0 = Integer.MAX_VALUE, gz0 = Integer.MAX_VALUE;
        int gx1 = Integer.MIN_VALUE, gz1 = Integer.MIN_VALUE;
        int gby = Integer.MAX_VALUE;

        for (StructurePiece p : st.getPieces()) {
            BoundingBox b = p.getBoundingBox();
            if (b == null)
                continue;
            if (b.minX() < gx0)
                gx0 = b.minX();
            if (b.minZ() < gz0)
                gz0 = b.minZ();
            if (b.maxX() > gx1)
                gx1 = b.maxX();
            if (b.maxZ() > gz1)
                gz1 = b.maxZ();
            if (b.minY() < gby)
                gby = b.minY();

            int lx0 = Math.max(0, b.minX() - cmx), lx1 = Math.min(15, b.maxX() - cmx);
            int lz0 = Math.max(0, b.minZ() - cmz), lz1 = Math.min(15, b.maxZ() - cmz);
            for (int lx = lx0; lx <= lx1; lx++)
                for (int lz = lz0; lz <= lz1; lz++) {
                    int idx = lx | (lz << 4);
                    if (b.minY() < col[idx])
                        col[idx] = b.minY();
                }
        }
        return new StructureColumnInfo(col, gx0, gz0, gx1, gz1, gby);
    }

    private static void fillUnderStructure(ChunkAccess ch, StructureColumnInfo ci,
            int cmx, int cmz, int minY, int maxY, BlockPos.MutableBlockPos cur) {
        for (int lx = 0; lx < 16; lx++)
            for (int lz = 0; lz < 16; lz++) {
                if (!ci.includesColumn(lx, lz))
                    continue;
                int wx = cmx + lx, wz = cmz + lz;
                int ft = Math.min(ci.baseYAt(lx, lz) - 1, maxY);
                int fb = Math.max(minY, ft - MAX_FILL_DEPTH);
                BlockState gf = probeColumnBase(ch, wx, ft, wz, minY, cur);
                for (int y = ft; y >= fb; y--) {
                    cur.set(wx, y, wz);
                    BlockState cs = ch.getBlockState(cur);
                    if (cs.isSolid() && cs.getFluidState().isEmpty())
                        break;
                    if (canOverwrite(cs)) {
                        ch.setBlockState(cur, gf, false);
                        eraseSurfaceCover(ch, wx, y, wz, cur);
                    }
                }
            }
    }

    private static void blendPerimeter(WorldGenRegion reg, ChunkAccess ch,
            StructureColumnInfo ci, int[][] nt, int cmx, int cmz,
            int minY, int maxY, BlockPos.MutableBlockPos cur, List<StructurePiece> pieces) {
        int left = MAX_COLUMNS_MODIFIED;
        for (int lx = 0; lx < 16; lx++)
            for (int lz = 0; lz < 16; lz++) {
                if (ci.includesColumn(lx, lz))
                    continue;
                if (left-- <= 0)
                    return;
                int wx = cmx + lx, wz = cmz + lz;

                double bestDs = Double.MAX_VALUE;
                for (StructurePiece p : pieces) {
                    BoundingBox b = p.getBoundingBox();
                    if (b == null)
                        continue;
                    int dx = wx < b.minX() ? b.minX() - wx : (wx > b.maxX() ? wx - b.maxX() : 0);
                    int dz = wz < b.minZ() ? b.minZ() - wz : (wz > b.maxZ() ? wz - b.maxZ() : 0);
                    double ds = (double) (dx * dx + dz * dz);
                    if (ds < bestDs)
                        bestDs = ds;
                }
                double dist = Math.sqrt(bestDs);
                if (dist > SLOPE_RADIUS)
                    continue;

                int gy = nt[lx][lz];
                int sby = Mth.clamp(ci.globalBaseY, minY, maxY);
                int lift = sby - gy;
                if (lift <= 1)
                    continue;

                double tn = Mth.clamp(dist / (double) SLOPE_RADIUS, 0.0, 1.0);
                double fall = 1.0 - tn * tn * (3.0 - 2.0 * tn);
                double jit = jitterNoise(wx, wz) * 1.5;
                int ty = Mth.clamp((int) Math.round(gy + fall * lift + jit), gy, sby);
                if (ty <= gy)
                    continue;

                int sy = ch.getHeight(Heightmap.Types.WORLD_SURFACE_WG, wx, wz);
                if (sy >= ty)
                    continue;
                int fh = ty - gy;
                if (fh <= 0)
                    continue;

                int cd = fh + 3;
                BlockState[] core = new BlockState[cd];
                for (int i = 0; i < cd; i++) {
                    int sy2 = gy - i;
                    if (sy2 < minY)
                        core[i] = Blocks.STONE.defaultBlockState();
                    else {
                        cur.set(wx, sy2, wz);
                        BlockState s = ch.getBlockState(cur);
                        core[i] = isSubsurfaceFill(s) ? s : Blocks.STONE.defaultBlockState();
                    }
                }
                int tpy = Integer.MIN_VALUE;
                for (int i = 0; i < fh; i++) {
                    int y = gy + 1 + i;
                    if (y > maxY)
                        break;
                    cur.set(wx, y, wz);
                    BlockState ex = ch.getBlockState(cur);
                    if (canOverwrite(ex)) {
                        int ci2 = Math.min(cd - 1, fh - 1 - i);
                        ch.setBlockState(cur, core[ci2], false);
                        eraseSurfaceCover(ch, wx, y, wz, cur);
                        tpy = y;
                    }
                }
                if (tpy != Integer.MIN_VALUE)
                    applySurfaceFromNeighbors(reg, ch, wx, tpy, wz, minY, maxY, cur);
            }
    }

    private static int[][] snapshotTerrainBase(ChunkAccess ch, int cmx, int cmz, int minY, int sby) {
        int[][] m = new int[16][16];
        int[] h = new int[256];
        for (int i = 0; i < 256; i++)
            h[i] = ch.getHeight(Heightmap.Types.WORLD_SURFACE_WG, cmx + (i & 0xF), cmz + (i >> 4));
        for (int lx = 0; lx < 16; lx++)
            for (int lz = 0; lz < 16; lz++)
                m[lx][lz] = locateSolidFloor(ch, cmx + lx, cmz + lz, Math.min(h[lx | (lz << 4)], sby), minY);
        return m;
    }

    private static int locateSolidFloor(ChunkAccess ch, int x, int z, int oy, int minY) {
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos();
        if (oy >= minY) {
            p.set(x, oy, z);
            BlockState s = ch.getBlockState(p);
            if (isTerrainBlock(s) && s.getFluidState().isEmpty())
                return oy;
        }
        for (int d = 1; d <= 7; d++) {
            int ya = oy + d;
            if (ya >= minY) {
                p.set(x, ya, z);
                BlockState s = ch.getBlockState(p);
                if (isTerrainBlock(s) && s.getFluidState().isEmpty())
                    return ya;
            }
            int yb = oy - d;
            if (yb >= minY) {
                p.set(x, yb, z);
                BlockState s = ch.getBlockState(p);
                if (isTerrainBlock(s) && s.getFluidState().isEmpty())
                    return yb;
            }
        }
        return minY;
    }

    private static boolean isStructureUnderground(StructureStart st, ChunkAccess ch) {
        BoundingBox b = st.getBoundingBox();
        if (b == null)
            return true;
        int minY = ch.getMinBuildHeight(), maxY = ch.getMaxBuildHeight() - 1;
        int sty = b.maxY(), step = Math.max(3, Math.min(b.getXSpan(), b.getZSpan()) / 4);
        int n = 0;
        double sum = 0;
        for (int sx = b.minX(); sx <= b.maxX() && n < 20; sx += step) {
            sum += Mth.clamp(ch.getHeight(Heightmap.Types.WORLD_SURFACE_WG, sx, b.minZ()), minY, maxY) - sty;
            n++;
        }
        for (int sx = b.minX(); sx <= b.maxX() && n < 20; sx += step) {
            sum += Mth.clamp(ch.getHeight(Heightmap.Types.WORLD_SURFACE_WG, sx, b.maxZ()), minY, maxY) - sty;
            n++;
        }
        for (int sz = b.minZ() + step; sz <= b.maxZ() - step && n < 20; sz += step) {
            sum += Mth.clamp(ch.getHeight(Heightmap.Types.WORLD_SURFACE_WG, b.minX(), sz), minY, maxY) - sty;
            n++;
        }
        for (int sz = b.minZ() + step; sz <= b.maxZ() - step && n < 20; sz += step) {
            sum += Mth.clamp(ch.getHeight(Heightmap.Types.WORLD_SURFACE_WG, b.maxX(), sz), minY, maxY) - sty;
            n++;
        }
        return n == 0 || sum / n > 3.0;
    }

    private static void applySurfaceFromNeighbors(WorldGenRegion reg, ChunkAccess ch,
            int x, int ty, int z, int minY, int maxY, BlockPos.MutableBlockPos cur) {
        if (ty + 1 <= maxY) {
            cur.set(x, ty + 1, z);
            if (!ch.getBlockState(cur).getFluidState().isEmpty())
                return;
        }
        SurfaceMaterials mat = sampleNearbySurface(ch, x, z, minY, maxY, cur);
        if (mat == null) {
            Holder<Biome> bi = reg.getBiome(BlockPos.containing(x, ty, z));
            mat = fallbackSurfaceByClimate(bi, x, z);
        }
        cur.set(x, ty, z);
        ch.setBlockState(cur, mat.top, false);
        for (int i = 1; i <= 2; i++) {
            int y = ty - i;
            if (y < minY)
                break;
            cur.set(x, y, z);
            BlockState ex = ch.getBlockState(cur);
            if (!ex.isSolid() || isSurfaceCover(ex) || ex.canBeReplaced())
                ch.setBlockState(cur, mat.filler, false);
        }
    }

    private static SurfaceMaterials sampleNearbySurface(ChunkAccess ch, int cx, int cz, int minY, int maxY,
            BlockPos.MutableBlockPos cur) {
        int[][] sp = { { 2, 0 }, { 0, 2 }, { -2, 0 }, { 0, -2 }, { 4, 0 }, { 4, 2 }, { 4, 4 }, { 2, 4 }, { 0, 4 },
                { -2, 4 }, { -4, 4 }, { -4, 2 }, { -4, 0 }, { -4, -2 }, { -4, -4 }, { -2, -4 }, { 0, -4 }, { 2, -4 },
                { 4, -4 }, { 4, -2 }, { 6, 0 }, { 6, 2 }, { 6, 4 }, { 6, 6 }, { 4, 6 }, { 2, 6 }, { 0, 6 }, { -2, 6 },
                { -4, 6 }, { -6, 6 }, { -6, 4 }, { -6, 2 }, { -6, 0 }, { -6, -2 }, { -6, -4 }, { -6, -6 }, { -4, -6 },
                { -2, -6 }, { 0, -6 }, { 2, -6 }, { 4, -6 }, { 6, -6 }, { 6, -4 }, { 6, -2 }, { 8, 0 }, { 8, 4 },
                { 8, 8 }, { 4, 8 }, { 0, 8 }, { -4, 8 }, { -8, 8 }, { -8, 4 }, { -8, 0 }, { -8, -4 }, { -8, -8 },
                { -4, -8 }, { 0, -8 }, { 4, -8 }, { 8, -8 }, { 8, -4 }, { 10, 0 }, { 10, 4 }, { 10, 8 }, { 10, 10 },
                { 8, 10 }, { 4, 10 }, { 0, 10 }, { -4, 10 }, { -8, 10 }, { -10, 10 }, { -10, 8 }, { -10, 4 },
                { -10, 0 }, { -10, -4 }, { -10, -8 }, { -10, -10 }, { -8, -10 }, { -4, -10 }, { 0, -10 }, { 4, -10 },
                { 8, -10 }, { 10, -10 }, { 10, -8 }, { 10, -4 } };
        for (int[] o : sp) {
            int sx = cx + o[0], sz = cz + o[1];
            int sy = ch.getHeight(Heightmap.Types.WORLD_SURFACE_WG, sx, sz);
            if (sy < minY || sy > maxY)
                continue;
            cur.set(sx, sy, sz);
            if (isNaturalSurfaceTop(ch.getBlockState(cur))) {
                BlockState fs = Blocks.DIRT.defaultBlockState();
                for (int dy = 1; dy <= 5; dy++) {
                    int fy = sy - dy;
                    if (fy < minY)
                        break;
                    cur.set(sx, fy, sz);
                    BlockState s = ch.getBlockState(cur);
                    if (s.isSolid() && isSubsurfaceFill(s)) {
                        fs = s;
                        break;
                    }
                }
                return new SurfaceMaterials(ch.getBlockState(cur.set(sx, sy, sz)), fs);
            }
        }
        return null;
    }

    private static SurfaceMaterials fallbackSurfaceByClimate(Holder<Biome> bi, int x, int z) {
        if (bi != null) {
            if (bi.value().coldEnoughToSnow(BlockPos.containing(x, 64, z)))
                return new SurfaceMaterials(Blocks.SNOW_BLOCK.defaultBlockState(), Blocks.DIRT.defaultBlockState());
            Biome.ClimateSettings cl = bi.value().getModifiedClimateSettings();
            float t = cl.temperature(), d = cl.downfall();
            if (t > 1.5f && d < 0.3f)
                return new SurfaceMaterials(Blocks.SAND.defaultBlockState(), Blocks.SAND.defaultBlockState());
            if (t > 1.5f)
                return new SurfaceMaterials(Blocks.RED_SAND.defaultBlockState(), Blocks.TERRACOTTA.defaultBlockState());
            if (t < 0.2f)
                return new SurfaceMaterials(Blocks.PODZOL.defaultBlockState(), Blocks.DIRT.defaultBlockState());
        }
        return new SurfaceMaterials(Blocks.GRASS_BLOCK.defaultBlockState(), Blocks.DIRT.defaultBlockState());
    }

    private static boolean isNaturalSurfaceTop(BlockState s) {
        return s.isSolid() && s.getFluidState().isEmpty() && (s.is(Blocks.GRASS_BLOCK) || s.is(Blocks.PODZOL)
                || s.is(Blocks.MYCELIUM) || s.is(Blocks.SNOW_BLOCK) || s.is(BlockTags.SAND) || s.is(BlockTags.DIRT));
    }

    private static BlockState probeColumnBase(ChunkAccess ch, int x, int ty, int z, int minY,
            BlockPos.MutableBlockPos cur) {
        for (int dy = 0; dy <= 8; dy++) {
            int y = ty - dy;
            if (y < minY)
                break;
            cur.set(x, y, z);
            BlockState s = ch.getBlockState(cur);
            if (isSubsurfaceFill(s))
                return s;
        }
        return Blocks.STONE.defaultBlockState();
    }

    private static void eraseSurfaceCover(ChunkAccess ch, int x, int y, int z, BlockPos.MutableBlockPos cur) {
        int by = y - 1;
        if (by < ch.getMinBuildHeight())
            return;
        cur.set(x, by, z);
        if (isSurfaceCover(ch.getBlockState(cur)))
            ch.setBlockState(cur, Blocks.DIRT.defaultBlockState(), false);
    }

    private static boolean isTerrainBlock(BlockState s) {
        return s.is(BlockTags.BASE_STONE_OVERWORLD) || s.is(Blocks.DEEPSLATE) || s.is(Blocks.TUFF)
                || s.is(BlockTags.DIRT) || s.is(BlockTags.SAND) || s.is(BlockTags.TERRACOTTA)
                || s.is(Blocks.GRAVEL) || s.is(Blocks.CLAY) || s.is(Blocks.MUD)
                || s.is(BlockTags.SNOW) || s.is(Blocks.POWDER_SNOW)
                || s.is(Blocks.SANDSTONE) || s.is(Blocks.RED_SANDSTONE)
                || s.is(Blocks.WATER) || s.is(Blocks.LAVA);
    }

    private static boolean isSubsurfaceFill(BlockState s) {
        return !s.isAir() && s.isSolid() && s.getFluidState().isEmpty() && !isSurfaceCover(s) && isTerrainBlock(s);
    }

    private static boolean canOverwrite(BlockState s) {
        return !s.getFluidState().isEmpty() || s.isAir() || s.is(Blocks.POWDER_SNOW) || s.is(Blocks.SNOW)
                || s.is(Blocks.ICE) || s.canBeReplaced();
    }

    private static boolean isSurfaceCover(BlockState s) {
        return s.is(Blocks.GRASS_BLOCK) || s.is(Blocks.PODZOL) || s.is(Blocks.MYCELIUM) || s.is(Blocks.FARMLAND)
                || s.is(Blocks.ROOTED_DIRT) || s.is(Blocks.COARSE_DIRT);
    }

    private static double jitterNoise(double wx, double wz) {
        final double F = 0.085;
        double fx = wx * F, fz = wz * F;
        int ix = floorToInt(fx), iz = floorToInt(fz);
        double u = fx - ix, v = fz - iz;
        u = u * u * u * (u * (u * 6.0 - 15.0) + 10.0);
        v = v * v * v * (v * (v * 6.0 - 15.0) + 10.0);
        return Mth.lerp(v, Mth.lerp(u, noiseHash(ix, iz), noiseHash(ix + 1, iz)),
                Mth.lerp(u, noiseHash(ix, iz + 1), noiseHash(ix + 1, iz + 1)));
    }

    private static double noiseHash(int x, int z) {
        long h = ((long) x * 0x7A3F9B2CL) ^ ((long) z * 0x4E6D1F8AL);
        h = (h ^ (h >>> 33)) * 0xFF51AFD7ED558CCDL;
        h = (h ^ (h >>> 33)) * 0xC4CEB9FE1A85EC53L;
        h = h ^ (h >>> 33);
        return (double) (h & 0x7FFFFFFFFFFFFFFFL) / (double) 0x7FFFFFFFFFFFFFFFL * 2.0 - 1.0;
    }

    private static int floorToInt(double v) {
        int i = (int) v;
        return v < (double) i ? i - 1 : i;
    }
}
