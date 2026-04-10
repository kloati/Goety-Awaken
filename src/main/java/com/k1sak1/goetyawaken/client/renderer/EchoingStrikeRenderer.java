package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.common.entities.projectiles.EchoingStrikeEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class EchoingStrikeRenderer extends EntityRenderer<EchoingStrikeEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/area_effect_cloud.png");

    public EchoingStrikeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(EchoingStrikeEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(EchoingStrikeEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack,
            MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }
}