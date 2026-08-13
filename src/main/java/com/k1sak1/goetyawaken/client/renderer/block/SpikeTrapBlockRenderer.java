package com.k1sak1.goetyawaken.client.renderer.block;

import com.Polarice3.Goety.client.render.ModRenderType;
import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.SpikeTrapBlockModel;
import com.k1sak1.goetyawaken.common.blocks.SpikeTrapBlock;
import com.k1sak1.goetyawaken.common.blocks.entity.SpikeTrapBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpikeTrapBlockRenderer implements BlockEntityRenderer<SpikeTrapBlockEntity> {

        private static final ResourceLocation TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
                        "textures/block/spike_trap_block.png");
        private static final ResourceLocation GLOW_TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
                        "textures/block/spike_trap_block_e.png");

        private final SpikeTrapBlockModel model;

        public SpikeTrapBlockRenderer(BlockEntityRendererProvider.Context pContext) {
                this.model = new SpikeTrapBlockModel(pContext.bakeLayer(SpikeTrapBlockModel.LAYER_LOCATION));
        }

        public void render(SpikeTrapBlockEntity pBlockEntity, float pPartialTick,
                        PoseStack pPoseStack, MultiBufferSource pBuffer,
                        int pPackedLight, int pPackedOverlay) {
                float ageInTicks = pBlockEntity.getLevel() == null ? 0.0F
                                : (pBlockEntity.getLevel().getGameTime() + pPartialTick);

                this.model.setupAnim(pBlockEntity, ageInTicks);
                pPoseStack.pushPose();
                pPoseStack.translate(0.5F, 1.5F, 0.5F);
                Direction facing = pBlockEntity.getBlockState().getValue(SpikeTrapBlock.FACING);
                switch (facing) {
                        case NORTH:
                                pPoseStack.mulPose(Axis.YP.rotationDegrees(180));
                                break;
                        case SOUTH:
                                break;
                        case WEST:
                                pPoseStack.mulPose(Axis.YP.rotationDegrees(90));
                                break;
                        case EAST:
                                pPoseStack.mulPose(Axis.YP.rotationDegrees(270));
                                break;
                }
                pPoseStack.scale(1.0F, -1.0F, 1.0F);
                VertexConsumer main = pBuffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
                this.model.renderToBuffer(pPoseStack, main, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);

                if (pBlockEntity.getSpikeTime() > 0) {
                        VertexConsumer glow = pBuffer.getBuffer(ModRenderType.wraith(GLOW_TEXTURE));
                        this.model.renderToBuffer(pPoseStack, glow, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F,
                                        1.0F,
                                        1.0F);
                }

                pPoseStack.popPose();
        }

}
