package com.k1sak1.goetyawaken.client.entity;

import com.k1sak1.goetyawaken.common.entities.projectiles.BlockClusterEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;

public class ClientBlockClusterEntity extends BlockClusterEntity {

    private final BlockClusterWorld blockGetter;
    private Map<RenderType, Map<BlockPos, BlockState>> renderLayers = new LinkedHashMap<>();
    private Map<BlockPos, BlockState> animatedBlocks = new LinkedHashMap<>();
    @Nullable
    private String renderCacheKey;
    public float fadeAmount = 1.0F;
    private float prevFadeAmount = 1.0F;

    public ClientBlockClusterEntity(EntityType<?> entityType, Level world) {
        super(entityType, world);
        this.blockGetter = new BlockClusterWorld(world, this);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key.equals(this.CLUSTER_BLOCKS)) {
            this.rebuildRenderCache();
        } else if (key.equals(this.CLUSTER_FADE_CENTER)) {
            this.recalcFade();
            this.prevFadeAmount = this.fadeAmount;
        }
    }

    private void rebuildRenderCache() {
        this.renderLayers.clear();
        this.animatedBlocks.clear();
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        for (var entry : this.getClusterBlocks().entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState state = entry.getValue();
            RenderShape shape = state.getRenderShape();
            if (shape == RenderShape.ENTITYBLOCK_ANIMATED) {
                this.animatedBlocks.put(pos, state);
            } else if (shape == RenderShape.MODEL) {
                BakedModel model = dispatcher.getBlockModel(state);
                ChunkRenderTypeSet types = model.getRenderTypes(state,
                        RandomSource.create(state.getSeed(this.getAnchorPosition())), ModelData.EMPTY);
                for (RenderType rt : RenderType.chunkBufferLayers()) {
                    if (types.contains(rt)) {
                        this.renderLayers.computeIfAbsent(rt, k -> new LinkedHashMap<>()).put(pos, state);
                    }
                }
            }
        }
        this.renderCacheKey = this.renderLayers.toString();
    }

    public Map<RenderType, Map<BlockPos, BlockState>> getRenderLayers() { return this.renderLayers; }
    public Map<BlockPos, BlockState> getAnimatedBlocks() { return this.animatedBlocks; }
    @Nullable
    public String getRenderCacheKey() { return this.renderCacheKey; }
    public BlockAndTintGetter getBlockGetter() { return this.blockGetter; }

    @Override
    public void tick() {
        super.tick();
        this.recalcFade();
    }

    private void recalcFade() {
        if (this.getWobbleTicks() <= 0) {
            this.prevFadeAmount = this.fadeAmount;
            BlockPos fc = this.getFadeCenter();
            if (fc != null) {
                double maxRange = Math.sqrt(this.getAnchorPosition().distSqr(fc)) - (double)this.getFadeMargin();
                double dist = Math.max(0.0D, Vec3.atCenterOf(fc).distanceTo(this.position()) - (double)this.getFadeMargin());
                this.fadeAmount = (float)Math.min(1.0D, dist / Math.min(maxRange, (double)this.getFadePower()));
            }
        }
    }

    public float getFadeAmount(float partialTicks) {
        return Mth.lerp(partialTicks, this.prevFadeAmount, this.fadeAmount);
    }

    public static class BlockClusterWorld implements BlockAndTintGetter {
        private final Level wrapped;
        private final BlockClusterEntity cluster;

        public BlockClusterWorld(Level wrapped, BlockClusterEntity cluster) {
            this.wrapped = wrapped;
            this.cluster = cluster;
        }

        @Override public BlockEntity getBlockEntity(BlockPos pos) { return null; }

        @Override
        public BlockState getBlockState(BlockPos pos) {
            BlockState state = this.cluster.getClusterBlocks()
                    .get(pos.subtract(this.cluster.getAnchorPosition()));
            return state != null ? state : Blocks.AIR.defaultBlockState();
        }

        @Override public FluidState getFluidState(BlockPos pos) { return this.getBlockState(pos).getFluidState(); }
        @Override public int getHeight() { return this.wrapped.getHeight(); }
        @Override public int getMinBuildHeight() { return this.wrapped.getMinBuildHeight(); }
        @Override public float getShade(Direction d, boolean shaded) { return this.wrapped.getShade(d, shaded); }
        @Override public LevelLightEngine getLightEngine() { return this.wrapped.getLightEngine(); }
        @Override public int getRawBrightness(BlockPos pos, int sky) { return 15; }
        @Override public int getBrightness(LightLayer layer, BlockPos pos) { return 15; }
        @Override public int getBlockTint(BlockPos pos, ColorResolver r) { return this.wrapped.getBlockTint(pos, r); }
    }
}
