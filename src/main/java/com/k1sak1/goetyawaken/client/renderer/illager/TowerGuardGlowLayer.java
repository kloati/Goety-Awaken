package com.k1sak1.goetyawaken.client.renderer.illager;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.TowerGuardModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class TowerGuardGlowLayer<T extends LivingEntity> extends RenderLayer<T, TowerGuardModel<T>> {
    private static final ResourceLocation GLOW_TEXTURE = GoetyAwaken
            .location("textures/entity/illager/tower_guard_glow.png");

    public TowerGuardGlowLayer(RenderLayerParent<T, TowerGuardModel<T>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
            T entity, float limbSwing, float limbSwingAmount, float partialTick,
            float ageInTicks, float netHeadYaw, float headPitch) {
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.eyes(GLOW_TEXTURE));
        this.getParentModel().renderToBuffer(poseStack, consumer, 15728640,
                net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected ResourceLocation getTextureLocation(T entity) {
        return GLOW_TEXTURE;
    }
}
