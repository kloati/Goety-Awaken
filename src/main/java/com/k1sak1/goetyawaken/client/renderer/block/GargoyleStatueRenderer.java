package com.k1sak1.goetyawaken.client.renderer.block;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.GargoyleStatueModel;
import com.k1sak1.goetyawaken.common.blocks.GargoyleStatueBlock;
import com.k1sak1.goetyawaken.common.blocks.entity.GargoyleStatueBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class GargoyleStatueRenderer implements BlockEntityRenderer<GargoyleStatueBlockEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/gargoyle_statue.png");
    private final GargoyleStatueModel<?> model;

    public GargoyleStatueRenderer(BlockEntityRendererProvider.Context pContext) {
        this.model = new GargoyleStatueModel<>(pContext.bakeLayer(GargoyleStatueModel.LAYER_LOCATION));
    }

    @Override
    public void render(GargoyleStatueBlockEntity pBlockEntity, float pPartialTick,
            PoseStack pPoseStack, MultiBufferSource pBuffer,
            int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.5D, 0.0D, 0.5D);

        Direction facing = pBlockEntity.getBlockState().getValue(GargoyleStatueBlock.FACING);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F - facing.toYRot()));
        pPoseStack.scale(0.5F, 0.5F, 0.5F);
        pPoseStack.translate(0.0D, -17.0D / 16.0D, 28.0D / 16.0D);

        pPoseStack.scale(-1.0F, -1.0F, 1.0F);
        pPoseStack.translate(0.0D, -1.5D, 0.0D);

        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityCutoutNoCullZOffset(TEXTURE));
        model.renderToBuffer(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);

        pPoseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(GargoyleStatueBlockEntity pBlockEntity) {
        return true;
    }
}
