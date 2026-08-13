package com.k1sak1.goetyawaken.client.renderer.block;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.common.blocks.VillagerStatueBlock;
import com.k1sak1.goetyawaken.common.blocks.entity.VillagerStatueBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class VillagerStatueRenderer implements BlockEntityRenderer<VillagerStatueBlockEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/villager_statue.png");
    private final VillagerModel<?> model;

    public VillagerStatueRenderer(BlockEntityRendererProvider.Context pContext) {
        this.model = new VillagerModel<>(pContext.bakeLayer(ModelLayers.VILLAGER));
    }

    @Override
    public void render(VillagerStatueBlockEntity pBlockEntity, float pPartialTick,
            PoseStack pPoseStack, MultiBufferSource pBuffer,
            int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.5D, 0.0D, 0.5D);

        Direction facing = pBlockEntity.getBlockState().getValue(VillagerStatueBlock.FACING);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F - facing.toYRot()));

        pPoseStack.scale(-1.0F, -1.0F, 1.0F);
        pPoseStack.translate(0.0D, -1.5D, 0.0D);

        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityCutoutNoCullZOffset(TEXTURE));
        model.renderToBuffer(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);

        pPoseStack.popPose();
    }
}
