package com.k1sak1.goetyawaken.client.renderer.block;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.MushroomMonstrosityHeadModel;
import com.k1sak1.goetyawaken.common.blocks.MushroomMonstrosityHeadBlock;
import com.k1sak1.goetyawaken.common.blocks.WallMushroomMonstrosityHeadBlock;
import com.k1sak1.goetyawaken.common.blocks.entity.MushroomMonstrosityHeadBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class MushroomMonstrosityHeadBlockEntityRenderer implements BlockEntityRenderer<MushroomMonstrosityHeadBlockEntity> {
    protected static final ResourceLocation TEXTURE = new ResourceLocation(GoetyAwaken.MODID, 
            "textures/entity/servants/mushroom_monstrosity/mushroom_monstrosity.png");

    public MushroomMonstrosityHeadBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {
    }

    @Override
    public void render(MushroomMonstrosityHeadBlockEntity pBlockEntity, float pPartialTicks, 
                       PoseStack pMatrixStack, MultiBufferSource pBuffer, int pCombinedLight, int pCombinedOverlay) {
        var blockstate = pBlockEntity.getBlockState();
        boolean flag = blockstate.getBlock() instanceof WallMushroomMonstrosityHeadBlock;
        Direction direction = flag ? blockstate.getValue(WallMushroomMonstrosityHeadBlock.FACING) : null;
        float f1 = 22.5F * (float)(flag ? (2 + direction.get2DDataValue()) * 4 : blockstate.getValue(MushroomMonstrosityHeadBlock.ROTATION));
        renderSkull(direction, f1, pMatrixStack, pBuffer, pCombinedLight);
    }

    public static void renderSkull(@Nullable Direction pDirection, float pRotation, 
                                   PoseStack pPoseStack, MultiBufferSource pBuffer, int pCombinedLight) {
        MushroomMonstrosityHeadModel skullModel = new MushroomMonstrosityHeadModel(
                Minecraft.getInstance().getEntityModels().bakeLayer(ModBlockLayer.MOOSHROOM_MONSTROSITY_HEAD));
        
        pPoseStack.pushPose();
        if (pDirection == null) {
            pPoseStack.translate(0.5D, 0.0D, 0.5D);
        } else {
            float f = 0.25F;
            pPoseStack.translate((double)(0.5F - (float)pDirection.getStepX() * f), 
                    f, (double)(0.5F - (float)pDirection.getStepZ() * f));
        }

        pPoseStack.scale(-1.0F, -1.0F, 1.0F);
        VertexConsumer ivertexbuilder = pBuffer.getBuffer(RenderType.entityCutoutNoCullZOffset(TEXTURE));
        skullModel.setupAnim(0, pRotation, 0.0F);
        skullModel.renderToBuffer(pPoseStack, ivertexbuilder, pCombinedLight, OverlayTexture.NO_OVERLAY, 
                1.0F, 1.0F, 1.0F, 1.0F);
        pPoseStack.popPose();
    }

    public static void renderItemSkull(ItemStack stack, @Nullable Direction pDirection, float pRotation, 
                                       PoseStack pPoseStack, MultiBufferSource pBuffer, int pCombinedLight) {
        MushroomMonstrosityHeadModel skullModel = new MushroomMonstrosityHeadModel(
                Minecraft.getInstance().getEntityModels().bakeLayer(ModBlockLayer.MOOSHROOM_MONSTROSITY_HEAD));
        
        pPoseStack.pushPose();
        if (pDirection == null) {
            pPoseStack.translate(0.5D, 0.0D, 0.5D);
        } else {
            float f = 0.25F;
            pPoseStack.translate((double)(0.5F - (float)pDirection.getStepX() * f), 
                    f, (double)(0.5F - (float)pDirection.getStepZ() * f));
        }

        pPoseStack.scale(-1.0F, -1.0F, 1.0F);
        pPoseStack.scale(0.25F, 0.25F, 0.25F);
        VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(pBuffer, 
                RenderType.entityTranslucent(TEXTURE), true, stack.hasFoil());
        skullModel.setupAnim(0, pRotation, 0.0F);
        skullModel.renderToBuffer(pPoseStack, vertexConsumer, pCombinedLight, OverlayTexture.NO_OVERLAY, 
                1.0F, 1.0F, 1.0F, 1.0F);
        pPoseStack.popPose();
    }
}
