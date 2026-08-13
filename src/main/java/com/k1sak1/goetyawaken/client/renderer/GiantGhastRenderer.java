package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.client.model.GiantGhastModel;
import com.k1sak1.goetyawaken.common.entities.hostile.GiantGhast;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class GiantGhastRenderer extends MobRenderer<GiantGhast, GiantGhastModel<GiantGhast>> {
    private static final ResourceLocation TEXTURE = GoetyAwaken.location("textures/entity/giant_ghast.png");
    public static final ResourceLocation OUTPOURING_TEXTURE = GoetyAwaken
            .location("textures/entity/giant_ghast_outpouring.png");
    private static final ResourceLocation TEXTURE_GLOW = GoetyAwaken.location("textures/entity/giant_ghast_glow.png");

    private static final ResourceLocation OUTPOURING_TEXTURE_GLOW = GoetyAwaken
            .location("textures/entity/giant_ghast_outpouring_glow.png");

    private static final ResourceLocation SERVANT_TEXTURE = GoetyAwaken
            .location("textures/entity/additional_giant_ghast_servant.png");
    private static final ResourceLocation SIDE_ATTACK_TEXTURE_1 = GoetyAwaken
            .location("textures/entity/giant_ghast_attack1.png");
    private static final ResourceLocation SIDE_ATTACK_TEXTURE_1_GLOW = GoetyAwaken
            .location("textures/entity/giant_ghast_attack1_glow.png");
    private static final ResourceLocation SIDE_ATTACK_TEXTURE_2 = GoetyAwaken
            .location("textures/entity/giant_ghast_attack2.png");
    private static final ResourceLocation SIDE_ATTACK_TEXTURE_2_GLOW = GoetyAwaken
            .location("textures/entity/giant_ghast_attack2_glow.png");
    private static final ResourceLocation SIDE_TEXTURE = GoetyAwaken.location("textures/entity/giant_ghast_side.png");

    public GiantGhastRenderer(EntityRendererProvider.Context context) {
        super(context, new GiantGhastModel<>(context.bakeLayer(ClientEventHandler.GIANT_GHAST_LAYER)), 2.0F);
        this.addLayer(new GiantGhastSideLayer(this));
        this.addLayer(new GiantGhastGlowLayer(this));
        this.addLayer(new GiantGhastServantLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(GiantGhast entity) {
        if (entity.isOutpouring()) {
            return OUTPOURING_TEXTURE;
        }
        return TEXTURE;
    }

    @Override
    protected void scale(GiantGhast entity, PoseStack matrixStack, float partialTickTime) {
        matrixStack.translate(0.0, 5.75, 0.0);
        matrixStack.scale(4.0F, 4.0F, 4.0F);
        super.scale(entity, matrixStack, partialTickTime);
    }

    private static class GiantGhastSideLayer extends RenderLayer<GiantGhast, GiantGhastModel<GiantGhast>> {
        public GiantGhastSideLayer(RenderLayerParent<GiantGhast, GiantGhastModel<GiantGhast>> pRenderer) {
            super(pRenderer);
        }

        @Override
        public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, GiantGhast pLivingEntity,
                float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw,
                float pHeadPitch) {
            ResourceLocation sideTexture;
            if (pLivingEntity.isSideAttacking()) {
                int textureId = pLivingEntity.getSideAttackTexture();
                sideTexture = textureId == 0 ? SIDE_ATTACK_TEXTURE_1 : SIDE_ATTACK_TEXTURE_2;
            } else {
                sideTexture = SIDE_TEXTURE;
            }

            renderColoredCutoutModel(this.getParentModel(), sideTexture, pPoseStack, pBuffer, pPackedLight,
                    pLivingEntity, 1.0F, 1.0F, 1.0F);
        }
    }

    private static class GiantGhastGlowLayer extends RenderLayer<GiantGhast, GiantGhastModel<GiantGhast>> {
        public GiantGhastGlowLayer(RenderLayerParent<GiantGhast, GiantGhastModel<GiantGhast>> pRenderer) {
            super(pRenderer);
        }

        @Override
        public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, GiantGhast pLivingEntity,
                float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw,
                float pHeadPitch) {
            ResourceLocation glowTexture;
            if (pLivingEntity.isOutpouring()) {
                glowTexture = OUTPOURING_TEXTURE_GLOW;
            } else if (pLivingEntity.isSideAttacking()) {
                int textureId = pLivingEntity.getSideAttackTexture();
                glowTexture = textureId == 0 ? SIDE_ATTACK_TEXTURE_1_GLOW : SIDE_ATTACK_TEXTURE_2_GLOW;
            } else {
                glowTexture = TEXTURE_GLOW;
            }

            VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.eyes(glowTexture));
            this.getParentModel().prepareMobModel(pLivingEntity, pLimbSwing, pLimbSwingAmount, pPartialTicks);
            this.getParentModel().setupAnim(pLivingEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw,
                    pHeadPitch);
            this.getParentModel().renderToBuffer(pPoseStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY,
                    1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private static class GiantGhastServantLayer extends RenderLayer<GiantGhast, GiantGhastModel<GiantGhast>> {
        public GiantGhastServantLayer(RenderLayerParent<GiantGhast, GiantGhastModel<GiantGhast>> pRenderer) {
            super(pRenderer);
        }

        @Override
        public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, GiantGhast pLivingEntity,
                float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw,
                float pHeadPitch) {
            if (!pLivingEntity.isHostile()) {
                VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entityCutoutNoCull(SERVANT_TEXTURE));
                this.getParentModel().prepareMobModel(pLivingEntity, pLimbSwing, pLimbSwingAmount, pPartialTicks);
                this.getParentModel().setupAnim(pLivingEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw,
                        pHeadPitch);
                this.getParentModel().renderToBuffer(pPoseStack, vertexconsumer, pPackedLight,
                        OverlayTexture.NO_OVERLAY,
                        1.0F, 1.0F, 1.0F, 1.0F);
            }
        }
    }
}
