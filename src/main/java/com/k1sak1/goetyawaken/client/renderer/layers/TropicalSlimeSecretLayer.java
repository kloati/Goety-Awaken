package com.k1sak1.goetyawaken.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class TropicalSlimeSecretLayer<T extends com.Polarice3.Goety.common.entities.ally.TropicalSlimeServant>
                extends RenderLayer<T, com.Polarice3.Goety.client.render.model.TropicalSlimeModel<T>> {
        private static final ResourceLocation TEXTURES = new ResourceLocation("goety",
                        "textures/entity/servants/slime/slime_servant_secret.png");
        private final com.Polarice3.Goety.client.render.model.TropicalSlimeModel<T> layerModel;

        public TropicalSlimeSecretLayer(
                        RenderLayerParent<T, com.Polarice3.Goety.client.render.model.TropicalSlimeModel<T>> pRenderer,
                        EntityModelSet pModelSet) {
                super(pRenderer);
                this.layerModel = new com.Polarice3.Goety.client.render.model.TropicalSlimeModel<>(
                                pModelSet.bakeLayer(
                                                com.Polarice3.Goety.client.render.ModModelLayer.TROPICAL_SLIME_INNER));
        }

        @Override
        public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, T pEntity,
                        float pLimbSwing,
                        float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw,
                        float pHeadPitch) {
                if (pEntity.isInterested()) {
                        this.getParentModel().copyPropertiesTo(this.layerModel);
                        this.layerModel.prepareMobModel(pEntity, pLimbSwing, pLimbSwingAmount, pPartialTicks);
                        this.layerModel.setupAnim(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw,
                                        pHeadPitch);
                        this.getParentModel().copyPropertiesTo(this.layerModel);
                        this.renderColoredCutoutModel(this.layerModel, TEXTURES, pMatrixStack, pBuffer, pPackedLight,
                                        pEntity, 1.0F,
                                        1.0F, 1.0F);
                }
        }
}