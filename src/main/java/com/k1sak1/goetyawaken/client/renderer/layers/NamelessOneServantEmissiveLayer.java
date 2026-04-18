package com.k1sak1.goetyawaken.client.renderer.layers;

import com.k1sak1.goetyawaken.client.model.undead.necromancer.NamelessOneModel;
import com.k1sak1.goetyawaken.client.renderer.GAModRenderTypes;
import com.k1sak1.goetyawaken.common.entities.ally.undead.necromancer.NamelessOneServant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class NamelessOneServantEmissiveLayer
        extends EyesLayer<NamelessOneServant, NamelessOneModel<NamelessOneServant>> {
    private final ResourceLocation texture;

    public NamelessOneServantEmissiveLayer(
            RenderLayerParent<NamelessOneServant, NamelessOneModel<NamelessOneServant>> renderer,
            ResourceLocation textureLocation) {
        super(renderer);
        this.texture = textureLocation;
    }

    @Override
    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight,
            NamelessOneServant pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks,
            float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        if (!pLivingEntity.isInvisible()) {
            VertexConsumer vertexconsumer = pBuffer.getBuffer(this.renderType());
            this.getParentModel().renderToBuffer(pPoseStack, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.5F,
                    1.5F, 1.5F, 0.6F);
        }
    }

    @Override
    public RenderType renderType() {
        return GAModRenderTypes.getEmissiveGlowType(this.texture);
    }
}