package com.k1sak1.goetyawaken.client.renderer.layers;

import com.k1sak1.goetyawaken.client.model.undead.necromancer.ParchedNecromancerModel;
import com.k1sak1.goetyawaken.common.entities.hostile.undead.necromancer.ParchedNecromancer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class ParchedNecromancerEmissiveLayer
        extends EyesLayer<ParchedNecromancer, ParchedNecromancerModel<ParchedNecromancer>> {
    private final ResourceLocation texture;

    public ParchedNecromancerEmissiveLayer(
            RenderLayerParent<ParchedNecromancer, ParchedNecromancerModel<ParchedNecromancer>> renderer,
            ResourceLocation textureLocation) {
        super(renderer);
        this.texture = textureLocation;
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight,
            ParchedNecromancer pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks,
            float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if (!pLivingEntity.isInvisible()) {
            VertexConsumer vertexconsumer = pBuffer.getBuffer(this.renderType());
            this.getParentModel().renderToBuffer(pPoseStack, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F,
                    1.0F, 1.0F, 1.0F);
        }
    }

    @Override
    public RenderType renderType() {
        return RenderType.eyes(this.texture);
    }
}