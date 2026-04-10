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

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class BoulderClusterRenderer extends EntityRenderer<BlockClusterEntity> {

    public BoulderClusterRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(BlockClusterEntity cluster, float entityYaw, float partialTicks,
            PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (!(cluster instanceof ClientBlockClusterEntity entity)) {
            return;
        }

        poseStack.pushPose();

        float xRot = Mth.lerp(partialTicks, entity.shakeO.x, entity.shake.x);
        float zRot = Mth.lerp(partialTicks, entity.shakeO.y, entity.shake.y);
        double halfYSize = entity.getBoundingBox().getYsize() / 2.0D;
        poseStack.translate(0.0D, halfYSize, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getClusterYRot(partialTicks) - xRot * 50.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(entity.getClusterXRot(partialTicks) - zRot * 30.0F));
        poseStack.translate(0.0D, -halfYSize, 0.0D);
        double xOffset = -0.5D + (Math.round(entity.getBoundingBox().getXsize()) % 2L == 0L ? -0.5D : 0.0D);
        double yOffset = -0.5D + (Math.round(entity.getBoundingBox().getYsize()) % 2L == 0L ? -0.5D : 0.0D);
        double zOffset = -0.5D + (Math.round(entity.getBoundingBox().getZsize()) % 2L == 0L ? -0.5D : 0.0D);
        poseStack.translate(xOffset, yOffset, zOffset);

        Minecraft minecraft = Minecraft.getInstance();
        BlockRenderDispatcher dispatcher = minecraft.getBlockRenderer();
        BlockAndTintGetter getter = entity.getBlockGetter();
        float fade = entity.lerpFadeAmount(partialTicks);
        float scale = Math.max(0.8F, fade * 0.5F + 0.5F);
        poseStack.scale(scale, scale, scale);
        poseStack.translate((double) xRot, 0.0D, (double) zRot);

        float r = Math.min(1.0F, fade + 0.1F);
        float g = fade;
        float b = Math.min(1.0F, fade + 0.2F);
        for (Map.Entry<RenderType, Map<BlockPos, BlockState>> entry : entity.toRender().entrySet()) {
            RenderType type = entry.getKey();

            for (Map.Entry<BlockPos, BlockState> blockEntry : entry.getValue().entrySet()) {
                BlockState state = blockEntry.getValue();
                BlockPos relativePos = blockEntry.getKey();

                poseStack.pushPose();
                poseStack.translate(
                        (double) relativePos.getX(),
                        (double) relativePos.getY() + halfYSize,
                        (double) relativePos.getZ());

                dispatcher.renderBatched(
                        state,
                        relativePos.offset(entity.getStartPos()),
                        getter,
                        poseStack,
                        buffer.getBuffer(type),
                        false,
                        RandomSource.create(relativePos.asLong()),
                        net.minecraftforge.client.model.data.ModelData.EMPTY,
                        type);

                poseStack.popPose();
            }
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public boolean shouldRender(BlockClusterEntity entity, Frustum frustum, double p_225626_3_,
            double p_225626_4_, double p_225626_5_) {
        return entity.forceRender() || super.shouldRender(entity, frustum, p_225626_3_, p_225626_4_, p_225626_5_);
    }

    @Override
    public ResourceLocation getTextureLocation(BlockClusterEntity entity) {
        return null;
    }
}
