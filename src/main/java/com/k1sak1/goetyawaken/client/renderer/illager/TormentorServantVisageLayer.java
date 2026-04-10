package com.k1sak1.goetyawaken.client.renderer.illager;

import com.k1sak1.goetyawaken.client.model.illager.TormentorServantModel;
import com.k1sak1.goetyawaken.common.entities.ally.illager.TormentorServant;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class TormentorServantVisageLayer extends EyesLayer<TormentorServant, TormentorServantModel> {
    private static final RenderType VISAGE = RenderType
            .eyes(new ResourceLocation("goety", "textures/entity/illagers/tormentor_visage.png"));

    public TormentorServantVisageLayer(RenderLayerParent<TormentorServant, TormentorServantModel> p_i50921_1_) {
        super(p_i50921_1_);
    }

    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn,
            TormentorServant entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks,
            float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entitylivingbaseIn.isCharging()) {
            super.render(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount,
                    partialTicks, ageInTicks, netHeadYaw, headPitch);
        }
    }

    public RenderType renderType() {
        return VISAGE;
    }
}