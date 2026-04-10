package com.k1sak1.goetyawaken.client.renderer;

import com.k1sak1.goetyawaken.common.entities.projectiles.GhostFireBolt;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.Polarice3.Goety.client.render.ModModelLayer;
import com.Polarice3.Goety.client.render.model.SoulBoltModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GhostFireBoltRenderer extends EntityRenderer<GhostFireBolt> {
    private final SoulBoltModel<GhostFireBolt> model;

    public GhostFireBoltRenderer(EntityRendererProvider.Context p_174449_) {
        super(p_174449_);
        this.model = new SoulBoltModel<>(p_174449_.bakeLayer(ModModelLayer.SOUL_BOLT));
    }

    @Override
    public void render(GhostFireBolt pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack,
            MultiBufferSource pBuffer, int pPackedLight) {
        pMatrixStack.pushPose();
        pMatrixStack.scale(-1.25F, -1.25F, 1.25F);
        float f = Mth.rotLerp(pPartialTicks, pEntity.yRotO, pEntity.getYRot());
        float f1 = Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot());
        VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.eyes(this.getTextureLocation(pEntity)));
        this.model.setupAnim(0.0F, f, f1);
        this.model.renderToBuffer(pMatrixStack, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F,
                0.5F);
        pMatrixStack.popPose();
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(GhostFireBolt pEntity) {
        return pEntity.getResourceLocation();
    }
}