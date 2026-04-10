package com.k1sak1.goetyawaken.client.renderer.layers;

import com.k1sak1.goetyawaken.client.model.ShulkerServantModel;
import com.k1sak1.goetyawaken.client.renderer.ShulkerServantRenderer;
import com.k1sak1.goetyawaken.common.entities.ally.ShulkerServant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShulkerServantHeadLayer extends RenderLayer<ShulkerServant, ShulkerServantModel> {
    public ShulkerServantHeadLayer(RenderLayerParent<ShulkerServant, ShulkerServantModel> pRenderer) {
        super(pRenderer);
    }

    public void render(PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, ShulkerServant pLivingEntity,
            float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw,
            float pHeadPitch) {
        ResourceLocation resourcelocation = ShulkerServantRenderer.SHULKER_SERVANT_TEXTURE;
        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.entitySolid(resourcelocation));
        this.getParentModel().getHead().render(pPoseStack, vertexconsumer, pPackedLight,
                LivingEntityRenderer.getOverlayCoords(pLivingEntity, 0.0F));
    }
}