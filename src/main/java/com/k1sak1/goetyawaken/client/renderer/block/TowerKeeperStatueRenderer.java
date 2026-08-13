package com.k1sak1.goetyawaken.client.renderer.block;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.TowerKeeperStatueModel;
import com.k1sak1.goetyawaken.common.blocks.TowerKeeperStatueBlock;
import com.k1sak1.goetyawaken.common.blocks.entity.TowerKeeperStatueBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class TowerKeeperStatueRenderer implements BlockEntityRenderer<TowerKeeperStatueBlockEntity> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/block/tower_keeper_statue.png");
    private final TowerKeeperStatueModel<?> model;

    public TowerKeeperStatueRenderer(BlockEntityRendererProvider.Context pContext) {
        this.model = new TowerKeeperStatueModel<>(pContext.bakeLayer(TowerKeeperStatueModel.LAYER_LOCATION));
    }

    @Override
    public void render(TowerKeeperStatueBlockEntity pBlockEntity, float pPartialTick,
            PoseStack pPoseStack, MultiBufferSource pBuffer,
            int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.5D, 0.0D, 0.5D);

        Direction facing = pBlockEntity.getBlockState().getValue(TowerKeeperStatueBlock.FACING);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(180.0F - facing.toYRot()));

        pPoseStack.scale(-1.0F, -1.0F, 1.0F);
        pPoseStack.translate(0.0D, -1.5D, 0.0D);

        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityCutoutNoCullZOffset(TEXTURE));
        model.renderToBuffer(pPoseStack, vertexconsumer, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);

        pPoseStack.popPose();
    }
}
