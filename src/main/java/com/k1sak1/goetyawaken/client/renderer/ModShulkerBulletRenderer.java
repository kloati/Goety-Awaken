package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.common.entities.projectiles.ModShulkerBullet;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.PoseStack;

public class ModShulkerBulletRenderer extends EntityRenderer<ModShulkerBullet> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/shulker/spark.png");

    public ModShulkerBulletRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(ModShulkerBullet entity) {
        return TEXTURE;
    }

    @Override
    public void render(ModShulkerBullet entity, float entityYaw, float partialTicks, PoseStack matrixStack,
            net.minecraft.client.renderer.MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }
}