package com.k1sak1.goetyawaken.client.renderer.layers;

import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class TropicalSlimeOuterLayer<T extends LivingEntity>
        extends RenderLayer<T, com.Polarice3.Goety.client.render.model.TropicalSlimeModel<T>> {
    private final EntityModel<T> model;

    public TropicalSlimeOuterLayer(
            RenderLayerParent<T, com.Polarice3.Goety.client.render.model.TropicalSlimeModel<T>> pRenderer,
            EntityModelSet pModelSet) {
        super(pRenderer);
        this.model = new com.Polarice3.Goety.client.render.model.TropicalSlimeModel<>(
                pModelSet.bakeLayer(com.Polarice3.Goety.client.render.ModModelLayer.TROPICAL_SLIME_OUTER));
    }

    @Override
    public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, T pEntity, float pLimbSwing,
            float pLimbSwingAmount, float pPartialTick, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        Minecraft minecraft = Minecraft.getInstance();
        boolean flag = minecraft.shouldEntityAppearGlowing(pEntity) && pEntity.isInvisible();
        if (!pEntity.isInvisible() || flag) {
            com.mojang.blaze3d.vertex.VertexConsumer vertexconsumer;
            if (flag) {
                vertexconsumer = pBuffer.getBuffer(RenderType.outline(this.getTextureLocation(pEntity)));
            } else {
                vertexconsumer = pBuffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(pEntity)));
            }

            this.getParentModel().copyPropertiesTo(this.model);
            this.model.prepareMobModel(pEntity, pLimbSwing, pLimbSwingAmount, pPartialTick);
            this.model.setupAnim(pEntity, pLimbSwing, pLimbSwingAmount, pAgeInTicks, pNetHeadYaw, pHeadPitch);
            this.model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight,
                    LivingEntityRenderer.getOverlayCoords(pEntity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(T pEntity) {
        return new ResourceLocation("goety", "textures/entity/servants/slime/tropical_slime.png");
    }
}