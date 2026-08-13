package com.k1sak1.goetyawaken.client.renderer;

import com.Polarice3.Goety.client.render.ModRenderType;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.common.entities.projectiles.GiantHellBlast;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class GiantHellBlastRenderer extends EntityRenderer<GiantHellBlast> {
    private final com.Polarice3.Goety.client.render.model.HellBlastModel<GiantHellBlast> model;

    public GiantHellBlastRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
        this.model = new com.Polarice3.Goety.client.render.model.HellBlastModel<>(
                pContext.bakeLayer(ClientEventHandler.GIANT_HELL_BLAST_LAYER));
    }

    protected int getBlockLightLevel(GiantHellBlast pEntity, BlockPos pPos) {
        return 15;
    }

    public void render(GiantHellBlast p_116484_, float p_116485_, float p_116486_, PoseStack p_116487_,
            MultiBufferSource p_116488_, int p_116489_) {
        p_116487_.pushPose();
        p_116487_.scale(-1.0F, -1.0F, 1.0F);
        p_116487_.scale(3.0F, 3.0F, 3.0F);
        float f = Mth.rotLerp(p_116486_, p_116484_.yRotO, p_116484_.getYRot());
        float f1 = Mth.lerp(p_116486_, p_116484_.xRotO, p_116484_.getXRot());
        VertexConsumer vertexconsumer = p_116488_
                .getBuffer(RenderType.entityCutoutNoCull(this.getTextureLocation(p_116484_)));
        this.model.setupAnim(0.0F, f, f1);
        this.model.renderToBuffer(p_116487_, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F,
                0.5F);
        VertexConsumer vertexconsumer2 = p_116488_.getBuffer(ModRenderType.wraith(this.getTextureLocation(p_116484_)));
        this.model.setupAnim(0.0F, f, f1);
        this.model.renderToBuffer(p_116487_, vertexconsumer2, 15728640, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F,
                0.5F);
        p_116487_.popPose();
        super.render(p_116484_, p_116485_, p_116486_, p_116487_, p_116488_, p_116489_);
    }

    public ResourceLocation getTextureLocation(GiantHellBlast pEntity) {
        return pEntity.getResourceLocation();
    }
}
