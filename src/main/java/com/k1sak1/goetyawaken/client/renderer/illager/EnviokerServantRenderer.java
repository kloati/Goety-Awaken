package com.k1sak1.goetyawaken.client.renderer.illager;

import com.k1sak1.goetyawaken.common.entities.ally.illager.EnviokerServant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.Polarice3.Goety.client.render.layer.HierarchicalArmorLayer;
import com.Polarice3.Goety.client.render.model.IllagerServantModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class EnviokerServantRenderer extends MobRenderer<EnviokerServant, IllagerServantModel<EnviokerServant>> {
    private static final ResourceLocation ENVIOKER_SERVANT = new ResourceLocation("goetyawaken",
            "textures/entity/illager/envioker_servant.png");

    public EnviokerServantRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn,
                new IllagerServantModel<>(
                        renderManagerIn.bakeLayer(com.Polarice3.Goety.client.render.ModModelLayer.ILLAGER_SERVANT)),
                0.5F);
        this.addLayer(new HierarchicalArmorLayer<>(this, renderManagerIn));
        this.addLayer(new ItemInHandLayer<>(this, renderManagerIn.getItemInHandRenderer()) {
            public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn,
                    EnviokerServant entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks,
                    float ageInTicks, float netHeadYaw, float headPitch) {
                if (entitylivingbaseIn
                        .getArmPose() != com.Polarice3.Goety.common.entities.ally.illager.AbstractIllagerServant.IllagerServantArmPose.CROSSED) {
                    super.render(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount,
                            partialTicks, ageInTicks, netHeadYaw, headPitch);
                }
            }
        });
    }

    protected void scale(EnviokerServant entity, PoseStack matrixStackIn, float partialTickTime) {
        float f = 0.9375F;
        matrixStackIn.scale(f, f, f);
    }

    public ResourceLocation getTextureLocation(EnviokerServant entity) {
        return ENVIOKER_SERVANT;
    }
}