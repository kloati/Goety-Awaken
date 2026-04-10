package com.k1sak1.goetyawaken.client.renderer.undead;

import com.k1sak1.goetyawaken.GoetyAwaken;
import com.k1sak1.goetyawaken.client.model.WightServantModel;
import com.k1sak1.goetyawaken.client.ClientEventHandler;
import com.k1sak1.goetyawaken.common.entities.ally.WightServant;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;

public class WightServantRenderer extends MobRenderer<WightServant, WightServantModel> {
    private static final ResourceLocation WIGHT_SERVANT_TEXTURE = new ResourceLocation(GoetyAwaken.MODID,
            "textures/entity/undead/wight_servant.png");
    protected static final ResourceLocation DEATH = new ResourceLocation("goety",
            "textures/entity/wight/wight_death.png");

    public WightServantRenderer(EntityRendererProvider.Context context) {
        super(context, new WightServantModel(context.bakeLayer(ClientEventHandler.WIGHT_SERVANT_LAYER)), 0.5F);
    }

    @Override
    public void render(WightServant pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack,
            MultiBufferSource pBuffer, int pPackedLight) {
        super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
        if (pEntity.deathTime > 0) {
            pMatrixStack.pushPose();
            boolean flag = pEntity.hurtTime > 0;
            float f = Mth.rotLerp(pPartialTicks, pEntity.yBodyRotO, pEntity.yBodyRot);
            float f1 = Mth.rotLerp(pPartialTicks, pEntity.yHeadRotO, pEntity.yHeadRot);
            float f2 = f1 - f;
            float f6 = Mth.lerp(pPartialTicks, pEntity.xRotO, pEntity.getXRot());
            float f71 = this.getBob(pEntity, pPartialTicks);
            this.setupRotations(pEntity, pMatrixStack, f71, f, pPartialTicks);
            pMatrixStack.scale(-1.0F, -1.0F, 1.0F);
            this.scale(pEntity, pMatrixStack, pPartialTicks);
            pMatrixStack.translate(0.0D, (double) -1.501F, 0.0D);
            this.model.prepareMobModel(pEntity, 0.0F, 0.0F, pPartialTicks);
            this.model.setupAnim(pEntity, 0.0F, 0.0F, f71, f2, f6);
            float f8 = 60.0F;
            float f9 = (float) pEntity.deathTime / f8;
            float f10 = 1.0F - (pEntity.deathTime / 60.0F);
            VertexConsumer vertexconsumer = pBuffer.getBuffer(RenderType.dragonExplosionAlpha(DEATH));
            this.model.renderToBuffer(pMatrixStack, vertexconsumer, pPackedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F,
                    1.0F, f9);
            VertexConsumer ivertexbuilder1 = pBuffer
                    .getBuffer(RenderType.entityDecal(this.getTextureLocation(pEntity)));
            this.model.renderToBuffer(pMatrixStack, ivertexbuilder1, pPackedLight, OverlayTexture.pack(0.0F, flag), f10,
                    f10, f10, 1.0F);
            pMatrixStack.popPose();
        }
    }

    @Nullable
    protected RenderType getRenderType(WightServant p_230496_1_, boolean p_230496_2_, boolean p_230496_3_,
            boolean p_230496_4_) {
        if (p_230496_1_.deathTime > 0) {
            return RenderType.dragonExplosionAlpha(DEATH);
        } else {
            return super.getRenderType(p_230496_1_, p_230496_2_, p_230496_3_, p_230496_4_);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(WightServant entity) {
        return WIGHT_SERVANT_TEXTURE;
    }
}