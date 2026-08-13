package com.k1sak1.goetyawaken.client.renderer.block;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.WildfireStatueModel;
import com.k1sak1.goetyawaken.common.blocks.WildfireStatueBlock;
import com.k1sak1.goetyawaken.common.blocks.entity.WildfireStatueBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class WildfireStatueRenderer implements BlockEntityRenderer<WildfireStatueBlockEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/wildfire_statue.png");
    private final WildfireStatueModel<?> model;

    public WildfireStatueRenderer(BlockEntityRendererProvider.Context pContext) {
        this.model = new WildfireStatueModel<>(pContext.bakeLayer(WildfireStatueModel.LAYER_LOCATION));
    }

    @Override
    public void render(WildfireStatueBlockEntity pBlockEntity, float pPartialTick,
            PoseStack pPoseStack, MultiBufferSource pBuffer,
            int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.5D, 0.0D, 0.5D);

        Direction facing = pBlockEntity.getBlockState().getValue(WildfireStatueBlock.FACING);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F - facing.toYRot()));

        pPoseStack.scale(-1.0F, -1.0F, 1.0F);
        pPoseStack.translate(0.0D, -1.5D, 0.0D);

        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityCutoutNoCullZOffset(TEXTURE));
        model.renderToBuffer(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);

        pPoseStack.popPose();
    }
}
