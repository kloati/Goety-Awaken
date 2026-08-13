package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.client.entity.ClientBlockClusterEntity;
import com.k1sak1.goetyawaken.common.entities.projectiles.BlockClusterEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BoulderClusterRenderer extends EntityRenderer<BlockClusterEntity> {

    private static final float WOBBLE_X_AMPLITUDE = 50.0F;
    private static final float WOBBLE_Z_AMPLITUDE = 30.0F;

    public BoulderClusterRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(BlockClusterEntity cluster, float yaw, float partialTicks,
            PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (!(cluster instanceof ClientBlockClusterEntity entity)) return;

        poseStack.pushPose();

        float wx = Mth.lerp(partialTicks, entity.previousWobble.x, entity.currentWobble.x);
        float wz = Mth.lerp(partialTicks, entity.previousWobble.y, entity.currentWobble.y);
        double halfH = entity.getBoundingBox().getYsize() / 2.0D;

        poseStack.translate(0.0D, halfH, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getClusterYRot(partialTicks) - wx * WOBBLE_X_AMPLITUDE));
        poseStack.mulPose(Axis.XP.rotationDegrees(entity.getClusterXRot(partialTicks) - wz * WOBBLE_Z_AMPLITUDE));
        poseStack.translate(0.0D, -halfH, 0.0D);

        double xo = -0.5D + (Math.round(entity.getBoundingBox().getXsize()) % 2L == 0L ? -0.5D : 0.0D);
        double yo = -0.5D + (Math.round(entity.getBoundingBox().getYsize()) % 2L == 0L ? -0.5D : 0.0D);
        double zo = -0.5D + (Math.round(entity.getBoundingBox().getZsize()) % 2L == 0L ? -0.5D : 0.0D);
        poseStack.translate(xo, yo, zo);

        float fade = entity.getFadeAmount(partialTicks);
        float sc = Math.max(0.8F, fade * 0.5F + 0.5F);
        poseStack.scale(sc, sc, sc);
        poseStack.translate((double)wx, 0.0D, (double)wz);

        BlockRenderDispatcher brd = Minecraft.getInstance().getBlockRenderer();
        BlockAndTintGetter getter = entity.getBlockGetter();

        for (var layerEntry : entity.getRenderLayers().entrySet()) {
            RenderType rt = layerEntry.getKey();
            for (var blockEntry : layerEntry.getValue().entrySet()) {
                BlockState state = blockEntry.getValue();
                BlockPos rp = blockEntry.getKey();
                poseStack.pushPose();
                poseStack.translate(rp.getX(), rp.getY() + halfH, rp.getZ());
                brd.renderBatched(state, rp.offset(entity.getAnchorPosition()), getter,
                        poseStack, buffer.getBuffer(rt), false,
                        RandomSource.create(rp.asLong()),
                        net.minecraftforge.client.model.data.ModelData.EMPTY, rt);
                poseStack.popPose();
            }
        }

        poseStack.popPose();
        super.render(entity, yaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public boolean shouldRender(BlockClusterEntity entity, Frustum frustum,
            double cx, double cy, double cz) {
        return entity.isForceVisible() || super.shouldRender(entity, frustum, cx, cy, cz);
    }

    @Override
    public ResourceLocation getTextureLocation(BlockClusterEntity entity) {
        return null;
    }
}
