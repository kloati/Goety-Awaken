package com.k1sak1.goetyawaken.client.renderer.layers;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.ParchedModel;
import com.k1sak1.goetyawaken.client.renderer.ParchedServantRenderer;
import com.k1sak1.goetyawaken.common.entities.ally.undead.skeleton.ParchedServant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class ParchedServantOverlayLayer extends RenderLayer<ParchedServant, ParchedModel<ParchedServant>> {
    private static final ResourceLocation OVERLAY_TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/parched_servant_overlay.png");

    public ParchedServantOverlayLayer(ParchedServantRenderer renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
            ParchedServant entity, float limbSwing, float limbSwingAmount,
            float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isHostile()) {
            RenderType renderType = RenderType.entityTranslucent(OVERLAY_TEXTURE);
            VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
            this.getParentModel().copyPropertiesTo(this.getParentModel());
            this.getParentModel().setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            this.getParentModel().renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY,
                    1.0F, 1.0F, 1.0F, 1.0F);
        }
    }
}