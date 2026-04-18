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
    private Map<RenderType, Map<BlockPos, BlockState>> toRender = new LinkedHashMap<>();
    private Map<BlockPos, BlockState> tilesToRender = new LinkedHashMap<>();
    @Nullable
    private String toRenderUniqueId;
    public float fadeAmount = 1.0F;
    private float fadeAmountO = 1.0F;

    public ClientBlockClusterEntity(EntityType<?> entityType, Level world) {
        super(entityType, world);
        this.blockGetter = new BlockClusterWorld(world, this);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> parameter) {
        super.onSyncedDataUpdated(parameter);
        if (parameter.equals(this.BLOCK_STATE_MAP)) {
            this.toRender.clear();
            this.tilesToRender.clear();

            for (Map.Entry<BlockPos, BlockState> entry : this.getBlocks().entrySet()) {
                BlockPos pos = entry.getKey();
                BlockState state = entry.getValue();
                if (state.getRenderShape() != RenderShape.ENTITYBLOCK_ANIMATED) {
                    if (state.getRenderShape() == RenderShape.MODEL) {
                        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
                        BakedModel model = dispatcher.getBlockModel(state);
                        ChunkRenderTypeSet blockRenderTypes = model.getRenderTypes(state,
                                RandomSource.create(state.getSeed(this.getStartPos())), ModelData.EMPTY);

                        for (RenderType type : RenderType.chunkBufferLayers()) {
                            if (blockRenderTypes.contains(type)) {
                                Map<BlockPos, BlockState> map = this.toRender.computeIfAbsent(type,
                                        (t) -> new LinkedHashMap<>());
                                map.put(pos, state);
                            }
                        }
                    }
                } else {
                    this.tilesToRender.put(pos, state);
                }
            }

            this.toRenderUniqueId = this.toRender.toString();
        } else if (parameter.equals(this.FADE_ORIGIN)) {
            this.calculateFade();
            this.fadeAmountO = this.fadeAmount;
        }
    }

    public Map<RenderType, Map<BlockPos, BlockState>> toRender() {
        return this.toRender;
    }

    public Map<BlockPos, BlockState> tilesToRender() {
        return this.tilesToRender;
    }

    @Nullable
    public String getToRenderUniqueId() {
        return this.toRenderUniqueId;
    }

    public BlockAndTintGetter getBlockGetter() {
        return this.blockGetter;
    }

    @Override
    public void tick() {
        super.tick();
        this.calculateFade();
    }

    private void calculateFade() {
        if (this.getShakeTime() <= 0) {
            this.fadeAmountO = this.fadeAmount;
            BlockPos point = this.getFadePos();
            if (point != null) {
                double distanceFromCreationToFade = Math.sqrt(this.getStartPos().distSqr(point))
                        - (double) this.getFadeDistanceOffset();
                double distance = Math.max((double) 0.0F,
                        Vec3.atCenterOf(point).distanceTo(this.position()) - (double) this.getFadeDistanceOffset());
                this.fadeAmount = Math.min(1.0F,
                        (float) distance / Math.min((float) distanceFromCreationToFade, this.getFadeStrength()));
            }
        }
    }

    public float lerpFadeAmount(float partialTicks) {
        return Mth.lerp(partialTicks, this.fadeAmountO, this.fadeAmount);
    }

    public static class BlockClusterWorld implements BlockAndTintGetter {
        private final Level wrapped;
        private final BlockClusterEntity cluster;

        public BlockClusterWorld(Level wrapped, BlockClusterEntity cluster) {
            this.wrapped = wrapped;
            this.cluster = cluster;
        }

        @Override
        public BlockEntity getBlockEntity(BlockPos pos) {
            return null;
        }

        @Override
        public BlockState getBlockState(BlockPos pos) {
            BlockState state = this.cluster.getBlocks().get(pos.subtract(this.cluster.getStartPos()));
            if (state == null) {
                state = Blocks.AIR.defaultBlockState();
            }
            return state;
        }

        @Override
        public FluidState getFluidState(BlockPos pos) {
            return this.getBlockState(pos).getFluidState();
        }

        @Override
        public int getHeight() {
            return this.wrapped.getHeight();
        }

        @Override
        public int getMinBuildHeight() {
            return this.wrapped.getMinBuildHeight();
        }

        @Override
        public float getShade(Direction direction, boolean p_45523_) {
            return this.wrapped.getShade(direction, p_45523_);
        }

        @Override
        public LevelLightEngine getLightEngine() {
            return this.wrapped.getLightEngine();
        }

        @Override
        public int getRawBrightness(BlockPos pos, int skyOffset) {
            return 15;
        }

        @Override
        public int getBrightness(LightLayer layer, BlockPos pos) {
            return 15;
        }

        @Override
        public int getBlockTint(BlockPos pos, ColorResolver resolver) {
            return this.wrapped.getBlockTint(pos, resolver);
        }
    }
}
