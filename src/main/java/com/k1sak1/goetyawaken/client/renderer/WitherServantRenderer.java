package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.WitherServantModel;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.client.renderer.layers.WitherServantArmorLayer;
import com.k1sak1.goetyawaken.common.entities.ally.WitherServant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.ResourceLocation;

public class WitherServantRenderer extends MobRenderer<WitherServant, WitherServantModel> {
    private static final ResourceLocation WITHER_SERVANT_TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/wither_servant.png");
    private static final ResourceLocation WITHER_INVULNERABLE_TEXTURE = new ResourceLocation(
            "textures/entity/wither/wither_invulnerable.png");
    private static final ResourceLocation WITHER_SERVANT_GLOW_TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/wither_servant_glow.png");

    public WitherServantRenderer(EntityRendererProvider.Context context) {
        super(context, new WitherServantModel(context.bakeLayer(ClientEventHandler.WITHER_SERVANT_LAYER)), 1.0F);
        this.addLayer(new WitherServantArmorLayer(this, context.getModelSet()));
        this.addLayer(new WitherServantEyesLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(WitherServant entity) {
        int i = entity.getInvulnerableTicks();
        if (entity.isFirstSpawn() && i > 0 && (i > 80 || i / 5 % 2 != 1)) {
            return WITHER_INVULNERABLE_TEXTURE;
        }
        return WITHER_SERVANT_TEXTURE;
    }

    public static class WitherServantEyesLayer extends EyesLayer<WitherServant, WitherServantModel> {
        private static final RenderType WITHER_SERVANT_EYES = RenderType
                .eyes(new ResourceLocation(GoetyAwaken.MODID, "textures/entity/wither_servant_glow.png"));

        public WitherServantEyesLayer(RenderLayerParent<WitherServant, WitherServantModel> renderer) {
            super(renderer);
        }

        @Override
        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, WitherServant entity,
                float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw,
                float headPitch) {
            VertexConsumer vertexconsumer = buffer.getBuffer(this.renderType());
            this.getParentModel().renderToBuffer(poseStack, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F,
                    1.0F, 1.0F, 1.0F);
        }

        @Override
        public RenderType renderType() {
            return WITHER_SERVANT_EYES;
        }
    }

    @Override
    protected void scale(WitherServant pLivingEntity, PoseStack pPoseStack, float pPartialTickTime) {
        float f = 2.0F;
        int i = pLivingEntity.getInvulnerableTicks();
        if (i > 0) {
            f -= ((float) i - pPartialTickTime) / 220.0F * 0.5F;
        }

        pPoseStack.scale(f, f, f);
    }
}