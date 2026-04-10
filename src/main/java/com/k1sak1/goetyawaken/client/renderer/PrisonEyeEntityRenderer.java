package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.common.entities.projectiles.PrisonEyeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PrisonEyeEntityRenderer extends EyeEntityRenderer<PrisonEyeEntity> {

    public PrisonEyeEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, "textures/item/prison_eye.png");
    }

    @Override
    public void render(PrisonEyeEntity pEntity, float pEntityYaw, float pPartialTicks, PoseStack pPoseStack,
            MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pPoseStack, pBuffer, pPackedLight);
    }
}