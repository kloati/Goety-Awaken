package com.k1sak1.goetyawaken.client.renderer.layers;

import com.k1sak1.goetyawaken.client.model.AngryMooshroomModel;
import com.k1sak1.goetyawaken.common.entities.ally.AngryMooshroom;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AngryMooshroomMushroomLayer extends RenderLayer<AngryMooshroom, AngryMooshroomModel<AngryMooshroom>> {
    private final BlockRenderDispatcher blockRenderer;
    private static final ResourceLocation RED_MUSHROOM_LOCATION = new ResourceLocation(
            "textures/block/red_mushroom.png");
    private static final ResourceLocation BROWN_MUSHROOM_LOCATION = new ResourceLocation(
            "textures/block/brown_mushroom.png");

    public AngryMooshroomMushroomLayer(RenderLayerParent<AngryMooshroom, AngryMooshroomModel<AngryMooshroom>> pRenderer,
            BlockRenderDispatcher pBlockRenderer) {
        super(pRenderer);
        this.blockRenderer = pBlockRenderer;
    }

    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, AngryMooshroom pLivingEntity,
            float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw,
            float pHeadPitch) {
        if (!pLivingEntity.isBaby()) {
            Minecraft minecraft = Minecraft.getInstance();
            boolean flag = minecraft.shouldEntityAppearGlowing(pLivingEntity) && pLivingEntity.isInvisible();
            if (!pLivingEntity.isInvisible() || flag) {
                BlockState blockstate = pLivingEntity.isBrownVariant() ? Blocks.BROWN_MUSHROOM.defaultBlockState()
                        : Blocks.RED_MUSHROOM.defaultBlockState();
                int i = LivingEntityRenderer.getOverlayCoords(pLivingEntity, 0.0F);
                BakedModel bakedmodel = this.blockRenderer.getBlockModel(blockstate);
                pPoseStack.pushPose();
                pPoseStack.translate(0.2F, -0.35F, 0.5F);
                pPoseStack.mulPose(Axis.YP.rotationDegrees(-48.0F));
                pPoseStack.scale(-1.0F, -1.0F, 1.0F);
                pPoseStack.translate(-0.5F, -0.5F, -0.5F);
                this.renderMushroomBlock(pPoseStack, pBuffer, pPackedLight, flag, blockstate, i, bakedmodel);
                pPoseStack.popPose();
                pPoseStack.pushPose();
                pPoseStack.translate(0.2F, -0.35F, 0.5F);
                pPoseStack.mulPose(Axis.YP.rotationDegrees(42.0F));
                pPoseStack.translate(0.1F, 0.0F, -0.6F);
                pPoseStack.mulPose(Axis.YP.rotationDegrees(-48.0F));
                pPoseStack.scale(-1.0F, -1.0F, 1.0F);
                pPoseStack.translate(-0.5F, -0.5F, -0.5F);
                this.renderMushroomBlock(pPoseStack, pBuffer, pPackedLight, flag, blockstate, i, bakedmodel);
                pPoseStack.popPose();
                pPoseStack.pushPose();
                this.getParentModel().getHead().translateAndRotate(pPoseStack);
                pPoseStack.translate(0.0F, -0.7F, -0.2F);
                pPoseStack.mulPose(Axis.YP.rotationDegrees(-78.0F));
                pPoseStack.scale(-1.0F, -1.0F, 1.0F);
                pPoseStack.translate(-0.5F, -0.5F, -0.5F);
                this.renderMushroomBlock(pPoseStack, pBuffer, pPackedLight, flag, blockstate, i, bakedmodel);
                pPoseStack.popPose();
            }
        }
    }

    private void renderMushroomBlock(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight,
            boolean pOutlineOnly, BlockState pState, int pPackedOverlay, BakedModel pModel) {
        if (pOutlineOnly) {
            this.blockRenderer.getModelRenderer().renderModel(pPoseStack.last(),
                    pBuffer.getBuffer(RenderType.outline(TextureAtlas.LOCATION_BLOCKS)), pState, pModel, 0.0F, 0.0F,
                    0.0F, pPackedLight, pPackedOverlay);
        } else {
            this.blockRenderer.renderSingleBlock(pState, pPoseStack, pBuffer, pPackedLight, pPackedOverlay);
        }
    }
}