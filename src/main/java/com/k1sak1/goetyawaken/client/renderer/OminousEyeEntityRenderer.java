package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.common.entities.projectiles.OminousEyeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OminousEyeEntityRenderer extends EyeEntityRenderer<OminousEyeEntity> {

    public OminousEyeEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, "textures/item/ominous_eye.png");
    }

    @Override
    public void render(OminousEyeEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack,
            MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }
}