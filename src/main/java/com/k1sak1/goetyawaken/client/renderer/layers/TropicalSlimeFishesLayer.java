package com.k1sak1.goetyawaken.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class TropicalSlimeFishesLayer<T extends com.Polarice3.Goety.common.entities.ally.TropicalSlimeServant>
        extends RenderLayer<T, com.Polarice3.Goety.client.render.model.TropicalSlimeModel<T>> {

    private final com.Polarice3.Goety.client.render.model.TropicalSlimeModel<T> model;

    public TropicalSlimeFishesLayer(
            RenderLayerParent<T, com.Polarice3.Goety.client.render.model.TropicalSlimeModel<T>> pRenderer,
            EntityModelSet pModelSet) {
        super(pRenderer);
        this.model = new com.Polarice3.Goety.client.render.model.TropicalSlimeModel<>(
                pModelSet.bakeLayer(com.Polarice3.Goety.client.render.ModModelLayer.TROPICAL_SLIME_INNER));
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, T pLivingEntity,
            float pLimbSwing, float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw,
            float pHeadPitch) {
        ResourceLocation fishTexture = pLivingEntity.getResourceLocation();

        if (fishTexture != null) {
            VertexConsumer vertexconsumer = pBufferSource.getBuffer(RenderType.entityCutoutNoCull(fishTexture));
            this.getParentModel().copyPropertiesTo(this.model);
            this.model.prepareMobModel(pLivingEntity, pLimbSwing, pLimbSwingAmount, pPartialTick);
            this.model.setupAnim(pLivingEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            this.model.renderToBuffer(pPoseStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F,
                    1.0F, 1.0F);
        }
    }
}