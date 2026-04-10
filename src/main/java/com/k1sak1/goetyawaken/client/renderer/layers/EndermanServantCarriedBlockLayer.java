package com.k1sak1.goetyawaken.client.renderer.layers;

import com.k1sak1.goetyawaken.client.model.EndermanServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.EndermanServant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EndermanServantCarriedBlockLayer extends RenderLayer<EndermanServant, EndermanServantModel> {
    private final BlockRenderDispatcher blockRenderer;

    public EndermanServantCarriedBlockLayer(RenderLayerParent<EndermanServant, EndermanServantModel> renderer,
            BlockRenderDispatcher blockRenderer) {
        super(renderer);
        this.blockRenderer = blockRenderer;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, EndermanServant entity,
            float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
            float headPitch) {
        BlockState blockstate = getCarriedBlockState(entity);

        if (blockstate != null) {
            poseStack.pushPose();
            poseStack.translate(0.0F, 0.6875F, -0.75F);
            poseStack.mulPose(Axis.XP.rotationDegrees(20.0F));
            poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
            poseStack.translate(0.25F, 0.1875F, 0.25F);
            float f = 0.5F;
            poseStack.scale(-0.5F, -0.5F, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
            this.blockRenderer.renderSingleBlock(blockstate, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
    }

    private BlockState getCarriedBlockState(EndermanServant entity) {
        if (!entity.getCarriedItem().isEmpty()) {
            if (entity.getCarriedItem().hasTag() && entity.getCarriedItem().getTag().contains("BlockEntityTag")) {
                BlockState blockState = net.minecraft.world.level.block.Block.byItem(entity.getCarriedItem().getItem())
                        .defaultBlockState();
                return blockState;
            } else {
                BlockState blockState = net.minecraft.world.level.block.Block.byItem(entity.getCarriedItem().getItem())
                        .defaultBlockState();
                return blockState;
            }
        }
        return null;
    }
}